import paramiko
import ftplib
import os
from datetime import datetime

class FTPService:
    @staticmethod
    def create_account(name, protocol, host, port, username, password, owner_id, user_ids=None):
        """Yeni FTP hesabı oluşturur (admin tarafından)"""
        from models.ftp_account import FTPAccount
        from models.ftp_user_assignment import FTPUserAssignment
        from models import db

        account = FTPAccount(
            name=name,
            protocol=protocol,
            host=host,
            port=port,
            username=username,
            password=password,
            owner_id=owner_id
        )

        try:
            db.session.add(account)
            db.session.flush()  # Get the account ID
            
            # Assign to users if provided
            if user_ids:
                for user_id in user_ids:
                    assignment = FTPUserAssignment(
                        ftp_account_id=account.id,
                        user_id=user_id,
                        assigned_by=owner_id
                    )
                    db.session.add(assignment)
            
            db.session.commit()
            return account, "FTP hesabı başarıyla oluşturuldu"
        except Exception as e:
            db.session.rollback()
            return None, f"FTP hesabı oluşturulurken hata oluştu: {str(e)}"
    
    @staticmethod
    def get_account_by_id(account_id):
        """ID'ye göre FTP hesabı getirir"""
        from models.ftp_account import FTPAccount
        return FTPAccount.query.get(account_id)
    
    @staticmethod
    def get_user_accounts(user_id):
        """Kullanıcının atanmış FTP hesaplarını getirir"""
        from models.ftp_account import FTPAccount
        from models.ftp_user_assignment import FTPUserAssignment
        
        return FTPAccount.query.join(FTPUserAssignment).filter(
            FTPUserAssignment.user_id == user_id
        ).all()
    
    @staticmethod
    def assign_account_to_users(account_id, user_ids, assigned_by):
        """FTP hesabını birden fazla kullanıcıya atar"""
        from models.ftp_account import FTPAccount
        from models.ftp_user_assignment import FTPUserAssignment
        from models import db
        
        account = FTPAccount.query.get(account_id)
        if not account:
            return False, "FTP hesabı bulunamadı"
        
        try:
            # Remove existing assignments
            FTPUserAssignment.query.filter_by(ftp_account_id=account_id).delete()
            
            # Add new assignments
            for user_id in user_ids:
                assignment = FTPUserAssignment(
                    ftp_account_id=account_id,
                    user_id=user_id,
                    assigned_by=assigned_by
                )
                db.session.add(assignment)
            
            db.session.commit()
            return True, f"FTP hesabı {len(user_ids)} kullanıcıya atandı"
        except Exception as e:
            db.session.rollback()
            return False, f"Atama sırasında hata oluştu: {str(e)}"
    
    @staticmethod
    def unassign_account_from_user(account_id, user_id):
        """FTP hesabının belirli bir kullanıcı atamasını kaldırır"""
        from models.ftp_user_assignment import FTPUserAssignment
        from models import db
        
        try:
            assignment = FTPUserAssignment.query.filter_by(
                ftp_account_id=account_id, 
                user_id=user_id
            ).first()
            
            if assignment:
                db.session.delete(assignment)
                db.session.commit()
                return True, "Kullanıcı ataması kaldırıldı"
            else:
                return False, "Atama bulunamadı"
        except Exception as e:
            db.session.rollback()
            return False, f"Atama kaldırılırken hata oluştu: {str(e)}"
    
    @staticmethod
    def unassign_account_from_all_users(account_id):
        """FTP hesabının tüm kullanıcı atamalarını kaldırır"""
        from models.ftp_user_assignment import FTPUserAssignment
        from models import db
        
        try:
            FTPUserAssignment.query.filter_by(ftp_account_id=account_id).delete()
            db.session.commit()
            return True, "Tüm kullanıcı atamaları kaldırıldı"
        except Exception as e:
            db.session.rollback()
            return False, f"Atamalar kaldırılırken hata oluştu: {str(e)}"
    
    @staticmethod
    def update_account(account_id, name, protocol, host, port, username, password, user_ids=None):
        """FTP hesabını günceller"""
        from models.ftp_account import FTPAccount
        from models.ftp_user_assignment import FTPUserAssignment
        from models import db
        
        account = FTPAccount.query.get(account_id)
        if not account:
            return False, "FTP hesabı bulunamadı"
        
        try:
            account.name = name
            account.protocol = protocol
            account.host = host
            account.port = port
            account.username = username
            if password:  # Şifre boş değilse güncelle
                account.password = password
            
            # Update user assignments if provided
            if user_ids is not None:
                # Remove existing assignments
                FTPUserAssignment.query.filter_by(ftp_account_id=account_id).delete()
                
                # Add new assignments
                for user_id in user_ids:
                    assignment = FTPUserAssignment(
                        ftp_account_id=account_id,
                        user_id=user_id,
                        assigned_by=account.owner_id
                    )
                    db.session.add(assignment)
            
            db.session.commit()
            return True, "FTP hesabı başarıyla güncellendi"
        except Exception as e:
            db.session.rollback()
            return False, f"FTP hesabı güncellenirken hata oluştu: {str(e)}"
    
    @staticmethod
    def get_all_accounts():
        """Tüm FTP hesaplarını getirir (admin için)"""
        from models.ftp_account import FTPAccount
        from models.user import User
        return FTPAccount.query.join(User, FTPAccount.owner_id == User.id).all()
    
    @staticmethod
    def get_account_assignments(account_id):
        """FTP hesabının atanmış kullanıcılarını getirir"""
        from models.ftp_user_assignment import FTPUserAssignment
        from models.user import User
        
        assignments = FTPUserAssignment.query.filter_by(ftp_account_id=account_id).join(
            User, FTPUserAssignment.user_id == User.id
        ).all()
        return [assignment.user for assignment in assignments]
    
    @staticmethod
    def get_accounts_with_users():
        """FTP hesaplarını atanmış kullanıcılarıyla birlikte getirir"""
        from models.ftp_account import FTPAccount
        from models.user import User
        
        accounts = FTPAccount.query.join(User, FTPAccount.owner_id == User.id).all()
        for account in accounts:
            account.assigned_users_list = FTPService.get_account_assignments(account.id)
        return accounts
    
    @staticmethod
    def delete_account(account_id):
        """FTP hesabını siler (kullanıcılara atanmış olsa bile)"""
        from models.ftp_account import FTPAccount
        from models.ftp_user_assignment import FTPUserAssignment
        from models.transfer_log import TransferLog
        from models import db
        
        account = FTPAccount.query.get(account_id)
        if not account:
            return False, "FTP hesabı bulunamadı"
        
        try:
            # İlişkili kayıtları temizle
            # Transfer loglarında ftp_account_id'yi NULL yap (audit trail için logları koru)
            TransferLog.query.filter_by(ftp_account_id=account_id).update({'ftp_account_id': None})
            
            # FTP kullanıcı atamalarını sil
            FTPUserAssignment.query.filter_by(ftp_account_id=account_id).delete()
            
            # FTP hesabını sil
            db.session.delete(account)
            db.session.commit()
            return True, "FTP hesabı başarıyla silindi"
        except Exception as e:
            db.session.rollback()
            return False, f"FTP hesabı silinirken hata oluştu: {str(e)}"
    
    @staticmethod
    def list_ftp_files(account):
        """FTP sunucusundaki dosyaları listeler"""
        try:
            ftp = ftplib.FTP()
            ftp.connect(account.host, account.port)
            ftp.login(account.username, account.password)
            files = []
            ftp.retrlines('LIST', files.append)
            ftp.quit()
            return files, None
        except Exception as e:
            return None, f"FTP bağlantı hatası: {str(e)}"
    
    @staticmethod
    def list_sftp_files(account):
        """SFTP sunucusundaki dosyaları listeler"""
        try:
            ssh = paramiko.SSHClient()
            ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            ssh.connect(account.host, port=account.port, username=account.username, password=account.password)
            sftp = ssh.open_sftp()
            files = sftp.listdir('.')
            ssh.close()
            return files, None
        except Exception as e:
            return None, f"SFTP bağlantı hatası: {str(e)}"
    
    @staticmethod
    def upload_to_ftp(account, local_file_path, remote_filename):
        """FTP sunucusuna dosya yükler"""
        try:
            ftp = ftplib.FTP()
            ftp.connect(account.host, account.port)
            ftp.login(account.username, account.password)
            
            with open(local_file_path, 'rb') as file:
                ftp.storbinary(f'STOR {remote_filename}', file)
            
            ftp.quit()
            return True, None
        except Exception as e:
            return False, f"FTP yükleme hatası: {str(e)}"
    
    @staticmethod
    def upload_to_sftp(account, local_file_path, remote_filename):
        """SFTP sunucusuna dosya yükler"""
        try:
            ssh = paramiko.SSHClient()
            ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            ssh.connect(account.host, port=account.port, username=account.username, password=account.password)
            sftp = ssh.open_sftp()
            
            sftp.put(local_file_path, remote_filename)
            
            sftp.close()
            ssh.close()
            return True, None
        except Exception as e:
            return False, f"SFTP yükleme hatası: {str(e)}"
    
    @staticmethod
    def download_from_ftp(account, filename):
        """FTP sunucusundan dosya indirir"""
        try:
            ftp = ftplib.FTP()
            ftp.connect(account.host, account.port)
            ftp.login(account.username, account.password)
            
            file_data = []
            ftp.retrbinary(f'RETR {filename}', lambda data: file_data.append(data))
            ftp.quit()
            
            return b''.join(file_data), None
        except Exception as e:
            return None, f"FTP indirme hatası: {str(e)}"
    
    @staticmethod
    def download_from_sftp(account, filename):
        """SFTP sunucusundan dosya indirir"""
        try:
            ssh = paramiko.SSHClient()
            ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            ssh.connect(account.host, port=account.port, username=account.username, password=account.password)
            sftp = ssh.open_sftp()
            
            file_data = sftp.open(filename, 'rb').read()
            
            sftp.close()
            ssh.close()
            
            return file_data, None
        except Exception as e:
            return None, f"SFTP indirme hatası: {str(e)}"
    
    @staticmethod
    def delete_from_ftp(account, filename):
        """FTP sunucusundan dosya siler"""
        try:
            ftp = ftplib.FTP()
            ftp.connect(account.host, account.port)
            ftp.login(account.username, account.password)
            
            ftp.delete(filename)
            ftp.quit()
            return True, None
        except Exception as e:
            return False, f"FTP silme hatası: {str(e)}"
    
    @staticmethod
    def delete_from_sftp(account, filename):
        """SFTP sunucusundan dosya siler"""
        try:
            ssh = paramiko.SSHClient()
            ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            ssh.connect(account.host, port=account.port, username=account.username, password=account.password)
            sftp = ssh.open_sftp()
            
            sftp.remove(filename)
            
            sftp.close()
            ssh.close()
            return True, None
        except Exception as e:
            return False, f"SFTP silme hatası: {str(e)}"

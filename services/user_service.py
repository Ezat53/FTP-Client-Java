from werkzeug.security import generate_password_hash

class UserService:
    @staticmethod
    def get_all_users():
        """Tüm kullanıcıları getirir"""
        from models.user import User
        return User.query.all()
    
    @staticmethod
    def get_user_by_id(user_id):
        """ID'ye göre kullanıcı getirir"""
        from models.user import User
        return User.query.get(user_id)
    
    @staticmethod
    def update_user(user_id, username, role, password=None):
        """Kullanıcıyı günceller"""
        from models.user import User
        from models import db
        from werkzeug.security import generate_password_hash
        
        user = User.query.get(user_id)
        if not user:
            return False, "Kullanıcı bulunamadı"
        
        # Kullanıcı adı kontrolü (mevcut kullanıcı hariç)
        existing_user = User.query.filter(User.username == username, User.id != user_id).first()
        if existing_user:
            return False, "Bu kullanıcı adı zaten kullanılıyor"
        
        try:
            user.username = username
            user.role = role
            if password:  # Şifre verilmişse güncelle
                user.password_hash = generate_password_hash(password)
            
            db.session.commit()
            return True, "Kullanıcı başarıyla güncellendi"
        except Exception as e:
            db.session.rollback()
            return False, f"Kullanıcı güncellenirken hata oluştu: {str(e)}"
    
    @staticmethod
    def delete_user(user_id):
        """Kullanıcıyı siler (FTP hesapları olsa bile)"""
        from models.user import User
        from models.transfer_log import TransferLog
        from models.ftp_user_assignment import FTPUserAssignment
        from models import db
        
        user = User.query.get(user_id)
        if not user:
            return False, "Kullanıcı bulunamadı"
        
        try:
            # İlişkili kayıtları temizle
            # Transfer loglarında user_id'yi NULL yap (audit trail için logları koru)
            TransferLog.query.filter_by(user_id=user_id).update({'user_id': None})
            
            # FTP kullanıcı atamalarını sil
            FTPUserAssignment.query.filter_by(user_id=user_id).delete()
            
            # Kullanıcıyı sil
            db.session.delete(user)
            db.session.commit()
            return True, "Kullanıcı başarıyla silindi"
        except Exception as e:
            db.session.rollback()
            return False, f"Kullanıcı silinirken hata oluştu: {str(e)}"
    
    @staticmethod
    def get_user_stats():
        """Kullanıcı istatistiklerini getirir"""
        from models.user import User
        
        total_users = User.query.count()
        admin_users = User.query.filter_by(role='admin').count()
        regular_users = User.query.filter_by(role='user').count()
        
        return {
            'total_users': total_users,
            'admin_users': admin_users,
            'regular_users': regular_users
        }

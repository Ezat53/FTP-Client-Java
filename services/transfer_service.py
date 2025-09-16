from datetime import datetime

class TransferService:
    @staticmethod
    def log_transfer(user_id, ftp_account_id, action, filename, file_size, status, error_message=None):
        """Transfer işlemini loglar"""
        from models.transfer_log import TransferLog
        from models import db
        
        try:
            log = TransferLog(
                user_id=user_id,
                ftp_account_id=ftp_account_id,
                action=action,
                filename=filename,
                file_size=file_size,
                status=status,
                error_message=error_message
            )
            db.session.add(log)
            db.session.commit()
            return True, "Transfer logu kaydedildi"
        except Exception as e:
            db.session.rollback()
            return False, f"Transfer logu kaydedilirken hata oluştu: {str(e)}"
    
    @staticmethod
    def get_user_transfers(user_id, limit=10):
        """Kullanıcının transfer loglarını getirir"""
        from models.transfer_log import TransferLog
        from models.user import User
        from models.ftp_account import FTPAccount
        
        return TransferLog.query.filter_by(user_id=user_id)\
            .join(User, TransferLog.user_id == User.id)\
            .join(FTPAccount, TransferLog.ftp_account_id == FTPAccount.id)\
            .order_by(TransferLog.created_at.desc())\
            .limit(limit).all()
    
    @staticmethod
    def get_all_transfers(limit=50):
        """Tüm transfer loglarını getirir"""
        from models.transfer_log import TransferLog
        from models.user import User
        from models.ftp_account import FTPAccount
        
        return TransferLog.query\
            .join(User, TransferLog.user_id == User.id)\
            .join(FTPAccount, TransferLog.ftp_account_id == FTPAccount.id)\
            .order_by(TransferLog.created_at.desc())\
            .limit(limit).all()
    
    @staticmethod
    def get_transfer_stats():
        """Transfer istatistiklerini getirir"""
        from models.transfer_log import TransferLog
        
        total_transfers = TransferLog.query.count()
        successful_transfers = TransferLog.query.filter_by(status='success').count()
        failed_transfers = TransferLog.query.filter_by(status='error').count()
        
        return {
            'total_transfers': total_transfers,
            'successful_transfers': successful_transfers,
            'failed_transfers': failed_transfers,
            'success_rate': (successful_transfers / total_transfers * 100) if total_transfers > 0 else 0
        }
    
    @staticmethod
    def get_transfers_by_account(ftp_account_id, limit=20):
        """Belirli FTP hesabının transfer loglarını getirir"""
        from models.transfer_log import TransferLog
        return TransferLog.query.filter_by(ftp_account_id=ftp_account_id).order_by(TransferLog.created_at.desc()).limit(limit).all()
    
    @staticmethod
    def get_transfers_by_action(action, limit=20):
        """Belirli işlem türünün transfer loglarını getirir"""
        from models.transfer_log import TransferLog
        return TransferLog.query.filter_by(action=action).order_by(TransferLog.created_at.desc()).limit(limit).all()

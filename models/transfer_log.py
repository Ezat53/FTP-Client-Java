from datetime import datetime
from . import db

class TransferLog(db.Model):
    __tablename__ = 'transfer_logs'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=True)
    ftp_account_id = db.Column(db.Integer, db.ForeignKey('ftp_accounts.id'), nullable=True)
    action = db.Column(db.String(50), nullable=False)  # upload, download, delete, list
    filename = db.Column(db.String(255), nullable=True)
    file_size = db.Column(db.Integer, nullable=True)
    status = db.Column(db.String(20), nullable=False)  # success, error
    error_message = db.Column(db.Text, nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f'<TransferLog {self.action} - {self.filename}>'
    
    def to_dict(self):
        return {
            'id': self.id,
            'user_id': self.user_id,
            'ftp_account_id': self.ftp_account_id,
            'action': self.action,
            'filename': self.filename,
            'file_size': self.file_size,
            'status': self.status,
            'error_message': self.error_message,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }

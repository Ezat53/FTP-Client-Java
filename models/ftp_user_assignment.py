from datetime import datetime
from . import db

class FTPUserAssignment(db.Model):
    """FTP hesabı ve kullanıcı arasındaki atama ilişkisi"""
    __tablename__ = 'ftp_user_assignments'
    
    id = db.Column(db.Integer, primary_key=True)
    ftp_account_id = db.Column(db.Integer, db.ForeignKey('ftp_accounts.id'), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    assigned_at = db.Column(db.DateTime, default=datetime.utcnow)
    assigned_by = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)  # Hangi admin atadı
    
    # Relationships
    ftp_account = db.relationship('FTPAccount', backref='user_assignments', overlaps="assigned_users,assigned_ftp_accounts")
    user = db.relationship('User', foreign_keys=[user_id], backref='ftp_assignments', overlaps="assigned_ftp_accounts,assigned_users")
    assigner = db.relationship('User', foreign_keys=[assigned_by], backref='admin_assigned_accounts')
    
    # Unique constraint - aynı kullanıcıya aynı FTP hesabı birden fazla atanamaz
    __table_args__ = (db.UniqueConstraint('ftp_account_id', 'user_id', name='unique_ftp_user_assignment'),)
    
    def __repr__(self):
        return f'<FTPUserAssignment {self.ftp_account.name} -> {self.user.username}>'
    
    def to_dict(self):
        return {
            'id': self.id,
            'ftp_account_id': self.ftp_account_id,
            'user_id': self.user_id,
            'assigned_at': self.assigned_at.isoformat() if self.assigned_at else None,
            'assigned_by': self.assigned_by
        }

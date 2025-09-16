from flask_login import UserMixin
from datetime import datetime
from . import db

class User(UserMixin, db.Model):
    __tablename__ = 'users'
    
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    password_hash = db.Column(db.String(120), nullable=False)
    role = db.Column(db.String(20), default='user')
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    # Relationships
    owned_ftp_accounts = db.relationship('FTPAccount', foreign_keys='FTPAccount.owner_id', back_populates='owner', lazy='dynamic')
    assigned_ftp_accounts = db.relationship('FTPAccount', secondary='ftp_user_assignments', 
                                          primaryjoin='User.id == FTPUserAssignment.user_id',
                                          secondaryjoin='FTPAccount.id == FTPUserAssignment.ftp_account_id',
                                          back_populates='assigned_users', lazy='dynamic',
                                          overlaps="user_assignments,ftp_assignments")
    transfer_logs = db.relationship('TransferLog', backref='user', lazy='dynamic')
    
    def __repr__(self):
        return f'<User {self.username}>'
    
    def to_dict(self):
        return {
            'id': self.id,
            'username': self.username,
            'role': self.role,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }

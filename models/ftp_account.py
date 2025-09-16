from datetime import datetime
from . import db

class FTPAccount(db.Model):
    __tablename__ = 'ftp_accounts'
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    protocol = db.Column(db.String(10), nullable=False)  # ftp, sftp, scp
    host = db.Column(db.String(255), nullable=False)
    port = db.Column(db.Integer, nullable=False)
    username = db.Column(db.String(100), nullable=False)
    password = db.Column(db.String(255), nullable=True)
    owner_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)  # Admin who created
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

    # Relationships
    transfer_logs = db.relationship('TransferLog', backref='ftp_account', lazy='dynamic')

    # Explicit relationships to avoid ambiguity
    owner = db.relationship('User', foreign_keys=[owner_id], back_populates='owned_ftp_accounts')
    
    # Many-to-many relationship with users through FTPUserAssignment
    assigned_users = db.relationship('User', secondary='ftp_user_assignments', 
                                   primaryjoin='FTPAccount.id == FTPUserAssignment.ftp_account_id',
                                   secondaryjoin='User.id == FTPUserAssignment.user_id',
                                   back_populates='assigned_ftp_accounts', lazy='dynamic',
                                   overlaps="user_assignments,ftp_assignments")
    
    def __repr__(self):
        return f'<FTPAccount {self.name}>'
    
    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'protocol': self.protocol,
            'host': self.host,
            'port': self.port,
            'username': self.username,
            'owner_id': self.owner_id,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }

from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

# Import models after db is defined
from .user import User
from .ftp_account import FTPAccount
from .transfer_log import TransferLog
from .ftp_user_assignment import FTPUserAssignment

__all__ = ['db', 'User', 'FTPAccount', 'TransferLog', 'FTPUserAssignment']

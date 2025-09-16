from .auth_controller import auth_bp
from .dashboard_controller import dashboard_bp
from .admin_controller import admin_bp
from .ftp_controller import ftp_bp
from .api_controller import api_bp

__all__ = ['auth_bp', 'dashboard_bp', 'admin_bp', 'ftp_bp', 'api_bp']

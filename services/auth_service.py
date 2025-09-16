from werkzeug.security import generate_password_hash, check_password_hash
from models import User
from models import db
from flask_login import login_user, logout_user
from datetime import datetime

class AuthService:
    @staticmethod
    def create_user(username, password, role='user'):
        """Yeni kullanıcı oluşturur"""
        if User.query.filter_by(username=username).first():
            return None, "Bu kullanıcı adı zaten kullanılıyor"
        
        user = User(
            username=username,
            password_hash=generate_password_hash(password, method='pbkdf2:sha256'),
            role=role
        )
        
        try:
            db.session.add(user)
            db.session.commit()
            return user, "Kullanıcı başarıyla oluşturuldu"
        except Exception as e:
            db.session.rollback()
            return None, f"Kullanıcı oluşturulurken hata oluştu: {str(e)}"
    
    @staticmethod
    def authenticate_user(username, password):
        """Kullanıcı kimlik doğrulaması yapar"""
        user = User.query.filter_by(username=username).first()
        
        if user and check_password_hash(user.password_hash, password):
            return user, "Giriş başarılı"
        else:
            return None, "Geçersiz kullanıcı adı veya şifre"
    
    @staticmethod
    def login_user_session(user):
        """Kullanıcıyı oturuma alır"""
        try:
            login_user(user)
            return True, "Oturum açıldı"
        except Exception as e:
            return False, f"Oturum açılırken hata oluştu: {str(e)}"
    
    @staticmethod
    def logout_user_session():
        """Kullanıcıyı oturumdan çıkarır"""
        try:
            logout_user()
            return True, "Oturum kapatıldı"
        except Exception as e:
            return False, f"Oturum kapatılırken hata oluştu: {str(e)}"
    
    @staticmethod
    def get_user_by_id(user_id):
        """ID'ye göre kullanıcı getirir"""
        return User.query.get(user_id)
    
    @staticmethod
    def get_user_by_username(username):
        """Kullanıcı adına göre kullanıcı getirir"""
        return User.query.filter_by(username=username).first()
    
    @staticmethod
    def is_admin(user):
        """Kullanıcının admin olup olmadığını kontrol eder"""
        return user and user.role == 'admin'

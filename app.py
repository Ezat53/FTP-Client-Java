from flask import Flask, request, flash, redirect, url_for
from flask_login import LoginManager
from models import db, User
from controllers import auth_bp, dashboard_bp, admin_bp, ftp_bp, api_bp
from config import config
import os
import logging

# Werkzeug hash metodunu pbkdf2:sha256 olarak ayarla (scrypt yerine)
import werkzeug.security
werkzeug.security.DEFAULT_PBKDF2_ITERATIONS = 100000

def create_app(config_name=None):
    app = Flask(__name__)
    
    # Load configuration
    config_name = config_name or os.environ.get('FLASK_ENV', 'default')
    app.config.from_object(config[config_name])
    
    # Configure logging
    logging.basicConfig(level=logging.INFO)
    logger = logging.getLogger(__name__)
    
    # Initialize extensions
    db.init_app(app)
    
    # Initialize Flask-Login
    login_manager = LoginManager()
    login_manager.init_app(app)
    login_manager.login_view = 'auth.login'
    login_manager.login_message = 'Bu sayfaya erişmek için giriş yapmalısınız.'
    login_manager.login_message_category = 'info'
    
    @login_manager.user_loader
    def load_user(user_id):
        return db.session.get(User, int(user_id))
    
    # Create upload directory if it doesn't exist
    os.makedirs(app.config['UPLOAD_FOLDER'], exist_ok=True)
    
    # Register blueprints
    app.register_blueprint(auth_bp)
    app.register_blueprint(dashboard_bp)
    app.register_blueprint(admin_bp)
    app.register_blueprint(ftp_bp)
    app.register_blueprint(api_bp)
    
    # Favicon route
    @app.route('/favicon.ico')
    def favicon():
        return '', 204  # No content
    
    # Error handlers
    @app.errorhandler(404)
    def not_found_error(error):
        return app.send_static_file('404.html'), 404
    
    @app.errorhandler(500)
    def internal_error(error):
        db.session.rollback()
        return app.send_static_file('500.html'), 500
    
    @app.errorhandler(413)
    def too_large(error):
        flash('Dosya boyutu çok büyük. Maksimum 16MB yükleyebilirsiniz.', 'error')
        return redirect(request.url)
    
    # Initialize database and create admin user
    with app.app_context():
        db.create_all()
        
        # Create admin user if not exists or recreate if hash is incompatible
        from werkzeug.security import generate_password_hash
        admin_user = User.query.filter_by(username='admin').first()
        
        if not admin_user:
            # Create new admin user
            admin = User(
                username='admin',
                password_hash=generate_password_hash('admin123', method='pbkdf2:sha256'),
                role='admin'
            )
            db.session.add(admin)
            db.session.commit()
            print("Admin kullanıcısı oluşturuldu: admin/admin123")
        else:
            # Update existing admin user's password hash if it's using scrypt
            if admin_user.password_hash.startswith('scrypt:'):
                admin_user.password_hash = generate_password_hash('admin123', method='pbkdf2:sha256')
                db.session.commit()
                print("Admin kullanıcısının şifresi güncellendi (pbkdf2:sha256)")
    
    return app

if __name__ == '__main__':
    app = create_app()
    app.run(debug=True, host='0.0.0.0', port=6756)

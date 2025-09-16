from flask import Blueprint, render_template, request, redirect, url_for, flash, current_app
from flask_login import login_user, logout_user, login_required, current_user
from services import AuthService
from models import db

auth_bp = Blueprint('auth', __name__)

@auth_bp.route('/')
def index():
    if current_user.is_authenticated:
        return redirect(url_for('dashboard.index'))
    return render_template('index.html')

@auth_bp.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        
        user, message = AuthService.authenticate_user(username, password)
        
        if user:
            success, login_message = AuthService.login_user_session(user)
            if success:
                flash('Giriş başarılı', 'success')
                return redirect(url_for('dashboard.index'))
            else:
                flash(login_message, 'error')
        else:
            flash(message, 'error')
    
    return render_template('login.html')

@auth_bp.route('/logout')
@login_required
def logout():
    success, message = AuthService.logout_user_session()
    if success:
        flash('Çıkış yapıldı', 'success')
    else:
        flash(message, 'error')
    return redirect(url_for('auth.index'))

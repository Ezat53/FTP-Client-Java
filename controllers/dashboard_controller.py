from flask import Blueprint, render_template, request, redirect, url_for, flash
from flask_login import login_required, current_user
from services import FTPService, TransferService

dashboard_bp = Blueprint('dashboard', __name__)

@dashboard_bp.route('/dashboard')
@login_required
def index():
    ftp_accounts = FTPService.get_user_accounts(current_user.id)
    recent_logs = TransferService.get_user_transfers(current_user.id, 10)
    return render_template('dashboard.html', ftp_accounts=ftp_accounts, recent_logs=recent_logs)

# FTP hesabı ekleme özelliği kaldırıldı - sadece admin ekleyebilir

@dashboard_bp.route('/browse/<int:account_id>')
@login_required
def browse_files(account_id):
    account = FTPService.get_account_by_id(account_id)
    
    if not account:
        flash('FTP hesabı bulunamadı', 'error')
        return redirect(url_for('dashboard.index'))
    
    # Check if user has access to this account
    assigned_users = FTPService.get_account_assignments(account_id)
    if current_user not in assigned_users:
        flash('Bu hesaba erişim yetkiniz yok', 'error')
        return redirect(url_for('dashboard.index'))
    
    try:
        files = []
        if account.protocol == 'ftp':
            files, error = FTPService.list_ftp_files(account)
        elif account.protocol == 'sftp':
            files, error = FTPService.list_sftp_files(account)
        else:
            flash('Desteklenmeyen protokol', 'error')
            return redirect(url_for('dashboard.index'))
        
        if error:
            flash(f'Dosya listesi alınırken hata oluştu: {error}', 'error')
            return redirect(url_for('dashboard.index'))
        
        return render_template('browse.html', account=account, files=files)
    except Exception as e:
        flash(f'Dosya listesi alınırken hata oluştu: {str(e)}', 'error')
        return redirect(url_for('dashboard.index'))

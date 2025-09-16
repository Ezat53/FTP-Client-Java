from flask import Blueprint, render_template, request, redirect, url_for, flash
from flask_login import login_required, current_user
from services import UserService, FTPService, TransferService, AuthService
from functools import wraps

admin_bp = Blueprint('admin', __name__)

def admin_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if not current_user.is_authenticated or current_user.role != 'admin':
            flash('Bu işlem için admin yetkisi gerekli', 'error')
            return redirect(url_for('dashboard.index'))
        return f(*args, **kwargs)
    return decorated_function

@admin_bp.route('/admin')
@login_required
@admin_required
def admin_panel():
    try:
        # Debug: Test each service individually
        print("Testing UserService...")
        users = UserService.get_all_users()
        print(f"Users loaded: {len(users)} users")
        
        # Convert users to dictionaries for JSON serialization
        users_data = [user.to_dict() for user in users]
        
        print("Testing FTPService...")
        all_accounts = FTPService.get_accounts_with_users()
        print(f"Accounts loaded: {len(all_accounts)} accounts")
        
        print("Testing TransferService...")
        transfer_stats = TransferService.get_transfer_stats()
        print(f"Transfer stats: {transfer_stats}")
        
        # Get recent transfers with user info
        recent_transfers = TransferService.get_all_transfers(10)
        print(f"Recent transfers: {len(recent_transfers)} transfers")
        
        return render_template('admin.html', 
                             users=users, 
                             users_data=users_data,
                             all_accounts=all_accounts,
                             recent_transfers=recent_transfers,
                             total_transfers=transfer_stats['total_transfers'],
                             successful_transfers=transfer_stats['successful_transfers'])
    except Exception as e:
        import traceback
        print(f"Error details: {str(e)}")
        print(f"Traceback: {traceback.format_exc()}")
        flash(f'Admin panel yüklenirken hata oluştu: {str(e)}', 'error')
        return redirect(url_for('dashboard.index'))

@admin_bp.route('/admin/add-user', methods=['GET', 'POST'])
@login_required
@admin_required
def add_user():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        role = request.form.get('role', 'user')
        
        user, message = AuthService.create_user(username, password, role)
        
        if user:
            flash(message, 'success')
            return redirect(url_for('admin.admin_panel'))
        else:
            flash(message, 'error')
    
    return render_template('add_user.html')

@admin_bp.route('/admin/add-ftp', methods=['GET', 'POST'])
@login_required
@admin_required
def add_ftp():
    if request.method == 'POST':
        name = request.form['name']
        protocol = request.form['protocol']
        host = request.form['host']
        port = int(request.form['port'])
        username = request.form['username']
        password = request.form['password']
        user_ids = request.form.getlist('user_ids')  # Multiple user selection

        if user_ids:
            user_ids = [int(uid) for uid in user_ids if uid]

        account, message = FTPService.create_account(
            name, protocol, host, port, username, password, current_user.id, user_ids
        )
        
        if account:
            flash(message, 'success')
            return redirect(url_for('admin.admin_panel'))
        else:
            flash(message, 'error')
    
    users = UserService.get_all_users()
    return render_template('admin_add_ftp.html', users=users)

@admin_bp.route('/admin/assign-ftp/<int:account_id>', methods=['POST'])
@login_required
@admin_required
def assign_ftp(account_id):
    user_ids = request.form.getlist('user_ids')
    if not user_ids:
        flash('Kullanıcı seçilmedi', 'error')
        return redirect(url_for('admin.admin_panel'))

    user_ids = [int(uid) for uid in user_ids if uid]
    success, message = FTPService.assign_account_to_users(account_id, user_ids, current_user.id)
    if success:
        flash(message, 'success')
    else:
        flash(message, 'error')

    return redirect(url_for('admin.admin_panel'))

@admin_bp.route('/admin/unassign-ftp/<int:account_id>/<int:user_id>', methods=['POST'])
@login_required
@admin_required
def unassign_ftp(account_id, user_id):
    success, message = FTPService.unassign_account_from_user(account_id, user_id)
    if success:
        flash(message, 'success')
    else:
        flash(message, 'error')
    
    return redirect(url_for('admin.admin_panel'))

@admin_bp.route('/admin/unassign-all-ftp/<int:account_id>', methods=['POST'])
@login_required
@admin_required
def unassign_all_ftp(account_id):
    success, message = FTPService.unassign_account_from_all_users(account_id)
    if success:
        flash(message, 'success')
    else:
        flash(message, 'error')
    
    return redirect(url_for('admin.admin_panel'))

@admin_bp.route('/admin/edit-ftp/<int:account_id>', methods=['GET', 'POST'])
@login_required
@admin_required
def edit_ftp(account_id):
    account = FTPService.get_account_by_id(account_id)
    if not account:
        flash('FTP hesabı bulunamadı', 'error')
        return redirect(url_for('admin.admin_panel'))
    
    if request.method == 'POST':
        name = request.form['name']
        protocol = request.form['protocol']
        host = request.form['host']
        port = int(request.form['port'])
        username = request.form['username']
        password = request.form['password']
        user_ids = request.form.getlist('user_ids')  # Multiple user selection
        
        if user_ids:
            user_ids = [int(uid) for uid in user_ids if uid]
        else:
            user_ids = []
        
        success, message = FTPService.update_account(
            account_id, name, protocol, host, port, username, password, user_ids
        )
        
        if success:
            flash(message, 'success')
            return redirect(url_for('admin.admin_panel'))
        else:
            flash(message, 'error')
    
    users = UserService.get_all_users()
    assigned_users = FTPService.get_account_assignments(account_id)
    assigned_user_ids = [user.id for user in assigned_users]
    return render_template('admin_edit_ftp.html', account=account, users=users, assigned_user_ids=assigned_user_ids)

@admin_bp.route('/admin/edit-user/<int:user_id>', methods=['GET', 'POST'])
@login_required
@admin_required
def edit_user(user_id):
    if user_id == current_user.id:
        flash('Kendi hesabınızı buradan düzenleyemezsiniz', 'error')
        return redirect(url_for('admin.admin_panel'))
    
    user = UserService.get_user_by_id(user_id)
    if not user:
        flash('Kullanıcı bulunamadı', 'error')
        return redirect(url_for('admin.admin_panel'))
    
    if request.method == 'POST':
        username = request.form['username']
        role = request.form['role']
        password = request.form.get('password')
        
        success, message = UserService.update_user(user_id, username, role, password)
        
        if success:
            flash(message, 'success')
            return redirect(url_for('admin.admin_panel'))
        else:
            flash(message, 'error')
    
    return render_template('admin_edit_user.html', user=user)

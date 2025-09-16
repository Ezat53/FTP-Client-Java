from flask import Blueprint, jsonify
from flask_login import login_required, current_user
from services import FTPService, UserService

api_bp = Blueprint('api', __name__)

@api_bp.route('/api/account/<int:account_id>', methods=['DELETE'])
@login_required
def delete_account(account_id):
    account = FTPService.get_account_by_id(account_id)
    if not account or (account.owner_id != current_user.id and current_user.role != 'admin'):
        return jsonify({'success': False, 'message': 'Bu hesaba erişim yetkiniz yok'})
    
    success, message = FTPService.delete_account(account_id)
    return jsonify({'success': success, 'message': message})

@api_bp.route('/api/admin/user/<int:user_id>', methods=['DELETE'])
@login_required
def delete_user(user_id):
    if current_user.role != 'admin':
        return jsonify({'success': False, 'message': 'Bu işlem için admin yetkisi gerekli'})
    
    if user_id == current_user.id:
        return jsonify({'success': False, 'message': 'Kendi hesabınızı silemezsiniz'})
    
    success, message = UserService.delete_user(user_id)
    return jsonify({'success': success, 'message': message})

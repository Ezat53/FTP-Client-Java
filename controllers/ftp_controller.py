from flask import Blueprint, render_template, request, redirect, url_for, flash, jsonify
from flask_login import login_required, current_user
from werkzeug.utils import secure_filename
import os
from services import FTPService, TransferService
from functools import wraps

ftp_bp = Blueprint('ftp', __name__)

def validate_file_type(filename):
    allowed_extensions = {'txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif', 'doc', 'docx', 'xls', 'xlsx', 'zip', 'rar', 'mp4', 'avi', 'mov', 'mp3', 'wav', 'csv', 'json', 'xml', 'html', 'css', 'js', 'py', 'java', 'cpp', 'c', 'sql', 'log'}
    if '.' not in filename:
        return False
    extension = filename.rsplit('.', 1)[1].lower()
    return extension in allowed_extensions

@ftp_bp.route('/api/upload/<int:account_id>', methods=['POST'])
@login_required
def upload_file(account_id):
    account = FTPService.get_account_by_id(account_id)
    if not account:
        return jsonify({'success': False, 'message': 'FTP hesabı bulunamadı'})
    
    # Check if user has access to this account
    assigned_users = FTPService.get_account_assignments(account_id)
    if current_user not in assigned_users:
        return jsonify({'success': False, 'message': 'Bu hesaba erişim yetkiniz yok'})
    
    if 'file' not in request.files:
        return jsonify({'success': False, 'message': 'Dosya seçilmedi'})
    
    file = request.files['file']
    if file.filename == '':
        return jsonify({'success': False, 'message': 'Dosya seçilmedi'})
    
    try:
        filename = secure_filename(file.filename)
        
        # Validate file type
        if not validate_file_type(filename):
            return jsonify({'success': False, 'message': 'Desteklenmeyen dosya türü'})
        
        file_path = os.path.join('uploads', filename)
        file.save(file_path)
        
        # Upload to FTP/SFTP server
        if account.protocol == 'ftp':
            success, error = FTPService.upload_to_ftp(account, file_path, filename)
        elif account.protocol == 'sftp':
            success, error = FTPService.upload_to_sftp(account, file_path, filename)
        else:
            return jsonify({'success': False, 'message': 'Desteklenmeyen protokol'})
        
        if success:
            # Log the transfer
            TransferService.log_transfer(current_user.id, account_id, 'upload', filename, os.path.getsize(file_path), 'success')
            
            # Clean up local file
            os.remove(file_path)
            
            return jsonify({'success': True, 'message': 'Dosya başarıyla yüklendi'})
        else:
            # Clean up local file if exists
            if os.path.exists(file_path):
                os.remove(file_path)
            
            TransferService.log_transfer(current_user.id, account_id, 'upload', filename, 0, 'error', error)
            return jsonify({'success': False, 'message': error})
    except Exception as e:
        # Clean up local file if exists
        if 'file_path' in locals() and os.path.exists(file_path):
            os.remove(file_path)
        
        TransferService.log_transfer(current_user.id, account_id, 'upload', filename, 0, 'error', str(e))
        return jsonify({'success': False, 'message': str(e)})

@ftp_bp.route('/api/download/<int:account_id>/<filename>')
@login_required
def download_file(account_id, filename):
    account = FTPService.get_account_by_id(account_id)
    if not account:
        return jsonify({'success': False, 'message': 'FTP hesabı bulunamadı'})
    
    # Check if user has access to this account
    assigned_users = FTPService.get_account_assignments(account_id)
    if current_user not in assigned_users:
        return jsonify({'success': False, 'message': 'Bu hesaba erişim yetkiniz yok'})
    
    try:
        if account.protocol == 'ftp':
            file_data, error = FTPService.download_from_ftp(account, filename)
        elif account.protocol == 'sftp':
            file_data, error = FTPService.download_from_sftp(account, filename)
        else:
            return jsonify({'success': False, 'message': 'Desteklenmeyen protokol'})
        
        if file_data is not None:
            TransferService.log_transfer(current_user.id, account_id, 'download', filename, len(file_data), 'success')
            
            from flask import Response
            return Response(
                file_data,
                mimetype='application/octet-stream',
                headers={'Content-Disposition': f'attachment; filename={filename}'}
            )
        else:
            TransferService.log_transfer(current_user.id, account_id, 'download', filename, 0, 'error', error)
            return jsonify({'success': False, 'message': error})
    except Exception as e:
        TransferService.log_transfer(current_user.id, account_id, 'download', filename, 0, 'error', str(e))
        return jsonify({'success': False, 'message': str(e)})

@ftp_bp.route('/api/delete/<int:account_id>/<filename>', methods=['DELETE'])
@login_required
def delete_file(account_id, filename):
    # Only admin users can delete files
    if current_user.role != 'admin':
        return jsonify({'success': False, 'message': 'Dosya silme yetkiniz yok. Sadece admin kullanıcıları dosya silebilir.'})
    
    account = FTPService.get_account_by_id(account_id)
    if not account:
        return jsonify({'success': False, 'message': 'FTP hesabı bulunamadı'})
    
    # Check if admin has access to this account (admin can delete from any account they own)
    if account.owner_id != current_user.id:
        return jsonify({'success': False, 'message': 'Bu hesaptan dosya silme yetkiniz yok'})
    
    try:
        if account.protocol == 'ftp':
            success, error = FTPService.delete_from_ftp(account, filename)
        elif account.protocol == 'sftp':
            success, error = FTPService.delete_from_sftp(account, filename)
        else:
            return jsonify({'success': False, 'message': 'Desteklenmeyen protokol'})
        
        if success:
            TransferService.log_transfer(current_user.id, account_id, 'delete', filename, 0, 'success')
            return jsonify({'success': True, 'message': 'Dosya başarıyla silindi'})
        else:
            TransferService.log_transfer(current_user.id, account_id, 'delete', filename, 0, 'error', error)
            return jsonify({'success': False, 'message': error})
    except Exception as e:
        TransferService.log_transfer(current_user.id, account_id, 'delete', filename, 0, 'error', str(e))
        return jsonify({'success': False, 'message': str(e)})

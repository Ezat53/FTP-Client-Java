// Main JavaScript for FTP Client Application

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Initialize file upload handlers
    initializeFileUpload();
    
    // Initialize form validation
    initializeFormValidation();
});

// File upload functionality
function initializeFileUpload() {
    const fileInputs = document.querySelectorAll('input[type="file"]');
    
    fileInputs.forEach(function(input) {
        input.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                // Validate file size (16MB limit)
                const maxSize = 16 * 1024 * 1024; // 16MB in bytes
                if (file.size > maxSize) {
                    showAlert('Dosya boyutu çok büyük. Maksimum 16MB yükleyebilirsiniz.', 'danger');
                    input.value = '';
                    return;
                }
                
                // Validate file type
                const allowedTypes = [
                    'txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif', 'doc', 'docx', 'xls', 'xlsx',
                    'zip', 'rar', 'mp4', 'avi', 'mov', 'mp3', 'wav', 'csv', 'json', 'xml',
                    'html', 'css', 'js', 'py', 'java', 'cpp', 'c', 'sql', 'log'
                ];
                
                const fileExtension = file.name.split('.').pop().toLowerCase();
                if (!allowedTypes.includes(fileExtension)) {
                    showAlert('Desteklenmeyen dosya türü: ' + fileExtension, 'danger');
                    input.value = '';
                    return;
                }
                
                showAlert('Dosya seçildi: ' + file.name, 'success');
            }
        });
    });
}

// Form validation
function initializeFormValidation() {
    const forms = document.querySelectorAll('.needs-validation');
    
    forms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
                showAlert('Lütfen tüm gerekli alanları doldurun.', 'danger');
            }
            
            form.classList.add('was-validated');
        });
    });
}

// Show alert message
function showAlert(message, type) {
    const alertContainer = document.querySelector('.container');
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        <i class="fas fa-${type === 'danger' ? 'exclamation-triangle' : 'check-circle'} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    alertContainer.insertBefore(alertDiv, alertContainer.firstChild);
    
    // Auto-hide after 5 seconds
    setTimeout(function() {
        const bsAlert = new bootstrap.Alert(alertDiv);
        bsAlert.close();
    }, 5000);
}

// Confirm dialog
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

// Loading state
function setLoading(element, loading) {
    if (loading) {
        element.classList.add('loading');
        element.disabled = true;
    } else {
        element.classList.remove('loading');
        element.disabled = false;
    }
}

// Get CSRF token
function getCSRFToken() {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    return { token, header };
}

// AJAX helper
function makeRequest(url, options = {}) {
    const csrf = getCSRFToken();
    const defaultOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    };
    
    // Add CSRF token to headers
    if (csrf.token && csrf.header) {
        defaultOptions.headers[csrf.header] = csrf.token;
    }
    
    const config = { ...defaultOptions, ...options };
    
    return fetch(url, config)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .catch(error => {
            console.error('Request failed:', error);
            showAlert('İstek başarısız: ' + error.message, 'danger');
            throw error;
        });
}

// File operations
function uploadFile(accountId, file, path = null) {
    const formData = new FormData();
    formData.append('file', file);
    
    // Add path parameter if provided
    if (path) {
        formData.append('path', path);
    }
    
    const csrf = getCSRFToken();
    const headers = {};
    
    // Add CSRF token to headers
    if (csrf.token && csrf.header) {
        headers[csrf.header] = csrf.token;
    }
    
    return makeRequest(`/xfer-ftp-web-service/api/upload/${accountId}`, {
        method: 'POST',
        body: formData,
        headers: headers // Let browser set Content-Type for FormData
    });
}

function downloadFile(accountId, filename) {
    window.open(`/xfer-ftp-web-service/api/download/${accountId}/${filename}`, '_blank');
}

function deleteFile(accountId, filename, path = null) {
    const pathParam = path ? `?path=${encodeURIComponent(path)}` : '';
    return makeRequest(`/xfer-ftp-web-service/api/delete/${accountId}/${encodeURIComponent(filename)}${pathParam}`, {
        method: 'DELETE'
    });
}

function listFiles(accountId) {
    return makeRequest(`/xfer-ftp-web-service/api/list/${accountId}`);
}

// FTP Account operations
function deleteFTPAccount(accountId) {
    confirmAction('Bu FTP hesabını silmek istediğinizden emin misiniz?', function() {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/xfer-ftp-web-service/admin/delete-ftp/${accountId}`;
        document.body.appendChild(form);
        form.submit();
    });
}

function deleteUser(userId) {
    confirmAction('Bu kullanıcıyı silmek istediğinizden emin misiniz?', function() {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/xfer-ftp-web-service/admin/delete-user/${userId}`;
        document.body.appendChild(form);
        form.submit();
    });
}

// File browser functionality
function browseFiles(accountId) {
    // Redirect to browse page instead of showing modal
    window.location.href = `/xfer-ftp-web-service/dashboard/browse/${accountId}`;
}


function getFileIcon(filename) {
    const extension = filename.split('.').pop().toLowerCase();
    
    const iconMap = {
        'pdf': 'fas fa-file-pdf',
        'doc': 'fas fa-file-word',
        'docx': 'fas fa-file-word',
        'xls': 'fas fa-file-excel',
        'xlsx': 'fas fa-file-excel',
        'ppt': 'fas fa-file-powerpoint',
        'pptx': 'fas fa-file-powerpoint',
        'txt': 'fas fa-file-alt',
        'jpg': 'fas fa-file-image',
        'jpeg': 'fas fa-file-image',
        'png': 'fas fa-file-image',
        'gif': 'fas fa-file-image',
        'mp4': 'fas fa-file-video',
        'avi': 'fas fa-file-video',
        'mov': 'fas fa-file-video',
        'mp3': 'fas fa-file-audio',
        'wav': 'fas fa-file-audio',
        'zip': 'fas fa-file-archive',
        'rar': 'fas fa-file-archive',
        'html': 'fas fa-file-code',
        'css': 'fas fa-file-code',
        'js': 'fas fa-file-code',
        'py': 'fas fa-file-code',
        'java': 'fas fa-file-code',
        'cpp': 'fas fa-file-code',
        'c': 'fas fa-file-code',
        'sql': 'fas fa-file-code',
        'json': 'fas fa-file-code',
        'xml': 'fas fa-file-code'
    };
    
    return iconMap[extension] || 'fas fa-file';
}


// Utility functions
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Export functions for global access
window.showAlert = showAlert;
window.confirmAction = confirmAction;
window.uploadFile = uploadFile;
window.downloadFile = downloadFile;
window.deleteFile = deleteFile;
window.listFiles = listFiles;
window.browseFiles = browseFiles;
window.deleteFTPAccount = deleteFTPAccount;
window.deleteUser = deleteUser;

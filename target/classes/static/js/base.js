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

console.log('🚀 Add User JavaScript yüklendi!');

// Şifre üretme fonksiyonu
function generateStrongPassword() {
    const length = 16;
    const charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
    let password = "";
    
    // En az bir büyük harf, küçük harf, rakam ve özel karakter garantisi
    password += "ABCDEFGHIJKLMNOPQRSTUVWXYZ"[Math.floor(Math.random() * 26)];
    password += "abcdefghijklmnopqrstuvwxyz"[Math.floor(Math.random() * 26)];
    password += "0123456789"[Math.floor(Math.random() * 10)];
    password += "!@#$%^&*()_+-=[]{}|;:,.<>?"[Math.floor(Math.random() * 25)];
    
    // Kalan karakterleri rastgele doldur
    for (let i = 4; i < length; i++) {
        password += charset[Math.floor(Math.random() * charset.length)];
    }
    
    // Karakterleri karıştır
    return password.split('').sort(() => Math.random() - 0.5).join('');
}

// Şifre üret butonuna tıklama
function generatePassword() {
    console.log('🔑 Şifre üret butonuna tıklandı!');
    try {
        const newPassword = generateStrongPassword();
        console.log('✅ Şifre başarıyla üretildi:', newPassword);
        
        const passwordInput = document.getElementById('password');
        const copyPasswordBtn = document.getElementById('copyPasswordBtn');
        const copyUsernameBtn = document.getElementById('copyUsernameBtn');
        const copyAllBtn = document.getElementById('copyAllBtn');
        
        passwordInput.value = newPassword;
        copyPasswordBtn.style.display = 'inline-block';
        copyUsernameBtn.style.display = 'inline-block';
        copyAllBtn.style.display = 'block';
        
        // Başarı mesajı göster
        showToast('Güçlü şifre üretildi!', 'success');
        console.log('📋 Kopya butonları görünür hale getirildi');
    } catch (error) {
        console.error('❌ Şifre üretilirken hata oluştu:', error);
        showToast('Şifre üretilirken hata oluştu!', 'error');
    }
}

// Şifre kopyalama
function copyPassword() {
    console.log('📋 Şifre kopyalama butonuna tıklandı');
    const passwordInput = document.getElementById('password');
    passwordInput.select();
    passwordInput.setSelectionRange(0, 99999);
    document.execCommand('copy');
    showToast('Şifre panoya kopyalandı!', 'success');
    console.log('✅ Şifre panoya kopyalandı');
}

// Kullanıcı adı kopyalama
function copyUsername() {
    console.log('👤 Kullanıcı adı kopyalama butonuna tıklandı');
    const usernameInput = document.getElementById('username');
    usernameInput.select();
    usernameInput.setSelectionRange(0, 99999);
    document.execCommand('copy');
    showToast('Kullanıcı adı panoya kopyalandı!', 'success');
    console.log('✅ Kullanıcı adı panoya kopyalandı');
}

// Tümünü kopyalama
function copyAll() {
    console.log('📋 Tümünü kopyala butonuna tıklandı');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();
    
    if (!username || !password) {
        console.warn('⚠️ Eksik alan: kullanıcı adı veya şifre boş');
        showToast('Lütfen hem kullanıcı adı hem de şifre alanlarını doldurun!', 'warning');
        return;
    }
    
    const copyText = `Kullanıcı Adı: ${username}\nŞifre: ${password}`;
    console.log('📝 Kopyalanacak metin:', copyText);
    
    // Modern clipboard API kullan (destekleniyorsa)
    if (navigator.clipboard && window.isSecureContext) {
        console.log('🔧 Modern clipboard API kullanılıyor');
        navigator.clipboard.writeText(copyText).then(function() {
            console.log('✅ Modern API ile kopyalama başarılı');
            showToast('Kullanıcı adı ve şifre panoya kopyalandı!', 'success');
        }).catch(function(err) {
            console.error('❌ Modern API kopyalama hatası:', err);
            fallbackCopyTextToClipboard(copyText);
        });
    } else {
        console.log('🔧 Fallback kopyalama yöntemi kullanılıyor');
        fallbackCopyTextToClipboard(copyText);
    }
}

// Fallback kopyalama fonksiyonu
function fallbackCopyTextToClipboard(text) {
    console.log('🔧 Fallback kopyalama fonksiyonu çalışıyor');
    const textArea = document.createElement("textarea");
    textArea.value = text;
    textArea.style.position = "fixed";
    textArea.style.left = "-999999px";
    textArea.style.top = "-999999px";
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    
    try {
        const successful = document.execCommand('copy');
        if (successful) {
            console.log('✅ Fallback kopyalama başarılı');
            showToast('Kullanıcı adı ve şifre panoya kopyalandı!', 'success');
        } else {
            console.error('❌ Fallback kopyalama başarısız');
            showToast('Kopyalama başarısız! Lütfen manuel olarak kopyalayın.', 'error');
        }
    } catch (err) {
        console.error('❌ Fallback kopyalama hatası:', err);
        showToast('Kopyalama başarısız! Lütfen manuel olarak kopyalayın.', 'error');
    }
    
    document.body.removeChild(textArea);
}

// Toast mesajı gösterme fonksiyonu
function showToast(message, type = 'info') {
    const existingToasts = document.querySelectorAll('.toast-notification');
    existingToasts.forEach(toast => toast.remove());

    let alertClass = 'alert-info';
    let iconClass = 'info-circle';
    
    switch(type) {
        case 'success':
            alertClass = 'alert-success';
            iconClass = 'check-circle';
            break;
        case 'warning':
            alertClass = 'alert-warning';
            iconClass = 'exclamation-triangle';
            break;
        case 'error':
            alertClass = 'alert-danger';
            iconClass = 'times-circle';
            break;
        default:
            alertClass = 'alert-info';
            iconClass = 'info-circle';
    }

    const toast = document.createElement('div');
    toast.className = `toast-notification alert ${alertClass} alert-dismissible fade show`;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        min-width: 300px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    `;
    
    toast.innerHTML = `
        <i class="fas fa-${iconClass} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        if (toast.parentNode) {
            toast.remove();
        }
    }, 3000);
}

// Input alanları değiştiğinde kopya butonlarını güncelle
function updateCopyButtons() {
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const copyUsernameBtn = document.getElementById('copyUsernameBtn');
    const copyPasswordBtn = document.getElementById('copyPasswordBtn');
    const copyAllBtn = document.getElementById('copyAllBtn');
    
    if (!usernameInput || !passwordInput) return;
    
    const hasUsername = usernameInput.value.trim().length > 0;
    const hasPassword = passwordInput.value.trim().length > 0;
    
    copyUsernameBtn.style.display = hasUsername ? 'inline-block' : 'none';
    copyPasswordBtn.style.display = hasPassword ? 'inline-block' : 'none';
    copyAllBtn.style.display = (hasUsername && hasPassword) ? 'block' : 'none';
}

// Sayfa yüklendikten sonra event listener'ları ekle
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 DOM yüklendi, event listener\'lar ekleniyor...');
    
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    
    if (usernameInput) {
        usernameInput.addEventListener('input', updateCopyButtons);
    }
    if (passwordInput) {
        passwordInput.addEventListener('input', updateCopyButtons);
    }
    
 
});



// Şifre üretme fonksiyonu
function generateStrongPassword() {
    const length = 16;
    const charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
    let password = "";
    
    // En az bir büyük harf, küçük harf, rakam ve özel karakter garantisi
    password += "ABCDEFGHIJKLMNOPQRSTUVWXYZ"[Math.floor(Math.random() * 26)];
    password += "abcdefghijklmnopqrstuvwxyz"[Math.floor(Math.random() * 26)];
    password += "0123456789"[Math.floor(Math.random() * 10)];
    password += "!@#$%^&*()_+-=[]{}|;:,.<>?"[Math.floor(Math.random() * 25)];
    
    // Kalan karakterleri rastgele doldur
    for (let i = 4; i < length; i++) {
        password += charset[Math.floor(Math.random() * charset.length)];
    }
    
    // Karakterleri karıştır
    return password.split('').sort(() => Math.random() - 0.5).join('');
}

// Şifre üret butonuna tıklama
function generatePassword() {

    try {
        const newPassword = generateStrongPassword();
 
        
        const passwordInput = document.getElementById('password');
        const copyPasswordBtn = document.getElementById('copyPasswordBtn');
        const copyUsernameBtn = document.getElementById('copyUsernameBtn');
        const copyAllBtn = document.getElementById('copyAllBtn');
        
        passwordInput.value = newPassword;
        copyPasswordBtn.style.display = 'inline-block';
        copyUsernameBtn.style.display = 'inline-block';
        copyAllBtn.style.display = 'block';
        
        // Başarı mesajı göster
        showToast('Güçlü şifre üretildi!', 'success');
 
    } catch (error) {
        console.error('❌ Şifre üretilirken hata oluştu:', error);
        showToast('Şifre üretilirken hata oluştu!', 'error');
    }
}

// Şifre kopyalama
function copyPassword() {
    console.log('📋 Şifre kopyalama butonuna tıklandı');
    const passwordInput = document.getElementById('password');
    passwordInput.select();
    passwordInput.setSelectionRange(0, 99999);
    document.execCommand('copy');
    showToast('Şifre panoya kopyalandı!', 'success');

}

// Kullanıcı adı kopyalama
function copyUsername() {
 
    const usernameInput = document.getElementById('username');
    usernameInput.select();
    usernameInput.setSelectionRange(0, 99999);
    document.execCommand('copy');
    showToast('Kullanıcı adı panoya kopyalandı!', 'success');
  
}

// Tümünü kopyalama
function copyAll() {
 
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();
    
    if (!username || !password) {
 
        showToast('Lütfen hem kullanıcı adı hem de şifre alanlarını doldurun!', 'warning');
        return;
    }
    
    const copyText = `Kullanıcı Adı: ${username}\nŞifre: ${password}`;

    
    // Modern clipboard API kullan (destekleniyorsa)
    if (navigator.clipboard && window.isSecureContext) {
  
        navigator.clipboard.writeText(copyText).then(function() {
      
            showToast('Kullanıcı adı ve şifre panoya kopyalandı!', 'success');
        }).catch(function(err) {
            console.error('❌ Modern API kopyalama hatası:', err);
            fallbackCopyTextToClipboard(copyText);
        });
    } else {
        console.log('🔧 Fallback kopyalama yöntemi kullanılıyor');
        fallbackCopyTextToClipboard(copyText);
    }
}

// Fallback kopyalama fonksiyonu
function fallbackCopyTextToClipboard(text) {
    console.log('🔧 Fallback kopyalama fonksiyonu çalışıyor');
    const textArea = document.createElement("textarea");
    textArea.value = text;
    textArea.style.position = "fixed";
    textArea.style.left = "-999999px";
    textArea.style.top = "-999999px";
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    
    try {
        const successful = document.execCommand('copy');
        if (successful) {
         
            showToast('Kullanıcı adı ve şifre panoya kopyalandı!', 'success');
        } else {
            console.error('❌ Fallback kopyalama başarısız');
            showToast('Kopyalama başarısız! Lütfen manuel olarak kopyalayın.', 'error');
        }
    } catch (err) {
        console.error('❌ Fallback kopyalama hatası:', err);
        showToast('Kopyalama başarısız! Lütfen manuel olarak kopyalayın.', 'error');
    }
    
    document.body.removeChild(textArea);
}

// Toast mesajı gösterme fonksiyonu
function showToast(message, type = 'info') {
    const existingToasts = document.querySelectorAll('.toast-notification');
    existingToasts.forEach(toast => toast.remove());

    let alertClass = 'alert-info';
    let iconClass = 'info-circle';
    
    switch(type) {
        case 'success':
            alertClass = 'alert-success';
            iconClass = 'check-circle';
            break;
        case 'warning':
            alertClass = 'alert-warning';
            iconClass = 'exclamation-triangle';
            break;
        case 'error':
            alertClass = 'alert-danger';
            iconClass = 'times-circle';
            break;
        default:
            alertClass = 'alert-info';
            iconClass = 'info-circle';
    }

    const toast = document.createElement('div');
    toast.className = `toast-notification alert ${alertClass} alert-dismissible fade show`;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        min-width: 300px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    `;
    
    toast.innerHTML = `
        <i class="fas fa-${iconClass} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        if (toast.parentNode) {
            toast.remove();
        }
    }, 3000);
}

// Input alanları değiştiğinde kopya butonlarını güncelle
function updateCopyButtons() {
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const copyUsernameBtn = document.getElementById('copyUsernameBtn');
    const copyPasswordBtn = document.getElementById('copyPasswordBtn');
    const copyAllBtn = document.getElementById('copyAllBtn');
    
    if (!usernameInput || !passwordInput) return;
    
    const hasUsername = usernameInput.value.trim().length > 0;
    const hasPassword = passwordInput.value.trim().length > 0;
    
    copyUsernameBtn.style.display = hasUsername ? 'inline-block' : 'none';
    copyPasswordBtn.style.display = hasPassword ? 'inline-block' : 'none';
    copyAllBtn.style.display = (hasUsername && hasPassword) ? 'block' : 'none';
}

// Sayfa yüklendikten sonra event listener'ları ekle
document.addEventListener('DOMContentLoaded', function() {
 
    
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    
    if (usernameInput) {
        usernameInput.addEventListener('input', updateCopyButtons);
    }
    if (passwordInput) {
        passwordInput.addEventListener('input', updateCopyButtons);
    }
    
   
});

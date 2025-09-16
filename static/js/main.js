// Main JavaScript for Xfer

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
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Add fade-in animation to cards
    const cards = document.querySelectorAll('.card');
    cards.forEach(function(card, index) {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        setTimeout(function() {
            card.style.transition = 'all 0.5s ease-in-out';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });
});

// Utility functions
function showLoading(element) {
    if (element) {
        element.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Yükleniyor...';
        element.disabled = true;
    }
}

function hideLoading(element, originalText) {
    if (element) {
        element.innerHTML = originalText;
        element.disabled = false;
    }
}

function showAlert(message, type = 'info') {
    const alertContainer = document.querySelector('.container');
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
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

// File operations
function uploadFile(accountId, file) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('account_id', accountId);
    
    showLoading(document.querySelector('#uploadModal .btn-primary'));
    
    fetch('/api/upload', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        hideLoading(document.querySelector('#uploadModal .btn-primary'), '<i class="fas fa-upload me-2"></i>Yükle');
        
        if (data.success) {
            showAlert('Dosya başarıyla yüklendi', 'success');
            // Close modal and refresh file list
            const modal = bootstrap.Modal.getInstance(document.getElementById('uploadModal'));
            modal.hide();
            setTimeout(() => location.reload(), 1000);
        } else {
            showAlert('Dosya yüklenirken hata oluştu: ' + data.message, 'danger');
        }
    })
    .catch(error => {
        hideLoading(document.querySelector('#uploadModal .btn-primary'), '<i class="fas fa-upload me-2"></i>Yükle');
        showAlert('Dosya yüklenirken hata oluştu: ' + error.message, 'danger');
    });
}

function downloadFile(accountId, filename) {
    showAlert('Dosya indiriliyor...', 'info');
    
    fetch(`/api/download/${accountId}/${filename}`)
    .then(response => {
        if (response.ok) {
            return response.blob();
        }
        throw new Error('Dosya indirilemedi');
    })
    .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        showAlert('Dosya başarıyla indirildi', 'success');
    })
    .catch(error => {
        showAlert('Dosya indirilirken hata oluştu: ' + error.message, 'danger');
    });
}

function deleteFile(accountId, filename) {
    if (confirm('Bu dosyayı silmek istediğinizden emin misiniz?')) {
        fetch(`/api/delete/${accountId}/${filename}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showAlert('Dosya başarıyla silindi', 'success');
                setTimeout(() => location.reload(), 1000);
            } else {
                showAlert('Dosya silinirken hata oluştu: ' + data.message, 'danger');
            }
        })
        .catch(error => {
            showAlert('Dosya silinirken hata oluştu: ' + error.message, 'danger');
        });
    }
}

// Account operations
function deleteAccount(accountId) {
    if (confirm('Bu hesabı silmek istediğinizden emin misiniz?')) {
        fetch(`/api/account/${accountId}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showAlert('Hesap başarıyla silindi', 'success');
                setTimeout(() => location.reload(), 1000);
            } else {
                showAlert('Hesap silinirken hata oluştu: ' + data.message, 'danger');
            }
        })
        .catch(error => {
            showAlert('Hesap silinirken hata oluştu: ' + error.message, 'danger');
        });
    }
}

// Form validation
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.classList.add('is-invalid');
            isValid = false;
        } else {
            field.classList.remove('is-invalid');
        }
    });
    
    return isValid;
}

// Auto-save form data to localStorage
function autoSaveForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return;
    
    const inputs = form.querySelectorAll('input, select, textarea');
    inputs.forEach(input => {
        const key = `${formId}_${input.name}`;
        
        // Load saved value
        const savedValue = localStorage.getItem(key);
        if (savedValue) {
            input.value = savedValue;
        }
        
        // Save on change
        input.addEventListener('input', function() {
            localStorage.setItem(key, this.value);
        });
    });
}

// Clear form data from localStorage
function clearFormData(formId) {
    const form = document.getElementById(formId);
    if (!form) return;
    
    const inputs = form.querySelectorAll('input, select, textarea');
    inputs.forEach(input => {
        const key = `${formId}_${input.name}`;
        localStorage.removeItem(key);
    });
}

// Initialize auto-save for forms
document.addEventListener('DOMContentLoaded', function() {
    autoSaveForm('loginForm');
    autoSaveForm('registerForm');
    autoSaveForm('addAccountForm');
});

// Clear form data on successful submission
document.addEventListener('submit', function(e) {
    if (e.target.id) {
        setTimeout(() => clearFormData(e.target.id), 1000);
    }
});

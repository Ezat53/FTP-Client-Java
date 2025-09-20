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
function copyToClipboard(text, button) {
    // Modern clipboard API kullan
    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text).then(function() {
            showCopySuccess(button);
        }).catch(function(err) {
            console.error('Kopyalama başarısız: ', err);
            fallbackCopyTextToClipboard(text, button);
        });
    } else {
        // Fallback for older browsers
        fallbackCopyTextToClipboard(text, button);
    }
}

function fallbackCopyTextToClipboard(text, button) {
    var textArea = document.createElement("textarea");
    textArea.value = text;
    
    // Avoid scrolling to bottom
    textArea.style.top = "0";
    textArea.style.left = "0";
    textArea.style.position = "fixed";
    textArea.style.opacity = "0";
    
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    
    try {
        var successful = document.execCommand('copy');
        if (successful) {
            showCopySuccess(button);
        } else {
            console.error('Fallback: Kopyalama başarısız');
        }
    } catch (err) {
        console.error('Fallback: Kopyalama başarısız: ', err);
    }
    
    document.body.removeChild(textArea);
}

function showCopySuccess(button) {
    var originalIcon = button.innerHTML;
    button.innerHTML = '<i class="fas fa-check"></i>';
    button.classList.add('btn-success');
    button.classList.remove('btn-outline-secondary');
    
    setTimeout(function() {
        button.innerHTML = originalIcon;
        button.classList.remove('btn-success');
        button.classList.add('btn-outline-secondary');
    }, 2000);
}
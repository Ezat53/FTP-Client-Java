function showUploadModal() {
    new bootstrap.Modal(document.getElementById('uploadModal')).show();
}

function uploadFileLocal() {
    const form = document.getElementById('uploadForm');
    const formData = new FormData(form);
    
    // Get account ID from hidden input
    const accountIdInput = document.getElementById('accountId');
    const pathInput = form.querySelector('input[name="path"]');
    
    if (!accountIdInput || !pathInput) {
        alert('Form bilgileri alınamadı. Sayfayı yenileyin.');
        return;
    }
    
    const accountId = accountIdInput.value;
    const currentPath = pathInput.value;
    
    console.log('Upload - Account ID:', accountId);
    console.log('Upload path:', currentPath);
    
    // Get CSRF token
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    
    fetch('/xfer-ftp-web-service/api/upload/' + accountId, {
        method: 'POST',
        headers: {
            [header]: token
        },
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.reload();
        } else {
            alert('Hata: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Upload error:', error);
        alert('Dosya yüklenirken bir hata oluştu: ' + error.message);
    });
}

function deleteFileLocal(filename) {
    if (confirm('Bu dosyayı silmek istediğinizden emin misiniz?')) {
        // Get account ID from hidden input in upload form
        const accountIdInput = document.getElementById('accountId');
        const pathInput = document.querySelector('#uploadForm input[name="path"]');
        
        if (!accountIdInput || !pathInput) {
            alert('Hesap bilgileri alınamadı. Sayfayı yenileyin.');
            return;
        }
        
        const accountId = accountIdInput.value;
        const currentPath = pathInput.value;
        
        console.log('Delete - Account ID:', accountId, 'Path:', currentPath);
        
        // Get CSRF token
        const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        
        // Get current path parameter
        const pathParam = currentPath && currentPath !== '/' ? '?path=' + encodeURIComponent(currentPath) : '';
        
        fetch('/xfer-ftp-web-service/api/delete/' + accountId + '/' + encodeURIComponent(filename) + pathParam, {
            method: 'DELETE',
            headers: {
                [header]: token
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                location.reload();
            } else {
                alert('Hata: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Delete error:', error);
            alert('Dosya silinirken bir hata oluştu: ' + error.message);
        });
    }
}

function navigateToDirectory(dirName) {
    const accountIdInput = document.getElementById('accountId');
    if (!accountIdInput) {
        alert('Hesap bilgileri alınamadı. Sayfayı yenileyin.');
        return;
    }
    
    const accountId = accountIdInput.value;
    const urlCurrentPath = new URLSearchParams(window.location.search).get('path') || '';
    const remotePath = window.remotePath || '/';
    
    let newPath;
    if (urlCurrentPath) {
        newPath = urlCurrentPath + '/' + dirName;
    } else {
        newPath = dirName;
    }
    
    // Ensure the new path starts with the remote path
    if (!newPath.startsWith(remotePath)) {
        newPath = remotePath + newPath;
    }
    
    window.location.href = '/xfer-ftp-web-service/dashboard/browse/' + accountId + '?path=' + encodeURIComponent(newPath);
}

function goBack() {
    const accountIdInput = document.getElementById('accountId');
    if (!accountIdInput) {
        alert('Hesap bilgileri alınamadı. Sayfayı yenileyin.');
        return;
    }
    
    const accountId = accountIdInput.value;
    const urlCurrentPath = new URLSearchParams(window.location.search).get('path') || '';
    const remotePath = window.remotePath || '/';
    
    if (urlCurrentPath) {
        const pathParts = urlCurrentPath.split('/');
        pathParts.pop(); // Remove last directory
        const newPath = pathParts.join('/');
        
        // Check if the new path would be above the remote path
        if (newPath && newPath !== '/' && !newPath.startsWith(remotePath)) {
            // If trying to go above remote path, stay at remote path
            window.location.href = '/xfer-ftp-web-service/dashboard/browse/' + accountId + '?path=' + encodeURIComponent(remotePath);
        } else {
            window.location.href = '/xfer-ftp-web-service/dashboard/browse/' + accountId + (newPath ? '?path=' + encodeURIComponent(newPath) : '');
        }
    }
}

function refreshFiles() {
    location.reload();
}

// Tarih sıralama fonksiyonu
let sortOrder = 'asc'; // Varsayılan: yeniden eskiye (yeni tarih önce)

function sortByDate() {
    const tbody = document.querySelector('tbody');
    if (!tbody) return;
    
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    // Klasörleri ve dosyaları ayır
    const directories = rows.filter(row => row.querySelector('.fa-folder'));
    const files = rows.filter(row => row.querySelector('.fa-file:not(.fa-folder)'));
    
    // Klasörleri tarihe göre sırala (yeni tarih önce)
    directories.sort((a, b) => {
        const dateA = getDateFromRow(a);
        const dateB = getDateFromRow(b);
        
        if (sortOrder === 'asc') {
            return dateB - dateA; // Yeniden eskiye (yeni tarih önce)
        } else {
            return dateA - dateB; // Eskiye yeniden (eski tarih önce)
        }
    });
    
    // Dosyaları tarihe göre sırala
    files.sort((a, b) => {
        const dateA = getDateFromRow(a);
        const dateB = getDateFromRow(b);
        
        if (sortOrder === 'asc') {
            return dateB - dateA; // Yeniden eskiye (yeni tarih önce)
        } else {
            return dateA - dateB; // Eskiye yeniden (eski tarih önce)
        }
    });
    
    // Klasörleri önce, sonra dosyaları sıralı şekilde ekle
    const sortedRows = [...directories, ...files];
    
    // Tabloyu yeniden oluştur
    tbody.innerHTML = '';
    sortedRows.forEach(row => tbody.appendChild(row));
    
    // Sıralama yönünü değiştir
    sortOrder = sortOrder === 'desc' ? 'asc' : 'desc';
    
    // İkonu güncelle
    const sortIcon = document.getElementById('dateSortIcon');
    if (sortIcon) {
        sortIcon.className = sortOrder === 'asc' ? 'fas fa-sort-down' : 'fas fa-sort-up';
    }
}

// Satırdan tarih bilgisini çıkaran yardımcı fonksiyon
function getDateFromRow(row) {
    const dateCell = row.cells[2]; // Tarih sütunu
    const dateText = dateCell.textContent.trim();
    
    // "Bilinmiyor" durumunu kontrol et
    if (dateText === 'Bilinmiyor') {
        return 0; // En eski olarak kabul et
    }
    
    // Tarih formatını parse et (dd.MM.yyyy HH:mm)
    const dateMatch = dateText.match(/(\d{2})\.(\d{2})\.(\d{4})\s+(\d{2}):(\d{2})/);
    if (dateMatch) {
        const [, day, month, year, hour, minute] = dateMatch;
        return new Date(year, month - 1, day, hour, minute).getTime();
    }
    
    return 0; // Parse edilemezse en eski olarak kabul et
}

// Show/hide back button based on current path and remote path restriction
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = new URLSearchParams(window.location.search).get('path');
    const remotePath = window.remotePath || '/';
    const backBtn = document.getElementById('backBtn');
    
    // Show back button only if we're not at the remote path root
    if (currentPath && currentPath !== remotePath && currentPath !== '/' && currentPath !== '' && backBtn) {
        // Check if we can go back without going above the remote path
        const pathParts = currentPath.split('/');
        pathParts.pop(); // Remove last directory
        const parentPath = pathParts.join('/');
        
        // Only show back button if parent path is still within or at the remote path
        if (parentPath.startsWith(remotePath) || parentPath === remotePath) {
            backBtn.style.display = 'inline-block';
        }
    }
    
    // Sayfa yüklendiğinde otomatik olarak yeniden eskiye sırala
    sortByDate();
});
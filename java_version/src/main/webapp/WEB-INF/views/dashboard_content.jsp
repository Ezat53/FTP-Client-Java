<div class="row">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>
                <i class="fas fa-tachometer-alt me-2"></i>Dashboard
            </h2>
            <div>
                <c:if test="${currentUser.role == 'admin'}">
                <a href="<c:url value='/admin'/>" class="btn btn-primary">
                    <i class="fas fa-cogs me-2"></i>Admin Panel
                </a>
                </c:if>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">
                    <i class="fas fa-server me-2"></i>FTP Hesaplarım
                </h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty ftpAccounts}">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>Hesap Adı</th>
                                        <th>Protokol</th>
                                        <th>Host</th>
                                        <th>Port</th>
                                        <th>Kullanıcı Adı</th>
                                        <th>İşlemler</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="account" items="${ftpAccounts}">
                                    <tr>
                                        <td>
                                            <i class="fas fa-${account.protocol == 'ftp' ? 'server' : 'shield-alt'} me-2"></i>
                                            <c:out value="${account.name}"/>
                                        </td>
                                        <td>
                                            <span class="badge bg-${account.protocol == 'ftp' ? 'primary' : 'success'}">
                                                <c:out value="${account.protocol.toUpperCase()}"/>
                                            </span>
                                        </td>
                                        <td><c:out value="${account.host}"/></td>
                                        <td><c:out value="${account.port}"/></td>
                                        <td><c:out value="${account.username}"/></td>
                                        <td>
                                            <button class="btn btn-sm btn-outline-primary" onclick="browseFiles(${account.id})">
                                                <i class="fas fa-folder-open me-1"></i>Gözat
                                            </button>
                                        </td>
                                    </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-4">
                            <i class="fas fa-server fa-3x text-muted mb-3"></i>
                            <h5 class="text-muted">Henüz size atanmış FTP hesabı yok</h5>
                            <p class="text-muted">Admin tarafından size FTP hesabı atanmasını bekleyin</p>
                            <c:if test="${currentUser.role == 'admin'}">
                            <a href="<c:url value='/admin/add-ftp'/>" class="btn btn-primary">
                                <i class="fas fa-plus me-2"></i>FTP Hesabı Ekle
                            </a>
                            </c:if>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">
                    <i class="fas fa-history me-2"></i>Son Aktiviteler
                </h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty recentLogs}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="log" items="${recentLogs}">
                            <div class="list-group-item px-0">
                                <div class="d-flex justify-content-between align-items-start">
                                    <div>
                                        <h6 class="mb-1">
                                            <i class="fas fa-${log.action == 'upload' ? 'upload' : log.action == 'download' ? 'download' : 'trash'} me-2"></i>
                                            <c:out value="${log.action}"/>
                                        </h6>
                                        <p class="mb-1 small text-muted">
                                            <c:out value="${log.filename != null ? log.filename : 'Dosya listesi'}"/>
                                        </p>
                                        <small class="text-muted">
                                            <fmt:formatDate value="${log.createdAt}" pattern="dd.MM.yyyy HH:mm"/>
                                        </small>
                                    </div>
                                    <span class="badge bg-${log.status == 'success' ? 'success' : 'danger'}">
                                        <c:out value="${log.status == 'success' ? 'Başarılı' : 'Hata'}"/>
                                    </span>
                                </div>
                            </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-3">
                            <i class="fas fa-history fa-2x text-muted mb-2"></i>
                            <p class="text-muted small">Henüz aktivite yok</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- File Browser Modal -->
<div class="modal fade" id="fileBrowserModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-folder-open me-2"></i>Dosya Yöneticisi
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div id="fileList">
                    <div class="text-center">
                        <div class="spinner-border" role="status">
                            <span class="visually-hidden">Yükleniyor...</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Kapat</button>
            </div>
        </div>
    </div>
</div>

<script>
function browseFiles(accountId) {
    $('#fileBrowserModal').modal('show');
    
    // Load files
    $.get('/ftp-client/api/list/' + accountId)
        .done(function(response) {
            if (response.success) {
                let html = '<div class="row">';
                response.files.forEach(function(file) {
                    html += '<div class="col-md-4 mb-3">';
                    html += '<div class="card">';
                    html += '<div class="card-body text-center">';
                    html += '<i class="fas fa-file fa-2x text-primary mb-2"></i>';
                    html += '<h6 class="card-title">' + file + '</h6>';
                    html += '<div class="btn-group btn-group-sm">';
                    html += '<button class="btn btn-outline-primary" onclick="downloadFile(' + accountId + ', \'' + file + '\')">';
                    html += '<i class="fas fa-download"></i>';
                    html += '</button>';
                    html += '</div>';
                    html += '</div>';
                    html += '</div>';
                    html += '</div>';
                });
                html += '</div>';
                $('#fileList').html(html);
            } else {
                $('#fileList').html('<div class="alert alert-danger">' + response.message + '</div>');
            }
        })
        .fail(function() {
            $('#fileList').html('<div class="alert alert-danger">Dosyalar yüklenirken hata oluştu</div>');
        });
}

function downloadFile(accountId, filename) {
    window.open('/ftp-client/api/download/' + accountId + '/' + filename, '_blank');
}

function uploadFile(accountId) {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.onchange = function(e) {
        const file = e.target.files[0];
        if (file) {
            const formData = new FormData();
            formData.append('file', file);
            
            $.ajax({
                url: '/ftp-client/api/upload/' + accountId,
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    if (response.success) {
                        alert('Dosya başarıyla yüklendi');
                        browseFiles(accountId); // Refresh file list
                    } else {
                        alert('Hata: ' + response.message);
                    }
                },
                error: function() {
                    alert('Dosya yüklenirken hata oluştu');
                }
            });
        }
    };
    fileInput.click();
}
</script>

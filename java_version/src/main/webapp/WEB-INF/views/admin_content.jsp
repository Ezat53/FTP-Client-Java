<div class="row">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>
                <i class="fas fa-cogs me-2"></i>Admin Panel
            </h2>
            <div>
                <a href="<c:url value='/admin/add-ftp'/>" class="btn btn-primary me-2">
                    <i class="fas fa-plus me-2"></i>FTP Hesabı Ekle
                </a>
                <a href="<c:url value='/admin/add-user'/>" class="btn btn-success">
                    <i class="fas fa-user-plus me-2"></i>Kullanıcı Ekle
                </a>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">
                    <i class="fas fa-server me-2"></i>FTP Hesapları
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
                                        <th>Sahibi</th>
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
                                        <td><c:out value="${account.owner.username}"/></td>
                                        <td>
                                            <div class="btn-group btn-group-sm">
                                                <a href="<c:url value='/admin/edit-ftp/${account.id}'/>" class="btn btn-outline-primary">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <button class="btn btn-outline-danger" onclick="deleteFTPAccount(${account.id})">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
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
                            <h5 class="text-muted">Henüz FTP hesabı yok</h5>
                            <a href="<c:url value='/admin/add-ftp'/>" class="btn btn-primary">
                                <i class="fas fa-plus me-2"></i>İlk FTP Hesabını Ekle
                            </a>
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
                    <i class="fas fa-users me-2"></i>Kullanıcılar
                </h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty users}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="user" items="${users}">
                            <div class="list-group-item px-0">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h6 class="mb-1">
                                            <i class="fas fa-user me-2"></i>
                                            <c:out value="${user.username}"/>
                                        </h6>
                                        <small class="text-muted">
                                            <span class="badge bg-${user.role == 'admin' ? 'danger' : 'primary'}">
                                                <c:out value="${user.role}"/>
                                            </span>
                                        </small>
                                    </div>
                                    <div class="btn-group btn-group-sm">
                                        <a href="<c:url value='/admin/edit-user/${user.id}'/>" class="btn btn-outline-primary">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <c:if test="${user.role != 'admin'}">
                                        <button class="btn btn-outline-danger" onclick="deleteUser(${user.id})">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-3">
                            <i class="fas fa-users fa-2x text-muted mb-2"></i>
                            <p class="text-muted small">Henüz kullanıcı yok</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<script>
function deleteFTPAccount(accountId) {
    if (confirm('Bu FTP hesabını silmek istediğinizden emin misiniz?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/ftp-client/admin/delete-ftp/' + accountId;
        document.body.appendChild(form);
        form.submit();
    }
}

function deleteUser(userId) {
    if (confirm('Bu kullanıcıyı silmek istediğinizden emin misiniz?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/ftp-client/admin/delete-user/' + userId;
        document.body.appendChild(form);
        form.submit();
    }
}
</script>

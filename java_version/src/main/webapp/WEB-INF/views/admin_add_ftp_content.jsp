<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">
                    <i class="fas fa-plus me-2"></i>Yeni FTP Hesabı Ekle
                </h5>
            </div>
            <div class="card-body">
                <form method="POST" action="<c:url value='/admin/add-ftp'/>">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="name" class="form-label">Hesap Adı</label>
                                <input type="text" class="form-control" id="name" name="name" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="protocol" class="form-label">Protokol</label>
                                <select class="form-select" id="protocol" name="protocol" required>
                                    <option value="">Protokol Seçin</option>
                                    <option value="ftp">FTP</option>
                                    <option value="sftp">SFTP</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-8">
                            <div class="mb-3">
                                <label for="host" class="form-label">Host</label>
                                <input type="text" class="form-control" id="host" name="host" required>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="mb-3">
                                <label for="port" class="form-label">Port</label>
                                <input type="number" class="form-control" id="port" name="port" value="21" required>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="username" class="form-label">Kullanıcı Adı</label>
                                <input type="text" class="form-control" id="username" name="username" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="password" class="form-label">Şifre</label>
                                <input type="password" class="form-control" id="password" name="password" required>
                            </div>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label">Kullanıcı Atamaları</label>
                        <div class="row">
                            <c:forEach var="user" items="${users}">
                            <div class="col-md-6">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" name="userIds" value="${user.id}" id="user_${user.id}">
                                    <label class="form-check-label" for="user_${user.id}">
                                        <c:out value="${user.username}"/>
                                        <span class="badge bg-${user.role == 'admin' ? 'danger' : 'primary'} ms-2">
                                            <c:out value="${user.role}"/>
                                        </span>
                                    </label>
                                </div>
                            </div>
                            </c:forEach>
                        </div>
                    </div>
                    
                    <div class="d-flex justify-content-between">
                        <a href="<c:url value='/admin'/>" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Geri
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-2"></i>Kaydet
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
document.getElementById('protocol').addEventListener('change', function() {
    const portInput = document.getElementById('port');
    if (this.value === 'sftp') {
        portInput.value = '22';
    } else if (this.value === 'ftp') {
        portInput.value = '21';
    }
});
</script>

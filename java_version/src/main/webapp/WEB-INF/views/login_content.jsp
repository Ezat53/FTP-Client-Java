<div class="row justify-content-center">
    <div class="col-lg-5 col-md-7">
        <div class="card hover-lift">
            <div class="card-header text-center">
                <div class="gradient-bg rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                    <i class="fas fa-sign-in-alt fa-2x text-white"></i>
                </div>
                <h3 class="mb-0 text-primary">Giriş Yap</h3>
                <p class="text-muted mt-2">Hesabınızla sisteme giriş yapın</p>
            </div>
            <div class="card-body p-4">
                <form method="POST" action="<c:url value='/login'/>">
                    <div class="mb-4">
                        <label for="username" class="form-label fw-semibold">Kullanıcı Adı</label>
                        <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-user text-primary"></i>
                            </span>
                            <input type="text" class="form-control" id="username" name="username" placeholder="Kullanıcı adınızı girin" required>
                        </div>
                    </div>
                    
                    <div class="mb-4">
                        <label for="password" class="form-label fw-semibold">Şifre</label>
                        <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-lock text-primary"></i>
                            </span>
                            <input type="password" class="form-control" id="password" name="password" placeholder="Şifrenizi girin" required>
                        </div>
                    </div>
                    
                    <div class="d-grid mb-3">
                        <button type="submit" class="btn btn-primary btn-lg">
                            <i class="fas fa-sign-in-alt me-2"></i>Giriş Yap
                        </button>
                    </div>
                </form>
                
                <div class="text-center">
                    <p class="mb-0 text-muted">
                        <i class="fas fa-info-circle me-1"></i>
                        Hesap oluşturmak için admin ile iletişime geçin
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

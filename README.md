# Xfer FTP Web Service - Java Version

Bu proje, Python Flask uygulamasının Java Spring Boot versiyonudur. Tomcat sunucusu altında çalışacak şekilde tasarlanmıştır.

## Özellikler

- **FTP/SFTP Desteği**: Hem FTP hem de SFTP protokollerini destekler
- **Kullanıcı Yönetimi**: Admin paneli ile kullanıcı ve hesap yönetimi
- **Dosya Transferi**: Güvenli dosya yükleme, indirme ve silme
- **Aktivite Takibi**: Detaylı transfer logları
- **Modern UI**: Bootstrap 5 ile responsive tasarım
- **Güvenlik**: Spring Security ile kimlik doğrulama ve yetkilendirme
- **Uzak Dizin Kısıtlaması**: FTP hesapları için başlangıç dizini belirleme
- **Otomatik Şifre Üretimi**: Güçlü şifre üretme ve kopyalama özellikleri
- **İzin Yönetimi**: Kullanıcı bazlı detaylı dosya işlem izinleri
- **Gerçek Zamanlı Dosya Tarayıcısı**: Web tabanlı dosya yönetimi

## Teknolojiler

- **Backend**: Spring Boot 2.7.18, Spring Security, Spring Data JPA
- **Database**: H2 Database (yerel dosya veritabanı)
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **FTP/SFTP**: Apache Commons Net, JSch
- **Build Tool**: Maven
- **Server**: Tomcat 9+

## Kurulum

### Gereksinimler

- Java 11 veya üzeri
- Maven 3.6+
- Tomcat 9+

### 1. Projeyi Derleme

```bash
cd java_version
mvn clean package
```

### 2. Tomcat'e Deploy Etme

1. Derlenen WAR dosyasını (`target/xfer-ftp-web-service.war`) Tomcat'in `webapps` klasörüne kopyalayın
2. Tomcat'i başlatın
3. Uygulamaya `http://localhost:8080/xfer-ftp-web-service` adresinden erişin

### 3. Veritabanı Yapılandırması

#### H2 Database (Varsayılan)
Uygulama varsayılan olarak H2 Database kullanır. Veritabanı dosyası `./data/ftp_manager.mv.db` konumunda oluşturulur.

Veritabanı dosyası:
- **Konum**: `./data/ftp_manager.mv.db`
- **Tip**: H2 Database (yerel dosya)
- **Yönetim**: H2 Console veya herhangi bir JDBC yönetim aracı ile açabilirsiniz
- **Web Console**: `http://localhost:8080/h2-console` (geliştirme modunda)

### 4. İlk Kullanım

1. Uygulamaya giriş yapın
2. Varsayılan admin hesabı:
   - Kullanıcı adı: `admin`
   - Şifre: `45SbxhWTTsXD/2oXQju_LsW`

## Yapılandırma

### Uygulama Özellikleri

`src/main/resources/application.properties` dosyasında aşağıdaki ayarları yapabilirsiniz:

```properties
# Sunucu portu
server.port=8080

# Context path
server.servlet.context-path=/xfer-ftp-web-service

# H2 Database yapılandırması
spring.datasource.url=jdbc:h2:file:./data/ftp_manager
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true

# JPA yapılandırması
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Dosya yükleme limiti
spring.servlet.multipart.max-file-size=16MB
spring.servlet.multipart.max-request-size=16MB

# Upload klasörü
app.upload.dir=uploads

# Logging
logging.level.com.xfer=DEBUG
logging.file.name=logs/application.log
```

### Güvenlik

Spring Security ile korunmuştur. Varsayılan roller:
- `ADMIN`: Tüm işlemlere erişim
- `USER`: Sadece atanmış FTP hesaplarına erişim

### İzin Sistemi

Kullanıcılar için detaylı izin yönetimi:
- **Okuma (Read)**: Dosya listeleme ve indirme
- **Yazma (Write)**: Dosya düzenleme
- **Silme (Delete)**: Dosya silme
- **Yükleme (Upload)**: Dosya yükleme

### Uzak Dizin Kısıtlaması

FTP hesapları için başlangıç dizini belirleme:
- Her FTP hesabı için özel başlangıç dizini ayarlanabilir
- Kullanıcılar sadece belirlenen dizin ve alt dizinlerine erişebilir
- Güvenlik için üst dizinlere erişim engellenir

## API Endpoints

### Kimlik Doğrulama
- `GET /` - Ana sayfa
- `GET /login` - Giriş sayfası
- `POST /login` - Giriş işlemi
- `GET /logout` - Çıkış işlemi

### Dashboard
- `GET /dashboard` - Kullanıcı dashboard'u
- `GET /dashboard/browse/{accountId}` - Dosya tarayıcısı

### FTP İşlemleri
- `POST /api/upload/{accountId}` - Dosya yükleme
- `GET /api/download/{accountId}/{filename}` - Dosya indirme
- `DELETE /api/delete/{accountId}/{filename}` - Dosya silme
- `GET /api/list/{accountId}` - Dosya listeleme

### Admin Panel
- `GET /admin` - Admin paneli
- `GET /admin/add-ftp` - FTP hesabı ekleme formu
- `POST /admin/add-ftp` - FTP hesabı oluşturma
- `GET /admin/edit-ftp/{id}` - FTP hesabı düzenleme formu
- `POST /admin/edit-ftp/{id}` - FTP hesabı güncelleme
- `POST /admin/delete-ftp/{id}` - FTP hesabı silme
- `GET /admin/add-user` - Kullanıcı ekleme formu
- `POST /admin/add-user` - Kullanıcı oluşturma
- `GET /admin/edit-user/{id}` - Kullanıcı düzenleme formu
- `POST /admin/edit-user/{id}` - Kullanıcı güncelleme
- `POST /admin/delete-user/{id}` - Kullanıcı silme
- `GET /admin/debug-account/{id}` - FTP hesabı debug sayfası

## Geliştirme

### Proje Yapısı

```
src/
├── main/
│   ├── java/com/xfer/
│   │   ├── FtpClientApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── DashboardController.java
│   │   │   ├── FTPController.java
│   │   │   ├── AdminController.java
│   │   │   ├── ApiController.java
│   │   │   └── CustomErrorController.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── FTPAccount.java
│   │   │   ├── TransferLog.java
│   │   │   └── FTPUserAssignment.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── FTPAccountRepository.java
│   │   │   ├── TransferLogRepository.java
│   │   │   └── FTPUserAssignmentRepository.java
│   │   └── service/
│   │       ├── AuthService.java
│   │       ├── FTPService.java
│   │       ├── TransferService.java
│   │       ├── FTPOperationsService.java
│   │       └── InitializationService.java
│   ├── resources/
│   │   ├── application.properties
│   │   ├── static/
│   │   │   ├── css/
│   │   │   │   ├── base.css
│   │   │   │   ├── components.css
│   │   │   │   ├── style.css
│   │   │   │   └── ...
│   │   │   └── js/
│   │   │       ├── base.js
│   │   │       ├── browse.js
│   │   │       ├── add_user.js
│   │   │       ├── edit_ftp.js
│   │   │       └── ...
│   │   └── templates/
│   │       ├── base.html
│   │       ├── index.html
│   │       ├── login.html
│   │       ├── dashboard.html
│   │       ├── browse.html
│   │       ├── admin/
│   │       │   ├── admin.html
│   │       │   ├── add_user.html
│   │       │   ├── admin_edit_user.html
│   │       │   ├── admin_add_ftp.html
│   │       │   ├── admin_edit_ftp.html
│   │       │   └── ...
│   │       └── error/
│   │           ├── 404.html
│   │           └── 500.html
│   └── webapp/
│       └── WEB-INF/
│           └── web.xml
```

### Veritabanı Şeması

- **users**: Kullanıcı bilgileri
- **ftp_accounts**: FTP hesap bilgileri (remote_path alanı dahil)
- **ftp_user_assignments**: Kullanıcı-FTP hesap atamaları (izinler dahil)
- **transfer_logs**: Transfer işlem logları

### Yeni Özellikler

#### 1. Uzak Dizin Kısıtlaması
- FTP hesapları için başlangıç dizini belirleme
- Kullanıcılar sadece belirlenen dizin ve alt dizinlerine erişebilir
- Güvenlik için üst dizinlere erişim engellenir

#### 2. Otomatik Şifre Üretimi
- Güçlü 16 karakterli şifre üretimi
- Şifre kopyalama özelliği
- Kullanıcı adı ve şifre birlikte kopyalama

#### 3. Detaylı İzin Yönetimi
- Okuma, yazma, silme, yükleme izinleri
- Kullanıcı bazlı izin ataması
- Admin panelinden kolay yönetim

#### 4. Modern UI/UX
- Thymeleaf template engine
- Bootstrap 5 responsive tasarım
- Toast bildirimleri
- Gelişmiş form validasyonu

## Sorun Giderme

### Yaygın Sorunlar

1. **Port zaten kullanımda**: `application.properties` dosyasında farklı bir port belirtin
2. **Veritabanı bağlantı hatası**: H2 veritabanı ayarlarını kontrol edin
3. **Dosya yükleme hatası**: Upload klasörü izinlerini kontrol edin
4. **FTP bağlantı hatası**: FTP hesap bilgilerini ve ağ bağlantısını kontrol edin
5. **Protokol uyumsuzluğu**: SFTP sunucuya FTP protokolü ile bağlanmaya çalışıyorsanız, hesap ayarlarında protokolü "sftp" olarak değiştirin
6. **Uzak dizin erişim hatası**: FTP hesabında belirlenen uzak dizinin mevcut olduğundan emin olun
7. **İzin hatası**: Kullanıcının gerekli izinlere sahip olduğunu kontrol edin

### Log Dosyaları

Uygulama logları `logs/application.log` dosyasında bulunur. Hata ayıklama için log seviyesini DEBUG olarak ayarlayabilirsiniz.

### Debug Özellikleri

- **FTP Hesap Debug**: `/admin/debug-account/{id}` endpoint'i ile FTP hesap detaylarını görüntüleyebilirsiniz
- **Console Logları**: Tarayıcı geliştirici araçlarında detaylı JavaScript logları
- **Server Logları**: Spring Boot uygulama loglarında detaylı hata mesajları

## Son Güncellemeler

### v2.0.0 - Major Update

#### Yeni Özellikler
- ✅ **Uzak Dizin Kısıtlaması**: FTP hesapları için başlangıç dizini belirleme
- ✅ **Otomatik Şifre Üretimi**: Güçlü şifre üretme ve kopyalama sistemi
- ✅ **Detaylı İzin Yönetimi**: Kullanıcı bazlı dosya işlem izinleri
- ✅ **Modern UI/UX**: Thymeleaf template engine ve Bootstrap 5
- ✅ **Gelişmiş Dosya Tarayıcısı**: Web tabanlı dosya yönetimi
- ✅ **Toast Bildirimleri**: Kullanıcı dostu geri bildirim sistemi

#### Teknik İyileştirmeler
- 🔄 **Veritabanı**: SQLite'dan H2 Database'e geçiş
- 🔄 **Template Engine**: JSP'den Thymeleaf'e geçiş
- 🔄 **Frontend**: Modern JavaScript ve CSS yapısı
- 🔄 **Güvenlik**: Gelişmiş izin kontrolü ve uzak dizin kısıtlaması
- 🔄 **API**: RESTful API endpoint'leri ve hata yönetimi

#### Bug Fixes
- 🐛 **Protokol Tespiti**: FTP/SFTP protokol otomatik tespiti
- 🐛 **İzin Yükleme**: Edit sayfalarında izin verilerinin doğru yüklenmesi
- 🐛 **Form Validasyonu**: Gelişmiş form doğrulama ve hata mesajları
- 🐛 **Responsive Design**: Mobil uyumlu arayüz iyileştirmeleri

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit yapın (`git commit -m 'Add some amazing feature'`)
4. Push yapın (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

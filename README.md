# Xfer FTP Web Service - Java Version

Bu proje, Python Flask uygulamasının Java Spring Boot versiyonudur. Tomcat sunucusu altında çalışacak şekilde tasarlanmıştır.

## Özellikler

- **FTP/SFTP Desteği**: Hem FTP hem de SFTP protokollerini destekler
- **Kullanıcı Yönetimi**: Admin paneli ile kullanıcı ve hesap yönetimi
- **Dosya Transferi**: Güvenli dosya yükleme, indirme ve silme
- **Aktivite Takibi**: Detaylı transfer logları
- **Modern UI**: Bootstrap 5 ile responsive tasarım
- **Güvenlik**: Spring Security ile kimlik doğrulama ve yetkilendirme

## Teknolojiler

- **Backend**: Spring Boot 2.7.18, Spring Security, Spring Data JPA
- **Database**: SQLite (yerel dosya veritabanı)
- **Frontend**: JSP, Bootstrap 5, JavaScript
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

#### SQLite (Varsayılan)
Uygulama varsayılan olarak SQLite veritabanı kullanır. Veritabanı dosyası `./data/ftp_manager.db` konumunda oluşturulur.

Veritabanı dosyası:
- **Konum**: `./data/ftp_manager.db`
- **Tip**: SQLite (yerel dosya)
- **Yönetim**: SQLite Browser veya herhangi bir SQLite yönetim aracı ile açabilirsiniz

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
server.servlet.context-path=/ftp-client

# Dosya yükleme limiti
spring.servlet.multipart.max-file-size=16MB
spring.servlet.multipart.max-request-size=16MB

# Upload klasörü
app.upload.dir=uploads
```

### Güvenlik

Spring Security ile korunmuştur. Varsayılan roller:
- `ADMIN`: Tüm işlemlere erişim
- `USER`: Sadece atanmış FTP hesaplarına erişim

## API Endpoints

### Kimlik Doğrulama
- `GET /` - Ana sayfa
- `GET /login` - Giriş sayfası
- `POST /login` - Giriş işlemi
- `GET /logout` - Çıkış işlemi

### Dashboard
- `GET /dashboard` - Kullanıcı dashboard'u

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

## Geliştirme

### Proje Yapısı

```
src/
├── main/
│   ├── java/com/xfer/
│   │   ├── FtpClientApplication.java
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── DashboardController.java
│   │   │   ├── FTPController.java
│   │   │   └── AdminController.java
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
│   │       └── FTPOperationsService.java
│   ├── resources/
│   │   └── application.properties
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── views/
│       │   │   ├── base.jsp
│       │   │   ├── index.jsp
│       │   │   ├── login.jsp
│       │   │   ├── dashboard.jsp
│       │   │   ├── admin.jsp
│       │   │   └── ...
│       │   └── web.xml
│       ├── css/
│       │   └── style.css
│       └── js/
│           └── main.js
```

### Veritabanı Şeması

- **users**: Kullanıcı bilgileri
- **ftp_accounts**: FTP hesap bilgileri
- **ftp_user_assignments**: Kullanıcı-FTP hesap atamaları
- **transfer_logs**: Transfer işlem logları

## Sorun Giderme

### Yaygın Sorunlar

1. **Port zaten kullanımda**: `application.properties` dosyasında farklı bir port belirtin
2. **Veritabanı bağlantı hatası**: Veritabanı ayarlarını kontrol edin
3. **Dosya yükleme hatası**: Upload klasörü izinlerini kontrol edin
4. **FTP bağlantı hatası**: FTP hesap bilgilerini ve ağ bağlantısını kontrol edin

### Log Dosyaları

Uygulama logları Tomcat'in log klasöründe bulunur. Hata ayıklama için log seviyesini DEBUG olarak ayarlayabilirsiniz.

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit yapın (`git commit -m 'Add some amazing feature'`)
4. Push yapın (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

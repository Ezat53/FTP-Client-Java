# Xfer

Web tabanlı FTP, SFTP ve SCP dosya yönetim sistemi. Kullanıcılar web arayüzü üzerinden güvenli dosya transferi yapabilir.

## 🚀 Özellikler

- **Çoklu Protokol Desteği**: FTP, SFTP ve SCP protokollerini destekler
- **Güvenli Bağlantı**: Şifreli bağlantılar ve güvenli dosya transferi
- **Kullanıcı Yönetimi**: Admin ve kullanıcı rolleri ile yetkilendirme
- **Dosya Yönetimi**: Dosya yükleme, indirme ve silme işlemleri
- **Transfer Logları**: Tüm dosya transfer işlemlerinin kayıtları
- **Modern Arayüz**: Bootstrap 5 ile responsive tasarım
- **Admin Paneli**: Sistem yönetimi ve kullanıcı kontrolü

## 🛠️ Teknolojiler

### Backend
- **Python 3.8+**
- **Flask** - Web framework
- **SQLAlchemy** - ORM
- **Flask-Login** - Kullanıcı oturum yönetimi
- **Paramiko** - SFTP/SCP bağlantıları
- **ftplib** - FTP bağlantıları

### Frontend
- **HTML5, CSS3, JavaScript**
- **Bootstrap 5** - UI framework
- **Font Awesome** - İkonlar

### Veritabanı
- **SQLite** - Lokal veritabanı

## 📦 Kurulum

### Gereksinimler
- Python 3.8 veya üzeri
- pip (Python paket yöneticisi)

### Adımlar

1. **Projeyi klonlayın:**
```bash
git clone <repository-url>
cd FTP-WEB-Client
```

2. **Sanal ortam oluşturun:**
```bash
python -m venv venv
```

3. **Sanal ortamı aktifleştirin:**
```bash
# Windows
venv\Scripts\activate

# Linux/Mac
source venv/bin/activate
```

4. **Gerekli paketleri yükleyin:**
```bash
pip install -r requirements.txt
```

5. **Uygulamayı çalıştırın:**
```bash
python app.py
```

6. **Tarayıcıda açın:**
```
http://localhost:5000
```

## 🔐 Varsayılan Hesaplar

Uygulama ilk çalıştırıldığında otomatik olarak admin hesabı oluşturulur:

- **Kullanıcı Adı:** admin
- **Şifre:** admin123

⚠️ **Güvenlik Uyarısı:** Üretim ortamında mutlaka admin şifresini değiştirin!

## 📱 Kullanım

### Kullanıcı Girişi
1. Ana sayfadan "Giriş Yap" butonuna tıklayın
2. Kullanıcı adı ve şifrenizi girin
3. Dashboard'a yönlendirileceksiniz

### FTP Hesabı Ekleme
1. Dashboard'da "Yeni Hesap Ekle" butonuna tıklayın
2. Hesap bilgilerini doldurun:
   - Hesap Adı: Kolay hatırlanır isim
   - Protokol: FTP, SFTP veya SCP
   - Host: Sunucu adresi
   - Port: Bağlantı portu
   - Kullanıcı Adı: FTP kullanıcı adı
   - Şifre: FTP şifresi (opsiyonel)

### Dosya İşlemleri
1. Dashboard'da istediğiniz hesaba "Gözat" butonuna tıklayın
2. Dosya listesini görüntüleyin
3. Dosya yüklemek için "Dosya Yükle" butonunu kullanın
4. Dosya indirmek için "İndir" butonunu kullanın
5. Dosya silmek için "Sil" butonunu kullanın

### Admin Paneli
Admin kullanıcıları için:
- Tüm kullanıcıları görüntüleme ve silme
- Tüm FTP hesaplarını görüntüleme ve silme
- Sistem istatistikleri
- Transfer logları

## 🔧 Yapılandırma

### Ortam Değişkenleri
```bash
# Güvenlik anahtarı (üretim için zorunlu)
export SECRET_KEY="your-secret-key-here"

# Veritabanı URL'i (opsiyonel)
export DATABASE_URL="sqlite:///ftp_manager.db"
```

### Dosya Boyutu Sınırı
Varsayılan maksimum dosya boyutu 16MB'dir. `app.py` dosyasında değiştirebilirsiniz:
```python
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16MB
```

### Desteklenen Dosya Türleri
Varsayılan olarak şu dosya türleri desteklenir:
- Metin dosyaları: .txt
- PDF: .pdf
- Resimler: .png, .jpg, .jpeg, .gif
- Office: .doc, .docx, .xls, .xlsx
- Arşivler: .zip, .rar

## 🚀 Üretim Dağıtımı

### Gunicorn ile
```bash
pip install gunicorn
gunicorn -w 4 -b 0.0.0.0:8000 app:app
```

### Docker ile
```dockerfile
FROM python:3.9-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt
COPY . .
EXPOSE 5000
CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:5000", "app:app"]
```

## 🛡️ Güvenlik

- Şifreler bcrypt ile hashlenir
- Dosya türü doğrulaması yapılır
- Güvenli dosya adları kullanılır
- SQL injection koruması
- XSS koruması
- CSRF koruması

## 📝 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit yapın (`git commit -m 'Add amazing feature'`)
4. Push yapın (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## 📞 Destek

Herhangi bir sorun yaşarsanız:
1. Issues bölümünden sorun bildirin
2. Detaylı hata mesajlarını paylaşın
3. Kullandığınız Python ve Flask versiyonlarını belirtin

## 🔄 Güncellemeler

### v1.0.0
- İlk sürüm
- FTP, SFTP, SCP desteği
- Kullanıcı yönetimi
- Admin paneli
- Dosya yönetimi
- Transfer logları

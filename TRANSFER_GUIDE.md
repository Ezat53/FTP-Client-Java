# Flask FTP Uygulaması - Transfer ve Çalıştırma Rehberi

## 📋 Genel Bakış
Bu rehber, Flask FTP uygulamanızı internete kapalı sunucuya transfer edip çalıştırmanız için gerekli adımları içerir.

## 📦 Transfer Edilecek Dosyalar

### 1. Temel Uygulama Dosyaları
```
/opt/ftp/
├── app.py                  # Ana uygulama dosyası
├── requirements.txt        # Gerekli kütüphaneler listesi
├── run_app.sh             # Linux/Unix çalıştırma scripti
├── config/                # Yapılandırma dosyaları
├── controllers/           # Flask controller'ları
├── models/               # Veritabanı modelleri
├── services/             # Servis katmanı
├── static/               # CSS, JS, resim dosyaları
├── templates/            # HTML şablonları
├── uploads/              # Dosya yükleme klasörü
└── instance/             # Uygulama verisi
```

### 2. Portable Python Kurulumu
```
/opt/ftp/python311/       # Komple Python 3.11.9 kurulumu (tüm alt klasörlerle)
├── bin/                  # Python çalıştırılabilir dosyaları
├── lib/                  # Python kütüphaneleri
├── include/              # Header dosyaları
└── share/                # Dokümantasyon
```

## 🚀 Transfer İşlemi

### Yöntem 1: Arşiv ile Transfer
```bash
# 1. Tüm dosyaları arşivle
cd /opt
tar -czf ftp_app_portable.tar.gz ftp/

# 2. Arşivi hedef sunucuya kopyala
scp ftp_app_portable.tar.gz user@hedef-sunucu:/opt/

# 3. Hedef sunucuda arşivi çıkar
cd /opt
tar -xzf ftp_app_portable.tar.gz
```

### Yöntem 2: Rsync ile Transfer
```bash
# Rsync ile senkronize et
rsync -avz /opt/ftp/ user@hedef-sunucu:/opt/ftp/
```

### Yöntem 3: USB/Harici Disk ile Transfer
```bash
# USB'ye kopyala
cp -r /opt/ftp /media/usb/

# Hedef sunucuda USB'den kopyala
cp -r /media/usb/ftp /opt/
```

## ⚙️ Hedef Sunucuda Kurulum

### 1. İzinleri Ayarla
```bash
cd /opt/ftp
chmod +x run_app.sh
chmod +x python311/bin/*
```

### 2. Gerekli Sistem Paketleri (Opsiyonel)
```bash
# Sadece çok gerekirse (genelde gerekli değil)
apt update
apt install -y libsqlite3-0 zlib1g
```

## 🏃‍♂️ Uygulamayı Çalıştırma

### Linux/Unix Sistemlerde
```bash
cd /opt/ftp
./run_app.sh
```

### Manuel Çalıştırma
```bash
cd /opt/ftp
export FLASK_APP=app.py
export PYTHONPATH="/opt/ftp:$PYTHONPATH"
./python311/bin/python3 -m flask run --host=0.0.0.0 --port=6756
```

## 🌐 Erişim Bilgileri

- **URL**: `http://sunucu-ip:6756`
- **Admin Kullanıcısı**: `admin`
- **Admin Şifresi**: `admin123`

## 🔧 Yapılandırma

### Port Değiştirme
`run_app.sh` dosyasında port numarasını değiştirebilirsiniz:
```bash
"$PYTHON_PATH" -m flask run --host=0.0.0.0 --port=YENI_PORT
```

### Veritabanı
- SQLite veritabanı otomatik olarak `instance/` klasöründe oluşturulur
- İlk çalıştırmada admin kullanıcısı otomatik oluşturulur

## 📁 Klasör Yapısı Kontrolü

Transfer sonrası şu komutla klasör yapısını kontrol edin:
```bash
ls -la /opt/ftp/
ls -la /opt/ftp/python311/bin/
```

## 🔍 Sorun Giderme

### Çalışmazsa Kontrol Edilecekler:
1. **İzinler**: `chmod +x run_app.sh python311/bin/python3`
2. **Python Versiyonu**: `./python311/bin/python3 --version`
3. **Kütüphaneler**: Script otomatik kontrol eder
4. **Port**: Başka uygulama kullanıyor olabilir
5. **Firewall**: Port 6756'nın açık olduğundan emin olun

### Log Kontrolü
```bash
# Uygulama loglarını görmek için
./run_app.sh 2>&1 | tee app.log
```

### Test Komutları
```bash
# Python çalışıyor mu?
./python311/bin/python3 -c "print('Python çalışıyor!')"

# Kütüphaneler yüklü mü?
./python311/bin/python3 -c "import flask, paramiko, sqlite3; print('Tüm kütüphaneler OK!')"

# Flask uygulaması import ediliyor mu?
./python311/bin/python3 -c "from app import create_app; print('Uygulama OK!')"
```

## 📊 Sistem Gereksinimleri

- **İşletim Sistemi**: Linux (Ubuntu, CentOS, RHEL, vb.)
- **Mimari**: x86_64
- **RAM**: Minimum 512MB (Önerilen 1GB+)
- **Disk**: ~200MB (Python + Uygulama)
- **Port**: 6756 (değiştirilebilir)

## 🔒 Güvenlik Notları

1. **Admin Şifresi**: İlk giriş sonrası mutlaka değiştirin
2. **Firewall**: Sadece gerekli portları açın
3. **SSL**: Üretim ortamında SSL sertifikası kullanın
4. **Backup**: Düzenli olarak `instance/` klasörünü yedekleyin

## 📞 Destek

Herhangi bir sorun yaşarsanız:
1. `run_app.sh` scriptinin çıktısını kontrol edin
2. `app.log` dosyasına bakın
3. Python ve kütüphane versiyonlarını kontrol edin

---

**✅ Kurulum Tamamlandı!** 
Uygulamanız artık internete kapalı sunucuda çalışmaya hazır.

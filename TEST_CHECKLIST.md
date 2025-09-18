# Xfer FTP Web Service - Test Checklist

## 📋 Genel Test Bilgileri

**Uygulama**: Xfer FTP Web Service (Java Spring Boot)  
**Test Tarihi**: [Tarih]  
**Test Edilen Versiyon**: [Versiyon]  
**Test Edilen Tarayıcı**: [Chrome/Firefox/Edge]  
**Test Ortamı**: [Development/Staging/Production]  

---

## 🔧 1. KURULUM VE YAPILANDIRMA TESTLERİ

### 1.1 Sistem Gereksinimleri
- [x] Java 11+ yüklü ve çalışıyor
- [x] Maven 3.6+ yüklü ve çalışıyor
- [x] Tomcat 9+ yüklü ve çalışıyor
- [x] Veritabanı dosyası oluşturuluyor (`data/ftp_manager.mv.db`)

### 1.2 Uygulama Derleme
- [x] `mvn clean compile` başarılı
- [x] `mvn clean package` başarılı
- [x] WAR dosyası oluşturuluyor (`target/xfer-ftp-web-service.war`)

### 1.3 Uygulama Başlatma
- [x] Uygulama başlatılıyor (hata yok)
- [x] Veritabanı bağlantısı başarılı
- [x] H2 Console erişilebilir (`/h2-console`)
- [x] Log dosyaları oluşturuluyor (`logs/application.log`)

### 1.4 Erişim Testleri
- [x] Ana sayfa erişilebilir (`http://localhost:8080/xfer-ftp-web-service/`)
- [x] Login sayfası erişilebilir (`/login`)
- [x] Static dosyalar yükleniyor (CSS, JS, images)

---

## 🔐 2. KİMLİK DOĞRULAMA VE GÜVENLİK TESTLERİ

### 2.1 Giriş İşlemleri
- [x] Varsayılan admin hesabı ile giriş (`admin` / `admin123`)
- [x] Yanlış kullanıcı adı ile giriş denemesi (hata mesajı)
- [x] Yanlış şifre ile giriş denemesi (hata mesajı)
- [x] Boş alanlarla giriş denemesi (validasyon hatası)

### 2.2 Yetkilendirme
- [ ] Giriş yapmadan admin paneline erişim (yönlendirme)
- [ ] Giriş yapmadan dashboard'a erişim (yönlendirme)
- [ ] Giriş yapmadan API endpoint'lerine erişim (401 hatası)

### 2.3 Çıkış İşlemleri
- [ ] Logout butonu çalışıyor
- [ ] Çıkış sonrası güvenli sayfalara erişim engelleniyor
- [ ] Session temizleniyor

### 2.4 Güvenlik
- [ ] CSRF koruması aktif
- [ ] SQL injection koruması
- [ ] XSS koruması
- [ ] Dosya yükleme güvenliği

---

## 👤 3. KULLANICI YÖNETİMİ TESTLERİ

### 3.1 Kullanıcı Oluşturma (Admin)
- [ ] Yeni kullanıcı ekleme formu açılıyor
- [ ] Geçerli bilgilerle kullanıcı oluşturma
- [ ] Kullanıcı adı zaten var hatası
- [ ] E-posta formatı validasyonu
- [ ] Şifre uzunluk validasyonu
- [ ] Rol seçimi (ADMIN/USER)

### 3.2 Kullanıcı Düzenleme (Admin)
- [ ] Kullanıcı düzenleme formu açılıyor
- [ ] Mevcut bilgiler formda görünüyor
- [ ] Bilgi güncelleme başarılı
- [ ] Şifre değiştirme (opsiyonel)

### 3.3 Kullanıcı Silme (Admin)
- [ ] Kullanıcı silme onayı
- [ ] Kullanıcı başarıyla siliniyor
- [ ] Silinen kullanıcı ile giriş yapılamıyor

### 3.4 Kullanıcı Listesi
- [ ] Tüm kullanıcılar listeleniyor
- [ ] Kullanıcı bilgileri doğru görünüyor
- [ ] Arama/filtreleme çalışıyor

---

## 🖥️ 4. FTP HESAP YÖNETİMİ TESTLERİ

### 4.1 FTP Hesabı Oluşturma (Admin)
- [ ] FTP hesabı ekleme formu açılıyor
- [ ] FTP bilgileri girişi (host, port, username, password)
- [ ] Protokol seçimi (FTP/SFTP)
- [ ] Kullanıcı ataması (çoklu seçim)
- [ ] Remote path belirleme
- [ ] Geçerli bilgilerle hesap oluşturma
- [ ] Bağlantı testi

### 4.2 FTP Hesabı Düzenleme (Admin)
- [ ] FTP hesabı düzenleme formu açılıyor
- [ ] Mevcut bilgiler formda görünüyor
- [ ] Bilgi güncelleme başarılı
- [ ] Kullanıcı atamaları güncelleniyor

### 4.3 FTP Hesabı Silme (Admin)
- [ ] FTP hesabı silme onayı
- [ ] Hesap başarıyla siliniyor
- [ ] İlişkili kullanıcı atamaları temizleniyor

### 4.4 FTP Hesap Listesi
- [ ] Tüm FTP hesapları listeleniyor
- [ ] Hesap bilgileri doğru görünüyor
- [ ] Durum göstergeleri çalışıyor

---

## 📁 5. DOSYA İŞLEMLERİ TESTLERİ

### 5.1 Dosya Listeleme
- [ ] FTP hesabına bağlanma
- [ ] Dosya listesi görüntüleme
- [ ] Klasör yapısı görüntüleme
- [ ] Dosya boyutu ve tarih bilgileri
- [ ] Hata durumunda uygun mesaj

### 5.2 Dosya Yükleme
- [ ] Dosya seçme (tek dosya)
- [ ] Dosya seçme (çoklu dosya)
- [ ] Desteklenen dosya formatları
- [ ] Dosya boyutu limiti (16MB)
- [ ] Yükleme progress göstergesi
- [ ] Başarılı yükleme mesajı
- [ ] Hata durumunda uygun mesaj

### 5.3 Dosya İndirme
- [ ] Dosya indirme başlatma
- [ ] Dosya adı korunuyor
- [ ] Dosya içeriği doğru
- [ ] Büyük dosya indirme
- [ ] Hata durumunda uygun mesaj

### 5.4 Dosya Silme
- [ ] Dosya silme onayı
- [ ] Dosya başarıyla siliniyor
- [ ] Silme işlemi loglanıyor
- [ ] Hata durumunda uygun mesaj

---

## 📊 6. DASHBOARD VE ARAYÜZ TESTLERİ

### 6.1 Dashboard Görünümü
- [ ] Hoş geldin mesajı görünüyor
- [ ] FTP hesapları kartları görünüyor
- [ ] Son aktiviteler tablosu görünüyor
- [ ] Responsive tasarım (mobil/tablet)
- [ ] Dark/Light tema geçişi

### 6.2 FTP Hesap Kartları
- [ ] Kartlar doğru bilgileri gösteriyor
- [ ] Hover efektleri çalışıyor
- [ ] Gözat butonu çalışıyor
- [ ] Durum göstergeleri doğru
- [ ] Dark tema'da kartlar görünür

### 6.3 Navigasyon
- [ ] Menü linkleri çalışıyor
- [ ] Breadcrumb navigasyonu
- [ ] Geri butonu çalışıyor
- [ ] Sayfa geçişleri sorunsuz

### 6.4 Responsive Tasarım
- [ ] Desktop görünümü (1920x1080)
- [ ] Tablet görünümü (768x1024)
- [ ] Mobil görünümü (375x667)
- [ ] Menü mobilde collapse oluyor

---

## 🎨 7. TEMA VE STİL TESTLERİ

### 7.1 Light Tema
- [ ] Light tema varsayılan olarak aktif
- [ ] Tüm elementler doğru renklerde
- [ ] Kontrast oranları uygun
- [ ] Okunabilirlik iyi

### 7.2 Dark Tema
- [ ] Dark tema toggle çalışıyor
- [ ] Tüm elementler dark tema renklerinde
- [ ] FTP kartları dark tema'da görünür
- [ ] Metin okunabilirliği iyi
- [ ] Tema tercihi hatırlanıyor

### 7.3 CSS ve JavaScript
- [ ] CSS dosyaları yükleniyor
- [ ] JavaScript hataları yok
- [ ] Animasyonlar çalışıyor
- [ ] Hover efektleri çalışıyor

---

## 🔌 8. API ENDPOINT TESTLERİ

### 8.1 FTP API Endpoints
- [ ] `POST /api/upload/{accountId}` - Dosya yükleme
- [ ] `GET /api/download/{accountId}/{filename}` - Dosya indirme
- [ ] `DELETE /api/delete/{accountId}/{filename}` - Dosya silme
- [ ] `GET /api/list/{accountId}` - Dosya listeleme

### 8.2 Admin API Endpoints
- [ ] `DELETE /api/account/{accountId}` - Hesap silme
- [ ] `DELETE /api/admin/user/{userId}` - Kullanıcı silme

### 8.3 API Response Formatları
- [ ] Başarılı işlemler için 200/201 status
- [ ] Hata durumları için uygun status kodları
- [ ] JSON response formatı doğru
- [ ] Error mesajları anlaşılır

---

## 📝 9. LOG VE MONİTÖRİNG TESTLERİ

### 9.1 Transfer Logları
- [ ] Dosya yükleme loglanıyor
- [ ] Dosya indirme loglanıyor
- [ ] Dosya silme loglanıyor
- [ ] Log bilgileri doğru (kullanıcı, tarih, işlem)

### 9.2 Hata Logları
- [ ] Hatalar log dosyasına yazılıyor
- [ ] Log seviyeleri doğru
- [ ] Stack trace'ler görünüyor
- [ ] Log dosyası boyutu kontrolü

### 9.3 Dashboard Logları
- [ ] Son aktiviteler görünüyor
- [ ] Log tarihleri doğru
- [ ] Kullanıcı bilgileri doğru
- [ ] İşlem durumları doğru

---

## ⚡ 10. PERFORMANS TESTLERİ

### 10.1 Sayfa Yükleme
- [ ] Ana sayfa < 2 saniye
- [ ] Dashboard < 3 saniye
- [ ] Admin paneli < 3 saniye
- [ ] Dosya listesi < 5 saniye

### 10.2 Dosya İşlemleri
- [ ] Küçük dosya yükleme < 5 saniye
- [ ] Büyük dosya yükleme < 30 saniye
- [ ] Dosya listesi < 3 saniye
- [ ] Dosya indirme < 10 saniye

### 10.3 Bellek Kullanımı
- [ ] Uygulama başlangıç bellek kullanımı
- [ ] Dosya işlemi sonrası bellek temizliği
- [ ] Memory leak kontrolü

---

## 🚨 11. HATA DURUMLARI TESTLERİ

### 11.1 Ağ Hataları
- [ ] FTP sunucusu erişilemez
- [ ] Geçersiz FTP bilgileri
- [ ] Timeout durumları
- [ ] Bağlantı kopması

### 11.2 Dosya Hataları
- [ ] Geçersiz dosya formatı
- [ ] Çok büyük dosya
- [ ] Bozuk dosya
- [ ] İzin hatası

### 11.3 Sistem Hataları
- [ ] Veritabanı bağlantı hatası
- [ ] Disk alanı dolu
- [ ] Bellek yetersizliği
- [ ] Uygulama çökmesi

---

## 🔄 12. ENTEGRASYON TESTLERİ

### 12.1 FTP Sunucu Entegrasyonu
- [ ] Gerçek FTP sunucusu ile test
- [ ] Gerçek SFTP sunucusu ile test
- [ ] Farklı FTP sunucu türleri
- [ ] Güvenlik duvarı arkasındaki sunucular

### 12.2 Veritabanı Entegrasyonu
- [ ] H2 veritabanı ile test
- [ ] Veri tutarlılığı
- [ ] Transaction yönetimi
- [ ] Backup/restore

---

## 📱 13. CROSS-BROWSER TESTLERİ

### 13.1 Tarayıcı Uyumluluğu
- [ ] Chrome (son versiyon)
- [ ] Firefox (son versiyon)
- [ ] Edge (son versiyon)
- [ ] Safari (Mac)

### 13.2 İşletim Sistemi Uyumluluğu
- [ ] Windows 10/11
- [ ] macOS
- [ ] Linux

---

## ✅ 14. KABUL TESTLERİ

### 14.1 Fonksiyonel Gereksinimler
- [ ] Tüm temel fonksiyonlar çalışıyor
- [ ] Kullanıcı hikayeleri tamamlanmış
- [ ] İş kuralları uygulanmış

### 14.2 Performans Gereksinimleri
- [ ] Response time kabul edilebilir
- [ ] Eşzamanlı kullanıcı desteği
- [ ] Bellek kullanımı kabul edilebilir

### 14.3 Güvenlik Gereksinimleri
- [ ] Kimlik doğrulama çalışıyor
- [ ] Yetkilendirme çalışıyor
- [ ] Veri güvenliği sağlanmış

---

## 📋 15. TEST SONUÇLARI

### 15.1 Test Özeti
- **Toplam Test**: [Sayı]
- **Başarılı**: [Sayı]
- **Başarısız**: [Sayı]
- **Atlandı**: [Sayı]
- **Başarı Oranı**: [%]

### 15.2 Kritik Hatalar
- [ ] [Hata 1]
- [ ] [Hata 2]
- [ ] [Hata 3]

### 15.3 Öneriler
- [ ] [Öneri 1]
- [ ] [Öneri 2]
- [ ] [Öneri 3]

---

## 📝 16. TEST NOTLARI

### 16.1 Test Ortamı Bilgileri
- **Sunucu**: [Sunucu bilgileri]
- **Veritabanı**: [Veritabanı bilgileri]
- **Test Verileri**: [Test veri bilgileri]

### 16.2 Bilinen Sorunlar
- [ ] [Sorun 1]
- [ ] [Sorun 2]

### 16.3 Test Edilmeyen Özellikler
- [ ] [Özellik 1]
- [ ] [Özellik 2]

---

**Test Edildi**: [Tester Adı]  
**Tarih**: [Tarih]  
**Onay**: [Onaylayan Adı]  
**Sonraki Test Tarihi**: [Tarih]

---

## 🚀 HIZLI TEST SENARYOLARI

### Temel Akış Testi (5 dakika)
1. Uygulamayı aç
2. Admin olarak giriş yap (`admin` / `admin123`)
3. Yeni FTP hesabı ekle
4. Dashboard'a git
5. FTP hesabına gözat
6. Test dosyası yükle
7. Dosyayı indir
8. Dosyayı sil
9. Çıkış yap

### Hata Testi (3 dakika)
1. Yanlış bilgilerle giriş yapmaya çalış
2. Geçersiz FTP bilgileri ile hesap ekle
3. Çok büyük dosya yüklemeye çalış
4. Olmayan dosyayı silmeye çalış

### Tema Testi (2 dakika)
1. Light tema'da sayfaları kontrol et
2. Dark tema'ya geç
3. Tüm sayfaları dark tema'da kontrol et
4. FTP kartlarının dark tema'da görünür olduğunu kontrol et

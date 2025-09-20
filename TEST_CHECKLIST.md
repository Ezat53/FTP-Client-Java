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
- [x] Varsayılan admin hesabı ile giriş (`admin` / `45SbxhWTTsXD/2oXQju_LsW`)
- [x] Yanlış kullanıcı adı ile giriş denemesi (hata mesajı)
- [x] Yanlış şifre ile giriş denemesi (hata mesajı)
- [x] Boş alanlarla giriş denemesi (validasyon hatası)

### 2.2 Yetkilendirme
- [x] Giriş yapmadan admin paneline erişim (yönlendirme)
- [x] Giriş yapmadan dashboard'a erişim (yönlendirme)
- [x] Giriş yapmadan API endpoint'lerine erişim (401 hatası)

### 2.3 Çıkış İşlemleri
- [x] Logout butonu çalışıyor
- [x] Çıkış sonrası güvenli sayfalara erişim engelleniyor
- [x] Session temizleniyor

### 2.4 Güvenlik
- [x] CSRF koruması aktif
- [x] SQL injection koruması
- [x] XSS koruması
- [x] Dosya yükleme güvenliği

---

## 👤 3. KULLANICI YÖNETİMİ TESTLERİ

### 3.1 Kullanıcı Oluşturma (Admin)
- [x] Yeni kullanıcı ekleme formu açılıyor
- [x] Geçerli bilgilerle kullanıcı oluşturma
- [ ] Kullanıcı adı zaten var hatası
- [x] E-posta formatı validasyonu
- [x] Şifre uzunluk validasyonu
- [x] Rol seçimi (ADMIN/USER)

### 3.2 Kullanıcı Düzenleme (Admin)
- [x] Kullanıcı düzenleme formu açılıyor
- [x] Mevcut bilgiler formda görünüyor
- [x] Bilgi güncelleme başarılı
- [x] Şifre değiştirme (opsiyonel)

### 3.3 Kullanıcı Silme (Admin)
- [x] Kullanıcı silme onayı
- [x] Kullanıcı başarıyla siliniyor
- [x] Silinen kullanıcı ile giriş yapılamıyor

### 3.4 Kullanıcı Listesi
- [x] Tüm kullanıcılar listeleniyor
- [x] Kullanıcı bilgileri doğru görünüyor
- [ ] Arama/filtreleme çalışıyor

---

## 🖥️ 4. FTP HESAP YÖNETİMİ TESTLERİ

### 4.1 FTP Hesabı Oluşturma (Admin)
- [x] FTP hesabı ekleme formu açılıyor
- [x] FTP bilgileri girişi (host, port, username, password)
- [x] Protokol seçimi (FTP/SFTP)
- [x] Kullanıcı ataması (çoklu seçim)
- [x] Remote path belirleme
- [x] Geçerli bilgilerle hesap oluşturma
- [x] Bağlantı testi

### 4.2 FTP Hesabı Düzenleme (Admin)
- [x] FTP hesabı düzenleme formu açılıyor
- [x] Mevcut bilgiler formda görünüyor
- [x] Bilgi güncelleme başarılı
- [x] Kullanıcı atamaları güncelleniyor

### 4.3 FTP Hesabı Silme (Admin)
- [x] FTP hesabı silme onayı
- [x] Hesap başarıyla siliniyor
- [x] İlişkili kullanıcı atamaları temizleniyor

### 4.4 FTP Hesap Listesi
- [x] Tüm FTP hesapları listeleniyor
- [x] Hesap bilgileri doğru görünüyor
- [x] Durum göstergeleri çalışıyor

---

## 📁 5. DOSYA İŞLEMLERİ TESTLERİ

### 5.1 Dosya Listeleme
- [x] FTP hesabına bağlanma
- [x] Dosya listesi görüntüleme
- [x] Klasör yapısı görüntüleme
- [x] Dosya boyutu ve tarih bilgileri
- [x] Hata durumunda uygun mesaj

### 5.2 Dosya Yükleme
- [x] Dosya seçme (tek dosya)
- [ ] Dosya seçme (çoklu dosya)
- [x] Desteklenen dosya formatları
- [x] Dosya boyutu limiti (16MB)
- [x] Yükleme progress göstergesi
- [x] Başarılı yükleme mesajı
- [x] Hata durumunda uygun mesaj

### 5.3 Dosya İndirme
- [x] Dosya indirme başlatma
- [x] Dosya adı korunuyor
- [x] Dosya içeriği doğru
- [x] Büyük dosya indirme
- [x] Hata durumunda uygun mesaj

### 5.4 Dosya Silme
- [x] Dosya silme onayı
- [x] Dosya başarıyla siliniyor
- [x] Silme işlemi loglanıyor
- [x] Hata durumunda uygun mesaj

---

## 📊 6. DASHBOARD VE ARAYÜZ TESTLERİ

### 6.1 Dashboard Görünümü
- [x] Hoş geldin mesajı görünüyor
- [x] FTP hesapları kartları görünüyor
- [x] Son aktiviteler tablosu görünüyor
- [x] Responsive tasarım (mobil/tablet)
- [x] Dark/Light tema geçişi

### 6.2 FTP Hesap Kartları
- [x] Kartlar doğru bilgileri gösteriyor
- [x] Hover efektleri çalışıyor
- [x] Gözat butonu çalışıyor
- [x] Durum göstergeleri doğru
- [x] Dark tema'da kartlar görünür

### 6.3 Navigasyon
- [x] Menü linkleri çalışıyor
- [x] Breadcrumb navigasyonu
- [x] Geri butonu çalışıyor
- [x] Sayfa geçişleri sorunsuz

### 6.4 Responsive Tasarım
- [x] Desktop görünümü (1920x1080)
- [x] Tablet görünümü (768x1024)
- [x] Mobil görünümü (375x667)
- [x] Menü mobilde collapse oluyor

---

## 🎨 7. TEMA VE STİL TESTLERİ

### 7.1 Light Tema
- [x] Light tema varsayılan olarak aktif
- [x] Tüm elementler doğru renklerde
- [x] Kontrast oranları uygun
- [x] Okunabilirlik iyi

### 7.2 Dark Tema
- [x] Dark tema toggle çalışıyor
- [x] Tüm elementler dark tema renklerinde
- [x] FTP kartları dark tema'da görünür
- [x] Metin okunabilirliği iyi
- [x] Tema tercihi hatırlanıyor

### 7.3 CSS ve JavaScript
- [x] CSS dosyaları yükleniyor
- [x] JavaScript hataları yok
- [x] Animasyonlar çalışıyor
- [x] Hover efektleri çalışıyor

---

## 🔌 8. API ENDPOINT TESTLERİ

### 8.1 FTP API Endpoints
- [x] `POST /api/upload/{accountId}` - Dosya yükleme
- [x] `GET /api/download/{accountId}/{filename}` - Dosya indirme
- [x] `DELETE /api/delete/{accountId}/{filename}` - Dosya silme
- [x] `GET /api/list/{accountId}` - Dosya listeleme

### 8.2 Admin API Endpoints
- [x] `DELETE /api/account/{accountId}` - Hesap silme
- [x] `DELETE /api/admin/user/{userId}` - Kullanıcı silme

### 8.3 API Response Formatları
- [x] Başarılı işlemler için 200/201 status
- [x] Hata durumları için uygun status kodları
- [x] JSON response formatı doğru
- [x] Error mesajları anlaşılır

---

## 📝 9. LOG VE MONİTÖRİNG TESTLERİ

### 9.1 Transfer Logları
- [x] Dosya yükleme loglanıyor
- [x] Dosya indirme loglanıyor
- [x] Dosya silme loglanıyor
- [x] Log bilgileri doğru (kullanıcı, tarih, işlem)

### 9.2 Hata Logları
- [x] Hatalar log dosyasına yazılıyor
- [x] Log seviyeleri doğru
- [x] Stack trace'ler görünüyor
- [x] Log dosyası boyutu kontrolü

### 9.3 Dashboard Logları
- [x] Son aktiviteler görünüyor
- [x] Log tarihleri doğru
- [x] Kullanıcı bilgileri doğru
- [x] İşlem durumları doğru

---

## ⚡ 10. PERFORMANS TESTLERİ

### 10.1 Sayfa Yükleme
- [x] Ana sayfa < 2 saniye
- [x] Dashboard < 3 saniye
- [x] Admin paneli < 3 saniye
- [x] Dosya listesi < 5 saniye

### 10.2 Dosya İşlemleri
- [x] Küçük dosya yükleme < 5 saniye
- [x] Büyük dosya yükleme < 30 saniye
- [x] Dosya listesi < 3 saniye
- [x] Dosya indirme < 10 saniye

### 10.3 Bellek Kullanımı
- [x] Uygulama başlangıç bellek kullanımı
- [x] Dosya işlemi sonrası bellek temizliği
- [x] Memory leak kontrolü

---

## 🚨 11. HATA DURUMLARI TESTLERİ

### 11.1 Ağ Hataları
- [x] FTP sunucusu erişilemez
- [x] Geçersiz FTP bilgileri
- [x] Timeout durumları
- [x] Bağlantı kopması

### 11.2 Dosya Hataları
- [ ] Geçersiz dosya formatı
- [ ] Çok büyük dosya
- [ ] Bozuk dosya
- [ ] İzin hatası

### 11.3 Sistem Hataları
- [x] Veritabanı bağlantı hatası
- [x] Disk alanı dolu
- [x] Bellek yetersizliği
- [x] Uygulama çökmesi

---

## 🔄 12. ENTEGRASYON TESTLERİ

### 12.1 FTP Sunucu Entegrasyonu
- [x] Gerçek FTP sunucusu ile test
- [x] Gerçek SFTP sunucusu ile test
- [x] Farklı FTP sunucu türleri
- [x] Güvenlik duvarı arkasındaki sunucular

### 12.2 Veritabanı Entegrasyonu
- [x] H2 veritabanı ile test
- [x] Veri tutarlılığı
- [x] Transaction yönetimi
- [x] Backup/restore

---

## 📱 13. CROSS-BROWSER TESTLERİ

### 13.1 Tarayıcı Uyumluluğu
- [x] Chrome (son versiyon)
- [ ] Firefox (son versiyon)
- [ ] Edge (son versiyon)
- [ ] Safari (Mac)

### 13.2 İşletim Sistemi Uyumluluğu
- [x] Windows 10/11
- [ ] macOS
- [ ] Linux

---

## ✅ 14. KABUL TESTLERİ

### 14.1 Fonksiyonel Gereksinimler
- [x] Tüm temel fonksiyonlar çalışıyor
- [x] Kullanıcı hikayeleri tamamlanmış
- [x] İş kuralları uygulanmış

### 14.2 Performans Gereksinimleri
- [x] Response time kabul edilebilir
- [x] Eşzamanlı kullanıcı desteği
- [x] Bellek kullanımı kabul edilebilir

### 14.3 Güvenlik Gereksinimleri
- [x] Kimlik doğrulama çalışıyor
- [x] Yetkilendirme çalışıyor
- [x] Veri güvenliği sağlanmış

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
2. Admin olarak giriş yap (`admin` / `45SbxhWTTsXD/2oXQju_LsW`)
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

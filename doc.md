Proje Dokümanı – Web Tabanlı FTP/SFTP/SCP Yönetim Sistemi
🎯 Ana Fikir

Kullanıcıların web arayüzü üzerinden FTP, SFTP, SCP protokolleri ile dosya transferi yapabilmelerini sağlamak.
Sistem üzerinde yetkilendirme olacak:

Admin: Kullanıcı oluşturabilir, FTP/SFTP/SCP hesaplarını yönetebilir.

User: Sadece giriş yapıp dosya aktarımı gerçekleştirebilir.

⚙️ Kullanılacak Teknolojiler

Backend:

Python (Flask veya FastAPI – tercih edebilirsin)

Paramiko (SFTP ve SCP bağlantıları için)

ftplib (FTP için)

SQLAlchemy (SQLite ile ORM)

Database:

SQLite (lokal kullanım için basit ve güvenilir)

Frontend (Web Arayüz):

HTML, CSS, JavaScript

Bootstrap veya TailwindCSS (kullanıcı dostu tasarım için)

Jinja2 (Flask için template engine)

Güvenlik:

bcrypt veya passlib (şifre hashlemek için)

JWT veya Flask-Login (oturum yönetimi için)

HTTPS (deployment aşamasında SSL/TLS ile güvenlik)

Ekstra:

Docker (deploy için isteğe bağlı)

Gunicorn + Nginx (production için)

🗄️ Database Tasarımı (SQLite)

Tablolar:

users

id (int, pk)

username (unique)

password_hash (string)

role (admin/user)

ftp_accounts

id (int, pk)

name (hesap adı/etiket)

protocol (ftp/sftp/scp)

host

port

username

password (opsiyonel veya şifreli saklanmalı)

owner_id (user id, foreign key)

transfer_logs

id

user_id (hangi kullanıcı işlem yaptı)

ftp_account_id

action (upload/download/delete vs.)
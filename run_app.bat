@echo off
REM Flask FTP Uygulaması Windows Çalıştırma Scripti
REM Bu script uygulamayı portable Python 3.11.9 ile çalıştırır

REM Script'in bulunduğu dizini al
set SCRIPT_DIR=%~dp0
set SCRIPT_DIR=%SCRIPT_DIR:~0,-1%

REM Portable Python yolunu ayarla
set PYTHON_PATH=%SCRIPT_DIR%\python311\python.exe
set PIP_PATH=%SCRIPT_DIR%\python311\Scripts\pip.exe

echo ======================================
echo Flask FTP Uygulaması Başlatılıyor...
echo ======================================
echo Script Dizini: %SCRIPT_DIR%
echo Python Yolu: %PYTHON_PATH%
echo ======================================

REM Python'un mevcut olup olmadığını kontrol et
if not exist "%PYTHON_PATH%" (
    echo ❌ HATA: Portable Python bulunamadı: %PYTHON_PATH%
    echo Lütfen python311 klasörünün mevcut olduğundan emin olun.
    pause
    exit /b 1
)

REM Python versiyonunu göster
echo Python Versiyonu:
"%PYTHON_PATH%" --version

echo ======================================

REM Uygulama dizinine geç
cd /d "%SCRIPT_DIR%"

REM Gerekli kütüphaneleri kontrol et
echo Gerekli kütüphaneler kontrol ediliyor...
"%PYTHON_PATH%" -c "import sys; required_modules = ['flask', 'flask_login', 'flask_sqlalchemy', 'paramiko', 'cryptography']; missing_modules = []; [missing_modules.append(module) if __import__(module) or True else print(f'✅ {module}') for module in required_modules]; [print(f'❌ {module}') for module in missing_modules]; sys.exit(1 if missing_modules else 0); print('\n✅ Tüm gerekli kütüphaneler mevcut!')"

if errorlevel 1 (
    echo ❌ Gerekli kütüphaneler eksik. Lütfen kurulum talimatlarını takip edin.
    pause
    exit /b 1
)

echo.
echo ✅ Tüm gerekli kütüphaneler mevcut!

echo ======================================
echo 🚀 Uygulama başlatılıyor...
echo Uygulamayı durdurmak için Ctrl+C tuşlarına basın
echo ======================================

REM Flask uygulamasını başlat
set FLASK_APP=app.py
set FLASK_ENV=development
set PYTHONPATH=%SCRIPT_DIR%;%PYTHONPATH%

"%PYTHON_PATH%" -m flask run --host=0.0.0.0 --port=6756

echo ======================================
echo Uygulama durduruldu.
echo ======================================
pause

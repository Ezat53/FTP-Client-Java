#!/bin/bash

# Flask FTP Uygulaması Portable Çalıştırma Scripti
# Bu script uygulamayı portable Python 3.11.9 ile çalıştırır

# Script'in bulunduğu dizini al
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# GLIBC 2.38 portable ile Python'u çalıştır
export LD_LIBRARY_PATH="$SCRIPT_DIR/glibc-2.38/lib:$LD_LIBRARY_PATH"

# Portable Python yolunu ayarla
PYTHON_PATH="$SCRIPT_DIR/python311/bin/python3"
PIP_PATH="$SCRIPT_DIR/python311/bin/pip3"

echo "======================================"
echo "Flask FTP Uygulaması Başlatılıyor..."
echo "======================================"
echo "Script Dizini: $SCRIPT_DIR"
echo "Python Yolu: $PYTHON_PATH"
echo "======================================"

# Python'un mevcut olup olmadığını kontrol et
if [ ! -f "$PYTHON_PATH" ]; then
    echo "❌ HATA: Portable Python bulunamadı: $PYTHON_PATH"
    echo "Lütfen python311 klasörünün mevcut olduğundan emin olun."
    exit 1
fi

# Python versiyonunu göster
echo "Python Versiyonu:"
"$PYTHON_PATH" --version

echo "======================================"

# Uygulama dizinine geç
cd "$SCRIPT_DIR"

# Gerekli kütüphaneleri kontrol et
echo "Gerekli kütüphaneler kontrol ediliyor..."
"$PYTHON_PATH" -c "
import sys
required_modules = ['flask', 'flask_login', 'flask_sqlalchemy', 'paramiko', 'cryptography']
missing_modules = []

for module in required_modules:
    try:
        __import__(module)
        print(f'✅ {module}')
    except ImportError:
        missing_modules.append(module)
        print(f'❌ {module}')

if missing_modules:
    print(f'\\n❌ Eksik modüller: {missing_modules}')
    sys.exit(1)
else:
    print('\\n✅ Tüm gerekli kütüphaneler mevcut!')
"

if [ $? -ne 0 ]; then
    echo "❌ Gerekli kütüphaneler eksik. Lütfen kurulum talimatlarını takip edin."
    exit 1
fi

echo "======================================"
echo "🚀 Uygulama başlatılıyor..."
echo "Uygulamayı durdurmak için Ctrl+C tuşlarına basın"
echo "======================================"

# Flask uygulamasını başlat
export FLASK_APP=app.py
export FLASK_ENV=development
export PYTHONPATH="$SCRIPT_DIR:$PYTHONPATH"

"$PYTHON_PATH" -m flask run --host=0.0.0.0 --port=6756

echo "======================================"
echo "Uygulama durduruldu."
echo "======================================"

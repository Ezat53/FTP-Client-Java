#!/bin/bash

# Xfer FTP Web Service - RedHat Deployment Script

echo "=== Xfer FTP Web Service - RedHat Deployment ==="

# Tomcat configuration
TOMCAT_HOME="/opt/apache-tomcat-8.5.47"
WAR_FILE="xfer-ftp-web-service.war"
APP_NAME="xfer-ftp-web-service"

# Check if running as root
if [ "$EUID" -ne 0 ]; then
    echo "Error: This script must be run as root"
    exit 1
fi

# Check if Tomcat exists
if [ ! -d "$TOMCAT_HOME" ]; then
    echo "Error: Tomcat directory not found at $TOMCAT_HOME"
    exit 1
fi

echo "Tomcat home: $TOMCAT_HOME ✓"

# Stop Tomcat
echo "Stopping Tomcat..."
$TOMCAT_HOME/bin/shutdown.sh
sleep 5

# Check if Tomcat is stopped
if pgrep -f "tomcat" > /dev/null; then
    echo "Warning: Tomcat is still running, forcing shutdown..."
    pkill -f "tomcat"
    sleep 3
fi

# Remove old deployment
echo "Removing old deployment..."
rm -f $TOMCAT_HOME/webapps/$WAR_FILE
rm -rf $TOMCAT_HOME/webapps/$APP_NAME

# Deploy new WAR file
echo "Deploying new WAR file..."
cp target/$WAR_FILE $TOMCAT_HOME/webapps/

# Create necessary directories
echo "Creating application directories..."
mkdir -p $TOMCAT_HOME/webapps/$APP_NAME/data
mkdir -p $TOMCAT_HOME/webapps/$APP_NAME/uploads
chmod 755 $TOMCAT_HOME/webapps/$APP_NAME/data
chmod 755 $TOMCAT_HOME/webapps/$APP_NAME/uploads

# Set proper ownership
chown -R tomcat:tomcat $TOMCAT_HOME/webapps/$APP_NAME

# Start Tomcat
echo "Starting Tomcat..."
$TOMCAT_HOME/bin/startup.sh

# Wait for Tomcat to start
echo "Waiting for Tomcat to start..."
sleep 10

# Check if Tomcat is running
if pgrep -f "tomcat" > /dev/null; then
    echo "Tomcat started successfully ✓"
    echo ""
    echo "=== Deployment Complete ==="
    echo "Application URL: http://172.29.1.166:8080/$APP_NAME"
    echo "Default admin credentials:"
    echo "  Username: admin"
    echo "  Password: admin123"
    echo ""
    echo "Database file: $TOMCAT_HOME/webapps/$APP_NAME/data/ftp_manager.db"
    echo "Upload directory: $TOMCAT_HOME/webapps/$APP_NAME/uploads"
    echo ""
    echo "To check logs: tail -f $TOMCAT_HOME/logs/catalina.out"
else
    echo "Error: Tomcat failed to start!"
    echo "Check Tomcat logs for more information:"
    echo "  tail -f $TOMCAT_HOME/logs/catalina.out"
    exit 1
fi

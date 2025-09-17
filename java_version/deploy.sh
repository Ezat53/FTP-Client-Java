#!/bin/bash

# FTP Client Java Version Deployment Script

echo "=== FTP Client Java Version Deployment ==="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 11 or higher first."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "Error: Java 11 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "Java version: $JAVA_VERSION ✓"
echo "Maven version: $(mvn -version | head -n 1) ✓"

# Clean and build the project
echo "Building the project..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Error: Build failed!"
    exit 1
fi

echo "Build completed successfully ✓"

# Check if WAR file exists
WAR_FILE="target/ftp-client.war"
if [ ! -f "$WAR_FILE" ]; then
    echo "Error: WAR file not found at $WAR_FILE"
    exit 1
fi

echo "WAR file created: $WAR_FILE ✓"

# Check if Tomcat is running
TOMCAT_HOME=${TOMCAT_HOME:-$CATALINA_HOME}
if [ -z "$TOMCAT_HOME" ]; then
    echo "Warning: TOMCAT_HOME or CATALINA_HOME not set."
    echo "Please set TOMCAT_HOME environment variable to your Tomcat installation directory."
    echo "Example: export TOMCAT_HOME=/opt/tomcat"
    exit 1
fi

if [ ! -d "$TOMCAT_HOME" ]; then
    echo "Error: Tomcat directory not found at $TOMCAT_HOME"
    exit 1
fi

echo "Tomcat home: $TOMCAT_HOME ✓"

# Stop Tomcat if running
echo "Stopping Tomcat..."
$TOMCAT_HOME/bin/shutdown.sh 2>/dev/null || true
sleep 5

# Remove old deployment
echo "Removing old deployment..."
rm -rf $TOMCAT_HOME/webapps/ftp-client*
rm -f $TOMCAT_HOME/webapps/ftp-client.war

# Deploy new WAR file
echo "Deploying new WAR file..."
cp $WAR_FILE $TOMCAT_HOME/webapps/

# Create uploads directory
echo "Creating uploads directory..."
mkdir -p $TOMCAT_HOME/webapps/ftp-client/uploads
chmod 755 $TOMCAT_HOME/webapps/ftp-client/uploads

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
    echo "Application URL: http://localhost:8080/ftp-client"
    echo "Default admin credentials:"
    echo "  Username: admin"
    echo "  Password: admin123"
    echo ""
    echo "H2 Database Console: http://localhost:8080/ftp-client/h2-console"
    echo "  JDBC URL: jdbc:h2:file:./data/ftp_manager"
    echo "  Username: sa"
    echo "  Password: (leave empty)"
    echo ""
else
    echo "Error: Tomcat failed to start!"
    echo "Check Tomcat logs for more information:"
    echo "  $TOMCAT_HOME/logs/catalina.out"
    exit 1
fi

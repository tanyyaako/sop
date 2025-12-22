#!/bin/bash
# Скрипт для первоначальной настройки Jenkins

echo "Waiting for Jenkins to start..."
sleep 30

# Получаем initial admin password
JENKINS_PASSWORD=$(cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null || echo "admin")

echo "Jenkins initial admin password: $JENKINS_PASSWORD"
echo "Please use this password when first accessing Jenkins at http://localhost:8080"


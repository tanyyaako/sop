pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK 17'
    }

    environment {
        JAVA_HOME = tool 'JDK 17'
        MAVEN_HOME = tool 'Maven'
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from Git...'
                checkout scm
            }
        }
        
        stage('Build Contracts') {
            parallel failFast: false, {
                stage('Build hotelBooking-api') {
                    steps {
                        dir('hotelBooking-api') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh 'mvn clean install -DskipTests'
                                    } else {
                                        bat 'mvnw.cmd clean install -DskipTests'
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build hotelBooking-api: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build events-roomBooking-contract') {
                    steps {
                        dir('events-roomBooking-contract') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh 'mvn clean install -DskipTests'
                                    } else {
                                        bat 'mvnw.cmd clean install -DskipTests'
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build events-roomBooking-contract: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build Services') {
            parallel failFast: false, {
                stage('Build roomBooking') {
                    steps {
                        dir('roomBooking') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh '''
                                            # Copy dependencies to lib
                                            cp ../hotelBooking-api/target/hotelBooking-api-0.0.1-SNAPSHOT.jar lib/hotelBooking-api.jar || true
                                            cp ../events-roomBooking-contract/target/events-roomBooking-contract-1.0-SNAPSHOT.jar lib/events-roomBooking-contract.jar || true
                                            
                                            # Build project
                                            mvn clean package -DskipTests
                                        '''
                                    } else {
                                        bat '''
                                            copy ..\\hotelBooking-api\\target\\hotelBooking-api-0.0.1-SNAPSHOT.jar lib\\hotelBooking-api.jar
                                            copy ..\\events-roomBooking-contract\\target\\events-roomBooking-contract-1.0-SNAPSHOT.jar lib\\events-roomBooking-contract.jar
                                            mvnw.cmd clean package -DskipTests
                                        '''
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build roomBooking: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build pricing-service') {
                    steps {
                        dir('pricing-service') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh 'mvn clean package -DskipTests'
                                    } else {
                                        bat 'mvnw.cmd clean package -DskipTests'
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build pricing-service: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build notification-service') {
                    steps {
                        dir('notification-service') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh '''
                                            cp ../events-roomBooking-contract/target/events-roomBooking-contract-1.0-SNAPSHOT.jar lib/events-roomBooking-contract.jar || true
                                            mvn clean package -DskipTests
                                        '''
                                    } else {
                                        bat '''
                                            copy ..\\events-roomBooking-contract\\target\\events-roomBooking-contract-1.0-SNAPSHOT.jar lib\\events-roomBooking-contract.jar
                                            mvnw.cmd clean package -DskipTests
                                        '''
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build notification-service: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build audit-booking-service') {
                    steps {
                        dir('audit-booking-service') {
                            script {
                                try {
                                    if (isUnix()) {
                                        sh '''
                                            cp ../events-roomBooking-contract/target/events-roomBooking-contract-1.0-SNAPSHOT.jar lib/events-roomBooking-contract.jar || true
                                            mvn clean package -DskipTests
                                        '''
                                    } else {
                                        bat '''
                                            copy ..\\events-roomBooking-contract\\target\\events-roomBooking-contract-1.0-SNAPSHOT.jar lib\\events-roomBooking-contract.jar
                                            mvnw.cmd clean package -DskipTests
                                        '''
                                    }
                                } catch (Exception e) {
                                    echo "Failed to build audit-booking-service: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build Docker Images') {
            parallel failFast: false, {
                stage('Build pricing-service image') {
                    steps {
                        dir('pricing-service') {
                            script {
                                echo "Building pricing-service Docker image..."
                                try {
                                    if (isUnix()) {
                                        sh 'docker build -t bookings/pricing-service:latest .'
                                    } else {
                                        bat 'docker build -t bookings/pricing-service:latest .'
                                    }
                                    echo "✓ pricing-service image built successfully"
                                } catch (Exception e) {
                                    echo "✗ Failed to build pricing-service image: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build audit-booking-service image') {
                    steps {
                        dir('audit-booking-service') {
                            script {
                                echo "Building audit-booking-service Docker image..."
                                try {
                                    if (isUnix()) {
                                        sh 'docker build -t bookings/audit-booking-service:latest .'
                                    } else {
                                        bat 'docker build -t bookings/audit-booking-service:latest .'
                                    }
                                    echo "✓ audit-booking-service image built successfully"
                                } catch (Exception e) {
                                    echo "✗ Failed to build audit-booking-service image: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build notification-service image') {
                    steps {
                        dir('notification-service') {
                            script {
                                echo "Building notification-service Docker image..."
                                try {
                                    if (isUnix()) {
                                        sh 'docker build -t bookings/notification-service:latest .'
                                    } else {
                                        bat 'docker build -t bookings/notification-service:latest .'
                                    }
                                    echo "✓ notification-service image built successfully"
                                } catch (Exception e) {
                                    echo "✗ Failed to build notification-service image: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
                stage('Build roomBooking image') {
                    steps {
                        dir('roomBooking') {
                            script {
                                echo "Building roomBooking Docker image..."
                                try {
                                    // Проверяем, что JAR файлы есть в lib/
                                    if (isUnix()) {
                                        sh '''
                                            if [ ! -f lib/hotelBooking-api.jar ] || [ ! -f lib/events-roomBooking-contract.jar ]; then
                                                echo "ERROR: Required JAR files not found in lib/"
                                                echo "Expected: lib/hotelBooking-api.jar and lib/events-roomBooking-contract.jar"
                                                exit 1
                                            fi
                                        '''
                                        sh 'docker build -t bookings/room-booking:latest .'
                                    } else {
                                        bat '''
                                            if not exist lib\\hotelBooking-api.jar (
                                                echo ERROR: Required JAR files not found in lib/
                                                exit /b 1
                                            )
                                            if not exist lib\\events-roomBooking-contract.jar (
                                                echo ERROR: Required JAR files not found in lib/
                                                exit /b 1
                                            )
                                        '''
                                        bat 'docker build -t bookings/room-booking:latest .'
                                    }
                                    echo "✓ roomBooking image built successfully"
                                } catch (Exception e) {
                                    echo "✗ Failed to build roomBooking image: ${e.getMessage()}"
                                    throw e
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Build completed successfully!'
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true, allowEmptyArchive: true
        }
        failure {
            echo 'Build failed!'
        }
    }
}

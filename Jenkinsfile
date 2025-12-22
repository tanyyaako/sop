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
            steps {
                script {
                    try {
                        echo 'Building Docker images with docker-compose...'
                        if (isUnix()) {
                            // Пробуем docker compose (новый синтаксис) или docker-compose (старый)
                            sh '''
                                if command -v docker-compose &> /dev/null; then
                                    docker-compose build --no-cache
                                elif docker compose version &> /dev/null; then
                                    docker compose build --no-cache
                                else
                                    echo "Neither docker-compose nor docker compose found"
                                    exit 1
                                fi
                            '''
                        } else {
                            bat '''
                                docker-compose build --no-cache || docker compose build --no-cache
                            '''
                        }
                    } catch (Exception e) {
                        echo "Failed to build Docker images: ${e.getMessage()}"
                        throw e
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

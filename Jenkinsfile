pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from Git...'
                checkout scm
            }
        }
        
        stage('Build Dependencies') {
            steps {
                echo 'Building hotelBooking-api...'
                dir('hotelBooking-api') {
                    sh './mvnw clean install -DskipTests || mvn clean install -DskipTests'
                }
                
                echo 'Building events-roomBooking-contract...'
                dir('events-roomBooking-contract') {
                    sh './mvnw clean install -DskipTests || mvn clean install -DskipTests'
                }
            }
        }
        
        stage('Build Services') {
            steps {
                script {
                    // Build roomBooking
                    dir('roomBooking') {
                        sh '''
                            # Copy dependencies to lib
                            cp ../hotelBooking-api/target/hotelBooking-api-0.0.1-SNAPSHOT.jar lib/hotelBooking-api.jar || true
                            cp ../events-roomBooking-contract/target/events-roomBooking-contract-1.0-SNAPSHOT.jar lib/events-roomBooking-contract.jar || true
                            
                            # Build project
                            ./mvnw clean package -DskipTests || mvn clean package -DskipTests
                        '''
                    }
                    
                    // Build pricing-service
                    dir('pricing-service') {
                        sh './mvnw clean package -DskipTests || mvn clean package -DskipTests'
                    }
                    
                    // Build notification-service
                    dir('notification-service') {
                        sh '''
                            cp ../events-roomBooking-contract/target/events-roomBooking-contract-1.0-SNAPSHOT.jar lib/events-roomBooking-contract.jar || true
                            ./mvnw clean package -DskipTests || mvn clean package -DskipTests
                        '''
                    }
                    
                    // Build audit-booking-service
                    dir('audit-booking-service') {
                        sh '''
                            cp ../events-roomBooking-contract/target/events-roomBooking-contract-1.0-SNAPSHOT.jar lib/events-roomBooking-contract.jar || true
                            ./mvnw clean package -DskipTests || mvn clean package -DskipTests
                        '''
                    }
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                echo 'Building Docker images...'
                sh 'docker-compose build'
            }
        }
        
        // stage('Docker Up') {
        //     steps {
        //         echo 'Starting containers...'
        //         sh 'docker-compose up -d'
        //     }
        // }
        
        stage('Health Check') {
            steps {
                echo 'Waiting for services to start...'
                sleep(time: 30, unit: 'SECONDS')
                
                script {
                    sh 'docker-compose ps'
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
            sh 'docker-compose ps'
        }
        failure {
            echo 'Pipeline failed!'
            sh 'docker-compose logs --tail=50'
        }
        always {
            echo 'Build completed!'
        }
    }
}


pipeline {
    agent {
        label 'docker'
    }
    stages {
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('Api tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('e2e tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('print logs') {
            steps {
                echo "Trabajo ${env.JOB_NAME}"
                echo "Ejecución número ${env.BUILD_NUMBER}"
            }
        }
    }
    post {
        always {
            junit 'results/*_result.xml'
        }
        failure {
            emailext subject: "Pipeline fallido: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: "El trabajo ${env.JOB_NAME} ha fallado en la ejecución número ${env.BUILD_NUMBER}. Por favor, revisa los registros y toma las acciones necesarias.",
                    to: "admaigualca@gmail.com"
        }
    }
}

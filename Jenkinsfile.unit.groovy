pipeline {
    agent {
        label 'docker'
    }
    stages {
        stage('Build') {
            steps {
                echo 'Iniciando etapa de compilación!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('End-to-end tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('Print logs') {
            steps {
                echo "Trabajo: ${env.JOB_NAME}"
                echo "Ejecución número: ${env.BUILD_NUMBER}"
            }
        }
    }
    post {
        always {
            junit 'results/*_result.xml'
        }
        failure {
            script {
                def jobName = env.JOB_NAME
                def buildNumber = env.BUILD_NUMBER
                def recipient = "admaigualca@gmail.com"
                def subject = "Pipeline fallido: ${jobName} #${buildNumber}"
                def body = "El trabajo ${jobName} ha fallado en la ejecución número ${buildNumber}. Por favor, revisa los registros y toma las acciones necesarias."

                emailext (
                    to: recipient,
                    subject: subject,
                    body: body
                )
            }
        }
    }
}

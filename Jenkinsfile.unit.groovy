pipeline {
    agent {
        label 'docker'
    }
    stages {
        stage('Build') {
            steps {
                script {
                    echo 'Iniciando etapa de compilación!'
                    sh 'make build'
                }
            }
        }
        stage('Unit tests') {
            steps {
                script {
                    sh 'make test-unit'
                    archiveArtifacts artifacts: 'results/*.xml'
                }
            }
        }
        stage('API tests') {
            steps {
                script {
                    sh 'make test-api'
                    archiveArtifacts artifacts: 'results/*.xml'
                }
            }
        }
        stage('End-to-end tests') {
            steps {
                script {
                    sh 'make test-e2e'
                    archiveArtifacts artifacts: 'results/*.xml'
                }
            }
        }
        stage('Print logs') {
            steps {
                script {
                    echo "Trabajo: ${env.JOB_NAME}"
                    echo "Ejecución número: ${env.BUILD_NUMBER}"
                }
            }
        }
    }
    post {
        always {
            script {
                junit 'results/*_result.xml'
            }
        }
        failure {
            script {
                def jobName = env.JOB_NAME
                def buildNumber = env.BUILD_NUMBER
                def recipient = "admaigualca@gmail.com"
                def subject = "Pipeline fallido: ${jobName} #${buildNumber}"
                def body = |
                    <p>El pipeline <strong>${jobName} #${buildNumber}</strong> ha fallado.</p>
                    <p>Revisar el estado del pipeline <a href="${env.BUILD_URL}">aquí</a>.</p>
                    <p>Puedes ver los logs de la ejecución <a href="${env.BUILD_URL}console">aquí</a>.</p>
                    <p>Saludos.</p>
                emailext (
                    to: recipient,
                    subject: subject,
                    body: body
                )
            }
        }
    }
}

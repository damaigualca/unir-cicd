pipeline {
    agent {
        label 'docker'
    }
    stages {
        stage('Build') {
            description 'Compila el proyecto'
            steps {
                echo 'Iniciando etapa de compilación!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            description 'Ejecuta pruebas unitarias'
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('API tests') {
            description 'Ejecuta pruebas de API'
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('End-to-end tests') {
            description 'Ejecuta pruebas end-to-end'
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('Print logs') {
            description 'Imprime información del trabajo y la ejecución'
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

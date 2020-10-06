 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build' 
                archiveArtifacts artifacts: '**output/*.jar', fingerprint: true 
            }
        }
    }
}

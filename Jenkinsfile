 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build deobf' 
                archiveArtifacts artifacts: '**output/*.jar', fingerprint: true 
            }
        }
    }
}

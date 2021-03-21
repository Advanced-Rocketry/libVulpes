 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build deobf curseforge236541' 
                archiveArtifacts artifacts: '**output/*.jar', fingerprint: true 
            }
        }
    }
}

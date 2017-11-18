 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'gradle clean'
                sh 'gradle build' 
                sh 'gradle curseforge236541'
                archiveArtifacts artifacts: '**output/*.jar', fingerprint: true 
            }
        }
    }
}
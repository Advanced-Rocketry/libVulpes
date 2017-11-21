 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'gradle clean build curseforge236541' 
                archiveArtifacts artifacts: '**output/*.jar', fingerprint: true 
            }
        }
    }
}

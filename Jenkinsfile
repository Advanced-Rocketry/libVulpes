 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
		sh 'mkdir -p libs'
                sh 'cp ../../libraries/*.jar ./libs'
            
                sh 'gradle clean build' 
                archiveArtifacts artifacts: '**output/*.jar', fingerprint: true 
            }
        }
    }
}

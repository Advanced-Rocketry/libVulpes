 pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
		sh 'mkdir -p libs'
            
                sh 'gradlew clean build mavenPublish' 
                archiveArtifacts artifacts: '**output/*.jar', fingerprint: true 
            }
        }
    }
}

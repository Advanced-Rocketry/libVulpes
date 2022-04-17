 pipeline {
    agent any

    withEnv(['JAVA_HOME=/usr/java/jdk1.8.0_311-amd64']) { somethingHere }

    stages {
        stage('Build') {
            steps {
		sh 'mkdir -p libs'
                sh 'chmod a+x ./gradlew'
                sh './gradlew clean build mavenPublish' 
                archiveArtifacts artifacts: '**output/*.jar', fingerprint: true 
            }
        }
    }
}

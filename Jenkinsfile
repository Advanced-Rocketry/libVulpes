 pipeline {
    agent any

    node {
    jdk = tool name: 'JDK8'
    env.JAVA_HOME = "${jdk}"

    echo "jdk installation path is: ${jdk}"

    // next 2 are equivalents
    sh "${jdk}/bin/java -version"

    // note that simple quote strings are not evaluated by Groovy
    // substitution is done by shell script using environment
    sh '$JAVA_HOME/bin/java -version'
}

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

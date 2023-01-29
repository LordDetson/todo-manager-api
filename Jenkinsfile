pipeline {
  agent any
  tools {
    jdk 'openjdk17'
    maven 'maven-3.6.3'
  }
  stages {
    stage ('Build todo-manager-api') {
      steps {
        withSonarQubeEnv('SonarQube') {
          sh 'mvn clean -Dmaven.test.failure.ignore=true install $SONAR_MAVEN_GOAL'
        }
      }
      post {
        success {
          junit '**/surefire-reports/*.xml'
        }
      }
    }
    stage ("Build and push docker image") {
      environment {
        DOCKER_HUB_LOGIN = credentials("DockerHub")
      }
      steps {
        sh 'mvn spring-boot:build-image -DpublishRegistry.username=$DOCKER_HUB_LOGIN_USR -DpublishRegistry.password=$DOCKER_HUB_LOGIN_PSW'
      }
    }
  }
}

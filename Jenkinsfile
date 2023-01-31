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
    stage ("Run docker image") {
      steps {
        sh "docker run -d --name todo-manager-api -p 80:8080 --network todo-manager -e APP_WORKDIR=/var/.todo-manager-api todo-manager-api"
      }
    }
  }
}

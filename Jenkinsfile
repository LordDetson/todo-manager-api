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
      environment {
        CONTAINER_NAME = "todo-manager-api"
        IMAGE_NAME = "lorddetson/todo-manager-api"
      }
      steps {
        sh "docker rm -f $CONTAINER_NAME"
        sh "docker rmi $IMAGE_NAME"
        sh "docker run -d --name $CONTAINER_NAME -p 80:8080 --network todo-manager -v /var/.todo-manager-api:/var/.todo-manager-api -e APP_WORKDIR=/var/.todo-manager-api $IMAGE_NAME"
      }
    }
  }
}

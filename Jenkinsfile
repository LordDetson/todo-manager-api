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
        DB_LOGIN = credentials("ToDoDB")
        DB_NAME = "todo"
        CONTAINER_NAME = "todo-manager-api"
        IMAGE_NAME = "lorddetson/todo-manager-api"
      }
      steps {
        sh "docker rm -f $CONTAINER_NAME"
        sh "docker rmi $IMAGE_NAME"
        sh "docker run -d --name $CONTAINER_NAME -p 80:8081 --network todo-manager-api -e DB_USER=$DB_LOGIN_USR -e DB_PASSWORD=$DB_LOGIN_PSW -e DB_NAME=$DB_NAME $IMAGE_NAME --spring.profiles.active=prod"
      }
    }
  }
}

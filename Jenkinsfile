pipeline {
  agent {
    label 'maven'
  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean compile'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test'
      }
    }
    stage('Deploy') {
      when {
        anyOf {
          branch 'develop'
          branch 'master'
        }
      }
      steps {
        sh 'mvn -P deploy deploy'
      }
    }
    stage('Site') {
      when {
        anyOf {
          branch 'develop'
          branch 'master'
        }
      }
      steps {
        sh 'mvn site-deploy'
      }
    }
  }
}
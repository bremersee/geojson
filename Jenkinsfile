pipeline {
  agent {
    label 'maven'
  }
  environment {
    CODECOV_TOKEN = credentials('geojson-codecov-token')
    DEPLOY = false
    SNAPSHOT_SITE = false
    RELEASE_SITE = true
    DEPLOY_FEATURE = true
  }
  tools {
    jdk 'jdk11'
    maven 'm3'
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '8', artifactNumToKeepStr: '8'))
  }
  stages {
    stage('Tools') {
      steps {
        sh 'java -version'
        sh 'mvn -B --version'
      }
    }
    stage('Test') {
      when {
        not {
          branch 'feature/*'
        }
      }
      steps {
        sh 'mvn -B clean test'
      }
      post {
        always {
          junit '**/surefire-reports/*.xml'
          jacoco(
              execPattern: '**/coverage-reports/*.exec'
          )
        }
      }
    }
    stage('Deploy') {
      when {
        allOf {
          environment name: 'DEPLOY', value: 'true'
          anyOf {
            branch 'develop'
            branch 'master'
          }
        }
      }
      steps {
        sh 'mvn -B -P deploy deploy'
      }
    }
    stage('Snapshot Site') {
      when {
        allOf {
          branch 'develop'
          environment name: 'SNAPSHOT_SITE', value: 'true'
        }
      }
      steps {
        sh 'mvn -B site-deploy'
      }
      post {
        always {
          sh 'curl -s https://codecov.io/bash | bash -s - -t ${CODECOV_TOKEN}'
        }
      }
    }
    stage('Release Site') {
      when {
        allOf {
          branch 'master'
          environment name: 'RELEASE_SITE', value: 'true'
        }
      }
      steps {
        sh 'mvn -B -P gh-pages-site site site:stage scm-publish:publish-scm'
      }
      post {
        always {
          sh 'curl -s https://codecov.io/bash | bash -s - -t ${CODECOV_TOKEN}'
        }
      }
    }
    stage('Deploy Feature') {
      when {
        allOf {
          branch 'feature/*'
          environment name: 'DEPLOY_FEATURE', value: 'true'
        }
      }
      steps {
        sh 'mvn -B -P feature,allow-features clean deploy'
      }
      post {
        always {
          junit '**/surefire-reports/*.xml'
          jacoco(
              execPattern: '**/coverage-reports/*.exec'
          )
        }
      }
    }
  }
}
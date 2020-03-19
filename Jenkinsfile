pipeline {
    agent {label '132'}
    stages {
        stage('preperation') {
            steps {
                sh 'npm install'
                sh 'npm run preperation'
            }
        }
        stage('build server') {
            steps {
                sh 'npm run build_server'
            }
        }
        stage('build web') {
            steps {
                sh 'npm run build_web'
            }
        }
        stage('deploy') {
            steps {
                sh 'npm run deploy'
            }
        }
    }
}

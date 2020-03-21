pipeline {
    agent {label '132'}
    stages {
        stage('preperation') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
                    sh 'target/o2server/stop_linux.sh'
                }
                sh 'npm install'
                sh 'npm run preperation:linux'
                sh 'npm run clear'
            }
        }
        stage('build') {
            parallel {
                stage('build server') {
                    steps {
                        sh 'id'
                        sh 'npm run build_server'
                    }
                }
                stage('build web') {
                    steps {
                        sh 'npm run build_web'
                    }
                }
            }
        }
        stage('deploy') {
            steps {
                sh 'npm run deploy:linux'
                sh 'chmod 777 target/o2server/*.sh'
            }
        }
        stage('run') {
            steps {
                sh 'JENKINS_NODE_COOKIE=dontKillMe nohup target/o2server/start_linux.sh > nohup.out &'
            }
        }
    }
}

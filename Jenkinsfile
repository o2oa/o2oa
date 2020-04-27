pipeline {
    agent {label '132'}
    stages {
        stage('Stop Server') {
            steps {
                catchError(buildResult: 'SUCCESS') {
                    sh 'target/o2server/stop_linux.sh'
                }
            }
        }
        stage('init') {
            steps {
                sh 'npm install'
                sh 'npm run clear_deploy'
            }
        }
        stage('dependency') {
            steps {
                sh 'npm run preperation:linux'
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
                sh 'chmod 777 -R target/o2server/jvm'
                sh 'chmod 777 -R target/o2server/commons'
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

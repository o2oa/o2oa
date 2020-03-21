pipeline {
    agent {label '132'}
    stages {
        stage('preperation') {
            steps {
                catchError {
                    sh 'target/o2server/stop_linux.sh'
                }
                sh 'npm install'
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
            parallel {
                stage('deploy sotre') {
                    steps {
                        sh 'npm run deploy:sotre'
                    }
                }
                stage('deploy commons') {
                    steps {
                        sh 'npm run deploy:commons'
                    }
                }
                stage('deploy jvm') {
                    steps {
                        sh 'npm run deploy:jvm'
                    }
                }
                stage('deploy config') {
                    steps {
                        sh 'npm run deploy:config'
                    }
                }
                stage('deploy local') {
                    steps {
                        sh 'npm run deploy:local'
                    }
                }
                stage('deploy script') {
                    steps {
                        sh 'deploy_script:linux'
                        sh 'chmod 777 target/o2server/*.sh'
                    }
                }
            }
        }
        stage('run') {
            steps {
                sh 'JENKINS_NODE_COOKIE=dontKillMe nohup target/o2server/start_linux.sh > nohup.out &'
            }
        }
    }
}

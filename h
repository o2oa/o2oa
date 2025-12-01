pipeline {
    // agent {label 'o2oaBuild9430'}
    agent {label 'node_build_jvm11'}
    stages {
        stage('ClearBuild'){
            steps {
                sh "rm -rf *"
            }
        }
        // stage('GetConfig'){
        //     steps {
        //         sh "cp ../gulpconfig.js ./"
        //     }
        // }
		stage('Git') {
            steps {
                //git branch: 'master', changelog: false, credentialsId: '331a42ab-5096-48bf-a568-fadeb71325a0', url: 'https://g.o2oa.net/o2oa/o2oa.git'
                //git changelog: false, credentialsId: '331a42ab-5096-48bf-a568-fadeb71325a0', url: 'https://g.o2oa.net/o2oa/o2oa.git'
                git changelog: false, credentialsId: '331a42ab-5096-48bf-a568-fadeb71325a0', url: 'https://g.o2oa.net/o2oa/o2oa.git'
            }
        }
		stage('Init') {
		    parallel {
		        stage('npm install') {
                    steps {
                        sh 'npm cache clean --force'
                        sh 'npm install'
                    }
                }
                stage('get env') {
                    steps {
        				sh  "cat /dev/null > gitTag.txt "
                        sh  "git describe --tags  >> gitTag.txt"
                        script {
                    	    json_file = "gitTag.txt"
        	                file_contents = readFile json_file
        	                if(file_contents.trim().indexOf("-")!=-1&&env.isallTag.trim().indexOf("true")==-1){
        						env.tag =file_contents.trim().substring(0,file_contents.trim().indexOf("-"))
        					}else{
        						env.tag =file_contents.trim();
        					}
                        }
                    }
                }
		    }
        }

        stage('Build') {
            parallel {
                stage('Dependency') {
                    steps {
                        sh 'npm run preperation'
                    }
                }
                stage('build server') {
                    steps {
                        sh 'id'
                        sh 'npm run build_server_deploy'
                    }
                }

                stage('build web') {
                    steps {
                        sh 'npm run build_web'
                    }
                }
            }
        }
        stage('Deploy') {
            parallel {
                stage('deploy api') {
                    steps {
                        sh 'npm run build_api'
                    }
                }
                stage('deploy server') {
                    steps {
                        sh 'npm run deploy'
                        sh 'chmod 777 -R target/o2server/jvm'
                        sh 'chmod 777 -R target/o2server/commons'
                        sh 'chmod 777 target/o2server/*.sh'
                    }
                }
            }

        }
        stage('Pack') {
            steps {
                sh 'ant  -DVERSION='+env.tag+'   -DpreName='+env.preName
            }
        }
        stage('DownloadJson') {
            steps {
                sh 'npm run build_historyJson -- --w https://www.o2oa.net'
            }
        }
        stage('Upload') {
            parallel {
                stage('deploy mirror') {
                    steps {
                        sshPublisher(publishers: [sshPublisherDesc(configName: ''+env.sshServer+'', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/o2server/servers/webServer/download', remoteDirectorySDF: false, removePrefix: '', sourceFiles: ''+env.preName+'-'+env.tag+'*.zip')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                    }
                }
                stage('deploy mirror json') {
                    steps {
                        sshPublisher(publishers: [sshPublisherDesc(configName: ''+env.sshServer+'', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/o2server/servers/webServer/download', remoteDirectorySDF: false, removePrefix: '', sourceFiles: 'download-history.json')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                    }
                }
                stage('deploy download zip') {
                    steps {
                        sshPublisher(publishers: [sshPublisherDesc(configName: ''+env.downloadServer+'', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/o2oadownload/download', remoteDirectorySDF: false, removePrefix: '', sourceFiles: ''+env.preName+'-'+env.tag+'*.zip')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                    }
                }
                stage('deploy history json') {
                    steps {
                        sshPublisher(publishers: [sshPublisherDesc(configName: ''+env.webServer+'', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/www.o2oa.net/website', remoteDirectorySDF: false, removePrefix: '', sourceFiles: 'history.json')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                    }
                }
                
            }
        }
        stage('Docker adn CDN') {
            parallel {
                stage('Push To Docker') {
                    steps {
                        echo 'cd /data/docker && ./build.sh /data/o2oa/'+env.preName+'-'+env.tag+'-linux-x64.zip '+env.tag+' > output.log'
                        sshPublisher(publishers: [sshPublisherDesc(configName: '172.16.94.2', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '', execTimeout: 5400000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/o2oa', remoteDirectorySDF: false, removePrefix: '', sourceFiles: ''+env.preName+'-'+env.tag+'-linux-x64.zip'), sshTransfer(cleanRemote: false, excludes: '', execCommand: 'cd /data/docker && ./build.sh /data/o2oa/'+env.preName+'-'+env.tag+'-linux-x64.zip '+env.tag+' > output.log', execTimeout: 5400000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                    }
                }
                stage('Refresh CDN') {
                    steps {
                        sh 'npm install @o2oa/build-tools'
                        sh './node_modules/.bin/o2-build cdn -t 47LRNDSZZ7LZB6ODEBIG,EtVna2WWxDc8Os1Enrwmkz4hqMfamllE8ZCX8FJs,0a06a0b6290025bd0ff6c014e1379640'
                    }
                }
            }
        }
        stage('Notice') {
            steps{
                script {
                    env.messageNotice="%e5%ae%8c%e6%88%90ssh%e6%9c%8d%e5%8a%a1%e5%99%a8%3d"+env.sshServer+",%e5%89%8d%e7%bc%80%3d"+env.preName+",tag%e7%89%88%e6%9c%ac%3d"+env.tag+"%e7%9a%84%e7%89%88%e6%9c%ac%e6%89%93%e5%8c%85%ef%bc%8c%e8%af%b7%e6%a0%b8%e5%ae%9e"+env.buildResult
                }
                echo 'env.messageNotice='+env.messageNotice
                httpRequest httpMode: 'POST', requestBody: 'fromqq=3256956076&togroup='+env.qqGroupid+'&text='+env.messageNotice, responseHandle: 'NONE', url:  env.qqUrl+'/sendgroupmsg', wrapAsMultipart: false
            }
		}
	}
}

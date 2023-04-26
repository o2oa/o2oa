import {lp} from '@o2oa/component';
function getMenuJson(){
    return [
        {
            'title': lp.baseConfig,
            "icon": "config",
            "expand": true,
            'children': [
                {'title': lp.systemInfo, 'component': 'BaseSystemInfo', 'icon': 'config', "expand": true},
                {'title': lp.uiConfig, 'component': 'BaseUIConfig', 'icon': 'ui'},
                // {'title': lp.componentDeploy, 'component': 'ComponentDeploy', 'icon': 'component'},
                {'title': lp.resourceDeploy, 'component': 'BaseResourceDeploy', 'icon': 'upload' }
                // {'title': lp.resourceDeploy, 'component': 'WebDeploy', 'icon': 'upload' },
                // {'title': lp.serviceDeploy, 'component': 'ServiceDeploy', 'icon': 'deploy' }
            ]
        },
        {
            'title': lp.serverConfig,
            "icon": "servers",
            'children': [
                {'title': lp.serversConfig, 'component': 'ServerServersConfig', 'icon': 'service' },
                // {'title': lp.centerServer, 'component': '', 'icon': 'center' },
                // {'title': lp.appServer, 'component': '', 'icon': 'service' },
                // {'title': lp.webServer, 'component': '', 'icon': 'web' },
                {'title': lp.databaseServer, 'component': 'ServerDatabaseConfig', 'icon': 'database' },
                {'title': lp.storageServer, 'component': 'ServerStorageConfig', 'icon': 'download' },
                {'title': lp.cloudConfig, 'component': 'ServerCloudConfig', 'icon': 'cloud' },
                {'title': lp.processConfig, 'component': 'ServerProcessConfig', 'icon': 'flow' },

                {'title': lp.messageConfig, 'component': 'ServerMessageConfig', 'icon': 'message' },
                {'title': lp.queryIndexConfig, 'component': 'ServerQueryConfig', 'icon': 'search' },
                {'title': lp.cacheConfig, 'component': 'ServerCacheConfig', 'icon': 'cache' },
                // {'title': lp.clusterConfig, 'component': '', 'icon': 'servers' },
                // {'title': lp.orgConfig, 'component': '', 'icon': 'org' },


                // {'title': lp.dumpConfig, 'component': '', 'icon': 'dump' },
                {'title': lp.worktimeConfig, 'component': 'ServerWorktimeConfig', 'icon': 'timer' }
            ]
        },
        {
            'title': lp.securityConfig,
            "icon": "security",
            'children': [
                {'title': lp.passwordConfig, 'component': 'SecurityPasswordConfig', 'icon': 'password' },
                {'title': lp.loginConfig, 'component': 'SecurityLoginConfig', 'icon': 'signin' },
                {'title': lp.ssoConfig, 'component': 'SecuritySSOConfig', 'icon': 'sso' },
                {'title': lp.ternaryManagement, 'component': 'SecurityTernaryConfig', 'icon': 'log' }
            ]
        },
        // {
        //     'title': lp.messageConfig,
        //     "icon": "message",
        //     'children': [
        //         {'title': lp.msgTypeConfig, 'component': '', 'icon': 'message' },
        //
        //         {'title': lp.mailConfig, 'component': '', 'icon': 'mail' },
        //         {'title': lp.smsConfig, 'component': '', 'icon': 'sms' },
        //         {'title': lp.mqConfig, 'component': '', 'icon': 'mq' }
        //     ]
        // },
        {
            'title': lp.mobileConfig,
            "icon": "mobile",
            'children': [
                {'title': lp.appConfig, 'component': 'MobileAppConfig', 'icon': 'connect' },
                {'title': lp.pushConfig, 'component': 'MobilePushConfig', 'icon': 'push' },
                // {'title': lp.moduleConfig, 'component': '', 'icon': 'apps' },
                // {'title': lp.iconConfig, 'component': '', 'icon': 'icon' },
                {'title': lp.integrationConfig, 'component': 'MobileIntegrationConfig', 'icon': 'dingding' },
                // {'title': lp.wechatConfig, 'component': '', 'icon': 'wechat' },
                // {'title': lp.welinkConfig, 'component': '', 'icon': 'welink' },
                {'title': lp.appTools, 'component': 'MobileToolsConfig', 'icon': 'tools' }
            ]
        }
    ];
}

export {getMenuJson};

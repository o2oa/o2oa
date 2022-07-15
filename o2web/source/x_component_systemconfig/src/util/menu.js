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
                {'title': lp.resourceDeploy, 'component': 'BaseResourceDeploy', 'icon': 'upload' },
                // {'title': lp.resourceDeploy, 'component': 'WebDeploy', 'icon': 'upload' },
                // {'title': lp.serviceDeploy, 'component': 'ServiceDeploy', 'icon': 'deploy' }
            ]
        },
        {
            'title': lp.securityConfig,
            "icon": "security",
            'children': [
                {'title': lp.passwordConfig, 'component': 'SecurityPasswordConfig', 'icon': 'password' },
                {'title': lp.loginConfig, 'component': '', 'icon': 'signin' },
                {'title': lp.ssoConfig, 'component': '', 'icon': 'sso' },
                {'title': lp.logConfig, 'component': '', 'icon': 'log' }
            ]
        },
        {
            'title': lp.serverConfig,
            "icon": "servers",
            'children': [
                {'title': lp.centerServer, 'component': '', 'icon': 'center' },
                {'title': lp.appServer, 'component': '', 'icon': 'service' },
                {'title': lp.webServer, 'component': '', 'icon': 'web' },
                {'title': lp.databaseServer, 'component': '', 'icon': 'database' },
                {'title': lp.storageServer, 'component': '', 'icon': 'download' },
                {'title': lp.cacheConfig, 'component': '', 'icon': 'cache' },
                {'title': lp.clusterConfig, 'component': '', 'icon': 'servers' },
                {'title': lp.orgConfig, 'component': '', 'icon': 'org' },
                {'title': lp.processConfig, 'component': '', 'icon': 'flow' },
                {'title': lp.cloudConfig, 'component': '', 'icon': 'cloud' },
                {'title': lp.dumpConfig, 'component': '', 'icon': 'dump' }
            ]
        },
        {
            'title': lp.messageConfig,
            "icon": "message",
            'children': [
                {'title': lp.msgTypeConfig, 'component': '', 'icon': 'message' },
                {'title': lp.pushConfig, 'component': '', 'icon': 'push' },
                {'title': lp.mailConfig, 'component': '', 'icon': 'mail' },
                {'title': lp.smsConfig, 'component': '', 'icon': 'sms' },
                {'title': lp.mqConfig, 'component': '', 'icon': 'mq' }
            ]
        },
        {
            'title': lp.mobileConfig,
            "icon": "mobile",
            'children': [
                {'title': lp.connectConfig, 'component': '', 'icon': 'connect' },
                {'title': lp.moduleConfig, 'component': '', 'icon': 'apps' },
                {'title': lp.iconConfig, 'component': '', 'icon': 'icon' },
                {'title': lp.ddConfig, 'component': '', 'icon': 'dingding' },
                {'title': lp.wechatConfig, 'component': '', 'icon': 'wechat' },
                {'title': lp.welinkConfig, 'component': '', 'icon': 'welink' },
                {'title': lp.appTools, 'component': '', 'icon': 'tools' }
            ]
        }
    ];
}

export {getMenuJson};

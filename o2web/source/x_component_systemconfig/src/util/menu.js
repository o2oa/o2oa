// jshint esversion: 6
import {lp} from '@o2oa/component';
function getMenuJson(){
    return [{
        'title': lp.baseConfig,
        'children': [
            {'title': lp.systemInfor, 'component': '', 'icon': 'config'},
            {'title': lp.uiConfig, 'component': '', 'icon': 'ui'},
            {'title': lp.componentDeploy,
            'component': '',
            'icon': 'component'
        }, {
            'title': lp.resourceDeploy,
            'component': '',
            'icon': 'webres'
        }, {
            'title': lp.serviceDeploy,
            'component': '',
            'icon': 'service'
        }]
    },{
        'title': lp.securityConfig,
        'children': [{
            'title': lp.systemInfor,
            'component': '',
            'icon': 'config'
        }, {
            'title': lp.uiConfig,
            'component': '',
            'icon': 'ui'
        }, {
            'title': lp.componentDeploy,
            'component': '',
            'icon': 'component'
        }, {
            'title': lp.resourceDeploy,
            'component': '',
            'icon': 'webres'
        }, {
            'title': lp.serviceDeploy,
            'component': '',
            'icon': 'service'
        }]
    }];
}

export {getMenuJson};
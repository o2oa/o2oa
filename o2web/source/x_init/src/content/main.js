import {component} from '@o2oa/oovm';
import {dom} from '@o2oa/util';
import template from './main.html?raw';
import password from './init/password.js';
import database from './init/database.js';
import restore from './init/restore.js';
import initInfo from './init/initInfo.js';
import execute from './init/execute.js';

import explain from './init/common/explain.js'
import {serverStatus} from "../common/action.js";
export default component({
    template,
    autoUpdate: true,
    components: {password, explain, database, restore, initInfo, execute},

    async bind() {
        const status = await serverStatus();
        const step = (status.status === 'waiting') ? 0 : 4;
        return {
            explain: [
                {
                    icon: 'password',
                    title: '设置管理员密码',
                    textList: ['首次启动服务器，您必须为超级管理员（xadmin）设置一个密码。', '密码长度必须6位以上，同时包含数字和字母。', '请牢记此密码!']
                },
                {
                    icon: 'database',
                    title: '设置数据库',
                    textList: [
                        'O2OA（翱途）平台内置H2数据库，它是一个内嵌式的内存数据库，适合用于开发环境、功能演示环境，并不适合用作正式环境。',
                        '如果作为正式环境使用，建议您使用拥有更高性能并且更加稳定的商用级别数据库。如Mysql8, Oracle12C, SQLServer 2012等',
                        '您可以在此初始化服务器页面选择使用内置H2数据库，或外部数据库。',
                        '更多数据库配置选项可在服务器初始化后，进入“系统配置”应用进行设置'
                    ]
                },
                {
                    icon: 'import',
                    title: '初始化数据',
                    textList: [
                        '您可以将从其它服务器导出的数据包，在此页面中导入，以便于快速恢复或搭建应用。',
                        '在您已有服务器进行导出操作（ctl -dd 命令，或在“系统配置”的“数据库配置”中操作），会在服务器目录“o2server/local/dump”下得到“dumpData_时间”的文件夹，将其打包为zip文件后，可在服务器初始化时导入所有数据',
                        '关于数据的导出可查看：',
                        '<a href="https://www.o2oa.net/cms/serverdeployment/256.html" style="color: #ffffff; text-indent: 0em; display: block;" target="_blank">《数据导出导入与系统数据备份》</a>',
                        '<a href="https://www.o2oa.net/cms/videoproduct/455.html" style="color: #ffffff; text-indent: 0em; display: block;" target="_blank">《系统配置-服务配置-数据库配置》</a>'
                    ]
                },
                {
                    icon: 'import',
                    title: '初始化信息',
                    textList: [
                        '您已经准备好了服务器初始化配置，请确认您的初始化信息。',
                        '点击“执行”按钮，执行服务器初始化，完成后服务器会自动启动。',
                        '点击“取消”按钮，取消服务器初始化，并关闭初始化服务器，您可以再次手工启动服务器，以完成初始化配置。'
                    ]
                },
                {
                    icon: 'reload',
                    title: '执行初始化',
                    textList: [
                        '服务器正在执行初始化任务，执行完成后会自动启动服务器。',
                    ]
                },
                {
                    icon: 'cancel',
                    title: '取消初始化',
                    textList: [
                        '取消服务器初始化，服务器已关闭，您可以再次手工启动服务器，以完成初始化配置',
                    ]
                }
            ],
            step,
            serverStop: false
        }
    },

    afterRender(){
        window.setTimeout(()=>{
            dom.setStyles(this.titleNode, {
                top: 0,
                opacity: 1
            });
        }, 600);
        window.setTimeout(()=>{
            dom.setStyles(this.paneNode, {
                opacity: 1
            });
            dom.setStyles(this.bottomNode, {
                opacity: 1
            });
        }, 1100);

    }
});

var o2DesignerConfig = [
    {
        app: 'portal.PortalExplorer',
        name: '门户管理',
        listAction: ()=>{ return o2.Actions.load('x_portal_assemble_surface').PortalAction.list() },
        icon: 'O2',
        children: [{
            app: 'portal.PortalManager',
            status: {navi:0,application:'{id}'},
            listAction: (obj)=>{ return o2.Actions.load('x_portal_assemble_surface').PageAction.list(obj.id) },
            name: '页面配置',
            icon: 'O2',
            category: '{category}',
            children: [{
                app: 'portal.PageDesigner',
                status: {id:'{id}'},
                name: '{name}',
                listAction: ()=>{ return o2.Actions.load('x_portal_assemble_surface').PortalAction.list() },
                icon: 'O2',
            }]
        }]
    },
    {
        app: 'process.ApplicationExplorer',
        name: '流程管理',
        listAction: ()=>{ return o2.Actions.load('x_portal_assemble_surface').PortalAction.list() },
        icon: 'O2'
    },
    {
        app: 'cms.Column',
        name: '内容管理',
        listAction: ()=>{ return o2.Actions.load('x_portal_assemble_surface').PortalAction.list() },
        icon: 'O2'
    },
    {
        app: 'query.QueryExplorer',
        name: '数据中心',
        listAction: ()=>{ return o2.Actions.load('x_portal_assemble_surface').PortalAction.list() },
        icon: 'O2'
    },
    {
        app: 'service.ServiceManager',
        name: '服务管理',
        listAction: ()=>{ return o2.Actions.load('x_portal_assemble_surface').PortalAction.list() },
        icon: 'O2'
    }
];
var o2DesignerBreadcrumb = new Class({

})
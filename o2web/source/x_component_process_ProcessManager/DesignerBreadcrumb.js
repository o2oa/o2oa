var _openApp = o2.api.page.openApplication;

var _sort =  (data, key='name', isDesc=false)=>{
    return data.sort(function (a, b){
        var av = a[key], bv = b[key];
        if( typeOf(av) === 'string' && typeOf(bv) === 'string' ){
            var isLetterA = /^[a-zA-Z0-9]/.test(av);
            var isLetterB = /^[a-zA-Z0-9]/.test(bv);

            if (isLetterA && !isLetterB) return isDesc ? 1 : -1; // a是字母，b不是，a排在前面
            if (!isLetterA && isLetterB) return isDesc ? -1 : 1;  // a不是字母，b是，b排在前面

            return isDesc ?  bv.localeCompare(av) : av.localeCompare(bv);
        }
        return isDesc ? (bv - av) : (av - bv);
    }.bind(this));
};

var o2DesignerConfig = {
    menus: [
        {
            handleClick: ()=>{ _openApp('portal.PortalManager'); },
            name: '门户管理',
            icon: 'O2',
            menus: ()=>{
                return o2.Actions.load('x_portal_assemble_surface').PortalAction.list().then((json)=>{
                    return json.data.map((item)=>{
                        return {
                            handleClick: ()=>{
                                _openApp('portal.PortalManager', null, {application:item.id});
                            },
                            category: item.category,
                            name: item.name,
                            icon: 'O2',
                            id: item.id,
                            menus: [{
                                name: '页面配置',
                                handleClick: ()=>{
                                    _openApp('portal.PortalManager', null, {navi:0,application:item.id});
                                },
                                menus: (item)=> {
                                    return o2.Actions.load('x_portal_assemble_surface').PageAction.list(item.id).then((pages)=>{
                                        return _sort( pages.data ).map((page)=>{
                                            return {
                                                handleClick: ()=>{
                                                    _openApp('portal.PageDesigner', null, {id: page.id});
                                                },
                                                name: page.name,
                                                icon: 'O2',
                                                id: page.id
                                            };
                                        });
                                    })
                                }
                            },{
                                name: '部件配置',
                                handleClick: ()=>{
                                    _openApp('portal.PortalManager', null, {navi:1,application:item.id});
                                },
                                menus: (item)=> {
                                    return o2.Actions.load('x_portal_assemble_surface').WidgetAction.list(item.id).then((widgets)=>{
                                        return _sort( widgets.data ).map((widget)=>{
                                            return {
                                                handleClick: ()=>{
                                                    _openApp('portal.WidgetDesigner', null, {id: widget.id});
                                                },
                                                name: widget.name,
                                                icon: 'O2',
                                                id: widget.id
                                            };
                                        });
                                    })
                                }
                            },{
                                name: '数据字典',
                            },{
                                name: '脚本配置',
                            },{
                                name: '资源文件',
                            },{
                                name: '门户属性',
                            }]
                        };
                    });
                })
            }
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
    ]
};

var o2DesignerBreadcrumb = new Class({
    Extends: o2.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "pathlist": ''
    },
    initialize: function(container, options){
        this.setOptions(options);
        this.path = `../x_component_process_ProcessManager/$DesignerBreadcrumb/${this.options.style}/`;
        this.container = $(container);
        this.items = [];
    },
    load: function (){
        this.node = new Element('div.breadcrumb').inject(this.container);
        this.node.loadCss(`${this.path}style.css`);
        this.addItem(o2DesignerConfig);
    },
    addItem: function(data){
        var item = new o2DesignerBreadcrumb.Item(this, this.items.getLast() || null, data);
        this.items.push(item);
        item.load();
        this.setCurrentItem(item);
        return item;
    },
    removeItem: function(item){
        this.items.erase(item);
        item.destroy();
    },
    toItem: function (item){
        var index = this.items.indexOf(item);
        if( index > -1 ){
            while( this.items.length > index+1 ){
                this.removeItem(this.items[this.items.length-1]);
            }
        }
        this.setCurrentItem(item);
    },
    setCurrentItem: function (item) {
        if( this.currentItem ){
            this.currentItem.cancelCurrent();
        }
        item.setCurrent();
        this.currentItem = item;
    },
    back: function (){
        if( this.currentItem ){
            var index = this.items.indexOf(this.currentItem);
            if( index > 0 ){
                this.toItem( this.items[index-1] );
                return true;
            }
        }
        return false;
    }
});

o2DesignerBreadcrumb.Item = new Class({
    initialize: function(breadcrumb, parent, data){
        this.breadcrumb = breadcrumb;
        this.app = breadcrumb.app;
        this.parent = parent;
        this.data = data;
        this.level = parent ? (parent.level + 1) : 1;
    },
    load: function (){
        if( this.level > 1 ){
            this.separator = new Element('div', {
                text: '>'
            }).inject(this.breadcrumb.node);
        }
        this.node = new Element('div.breadcrumb-item', {
            text: this.isRoot ?  '顶层' : this.category._getShowName()
        }).inject(this.breadcrumb.node);
        this.node.addEvent('click', function (ev){
            this.breadcrumb.toItem(this);
        }.bind(this));
    },
    loadTooltip: function (){
        const { app, node, data } = this;
        this.tooltip = new o2DesignerBreadcrumb.Menu(app.content, node, app, data, {
            axis : "y",
            hiddenDelay : 300,
            displayDelay : 300
        });
    },
    setCurrent: function (){
        this.node.addClass('current').addClass('mainColor_color');
        this.switchCategory(true);
    },
    cancelCurrent: function (){
        this.node.removeClass('current').removeClass('mainColor_color');
        this.switchCategory(false);
    },
    destroy: function () {
        this.separator && this.separator.destroy();
        this.node.destroy();
        this.switchCategory(false);
    }
});

o2.xDesktop.requireApp('Template', 'MTooltips', null, false);
o2DesignerBreadcrumb.Menu = new Class({
    Extends: MTooltips,
    Implements: [Options, Events],
    //执行后才显示位置也样式
    _loadCustom : function( callback ){
        this.contentNode.loadHtml(this.breadcrumb.path+"/menu.html", {
             "bind": {"lp": this.lp, "data": this.data}, "module": this},
            function(){
                if(callback)callback();
            }.bind(this)
        );
    },
    _customNode : function( node, contentNode ){
        this.fireEvent("customContent", [contentNode, node])
    },
});

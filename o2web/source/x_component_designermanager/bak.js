o2.require("o2.widget.PinYin", null, false);
o2.xDesktop.requireApp('Template', 'MTooltips', null, false);

var o2DesignerBreadcrumb = new Class({
    Extends: o2.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default", //样式
        "keepActive": true, //是否保持上次的激活状态
        "pathlist": [] //路径列表 [{id, name, _type}]
    },
    initialize: function(container, app, options){
        this.setOptions(options);
        this.app = app;
        this.path = `../x_component_process_ProcessManager/$DesignerBreadcrumb/${this.options.style}/`;
        this.container = $(container);
        this.items = [];
    },
    load: function (){
        this.container.loadCss(`${this.path}style.css`);
        this.node = new Element('div.breadcrumb').inject(this.container);

        var p;
        var componentNames = [];
        this.options.pathlist.each((pathData, i)=>{
            componentNames.push(pathData.componentName);
            if( pathData._type === 'app' ){
                if( !pathData.hasOwnProperty('name') || !pathData.hasOwnProperty('alias') ){
                    var config = this.getConfig( componentNames );
                    p = config.getAction( pathData.id ).then((json)=>{
                        pathData.name = json.data.name;
                        pathData.alias = json.data.alias || '';
                        pathData._appType = config._appType;
                    });
                }
            }
        });

        Promise.resolve(p).then(() => {
            this.options.pathlist.each((pathData, i)=>{
                this.addItem(pathData, i);
            });
            this.app.content.addEvent('mousedown', (e)=>{
                if( this.activeMenu ){
                    this.activeMenu.hide();
                    this.activeMenu.target.removeClass('active');
                    this.activeMenu = null;
                }
            });
            this.node.addEvent('mousedown', (e)=>{ e.stopPropagation(); });
            this.addToHistory();
        });
    },
    getConfig: function( componentNamePaths){
        var cfg = o2DB._config;
        for( var i=0; i < componentNamePaths.length; i++ ){
            if( !cfg  )return null;
            var arr = cfg.children && cfg.children.filter((item)=>{
                return item.componentName === componentNamePaths[i];
            });
            cfg = arr && arr.length ? arr[0] : null;
        }
        return cfg;
    },
    addItem: function(pathData){
        var item = new o2DB.Item(this, this.items.getLast() || null, pathData);
        this.items.push(item);
        item.load();
        return item;
    },
    addToHistory: function(){
        var pathlist = this.options.pathlist;
        var path = pathlist.getLast();
        var appData = pathlist.length === 4 ? pathlist[1] : pathlist[0];
        if( path.id ){
            o2.UD.getDataJson(o2DB._HISTORY_DESIGNER_NAME).then((items) => {
                var list = o2DB._sort(items || [], 'time', true).filter((item) => {
                    return item.id !== path.id;
                });
                list.unshift({
                    isHistory: true,
                    applicationName: appData.name,
                    _appType: appData._appType,
                    _type: path._type || 'designer',
                    appData: appData,
                    componentName: this.app.options.name,
                    id: path.id,
                    name: path.name,
                    alias: path.alias || '',
                    time: new Date().getTime(),
                    timeString: new Date().format('db')
                });
                (list.length > o2DB._HISTORY_DESIGNER_MAX_COUNT) && (list.length = o2DB._HISTORY_DESIGNER_MAX_COUNT);
                o2.UD.putData(o2DB._HISTORY_DESIGNER_NAME, list);
            });
        }
    }
});
var o2DB = o2DesignerBreadcrumb;

o2DB.Item = new Class({
    initialize: function(breadcrumb, parent, pathData, config){
        this.breadcrumb = breadcrumb;
        this.app = breadcrumb.app;
        this.parent = parent;
        this.pathData = pathData;
        this.data = pathData;
        this.level = parent ? (parent.level + 1) : 1;
        this.siblingConfigs = this.level === 1 ? o2DB._config.children : this.parent.config.children;
        this.config = this.siblingConfigs.length === 1 ? this.siblingConfigs[0] : this.siblingConfigs.find((item)=>{
            return item.componentName === pathData.componentName ||
                (pathData.id && item.id === pathData.id) ||
                (pathData.name && item.name === pathData.name);
        });
    },
    getPaths: function(){
        var paths = [], parent = this;
        while (parent){
            paths.unshift(parent.pathData.componentName || parent.pathData.id);
            parent = parent.parent;
        }
        return paths;
    },
    load: function (){
        if( this.level > 1 ){
            this.separator = new Element('div.breadcrumb-separator', {
                text: '>'
            }).inject(this.breadcrumb.node);
        }
        this.node = new Element('div.breadcrumb-item', {
            text: (this.config.label ? this.config.label+'：' : '') + this.pathData.name
        }).inject(this.breadcrumb.node);

        this.loadMenu();
    },
    loadMenu: function (){
        const { app, node, data } = this;
        var _self = this;
        this.menu = new o2DB.Menu(app.content, node, app, data, {
            onPostCreate: function (){ _self.setActiveMenu(this); },
            onShow: function (){ _self.setActiveMenu(this); },
            onHide: function (){ _self.cancelActiveMenu(this); },
            onPostSetCoondinates: function (){ _self.setActiveMenuCoondinates(this); }
        });
        this.menu.parent = this;
        this.menu.level = this.level;
        this.menu.item = this;
    },
    setActiveMenu: function ( submenu ){
        var bc = this.breadcrumb;
        if( bc.activeMenu ){
            // if(bc.options.keepActive){
            //     this.lastActiveMenus = [];
            //     var activeMenu = this.activeMenu;
            //     while(activeMenu){
            //         this.lastActiveMenus.push(activeMenu);
            //         activeMenu = activeMenu.activeMenu;
            //     }
            // }
            bc.activeMenu.hide();
            bc.activeMenu.target.removeClass('active');
            bc.activeMenu = null;
        }
        bc.activeMenu = submenu;
        this.node.addClass('active');
    },
    cancelActiveMenu: function ( submenu ){
        if( submenu.activeMenu ){
            submenu.activeMenu.hide();
        }
    },
    setActiveMenuCoondinates: function (submenu){
        if( submenu.activeMenu ){
            submenu.activeMenu.setCoondinates();
        }
    },
    destroy: function () {
        this.separator && this.separator.destroy();
        this.node.destroy();
        this.switchCategory(false);
    }
});

o2DB.Menu = new Class({
    Extends: MTooltips,
    Implements: [Options, Events],
    options: {
        axis : "y",
        hiddenDelay : 300,
        displayDelay : 300,
        offset : {
            x : 0,
            y : 4
        },
        priorityOfAuto: {
            x : ["right", "center", "left" ], //当position x 为 auto 时候的优先级
            y : ["bottom", "middle", "top" ] //当position y 为 auto 时候的优先级
        },
        hasArrow: false,
        isAutoHide: false,
        overflow: 'scroll',
        // hideByClickBody : true,
        nodeStyles: {
            "max-height":  "80%",
            "display": "flex",
            "position" : "absolute",
            "max-width" : "500px",
            "min-width" : "100px",
            "z-index" : "101",
            "border-color" : "var(--oo-color-gray-d)",
            "background-color" : "#fff",
            "padding" : "5px 0px",
            "border-radius" : "var(--oo-default-radius)",
            "box-shadow": "0 0 10px 1px var(--oo-color-gray-d)",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        }
    },
    //执行后才显示位置也样式
    _loadCustom : function( callback, refresh ){
        this.menus = [];
        if( !refresh ){
            this.contentNode.addEvent('mousedown', (e)=>{ e.stopPropagation(); });
            this.contentNode.loadCss(`${this.item.breadcrumb.path}style.css`);
            this.contentNode.setStyles({'height': 'auto'});
        }

        var template = Array.clone(this.getChildrenTempalte());
        // var creatable = !!(template.length === 1 && template[0].createFunction);
        Promise.resolve(this.getList( template )).then((data)=>{
            this.parseCategory();
            this.contentNode.loadHtml(
                this.item.breadcrumb.path+"menu.html",
                {
                    bind: {
                        lp: this.lp,
                        data: data,
                        _type: template[0]._type,
                        editingAppid: this.getPathAppid(),
                        pathData: this.getPathData(),
                        categories: this.categories
                    },
                    module: this
                },
                function(){
                    if(callback)callback();
                }.bind(this)
            );
        });
    },
    reload: function (){
        this.refresh(true);
    },
    getPathAppid : function(){
        var pathlist = this.item.breadcrumb.options.pathlist;
        return pathlist.length === 4 ? pathlist[1].id : pathlist[0].id;
    },
    getPathData: function(){
        return this.item.breadcrumb.options.pathlist[this.level-1] || {};
    },
    // getParentData: function (type){
    //     var parent = this.item.parent;
    //     while(parent){
    //         if( parent.pathData && parent.pathData._type === type ){
    //             return parent.pathData;
    //         }
    //         parent = parent.parent;
    //     }
    // },
    getParentData: function (_type) {
        var parent = this;
        while(parent){
            if( parent.data && parent.data._type === _type ){
                return parent.data;
            }
            parent = parent.parent;
        }
    },
    getDesignerData : function(){
        return this.getParentData('designer');
    },
    getAppdata: function(){
        return this.getParentData('app');
    },
    getAppid: function(){
        return this.getAppdata()?.id;
    },
    getAppname: function(){
        return this.getAppdata()?.name;
    },
    getCurrentAppid: function (){
        return this.getAppid();
    },
    getCurrentAppname: function (){
        return this.getAppname();
    },
    getChildrenTempalte: function () {
        return this.item.siblingConfigs;
    },
    getList: function ( template ) {
        var list = template;
        this.categories = [];
        var appid = this.getAppid();
        if( list.length === 1 && list[0].listAction){
            return list[0].listAction( appid ).then((data)=>{
                return data.map(d=>{
                    switch ( list[0]._type ){
                        case 'app':
                            d.children = list[0].children.map((child)=>{
                                child.appid = d.id;
                                return child;
                            });
                            d.children.push({_type: 'separator'});
                            d.children.push(...o2DB._appTools);
                            break;
                        case 'designer':
                            d.children = [...o2DB._designerTools];
                            break;
                    }
                    d.handleClick = ()=>{
                        list[0].handleClick(d, appid);
                    };
                    if( list[0].categorized ){
                        (!d.category || d.category==='未分类') && (d.category = o2DB._UNCATEGORIZED);
                        !this.categories.includes( d.category ) && this.categories.push( d.category );
                    }
                    d._type = list[0]._type;
                    d._config = list[0];
                    return d;
                });
            });
        }else{
            return list.map(child=>{
                child.appid = appid;
                return child;
            });
        }
    },
    switchCategoryArea: function (e) {
        if( this.categoryArea.hasClass('hide') ){
            this.categoryArea.setStyles({'width': this.menuNode.getSize().x+'px'})
        }
        o2DB._checkClass(this.categoryArea, 'hide', !this.categoryArea.hasClass('hide'));
        o2DB._checkClass(e.currentTarget, 'active', !this.categoryArea.hasClass('hide'));
        !!this.activeMenu && this.activeMenu.setCoondinates();
    },
    parseCategory: function (e){
        var hasUncategorized = this.categories.includes(o2DB._UNCATEGORIZED);
        hasUncategorized && (this.categories = this.categories.erase( o2DB._UNCATEGORIZED ));
        if( this.categories.length > 0 ){
            this.categories = o2DB._sort(this.categories,'').map(c=>{ return {value: c, label: c}; });
            this.categories.unshift({value: o2DB._ALL, label: '全部分类'});
            hasUncategorized && this.categories.push({value: o2DB._UNCATEGORIZED, label: '未分类'});
        }
    },
    filterByCategory: function (e, data){
        !!this.activeCategoryNode && this.activeCategoryNode.removeClass('active');
        this.activeCategoryNode = e.currentTarget;
        e.currentTarget.addClass('active');

        this.currentCategory = data.value;

        this.search();
    },
    loadSearchInput: function (e) {
        var searchInput = this.searchInput;

        var isComposing = false; // 标记是否处于输入法输入中

        //输入法开始输入事件
        searchInput.addEventListener('compositionstart', () => { isComposing = true; });

        //输入法结束输入事件
        searchInput.addEventListener('compositionend', (e) => {
            isComposing = false;
            this.currentSearchKey = e.currentTarget.value;
            this.search();
        });

        //常规的input事件
        searchInput.addEventListener('input', (e) => {
            this.currentSearchKey = e.currentTarget.value;
            !isComposing && this.search();
        });
    },
    clearSearch: function (){
        this.currentSearchKey = '';
        this.searchInput.setAttribute('value', '');
        this.search();
    },
    search: function() {
        var category = this.currentCategory;
        var key = (this.currentSearchKey||'').toLowerCase();
        var items = this.menuNode && this.menuNode.querySelectorAll('.breadcrumb-menu-item');
        (items || []).forEach(item=>{
            var ds = item.dataset;
            var isMatchKey = !key || (ds.id.includes(key) ||
                ds.name.includes(key) ||
                (ds.alias||'').includes(key) ||
                (ds.pinyin||'').includes(key)
            );
            var isMatchCategory = !category || category === o2DB._ALL || ds.category === category;
            o2DB._checkClass( item, 'hide', !isMatchKey || !isMatchCategory );
        });

        o2DB._checkClass(this.clearSearchNode, 'hide', !key);

        if( !!this.activeMenu ){
            var fun = this.activeMenu.target.offsetParent === null ? 'hide' : 'setCoondinates';
            this.activeMenu[fun]();
        }
    },
    checkCondition: function (e, data){
        if( !data.condition ){
            return true;
        }
        var flag = true;
        switch(data._type){
            case 'app-tool':
                flag = data.condition(data, this.getAppdata());
                break;
            case 'designer-tool':
                flag = data.condition(this.getDesignerData(), this.getAppdata());
                break;
        }
        !flag && e.currentTarget.addClass('hide');
        return flag;
    },
    handleLoadItem: function (e, data){
        if( !this.checkCondition(e, data) )return;
        var app = this.item.breadcrumb.app;
        var _self = this;
        if( data.children && data.children.length > 0 ){
            var menu = new o2DB.SubMenu(app.content, e.currentTarget, app, data, {
                autoRefresh: data.autoRefresh,
                onPostCreate: function (){ _self.setActiveMenu(this); },
                onShow: function (){ _self.setActiveMenu(this); },
                onHide: function (){ _self.cancelActiveMenu(this); },
                onPostSetCoondinates: function (){ _self.setActiveMenuCoondinates(this); }
            });
            menu.level = this.level + 1;
            menu.item = this.item;
            menu.parent = this;
            menu.currentAppid = this.getCurrentAppid();
            menu.currentAppname = this.getCurrentAppname();
            this.menus.push(menu);
        }
    },
    setActiveMenu: function ( submenu ){
        this.activeEl = submenu.target;
        this.activeEl.addClass('active');
        this.activeMenu = submenu;
    },
    cancelActiveMenu: function ( submenu ){
        submenu.target.removeClass('active');
        if( submenu.activeMenu ){
            submenu.activeMenu.hide();
        }
    },
    setActiveMenuCoondinates: function ( submenu ){
        if( submenu.activeMenu ){
            submenu.activeMenu.setCoondinates();
        }
    },
    handleMouseEnter: function (e, data){
        this.timer_hide_acitvie = window.setTimeout(() => {
            if(this.activeMenu ){
                var d = this.activeMenu.data;
                if( (d.id && d.id !== data.id) || (d.componentName && d.componentName !== data.componentName ) ){
                    this.activeMenu.hide();
                }
            }
            this.timer_hide_acitvie = null;
        }, this.options.hiddenDelay);
    },
    handleMouseLeave: function (e, data){
        if(this.timer_hide_acitvie){
            window.clearTimeout(this.timer_hide_acitvie);
            this.timer_hide_acitvie = null;
        }
    },
    handleCreate: function (e, data){
        data.handleCreate(this.getAppid(), this.getAppname());
        e.stopPropagation();
    },
    handleClick: function (e, data){
        data.handleClick(data, this.getAppid());
    },
    _customNode : function( node, contentNode ){
        this.fireEvent("customContent", [contentNode, node])
    }
});

o2DB.SubMenu = new Class({
    Extends: o2DB.Menu,
    Implements: [Options, Events],
    options: {
        axis : "x",
        offset : {
            x : 5,
            y : 0
        },
        priorityOfAuto :{
            x : [ "center", "right", "left" ], //当position x 为 auto 时候的优先级
            y : ["bottom", "middle", "top" ] //当position y 为 auto 时候的优先级
        }
    },
    // getParentData: function (_type) {
    //     var parent = this;
    //     while(parent){
    //         if( parent.data && parent.data._type === _type ){
    //             return parent.data;
    //         }
    //         parent = parent.parent;
    //     }
    // },
    // getDesignerData: function () {
    //     return this.getParentData('designer');
    // },
    // getAppdata: function (){
    //     return this.getParentData('app');
    // },
    getAppid: function(){
        var appdata = this.getAppdata();
        return !!appdata ? appdata.id : this.currentAppid;
    },
    getAppname: function(){
        var appdata = this.getAppdata();
        return !!appdata ? (appdata.name || appdata.appName) : this.currentAppname;
    },
    getChildrenTempalte: function () {
        return this.data.children;
    },
    handleCreate: function (e, data){
        data.handleCreate(data.appid || this.getAppid(), this.getAppname());
        e.stopPropagation();
    },
    handleClick: function (e, data){
        if( data._type === 'designer-tool' ){
            var d = this.getDesignerData();
            data.handleClick(d, d.isHistory ? d.appData : this.getAppdata());
        }else{
            data.handleClick(data, data.appid || this.getAppid(), this.getAppdata());
        }
    },
    getCurrentAppid: function (){
        return this.currentAppid;
    },
    getCurrentAppname: function (){
        return this.currentAppname;
    }
});

o2DB._portalAction = o2.Actions.load('x_portal_assemble_designer');
o2DB._processAction = o2.Actions.load('x_processplatform_assemble_designer');
o2DB._cmsAction = o2.Actions.load('x_cms_assemble_control');
o2DB._queryAction = o2.Actions.load('x_query_assemble_designer');
o2DB._serviceAction = o2.Actions.load('x_program_center');
o2DB._openApp = o2.api.page.openApplication;

o2DB._UNCATEGORIZED = 'uncategorized';
o2DB._ALL = 'all';
o2DB._HISTORY_DESIGNER_NAME = 'HistoryOpenedDesigner';
o2DB._HISTORY_DESIGNER_MAX_COUNT = 20; //最近打开的设计元素数量

o2DB._ooiconMap = {
    'portal.PageDesigner': 'pagepeizhi',
    'portal.WidgetDesigner': 'app-center',
    'portal.DictionaryDesigner': 'js',
    'portal.ScriptDesigner': 'jiaoben',
    'process.FormDesigner': 'biaodan',
    'process.ProcessDesigner': 'a-flowprocess',
    'process.DictionaryDesigner': 'js',
    'process.ScriptDesigner': 'jiaoben',
    'cms.CategoryManager': 'bujianpeizhi',
    'cms.FormDesigner': 'biaodan',
    'cms.DictionaryDesigner': 'js',
    'cms.ScriptDesigner': 'jiaoben',
    'query.ViewDesigner': 'shitupeizhi3',
    'query.StatDesigner': 'shujubiao2',
    'query.TableDesigner': 'shujubiao',
    'query.StatementDesigner': 'chaxunpeizhi',
    'query.ImporterDesigner': 'file_upload',
    'service.AgentDesigner': 'dailipeizhi1',
    'service.InvokeDesigner': 'jiekoupeizhi21',
    'service.ScriptDesigner': 'jiaoben',
    'service.DictionaryDesigner': 'js'
};
o2DB._appNameMap = {
    'portal.PageDesigner': '门户页面',
    'portal.WidgetDesigner': '门户部件',
    'portal.DictionaryDesigner': '门户数据字典',
    'portal.ScriptDesigner': '门户脚本',
    'process.FormDesigner': '流程表单',
    'process.ProcessDesigner': '流程设计',
    'process.DictionaryDesigner': '流程数据字典',
    'process.ScriptDesigner': '流程脚本',
    'cms.CategoryManager': '内容管理分类',
    'cms.FormDesigner': '内容管理表单',
    'cms.DictionaryDesigner': '内容管理数据字典',
    'cms.ScriptDesigner': '内容管理脚本',
    'query.ViewDesigner': '视图配置',
    'query.StatDesigner': '统计配置',
    'query.TableDesigner': '数据表',
    'query.StatementDesigner': '查询配置',
    'query.ImporterDesigner': '导入模型',
    'service.AgentDesigner': '代理',
    'service.InvokeDesigner': '接口',
    'service.ScriptDesigner': '服务中心脚本',
    'service.DictionaryDesigner': '服务中心数据字典'
};

o2DB._checkClass = (dom, clazz, flag)=>{
    !!flag ?
        !dom.hasClass(clazz) && dom.addClass(clazz) :
        dom.hasClass(clazz) && dom.removeClass(clazz);
};

o2DB._toPY = (item)=>{
    return `${item.name}`.toPY().toLowerCase() + '#' +
        `${item.alias||''}`.toPY().toLowerCase() + '#' +
        `${item.name}`.toPYFirst().toLowerCase() + '#' +
        `${item.alias||''}`.toPYFirst().toLowerCase() + '#';
};

o2DB._sort =  (data, key='name', isDesc=false)=>{
    return data.sort(function (a, b){
        var av = !!key ? a[key] : a, bv = !!key ? b[key] : b;
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

o2DB._copyTextToClipboard=(text)=>{
    try{
        navigator.clipboard.writeText(text);
    }catch(err){
        o2.api.page.notice(err.message, 'error');
        throw err;
    }
};

o2DB._appTools = [
    {
        ooicon: 'window-max',
        _type: 'app-tool',
        name: '复制名称',
        handleClick: (data, appid, appdata)=>{ o2DB._copyTextToClipboard(appdata.name); },
    },{
        ooicon: 'window-max',
        _type: 'app-tool',
        name: '复制别名',
        condition: (data, appdata)=>{ return !!appdata.alias; },
        handleClick: (data, appid, appdata)=>{ o2DB._copyTextToClipboard(appdata.alias); },
    },{
        ooicon: 'window-max',
        _type: 'app-tool',
        name: '复制id',
        handleClick: (data, appid, appdata)=>{ o2DB._copyTextToClipboard(appdata.id); },
    },{
        ooicon: 'window-max',
        _type: 'app-tool',
        name: '复制对象',
        handleClick: (data, appid, appdata)=>{
            var {name, alias, id} = appdata;
            o2DB._copyTextToClipboard(
                JSON.stringify({name, alias, id}, null, 2)
            )
        }
    }
];

o2DB._designerTools = [
    {
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '复制名称',
        handleClick: (data, appdata)=>{ o2DB._copyTextToClipboard(data.name); },
    },{
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '复制别名',
        condition: (data, appdata)=>{ return !!data.alias; },
        handleClick: (data, appdata)=>{ o2DB._copyTextToClipboard(data.alias); },
    },{
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '复制id',
        handleClick: (data, appdata)=>{ o2DB._copyTextToClipboard(data.id); },
    },{
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '复制对象',
        handleClick: (data, appdata)=>{
            var {name, alias, id} = data;
            o2DB._copyTextToClipboard(
                JSON.stringify({name, alias, id}, null, 2)
            )
        }
    },{
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '复制应用和设计对象',
        title: '优先级：别名->名称',
        handleClick: (data, appdata)=>{
            debugger;
            var obj = {
                type: appdata ? appdata._appType : data._appType,
                name: data.alias || data.name
            };
            if( appdata ){
                obj.application = appdata.alias || appdata.name;
            }
            o2DB._copyTextToClipboard( JSON.stringify(obj, null, 2) )
        }
    },{
        ooicon: 'window-max',
        _type: 'designer-tool',
        name: '拷贝到其他应用',
        handleClick: (data, appid, appdata)=>{
            var {name, alias, id} = appdata;
            o2DB._copyTextToClipboard(
                JSON.stringify({name, alias, id}, null, 2)
            )
        }
    }
];

o2DB._config = {
    _type: 'root',
    children: [
        {
            handleClick: () => {
                o2DB._openApp('portal.PortalExplorer');
            },
            name: '门户管理',
            componentName: 'portal.PortalExplorer',
            ooicon: 'portal',
            _type: 'app-category',
            children: [{
                name: '门户应用',
                componentName: 'portal.PortalManager',
                label: '应用',
                _type: 'app',
                _appType: 'portal',
                categorized: true,
                handleClick: (item) => {
                    o2DB._openApp('portal.PortalManager', null, {application: item.id});
                },
                getAction: (appid)=>{
                    return o2DB._portalAction.PortalAction.get(appid);
                },
                listAction: () => {
                    return o2DB._portalAction.PortalAction.list().then((json) => {
                        return o2DB._sort(json.data, 'name').map((item) => {
                            return {
                                ...item,
                                _pinyin: o2DB._toPY(item),
                                category: item.portalCategory,
                                defaultIcon: '../x_component_portal_PortalExplorer/$Main/default/icon/application.png',
                                _appType: 'portal'
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '页面配置',
                        componentName: 'portal.PageManager',
                        _type: 'desiginer-category',
                        ooicon: 'pagepeizhi',
                        handleClick: (item, appid) => {
                            o2DB._openApp('portal.PortalManager', null, {navi: 0, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('portal.PageDesigner', opt, opt);
                        },
                        children: [{
                            label: '页面',
                            _type: 'designer',
                            categorized: true,
                            handleClick: (page) => {
                                o2DB._openApp('portal.PageDesigner', null, {id: page.id});
                            },
                            listAction: (appid) => {
                                return o2DB._portalAction.PageAction.listWithPortal(appid).then((pages) => {
                                    return o2DB._sort(pages.data).map((page) => {
                                        return {
                                            ...page,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(page)
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '部件配置',
                        componentName: 'portal.WidgetDesigner',
                        _type: 'designer-category',
                        label: '部件',
                        ooicon: 'app-center',
                        handleClick: (item, appid) => {
                            o2DB._openApp('portal.PortalManager', null, {navi: 1, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('portal.WidgetDesigner', opt, opt);
                        },
                        children: [{
                            label: '部件',
                            _type: 'designer',
                            categorized: true,
                            handleClick: (portal) => {
                                o2DB._openApp('portal.WidgetDesigner', null, {id: portal.id});
                            },
                            listAction: (appid) => {
                                return o2DB._portalAction.WidgetAction.listWithPortal(appid).then((widgets) => {
                                    return o2DB._sort(widgets.data).map((widget) => {
                                        return {
                                            ...widget,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(widget)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据字典',
                        componentName: 'portal.DictionaryDesigner',
                        _type: 'designer-category',
                        label: '数据字典',
                        ooicon: 'js',
                        handleClick: (item, appid) => {
                            o2DB._openApp('portal.PortalManager', null, {navi: 2, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('portal.DictionaryDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据字典',
                            _type: 'designer',
                            handleClick: (dict) => {
                                o2DB._openApp('portal.DictionaryDesigner', null, {
                                    id: dict.id, application: { id: dict.appid }
                                });
                            },
                            listAction: (appid) => {
                                return o2DB._portalAction.DictAction.listWithApplication(appid).then((dicts) => {
                                    return o2DB._sort(dicts.data).map((dict) => {
                                        return {
                                            ...dict,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(dict)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        componentName: 'portal.ScriptDesigner',
                        _type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'jiaoben',
                        handleClick: (item, appid) => {
                            o2DB._openApp('portal.PortalManager', null, {navi: 3, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('portal.ScriptDesigner', opt, opt);
                        },
                        children: [{
                            label: '脚本配置',
                            _type: 'designer',
                            handleClick: (script) => {
                                o2DB._openApp('portal.ScriptDesigner', null, {
                                    id: script.id,
                                    application: {id: script.appid}
                                });
                            },
                            listAction: (appid) => {
                                return o2DB._portalAction.ScriptAction.listWithPortal(appid).then((scripts) => {
                                    return o2DB._sort(scripts.data).map((script) => {
                                        return {
                                            ...script,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(script)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        componentName: 'portal.FileDesigner',
                        _type: 'designer-category',
                        ooicon: 'folder-open',
                        handleClick: (item, appid) => {
                            o2DB._openApp('portal.PortalManager', null, {navi: 4, application: appid});
                        }
                    }, {
                        name: '门户属性',
                        componentName: 'portal.Property',
                        _type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            o2DB._openApp('portal.PortalManager', null, {navi: 5, application: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                o2DB._openApp('process.ApplicationExplorer');
            },
            name: '流程管理',
            componentName: 'process.ApplicationExplorer',
            ooicon: 'liucheng',
            _type: 'app-category',
            children: [{
                name: '流程应用',
                componentName: 'process.ProcessManager',
                label: '应用',
                _type: 'app',
                _appType: 'process',
                categorized: true,
                handleClick: (item) => {
                    o2DB._openApp('process.ProcessManager', null, {application: item.id});
                },
                getAction: (appid)=>{
                    return o2DB._processAction.ApplicationAction.get(appid);
                },
                listAction: () => {
                    return o2DB._processAction.ApplicationAction.listSummary().then((json) => {
                        return o2DB._sort(json.data, 'name').map((item) => {
                            return {
                                ...item,
                                category: item.applicationCategory,
                                defaultIcon: '../x_component_process_ApplicationExplorer/$Main/default/icon/application.png',
                                _appType: 'process',
                                _pinyin: o2DB._toPY(item)
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '表单配置',
                        componentName: 'process.FormManager',
                        _type: 'designer-category',
                        ooicon: 'biaodan',
                        handleClick: (item, appid) => {
                            o2DB._openApp('process.ProcessManager', null, {navi: 0, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('process.FormDesigner', opt, opt);
                        },
                        children: [{
                            label: '表单',
                            _type: 'designer',
                            categorized: true,
                            handleClick: (form) => {
                                o2DB._openApp('process.FormDesigner', null, {id: form.id});
                            },
                            listAction: (appid) => {
                                return o2DB._processAction.FormAction.listWithApplication(appid).then((forms) => {
                                    return o2DB._sort(forms.data).map((form) => {
                                        return {
                                            ...form,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(form)
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '流程配置',
                        componentName: 'process.ProcessDesigner',
                        _type: 'designer-category',
                        label: '流程',
                        ooicon: 'a-flowprocess',
                        handleClick: (item, appid) => {
                            o2DB._openApp('process.ProcessManager', null, {navi: 1, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('process.ProcessDesigner', opt, opt);
                        },
                        children: [{
                            label: '流程',
                            _type: 'designer',
                            categorized: true,
                            handleClick: (process) => {
                                o2DB._openApp('process.ProcessDesigner', null, {id: process.id});
                            },
                            listAction: (appid) => {
                                return o2DB._processAction.ProcessAction.listWithApplication(appid).then((processes) => {
                                    return o2DB._sort(processes.data).map((process) => {
                                        return {
                                            ...process,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(process)
                                        };
                                    });
                                });
                            }
                        }]
                    }, {
                        name: '数据字典',
                        componentName: 'process.DictionaryDesigner',
                        _type: 'designer-category',
                        label: '数据字典',
                        ooicon: 'js',
                        handleClick: (item, appid) => {
                            o2DB._openApp('process.ProcessManager', null, {navi: 2, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('process.DictionaryDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据字典',
                            _type: 'designer',
                            handleClick: (dict) => {
                                o2DB._openApp('process.DictionaryDesigner', null, { id: dict.id, application: { id: dict.appid } });
                            },
                            listAction: (appid) => {
                                return o2DB._processAction.ApplicationDictAction.listWithApplication(appid).then((dicts) => {
                                    return o2DB._sort(dicts.data).map((dict) => {
                                        return {
                                            ...dict,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(dict)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        componentName: 'process.ScriptDesigner',
                        _type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'jiaoben',
                        handleClick: (item, appid) => {
                            o2DB._openApp('process.ProcessManager', null, {navi: 3, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('process.ScriptDesigner', opt, opt);
                        },
                        children: [{
                            label: '脚本配置',
                            _type: 'designer',
                            handleClick: (script) => {
                                o2DB._openApp('process.ScriptDesigner', null, {
                                    id: script.id,
                                    application: {id: script.appid}
                                });
                            },
                            listAction: (appid) => {
                                return o2DB._processAction.ScriptAction.listWithApplication(appid).then((scripts) => {
                                    return o2DB._sort(scripts.data).map((script) => {
                                        return {
                                            ...script,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(script)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        componentName: 'process.FileDesigner',
                        _type: 'designer-category',
                        ooicon: 'folder-open',
                        handleClick: (item, appid) => {
                            o2DB._openApp('process.ProcessManager', null, {navi: 4, application: appid});
                        }
                    }, {
                        name: '应用属性',
                        componentName: 'process.Property',
                        _type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            o2DB._openApp('process.ProcessManager', null, {navi: 5, application: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                o2DB._openApp('cms.Column');
            },
            name: '内容管理',
            componentName: 'cms.Column',
            ooicon: 'note',
            _type: 'app-category',
            children: [{
                name: '内容管理',
                componentName: 'cms.ColumnManager',
                label: '应用',
                _type: 'app',
                _appType: 'cms',
                categorized: true,
                handleClick: (item) => {
                    o2DB._openApp('cms.ColumnManager', null, {column: item.id});
                },
                getAction: (appid)=>{
                    return o2DB._cmsAction.AppInfoAction.getAppInfo(appid).then((item) => {
                        item.data = {
                            ...item.data,
                            alias: item.appAlias,
                            name: item.appName,
                        }
                        return item;
                    });
                },
                listAction: () => {
                    return o2DB._cmsAction.AppInfoAction.listAllAppInfo().then((json) => {
                        return o2DB._sort(json.data, 'appName').map((item) => {
                            return {
                                ...item,
                                category: item.appType,
                                alias: item.appAlias,
                                name: item.appName,
                                icon: item.appIcon,
                                defaultIcon: '../x_component_cms_Column/$Main/default/icon/column.png',
                                _appType: 'cms',
                                _pinyin: o2DB._toPY({alias: item.appAlias, name: item.appName})
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '分类配置',
                        componentName: 'cms.CategoryManager',
                        _type: 'designer-category',
                        ooicon: 'bujianpeizhi',
                        handleClick: (item, appid) => {
                            o2DB._openApp('cms.ColumnManager', null, {navi: 'categoryConfig', column: appid});
                        },
                        children: [{
                            label: '分类',
                            _type: 'designer',
                            handleClick: (category) => {
                                o2DB._openApp('cms.ColumnManager', null, {
                                    navi: 'categoryConfig',
                                    column: category.appid,
                                    categoryId: category.id
                                });
                            },
                            listAction: (appid) => {
                                return o2DB._cmsAction.CategoryInfoAction.listViewableCategoryInfo_AllType(appid).then((categorys) => {
                                    return o2DB._sort(categorys.data).map((category) => {
                                        return {
                                            ...category,
                                            alias: category.categoryAlias,
                                            name: category.categoryName,
                                            appid: category.appId,
                                            _pinyin: o2DB._toPY({alias: category.categoryAlias, name: category.categoryName})
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '表单配置',
                        componentName: 'cms.FormDesigner',
                        _type: 'designer-category',
                        label: '表单',
                        ooicon: 'biaodan',
                        handleClick: (item, appid) => {
                            o2DB._openApp('cms.ColumnManager', null, {navi: 'formConfig', column: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('cms.FormDesigner', opt, opt);
                        },
                        children: [{
                            label: '表单',
                            _type: 'designer',
                            handleClick: (form) => {
                                o2DB._openApp('cms.FormDesigner', null, {id: form.id});
                            },
                            listAction: (appid) => {
                                return o2DB._cmsAction.FormAction.listFormByAppId(appid).then((forms) => {
                                    return o2DB._sort(forms.data).map((form) => {
                                        return {
                                            ...form,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(form)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据字典',
                        componentName: 'cms.DictionaryDesigner',
                        _type: 'designer-category',
                        label: '数据字典',
                        ooicon: 'js',
                        handleClick: (item, appid) => {
                            o2DB._openApp('cms.ColumnManager', null, {navi: 'dataConfig', column: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('cms.DictionaryDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据字典',
                            _type: 'designer',
                            handleClick: (dict) => {
                                o2DB._openApp('cms.DictionaryDesigner', null, {
                                    id: dict.id, application: { id: dict.appid }
                                });
                            },
                            listAction: (appid) => {
                                return o2DB._cmsAction.AppDictDesignAction.listWithAppInfo(appid).then((dicts) => {
                                    return o2DB._sort(dicts.data).map((dict) => {
                                        return {
                                            ...dict,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(dict)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '脚本配置',
                        componentName: 'cms.ScriptDesigner',
                        _type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'jiaoben',
                        handleClick: (item, appid) => {
                            o2DB._openApp('cms.ColumnManager', null, {navi: 'scriptConfig', column: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('cms.ScriptDesigner', opt, opt);
                        },
                        children: [{
                            label: '脚本配置',
                            _type: 'designer',
                            handleClick: (script) => {
                                o2DB._openApp('cms.ScriptDesigner', null, {id: script.id, application: {id: script.appid}});
                            },
                            listAction: (appid) => {
                                return o2DB._cmsAction.ScriptAction.listWithApplication(appid).then((scripts) => {
                                    return o2DB._sort(scripts.data).map((script) => {
                                        return {
                                            ...script,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(script)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '资源文件',
                        componentName: 'cms.FileDesigner',
                        _type: 'designer-category',
                        ooicon: 'folder-open',
                        handleClick: (item, appid) => {
                            o2DB._openApp('cms.ColumnManager', null, {navi: 'fileConfig', column: appid});
                        }
                    }, {
                        name: '栏目属性',
                        componentName: 'cms.Property',
                        _type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            o2DB._openApp('cms.ColumnManager', null, {navi: 'applicationProperty', column: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                o2DB._openApp('query.QueryExplorer');
            },
            name: '数据中心',
            componentName: 'query.QueryExplorer',
            ooicon: 'integral',
            _type: 'app-category',
            children: [{
                name: '数据应用',
                componentName: 'query.QueryManager',
                label: '应用',
                _type: 'app',
                _appType: 'query',
                categorized: true,
                handleClick: (item) => {
                    o2DB._openApp('query.QueryManager', null, {application: item.id});
                },
                getAction: (appid)=>{
                    return o2DB._queryAction.QueryAction.get(appid);
                },
                listAction: () => {
                    return o2DB._queryAction.QueryAction.listAll().then((json) => {
                        return o2DB._sort(json.data, 'name').map((item) => {
                            return {
                                ...item,
                                category: item.queryCategory,
                                defaultIcon: '../x_component_query_QueryExplorer/$Main/default/icon/application.png',
                                _appType: 'query',
                                _pinyin: o2DB._toPY(item)
                            };
                        });
                    })
                },
                children: [
                    {
                        name: '视图配置',
                        componentName: 'query.ViewDesigner',
                        _type: 'designer-category',
                        ooicon: 'shitupeizhi3',
                        handleClick: (item, appid) => {
                            o2DB._openApp('query.QueryManager', null, {navi: 0, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('query.ViewDesigner', opt, opt);
                        },
                        children: [{
                            label: '视图',
                            _type: 'designer',
                            handleClick: (view) => {
                                o2DB._openApp('query.ViewDesigner', null, {id: view.id, application: {id: view.appid}});
                            },
                            listAction: (appid) => {
                                return o2DB._queryAction.ViewAction.listWithQuery(appid).then((views) => {
                                    return o2DB._sort(views.data).map((view) => {
                                        return {
                                            ...view,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(view)
                                        };
                                    });
                                })
                            }
                        }]
                    },
                    {
                        name: '统计配置',
                        componentName: 'query.StatDesigner',
                        _type: 'designer-category',
                        label: '统计',
                        ooicon: 'shujubiao2',
                        handleClick: (item, appid) => {
                            o2DB._openApp('query.QueryManager', null, {navi: 1, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('query.StatDesigner', opt, opt);
                        },
                        children: [{
                            label: '统计',
                            _type: 'designer',
                            handleClick: (stat) => {
                                o2DB._openApp('query.StatDesigner', null, {id: stat.id, application: {id: stat.appid}});
                            },
                            listAction: (appid) => {
                                return o2DB._queryAction.StatAction.listWithQuery(appid).then((stats) => {
                                    return o2DB._sort(stats.data).map((stat) => {
                                        return {
                                            ...stat,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(stat)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据表',
                        componentName: 'query.TableDesigner',
                        _type: 'designer-category',
                        label: '数据表',
                        ooicon: 'shujubiao',
                        handleClick: (item, appid) => {
                            o2DB._openApp('query.QueryManager', null, {navi: 2, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('query.TableDesigner', opt, opt);
                        },
                        children: [{
                            label: '数据表',
                            _type: 'designer',
                            handleClick: (table) => {
                                o2DB._openApp('query.TableDesigner', null, {
                                    id: table.id, application: { id: table.appid }
                                });
                            },
                            listAction: (appid) => {
                                return o2DB._queryAction.TableAction.listWithQuery(appid).then((tables) => {
                                    return o2DB._sort(tables.data).map((table) => {
                                        return {
                                            ...table,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(table)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '查询配置',
                        componentName: 'query.StatementDesigner',
                        _type: 'designer-category',
                        label: '脚本配置',
                        ooicon: 'chaxunpeizhi',
                        handleClick: (item, appid) => {
                            o2DB._openApp('query.QueryManager', null, {navi: 3, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('query.StatementDesigner', opt, opt);
                        },
                        children: [{
                            label: '查询配置',
                            _type: 'designer',
                            handleClick: (statement) => {
                                o2DB._openApp('query.StatementDesigner', null, {
                                    id: statement.id,
                                    application: {id: statement.appid}
                                });
                            },
                            listAction: (appid) => {
                                return o2DB._queryAction.StatementAction.listWithQuery(appid).then((statements) => {
                                    return o2DB._sort(statements.data).map((statement) => {
                                        return {
                                            ...statement,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(statement)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '导入模型',
                        componentName: 'query.ImporterDesigner',
                        _type: 'designer-category',
                        ooicon: 'file_upload',
                        handleClick: (item, appid) => {
                            o2DB._openApp('query.QueryManager', null, {navi: 4, application: appid});
                        },
                        handleCreate: (appid, appname)=>{
                            var opt = {application: {id: appid, name: appname}};
                            o2DB._openApp('query.ImporterDesigner', opt, opt);
                        },
                        children: [{
                            label: '导入模型',
                            _type: 'designer',
                            handleClick: (importer) => {
                                o2DB._openApp('query.ImporterDesigner', null, {
                                    id: importer.id,
                                    application: {id: importer.appid}
                                });
                            },
                            listAction: (appid) => {
                                return o2DB._queryAction.ImportModelAction.listWithQuery(appid).then((items) => {
                                    return o2DB._sort(items.data).map((item) => {
                                        return {
                                            ...item,
                                            appid: appid,
                                            _pinyin: o2DB._toPY(item)
                                        };
                                    });
                                })
                            }
                        }]
                    }, {
                        name: '数据中心属性',
                        componentName: 'query.Property',
                        _type: 'designer-category',
                        ooicon: 'jiekoupeizhi2',
                        handleClick: (item, appid) => {
                            o2DB._openApp('query.QueryManager', null, {navi: 5, application: appid});
                        }
                    }]
            }]
        },
        {
            handleClick: () => {
                o2DB._openApp('service.ServiceManager');
            },
            name: '服务管理',
            componentName: 'service.ServiceManager',
            ooicon: 'process-service',
            _type: 'app-category',
            children: [{
                name: '代理配置',
                componentName: 'service.AgentDesigner',
                _type: 'designer-category',
                ooicon: 'dailipeizhi1',
                handleClick: (item, appid) => {
                    o2DB._openApp('service.ServiceManager', null, {navi: 0});
                },
                handleCreate: ()=>{
                    o2DB._openApp('service.AgentDesigner', null, {});
                },
                children: [{
                    label: '代理',
                    _type: 'designer',
                    handleClick: (item) => {
                        o2DB._openApp('service.AgentDesigner', null, {id: item.id});
                    },
                    listAction: () => {
                        return o2DB._serviceAction.AgentAction.list().then((agents) => {
                            return o2DB._sort(agents.data).map(agent=>{
                                return {
                                    ...agent,
                                    _appType: 'service',
                                    _pinyin: o2DB._toPY(agent)
                                };
                            });
                        })
                    }
                }]
            },
                {
                    name: '接口配置',
                    componentName: 'portal.InvokeDesigner',
                    _type: 'designer-category',
                    label: '接口',
                    ooicon: 'jiekoupeizhi21',
                    handleClick: (item, appid) => {
                        o2DB._openApp('service.ServiceManager', null, {navi: 1});
                    },
                    handleCreate: ()=>{
                        o2DB._openApp('service.InvokeDesigner', null, {});
                    },
                    children: [{
                        label: '接口',
                        _type: 'designer',
                        categorized: true,
                        handleClick: (item) => {
                            o2DB._openApp('service.InvokeDesigner', null, {id: item.id});
                        },
                        listAction: () => {
                            return o2DB._serviceAction.InvokeAction.list().then((items) => {
                                return o2DB._sort(items.data).map(item=>{
                                    return {
                                        ...item,
                                        _appType: 'service',
                                        _pinyin: o2DB._toPY(item)
                                    };
                                });
                            })
                        }
                    }]
                }, {
                    name: '脚本配置',
                    componentName: 'service.ScriptDesigner',
                    _type: 'designer-category',
                    label: '脚本配置',
                    ooicon: 'jiaoben',
                    handleClick: (item, appid) => {
                        o2DB._openApp('service.ServiceManager', null, {navi: 2});
                    },
                    handleCreate: ()=>{
                        o2DB._openApp('service.ScriptDesigner', null, {});
                    },
                    children: [{
                        label: '脚本配置',
                        _type: 'designer',
                        handleClick: (item) => {
                            o2DB._openApp('service.ScriptDesigner', null, { id: item.id });
                        },
                        listAction: () => {
                            return o2DB._serviceAction.ScriptAction.list().then((items) => {
                                return o2DB._sort(items.data).map(item=>{
                                    return {
                                        ...item,
                                        _appType: 'service',
                                        _pinyin: o2DB._toPY(item)
                                    };
                                });
                            })
                        }
                    }]
                }, {
                    name: '数据配置',
                    componentName: 'service.DictionaryDesigner',
                    _type: 'designer-category',
                    label: '数据配置',
                    ooicon: 'js',
                    handleClick: () => {
                        o2DB._openApp('service.ServiceManager', null, {navi: 3});
                    },
                    handleCreate: ()=>{
                        o2DB._openApp('service.DictionaryDesigner', null, {});
                    },
                    children: [{
                        label: '数据配置',
                        _type: 'designer',
                        handleClick: (item) => {
                            o2DB._openApp('service.DictionaryDesigner', null, {id: item.id});
                        },
                        listAction: () => {
                            return o2DB._serviceAction.DictAction.list().then((items) => {
                                return o2DB._sort(items.data).map(item=>{
                                    return {
                                        ...item,
                                        _appType: 'service',
                                        _pinyin: o2DB._toPY(item)
                                    };
                                });
                            });
                        }
                    }]
                }]
        },
        {_type: 'separator'},
        {
            name: '搜索设计',
            title: '根据关键字搜索具体设计',
            componentName: 'FindDesigner',
            ooicon: 'search',
            handleClick: (item) => {
                o2DB._openApp('FindDesigner');
            }
        },
        {
            name: '最近打开',
            title: '最近打开的设计元素',
            componentName: 'historyOpened',
            ooicon: 'clock',
            _type: 'app-category',
            autoRefresh: true,
            children: [{
                label: '最近打开',
                _type: 'designer',
                handleClick: (item) => {
                    o2DB._openApp(item.componentName, null, { id: item.id, application: {id: item.appid} });
                },
                listAction: () => {
                    return o2.UD.getDataJson(o2DB._HISTORY_DESIGNER_NAME).then((items) => {
                        return o2DB._sort(items || [], 'time', true).map((item) => {
                            return {
                                ...item,
                                _pinyin: o2DB._toPY(item),
                                ooicon: o2DB._ooiconMap[item.componentName] || '',
                                title: (item.applicationName || '') + ' ' + o2DB._appNameMap[item.componentName] + ' ' + item.componentName
                            };
                        });
                    })
                }
            }]
        },
    ]
};

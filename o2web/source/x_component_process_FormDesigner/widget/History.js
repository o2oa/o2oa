MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.process.FormDesigner.widget.History = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {

	},
	initialize: function(form, actionNode, options){
		this.setOptions(options);
        this.form = form;
        this.actionNode = actionNode;
        this.root = this.form.node;
		// this.path = "../x_component_process_FormDesigner/widget/$ImageClipper/";
		// this.cssPath = "../x_component_process_FormDesigner/widget/$ImageClipper/"+this.options.style+"/css.wcss";
		// this._loadCss();
	},

	load: function(data) {
        //存储当前表面状态数组-上一步
        this.preArray = [];
        //存储当前表面状态数组-下一步
        this.nextArray = [];

        this.node = new Element("div", {"height":"100px"});

        var _self = this;
        this.tooltips = new MWF.FCWHistory.Tooltips(
            this.form.designer.formNode,
            this.actionNode,
            this.form.designer,
            null,
            {
                onPostCreate: function () {
                    _self.node.inject( this.contentNode );
                    _self.add({
                        "operation": "open", //操作 create, copy, move, delete
                        "type": "form", //property
                        "moduleId": "form"
                    });

                }
            }
        );
        this.tooltips.load();
    },
    //获取domPath
    getPath: function (node) {
	    var root = this.root;
        var path = [];
        var parent, childrens, nodeIndex;
        while (node && node !== root) {
            parent = node.parentElement;
            childrens = Array.from(parent.children);
            nodeIndex = childrens.indexOf(node);
            path.push(nodeIndex);
            node = parent;
        }
        return path.reverse();
    },
    add: function(log, module) {
        // var log = { //也有可能是对象数组
        //     "operation": "create", //操作 create, copy, move, delete
        //     "type": "module", //property
        //     "moduleType": "", //模块类型
        //     "moduleId": "", //模块id
        //      "fromList": [{  //初始数据
        //          "json": {},  //最终json
        //         "jsonObject": {}, //本json所包含的子json
        //         "html": "", //最终html
        //         "path": "", //最终dom path
        //      }],
        //      "toList": [{  //结束数据
        //          "json": {},  //最终json
        //          "jsonObject": {}, //本json所包含的子json
        //          "html": "", //最终html
        //          "path": "", //最终dom path
        //      }]
        // };
        var item;
        switch (log.moduleType) {
            case "Table$Td":
                item = new MWF.FCWHistory.TableTdItem(this, log);
                break;
            case "Datatable$Title":
            case "Datatable$Data":
                item = new MWF.FCWHistory.DatatableTdItem(this, log);
                break;
            case "Tab$Page":
                item = new MWF.FCWHistory.TabpageItem(this, log);
                break;
            default:
                item = new MWF.FCWHistory.Item(this, log);
        }
        item.load();

        var it;
        while( this.nextArray.length ){
            it = this.nextArray.pop();
            it.destroy();
        }

        this.preArray.push(item);
    },
    goto: function(item){
	    var it;
	    if( item.status === "pre" ){
	        it = this.preArray.getLast();
	        while (it && item !== it){
                it.undo();
                this.nextArray.unshift(it); //插入到灰显数组前面
                this.preArray.pop(); //删除preArray最后一个
                it = this.preArray.getLast();
            }
        }else if( item.status === "next" ){
            it = this.nextArray[0];
            while (it && item !== it){
                it.redo();
                this.preArray.push(it); //插入到preArray数组最后
                this.nextArray.shift();
                it = this.nextArray[0];
            }
            item.redo();
            this.preArray.push(item); //插入到preArray数组最后
            this.nextArray.shift();
        }
    }
});

MWF.FCWHistory = MWF.xApplication.process.FormDesigner.widget.History;

MWF.FCWHistory.Item = new Class({
    Implements: [Options, Events],
    options: {},
    initialize: function (history, log) {
        this.history = history;
        this.data = log;
        this.status = "pre";
        this.form = this.history.form;
        this.root = this.history.root;
    },
    load: function () {
        this.node = new Element("div", {
            styles : {
                "color": "#333",
                "padding": "5px"
            },
            text: this.getText(),
            events: {
                click: this.comeHere.bind(this)
            }
        }).inject( this.history.node );
        if( this.data.toList && this.data.toList.length > 1 ){
            this.sortByPath(this.data.toList);
        }
        if( this.data.fromList && this.data.fromList.length > 1 ){
            this.sortByPath(this.data.fromList);
        }
    },
    getText: function () {
        return this.data.title || (this.data.operation + " " + this.data.moduleId)
    },
    comeHere: function () {
        this.history.goto(this)
    },
    undo: function () { //回退
        this.status = "next";
        this.node.setStyles({
            "color": "#ccc"
        });
        switch (this.data.type) {
            case "module":
                this.undoModule();
                break;
            case "property":
                this.undoPropery();
                break;
        }
        console.log( this.data );
    },
    redo: function(){ //重做
        this.status = "pre";
        this.node.setStyles({
            "color": "#333"
        });
        switch (this.data.type) {
            case "module":
                this.redoModule();
                break;
            case "property":
                this.redoPropery();
                break;
        }
        console.log( this.data );
    },
    undoPropery: function(){

    },
    redoPropery: function(){

    },
    undoModule: function(){
        debugger;
        switch (this.data.operation) {
            case "create":
                this.deleteModuleList();
                break;
            case "copy":
                this.deleteModuleList();
                break;
            case "move":
                var to = this.data.toList[0];
                var from = this.data.fromList[0];
                var dom = this.getDomByPath( to.path );
                this.injectToByPath( from.path, dom );
                break;
            case "delete":
                this.loadModuleList();
                break;
            case "cut":
                this.loadModuleList();
                break;
            case "paste":
                this.deleteModuleList();
                break;
        }
        this.unselectModule();
    },
    redoModule: function(){
        switch (this.data.operation) {
            case "create":
                this.loadModuleList();
                break;
            case "copy":
                this.loadModuleList();
                break;
            case "move":
                var to = this.data.toList[0];
                var from = this.data.fromList[0];
                var dom = this.getDomByPath( from.path );
                this.injectToByPath( to.path, dom );
                break;
            case "delete":
                this.deleteModuleList();
                break;
            case "cut":
                this.deleteModuleList();
                break;
            case "paste":
                this.loadModuleList();
                break;
        }
        this.unselectModule();
    },

    destroy: function () {
        this.node.destroy();
    },
    unselectModule: function () {
        if(this.form.currentSelectedModule && this.form.currentSelectedModule.unSelected){
            this.form.currentSelectedModule.unSelected()
        }
        this.form.currentSelectedModule = null;

        if( this.form.selectedModules && this.form.selectedModules.length ){
            this.form.selectedModules = []
        }
    },
    deleteModuleList: function(){
        for( var i=this.data.toList.length-1; i>-1; i-- ){
            var to = this.data.toList[i];
            this._deleteModule( to.path );
        }
    },
    _deleteModule: function( path ){
        var module, dom = this.getDomByPath( path );
        if(dom)module = dom.retrieve("module");
        if(module)module.destroy();
    },
    loadModuleList: function(){
        for( var i=0; i<this.data.toList.length; i++ ) {
            var to = this.data.toList[i];
            this._loadModule(to.path, to.html, to.json, to.jsonObject);
        }
    },
    _loadModule: function( path, html, json, jsonObject ){
        var dom = this.injectHtmlByPath( path, html );
        this.addModulesJson(jsonObject);
        var parentModule = this.getParentModule(dom);
        var module = this.form.loadModule(json, dom, parentModule || this.form);
        module._setEditStyle_custom("id");
    },
    //根据路径顺序排序
    sortByPath: function( arr ){
        arr.sort(function (a, b) {
            var max = Math.max(a.path.length, b.path.length);
            for( var i=0; i< max; i++ ){
                if( a.path[i] && !b.path[i] && b.path[i]!==0 )return -1;
                if( b.path[i] && !a.path[i] && a.path[i]!==0 )return 1;
                if( a.path[i] !== b.path[i] )return a.path[i] - b.path[i];
            }
            return -1;
        })
    },
    //根据路径获取dom
    getDomByPath: function(path){
        var i, nodeIndex;
        var node = this.root;
        for( i=0; i<path.length; i++ ){
            nodeIndex = path[i];
            node = node.children[nodeIndex];
        }
        return node;
    },
    //插入到对应位置
    injectToByPath: function(path, dom){
        var i, nodeIndex;
        var node = this.root;
        for( i=0; i<path.length - 1; i++ ){
            nodeIndex = path[i];
            node = node.children[nodeIndex];
        }
        var last = path.getLast();
        if( last === 0 ){
            dom.inject( node, "top" );
        }else{
            var contains = false;
            for( i=0; i<last; i++ ){
                if( node.children[i] === dom ){ //如果位置包含当前dom
                    contains = true;
                    break;
                }
            }
            node = node.children[contains ? last : (last-1)];
            dom.inject(node, "after");
        }
        this.resetTreeNode( dom );
    },
    //插入HTML到对应位置
    injectHtmlByPath: function(path, html){
        debugger;
        var i, nodeIndex;
        var node = this.root;
        for( i=0; i<path.length - 1; i++ ){
            nodeIndex = path[i];
            node = node.children[nodeIndex];
        }
        var dom = new Element("div");
        var last = path.getLast();
        var parentNode = node;
        if( last === 0 ){
            dom.inject( node, "top" );
        }else{
            node = node.children[last-1];
            dom.inject(node, "after");
        }
        dom.outerHTML = html; //dom没了
        dom = parentNode.children[last];
        return dom;
    },
    resetTreeNode: function(node){
        var module = node.retrieve("module");
        if(module){
            module.parentContainer = this.getParentModule( node );
            module._resetTreeNode();
        }
    },
    addModulesJson: function( jsonObject ){
        if(jsonObject){
            for( var id in jsonObject ){
                this.form.json.moduleList[id] = jsonObject[id];
            }
        }
    },
    getParentModule: function (node) {
        var parent, parentNode = node.getParent();
        while( parentNode && !parent ){
            var mwftype = parentNode.get("mwftype");
            if( mwftype === "form") {
                parent = this.form;
            }else if( mwftype ){
                parent = parentNode.retrieve("module");
            }else{
                parentNode = parentNode.getParent();
            }
        }
        return parent;
    },
    getParentModuleByType: function( node, moduleType ){
        var parentNode = node;
        var module;
        while( parentNode && !module ){
            if( parentNode.get("mwftype") === moduleType )module = parentNode.retrieve("module");
            parentNode = parentNode.getParent();
        }
        return module;
    }
});


MWF.FCWHistory.TableTdItem = new Class({
    Extends: MWF.xApplication.process.FormDesigner.widget.History.Item,
    getTrPathList: function(){
        var trPathStrList = [];
        this.data.toList.each(function (log) {
            var path = Array.clone(log.path);
            path.pop();
            var str = path.join(",");
            if( trPathStrList.indexOf( str ) === -1 )trPathStrList.push(str);
        });
        return trPathStrList.map(function (str) {
            return str.split(",").map(function (path) {
                return path.toInt()
            });
        }.bind(this));
    },
    restoreRow: function(){
        this.getTrPathList().each(function (path) {
            this.injectHtmlByPath( path, "<tr></tr>" ); //创建tr
        }.bind(this));
        this.restoreTds();
    },
    restoreTds: function(){
        var log;
        for( var i=0; i<this.data.toList.length; i++ ){
            log = this.data.toList[i];
            this.restoreTd( log.path, log.html, log.json, log.jsonObject, i );
        }
    },
    restoreTd: function( path, html, json, jsonObject, i ){
        var tdNode = this.injectHtmlByPath( path, html );
        this.addModulesJson( jsonObject );

        var tableModule = this.getParentModuleByType(tdNode, "table");
        tableModule.loadExistedNodeTd(tdNode, json);
    },
    deleteRow: function(){
        this.deleteTds();
        this.getTrPathList().reverse().each(function (path) {
            var tr = this.getDomByPath( path );
            if(tr)tr.destroy();
        }.bind(this));
    },
    deleteTds: function(){
        var log, dom;
        for( var i=this.data.toList.length-1; i>-1; i-- ){
            log = this.data.toList[i];
            dom = this.getDomByPath( log.path );
            this.deleteTd( dom, i );
        }
    },
    deleteTd: function( dom, i ){
        var tableModule = this.getParentModuleByType(dom, "table");
        tableModule.deleteTdWithNode(dom);
    },
    undoModule: function(){
        debugger;
        switch (this.data.operation) {
            case "insertRow": //td的操作，插入行
                this.deleteRow();
                break;
            case "insertCol": //td的操作，插入列
                this.deleteTds();
                break;
            case "deleteRow": //td的操作，删除行
                this.restoreRow();
                break;
            case "deleteCol": //td的操作，删除列
                this.restoreTds();
                break;
            case "splitCell": //拆分单元格
                this.deleteTds(); //先删除新建的单元格
                var fromLog = this.data.fromList[0]; //恢复原有的单元格
                this.restoreTd( fromLog.path, fromLog.html, fromLog.json, fromLog.jsonObject );
                break;
            case "mergeCell": //合并单元格
                //先删除新建的单元格
                var dom = this.getDomByPath( this.data.toList[0].path );
                this.deleteTd(dom);
                //恢复原来的单元格
                for( var i=0; i<this.data.fromList.length; i++ ){
                    var log = this.data.fromList[i];
                    this.restoreTd( log.path, log.html, log.json, log.jsonObject );
                }
                break;
        }
        this.unselectModule();
    },
    redoModule: function(){
        var dom, module, log;
        switch (this.data.operation) {
            case "insertRow": //td的操作，插入行
                this.restoreRow();
                break;
            case "insertCol": //td的操作，插入列
                this.restoreTds();
                break;
            case "deleteRow": //td的操作，插入列
                this.deleteRow();
                break;
            case "deleteCol": //td的操作，删除列
                this.deleteTds();
                break;
            case "splitCell": //拆分单元格
                dom = this.getDomByPath( this.data.fromList[0].path );
                this.deleteTd( dom );
                this.restoreTds();
                break;
            case "mergeCell": //合并单元格
                //删除原单元格
                for( var i=this.data.fromList.length-1; i>-1; i--){
                    dom = this.getDomByPath( this.data.fromList[i].path );
                    this.deleteTd(dom);
                }
                //恢复新的单元格
                var to = this.data.toList[0];
                this.restoreTd( to.path, to.html, to.json, to.jsonObject );
                break;
        }
        this.unselectModule();
    }
});

MWF.FCWHistory.DatatableTdItem = new Class({
    Extends: MWF.xApplication.process.FormDesigner.widget.History.TableTdItem,
    restoreTd: function (path, html, json, jsonObject, i) {
        var tdNode = this.injectHtmlByPath(path, html);
        this.addModulesJson(jsonObject);

        var tableModule = this.getParentModuleByType(tdNode, "datatable");
        if( tdNode.tagName === "TD"){
            tableModule.loadExistedNodeTd(tdNode, json);
        }else if( tdNode.tagName === "TH"){
            tableModule.loadExistedNodeTh(tdNode, json);
        }
    },
    deleteTd: function( dom, i ){
        var tableModule = this.getParentModuleByType(dom, "datatable");
        if( dom.tagName === "TD"){
            tableModule.deleteTdWithNode(dom);
        }else if( dom.tagName === "TH"){
            tableModule.deleteThWithNode(dom);
        }
    },
    undoModule: function(){
        debugger;
        switch (this.data.operation) {
            case "insertCol": //td的操作，插入列
                this.deleteTds();
                break;
            case "deleteCol": //td的操作，删除列
                this.restoreTds();
                break;
        }
        this.unselectModule();
    },
    redoModule: function(){
        var dom, module, log;
        switch (this.data.operation) {
            case "insertCol": //td的操作，插入列
                this.restoreTds();
                break;
            case "deleteCol": //td的操作，删除列
                this.deleteTds();
                break;
        }
        this.unselectModule();
    }
});

MWF.FCWHistory.TabpageItem = new Class({
    Extends: MWF.xApplication.process.FormDesigner.widget.History.Item,
    restoreTabage: function(){
        var to = this.data.toList[0];

        var contentNode = this.injectHtmlByPath( to.content.path, to.content.html );
        this.addModulesJson( to.content.jsonObject );

        var tabNode = this.injectHtmlByPath( to.path, to.html );
        this.addModulesJson( to.jsonObject );

        var tabModule = this.getParentModuleByType(tabNode, "tab");

        tabModule.loadExistedNodePage(tabNode, contentNode, to.json, to.content.json);
    },
    undoModule: function(){
        var dom, module, to = this.data.toList[0], from;
        switch (this.data.operation) {
            case "add":
                dom = this.getDomByPath( to.path );
                if(dom)module = dom.retrieve("module");
                if(module)module._delete();
                break;
            case "copy":
                break;
            case "move":
                dom = this.getDomByPath( to.path );
                this.injectToByPath( this.data.fromList[0].path, dom );

                dom = this.getDomByPath( to.content.path );
                this.injectToByPath( this.data.fromList[0].content.path, dom );
                break;
            case "delete":
                this.restoreTabage();
                break;
        }
        this.unselectModule();
    },
    redoModule: function(){
        var dom, module, to = this.data.toList[0], from;
        switch (this.data.operation) {
            case "add":
                this.restoreTabage();
                break;
            case "copy":
                break;
            case "move":
                debugger;
                dom = this.getDomByPath( this.data.fromList[0].path );
                this.injectToByPath( to.path, dom );

                dom = this.getDomByPath( this.data.fromList[0].content.path );
                this.injectToByPath( to.content.path, dom );
                break;
            case "delete":
                dom = this.getDomByPath( to.path );
                if(dom)module = dom.retrieve("module");
                if(module)module._delete();
                break;
        }
        this.unselectModule();
    }
});


MWF.FCWHistory.Tooltips = new Class({
    Extends: MTooltips,
    options : {
        style: "design",
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "right", //x轴上left center right,  auto 系统自动计算
            y : "bottom" //y 轴上top middle bottom, auto 系统自动计算
        },
        event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        isAutoHide: false,
        hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : false,
        hasCloseAction: true,
        hasMask: false,
        isParentOffset: true,
        nodeStyles: {
            padding: "0px",
            "min-height": "100px",
            "border-radius" : "0px"
        }
    },
    _customNode : function( node, contentNode ){
        new Element("div", {
            "style": "padding-left: 10px; background-color: rgb(242, 242, 242); color: #333333; height: 30px; line-height: 30px; ",
            "text": "历史记录"
        }).inject(contentNode, "before");
        //var width = ( parseInt( this.selector.options.width )  )+ "px";
        //node.setStyles({
        //    "width": width,
        //    "max-width": width
        //});
        debugger;
        // if( this.data && this.data.length > 0 ){
        //     this.createItemList( this.data, contentNode )
        // }else if( this.selector.options.tooltipWhenNoSelectValue ){
        //     this.createNoSelectValueNode( contentNode );
        // }
    },
})

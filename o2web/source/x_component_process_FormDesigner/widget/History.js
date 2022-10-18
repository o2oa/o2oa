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
        debugger;
        this.tooltips = new MWF.xApplication.process.FormDesigner.widget.History.Tooltips(
            this.form.designer.formNode,
            this.actionNode,
            this.form.designer,
            null,
            {
                onPostCreate: function () {
                    _self.node.inject( this.contentNode );
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
            node = node.children[last-1];
            dom.inject(node, "after");
        }
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
    //给指定位置插入outerHTML
    getInjectPositionByPath: function(path){
        var i, nodeIndex;
        var node = this.root;
        for( i=0; i<path.length - 1; i++ ){
            nodeIndex = path[i];
            node = node.children[nodeIndex];
        }
        var dom = new Element("div");
        var last = path.getLast();
        if( last === 0 ){
            return {
                node: node,
                position: "top"
            };
        }else{
            node = node.children[last-1];
            return {
                node: node,
                position: "after"
            };
        }
    },
    loadModule: function( path, html, json, jsonObject ){
        var dom = this.injectHtmlByPath( path, html );
        if(jsonObject){
            for( var id in jsonObject ){
                this.form.json.moduleList[id] = jsonObject[id];
            }
        }
        var parent, parentNode = dom.getParent();
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
        var module = this.form.loadModule(json, dom, parent || this.form);
        module._setEditStyle_custom("id");
    },
    add: function(log, module) {
        // var log = {
        //     "operation": "create", //操作 create, copy, move, delete
        //     "type": "module", //property
        //     "json": {},
        //     "jsonObject": {},
        //     "html": "",
        //     "path": ""
        // };
        debugger;

        log.toPath = this.getPath(module.node);

        var item = new MWF.xApplication.process.FormDesigner.widget.History.Item(this, log);
        item.load();

        var it;
        while( this.nextArray.length ){
            it = this.nextArray.pop();
            it.destroy();
        }

        this.preArray.push(item);
    },
    goto: function(item){
	    debugger;
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
	    console.log( this.preArray, this.nextArray );
    }
});

MWF.xApplication.process.FormDesigner.widget.History.Item = new Class({
    Implements: [Options, Events],
    options: {},
    initialize: function (history, log) {
        this.history = history;
        this.data = log;
        this.status = "pre";
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


        // var log = {
        //     "operation": "create", //操作 create, copy, move, delete
        //     "type": "module", //property
        //     "json": {},
        //     "html": "",
        //     "path": ""
        // };
    },
    getText: function () {
        return this.data.operation + " " + this.data.json.id
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
    },
    undoPropery: function(){

    },
    undoModule: function(){
        var dom, module;
        switch (this.data.operation) {
            case "create":
                dom = this.history.getDomByPath( this.data.toPath );
                if(dom)module = dom.retrieve("module");
                if(module)module.destroy();
                break;
            case "copy":
                dom = this.history.getDomByPath( this.data.toPath );
                if(dom)module = dom.retrieve("module");
                if(module)module.destroy();
                break;
            case "move":
                dom = this.history.getDomByPath( this.data.toPath );
                this.history.injectToByPath( this.data.fromPath, dom );
                break;
            case "delete":
                this.history.loadModule( this.data.toPath, this.data.html, this.data.json, this.data.jsonObject );
                // var obj = this.history.getInjectPositionByPath( this.data.toPath );
                // this.history.form.createModuleImmediately( this.data.json, obj.node, obj.position, true );
                break;
        }
        if(this.history.form.currentSelectedModule && this.history.form.currentSelectedModule.unSelected){
            this.history.form.currentSelectedModule.unSelected()
        }
        this.history.form.currentSelectedModule = null;
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
    },
    redoPropery: function(){

    },
    redoModule: function(){
        var dom, module;
        switch (this.data.operation) {
            case "create":
                this.history.loadModule( this.data.toPath, this.data.html, this.data.json, this.data.jsonObject);
                break;
            case "copy":
                this.history.loadModule( this.data.toPath, this.data.html, this.data.json, this.data.jsonObject );
                break;
            case "move":
                dom = this.history.getDomByPath( this.data.fromPath );
                this.history.injectToByPath( this.data.toPath, dom );
                break;
            case "delete":
                dom = this.history.getDomByPath( this.data.toPath );
                if(dom)module = dom.retrieve("module");
                if(module)module.destroy();
                break;
        }
        if(this.history.form.currentSelectedModule && this.history.form.currentSelectedModule.unSelected){
            this.history.form.currentSelectedModule.unSelected();
        }
        this.history.form.currentSelectedModule = null;
    },
    destroy: function () {
        this.node.destroy();
    }
})

MWF.xApplication.process.FormDesigner.widget.History.Tooltips = new Class({
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

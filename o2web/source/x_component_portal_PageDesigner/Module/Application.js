MWF.xApplication.portal.PageDesigner.Module.Application = MWF.PCApplication = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Application/application.html",
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Application/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Application/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "application";
		
		this.Node = null;
		this.form = form;
		this.page = form;
	},
	load : function(json, node, parent){

		this.json = json;
		this.node= node;
		this.node.store("module", this);

		//this.node.empty();

		this.node.setStyles(this.css.moduleNode);

		//this._loadNodeStyles();

		this._initModule();

        this.loadApplication();

		this._loadTreeNode(parent);

        this.setCustomStyles();

		this.parentContainer = this.treeNode.parentNode.module;
        this._setEditStyle_custom("id");

		this.parseModules();
        this.json.moduleName = this.moduleName;
        // this.node.addEvent("click", function(){
        //     this.refreshApplication();
        // }.bind(this));
        // this.node.addEvent("dblclick", function(e){
        //     this.openApplication(e);
        // }.bind(this));
	},
	_initModule: function(){
		if (!this.json.isSaved) this.setStyleTemplate();

		//this._resetModuleDomNode();

		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("properties");

		this._setNodeProperty();
		if (!this.form.isWidget) this._createIconAction();
		this._setNodeEvent();
		this.json.isSaved = true;

        this.queryGetPageDataFun = this.queryGetPageData.bind(this);
        this.postGetPageDataFun = this.postGetPageData.bind(this);
        this.page.addEvent("queryGetPageData", this.queryGetPageDataFun);
        this.page.addEvent("postGetPageData", this.postGetPageDataFun);
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "application",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.page.container);
	},
	_getDroppableNodes: function(){
		var nodes = [this.form.node].concat(this.form.moduleElementNodeList, this.form.moduleContainerNodeList, this.form.moduleComponentNodeList);
		this.form.moduleList.each( function(module){
			//不能往数据模板里拖
			if( module.moduleName === "datatemplate" ){
				var subDoms = this.form.getModuleNodes(module.node);
				nodes.erase( module.node );
				subDoms.each(function (dom) {
					nodes.erase( dom );
				})
			}
		}.bind(this));
		return nodes;
	},
	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.addEvent("selectstart", function(){
			return false;
		});
        // debugger;
        // if (this.json.applicationSelected && this.json.applicationSelected!="none" && this.json.applicationType!=="script"){
        //     this.redoSelectedApplication(this.json.applicationSelected, $(this.property.data.pid+"selectApplication").getElement("select"), "");
        // }else{
             this.loadIcon();
        // }
        // this.node.addEvent("click", function(){
        //     this.refreshApplication();
        // }.bind(this));
		//
        // debugger;
        // this.node.addEvent("dblclick", function(e){
        //     this.openApplication(e);
        // }.bind(this));

	},
    postGetPageData: function(node){
        if (!node || node.contains(this.node)) this.show();
    },
    queryGetPageData: function(node){
        if (!node || node.contains(this.node)) this.hide();
    },
    hide: function(){
		if( this.application ){
			this.application.close();
			this.application = null;
		}
        this.node.empty();
    },
    show: function(){
        this.loadApplication()
    },
    "delete": function(e){
        var module = this;
        this.page.designer.shortcut = false;
        this.page.designer.confirm("warn", module.node, MWF.APPPOD.LP.notice.deleteElementTitle, MWF.APPPOD.LP.notice.deleteElement, 300, 120, function(){
            if (this.queryGetPageDataFun) module.page.removeEvent("queryGetPageData", this.queryGetPageDataFun);
            if (this.postGetPageDataFun) module.page.removeEvent("postGetPageData", this.postGetPageDataFun);

            module.destroy();
            module.page.selected();

            module.page.designer.shortcut = true;
            this.close();
        }, function(){
            module.page.designer.shortcut = true;
            this.close();
        }, null);
    },
    refreshApplication: function(){
		this.loadApplication()
    },
	loadIcon: function(){
        this.iconNode = new Element("div", {
            "styles": this.css.iconNode
        }).inject(this.node);
        new Element("div", {
            "styles": this.css.iconNodeIcon
        }).inject(this.iconNode);
        new Element("div", {
            "styles": this.css.iconNodeText,
            "text": "Widget"
        }).inject(this.iconNode);
	},

	_loadNodeStyles: function(){
		this.iconNode = this.node.getElement("div").setStyles(this.css.iconNode);
		this.iconNode.getFirst("div").setStyles(this.css.iconNodeIcon);
		this.iconNode.getLast("div").setStyles(this.css.iconNodeText);
	},
    _setEditStyle_custom: function(name, input, oldValue){
		if (name==="componentSelected"){
			if (this.json.componentSelected!==oldValue){
				this.loadApplication();
			}
		}
        if (name==="componentType"){
		    if (this.json.componentType!==oldValue){
				this.loadApplication();
            }
        }
	},
	openApplication: function(e){
		if (this.json.componentSelected && this.json.componentSelected!=="none" && this.json.componentType!=="script"){
			layout.desktop.openApplication(e, this.json.componentSelected);
		}
	},
	loadApplication: function ( ) {
		if(this.node)this.node.empty();
		if(this.application){
			this.application.close();
			this.application = null;
		}
		var options = this.options.optionType === "map" ? this.options.optionsMapList : {};
		if (this.json.componentSelected && this.json.componentSelected!=="none" && this.json.componentType!=="script"){
			this._loadApplication( this.json.componentSelected, options )
		}else{
			this.loadIcon();
		}
	},
	_loadApplication: function ( app, options ) {
		try{
			debugger;
			MWF.xApplication[app] = MWF.xApplication[app] || {};
			MWF.xDesktop.requireApp(app, "lp."+o2.language, null, false);
			MWF.xApplication[app].options = MWF.xApplication[app].options || {};
			MWF.xDesktop.requireApp(app, "Main", null, false);
			var opt = options || {};
			opt.embededParent = this.node;
			this.application = new MWF.xApplication[app].Main(this.form.designer.desktop, opt);
			this.application.status = opt;
			this.application.load();
			this.application.setEventTarget(this.form.designer);
		}catch (e) {

		}
	},
    destroy: function(){
        this.form.moduleList.erase(this);
		this.form.moduleNodeList.erase(this.node);
		this.form.moduleElementNodeList.erase(this.node);

        if (this.form.scriptDesigner){
            this.form.scriptDesigner.removeModule(this.json);
		}

        if (this.property) this.property.destroy();
		this.node.destroy();
		this.actionArea.destroy();

		delete this.form.json.moduleList[this.json.id];
		this.json = null;
		delete this.json;

		this.treeNode.destroy();

        if( this.application ){
			this.application.close();
			this.application = null;
		}

		o2.release(this);
    }
});

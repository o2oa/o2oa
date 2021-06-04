MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.require("MWF.widget.Common", null, false);
MWF.xApplication.process.FormDesigner.Module.Form = MWF.FCForm = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Form/form.html",
        "mode": "PC",
		"fields": ["Calendar", "Checkbox", "Datagrid", "Datagrid$Title", "Datagrid$Data", "Datatable", "Datatable$Title", "Datatable$Data", "Datatemplate","Htmleditor", "Number", "Office", "Orgfield", "org", "Personfield", "Radio", "Select", "Textarea", "Textfield"],
		"injectActions" : [
			{
				"name" : "top",
				"styles" : "injectActionTop",
				"event" : "click",
				"action" : "injectTop",
				"title": MWF.APPFD.LP.formAction["insertTop"]
			},
			{
				"name" : "bottom",
				"styles" : "injectActionBottom",
				"event" : "click",
				"action" : "injectBottom",
				"title": MWF.APPFD.LP.formAction["insertBottom"]
			}
		]
	},
	
	initialize: function(designer, container, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Form/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Form/"+this.options.style+"/css.wcss";

		this._loadCss();
		
		this.container = null;
		this.form = this;
        this.moduleType = "form";
		
		this.moduleList = [];
		this.moduleNodeList = [];
		
		this.moduleContainerNodeList = [];
		this.moduleElementNodeList = [];
		this.moduleComponentNodeList = [];

	//	this.moduleContainerList = [];
		this.dataTemplate = {};
		
		this.designer = designer;
		this.container = container;
		
		this.selectedModules = [];
	},
    reload: function(data){
        this.moduleList.each(function(module){
            if (module.property){
                module.property.destroy();
            }
        }.bind(this));
        if (this.property) this.property.destroy();
        this.property = null;

        this.moduleList = [];
        this.moduleNodeList = [];
        this.moduleContainerNodeList = [];
        this.moduleElementNodeList = [];
        this.moduleComponentNodeList = [];
        this.dataTemplate = {};
        this.selectedModules = [];
        this.container.empty();

        if (this.treeNode){
            this.domTree.empty();
            this.domTree.node.destroy();
            this.domTree = null;
            this.treeNode = null;
        }
        this.currentSelectedModule = null;
        this.propertyMultiTd = null;

        if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        this.load(data);
        this.selected();
    },

	load : function(data){
		debugger;
		this.data = data;
		this.json = data.json;
		this.html = data.html;
		this.json.mode = this.options.mode;
		if (!this.json.css) this.json.css = {"code":""};

		if (this.options.mode==="Mobile"){
			if (!this.json.defaultTools){
				this.json.defaultTools = o2.JSON.get(this.path+"toolbars.json", null,false);
			}
			if (!this.json.tools) this.json.tools=[];
		}

		this.isNewForm = (this.json.id) ? false : true;
		if (this.isNewForm) this.checkUUID();
		if(this.designer.application) this.data.json.applicationName = this.designer.application.name;
		if(this.designer.application) this.data.json.application = this.designer.application.id;

		this.container.set("html", this.html);
		this.loadStylesList(function(){
			var formStyleType = this.json.formStyleType;
			if( typeOf( formStyleType ) === "object" && formStyleType.type === "script"  ){ //如果是自定义表单样式
				this.loadCustomTemplateStyles( formStyleType, function ( templateStyles ) {
					this._load( templateStyles );
				}.bind(this))
			}else {
				if( typeOf( formStyleType ) === "object" )formStyleType = formStyleType.id;

				var oldStyleValue = "";
				if ((!formStyleType) || !this.stylesList[formStyleType]){
					this.json.formStyleType = "blue-simple";
					formStyleType = "blue-simple";
				}
				if (this.options.mode == "Mobile") {
					if ( formStyleType != "defaultMobile") {
						var styles = this.stylesList[formStyleType];
						if (!styles || typeOf(styles.mode) !== "array" || !styles.mode.contains("mobile")) {
							oldStyleValue = formStyleType;
							this.json.formStyleType = "defaultMobile";
							formStyleType = "defaultMobile";
						}
					}
				}

				this.loadTemplateStyles(this.stylesList[formStyleType].file, this.stylesList[formStyleType].extendFile, function (templateStyles) {
					//this.templateStyles = (this.stylesList && this.json.formStyleType) ? this.stylesList[this.json.formStyleType] : null;
					this._load(templateStyles, oldStyleValue);
				}.bind(this));
			}
		}.bind(this));
	},
	_load : function( templateStyles, oldStyleValue ){
		this.templateStyles = templateStyles;
		this.loadDomModules();

		if (this.json.formStyleType && this.templateStyles && this.templateStyles["form"]){
			this.setTemplateStyles(this.templateStyles["form"]);
		}

		this.setCustomStyles();
		this.node.setProperties(this.json.properties);

		this.setNodeEvents();

		if (this.options.mode=="Mobile"){
			if (oldStyleValue) this._setEditStyle("formStyleType", null, oldStyleValue);
		}

		this.selected();
		this.autoSave();
		this.designer.addEvent("queryClose", function(){
			if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
		}.bind(this));

		this.designer.fireEvent("postFormLoad");
	},
    removeStyles: function(from, to){
        if (this.json[to]){
            Object.each(from, function(style, key){
                if (this.json[to][key] && this.json[to][key]==style){
                    delete this.json[to][key];
                }
            }.bind(this));
        }
    },
    copyStyles: function(from, to){
        if (!this.json[to]) this.json[to] = {};
        Object.each(from, function(style, key){
            if (!this.json[to][key]) this.json[to][key] = style;
        }.bind(this));
    },
    clearTemplateStyles: function(styles){
        if (styles){
            if (styles.styles) this.removeStyles(styles.styles, "styles");
            if (styles.properties) this.removeStyles(styles.properties, "properties");
        }
		if( this.json.confirmStyle )delete this.json.confirmStyle;
		if( this.json.dialogStyle )delete this.json.dialogStyle;
		if( this.json.attachmentStyle )delete this.json.attachmentStyle;
		if( this.json.hideModuleIcon )delete this.json.hideModuleIcon;
		if( this.json.nodeStyleWithhideModuleIcon )delete this.json.nodeStyleWithhideModuleIcon;
		if( this.json.confirmIcon )delete this.json.confirmIcon;
		if( this.json.submitedDlgUseNotice )delete this.json.submitedDlgUseNotice;
		if( this.json.submitedDlgStyle )delete this.json.submitedDlgStyle;
		if( this.json.selectorStyle )delete this.json.selectorStyle;
		if( this.json.errorStyle )delete this.json.errorStyle;
		if( this.json.noticeStyle )delete this.json.noticeStyle;
		if( this.json.noticeErrorStyle )delete this.json.noticeErrorStyle;
		if( this.json.noticeSuccessStyle )delete this.json.noticeSuccessStyle;
		if( this.json.noticeOkStyle )delete this.json.noticeOkStyle;
		if( this.json.noticeNoticeStyle )delete this.json.noticeNoticeStyle;
    },
    setTemplateStyles: function(styles){
        if (styles.styles) this.copyStyles(styles.styles, "styles");
        if (styles.properties) this.copyStyles(styles.properties, "properties");
		//if( styles.confirmStyle )this.json.confirmStyle = styles.confirmStyle;
		//if( styles.dialogStyle )this.json.dialogStyle = styles.dialogStyle;
		//if( styles.selectorStyle )this.json.selectorStyle = styles.selectorStyle;
    },
	loadCustomTemplateStyles : function( scriptObject, callback ){
		// {
		// 	"type" : "script",
		// 	"name": script.name,
		// 	"alias": script.alias,
		// 	"id": script.id,
		// 	"appName" : script.appName || script.applicationName,
		// 	"appId": script.appId,
		// 	"application": script.application
		// }
		this.designer.actions.getScriptByName(  scriptObject.name, scriptObject.application,  function( json ) {
			try{
				var f = eval("(function(){\n return "+json.data.text+"\n})");
				var j = f();
				if( typeOf(j) !== "object" ){
					this.designer.notice( MWF.APPFD.LP.notValidJson, "error" );
				}else{
					if(callback)callback(j);
				}
			}catch (e) {
				this.designer.notice( e.message, "error" )
			}
		}.bind(this), function( responseJSON ){
			this.designer.notice( JSON.parse(responseJSON.responseText).message, "error" );
			if(callback)callback({});
		}.bind(this))
	},
	loadTemplateStyles : function( file, extendFile, callback ){
		if( !file ){
			if (callback) callback({});
			return;
		}
		this.templateStylesList = this.templateStylesList || {};
		if( this.templateStylesList[file] ){
			if (callback) callback(this.templateStylesList[file]);
			return;
		}
		this.loadTemplateStyleFile( file, function( json_file ){
			this.loadTemplateExtendStyleFile( extendFile, function( json_extend ){
				this.templateStylesList[file] = Object.merge( json_file, json_extend );
				if (callback) callback(this.templateStylesList[file]);
			}.bind(this))
		}.bind(this))

	},
	loadTemplateStyleFile : function(file, callback ){
		if( !file ){
			if (callback) callback({});
			return;
		}
		var stylesUrl = "../x_component_process_FormDesigner/Module/Form/skin/"+file;
		MWF.getJSON(stylesUrl,{
				"onSuccess": function(responseJSON){
					//this.templateStylesList[file] = responseJSON;
					if (callback) callback(responseJSON);
				}.bind(this),
				"onRequestFailure": function(){
					if (callback) callback({});
				}.bind(this),
				"onError": function(){
					if (callback) callback({});
				}.bind(this)
			}
		);
	},
	loadTemplateExtendStyleFile : function(extendFile, callback ){
		if( !extendFile ){
			if (callback) callback({});
			return;
		}
		var stylesUrl = "../x_component_process_FormDesigner/Module/Form/skin/"+extendFile;
		MWF.getJSON(stylesUrl,{
				"onSuccess": function(responseJSON){
					//this.templateStylesList[file] = responseJSON;
					if (callback) callback(responseJSON);
				}.bind(this),
				"onRequestFailure": function(){
					if (callback) callback({});
				}.bind(this),
				"onError": function(){
					if (callback) callback({});
				}.bind(this)
			}
		);
	},
    loadStylesList: function(callback){
        //var stylesUrl = "../x_component_process_FormDesigner/Module/Form/template/"+((this.options.mode=="Mobile") ? "mobileStyles": "styles")+".json";
        //var stylesUrl = "../x_component_process_FormDesigner/Module/Form/template/"+((this.options.mode=="Mobile") ? "styles": "styles")+".json";
		if( this.stylesList ){
			callback( this.stylesList )
		}else{
			var configUrl = "../x_component_process_FormDesigner/Module/Form/skin/config.json";
			MWF.getJSON(configUrl,{
					"onSuccess": function(responseJSON){
						this.stylesList = responseJSON;
						if (callback) callback(this.stylesList);
					}.bind(this),
					"onRequestFailure": function(){
						this.stylesList = {};
						if (callback) callback(this.stylesList);
					}.bind(this),
					"onError": function(){
						this.stylesList = {};
						if (callback) callback(this.stylesList);
					}.bind(this)
				}
			);
		}
    },
    autoSave: function(){
        this.autoSaveCheckNode = this.designer.formToolbarNode.getElement("#MWFFormAutoSaveCheck");
        if (this.autoSaveCheckNode){
            this.autoSaveTimerID = window.setInterval(function(){
                if (this.autoSaveCheckNode.get("checked")){
                    this.save();
                }
            }.bind(this), 60000);
        }
    },
	checkUUID: function(){
		this.designer.actions.getUUID(function(id){
            this.json.id = id;
        }.bind(this));
	},
	loadDomModules: function(){
		this.node = this.container.getFirst();
		this.node.set("id", this.json.id);
		this.node.setStyles((this.options.mode==="Mobile") ? this.css.formMobileNode : this.css.formNode);
		this.node.store("module", this);

        var id = this.json.id.replace(/\-/g, "");

		( this.node.get("class") || "" ).split(" ").each(function(className){
			if( className.indexOf("css") === 0 && className.length === 35 ){
				this.node.removeClass(className);
			}
		}.bind(this));

        this.node.addClass("css"+id);

        this.reloadCss();

		var y = this.container.getStyle("height");
		y = (y) ? y.toInt()-2 : this.container.getSize().y-2;
		this.node.setStyle("min-height", ""+y+"px");
		this.designer.addEvent("resize", function(){
            var y = this.container.getStyle("height");
            y = (y) ? y.toInt()-2 : this.container.getSize().y-2;
			this.node.setStyle("min-height", ""+y+"px");
		}.bind(this));

		this.loadDomTree();
	},
	
	loadDomTree: function(){
		MWF.require("MWF.widget.Tree", function(){
			this.domTree = new MWF.widget.Tree(this.designer.propertyDomArea, {"style": "domtree"});
			this.domTree.load();
			
			this.createFormTreeNode();
			this.parseModules(this, this.node);
		}.bind(this));
	},
	createFormTreeNode: function(){
        var text = "<"+this.json.type+"> "+this.json.name+" ["+this.options.mode+"] ";
		var o = {
			"expand": true,
			"title": this.json.id,
			"text": "<"+this.json.type+"> "+this.json.name+" ["+this.options.mode+"] ",
			"icon": (this.options.mode=="Mobile") ? "mobile.png": "pc.png"
		};
		o.action = function(){
			if (this.module) this.module.selected();
		};
		this.treeNode = this.domTree.appendChild(o);
        this.treeNode.setText(text);
		this.treeNode.module = this;
	},

	getModuleNodes: function (dom, ignoreMultipleModule) {
		var moduleNodes = [];
		var subDom = dom.getFirst();
		while (subDom) {
			var mwftype = subDom.get("MWFtype") || subDom.get("mwftype");
			if (mwftype) {
				if( ignoreMultipleModule ){
					var type = mwftype;
					if ( type.indexOf("$") === -1)moduleNodes.push(subDom);
				}else{
					moduleNodes.push(subDom);
				}
				moduleNodes = moduleNodes.concat(this.getModuleNodes(subDom));
			} else {
				moduleNodes = moduleNodes.concat(this.getModuleNodes(subDom));
			}
			subDom = subDom.getNext();
		}
		return moduleNodes;
	},

	parseModules: function(parent, dom){
		var moduleNodes = [];
		var subDom = dom.getFirst();
		while (subDom){
			if (subDom.get("MWFtype")){
//				var module = subDom.retrieve("module");
//				alert(subDom.get("id")+": "+module);
//				if (!module){
					var json = this.getDomjson(subDom);
					var moduleNode = subDom;
				moduleNodes.push({"dom": moduleNode, "json": json});
					//module = this.loadModule(json, subDom, parent);
//				}
//                if (module.moduleType=="container") this.parseModules(module, subDom);
//			}else{
//				this.parseModules(parent, subDom);
			}
//			else if (subDom.getFirst()){
//				subDom = subDom.getFirst();
//				this.parseModules(parent, subDom);
//			}else{
//				subDom = subDom.getNext();
//			}
			subDom = subDom.getNext();
		}

		moduleNodes.each(function(obj){
			module = this.loadModule(obj.json, obj.dom, parent);
		}.bind(this));
	},
	
	getDomjson: function(dom){
		var mwfType = dom.get("MWFtype");
		switch (mwfType) {
			case "form":
				return this.json;
			case "":
				return null;
			default:
				var id = dom.get("id");
				if (id){
					return this.json.moduleList[id];
				}else{
					return null;
				}
		}
	},
	
	loadModule: function(json, dom, parent){
		if( !json ){
			var module;
			var className = ( dom.get("MWFType") || "div" ).capitalize();
			this.getTemplateData(className, function(data){
				var moduleData = Object.clone(data);
				moduleData.id = dom.get("id");
				this.json.moduleList[dom.get("id")] = moduleData;
				module = new MWF["FC"+className](this);
				module.load(moduleData, dom, parent);
			}.bind(this), false);
			return module;
		}else if( MWF["FC"+json.type] ){
			var module;
			var className = json.type.capitalize();
			this.getTemplateData(className, function(data){
				var moduleData = Object.clone(data);
				Object.merge(moduleData, json);
				Object.merge(json, moduleData);
				module = new MWF["FC"+json.type](this);
				module.load(json, dom, parent);
			}.bind(this), false);
			return module;
		}else{
			var module;
			var className = json.type.capitalize();
			this.getTemplateData(className, function(data){
				var moduleData = Object.clone(data);
				Object.merge(moduleData, json);
				Object.merge(json, moduleData);
				module = new MWF["FCDiv"](this);
				module.load(json, dom, parent);
			}.bind(this), false);
			return module;
		}
	},
	
	setNodeEvents: function(){
		this.node.addEvent("click", function(e){
			this.selected();
		}.bind(this));
		// this._controlKeyEventFun = function(e){
		// 	if (e.control){
		// 		this.controlMode = true;
		// 		if (this.copyNode) this.copyNode.destroy();
		// 	}else{
		// 		this.controlMode = false;
		// 		// var copyNode = this._getCopyNode(this);
		// 		// copyNode.inject(this.node, "before");
		// 	}
		// }.bind(this);
		this.designer.content.addEvent("keydown", function(e){
			if (this.moveModule){
				if (e.control){
					this.moveModule._setControlMode(true);
				}else{
					this.moveModule._setControlMode(false);
				}
			}
		}.bind(this));
		this.designer.content.addEvent("keyup", function(e){
			if (this.moveModule){
				if (e.control){
					this.moveModule._setControlMode(true);
				}else{
					this.moveModule._setControlMode(false);
				}
			}
		}.bind(this));
	},

	createModuleImmediately: function( className, parentModule, relativeNode, position, selectDisabled, async ){
		var module;
		this.getTemplateData(className, function(data){
			var moduleData = Object.clone(data);
			module = new MWF["PC"+className](this);
			if( parentModule ){
				module.onDragModule = parentModule;
				if (!parentModule.Component) module.inContainer = parentModule;
				module.parentContainer = parentModule;
				module.nextModule = null;
			}
			module.createImmediately(moduleData, relativeNode, position, selectDisabled);
		}.bind(this), async);
		return module;
	},

	createModule: function(className, e){
		this.getTemplateData(className, function(data){
			var moduleData = Object.clone(data);
			var newTool = new MWF["FC"+className](this);
			newTool.create(moduleData, e);
		}.bind(this));
	},
	getTemplateData: function(className, callback , async){
		if (this.dataTemplate[className]){
			if (callback) callback(this.dataTemplate[className]);
		}else{
			var templateUrl = "../x_component_process_FormDesigner/Module/"+className+"/template.json";
			MWF.getJSON(templateUrl, function(responseJSON, responseText){
				this.dataTemplate[className] = responseJSON;
				if (callback) callback(responseJSON);
			}.bind(this), async);
		}
	},
	selected: function(){
		if (this.currentSelectedModule){
			if (this.currentSelectedModule==this){
				return true;
			}else{
				this.currentSelectedModule.unSelected();
			}
		}
        if (this.propertyMultiTd){
            this.propertyMultiTd.hide();
            this.propertyMultiTd = null;
        }
		this.unSelectedMulti();

		this.currentSelectedModule = this;
		
		if (this.treeNode){
			this.treeNode.selectNode();
		}
		
		this.showProperty();
    //    this.isFocus = true;
	},
	unSelectedMulti: function(){
		while (this.selectedModules.length){
			this.selectedModules[0].unSelectedMulti();
		}
		if (this.multimoduleActionsArea) this.multimoduleActionsArea.setStyle("display", "none");
	},
	unSelectAll: function(){
		
	},
	_beginSelectMulti: function(){
		if (this.currentSelectedModule) this.currentSelectedModule.unSelected();
		this.unSelectedMulti();
		this.noSelected = true;
	},
	_completeSelectMulti: function(){
		if (this.selectedModules.length<2){
			this.selectedModules[0].selected();
		}else{
			this._showMultiActions();
		}
	},
	createMultimoduleActionsArea: function(){
		this.multimoduleActionsArea = new Element("div", {
			styles: {
				"display": "none",
		//		"width": 18*this.options.actions.length,
				"position": "absolute",
				"background-color": "#F1F1F1",
				"padding": "1px",
				"padding-right": "0px",
				"border": "1px solid #AAA",
				"box-shadow": "0px 2px 5px #999", 
				"z-index": 10001
			}
		}).inject(this.form.container, "after");
	},
	_showMultiActions: function(){
		if (!this.multimoduleActionsArea) this.createMultimoduleActionsArea();
		var firstModule = this._getFirstMultiSelectedModule();
		if (firstModule){
		//	var module = firstModule.module;
			var y = firstModule.position.y-25;
			var x = firstModule.position.x;
			this.multimoduleActionsArea.setPosition({"x": x, "y": y});
			this.multimoduleActionsArea.setStyle("display", "block");
		}
	},
	
	_getFirstMultiSelectedModule: function(){
		var firstModule = null;
		this.selectedModules.each(function(module){
			var position = module.node.getPosition(module.form.node.getOffsetParent());
			if (!firstModule){
				firstModule = {"module": module, "position": position};
			}else{
				if (position.y<firstModule.position.y){
					firstModule = {"module": module, "position": position};
				}else if (position.y==firstModule.position.y){
					if (position.x<firstModule.position.x){
						firstModule = {"module": module, "position": position};
					}
				}
			}
		});
		return firstModule;
	},
	
	
	showProperty: function(){
		if (!this.property){
			this.property = new MWF.xApplication.process.FormDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
				"path": this.options.propertyPath,
				"onPostLoad": function(){
					this.property.show();
				}.bind(this)
			});
			this.property.load();	
		}else{
			this.property.show();
		}
	},
    hideProperty: function(){
        if (this.property) this.property.hide();
    },
	
	unSelected: function(){
		this.currentSelectedModule = null;
        this.hideProperty();
	},
	
	_dragIn: function(module){
		if (!this.Component) module.inContainer = this;
		module.parentContainer = this;
		this.node.setStyles({"border": "1px solid #ffa200"});
		var copyNode = module._getCopyNode();
		copyNode.inject(this.node);
	},
	_dragOut: function(module){
		module.inContainer = null;
		module.parentContainer = null;
		this.node.setStyles((this.options.mode==="Mobile") ? this.css.formMobileNode : this.css.formNode);
		this.node.setStyles(this.json.styles);
		var copyNode = module._getCopyNode();
		copyNode.setStyle("display", "none");
	},
	_dragDrop: function(module, flag){
		var f = flag || !(new Event(event)).control;
		if( f ){
			this.node.setStyles((this.options.mode==="Mobile") ? this.css.formMobileNode : this.css.formNode);
			this.node.setStyles(this.json.styles);
		}

		//this._hideInjectAction();
	},
	_showInjectAction : function( module ){
		if ( module.moveNode ){
			module.moveNode.setStyle("display","none");
		}

		//debugger;
		this.draggingModule = module;
		//if( !this.node.getFirst() ){
		//	this.inject( "top" );
		//	return;
		//}

		if( !this.injectActionArea )this._createInjectAction();
		this.injectActionArea.setStyle("display","block");
		this._setInjectActionAreaPosition();

		this.injectActionEffect = new Fx.Morph(this.injectActionArea, {
			duration: 200,
			transition: Fx.Transitions.Sine.easeOut
		});
		this.injectActionEffect.start(this.form.css.injectActionArea_to);
	},
	_hideInjectAction : function(){
		this.draggingModule = null;
		if( this.injectActionArea ){
			this.injectActionArea.setStyle("display","none");
		}
	},
	_createInjectAction : function(){
		var css = this.form.css;
		if( !this.injectActionArea ){
			this.injectActionArea = new Element("div", { styles: css.injectActionArea }).inject(this.form.container, "after");

			this.injectActionTopBGNode = new Element("div", { styles : css.injectActionTopBGNode }).inject( this.injectActionArea );
			this.injectActionLeftBGNode = new Element("div", { styles : css.injectActionLeftBGNode }).inject( this.injectActionArea );
			this.injectActionRightBGNode = new Element("div", { styles : css.injectActionRightBGNode }).inject( this.injectActionArea );
			this.injectActionBottomBGNode = new Element("div", { styles : css.injectActionBottomBGNode }).inject( this.injectActionArea );

			var injectActions = {};
			this.options.injectActions.each( function( action ){
				injectActions[ action.name ] = action;
			});

			if( injectActions.before )this._createInjectActionNode( injectActions.before, this.injectActionTopBGNode );
			if( injectActions.top )this._createInjectActionNode( injectActions.top, this.injectActionLeftBGNode );
			if( injectActions.bottom )this._createInjectActionNode( injectActions.bottom, this.injectActionRightBGNode );
			if( injectActions.after )this._createInjectActionNode( injectActions.after, this.injectActionBottomBGNode );

			new Element("div", {
				styles : css.injectActionCancelNode,
				events : {
					click : function(){
						this.draggingModule._dragCancel();
						this._dragDrop( this.node, true );
						this._hideInjectAction();
					}.bind(this),
					mouseover : function(){
						this.setStyles( css.injectActionCancelNode_over )
					},
					mouseout : function(){
						this.setStyles( css.injectActionCancelNode )
					}
				}
			}).inject(this.injectActionArea);

		}
	},
	_createInjectActionNode : function( action, relativeNode ){
		var actionNode = new Element("div", {
			"styles": this.css[action.styles],
			"title": action.title
		}).inject( this.injectActionArea );
		actionNode.addEvent(action.event, function(e){
			this[action.action](e);
		}.bind(this));
		actionNode.addEvents({
			"mouseover": function(e){
				relativeNode.setStyle("background", "#ddd");
				this.draggingModule.copyNode.setStyle("display","");
				this.draggingModule.copyNode.inject( this.node, action.name );
			}.bind(this),
			"mouseout": function(e){
				relativeNode.setStyle("background", "transparent");
			}.bind(this)
		});
		relativeNode.set("title",action.title);
		relativeNode.addEvent(action.event, function(e){
			this[action.action](e);
		}.bind(this));
		relativeNode.setStyle("cursor","pointer");
		relativeNode.addEvents({
			"mouseenter": function(e){
				relativeNode.setStyle("background", "#ddd");
				this.draggingModule.copyNode.setStyle("display","");
				this.draggingModule.copyNode.inject( this.node, action.name );
			}.bind(this),
			"mouseleave": function(e){
				relativeNode.setStyle("background", "transparent");
				//this.draggingModule.copyNode.setStyle("display","none");
			}.bind(this)
		});
	},
	_setInjectActionAreaPosition: function(){
		var e = new Event(event);
		var formOffset = this.node.getOffsetParent().getPosition();
		//var p = this.node.getPosition(this.form.node.getOffsetParent());
		var y = e.page.y - formOffset.y - 60;
		var x = e.page.x - formOffset.x - 60;
		this.injectActionArea.setPosition({"x": x, "y": y});
	},
	injectBefore : function( e ){
		this.inject( "before" )
	},
	injectAfter : function( e ){
		this.inject( "after" )
	},
	injectTop : function( e ){
		this.inject( "top" )
	},
	injectBottom : function( e ){
		this.inject( "bottom" )
	},
	inject : function( position ){
		if ( this.draggingModule.moveNode ){
			this.draggingModule.moveNode.setStyle("display","");
		}
		this.draggingModule._dragComplete( this.node, position );
		this._dragDrop( this.node, true );
		this._hideInjectAction();
	},

	// _clearSubform: function(node){
	// 	var subNode = node.getFirst();
	// 	while (subNode){
	// 		var nextNode = subNode.getNext();
	// 		if (subNode.get("MWFType")){
	// 			if ( subNode.get("MWFType") === "subform" ){
	// 				subNode.destroy();
	// 			}else{
	// 				if (subNode) this._clearSubform(subNode);
	// 			}
	// 		}else{
	// 			if (subNode) this._clearSubform(subNode);
	// 		}
	// 		subNode = nextNode;
	// 	}
	// },
    _clearNoId: function(node){
        var subNode = node.getFirst();
        while (subNode){
            var nextNode = subNode.getNext();
            if (subNode.get("MWFType")){
                if (!subNode.get("id")){
                    subNode.destroy();
                }else{
                    if (subNode) this._clearNoId(subNode);
                }
            }else{
                if (subNode) this._clearNoId(subNode);
            }
            subNode = nextNode;
        }
    },

	_copyFormJson: function(initial, final){
		var data = final || {};
		Object.keys(initial).each(function(k){
			if (k!="defaultValue"){
					var t = typeOf(initial[k]);
					switch (t) {
						case "object":
							var s = JSON.stringify(initial[k], null, "\t");
							if (/((?:\:\s*)((\".+\")|(\d+)|(\[.+\])))/.test(s)){
								//data[k] = {};
								data[k] = this._copyFormJson(initial[k], data[k]);
							}
							break;
						case "boolean":
							data[k] = initial[k];
							break;
						default :
							if (initial[k]) data[k] = initial[k];
					}
				}else{
					data[k] = initial[k];
			}

		}.bind(this));
		return data;
	},
	_preprocessingModuleData: function(){
		//var html = this.node.innerHTML;
		this.moduleList.each(function(module){
			module._preprocessingModuleData();
		});
		// return {
		// 	"json": this.data.json,
		// 	"html": this.node.outerHTML
		// }
	},
	_recoveryModuleData: function(){
		this.moduleList.each(function(module){
			//module._recoveryModuleData();
			module.setCustomStyles();
			if (module.setCustomInputStyles) module.setCustomInputStyles();
		});
	},

	_getFormData: function(callback){
		debugger;
    	this.fireEvent("queryGetFormData");

    	this._preprocessingModuleData();

    	var copy = this.node.clone(true, true);
		copy.clearStyles();

		this.fireEvent("postGetFormData");

        this._clearNoId(copy);
        var html = copy.outerHTML;
		//this._clearNoDomModule();
		copy.destroy();

		this.data.json.mode = this.options.mode;
		this.data.html = html;

		debugger;
		var data = this._copyFormJson(this.data);

		if (data.json.styleConfig && data.json.styleConfig.extendFile){
			var stylesUrl = "../x_component_process_FormDesigner/Module/Form/skin/" + this.json.styleConfig.extendFile;
			MWF.getJSON(stylesUrl, function (responseJSON) {
					if (responseJSON && responseJSON.form) {
						data.json = Object.merge(data.json, responseJSON.form);
					}
					if (callback) callback();
				}.bind(this), false
			);
		}
		data.json["$version"] = "5.2";

		this._recoveryModuleData();

		//@todo 预先整理表单样式
		// var tmpFormNode = new Element("div", {
		// 	"opacity": 0
		// }).inject(this.designer.content);
		// MWF.xDesktop.requireApp("process.Xform2", "Form_", null, false);
		// this.appForm = new MWF.APPForm_(tmpFormNode, this.data, {
		// 	"onAfterLoad": function(){
		// 		var preprocessHtml = tmpFormNode.get("html");
		// 		this.data.preprocessHtml = preprocessHtml;
		//
		// 		if (callback) callback();
		// 	}.bind(this)
		// });
		// this.appForm.businessData = {};
		// this.appForm.load();

		return data;
		//return this.data;
	},
	_clearNoDomModule : function(){
		debugger;
		var existModuleList = {};
		Object.each(  this.moduleList, function( module ){
			existModuleList[ module.json.id ] = true;
		});
		Object.each( this.data.json.moduleList , function( module, key ){
			//if( !this.node.getElement( "#" + module.id ) && !existModuleList[ module.id ] ){
			if( !existModuleList[ module.id ] ){
				delete this.data.json.moduleList[key];
			}
		}.bind(this));
	},
	preview: function(){

        MWF.xDesktop.requireApp("process.FormDesigner", "Preview", function(){

            if (this.options.mode=="Mobile"){
                this.previewBox = new MWF.xApplication.process.FormDesigner.Preview(this, {"size": {"x": "400", "y": 580}, "mode": "mobile"});
            }else{
                this.previewBox = new MWF.xApplication.process.FormDesigner.Preview(this);
            }
            this.previewBox.load();

		}.bind(this));
	},
	save: function(callback){
		// debugger;
        // this.moduleList.each(function(module){
		// 	if (module.moduleName==="subform"){
		// 		module.refreshSubform();
		// 	}
		// }.bind(this));
        this.designer.saveForm();
		//this._getFormData();
		//this.designer.actions.saveForm(this.data, function(responseJSON){
		//	this.form.designer.notice(MWF.APPFD.LP.notice["save_success"], "ok", null, {x: "left", y:"bottom"});
        //
		//	//this.json.id = responseJSON.data;
		//	if (!this.json.name) this.treeNode.setText("<"+this.json.type+"> "+this.json.id);
		//	this.treeNode.setTitle(this.json.id);
		//	this.node.set("id", this.json.id);
		//
		//	if (callback) callback();
		//	//this.reload(responseJSON.data);
		//}.bind(this));
	},
    explode: function(){
        this._getFormData();
        MWF.require("MWF.widget.Base64", null, false);
        var data = MWF.widget.Base64.encode(JSON.encode(this.data));

        MWF.require("MWF.widget.Panel", function(){
            var node = new Element("div");
            var size = this.designer.formNode.getSize();
            var position = this.designer.formNode.getPosition(this.designer.formNode.getOffsetParent());

            var textarea = new Element("textarea", {
                "styles": {
                    "border": "1px solid #999",
                    "width": "770px",
                    "margin-left": "14px",
                    "margin-top": "14px",
                    "height": "580px"
                },
                "text": JSON.encode(this.data)
            }).inject(node);


            this.explodePanel = new MWF.widget.Panel(node, {
                "style": "form",
                "isResize": false,
                "isMax": false,
                "title": "",
                "width": 800,
                "height": 660,
                "top": position.y,
                "left": position.x+3,
                "isExpand": false,
                "target": this.designer.node
            });

            this.explodePanel.load();
        }.bind(this));

    },
    implode: function(){
        MWF.xDesktop.requireApp("portal.PageDesigner", "Import", function(){
            MWF.FormImport.create("O2", this);
        }.bind(this));

    },
    // implodeJsonData: function(str){
    //     if (str){
    //         //try{
    //         debugger;
    //         var data = JSON.decode(str);
    //         if (data){
    //             var json = data.json;
    //             data.id = this.data.id;
    //             data.isNewPage = this.data.isNewPage;
    //             json.id = this.json.id;
    //             json.name = this.json.name;
    //             json.application = this.json.application;
    //             json.applicationName = this.json.applicationName;
    //
    //             this.reload(data);
    //             this.implodePanel.closePanel();
    //         }else{
    //             this.designer.notice(this.designer.lp.implodeError, "error");
    //         }
    //         // }catch(e){
    //         //     this.designer.notice(this.designer.lp.implodeError, "error");
    //         // }
    //     }else{
    //         this.designer.notice(this.designer.lp.implodeEmpty, "error");
    //     }
    // },
    implodeHTML: function(){
        MWF.xDesktop.requireApp("portal.PageDesigner", "Import", function(){
            MWF.FormImport.create("html", this, {"type": "process"});
        }.bind(this));
    },
    implodeOffice: function(){
        MWF.xDesktop.requireApp("portal.PageDesigner", "Import", function(){
            MWF.FormImport.create("office", this);
        }.bind(this));
    },
	showFormVersion: function(){
		this.versionNode = new Element("div");
		this.dlg = o2.DL.open({
			"title": MWF.APPFD.LP.version["title"],
			"content": this.versionNode,
			"offset": {"y": -100},
			"isMax": false,
			"width": 500,
			"height": 300,
			"buttonList": [
				{
					"type": "cancel",
					"text": MWF.APPFD.LP.version["close"],
					"action": function(){ this.close(); }
				}
			],
			"onPostShow": function(){
				this.loadVersionList();
			}.bind(this),
			"onPostClose": function(){
				this.dlg = null;
			}.bind(this)
		});
	},
	loadVersionList : function(){
		var tableHtml = "<table width='100%' cellspacing='0' cellpadding='3' style='margin-top: 1px'><tr>" +
			"<th>"+MWF.APPFD.LP.version["no"]+"</th>" +
			"<th>"+MWF.APPFD.LP.version["updateTime"]+"</th>" +
			"<th>"+MWF.APPFD.LP.version["op"]+"</th>" +
			"</tr></table>";
		this.versionNode.set("html", tableHtml);
		this.versionTable = this.versionNode.getElement("table");
		this.action = o2.Actions.load("x_processplatform_assemble_designer");
		this.action.FormVersionAction.listWithForm(this.form.json.id, function(json){
			this.versionList = json.data;
			this.versionList.each(function (version,index) {
				var node = new Element("tr").inject(this.versionTable);
				var html = "<td>"+(index+1)+"</td>" +
					"<td>"+version.updateTime+"</td>" +
					"<td></td>";
				node.set("html", html);
				var actionNode = new Element("div",{"styles":{
						"width": "60px",
						"padding": "0px 3px",
						"border-radius": "20px",
						"cursor" : "pointer",
						"color": "#ffffff",
						"background-color": "#4A90E2",
						"float": "left",
						"margin-right": "2px",
						"text-align": "center",
						"font-weight": "100"
					}}).inject(node.getLast("td"));
				actionNode.set("text", MWF.APPFD.LP.version["resume"]);
				actionNode.addEvent("click",function (e) {
					var _self = this;
					this.designer.confirm("warn", e,  MWF.APPFD.LP.version["resumeConfirm"], MWF.APPFD.LP.version["resumeInfo"], 460, 120, function(){
						_self.resumeForm(version);
						this.close();
					}, function(){
						this.close();
					});
				}.bind(this));
			}.bind(this))
		}.bind(this));
	},
	resumeForm : function(version){
		this.action.FormVersionAction.get(version.id, function( json ){
			var formData = JSON.parse(json.data.data);
			//this.action.FormAction.update(version.form, formData,function( json ){
				this.designer.notice(MWF.APPFD.LP.version["resumeSuccess"]);
				this.reload(JSON.decode(MWF.decodeJsonString(formData.data)));
				this.dlg.close();
			//}.bind(this), null, false);
		}.bind(this), null, false);
	},
	deletePropertiesOrStyles: function(name, key){
		if (name=="styles"){
			try{
				if( key && this.json.styles[key] ){
					delete this.json.styles[key];
				}
				this.setCustomStyles();
			}catch(e){}
		}
		if (name=="properties"){
			try{
				this.node.removeProperty(key);
			}catch(e){}
		}
	},
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			this.setCustomStyles();
		}
		if (name=="properties"){
			this.node.setProperties(this.json.properties);
		}
	},
	setCustomStyles: function(){
		var border = this.node.getStyle("border");
		this.node.clearStyles();
		this.node.setStyles((this.options.mode==="Mobile") ? this.css.formMobileNode : this.css.formNode);
        var y = this.container.getStyle("height");
        y = (y) ? y.toInt()-2 : this.container.getSize().y-2;
		this.node.setStyle("min-height", ""+y+"px");
		
		if (this.initialStyles) this.node.setStyles(this.initialStyles);
		this.node.setStyle("border", border);

		Object.each(this.json.styles, function(value, key){
			var reg = /^border\w*/ig;
			if (!key.test(reg)){
				this.node.setStyle(key, value);
			}
		}.bind(this));
	},
	_setEditStyle: function(name, obj, oldValue){
		if (name=="name"){
			var title = this.json.name || this.json.id;
			this.treeNode.setText("<"+this.json.type+"> "+title+" ["+this.options.mode+"] ");
		}
		if (name=="id"){
			if (!this.json.name) this.treeNode.setText("<"+this.json.type+"> "+this.json.id+" ["+this.options.mode+"] ");
			this.treeNode.setTitle(this.json.id);
			this.node.set("id", this.json.id);
		}
        if ( name=="formStyleType" ){

			var loadOldTemplateStyle = function () {
				if( typeOf(oldValue) === "object" && oldValue.type === "script" ){ //如果原来是自定义表单样式
					this.loadCustomTemplateStyles( oldValue , function (oldTemplateStyles) {
						this.switchTemplateStyles( oldTemplateStyles );
					}.bind(this))
				}else{
					var oldFile, oldExtendFile;
					if( typeOf(oldValue) === "object" )oldValue === oldValue.id;
					if( oldValue && this.stylesList[oldValue] ){
						oldFile = this.stylesList[oldValue].file;
						oldExtendFile = this.stylesList[oldValue].extendFile;
					}
					this.loadTemplateStyles( oldFile, oldExtendFile, function( oldTemplateStyles ){
						this.switchTemplateStyles( oldTemplateStyles );
					}.bind(this))
				}
			}.bind(this);

			var formStyleType = this.json.formStyleType;
			if( typeOf(formStyleType) === "object" && formStyleType.type === "script" ){
				this.loadCustomTemplateStyles( formStyleType , function (templateStyles) {
					this.templateStyles = templateStyles;
					loadOldTemplateStyle();
					this.json.styleConfig = formStyleType;
				}.bind(this))
			}else{
				if( typeOf(formStyleType) === "object" )formStyleType = formStyleType.id;

				var file = (this.stylesList && formStyleType) ? this.stylesList[formStyleType].file : null;
				var extendFile = (this.stylesList && formStyleType) ? this.stylesList[formStyleType].extendFile : null;
				this.loadTemplateStyles( file, extendFile, function( templateStyles ){
					this.templateStyles = templateStyles;
					loadOldTemplateStyle();
					this.json.styleConfig = (this.stylesList && formStyleType) ? this.stylesList[formStyleType] : null;
				}.bind(this))
			}
        }
        if (name==="css"){
            this.reloadCss();
        }
		this._setEditStyle_custom(name, obj, oldValue);
	},
	switchTemplateStyles : function( oldTemplateStyles ){
		if (oldTemplateStyles["form"]) this.clearTemplateStyles(oldTemplateStyles["form"]);
		if (this.templateStyles["form"]) this.setTemplateStyles(this.templateStyles["form"]);

		this.setAllStyles();

		this.moduleList.each(function(module){
			if (oldTemplateStyles[module.moduleName]){
				module.clearTemplateStyles(oldTemplateStyles[module.moduleName]);
			}
			module.setStyleTemplate();
			module.setAllStyles();
		}.bind(this));
	},

    parseCSS: function(css){
        var rex = /(url\(.*\))/g;
        var match;
        while ((match = rex.exec(css)) !== null) {
            var pic = match[0];
            var len = pic.length;
            var s = pic.substring(pic.length-2, pic.length-1);
            var n0 = (s==="'" || s==="\"") ? 5 : 4;
            var n1 = (s==="'" || s==="\"") ? 2 : 1;
            pic = pic.substring(n0, pic.length-n1);

            if ((pic.indexOf("x_processplatform_assemble_surface")!=-1 || pic.indexOf("x_portal_assemble_surface")!=-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                if (pic.indexOf("/x_processplatform_assemble_surface")!==-1){
                    pic = pic.replace("/x_processplatform_assemble_surface", pic+"/x_processplatform_assemble_surface");
                }else if (pic.indexOf("x_processplatform_assemble_surface")!==-1){
                    pic = pic.replace("x_processplatform_assemble_surface", pic+"/x_processplatform_assemble_surface");
                }
                if (pic.indexOf("/x_portal_assemble_surface")!==-1){
                    pic = pic.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (pic.indexOf("x_portal_assemble_surface")!==-1){
                    pic = pic.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
				pic = o2.filterUrl(pic);
            }
            pic = "url('"+pic+"')";
            var len2 = pic.length;

            css = css.substring(0, match.index) + pic + css.substring(rex.lastIndex, css.length);
            rex.lastIndex = rex.lastIndex + (len2-len);
        }
        return css;
    },
    reloadCss: function(){
        var cssText = (this.json.css) ? this.json.css.code : "";
        //var head = (document.head || document.getElementsByTagName("head")[0] || document.documentElement);

        var styleNode = $("style"+this.json.id);
        if (styleNode) styleNode.destroy();
        if (cssText){
            cssText = this.parseCSS(cssText);
            var rex = new RegExp("(.+)(?=\\{)", "g");
            var match;
            var id = this.json.id.replace(/\-/g, "");
			var prefix = ".css" + id + " ";

            while ((match = rex.exec(cssText)) !== null) {
                // var rule = prefix + match[0];
                // cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                // rex.lastIndex = rex.lastIndex + prefix.length;

				var rulesStr = match[0];
				if (rulesStr.indexOf(",")!=-1){
					var rules = rulesStr.split(/\s*,\s*/g);
					rules = rules.map(function(r){
						return prefix + r;
					});
					var rule = rules.join(", ");
					cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
					rex.lastIndex = rex.lastIndex + (prefix.length*rules.length);

				}else{
					var rule = prefix + match[0];
					cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
					rex.lastIndex = rex.lastIndex + prefix.length;
				}
            }

            var styleNode = document.createElement("style");
            styleNode.setAttribute("type", "text/css");
            styleNode.id="style"+this.json.id;
            styleNode.inject(this.container, "before");

            if(styleNode.styleSheet){
                var setFunc = function(){
                    styleNode.styleSheet.cssText = cssText;
                };
                if(styleNode.styleSheet.disabled){
                    setTimeout(setFunc, 10);
                }else{
                    setFunc();
                }
            }else{
                var cssTextNode = document.createTextNode(cssText);
                styleNode.appendChild(cssTextNode);
            }
        }
    },
    setAllStyles: function(){
        this.setPropertiesOrStyles("styles");
        this.setPropertiesOrStyles("properties");
        this.reloadMaplist();
    },
    reloadMaplist: function(){
        if (this.property) Object.each(this.property.maplists, function(map, name){ map.reload(this.json[name]);}.bind(this));
    },
	_setEditStyle_custom: function(){
    },
    saveAsTemplete: function(){

    },
	isModuleExited : function( id ){
		for( var i=0; i<this.moduleList.length; i++ ){
			if(this.moduleList[i].json.id === id)return true;
		}
		return false;
	},
	checkModuleId: function(id, type, currentSubform){
    	var fieldConflict = false;
        var elementConflict = false;
        //if (this.json.moduleList[id]){
		if( this.isModuleExited(id) ){
            elementConflict = true;
        	if (this.options.fields.indexOf(type)!=-1 || this.options.fields.indexOf(this.json.moduleList[id].type)!=-1){
                fieldConflict = true;
			}
			return {"fieldConflict": fieldConflict, "elementConflict": elementConflict};
		}
		//if (this.subformList){
		//	Object.each(this.subformList, function(subform){
		//		if (!currentSubform || currentSubform!=subform.id){
		//			if (subform.moduleList[id]){
		//				elementConflict = true;
		//				if (this.options.fields.indexOf(type)!=-1 || this.options.fields.indexOf(subform.moduleList[id].type)!=-1){
		//					fieldConflict = true;
		//				}
		//			}
		//		}
		//	}.bind(this));
		//}
		var subformList = this.getAllSubformJsonObject();
		if (subformList){
			Object.each(subformList, function(subform){
				if (!currentSubform || currentSubform!=subform.id){
					if (subform.moduleList[id]){
						elementConflict = true;
						if (this.options.fields.indexOf(type)!=-1 || this.options.fields.indexOf(subform.moduleList[id].type)!=-1){
							fieldConflict = true;
						}
					}
				}
			}.bind(this));
		}
        return {"fieldConflict": fieldConflict, "elementConflict": elementConflict};
	},
    _resetTreeNode: function(){},

	clearSubformList : function( level1subformName ){
		if( !this.level1Subformlist )return;
		if( !this.level1Subformlist[level1subformName] )return;
		delete this.level1Subformlist[level1subformName];
	},
	addSubformList : function( level1subformName, addedSubformId ){
		if( !this.level1Subformlist ){
			this.level1Subformlist = {};
		}
		if( !this.level1Subformlist[level1subformName] ){
			this.level1Subformlist[level1subformName] = [];
		}
		this.level1Subformlist[level1subformName].push( addedSubformId );
	},
	isSubformUnique : function( checkedSubformId, level1subformName,  deletedSubformId){
		if( !this.level1Subformlist )return true;
		var level1Subformlist = Object.clone( this.level1Subformlist );
		if( deletedSubformId && level1Subformlist[deletedSubformId] )delete level1Subformlist[deletedSubformId];
		for( var key in level1Subformlist ){
			if( key !== level1subformName ){
				if( level1Subformlist[key].contains( checkedSubformId ) ){
					return false;
				}
			}
		}
		return true;
	},
	getAllSubformTiled : function(){
		var _nestToTiled = function( form , array ){
			if ( form.subformModuleList && form.subformModuleList.length){
				Array.each( form.subformModuleList, function( module ){
					array.push( module );
					if( module.subformModule )_nestToTiled( module.subformModule, array );
				}.bind(this))
			}
		};
		var array = [];
		_nestToTiled( this, array );
		return array;
	},
	getAllSubformJsonObject : function(){
		var list = this.getAllSubformTiled();
		var object = {};
		Array.each( list, function( subform ){
			if( subform && subform.json.subformSelected  && subform.subformData && subform.subformData.json){
				object[ subform.json.subformSelected ] = subform.subformData.json;
			}
		}.bind(this));
		return object;
	}
	// getAllFieldModuleNameList: function(){
    	// var moduleNameList = [];
    	// Object.each(this.json.moduleList, function(o, k){
    	// 	if (this.options.fields.indexOf(o.type))
	// 	}.bind(this))
	// }
	
});

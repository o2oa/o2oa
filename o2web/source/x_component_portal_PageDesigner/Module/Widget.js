MWF.xApplication.portal.PageDesigner.Module = MWF.xApplication.portal.PageDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.portal.PageDesigner.Module.Widget = MWF.PCWidget = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Widget/widget.html",
        "actions": [
            {
                "name": "move",
                "icon": "move1.png",
                "event": "mousedown",
                "action": "move",
                "title": MWF.APPPD.LP.formAction.move
            },
            {
                "name": "delete",
                "icon": "delete1.png",
                "event": "click",
                "action": "delete",
                "title": MWF.APPPD.LP.formAction["delete"]
            }
            // {
            //     "name": "styleBrush",
            //     "icon": "styleBrush.png",
            //     "event": "click",
            //     "action": "styleBrush",
            //     "title": MWF.APPPD.LP.formAction["styleBrush"]
            // }
        ]
	},
	
	initialize: function(page, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Widget/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Widget/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "widget";

		this.page = page;
        this.form = page;
		this.container = null;
		this.containerNode = null;
	},
	load : function(json, node, parent){

		this.json = json;
		this.node= node;
		this.node.store("module", this);

		//this.node.empty();

		this.node.setStyles(this.css.moduleNode);

		//this._loadNodeStyles();

		this._initModule();

        if (this.json.widgetSelected && this.json.widgetSelected!=="none" && this.json.widgetType!=="script"){
            this.redoSelectedWidget(this.json.widgetSelected, null, "");
        }else{
            this.node.empty();
            this.loadIcon();
        }

		this._loadTreeNode(parent);

        this.setCustomStyles();

		this.parentContainer = this.treeNode.parentNode.module;
        this._setEditStyle_custom("id");

		this.parseModules();
        this.json.moduleName = this.moduleName;
        this.node.addEvent("click", function(){
            this.refreshWidget();
        }.bind(this));
        this.node.addEvent("dblclick", function(e){
            this.openWidget(e);
        }.bind(this));
	},
    _initModule: function(){
        if (!this.json.isSaved) this.setStyleTemplate();

        this.setPropertiesOrStyles("styles");
        this.setPropertiesOrStyles("inputStyles");
        this.setPropertiesOrStyles("properties");

        this._setNodeProperty();
        if (!this.page.isWidget) this._createIconAction();
        this._setNodeEvent();
        this.json.isSaved = true;

        this.queryGetPageDataFun = this.queryGetPageData.bind(this);
        this.postGetPageDataFun = this.postGetPageData.bind(this);
        this.page.addEvent("queryGetPageData", this.queryGetPageDataFun);
        this.page.addEvent("postGetPageData", this.postGetPageDataFun);
    },

    openWidget: function(e){
        if (this.json.widgetSelected && this.json.widgetSelected!=="none" && this.json.widgetType!=="script"){
            layout.desktop.openApplication(e, "portal.WidgetDesigner", {"id": this.json.widgetSelected, "appId": "WidgetDesigner"+this.json.widgetSelected});
        }
    },
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "widget",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.page.container);
	},
	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.addEvent("selectstart", function(){
			return false;
		});
        // debugger;
        // if (this.json.widgetSelected && this.json.widgetSelected!="none" && this.json.widgetType!=="script"){
        //     this.redoSelectedWidget(this.json.widgetSelected, $(this.property.data.pid+"selectWidget").getElement("select"), "");
        // }else{
             this.loadIcon();
        // }
        this.node.addEvent("click", function(){
            this.refreshWidget();
        }.bind(this));


	},
    postGetPageData: function(node){
        if (!node || node.contains(this.node)) this.show();
    },
    queryGetPageData: function(node){
        if (!node || node.contains(this.node)) this.hide();
    },
    hide: function(){
        this.node.empty();
    },
    show: function(){
        if (this.widgetData ){
            this.widgetModule = new MWF.PCWidget.Page(this.page, this.node, {
                parentpageIdList : this.getParentpageIdList(),
                level : this.getLevel()
            });
            this.widgetModule.widgetSelector = this.getWidgetSelector();
            this.widgetModule.widgetSelectedValue = this.getWidgetSelectedValue();
            this.widgetModule.level1Widget = this.getLevel1Widget();
            this.widgetModule.load(this.widgetData);
        }else{
            this.node.empty();
            this.loadIcon();
        }
    },
    "delete": function(e){
        var module = this;
        this.page.designer.shortcut = false;
        this.page.designer.confirm("warn", module.node, MWF.APPPD.LP.notice.deleteElementTitle, MWF.APPPD.LP.notice.deleteElement, 300, 120, function(){
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
    getLevel : function(){
        return ( this.page.options.level1 || 0 ) + 1;
    },
    getLevel1Widget : function(){
        return this.page.level1Widget || this;
    },
    getWidgetSelector : function(){
        return this.widgetSelector || this.page.widgetSelector;
    },
    getWidgetSelectedValue : function(){
        return this.widgetSelectedValue || this.page.widgetSelectedValue;
    },
    checkWidgetNested : function( id ){
        if( this.page.options.parentpageIdList ){
            return !this.page.options.parentpageIdList.contains( id );
        }
        return true;
    },
    getParentpageIdList : function(){
        var parentpageIdList;
        if( this.page.options.parentpageIdList ){
            parentpageIdList = Array.clone( this.page.options.parentpageIdList );
            parentpageIdList.push( this.page.json.id )
        }else{
            parentpageIdList = [ this.page.json.id ];
        }
        return parentpageIdList;
    },
    refreshWidget: function(){
        if (this.json.widgetSelected && this.json.widgetSelected!=="none" && this.json.widgetType!=="script"){
            MWF.Actions.get("x_portal_assemble_designer").getWidget(this.json.widgetSelected, function(json){
                if (this.widgetData.updateTime!==json.data.updateTime){
                    var select = null;
                    if (this.property){
                        select = $(this.property.data.pid+"selectWidget").getElement("select");
                    }
                    this.clearWidgetList(this.json.widgetSelected);
                    this.reloadWidget(json.data, select, "");
                }
            }.bind(this));
        }
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
		if (name==="widgetSelected"){
			if (this.json.widgetSelected!==oldValue){
                this.redoSelectedWidget(name, input, oldValue);
			}
		}
        if (name==="widgetType"){
		    if (this.json.widgetType!==oldValue){
                if (this.json.widgetType !== "script"){
                    this.redoSelectedWidget(name, $(this.property.data.pid+"selectWidget").getElement("select"), "");
                }
                if (this.json.widgetType === "script"){
                    this.widgetData = null;
                    this.clearWidgetList(this.json.widgetSelected);
                    this.node.empty();
                    this.loadIcon();
                }
            }
        }
	},
    redoSelectedWidget: function(name, input, oldValue){
        if (this.json.widgetSelected==="none") this.json.widgetSelected="";
        if (this.json.widgetSelected && this.json.widgetSelected!=="none"){

            if(input)this.widgetSelector = input;
            if( !input )input = this.getWidgetSelector();

            if( oldValue )this.widgetSelectedValue = oldValue;
            if( !oldValue )oldValue = this.getWidgetSelectedValue() || "";

            var level1Widget = this.getLevel1Widget();

            if( !this.checkWidgetNested(this.json.widgetSelected) ){
                //var p = this.node.getPosition(document.body);
                //this.page.designer.alert("error", {
                //    "event": {
                //        "x": p.x + 150,
                //        "y": p.y + 80
                //    }
                //}, this.page.designer.lp.widgetNestedTitle, this.page.designer.lp.widgetNestedInfor, 400, 120);
                this.page.designer.notice( this.page.designer.lp.widgetNestedInfor, "error", level1Widget.node );
                level1Widget.json.widgetSelected = oldValue;
                if (input) {
                    for (var i = 0; i < input.options.length; i++) {
                        if (input.options[i].value === oldValue || (input.options[i].value==="none" && !oldValue ) ) {
                            input.options[i].set("selected", true);
                            break;
                        }
                    }
                }
                if( !oldValue ){
                    level1Widget.node.empty();
                    level1Widget.loadIcon();
                }else{
                    level1Widget.refreshWidget();
                }
            }else{
                MWF.Actions.get("x_portal_assemble_designer").getWidget(this.json.widgetSelected, function(json){
                    this.reloadWidget(json.data, input, oldValue);
                }.bind(this));
            }
        }else{
            this.widgetData = null;
            this.clearWidgetList(oldValue);
            this.node.empty();
            this.loadIcon();
        }
    },
    clearWidgetList: function(pageName){
        if (!this.page.widgetList) this.page.widgetList = {};
        if (pageName) if (this.page.widgetList[pageName]) delete this.page.widgetList[pageName];
    },
    addWidgetList: function(){
        if (!this.page.widgetList) this.page.widgetList = {};
        this.page.widgetList[this.json.widgetSelected] = Object.clone(this.widgetData.json);
    },
	getWidgetData: function(data){
        var widgetDataStr = null;
        if (this.page.options.mode !== "Mobile"){
            widgetDataStr = data.data;
        }else{
            widgetDataStr = data.mobileData;
        }
        this.widgetData = null;
        if (widgetDataStr){
            this.widgetData = JSON.decode(MWF.decodeJsonString(widgetDataStr));
            this.widgetData.updateTime = data.updateTime;
        }
	},
	reloadWidget: function(data, input, oldValue){
        this.getWidgetData(data);
		if (this.widgetData){
		    var oldWidgetData = (this.page.widgetList && oldValue) ? this.page.widgetList[oldValue] : null;
            this.clearWidgetList(oldValue);

            if (this.checkWidget(data, input)){
                this.node.empty();
                this.loadWidget();
                this.addWidgetList();
            }else{
                if (oldWidgetData){
                    if (!this.page.widgetList) this.page.widgetList = {};
                    this.page.widgetList[oldValue] = oldWidgetData;
                }else{
                    this.clearWidgetList(oldValue);
                    this.node.empty();
                    this.loadIcon();
                }

                this.json.widgetSelected = oldValue;
                if (!oldValue){
                    if (input) input.options[0].set("selected", true);
                }else{
                    if (input){
                        for (var i=0; i<input.options.length; i++){
                            if (input.options[i].value===oldValue){
                                input.options[i].set("selected", true);
                                break;
                            }
                        }
                    }
                }
            }
		}else{
            this.json.widgetSelected = oldValue;
            if (input){
                if (!oldValue){
                    input.options[0].set("selected", true);
                }else{
                    for (var i=0; i<input.options.length; i++){
                        if (input.options[i].value===oldValue){
                            input.options[i].set("selected", true);
                            break;
                        }
                    }
                }
            }
		}
	},

    regetWidgetData: function(){
	    var flag = false;
        if (this.json.widgetSelected && this.json.widgetSelected!=="none" && this.json.widgetType!=="script"){
            MWF.Actions.get("x_portal_assemble_designer").getWidget(this.json.widgetSelected, function(json){
                if (!this.widgetData || this.widgetData.updateTime!==json.data.updateTime){
                    this.getWidgetData(json.data);
                    flag = true;
                }
            }.bind(this), null, false);
        }
        return flag;
    },
    getConflictFields: function(){
        var moduleNames = [];
        if (this.widgetData){
            Object.each(this.widgetData.json.moduleList, function(o, key){
                var check = this.page.checkModuleId(key, o.type, this.widgetData.json.id);
                if (check.fieldConflict){
                    moduleNames.push(key)
                }else if (check.elementConflict){
                    o.changeId = this.json.id+"_"+key;
                }
            }.bind(this));
        }
        return moduleNames;
    },
    checkWidget: function(data, input){
        return true;

	    var moduleNames = this.getConflictFields();
        // Object.each(this.widgetData.json.moduleList, function(o, key){
        //     var check = this.page.checkModuleId(key, o.type, this.widgetData.json.id);
        //     if (check.fieldConflict){
        //         moduleNames.push(key)
        //     }else if (check.elementConflict){
        //         o.changeId = this.json.id+"_"+key;
        //     }
        // }.bind(this));
        if (moduleNames.length){
            var txt = this.page.designer.lp.widgetNameConflictInfor;
            txt = txt.replace("{name}", moduleNames.join(", "));
            //var p = (input) ? input.getPosition() : this.node.getPosition();
            // var p = this.node.getPosition(document.body);
            // this.page.designer.alert("error", {
            //     "event": {
            //         "x": p.x+150,
            //         "y": p.y+80
            //     }
            // }, this.page.designer.lp.widgetNameConflictTitle, txt, 400, 200);

            this.page.designer.notice(txt, "error", this.node);

            return false;
        }
		return true;
    },
	loadWidget: function(data) {
        this.widgetData.json.style = this.page.json.style;
        this.widgetData.json.properties = this.page.json.properties;
        this.widgetData.json.jsheader = {"code": "", "html": ""};
        this.widgetData.json.events = {};
        this.widgetData.json.pageStyleType = this.page.json.pageStyleType;
        //this.widgetData.json.id = this.json.id;

        this.widgetModule = new MWF.PCWidget.Page(this.page, this.node,{
            parentpageIdList : this.getParentpageIdList(),
            level : this.getLevel()
        });
        this.widgetModule.widgetSelector = this.getWidgetSelector();
        this.widgetModule.widgetSelectedValue = this.getWidgetSelectedValue();
        this.widgetModule.level1Widget = this.getLevel1Widget();
        this.widgetModule.load(this.widgetData);

       //this.createRefreshNode();
    },
    destroy: function(){
        this.page.moduleList.erase(this);
        this.page.moduleNodeList.erase(this.node);
        this.page.moduleElementNodeList.erase(this.node);
        this.clearWidgetList(this.json.widgetSelected);

        this.node.destroy();
        this.actionArea.destroy();

        delete this.page.json.moduleList[this.json.id];
        this.json = null;
        delete this.json;

        this.treeNode.destroy();
    }
});

MWF.xApplication.portal.PageDesigner.Module.Widget.Page = new Class({
    Extends: MWF.PCPage,
    initialize: function(page, container, options){
        this.setOptions(options);
        this.toppage = page.toppage || page;
    	this.parentpage = page;
        this.parentform = page;
        this.css = this.parentpage.css;

        this.container = container;
        this.form = this;
        this.page = this;
        this.isWidget = true;
        this.isSubform = true;
        this.moduleType = "widget";

        this.moduleList = [];
        this.moduleNodeList = [];

        this.moduleContainerNodeList = [];
        this.moduleElementNodeList = [];
        this.moduleComponentNodeList = [];

        //	this.moduleContainerList = [];
        this.dataTemplate = {};

        this.designer = this.parentpage.designer;
        this.selectedModules = [];
    },
    load : function(data){
        this.data = data;
        this.json = data.json;
        this.html = data.html;
        this.json.mode = this.options.mode;

        this.container.set("html", this.html);

        this.loadDomModules();
        //this.setCustomStyles();
        //this.node.setProperties(this.json.properties);
        //this.setNodeEvents();
        if (this.options.mode==="Mobile"){
            if (oldStyleValue) this._setEditStyle("pageStyleType", null, oldStyleValue);
        }
    },
    loadDomModules: function(){
        this.node = this.container.getFirst();
        this.node.set("id", this.json.id);
        this.node.setStyles((this.options.mode==="Mobile") ? this.css.pageMobileNode : this.css.pageNode);
        this.node.store("module", this);
        this.loadDomTree();
    },
    loadDomTree: function(){
        this.createPageTreeNode();
        this.parseModules(this, this.node);
    },
    createPageTreeNode: function(){
        this.treeNode = {
            "insertChild": function(){return this;},
            "appendChild": function(){return this;},
            "selectNode": function(){},
            "node": null,
            "parentNode": {}
        };
        this.treeNode.module = this;
    }
});
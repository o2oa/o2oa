MWF.xApplication.portal.PageDesigner.Module = MWF.xApplication.portal.PageDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.portal.PageDesigner.Module.Subpage = MWF.PCSubpage = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Subpage/subpage.html",
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
		
		this.path = "/x_component_portal_PageDesigner/Module/Subpage/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Subpage/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "subpage";

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

        if (this.json.subpageSelected && this.json.subpageSelected!=="none" && this.json.subpageType!=="script"){
            this.redoSelectedSubpage(this.json.subpageSelected, null, "");
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
            this.refreshSubpage();
        }.bind(this));
        this.node.addEvent("dblclick", function(e){
            this.openSubpage(e);
        }.bind(this));
	},
    _initModule: function(){
        if (!this.json.isSaved) this.setStyleTemplate();

        this.setPropertiesOrStyles("styles");
        this.setPropertiesOrStyles("inputStyles");
        this.setPropertiesOrStyles("properties");

        this._setNodeProperty();
        if (!this.page.isSubpage) this._createIconAction();
        this._setNodeEvent();
        this.json.isSaved = true;

        this.queryGetPageDataFun = this.queryGetPageData.bind(this);
        this.postGetPageDataFun = this.postGetPageData.bind(this);
        this.page.addEvent("queryGetPageData", this.queryGetPageDataFun);
        this.page.addEvent("postGetPageData", this.postGetPageDataFun);
    },

    openSubpage: function(e){
        if (this.json.subpageSelected && this.json.subpageSelected!=="none" && this.json.subpageType!=="script"){
            layout.desktop.openApplication(e, "portal.PageDesigner", {"id": this.json.subpageSelected, "appId": "PageDesigner"+this.json.subpageSelected});
        }
    },
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "subpage",
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
        // if (this.json.subpageSelected && this.json.subpageSelected!="none" && this.json.subpageType!=="script"){
        //     this.redoSelectedSubpage(this.json.subpageSelected, $(this.property.data.pid+"selectSubpage").getElement("select"), "");
        // }else{
             this.loadIcon();
        // }
        this.node.addEvent("click", function(){
            this.refreshSubpage();
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
        if (this.subpageData ){
            this.subpageModule = new MWF.PCSubpage.Page(this.page, this.node, {
                parentpageIdList : this.getParentpageIdList(),
                level : this.getLevel()
            });
            this.subpageModule.subpageSelector = this.getSubpageSelector();
            this.subpageModule.subpageSelectedValue = this.getSubpageSelectedValue();
            this.subpageModule.level1Subpage = this.getLevel1Subpage();
            this.subpageModule.load(this.subpageData);
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
    getLevel1Subpage : function(){
        return this.page.level1Subpage || this;
    },
    getSubpageSelector : function(){
        return this.subpageSelector || this.page.subpageSelector;
    },
    getSubpageSelectedValue : function(){
        return this.subpageSelectedValue || this.page.subpageSelectedValue;
    },
    checkSubpageNested : function( id ){
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
    refreshSubpage: function(){
        if (this.json.subpageSelected && this.json.subpageSelected!=="none" && this.json.subpageType!=="script"){
            MWF.Actions.get("x_portal_assemble_designer").getPage(this.json.subpageSelected, function(json){
                if (this.subpageData.updateTime!==json.data.updateTime){
                    var select = null;
                    if (this.property){
                        select = $(this.property.data.pid+"selectSubpage").getElement("select");
                    }
                    this.clearSubpageList(this.json.subpageSelected);
                    this.reloadSubpage(json.data, select, "");
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
            "text": "Subpage"
        }).inject(this.iconNode);
	},

	_loadNodeStyles: function(){
		this.iconNode = this.node.getElement("div").setStyles(this.css.iconNode);
		this.iconNode.getFirst("div").setStyles(this.css.iconNodeIcon);
		this.iconNode.getLast("div").setStyles(this.css.iconNodeText);
	},
    _setEditStyle: function(name, input, oldValue){
		if (name==="subpageSelected"){
			if (this.json.subpageSelected!==oldValue){
                this.redoSelectedSubpage(name, input, oldValue);
			}
		}
        if (name==="subpageType"){
		    if (this.json.subpageType!==oldValue){
                if (this.json.subpageType !== "script"){
                    this.redoSelectedSubpage(name, $(this.property.data.pid+"selectSubpage").getElement("select"), "");
                }
                if (this.json.subpageType === "script"){
                    this.subpageData = null;
                    this.clearSubpageList(this.json.subpageSelected);
                    this.node.empty();
                    this.loadIcon();
                }
            }
        }
	},
    redoSelectedSubpage: function(name, input, oldValue){
        if (this.json.subpageSelected==="none") this.json.subpageSelected="";
        if (this.json.subpageSelected && this.json.subpageSelected!=="none"){

            if(input)this.subpageSelector = input;
            if( !input )input = this.getSubpageSelector();

            if( oldValue )this.subpageSelectedValue = oldValue;
            if( !oldValue )oldValue = this.getSubpageSelectedValue() || "";

            var level1Subpage = this.getLevel1Subpage();

            if( !this.checkSubpageNested(this.json.subpageSelected) ){
                //var p = this.node.getPosition(document.body);
                //this.page.designer.alert("error", {
                //    "event": {
                //        "x": p.x + 150,
                //        "y": p.y + 80
                //    }
                //}, this.page.designer.lp.subpageNestedTitle, this.page.designer.lp.subpageNestedInfor, 400, 120);
                this.page.designer.notice( this.page.designer.lp.subpageNestedInfor, "error", level1Subpage.node );
                level1Subpage.json.subpageSelected = oldValue;
                if (input) {
                    for (var i = 0; i < input.options.length; i++) {
                        if (input.options[i].value === oldValue || (input.options[i].value==="none" && !oldValue ) ) {
                            input.options[i].set("selected", true);
                            break;
                        }
                    }
                }
                if( !oldValue ){
                    level1Subpage.node.empty();
                    level1Subpage.loadIcon();
                }else{
                    level1Subpage.refreshSubpage();
                }
            }else{
                MWF.Actions.get("x_portal_assemble_designer").getPage(this.json.subpageSelected, function(json){
                    this.reloadSubpage(json.data, input, oldValue);
                }.bind(this));
            }
        }else{
            this.subpageData = null;
            this.clearSubpageList(oldValue);
            this.node.empty();
            this.loadIcon();
        }
    },
    clearSubpageList: function(pageName){
        if (!this.page.subpageList) this.page.subpageList = {};
        if (pageName) if (this.page.subpageList[pageName]) delete this.page.subpageList[pageName];
    },
    addSubpageList: function(){
        if (!this.page.subpageList) this.page.subpageList = {};
        this.page.subpageList[this.json.subpageSelected] = Object.clone(this.subpageData.json);
    },
	getSubpageData: function(data){
        var subpageDataStr = null;
        if (this.page.options.mode !== "Mobile"){
            subpageDataStr = data.data;
        }else{
            subpageDataStr = data.mobileData;
        }
        this.subpageData = null;
        if (subpageDataStr){
            this.subpageData = JSON.decode(MWF.decodeJsonString(subpageDataStr));
            this.subpageData.updateTime = data.updateTime;
        }
	},
	reloadSubpage: function(data, input, oldValue){
        this.getSubpageData(data);
		if (this.subpageData){
		    var oldSubpageData = (this.page.subpageList && oldValue) ? this.page.subpageList[oldValue] : null;
            this.clearSubpageList(oldValue);

            if (this.checkSubpage(data, input)){
                this.node.empty();
                this.loadSubpage();
                this.addSubpageList();
            }else{
                if (oldSubpageData){
                    if (!this.page.subpageList) this.page.subpageList = {};
                    this.page.subpageList[oldValue] = oldSubpageData;
                }else{
                    this.clearSubpageList(oldValue);
                    this.node.empty();
                    this.loadIcon();
                }

                this.json.subpageSelected = oldValue;
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
            this.json.subpageSelected = oldValue;
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

    regetSubpageData: function(){
	    var flag = false;
        if (this.json.subpageSelected && this.json.subpageSelected!=="none" && this.json.subpageType!=="script"){
            MWF.Actions.get("x_portal_assemble_designer").getPage(this.json.subpageSelected, function(json){
                if (!this.subpageData || this.subpageData.updateTime!==json.data.updateTime){
                    this.getSubpageData(json.data);
                    flag = true;
                }
            }.bind(this), null, false);
        }
        return flag;
    },
    getConflictFields: function(){
        var moduleNames = [];
        if (this.subpageData){
            Object.each(this.subpageData.json.moduleList, function(o, key){
                var check = this.page.checkModuleId(key, o.type, this.subpageData.json.id);
                if (check.fieldConflict){
                    moduleNames.push(key)
                }else if (check.elementConflict){
                    o.changeId = this.json.id+"_"+key;
                }
            }.bind(this));
        }
        return moduleNames;
    },
    checkSubpage: function(data, input){

	    var moduleNames = this.getConflictFields();
        // Object.each(this.subpageData.json.moduleList, function(o, key){
        //     var check = this.page.checkModuleId(key, o.type, this.subpageData.json.id);
        //     if (check.fieldConflict){
        //         moduleNames.push(key)
        //     }else if (check.elementConflict){
        //         o.changeId = this.json.id+"_"+key;
        //     }
        // }.bind(this));
        if (moduleNames.length){
            var txt = this.page.designer.lp.subpageNameConflictInfor;
            txt = txt.replace("{name}", moduleNames.join(", "));
            //var p = (input) ? input.getPosition() : this.node.getPosition();
            // var p = this.node.getPosition(document.body);
            // this.page.designer.alert("error", {
            //     "event": {
            //         "x": p.x+150,
            //         "y": p.y+80
            //     }
            // }, this.page.designer.lp.subpageNameConflictTitle, txt, 400, 200);

            this.page.designer.notice(txt, "error", this.node);

            return false;
        }
		return true;
    },
	loadSubpage: function(data) {
        this.subpageData.json.style = this.page.json.style;
        this.subpageData.json.properties = this.page.json.properties;
        this.subpageData.json.jsheader = {"code": "", "html": ""};
        this.subpageData.json.events = {};
        this.subpageData.json.pageStyleType = this.page.json.pageStyleType;
        //this.subpageData.json.id = this.json.id;

        this.subpageModule = new MWF.PCSubpage.Page(this.page, this.node,{
            parentpageIdList : this.getParentpageIdList(),
            level : this.getLevel()
        });
        this.subpageModule.subpageSelector = this.getSubpageSelector();
        this.subpageModule.subpageSelectedValue = this.getSubpageSelectedValue();
        this.subpageModule.level1Subpage = this.getLevel1Subpage();
        this.subpageModule.load(this.subpageData);

       //this.createRefreshNode();
    },
    destroy: function(){
        this.page.moduleList.erase(this);
        this.page.moduleNodeList.erase(this.node);
        this.page.moduleElementNodeList.erase(this.node);
        this.clearSubpageList(this.json.subpageSelected);

        this.node.destroy();
        this.actionArea.destroy();

        delete this.page.json.moduleList[this.json.id];
        this.json = null;
        delete this.json;

        this.treeNode.destroy();
    }
});

MWF.xApplication.portal.PageDesigner.Module.Subpage.Page = new Class({
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
        this.isSubpage = true;
        this.isSubform = true;
        this.moduleType = "subpage";

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
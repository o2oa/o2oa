MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.View = MWF.FCView = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/View/view.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/View/";
		this.cssPath = "../x_component_process_FormDesigner/Module/View/"+this.options.style+"/css.wcss";

        this.imagePath_default = "../x_component_query_ViewDesigner/$View/";
        this.imagePath_custom = "../x_component_process_FormDesigner/Module/Actionbar/";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "view";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	load : function(json, node, parent){

		this.json = json;
		this.node= node;
		this.node.store("module", this);
		
		//this.node.empty();
		
		this.node.setStyles(this.css.moduleNode);
		
		this._loadNodeStyles();
		
		this._initModule();
		this._loadTreeNode(parent);

        //this.setCustomStyles();
		
		this.parentContainer = this.treeNode.parentNode.module;
        this._setEditStyle_custom("id");

        this.json.moduleName = this.moduleName;
        
	//	this.parseModules();
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "view",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
    _initModule: function(){
        this.setPropertiesOrStyles("styles");
        this.setPropertiesOrStyles("properties");

        this._checkView(function(){
            this._setTitleStyles();
        }.bind(this));

        this._setNodeProperty();
        if (!this.form.isSubform) this._createIconAction();

        this._setNodeEvent();
    },

	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.addEvent("selectstart", function(){
			return false;
		});
		
		this.iconNode = new Element("div", {
			"styles": this.css.iconNode
		}).inject(this.node);
		new Element("div", {
			"styles": this.css.iconNodeIcon
		}).inject(this.iconNode);
		new Element("div", {
			"styles": this.css.iconNodeText,
			"text": "VIEW"
		}).inject(this.iconNode);

		this.iconNode.addEvent("click", function(){
            this._checkView();
        }.bind(this));
	},
	_loadNodeStyles: function(){
		this.iconNode = this.node.getElement("div").setStyles(this.css.iconNode);
		this.iconNode.getFirst("div").setStyles(this.css.iconNodeIcon);
		this.iconNode.getLast("div").setStyles(this.css.iconNodeText);

        this.viewNode = this.node.getChildren("div")[1];
        if (this.viewNode){
            this.node.setStyle("background", "transparent");
            this.actionbarNode = this.viewNode.getChildren("div")[0];
            if( this.actionbarNode ){
                this.actionbarNode.destroy();
                this.actionbarNode = null;
            }
            this.viewTable = this.viewNode.getElement("table").setStyles(this.css.viewTitleTableNode);
            this.viewLine = this.viewTable.getElement("tr").setStyles(this.css.viewTitleLineNode);
            this.viewSelectCell = this.viewLine.getElement("td");
            if (this.viewSelectCell) this.viewSelectCell.setStyles(this.css.viewTitleCellNode);

            this._setViewNodeTitle();
        }
	},
    _createViewNode: function(callback){

        if (!this.viewNode) this.viewNode = new Element("div", {"styles": this.css.viewNode}).inject(this.node);
        if( !this.actionbarNode)this.actionbarNode = new Element("div.actionbarNode",{}).inject( this.viewNode, "top" );

        this.node.setStyle("background", "transparent");


        this.viewTable = new Element("table", {
            "styles": this.css.viewTitleTableNode,
            "border": "0px",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.viewNode);
        this.viewLine = new Element("tr", {"styles": this.css.viewTitleLineNode}).inject(this.viewTable);

        if (this.json.select!="no"){
            this.viewSelectCell = new Element("td", {
                "styles": this.css.viewTitleCellNode
            }).inject(this.viewLine);
            this.viewSelectCell.setStyle("width", "10px");
        }

        MWF.Actions.get("x_query_assemble_designer").getView(this.json["queryView"].id, function(json){
            var viewData = JSON.decode(json.data.data);

            this.viewData = viewData;
            if( this.json.actionbar === "show" ){
                this.actionbarList = [];
                this._showActionbar();
            }

            var columnList = viewData.selectEntryList || viewData.selectList;
            columnList.each(function(column){
                if (!column.hideColumn){
                    var viewCell = new Element("td", {
                        "styles": this.css.viewTitleCellNode,
                        "text": column.displayName
                    }).inject(this.viewLine);
                }
            }.bind(this));

            if (callback) callback();
        }.bind(this));
        this._setViewNodeTitle();
    },
    _setViewNodeTitle: function(){
        if (this.viewTable){
            if (this.json["isTitle"] == "no"){
                this.viewTable.setStyle("opacity", 0.5);
            }else{
                this.viewTable.setStyle("opacity", 1);
            }
        }
    },
    _setEditStyle: function(name, input, oldValue){
        if (name=="view" || name=="processView" || name=="CMSView" || name=="queryView"){
            if (this.json[name]!=oldValue) this._checkView();
        }
        if (name=="select") this._checkSelect();
        if (name=="isTitle") this._checkTitle();
        if (name=="titleStyles") this._setTitleStyles();

        if (name=="name"){
            var title = this.json.name || this.json.id;
            var text = this.json.type.substr(this.json.type.lastIndexOf("$")+1, this.json.type.length);
            this.treeNode.setText("<"+text+"> "+title);
        }
        if (name=="id"){
            if (!this.json.name){
                var text = this.json.type.substr(this.json.type.lastIndexOf("$")+1, this.json.type.length);
                this.treeNode.setText("<"+text+"> "+this.json.id);
            }
            this.treeNode.setTitle(this.json.id);
            this.node.set("id", this.json.id);
        }
        if(name=="actionbar"){
            this.json.actionbar === "show" ? this._showActionbar() : this._hideActionbar();
        }

        this._setEditStyle_custom(name, input, oldValue);

    },
    _setTitleStyles: function(){
        if (this.viewLine){
            this.viewLine.getElements("td").each(function(td){
                td.clearStyles();
                td.setStyles(this.css.viewTitleCellNode);
                if (this.json.titleStyles) td.setStyles(this.json.titleStyles);
            }.bind(this));
            if (this.viewSelectCell) this.viewSelectCell.setStyle("width", "10px");

            this._checkSelect();
            this._checkTitle();
        }
    },
    _checkView: function(callback){
        if (this.json["queryView"] && this.json["queryView"]!="none"){
            this.iconNode.setStyle("display", "none");
            if (this.viewNode) this.viewNode.destroy();
            this.viewNode = null;
            this._createViewNode(function(){
                if (callback) callback();
            }.bind(this));
        }else{
            this.iconNode.setStyle("display", "block");
            if (this.viewNode) this.viewNode.destroy();
            this.node.setStyles(this.css.moduleNode);
            if (callback) callback();
        }
    },
    _checkSelect: function(){
        if (this.json["select"]!="no"){
            if (!this.viewSelectCell){
                this.viewSelectCell = new Element("td", {
                    "styles": this.css.viewTitleCellNode
                }).inject(this.viewLine, "top");
                this.viewSelectCell.setStyle("width", "10px");
            }
            if (this.json["select"]=="single"){
                this.viewSelectCell.setStyle("background", "url(../x_component_process_FormDesigner/Module/View/default/icon/single.png) center center no-repeat");
            }else{
                this.viewSelectCell.setStyle("background", "url(../x_component_process_FormDesigner/Module/View/default/icon/multi.png) center center no-repeat");
            }
        }else{
            if (this.viewSelectCell){
                this.viewSelectCell.destroy();
                this.viewSelectCell = null;
            }
        }
    },
    _checkTitle: function(){
        if (!this.json["isTitle"]) this.json["isTitle"] = "yes";
        if (this.viewNode) this._setViewNodeTitle();
    },

    _hideActionbar : function(){
        if(this.actionbarNode)this.actionbarNode.hide();
    },
    _showActionbar : function(){
	    if( this.actionbarLoading )return;
	    if( !this.actionbarNode )return;
        this.actionbarLoading = true;
        MWF.require("MWF.widget.Toolbar", null, false);
        this.actionbarNode.show();
        if( !this.viewData.actionbarList )this.viewData.actionbarList = [];
        if( !this.actionbarList || this.actionbarList.length == 0 ){
            this.actionbarNode.empty();
            if( this.viewData.actionbarList.length ){
                if( !this.actionbarList )this.actionbarList = [];
                this.viewData.actionbarList.each( function(json){

                    var toolbarWidget = new MWF.widget.Toolbar(this.actionbarNode, {"style": json.style}, this);
                    if (json.actionStyles)toolbarWidget.css = json.actionStyles;

                    if( !json.hideSystemTools )this.setToolbars( json.defaultTools, this.actionbarNode, json );
                    this.setCustomToolbars( json.tools, this.actionbarNode, json );

                    toolbarWidget.load();
                    this.actionbarList.push( toolbarWidget );
                    this.actionbarLoading = false;
                }.bind(this));
            }else{
                this.actionbarLoading = false;
            }
        }else{
            this.actionbarLoading = false;
        }
    },
    setToolbars: function(tools, node, json){
	    var style = "default";
        tools.each(function(tool){
            var actionNode = new Element("div", {
                "MWFnodetype": tool.type,
                "MWFButtonImage": this.imagePath_default+""+style+"/actionbar/"+tool.img,
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text
            }).inject(node);
            if( this.json.iconOverStyle ){
                actionNode.set("MWFButtonImageOver" , this.imagePath_default+""+ style+"/actionbar/"+json.iconOverStyle+"/"+tool.img );
            }
            // this.systemTools.push(actionNode);
            // if (tool.sub){
            //     var subNode = node.getLast();
            //     this.setToolbars(tool.sub, subNode);
            // }
        }.bind(this));
    },
    setCustomToolbars: function(tools, node, json){
        //var style = (this.json.style || "default").indexOf("red") > -1 ? "red" : "blue";
        var path = "";
        if( json.customIconStyle ){
            path = json.customIconStyle+ "/";
        }
        var customImageStyle = "default";

        tools.each(function(tool){
            var actionNode = new Element("div", {
                "MWFnodetype": tool.type,
                "MWFButtonImage": this.imagePath_custom+""+customImageStyle +"/custom/"+path+tool.img,
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text
            }).inject(node);
            if( this.json.customIconOverStyle ){
                actionNode.set("MWFButtonImageOver" , this.imagePath_custom+""+customImageStyle +"/custom/"+json.customIconOverStyle+ "/" +tool.img );
            }
            // this.customTools.push(actionNode);
            // if (tool.sub){
            //     var subNode = node.getLast();
            //     this.setCustomToolbars(tool.sub, subNode);
            // }
        }.bind(this));
    }
});

MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.View", null, false);
MWF.xApplication.process.FormDesigner.Module.Statement = MWF.FCStatement = new Class({
	Extends: MWF.FCView,
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Statement/statement.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Statement/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Statement/"+this.options.style+"/css.wcss";

        this.imagePath_default = "../x_component_query_ViewDesigner/$Statement/";
        this.imagePath_custom = "../x_component_process_FormDesigner/Module/Actionbar/";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "statement";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "statement",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
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
			"text": "Statement"
		}).inject(this.iconNode);

		this.iconNode.addEvent("click", function(){
            this._checkView();
        }.bind(this));
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
    }
});

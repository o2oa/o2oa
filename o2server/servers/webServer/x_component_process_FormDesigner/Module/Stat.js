MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Stat = MWF.FCStat = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Stat/stat.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Stat/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Stat/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "stat";

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
			"text": "STAT"
		}).inject(this.iconNode);
	},
	_loadNodeStyles: function(){
		this.iconNode = this.node.getElement("div").setStyles(this.css.iconNode);
		this.iconNode.getFirst("div").setStyles(this.css.iconNodeIcon);
		this.iconNode.getLast("div").setStyles(this.css.iconNodeText);

        // this.viewNode = this.node.getChildren("div")[1];
        // if (this.viewNode){
        //     this.viewTable = this.viewNode.getElement("table").setStyles(this.css.viewTitleTableNode);
        //     this.viewLine = this.viewTable.getElement("tr").setStyles(this.css.viewTitleLineNode);
        //     this.viewSelectCell = this.viewLine.getElement("td");
        //     if (this.viewSelectCell) this.viewSelectCell.setStyles(this.css.viewTitleCellNode);
        //
        //     this._setViewNodeTitle();
        // }
	},

    _setEditStyle: function(name, input, oldValue){
    }
});

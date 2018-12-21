MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Tree = MWF.FCTree = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Tree/tree.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Tree/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Tree/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "tree";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "tree",
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
		this._setNodeProperty();
        if (!this.form.isSubform) this._createIconAction();
		this._setNodeEvent();
		this._refreshTree();
	},
	_setEditStyle_custom: function(name){
		if (name=="areaNodeStyle"){
			this.tree.node.setStyles(this.json.areaNodeStyle);
		}
		if (name=="treeItemNodeStyle" || name=="textDivNodeStyle" || name=="textDivNodeSelectedStyle"){
			this._setTreeWidgetStyles();
			this._refreshTree();
		}
        if (name=="dataType") this._refreshTree();
	},
	_setTreeStyles: function(){
		this.json.areaNodeStyle = this.tree.css.areaNode;
		this.json.treeItemNodeStyle = this.tree.css.treeItemNode;
		this.json.textDivNodeStyle = this.tree.css.textDivNode;
		this.json.textDivNodeSelectedStyle = this.tree.css.textDivNodeSelected;
	},
	_setTreeWidgetStyles: function(){
		this.tree.css.areaNode = this.json.areaNodeStyle;
		this.tree.css.treeItemNode = this.json.treeItemNodeStyle;
		this.tree.css.textDivNode = this.json.textDivNodeStyle;
		this.tree.css.textDivNodeSelected = this.json.textDivNodeSelectedStyle;
	},
	_refreshTree: function(){

        var treeData = this.json.data
        if (this.json.dataType == "script"){
            treeData = [
                {
                    "title": "[script]",
                    "text": "[script]",
                    "id": "1",
                    "icon": "none",
                    "action": "",
                    "expand": true,
                    "sub": []
                }
            ];
        }

		if (!this.tree){
			this.node.empty();
			this.tree = new MWF.widget.Tree(this.node, {"style":"form"});
			this.tree.load(treeData);
			this._setTreeStyles();
		}else{
			this.tree.reLoad(treeData);
		}
	}
});

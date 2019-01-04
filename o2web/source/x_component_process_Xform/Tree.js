MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.require("MWF.widget.Tree", null, false);
MWF.xApplication.process.Xform.Tree = MWF.APPTree =  new Class({
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
		this.node.empty();
		this.tree = new MWF.widget.Tree(this.node, {"style":"form"});
        this.tree.form = this.form;

		this._setTreeWidgetStyles();


        var treeData = this.json.data;
        if (this.json.dataType == "script") treeData = this.form.Macro.exec(this.json.dataScript.code, this);

		this.tree.load(treeData);
	},
	_setTreeWidgetStyles: function(){
		this.tree.css.areaNode = this.json.areaNodeStyle;
		this.tree.css.treeItemNode = this.json.treeItemNodeStyle;
		this.tree.css.textDivNode = this.json.textDivNodeStyle;
		this.tree.css.textDivNodeSelected = this.json.textDivNodeSelectedStyle;
	}
}); 
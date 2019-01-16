MWF.xApplication.portal.PageDesigner.Module.Source = MWF.PCSource = new Class({
	Extends: MWF.FCDiv,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Source/source.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Source/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Source/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "Source";
		
		this.Node = null;
		this.form = form;
	},
    _createMoveNode: function(){
        this.moveNode = new Element("div", {
            "MWFType": "source",
            "id": this.json.id,
            "styles": this.css.moduleNodeMove,
            "events": {
                "selectstart": function(){
                    return false;
                }
            }
        }).inject(this.form.container);
    }
});

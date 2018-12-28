MWF.xApplication.portal.PageDesigner.Module.SubSource = MWF.PCSubSource = new Class({
	Extends: MWF.FCDiv,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/SubSource/subSource.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/SubSource/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/SubSource/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "SubSource";
		
		this.Node = null;
		this.form = form;
	},
    _createMoveNode: function(){
        this.moveNode = new Element("div", {
            "MWFType": "subSource",
            "id": this.json.id,
            "styles": this.css.moduleNodeMove,
            "events": {
                "selectstart": function(){
                    return false;
                }
            }
        }).inject(this.form.container);
    },
    _onMoveEnter: function(dragging, inObj){
        var module = inObj.retrieve("module");

        var pmodule = module;
        var flag = false;
        while (pmodule){
            if (pmodule.moduleName == "Source"){
                flag = true;
                break;
            }
            pmodule = pmodule.parentContainer;
        }
        if (flag){
            if (module) module._dragIn(this);
            this._onEnterOther(dragging, inObj);
        }
    },
    _nodeDrag: function(){}
});

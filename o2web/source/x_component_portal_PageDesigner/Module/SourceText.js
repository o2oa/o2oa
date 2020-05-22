MWF.xApplication.portal.PageDesigner.Module.SourceText = MWF.PCSourceText = new Class({
	Extends: MWF.FCLabel,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/SourceText/sourceText.html"
	},
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/SourceText/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/SourceText/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "SourceText";
		
		this.Node = null;
		this.form = form;
	},
    _createMoveNode: function(){
        this.moveNode = new Element("div", {
            "MWFType": "sourceText",
            "styles": this.css.moduleNodeMove,
            "text": "{T}Text",
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

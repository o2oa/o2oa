MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("cms.FormDesigner", "Module.$Element", null, false);
MWF.require("MWF.widget.SimpleToolbar", null, false);
MWF.xApplication.cms.FormDesigner.Module.Actionbar = MWF.CMSFCActionbar = new Class({
	Extends: MWF.CMSFC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Actionbar/actionbar.html"
        //"actions": [
        //    {
        //        "name": "move",
        //        "icon": "move1.png",
        //        "event": "mousedown",
        //        "action": "move",
        //        "title": MWF.CMSFD.LP.formAction.move
        //    },
        //    {
        //        "name": "copy",
        //        "icon": "copy1.png",
        //        "event": "mousedown",
        //        "action": "copy",
        //        "title": MWF.CMSFD.LP.formAction.copy
        //    },
        //    {
        //        "name": "add",
        //        "icon": "add.png",
        //        "event": "click",
        //        "action": "addAction",
        //        "title": MWF.CMSFD.LP.formAction.add
        //    },
        //    {
        //        "name": "delete",
        //        "icon": "delete1.png",
        //        "event": "click",
        //        "action": "delete",
        //        "title": MWF.CMSFD.LP.formAction["delete"]
        //    }
        //]
	},
    addAction: function(){

    },
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_cms_FormDesigner/Module/Actionbar/";
		this.cssPath = "/x_component_cms_FormDesigner/Module/Actionbar/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "actionbar";
		
		this.Node = null;
		this.form = form;
        this.container = null;
        this.containerNode = null;
        //this.containers = [];
        //this.elements = [];
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "actionbar",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(e){
                    e.preventDefault();
				}
			}
		}).inject(this.form.container);
	},
    _createNode: function(callback){
        this.node = new Element("div", {
            "id": this.json.id,
            "MWFType": "actionbar",
            "styles": this.css.moduleNode,
            "events": {
                "selectstart": function(e){
                    e.preventDefault();
                }
            }

        }).inject(this.form.node);
        if (this.form.options.mode == "Mobile"){
            this.node.set("text", MWF.CMSFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
            this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
        }else{
            this.toolbarNode = new Element("div").inject(this.node);

            this.toolbarWidget = new MWF.widget.SimpleToolbar(this.toolbarNode, {"style": this.json.style}, this);

            if (this.json.sysTools.editTools){
                this.setToolbars(this.json.sysTools.editTools, this.toolbarNode);
      //          this.setToolbars(this.json.tools.editTools, this.toolbarNode);
            }else{
                this.setToolbars(this.json.sysTools, this.toolbarNode);
      //          this.setToolbars(this.json.tools, this.toolbarNode);
            }

//        this.resetIcons();
            this.toolbarWidget.load();
        }

    },
    _initModule: function(){
        this._setNodeProperty();
        this._createIconAction();
        this._setNodeEvent();
        this._refreshActionbar();
    },
    _refreshActionbar: function(){
        if (this.form.options.mode == "Mobile"){
            this.node.set("text", MWF.CMSFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
            this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
        }else{
            this.toolbarNode = this.node.getFirst("div");
            this.toolbarNode.empty();
            this.toolbarWidget = new MWF.widget.SimpleToolbar(this.toolbarNode, {"style": this.json.style}, this);
            if (this.json.sysTools.editTools){
                this.setToolbars(this.json.sysTools.editTools, this.toolbarNode);
         //       this.setToolbars(this.json.tools.editTools, this.toolbarNode);
            }else{
                this.setToolbars(this.json.sysTools, this.toolbarNode);
         //       this.setToolbars(this.json.tools, this.toolbarNode);
            }
            this.toolbarWidget.load();
        }

    },
    //resetIcons: function(){
    //    var divs = this.toolbarNode.getElements("div");
    //    divs.each(function(item, idx){
    //        var img = item.get("MWFButtonImage");
    //        if (img){
    //            item.set("MWFButtonImage", this.path+""+this.options.style+"/tools/"+img);
    //        }
    //    }.bind(this));
    //
    //},
    setToolbars: function(tools, node){
        tools.each(function(tool){
            var actionNode = new Element("div", {
                "MWFnodetype": tool.type,
                "MWFButtonImage": this.path+""+this.options.style+"/tools/"+tool.img,
                "MWFButtonImageOver": this.path+""+this.options.style+"/tools/"+tool.img_over,
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text,
            }).inject(node);
            if (tool.sub){
                var subNode = node.getLast();
                this.setToolbars(tool.sub, subNode);
            }
        }.bind(this));
    },


	_setEditStyle_custom: function(name){
		
	}
	
});

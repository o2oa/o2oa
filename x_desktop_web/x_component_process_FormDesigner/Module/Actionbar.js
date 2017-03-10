MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.require("MWF.widget.Toolbar", null, false);
MWF.xApplication.process.FormDesigner.Module.Actionbar = MWF.FCActionbar = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Actionbar/actionbar.html"
        //"actions": [
        //    {
        //        "name": "move",
        //        "icon": "move1.png",
        //        "event": "mousedown",
        //        "action": "move",
        //        "title": MWF.APPFD.LP.formAction.move
        //    },
        //    {
        //        "name": "copy",
        //        "icon": "copy1.png",
        //        "event": "mousedown",
        //        "action": "copy",
        //        "title": MWF.APPFD.LP.formAction.copy
        //    },
        //    {
        //        "name": "add",
        //        "icon": "add.png",
        //        "event": "click",
        //        "action": "addAction",
        //        "title": MWF.APPFD.LP.formAction.add
        //    },
        //    {
        //        "name": "delete",
        //        "icon": "delete1.png",
        //        "event": "click",
        //        "action": "delete",
        //        "title": MWF.APPFD.LP.formAction["delete"]
        //    }
        //]
	},
    addAction: function(){

    },
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Actionbar/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Actionbar/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "actionbar";
		
		this.Node = null;
		this.form = form;
        this.container = null;
        this.containerNode = null;
        this.systemTools = [];
        //this.containers = [];
        //this.elements = [];
	},
    setTemplateStyles: function(styles){
        this.json.style = styles.style;
    },
    clearTemplateStyles: function(styles){
        this.json.style = "form";
    },
    setAllStyles: function(){
        this._refreshActionbar();
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
            this.node.set("text", MWF.APPFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
            this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
        }else{
            this.toolbarNode = new Element("div").inject(this.node);

            this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);

            MWF.getJSON(this.path+"toolbars.json", function(json){
                this.setToolbars(json, this.toolbarNode);
                this.toolbarWidget.load();
            }.bind(this), false);
      //      if (this.json.sysTools.editTools){
      //          this.setToolbars(this.json.sysTools.editTools, this.toolbarNode);
      ////          this.setToolbars(this.json.tools.editTools, this.toolbarNode);
      //      }else{
      //          this.setToolbars(this.json.sysTools, this.toolbarNode);
      ////          this.setToolbars(this.json.tools, this.toolbarNode);
      //      }

//        this.resetIcons();

        }

    },
    _initModule: function(){
        this.setStyleTemplate();
        this._setNodeProperty();
        this._createIconAction();
        this._setNodeEvent();
        this._refreshActionbar();
    },
    _refreshActionbar: function(){
        if (this.form.options.mode == "Mobile"){
            this.node.set("text", MWF.APPFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
            this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
        }else{
            this.toolbarNode = this.node.getFirst("div");
            this.toolbarNode.empty();
            this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);

            MWF.getJSON(this.path+"toolbars.json", function(json){
                this.setToolbars(json, this.toolbarNode);
                this.toolbarWidget.load();
            }.bind(this), false);

         //   if (this.json.sysTools.editTools){
         //       this.setToolbars(this.json.sysTools.editTools, this.toolbarNode);
         ////       this.setToolbars(this.json.tools.editTools, this.toolbarNode);
         //   }else{
         //       this.setToolbars(this.json.sysTools, this.toolbarNode);
         ////       this.setToolbars(this.json.tools, this.toolbarNode);
         //   }

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
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text
            }).inject(node);
            this.systemTools.push(actionNode);
            if (tool.sub){
                var subNode = node.getLast();
                this.setToolbars(tool.sub, subNode);
            }
        }.bind(this));
    },
	_setEditStyle_custom: function(name){
		if (name=="hideSystemTools"){
            if (this.json.hideSystemTools){
                this.systemTools.each(function(tool){
                    tool.setStyle("display", "none");
                });
            }else{
                this.systemTools.each(function(tool){
                    tool.setStyle("display", "block");
                });
            }
        }
	}
	
});

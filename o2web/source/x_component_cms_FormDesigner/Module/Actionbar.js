MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Actionbar", null, false);
MWF.require("MWF.widget.SimpleToolbar", null, false);
MWF.xApplication.cms.FormDesigner.Module.Actionbar = MWF.CMSFCActionbar = new Class({
	Extends: MWF.FCActionbar,
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Actionbar/actionbar.html"
	},
	Implements : [MWF.CMSFCMI],
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
		this.systemTools = [];
		this.customTools = [];
	},
	setTemplateStyles: function(styles){
		this.json.style = styles.style;
		this.json.customIconStyle = styles.customIconStyle;
		this.json.customIconOverStyle = styles.customIconOverStyle;
	},
	clearTemplateStyles: function(styles){
		this.json.style = "form";
		this.json.customIconStyle = "blue";
		this.json.customIconOverStyle = "white";
	},
	setAllStyles: function(){
		this._refreshActionbar();
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

			this.toolbarWidget = new MWF.widget.SimpleToolbar(this.toolbarNode, {"style": this.json.style}, this);

			MWF.getJSON(this.path+"toolbars.json", function(json){
			    this.json.defaultTools = json;
				this.setToolbars(json, this.toolbarNode);
				this.toolbarWidget.load();
				this._setEditStyle_custom( "hideSystemTools" );
				this._setEditStyle_custom( "hideSetPopularDocumentTool" );
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
	_refreshActionbar: function(){
		if (this.form.options.mode == "Mobile"){
			this.node.set("text", MWF.APPFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
			this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
		}else{
			this.toolbarNode = this.node.getFirst("div");
			this.toolbarNode.empty();
			this.toolbarWidget = new MWF.widget.SimpleToolbar(this.toolbarNode, {"style": this.json.style}, this);
            //if (!this.json.actionStyles) this.json.actionStyles = Object.clone(this.toolbarWidget.css);
            //this.toolbarWidget.css = this.json.actionStyles;

			if (this.json.defaultTools){
				var json = Array.clone(this.json.defaultTools);
				this.setToolbars(json, this.toolbarNode);
				//if (this.json.tools) json.append(this.json.tools);
				//this.setToolbars(json, this.toolbarNode);
				if (this.json.tools){
					this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
				}
				this.toolbarWidget.load();
				this._setEditStyle_custom( "hideSystemTools" );
				this._setEditStyle_custom( "hideSetPopularDocumentTool" );
				//json = null;
			}else{
				MWF.getJSON(this.path+"toolbars.json", function(json){
					this.json.defaultTools = json;
					var json = Array.clone(this.json.defaultTools);
					this.setToolbars(json, this.toolbarNode);
					//if (this.json.tools) json.append(this.json.tools);
					//this.setToolbars(json, this.toolbarNode);
					if (this.json.tools){
						this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
					}
					this.toolbarWidget.load();
					this._setEditStyle_custom( "hideSystemTools" );
					this._setEditStyle_custom( "hideSetPopularDocumentTool" );
					//json = null;
				}.bind(this), false);
			}

			//   if (this.json.sysTools.editTools){
			//       this.setToolbars(this.json.sysTools.editTools, this.toolbarNode);
			////       this.setToolbars(this.json.tools.editTools, this.toolbarNode);
			//   }else{
			//       this.setToolbars(this.json.sysTools, this.toolbarNode);
			////       this.setToolbars(this.json.tools, this.toolbarNode);
			//   }

		}

	},
	//_refreshActionbar: function(){
	//	if (this.form.options.mode == "Mobile"){
	//		this.node.set("text", MWF.APPFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
	//		this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
	//	}else{
	//		this.toolbarNode = this.node.getFirst("div");
	//		this.toolbarNode.empty();
	//		this.toolbarWidget = new MWF.widget.SimpleToolbar(this.toolbarNode, {"style": this.json.style}, this);
    //
	//		MWF.getJSON(this.path+"toolbars.json", function(json){
	//			this.setToolbars(json, this.toolbarNode);
	//			this.toolbarWidget.load();
	//			this._setEditStyle_custom( "hideSetPopularDocumentTool" );
	//		}.bind(this), false);
	//	}
    //
	//},
	setToolbars: function(tools, node){
		tools.each(function(tool){
			var actionNode = new Element("div", {
				"MWFnodetype": tool.type,
				"MWFButtonImage": this.path+""+this.options.style +"/tools/"+ (this.json.style || "default") +"/"+tool.img,
				"MWFButtonImageOver": this.path+""+this.options.style+"/tools/"+ (this.json.style || "default") +"/"+tool.img_over,
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
	setCustomToolbars: function(tools, node){
		//var style = (this.json.style || "default").indexOf("red") > -1 ? "red" : "blue";
		var style;
		if( this.json.customIconStyle ){
			style = this.json.customIconStyle;
		}else{
			style = (this.json.style || "default").indexOf("red") > -1 ? "red" : "blue";
		}
		var style_over = this.json.customIconOverStyle || "white";
		tools.each(function(tool){
			var actionNode = new Element("div", {
				"MWFnodetype": tool.type,
				"MWFButtonImage": this.path+""+this.options.style +"/custom/"+ style +"/"+tool.img,
				"MWFButtonImageOver": this.path+""+this.options.style+"/custom/"+ style_over +"/"+tool.img,
				"title": tool.title,
				"MWFButtonAction": tool.action,
				"MWFButtonText": tool.text
			}).inject(node);
			this.customTools.push(actionNode);
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
		if (name=="hideSetPopularDocumentTool"){
			if (this.json.hideSetPopularDocumentTool){
				this.systemTools.each(function(tool){
					if( tool.get("MWFButtonAction") == "setPopularDocument" ){
						tool.setStyle("display", "none");
					}
				});
			}else{
				this.systemTools.each(function(tool){
					if( tool.get("MWFButtonAction") == "setPopularDocument" ){
						tool.setStyle("display", "block");
					}
				});
			}
		}
		if (name=="defaultTools" || name=="tools" || name==="actionStyles"){
			this._refreshActionbar();
		}
	}
});

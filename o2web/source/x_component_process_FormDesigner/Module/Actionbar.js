MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.require("MWF.widget.Toolbar", null, false);
MWF.xApplication.process.FormDesigner.Module.Actionbar = MWF.FCActionbar = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Actionbar/actionbar.html"
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
        this.customTools = [];
        //this.containers = [];
        //this.elements = [];
	},
    setTemplateStyles: function(styles){
        this.json.style = styles.style;
        this.json.iconOverStyle = styles.iconOverStyle || "";
        this.json.customIconStyle = styles.customIconStyle;
        this.json.customIconOverStyle = styles.customIconOverStyle || "";
    },
    clearTemplateStyles: function(styles){
        this.json.style = "form";
        this.json.iconOverStyle = "";
        this.json.customIconStyle = "";
        this.json.customIconOverStyle = "";
    },
    setAllStyles: function(){
        this._resetActionbar();
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
                this.json.defaultTools = json;
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
        if (!this.form.isSubform) this._createIconAction();
        this._setNodeEvent();
        this._refreshActionbar();
        if( !this.json.events ){
            MWF.getJSON(this.path+"template.json", function(json){
               this.json.events = json.events;
            }.bind(this), false);
        }
    },
    _refreshActionbar: function(){
        if (this.form.options.mode == "Mobile"){
            this.node.set("text", MWF.APPFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
            this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
        }else{
            this.toolbarNode = this.node.getFirst("div");
            this.toolbarNode.empty();
            this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);
            if (!this.json.actionStyles) this.json.actionStyles = Object.clone(this.toolbarWidget.css);
            this.toolbarWidget.css = this.json.actionStyles;

            if (this.json.defaultTools){
                var json = Array.clone(this.json.defaultTools);
                //if (this.json.tools) json.append(this.json.tools);
                this.setToolbars(json, this.toolbarNode);
                if (this.json.tools){
                    this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
                }
                this.toolbarWidget.load();
                //json = null;
            }else{
                MWF.getJSON(this.path+"toolbars.json", function(json){
                    this.json.defaultTools = json;
                    var json = Array.clone(this.json.defaultTools);
                    //if (this.json.tools) json.append(this.json.tools);
                    this.setToolbars(json, this.toolbarNode);
                    if (this.json.tools){
                        this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
                    }
                    this.toolbarWidget.load();
                    //json = null;
                }.bind(this), false);
            }
        }

    },
    _resetActionbar: function(){
        if (this.form.options.mode == "Mobile"){
            this.node.set("text", MWF.APPFD.LP.notice.notUseModuleInMobile+"("+this.moduleName+")");
            this.node.setStyles({"height": "24px", "line-height": "24px", "background-color": "#999"});
        }else{
            this.toolbarNode = this.node.getFirst("div");
            this.toolbarNode.empty();
            this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {"style": this.json.style}, this);
            if (!this.json.actionStyles){
                this.json.actionStyles = Object.clone(this.toolbarWidget.css);
            }else{
                this.toolbarWidget.css = Object.merge( Object.clone(this.json.actionStyles), this.toolbarWidget.css );
                this.json.actionStyles = Object.clone(this.toolbarWidget.css);
            }

            if (this.json.defaultTools){
                var json = Array.clone(this.json.defaultTools);
                //if (this.json.tools) json.append(this.json.tools);
                this.setToolbars(json, this.toolbarNode);
                if (this.json.tools){
                    this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
                }
                this.toolbarWidget.load();
                //json = null;
            }else{
                MWF.getJSON(this.path+"toolbars.json", function(json){
                    this.json.defaultTools = json;
                    var json = Array.clone(this.json.defaultTools);
                    //if (this.json.tools) json.append(this.json.tools);
                    this.setToolbars(json, this.toolbarNode);
                    if (this.json.tools){
                        this.setCustomToolbars(Array.clone(this.json.tools), this.toolbarNode);
                    }
                    this.toolbarWidget.load();
                    //json = null;
                }.bind(this), false);
            }
        }
    },
    setToolbars: function(tools, node){
        tools.each(function(tool){
            var actionNode = new Element("div", {
                "MWFnodetype": tool.type,
                "MWFButtonImage": this.path+""+this.options.style+"/tools/"+(this.json.style || "default")+"/"+tool.img,
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text
            }).inject(node);
            if( this.json.iconOverStyle ){
                actionNode.set("MWFButtonImageOver" , this.path+""+this.options.style+"/tools/"+this.json.iconOverStyle+"/"+tool.img );
            }
            this.systemTools.push(actionNode);
            if (tool.sub){
                var subNode = node.getLast();
                this.setToolbars(tool.sub, subNode);
            }
        }.bind(this));
    },
    setCustomToolbars: function(tools, node){
        //var style = (this.json.style || "default").indexOf("red") > -1 ? "red" : "blue";
        var path = "";
        if( this.json.customIconStyle ){
            path = this.json.customIconStyle+ "/";
        }

        tools.each(function(tool){
            var actionNode = new Element("div", {
                "MWFnodetype": tool.type,
                "MWFButtonImage": this.path+""+this.options.style +"/custom/"+path+tool.img,
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text
            }).inject(node);
            if( this.json.customIconOverStyle ){
                actionNode.set("MWFButtonImageOver" , this.path+""+this.options.style +"/custom/"+this.json.customIconOverStyle+ "/" +tool.img );
            }
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
        if (name=="defaultTools" || name=="tools" || name==="actionStyles"){
            this._refreshActionbar();
        }

	}
	
});

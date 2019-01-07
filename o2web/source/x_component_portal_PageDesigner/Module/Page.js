MWF.xApplication.portal.PageDesigner.Module = MWF.xApplication.portal.PageDesigner.Module || {};
MWF.require("MWF.widget.Common", null, false);
MWF.xApplication.portal.PageDesigner.Module.Page = MWF.PCPage = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Page/page.html",
        "mode": "PC",
        "fields": ["Calendar", "Checkbox", "Datagrid", "Datagrid$Title", "Datagrid$Data", "Htmleditor", "Number", "Office", "Orgfield", "Personfield", "Radio", "Select", "Textarea", "Textfield"]
    },
    initializeBase: function(options){
        this.setOptions(options);

        this.path = "/x_component_portal_PageDesigner/Module/Page/";
        this.cssPath = "/x_component_portal_PageDesigner/Module/Page/"+this.options.style+"/css.wcss";

        this._loadCss();
    },
	initialize: function(designer, container, options){
        this.initializeBase(options);
		
		this.container = null;
		this.page = this;
        this.form = this;
        this.moduleType = "page";
		
		this.moduleList = [];
		this.moduleNodeList = [];
		
		this.moduleContainerNodeList = [];
		this.moduleElementNodeList = [];
		this.moduleComponentNodeList = [];

	//	this.moduleContainerList = [];
		this.dataTemplate = {};
		
		this.designer = designer;
		this.container = container;
		
		this.selectedModules = [];
	},
	reload: function(data){
        this.moduleList.each(function(module){
            if (module.property){
                module.property.destroy();
			}
        }.bind(this));
        if (this.property) this.property.destroy();
        this.property = null;

        this.moduleList = [];
        this.moduleNodeList = [];
        this.moduleContainerNodeList = [];
        this.moduleElementNodeList = [];
        this.moduleComponentNodeList = [];
        this.dataTemplate = {};
        this.selectedModules = [];
        this.container.empty();

        if (this.treeNode){
            this.domTree.empty();
            this.domTree.node.destroy();
            this.domTree = null;
            this.treeNode = null;
		}
        this.currentSelectedModule = null;
        this.propertyMultiTd = null;

        if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        this.load(data);
	},
	load : function(data){
		this.data = data;
		this.json = data.json;
		this.html = data.html;
        this.json.mode = this.options.mode;
        if (!this.json.css) this.json.css = {"code":""};

		this.isNewPage = (this.json.id) ? false : true;
		if (this.isNewPage) this.checkUUID();
		if(this.designer.application) this.data.json.applicationName = this.designer.application.name;
		if(this.designer.application) this.data.json.application = this.designer.application.id;
		
		this.container.set("html", this.html);
        this.loadStylesList(function(){
            var oldStyleValue = "";
            if ((!this.json.pageStyleType) || !this.stylesList[this.json.pageStyleType]) this.json.pageStyleType="blue-simple";
            if (this.options.mode=="Mobile"){
                if (this.json.pageStyleType != "defaultMobile"){
                    oldStyleValue = this.json.pageStyleType;
                    this.json.pageStyleType = "defaultMobile";
                }
            }

            this.templateStyles = (this.stylesList && this.json.pageStyleType) ? this.stylesList[this.json.pageStyleType] : null;
            this.loadDomModules();

            if (this.json.pageStyleType){
                if (this.stylesList[this.json.pageStyleType]){
                    if (this.stylesList[this.json.pageStyleType]["page"]){
                        this.setTemplateStyles(this.stylesList[this.json.pageStyleType]["page"]);
                    }
                }
            }

            this.setCustomStyles();
            this.node.setProperties(this.json.properties);

            this.setNodeEvents();

            if (this.options.mode=="Mobile"){
                if (oldStyleValue) this._setEditStyle("pageStyleType", null, oldStyleValue);
            }

            this.selected();
            this.autoSave();
            this.designer.addEvent("queryClose", function(){
                if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
            }.bind(this));
        }.bind(this));
	},
    removeStyles: function(from, to){
        if (this.json[to]){
            Object.each(from, function(style, key){
                if (this.json[to][key] && this.json[to][key]==style){
                    delete this.json[to][key];
                }
            }.bind(this));
        }
    },
    copyStyles: function(from, to){
        if (!this.json[to]) this.json[to] = {};
        Object.each(from, function(style, key){
            if (!this.json[to][key]) this.json[to][key] = style;
        }.bind(this));
    },
    clearTemplateStyles: function(styles){
        if (styles){
            if (styles.styles) this.removeStyles(styles.styles, "styles");
            if (styles.properties) this.removeStyles(styles.properties, "properties");
        }
    },
    setTemplateStyles: function(styles){
        if (styles.styles) this.copyStyles(styles.styles, "styles");
        if (styles.properties) this.copyStyles(styles.properties, "properties");
    },

    loadStylesList: function(callback){
        var stylesUrl = "/x_component_portal_PageDesigner/Module/Page/template/"+((this.options.mode=="Mobile") ? "styles": "styles")+".json";
        MWF.getJSON(stylesUrl,{
                "onSuccess": function(responseJSON){
                    this.stylesList= responseJSON;
                    if (callback) callback(this.stylesList);
                }.bind(this),
                "onRequestFailure": function(){
                    this.stylesList = {};
                    if (callback) callback(this.stylesList);
                }.bind(this),
                "onError": function(){
                    this.stylesList = {};
                    if (callback) callback(this.stylesList);
                }.bind(this)
            }
        );
    },
    autoSave: function(){
        this.autoSaveCheckNode = this.designer.pageToolbarNode.getElement("#MWFPageAutoSaveCheck");
        if (this.autoSaveCheckNode){
            this.autoSaveTimerID = window.setInterval(function(){
                if (this.autoSaveCheckNode.get("checked")){
                    this.save();
                }
            }.bind(this), 60000);
        }
    },
	checkUUID: function(){
		this.designer.actions.getUUID(function(id){
            this.json.id = id;
        }.bind(this));
	},
	loadDomModules: function(){
		this.node = this.container.getFirst();
		this.node.set("id", this.json.id);
		this.node.setStyles(this.css.pageNode);
		this.node.store("module", this);

		var id = this.json.id.replace(/\-/g, "");
        this.node.addClass("css"+id);
        this.reloadCss();

        var y = this.container.getStyle("height");
        y = (y) ? y.toInt()-2 : this.container.getSize().y-2;
		this.node.setStyle("min-height", ""+y+"px");
		this.designer.addEvent("resize", function(){
            var y = this.container.getStyle("height");
            y = (y) ? y.toInt()-2 : this.container.getSize().y-2;
			this.node.setStyle("min-height", ""+y+"px");
		}.bind(this));

		this.loadDomTree();
	},
	
	loadDomTree: function(){
		MWF.require("MWF.widget.Tree", function(){
			this.domTree = new MWF.widget.Tree(this.designer.propertyDomArea, {"style": "domtree"});
			this.domTree.load();
			
			this.createPageTreeNode();
			this.parseModules(this, this.node);
		}.bind(this));
	},
	createPageTreeNode: function(){
		var text = "<"+this.json.type+"> "+this.json.name+" ["+this.options.mode+"] ";
		var o = {
			"expand": true,
			"title": this.json.id,
			"text": "<"+this.json.type+"> "+this.json.name+" ["+this.options.mode+"] ",
			"icon": (this.options.mode=="Mobile") ? "mobile.png": "pc.png"
		};
		o.action = function(){
			if (this.module) this.module.selected();
		};
		this.treeNode = this.domTree.appendChild(o);
        this.treeNode.setText(text);
        this.treeNode.module = this;
	},

	parseModules: function(parent, dom){
        var tmpDom = null;
		var subDom = dom.getFirst();
		while (subDom){
			if (subDom.get("MWFtype")){
//				var module = subDom.retrieve("module");
//				alert(subDom.get("id")+": "+module);
//				if (!module){
					var json = this.getDomjson(subDom);
                    if (json) module = this.loadModule(json, subDom, parent);

//				}
//                if (module.moduleType=="container") this.parseModules(module, subDom);
//			}else{
//				this.parseModules(parent, subDom);
			}
//			else if (subDom.getFirst()){
//				subDom = subDom.getFirst();
//				this.parseModules(parent, subDom);
//			}else{
//				subDom = subDom.getNext();
//			}
            if (!json) tmpDom = subDom;
			subDom = subDom.getNext();

            if (tmpDom){
                tmpDom.destroy();
                tmpDom = null;
            }
		}
	},
	
	getDomjson: function(dom){
		var mwfType = dom.get("MWFtype");

		switch (mwfType) {
			case "page":
				return this.json;
			case "":
				return null;
			default:
				var id = dom.get("id");
				if (id){
					return this.json.moduleList[id];
				}else{
					return null;
				}
		}
	},
	
	loadModule: function(json, dom, parent){
        if (json) {
            var module = new MWF["PC" + json.type](this);
            module.load(json, dom, parent);
            return module;
        }
	},
	
	setNodeEvents: function(){
		this.node.addEvent("click", function(e){
			this.selected();
		}.bind(this));

	},
	
	createModule: function(className, e){
		this.getTemplateData(className, function(data){
			var moduleData = Object.clone(data);
			var newTool = new MWF["PC"+className](this);
			newTool.create(moduleData, e);
		}.bind(this));
	},
	getTemplateData: function(className, callback, async){
		if (this.dataTemplate[className]){
			if (callback) callback(this.dataTemplate[className]);
		}else{
			var templateUrl = "/x_component_portal_PageDesigner/Module/"+className+"/template.json";
			MWF.getJSON(templateUrl, function(responseJSON, responseText){
				this.dataTemplate[className] = responseJSON;
				if (callback) callback(responseJSON);
			}.bind(this), async);
		}
	},
	selected: function(){
		if (this.currentSelectedModule){
			if (this.currentSelectedModule==this){
				return true;
			}else{
				this.currentSelectedModule.unSelected();
			}
		}
        if (this.propertyMultiTd){
            this.propertyMultiTd.hide();
            this.propertyMultiTd = null;
        }
		this.unSelectedMulti();

		this.currentSelectedModule = this;
		
		if (this.treeNode){
			this.treeNode.selectNode();
		}
		
		this.showProperty();
    //    this.isFocus = true;
	},
	unSelectedMulti: function(){
		while (this.selectedModules.length){
			this.selectedModules[0].unSelectedMulti();
		}
		if (this.multimoduleActionsArea) this.multimoduleActionsArea.setStyle("display", "none");
	},
	unSelectAll: function(){
		
	},
	_beginSelectMulti: function(){
		if (this.currentSelectedModule) this.currentSelectedModule.unSelected();
		this.unSelectedMulti();
		this.noSelected = true;
	},
	_completeSelectMulti: function(){
		if (this.selectedModules.length<2){
			this.selectedModules[0].selected();
		}else{
			this._showMultiActions();
		}
	},
	createMultimoduleActionsArea: function(){
		this.multimoduleActionsArea = new Element("div", {
			styles: {
				"display": "none",
		//		"width": 18*this.options.actions.length,
				"position": "absolute",
				"background-color": "#F1F1F1",
				"padding": "1px",
				"padding-right": "0px",
				"border": "1px solid #AAA",
				"box-shadow": "0px 2px 5px #999", 
				"z-index": 10001
			}
		}).inject(this.page.container, "after");
	},
	_showMultiActions: function(){
		if (!this.multimoduleActionsArea) this.createMultimoduleActionsArea();
		var firstModule = this._getFirstMultiSelectedModule();
		if (firstModule){
		//	var module = firstModule.module;
			var y = firstModule.position.y-25;
			var x = firstModule.position.x;
			this.multimoduleActionsArea.setPosition({"x": x, "y": y});
			this.multimoduleActionsArea.setStyle("display", "block");
		}
	},
	
	_getFirstMultiSelectedModule: function(){
		var firstModule = null;
		this.selectedModules.each(function(module){
			var position = module.node.getPosition(module.form.node.getOffsetParent());
			if (!firstModule){
				firstModule = {"module": module, "position": position};
			}else{
				if (position.y<firstModule.position.y){
					firstModule = {"module": module, "position": position};
				}else if (position.y==firstModule.position.y){
					if (position.x<firstModule.position.x){
						firstModule = {"module": module, "position": position};
					}
				}
			}
		});
		return firstModule;
	},
	
	
	showProperty: function(){
		if (!this.property){
			this.property = new MWF.xApplication.process.FormDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
				"path": this.options.propertyPath,
				"onPostLoad": function(){
					this.property.show();
				}.bind(this)
			});
			this.property.load();	
		}else{
			this.property.show();
		}
	},
    hideProperty: function(){
        if (this.property) this.property.hide();
    },
	
	unSelected: function(){
		this.currentSelectedModule = null;
        this.hideProperty();
	},
	
	_dragIn: function(module){
		if (!this.Component) module.inContainer = this;
		module.parentContainer = this;
		this.node.setStyles({"border": "1px solid #ffa200"});
		var copyNode = module._getCopyNode();
		copyNode.inject(this.node);
	},
	_dragOut: function(module){
		module.inContainer = null;
		module.parentContainer = null;
		this.node.setStyles(this.css.pageNode);
		this.node.setStyles(this.json.styles);
		var copyNode = module._getCopyNode();
		copyNode.setStyle("display", "none");
	},
	_dragDrop: function(module){
		this.node.setStyles(this.css.pageNode);
		this.node.setStyles(this.json.styles);
	},
    _clearNoId: function(node){
        var subNode = node.getFirst();
        while (subNode){
            var nextNode = subNode.getNext();
            if (subNode.get("MWFType")){
                if (!subNode.get("id")){
                    subNode.destroy();
                }else{
                    if (subNode) this._clearNoId(subNode);
                }
            }else{
                if (subNode) this._clearNoId(subNode);
            }
            subNode = nextNode;
        }
    },
    _getPageData: function(){
		var copy = this.node.clone(true, true);
		copy.clearStyles(true);

        this._clearNoId(copy);
		var html = copy.outerHTML;
		copy.destroy();

        this.data.json.mode = this.options.mode;
		this.data.html = html;
		return this.data;
	},
	preview: function(){
        var url = "/x_desktop/portal.html?id="+this.json.application+"&page="+this.json.id;
        window.open(url);
        // MWF.xDesktop.requireApp("process.FormDesigner", "Preview", function(){
        //
         //    if (this.options.mode=="Mobile"){
         //        this.previewBox = new MWF.xApplication.process.FormDesigner.Preview(this, {"size": {"x": "340", "y": 580, "layout":"mobile"}});
         //    }else{
         //        this.previewBox = new MWF.xApplication.process.FormDesigner.Preview(this);
         //    }
         //    this.previewBox.load();
        //
		// }.bind(this));
	},
	save: function(callback){
        this.designer.savePage();
	},
    explode: function(){
        this._getPageData();
        MWF.require("MWF.widget.Base64", null, false);
        var data = MWF.widget.Base64.encode(JSON.encode(this.data));

        MWF.require("MWF.widget.Panel", function(){
            var node = new Element("div");
            var size = this.designer.pageNode.getSize();
            var position = this.designer.pageNode.getPosition(this.designer.pageNode.getOffsetParent());

            var textarea = new Element("textarea", {
                "styles": {
                    "border": "1px solid #999",
                    "width": "770px",
                    "margin-left": "14px",
                    "margin-top": "14px",
                    "height": "580px"
                },
                "text": JSON.encode(this.data)
            }).inject(node);


            this.explodePanel = new MWF.widget.Panel(node, {
                "style": "page",
                "isResize": false,
                "isMax": false,
                "title": "",
                "width": 800,
                "height": 660,
                "top": position.y,
                "left": position.x+3,
                "isExpand": false,
                "target": this.designer.node
            });

            this.explodePanel.load();
        }.bind(this));
    },
    implode: function(){
        MWF.xDesktop.requireApp("portal.PageDesigner", "Import", function(){
            MWF.FormImport.create("O2", this);
		}.bind(this));

        // MWF.require("MWF.widget.Panel", function(){
        //     var node = new Element("div");
        //     var size = this.designer.pageNode.getSize();
        //     var position = this.designer.pageNode.getPosition(this.designer.pageNode.getOffsetParent());
        //
        //     var textarea = new Element("textarea", {
        //         "styles": {
        //             "border": "1px solid #999",
        //             "width": "770px",
        //             "margin-left": "14px",
        //             "margin-top": "14px",
        //             "height": "540px"
        //         }
        //     }).inject(node);
        //     var button = new Element("div", {
        //     	"styles": {
			// 		"margin": "10px auto",
			// 		"width": "100px",
			// 		"border-radius": "8px",
			// 		"height": "30px",
			// 		"line-height": "30px",
			// 		"text-align": "center",
			// 		"cursor": "pointer",
			// 		"color": "#ffffff",
			// 		"background-color": "#4c6b87"
			// 	},
			// 	"text": "OK"
        //     }).inject(node);
        //     button.addEvent("click", function(e){
        //         var _self = this;
        //         this.designer.confirm("warn", e, this.designer.lp.implodeConfirmTitle, this.designer.lp.implodeConfirmText, 300, 120, function(){
        //             var str = textarea.get("value");
        //             _self.implodeJsonData(str);
        //             this.close();
        //         }, function(){
        //             this.close();
        //         });
			// }.bind(this));
        //
        //     this.implodePanel = new MWF.widget.Panel(node, {
        //         "style": "page",
        //         "isResize": false,
        //         "isMax": false,
        //         "title": "",
        //         "width": 800,
        //         "height": 660,
        //         "top": position.y,
        //         "left": position.x+3,
        //         "isExpand": false,
        //         "target": this.designer.node
        //     });
        //
        //     this.implodePanel.load();
        // }.bind(this));
	},
    // implodeJsonData: function(str){
    //     if (str){
    //         //try{
    //         	debugger;
    //             var data = JSON.decode(str);
    //             if (data){
    //                 var json = data.json;
    //                 data.id = this.data.id;
    //                 data.isNewPage = this.data.isNewPage;
    //                 json.id = this.json.id;
    //                 json.name = this.json.name;
    //                 json.application = this.json.application;
    //                 json.applicationName = this.json.applicationName;
    //
    //                 this.reload(data);
    //                 this.implodePanel.closePanel();
    //             }else{
    //                 this.designer.notice(this.designer.lp.implodeError, "error");
    //             }
    //         // }catch(e){
    //         //     this.designer.notice(this.designer.lp.implodeError, "error");
    //         // }
    //     }else{
    //         this.designer.notice(this.designer.lp.implodeEmpty, "error");
    //     }
    // },
    implodeHTML: function(){
        MWF.xDesktop.requireApp("portal.PageDesigner", "Import", function(){
            MWF.FormImport.create("html", this);
        }.bind(this));
    },
    implodeOffice: function(){
        MWF.xDesktop.requireApp("portal.PageDesigner", "Import", function(){
            MWF.FormImport.create("office", this);
        }.bind(this));
    },

	setPropertiesOrStyles: function(name){
		if (name==="styles"){
			this.setCustomStyles();
		}
		if (name==="properties"){
			this.node.setProperties(this.json.properties);
		}
	},
	setCustomStyles: function(){
		var border = this.node.getStyle("border");
		this.node.clearStyles();
		this.node.setStyles(this.css.pageNode);
		var y = this.container.getSize().y-2;
		this.node.setStyle("min-height", ""+y+"px");
		
		if (this.initialStyles) this.node.setStyles(this.initialStyles);
		this.node.setStyle("border", border);

		Object.each(this.json.styles, function(value, key){
			var reg = /^border\w*/ig;
			if (!key.test(reg)){
				this.node.setStyle(key, value);
			}
		}.bind(this));
	},
	_setEditStyle: function(name, obj, oldValue){
		if (name=="name"){
			var title = this.json.name || this.json.id;
			this.treeNode.setText("<"+this.json.type+"> "+title+" ["+this.options.mode+"] ");
		}
		if (name=="id"){
			if (!this.json.name) this.treeNode.setText("<"+this.json.type+"> "+this.json.id+" ["+this.options.mode+"] ");
			this.treeNode.setTitle(this.json.id);
			this.node.set("id", this.json.id);
		}
        if (name=="pageStyleType"){
            this.templateStyles = (this.stylesList && this.json.pageStyleType) ? this.stylesList[this.json.pageStyleType] : null;
            if (oldValue) {
                var oldTemplateStyles = this.stylesList[oldValue];
                if (oldTemplateStyles){
                    if (oldTemplateStyles["page"]) this.clearTemplateStyles(oldTemplateStyles["page"]);
                }
            }
            if (this.templateStyles){
                if (this.templateStyles["page"]) this.setTemplateStyles(this.templateStyles["page"]);
            }
            this.setAllStyles();

            this.moduleList.each(function(module){
                if (oldTemplateStyles){
                    module.clearTemplateStyles(oldTemplateStyles[module.moduleName]);
                }
                module.setStyleTemplate();
                module.setAllStyles();
            }.bind(this));
        }
        if (name==="css"){
			this.reloadCss();
		}
		this._setEditStyle_custom(name, obj, oldValue);
	},

    parseCSS: function(css){
        var rex = /(url\(.*\))/g;
        var match;
        while ((match = rex.exec(css)) !== null) {
            var pic = match[0];
            var len = pic.length;
            var s = pic.substring(pic.length-2, pic.length-1);
            var n0 = (s==="'" || s==="\"") ? 5 : 4;
            var n1 = (s==="'" || s==="\"") ? 2 : 1;
            pic = pic.substring(n0, pic.length-n1);

            if ((pic.indexOf("x_processplatform_assemble_surface")!=-1 || pic.indexOf("x_portal_assemble_surface")!=-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                if (pic.indexOf("/x_processplatform_assemble_surface")!==-1){
                    pic = pic.replace("/x_processplatform_assemble_surface", pic+"/x_processplatform_assemble_surface");
                }else if (pic.indexOf("x_processplatform_assemble_surface")!==-1){
                    pic = pic.replace("x_processplatform_assemble_surface", pic+"/x_processplatform_assemble_surface");
                }
                if (pic.indexOf("/x_portal_assemble_surface")!==-1){
                    pic = pic.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (pic.indexOf("x_portal_assemble_surface")!==-1){
                    pic = pic.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
            }
            pic = "url('"+pic+"')";
            var len2 = pic.length;

            css = css.substring(0, match.index) + pic + css.substring(rex.lastIndex, css.length);
            rex.lastIndex = rex.lastIndex + (len2-len);
        }
        return css;
    },
    reloadCss: function(){
        cssText = (this.json.css) ? this.json.css.code : "";
        //var head = (document.head || document.getElementsByTagName("head")[0] || document.documentElement);

        var styleNode = $("style"+this.json.id);
        if (styleNode) styleNode.destroy();
        if (cssText){
            cssText = this.parseCSS(cssText);
            var rex = new RegExp("(.+)(?=\\{)", "g");
            var match;
            var id = this.json.id.replace(/\-/g, "");
            while ((match = rex.exec(cssText)) !== null) {
                var prefix = ".css" + id + " ";
                var rule = prefix + match[0];
                cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                rex.lastIndex = rex.lastIndex + prefix.length;
            }

            var styleNode = document.createElement("style");
            styleNode.setAttribute("type", "text/css");
            styleNode.id="style"+this.json.id;
            styleNode.inject(this.container, "before");

            if(styleNode.styleSheet){
                var setFunc = function(){
                    styleNode.styleSheet.cssText = cssText;
                };
                if(styleNode.styleSheet.disabled){
                    setTimeout(setFunc, 10);
                }else{
                    setFunc();
                }
            }else{
                var cssTextNode = document.createTextNode(cssText);
                styleNode.appendChild(cssTextNode);
            }
		}
	},

    setAllStyles: function(){
        this.setPropertiesOrStyles("styles");
        this.setPropertiesOrStyles("properties");
        this.reloadMaplist();
    },
    reloadMaplist: function(){
        if (this.property) Object.each(this.property.maplists, function(map, name){ map.reload(this.json[name]);}.bind(this));
    },
	_setEditStyle_custom: function(){
    },
    saveAsTemplete: function(){

    },
    checkModuleId: function(id, type, currentSubform){
        var fieldConflict = false;
        var elementConflict = false;
        if (this.json.moduleList[id]){
            elementConflict = true;
            if (this.options.fields.indexOf(type)!=-1 || this.options.fields.indexOf(this.json.moduleList[id].type)!=-1){
                fieldConflict = true;
            }
            return {"fieldConflict": fieldConflict, "elementConflict": elementConflict};
        }
        // if (this.subformList){
        //     Object.each(this.subformList, function(subform){
        //         if (!currentSubform || currentSubform!=subform.id){
        //             if (subform.moduleList[id]){
        //                 elementConflict = true;
        //                 if (this.options.fields.indexOf(type)!=-1 || this.options.fields.indexOf(subform.moduleList[id].type)!=-1){
        //                     fieldConflict = true;
        //                 }
        //             }
        //         }
        //     }.bind(this));
        // }
        return {"fieldConflict": fieldConflict, "elementConflict": elementConflict};
    }
	
});

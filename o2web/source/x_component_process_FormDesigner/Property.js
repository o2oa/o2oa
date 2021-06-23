MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.widget.JsonTemplate", null, false);
MWF.xApplication.process.FormDesigner.Property = MWF.FCProperty = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"path": "../x_component_process_FormDesigner/property/property.html"
	},
	
	initialize: function(module, propertyNode, designer, options){
		this.setOptions(options);
		this.module = module;
		this.form = module.form;
		this.data = module.json;
		this.data.pid = this.form.options.mode+this.form.json.id+this.data.id;
		this.htmlPath = this.options.path;
		this.designer = designer;
		this.maplists = {};
		this.propertyNode = propertyNode;
	},

	load: function(){
		if (this.fireEvent("queryLoad")){
			MWF.getRequestText(this.htmlPath, function(responseText, responseXML){
				this.htmlString = responseText;
				this.fireEvent("postLoad");
			}.bind(this));
		}
        this.propertyNode.addEvent("keydown", function(e){e.stopPropagation();});
	},
	editProperty: function(td){
	},
    getHtmlString: function(callback){
        if (!this.htmlString){
            MWF.getRequestText(this.htmlPath, function(responseText, responseXML){
                this.htmlString = responseText;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
	show: function(){
        if (!this.propertyContent){
            this.getHtmlString(function(){
                if (this.htmlString){
                    var lp;
                    if( this.options.appType === "cms" ){
                        lp = MWF.xApplication.cms.FormDesigner.LP.propertyTemplate;
                    }else{
                        lp = MWF.xApplication.process.FormDesigner.LP.propertyTemplate;
                    }
                    this.htmlString = o2.bindJson(this.htmlString, {"lp": lp});
                    // this.htmlString = o2.bindJson(this.htmlString, {"lp": MWF.xApplication.process.FormDesigner.LP.propertyTemplate});
                    this.JsonTemplate = new MWF.widget.JsonTemplate(this.data, this.htmlString);
                    this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.propertyNode);
                    // var htmlStr = this.JsonTemplate.load();
                    // this.propertyContent.injectHtml(htmlStr, {"bind": {"lp": MWF.xApplication.process.FormDesigner.LP.propertyTemplate}});
                    this.propertyContent.set("html", this.JsonTemplate.load());

                    this.setEditNodeEvent();
                    this.setEditNodeStyles(this.propertyContent);
                    this.loadPropertyTab();
                    this.loadMaplist();
                    this.loadStylesList();
                    this.loadDivTemplateType();
                    this.loadPersonInput();
                    this.loadFormFieldInput();
                    this.loadScriptArea();
                    this.loadCssArea();
                    this.loadHtmlEditorArea();
                    this.loadTreeData();
                    this.loadArrayList();
                    this.loadEventsEditor();
                    this.loadActionArea();
                    this.loadActionStylesArea();
                    this.loadHTMLArea();
                    this.loadJSONArea();
                    this.loadFormSelect();
                    this.loadSubformSelect();
                    //this.loadPageSelect();
                    this.loadWidgetSelect();
                    this.loadANNModelSelect();
                    //this.loadViewSelect();
                    this.loadValidation();
                    this.loadIconSelect();
                    this.loadLabelFlagSelect();
                    this.loadImageClipper();
                    this.loadImageFileSelect();
                    this.loadParameterEditor();
                    this.loadContextRoot();
                    this.loadUnitTypeSelector();
                    this.loadSourceTestRestful();
                    this.loadSidebarPosition();
                    this.loadViewFilter();
                    this.loadStatementFilter();
                    this.loadDocumentTempleteSelect();
                    this.loadFieldConfig();
                    this.loadHelp();
                    // this.loadScriptIncluder();
                    // this.loadDictionaryIncluder();
                    //this.testRestful();
//			this.loadScriptInput();
                    //MWF.process.widget.EventsEditor
                }
            }.bind(this));
        }else{
            this.propertyContent.setStyle("display", "block");
        }

        (new Fx.Scroll(layout.desktop.node)).toTop();

	},

	hide: function(){
		//this.JsonTemplate = null;
		//this.propertyNode.set("html", "");
        if (this.propertyContent) this.propertyContent.setStyle("display", "none");
	},
    destroy: function(){
        if (this.propertyContent){
            this.propertyContent.destroy();
        }
        MWF.release(this);
    },
	
	loadTreeData: function(){
		var arrays = this.propertyContent.getElements(".MWFTreeData");
		arrays.each(function(node){
			var title = node.get("title");
			var name = node.get("name");
			var json = this.data[name];
			if (!json) json = [];
			MWF.require("MWF.widget.TreeEditor", function(){
				var treeEditor = new MWF.widget.TreeEditor(node, {
					"title": title,
					"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
					"onChange": function(){
						this.data[name] = treeEditor.toJson();
						this.module.json[name] = this.data[name];

						this.module._refreshTree();
					}.bind(this)
				});
				treeEditor.load(json);
			}.bind(this));
            node.addEvent("keydown", function(e){e.stopPropagation();});
		}.bind(this));
	},

	loadJSONArea: function(){
		var jsonNode = this.propertyContent.getElement(".MWFJSONArea");

        if (jsonNode){
            this.propertyTab.pages.each(function(page){
                if (page.contentNode === jsonNode.parentElement){
                    page.setOptions({
                        "onShow": function(){
                            jsonNode.empty();
                            MWF.require("MWF.widget.JsonParse", function(){
                                this.json = new MWF.widget.JsonParse(this.module.json, jsonNode, null);
                                this.json.load();
                            }.bind(this));
                        }.bind(this)
                    });
                }
            }.bind(this));
        }
	},
	loadHTMLArea: function(){
		var htmlNode = this.propertyContent.getElement(".MWFHTMLArea");
		if (htmlNode){
			var copy = this.module.node.clone(true, true);
			copy.clearStyles(true);
			htmlNode.set("text", copy.outerHTML);
			copy.destroy();

			this.propertyTab.pages.each(function(page){
				if (page.contentNode == htmlNode.parentElement){
					page.setOptions({
						"onShow": function(){
							var copy = this.module.node.clone(true, true);
							copy.clearStyles(true);

                            //MWF.require("MWF.widget.HtmlEditor", function(){
                            //    debugger;
                            //    var editor = new MWF.widget.HtmlEditor(htmlNode);
                            //    editor.load(function(){
                            //        editor.editor.setValue(copy.outerHTML)
                            //    }.bind(this));
                            //}.bind(this));

                            o2.load("JSBeautifier_html", function(){
                                htmlNode.set("text", html_beautify(copy.outerHTML, {"indent_size":1}));

                                o2.require("o2.widget.ace", function(){
                                    MWF.widget.ace.load(function(){
                                        o2.load("../o2_lib/ace/src-min-noconflict/ext-static_highlight.js", function(){
                                            var highlight = ace.require("ace/ext/static_highlight");
                                            highlight(htmlNode, {mode: "ace/mode/html", theme: "ace/theme/eclipse", "fontSize": 16});
                                        }.bind(this));
                                    }.bind(this));
                                }.bind(this));

                                copy.destroy();

                            }.bind(this));


						}.bind(this)
					});
				}
			}.bind(this));
		}
	},
    loadSidebarPosition: function(){
        var nodes = this.propertyContent.getElements(".MWFSidebarReposition");
        if (nodes.length){
            nodes.each(function(node){
                node.addEvent("click", function(){
                    this.module.json.styles.top = "";
                    this.module.loadPosition();
                }.bind(this));
            }.bind(this));
        }
    },
    loadANNModelSelect: function(){
        var nodes = this.propertyContent.getElements(".MWFANNModelSelect");
        if (nodes.length){
            this.getModelList(function(){
                nodes.each(function(node){
                    var select = new Element("select").inject(node);
                    select.addEvent("change", function(e){
                        this.setValue(e.target.getParent("div").get("name"), e.target.options[e.target.selectedIndex].value, select);
                    }.bind(this));
                    this.setModelSelectOptions(node, select);

                    var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node);
                    refreshNode.addEvent("click", function(e){
                        this.getModelList(function(){
                            this.setModelSelectOptions(node, select);
                        }.bind(this), true);
                    }.bind(this));

                }.bind(this));
            }.bind(this));
        }
    },
    getModelList: function(callback, refresh){
        if (!this.models || refresh){
            var action = o2.Actions.get("x_query_assemble_designer");
            if (action.listModel) action.listModel(function(json){
                this.models = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    setModelSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"text": "none"}).inject(select);
        this.models.each(function(model){
            var option = new Element("option", {
                "text": model.name,
                "value": model.id,
                "selected": (this.data[name]==model.id)
            }).inject(select);
        }.bind(this));
    },
    loadFormSelect: function(){
        var formNodes = this.propertyContent.getElements(".MWFFormSelect");
        if (formNodes.length){
            this.getFormList(function(){
                formNodes.each(function(node){
                    var select = new Element("select").inject(node);

                    select.addEvent("change", function(e){
                        var value = e.target.options[e.target.selectedIndex].value;
                        this.setValue(e.target.getParent("div").get("name"), value, select);
                    }.bind(this));
                    this.setFormSelectOptions(node, select);

                    var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node);
                    refreshNode.addEvent("click", function(e){
                        this.getFormList(function(){
                            this.setFormSelectOptions(node, select);
                        }.bind(this), true);
                    }.bind(this));
                    //select.addEvent("click", function(e){
                    //    this.setFormSelectOptions(node, select);
                    //}.bind(this));
                }.bind(this));
            }.bind(this));
        }
    },
    setFormSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"text": "none"}).inject(select);
        this.forms.each(function(form){
            if( this.form.json.id !== form.id ){
                var option = new Element("option", {
                    "text": form.name,
                    "value": form.id,
                    "selected": (this.data[name]==form.id)
                }).inject(select);
            }
        }.bind(this));
    },
    getFormList: function(callback, refresh){
        if (!this.forms || refresh){
            this.form.designer.actions.listForm(this.form.designer.application.id, function(json){
                this.forms = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },


    loadSubformSelect: function(){
        var subformContainers = this.propertyContent.getElements(".MWFSubFormSelectContainer");
        if (subformContainers.length){
            subformContainers.each( function( container ){
                var appSelectNode = container.getElement(".MWFSubformAppSelect");
                var formSelectNode = container.getElement(".MWFSubformSelect");
                var formSelect;

                var appNodeName = appSelectNode.get("name");
                var formNodeName = formSelectNode.get("name");

                this.loadProcessApplictionSelect( appSelectNode, appNodeName, function( apps ){
                    var oldValue = this.data[appNodeName] || "";
                    this.data[appNodeName] = !apps.length ? "" : apps[0].data.id;
                    if( oldValue !== this.data[appNodeName] ){
                        this.getSubFormList(function(){
                            this.setSubformSelectOptions(formSelectNode, formSelect);
                            formSelect.fireEvent("change");
                        }.bind(this), true, appNodeName);
                    }
                }.bind(this));
                formSelect = this._loadSubformSelect( formSelectNode, formNodeName,  appNodeName ) ;
            }.bind(this))
        }
    },
    loadProcessApplictionSelect : function( node, appNodeName, callback ){
        var application = appNodeName ? this.data[appNodeName] : "";
        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function() {
            new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                "title" : this.form.designer.lp.selectApplication,
                "type": "application",
                "count" : 1,
                "names": application ? [ {id : application} ] : [],
                "onChange": function (apps) {
                    callback(apps)
                }.bind(this)
            });
        }.bind(this))
    },
    _loadSubformSelect : function( node, formNodeName, appNodeName ){
        var select;
        this.getSubFormList(function(){
            select = new Element("select").inject(node);

            select.addEvent("change", function(e){
                var value = select.options[select.selectedIndex].value;
                this.setValue(formNodeName, value, select);
            }.bind(this));
            this.setSubformSelectOptions(node, select);

            var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node);
            refreshNode.addEvent("click", function(e){
                this.getSubFormList(function(){
                    this.setSubformSelectOptions(node, select);
                }.bind(this), true, appNodeName);
            }.bind(this));
        }.bind(this), false, appNodeName );
        return select;
    },
    setSubformSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"text": "none"}).inject(select);
        this.subforms.each(function(subforms){
            if( this.form.json.id !== subforms.id ){
                var option = new Element("option", {
                    "text": subforms.name,
                    "value": subforms.id,
                    "selected": (this.data[name]==subforms.id)
                }).inject(select);
            }
        }.bind(this));
    },
    getSubFormList: function(callback, refresh, appNodeName){
        var application = appNodeName ? this.data[appNodeName] : "";
        if (!this.subforms || refresh){
            this.form.designer.actions.listForm( application || this.form.designer.application.id, function(json){
                this.subforms = json.data;
                if (callback) callback();
            }.bind(this), null, false);
        }else{
            if (callback) callback();
        }
    },


    loadPageSelect: function(){
        var pageNodes = this.propertyContent.getElements(".MWFPageSelect");
        if (pageNodes.length){
            this.getPageList(function(){
                pageNodes.each(function(node){
                    var select = new Element("select").inject(node);
                    select.addEvent("change", function(e){
                        var value = e.target.options[e.target.selectedIndex].value;
                        this.setValue(e.target.getParent("div").get("name"), value, select);
                    }.bind(this));
                    this.setPageSelectOptions(node, select);

                    var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node);
                    refreshNode.addEvent("click", function(e){
                        this.getPageList(function(){
                            this.setPageSelectOptions(node, select);
                        }.bind(this), true);
                    }.bind(this));
                }.bind(this));
            }.bind(this));
        }
    },
    setPageSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"text": "none"}).inject(select);
        this.pages.each(function(page){
            if( this.form.json.id !== page.id ){
                var option = new Element("option", {
                    "text": page.name,
                    "value": page.id,
                    "selected": (this.data[name]==page.id)
                }).inject(select);
            }
        }.bind(this));
    },
    getPageList: function(callback, refresh){
        if (!this.pages || refresh){
            this.form.designer.actions.listPage(this.form.designer.application.id, function(json){
                this.pages = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    loadWidgetSelect: function(){
        var widgetNodes = this.propertyContent.getElements(".MWFWidgetSelect");
        if (widgetNodes.length){
            this.getWidgetList(function(){
                widgetNodes.each(function(node){
                    var select = new Element("select").inject(node);
                    select.addEvent("change", function(e){
                        var value = e.target.options[e.target.selectedIndex].value;
                        this.setValue(e.target.getParent("div").get("name"), value, select);
                    }.bind(this));
                    this.setWidgetSelectOptions(node, select);

                    var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node);
                    refreshNode.addEvent("click", function(e){
                        this.getWidgetList(function(){
                            this.setWidgetSelectOptions(node, select);
                        }.bind(this), true);
                    }.bind(this));
                }.bind(this));
            }.bind(this));
        }
    },
    setWidgetSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"text": "none"}).inject(select);
        this.widgets.each(function(widget){
            if( this.form.json.id !== widget.id ){
                var option = new Element("option", {
                    "text": widget.name,
                    "value": widget.id,
                    "selected": (this.data[name]==widget.id)
                }).inject(select);
            }
        }.bind(this));
    },
    getWidgetList: function(callback, refresh){
        if (!this.widgets || refresh){
            this.form.designer.actions.listWidget(this.form.designer.application.id, function(json){
                this.widgets = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

//	clearStyles: function(node){
//		node.removeProperty("style");
//		var subNode = node.getFirst();
//		while (subNode){
//			this.clearStyles(subNode);
//			subNode = subNode.getNext();
//		}
//	},
    loadDocumentTempleteSelect: function(){
        var nodes = this.propertyContent.getElements(".MWFDocumentTempleteSelect");
        if (nodes.length){
            o2.getJSON("../x_component_process_FormDesigner/Module/Documenteditor/templete/templete.json", function(json){
                nodes.each(function(node){
                    var name = node.get("name");
                    Object.each(json, function(o, k){
                        new Element("option", {
                            "text": o.name,
                            "value": k,
                            "selected": (this.data[name]==k)
                        }).inject(node);
                    }.bind(this));
                    node.addEvent("change", function(e){
                        var oldValue = this.data[name];
                        var value = e.target.options[e.target.selectedIndex].value;
                        var name = e.target.options[e.target.selectedIndex].get("text");
                        this.changeJsonDate([name], value);
                        this.changeData(name, node, oldValue);
                    }.bind(this));
                }.bind(this));
            }.bind(this));
        }
    },
    loadStatementFilter: function(){
	    debugger;
        var nodes = this.propertyContent.getElements(".MWFStatementFilter");
        var filtrData = this.data.filterList;
        nodes.each(function(node){
            MWF.xDesktop.requireApp("query.StatementDesigner", "widget.ViewFilter", function(){
                var _slef = this;
                this.viewFilter = new MWF.xApplication.query.StatementDesigner.widget.ViewFilter(node, this.form.designer, {"filtrData": filtrData, "customData": null, "parameterData": null}, {
                    "statementId" : this.data.queryStatement ? this.data.queryStatement.id : "",
                    "withForm" : true,
                    "onChange": function(ids){
                        var data = this.getData();
                        _slef.changeJsonDate(["filterList"], data.filterData);
                        //_slef.changeJsonDate(["data", "customFilterEntryList"], data.customData);
                    }
                });
            }.bind(this));
        }.bind(this));
    },
    loadViewFilter: function(){
        var nodes = this.propertyContent.getElements(".MWFViewFilter");
        var filtrData = this.data.filterList;
        nodes.each(function(node){
            MWF.xDesktop.requireApp("query.ViewDesigner", "widget.ViewFilter", function(){
                var _slef = this;
                new MWF.xApplication.query.ViewDesigner.widget.ViewFilter(node, this.form.designer, {"filtrData": filtrData, "customData": null}, {
                    "onChange": function(ids){
                        var data = this.getData();
                        _slef.changeJsonDate(["filterList"], data.data);
                        //_slef.changeJsonDate(["data", "customFilterEntryList"], data.customData);
                    }
                });
            }.bind(this));
        }.bind(this));
    },
    loadViewSelect: function(){
        var viewNodes = this.propertyContent.getElements(".MWFViewSelect");
        if (viewNodes.length){
            this.getViewList(function(){
                viewNodes.each(function(node){
                    var select = new Element("select").inject(node);
                    select.addEvent("change", function(e){
                        var viewId = e.target.options[e.target.selectedIndex].value;
                        var viewName = e.target.options[e.target.selectedIndex].get("text");
                        this.setValue(e.target.getParent("div").get("name"), viewId);
                        this.setValue(e.target.getParent("div").get("name")+"Name", viewName);
                    }.bind(this));
                    this.setViewSelectOptions(node, select);

                    var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node);
                    refreshNode.addEvent("click", function(e){
                        this.getViewList(function(){
                            this.setViewSelectOptions(node, select);
                        }.bind(this), true);
                    }.bind(this));
                    //select.addEvent("click", function(e){
                    //    this.setFormSelectOptions(node, select);
                    //}.bind(this));
                }.bind(this));
            }.bind(this));
        }
    },
    setViewSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"text": "none"}).inject(select);
        this.views.each(function(view){
            var option = new Element("option", {
                "text": view.name,
                "value": view.id,
                "selected": (this.data[name]==view.id)
            }).inject(select);
        }.bind(this));
    },
    getViewList: function(callback, refresh){
        if (!this.views || refresh){
            this.form.designer.actions.listView(this.form.designer.application.id, function(json){
                this.views = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    loadValidation: function(){
        var nodes = this.propertyContent.getElements(".MWFValidation");
        if (nodes.length){
            nodes.each(function(node){
                var name = node.get("name");
                MWF.xDesktop.requireApp("process.FormDesigner", "widget.ValidationEditor", function(){
                    var validationEditor = new MWF.xApplication.process.FormDesigner.widget.ValidationEditor(node, this.designer, {
                        "onChange": function(){
                            var data = validationEditor.getValidationData();
                            this.data[name] = data;
                        }.bind(this)
                    });
                    validationEditor.load(this.data[name])
                }.bind(this));

                //new MWF.xApplication.process.FormDesigner.widget.ValidationEditor(node, this.designer);
            }.bind(this));
        }
    },

    loadFieldConfig: function(){
        var nodes = this.propertyContent.getElements(".MWFFieldConfigArea");
        if (nodes.length){
            nodes.each(function(node){
                debugger;
                var name = node.get("name");
                var data;
               if( this.data[name] ){
                   data = this.data[name];
               }else{
                   data = this.data[name] = [];
               }
                MWF.xDesktop.requireApp("process.FormDesigner", "widget.FiledConfigurator", function(){
                    var filedConfigurator = new MWF.xApplication.process.FormDesigner.widget.FiledConfigurator(node, this.designer, {
                        "onChange": function(){
                            debugger;
                            this.data[name] = filedConfigurator.getData();
                        }.bind(this)
                    }, data);
                    filedConfigurator.load()
                }.bind(this));
            }.bind(this));
        }
    },

    loadIconSelect: function(){
        var nodes = this.propertyContent.getElements(".MWFIcon");
        if (nodes.length){
            nodes.each(function(node){
                var id = node.get("name");
                var icon = this.data[id];
                var iconNode = new Element("div", {"styles": this.form.css.processIconNode}).inject(node);
                if (icon) iconNode.setStyles({"background": "url("+icon+") center center no-repeat"});
                var selectNode = new Element("div", {"styles": this.form.css.processIconSelectNode, "text": this.form.designer.lp.selectIcon}).inject(node);
                selectNode.addEvent("click", function(){
                    this.selectIcon(node);
                }.bind(this));


            }.bind(this));
        }
    },
    selectIcon: function(node){
        if (!node.iconMenu){
            var iconSelectMenu = new MWF.widget.Menu(node, {"event": "click", "style": "processIcon"});
            iconSelectMenu.load();
            node.iconMenu = iconSelectMenu;
            var _self = this;
            for (var i=0; i<=48; i++){
                var icon = "../x_component_process_ProcessManager/$Explorer/default/processIcon/process_icon_"+i+".png";
                var item = iconSelectMenu.addMenuItem("", "click", function(){
                    var id = node.get("name");
                    var src = this.item.getElement("img").get("src");

                    _self.data[id] = src;
                    node.getFirst("div").setStyle("background-image", "url("+src+")");
                }, icon);
                item.iconName = icon;
            }
        }
    },

    loadLabelFlagSelect: function(){
        var nodes = this.propertyContent.getElements(".MWFLabelFlag");
        if (nodes.length){
            nodes.each(function(node){
                var id = node.get("name");
                var icon = this.data[id];
                var iconNode = new Element("div", {"styles": this.form.css.labelFlagNode}).inject(node);
                if (icon) iconNode.setStyles({"background": "url("+icon+") center center no-repeat"});

                var selectNode = new Element("div", {"styles": this.form.css.processIconSelectNode, "text": this.form.designer.lp.empty}).inject(node);
                selectNode.addEvent("click", function(e){
                    var id = node.get("name");
                    this.data[id] = "";
                    node.getFirst("div").setStyle("background-image", "");
                    this.changeData(id);
                    e.stopPropagation();
                }.bind(this));

                var selectNode = new Element("div", {"styles": this.form.css.processIconSelectNode, "text": this.form.designer.lp.select}).inject(node);
                selectNode.addEvent("click", function(){
                    this.selectLabelFlag(node);
                }.bind(this));
            }.bind(this));
        }
    },
    selectLabelFlag: function(node){
        if (!node.iconMenu){
            var iconSelectMenu = new MWF.widget.Menu(node, {"event": "click", "style": "labelFlag"});
            iconSelectMenu.load();
            node.iconMenu = iconSelectMenu;
            var _self = this;
            for (var i=1; i<=21; i++){
                var icon = "../x_component_process_FormDesigner/Module/Label/default/icon/flag/"+i+".png";
                var item = iconSelectMenu.addMenuItem("", "click", function(){
                    var id = node.get("name");
                    var src = this.item.getElement("img").get("src");
                    _self.data[id] = src;
                    node.getFirst("div").setStyle("background-image", "url("+src+")");
                    _self.changeData(id);
                }, icon);
                item.iconName = icon;
            }
        }
    },

    loadImageFileSelect: function(){
        // var nodes = this.propertyContent.getElements(".MWFImageFileSelect");
        // if (nodes.length){
        //
        //
        //
        //
        //     this.getFileList(function(){
        //         nodes.each(function(node){
        //             var select = new Element("select").inject(node);
        //             select.addEvent("change", function(e){
        //                 this.setValue(e.target.getParent("div").get("name"), e.target.options[e.target.selectedIndex].value, select);
        //
        //             }.bind(this));
        //             this.setFileSelectOptions(node, select);
        //
        //             var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node);
        //             refreshNode.addEvent("click", function(e){
        //                 this.getFileList(function(){
        //                     this.setFileSelectOptions(node, select);
        //                 }.bind(this), true);
        //             }.bind(this));
        //         }.bind(this));
        //     }.bind(this));
        // }
    },
    setFileSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"text": "none"}).inject(select);
        this.files.each(function(file){
            var option = new Element("option", {
                "text": file.name,
                "value": file.id,
                "selected": (this.data[name]==file.id)
            }).inject(select);
        }.bind(this));
    },
    getFileList: function(callback, refresh){
        if (!this.files || refresh){
            this.form.designer.actions.listFile(this.form.designer.application.id, function(json){
                this.files = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    loadImageClipper: function(){
        var nodes = this.propertyContent.getElements(".MWFImageClipper");
        if (nodes.length){
            nodes.each(function(node){
                var id = node.get("name");
                var selectNode = new Element("div", {"styles": this.form.css.processIconSelectNode, "text": this.form.designer.lp.selectImage}).inject(node);
                selectNode.addEvent("click", function(){
                    this.selectImage(node, id);
                }.bind(this));
            }.bind(this));
        }
    },
    createUploadFileAreaNode: function(node, name){
        this.uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" multiple type=\"file\" accept=\"images/*\" />";
        this.uploadFileAreaNode.set("html", html);

        this.fileUploadNode = this.uploadFileAreaNode.getFirst();
        this.fileUploadNode.addEvent("change", function(){
            //var fileId = attachment.data.id;

            var files = this.fileUploadNode.files;
            if (files.length){
                var count = files.length;
                for (var i = 0; i < files.length; i++) {
                    var file = files.item(i);

                    var formData = new FormData();
                    formData.append('file', file);

                    MWF.xDesktop.uploadImage(
                        this.form.json.id,
                        (this.module.form.moduleType=="page") ? "portalPage" : "processPlatformForm",
                        formData,
                        file,
                        function(json){
                            var id = json.id;
                            var src = MWF.xDesktop.getImageSrc(id);
                            var data = {"imageSrc": src, "imageId": id};
                            this.changeJsonDate(name, data);
                            this.changeData(name, node, null);
                        }.bind(this)
                    );
                }
            }
        }.bind(this));
    },
    selectImage: function(node, name){
        if (!this.uploadFileAreaNode){
            this.createUploadFileAreaNode(node, name);
        }
        //this.fileUploadNode.set("accept", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        this.fileUploadNode.set("multiple", false);

        var fileNode = this.uploadFileAreaNode.getFirst();
        fileNode.set("accept", ".png,.jpg,.bmp,.gif,.jpeg,.jpe");
        fileNode.click();

        //MWF.xDesktop.requireApp("process.FormDesigner", "widget.ImageClipper", function(){
        //    var size = this.module.node.getSize();
        //    var image = new MWF.xApplication.process.FormDesigner.widget.ImageClipper(this.designer, {
        //        "title": this.form.designer.lp.selectImage,
        //        "width": (this.data.styles.width) ? size.x : 0,
        //        "height": (this.data.styles.height) ? size.y : 0,
        //        "imageUrl" : this.data.imageSrc,
        //        "reference" : this.form.json.id,
        //        "referenceType": (this.module.form.moduleType=="page") ? "portalPage" : "processPlatformForm",
        //        "onChange": function(){
        //            var data = {"imageSrc": image.imageSrc, "imageId": image.imageId};
        //            this.changeJsonDate(name, data);
        //            this.changeData(name, node, null);
        //        }.bind(this)
        //    });
        //    image.load(this.data[name])
        //}.bind(this));
    },
	
	loadEventsEditor: function(){
		var events = this.propertyContent.getElement(".MWFEventsArea");
		if (events){
			var name = events.get("name");
			var eventsObj = this.data[name];
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.EventsEditor", function(){
				var eventsEditor = new MWF.xApplication.process.FormDesigner.widget.EventsEditor(events, this.designer, {
					//"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.designer.formContentNode  || this.designer.pageContentNode
				});
				eventsEditor.load(eventsObj, this.data, name);
			}.bind(this));
		}
	},
    testRestful: function(){
        var node = this.propertyContent.getElements(".MWFTestRestful");
        if (node){
            var resultNode = node.getLast();
            node.getFirst().addEvent("click", function(){

            }.bind(this));
        }
    },
    loadContextRoot: function(){
        var nodes = this.propertyContent.getElements(".MWFContextRoot");
        if (nodes){
            nodes.each(function(node){
                var name = node.get("name");
                var selects = node.getElements("select");
                var rootSelect = selects[0];
                var actionSelect = selects[1];
                var methodSelect = selects[2];
                var pathInput = node.getElement("input");
                //var select = new Element("select").inject(node);
                //var methodSelect = new Element("select").inject(node);
debugger;
                var getValue = function(){
                    var v;
                    try {
                        v = JSON.parse(this.data[name]);
                    }catch(e){
                        v = {"root": this.data[name], "action":"", "method": "", "uri": ""};
                    }
                    return v
                }.bind(this);
                var value = getValue();

                var resetRootSelects = function(){
                    var root = rootSelect.options[rootSelect.selectedIndex].value;
                    var action = o2.Actions.load(root);
                    actionSelect.empty();
                    methodSelect.empty();
                    pathInput.set("value", "");
                    var value = getValue();
                    Object.each(action, function(o, key){
                        var option = new Element("option", {"value": key, "text": key, "selected": (value.action==key)}).inject(actionSelect);
                    });
                    return JSON.stringify({"root": root, "action":"", "method": "", "uri": ""});
                };
                var resetActionSelects = function(){
                    var root = rootSelect.options[rootSelect.selectedIndex].value;
                    var actionName = actionSelect.options[actionSelect.selectedIndex].value;
                    var action = o2.Actions.load(root);
                    methodSelect.empty();
                    pathInput.set("value", "");
                    var value = getValue();
                    Object.each(action[actionName].action.actions, function(o, key){
                        var option = new Element("option", {"value": key, "text": key, "selected": (value.method==key)}).inject(methodSelect);
                    });
                    return JSON.stringify({"root": root, "action":actionName, "method": "", "uri": ""});
                };
                var resetMethodSelects = function(){
                    var root = rootSelect.options[rootSelect.selectedIndex].value;
                    var actionName = actionSelect.options[actionSelect.selectedIndex].value;
                    var methodName = methodSelect.options[methodSelect.selectedIndex].value;
                    var action = o2.Actions.load(root);
                    var uri = action[actionName].action.actions[methodName].uri;
                    pathInput.set("value", uri);
                    return JSON.stringify({"root": root, "action":actionName, "method": methodName, "uri": uri});
                };

                Object.each(layout.serviceAddressList, function(v, key){
                    var option = new Element("option", {"value": key, "text": v.name, "selected": (value.root==key)}).inject(rootSelect);
                }.bind(this));
                resetRootSelects()
                resetActionSelects();
                resetMethodSelects();

                rootSelect.addEvent("change", function(){
                    resetRootSelects();
                    resetActionSelects();
                    var data = resetMethodSelects();
                    this.changeJsonDate(name, data);
                    this.changeData(name, node, value);
                }.bind(this));

                actionSelect.addEvent("change", function(){
                    resetActionSelects();
                    var data = resetMethodSelects();
                    this.changeJsonDate(name, data);
                    this.changeData(name, node, value);
                }.bind(this));

                methodSelect.addEvent("change", function(){
                    var data = resetMethodSelects();
                    this.changeJsonDate(name, data);
                    this.changeData(name, node, value);
                }.bind(this));

            }.bind(this));
        }
    },
    loadSourceTestRestful: function(){
        var nodes = this.propertyContent.getElements(".MWFSourceTestRestful");
        if (nodes.length){
            nodes.each(function(node){
                var button = node.getFirst();
                var content = node.getLast();
                //var button = new Element("input", {"type": "button", "value": "Test"}).inject(node);
                button.addEvent("click", function(e){
                    this.testSourceRestful(content);
                }.bind(this));
            }.bind(this));
        }
    },
    testSourceRestful: function(content){
	    var service;
        try {
            service = JSON.parse(this.module.json.contextRoot);
        }catch(e){
            service = {"root": this.module.json.contextRoot, "action":"", "method": "", "url": ""};
        }

        var address = this._getO2Address(service.root);
        var uri = this._getO2Uri(this.module, address);
        this._invoke(this.module, uri, function(json){
            content.empty();
            MWF.require("MWF.widget.JsonParse", function(){
                var jsonParse = new MWF.widget.JsonParse(json, content, null);
                jsonParse.load();
            }.bind(this));
        }.bind(this));
    },
    _getO2Address: function(contextRoot){
        var addressObj = layout.serviceAddressList[contextRoot];
        var address = "";
        if (addressObj){
            address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context;
        }else{
            var host = layout.desktop.centerServer.host || window.location.hostname;
            var port = layout.desktop.centerServer.port;
            address = layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port)+"/x_program_center";
        }
        return address;
    },
    _getO2Uri: function(module, address){
        //var uri = module.json.path || module.json.selectPath;
        var uri = module.json.path;
        var pars = {};

        MWF.require("MWF.xScript.Macro", null, false);
        var macro = new MWF.Macro["PageContext"]({"businessData": {}, "json": {}, "options": {}});

        if (module.json.parameters){
            Object.each(module.json.parameters, function(v, key){
                if (uri.indexOf("{"+key+"}")!==-1){
                    var reg = new RegExp("{"+key+"}", "g");
                    uri = uri.replace(reg, encodeURIComponent((v && v.code) ? (macro.exec(v.code, this) || "") : v));
                }else{
                    pars[key] = v;
                }
            }.bind(this));
        }

        var data = null;
        if (module.json.requestBody){
            if (module.json.requestBody.code){
                data = macro.exec(module.json.requestBody.code, this)
            }
        }

        if (module.json.httpMethod==="GET" || module.json.httpMethod==="OPTIONS" || module.json.httpMethod==="HEAD" || module.json.httpMethod==="DELETE"){
            var tag = "?";
            if (uri.indexOf("?")!==-1) tag = "&";
            Object.each(pars, function(v, k){
                var value = (v && v.code) ? (macro.exec(v.code, this) || "") : v;
                uri = uri+tag+k+"="+value;
            }.bind(this));
        }else{
            Object.each(pars, function(v, k){
                if (!data) data = {};
                var value = (v && v.code) ? (macro.exec(v.code, this) || "") : v;
                data[k] = value;
            }.bind(this));
        }
        this.body = data;
        return {"uri": address+uri, "body": data};
    },
    _invoke: function(module, uri, callback){
        MWF.restful(module.json.httpMethod, uri.uri, JSON.encode(uri.body), function(json){
            //this.data = json;
            if (callback) callback(json);
        }.bind(this), true, true);
    },

    loadUnitTypeSelector: function(){
        var nodes = this.propertyContent.getElements(".MWFFormUnitTypeSelector");
        if (nodes.length){
            this.getUnitTypeList(function(){
                nodes.each(function(node){
                    var select = new Element("select").inject(node);
                    select.addEvent("change", function(e){
                        this.setValue(e.target.getParent("div").get("name"), e.target.options[e.target.selectedIndex].value);
                    }.bind(this));
                    this.setUnitTypeSelectOptions(node, select);
                    this.setValue(select.getParent("div").get("name"), select.options[select.selectedIndex].value);
                    // var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node);
                    // refreshNode.addEvent("click", function(e){
                    //     this.getUnitTypeList(function(){
                    //         this.setUnitTypeSelectOptions(node, select);
                    //     }.bind(this), true);
                    // }.bind(this));

                }.bind(this));
            }.bind(this));
        }
    },

    setUnitTypeSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"value":"all", "text": this.form.designer.lp.all, "selected": (!this.data[name] || this.data[name]==="all")}).inject(select);
        this.unitTypeList.each(function(unitType){
            var option = new Element("option", {
                "text": unitType,
                "value": unitType,
                "selected": (this.data[name]===unitType)
            }).inject(select);
        }.bind(this));
    },
    getUnitTypeList: function(callback, refresh){
        if (!this.unitTypeList || refresh){
            //MWF.xDesktop.requireApp("Org", "Actions.RestActions", function(){
            //    var action = new MWF.xApplication.Org.Actions.RestActions();
                var action = MWF.Actions.get("x_organization_assemble_control");
                action.listUnitType(function(json){
                    this.unitTypeList = json.data.valueList;
                    if (callback) callback();
                }.bind(this));
            //}.bind(this));
        }else{
            if (callback) callback();
        }
    },

    loadParameterEditor: function(){
        var pars = this.propertyContent.getElements(".MWFParameterArea");
        if (pars){
            pars.each(function(par){
                var name = par.get("name");
                if (!this.data[name]) this.data[name] = {};
                var parObj = this.data[name];
                MWF.xDesktop.requireApp("process.FormDesigner", "widget.ParameterEditor", function(){
                    var parameterEditor = new MWF.xApplication.process.FormDesigner.widget.ParameterEditor(par, this.designer, {
                        //"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                        "maxObj": this.designer.formContentNode
                    });
                    parameterEditor.load(parObj, this.data, name);
                }.bind(this));
            }.bind(this));

        }
    },
	
	loadArrayList: function(){
		var arrays = this.propertyContent.getElements(".MWFArraylist");
		arrays.each(function(node){
			var title = node.get("title");
			var name = node.get("name");
			var arr = this.data[name];
			if (!arr) arr = [];
			MWF.require("MWF.widget.Arraylist", function(){
				var arraylist = new MWF.widget.Arraylist(node, {
					"title": title,
					"onChange": function(){
						this.data[name] = arraylist.toArray();
					}.bind(this)
				});
				arraylist.load(arr);
			}.bind(this));
            node.addEvent("keydown", function(e){e.stopPropagation();});
		}.bind(this));
	},

    loadHtmlEditorArea: function(){
        var htmlAreas = this.propertyContent.getElements(".MWFHtmlEditorArea");
        htmlAreas.each(function(node){
            var title = node.get("title");
            var name = node.get("name");
            var scriptContent = this.data[name];
            MWF.require("MWF.widget.HtmlEditorArea", function(){
                var htmlArea = new MWF.widget.HtmlEditorArea(node, {
                    "title": title,
                    //"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.designer.formContentNode || this.designer.pageContentNode,
                    "onChange": function(){
                        this.data[name] = htmlArea.getValue();
                        this.changeData(name);
                        htmlArea.isChanged = true;
                    }.bind(this),
                    // "onBlur": function(){
                    //     if (htmlArea.isChanged){
                    //         this.changeData(name, node, "");
                    //         htmlArea.isChanged = false;
                    //     }
                    // }.bind(this),
                    "onSave": function(){
                        this.designer.saveForm();
                    }.bind(this)
                });
                htmlArea.load({"code": scriptContent});
            }.bind(this));

        }.bind(this));
    },
    loadStylesList: function(){
        var _self = this;
        var styleSelNodes = this.propertyContent.getElements(".MWFFormStyle");
        styleSelNodes.each(function(node){
            if (this.module.form.stylesList){
                if (!this.data.formStyleType) this.data.formStyleType = "default";
                var mode = ( this.form.options.mode || "" ).toLowerCase() === "mobile" ? "mobile" : "pc";
                Object.each(this.module.form.stylesList, function(s, key){
                    if( s.mode.contains( mode ) ){
                        new Element("option", {
                            "text": s.name,
                            "value": key,
                            "selected": ((!this.data.formStyleType && key=="default") || (this.data.formStyleType==key))
                        }).inject(node)
                    }
                }.bind(this));
            }else{
                node.getParent("tr").setStyle("display", "none");
            }

            var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node, "after");
            refreshNode.addEvent("click", function(e){
                _self.changeData(this.get("name"), this );
            }.bind(node));
        }.bind(this));
    },
    // loadStylesList: function(){
	//     var _self = this;
    //     var styleSelNodes = this.propertyContent.getElements(".MWFFormStyle");
    //     styleSelNodes.each(function(node){
    //         if (this.module.form.stylesList){
    //             if (!this.data.formStyleType) this.data.formStyleType = "default";
    //             var mode = ( this.form.options.mode || "" ).toLowerCase() === "mobile" ? "mobile" : "pc";
    //             Object.each(this.module.form.stylesList, function(s, key){
    //                 if( s.mode.contains( mode ) ){
    //                     new Element("option", {
    //                         "text": s.name,
    //                         "value": key,
    //                         "selected": ((!this.data.formStyleType && key=="default") || (this.data.formStyleType==key))
    //                     }).inject(node)
    //                 }
    //             }.bind(this));
    //         }else{
    //             node.getParent("tr").setStyle("display", "none");
    //         }
    //
    //         var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node, "after");
    //         refreshNode.addEvent("click", function(e){
    //             _self.changeData(this.get("name"), this );
    //         }.bind(node));
    //     }.bind(this));
    // },
    loadDivTemplateType: function(){
        var nodes = this.propertyContent.getElements(".MWFDivTemplate");
        if (nodes.length){
            var keys = [];
            //if (this.module.form.stylesList) {
            //    if (this.module.form.stylesList[this.module.form.json.formStyleType]){
            //        var styles = this.module.form.stylesList[this.module.form.json.formStyleType][this.module.moduleName];
            //        if (styles) {
            //            Object.each(styles, function (v, k) {
            //                keys.push(k);
            //            }.bind(this));
            //        }
            //    }
            //}
            if (this.module.form.templateStyles && this.module.form.templateStyles[this.module.moduleName]) {
                var styles = this.module.form.templateStyles[this.module.moduleName];
                if (styles) {
                    Object.each(styles, function (v, k) {
                        keys.push(k);
                    }.bind(this));
                }
            }

            nodes.each(function(node){
                node.empty();
                new Element("option", {
                    "text": "default",
                    "value": "default",
                    "selected": (!this.data.templateType || this.data.templateType=="default")
                }).inject(node);
                if (keys.length){
                    keys.each(function(k){
                        new Element("option", {
                            "text": styles[k].name,
                            "value": k,
                            "selected": (this.data.templateType==k)
                        }).inject(node)
                    }.bind(this));
                }else{
                    node.getParent("tr").setStyle("display", "none");
                }
            }.bind(this));
        }

    },
    loadPersonInput: function(){
        var personIdentityNodes = this.propertyContent.getElements(".MWFPersonIdentity");
        var personUnitNodes = this.propertyContent.getElements(".MWFPersonUnit");
        var dutyNodes = this.propertyContent.getElements(".MWFDutySelector");
        var dutyNameNodes = this.propertyContent.getElements(".MWFPersonDuty");
        var viewNodes = this.propertyContent.getElements(".MWFViewSelect");
        var cmsviewNodes = this.propertyContent.getElements(".MWFCMSViewSelect");
        var queryviewNodes = this.propertyContent.getElements(".MWFQueryViewSelect");
        var queryStatementNodes = this.propertyContent.getElements(".MWFQueryStatementSelect");
        var querystatNodes = this.propertyContent.getElements(".MWFQueryStatSelect");
        var fileNodes = this.propertyContent.getElements(".MWFImageFileSelect");
        var processFileNodes = this.propertyContent.getElements(".MWFProcessImageFileSelect");
        var scriptNodes = this.propertyContent.getElements(".MWFScriptSelect");
        var formStyleNodes = this.propertyContent.getElements(".MWFFormStyleSelect");
        var dictionaryNodes = this.propertyContent.getElements(".MWFDictionarySelect");
        var queryImportModelNodes = this.propertyContent.getElements(".MWFQueryImportModelSelect");


        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function(){
            personIdentityNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "identity",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));

            personUnitNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "unit",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));
            dutyNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "duty",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.addDutyItem(node, ids);}.bind(this),
                    "onRemoveDuty": function(item){this.removeDutyItem(node, item);}.bind(this)
                });
            }.bind(this));

            dutyNameNodes.each(function(node){
                debugger;
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "dutyName",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));

            viewNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "View",
                    "count": 1,
                    "names": [this.data[node.get("name")]],
                    "onChange": function(ids){this.saveViewItem(node, ids);}.bind(this)
                });
            }.bind(this));
            cmsviewNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "CMSView",
                    "count": 1,
                    "names": [this.data[node.get("name")]],
                    "onChange": function(ids){this.saveViewItem(node, ids);}.bind(this)
                });
            }.bind(this));
            queryviewNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "QueryView",
                    "count": 1,
                    "names": [this.data[node.get("name")]],
                    "onChange": function(ids){this.saveViewItem(node, ids);}.bind(this)
                });
            }.bind(this));

            queryStatementNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "QueryStatement",
                    "count": 1,
                    "names": [this.data[node.get("name")]],
                    "onChange": function(ids){this.saveViewItem(node, ids);}.bind(this)
                });
            }.bind(this));

            querystatNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "QueryStat",
                    "count": 1,
                    "names": [this.data[node.get("name")]],
                    "onChange": function(ids){this.saveViewItem(node, ids);}.bind(this)
                });
            }.bind(this));

            queryImportModelNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "QueryImportModel",
                    "count": 1,
                    "names": [this.data[node.get("name")]],
                    "onChange": function(ids){this.saveViewItem(node, ids);}.bind(this)
                });
            }.bind(this));

            scriptNodes.each(function(node){
                var ps = new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "Script",
                    "count": node.dataset["count"] || 1,
                    "names": (!node.dataset["count"] || node.dataset["count"].toInt()==1) ? [this.data[node.get("name")]] : this.data[node.get("name")],
                    "onChange": function(ids){
                        this.saveScriptSelectItem(node, ids);
                    }.bind(this)
                });
                node.store("selector", ps);

                var actionNode = node.getNext();
                if (actionNode.hasClass("MWFScriptSelectAction")){
                    var _self = this;
                    var copyNode = actionNode.getFirst();
                    var pasteNode = actionNode.getLast();
                    copyNode.store("slectNode", node);
                    pasteNode.store("slectNode", node);
                    copyNode.addEvent("click", function(e){
                        var selectNode = this.retrieve("slectNode");
                        if (selectNode){
                            var name = node.get("name");
                            var data = _self.data[name];
                            if (data){
                                var str = JSON.encode(data);
                                o2.DL.open({
                                    "isTitle": false,
                                    "width": 400,
                                    "height": 500,
                                    "html": "<textarea style='width: 99%; height: 98%'>"+str+"</textarea>",
                                    "buttonList": [{
                                        "type": "ok",
                                        "text": "ok",
                                        "action": function(){this.close();}
                                    }]
                                })
                            }
                        }
                    });

                    pasteNode.addEvent("click", function(e){
                        var selectNode = this.retrieve("slectNode");
                        if (selectNode){

                            o2.DL.open({
                                "isTitle": false,
                                "width": 400,
                                "height": 500,
                                "html": "<textarea style='width: 99%; height: 98%'></textarea>",
                                "buttonList": [{
                                    "type": "ok",
                                    "text": "ok",
                                    "action": function(){
                                        var dataStr = this.content.getElement("textarea").get("value");
                                        try{

                                            var data = JSON.decode(dataStr);
                                            var s = selectNode.retrieve("selector");
                                            s.setData(data);
                                            _self.saveScriptSelectItem(selectNode, s.identitys);
                                        }catch(e){
                                            throw e;
                                        }
                                        this.close();
                                    }
                                },{
                                    "type": "cancel",
                                    "text": "cancel",
                                    "action": function(){this.close();}
                                }]
                            })

                        }
                    });
                }


            }.bind(this));

            var _self = this;
            formStyleNodes.each(function(node){
                debugger;
                var data = this.data[node.get("name")];
                if( typeOf( data ) === "string" ){
                    for( var key in this.module.form.stylesList ){
                        var s = this.module.form.stylesList[key];
                        if( ((!data && key=="default") || (data==key)) ){
                            data = {
                                name : s.name,
                                id : key
                            };
                            break;
                        }
                    }
                }
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "FormStyle",
                    "count": 1,
                    "names": [data],
                    "selectorOptions" : {
                        "mode" : ( this.form.options.mode || "" ).toLowerCase() === "mobile" ? "mobile" : "pc"
                    },
                    "validFun" : function (ids) {
                        var flag = true;
                        if( ids.length === 0 ){
                            this.designer.notice(MWF.APPFD.LP.mustSelectFormStyle, "error");
                            flag = false;
                        }else if( ids[0].data.type === "script" ){
                            this.designer.actions.getScriptByName(  ids[0].data.name, ids[0].data.application,  function( json ) {
                                debugger;
                                try{
                                    var f = eval("(function(){\n return "+json.data.text+"\n})");
                                    var j = f();
                                    if( typeOf(j) !== "object" ){
                                        this.designer.notice( MWF.APPFD.LP.notValidJson, "error" );
                                        flag = false;
                                    }
                                }catch (e) {
                                    this.designer.notice( MWF.APPFD.LP.notValidJson +"："+ e.message, "error" );
                                    flag = false;
                                }
                            }.bind(this), function () {
                                flag = false;
                            }, false);
                        }
                        return flag;
                    }.bind(this),
                    "onChange": function(ids){
                        var d = ids[0].data;
                        var data;
                        if( d.type === "script" ){
                            data = {
                                "type" : "script",
                                "name": d.name,
                                "alias": d.alias,
                                "id": d.id,
                                "appName" : d.appName || d.applicationName,
                                "appId": d.appId,
                                "application": d.application
                            };
                        }else{
                            data = d.id;
                        }
                        var name = node.get("name");
                        var oldValue = this.data[name];
                        this.data[name] = data;
                        this.changeData(name, node, oldValue);
                    }.bind(this)
                });

                var next = node.getNext();
                if( next && next.get("class") === "MWFScriptSelectRefresh" ){
                    var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(next);
                    refreshNode.addEvent("click", function(e){
                        _self.changeData(this.get("name"), this );
                    }.bind(node));
                }
            }.bind(this));

            dictionaryNodes.each(function(node){
                var data = this.data[node.get("name")];
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "Dictionary",
                    "count": 0,
                    "names": typeOf(data)==="array" ? data : [data],
                    "onChange": function(ids){
                        var data = [];
                        var name = node.get("name");
                        if( ids.length > 0 ){
                            // var d = ids[0].data;
                            ids.each( function (id) {
                                debugger;
                                var d = id.data;
                                data.push({
                                    "type" : "dictionary",
                                    "name": d.name,
                                    "alias": d.alias,
                                    "id": d.id,
                                    "appName" : d.appName || d.applicationName,
                                    "appAlias" : d.appAlias || d.applicationAlias,
                                    "appId": d.appId,
                                    "application": d.application,
                                    "appType" : d.appType
                                })
                            });
                        }
                        var oldValue = this.data[name];
                        this.data[name] = data;
                        this.changeData(name, node, oldValue);
                    }.bind(this)
                });
            }.bind(this));

            fileNodes.each(function(node){
                var d = this.data[node.get("name")];
                var data = d || {};
                //this.form
                if (d && typeOf(d)==="string"){
                    if (this.form.page){
                        data = {"id": d, "portal": this.form.application}
                    }else{
                        data = {"id": d, "application": this.form.application}
                    }
                }
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "PortalFile",
                    "count": 1,
                    "isImage": true,
                    "values": (data.id) ? [data.id] : [],
                    "onChange": function(ids){this.saveFileItem(node, ids);}.bind(this)
                });
            }.bind(this));

            processFileNodes.each(function(node){
                var d = this.data[node.get("name")];
                var data = d || {};
                //this.form
                if (d && typeOf(d)==="string"){
                    if (this.form.page){
                        data = {"id": d, "portal": this.form.application}
                    }else{
                        data = {"id": d, "application": this.form.application}
                    }
                }
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "ProcessFile",
                    "count": 1,
                    "isImage": true,
                    "values": (data.id) ? [data.id] : [],
                    "onChange": function(ids){this.saveFileItem(node, ids);}.bind(this)
                });
            }.bind(this));


        }.bind(this));
    },
    loadDictionaryIncluder : function(){
        var nodes = this.propertyContent.getElements(".MWFDictionaryIncluder");
        if (nodes.length){
            nodes.each(function(node){
                var name = node.get("name");
                MWF.xDesktop.requireApp("process.FormDesigner", "widget.DictionaryIncluder", function(){
                    var dictionaryIncluder = new MWF.xApplication.process.FormDesigner.widget.DictionaryIncluder(node, this.designer, {
                        "onChange": function(){
                            var data = dictionaryIncluder.getData();
                            this.data[name] = data;
                        }.bind(this)
                    });
                    dictionaryIncluder.load(this.data[name])
                }.bind(this));
            }.bind(this));
        }

    },
    loadScriptIncluder : function(){
        var nodes = this.propertyContent.getElements(".MWFScriptIncluder");
        if (nodes.length){
            nodes.each(function(node){
                var name = node.get("name");
                MWF.xDesktop.requireApp("process.FormDesigner", "widget.ScriptIncluder", function(){
                    var scriptIncluder = new MWF.xApplication.process.FormDesigner.widget.ScriptIncluder(node, this.designer, {
                        "onChange": function(){
                            var data = scriptIncluder.getData();
                            this.data[name] = data;
                        }.bind(this)
                    });
                    scriptIncluder.load(this.data[name])
                }.bind(this));
            }.bind(this));
        }

    },
    saveFileItem: function(node, ids){
        if (ids[0]){
            var file = ids[0].data;
            this.data[node.get("name")] = file;
        }else{
            this.data[node.get("name")] = null;
        }
        this.changeData(node.get("name"));
    },
    saveViewItem: function(node, ids){
        if (ids[0]){
            var view = ids[0].data;
            var data = {
                "name": view.name,
                "alias": view.alias,
                "id": view.id,
                "appName" : view.appName || view.applicationName || view.query,
                "appId": view.appId,
                "application": view.application || view.query
            };
            this.data[node.get("name")] = data;
        }else{
            this.data[node.get("name")] = null;
        }
        if (this.module._checkView) this.module._checkView();
    },
    removeViewItem: function(node, item){

    },
    saveScriptSelectItem: function(node, ids){
        var count = (node.dataset["count"] || 1).toInt();
        if (count==1){
            if (ids[0]){
                var script = ids[0].data;
                var data = {
                    "appType": script.appType,
                    "type" : "script",
                    "name": script.name,
                    "alias": script.alias,
                    "id": script.id,
                    "appName" : script.appName || script.applicationName,
                    "appId": script.appId,
                    "application": script.application
                };

                var name = node.get("name");
                var oldValue = this.data[name];
                this.data[name] = data;

                // this.changeJsonDate(name, data );
                this.changeData(name, node, oldValue);
            }else{
                // this.data[node.get("name")] = null;
            }
        }else{
            var scriptValues = [];
            ids.each(function(s){
                var scriptValue = {
                    "appType": s.data.appType,
                    "type" : "script",
                    "name": s.data.name,
                    "alias": s.data.alias,
                    "id": s.data.id,
                    "appName" : s.data.appName || s.data.applicationName,
                    "appId": s.data.appId,
                    "application": s.data.application
                }
                scriptValues.push(scriptValue);
            }.bind(this));
            var name = node.get("name");
            var oldValue = this.data[name];
            this.data[name] = scriptValues;
            this.changeData(name, node, oldValue);
        }
    },
    removeDutyItem: function(node, item){
        if (item.data.id){
            var values = JSON.decode(this.data[node.get("name")] || []);
            var value = values.filter(function(v){
                return v.id == item.data.id;
            });
            value.each(function(v) {
                values = values.erase(v);
            });
            this.data[node.get("name")] = JSON.encode(values);
        }
        item.node.destroy();
        MWF.release(item);
        delete item;
    },
    addDutyItem: function(node, ids){
        var value = this.data[node.get("name")] || "";
        if (!value) value = "[]";
        var values = JSON.decode(value);
        ids.each(function(id){
            if (id.data.dutyId){
                for (var i=0; i<values.length; i++){
                    if (values[i].dutyId===id.data.dutyId){
                        values[i].name = id.data.name;
                        values[i].code = id.data.code;
                        break;
                    }
                }
            }else{
                id.data.dutyId = new MWF.widget.UUID().toString();
                values.push({"name": id.data.name, "id": id.data.id, "dutyId": id.data.dutyId, "code": id.data.code});
            }
        }.bind(this));
        this.data[node.get("name")] = JSON.encode(values);
    },
    loadFormFieldInput: function(){
        var fieldNodes = this.propertyContent.getElements(".MWFFormFieldPerson");
        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function(){
            fieldNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "formField",
                    "application": this.form.json.application,
                    "fieldType": "person",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));
        }.bind(this));
    },
    savePersonItem: function(node, ids){
        var values = [];
        ids.each(function(id){
            values.push(MWF.org.parseOrgData(id.data));
        }.bind(this));
        this.data[node.get("name")] = values;
    },


	loadScriptArea: function(){
		var scriptAreas = this.propertyContent.getElements(".MWFScriptArea");
        var formulaAreas = this.propertyContent.getElements(".MWFFormulaArea");
        this.loadScriptEditor(scriptAreas);
        this.loadScriptEditor(formulaAreas, "formula");
	},
    loadScriptEditor: function(scriptAreas, style){
        scriptAreas.each(function(node){
            var title = node.get("title");
            var name = node.get("name");
            if (!this.data[name]) this.data[name] = {"code": "", "html": ""};
            var scriptContent = this.data[name];

            var mode = node.dataset["mode"];
            MWF.require("MWF.widget.ScriptArea", function(){
                var scriptArea = new MWF.widget.ScriptArea(node, {
                    "title": title,
                    "mode": mode || "javascript",
                    //"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.designer.formContentNode || this.designer.pageContentNode,
                    "onChange": function(){
                        //this.data[name] = scriptArea.toJson();
                        if (!this.data[name]){
                            this.data[name] = {"code": "", "html": ""};
                            if (this.module.form.scriptDesigner) this.module.form.scriptDesigner.addScriptItem(this.data[name], "code", this.data, name);
                        }
                        var json = scriptArea.toJson();
                        this.data[name].code = json.code;
                        //this.data[name].html = json.html;
                    }.bind(this),
                    "onSave": function(){
                        this.designer.saveForm();
                    }.bind(this),
                    "style": style || "default",
                    "runtime": "web"
                });
                scriptArea.load(scriptContent);
            }.bind(this));

        }.bind(this));
    },

    loadCssArea: function(style){
        var cssAreas = this.propertyContent.getElements(".MWFCssArea");
        cssAreas.each(function(node){
            var title = node.get("title");
            var name = node.get("name");
            if (!this.data[name]) this.data[name] = {"code": "", "html": ""};
            var cssContent = this.data[name];

            o2.require("o2.widget.CssArea", function(){
                var cssArea = new o2.widget.CssArea(node, {
                    "title": title,
                    "maxObj": this.designer.formContentNode || this.designer.pageContentNode,
                    "onChange": function(){
                        //this.data[name] = scriptArea.toJson();
                        if (!this.data[name]){
                            this.data[name] = {"code": "", "html": ""};
                            if (this.module.form.scriptDesigner) this.module.form.scriptDesigner.addScriptItem(this.data[name], "code", this.data, name);
                        }
                        var json = cssArea.toJson();
                        this.data[name].code = json.code;
                        cssArea.isChanged = true;
                        //this.data[name].html = json.html;
                    }.bind(this),
                    "onBlur": function(){
                        if (cssArea.isChanged){
                            this.changeData(name, node, "");
                            cssArea.isChanged = false;
                        }

                    }.bind(this),
                    "onSave": function(){
                        this.designer.saveForm();
                    }.bind(this),
                    "style": style || "default"
                });
                cssArea.load(cssContent);
            }.bind(this));
        }.bind(this));
    },
    loadActionStylesArea: function(){
        var _self = this;
        var actionAreas = this.propertyContent.getElements(".MWFActionStylesArea");
        actionAreas.each(function(node){
            var name = node.get("name");
            var actionStyles = this.data[name];
            MWF.require("MWF.widget.Maplist", function(){
                var maps = [];
                Object.each(actionStyles, function(v, k){
                    var mapNode = new Element("div").inject(node);
                    mapNode.empty();

                    var maplist = new MWF.widget.Maplist(mapNode, {
                        "title": k,
                        "collapse": true,
                        "onChange": function(){
                            var oldData = _self.data[name];
                            maps.each(function(o){
                                _self.data[name][o.key] = o.map.toJson();
                            }.bind(this));
                            _self.changeData(name, node, oldData);
                        }
                    });
                    maps.push({"key": k, "map": maplist});
                    maplist.load(v);
                }.bind(this));
            }.bind(this));


        }.bind(this));
    },
    loadActionArea: function(){
	    var multiActionArea = this.propertyContent.getElements(".MWFMultiActionArea");
        multiActionArea.each(function(node){
            var name = node.get("name");
            var actionContent = this.data[name];
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function(){
                var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "isSystemTool" : true,
                    "onChange": function(){
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);
            }.bind(this));
        }.bind(this));

        var actionAreas = this.propertyContent.getElements(".MWFActionArea");
        actionAreas.each(function(node){
            var name = node.get("name");
            var actionContent = this.data[name];
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function(){

                // debugger;
                // var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, {
                //     "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                //     "noCreate": true,
                //     "noDelete": true,
                //     "noCode": true,
                //     "onChange": function(){
                //         this.data[name] = actionEditor.data;
                //     }.bind(this)
                // });
                // actionEditor.load(this.module.defaultToolBarsData);

                var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "onChange": function(){
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);
            }.bind(this));

        }.bind(this));

        var actionAreas = this.propertyContent.getElements(".MWFDefaultActionArea");
        actionAreas.each(function(node){
            var name = node.get("name");
            var actionContent = this.data[name] || this.module.defaultToolBarsData;
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function(){

                var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "isSystemTool" : true,
                    "noCreate": true,
                    "noDelete": false,
                    "noCode": true,
                    "noReadShow": true,
                    "target" : node.get("data-target"),
                    "noEditShow": true,
                    "onChange": function(){
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);

                // var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, {
                //     "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                //     "onChange": function(){
                //         this.data[name] = actionEditor.data;
                //     }.bind(this)
                // });
                // actionEditor.load(actionContent);
            }.bind(this));

        }.bind(this));

    },


	loadMaplist: function(){
		var maplists = this.propertyContent.getElements(".MWFMaplist");
		debugger;
		maplists.each(function(node){
			var title = node.get("title");
			var name = node.get("name");
			var lName = name.toLowerCase();
			var collapse = node.get("collapse");
			var mapObj = this.data[name];
			if (!mapObj) mapObj = {};
			MWF.require("MWF.widget.Maplist", function(){
                node.empty();
				var maplist = new MWF.widget.Maplist(node, {
					"title": title,
					"collapse": (collapse) ? true : false,
					"onChange": function(){
						//this.data[name] = maplist.toJson();
						//
                        var oldData = this.data[name];
                        this.changeJsonDate(name, maplist.toJson());
                        this.changeStyle(name, oldData);
                        this.changeData(name);
					}.bind(this),
                    "onDelete": function(key){
					    debugger;

                        this.module.deletePropertiesOrStyles(name, key);
                    }.bind(this),
                    "isProperty": (lName.contains("properties") || lName.contains("property") || lName.contains("attribute"))
				});
				maplist.load(mapObj);
                this.maplists[name] = maplist;
			}.bind(this));
		}.bind(this));
	},
	//loadPropertyTab: function(){
	//	var tabNodes = this.propertyContent.getElements(".MWFTab");
	//	if (tabNodes.length){
	//		var tmpNode = this.propertyContent.getFirst();
	//		var tabAreaNode = new Element("div", {
	//			"styles": this.form.css.propertyTabNode
	//		}).inject(tmpNode, "before");
	//
	//		MWF.require("MWF.widget.Tab", function(){
	//			var tab = new MWF.widget.Tab(tabAreaNode, {"style": "formPropertyList"});
	//			tab.load();
	//			var tabPages = [];
	//			tabNodes.each(function(node){
	//			    if (node.getStyle("display")!="none"){
     //                   var page = tab.addTab(node, node.get("title"), false);
     //                   tabPages.push(page);
     //                   page.contentScrollNode = new Element("div", {"styles": {"height": "100%", "overflow": "hidden"}}).inject(page.contentNodeArea);
     //                   node.inject(page.contentScrollNode);
     //                   this.setScrollBar(page.contentScrollNode, "small", null, null);
     //               }
	//			}.bind(this));
	//			tabPages[0].showTab();
	//
	//			this.propertyTab = tab;
	//
	//			this.designer.resizeNode();
	//		}.bind(this), false);
	//	}
	//},
    loadPropertyTab: function(){
        var tabNodes = this.propertyContent.getElements(".MWFTab");
        var groupObject = {};  //data-group 属性可以表示不同的分组
        if (tabNodes.length){
            tabNodes.each( function(node){
                var group = node.get("data-group") || "default";
                groupObject[group] = groupObject[group] || [];
                groupObject[group].push( node );
            }.bind(this))
        }
        for( var group in groupObject ){
            if( group === "default" ){
                var tmpNode = this.propertyContent.getFirst();
                var tabAreaNode = new Element("div", {
                    "styles": this.form.css.propertyTabNode
                }).inject(tmpNode, "before");

                MWF.require("MWF.widget.Tab", function(){
                    var tab = new MWF.widget.Tab(tabAreaNode, {"style": "formPropertyList"});
                    tab.load();
                    var tabPages = [];
                    groupObject["default"].each(function(node){
                        if (node.getStyle("display")!="none"){
                            var page = tab.addTab(node, node.get("title"), false);
                            tabPages.push(page);
                            page.contentScrollNode = new Element("div", {"styles": {"height": "100%", "overflow": "hidden"}}).inject(page.contentNodeArea);
                            node.inject(page.contentScrollNode);
                            this.setScrollBar(page.contentScrollNode, "small", null, null);
                        }
                    }.bind(this));
                    tabPages[0].showTab();

                    this.propertyTab = tab;

                    this.designer.resizeNode();
                }.bind(this), false);
            }else{
                var tmpNode = groupObject[group][0];
                var tabAreaNode = new Element("div", {
                    "styles": this.form.css.propertyTabNode
                }).inject(tmpNode, "before");
                MWF.require("MWF.widget.Tab", function(){
                    var tab = new MWF.widget.Tab(tabAreaNode, {"style": tmpNode.get("data-style") || "formPropertyList"});
                    tab.load();
                    var tabPages = [];
                    groupObject[group].each(function(node) {
                        var page = tab.addTab(node, node.get("title"), false);
                        tabPages.push(page);
                        node.store("tab", page);
                        if (node.getStyle("display") === "none"){
                            page.disableTab( true );
                            node.show();
                        }
                    }.bind(this));
                    for( var i=0; i<tabPages.length; i++ ){
                        if( !tabPages[i].disabled ){
                            tabPages[i].showTab();
                            break
                        }
                    }
                }.bind(this), false);
            }
        }
    },
	
	setEditNodeEvent: function(){
		var property = this;
	//	var inputs = this.process.propertyListNode.getElements(".editTableInput");
		var inputs = this.propertyContent.getElements("input");
		inputs.each(function(input){

			var jsondata = input.get("name");

            if (this.module){
                var id = this.data.pid;
                //var id = this.form.json.id;
                // input.set("name", this.form.options.mode+id+jsondata);
                input.set("name", id+jsondata);
            }

			if (jsondata){
				var inputType = input.get("type").toLowerCase();
				switch (inputType){
					case "radio":
						input.addEvent("change", function(e){
							property.setRadioValue(jsondata, this);
						});
						input.addEvent("blur", function(e){
							property.setRadioValue(jsondata, this);
						});
                        input.addEvent("keydown", function(e){
                            e.stopPropagation();
                        });
                        property.setRadioValue(jsondata, input);
						break;
					case "checkbox":

						input.addEvent("change", function(e){
							property.setCheckboxValue(jsondata, this);
						});
						input.addEvent("click", function(e){
							property.setCheckboxValue(jsondata, this);
						});
                        input.addEvent("keydown", function(e){
                            e.stopPropagation();
                        });
						break;
					default:
						input.addEvent("change", function(e){
							property.setValue(jsondata, this.value, this);
						});
						input.addEvent("blur", function(e){
							property.setValue(jsondata, this.value, this);
						});
						input.addEvent("keydown", function(e){
							if (e.code==13){
								property.setValue(jsondata, this.value, this);
							}
                            e.stopPropagation();
						});
				}
			}
		}.bind(this));
		
		var selects = this.propertyContent.getElements("select");

		selects.each(function(select){
			var jsondata = select.get("name");
			if (jsondata){
				select.addEvent("change", function(e){
					property.setSelectValue(jsondata, this);
				});
                //property.setSelectValue(jsondata, select);
			}
		});
		
		var textareas = this.propertyContent.getElements("textarea");
		textareas.each(function(input){
			var jsondata = input.get("name");
			if (jsondata){
				input.addEvent("change", function(e){
					property.setValue(jsondata, this.value);
				});
				input.addEvent("blur", function(e){
					property.setValue(jsondata, this.value);
				});
                input.addEvent("keydown", function(e){
                    e.stopPropagation();
                });
			}
		}.bind(this));
		
	},
    changeStyle: function(name, oldData){
        this.module.setPropertiesOrStyles(name, oldData);
    },
    changeData: function(name, input, oldValue){
        this.module._setEditStyle(name, input, oldValue);
    },
    changeJsonDate: function(key, value){
        if (typeOf(key)!=="array") key = [key];
        var o = this.data;
        var len = key.length-1;
        key.each(function(n, i){
            if (!o[n]) o[n] = {};
            if (i<len) o = o[n];
        }.bind(this));
        o[key[len]] = value;

        //this.data[key] = value;
    },
	setRadioValue: function(name, input){
		if (input.checked){
            var i = name.indexOf("*");
            var names = (i==-1) ? name.split(".") : name.substr(i+1, name.length).split(".");

            var value = input.value;
            if (value=="false") value = false;
            if (value=="true") value = true;

            var oldValue = this.data;
            for (var idx = 0; idx<names.length; idx++){
                if (!oldValue[names[idx]]){
                    oldValue = null;
                    break;
                }else{
                    oldValue = oldValue[names[idx]];
                }
            }

			// var value = input.value;
			// if (value==="false") value = false;
			// if (value==="true") value = true;
			//var oldValue = this.data[name];
			this.changeJsonDate(names, value);
            this.changeData(name, input, oldValue);
		}
	},
	setCheckboxValue: function(name, input){
        //var id = this.module.json.id;
        //var id = this.form.json.id;
        var id = this.data.pid;

		// var checkboxList = $$("input[name='"+this.form.options.mode+id+name+"']");
        var checkboxList = $$("input[name='"+id+name+"']");
		var values = [];
		checkboxList.each(function(checkbox){
			if (checkbox.get("checked")){
				values.push(checkbox.value);
			}
		});
		var oldValue = this.data[name];
		//this.data[name] = values;
        this.changeJsonDate(name, values);
        this.changeData(name, input, oldValue);
	},
	setSelectValue: function(name, select){
		var idx = select.selectedIndex;
		var options = select.getElements("option");
		var value = "";
		if (options[idx]){
			value = options[idx].get("value");
		}
		var oldValue = this.data[name];
		//this.data[name] = value;
        this.changeJsonDate(name, value);
        this.changeData(name, select, oldValue);
	},
	
	setValue: function(name, value, obj){
		if (name==="id"){
			if (value!==this.module.json.id) {
                if (!value) {
                    this.designer.notice(MWF.APPFD.LP.notNullId, "error", this.module.form.designer.propertyContentArea, {
                        x: "right",
                        y: "bottom"
                    });
                    obj.focus();
                    return false;
                } else {
                    var check = this.module.form.checkModuleId(value, this.module.json.type);
                    if (check.elementConflict) {
                        this.designer.notice(MWF.APPFD.LP.repetitionsId, "error", this.module.form.designer.propertyContentArea, {
                            x: "right",
                            y: "bottom"
                        });
                        obj.focus();
                        return false;
                    } else {
                        var json = this.module.form.json.moduleList[this.module.json.id];
                        this.module.form.json.moduleList[value] = json;
                        delete this.module.form.json.moduleList[this.module.json.id];
                    }
                }

                // if (this.module.form.json.moduleList[value]){
				// 	this.designer.notice(MWF.APPFD.LP.repetitionsId, "error", this.module.form.designer.propertyContentArea, {x:"right", y:"bottom"});
				// 	obj.focus();
				// 	return false;
				// }else{
				//     var flag = false;
                 //    if (this.module.form.subformList){
                 //        Object.each(this.module.form.subformList, function(o, k){
                 //            if (o.subformData.moduleList[value] && o.subformData.moduleList[value] ) flag = true;
                 //        });
                 //    }
                 //    if (flag){
                 //        this.designer.notice(MWF.APPFD.LP.repetitionsId, "error", this.module.form.designer.propertyContentArea, {x:"right", y:"bottom"});
                 //        obj.focus();
                 //        return false;
                 //    }else{
                 //        var json = this.module.form.json.moduleList[this.module.json.id];
                 //        this.module.form.json.moduleList[value]=json;
                 //        delete this.module.form.json.moduleList[this.module.json.id];
                 //    }
				// }
			}
		}
		//var oldValue = this.data[name];

        var names = name.split(".");
        var oldValue = this.data;
        for (var idx = 0; idx<names.length; idx++){
            if (!oldValue[names[idx]]){
                oldValue = null;
                break;
            }else{
                oldValue = oldValue[names[idx]];
            }
        }

		//this.data[name] = value;
        this.changeJsonDate(names, value);
        this.changeData(name, obj, oldValue);
	},
	setEditNodeStyles: function(node){
		var nodes = node.getChildren();
		if (nodes.length){
			nodes.each(function(el){
				var cName = el.get("class");
				if (cName){
					if (this.form.css[cName]) el.setStyles(this.form.css[cName]);
				}
				this.setEditNodeStyles(el);
			}.bind(this));
		}
	},
	loadScriptInput: function(){

		var scriptNodes = this.propertyContent.getElements(".MWFScript");
		scriptNodes.each(function(node){
			MWF.require("MWF.widget.ScriptEditor", function(){
				var script = new MWF.widget.ScriptEditor(node, {
					"onPostSave": function(script){
						this.saveScriptItem(node, script);
					}.bind(this),
					"onQueryDelete": function(script){
						this.deleteScriptItem(node, script);
					}.bind(this)
				});
				this.setScriptItems(script, node);
			}.bind(this));
		}.bind(this));
	},
	deleteScriptItem: function(node, script){
		var jsondata = node.get("name");
		this.data[jsondata].erase(script.data.id);
		this.process.scripts[script.data.id] = null;
		delete this.process.scripts[script.data.id];
		this.process.process.scriptList.erase(script.data);
	},
	saveScriptItem: function(node, script){
		var jsondata = node.get("name");
		var scriptList = this.data[jsondata];
		
		var data = script.data;
		var scriptData = this.process.scripts[script.data.id];
		if (!scriptData){
			this.process.process.scriptList.push(data);
			this.process.scripts[script.data.id] = data;
		}
		if (scriptList.indexOf(data.id) == -1){
			this.data[jsondata].push(data.id);
		}
	},
	setScriptItems: function(script, node){
		var jsondata = node.get("name");
		var scriptList = this.data[jsondata];
		scriptList.each(function(id){
			if (id){
				var data = this.process.scripts[id];
				if (data) script.setScriptItem(data);
			}
		}.bind(this));
	},
    loadHelp: function () {
        var nodes = this.propertyContent.getElements(".MWFHelp");
        if (nodes.length){
            nodes.each(function(node){
                var html = node.get("text");
                node.empty();

                MWF.xDesktop.requireApp("Template", "MTooltips", function() {
                    new MTooltips(this.designer.node, node, this.designer, {}, {
                        "nodeStyles":{
                            "max-width": "370px",
                            "width": "370px",
                            "line-height": "24px",
                            "font-size": "14px"
                        },
                        "onCustomContent": function (content, node) {
                            content.set("html", html);
                        }
                    });
                }.bind(this))
            }.bind(this));
        }
    }
});
MWF.xApplication.process.FormDesigner.PropertyMulti = new Class({
    Extends: MWF.xApplication.process.FormDesigner.Property,
    Implements: [Options, Events],

    initialize: function(form, modules, propertyNode, designer, options){
        this.setOptions(options);
        this.modules = modules;
        this.form = form;
    //    this.data = module.json;
        this.data = {};
        this.htmlPath = this.options.path;
        this.designer = designer;
        this.maplists = {};
        this.propertyNode = propertyNode;
    },
    load: function(){
        if (this.fireEvent("queryLoad")){
            MWF.getRequestText(this.htmlPath, function(responseText, responseXML){
                this.htmlString = responseText;
                MWF.require("MWF.widget.JsonTemplate", function(){
                    this.fireEvent("postLoad");
                }.bind(this));
            }.bind(this));
        }
    },
    show: function(){
        if (!this.propertyContent){
            if (this.htmlString){
                this.htmlString = o2.bindJson(this.htmlString, {"lp": MWF.xApplication.process.FormDesigner.LP.propertyTemplate});
                this.JsonTemplate = new MWF.widget.JsonTemplate({}, this.htmlString);
                this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.propertyNode);
                this.propertyContent.set("html", this.JsonTemplate.load());

                this.setEditNodeEvent();
                this.setEditNodeStyles(this.propertyContent);
                this.loadPropertyTab();
                this.loadMaplist();
                this.loadScriptArea();
                this.loadTreeData();
                this.loadArrayList();
                //this.loadEventsEditor();
                //this.loadHTMLArea();
                //this.loadJSONArea();
//			this.loadScriptInput();
                //MWF.process.widget.EventsEditor
            }
        }else{
            this.propertyContent.setStyle("display", "block");
        }

    },
    hide: function(){
        if (this.propertyContent) this.propertyContent.destroy();
    },
    changeStyle: function(name){
        this.modules.each(function(module){
            module.setPropertiesOrStyles(name);
        }.bind(this));
    },
    changeData: function(name, input, oldValue){

        this.modules.each(function(module){
            module._setEditStyle(name, input, oldValue);
        }.bind(this));
    },
    changeJsonDate: function(key, value){
        //alert(key+": "+value );
        this.modules.each(function(module){
            module.json[key] = value;
        }.bind(this));
    },
});

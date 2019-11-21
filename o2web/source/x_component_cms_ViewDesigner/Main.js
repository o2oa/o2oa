MWF.CMSVD = MWF.xApplication.cms.ViewDesigner;
MWF.CMSVD.options = {
	"multitask": true,
	"executable": false
};
//MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("cms.ViewDesigner", "View", null, false);
MWF.xApplication.cms.ViewDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "cms.ViewDesigner",
		"icon": "icon.png",
		"title": MWF.CMSVD.LP.title,
		"appTitle": MWF.CMSVD.LP.title,
		"id": "",
		"actions": null,
		"category": null,
		"processData": null
	},
	onQueryLoad: function(){
        this.shortcut = true;
        if (!this.options.id && this.status){
            this.options.application = this.status.applicationId;
            this.application = this.status.application;
            this.options.id = this.status.id;
        }

		if (!this.options.id){
			this.options.desktopReload = false;
			this.options.title = this.options.title + "-"+MWF.CMSVD.LP.newView;
		}
		this.actions = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.ColumnManager.Actions.RestActions();

        this.path = "/x_component_cms_ViewDesigner/$Main/";

		this.lp = MWF.xApplication.cms.ViewDesigner.LP;
//		this.cmsData = this.options.processData;
	},
	
	loadApplication: function(callback){
		this.createNode();
		if (!this.options.isRefresh){
			this.maxSize(function(){
                this.openForm();
			}.bind(this));
		}else{
            this.openForm();
		}

		if (callback) callback();
	},


	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
	openForm: function(){
		this.loadNodes();
        this.loadViewListNodes();
	//	this.loadToolbar();
		this.loadContentNode();
	//	this.loadProperty();
	//	this.loadTools();
		this.resizeNode();
		this.addEvent("resize", this.resizeNode.bind(this));
		this.loadView();
		
		if (this.toolbarContentNode){
			this.setScrollBar(this.toolbarContentNode, null, {
				"V": {"x": 0, "y": 0},
				"H": {"x": 0, "y": 0}
			});
            //this.setScrollBar(this.propertyDomArea, null, {
            //    "V": {"x": 0, "y": 0},
            //    "H": {"x": 0, "y": 0}
            //});
		}
	},
	initOptions: function(){
		//this.toolsData = null;
		//this.toolbarMode = "all";
		//this.tools = [];
		//this.toolbarDecrease = 0;
		//
		//this.designNode = null;
		//this.form = null;
	},
	loadNodes: function(){
        this.viewListNode = new Element("div", {
            "styles": this.css.viewListNode
        }).inject(this.node);
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.node);
	},

    //loadViewListNodes-------------------------------
    loadViewListNodes: function(){
        this.viewListTitleNode = new Element("div", {
            "styles": this.css.viewListTitleNode,
            "text": MWF.CMSVD.LP.view
        }).inject(this.viewListNode);

        this.viewListResizeNode = new Element("div", {"styles": this.css.viewListResizeNode}).inject(this.viewListNode);
        this.viewListAreaSccrollNode = new Element("div", {"styles": this.css.viewListAreaSccrollNode}).inject(this.viewListNode);
        this.viewListAreaNode = new Element("div", {"styles": this.css.viewListAreaNode}).inject(this.viewListAreaSccrollNode);

        this.loadViewListResize();

        this.loadViewList();
    },

    loadViewListResize: function(){
        this.viewListResize = new Drag(this.viewListResizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.viewListAreaSccrollNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "onDrag": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
//				var y = e.event.y;
                var bodySize = this.content.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx = x.toFloat() - position.x.toFloat();

                var width = initialWidth+dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<40) width = 40;
                this.contentNode.setStyle("margin-left", width+1);
                this.viewListNode.setStyle("width", width);
            }.bind(this)
        });
    },

    loadViewList: function(){
        this.actions.listView(this.application.id, function (json) {
            json.data.each(function(view){
                this.createListViewItem(view);
            }.bind(this));
        }.bind(this), null, false);
    },

    createListViewItem: function(view, isNew){
        var _self = this;
        var listViewItem = new Element("div", {"styles": this.css.listViewItem}).inject(this.viewListAreaNode, (isNew) ? "top": "bottom");
        var listViewItemIcon = new Element("div", {"styles": this.css.listViewItemIcon}).inject(listViewItem);
        var listViewItemText = new Element("div", {"styles": this.css.listViewItemText, "text": (view.name) ? view.name+" ("+view.alias+")" : this.lp.newView}).inject(listViewItem);

        listViewItem.store("view", view);
        listViewItem.addEvents({
            "dblclick": function(e){_self.loadViewByData(this, e);},
            "mouseover": function(){if (_self.currentListViewItem!=this) this.setStyles(_self.css.listViewItem_over);},
            "mouseout": function(){if (_self.currentListViewItem!=this) this.setStyles(_self.css.listViewItem);}
        });
    },
    loadViewByData: function(node, e){
        var view = node.retrieve("view");

        var openNew = true;
        for (var i = 0; i<this.tab.pages.length; i++){
            if (view.id==this.tab.pages[i].view.data.id){
                this.tab.pages[i].showTabIm();
                openNew = false;
                break;
            }
        }
        if (openNew){
            this.loadViewData(view.id, function(data){
                var view = new MWF.xApplication.cms.ViewDesigner.View(this, data);
                view.load();
            }.bind(this), true);
        }
    },

    loadRelativeForm : function( formId, callback ){
        this.actions.getForm(formId, function(json){
            this.relativeFormData = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
           this.getFields( callback );
        }.bind(this));
    },
    getFields : function(callback){
        var url = this.path+"fieldConfig.json";
        MWF.getJSON(url, function(json){
            this.documentFields = json.documentFields;
            var formFileldTypeName = [];
            json.formFileldType.each(function( ft ){
                formFileldTypeName.push( ft.name );
            });
            this.formFields =[];
            Object.each(this.relativeFormData.json.moduleList, function(v, key){
                var idx = formFileldTypeName.indexOf( v.type.toLowerCase() );
                if( idx > -1 ){
                    this.formFields.push( {
                        "name" : key,
                        "type" : json.formFileldType[idx].type
                    });
                }
            }.bind(this));
            if(callback)callback();
        }.bind(this))
    },

	//loadContentNode------------------------------
    loadContentNode: function(){
		this.contentToolbarNode = new Element("div#contentToolbarNode", {
			"styles": this.css.contentToolbarNode
		}).inject(this.contentNode);
		if (!this.options.readMode) this.loadContentToolbar();
		
		this.editContentNode = new Element("div.editContentNode", {
			"styles": this.css.editContentNode
		}).inject(this.contentNode);

		this.loadEditContent(function(){
		//	if (this.designDcoument) this.designDcoument.body.setStyles(this.css.designBody);
			if (this.designNode) this.designNode.setStyles(this.css.designNode);
		}.bind(this));
	},
    loadContentToolbar: function(callback){
		this.getFormToolbarHTML(function(toolbarNode){
			var spans = toolbarNode.getElements("span");
			spans.each(function(item, idx){
				var img = item.get("MWFButtonImage");
				if (img){
					item.set("MWFButtonImage", this.path+""+this.options.style+"/toolbar/"+img);
				}
			}.bind(this));

			$(toolbarNode).inject(this.contentToolbarNode);
			MWF.require("MWF.widget.Toolbar", function(){
				this.toolbar = new MWF.widget.Toolbar(toolbarNode, {"style": "ProcessCategory"}, this);
				this.toolbar.load();
				if (callback) callback();
			}.bind(this));
		}.bind(this));
	},
	getFormToolbarHTML: function(callback){
		var toolbarUrl = this.path+this.options.style+"/toolbars.html";
		var r = new Request.HTML({
			url: toolbarUrl,
			method: "get",
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				var toolbarNode = responseTree[0];
				if (callback) callback(toolbarNode);
			}.bind(this),
			onFailure: function(xhr){
				this.notice("request cmsToolbars error: "+xhr.responseText, "error");
			}.bind(this)
		});
		r.send();
	},
    maxOrReturnEditor: function(){
        if (!this.isMax){
            this.designNode.inject(this.node);
            this.designNode.setStyles({
                "position": "absolute",
                "width": "100%",
                "height": "100%",
                "top": "0px",
                "margin": "0px",
                "left": "0px"
            });
            this.tab.pages.each(function(page){
                page.view.setAreaNodeSize();
            });
            this.isMax = true;
        }else{
            this.isMax = false;
            this.designNode.inject(this.editContentNode);
            this.designNode.setStyles(this.css.designNode);
            this.designNode.setStyles({
                "position": "static"
            });
            this.resizeNode();
            this.tab.pages.each(function(page){
                page.view.setAreaNodeSize();
            });
        }

    },
    loadEditContent: function(callback){
        this.designNode = new Element("div.designNode", {
            "styles": this.css.designNode
        }).inject(this.editContentNode);

        MWF.require("MWF.widget.Tab", function(){
            this.tab = new MWF.widget.Tab(this.designNode, {"style": "dictionary"});
            this.tab.load();
        }.bind(this), false);

        //    MWF.require("MWF.widget.ScrollBar", function(){
        //        new MWF.widget.ScrollBar(this.designNode, {"distance": 100});
        //    }.bind(this));
	},
	
	//resizeNode------------------------------------------------
	resizeNode: function(){
		var nodeSize = this.node.getSize();
		this.contentNode.setStyle("height", ""+nodeSize.y+"px");
		
		var contentToolbarMarginTop = this.contentToolbarNode.getStyle("margin-top").toFloat();
		var contentToolbarMarginBottom = this.contentToolbarNode.getStyle("margin-bottom").toFloat();
		var allContentToolberSize = this.contentToolbarNode.getComputedSize();
		var y = nodeSize.y - allContentToolberSize.totalHeight - contentToolbarMarginTop - contentToolbarMarginBottom;
		this.editContentNode.setStyle("height", ""+y+"px");
		
		if (this.designNode){
			var designMarginTop = this.designNode.getStyle("margin-top").toFloat();
			var designMarginBottom = this.designNode.getStyle("margin-bottom").toFloat();
			y = nodeSize.y - allContentToolberSize.totalHeight - contentToolbarMarginTop - contentToolbarMarginBottom - designMarginTop - designMarginBottom;
			this.designNode.setStyle("height", ""+y+"px");
		}
		
		
		//titleSize = this.propertyTitleNode.getSize();
		//titleMarginTop = this.propertyTitleNode.getStyle("margin-top").toFloat();
		//titleMarginBottom = this.propertyTitleNode.getStyle("margin-bottom").toFloat();
		//titlePaddingTop = this.propertyTitleNode.getStyle("padding-top").toFloat();
		//titlePaddingBottom = this.propertyTitleNode.getStyle("padding-bottom").toFloat();
		//
		//y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom;
		//y = nodeSize.y-y;
		//this.propertyContentNode.setStyle("height", ""+y+"px");
		//
		//
		//this.setPropertyContentResize();

        titleSize = this.viewListTitleNode.getSize();
        titleMarginTop = this.viewListTitleNode.getStyle("margin-top").toFloat();
        titleMarginBottom = this.viewListTitleNode.getStyle("margin-bottom").toFloat();
        titlePaddingTop = this.viewListTitleNode.getStyle("padding-top").toFloat();
        titlePaddingBottom = this.viewListTitleNode.getStyle("padding-bottom").toFloat();
        nodeMarginTop = this.viewListAreaSccrollNode.getStyle("margin-top").toFloat();
        nodeMarginBottom = this.viewListAreaSccrollNode.getStyle("margin-bottom").toFloat();

        y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom+nodeMarginTop+nodeMarginBottom;
        y = nodeSize.y-y;
        this.viewListAreaSccrollNode.setStyle("height", ""+y+"px");
        this.viewListResizeNode.setStyle("height", ""+y+"px");
	},
	
	//loadForm------------------------------------------
    loadView: function(){
		this.getViewData(this.options.id, function(vdata){
            var name = vdata.name || "";
            this.setTitle(this.options.appTitle + "-"+ name);

            this.taskitem.setText(this.options.appTitle + "-"+name);
            this.options.appTitle = this.options.appTitle + "-"+name;

            this.view = new MWF.xApplication.cms.ViewDesigner.View(this, vdata);
			this.view.load();

            if (this.status){
                if (this.status.openViews){
                    this.status.openViews.each(function(id){
                        this.loadViewData(id, function(data){
                            var showTab = true;
                            if (this.status.currentId){
                                if (this.status.currentId!=data.id) showTab = false;
                            }
                            var view = new MWF.xApplication.cms.ViewDesigner.View(this, data, {"showTab": showTab});
                            view.load();
                        }.bind(this), true);
                    }.bind(this));
                }
            }
		}.bind(this));
	},
    getViewData: function(id, callback){
		if (!id){
			this.loadNewViewData(callback);
		}else{
			this.loadViewData(id, callback);
		}
	},
	loadNewViewData: function(callback){
        var data = {
            "content": {
                "isNew" : true,
                "name": "",
                "id": this.actions.getUUID(),
                "application": this.application.id,
                "applicationName" : this.application.appName,
                "alias": "",
                "description": "",
                "relativeForm" : this.relativeForm,
                "events" : null,
                "columns" :[],
                "jsheader" : null,
                "sortType" : "DESC"
            }
        };
        this.createListViewItem(data, true); 
        this.loadRelativeForm( this.options.formId || this.relativeForm.id, function(){
            if (callback) callback(data);
        })
	},
	loadViewData: function(id, callback){
		this.actions.getView(id, function(data){
			if (data){
                data.data.content = JSON.parse(data.data.content);
                if( data.data.content.id != id )data.data.content.id = id;
                if (!this.application){
                    this.actions.getColumn( data.appId, function(json){
                        this.application = {"name": json.data.name, "id": json.data.id};
                        this.loadRelativeForm( data.data.content.relativeForm.id, function(){
                            if (callback) callback(data.data);
                        })
                    }.bind(this));
                }else{
                    this.loadRelativeForm( data.data.content.relativeForm.id, function(){
                        if (callback) callback(data.data);
                    })
                }
			}
		}.bind(this));
	},

    saveView: function(){
        if (this.tab.showPage){
            var view = this.tab.showPage.view;
            view.save(function(){
                if (view==this.view){
                    var name = view.data.name || "";
                    this.setTitle(MWF.CMSVD.LP.title + "-"+name);
                    this.options.desktopReload = true;
                    this.options.id = view.data.id;
                }
                this.fireAppEvent("postSave");
            }.bind(this));
        }
	},
    saveViewAs: function(){
        this.view.saveAs();
	},
    viewExplode: function(){
        this.view.explode();
    },
    viewImplode: function(){
        this.view.implode();
    },
	//recordStatus: function(){
	//	return {"id": this.options.id};
	//},
    recordStatus: function(){
        if (this.tab){
            var openViews = [];
            this.tab.pages.each(function(page){
                if (page.view.data.id!=this.options.id) openViews.push(page.view.data.id);
            }.bind(this));
            var currentId = this.tab.showPage.view.data.id;
            var status = {
                "id": this.options.id,
                "application": this.application,
                "openViews": openViews,
                "currentId": currentId
            };
            return status;
        }
        return {"id": this.options.id, "application": this.application};
    }
});

MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.ApplicationExplorer = MWF.xApplication.process.ApplicationExplorer || {};
MWF.xDesktop.requireApp("process.ApplicationExplorer", "lp."+MWF.language, null, false);
MWF.xApplication.process.ApplicationExplorer.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "process.ApplicationExplorer",
		"icon": "icon.png",
		"width": "1000",
		"height": "600",
		"title": MWF.xApplication.process.ApplicationExplorer.LP.title,
        "tooltip": {
            "cancel": MWF.xApplication.process.ApplicationExplorer.LP.application.action_cancel,
            "ok": MWF.xApplication.process.ApplicationExplorer.LP.application.action_ok,

            "create": MWF.xApplication.process.ApplicationExplorer.LP.application.create,
            "search": MWF.xApplication.process.ApplicationExplorer.LP.application.search,
            "searchText": MWF.xApplication.process.ApplicationExplorer.LP.application.searchText,
            "allCategory": MWF.xApplication.process.ApplicationExplorer.LP.application.allCategory,
            "unCategory": MWF.xApplication.process.ApplicationExplorer.LP.application.unCategory,
            "selectCategory": MWF.xApplication.process.ApplicationExplorer.LP.application.selectCategory,

            "nameLabel": MWF.xApplication.process.ApplicationExplorer.LP.application.name,
            "aliasLabel": MWF.xApplication.process.ApplicationExplorer.LP.application.alias,
            "descriptionLabel": MWF.xApplication.process.ApplicationExplorer.LP.application.description,
            "typeLabel": MWF.xApplication.process.ApplicationExplorer.LP.application.type,
            "iconLabel": MWF.xApplication.process.ApplicationExplorer.LP.application.icon,
            "createApplication_cancel_title": MWF.xApplication.process.ApplicationExplorer.LP.application.createApplication_cancel_title,
            "createApplication_cancel": MWF.xApplication.process.ApplicationExplorer.LP.application.createApplication_cancel,
            "inputApplicationName": MWF.xApplication.process.ApplicationExplorer.LP.application.inputApplicationName,
            "createApplicationSuccess": MWF.xApplication.process.ApplicationExplorer.LP.application.createApplicationSuccess,
            //"unCategory": MWF.xApplication.process.ApplicationExplorer.LP.application.unCategory,
            "unDescription": MWF.xApplication.process.ApplicationExplorer.LP.application.unDescription,
            "noProcess": MWF.xApplication.process.ApplicationExplorer.LP.application.noProcess,
            "noForm": MWF.xApplication.process.ApplicationExplorer.LP.application.noForm,
            "noApplication": MWF.xApplication.process.ApplicationExplorer.LP.application.noApplication,
            "noApplicationCreate": MWF.xApplication.process.ApplicationExplorer.LP.application.noApplicationCreate,
            "loadding": MWF.xApplication.process.ApplicationExplorer.LP.application.loadding
        }
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.process.ApplicationExplorer.LP;
		this.currentContentNode = null;
	},
	loadApplication: function(callback){
        if (!this.restActions) this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
        //if (!this.restActions) this.restActions = new MWF.xApplication.process.ApplicationExplorer.Actions.RestActions();
        this.category = null;
        this.applications = [];
        this.deleteElements = [];
		this.createNode();
		this.loadApplicationContent();
		if (callback) callback();
	},
    hasCreatorRole: function(){
	    return MWF.AC.isProcessPlatformCreator();
    },
    hasManagerRole: function(){
        if (MWF.AC.isAdministrator()) return true;
        if (MWF.AC.isProcessManager()) return true;
        return false;
    },
	loadApplicationContent: function(){
	//	this.loadStartMenu();
        this.loadToolbar();
        this.loadCategoryArea();
		this.loadApplicationArea();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
    loadToolbar: function(){
        this.toolbarAreaNode = new Element("div", {
            "styles": this.css.toolbarAreaNode
        }).inject(this.node);
        this.createCreateAction();
        this.createSearchAction();
    },
    createCreateAction: function(){
        if (this.hasCreatorRole()){
            this.createApplicationNode = new Element("div", {
                "styles": this.css.createApplicationNode,
                "title": this.options.tooltip.create
            }).inject(this.toolbarAreaNode);
            this.createApplicationNode.addEvent("click", function(){
                this.createApplication();
            }.bind(this));
        }
    },
    createSearchAction: function(){
        this.searchApplicationNode = new Element("div", {
            "styles": this.css.searchApplicationNode,
            "text": this.options.title
        }).inject(this.toolbarAreaNode);

        //@todo
        this.searchApplicationNode.setStyles({
            "color": "#FFF",
            "font-size": "18px",
            "font-weight": "bold",
            "line-height": "50px",
            "text-align": "right",
            "margin-right": "20px"
        });
        return true;

        //this.searchApplicationButtonNode = new Element("div", {
        //    "styles": this.css.searchApplicationButtonNode,
        //    "title": this.options.tooltip.search
        //}).inject(this.searchApplicationNode);
        //
        //this.searchApplicationInputAreaNode = new Element("div", {
        //    "styles": this.css.searchApplicationInputAreaNode
        //}).inject(this.searchApplicationNode);
        //
        //this.searchApplicationInputBoxNode = new Element("div", {
        //    "styles": this.css.searchApplicationInputBoxNode
        //}).inject(this.searchApplicationInputAreaNode);
        //
        //this.searchApplicationInputNode = new Element("input", {
        //    "type": "text",
        //    "value": this.options.tooltip.searchText,
        //    "styles": this.css.searchApplicationInputNode,
        //    "x-webkit-speech": "1"
        //}).inject(this.searchApplicationInputBoxNode);
        //var _self = this;
        //this.searchApplicationInputNode.addEvents({
        //    "focus": function(){
        //        if (this.value==_self.options.tooltip.searchText) this.set("value", "");
        //    },
        //    "blur": function(){if (!this.value) this.set("value", _self.options.tooltip.searchText);},
        //    "keydown": function(e){
        //        if (e.code==13){
        //            this.searchApplication();
        //            e.preventDefault();
        //        }
        //    }.bind(this),
        //    "selectstart": function(e){
        //        e.preventDefault();
        //    }
        //});
        //this.searchApplicationButtonNode.addEvent("click", function(){this.searchApplication();}.bind(this));
    },
    importApplication: function(e){
        MWF.xDesktop.requireApp("process.ApplicationExplorer", "Importer", function(){
            (new MWF.xApplication.process.ApplicationExplorer.Importer(this, e)).load();
        }.bind(this));
    },
    loadCategoryArea: function(){
        this.categoryAreaNode = new Element("div", {
            "styles": this.css.categoryAreaNode
        }).inject(this.node);

        //this.categoryActionNode = new Element("div", {
        //    "styles": this.css.categoryActionNode,
        //    "text": this.options.tooltip.selectCategory
        //}).inject(this.categoryAreaNode);
        if (this.hasCreatorRole()){
            this.importActionNode = new Element("div", {
                "styles": this.css.importActionNode,
                "text": this.lp.application.import
            }).inject(this.categoryAreaNode);
            this.importActionNode.addEvent("click", function(e){
                this.importApplication(e);
            }.bind(this));
        }

        this.categoryListAreaNode = new Element("div", {
            "styles": this.css.categoryListAreaNode
        }).inject(this.categoryAreaNode);

        this.createAllCategoryItemNode();

        this.createCategoryNodes();

        //this.createCategoryItemNode("公文类");
        //this.createCategoryItemNode("工单类");
        //this.createCategoryItemNode("财务类");
        //this.createCategoryItemNode("合同类");
        //this.createCategoryItemNode("人力资源类");
        //this.createCategoryItemNode("固定资产类");
        //this.createCategoryItemNode("业务类");
    },
    createCategoryNodes: function(){
        this.restActions.listApplicationCategory(function(json){
            var emptyCategory = null;
            json.data.each(function(category){
                if (category.applicationCategory || category.portalCategory){
                    this.createCategoryItemNode(category.applicationCategory || category.portalCategory, category.count);
                }else{
                    emptyCategory = category;
                }
            }.bind(this));

         //   if (emptyCategory) this.createCategoryItemNode()
        }.bind(this));
    },


    createAllCategoryItemNode: function(){
        var itemNode = new Element("div", {
            "styles": this.css.allCategoryItemNode,
            "text": this.options.tooltip.allCategory
        }).inject(this.categoryListAreaNode);
        itemNode.setStyles(this.css.allCategoryItemNode_current);
        this.category = itemNode;
        var _self = this;
        itemNode.addEvents({
            "click": function(){_self.clickAllCategoryNode(this)}
        });
    },
    createCategoryItemNode: function(text, count){
        var categoryName = text || this.options.tooltip.allCategory;

        var itemNode = new Element("div.categoryItem", {
            "styles": this.css.categoryItemNode,
            "text": (count) ? categoryName+" ("+count+") " : categoryName
        }).inject(this.categoryListAreaNode);
        itemNode.store("categoryName", categoryName);

        var _self = this;
        itemNode.addEvents({
            "mouseover": function(){if (_self.category != this) this.setStyles(_self.css.categoryItemNode_over);},
            "mouseout": function(){if (_self.category != this) this.setStyles(_self.css.categoryItemNode);},
            "click": function(){_self.clickCategoryNode(this)}
        });
    },
    createLoadding: function(){
        this.loaddingNode = new Element("div", {
            "styles": this.css.noApplicationNode,
            "text": this.options.tooltip.loadding
        }).inject(this.applicationContentNode);
    },
    removeLoadding: function(){
        if (this.loaddingNode) this.loaddingNode.destroy();
    },
    loadApplicationArea: function(){
        this.applicationAreaNode = new Element("div", {
            "styles": this.css.applicationAreaNode
        }).inject(this.node);
        this.setApplicationAreaSize();
        this.addEvent("resize", this.setApplicationAreaSize);

        this.applicationContentNode = new Element("div", {
            "styles": this.css.applicationContentNode
        }).inject(this.applicationAreaNode);
        this.createLoadding();

        //MWF.require("MWF.widget.DragScroll", function(){
        //    new MWF.widget.DragScroll(this.applicationAreaNode);
        //}.bind(this));
        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.applicationAreaNode);
        //}.bind(this));

        this.loadApplicationByCategory();

        this.setApplicationContentSize();
    },

    setApplicationAreaSize: function(){
        var nodeSize = this.node.getSize();
        var toolbarSize = this.toolbarAreaNode.getSize();
        var categorySize = this.categoryAreaNode.getSize();
        var y = nodeSize.y - toolbarSize.y - categorySize.y;

        this.applicationAreaNode.setStyle("height", ""+y+"px");

        if (this.applicationContentNode){
            var count = (nodeSize.x/282).toInt();
            var x = 282 * count;
            var m = (nodeSize.x-x)/2-10;
            this.applicationContentNode.setStyles({
                "width": ""+x+"px",
                "margin-left": ""+m+"px"
            });
        }
    },
    setApplicationContentSize: function(){
        var nodeSize = this.node.getSize();
        if (this.applicationContentNode){
            var count = (nodeSize.x/282).toInt();
            var x = 282 * count;
            var m = (nodeSize.x-x)/2-10;
            this.applicationContentNode.setStyles({
                "width": ""+x+"px",
                "margin-left": ""+m+"px"
            });
        }
    },
    clearDeleteReady: function(){
        this.deleteElements.each(function(app){
            app.delAdctionNode.setStyles(this.css.applicationItemDelActionNode);
            app.node.setStyles(this.css.applicationItemNode);
            var bgcolor = app.topNode.retrieve("bgcolor");
            app.topNode.setStyle("background-color", bgcolor);
            app.readyDelete = false;
        }.bind(this));
        this.deleteElements = [];
        this.checkDeleteApplication();
    },
    clickAllCategoryNode: function(){
        var node = this.categoryListAreaNode.getFirst("div");
        var items = this.categoryListAreaNode.getElements(".categoryItem");
        node.setStyles(this.css.allCategoryItemNode_current);
        this.category = node;
        items.setStyles(this.css.categoryItemNode);
        this.loadApplicationByCategory(node);
        this.clearDeleteReady();
    },

    clickCategoryNode: function(item){
        var node = this.categoryListAreaNode.getFirst("div");
        node.setStyles(this.css.allCategoryItemNode);
        var items = this.categoryListAreaNode.getElements(".categoryItem");
        items.setStyles(this.css.categoryItemNode);
        item.setStyles(this.css.categoryItemNode_current);
        this.category = item;
        this.loadApplicationByCategory(item);
    },


    loadApplicationByCategory: function(item){
        var name = "";
        if (item){name = item.retrieve("categoryName", "")};
        this.restActions.listApplicationSummary(name, function(json){
        //this.restActions.listApplication(name, function(json){
            this.applicationContentNode.empty();
            if (json.data.length){
                //for (var i=0; i<15; i++){
                json.data.each(function(appData){
                    var application = new MWF.xApplication.process.ApplicationExplorer.Application(this, appData);
                    application.load();
                    this.applications.push(application);
                }.bind(this));
                //}
            }else {
                if (this.hasCreatorRole()){
                    var noApplicationNode = new Element("div", {
                        "styles": this.css.noApplicationNode,
                        "text": this.options.tooltip.noApplicationCreate
                    }).inject(this.applicationContentNode);
                    noApplicationNode.addEvent("click", function(){
                        this.createApplication();
                    }.bind(this));
                }else{
                    var noApplicationNode = new Element("div", {
                        "styles": this.css.noApplicationNode,
                        "text": this.options.tooltip.noApplication
                    }).inject(this.applicationContentNode);
                }
            }
        }.bind(this));
    },
    createApplication: function(){
        this.createApplicationCreateMarkNode();
        this.createApplicationCreateAreaNode();
        this.createApplicationCreateNode();

        this.applicationCreateAreaNode.inject(this.applicationCreateMarkNode, "after");
        this.applicationCreateAreaNode.fade("in");
        $("createApplicationName").focus();

        this.setApplicationCreateNodeSize();
        this.setApplicationCreateNodeSizeFun = this.setApplicationCreateNodeSize.bind(this);
        this.addEvent("resize", this.setApplicationCreateNodeSizeFun);
    },
    createApplicationCreateMarkNode: function(){
        this.applicationCreateMarkNode = new Element("div", {
            "styles": this.css.applicationCreateMarkNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.node, "after");
    },
    createApplicationCreateAreaNode: function(){
        this.applicationCreateAreaNode = new Element("div", {
            "styles": this.css.applicationCreateAreaNode
        });
    },
    createApplicationCreateNode: function(){
        this.applicationCreateNode = new Element("div", {
            "styles": this.css.applicationCreateNode
        }).inject(this.applicationCreateAreaNode);
        this.applicationCreateNewNode = new Element("div", {
            "styles": this.css.applicationCreateNewNode
        }).inject(this.applicationCreateNode);

        this.applicationCreateFormNode = new Element("div", {
            "styles": this.css.applicationCreateFormNode
        }).inject(this.applicationCreateNode);

        var html = "<table width=\"100%\" height=\"80%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
            "<tr><td style=\"height: 30px; line-height: 30px; text-align: left; min-width: 80px; width:25%\">" +
            this.options.tooltip.nameLabel+":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createApplicationName\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px; text-align: left\">"+this.options.tooltip.aliasLabel+":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createApplicationAlias\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.options.tooltip.descriptionLabel+":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createApplicationDescription\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.options.tooltip.typeLabel+":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createApplicationType\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\"/></td></tr>" +
            //"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.options.tooltip.iconLabel+":</td>" +
            //"<td style=\"; text-align: right;\"><input type=\"text\" id=\"createApplicationType\" " +
            //"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            //"height: 26px;\"/></td></tr>" +
            "</table>";
        this.applicationCreateFormNode.set("html", html);

        this.applicationCancelActionNode = new Element("div", {
            "styles": this.css.applicationCreateCancelActionNode,
            "text": this.options.tooltip.cancel
        }).inject(this.applicationCreateFormNode);
        this.applicationCreateOkActionNode = new Element("div", {
            "styles": this.css.applicationCreateOkActionNode,
            "text": this.options.tooltip.ok
        }).inject(this.applicationCreateFormNode);

        this.applicationCancelActionNode.addEvent("click", function(e){
            this.cancelCreateApplication(e);
        }.bind(this));
        this.applicationCreateOkActionNode.addEvent("click", function(e){
            this.okCreateApplication(e);
        }.bind(this));
    },

    setApplicationCreateNodeSize: function(){
        var size = this.node.getSize();
        var allSize = this.content.getSize();
        this.applicationCreateMarkNode.setStyles({
            "width": ""+allSize.x+"px",
            "height": ""+allSize.y+"px"
        });
        this.applicationCreateAreaNode.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px"
        });
        var hY = size.y*0.8;
        var mY = size.y*0.2/2;
        this.applicationCreateNode.setStyles({
            "height": ""+hY+"px",
            "margin-top": ""+mY+"px"
        });

        var iconSize = this.applicationCreateNewNode.getSize();
        var formHeight = hY*0.7;
        if (formHeight>250) formHeight = 250;
        var formMargin = hY*0.3/2-iconSize.y;
        this.applicationCreateFormNode.setStyles({
            "height": ""+formHeight+"px",
            "margin-top": ""+formMargin+"px"
        });
    },
    cancelCreateApplication: function(e){
        var _self = this;
        if ($("createApplicationName").get("value") || $("createApplicationAlias").get("value") || $("createApplicationDescription").get("value")){
            this.confirm("warn", e, this.options.tooltip.createApplication_cancel_title, this.options.tooltip.createApplication_cancel, 320, 100, function(){
                _self.applicationCreateMarkNode.destroy();
                _self.applicationCreateAreaNode.destroy();
                this.close();
            },function(){
                this.close();
            });
        }else{
            this.applicationCreateMarkNode.destroy();
            this.applicationCreateAreaNode.destroy();
        }
    },
    okCreateApplication: function(e){
        var data = {
            "name": $("createApplicationName").get("value"),
            "alias": $("createApplicationAlias").get("value"),
            "description": $("createApplicationDescription").get("value"),
            "applicationCategory": $("createApplicationType").get("value")
        };
        if (data.name){
            this.restActions.saveApplication(data, function(json){
                this.applicationCreateMarkNode.destroy();
                this.applicationCreateAreaNode.destroy();

                this.restActions.getApplication(json.data.id, function(json){
                    json.data.processList = [];
                    json.data.formList = [];
                    var application = new MWF.xApplication.process.ApplicationExplorer.Application(this, json.data, {"where": "top"});
                    application.load();
                    this.applications.push(application);
                }.bind(this));

                this.notice(this.options.tooltip.createApplicationSuccess, "success");
            //    this.app.processConfig();
            }.bind(this));
        }else{
            $("createApplicationName").setStyle("border-color", "red");
            $("createApplicationName").focus();
            this.notice(this.options.tooltip.inputApplicationName, "error");
        }
    },
    checkDeleteApplication: function(){
        if (this.deleteElements.length){
            if (!this.deleteElementsNode){
                this.deleteElementsNode = new Element("div", {
                    "styles": this.css.deleteElementsNode,
                    "text": this.lp.application.deleteElements
                }).inject(this.node);
                this.deleteElementsNode.position({
                    relativeTo: this.applicationContentNode,
                    position: "centerTop",
                    edge: "centerbottom",
                    "offset": {"y": this.applicationAreaNode.getScroll().y}
                });
                this.deleteElementsNode.addEvent("click", function(e){
                    this.deleteSelectedElements(e);
                }.bind(this));
            }
        }else{
            if (this.deleteElementsNode){
                this.deleteElementsNode.destroy();
                this.deleteElementsNode = null;
                delete this.deleteElementsNode;
            }
        }
    },
    deleteSelectedElements: function(e){
        var _self = this;
        var applicationList = [];
        this.deleteElements.each(function(app){
            applicationList.push(app.data.name);
        });
        var confirmStr = this.lp.application.deleteElementsConfirm+" ("+applicationList.join("、")+") ";
        var check = "<br/><br/><input type=\"checkbox\" id=\"deleteApplicationAllCheckbox\" value=\"yes\">"+this.lp.application.deleteApplicationAllConfirm;
        confirmStr += check;

        this.confirm("infor", e, this.lp.application.deleteElementsTitle, {"html":confirmStr}, 530, 250, function(){
            confirmStr = _self.lp.application.deleteElementsConfirmAgain+"<br/><br/><font style='color:red; font-size:14px; font-weight: bold'>"+applicationList.join("、")+"</font>";
            var checkbox = this.content.getElement("#deleteApplicationAllCheckbox");

            var onlyRemoveNotCompleted = true;
            if (checkbox.checked){
                onlyRemoveNotCompleted = false;
                confirmStr = _self.lp.application.deleteElementsAllConfirmAgain+"<br/><br/><font style='color:red; font-size:14px; font-weight: bold'>"+applicationList.join("、")+"</font>";
            }

            this.close();

            _self.confirm("infor", e, _self.lp.application.deleteElementsTitle, {"html":confirmStr}, 500, 200, function(){
                var deleted = [];
                var doCount = 0;
                var readyCount = _self.deleteElements.length;
                var errorText = "";

                var complete = function(){
                    if (doCount == readyCount){
                        if (errorText){
                            _self.app.notice(errorText, "error");
                        }
                    }
                };
                _self.deleteElements.each(function(application){
                    application["delete"](onlyRemoveNotCompleted, function(){
                        deleted.push(application);
                        doCount++;
                        if (_self.deleteElements.length==doCount){
                            _self.deleteElements = _self.deleteElements.filter(function(item, index){
                                return !deleted.contains(item);
                            });
                            _self.checkDeleteApplication();
                        }
                        complete();
                    }, function(error){
                        errorText = (errorText) ? errorText+"<br/><br/>"+error : error;
                        doCount++;
                        if (_self.deleteElements.length==doCount){
                            _self.deleteElements = _self.deleteElements.filter(function(item, index){
                                return !deleted.contains(item);
                            });
                            _self.checkDeleteApplication();
                        }
                        complete();
                    });
                });
                this.close();
            }, function(){
                this.close();
            });

            this.close();
        }, function(){
            this.close();
        });
    }
});

MWF.xApplication.process.ApplicationExplorer.Application = new Class({
	Implements: [Options, Events],
    options: {
        "where": "bottom",
        "bgColor": ["#30afdc", "#e9573e", "#8dc153", "#9d4a9c", "#ab8465", "#959801", "#434343", "#ffb400", "#9e7698", "#00a489"]
    },
	
	initialize: function(app, data, options){
		this.setOptions(options);
		this.app = app;
		this.container = this.app.applicationContentNode;
        this.css = this.app.css;
        this.data = data;
	},
	load: function(){
		this.node = new Element("div", {
            "styles": this.css.applicationItemNode
        });

        this.loadTopNode();

        this.loadIconNode();

        this.loadDeleteAction();
        this.loadExportAction();

        this.loadTitleNode();

        this.loadNewNode();

        this.loadInforNode();
        this.loadProcessNode();
        this.loadFormNode();

    //    this.loadDateNode();

        this.node.inject(this.container, this.options.where);
	},

    canManage: function(){
        if (this.app.hasCreatorRole()){
            if ((this.data.creatorPerson==layout.desktop.session.user.name) || MWF.AC.isAdministrator() || this.app.hasManagerRole()){
                return true;
            }
        }else{
           if (this.data.controllerList.indexOf(layout.desktop.session.user.distinguishedName)!==-1) return true;
        }
        return false;
    },
    loadTopNode: function(){
        this.topNode = new Element("div", {
            "styles": this.css.applicationItemTopNode
        }).inject(this.node);
        this.topNode.setStyle("background-color", this.options.bgColor[(Math.random()*10).toInt()]);

        this.topNode.addEvent("click", function(e){
            this.openApplication(e);
        }.bind(this));
    },
    loadDeleteAction: function(){
        if (this.canManage()){
            //if ((this.data.creatorPerson==layout.desktop.session.user.name) || (this.data.controllerList.indexOf(layout.desktop.session.user.distinguishedName)!==-1) || MWF.AC.isAdministrator()){
                this.delAdctionNode = new Element("div", {
                    "styles": this.css.applicationItemDelActionNode
                }).inject(this.topNode);

                this.topNode.addEvents({
                    "mouseover": function(){if (!this.readyDelete) this.delAdctionNode.fade("in"); }.bind(this),
                    "mouseout": function(){if (!this.readyDelete) this.delAdctionNode.fade("out"); }.bind(this)
                });
                this.delAdctionNode.addEvent("click", function(e){
                    this.checkDeleteApplication(e);
                    e.stopPropagation();
                }.bind(this));
            //}
        }
    },
    loadExportAction: function(){
        if (this.canManage()) {
            //if ((this.data.creatorPerson == layout.desktop.session.user.name) || MWF.AC.isAdministrator() || MWF.AC.isProcessManager()) {
                this.exportAdctionNode = new Element("div", {
                    "styles": this.css.applicationItemExportActionNode,
                    "title": this.app.lp.application.export
                }).inject(this.topNode);

                this.topNode.addEvents({
                    "mouseover": function () {
                        if (!this.readyDelete) this.exportAdctionNode.fade("in");
                    }.bind(this),
                    "mouseout": function () {
                        if (!this.readyDelete) this.exportAdctionNode.fade("out");
                    }.bind(this)
                });
                this.exportAdctionNode.addEvent("click", function (e) {
                    this.exportApplication(e);
                    e.stopPropagation();
                }.bind(this));
            //}
        }
    },
    exportApplication: function(){
        //var applicationjson = {
        //    "application": {},
        //    "processList": [],
        //    "formList": [],
        //    "dictionaryList": [],
        //    "scriptList": []
        //};
        //this.app.restActions.getApplication(this.data.name, function(json){
        //
        //}
        MWF.xDesktop.requireApp("process.ApplicationExplorer", "Exporter", function(){
            (new MWF.xApplication.process.ApplicationExplorer.Exporter(this.app, this.data)).load();
        }.bind(this));
    },

    checkDeleteApplication: function(e){
        if (!this.readyDelete){
            this.delAdctionNode.setStyles(this.css.applicationItemDelActionNode_select);
            this.node.setStyles(this.css.applicationItemNode_select);
            var bgcolor = this.topNode.getStyle("background-color");
            this.topNode.store("bgcolor", bgcolor);
            this.topNode.setStyles(this.css.applicationItemTopNode_select);
            this.readyDelete = true;
            this.app.deleteElements.push(this);
        }else{
            this.delAdctionNode.setStyles(this.css.applicationItemDelActionNode);
            this.node.setStyles(this.css.applicationItemNode);
            var bgcolor = this.topNode.retrieve("bgcolor");
            this.topNode.setStyle("background-color", bgcolor);
            this.readyDelete = false;
            this.app.deleteElements.erase(this);
        }
        this.app.checkDeleteApplication();
    },
    "delete": function(onlyRemoveNotCompleted, success, failure){
        this._deleteElement(this.data.id, onlyRemoveNotCompleted, function(){
            this.destroy();
            if (success) success();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            //	this.explorer.app.notice(errorText, "error", this.explorer.propertyContentNode, {x: "left", y:"top"});

            if (failure) failure(errorText);
        }.bind(this));
    },
    _deleteElement: function(id, onlyRemoveNotCompleted, success, failure){
        this.app.restActions.deleteApplication(id, onlyRemoveNotCompleted, success, failure);
    },
    destroy: function(){
        this.node.destroy();
        MWF.release(this);
        delete this;
    },
    loadNewNode: function(){
        if (this.data.updateTime){
            var createDate = Date.parse(this.data.createTime);
            var currentDate = new Date();
            if (createDate.diff(currentDate, "hour")<12) {
                this.newNode = new Element("div", {
                    "styles": this.css.applicationItemNewNode
                }).inject(this.topNode);
            }
        }
    },

    loadIconNode: function(){
        this.iconNode = new Element("div", {
            "styles": this.css.applicationItemIconNode
        }).inject(this.topNode);
        if (this.data.icon){
            this.iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.icon+")");
        }else{
            this.iconNode.setStyle("background-image", "url("+"/x_component_process_ApplicationExplorer/$Main/default/icon/application.png)")
        }
        this.iconNode.makeLnk({
            "par": this._getLnkPar()
        });
    },
    _getLnkPar: function(){
        var lnkIcon = "/x_component_process_ApplicationExplorer/$Main/default/lnk.png";
        if (this.data.icon) lnkIcon = "data:image/png;base64,"+this.data.icon;

        var appId = "process.ProcessManager"+this.data.id;
        return {
            "icon": lnkIcon,
            "title": this.data.name,
            "par": "process.ProcessManager#{\"application\": \""+this.data.id+"\", \"appId\": \""+appId+"\"}"
        };
    },
    loadTitleNode: function(){
        this.titleNode = new Element("div", {
            "styles": this.css.applicationItemTitileNode
        }).inject(this.topNode);
        this.nameNode = new Element("div", {
            "styles": this.css.applicationItemNameNode,
            "text": this.data.name,
            "title": this.data.name+"--"+((this.data.applicationCategory || this.data.portalCategory) || this.app.options.tooltip.unCategory)
        }).inject(this.titleNode);
        this.typeNode = new Element("div", {
            "styles": this.css.applicationItemTypeNode,
            "text": "--"+((this.data.applicationCategory || this.data.portalCategory) || this.app.options.tooltip.unCategory)
        }).inject(this.titleNode);
    },
    loadInforNode: function(){
        this.inforNode = new Element("div", {
            "styles": this.css.applicationItemInforNode
        }).inject(this.node);
        this.descriptionNode = new Element("div", {
            "styles": this.css.applicationItemDescriptionNode,
            "text": this.data.description || this.app.options.tooltip.unDescription
        }).inject(this.inforNode);
    },
    loadProcessNode: function(){
        this.processNode =  new Element("div", {
            "styles": this.css.applicationItemElNode
        }).inject(this.inforNode);
        this.processTitleNode =  new Element("div", {
            "styles": this.css.applicationItemElTitleNode,
            "text": "流程"
        }).inject(this.inforNode);
        this.processListNode =  new Element("div", {
            "styles": this.css.applicationItemElListNode
        }).inject(this.inforNode);

        this.loadProcessList();
    },
    // loadProcessList: function(){
    //     var _self = this;
    //     this.app.restActions.listProcess(this.data.id, function(json){
    //         var processList = json.data;
    //
    //         if (processList.length) {
    //             for (var i=0; i<(4).min(processList.length); i++){
    //                 var process = processList[i];
    //                 var processNode = new Element("div", {
    //                     "styles": this.css.listItemNode,
    //                     "text": process.name
    //                 }).inject(this.processListNode);
    //                 processNode.store("processId", process.id);
    //                 var _self = this;
    //                 processNode.addEvents({
    //                     "click": function(e){debugger;_self.openProcess(this, e)},
    //                     "mouseover": function(){this.setStyle("color", "#3c5eed");},
    //                     "mouseout": function(){this.setStyle("color", "#666");}
    //                 });
    //             }
    //         }else{
    //             var node = new Element("div", {
    //                 "text": this.app.options.tooltip.noProcess,
    //                 "styles": {"cursor": "pointer", "line-height": "30px"}
    //             }).inject(this.processListNode);
    //             node.addEvent("click", function(e){
    //                 this.createNewProcess(e);
    //             }.bind(this));
    //         }
    //     }.bind(this));
    //     //    }.bind(this));
    // },
    loadProcessList: function(){
    //    this.app.restActions.listProcess(this.data.id, function(json){
            if (this.data.processList.length) {
               // json.data.each(function(process){
                for (var i=0; i<(4).min(this.data.processList.length); i++){
                    var process = this.data.processList[i];
                    var processNode = new Element("div", {
                        "styles": this.css.listItemNode,
                        "text": process.name
                    }).inject(this.processListNode);
                    processNode.store("processId", process.id);
                    var _self = this;
                    processNode.addEvents({
                        "click": function(e){_self.openProcess(this, e)},
                        "mouseover": function(){this.setStyle("color", "#3c5eed");},
                        "mouseout": function(){this.setStyle("color", "#666");}
                    });
                }
               //}.bind(this));
            }else{
                var node = new Element("div", {
                    "text": this.app.options.tooltip.noProcess,
                    "styles": {"cursor": "pointer", "line-height": "30px"}
                }).inject(this.processListNode);
                node.addEvent("click", function(e){
                    this.createNewProcess(e);
                }.bind(this));
            }
    //    }.bind(this));
    },

    createNewProcess: function(e){
        this.openApplication(e, 1);
    },
    openProcess: function(node, e){
        var id = node.retrieve("processId");
        if (id){
            var _self = this;
            var options = {
                "appId": "process.ProcessDesigner"+id,
                "onQueryLoad": function(){
                    this.actions = _self.app.actions;
                    //this.category = _self;
                    this.options.id = id;
                    this.application = _self.data;
                }
            };
            this.app.desktop.openApplication(e, "process.ProcessDesigner", options);
        }
    },
    loadFormNode: function(){
        this.formNode =  new Element("div", {
            "styles": this.css.applicationItemElNode
        }).inject(this.inforNode);
        this.formTitleNode =  new Element("div", {
            "styles": this.css.applicationItemElTitleNode,
            "text": "表单"
        }).inject(this.inforNode);
        this.formListNode =  new Element("div", {
            "styles": this.css.applicationItemElListNode
        }).inject(this.inforNode);

        this.loadFormList();
    },
    // loadFormList: function(){
    //     var _self = this;
    //     this.app.restActions.listForm(this.data.id, function(json){
    //         var formList = json.data;
    //         if (formList.length){
    //             for (var i=0; i<(4).min(formList.length); i++){
    //                 var form = formList[i];
    //                 var formNode = new Element("div", {
    //                     "styles": this.css.listItemNode,
    //                     "text": form.name
    //                 }).inject(this.formListNode);
    //                 formNode.store("formId", form.id);
    //                 var _self = this;
    //                 formNode.addEvents({
    //                     "click": function(e){_self.openForm(this, e)},
    //                     "mouseover": function(){this.setStyle("color", "#3c5eed");},
    //                     "mouseout": function(){this.setStyle("color", "#666");}
    //                 });
    //             }
    //         }else{
    //             var node = new Element("div", {
    //                 "text": this.app.options.tooltip.noForm,
    //                 "styles": {"cursor": "pointer"}
    //             }).inject(this.formListNode);
    //             node.addEvent("click", function(e){
    //                 this.createNewForm(e);
    //             }.bind(this));
    //         }
    //
    //     }.bind(this));
    // },
    loadFormList: function(){
        if (this.data.formList.length){
            for (var i=0; i<(4).min(this.data.formList.length); i++){
                var form = this.data.formList[i];
                var formNode = new Element("div", {
                    "styles": this.css.listItemNode,
                    "text": form.name
                }).inject(this.formListNode);
                formNode.store("formId", form.id);
                var _self = this;
                formNode.addEvents({
                    "click": function(e){_self.openForm(this, e)},
                    "mouseover": function(){this.setStyle("color", "#3c5eed");},
                    "mouseout": function(){this.setStyle("color", "#666");}
                });
            }
        }else{
            var node = new Element("div", {
                "text": this.app.options.tooltip.noForm,
                "styles": {"cursor": "pointer"}
            }).inject(this.formListNode);
            node.addEvent("click", function(e){
                this.createNewForm(e);
            }.bind(this));
        }
    },

    createNewForm: function(e){
        this.openApplication(e, 0);
    },

    openForm: function(node, e){
        var id = node.retrieve("formId");
        if (id){
            layout.desktop.getFormDesignerStyle(function(){
                var _self = this;
                var options = {
                    "style": layout.desktop.formDesignerStyle,
                    "appId": "process.FormDesigner"+id,
                    "onQueryLoad": function(){
                        this.actions = _self.app.actions;
                        //this.category = _self;
                        this.options.id = id;
                        this.application = _self.data;
                    }
                };
                this.app.desktop.openApplication(e, "process.FormDesigner", options);
            }.bind(this));
        }
    },

    openApplication: function(e, navi){
        var appId = "process.ProcessManager"+this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(e, "process.ProcessManager", {
                "application": this.data,
                "appId": appId,
                "onQueryLoad": function(){
                    this.status = {"navi": navi || null};
                }
            });
        }
    },

    loadDateNode: function(){
        this.dateNode =  new Element("div", {
            "styles": this.css.applicationItemDateNode,
            "text": this.data.updateTime
        }).inject(this.inforNode);
    }
});

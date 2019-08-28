MWF.xDesktop.requireApp("process.ApplicationExplorer", "", null, false);
MWF.xApplication.query.QueryExplorer.Main = new Class({
	Extends: MWF.xApplication.process.ApplicationExplorer.Main,
	Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "query.QueryExplorer",
        "icon": "icon.png",
        "width": "1000",
        "height": "600",
        "title": MWF.QueryLP.title,
        "tooltip": {
            "cancel": MWF.QueryLP.application.action_cancel,
            "ok": MWF.QueryLP.application.action_ok,

            "create": MWF.QueryLP.application.create,
            "search": MWF.QueryLP.application.search,
            "searchText": MWF.QueryLP.application.searchText,
            "allCategory": MWF.QueryLP.application.allCategory,
            "unCategory": MWF.QueryLP.application.unCategory,
            "selectCategory": MWF.QueryLP.application.selectCategory,

            "nameLabel": MWF.QueryLP.application.name,
            "aliasLabel": MWF.QueryLP.application.alias,
            "descriptionLabel": MWF.QueryLP.application.description,
            "typeLabel": MWF.QueryLP.application.type,
            "iconLabel": MWF.QueryLP.application.icon,
            "createApplication_cancel_title": MWF.QueryLP.application.createApplication_cancel_title,
            "createApplication_cancel": MWF.QueryLP.application.createApplication_cancel,
            "inputApplicationName": MWF.QueryLP.application.inputApplicationName,
            "createApplicationSuccess": MWF.QueryLP.application.createApplicationSuccess,
            "unDescription": MWF.QueryLP.application.unDescription,
            "noPage": MWF.QueryLP.application.noPage,
            "noView": MWF.QueryLP.application.noView,
            "noStat": MWF.QueryLP.application.noStat,
            "noApplication": MWF.QueryLP.application.noApplication,
            "noApplicationCreate": MWF.QueryLP.application.noApplicationCreate,
            "loadding": MWF.QueryLP.application.loadding
        }
    },
	onQueryLoad: function(){
		this.lp = MWF.QueryLP;
		this.currentContentNode = null;
	},
	loadApplication: function(callback){
        if (!this.restActions) this.restActions = MWF.Actions.get("x_query_assemble_designer");
        //if (!this.restActions) this.restActions = new MWF.xApplication.process.ApplicationExplorer.Actions.RestActions();
        this.category = null;
        this.applications = [];
        this.deleteElements = [];
        this.createNode();
        this.loadApplicationContent();
        if (callback) callback();
	},

    hasCreatorRole: function(){
        return MWF.AC.isQueryPlatformCreator();
    },
    hasManagerRole: function(){
        if (MWF.AC.isAdministrator()) return true;
        if (MWF.AC.isQueryManager()) return true;
        return false;
    },

    loadApplicationByCategory: function(item){
        var name = "";
        if (item){name = item.retrieve("categoryName", "")}
        this.restActions.listApplicationSummary(name, function(json){
            this.applicationContentNode.empty();
            if (json.data.length){
                //for (var i=0; i<15; i++){
                json.data.each(function(appData){
                    var application = new MWF.xApplication.query.QueryExplorer.Query(this, appData);
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
    importApplication: function(e){
        MWF.xDesktop.requireApp("query.QueryExplorer", "Importer", function(){
            (new MWF.xApplication.query.QueryExplorer.Importer(this, e)).load();
        }.bind(this));
    },
    okCreateApplication: function(e){
        var data = {
            "name": $("createApplicationName").get("value"),
            "alias": $("createApplicationAlias").get("value"),
            "description": $("createApplicationDescription").get("value"),
            "portalCategory": $("createApplicationType").get("value")
        };
        if (data.name){
            this.restActions.saveApplication(data, function(json){
                this.applicationCreateMarkNode.destroy();
                this.applicationCreateAreaNode.destroy();

                this.restActions.getApplication(json.data.id, function(json){
                    json.data.processList = [];
                    json.data.formList = [];
                    var application = new MWF.xApplication.query.QueryExplorer.Query(this, json.data, {"where": "top"});
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
    createCategoryNodes: function(){
        this.restActions.listApplicationCategory(function(json){
            var emptyCategory = null;
            json.data.each(function(category){
                if (category.name){
                    this.createCategoryItemNode(category.name, category.count);
                }else{
                    emptyCategory = category;
                }
            }.bind(this));

            //   if (emptyCategory) this.createCategoryItemNode()
        }.bind(this));
    },
    deleteSelectedElements: function(e){
        var _self = this;
        var applicationList = [];
        this.deleteElements.each(function(app){
            applicationList.push(app.data.name);
        });
        var confirmStr = this.lp.application.deleteElementsConfirm+" ("+applicationList.join("、")+") ";
        //confirmStr += check;

        this.confirm("infor", e, this.lp.application.deleteElementsTitle, {"html":confirmStr}, 530, 210, function(){
            confirmStr = _self.lp.application.deleteElementsConfirmAgain+"<br/><br/><font style='color:red; font-size:14px; font-weight: bold'>"+applicationList.join("、")+"</font>";
            // var checkbox = this.content.getElement("#deleteApplicationAllCheckbox");
            //
            // var onlyRemoveNotCompleted = true;
            // if (checkbox.checked){
            //     onlyRemoveNotCompleted = false;
            //     confirmStr = _self.lp.application.deleteElementsAllConfirmAgain+"<br/><br/><font style='color:red; font-size:14px; font-weight: bold'>"+applicationList.join("、")+"</font>";
            // }

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
                    application["delete"]("", function(){
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

MWF.xApplication.query.QueryExplorer.Query = new Class({
    Extends: MWF.xApplication.process.ApplicationExplorer.Application,
	Implements: [Options, Events],
    options: {
        "where": "bottom",
        "bgColor": ["#30afdc", "#e9573e", "#8dc153", "#9d4a9c", "#ab8465", "#959801", "#434343", "#ffb400", "#9e7698", "#00a489"]
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

        this.loadViewNode();
        this.loadStatNode();
        //this.loadFormNode();
        this.node.inject(this.container, this.options.where);
	},

    loadIconNode: function(){
        this.iconNode = new Element("div", {
            "styles": this.css.applicationItemIconNode
        }).inject(this.topNode);
        if (this.data.icon){
            this.iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.icon+")");
        }else{
            this.iconNode.setStyle("background-image", "url("+"/x_component_query_QueryExplorer/$Main/default/icon/application.png)")
        }
        this.iconNode.makeLnk({
            "par": this._getLnkPar()
        });
    },

    exportApplication: function(){
        MWF.xDesktop.requireApp("query.QueryExplorer", "Exporter", function(){
            (new MWF.xApplication.query.QueryExplorer.Exporter(this.app, this.data)).load();
        }.bind(this));
    },

    _deleteElement: function(id, onlyRemoveNotCompleted, success, failure){
        this.app.restActions.deleteApplication(id, success, failure);
    },

    _getLnkPar: function(){
        var lnkIcon = "/x_component_query_QueryExplorer/$Main/default/lnk.png";
        if (this.data.icon) lnkIcon = "data:image/png;base64,"+this.data.icon;

        var appId = "query.QueryManager"+this.data.id;
        return {
            "icon": lnkIcon,
            "title": this.data.name,
            "par": "query.QueryManager#{\"application\": \""+this.data.id+"\", \"appId\": \""+appId+"\"}"
        };
    },

    loadViewNode: function(){
        this.viewNode =  new Element("div", {
            "styles": this.css.applicationItemElNode
        }).inject(this.inforNode);
        this.viewTitleNode =  new Element("div", {
            "styles": this.css.applicationItemElTitleNode,
            "text": this.app.lp.view
        }).inject(this.inforNode);
        this.viewListNode =  new Element("div", {
            "styles": this.css.applicationItemElListNode
        }).inject(this.inforNode);

        this.loadViewList();
    },
    loadViewList: function(){
        if (this.data.viewList && this.data.viewList.length) {
            for (var i=0; i<(4).min(this.data.viewList.length); i++){
                var view = this.data.viewList[i];
                var viewNode = new Element("div", {
                    "styles": this.css.listItemNode,
                    "text": view.name
                }).inject(this.viewListNode);
                viewNode.store("viewId", view.id);
                var _self = this;
                viewNode.addEvents({
                    "click": function(e){_self.openView(this, e)},
                    "mouseover": function(){this.setStyle("color", "#3c5eed");},
                    "mouseout": function(){this.setStyle("color", "#666");}
                });
            }
        }else{
            var node = new Element("div", {
                "text": this.app.options.tooltip.noView,
                "styles": {"cursor": "pointer", "line-height": "30px"}
            }).inject(this.viewListNode);
            node.addEvent("click", function(e){
                this.createNewView(e);
            }.bind(this));
        }
        //    }.bind(this));
    },

    openView: function(node, e){
        var id = node.retrieve("viewId");
        if (id){
            var _self = this;
            var options = {
                "appId": "query.ViewDesigner"+id,
                "onQueryLoad": function(){
                    this.actions = _self.app.actions;
                    //this.category = _self;
                    this.options.id = id;
                    this.application = _self.data;
                }
            };
            this.app.desktop.openApplication(e, "query.ViewDesigner", options);
        }
    },

    loadStatNode: function(){
        this.statNode =  new Element("div", {
            "styles": this.css.applicationItemElNode
        }).inject(this.inforNode);
        this.statTitleNode =  new Element("div", {
            "styles": this.css.applicationItemElTitleNode,
            "text": this.app.lp.stat
        }).inject(this.inforNode);
        this.statListNode =  new Element("div", {
            "styles": this.css.applicationItemElListNode
        }).inject(this.inforNode);

        this.loadStatList();
    },
    loadStatList: function(){
        if (this.data.statList && this.data.statList.length) {
            for (var i=0; i<(4).min(this.data.statList.length); i++){
                var stat = this.data.statList[i];
                var statNode = new Element("div", {
                    "styles": this.css.listItemNode,
                    "text": stat.name
                }).inject(this.statListNode);
                statNode.store("statId", stat.id);
                var _self = this;
                statNode.addEvents({
                    "click": function(e){_self.openStat(this, e)},
                    "mouseover": function(){this.setStyle("color", "#3c5eed");},
                    "mouseout": function(){this.setStyle("color", "#666");}
                });
            }
        }else{
            var node = new Element("div", {
                "text": this.app.options.tooltip.noStat,
                "styles": {"cursor": "pointer", "line-height": "30px"}
            }).inject(this.statListNode);
            node.addEvent("click", function(e){
                this.createNewStat(e);
            }.bind(this));
        }
        //    }.bind(this));
    },
    openStat: function(node, e){
        var id = node.retrieve("statId");
        if (id){
            var _self = this;
            var options = {
                "appId": "query.StatDesigner"+id,
                "onQueryLoad": function(){
                    this.actions = _self.app.actions;
                    //this.category = _self;
                    this.options.id = id;
                    this.application = _self.data;
                }
            };
            this.app.desktop.openApplication(e, "query.StatDesigner", options);
        }
    },
    createNewView: function(e){
        this.openApplication(e, 0);
    },
    createNewStat: function(e){
        this.openApplication(e, 1);
    },
    openApplication: function(e, navi){
        var appId = "query.QueryManager"+this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(e, "query.QueryManager", {
                "application": this.data,
                "appId": appId,
                "onQueryLoad": function(){
                    this.status = {"navi": navi || null};
                }
            });
        }
    },

    openApplication: function(e, navi){
        var appId = "portal.PortalManager"+this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(e, "query.QueryManager", {
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

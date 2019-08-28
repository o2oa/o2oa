MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.PortalExplorer = MWF.xApplication.portal.PortalExplorer || {};

MWF.xDesktop.requireApp("process.ApplicationExplorer", "", null, false);
MWF.xApplication.portal.PortalExplorer.Main = new Class({
	Extends: MWF.xApplication.process.ApplicationExplorer.Main,
	Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "portal.PortalExplorer",
        "icon": "icon.png",
        "width": "1000",
        "height": "600",
        "title": MWF.PortalLP.title,
        "tooltip": {
            "cancel": MWF.PortalLP.application.action_cancel,
            "ok": MWF.PortalLP.application.action_ok,

            "create": MWF.PortalLP.application.create,
            "search": MWF.PortalLP.application.search,
            "searchText": MWF.PortalLP.application.searchText,
            "allCategory": MWF.PortalLP.application.allCategory,
            "unCategory": MWF.PortalLP.application.unCategory,
            "selectCategory": MWF.PortalLP.application.selectCategory,

            "nameLabel": MWF.PortalLP.application.name,
            "aliasLabel": MWF.PortalLP.application.alias,
            "descriptionLabel": MWF.PortalLP.application.description,
            "typeLabel": MWF.PortalLP.application.type,
            "iconLabel": MWF.PortalLP.application.icon,
            "createApplication_cancel_title": MWF.PortalLP.application.createApplication_cancel_title,
            "createApplication_cancel": MWF.PortalLP.application.createApplication_cancel,
            "inputApplicationName": MWF.PortalLP.application.inputApplicationName,
            "createApplicationSuccess": MWF.PortalLP.application.createApplicationSuccess,
            "unDescription": MWF.PortalLP.application.unDescription,
            "noPage": MWF.PortalLP.application.noPage,
            "noApplication": MWF.PortalLP.application.noApplication,
            "noApplicationCreate": MWF.PortalLP.application.noApplicationCreate,
            "loadding": MWF.PortalLP.application.loadding
        }
    },
	onQueryLoad: function(){
		this.lp = MWF.PortalLP;
		this.currentContentNode = null;
	},
	loadApplication: function(callback){
        this.restActions = MWF.Actions.get("x_portal_assemble_designer");

        //if (!this.restActions) this.restActions = new MWF.xApplication.portal.PortalExplorer.Actions.RestActions();
        //if (!this.restActions) this.restActions = new MWF.xApplication.process.ApplicationExplorer.Actions.RestActions();
        this.category = null;
        this.applications = [];
        this.deleteElements = [];
        this.createNode();
        this.loadApplicationContent();
        if (callback) callback();
	},

    loadApplicationByCategory: function(item){
        var name = "";
        if (item){name = item.retrieve("categoryName", "")}
        this.restActions.listApplicationSummary(name, function(json){
            this.applicationContentNode.empty();
            if (json.data.length){
                //for (var i=0; i<15; i++){
                json.data.each(function(appData){
                    var application = new MWF.xApplication.portal.PortalExplorer.Portal(this, appData);
                    application.load();
                    this.applications.push(application);
                }.bind(this));
                //}
            }else {
                if (MWF.AC.isProcessPlatformCreator()){
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
        MWF.xDesktop.requireApp("portal.PortalExplorer", "Importer", function(){
            (new MWF.xApplication.portal.PortalExplorer.Importer(this, e)).load();
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
                    var application = new MWF.xApplication.portal.PortalExplorer.Portal(this, json.data, {"where": "top"});
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

MWF.xApplication.portal.PortalExplorer.Portal = new Class({
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
        this.loadPageNode();
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
            this.iconNode.setStyle("background-image", "url("+"/x_component_portal_PortalExplorer/$Main/default/icon/application.png)")
        }
        this.iconNode.makeLnk({
            "par": this._getLnkPar()
        });
    },

    exportApplication: function(){
        MWF.xDesktop.requireApp("portal.PortalExplorer", "Exporter", function(){
            (new MWF.xApplication.portal.PortalExplorer.Exporter(this.app, this.data)).load();
        }.bind(this));
    },

    _deleteElement: function(id, onlyRemoveNotCompleted, success, failure){
        this.app.restActions.deleteApplication(id, success, failure);
    },

    _getLnkPar: function(){
        var lnkIcon = "/x_component_portal_PortalExplorer/$Main/default/lnk.png";
        if (this.data.icon) lnkIcon = "data:image/png;base64,"+this.data.icon;

        var appId = "portal.PortalManager"+this.data.id;
        return {
            "icon": lnkIcon,
            "title": this.data.name,
            "par": "portal.PortalManager#{\"application\": \""+this.data.id+"\", \"appId\": \""+appId+"\"}"
        };
    },

    loadPageNode: function(){
        this.pageNode =  new Element("div", {
            "styles": this.css.applicationItemElNode
        }).inject(this.inforNode);
        this.pageTitleNode =  new Element("div", {
            "styles": this.css.applicationItemElTitleNode,
            "text": "页面"
        }).inject(this.inforNode);
        this.pageListNode =  new Element("div", {
            "styles": this.css.applicationItemElListNode
        }).inject(this.inforNode);

        this.loadPageList();
    },
    loadPageList: function(){
        if (this.data.pageList && this.data.pageList.length) {
            for (var i=0; i<(8).min(this.data.pageList.length); i++){
                var page = this.data.pageList[i];
                var pageNode = new Element("div", {
                    "styles": this.css.listItemNode,
                    "text": page.name
                }).inject(this.pageListNode);
                pageNode.store("pageId", page.id);
                var _self = this;
                pageNode.addEvents({
                    "click": function(e){_self.openPage(this, e)},
                    "mouseover": function(){this.setStyle("color", "#3c5eed");},
                    "mouseout": function(){this.setStyle("color", "#666");}
                });
            }
        }else{
            var node = new Element("div", {
                "text": this.app.options.tooltip.noPage,
                "styles": {"cursor": "pointer", "line-height": "30px"}
            }).inject(this.pageListNode);
            node.addEvent("click", function(e){
                this.createNewPage(e);
            }.bind(this));
        }
        //    }.bind(this));
    },

    openPage: function(node, e){
        var id = node.retrieve("pageId");
        if (id){
            var _self = this;
            var options = {
                "appId": "portal.PageDesigner"+id,
                "onQueryLoad": function(){
                    this.actions = _self.app.actions;
                    //this.category = _self;
                    this.options.id = id;
                    this.application = _self.data;
                }
            };
            this.app.desktop.openApplication(e, "portal.PageDesigner", options);
        }
    },

    openApplication: function(e, navi){
        var appId = "portal.PortalManager"+this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(e, "portal.PortalManager", {
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

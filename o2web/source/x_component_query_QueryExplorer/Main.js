MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.QueryExplorer = MWF.xApplication.query.QueryExplorer || {};
MWF.xDesktop.requireApp("process.ApplicationExplorer", "", null, false);
MWF.xApplication.query.QueryExplorer.Main = new Class({
	Extends: MWF.xApplication.process.ApplicationExplorer.Main,
	Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "query.QueryExplorer",
        "mvcStyle": "style.css",
        "icon": "icon.png",
        "width": "1500",
        "height": "760",
        "isResize": true,
        "isMax": true,
        "title": MWF.xApplication.query.QueryExplorer.LP.title,
        "maxWidth": 840,
        "minWidth": 540
    },
	onQueryLoad: function(){
		this.lp = MWF.xApplication.query.QueryExplorer.LP;
        this.viewPath = this.path+this.options.style+"/view.html";
        this.restActions = MWF.Actions.get("x_query_assemble_designer");
        this.deleteElements = [];
	},

    loadControl: function(){
        this.control = {};
        this.control.canCreate = MWF.AC.isQueryPlatformCreator();
        this.control.canManage = !!(MWF.AC.isAdministrator() || MWF.AC.isQueryManager());
    },
    openFindDesigner: function(){
        var options = {
            "filter": {
                "moduleList": ["query"]
            }
        };
        layout.openApplication(null, "FindDesigner", options);
    },
    createApplicationItem: function(appData, where){
        var application = new MWF.xApplication.query.QueryExplorer.Query(this, appData, where);
        application.load();
		this.applicationList.push(application);
    },
    okCreateApplication: function(e){
        var nameNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationName");
        var aliasNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationAlias");
        var descriptionNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationDescription");
        var typeNode = this.applicationCreateFormNode.getElement(".o2_process_AppExp_createApplicationType");
        var data = {
            "name": nameNode.get("value"),
            "alias": aliasNode.get("value"),
            "description": descriptionNode.get("value"),
            "queryCategory": typeNode.get("value")
        };
        if (data.name){
            this.restActions.saveApplication(data, function(json){
                this.applicationCreateMarkNode.destroy();
                this.applicationCreateAreaNode.destroy();

                this.restActions.getApplication(json.data.id, function(json){
                    json.data.viewList = [];
                    json.data.statList = [];
                    this.createApplicationItem(json.data, "top");
                }.bind(this));

                this.reloadApplicationCategoryList(true);
                this.notice(this.lp.application.createApplicationSuccess, "success");
            }.bind(this));
        }else{
            nameNode.setStyle("border-color", "red");
            nameNode.focus();
            this.notice(this.lp.application.inputApplicationName, "error");
        }
    },
    importApplication: function(e){
        MWF.xDesktop.requireApp("query.QueryExplorer", "Importer", function(){
            (new MWF.xApplication.query.QueryExplorer.Importer(this, e)).load();
        }.bind(this));
    },

    deleteSelectedElements: function(e){
        var _self = this;
        var applicationList = [];
        this.deleteElements.each(function(app){
            applicationList.push(app.data.name);
        });
        var confirmStr = this.lp.application.deleteElementsConfirm+" ("+applicationList.join("、")+") ";
        var check = "<div style='display: none'><br/><br/><input type=\"checkbox\" id=\"deleteApplicationAllCheckbox\" value=\"yes\">"+this.lp.application.deleteApplicationAllConfirm+"</div>";
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
                        _self.reloadApplicationCategoryList( true );
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

MWF.xApplication.query.QueryExplorer.Query = new Class({
    Extends: MWF.xApplication.process.ApplicationExplorer.Application,
    Implements: [Events],

    loadElements: function(){
        this.loadElementList("viewList", this.viewListNode, this.openView.bind(this), this.lp.noView, this.createNewView.bind(this));
        this.loadElementList("statList", this.statListNode, this.openStat.bind(this), this.lp.noStat, this.createNewStat.bind(this));
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
                "application": {"id": this.data.id, "name": this.data.name},
                "appId": appId,
                "onQueryLoad": function(){
                    this.status = {"navi": navi || null};
                }
            });
        }
    },
    openView: function(id, e){
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
    openStat: function(id, e){
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

    setIconNode: function(){
        if (this.data.icon){
            this.iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.icon+")");
        }else{
            this.iconNode.setStyle("background-image", "url("+"../x_component_query_QueryExplorer/$Main/default/icon/application.png)")
        }
        this.iconNode.makeLnk({
            "par": this._getLnkPar()
        });
    },
    _getLnkPar: function(){
        var lnkIcon = "../x_component_query_QueryExplorer/$Main/default/lnk.png";
        if (this.data.icon) lnkIcon = "data:image/png;base64,"+this.data.icon;

        var appId = "query.QueryManager"+this.data.id;
        return {
            "icon": lnkIcon,
            "title": this.data.name,
            "par": "query.QueryManager#{\"application\": \""+this.data.id+"\", \"appId\": \""+appId+"\"}"
        };
    },

    exportApplication: function(){
        MWF.xDesktop.requireApp("query.QueryExplorer", "Exporter", function(){
            (new MWF.xApplication.query.QueryExplorer.Exporter(this.app, this.data)).load();
        }.bind(this));
    },
    _deleteElement: function(id, onlyRemoveNotCompleted, success, failure){
        this.app.restActions.deleteApplication(id, success, failure);
    }
});

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
        "minWidth": 720
    },
	onQueryLoad: function(){
		this.lp = MWF.QueryLP;
        this.viewPath = this.path+this.options.style+"/view.html";
        this.restActions = MWF.Actions.get("x_query_assemble_designer");
        this.deleteElements = [];
	},

    loadControl: function(){
        this.control = {};
        this.control.canCreate = MWF.AC.isQueryPlatformCreator();
        this.control.canManage = !!(MWF.AC.isAdministrator() || MWF.AC.isQueryManager());
    },
    createApplicationItem: function(appData, where){
        var application = new MWF.xApplication.query.QueryExplorer.Query(this, appData, where);
        application.load();
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
                "application": this.data,
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
            this.iconNode.setStyle("background-image", "url("+"/x_component_query_QueryExplorer/$Main/default/icon/application.png)")
        }
        this.iconNode.makeLnk({
            "par": this._getLnkPar()
        });
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

    exportApplication: function(){
        MWF.xDesktop.requireApp("query.QueryExplorer", "Exporter", function(){
            (new MWF.xApplication.query.QueryExplorer.Exporter(this.app, this.data)).load();
        }.bind(this));
    },
    _deleteElement: function(id, onlyRemoveNotCompleted, success, failure){
        this.app.restActions.deleteApplication(id, success, failure);
    }
});

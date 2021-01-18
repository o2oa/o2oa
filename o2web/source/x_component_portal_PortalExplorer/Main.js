MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.PortalExplorer = MWF.xApplication.portal.PortalExplorer || {};
MWF.xDesktop.requireApp("process.ApplicationExplorer", "", null, false);
MWF.xApplication.portal.PortalExplorer.Main = new Class({
	Extends: MWF.xApplication.process.ApplicationExplorer.Main,
	Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "portal.PortalExplorer",
        "mvcStyle": "style.css",
        "icon": "icon.png",
        "width": "1500",
        "height": "760",
        "isResize": true,
        "isMax": true,
        "title": MWF.xApplication.portal.PortalExplorer.LP.title,
        "maxWidth": 840,
        "minWidth": 540
    },
	onQueryLoad: function(){
        this.lp = MWF.xApplication.portal.PortalExplorer.LP;
        this.viewPath = this.path+this.options.style+"/view.html";
        this.restActions = MWF.Actions.get("x_portal_assemble_designer");
        this.deleteElements = [];
	},
    loadControl: function(){
        this.control = {};
        this.control.canCreate = MWF.AC.isPortalPlatformCreator();
        this.control.canManage = !!(MWF.AC.isAdministrator() || MWF.AC.isPortalManager());
    },
    openFindDesigner: function(){
        var options = {
            "filter": {
                "moduleList": ["portal"]
            }
        };
        layout.openApplication(null, "FindDesigner", options);
    },
    createApplicationItem: function(appData, where){
        var application = new MWF.xApplication.portal.PortalExplorer.Portal(this, appData, where);
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
            "portalCategory": typeNode.get("value") || ""
        };
        if (data.name){
            this.restActions.saveApplication(data, function(json){
                this.applicationCreateMarkNode.destroy();
                this.applicationCreateAreaNode.destroy();

                this.restActions.getApplication(json.data.id, function(json){
                    json.data.pageList = [];
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
        MWF.xDesktop.requireApp("portal.PortalExplorer", "Importer", function(){
            (new MWF.xApplication.portal.PortalExplorer.Importer(this, e)).load();
        }.bind(this));
    }
});

MWF.xApplication.portal.PortalExplorer.Portal = new Class({
    Extends: MWF.xApplication.process.ApplicationExplorer.Application,
	Implements: [Events],
    checkManage: function(){
        if (this.app.control.canManage) return true;
        if (this.app.control.canCreate && (this.data.creatorPerson==layout.desktop.session.user.name)) return true;
        //if (this.data.controllerList.indexOf(layout.desktop.session.user.distinguishedName)!==-1) return true;
        return false;
    },
    loadElements: function(){
        this.loadElementList("pageList", this.pageListNode, this.openPage.bind(this), this.lp.noPage, this.createNewPage.bind(this));
    },
    createNewPage: function(e){
        this.openApplication(e, 0);
    },
    openPage: function(id, e){
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
    setIconNode: function(){
        if (this.data.icon){
            this.iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.icon+")");
        }else{
            this.iconNode.setStyle("background-image", "url("+"../x_component_portal_PortalExplorer/$Main/default/icon/application.png)")
        }
        this.iconNode.makeLnk({
            "par": this._getLnkPar()
        });
    },
    _getLnkPar: function(){
        var lnkIcon = "../x_component_portal_PortalExplorer/$Main/default/lnk.png";
        if (this.data.icon) lnkIcon = "data:image/png;base64,"+this.data.icon;

        var appId = "portal.PortalManager"+this.data.id;
        return {
            "icon": lnkIcon,
            "title": this.data.name,
            "par": "portal.PortalManager#{\"application\": \""+this.data.id+"\", \"appId\": \""+appId+"\"}"
        };
    },

    exportApplication: function(){
        MWF.xDesktop.requireApp("portal.PortalExplorer", "Exporter", function(){
            (new MWF.xApplication.portal.PortalExplorer.Exporter(this.app, this.data)).load();
        }.bind(this));
    },
    _deleteElement: function(id, onlyRemoveNotCompleted, success, failure){
        this.app.restActions.deleteApplication(id, success, failure);
    }
});

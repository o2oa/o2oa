MWF.xApplication.portal.Portal.options.multitask = true;
MWF.xApplication.portal.Portal.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "portal.Portal",
        "icon": "icon.png",
        "width": "1200",
        "height": "800",
        "title": MWF.xApplication.portal.Portal.LP.title,
        "portalId": "",
        "pageId": "",
        "widgetId" : "",
        "isControl": false,
        "taskObject": null,
        "parameters": "",
        "readonly": false
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.portal.Portal.LP;
        if (this.status){
            this.options.portalId = this.status.portalId;
            this.options.pageId = this.status.pageId;
            this.options.widgetId = this.status.widgetId;
        }
    },
    loadApplication: function(callback){
        this.node = new Element("div", {"styles": this.css.content}).inject(this.content);

        //MWF.require("MWF.widget.Mask", function(){
        //this.mask = new MWF.widget.Mask({"style": "desktop"});

        this.formNode = new Element("div", {"styles": {"min-height": "100%", "font-size": "14px"}}).inject(this.node);
        this.action = MWF.Actions.get("x_portal_assemble_surface");

        //MWF.xDesktop.requireApp("portal.Portal", "Actions.RestActions", function(){
        //    this.action = new MWF.xApplication.portal.Portal.Actions.RestActions();
        if (!this.options.isRefresh){
            this.maxSize(function(){
                //this.mask.loadNode(this.content);
                this.loadPortal(this.options.parameters, callback);
            }.bind(this));
        }else {
            //this.mask.loadNode(this.content);
            this.loadPortal(this.options.parameters, callback);
        }
        //if (callback) callback();
        //}.bind(this));

        //}.bind(this));
    },
    //reload: function(data){
    //    if (this.form){
    //        this.formNode.empty();
    //        MWF.release(this.form);
    //        this.form = null;
    //    }
    //    //this.parseData(data);
    //    this.openPortal();
    //},
    toPortal: function(portal, page, par, nohis){
        this.options.portalId = portal;
        this.options.pageId = page;
        if (!nohis) this.doHistory(page,this.options.portalId);

        if (this.appForm) this.appForm.fireEvent("beforeClose");
        Object.keys(this.$events).each(function(k){
            this.removeEvents(k);
        }.bind(this));

        MWF.release(this.appForm);
        this.appForm = null;
        this.formNode.empty();
        this.loadPortal(par);
    },
    doHistory: function(name, portal){
        if (this.inBrowser){
            var stateObj = { "page": name, "id": portal || this.options.portalId };
            history.pushState(stateObj, "page");
        }
    },
    toPage: function(name, par, nohis){
        debugger;

        if (!nohis) this.doHistory(name, this.portal.id);

        if (name){
            var m = (layout.mobile) ? "getPageByNameMobile" : "getPageByName";
            this.action[m](name, this.portal.id, function(json){
                this.pageInfor = json.data;
                this.setTitle(this.portal.name+"-"+json.data.name);
                var page = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
                if (page){
                    this.options.pageId = json.data.id;

                    if (this.appForm) this.appForm.fireEvent("beforeClose");
                    Object.keys(this.$events).each(function(k){
                        this.removeEvents(k);
                    }.bind(this));

                    MWF.release(this.appForm);
                    this.appForm = null;
                    this.formNode.empty();
                    this.page = page;
                    this.openPortal(par);
                }
            }.bind(this));
        }else{
            if (this.options.pageId){
                var m = (layout.mobile) ? "getPageByNameMobile" : "getPageByName";
                this.action[m](this.options.pageId, this.portal.id, function(json){
                    this.pageInfor = json.data;
                    this.setTitle(this.portal.name+"-"+json.data.name);
                    var page = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
                    if (page){
                        this.options.pageId = json.data.id;

                        if (this.appForm) this.appForm.fireEvent("beforeClose");
                        Object.keys(this.$events).each(function(k){
                            this.removeEvents(k);
                        }.bind(this));

                        MWF.release(this.appForm);
                        this.appForm = null;
                        this.formNode.empty();
                        this.page = page;
                        this.openPortal(par);
                    }
                }.bind(this));
            }
        }
    },
    loadPortal: function(par, callback){
        this.action.getApplication(this.options.portalId, function(json){
            this.portal = json.data;
            this.setTitle(this.portal.name);

            if (!this.options.pageId) this.options.pageId = this.portal.firstPage;
            if (this.portal.icon){
                if (this.taskitem){
                    this.taskitem.iconNode.setStyles({
                        "background-image": "url(data:image/png;base64,"+this.portal.icon+")",
                        "background-size": "24px 24px"
                    });
                }
            }
            var m;
            if( this.options.widgetId ){
                m = (layout.mobile) ? "getWidgetByNameMobile" : "getWidgetByName";
            }else{
                m = (layout.mobile) ? "getPageByNameMobile" : "getPageByName";
            }
            this.action[m]( this.options.widgetId || this.options.pageId, this.options.portalId, function(json){
                // if (layout.mobile) {
                //     this.page = (json.data.mobileData) ? JSON.decode(MWF.decodeJsonString(json.data.mobileData)): null;
                //     if (!this.page || !this.page.json.moduleList.length){
                //         this.page = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
                //     }
                // }else{
                this.setTitle(this.portal.name+"-"+json.data.name);
                this.page = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
                this.pageInfor = json.data;
                // }
                this.openPortal(par, callback);
            }.bind(this));
        }.bind(this));
    },

    openPortal: function(par, callback){
        if (this.page){
            MWF.xDesktop.requireApp("process.Xform", "Form", function(){
                this.appForm = new MWF.APPForm(this.formNode, this.page, {
                    "macro": "PageContext",
                    "parameters": par
                });
                this.appForm.businessData = {
                    "control": {
                        "allowSave": true
                    },
                    "pageInfor": this.pageInfor,
                    "data": {}
                };

                this.appForm.workAction = this.action;
                this.appForm.app = this;
                this.appForm.load();

                if (callback) callback();
                if (this.mask) this.mask.hide();
            }.bind(this));
        }
    },
    recordStatus: function(){
        return {"portalId": this.options.portalId, "pageId": this.options.pageId};
    },
    onPostClose: function(){
        if (this.appForm){
            this.appForm.modules.each(function(module){
                MWF.release(module);
            });
            MWF.release(this.appForm);
        }
    }

});

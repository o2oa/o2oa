MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.Portal = MWF.xApplication.portal.Portal || {};
MWF.xApplication.portal.Portal.options = Object.clone(o2.xApplication.Common.options);

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
        "title": "",
        "portalId": "",
        "pageId": "",
        "widgetId" : "",
        "isControl": false,
        "taskObject": null,
        "parameters": "",
        "readonly": false
    },
    onQueryLoad: function(){
        if (!this.options.title) this.setOptions({"title": MWF.xApplication.portal.Portal.LP.title});
        this.lp = MWF.xApplication.portal.Portal.LP;
        if (this.status){
            this.options.portalId = this.status.portalId;
            this.options.pageId = this.status.pageId;
            this.options.widgetId = this.status.widgetId;
            this.options.parameters = this.status.parameters;
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
        if (!nohis) this.doHistory(name, this.portal.id);
        var pageJson = null;
        var loadModuleFlag = false;
        var check = function(){
            if (!!pageJson && loadModuleFlag){
                this.pageJson = pageJson;
                layout.sessionPromise.finally(function(){
                    // this.pageInfor = pageJson.data;
                    // this.setTitle(this.portal.name+"-"+pageJson.data.name);
                    // var page = (pageJson.data.data) ? JSON.decode(MWF.decodeJsonString(pageJson.data.data)): null;

                    var pageName = pageJson.data.page ? pageJson.data.page.name : pageJson.data.name;
                    this.setTitle((this.portal && this.portal.name) ? this.portal.name+"-"+pageName : pageName);
                    var page;
                    if (pageJson.data.page){
                        this.pageDataText = (pageJson.data.page.data) ? MWF.decodeJsonString(pageJson.data.page.data): "";
                        page = (this.pageDataText) ? JSON.decode(this.pageDataText): null;
                        this.relatedFormMap = pageJson.data.relatedWidgetMap;
                        this.relatedScriptMap = pageJson.data.relatedScriptMap;
                        delete pageJson.data.page.data;
                        this.pageInfor = pageJson.data.page;
                    }else{
                        this.pageDataText = (pageJson.data.data) ? MWF.decodeJsonString(pageJson.data.data): "";
                        page = (this.pageDataText) ? JSON.decode(this.pageDataText): null;
                        delete pageJson.data.data;
                        this.pageInfor = pageJson.data;
                    }

                    if (page){
                        this.options.pageId = pageJson.data.id;

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
        }.bind(this);

        if (name){
            var m = (layout.mobile) ? "getPageByNameMobileV2" : "getPageByNameV2";
            this.action[m](name, this.portal.id, function(json){
                pageJson = json;
                check();
            }.bind(this));
            var cl = "$all";
            MWF.xDesktop.requireApp("process.Xform", cl, function(){
                loadModuleFlag = true;
                check();
            });

        }else{
            if (this.options.pageId){
                var m = (layout.mobile) ? "getPageByNameMobileV2" : "getPageByNameV2";
                this.action[m](this.options.pageId, this.portal.id, function(json){
                    pageJson = json;
                    check();
                }.bind(this));

                MWF.xDesktop.requireApp("process.Xform", "$all", function(){
                    loadModuleFlag = true;
                    check();
                });
            }
        }
    },
    loadPortal: function(par, callback){
        if (this.options.pageId || this.options.widgetId){
            this.loadPortalPage(par, callback);
            this.getPageData();
        }else{
            this.getPageData(function(json){
                this.options.pageId = this.portal.firstPage;
                this.loadPortalPage();
            }.bind(this));
        }
    },
    openPage: function(pageJson, par, callback){
        var pageName = pageJson.data.page ? pageJson.data.page.name : pageJson.data.name;
        this.setTitle((this.portal && this.portal.name) ? this.portal.name+"-"+pageName : pageName);
        if (pageJson.data.page){
            this.pageDataText = (pageJson.data.page.data) ? MWF.decodeJsonString(pageJson.data.page.data): "";
            this.page = (this.pageDataText) ? JSON.decode(this.pageDataText): null;
            this.relatedFormMap = pageJson.data.relatedWidgetMap;
            this.relatedScriptMap = pageJson.data.relatedScriptMap;
            delete pageJson.data.page.data;
            this.pageInfor = pageJson.data.page;
        }else{
            this.pageDataText = (pageJson.data.data) ? MWF.decodeJsonString(pageJson.data.data): "";
            this.page = (this.pageDataText) ? JSON.decode(this.pageDataText): null;
            delete pageJson.data.data;
            this.pageInfor = pageJson.data;
        }
        this.openPortal(par, callback);
    },
    loadPortalPage: function(par, callback){
        var pageJson = null;
        var loadModuleFlag = false;
        var check = function(){
            if (!!pageJson && loadModuleFlag){
                this.pageJson = pageJson;
                if (layout.session && layout.session.user){
                    this.openPage(pageJson, par, callback);
                }else if( layout.sessionPromise ){
                    layout.sessionPromise.then(function () {
                        this.openPage(pageJson, par, callback);
                    }.bind(this), function () {
                        this.openPage(pageJson, par, callback);
                    }.bind(this));
                }
            }
        }.bind(this);

        var m;
        if( this.options.widgetId ){
            m = (layout.mobile) ? "getWidgetByNameMobile" : "getWidgetByName";
        }else{
            m = (layout.mobile) ? "getPageByNameMobileV2" : "getPageByNameV2";
        }
        this.action[m]( this.options.widgetId || this.options.pageId, this.options.portalId, function(json){
            pageJson = json;
            check();
        }.bind(this));
        MWF.xDesktop.requireApp("process.Xform", "$all", function(){
            loadModuleFlag = true;
            check();
        });
    },

    getPageData: function(callback){
        if (this.portal){
            if (callback) callback(this.portal);
            return ;
        }
        this.action.getApplication(this.options.portalId, function(json){
            this.portal = json.data;
            if (this.pageJson){
                var pageName = this.pageJson.data.page ? this.pageJson.data.page.name : this.pageJson.data.name;
                this.setTitle(this.portal.name+"-"+pageName);
            }

            if (this.portal.icon){
                if (this.taskitem){
                    this.taskitem.iconNode.setStyles({
                        "background-image": "url(data:image/png;base64,"+this.portal.icon+")",
                        "background-size": "24px 24px"
                    });
                }
            }
            if (callback) callback(json)
        }.bind(this));
    },

    openPortal: function(par, callback){
        debugger;
        if (this.page){
            //MWF.xDesktop.requireApp("process.Xform", "Form", function(){
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
                this.appForm.formDataText = this.pageDataText;

                this.appForm.load();

                if (callback) callback();
                if (this.mask) this.mask.hide();
            //}.bind(this));
        }
    },
    recordStatus: function(){
        return {"portalId": this.options.portalId, "pageId": this.options.pageId, "parameters" : this.options.parameters};
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

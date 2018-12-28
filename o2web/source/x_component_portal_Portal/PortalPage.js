MWF.xApplication.portal.Portal.options.multitask = true;
MWF.xApplication.portal.Portal.Main = new Class({
    Implements: [Options, Events],

	options: {
        "portalId": "",
        "pageId": ""
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.portal.Portal.LP;
        if (this.status){
            this.options.portalId = this.status.portalId;
            this.options.pageId = this.status.pageId;
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
                        this.loadPortal(this.options.parameters);
                    }.bind(this));
                }else{
                    //this.mask.loadNode(this.content);
                    this.loadPortal(this.options.parameters);
                }
                if (callback) callback();
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
    toPortal: function(portal, page, par){
        this.options.portalId = portal;
        this.options.pageId = page;

        if (this.appForm) this.appForm.fireEvent("beforeClose");
        Object.keys(this.$events).each(function(k){
            this.removeEvents(k);
        }.bind(this));

        MWF.release(this.appForm);
        this.appForm = null;
        this.formNode.empty();
        this.loadPortal(par);
    },
    toPage: function(name, par){
        if (name){
            var m = (layout.mobile) ? "getPageByNameMobile" : "getPageByName";
            this.action[m](name, this.portal.id, function(json){
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
    loadPortal: function(par){
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
            var m = (layout.mobile) ? "getPageByNameMobile" : "getPageByName";
            this.action[m](this.options.pageId, this.options.portalId, function(json){
                // if (layout.mobile) {
                //     this.page = (json.data.mobileData) ? JSON.decode(MWF.decodeJsonString(json.data.mobileData)): null;
                //     if (!this.page || !this.page.json.moduleList.length){
                //         this.page = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
                //     }
                // }else{
                this.page = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
                // }
                this.openPortal(par);
            }.bind(this));
        }.bind(this));
    },

    openPortal: function(par){
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
                    "data": {}
                };

                this.appForm.workAction = this.action;
                this.appForm.app = this;
                this.appForm.load();

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
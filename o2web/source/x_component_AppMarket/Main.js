MWF.require("MWF.widget.MaskNode", null, false);
MWF.xApplication.AppMarket.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "AppMarket",
        "icon": "icon.png",
        "width": "1000",
        "height": "700",
        "title": MWF.xApplication.AppMarket.LP.title
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.AppMarket.LP;
        this.actions = MWF.Actions.get("x_program_center");
    },
    mask: function(){
        if (!this.maskNode){
            this.maskNode = new MWF.widget.MaskNode(this.contentNode, {"style": "bam"});
            this.maskNode.load();
        }
    },
    unmask: function(){
        if (this.maskNode) this.maskNode.hide(function(){
            MWF.release(this.maskNode);
            this.maskNode = null;
        }.bind(this));
    },
    loadApplication: function(callback){
        this.components = [];
        this.loadTitle();

        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
        this.contentModuleArea = new Element("div", {"styles": this.css.contentModuleArea}).inject(this.contentNode);
        this.setContentSize();
        this.addEvent("resize", this.setContentSize);

        //this.mask();
        this.loadCloudAppsContent();
    },

    loadTitle: function(){
        this.titleBar = new Element("div", {"styles": this.css.titleBar}).inject(this.content);
        this.titleActionNode = new Element("div", {"styles": this.css.titleActionNode,"text": this.lp.implodeLocal}).inject(this.titleBar);
        this.taskTitleTextNode = new Element("div", {"styles": this.css.titleTextNode,"text": this.lp.title}).inject(this.titleBar);
        this.titleActionNode.addEvent("click", function(){
            //this.implodeLocal();
            this.implodeLocal();
        }.bind(this));
    },
    implodeLocal: function(e){
        MWF.xDesktop.requireApp("AppCenter", "", function(){
            if (!this.uploadFileAreaNode){
                this.uploadFileAreaNode = new Element("div");
                var html = "<input name=\"file\" type=\"file\" accept=\".xapp\"/>";
                this.uploadFileAreaNode.set("html", html);
                this.fileUploadNode = this.uploadFileAreaNode.getFirst();
                this.fileUploadNode.addEvent("change", this.importLocalFile.bind(this));
            }else{
                if (this.fileUploadNode) this.fileUploadNode.destroy();
                this.uploadFileAreaNode.empty();
                var html = "<input name=\"file\" type=\"file\" accept=\".xapp\"/>";
                this.uploadFileAreaNode.set("html", html);
                this.fileUploadNode = this.uploadFileAreaNode.getFirst();
                this.fileUploadNode.addEvent("change", this.importLocalFile.bind(this));
            }
            this.fileUploadNode.click();
        }.bind(this));
    },
    importLocalFile: function(){
        var files = this.fileUploadNode.files;
        if (files.length){
            var file = files[0];
            var position = this.titleActionNode.getPosition(this.content);
            var size = this.contentNode.getSize();
            var width = size.x*0.9;
            if (width>600) width = 600;
            var height = size.y*0.9;
            var x = (size.x-width)/2;
            var y = (size.y-height)/2;

            var setupModule = null;
            var appCenter = new MWF.xApplication.AppCenter.Main();
            appCenter.inBrowser = true;
            appCenter.load(true);
            MWF.require("MWF.xDesktop.Dialog", function(){
                var dlg = new MWF.xDesktop.Dialog({
                    "title": this.lp.setupTitle,
                    "style": "appMarket",
                    "top": y+20,
                    "left": x,
                    "fromTop":position.y,
                    "fromLeft": position.x,
                    "width": width,
                    "height": height,
                    "html": "",
                    "maskNode": this.node,
                    "container": this.node,
                    "buttonList": [
                        {
                            "text": appCenter.lp.ok,
                            "action": function(){
                                if (setupModule) setupModule.setup();
                                this.close();
                            }
                        },
                        {
                            "text": appCenter.lp.cancel,
                            "action": function(){this.close();}
                        }
                    ]
                });
                dlg.show();

                setupModule = new MWF.xApplication.AppCenter.Module.SetupLocal(file, dlg, appCenter);

            }.bind(this));
        }
    },
    setContentSize: function(){
        var size = this.content.getSize();
        var titleSize = this.titleBar.getSize();
        var height = size.y-titleSize.y;
        this.contentNode.setStyles({"height": ""+height+"px", "overflow": "auto"});

        var max = size.x*0.98;
        var n = (size.x/170).toInt();
        var x = n*170;
        while (x>max){
            n--;
            x = n*170;
        }

        this.contentModuleArea.setStyle("width", ""+x+"px");
    },
    loadCloudAppsContent: function(){
        this.loadCloudApps(function(){
            if (MWF.AC.isAdministrator()) this.loadNewApp();
        }.bind(this));
    },
    loadCloudApps: function(callback){
        this.categoryList = [];
        this.itemList = [];
        this.actions.listModule({"categoryList":[]}, function(json){

            json.data.each(function(category){
                this.categoryList.push(category.category);
                category.moduleList.each(function(module){
                    //for (var i=0; i<20; i++)
                    this.itemList.push(new MWF.xApplication.AppMarket.Module(this, module));
                }.bind(this));
            }.bind(this));

            this.unmask();
        }.bind(this), function(xhr, text, error){
            this.unmask();
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        }.bind(this));
    }
});
MWF.xApplication.AppMarket.Module = new Class({
    initialize: function(app, data){
        this.app = app;
        this.data = data;
        this.lp = this.app.lp;
        this.css = this.app.css;
        this.content = this.app.contentModuleArea;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.moduleNode}).inject(this.content);
        this.iconAreaNode = new Element("div", {"styles": this.css.moduleIconAreaNode}).inject(this.node);
        this.iconNode = new Element("div", {"styles": this.css.moduleIconNode}).inject(this.iconAreaNode);
        if (this.data.icon){
            this.iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.icon+")");
            this.iconNode.setStyle("background-size", "cover");
        }

        this.contentNode = new Element("div", {"styles": this.css.moduleContentNode}).inject(this.node);
        this.nameNode = new Element("div", {"styles": this.css.moduleNameNode}).inject(this.contentNode);
        this.categoryNode = new Element("div", {"styles": this.css.moduleCategoryNode}).inject(this.contentNode);
        this.descriptionNode = new Element("div", {"styles": this.css.moduleDescriptionNode}).inject(this.contentNode);
        this.actionNode = new Element("div", {"styles": this.css.moduleActionNode}).inject(this.contentNode);

        this.nameNode.set("text", this.data.name);
        this.categoryNode.set("text", this.data.category);
        this.descriptionNode.set("text", this.data.description);
        this.actionNode.set("text", this.lp.download);

        this.loadEvent();
    },
    loadEvent: function(){
        this.node.addEvents({
            "mouseover": function(){this.setStyle("background-color", "#ffffff")},
            "mouseout": function(){this.setStyle("background-color", "#f5f5f5")}
        });

        this.actionNode.addEvent("click", function(e){
            this.downloadApp();
            e.stopPropagation();
        }.bind(this));
        this.node.addEvent("click", function(){
            this.openApp();
        }.bind(this));
    },
    downloadApp: function(){
        var position = this.actionNode.getPosition(this.app.content);
        var size = this.app.contentNode.getSize();
        var width = size.x*0.9;
        if (width>600) width = 600;
        var height = size.y*0.9;
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;

        var setupModule = null;
        MWF.require("MWF.xDesktop.Dialog", function(){
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.lp.setupTitle+" "+this.data.name,
                "style": "appMarket",
                "top": y+20,
                "left": x,
                "fromTop":position.y,
                "fromLeft": position.x,
                "width": width,
                "height": height,
                "html": "",
                "maskNode": this.app.content,
                "container": this.app.content,
                "buttonList": [
                    {
                        "text": this.lp.ok,
                        "action": function(){
                            if (setupModule) setupModule.setup();
                            this.close();
                        }
                    },
                    {
                        "text": this.lp.cancel,
                        "action": function(){this.close();}
                    }
                ]
            });
            dlg.show();
            setupModule = new MWF.xApplication.AppMarket.Module.Setup(this, dlg);
        }.bind(this));
    },
    openApp: function(){

    }
});

MWF.xApplication.AppMarket.Module.Setup = new Class({
    initialize: function(module, dlg){
        this.module = module;
        this.app = this.module.app;
        this.data = this.module.data;
        this.lp = this.module.lp;
        this.css = this.app.css;
        this.dlg = dlg;
        this.content = this.dlg.content;
        this.setupData = {};
        this.compareData = null;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.moduleSetupContentNode}).inject(this.content);
        this.loadTitle();
        this.loadContent();
    },
    loadTitle: function(){
        this.titleNode = new Element("div", {"styles": this.css.moduleSetupTitleNode}).inject(this.node);
        this.iconAreaNode = new Element("div", {"styles": this.css.moduleSetupIconAreaNode}).inject(this.titleNode);
        var iconNode = new Element("div", {"styles": this.css.moduleSetupIconNode}).inject(this.iconAreaNode);

        if (this.data.icon){
            iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.icon+")");
            iconNode.setStyle("background-size", "cover");
        }

        var contentNode = new Element("div", {"styles": this.css.moduleSetupTitleContentNode}).inject(this.titleNode);
        var nameNode = new Element("div", {"styles": this.css.moduleSetupNameNode}).inject(contentNode);
        var categoryNode = new Element("div", {"styles": this.css.moduleSetupCategoryNode}).inject(contentNode);
        var descriptionNode = new Element("div", {"styles": this.css.moduleSetupDescriptionNode}).inject(contentNode);
        nameNode.set("text", this.data.name);
        categoryNode.set("text", this.data.category);
        descriptionNode.set("text", this.data.description);
    },
    loadContent: function(){
        this.contentNode = new Element("div", {"styles": this.css.moduleSetupCompareContentNode}).inject(this.node);
        this.createLoading(this.contentNode);
        this.loadCompare();
    },
    createLoading: function(node){
        this.dlg.button.setStyle("display", "none");
        this.loadingAreaNode = new Element("div", {"styles": this.css.moduleLoadingAreaNode}).inject(node);
        var img = new Element("img", {
            "styles": this.css.moduleLoadingImgNode,
            "src": this.app.path+this.app.options.style+"/icon/loading.gif"
        }).inject(this.loadingAreaNode);
    },
    clearLoading: function(){
        if (this.loadingAreaNode){
            this.loadingAreaNode.destroy();
            this.loadingAreaNode = null;
        }
        this.dlg.button.setStyle("display", "block");
    },

    loadCompare: function(){
        this.app.actions.compareModule(this.data.id, function(json){
            this.clearLoading();
            this.setupData.flag = json.data.flag;
            this.createListArea();
            this.compareData = json.data;
            this.loadProcessList();
            this.loadPortalList();
            this.loadCMSList();
            this.loadQueryList();
            //json.data.processPlatformList

        }.bind(this));
    },
    createListArea: function(){
        this.contentAreaNode = new Element("div").inject(this.contentNode);
        this.contentInforNode = new Element("div", {"styles": this.css.moduleSetupContentInforNode, "text": this.lp.downloadInfor}).inject(this.contentAreaNode);
        //this.processArea = new Element("div", {"styles": this.css.moduleSetupListAreaNode}).inject(this.contentNode);
        this.processAreaTitle = new Element("div", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.process}).inject(this.contentAreaNode);
        this.processAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode}).inject(this.contentAreaNode);

        this.portalAreaTitle = new Element("div", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.portal}).inject(this.contentAreaNode);
        this.portalAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode}).inject(this.contentAreaNode);

        this.cmsAreaTitle = new Element("div", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.cms}).inject(this.contentAreaNode);
        this.cmsAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode}).inject(this.contentAreaNode);

        this.queryAreaTitle = new Element("div", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.query}).inject(this.contentAreaNode);
        this.queryAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode}).inject(this.contentAreaNode);
    },
    loadProcessList: function(){
        this.processListNodes = [];
        this.compareData.processPlatformList.each(function(item){
            this.processListNodes.push(new MWF.xApplication.AppMarket.Module.Setup.ProcessElement(this, this.processAreaContent, item));
        }.bind(this));
    },
    loadPortalList: function(){
        this.portalListNodes = [];
        this.compareData.portalList.each(function(item){
            this.portalListNodes.push(new MWF.xApplication.AppMarket.Module.Setup.PortalElement(this, this.portalAreaContent, item));
        }.bind(this));
    },
    loadCMSList: function(){
        this.cmsListNodes = [];
        this.compareData.cmsList.each(function(item){
            this.cmsListNodes.push(new MWF.xApplication.AppMarket.Module.Setup.CmsElement(this, this.cmsAreaContent, item));
        }.bind(this));
    },
    loadQueryList: function(){
        this.queryListNodes = [];
        this.compareData.queryList.each(function(item){
            this.queryListNodes.push(new MWF.xApplication.AppMarket.Module.Setup.QueryElement(this, this.queryAreaContent, item));
        }.bind(this));
    },
    setup: function(){
        this.setupData.flag = this.compareData.flag;
        this.setupData.processPlatformList = [];
        this.setupData.portalList = [];
        this.setupData.queryList = [];
        this.setupData.cmsList = [];

        this.getWriteData(this.processListNodes, this.setupData.processPlatformList);
        this.getWriteData(this.portalListNodes, this.setupData.portalList);
        this.getWriteData(this.cmsListNodes, this.setupData.cmsList);
        this.getWriteData(this.queryListNodes, this.setupData.queryList);

        this.contentAreaNode.setStyle("display", "none");
        this.createLoading(this.contentNode);
        this.app.actions.importModule(this.compareData.flag, this.setupData, function(){
            this.app.notice(this.module.data.name+" "+this.lp.setupSuccess, "success");
            this.clearLoading();
        }.bind(this));
    },
    getWriteData: function(nodes, json){
        nodes.each(function(item){
            if (item.action){
                var v = item.action.options[item.action.selectedIndex].get("value");
                if (v!="ignore"){
                    json.push({"id": item.data.id, "method":v});
                }
            }else{
                json.push({"id": item.data.id});
            }

        }.bind(this));
    }
});

MWF.xApplication.AppMarket.Module.SetupLocal = new Class({
    Extends: MWF.xApplication.AppMarket.Module.Setup,
    initialize: function(file, dlg, app){
        this.app = app;
        this.file = file;
        this.lp = this.app.lp;

        this.module = {
            "data": {
                "name": this.lp.localApp,
                "category": "",
                "description": ""
            }
        };

        this.data = this.module.data;
        this.css = this.app.css;
        this.dlg = dlg;
        this.content = this.dlg.content;
        this.setupData = {};
        this.compareData = null;
        this.load();
    },
    loadCompare: function(){
        var formData = new FormData();
        formData.append('file', this.file);

        this.app.actions.compareUpload(formData, this.file, function(json){
            this.clearLoading();
            this.setupData.flag = json.data.flag;
            this.createListArea();
            this.compareData = json.data;
            this.loadProcessList();
            this.loadPortalList();
            this.loadCMSList();
            this.loadQueryList();
            //json.data.processPlatformList

        }.bind(this));
    }
});

MWF.xApplication.AppMarket.Module.Setup.Element = new Class({
    initialize: function(setup, content, data){
        this.setup = setup;
        this.app = this.setup.app;
        this.data = data;
        this.lp = this.app.lp;
        this.css = this.app.css;
        this.content = content;
        this.load();
    },
    load: function(){
        this.contentNode = new Element("div", {"styles": this.css.moduleSetupListContentNode}).inject(this.content);
        this.iconNode = new Element("div", {"styles": this.css.moduleSetupListIconNode}).inject(this.contentNode);
        this.actionNode = new Element("div", {"styles": this.css.moduleSetupListActionNode}).inject(this.contentNode);
        this.inforNode = new Element("div", {"styles": this.css.moduleSetupListInforNode}).inject(this.contentNode);
        this.nameNode = new Element("div", {"styles": this.css.moduleSetupListNameNode}).inject(this.contentNode);
        this.nameNode.set(this.getNameContent());

        if (this.data.exist){
            this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/conflict.png) center center no-repeat");
            this.contentNode.setStyle("color", "#e86a58");
            this.inforNode.set("text", this.lp.conflict);

            this.action = new Element("select", {"styles": this.css.moduleSetupListActionSelectNode}).inject(this.actionNode);
            var options = "<option value='ignore' selected>"+this.lp.ignore+"</option>";
            options += "<option value='create'>"+this.lp.create+"</option>";
            options += "<option value='cover'>"+this.lp.cover+"</option>";
            this.action.set("html", options);

            // this.action.addEvent("change", function(e){
            //
            // }.bind(this));

        }else{
            this.action = new Element("select", {"styles": this.css.moduleSetupListActionSelectNode}).inject(this.actionNode);
            var options = "<option value='ignore'>"+this.lp.ignore+"</option>";
            options += "<option value='create' selected>"+this.lp.create+"</option>";
            this.action.set("html", options);

            //this.inforNode.set("text", this.lp.setup);
        }
    },
    getNameContent: function(){
        return {
            "title": this.lp.name+": "+this.data.name+" "+this.lp.id+": "+this.data.id,
            "text": this.data.name
        }
    }
});

MWF.xApplication.AppMarket.Module.Setup.ProcessElement = new Class({
    Extends: MWF.xApplication.AppMarket.Module.Setup.Element
});
MWF.xApplication.AppMarket.Module.Setup.PortalElement = new Class({
    Extends: MWF.xApplication.AppMarket.Module.Setup.Element
});
MWF.xApplication.AppMarket.Module.Setup.CmsElement = new Class({
    Extends: MWF.xApplication.AppMarket.Module.Setup.Element
});
MWF.xApplication.AppMarket.Module.Setup.QueryElement = new Class({
    Extends: MWF.xApplication.AppMarket.Module.Setup.Element
});
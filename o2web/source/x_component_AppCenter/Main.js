MWF.xApplication.AppCenter = MWF.xApplication.AppCenter || {};
MWF.xDesktop.requireApp("AppCenter", "lp."+MWF.language, null, false);
MWF.xApplication.AppCenter.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "AppCenter",
        "icon": "icon.png",
        "width": "1000",
        "height": "700",
        "title": MWF.xApplication.AppCenter.LP.title
    },
    onQueryLoad: function(){

        this.lp = MWF.xApplication.AppCenter.LP;
        this.actions = MWF.Actions.get("x_program_center");
    },
    loadApplication: function(callback){
        this.components = [];
        //this.node = new Element("div", {"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}}).inject(this.content);
        this.loadTitle();

        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
        this.contentModuleArea = new Element("div", {"styles": this.css.contentModuleArea}).inject(this.contentNode);
        this.setContentSize();
        this.addEvent("resize", this.setContentSize);

        this.loadModuleContent();
    },
    loadTitle: function(){
        this.titleBar = new Element("div", {"styles": this.css.titleBar}).inject(this.content);

        if (MWF.AC.isProcessPlatformCreator()){
            this.createApplicationNode = new Element("div", {
                "styles": this.css.createApplicationNode,
                "title": this.lp.export
            }).inject(this.titleBar);
            this.createApplicationNode.addEvent("click", function(){
                this.createApplication();
            }.bind(this));

            this.importApplicationNode = new Element("div", {
                "styles": this.css.setupApplicationNode,
                "title": this.lp.setup
            }).inject(this.titleBar);
            this.importApplicationNode.addEvent("click", function(){
                this.implodeLocal();
            }.bind(this));


        }
        this.taskTitleTextNode = new Element("div", {"styles": this.css.titleTextNode,"text": this.lp.title}).inject(this.titleBar);
    },


    implodeLocal: function(){
        if (!this.uploadFileAreaNode){
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" type=\"file\" accept=\".xapp\"/>";
            this.uploadFileAreaNode.set("html", html);
            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", this.implodeLocalFile.bind(this));
        }else{
            if (this.fileUploadNode) this.fileUploadNode.destroy();
            this.uploadFileAreaNode.empty();
            var html = "<input name=\"file\" type=\"file\" accept=\".xapp\"/>";
            this.uploadFileAreaNode.set("html", html);
            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", this.implodeLocalFile.bind(this));
        }
        this.fileUploadNode.click();
    },
    implodeLocalFile: function(){
        var files = this.fileUploadNode.files;
        if (files.length){
            var file = files[0];
            var position = this.titleBar.getPosition(this.content);
            var size = this.contentNode.getSize();
            var width = size.x*0.9;
            if (width>600) width = 600;
            var height = size.y*0.9;
            var x = (size.x-width)/2;
            var y = (size.y-height)/2;

            var setupModule = null;
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
                    "maskNode": this.content,
                    "container": this.content,
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
                setupModule = new MWF.xApplication.AppCenter.Module.SetupLocal(file, dlg, this);
            }.bind(this));
        }
    },





    setContentSize: function(){
        var size = this.content.getSize();
        var titleSize = this.titleBar.getSize();
        var height = size.y-titleSize.y;
        this.contentNode.setStyles({"height": ""+height+"px", "overflow": "auto"});

        var max = size.x*0.98;
        var n = (size.x/320).toInt();
        var x = n*320;
        while (x>max){
            n--;
            x = n*320;
        }

        this.contentModuleArea.setStyle("width", ""+x+"px");
    },
    loadModuleContent: function(){
        this.actions.listStructure(function(json){
            this.moduleList = json.data;
            if (this.moduleList.length){
                this.moduleList.each(function(module){
                    new MWF.xApplication.AppCenter.Module(this, module);
                }.bind(this));
            }else{
                this.createEmptyElement();
            }
        }.bind(this));
    },
    createEmptyElement: function(){
        this.emptyNode = new Element("div", {"styles": this.css.emptyNode}).inject(this.contentModuleArea);
        if (MWF.AC.isProcessPlatformCreator()){
            this.emptyNode.set("text", this.lp.emptyModuleManagerInfo);
            this.emptyNode.addEvent("click", function(){
                this.createApplication();
            }.bind(this));
        }else{
            this.emptyNode.set("text", this.lp.emptyModuleInfo);
        }
    },

    createApplication: function(target,id){
        if(id) this.curAppId = id;
        if(target) this.content = target;
        new MWF.xApplication.AppCenter.Exporter(this);

    }
});
MWF.xApplication.AppCenter.Module = new Class({
    initialize: function(app, data){
        this.app = app;
        this.data = data;
        this.json = JSON.decode(this.data.data);
        this.json.structure = this.data.id;
        this.lp = this.app.lp;
        this.css = this.app.css;
        this.content = this.app.contentModuleArea;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.moduleNode}).inject(this.content);
        this.iconNode = new Element("div", {"styles": this.css.moduleIconNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.moduleContentNode}).inject(this.node);
        this.nameNode = new Element("div", {"styles": this.css.moduleNameNode}).inject(this.contentNode);
        this.categoryNode = new Element("div", {"styles": this.css.moduleCategoryNode}).inject(this.contentNode);
        this.descriptionNode = new Element("div", {"styles": this.css.moduleDescriptionNode}).inject(this.contentNode);


        this.actionNode = new Element("div", {"styles": this.css.moduleActionNode}).inject(this.contentNode);
        this.viewActionNode = new Element("div", {"styles": this.css.moduleActionNode}).inject(this.contentNode);


        this.deleteActionNode = new Element("div", {"styles": this.css.moduleDeleteActionNode}).inject(this.contentNode);
        this.deleteActionNode.set("text", this.lp.delete);


        this.nameNode.set("text", this.data.name);
        this.categoryNode.set("text", this.data.createTime);
        this.descriptionNode.set("text", this.data.description);
        this.actionNode.set("text", this.lp.output);
        this.viewActionNode.set("text", this.lp.outputView);
        this.viewActionNode.setStyle("margin-left", "10px");
        this.loadEvent();
    },
    loadEvent: function(){
        this.actionNode.addEvent("click", function(e){

            var uri = this.app.actions.action.actions["download"].uri;
            uri = uri.replace("{flag}", this.json.structure);
            window.open(o2.filterUrl(this.app.actions.action.address+uri));

        }.bind(this));

        this.viewActionNode.addEvent("click", function(e){

            this.outputApp();
            e.stopPropagation();
        }.bind(this));

        this.node.addEvent("click", function(){
            this.openApp();
        }.bind(this));
        var  _self = this;
        this.deleteActionNode.addEvent("click",function (e) {
            this.app.confirm("warn", e, "tip", "confirm delete?", 300, 120, function(){
                this.structureRes = _self.app.actions.removeStructure(_self.json.structure,function(json){
                    _self.app.notice("success");
                    _self.node.destroy();
                }.bind(this));
                this.close();
            }, function(){
                this.close();
            }, null);

        }.bind(this));
    },
    outputApp: function(){
        new MWF.xApplication.AppCenter.Exporter(this.app, this.json)
    },
    openApp: function(){

    }
});
MWF.xApplication.AppCenter.Exporter = new Class({
    initialize: function(app, selectData){
        this.app = app;
        this.lp = this.app.lp;
        this.css = this.app.css;
        this.structure = null;
        this.dlg = null;
        this.selectData = selectData || {
            "structure": "",
            "name": "",
            "description": "",
            "processPlatformList": [],
            "portalList": [],
            "queryList": [],
            "serviceModuleList": [],
            "cmsList": []
        };
        this.setp = 1;
        this.load();
    },
    loadStructure: function(){
        this.structureRes = this.app.actions.outputStructure(function(json){

            if(this.app.curAppId){

                var cmsList = json.data.cmsList;
                var portalList = json.data.portalList;
                var processPlatformList = json.data.processPlatformList;
                var queryList = json.data.queryList;
                var serviceModuleList = json.data.serviceModuleList;

                var curAppData = {
                    "portalList":[],
                    "cmsList":[],
                    "processPlatformList":[],
                    "queryList":[],
                    "serviceModuleList":[]
                };
                cmsList.each(function (cms) {
                    if(cms.id===this.app.curAppId) {
                        curAppData.cmsList.push(cms);
                    }
                }.bind(this));

                portalList.each(function (portal) {
                    if(portal.id === this.app.curAppId){
                        curAppData.portalList.push(portal);
                    }
                }.bind(this));

                processPlatformList.each(function (processPlatform) {
                    if(processPlatform.id===this.app.curAppId){
                        curAppData.processPlatformList.push(processPlatform)
                    }
                }.bind(this));

                queryList.each(function (query) {
                    if(query.id===this.app.curAppId) {
                        curAppData.queryList.push(query);
                    }
                }.bind(this));
                serviceModuleList.each(function (service) {
                    if(service.id===this.app.curAppId) {
                        curAppData.serviceModuleList.push(service);
                    }
                }.bind(this));


                this.structure = curAppData;
            }else{
                this.structure = json.data;
            }

            this.createContent();
        }.bind(this));
    },
    showDlg: function(callback){
        var position = this.app.createApplicationNode.getPosition(this.app.content);
        var size = this.app.content.getSize();
        var width = size.x*0.9;
        if (width>600) width = 600;
        var height = size.y*0.8;
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (y<80) y = 80;

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function(){
            this.dlg = new MWF.xDesktop.Dialog({
                "title": this.lp.exportTitle,
                "style": "appCenter",
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
                        "text": this.lp.next,
                        "action": function(){
                            _self.next();
                            //this.close();
                        }
                    },
                    {
                        "text": this.lp.prev,
                        "action": function(){
                            _self.prev();
                            //this.close();
                        }
                    },
                    {
                        "text": this.lp.ok,
                        "action": function(){
                            _self.output();
                            //this.close();
                        }
                    },
                    {
                        "text": this.lp.cancel,
                        "action": function(){
                            this.close();
                            if (_self.structureRes && _self.structureRes.res){
                                if (_self.structureRes.res.isRunning()){_self.structureRes.res.cancel();}
                                _self.structureRes = null;
                            }
                        }
                    }
                ],
                "onPostShow": function(){
                    if (callback) callback();
                }.bind(this),
                "onPostClose": function() {
                    if(_self.app){
                        _self.app.fireEvent("exporterClose");
                    }
                    MWF.release(_self);
                }
            });
            this.dlg.show();
        }.bind(this));
    },
    checkInput: function(){
        var name = this.moduleNameInput.get("value");
        var category = this.moduleCategoryInput.get("value");
        var description = this.moduleDescriptionInput.get("value");

        if (!name){
            this.app.notice(this.lp.noNameError, "error");
            return false;
        }
        if (!this.selectData.processPlatformList.length &&
            !this.selectData.portalList.length &&
            !this.selectData.queryList.length &&
            !this.selectData.serviceModuleList.length &&
            !this.selectData.cmsList.length){
            this.app.notice(this.lp.noModuleError, "error");
            return false;
        }
        this.selectData.name = name;
        this.selectData.category = category;
        this.selectData.description = description;
        return true;
    },
    next: function(){
        if (this.setp==1){
            if (this.checkInput()) this.showStatus();
        }
    },
    prev: function(){
        if (this.step==2){
            if (this.statusContentNode){
                this.statusContentNode.destroy();
                this.statusContentNode = null;
            }
            this.contentNode.setStyle("display", "block");
            this.okBut.setStyle("display", "none");
            this.prevBut.setStyle("display", "nonde");
            this.nextBut.setStyle("display", "inline");
        }
    },
    output: function(){
        if (this.step==2){
            if (this.checkInput()){

                MWF.require("o2.widget.Mask", function(){
                    this.mask = new o2.widget.Mask();
                    this.mask.loadNode(this.dlg.node);

                    this.app.actions.output(this.selectData, function(json){
                        var uri = this.app.actions.action.actions["download"].uri;
                        uri = uri.replace("{flag}", json.data.flag);

                        if (this.mask) this.mask.hide();
                        this.dlg.close();
                        window.open(o2.filterUrl(this.app.actions.action.address+uri));
                        this.app.fireEvent("exporterClose");
                        MWF.release(this);
                    }.bind(this), function(xhr, text, error){
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
                        if (this.mask) this.mask.hide();
                    }.bind(this));
                }.bind(this));
            }
        }
    },
    showStatus: function(){
        this.statusContentNode = new Element("div", {"styles": this.css.moduleSelectContentAreaNode}).inject(this.contentNode, "after");
        this.statusTitleNode = new Element("div", {"styles": this.css.moduleSelectTitleNode, "text": this.lp.selected}).inject(this.statusContentNode);
        this.statusInfoNode = new Element("div", {"styles": this.css.moduleSelectContentNode}).inject(this.statusContentNode);

        var size = this.contentNode.getSize();
        var position = this.contentNode.getPosition(this.contentNode.getOffsetParent());
        var css = {
            "height": ""+size.y+"px",
            "width": ""+size.x+"px",
            "top": ""+position.y+"px",
            "left": ""+position.x+"px",
            "background-color": "#eeeeee"
        };
        this.statusContentNode.setStyles(css);
        var titleSize = this.statusTitleNode.getSize();
        var h = size.y-titleSize.y-20;
        this.statusInfoNode.setStyle("height", ""+h+"px");

        this.showStatusList();
        this.contentNode.setStyle("display", "none");
        this.okBut.setStyle("display", "inline");
        this.prevBut.setStyle("display", "inline");
        this.nextBut.setStyle("display", "none");
        this.step = 2;
    },
    showStatusList: function(){
        this.showStatusItemList("processPlatformList", ["processList", "formList", "applicationDictList", "scriptList", "fileList"]);
        this.showStatusItemList("portalList", ["pageList", "scriptList", "widgetList", "fileList"]);
        this.showStatusItemList("cmsList", ["categoryInfoList", "formList", "appDictList", "scriptList"]);
        this.showStatusItemList("queryList", ["viewList", "statList", "revealList", "tableList", "statementList", "importModelList"]);
        this.showStatusItemList("serviceModuleList", ["agentList", "invokeList"]);

    },
    showStatusItemList: function(listName, subList){
        this.selectData[listName].each(function(app){
            new Element("div", {"styles": this.css.moduleStatusInforNode1, "text": "["+this.lp[listName]+"] "+(app.name || app.appName)}).inject(this.statusInfoNode);
            subList.each(function(name){
                if (app[name] && app[name].length) app[name].each(function(process){
                    new Element("div", {"styles": this.css.moduleStatusInforNode2, "text": "["+this.lp[name]+"] "+(process.name || process.categoryName)}).inject(this.statusInfoNode);
                }.bind(this));
            }.bind(this));
        }.bind(this));
    },

    load: function(){
        this.showDlg(function(){
            this.createLayout();
            this.loadStructure();
        }.bind(this));
    },
    createLayout: function(){
        this.nextBut = this.dlg.button.getFirst("input");
        this.prevBut = this.nextBut.getNext("input");
        this.okBut = this.prevBut.getNext("input");
        if (this.setp==1){
            this.okBut.setStyle("display", "none");
            this.prevBut.setStyle("display", "none");
        }
        this.node = new Element("div", {"styles": this.css.moduleSetupContentNode}).inject(this.dlg.content);
        this.titleNode = new Element("div", {"styles": this.css.moduleSetupTitleNode}).inject(this.node);
        var iconNode = new Element("div", {"styles": this.css.moduleIconNode}).inject(this.titleNode);
        var contentNode = new Element("div", {"styles": this.css.moduleSetupTitleContentNode}).inject(this.titleNode);
        var nameNode = new Element("div", {"styles": this.css.moduleSetupNameNode}).inject(contentNode);
        var categoryNode = new Element("div", {"styles": this.css.moduleSetupCategoryNode}).inject(contentNode);

        categoryNode.hide();
        var descriptionNode = new Element("div", {"styles": this.css.moduleSetupDescriptionNode}).inject(contentNode);

        var nameTitleNode = new Element("div", {"styles": this.css.moduleInputTitleNode, "text": this.lp.moduleName}).inject(nameNode);
        var nameContentNode = new Element("div", {"styles": this.css.moduleInputContentNode}).inject(nameNode);
        this.moduleNameInput = new Element("input", {"styles": this.css.moduleInputNode}).inject(nameContentNode);

        var categoryTitleNode = new Element("div", {"styles": this.css.moduleInputTitleNode, "text": this.lp.moduleCategory}).inject(categoryNode);

        var categoryContentNode = new Element("div", {"styles": this.css.moduleInputContentNode}).inject(categoryNode);
        this.moduleCategoryInput = new Element("input", {"styles": this.css.moduleInputNode}).inject(categoryContentNode);

        var descriptionTitleNode = new Element("div", {"styles": this.css.moduleInputTitleNode, "text": this.lp.moduleDescription}).inject(descriptionNode);
        var descriptionContentNode = new Element("div", {"styles": this.css.moduleInputContentNode}).inject(descriptionNode);
        this.moduleDescriptionInput = new Element("input", {"styles": this.css.moduleInputNode}).inject(descriptionContentNode);

        this.moduleNameInput.set("value", this.selectData.name);
        this.moduleCategoryInput.set("value", this.selectData.category);
        this.moduleDescriptionInput.set("value", this.selectData.description);

        this.contentNode = new Element("div", {"styles": this.css.moduleSetupCompareContentNode}).inject(this.node);
        this.setListContentSize();
        this.createLoading(this.contentNode);
    },
    setListContentSize: function(){
        var size = this.dlg.content.getSize();
        var h = size.y;
        var titleH = this.titleNode.getSize().y+10;
        var contentH = h-titleH-10;
        this.contentNode.setStyle("height", ""+contentH+"px");
    },

    createLoading: function(node){
        //this.okBut.setStyle("display", "none");
        this.nextBut.setStyle("display", "none");
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
        //this.okBut.setStyle("display", "inline");
        this.nextBut.setStyle("display", "inline");
    },

    createContent: function(){
        this.clearLoading();
        this.createListArea();
        this.loadProcessList();
        this.loadPortalList();
        this.loadCMSList();
        this.loadQueryList();
        this.loadServiceList();
        //this.structure
    },
    createListArea: function(){
        this.contentAreaNode = new Element("div").inject(this.contentNode);
        // this.contentInforNode = new Element("div", {"styles": this.css.moduleSetupContentInforNode, "text": this.lp.selectModules}).inject(this.contentAreaNode);
        // //this.processArea = new Element("div", {"styles": this.css.moduleSetupListAreaNode}).inject(this.contentNode);
        //
        // this.listAreaNode = new Element("div").inject(this.contentAreaNode);
        this.processAreaTitle = new Element("div.moduleSetupListAreaTitle", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.process}).inject(this.contentAreaNode);
        this.processAreaContent = new Element("div.", {"styles": this.css.moduleSetupListAreaContentNode}).inject(this.contentAreaNode);

        this.portalAreaTitle = new Element("div.moduleSetupListAreaTitle", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.portal}).inject(this.contentAreaNode);
        this.portalAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode}).inject(this.contentAreaNode);

        this.cmsAreaTitle = new Element("div.moduleSetupListAreaTitle", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.cms}).inject(this.contentAreaNode);
        this.cmsAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode}).inject(this.contentAreaNode);

        this.queryAreaTitle = new Element("div.moduleSetupListAreaTitle", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.query}).inject(this.contentAreaNode);
        this.queryAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode}).inject(this.contentAreaNode);


        this.serviceAreaTitle = new Element("div.moduleSetupListAreaTitle", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.service}).inject(this.contentAreaNode);
        this.serviceAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode}).inject(this.contentAreaNode);

        this.contentAreaNode.getElements(".moduleSetupListAreaTitle").each(function (node){
            var spanNode = new Element("span",{"text":"︽","style":"float:right;font-size:16px;padding-right:15px;cursor:pointer"}).inject(node);
            spanNode.addEvent("click",function (){
                if(spanNode.get("text") === "︽"){
                    node.getNext().hide();
                    spanNode.set("text","︾");
                }else {
                    node.getNext().show();
                    spanNode.set("text","︽");
                }
            }.bind(this));
        }.bind(this));

    },
    loadProcessList: function(){
        this.processListNodes = [];
        this.structure.processPlatformList.each(function(item){
            var postData = null;
            for (var i=0; i<this.selectData.processPlatformList.length; i++){
                if (this.selectData.processPlatformList[i].id == item.id){
                    postData = this.selectData.processPlatformList[i];
                    break;
                }
            }
            this.processListNodes.push(new MWF.xApplication.AppCenter.Exporter.ProcessElement(this, this.processAreaContent, item, postData));
        }.bind(this));
    },
    loadPortalList: function(){
        this.portalListNodes = [];
        this.structure.portalList.each(function(item){
            this.portalListNodes.push(new MWF.xApplication.AppCenter.Exporter.PortalElement(this, this.portalAreaContent, item));
        }.bind(this));
    },
    loadCMSList: function(){
        this.cmsListNodes = [];
        this.structure.cmsList.each(function(item){
            this.cmsListNodes.push(new MWF.xApplication.AppCenter.Exporter.CmsElement(this, this.cmsAreaContent, item));
        }.bind(this));
    },
    loadQueryList: function(){
        this.queryListNodes = [];
        this.structure.queryList.each(function(item){
            this.queryListNodes.push(new MWF.xApplication.AppCenter.Exporter.QueryElement(this, this.queryAreaContent, item));
        }.bind(this));
    },
    loadServiceList: function(){
        this.serviceListNodes = [];
        this.structure.serviceModuleList.each(function(item){
            this.serviceListNodes.push(new MWF.xApplication.AppCenter.Exporter.ServiceElement(this, this.serviceAreaContent, item));
        }.bind(this));
    }
});
MWF.xApplication.AppCenter.Exporter.Element = new Class({
    initialize: function(exporter, content, data, postData){
        this.exporter = exporter;
        this.app = this.exporter.app;
        this.data = data;
        this.lp = this.app.lp;
        this.css = this.app.css;
        this.content = content;
        this.initPostData(postData);
        //this.selectStatus = selectStatus || "none";
        this.load();
    },
    initPostData: function(postData){
        this.postData = postData || {
            "id": this.data.id,
            "name": this.data.name || this.data.appName,
            "alias": this.data.alias,
            "description": this.data.description,
            "processList": [],
            "formList": [],
            "applicationDictList": [],
            "scriptList": [],
            "fileList": []
        };
    },
    load: function(){
        this.contentNode = new Element("div", {"styles": this.css.moduleSetupListContentNode}).inject(this.content);
        this.iconNode = new Element("div", {"styles": this.css.moduleSetupListIconNode}).inject(this.contentNode);
        this.actionNode = new Element("div", {"styles": this.css.moduleSetupListActionNode}).inject(this.contentNode);
        this.inforNode = new Element("div", {"styles": this.css.moduleSetupListInforNode}).inject(this.contentNode);
        this.nameNode = new Element("div", {"styles": this.css.moduleSetupListNameNode}).inject(this.contentNode);
        this.nameNode.set(this.getNameContent());

        // switch (this.selectStatus){
        //     case "all":
        //         this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_all.png) center center no-repeat");
        //         break;
        //     case "part":
        //         this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_part.png) center center no-repeat");
        //         break;
        //     default:
        this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_none.png) center center no-repeat");
        //}
        this.action = new Element("div", {"styles": this.css.moduleSelectActionNode, "text": this.lp.select}).inject(this.actionNode);

        this.setEvent();

        this.checkSelect(this.postData);
    },
    setEvent: function(){
        this.contentNode.addEvents({
            "mouseover": function(){this.contentNode.setStyles(this.css.moduleSetupListContentNode_over);}.bind(this),
            "mouseout": function(){this.contentNode.setStyles(this.css.moduleSetupListContentNode);}.bind(this)
        });
        this.iconNode.addEvent("click", this.selectAll.bind(this));
        this.action.addEvent("click", function(){
            this.selectElements();
        }.bind(this));
        this.nameNode.addEvent("click", function(){
            this.selectElements();
        }.bind(this));
    },
    selectAll: function(){
        var selectData = this.postData;
        if (selectData.processList.length || selectData.formList.length || selectData.applicationDictList.length || selectData.scriptList.length || selectData.fileList.length){
            if (selectData.processList.length===this.data.processList.length &&
                selectData.formList.length===this.data.formList.length &&
                selectData.applicationDictList.length===this.data.applicationDictList.length &&
                selectData.scriptList.length===this.data.scriptList.length &&
                selectData.fileList.length===this.data.fileList.length){
                selectData =  {
                    "processList": [],
                    "formList": [],
                    "applicationDictList": [],
                    "scriptList": [],
                    "fileList": []
                };
            }else{
                selectData =  {
                    "processList": this.data.processList,
                    "formList": this.data.formList,
                    "applicationDictList": this.data.applicationDictList,
                    "scriptList": this.data.scriptList,
                    "fileList": this.data.fileList
                };
            }
        }else{
            selectData =  {
                "processList": this.data.processList,
                "formList": this.data.formList,
                "applicationDictList": this.data.applicationDictList,
                "scriptList": this.data.scriptList,
                "fileList": this.data.fileList
            };
        }

        this.checkSelect(selectData);
    },
    getNameContent: function(){
        return {
            "title": this.lp.name+": "+this.data.name+" "+this.lp.id+": "+this.data.id,
            "text": this.data.name
        }
    },
    selectElements: function(){
        new MWF.xApplication.AppCenter.Exporter.Element.Selector(this, this.data);
    },
    checkSelect: function(selectData){
        this.postData.processList = selectData.processList;
        this.postData.formList = selectData.formList;
        this.postData.applicationDictList = selectData.applicationDictList;
        this.postData.scriptList = selectData.scriptList;
        this.postData.fileList = selectData.fileList;

        this.exporter.selectData.processPlatformList.erase(this.postData);
        if (selectData.processList.length || selectData.formList.length || selectData.applicationDictList.length || selectData.scriptList.length || selectData.fileList.length){
            this.exporter.selectData.processPlatformList.push(this.postData);
            if (selectData.processList.length==this.data.processList.length &&
                selectData.formList.length==this.data.formList.length &&
                selectData.applicationDictList.length==this.data.applicationDictList.length &&
                selectData.scriptList.length==this.data.scriptList.length &&
                selectData.fileList.length==this.data.fileList.length){
                this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_all.png) center center no-repeat");
            }else{
                this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_part.png) center center no-repeat");
            }
        }else{

            this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_none.png) center center no-repeat");
        }
    }
});
MWF.xApplication.AppCenter.Exporter.ProcessElement = new Class({
    Extends: MWF.xApplication.AppCenter.Exporter.Element
});
MWF.xApplication.AppCenter.Exporter.PortalElement = new Class({
    Extends: MWF.xApplication.AppCenter.Exporter.Element,
    initPostData: function(postData){
        this.postData = postData || {
            "id": this.data.id,
            "name": this.data.name || this.data.appName,
            "alias": this.data.alias,
            "description": this.data.description,
            "pageList": [],
            "scriptList": [],
            "widgetList": [],
            "fileList": []
        };
    },
    selectAll: function(){
        var selectData = this.postData;
        if (selectData.pageList.length || selectData.scriptList.length || selectData.widgetList.length || selectData.fileList.length){
            if (selectData.pageList.length===this.data.pageList.length &&
                selectData.scriptList.length===this.data.scriptList.length &&
                selectData.widgetList.length===this.data.widgetList.length &&
                selectData.fileList.length===this.data.fileList.length){
                selectData =  {
                    "pageList": [],
                    "scriptList": [],
                    "widgetList": [],
                    "fileList": []
                };
            }else{
                selectData =  {
                    "pageList": this.data.pageList,
                    "scriptList": this.data.scriptList,
                    "widgetList": this.data.widgetList,
                    "fileList": this.data.fileList
                };
            }
        }else{
            selectData =  {
                "pageList": this.data.pageList,
                "scriptList": this.data.scriptList,
                "widgetList": this.data.widgetList,
                "fileList": this.data.fileList
            };
        }
        this.checkSelect(selectData);
    },
    selectElements: function(){
        new MWF.xApplication.AppCenter.Exporter.Element.PortalSelector(this, this.data);
    },
    checkSelect: function(selectData){
        this.postData.pageList = selectData.pageList;
        this.postData.scriptList = selectData.scriptList;
        this.postData.widgetList = selectData.widgetList;
        this.postData.fileList = selectData.fileList;

        this.exporter.selectData.portalList.erase(this.postData);
        if (selectData.pageList.length || selectData.scriptList.length || selectData.widgetList.length || selectData.fileList.length){
            this.exporter.selectData.portalList.push(this.postData);
            if (selectData.pageList.length==this.data.pageList.length &&
                selectData.scriptList.length==this.data.scriptList.length &&
                selectData.widgetList.length==this.data.widgetList.length &&
                selectData.fileList.length==this.data.fileList.length){
                this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_all.png) center center no-repeat");
            }else{
                this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_part.png) center center no-repeat");
            }
        }else{

            this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_none.png) center center no-repeat");
        }
    }
});
MWF.xApplication.AppCenter.Exporter.CmsElement = new Class({
    Extends: MWF.xApplication.AppCenter.Exporter.Element,
    getNameContent: function(){
        return {
            "title": this.lp.name+": "+this.data.appName+" "+this.lp.id+": "+this.data.id,
            "text": this.data.appName
        }
    },
    initPostData: function(postData){
        this.postData = postData || {
            "id": this.data.id,
            "name": this.data.name || this.data.appName,
            "alias": this.data.alias,
            "description": this.data.description,
            "categoryInfoList": [],
            "formList": [],
            "appDictList": [],
            "scriptList": [],
            "fileList": []
        };
    },
    selectElements: function(){
        new MWF.xApplication.AppCenter.Exporter.Element.CmsSelector(this, this.data);
    },
    selectAll: function(){
        var selectData = this.postData;
        if (selectData.categoryInfoList.length || selectData.formList.length || selectData.appDictList.length || selectData.scriptList.length || selectData.fileList.length){
            if (selectData.categoryInfoList.length===this.data.categoryInfoList.length &&
                selectData.formList.length===this.data.formList.length &&
                selectData.appDictList.length===this.data.appDictList.length &&
                selectData.scriptList.length===this.data.scriptList.length &&
                selectData.fileList.length===this.data.fileList.length){
                selectData =  {
                    "categoryInfoList": [],
                    "formList": [],
                    "appDictList": [],
                    "scriptList": [],
                    "fileList": []
                };
            }else{
                selectData =  {
                    "categoryInfoList": this.data.categoryInfoList,
                    "formList": this.data.formList,
                    "appDictList": this.data.appDictList,
                    "scriptList": this.data.scriptList,
                    "fileList": this.data.fileList
                };
            }
        }else{
            selectData =  {
                "categoryInfoList": this.data.categoryInfoList,
                "formList": this.data.formList,
                "appDictList": this.data.appDictList,
                "scriptList": this.data.scriptList,
                "fileList": this.data.fileList
            };
        }
        this.checkSelect(selectData);
    },
    checkSelect: function(selectData){
        this.postData.categoryInfoList = selectData.categoryInfoList;
        this.postData.formList = selectData.formList;
        this.postData.appDictList = selectData.appDictList;
        this.postData.scriptList = selectData.scriptList;
        this.postData.fileList = selectData.fileList;
        this.exporter.selectData.cmsList.erase(this.postData);
        if (selectData.categoryInfoList.length || selectData.formList.length || selectData.appDictList.length || selectData.scriptList.length || selectData.fileList.length){
            this.exporter.selectData.cmsList.push(this.postData);
            if (selectData.categoryInfoList.length===this.data.categoryInfoList.length &&
                selectData.formList.length===this.data.formList.length &&
                selectData.appDictList.length===this.data.appDictList.length &&
                selectData.scriptList.length===this.data.scriptList.length &&
                selectData.fileList.length===this.data.fileList.length){
                this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_all.png) center center no-repeat");
            }else{
                this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_part.png) center center no-repeat");
            }
        }else{

            this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_none.png) center center no-repeat");
        }
    }
});
MWF.xApplication.AppCenter.Exporter.QueryElement = new Class({
    Extends: MWF.xApplication.AppCenter.Exporter.Element,
    initPostData: function(postData){
        this.postData = postData || {
            "id": this.data.id,
            "name": this.data.name || this.data.appName,
            "alias": this.data.alias,
            "description": this.data.description,
            "viewList": [],
            "statList": [],
            "revealList": [],
            "statementList": [],
            "tableList": [],
            "importModelList": []
        };
    },
    selectElements: function(){
        new MWF.xApplication.AppCenter.Exporter.Element.QuerySelector(this, this.data);
    },
    selectAll: function(){
        var selectData = this.postData;
        if (selectData.viewList.length || selectData.statList.length || selectData.revealList.length|| selectData.statementList.length|| selectData.tableList.length|| selectData.importModelList.length){
            if (selectData.viewList.length===this.data.viewList.length &&
                selectData.statList.length===this.data.statList.length &&
                selectData.statementList.length===this.data.statementList.length &&
                selectData.tableList.length===this.data.tableList.length &&
                selectData.revealList.length===this.data.revealList.length &&
                selectData.importModelList.length===this.data.importModelList.length){
                selectData =  {
                    "viewList": [],
                    "statList": [],
                    "revealList": [],
                    "statementList": [],
                    "tableList": [],
                    "importModelList": []
                };
            }else{
                selectData =  {
                    "viewList": this.data.viewList,
                    "statList": this.data.statList,
                    "revealList": this.data.revealList,
                    "statementList": this.data.statementList,
                    "tableList": this.data.tableList,
                    "importModelList": this.data.importModelList,
                };
            }
        }else{
            selectData =  {
                "viewList": this.data.viewList,
                "statList": this.data.statList,
                "revealList": this.data.revealList,
                "statementList": this.data.statementList,
                "tableList": this.data.tableList,
                "importModelList": this.data.importModelList
            };
        }
        this.checkSelect(selectData);
    },
    checkSelect: function(selectData){
        this.postData.viewList = selectData.viewList;
        this.postData.statList = selectData.statList;
        this.postData.revealList = selectData.revealList;

        this.postData.statementList = selectData.statementList;
        this.postData.tableList = selectData.tableList;
        this.postData.importModelList = selectData.importModelList;

        this.exporter.selectData.queryList.erase(this.postData);

        if (selectData.viewList.length || selectData.statList.length || selectData.revealList.length|| selectData.statementList.length|| selectData.tableList.length|| selectData.importModelList.length){
            this.exporter.selectData.queryList.push(this.postData);
            if (selectData.viewList.length==this.data.viewList.length &&
                selectData.statList.length==this.data.statList.length &&
                selectData.statementList.length==this.data.statementList.length &&
                selectData.tableList.length==this.data.tableList.length &&
                selectData.importModelList.length==this.data.importModelList.length &&
                selectData.revealList.length==this.data.revealList.length){
                this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_all.png) center center no-repeat");
            }else{
                this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_part.png) center center no-repeat");
            }
        }else{

            this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_none.png) center center no-repeat");
        }
    }
});
MWF.xApplication.AppCenter.Exporter.ServiceElement = new Class({
    Extends: MWF.xApplication.AppCenter.Exporter.Element,
    initPostData: function(postData){
        this.postData = postData || {
            "id": this.data.id,
            "name": this.data.name || this.data.appName,
            "alias": this.data.alias,
            "description": this.data.description,
            "agentList": [],
            "invokeList": []
        };
    },
    selectElements: function(){
        new MWF.xApplication.AppCenter.Exporter.Element.ServiceSelector(this, this.data);
    },
    selectAll: function(){
        var selectData = this.postData;
        if (selectData.agentList.length  || selectData.invokeList.length){
            if (selectData.agentList.length===this.data.agentList.length  || selectData.invokeList.length===this.data.invokeList.length){
                selectData =  {
                    "agentList": [],
                    "invokeList": []
                };
            }else{
                selectData =  {
                    "agentList": this.data.agentList,
                    "invokeList": this.data.invokeList
                };
            }
        }else{
            selectData =  {
                "agentList": this.data.agentList,
                "invokeList": this.data.invokeList
            };
        }
        this.checkSelect(selectData);
    },
    checkSelect: function(selectData){
        this.postData.agentList = selectData.agentList;
        this.postData.invokeList = selectData.invokeList;

        this.exporter.selectData.serviceModuleList.erase(this.postData);
        if (selectData.agentList.length || selectData.invokeList.length ){
            this.exporter.selectData.serviceModuleList.push(this.postData);
            if (selectData.agentList.length===this.data.agentList.length &&
                selectData.invokeList.length===this.data.invokeList.length
            ){
                this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_all.png) center center no-repeat");
            }else{
                this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_part.png) center center no-repeat");
            }
        }else{

            this.iconNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/sel_none.png) center center no-repeat");
        }
    }
});
MWF.xApplication.AppCenter.Exporter.Element.Selector = new Class({
    initialize: function(element, data){
        this.element = element;
        this.app = this.element.app;
        this.data = data;
        this.lp = this.app.lp;
        this.css = this.app.css;
        this.content = this.element.contentNode;
        this.areaNode = this.element.exporter.contentNode;
        this.selectData = this.initData();
        this.load();
    },
    initData: function(){
        return {
            "processList": [],
            "formList": [],
            "applicationDictList": [],
            "scriptList": [],
            "fileList": []
        }
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.moduleSelectContentAreaNode}).inject(this.areaNode, "after");
        this.titleNode = new Element("div", {"styles": this.css.moduleSelectTitleNode, "text": this.data.name}).inject(this.node);
        var size = this.content.getSize();
        this.node.setStyle("width", ""+size.x+"px");
        this.node.position({
            "relativeTo": this.content,
            "position": "topLeft",
            "edge": "topLeft"
        });
        this.element.exporter.dlg.button.setStyle("display", "none");

        this.show();
    },
    show: function(){
        var size = this.areaNode.getSize();
        var height = size.y+40;
        var width = size.x;
        var position = this.areaNode.getPosition(this.areaNode.getOffsetParent());
        //var oStyles = this.node.getStyles("height", "width", "top", "left", "background-color");
        //this.node.store("ostyles", oStyles);
        var css = {
            "height": ""+height+"px",
            "width": ""+width+"px",
            "top": ""+position.y+"px",
            "left": ""+position.x+"px",
            "background-color": "#eeeeee"
        };
        this.morph = new Fx.Morph(this.node, {"duration": 100});
        this.morph.start(css).chain(function(){
            this.loadContent();
        }.bind(this));
    },
    hide: function(){
        if (!this.morph) this.morph = new Fx.Morph(this.node, {"duration": 100});
        this.areaNode.setStyle("display", "block");
        this.element.exporter.dlg.button.setStyle("display", "block");

        var size = this.content.getSize();
        var height = size.y;
        var width = size.x;
        var position = this.content.getPosition(this.areaNode);
        var thisPosition = this.node.getPosition(this.node.getOffsetParent());
        var x = thisPosition.x+position.x;
        var y = thisPosition.y+position.y;

        var css = {
            "height": ""+height+"px",
            "width": ""+width+"px",
            "top": ""+y+"px",
            "left": ""+x+"px"
        };
        this.contentNode.destroy();
        this.morph.start(css).chain(function(){
            this.node.destroy();
            MWF.release(this);
        }.bind(this));
    },

    loadContent: function(){
        this.areaNode.setStyle("display", "none");
        this.contentNode = new Element("div", {"styles": this.css.moduleSelectContentNode}).inject(this.node);
        this.buttonNode = new Element("div", {"styles": this.css.moduleSelectButtonNode}).inject(this.node);
        this.cancelButton = new Element("div", {"styles": this.css.moduleSelectButtonActionNode, "text": this.lp.cancel}).inject(this.buttonNode);
        this.okButton = new Element("div", {"styles": this.css.moduleSelectButtonActionNode, "text": this.lp.ok}).inject(this.buttonNode);

        this.setContentHeight();
        this.loadContentList();

        this.cancelButton.addEvent("click", function(){
            this.hide();
        }.bind(this));
        this.okButton.addEvent("click", function(){
            this.checkSelect();
        }.bind(this));
    },
    checkSelect: function() {
        this.selectData.processList = this.getCheckedList(this.listProcessContent);
        this.selectData.formList = this.getCheckedList(this.listFormContent);
        this.selectData.applicationDictList = this.getCheckedList(this.listDictContent);
        this.selectData.scriptList = this.getCheckedList(this.listScriptContent);
        this.selectData.fileList = this.getCheckedList(this.listFileContent);
        this.element.checkSelect(this.selectData);
        this.hide();
    },
    getCheckedList: function(node){
        var list = [];
        node.getElements("input").each(function(input){
            if (input.checked){
                list.push(input.retrieve("data"));
            }
        }.bind());
        return list;
    },
    setContentHeight: function(){
        var size = this.node.getSize();
        var titleSize = this.titleNode.getSize();
        var buttonSize = this.buttonNode.getSize();

        var h = size.y-titleSize.y-buttonSize.y;
        this.contentNode.setStyle("height", ""+h+"px");
    },

    loadContentList: function(){
        this.contentAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentNode);
        this.listProcessContent = this.listProcess("processList");
        this.listFormContent = this.listProcess("formList");
        this.listDictContent = this.listProcess("applicationDictList");
        this.listScriptContent = this.listProcess("scriptList");
        this.listFileContent = this.listProcess("fileList");
    },
    listProcess: function(name){
        var title = new Element("div", {"styles": this.css.moduleSelectContentTitleNode}).inject(this.contentAreaNode);
        var titleActionNode = new Element("div", {"styles": this.css.moduleSelectContentTitleButtonNode}).inject(title);
        var inverseAction = new Element("div", {"styles": this.css.moduleSelectContentTitleButtonActionNode, "text": this.lp.inverse}).inject(titleActionNode);
        var selectAllAction = new Element("div", {"styles": this.css.moduleSelectContentTitleButtonActionNode, "text": this.lp.selectAll}).inject(titleActionNode);
        var titleText = new Element("div", {"styles": this.css.moduleSelectContentTitleTextNode, "text": this.lp[name]}).inject(title);
        //moduleSelectContentTitleButtonActionNode
        var listContent = new Element("div", {"styles": this.css.moduleSelectContentListNode}).inject(this.contentAreaNode);
        this.listProcessItems(name, listContent);

        inverseAction.addEvent("click", function(){
            inputs = listContent.getElements("input");
            inputs.each(function(checkbox){
                checkbox.set("checked", !checkbox.get("checked"));
            });
        });
        selectAllAction.addEvent("click", function(){
            inputs = listContent.getElements("input");
            inputs.each(function(checkbox){
                checkbox.set("checked", true);
            });
        });

        return listContent;
    },
    listProcessItems: function(name, listContent){
        this.data[name].each(function(item){
            var div = new Element("div", {"styles": this.css.moduleSelectContentListItemNode}).inject(listContent);
            var flag = false;
            var selectedList = this.element.postData[name];
            if (selectedList){
                for (var i=0; i<selectedList.length; i++){
                    if (selectedList[i].id==item.id){
                        flag = true;
                        break;
                    }
                }
            }
            var checkNode = new Element("input", {
                "styles": {"float": "left"},
                "type": "checkbox",
                //"checked": (this.element.postData[name] && this.element.postData[name].indexOf(item)!=-1),
                "checked": flag,
                "value": item.id
            }).inject(div);
            new Element("div", {
                "styles": {"float": "left"},
                "text": this.getItemName(item),
                "events": {
                    "click": function(){checkNode.click();}
                }
            }).inject(div);
            checkNode.store("data", item);
        }.bind(this));
    },
    getItemName: function(item){
        return item.name;
    }
});
MWF.xApplication.AppCenter.Exporter.Element.ProcessSelector = new Class({
    Extends: MWF.xApplication.AppCenter.Exporter.Element.Selector
});
MWF.xApplication.AppCenter.Exporter.Element.PortalSelector = new Class({
    Extends: MWF.xApplication.AppCenter.Exporter.Element.Selector,
    initData: function(){
        return {
            "pageList": [],
            "scriptList": [],
            "widgetList": [],
            "fileList": []
        }
    },
    checkSelect: function() {
        this.selectData.pageList = this.getCheckedList(this.listPageContent);
        this.selectData.scriptList = this.getCheckedList(this.listScriptContent);
        this.selectData.widgetList = this.getCheckedList(this.listWidgetContent);
        this.selectData.fileList = this.getCheckedList(this.listFileContent);
        this.element.checkSelect(this.selectData);
        this.hide();
    },
    loadContentList: function(){
        this.contentAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentNode);
        this.listPageContent = this.listProcess("pageList");
        this.listScriptContent = this.listProcess("scriptList");
        this.listWidgetContent = this.listProcess("widgetList");
        this.listFileContent = this.listProcess("fileList");
    }
});
MWF.xApplication.AppCenter.Exporter.Element.CmsSelector = new Class({
    Extends: MWF.xApplication.AppCenter.Exporter.Element.Selector,
    initData: function(){
        return {
            "categoryInfoList": [],
            "formList": [],
            "appDictList": [],
            "scriptList": [],
            "fileList": []
        }
    },
    checkSelect: function() {
        this.selectData.categoryInfoList = this.getCheckedList(this.listCategoryInfoContent);
        this.selectData.formList = this.getCheckedList(this.listFormContent);
        this.selectData.appDictList = this.getCheckedList(this.listDictContent);
        this.selectData.scriptList = this.getCheckedList(this.listScriptContent);
        this.selectData.fileList = this.getCheckedList(this.listFileContent);
        this.element.checkSelect(this.selectData);
        this.hide();
    },
    loadContentList: function(){
        this.contentAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentNode);
        this.listCategoryInfoContent = this.listProcess("categoryInfoList");
        this.listFormContent = this.listProcess("formList");
        this.listDictContent = this.listProcess("appDictList");
        this.listScriptContent = this.listProcess("scriptList");
        this.listFileContent = this.listProcess("fileList");
    },
    getItemName: function(item){
        return item.name || item.categoryName;
    }
});
MWF.xApplication.AppCenter.Exporter.Element.QuerySelector = new Class({
    Extends: MWF.xApplication.AppCenter.Exporter.Element.Selector,
    initData: function(){
        return {
            "viewList": [],
            "statList": [],
            "revealList": [],
            "tableList": [],
            "statementList": [],
            "importModelList": []
        }
    },
    checkSelect: function() {
        this.selectData.viewList = this.getCheckedList(this.listViewContent);
        this.selectData.statList = this.getCheckedList(this.listStatContent);
        this.selectData.revealList = this.getCheckedList(this.listRevealContent);
        this.selectData.statementList = this.getCheckedList(this.listStatementContent);
        this.selectData.tableList = this.getCheckedList(this.listTableContent);
        this.selectData.importModelList = this.getCheckedList(this.listImportModelContent);
        this.element.checkSelect(this.selectData);
        this.hide();
    },
    loadContentList: function(){
        this.contentAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentNode);
        this.listViewContent = this.listProcess("viewList");
        this.listStatContent = this.listProcess("statList");
        this.listRevealContent = this.listProcess("revealList");

        this.listTableContent = this.listProcess("tableList");
        this.listStatementContent = this.listProcess("statementList");
        this.listImportModelContent = this.listProcess("importModelList");
    }
});
MWF.xApplication.AppCenter.Exporter.Element.ServiceSelector = new Class({
    Extends: MWF.xApplication.AppCenter.Exporter.Element.Selector,
    initData: function(){
        return {
            "agentList": [],
            "invokeList": []
        }
    },
    checkSelect: function() {
        this.selectData.agentList = this.getCheckedList(this.listAgentContent);
        this.selectData.invokeList = this.getCheckedList(this.listInvokeContent);

        this.element.checkSelect(this.selectData);
        this.hide();
    },
    loadContentList: function(){
        this.contentAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentNode);

        this.listAgentContent = this.listProcess("agentList");
        this.listInvokeContent = this.listProcess("invokeList");

    },
    getItemName: function(item){

        return item.name
    }
});

MWF.xApplication.AppCenter.Module.Setup = new Class({
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
        this.titleNode = new Element("div", {"styles": this.css.moduleSetupTitleNode2}).inject(this.node);
        this.iconAreaNode = new Element("div", {"styles": this.css.moduleSetupIconAreaNode}).inject(this.titleNode);
        var iconNode = new Element("div", {"styles": this.css.moduleSetupIconNode}).inject(this.iconAreaNode);

        if (this.data.icon){
            iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.icon+")");
            iconNode.setStyle("background-size", "cover");
        }

        var contentNode = new Element("div", {"styles": this.css.moduleSetupTitleContentNode2}).inject(this.titleNode);
        var nameNode = new Element("div", {"styles": this.css.moduleSetupNameNode2}).inject(contentNode);
        var categoryNode = new Element("div", {"styles": this.css.moduleSetupCategoryNode2}).inject(contentNode);
        var descriptionNode = new Element("div", {"styles": this.css.moduleSetupDescriptionNode2}).inject(contentNode);
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
            this.loadServiceList();
            //json.data.processPlatformList
        }.bind(this));
    },
    createListArea: function(){
        this.contentAreaNode = new Element("div").inject(this.contentNode);
        this.contentInforNode = new Element("div", {"styles": this.css.moduleSetupContentInforNode, "text": this.lp.downloadInfor}).inject(this.contentAreaNode);
        //this.processArea = new Element("div", {"styles": this.css.moduleSetupListAreaNode}).inject(this.contentNode);
        this.processAreaTitle = new Element("div", {"styles": this.css.moduleSetupListAreaTitleNode2, "text": this.lp.process}).inject(this.contentAreaNode);
        this.processAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode}).inject(this.contentAreaNode);

        this.portalAreaTitle = new Element("div", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.portal}).inject(this.contentAreaNode);
        this.portalAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode2}).inject(this.contentAreaNode);

        this.cmsAreaTitle = new Element("div", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.cms}).inject(this.contentAreaNode);
        this.cmsAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode2}).inject(this.contentAreaNode);

        this.queryAreaTitle = new Element("div", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.query}).inject(this.contentAreaNode);
        this.queryAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode2}).inject(this.contentAreaNode);

        this.serviceAreaTitle = new Element("div", {"styles": this.css.moduleSetupListAreaTitleNode, "text": this.lp.service}).inject(this.contentAreaNode);
        this.serviceAreaContent = new Element("div", {"styles": this.css.moduleSetupListAreaContentNode2}).inject(this.contentAreaNode);
    },
    loadProcessList: function(){
        this.processListNodes = [];
        this.compareData.processPlatformList.each(function(item){
            this.processListNodes.push(new MWF.xApplication.AppCenter.Module.Setup.ProcessElement(this, this.processAreaContent, item));
        }.bind(this));
    },
    loadPortalList: function(){
        this.portalListNodes = [];
        this.compareData.portalList.each(function(item){
            this.portalListNodes.push(new MWF.xApplication.AppCenter.Module.Setup.PortalElement(this, this.portalAreaContent, item));
        }.bind(this));
    },
    loadCMSList: function(){
        this.cmsListNodes = [];
        this.compareData.cmsList.each(function(item){
            this.cmsListNodes.push(new MWF.xApplication.AppCenter.Module.Setup.CmsElement(this, this.cmsAreaContent, item));
        }.bind(this));
    },
    loadQueryList: function(){
        this.queryListNodes = [];
        this.compareData.queryList.each(function(item){
            this.queryListNodes.push(new MWF.xApplication.AppCenter.Module.Setup.QueryElement(this, this.queryAreaContent, item));
        }.bind(this));
    },
    loadServiceList: function(){
        this.serviceListNodes = [];
        this.compareData.serviceModuleList.each(function(item){
            this.serviceListNodes.push(new MWF.xApplication.AppCenter.Module.Setup.ServiceElement(this, this.serviceAreaContent, item));
        }.bind(this));
    },
    setup: function(){
        this.setupData.flag = this.compareData.flag;
        this.setupData.processPlatformList = [];
        this.setupData.portalList = [];
        this.setupData.queryList = [];
        this.setupData.serviceModuleList = [];
        this.setupData.cmsList = [];

        this.getWriteData(this.processListNodes, this.setupData.processPlatformList);
        this.getWriteData(this.portalListNodes, this.setupData.portalList);
        this.getWriteData(this.cmsListNodes, this.setupData.cmsList);
        this.getWriteData(this.queryListNodes, this.setupData.queryList);

        this.getWriteData(this.serviceListNodes, this.setupData.serviceModuleList);

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
MWF.xApplication.AppCenter.Module.SetupLocal = new Class({
    Extends: MWF.xApplication.AppCenter.Module.Setup,
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
        debugger;
        this.app.actions.compareUpload(formData, this.file, function(json){
            this.clearLoading();
            this.setupData.flag = json.data.flag;
            this.createListArea();
            this.compareData = json.data;
            this.loadProcessList();
            this.loadPortalList();
            this.loadCMSList();
            this.loadQueryList();
            this.loadServiceList();
            //json.data.processPlatformList

        }.bind(this));
    }
});
MWF.xApplication.AppCenter.Module.Setup.Element = new Class({
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
            var options = "<option value='cover' selected>"+this.lp.cover+"</option>";
            options += "<option value='create'>"+this.lp.create+"</option>";
            options += "<option value='ignore'>"+this.lp.ignore+"</option>";
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
MWF.xApplication.AppCenter.Module.Setup.ProcessElement = new Class({
    Extends: MWF.xApplication.AppCenter.Module.Setup.Element
});
MWF.xApplication.AppCenter.Module.Setup.PortalElement = new Class({
    Extends: MWF.xApplication.AppCenter.Module.Setup.Element
});
MWF.xApplication.AppCenter.Module.Setup.CmsElement = new Class({
    Extends: MWF.xApplication.AppCenter.Module.Setup.Element
});
MWF.xApplication.AppCenter.Module.Setup.QueryElement = new Class({
    Extends: MWF.xApplication.AppCenter.Module.Setup.Element
});
MWF.xApplication.AppCenter.Module.Setup.ServiceElement = new Class({
    Extends: MWF.xApplication.AppCenter.Module.Setup.Element
});

MWF.xApplication.Setting.servers = MWF.xApplication.Setting.servers || {};
MWF.xApplication.Setting.servers.ApplicationServers = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.applicationServers = [];
        this.content = this.explorer.applicationServerContent;
        this.page = this.app.serverPage;
        this.currentDoc = null;
        this.load();
    },
    load: function(){
        this.actions.listApplicationServer(function(json){
            json.data.each(function(serverJson){
                this.applicationServers.push(new MWF.xApplication.Setting.servers.ApplicationServer(this, serverJson));
            }.bind(this));

            this.createAddAction();
            this.setApplicationServerAreaWidth();
            this.setApplicationServerAreaWidthFun = this.setApplicationServerAreaWidth.bind(this);
            this.app.addEvent("resize", this.setApplicationServerAreaWidthFun);
        }.bind(this));
    },
    createAddAction: function(){
        this.createServerAction = new Element("div", {"styles": this.css.applicationServerCreateAction}).inject(this.content);
        this.createServerAction.addEvents({
            "mouseover": function(){this.createServerAction.setStyles( this.css.applicationServerCreateAction_over);}.bind(this),
            "mouseout": function(){this.createServerAction.setStyles( this.css.applicationServerCreateAction);}.bind(this),
            "click": function(){this.createServer();}.bind(this),
        });
    },
    createServer: function(){
        var server = {
            "list": this,
            "app": this.app,
            "json": {
                "contextList": [],
                "planList": []
            },
            "node": this.createServerAction,
            "name": "",
            "reload": function(){
                this.list.applicationServers.push(new MWF.xApplication.Setting.servers.ApplicationServer(this.list, this.json));
            }
        }
        this.currentDoc = new MWF.xApplication.Setting.servers.ApplicationServer.Document(server);
    },
    setApplicationServerAreaWidth: function(){
        var count = this.applicationServers.length;
        var width = this.explorer.contentAreaNode.getSize().x;
        var nodeWidth = this.applicationServers[0].node.getSize().x;
        var x1 = this.applicationServers[0].node.getStyle("margin-left").toInt();
        var x2 = this.applicationServers[0].node.getStyle("margin-right").toInt();
        nodeWidth = nodeWidth+x1+x2;

        var n = (width/nodeWidth).toInt();
        var w = nodeWidth*n;
        this.content.setStyle("width", ""+w+"px");
    },
    getDepolyableList: function(callback){
        this.actions.listDepolyable(function(json){
            this.depolyableList = json.data;
            if (callback) callback(json.data);
        }.bind(this));
    },
    destroy: function(){
        if (this.setApplicationServerAreaWidthFun) this.app.removeEvent("resize", this.setApplicationServerAreaWidthFun);
        this.applicationServers.each(function(server){
            server.destroy();
        }.bind(this));
        if (this.currentDoc) this.currentDoc.destroy();
        this.content.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Setting.servers.ApplicationServer = new Class({
    Implements: [Events],
    initialize: function(list, json){
        this.list = list
        this.explorer = this.list.explorer;
        this.app = this.list.explorer.app;
        this.json = json;
        this.container = this.list.content;
        this.css = this.app.css;
        this.name = this.json.name;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.applicationServerNode}).inject(this.container);
        if (this.list.createServerAction) this.node.inject(this.list.createServerAction, "before");

        this.iconNode = new Element("div", {"styles": this.css.applicationServerIconNode}).inject(this.node);

        this.statusNode = new Element("div", {"styles": this.css.applicationServerStatusNode}).inject(this.node);
        if (this.json.status=="connected"){
            this.statusNode.setStyle("background-color", "#23b107");
        }else{
            this.statusNode.setStyle("background-color", "#999");
        }

        this.textNode = new Element("div", {"styles": this.css.applicationServerTextNode}).inject(this.node);
        this.nameNode = new Element("div", {"styles": this.css.applicationServerNameNode}).inject(this.textNode);
        this.hostNode = new Element("div", {"styles": this.css.applicationServerHostNode}).inject(this.textNode);
        this.adminNode = new Element("div", {"styles": this.css.applicationServerAdminNode}).inject(this.textNode);
        this.messageNode = new Element("div", {"styles": this.css.applicationServerMessageNode}).inject(this.textNode);
        this.setServerText();

        this.node.addEvent("click", this.open.bind(this));

        //this.checkWidthFun = this.checkWidth.bind(this);
        //this.app.addEvent("resize", this.checkWidthFun);
    },
    setServerText: function(){
        this.nameNode.set("text", (this.json.name || ""));
        this.hostNode.set("text", (this.json.host || "")+" : "+(this.json.port || ""));
        this.adminNode.set("text", (this.json.username || ""));
        this.messageNode.set("text", (this.json.message || ""));
    },
    open: function(){
        this.list.currentDoc = new MWF.xApplication.Setting.servers.ApplicationServer.Document(this);
    },
    reload: function(){
        this.app.actions.getAppServer(this.json.name, function(json){
            this.name = this.json.name;
            this.json = json.data;
            this.nameNode.set("text", this.json.name);
            this.hostNode.set("text", this.json.host+" : "+this.json.port);
            this.adminNode.set("text", this.json.username);
            this.messageNode.set("text", this.json.message);
        }.bind(this));
    },

    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Setting.servers.ApplicationServer.Document = new Class({
    Implements: [Events],
    initialize: function(server){
        this.server = server;
        this.list = this.server.list
        this.app = this.server.app;
        this.json = this.server.json;
        this.container = this.list.explorer.container;
        this.css = this.app.css;
        this.apps = [];
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.applicationServerDocumentNode}).inject(this.container);
        this.setNodeSize();
        this.show();
        //this.createForm();
    },
    setNodeSize: function(){
        var size = this.server.node.getSize();
        var position = this.server.node.getPosition(this.server.node.getOffsetParent());
        this.node.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px",
            "top": ""+position.y+"px",
            "left": ""+position.x+"px"
        });
    },
    show: function(){
        var size = this.list.page.contentNodeArea.getSize();
        var position = this.list.page.contentNodeArea.getPosition(this.list.page.contentNodeArea.getOffsetParent());
        var css = {
            "width": ""+size.x+"px",
            "height": ""+size.y+"px",
            "top": ""+position.y+"px",
            "left": ""+position.x+"px"
        }
        this.morph = new Fx.Morph(this.node,{
            "duration": 100,
            "transition": Fx.Transitions.Sine.easeOut
        });
        this.morph.start(css).chain(function(){
            this.list.content.setStyle("display", "none");

            this.createForm();

            this.setDocumentSizeFun = this.setDocumentSize.bind(this);
            this.setDocumentSize();
            this.app.addEvent("resize", this.setDocumentSizeFun);
        }.bind(this));
    },
    setDocumentSize: function(){
        var size = this.list.page.contentNodeArea.getSize();
        var actionSize = this.actionNode.getSize();
        var h = size.y-actionSize.y;
        this.inforNode.setStyle("height", ""+h+"px");
        this.applicationAreaNode.setStyle("height", ""+h+"px");
        //this.inforNode.setStyle("min-height", ""+h+"px");
        this.node.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px",
        });
    },
    createForm: function(){
        this.createActions();
        this.createBaseInfo();
    },
    createActions: function(){
        this.actionNode = new Element("div", {"styles": this.css.applicationServerDocumentActionNode}).inject(this.node);
        this.saveAction = new Element("div", {"styles": this.css.applicationServerDocumentSaveNode}).inject(this.actionNode);
        if (this.server.name) this.deleteAction = new Element("div", {"styles": this.css.applicationServerDocumentDeleteNode}).inject(this.actionNode);
        if (this.server.name) this.deployAction = new Element("div", {"title": this.app.lp.deploy, "styles": this.css.applicationServerDocumentDeployNode}).inject(this.actionNode);

        this.closeAction = new Element("div", {"styles": this.css.applicationServerDocumentCloseNode}).inject(this.actionNode);

        this.saveAction.addEvents({
            "mouseover": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode_over);}.bind(this),
            "mouseout": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode);}.bind(this),
            "mousedown": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode_down);}.bind(this),
            "mouseup": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode_over);}.bind(this),
            "click": function(e){this.saveDocument();}.bind(this)
        });

        if (this.deleteAction){
            this.deleteAction.addEvents({
                "mouseover": function(){this.deleteAction.setStyles(this.css.applicationServerDocumentDeleteNode_over);}.bind(this),
                "mouseout": function(){this.deleteAction.setStyles(this.css.applicationServerDocumentDeleteNode);}.bind(this),
                "mousedown": function(){this.deleteAction.setStyles(this.css.applicationServerDocumentDeleteNode_down);}.bind(this),
                "mouseup": function(){this.deleteAction.setStyles(this.css.applicationServerDocumentDeleteNode_over);}.bind(this),
                "click": function(e){this.deleteDocument(e);}.bind(this)
            });
        }

        this.closeAction.addEvents({
            "mouseover": function(){this.closeAction.setStyles(this.css.applicationServerDocumentCloseNode_over);}.bind(this),
            "mouseout": function(){this.closeAction.setStyles(this.css.applicationServerDocumentCloseNode);}.bind(this),
            "mousedown": function(){this.closeAction.setStyles(this.css.applicationServerDocumentCloseNode_down);}.bind(this),
            "mouseup": function(){this.closeAction.setStyles(this.css.applicationServerDocumentCloseNode_over);}.bind(this),
            "click": function(e){this.closeDocument();}.bind(this)
        });

        if (this.deployAction){
            this.deployAction.addEvents({
                "mouseover": function(){this.deployAction.setStyles(this.css.applicationServerDocumentDeployNode_over);}.bind(this),
                "mouseout": function(){this.deployAction.setStyles(this.css.applicationServerDocumentDeployNode);}.bind(this),
                "mousedown": function(){this.deployAction.setStyles(this.css.applicationServerDocumentDeployNode_down);}.bind(this),
                "mouseup": function(){this.deployAction.setStyles(this.css.applicationServerDocumentDeployNode_over);}.bind(this),
                "click": function(e){this.deploy(e);}.bind(this)
            });
        }
    },
    createBaseInfo: function(){
        this.inforAreaNode = new Element("div", {"styles": this.css.applicationServerDocumentInforAreaNode}).inject(this.node);
        this.inforNode = new Element("div", {"styles": this.css.applicationServerDocumentInforNode}).inject(this.inforAreaNode);
        var html = "<table cellSpacing='8px' width='90%' align='center'>" +
            "<tr><td>name<input value='"+(this.json.name || "")+"'/></td></tr>" +
            "<tr><td>order<input value='"+(this.json.order || "0")+"'/></td></tr>" +
            "<tr><td>containerType<input value='"+(this.json.containerType || "")+"'/></td></tr>" +
            "<tr><td>host<input value='"+(this.json.host || "")+"'/></td></tr>" +
            "<tr><td>port<input value='"+(this.json.port || "")+"'/></td></tr>" +
            "<tr><td>proxyHost<input value='"+(this.json.proxyHost || "")+"'/></td></tr>" +
            "<tr><td>proxyPort<input value='"+(this.json.proxyPort || "")+"'/></td></tr>" +
            "<tr><td>username<input value='"+(this.json.username || "")+"'/></td></tr>" +
            "<tr><td>password<input type='password' value='"+(this.json.password || "")+"'/></td></tr>" +
            "</table>";




        this.inforNode.set("html", html);
        var tds = this.inforNode.getElements("td");
        var inputs = this.inforNode.getElements("input");

        tds.setStyles(this.css.applicationServerDocumentTdNode);
        inputs.setStyles(this.css.applicationServerDocumentInputNode);

        this.applicationAreaNode = new Element("div", {"styles": this.css.applicationServerDocumentApplicationAreaNode}).inject(this.inforAreaNode);
        this.applicationNode = new Element("div", {"styles": this.css.applicationServerDocumentApplicationNode}).inject(this.applicationAreaNode);

        this.listApplications();
    },
    listApplications: function(){
        var html = "<table cellPadding='5px' cellSpacing='0' width='100%' align='center'>" +
            "<tr><th align='left'>name</th><th align='left'>description</th><th align='left'>weight</th><th align='left'>deployed</th><th align='left'>plan</th></tr>" +
            "</table>";
        this.applicationNode.set("html", html);
        this.applicationTable = this.applicationNode.getElement("table");

        this.list.getDepolyableList(function(){
            this.list.depolyableList.each(function(json, i){
                var color = "#FFF";
                if (i%2==0) color = "#f3f4ff";
                this.apps.push(new MWF.xApplication.Setting.servers.ApplicationServer.Document.App(this, json.name, color));
            }.bind(this));
        }.bind(this));
    },
    closeDocument: function(){
        this.node.empty();
        this.list.content.setStyle("display", "block");
        var size = this.server.node.getSize();
        var position = this.server.node.getPosition(this.server.node.getOffsetParent());
        var css = {
            "width": ""+size.x+"px",
            "height": ""+size.y+"px",
            "top": ""+position.y+"px",
            "left": ""+position.x+"px"
        }
        //this.morph = new Fx.Morph(this.node,{
        //    "duration": 100,
        //    "transition": Fx.Transitions.Sine.easeIn
        //});
        this.morph.start(css).chain(function(){
            this.destroy();
        }.bind(this));
    },
    destroy: function(){
        this.app.removeEvent("resize", this.setDocumentSizeFun);
        this.apps.each(function(app){
            app.destroy();
        }.bind(this));
        this.node.destroy();
        this.list.currentDoc = null;
        MWF.release(this);
    },
    saveDocument: function(){
        debugger;
        var inputs = this.inforNode.getElements("input");
        this.json.name = inputs[0].get("value");
        this.json.order = inputs[1].get("value");
        this.json.host = inputs[3].get("value");
        this.json.port = inputs[4].get("value");
        this.json.proxyHost = inputs[5].get("value");
        this.json.proxyPort = inputs[6].get("value");
        this.json.username = inputs[7].get("value");
        this.json.password = inputs[8].get("value");

        this.apps.each(function(app){
            if (app.weightInput){
                if (app.planAppArr.length){
                    var weight = app.weightInput.get("value").toFloat();
                    if (!isNaN(weight)){
                        app.planAppArr[0].weight = weight;
                    }
                }
            }
        }.bind(this));

        if (this.server.name){
            this.app.actions.updateAppServer(this.server.name, this.json, function(){
                this.closeDocument();
                this.server.reload();
            }.bind(this));
        }else{
            this.app.actions.addAppServer(this.json, function(){
                this.closeDocument();
                this.server.reload();
            }.bind(this));
        }

    },
    deleteDocument: function(e){
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.deleteAppServer_title, this.app.lp.deleteAppServer, "350", "120", function(){
            _self.app.actions.removeAppServer(_self.server.name, function(){
                this.closeDocument();
                this.server.destroy();
            }.bind(_self));
            this.close();
        }, function(){
            this.close();
        });
    },
    deploy: function(e){
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.deployAppServer_title, {"html": this.app.lp.deployAppServer}, "350", "150", function(){
            var input = this.content.getElement("input");
            var force = "false";
            if (input.checked) force = "true";

            var inputs = _self.inforNode.getElements("input");
            _self.json.name = inputs[0].get("value");
            _self.json.order = inputs[1].get("value");
            _self.json.host = inputs[3].get("value");
            _self.json.port = inputs[4].get("value");
            _self.json.username = inputs[5].get("value");
            _self.json.password = inputs[6].get("value");
            _self.json.weight = inputs[7].get("value");

            if (_self.server.name){
                _self.app.actions.updateAppServer(_self.server.name, _self.json, function(){
                    this.app.actions.deploy(this.server.name, force, function(){
                        this.closeDocument();
                        this.server.reload();
                    }.bind(this));
                }.bind(_self));
            }
            this.close();

        }, function(){
            this.close();
        });
    }

});
MWF.xApplication.Setting.servers.ApplicationServer.Document.App = new Class({
    Implements: [Events],
    initialize: function(doc, appName, color){
        this.document = doc;
        this.server = this.document.server;
        this.app = this.server.app;
        this.json = this.server.json;
        this.css = this.app.css;
        this.appName = appName;
        this.table = this.document.applicationTable;
        this.status = "";
        this.load(color);
    },
    load: function(color){
        this.checkbox = new Element("input", {"type": "checkbox", "name": this.appName});

        this.tr = new Element("tr", {"styles": {"background-color": color}}).inject(this.table);
        var td = new Element("td", {"text": this.appName}).inject(this.tr);
        td = new Element("td").inject(this.tr);

        this.weightTd = new Element("td").inject(this.tr);
        this.weightTd.setStyle("width", "80px");

        td = new Element("td").inject(this.tr);

        var idx1 = this.json.contextList.indexOf(this.appName);
        this.planAppArr = this.json.planList.filter(function(item, index){
            return (item.name==this.appName);
        }.bind(this));

        if ((idx1!=-1) && (this.planAppArr.length)){
            new Element("div", {"styles": this.css.applicationServerDocumentApplicationDeployedNode}).inject(td);
            this.checkbox.set("checked", true);
            this.status = "depolyed";
        }else if ((idx1==-1) && (this.planAppArr.length)){
            new Element("div", {"styles": this.css.applicationServerDocumentApplicationReadyDeployNode}).inject(td);
            this.checkbox.set("checked", true);
            this.status = "readyDepoly";
        }else if ((idx1!=-1) && (!this.planAppArr.length)){
            new Element("div", {"styles": this.css.applicationServerDocumentApplicationReadyDeleteNode}).inject(td);
            this.status = "readyDelete";
        }else{
            new Element("div", {"styles": this.css.applicationServerDocumentApplicationNotDepolyNode}).inject(td);
            this.status = "";
        }


        if (this.checkbox.get("checked")){
            this.createWeightInput();
        }

        td = new Element("td").inject(this.tr);
        this.checkbox.inject(td);

        var _self = this;
        this.checkbox.addEvent("click", function(){
            var td = this.getParent().getPrevious("td");
            var div = td.getElement("div");

            var name = this.get("name");
            var tmpArr = _self.json.planList.filter(function(item, index){
                return (item.name==name);
            });

            if (this.checked){
                _self.createWeightInput();
                if (!tmpArr.length) _self.json.planList.push({"name": name, "weight": 100});
                if (_self.status == "depolyed"){
                    div.setStyle("background", "url(/x_component_Setting/$Main/default/icon/deployed.png) no-repeat center center");
                }else{
                    div.setStyle("background", "url(/x_component_Setting/$Main/default/icon/readyDeploy.png) no-repeat center center");
                }
            }else{
                _self.removeWeightInput();
                if (tmpArr.length) _self.json.planList.erase(tmpArr[0]);
                if (_self.status == "depolyed"){
                    div.setStyle("background", "url(/x_component_Setting/$Main/default/icon/readyDelete.png) no-repeat center center");
                }else{
                    div.setStyle("background", "");
                }
            }
        });
    },
    createWeightInput: function(){
        if (!this.weightInput){
            this.weightInput = new Element("input", {
                "styles": {
                    "border": "1px solid #BBB",
                    "width": "80px"
                },
                "type": "text",
                "value": (this.planAppArr.length) ? this.planAppArr[0].weight : ""
            }).inject(this.weightTd);
        }
    },
    removeWeightInput: function(){
        if (this.weightInput){
            this.weightInput.destroy();
            this.weightInput = null;
        }
    },

    destroy: function(){
        this.tr.destroy();
        MWF.release(this);
    }
});








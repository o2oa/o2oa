MWF.xApplication.Setting.applications = MWF.xApplication.Setting.applications || {};
MWF.require("MWF.widget.Mask", null, false);
MWF.xApplication.Setting.applications.Storages = new Class({
    Implements: [Options, Events],

    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.items = [];
        this.content = this.explorer.storagesContent;
        this.storageJson = null;
        this.mappingJson = null;
        this.page = this.app.applicationPage;
        this.load();
    },
    load: function(){
        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.explorer.contentAreaNode);

        this.actions.listStorageMappings(function(mappingJson){
            this.mappingJson = mappingJson.data;
            this.loadStorages();
        }.bind(this));
        this.actions.listStorages(function(json){
            this.storageJson = json.data;
            this.loadStorages();
        }.bind(this));
    },
    loadStorages: function(){
        if (this.storageJson && this.mappingJson){
            Object.each(this.storageJson, function(value, key){
                this.items.push(new MWF.xApplication.Setting.applications.Storage(this, value, key));
            }.bind(this));

            if (this.mask) this.mask.hide();
        }
    },
    destroy: function(){
        this.items.each(function(item){
            item.destroy();
        }.bind(this));
        if (this.currentDoc) this.currentDoc.destroy();
        this.content.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Setting.applications.Storage = new Class({
    Implements: [Events],
    initialize: function(list, json, key){
        this.list = list
        this.explorer = this.list.explorer;
        this.app = this.list.explorer.app;
        this.json = json;
        this.mappingJson = this.list.mappingJson[key];
        this.key = key;
        this.container = this.list.content;
        this.css = this.app.css;
        this.servers = [];
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.applicationNode}).inject(this.container);
        this.nameNode = new Element("div", {"styles": this.css.applicationNameNode}).inject(this.node);
        this.iconNode = new Element("div", {"styles": this.css.storageNameIconNode}).inject(this.nameNode);
        this.addNode = new Element("div", {"styles": this.css.storageNameAddNode, "text": "add"}).inject(this.nameNode);
        this.textNode = new Element("div", {"styles": this.css.applicationNameTextNode}).inject(this.nameNode);
        this.textNode.set("text", this.key);

        this.serverListNode = new Element("div", {"styles": this.css.applicationServerListNode}).inject(this.node);

        this.json.each(function(server, i){
            var mappingServer = this.mappingJson[i];
            this.servers.push(new MWF.xApplication.Setting.applications.Storage.Server(this, server, mappingServer));
        }.bind(this));

        this.addNode.addEvent("click", function(){
            var server = {
                "storage": this,
                "list": this.list,
                "app": this.app,
                "explorer": this.explorer,
                "json": {
                    "weight": "",
                    "storageServer": "",
                    "order": "",
                    "enable": true
                },
                "mappingJson": {
                    "storageServiceType": "ftp",
                    "enable": true,
                    "weight": "",
                    "name": "",
                    "ftpHost": "",
                    "ftpPort": "",
                    "ftpUsername": "",
                    "ftpPassword": "",
                    "ftpPath": ""
                },
                "node": this.addNode,
                "container": this.serverListNode,
                "name": "",
                "reload": function(){
                    this.app.actions.getStorageServer(this.json.storageServer, function(json){
                        debugger;
                        this.mappingJson.storageServiceType = json.data.storageServiceType;
                        this.mappingJson.enable = this.json.enable;
                        this.mappingJson.weight = this.json.weight;
                        this.mappingJson.name = this.json.storageServer;
                        this.mappingJson.ftpHost = json.data.host;
                        this.mappingJson.ftpPort = json.data.port;
                        this.mappingJson.ftpUsername = json.data.username;
                        this.mappingJson.ftpPassword = json.data.password;
                        this.mappingJson.ftpPath = json.data.path;
                        this.storage.servers.push(new MWF.xApplication.Setting.applications.Storage.Server(this.storage, this.json, this.mappingJson));
                    }.bind(this));
                }
            }
            this.currentDoc = new MWF.xApplication.Setting.applications.Storage.Server.Document(server);
        }.bind(this));

    },
    destroy: function(){
        this.servers.each(function(server){
            server.destroy();
        }.bind(this));
        this.node.destroy();
        MWF.release(this);
    }
});

MWF.xApplication.Setting.applications.Storage.Server = new Class({
    initialize: function(storage, json, mappingJson){
        this.storage = storage;
        this.list = this.storage.list
        this.explorer = this.storage.explorer;
        this.app = this.storage.explorer.app;
        this.json = json;
        this.mappingJson = mappingJson;
        this.container = this.storage.serverListNode;
        this.css = this.app.css;
        this.name = this.json.storageServer;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.applicationInServerNode}).inject(this.container);

        this.tableNode = new Element("div", {"styles": this.css.applicationInServerTableNode}).inject(this.node);
        var html = "<table width='100%' cellSpacing='0' cellPadding='0'><tr>" +
            "<td  style='width: 40px'></td>" +
            "<td  style='padding: 0px 5px'>"+this.json.storageServer+"</td>" +
            "<td  style='padding: 0px 5px'>"+this.json.enable+"</td>" +
            "<td  style='padding: 0px 5px'>"+this.mappingJson.storageServiceType+"</td>" +
            "<td  style='padding: 0px 5px'>"+this.mappingJson.ftpPort+"</td>"+
            "<td  style='padding: 0px 5px'>"+this.mappingJson.ftpUsername+"</td>"+
            "<td  style='padding: 0px 5px'>"+this.mappingJson.ftpPassword+"</td>"+
            "<td width='80px' style='padding: 0px 5px'>"+this.json.weight+"</td>"+
            "<td width='40px'></td>" +
            "</tr></table>";
        this.tableNode.set("html", html);
        tds = this.tableNode.getElements("td");
        tds[0].setStyles(this.css.applicationInServerIconNode);

        this.editTd = tds[tds.length-1];
        this.editTd.setStyles(this.css.applicationInServerEditNode);

        this.jsonNode = new Element("div", {"styles": this.css.applicationInServerJsonNode}).inject(this.node);
        this.dataJsonNode = new Element("div", {"styles": this.css.dataInServerDataJsonNode}).inject(this.jsonNode);
        this.mappingJsonNode = new Element("div", {"styles": this.css.dataInServerMappingJsonNode}).inject(this.jsonNode);

        this.jsonButtonNode = new Element("div", {"styles": this.css.applicationInServerJsonButtonNode}).inject(this.node);
        var jsonStr = JSON.stringify(this.json, null, "\t");
        jsonHtml = jsonStr.replace(/\n|\r/g, "<br>");
        jsonHtml = jsonHtml.replace(/\t/g, "<font>&nbsp;&nbsp;&nbsp;&nbsp;</font>");
        this.dataJsonNode.set("html", "<div style='font-weight: bold'>Data</div>"+jsonHtml);
        var mappingJsonStr = JSON.stringify(this.mappingJson, null, "\t");
        mappingJsonHtml = mappingJsonStr.replace(/\n|\r/g, "<br>");
        mappingJsonHtml = mappingJsonHtml.replace(/\t/g, "<font>&nbsp;&nbsp;&nbsp;&nbsp;</font>");
        this.mappingJsonNode.set("html", "<div style='font-weight: bold'>DataMappings</div>"+mappingJsonHtml);

        this.editTd.addEvent("click", function(){
            this.open();
        }.bind(this));

        this.jsonButtonNode.addEvent("click", function(){
            if (this.jsonNode.getStyle("display")=="none"){
                this.jsonNode.setStyle("display", "block");
                this.jsonButtonNode.setStyle("background", "url(/x_component_Setting/$Main/default/icon/up.png) no-repeat center center");
            }else{
                this.jsonNode.setStyle("display", "none");
                this.jsonButtonNode.setStyle("background", "url(/x_component_Setting/$Main/default/icon/down.png) no-repeat center center");
            }
        }.bind(this));
    },
    open: function(){
        this.list.currentDoc = new MWF.xApplication.Setting.applications.Storage.Server.Document(this);
    },
    reload: function(){
        this.name = this.json.storageServer;
        this.node.destroy();
        this.load();
    },
    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    }
});

MWF.xApplication.Setting.applications.Storage.Server.Document = new Class({
    Implements: [Events],
    initialize: function(server){
        this.server = server;
        this.list = this.server.list
        this.app = this.server.app;
        this.json = this.server.json;
        this.mappingJson = this.server.mappingJson;
        this.container = this.list.explorer.container;
        this.css = this.app.css;
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
        //this.applicationAreaNode.setStyle("height", ""+h+"px");
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
    },
    createBaseInfo: function(){

        this.inforAreaNode = new Element("div", {"styles": this.css.applicationServerDocumentInforAreaNode}).inject(this.node);
        this.inforNode = new Element("div", {"styles": this.css.dataServerDocumentInforNode}).inject(this.inforAreaNode);
        var html = "<table cellSpacing='8px' width='90%' align='center'>" +
            "<tr><td width='160px'>storageType</td><td>"+this.server.storage.key+"</td></tr>" +
            "<tr><td>enable</td><td><select>" +
            "</select></td></tr>" +
            "<tr><td>weight</td><td><input value='"+(this.json.weight || "")+"'/></td></tr>" +
            "<tr><td>order</td><td><input value='"+(this.json.order || "")+"'/></td></tr>" +
            "<tr><td>enable</td><td><select>" +
            "<option value='true' "+((this.json.enable) ? "selected" : "")+">true</option>" +
            "<option value='false' "+((!this.json.enable) ? "selected" : "")+">false</option>" +
            "</select></td></tr>" +
            "</table>";

        this.inforNode.set("html", html);
        var tds = this.inforNode.getElements("td");
        var inputs = this.inforNode.getElements("input");

        tds.setStyles(this.css.applicationServerDocumentTdNode);
        inputs.setStyles(this.css.applicationServerDocumentInputNode);

        var select = this.inforNode.getElement("select");
        this.app.actions.listStorageServer(function(json){
            json.data.each(function(server){
                var option = new Element("option", {"value": server.name, "text": server.name}).inject(select);
                if (server.name==this.json.storageServer) option.set("selected", true);
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
        this.morph.start(css).chain(function(){
            this.destroy();
        }.bind(this));
    },
    destroy: function(){
        this.app.removeEvent("resize", this.setDocumentSizeFun);
        this.node.destroy();
        this.list.currentDoc = null;
        MWF.release(this);
    },
    saveDocument: function(){
        var inputs = this.inforNode.getElements("input");
        this.json.weight = inputs[0].get("value");
        this.json.order = inputs[1].get("value");

        var selects = this.inforNode.getElements("select");
        this.json.storageServer = selects[0].options[selects[0].selectedIndex].value;

        var str = selects[1].options[selects[1].selectedIndex].value;
        this.json.enable = (str=="true") ? true : false;

        if (this.server.name){
            this.app.actions.updateStorage(this.server.storage.key, this.server.name, this.json, function(){
                this.closeDocument();
                this.server.reload();
            }.bind(this));
        }else{
            this.app.actions.addStorage(this.server.storage.key, this.json, function(){
                this.closeDocument();
                this.server.reload();
            }.bind(this));
        }

    },
    deleteDocument: function(e){
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.deleteStorage_title, this.app.lp.deleteStorage, "350", "120", function(){
            _self.app.actions.removeStorage(_self.server.storage.key, _self.server.name, function(){
                this.closeDocument();
                this.server.destroy();
            }.bind(_self));
            this.close();
        }, function(){
            this.close();
        });
    }

});
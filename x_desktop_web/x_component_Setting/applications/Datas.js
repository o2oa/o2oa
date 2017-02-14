MWF.xApplication.Setting.applications = MWF.xApplication.Setting.applications || {};
//MWF.xDesktop.requireApp("Setting", "applications.Applications", null, false);
MWF.require("MWF.widget.Mask", null, false);
MWF.xApplication.Setting.applications.Datas = new Class({
    //Extends: MWF.xApplication.Setting.applications.Applications,
    Implements: [Options, Events],

    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.items = [];
        this.content = this.explorer.datasContent;
        this.dataJson = null;
        this.mappingJson = null;
        this.load();
    },
    load: function(){
        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.explorer.contentAreaNode);

        this.actions.listDataMappings(function(mappingJson){
            this.mappingJson = mappingJson.data;
            this.loadDatas();
        }.bind(this));
        this.actions.listDatas(function(json){
            this.dataJson = json.data;
            this.loadDatas();
        }.bind(this));
    },
    loadDatas: function(){
        if (this.dataJson && this.mappingJson){
            Object.each(this.dataJson, function(value, key){
                this.items.push(new MWF.xApplication.Setting.applications.Data(this, value, key));
            }.bind(this));

            if (this.mask) this.mask.hide();
        }
    },
    destroy: function(){
        this.items.each(function(item){
            item.destroy();
        }.bind(this));
        this.content.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Setting.applications.Data = new Class({
    Implements: [Events],
    initialize: function(list, json, key){
        this.list = list
        this.explorer = this.list.explorer;
        this.app = this.list.explorer.app;
        this.dataJson = json;
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
        this.iconNode = new Element("div", {"styles": this.css.dataNameIconNode}).inject(this.nameNode);
        this.textNode = new Element("div", {"styles": this.css.applicationNameTextNode}).inject(this.nameNode);
        this.textNode.set("text", this.key);

        this.serverListNode = new Element("div", {"styles": this.css.applicationServerListNode}).inject(this.node);

        this.dataJson.each(function(server, i){
            var mappingServer = this.mappingJson[i];
            this.servers.push(new MWF.xApplication.Setting.applications.Data.Server(this, server, mappingServer));
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

MWF.xApplication.Setting.applications.Data.Server = new Class({
    initialize: function(data, json, mappingJson){
        this.data = data;
        this.list = this.data.list
        this.explorer = this.data.explorer;
        this.app = this.data.explorer.app;
        this.json = json;
        this.mappingJson = mappingJson;
        this.container = this.data.serverListNode;
        this.css = this.app.css;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.applicationInServerNode}).inject(this.container);

        this.tableNode = new Element("div", {"styles": this.css.applicationInServerTableNode}).inject(this.node);
        var html = "<table width='100%' cellSpacing='0' cellPadding='0'><tr>" +
            "<td width='40px'></td>" +
            "<td style='padding: 0px 5px'>"+this.json.dataServer+"</td>" +
            "<td style='padding: 0px 5px'>"+this.mappingJson.url+"</td>" +
            "<td style='padding: 0px 5px'>"+this.mappingJson.username+"</td>" +
            "<td style='padding: 0px 5px'>"+this.mappingJson.password+"</td>"+
            "</tr></table>";
        this.tableNode.set("html", html);
        tds = this.tableNode.getElements("td");
        tds[0].setStyles(this.css.applicationInServerIconNode);

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

    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    }
});
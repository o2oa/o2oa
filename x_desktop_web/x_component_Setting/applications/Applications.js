MWF.xApplication.Setting.applications = MWF.xApplication.Setting.applications || {};
MWF.require("MWF.widget.Mask", null, false);
MWF.xApplication.Setting.applications.Applications = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.applications = [];
        this.content = this.explorer.applicationsContent;
        this.load();
    },
    load: function(){
        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.explorer.contentAreaNode);
        this.actions.listApplications(function(json){
            Object.each(json.data, function(value, key){
                this.applications.push(new MWF.xApplication.Setting.applications.Application(this, value, key));
            }.bind(this));

            if (this.mask) this.mask.hide();
        }.bind(this));
    },
    destroy: function(){
        this.applications.each(function(application){
            application.destroy();
        }.bind(this));
        this.content.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Setting.applications.Application = new Class({
    Implements: [Events],
    initialize: function(list, json, key){
        this.list = list
        this.explorer = this.list.explorer;
        this.app = this.list.explorer.app;
        this.json = json;
        this.key = key;
        this.container = this.list.content;
        this.css = this.app.css;
        this.servers = [];
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.applicationNode}).inject(this.container);
        this.nameNode = new Element("div", {"styles": this.css.applicationNameNode}).inject(this.node);
        this.iconNode = new Element("div", {"styles": this.css.applicationNameIconNode}).inject(this.nameNode);
        this.textNode = new Element("div", {"styles": this.css.applicationNameTextNode}).inject(this.nameNode);
        //this.textContextNode = new Element("div", {"styles": this.css.applicationNameTextContextNode}).inject(this.nameNode);
        //this.textPackageNode = new Element("div", {"styles": this.css.applicationNameTextPackageNode}).inject(this.nameNode);
        //thisAppName = this.json.context.substr(1, this.json.context.lenght);
        //this.textContextNode.set("text", this.key);
        //this.textPackageNode.set("text", this.key);
        this.textNode.set("text", this.key);

        this.serverListNode = new Element("div", {"styles": this.css.applicationServerListNode}).inject(this.node);

        this.weight = 0;
        var count = 0;
        this.json.each(function(server, i){
            if (server.weight){
                this.weight += server.weight.toFloat();
                count++;
            }
        }.bind(this));
        var v = 100;
        if (count>0){
            v = this.weight/count;
        }
        if (count<this.json.length){
            this.json.each(function(server, i){
                if (!server.weight) this.weight += v;
            }.bind(this));
        }

        this.json.each(function(server, i){
            if (!server.weight) server.weight = v;
            this.servers.push(new MWF.xApplication.Setting.applications.Application.Server(this, server));
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

MWF.xApplication.Setting.applications.Application.Server = new Class({
    initialize: function(application, json){
        this.application = application;
        this.list = this.application.list
        this.explorer = this.application.explorer;
        this.app = this.application.explorer.app;
        this.json = json;
        this.container = this.application.serverListNode;
        this.css = this.app.css;
        this.load();
    },
    load: function(){
        //var width = (this.json.weight/this.application.weight)*100;
        this.node = new Element("div", {"styles": this.css.applicationInServerNode}).inject(this.container);
        //this.node.setStyles({
        //    "background-color": this.color,
        ////    "width": width+"%"
        //});
        this.tableNode = new Element("div", {"styles": this.css.applicationInServerTableNode}).inject(this.node);
        var html = "<table width='100%' cellSpacing='0' cellPadding='0'><tr>" +
            "<td width='40px'></td>" +
            "<td style='padding: 0px 5px'>"+this.json.applicationServer+"</td>" +
            "<td style='padding: 0px 5px'>"+this.json.host+"</td>" +
            "<td style='padding: 0px 5px'>"+this.json.port+"</td>" +
            "<td style='padding: 0px 5px'>"+this.json.context+"</td>" +
            "<td width='80px' style='padding: 0px 5px'>"+this.json.weight+"</td>" +
            "<td width='40px'></td>" +
            "</tr></table>";
        this.tableNode.set("html", html);
        tds = this.tableNode.getElements("td");
        tds[0].setStyles(this.css.applicationInServerIconNode);
        this.editTd = tds[tds.length-1];
        this.editTd.setStyles(this.css.applicationInServerEditNode);

        this.jsonNode = new Element("div", {"styles": this.css.applicationInServerJsonNode}).inject(this.node);
        this.jsonButtonNode = new Element("div", {"styles": this.css.applicationInServerJsonButtonNode}).inject(this.node);
        var jsonStr = JSON.stringify(this.json, null, "\t");
        jsonHtml = jsonStr.replace(/\n|\r/g, "<br>");
        jsonHtml = jsonHtml.replace(/\t/g, "<font>&nbsp;&nbsp;&nbsp;&nbsp;</font>");
        this.jsonNode.set("html", jsonHtml);

        this.editTd.addEvent("click", function(){
            if (this.status=="edit"){
                this.saveWeight();
            }else{
                this.editWeight();
            }
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
    editWeight: function(){
        tds = this.tableNode.getElements("td");
        this.editTd.setStyles(this.css.applicationInServerSaveNode);
        weightTd = tds[tds.length-2];
        weightTd.empty();
        this.weightInput = new Element("input", {
            "styles": this.css.applicationInServerWeightInputNode,
            "type": "text"
        }).inject(weightTd);
        this.weightInput.set("value", this.json.weight);
        this.status="edit";
    },
    saveWeight: function(){
        var weight = this.weightInput.get("value");
        weight = weight.toFloat();
        if (isNaN(weight)) {
            this.app.notice(this.app.lp.saveWeightError, "error");
            return false;
        }
        if (weight != this.json.weight){
            this.json.weight = weight;
            this.app.actions.getAppServer(this.json.applicationServer, function(json){
                serverJson = json.data;
                thisAppName = this.json.context.substr(1, this.json.context.lenght);
                serverJson.planList.each(function(app){
                    if (app.name==thisAppName){
                        app.weight = weight;
                    }
                }.bind(this));

                this.app.actions.updateAppServer(serverJson.name, serverJson, function(){
                    this.saveWeightCompleted();
                }.bind(this));
            }.bind(this));
        }else{
            this.saveWeightCompleted();
        }
    },
    saveWeightCompleted: function(){
        tds = this.tableNode.getElements("td");
        this.editTd.setStyles(this.css.applicationInServerEditNode);
        weightTd = tds[tds.length-2];
        weightTd.empty();
        weightTd.set("text", this.json.weight);
        this.status="read";
    },

    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    }
});
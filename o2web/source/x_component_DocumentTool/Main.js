MWF.xApplication.DocumentTool = MWF.xApplication.DocumentTool || {};
o2.xDesktop.requireApp("Selector", "package", null, false);
//MWF.require("MWF.widget.O2Identity", null, false);

MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.DocumentTool.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.DocumentTool.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "DocumentTool",
        "icon": "icon.png",
        "width": "1220",
        "height": "700",
        "isResize": true,
        "isMax": true,
        "viewPageNum": 1,
        "title": MWF.xApplication.DocumentTool.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.DocumentTool.LP;
        this.action = o2.Actions.load("x_cms_assemble_control");
        this.action.CategoryInfoAction.listAllCategoryInfo(function(json){
            this.appList = json.data;
        }.bind(this),null,false);
    },
    getAppName : function (appId){
        var appName = "";
        this.appList.each(function(app){
            if(app.appId === appId) appName = app.appName;
        }.bind(this));

        return appName;
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.distinguishedName;

        this.path = "../x_component_DocumentTool/$Main/" + this.options.style + "/";
        this.stylePath = "../x_component_DocumentTool/$Main/" + this.options.style + "/style.css";
        this.createNode();


        this.content.loadCss(this.stylePath, this.loadApplicationContent());


    },
    loadController: function (callback) {

        if (callback) callback();
    },
    createNode: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": this.css.node
        }).inject(this.content);
    },
    loadApplicationContent: function () {
        this.loadController(function () {
            this.loadApplicationLayout();
        }.bind(this))
    },
    loadApplicationLayout: function () {
        this.naviNode = new Element("div.naviNode", {
            "styles": this.css.naviNode
        }).inject(this.node);
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);
        this.loaNavi();
    },
    loaNavi: function (callback) {
        var naviOpt = {};
        if (this.status) {
            naviOpt.module = this.status.module || "info";
            naviOpt.operation = this.status.operation;
        } else {
            naviOpt.module = this.options.module || "info";
            naviOpt.operation = this.options.operation;
        }
        this.navi = new MWF.xApplication.DocumentTool.Navi(this, this.naviNode, naviOpt);
    },
    openInfo : function(){
        MWF.xDesktop.requireApp("DocumentTool", "Info", function(){
            var task = new MWF.xApplication.DocumentTool.Info(this.contentContainerNode,this,{
                "className" : "Info"
            });
            task.load();
        }.bind(this));
    },
    getOffsetY: function (node) {
        return (node.getStyle("margin-top").toInt() || 0) +
            (node.getStyle("margin-bottom").toInt() || 0) +
            (node.getStyle("padding-top").toInt() || 0) +
            (node.getStyle("padding-bottom").toInt() || 0) +
            (node.getStyle("border-top-width").toInt() || 0) +
            (node.getStyle("border-bottom-width").toInt() || 0);
    },
    recordStatus: function () {
        return this.navi.currentStatus || {};
    }
});

MWF.xApplication.DocumentTool.Navi = new Class({
    Implements: [Options, Events],
    options: {
        "module": "task"
    },
    initialize: function (app, node, options) {
        this.setOptions(options);
        this.app = app;
        this.node = $(node);
        this.css = this.app.css;
        this.currentMenu = null;
        this.currentItem = null;
        this.load();
    },
    load: function () {
        this.scrollNode = new Element("div.naviScrollNode", {"styles": this.css.naviScrollNode}).inject(this.node);
        this.areaNode = new Element("div.naviAreaNode", {"styles": this.css.naviAreaNode}).inject(this.scrollNode);

        this.naviTopNode = new Element("div.naviTopNode", {
            "styles": this.css.naviTopNode,
            "text": this.app.lp.title
        }).inject(this.areaNode);

        var naviUrl = this.app.path + "navi.json";

        o2.getJSON(naviUrl, function (json) {

            json.each(function (navi) {
                this.createMenuNode(navi);
            }.bind(this));

            if (this.options.id == "") this.elements[1].click();

            this.setContentSize();

            this.app.addEvent("resize", this.setContentSize.bind(this));
        }.bind(this));

    },
    createMenuNode: function (navi) {
        var _self = this;
        var menuNode = new Element("div", {
            "styles": this.css.naviMenuNode
        });

        var iconNode = new Element("div", {
            "styles": this.css.naviMenuIconNode_collapse
        }).inject(menuNode);
        iconNode.setStyle("background", "url(../x_component_DocumentTool/$Main/default/icon/"+navi.module+".png) no-repeat 5px center")

        var textNode = new Element("div", {
            "styles": this.css.naviMenuTextNode,
            "text": navi.title
        }).inject(menuNode);
        menuNode.inject(this.areaNode);

        navi.node = menuNode;
        menuNode.addEvents({
            "mouseover": function () {
                if (_self.currentMenu != this) this.setStyles(_self.app.css.naviMenuNode_over);
            },
            "mouseout": function () {
                if (_self.currentMenu != this) this.setStyles(_self.app.css.naviMenuNode_normal);
            },
            "click": function (ev) {
                _self.setCurrentMenu(navi);
                ev.stopPropagation();
            }
        });
        if (this.options.module === navi.module) {
            menuNode.click()
        }
    },
    cancelCurrent: function () {
        if (this.currentMenu) {
            this.currentMenu.setStyles(this.css.naviMenuNode);
            this.currentMenu.setStyles(this.css.naviMenuNode_normal);
            this.currentMenu = false;
        }
        if (this.currentItem) {
            this.currentItem.setStyles(this.css.naviItemNode);
            this.currentItem = false;
        }
        if (this.currentAll) {
            this.currentAll.setStyles(this.css.naviAllNode_normal);
            this.currentAll = false;
        }
        this.currentStatus = null;
    },
    setCurrentMenu: function (navi) {
        this.cancelCurrent();
        this.currentStatus = {
            module: navi.module
        };
        this.currentMenu = navi.node;
        navi.node.setStyles(this.css.naviMenuNode_current);
        this.app[navi.action].call(this.app, navi);
    },
    setContentSize: function () {
        var nodeSize = this.app.node.getSize();
        var h = nodeSize.y - this.app.getOffsetY(this.app.node);
        this.node.setStyle("height", h);
        this.scrollNode.setStyle("height", h);
    }
});

MWF.xApplication.OnlyOffice = MWF.xApplication.OnlyOffice || {};
o2.xDesktop.requireApp("Selector", "package", null, false);
//MWF.require("MWF.widget.O2Identity", null, false);

MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.OnlyOffice.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.OnlyOffice.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "OnlyOffice",
        "icon": "icon.png",
        "width": "1220",
        "height": "700",
        "isResize": true,
        "isMax": true,
        "viewPageNum": 1,
        "title": MWF.xApplication.OnlyOffice.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.OnlyOffice.LP;
        this.action = o2.Actions.load("x_onlyofficefile_assemble_control");
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.distinguishedName;

        this.path = "../x_component_OnlyOffice/$Main/" + this.options.style + "/";
        this.stylePath = "../x_component_OnlyOffice/$Main/" + this.options.style + "/style.css";
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
            naviOpt.module = this.status.module || "document";
            naviOpt.operation = this.status.operation;
        } else {
            naviOpt.module = this.options.module || "document";
            naviOpt.operation = this.options.operation;
        }
        this.navi = new MWF.xApplication.OnlyOffice.Navi(this, this.naviNode, naviOpt);
    },
    openDocument : function(){
        MWF.xDesktop.requireApp("OnlyOffice", "DocumentList", function(){
            new MWF.xApplication.OnlyOffice.DocumentList(this.contentContainerNode,this,{
                "className" : "DocumentList"
            }).load();
        }.bind(this));
    },
    openTemplate : function(){
        MWF.xDesktop.requireApp("OnlyOffice", "TemplateList", function(){
            new MWF.xApplication.OnlyOffice.TemplateList(this.contentContainerNode,this,{
                "className" : "TemplateList"
            }).load();
        }.bind(this));
    },
    openSetting : function (){
        MWF.xDesktop.requireApp("OnlyOffice", "Setting", function(){
            this.action.OnlyofficeConfigAction.getConfig(function (json){
                var form = new MWF.xApplication.OnlyOffice.Setting({app: this}, json.data );
                form.edit();
            }.bind(this));
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

MWF.xApplication.OnlyOffice.Navi = new Class({
    Implements: [Options, Events],
    options: {
        "module": "document"
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
        iconNode.setStyle("background", "url(../x_component_OnlyOffice/$Main/default/icon/"+navi.module+".png) no-repeat 5px center")

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
        debugger
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

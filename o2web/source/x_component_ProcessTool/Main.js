MWF.xApplication.ProcessTool = MWF.xApplication.ProcessTool || {};
o2.xDesktop.requireApp("Selector", "package", null, false);
//MWF.require("MWF.widget.O2Identity", null, false);

MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.ProcessTool.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.ProcessTool.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "ProcessTool",
        "icon": "icon.png",
        "width": "1220",
        "height": "700",
        "isResize": true,
        "isMax": true,
        "viewPageNum": 1,
        "title": MWF.xApplication.ProcessTool.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.ProcessTool.LP;
        this.action = o2.Actions.load("x_processplatform_assemble_surface");
        this.orgAction = o2.Actions.load("x_organization_assemble_personal");
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.distinguishedName;

        this.path = "../x_component_ProcessTool/$Main/" + this.options.style + "/";
        this.stylePath = "../x_component_ProcessTool/$Main/" + this.options.style + "/style.css";
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
            naviOpt.module = this.status.module || "task";
            naviOpt.operation = this.status.operation;
        } else {
            naviOpt.module = this.options.module || "task";
            naviOpt.operation = this.options.operation;
        }
        this.navi = new MWF.xApplication.ProcessTool.Navi(this, this.naviNode, naviOpt);
    },
    openTask : function(){
        MWF.xDesktop.requireApp("ProcessTool", "Task", function(){
            var task = new MWF.xApplication.ProcessTool.Task(this.contentContainerNode,this,{
                "className" : "Task"
            });
            task.load();
        }.bind(this));
    },
    openTaskDone : function(){
        MWF.xDesktop.requireApp("ProcessTool", "TaskDone", function(){
            var taskDone = new MWF.xApplication.ProcessTool.TaskDone(this.contentContainerNode,this,{
                "className" : "TaskDone"
            });
            taskDone.load();
        }.bind(this));
    },
    openRead : function(){
        MWF.xDesktop.requireApp("ProcessTool", "Read", function(){
            var read = new MWF.xApplication.ProcessTool.Read(this.contentContainerNode,this,{
                "className" : "Read"
            });
            read.load();
        }.bind(this));
    },
    openReadDone : function(){
        MWF.xDesktop.requireApp("ProcessTool", "ReadDone", function(){
            var readDone = new MWF.xApplication.ProcessTool.ReadDone(this.contentContainerNode,this,{
                "className" : "ReadDone"
            });
            readDone.load();
        }.bind(this));
    },
    openWork : function (){
        MWF.xDesktop.requireApp("ProcessTool", "Work", function(){
            var work = new MWF.xApplication.ProcessTool.Work(this.contentContainerNode,this,{
                "className" : "Work"
            });
            work.load();
        }.bind(this));
    },
    openWorkCompleted : function (){
        MWF.xDesktop.requireApp("ProcessTool", "WorkCompleted", function(){
            var workCompleted = new MWF.xApplication.ProcessTool.WorkCompleted(this.contentContainerNode,this,{
                "className" : "WorkCompleted"
            });
            workCompleted.load();
        }.bind(this));
    },
    openSnap : function (){
        MWF.xDesktop.requireApp("ProcessTool", "Snap", function(){
            var snap = new MWF.xApplication.ProcessTool.Snap(this.contentContainerNode,this,{
                "className" : "Snap"
            });
            snap.load();
        }.bind(this));
    },
    openEmpowerLog : function (){
        MWF.xDesktop.requireApp("ProcessTool", "EmpowerLog", function(){
            var snap = new MWF.xApplication.ProcessTool.EmpowerLog(this.contentContainerNode,this,{
                "className" : "EmpowerLog"
            });
            snap.load();
        }.bind(this));
    },
    openEmpower : function (){
        MWF.xDesktop.requireApp("ProcessTool", "Empower", function(){
            var empower = new MWF.xApplication.ProcessTool.Empower(this.contentContainerNode,this,{
                "className" : "Empower"
            });
            empower.load();
        }.bind(this));
    },
    openHandover : function (){
        MWF.xDesktop.requireApp("ProcessTool", "Handover", function(){
            var handover = new MWF.xApplication.ProcessTool.Handover(this.contentContainerNode,this,{
                "className" : "Handover"
            });
            handover.load();
        }.bind(this));
    },
    openInfo : function(){
        MWF.xDesktop.requireApp("ProcessTool", "Info", function(){
            var info = new MWF.xApplication.ProcessTool.Info(this.contentContainerNode,this,{
                "className" : "Info"
            });
            info.load();
        }.bind(this));
    },
    openTable : function(){
        MWF.xDesktop.requireApp("ProcessTool", "Table", function(){
            var table = new MWF.xApplication.ProcessTool.Table(this.contentContainerNode,this,{
                "className" : "Table"
            });
            table.load();
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

MWF.xApplication.ProcessTool.Navi = new Class({
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
        iconNode.setStyle("background", "url(../x_component_ProcessTool/$Main/default/icon/"+navi.module+".png) no-repeat 5px center")

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

MWF.xApplication.BAM.summary = MWF.xApplication.BAM.summary || {};
MWF.xApplication.BAM.summary.Overview = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(summary, node, data, options){
        this.setOptions(options);

        this.path = "/x_component_BAM/summary/$Overview/";
        this.cssPath = "/x_component_BAM/summary/$Overview/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.summary = summary;
        this.app = this.summary.app;
        this.container = $(node);
        this.data = data;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        var html = "<table border='0' cellpadding='0' cellSpacing='0' align='center'><tr>" +
            "<td></td><td></td><td></td><td></td><tr></tr><td></td><td></td><td></td><td></td>" +
            "</tr></table>"

        this.node.set("html", html);
        this.table = this.node.getElement("table");
        this.cells = this.node.getElements("td");

        this.table.setStyles(this.css.table);
        this.cells.setStyles(this.css.cells);

        for (var i=0; i<4; i++){
            this.cells[i].setStyle("border-top", "0px");
        }
        for (var i=4; i<this.cells.length; i++){
            this.cells[i].setStyle("border-bottom", "0px");
        }
        this.cells[0].setStyle("border-left", "0px");
        this.cells[4].setStyle("border-left", "0px");
        this.cells[3].setStyle("border-right", "0px");
        this.cells[7].setStyle("border-right", "0px");

        this.loadData();
    },
    loadData: function(){
        this.loadItem(0, "application.png", this.data.applicationCount, this.app.lp.applicationCount);
        this.loadItem(1, "process.png", this.data.processCount, this.app.lp.processCount);
        this.loadItem(2, "task.png", this.data.taskCount, this.app.lp.taskCount);
        this.loadItem(3, "taskCompleted.png", this.data.taskCompletedCount, this.app.lp.taskCompletedCount);
        this.loadItem(4, "work.png", this.data.workCount, this.app.lp.workCount);
        this.loadItem(5, "workCompleted.png", this.data.workCompletedCount, this.app.lp.workCompletedCount);
        this.loadItem(6, "read.png", this.data.readCount, this.app.lp.readCount);
        this.loadItem(7, "readCompleted.png", this.data.readCompletedCount, this.app.lp.readCompletedCount);
    },

    loadItem: function(idx, icon, number, title){
        var itemNode = new Element("div", {"styles": this.css.itemNode}).inject(this.cells[idx]);
        var itemIconNode = new Element("div", {"styles": this.css.itemIconNode}).inject(itemNode);
        var itemTextNode = new Element("div", {"styles": this.css.itemTextNode}).inject(itemNode);
        var itemNumberNode = new Element("div", {"styles": this.css.itemNumberNode}).inject(itemTextNode);
        var itemTitleNode = new Element("div", {"styles": this.css.itemTitleNode}).inject(itemTextNode);

        itemIconNode.setStyle("background-image", "url(/x_component_BAM/summary/$Overview/"+this.options.style+"/icon/"+icon+")");
        itemNumberNode.set("text", number || 0);
        itemTitleNode.set("text", title);
    },
    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    }
});
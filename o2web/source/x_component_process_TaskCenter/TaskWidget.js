MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.TaskCenter = MWF.xApplication.process.TaskCenter || {};
MWF.xApplication.process.TaskCenter.TaskWidget = new Class({
    Extends: MWF.xApplication.Common.Widget,
    Implements: [Options, Events],
	options: {
        "style": "default",
        "title": MWF.xApplication.process.TaskCenter.LP.title,
        "appName": "process.TaskCenter",
        "name": "TaskWidget",
        "position": {"right": 10, "bottom": 10},
        "width": "400",
        "height": "550"
	},
    init: function(){
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
        this.items = [];
        this.pageCount = 10;
    },
    loadContent: function(callback){

    //    this.content.set("text", "ok")

        this.init();
        this.addEvent("scroll", function(y){

            var scrollSize = this.widget.contentScrollNode.getScrollSize();
            var clientSize = this.widget.contentScrollNode.getSize();
            var scrollHeight = scrollSize.y-clientSize.y;
            if (y+60>scrollHeight) {
                if (!this.isElementLoaded) this.listItemNext();
            }
        }.bind(this));
        this.addEvent("dragComplete", function(el, e){
            var p = this.widget.node.getPosition(this.widget.node.getOffsetParent());
            this.options.position = {"top": p.y, "left": p.x};
        });

        this.listItemNext();

        if (callback) callback();
    },

    listItemNext: function(count){
        if (!this.isItemsLoaded){
            if (!this.isItemLoadding){
                this.isItemLoadding = true;
                this._getCurrentPageData(function(json){
                    this.count = json.count;
                    if (json.count<=this.items.length){
                        this.isItemsLoaded = true;
                    }
                    json.data.each(function(task){
                        this.items.push(new MWF.xApplication.process.TaskCenter.TaskWidget.Item(task, this));
                    }.bind(this));

                    this.isItemLoadding = false;

                    if (this.loadItemQueue>0){
                        this.loadItemQueue--;
                        this.listItemNext();
                    }

                }.bind(this), count);
            }else{
                this.loadItemQueue++;
            }
        }
    },
    _getCurrentPageData: function(callback, count){
        this.getAction(function(){
            var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
            this.action.listTaskNext(function(json){
                if (callback) callback(json);
            }, null, id, count || this.pageCount);
        }.bind(this));
    },


    getAction: function(callback){
        if (!this.action){
            this.action = MWF.Actions.get("x_processplatform_assemble_surface");
            if (callback) callback();
            // MWF.xDesktop.requireApp("process.TaskCenter", "Actions.RestActions", function(){
            //     this.action = new MWF.xApplication.process.TaskCenter.Actions.RestActions();
            //     if (callback) callback();
            // }.bind(this));
        }else{
            if (callback) callback();
        }
    }
});

MWF.xApplication.process.TaskCenter.TaskWidget.Item = new Class({
    initialize: function (data, list) {
        this.data = data;
        this.list = list;
        this.container = this.list.content;

        this.load();
    },
    load: function () {
        this.node = new Element("div", {"styles": this.list.css.itemNode}).inject(this.container);
        this.iconNode = new Element("div", {"styles": this.list.css.itemIconNode}).inject(this.node);
        this.actionAreaNode = new Element("div", {"styles": this.list.css.itemActionAreaNode}).inject(this.node);

        this.contentNode = new Element("div", {"styles": this.list.css.itemContentNode}).inject(this.node);
        this.inforTopNode = new Element("div", {"styles": this.list.css.itemInforTopNode}).inject(this.contentNode);
        this.inforTopActivityNode = new Element("div", {"styles": this.list.css.itemInforTopActivityNode}).inject(this.inforTopNode);
        this.inforTopTimeNode = new Element("div", {"styles": this.list.css.itemInforTopTimeNode}).inject(this.inforTopNode);

        this.titleNode = new Element("div", {"styles": this.list.css.itemTitleNode}).inject(this.contentNode);

        this.inforBottomNode = new Element("div", {"styles": this.list.css.itemInforBottomNode}).inject(this.contentNode);

        this.setContent();
        //this.setNewIcon();
        this.setEvent();
    },
    setContent: function(){
        var i = (Math.random()*10).toInt();
        if ((i % 2)==0){
            this.iconNode.setStyle("background-image", "url("+"../x_component_process_TaskCenter/$TaskWidget/default/read2.png)");
        }else{
            this.iconNode.setStyle("background-image", "url("+"../x_component_process_TaskCenter/$TaskWidget/default/task2.png)");
        }

        this.inforTopActivityNode.set("text", this.data.activityName);
        this.inforTopTimeNode.set("text", this.data.startTime);

        this.titleNode.set("text", this.data.title);
        this.inforBottomNode.set("text", this.data.applicationName+">>"+this.data.processName);
    },
    setEvent: function(){
        this.contentNode.addEvent("click", function(){
            this.openTask();
        }.bind(this));
    },
    openTask: function(e){
        var options = {"workId": this.data.work};
        this.list.desktop.openApplication(e, "process.Work", options, {"taskObject": this});
    },
    destroy: function(){
        this.node.destroy();
        delete this.node;
        delete this.iconNode;
        delete this.actionAreaNode;
        delete this.contentNode;
        delete this.inforTopNode;
        delete this.inforTopActivityNode;
        delete this.inforTopTimeNode;
        delete this.titleNode;
        delete this.inforBottomNode;
        delete this;
    }
});
//MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.Mask", null, false);
MWF.require("MWF.widget.Identity", null,false);
//MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xDesktop.requireApp("process.Application", "WorkExplorer", null, false);
MWF.xApplication.process.Application.WorkCompletedExplorer = new Class({
	Extends: MWF.xApplication.process.Application.WorkExplorer,
	Implements: [Options, Events],

    createSearchElementNode: function(){
        this.toCompletedNode = new Element("div", {
            "styles": this.css.toCompletedNode,
            "text": this.app.lp.toWork
        }).inject(this.toolbarNode);
        this.toCompletedNode.addEvents({
            "mouseover": function(){
                this.toCompletedNode.setStyles(this.css.toCompletedNode_over);
            }.bind(this),
            "mouseout": function(){
                this.toCompletedNode.setStyles(this.css.toCompletedNode);
            }.bind(this),
            "click": function(){
                this.app.workConfig();
            }.bind(this)
        });
    },

    _getFilterCount: function(callback){
        this.actions.listWorkCompletedFilterAttributeManage(this.app.options.id, function(json){
            if (callback) callback(json);
        });
    },

    loadProcess: function(){
        this.actions.listWorkCompletedProcessManage(this.app.options.id, function(json){
            json.data.each(function(process){
                this.loadProcessNode(process);
            }.bind(this));
        }.bind(this));
    },


    createWorkListHead: function(){
        var headNode = new Element("div", {"styles": this.css.workItemHeadNode}).inject(this.elementContentListNode);
        var html = "<div class='headArea1Node'><div class='checkAreaHeadNode'></div><div class='iconAreaHeadNode'></div><div class='titleAreaHeadNode'>"+this.app.lp.workTitle+"</div></div>" +
            "<div class='headArea2Node'><div class='timeAreaHeadNode'>"+this.app.lp.completedTime+"</div><div class='processAreaHeadNode'>"+this.app.lp.process+"</div></div>" +
            "<div class='headArea3Node'><div class='actionAreaHeadNode'>"+this.app.lp.action+"</div><div class='expireAreaHeadNode'>"+this.app.lp.expire+"</div><div class='personAreaHeadNode'>"+this.app.lp.person+"</div></div>";
        headNode.set("html", html);

        headNode.getElement(".headArea1Node").setStyles(this.css.headArea1Node);
        headNode.getElement(".headArea2Node").setStyles(this.css.headArea2Node);
        headNode.getElement(".headArea3Node").setStyles(this.css.headArea3Node);

        headNode.getElement(".checkAreaHeadNode").setStyles(this.css.checkAreaHeadNode);
        headNode.getElement(".iconAreaHeadNode").setStyles(this.css.iconAreaHeadNode);
        headNode.getElement(".titleAreaHeadNode").setStyles(this.css.titleAreaHeadNode);
        headNode.getElement(".timeAreaHeadNode").setStyles(this.css.timeAreaHeadNode);
        headNode.getElement(".processAreaHeadNode").setStyles(this.css.processAreaHeadNode);
        headNode.getElement(".personAreaHeadNode").setStyles(this.css.personAreaHeadNode);
        headNode.getElement(".actionAreaHeadNode").setStyles(this.css.actionAreaHeadNode);
        headNode.getElement(".expireAreaHeadNode").setStyles(this.css.expireAreaHeadNode);
    },

    _getCurrentPageData: function(callback, count){
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        if (this.filter){
            var filterData = {};
            Object.each(this.filter, function(v, k){
                if (k!="key"){
                    if (!filterData[k]) filterData[k] = [];
                    v.each(function(o){
                        filterData[k].push(o.value);
                    });
                }else{
                    filterData[k] = v;
                }
            });
            this.actions.listWorkCompletedFilterManage(id, count || this.pageCount, this.app.options.id, filterData, function(json){
                if (callback) callback(json);
            });
        }else{
            this.actions.listWorkCompletedNextManage(id, count || this.pageCount, this.app.options.id, function(json){
                if (callback) callback(json);
            });
        }
    },
    loadElementList: function(count){
        if (!this.isItemsLoaded){
            if (!this.isItemLoadding){
                this.isItemLoadding = true;
                this._getCurrentPageData(function(json){

                    this.count = json.count;

                    if (!this.isCountShow){
                        this.filterAllProcessNode.getFirst("span").set("text", "("+this.count+")");
                        this.isCountShow = true;
                    }
                    if (json.count<=this.items.length){
                        this.isItemsLoaded = true;
                    }
                    json.data.each(function(data){
                        var item = this._createItem(data);
                        this.items.push(item);
                        this.works[data.id] = item;
                    }.bind(this));

                    this.isItemLoadding = false;

                    if (this.loadItemQueue>0){
                        this.loadItemQueue--;
                        this.loadElementList();
                    }

                    this.mask.hide();
                }.bind(this), count);
            }else{
                this.loadItemQueue++;
            }
        }
    },
    _createItem: function(data){
        return new MWF.xApplication.process.Application.WorkCompletedExplorer.Work(data, this);
    },

    removeWorkCompleted: function(work){
        this.actions.removeWorkCompleted(work.data.id, function(json){
            json.data.each(function(item){
                this.items.erase(this.works[item.id]);
                this.works[item.id].destroy();
                MWF.release(this.works[item.id]);
                delete this.works[item.id];
            }.bind(this));
        }.bind(this));
    }

});

MWF.xApplication.process.Application.WorkCompletedExplorer.Work = new Class({
    Extends: MWF.xApplication.process.Application.WorkExplorer.Work,

    load: function(){
        this.node = new Element("div", {"styles": this.css.workItemNode});

        if (this.relative){
            this.node.inject(this.relative.node, "after");
        }else{
            this.node.inject(this.container);
        }

        this.workAreaNode =  new Element("div", {"styles": this.css.workItemWorkNode}).inject(this.node);
        //this.otherWorkAreaNode =  new Element("div", {"styles": this.css.workItemWorkNode}).inject(this.node);

        var html = "<div class='area1Node'><div class='checkAreaNode'></div><div class='iconAreaNode'></div><div class='titleAreaNode'><div class='titleAreaTextNode'>"+( this.data.title || "" )+"</div></div></div>" +
            "<div class='area2Node'><div class='timeAreaNode'></div><div class='processAreaNode'><div class='processAreaTextNode'>"+this.data.processName+"</div></div></div>" +
            "<div class='area3Node'><div class='actionAreaNode'></div><div class='expireAreaNode'></div><div class='personAreaNode'></div></div>";
        this.workAreaNode.set("html", html);

        this.workAreaNode.getElement(".area1Node").setStyles(this.css.headArea1Node);
        this.workAreaNode.getElement(".area2Node").setStyles(this.css.headArea2Node);
        this.workAreaNode.getElement(".area3Node").setStyles(this.css.headArea3Node);

        this.checkAreaNode = this.workAreaNode.getElement(".checkAreaNode").setStyles(this.css.checkAreaHeadNode);
        this.iconAreaNode = this.workAreaNode.getElement(".iconAreaNode").setStyles(this.css.iconAreaHeadNode);
        this.titleAreaNode = this.workAreaNode.getElement(".titleAreaNode").setStyles(this.css.titleAreaHeadNode);
        this.timeAreaNode = this.workAreaNode.getElement(".timeAreaNode").setStyles(this.css.timeAreaHeadNode);
        this.processAreaNode = this.workAreaNode.getElement(".processAreaNode").setStyles(this.css.processAreaHeadNode);
        this.personAreaNode = this.workAreaNode.getElement(".personAreaNode").setStyles(this.css.personAreaHeadNode);
        this.actionAreaNode = this.workAreaNode.getElement(".actionAreaNode").setStyles(this.css.actionAreaHeadNode);
        this.expireAreaNode = this.workAreaNode.getElement(".expireAreaNode").setStyles(this.css.expireAreaHeadNode);

        this.processAreaTextNode = this.workAreaNode.getElement(".processAreaTextNode").setStyles(this.css.processAreaTextNode);
        this.titleAreaTextNode = this.workAreaNode.getElement(".titleAreaTextNode").setStyles(this.css.titleAreaTextNode);

        if (this.data.expired){
            this.expireAreaNode.setStyle("color", "#FF0000");
            this.expireAreaNode.set("text", this.explorer.app.lp.expired);
        }else{
            this.expireAreaNode.set("text", this.explorer.app.lp.notExpired);
        }
        this.expireAreaNode.setStyles(this.css.expireAreaNode);

        if (!this.data.control.allowVisit){
            this.node.setStyles(this.css.workItemNode_noread)
            this.checkAreaNode.setStyles(this.css.actionStopWorkNode);
            this.actionAreaNode.setStyles(this.css.actionStopWorkActionNode);
        }

        this.iconAreaNode.setStyles(this.css.iconWorkNode);
        this.titleAreaNode.setStyles(this.css.titleWorkNode);
        this.setPersonData();
        this.setTimeData();

        this.setActions();

        this.setEvents();
    },
    reload: function(callback){
        this.explorer.actions.getWorkCompleted(this.data.id, function(json){
            this.data = json.data;
            this.titleAreaTextNode.set("text", this.data.title || "");
            this.activityAreaTextNode.set("text", this.data.activityName);

            this.statusAreaNode.empty();
            this.setStatusData();

            if (this.relativeWorks.length){
                this.relativeWorks.each(function(work){
                    work.destroy();
                });
            }
            this.relativeWorks = [];
            this.listRelatives();

            if (callback) callback();
        }.bind(this));
    },

    openWork: function(e){
        var options = {"workCompletedId": this.data.id, "isControl": this.explorer.app.options.application.allowControl};
        this.explorer.app.desktop.openApplication(e, "process.Work", options);
    },
    remove: function(e){
        var lp = this.explorer.app.lp;
        var text = lp.deleteWork.replace(/{title}/g, this.data.title || "");
        var _self = this;
        this.workAreaNode.setStyles(this.css.workItemWorkNode_remove);
        this.readyRemove = true;
        this.explorer.app.confirm("warn", e, lp.deleteWorkTitle, text, 350, 120, function(){
            _self.explorer.removeWorkCompleted(_self);
            this.close();
        }, function(){
            _self.workAreaNode.setStyles(_self.css.workItemWorkNode);
            _self.readyRemove = false;
            this.close();
        });
    },
    createChildNode: function(){
        this.childNode =  new Element("div", {"styles": this.css.workItemChildNode}).inject(this.node);
    //    this.taskAreaNode =  new Element("div", {"styles": this.css.workItemTaskNode}).inject(this.childNode);
        this.doneAreaNode =  new Element("div", {"styles": this.css.workItemDonwNode}).inject(this.childNode);
        this.readAreaNode =  new Element("div", {"styles": this.css.workItemReadNode}).inject(this.childNode);
        this.readedAreaNode =  new Element("div", {"styles": this.css.workItemReadedNode}).inject(this.childNode);

    //    this.taskAreaTitleAreaNode =  new Element("div", {"styles": this.css.workItemListTitleNode}).inject(this.taskAreaNode);
        this.doneAreaTitleAreaNode =  new Element("div", {"styles": this.css.workItemListTitleNode}).inject(this.doneAreaNode);
        this.readAreaTitleAreaNode =  new Element("div", {"styles": this.css.workItemListTitleNode}).inject(this.readAreaNode);
        this.readedAreaTitleAreaNode =  new Element("div", {"styles": this.css.workItemListTitleNode}).inject(this.readedAreaNode);

    //    var taskAreaTitleNode =  new Element("div", {"styles": this.css.workItemTaskTitleNode, "text": this.explorer.app.lp.task}).inject(this.taskAreaTitleAreaNode);
        var doneAreaTitleNode =  new Element("div", {"styles": this.css.workItemDoneTitleNode, "text": this.explorer.app.lp.done}).inject(this.doneAreaTitleAreaNode);
        var readAreaTitleNode =  new Element("div", {"styles": this.css.workItemReadTitleNode, "text": this.explorer.app.lp.read}).inject(this.readAreaTitleAreaNode);
        var readedAreaTitleNode =  new Element("div", {"styles": this.css.workItemReadedTitleNode, "text": this.explorer.app.lp.readed}).inject(this.readedAreaTitleAreaNode);

    //    this.taskAreaContentNode =  new Element("div", {"styles": this.css.taskAreaContentNode}).inject(this.taskAreaNode);
        this.doneAreaContentNode =  new Element("div", {"styles": this.css.doneAreaContentNode}).inject(this.doneAreaNode);
        this.readAreaContentNode =  new Element("div", {"styles": this.css.readAreaContentNode}).inject(this.readAreaNode);
        this.readedAreaContentNode =  new Element("div", {"styles": this.css.readedAreaContentNode}).inject(this.readedAreaNode);

    },
    listAssignments: function(){
        this.explorer.actions.listWorkCompletedAssignments(this.data.id, function(json){
    //        this.listTasks(json.data.taskList);
            this.listDones(json.data.taskCompletedList);
            this.listReads(json.data.readList);
            this.listReadeds(json.data.readCompletedList);
        }.bind(this));
    },
    listDones: function(list){
        if (list.length){
            list.each(function(data){
                var item = new MWF.xApplication.process.Application.WorkCompletedExplorer.Done(data, this.doneAreaContentNode, this.explorer, this);
                this.dones.push(item);
                this.doneChildren[data.id] = item;
            }.bind(this));
        }else{
            new Element("div", {"styles": this.css.noListText, "text": "［"+this.explorer.app.lp.noDone+"］"}).inject(this.doneAreaTitleAreaNode);
        }
    },
    listReads: function(list){
        if (list.length){
            list.each(function(data){
                var item = new MWF.xApplication.process.Application.WorkCompletedExplorer.Read(data, this.readAreaContentNode, this.explorer, this);
                this.reads.push(item);
                this.readChildren[data.id] = item;
            }.bind(this));
        }else{
            new Element("div", {"styles": this.css.noListText, "text": "［"+this.explorer.app.lp.noRead+"］"}).inject(this.readAreaTitleAreaNode);
        }
    },
    listReadeds: function(list){
        if (list.length){
            list.each(function(data){
                var item = new MWF.xApplication.process.Application.WorkCompletedExplorer.Readed(data, this.readedAreaContentNode, this.explorer, this);
                this.readeds.push(item);
                this.readedChildren[data.id] = item;
            }.bind(this));
        }else{
            new Element("div", {"styles": this.css.noListText, "text": "［"+this.explorer.app.lp.noReaded+"］"}).inject(this.readedAreaTitleAreaNode);
        }
    },

    setTimeData: function(){
        var d = new Date().parse(this.data.completedTime).format("%Y-%m-%d");
        var t = new Date().parse(this.data.completedTime).format("%Y-%m-%d %H:%M");
        this.timeAreaNode.set("text", d);
        this.timeAreaNode.set("title", t);
    }
});

MWF.xApplication.process.Application.WorkCompletedExplorer.Done = new Class({
    Extends: MWF.xApplication.process.Application.WorkExplorer.Done
});

MWF.xApplication.process.Application.WorkCompletedExplorer.Read = new Class({
    Extends: MWF.xApplication.process.Application.WorkExplorer.Read
});

MWF.xApplication.process.Application.WorkCompletedExplorer.Readed = new Class({
    Extends: MWF.xApplication.process.Application.WorkExplorer.Readed
});
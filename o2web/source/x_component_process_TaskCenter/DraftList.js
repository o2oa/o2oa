MWF.xDesktop.requireApp("process.TaskCenter", "TaskCompletedList", null, false);
MWF.xApplication.process.TaskCenter.DraftList = new Class({
    Extends: MWF.xApplication.process.TaskCenter.TaskCompletedList,
    initData: function(){
        this.count = 0;
        this.isCountShow = false;
        this.currentPage = 1;
        this.pageCount = 20;
        this.pages = 0;

        this.items = [];
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;

        this.filterApplication = "";
        this.currentFilterNode = null;
        this.filterListNode = null;
    },
    refresh: function(){
        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.app.content);
        this.initData();
        this.filterData = null;
        if (this.applicationFilterAreaNode) this.applicationFilterAreaNode.empty();
        this.createAppFilterNodes();
        this.listAreaNode.empty();
        this.resetListAreaHeight();
        this.app.getWorkCounts();
    },
    _getCurrentPageData: function(callback, count){
        this.app.getAction(function(){
            if (this.filterData){
                this.filterListNode.empty();
                var data = {};
                Object.each(this.filterData, function(v, key){
                    if (key!="key"){
                        if (v) {
                            //data[this.app.options.filterMap[key]] = v.value;
                            if (!data[this.app.options.filterMap[key]]) data[this.app.options.filterMap[key]] = [];
                            data[this.app.options.filterMap[key]].push(v.value);
                            this.createFilterItemNode(key, v);
                        }
                    }else{
                        data.key = v;
                    }
                }.bind(this));
                if (this.filterData.key){
                    this.createFilterItemNode("key", {"name": this.filterData.key});
                }

                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listReviewFilter(function(json){

                    if (callback) callback(json);
                }, null, id, count || this.pageCount, data);

            }else{
                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listDraftNext(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount);

            }

        }.bind(this));
    },
    // createActionBarNode: function(){
    //     return false;
    // },

    _getApplicationCount: function(callback){
        // this.app.getAction(function(){
        //     this.app.action.listReviewApplication(function(json){
        //         if (callback) callback(json);
        //     }.bind(this));
        // }.bind(this));
    },
    _createItem: function(task){
        return new MWF.xApplication.process.TaskCenter.DraftList.Item(task, this)
    },
    _getFilterCount: function(callback){
        // this.app.action.listReviewFilterCount(function(json){
        //     if (callback) callback(json);
        // });
    }
});

MWF.xApplication.process.TaskCenter.DraftList.Item = new Class({
    Extends: MWF.xApplication.process.TaskCenter.List.Item,
    setContent: function(){
        this.applicationTitleNode.set("text", this.data.applicationName);
        this.titleNode.set("html", "<font style=\"color: #333;\">["+this.data.processName+"]&nbsp;&nbsp;</font>"+o2.txt(this.data.title));
        this.titleNode.set("title", this.data.title);
        this.activityNode.set("text", "( "+this.list.app.lp.draftTab+" )");

        this.timeContentNode.set("text", this.list.app.lp.list_createDate+": "+this.data.createTime);

        this.loadActions();
        this.loadApplicationIcon();
        //this.setTimeIconNode();
    },
    openTask: function(e){
        //     this._getJobByTask(function(data){
        var options = {"draftId": this.data.id, "appId": "process.Work"+this.data.id};
        this.list.app.desktop.openApplication(e, "process.Work", options);
        //     }.bind(this));
    }
});

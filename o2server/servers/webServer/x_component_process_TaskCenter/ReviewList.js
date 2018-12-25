MWF.xDesktop.requireApp("process.TaskCenter", "TaskCompletedList", null, false);
MWF.xApplication.process.TaskCenter.ReviewList = new Class({
    Extends: MWF.xApplication.process.TaskCenter.TaskCompletedList,

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
                this.app.action.listReviewNext(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount);

            }

        }.bind(this));
    },

    _getApplicationCount: function(callback){
        this.app.getAction(function(){
            this.app.action.listReviewApplication(function(json){
                if (callback) callback(json);
            }.bind(this));
        }.bind(this));
    },
    _createItem: function(task){
        return new MWF.xApplication.process.TaskCenter.ReviewList.Item(task, this)
    },
    _getFilterCount: function(callback){
        this.app.action.listReviewFilterCount(function(json){
            if (callback) callback(json);
        });
    }
});

MWF.xApplication.process.TaskCenter.ReviewList.Item = new Class({
    Extends: MWF.xApplication.process.TaskCenter.TaskCompletedList.Item,
    setFlowChart: function(data){
        var idx = 0;
        data.workLogTokenList = {};
        data.workLogList.each(function(worklog){
            data.workLogTokenList[worklog.fromActivityToken] = worklog;

            if (!worklog.taskCompletedList) worklog.taskCompletedList = [];
            if (!worklog.taskList) worklog.taskList = [];
            if (worklog.taskCompletedList.length || worklog.taskList.length){
                this.createFlowInforWorklogNode(worklog.fromActivityName, worklog.taskCompletedList, worklog.taskList || [], idx, false);
                idx++;
            }
        }.bind(this));
        return idx;
    },
    _getSimpleJobByTaskComplete: function(callback){
        this.list.app.action.getSimpleJobByReview(function(json){
            if (callback) callback(json.data);
        }.bind(this), null, this.data.id);
    }
    // setContent: function(){
    //     this.titleActionNode = new Element("div", {"styles": this.list.css.titleActionNode}).inject(this.titleNode);
    //     this.titleTextNode = new Element("div", {"styles": this.list.css.titleTextNode, "title": this.data.title}).inject(this.titleNode);
    //
    //     this.titleTextNode.set("html", "<font style=\"color: #333;\">["+this.data.processName+"]</font>"+this.data.title);
    //
    //     //var processNode = new Element("div", {"styles": this.list.css.itemInforProcessNode,"text": this.data.processName}).inject(this.inforNode);
    //     var timeNode = new Element("div", {"styles": this.list.css.itemInforTimeNode, "text": this.data.startTime}).inject(this.inforNode);
    //     var activityNode = new Element("div", {"styles": this.list.css.itemInforActivityNode, "text": this.data.applicationName, "title": this.data.applicationName}).inject(this.inforNode);
    //
    //     this.loadActions();
    //     this.loadApplicationIcon();
    //     //    this.setTimeIconNode();
    // }
});
MWF.xDesktop.requireApp("process.TaskCenter", "TaskList", null, false);
MWF.xApplication.process.TaskCenter.MyCreatedList = new Class({
    Extends: MWF.xApplication.process.TaskCenter.TaskList,

    _getCurrentPageData: function(callback, count){
        if (this.filterData){
            this.filterListNode.empty();
            var data = {};
            Object.each(this.filterData, function(v, key){
                if (key!=="key"){
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
            o2.Actions.load("x_processplatform_assemble_surface").ReviewAction.V2ListCreateNext(id, this.pageCount, data).then(function(json){
                if (callback) callback(json);
            }.bind(this));

        }else{
            var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
            o2.Actions.load("x_processplatform_assemble_surface").ReviewAction.V2ListCreateNext(id, this.pageCount, {}).then(function(json){
                if (callback) callback(json);
            }.bind(this));
        }
    },

    _getApplicationCount: function(callback){
        this.app.getAction(function(){
            this.app.action.listReviewApplication(function(json){
                if (callback) callback(json);
            }.bind(this));
        }.bind(this));
    },
    _createItem: function(task){
        return new MWF.xApplication.process.TaskCenter.MyCreatedList.Item(task, this)
    },
    _getFilterCount: function(callback){
        o2.Actions.load("x_processplatform_assemble_surface").ReviewAction.filterCreateEntry().then(function(json){
            if (callback) callback(json);
        }.bind(this));
    }
});

MWF.xApplication.process.TaskCenter.MyCreatedList.Item = new Class({
    Extends: MWF.xApplication.process.TaskCenter.TaskList.Item
});
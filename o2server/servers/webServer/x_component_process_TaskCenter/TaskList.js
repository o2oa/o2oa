MWF.xDesktop.requireApp("process.TaskCenter", "List", null, false);
MWF.xApplication.process.TaskCenter.TaskList = new Class({
    Extends: MWF.xApplication.process.TaskCenter.List,

    _getFilterCount: function(callback){
        this.app.action.listTaskFilterCount(function(json){
            if (callback) callback(json);
        });
    },
    createAppFilterNodes: function(){
        var allApp = {"name": this.app.lp.all, "application": "", "count": 0};
        this.allAppFilterNode = this.createAppFilterNode(allApp, "appFilterNode_current");
        this.currentFilterNode = this.allAppFilterNode;

        this.filterListNode = new Element("div", {"styles": this.css.filterListNode}).inject(this.applicationFilterAreaNode);
    },
    createFilterItemNode: function(key, v){
        var _self = this;

        var node = new Element("div", {"styles": this.css.filterListItemNode}).inject(this.filterListNode);
        var actionNode = new Element("div", {"styles": this.css.filterListItemActionNode}).inject(node);
        var textNode = new Element("div", {"styles": this.css.filterListItemTextNode}).inject(node);
        textNode.set("text", this.app.lp[key]+": "+ v.name);

        actionNode.store("key", key);
        node.addEvents({
            "mouseover": function(){
                this.setStyles(_self.css.filterListItemNode_over);
                this.getLast().setStyles(_self.css.filterListItemTextNode_over);
                this.getFirst().setStyles(_self.css.filterListItemActionNode_over);
            },
            "mouseout": function(){
                this.setStyles(_self.css.filterListItemNode);
                this.getLast().setStyles(_self.css.filterListItemTextNode);
                this.getFirst().setStyles(_self.css.filterListItemActionNode);
            }
        });
        actionNode.addEvent("click", function(){
            var key = this.retrieve("key");
            if (_self.filterData[key]) _self.filterData[key] = null;
            delete _self.filterData[key];
            this.destroy();
            _self.refilter();
        });
    },
    _getCurrentPageData: function(callback, count){
        this.app.getAction(function(){
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
                this.app.action.listTaskFilter(function(json){

                    if (callback) callback(json);
                }, null, id, count || this.pageCount, data);

            }else{
                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listTaskNext(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount);

            }

        }.bind(this));
    }
});
MWF.xApplication.process.TaskCenter.TaskList.Item = new Class({
    Extends: MWF.xApplication.process.TaskCenter.List.Item
});
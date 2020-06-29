MWF.xDesktop.requireApp("process.Application", "WorkExplorer", null, false);
MWF.xApplication.process.Application.MyWorkExplorer = new Class({
	Extends: MWF.xApplication.process.Application.WorkExplorer,
	Implements: [Options, Events],
    createSearchElementNode: function(){
        this.toCompletedNode = new Element("div", {
            "styles": this.css.toCompletedNode,
            "text": this.app.lp.toCompleted
        }).inject(this.toolbarNode);
        this.toCompletedNode.addEvents({
            "mouseover": function(){
                this.toCompletedNode.setStyles(this.css.toCompletedNode_over);
            }.bind(this),
            "mouseout": function(){
                this.toCompletedNode.setStyles(this.css.toCompletedNode);
            }.bind(this),
            "click": function(){
                this.app.myWorkCompletedConfig();
            }.bind(this)
        });
    },

    _getFilterCount: function(callback){
        this.actions.listFilterAttribute(this.app.options.id, function(json){
            if (callback) callback(json);
        });
    },
    loadProcess: function(){
        this.actions.listProcessCount(this.app.options.id, function(json){
            json.data.each(function(process){
                this.loadProcessNode(process);
            }.bind(this));
        }.bind(this));
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
            this.actions.listWorkFilter(id, count || this.pageCount, this.app.options.id, filterData, function(json){
                if (callback) callback(json);
            });
        }else{
            this.actions.listWorkNext(id, count || this.pageCount, this.app.options.id, function(json){
                if (callback) callback(json);
            });
        }
    },
    _createItem: function(data){
        return new MWF.xApplication.process.Application.WorkExplorer.Work(data, this);
    },
    removeWork: function(work, all){
        this.actions.removeWork(work.data.id, this.app.options.id, all, function(json){
            json.data.each(function(item){
                this.items.erase(this.works[item.id]);
                if (this.works[item.id]) this.works[item.id].destroy();
                MWF.release(this.works[item.id]);
                delete this.works[item.id];
            }.bind(this));
        }.bind(this));
    }
});

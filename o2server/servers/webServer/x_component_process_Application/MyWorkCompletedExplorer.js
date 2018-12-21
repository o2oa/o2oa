MWF.xDesktop.requireApp("process.Application", "WorkCompletedExplorer", null, false);
MWF.xApplication.process.Application.MyWorkCompletedExplorer = new Class({
	Extends: MWF.xApplication.process.Application.WorkCompletedExplorer,
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
                this.app.myWorkConfig();
            }.bind(this)
        });
    },

    _getFilterCount: function(callback){
        this.actions.listWorkCompletedFilterAttribute(this.app.options.id, function(json){
            if (callback) callback(json);
        });
    },

    loadProcess: function(){
        this.actions.listWorkCompletedProcess(this.app.options.id, function(json){
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
            this.actions.listWorkCompletedFilter(id, count || this.pageCount, this.app.options.id, filterData, function(json){
                if (callback) callback(json);
            });
        }else{
            this.actions.listWorkCompletedNext(id, count || this.pageCount, this.app.options.id, function(json){
                if (callback) callback(json);
            });
        }
    }
});
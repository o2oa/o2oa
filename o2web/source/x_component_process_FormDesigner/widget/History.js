MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.xApplication.process.FormDesigner.widget.History = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {

	},
	initialize: function(form, options){
		this.setOptions(options);
        this.form = form;
		// this.path = "../x_component_process_FormDesigner/widget/$ImageClipper/";
		// this.cssPath = "../x_component_process_FormDesigner/widget/$ImageClipper/"+this.options.style+"/css.wcss";
		// this._loadCss();
	},

	load: function(data) {
        //存储当前表面状态数组-上一步
        this.preArray = [];
        //存储当前表面状态数组-下一步
        this.nextArray = [];
        //中间数组
        this.middleArray = [];
    },
    //获取domPath
    getPath: function (root, node) {
        var path = [];
        var parent, childrens, nodeIndex;
        while (node && node !== root) {
            parent = node.parentElement;
            childrens = Array.from(parent.children);
            nodeIndex = childrens.indexOf(node);
            path.push(nodeIndex);
            node = parent;
        }
        return path.reverse();
    },
    createItem: function(){

    },
    addItem: function(log, module) {
        // var log = {
        //     "operation": "create", //操作 create, copy, move, delete
        //     "type": "module", //property
        //     "json": {},
        //     "html": "",
        //     "path": ""
        // };
        debugger;

        log.newPath = this.getPath(this.form.node, module.node);
        this.middleArray.push(log);
    },
    undo : function( itemNode ){
        if(this.preArray.length>0){
            var popData=this.preArray.pop();
            var midData=this.middleArray[this.preArray.length+1];
            this.nextArray.push(midData);
            this.ctx.putImageData(popData,0,0);
        }

        this.toolbar.setAllItemsStatus();
    },
    redo : function( itemNode ){
        if(this.nextArray.length){
            var popData=this.nextArray.pop();
            var midData=this.middleArray[this.middleArray.length-this.nextArray.length-2];
            this.preArray.push(midData);
            this.ctx.putImageData(popData,0,0);
        }
        this.toolbar.setAllItemsStatus();
    },
    storeToPreArray : function(preData){
        //当前表面进栈
        this.preArray.push(preData);
    },
    storeToMiddleArray : function( preData ){
        //当前状态
        if( this.nextArray.length==0){
            this.middleArray.push(preData);
        }else{
            this.middleArray=[];
            this.middleArray=this.middleArray.concat(this.preArray);
            this.middleArray.push(preData);
            this.nextArray=[];
            this.toolbar.enableItem("redo");
        }

        if(this.preArray.length){
            this.toolbar.enableItem("undo");
            this.toolbar.enableItem("reset");
        }
    },
});

MWF.xApplication.process.FormDesigner.widget.HistoryItem = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {},
    initialize: function (history, options) {
        this.history = history;
    }
})


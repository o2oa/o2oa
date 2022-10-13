MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.process.FormDesigner.widget.History = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {

	},
	initialize: function(form, actionNode, options){
		this.setOptions(options);
        this.form = form;
        this.actionNode = actionNode;
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

        this.node = new Element("div", {"height":"100px"});

        var _self = this;
        debugger;
        this.tooltips = new MWF.xApplication.process.FormDesigner.widget.History.Tooltips(
            this.form.designer.formNode,
            this.actionNode,
            this.form.designer,
            null,
            {
                onPostCreate: function () {
                    _self.node.inject( this.contentNode );
                }
            }
        );
        this.tooltips.load();
    },
    //获取domPath
    getPath: function (node) {
	    var root = this.form.node;
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
    add: function(log, module) {
        // var log = {
        //     "operation": "create", //操作 create, copy, move, delete
        //     "type": "module", //property
        //     "json": {},
        //     "html": "",
        //     "path": ""
        // };
        debugger;

        log.newPath = this.getPath(module.node);

        var item = new MWF.xApplication.process.FormDesigner.widget.History.Item(this, log);

        this.middleArray.push(item);
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

MWF.xApplication.process.FormDesigner.widget.History.Tooltips = new Class({
    Extends: MTooltips,
    options : {
        style: "design",
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "right", //x轴上left center right,  auto 系统自动计算
            y : "bottom" //y 轴上top middle bottom, auto 系统自动计算
        },
        event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        isAutoHide: false,
        hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : false,
        hasCloseAction: true,
        hasMask: false,
        isParentOffset: true,
        nodeStyles: {
            padding: "0px",
            "min-height": "100px",
            "border-radius" : "0px"
        }
    },
    _customNode : function( node, contentNode ){
        new Element("div", {
            "style": "padding-left: 10px; background-color: rgb(242, 242, 242); color: #333333; height: 30px; line-height: 30px; ",
            "text": "历史记录"
        }).inject(contentNode, "before");
        //var width = ( parseInt( this.selector.options.width )  )+ "px";
        //node.setStyles({
        //    "width": width,
        //    "max-width": width
        //});
        debugger;
        // if( this.data && this.data.length > 0 ){
        //     this.createItemList( this.data, contentNode )
        // }else if( this.selector.options.tooltipWhenNoSelectValue ){
        //     this.createNoSelectValueNode( contentNode );
        // }
    },
})

MWF.xApplication.process.FormDesigner.widget.History.Item = new Class({
    Implements: [Options, Events],
    options: {},
    initialize: function (history, log) {
        this.history = history;
        this.data = log;
    },

})


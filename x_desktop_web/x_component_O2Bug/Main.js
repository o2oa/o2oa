MWF.require("MWF.widget.MaskNode", null, false);
MWF.xApplication.O2Bug.options.multitask = false;
MWF.xApplication.O2Bug.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "O2Bug",
		"icon": "icon.png",
		"width": "1200",
		"height": "700",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.O2Bug.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.O2Bug.LP;
	},
    loadApplication: function(callback){
        this.loadTitle();
        this.loadFilter();
    },
    loadTitle: function(){
        this.loadTitleBar();
        this.loadTitleUserNode();
        this.loadCreateActionNode();
        this.loadTitleTextNode();
        this.loadSearchNode();
    },
    loadTitleBar: function(){
        this.titleBar = new Element("div", {
            "styles": this.css.titleBar
        }).inject(this.content);
    },
    loadTitleUserNode: function(){
        this.titleUserNode = new Element("div", {
            "styles": this.css.titleUserNode
        }).inject(this.titleBar);
        this.titleUserIconNode = new Element("div", {
            "styles": this.css.titleUserIconNode
        }).inject(this.titleUserNode);
        this.titleUserTextNode = new Element("div", {
            "styles": this.css.titleUserTextNode,
            "text": this.desktop.session.user.name
        }).inject(this.titleUserNode);
    },
    loadCreateActionNode: function() {
        this.createAction = new Element("div", {
            "styles": this.css.createAction
        }).inject(this.titleBar);
        this.createAction.addEvents({
            "click": function(e){
                this.createBug();
            }.bind(this)
        });
    },
    loadTitleTextNode: function(){
        this.titleTextNode = new Element("div", {
            "styles": this.css.titleTextNode,
            "text": this.lp.title
        }).inject(this.titleBar);
    },
    loadSearchNode: function(){
        this.searchBarAreaNode = new Element("div", {
            "styles": this.css.searchBarAreaNode
        }).inject(this.titleBar);

        this.searchBarNode = new Element("div", {
            "styles": this.css.searchBarNode
        }).inject(this.searchBarAreaNode);

        this.searchBarActionNode = new Element("div", {
            "styles": this.css.searchBarActionNode
        }).inject(this.searchBarNode);
        this.searchBarInputBoxNode = new Element("div", {
            "styles": this.css.searchBarInputBoxNode
        }).inject(this.searchBarNode);
        this.searchBarInputNode = new Element("input", {
            "type": "text",
            "value": this.lp.searchKey,
            "styles": this.css.searchBarInputNode
        }).inject(this.searchBarInputBoxNode);

        var _self = this;
        this.searchBarActionNode.addEvent("click", function(){
            this.searchTask();
        }.bind(this));
        this.searchBarInputNode.addEvents({
            "focus": function(){
                if (this.value==_self.lp.searchKey) this.set("value", "");
            },
            "blur": function(){if (!this.value) this.set("value", _self.lp.searchKey);},
            "keydown": function(e){
                if (e.code==13){
                    this.searchTask();
                    e.preventDefault();
                }
            }.bind(this),
            "selectstart": function(e){
                e.preventDefault();
            }
        });
    },
    loadFilter: function(){
        this.filterBar = new Element("div", {"styles": this.css.filterBar}).inject(this.content);
        this.filterTitleNode = new Element("div", {"styles": this.css.filterTitleNode}).inject(this.filterBar);
        this.filterTitleNode.set("text", this.lp.filter);
        this.filterContentNode = new Element("div", {"styles": this.css.filterContentNode}).inject(this.filterBar);

        var html = this.lp.bugType+": <select id='sel_bugType'></select><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>"+
            this.lp.creator+": <select id='sel_creator'></select><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>"+
            this.lp.targetUser+": <select id='sel_targetUser'></select><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>"+
            this.lp.status+": <select id='sel_status'></select><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        this.filterContentNode.set("html", html);

        this.filterBugTypeNode = this.filterContentNode.getElementById("sel_bugType");
        this.filterCreatorNode = this.filterContentNode.getElementById("sel_targetUser");
        this.filterTargetUserNode = this.filterContentNode.getElementById("sel_targetUser");
        this.filterStatusNode = this.filterContentNode.getElementById("sel_status");

        Object.each(this.lp.bugTypeList, function(v, k){new Element("option", {"value": k,"text": v}).inject(this.filterBugTypeNode)}.bind(this));
        Object.each(this.lp.statusList, function(v, k){new Element("option", {"value": k,"text": v}).inject(this.filterStatusNode)}.bind(this));
    },

    createBug: function(){
        this.note = new MWF.xApplication.O2Bug.Note(null, this.createAction, this);
    }
});

MWF.xApplication.O2Bug.Note = new Class({
    initialize: function(data, node, app){
        this.data = data;
        this.startNode = node;
        this.app = app;
        this.css = this.app.css;
        this.container = this.app.content;
        this.isNew = ((this.data) && (this.data.id)) ? true : false;
        this.load();
    },
    load: function(){
        if (!this.data) this.createNewData();
        this.mask = new MWF.widget.MaskNode(this.container, {"loading": false});
        this.mask.load();
        this.createNode();
        this.show();
        //this.loadContent();
    },
    createNewData: function(){
        this.data = {}
    },
    createNode: function(){
        var size = this.startNode.getSize();
        this.node = new Element("div", {
            "styles": this.css.bugNoteNode
        }).inject(this.container);
        this.node.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px"
        });
        this.node.position({
            relativeTo: this.startNode,
            position: 'topLeft',
            edge: 'topLeft'
        });
    },
    show: function(){
        var o = this.getNodeCoordinates();

        var fx = new Fx.Morph(this.node, {
            "duration": "500",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            "opacity": 1,
            "width": ""+ o.width+"px",
            "height": ""+ o.height+"px",
            "left": ""+ o.left+"px",
            "top": ""+ o.top+"px"
        }).chain(function(){
            this.setNodeSizeFun = this.setNodeSize.bind(this);
            this.app.addEvent("resize", this.setNodeSizeFun);
        }.bind(this));
    },
    setNodeSize: function(){
        var o = this.getNodeCoordinates();
        this.node.setStyles({
            "width": ""+ o.width+"px",
            "height": ""+ o.height+"px",
            "left": ""+ o.left+"px",
            "top": ""+ o.top+"px"
        });
    },
    getNodeCoordinates: function(){
        var size = this.container.getSize();
        var w = size.x*0.8;
        if (w<800) w = 800;
        var h = size.y*0.8;
        if (h<300) h = 300;
        var position = this.container.getPosition(this.container.getOffsetParent());
        var l = size.x/2-w/2;
        if (l<0) l=0;
        l = position.x+l;
        var t = size.y/2-h/2;
        if (t<0) t=0;
        t = position.y+t;

        return {
            "width": w,
            "height": h,
            "left": l,
            "top": t
        }
    },

});
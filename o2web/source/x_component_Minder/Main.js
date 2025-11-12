//MWF.xDesktop.requireApp("Minder", "Actions.RestActions", null, false);

MWF.require("MWF.widget.Tree", null, false);
MWF.xApplication.Minder.MainPc = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Minder",
		"icon": "icon.png",
		"width": "1200",
        "height": "700",
        "isResize": false,
		"title": MWF.xApplication.Minder.LP.title,
        "defaultAction" : "openMineExplorer"
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Minder.LP;
        this.restActions = MWF.Actions.get("x_mind_assemble_control"); //new MWF.xApplication.Minder.Actions.RestActions();
	},
	loadApplication: function(callback){
		this.createNode();
		this.loadApplicationContent();
		if (callback) callback();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": this.css.node
		}).inject(this.content);
	},
	loadApplicationContent: function() {
        this.naviNode = new Element("div.naviNode",{
            "styles" : this.css.naviNode
        }).inject(this.node);
        // new Element("div.naviTopNode",{
        //     "styles" : this.css.naviTopNode
        // }).inject(this.naviNode);

        this.contentNode = new Element("div.contentNode",{
            "styles" : this.css.contentNode
        }).inject(this.node);

        //this.histroy = new MWF.xApplication.Minder.History(this);

        this.resizeContent();
        this.resizeFun = this.resizeContent.bind( this );
        this.addEvent("resize", this.resizeFun);

        this.loadNavi();
    },
    loadNavi : function(){
        var naviJson = [
            {
                "title": this.lp.myFiles,
                "action": "openMineExplorer",
                "icon": "ooicon-personnel"
            },
            {
                "title": this.lp.shareFiles,
                "action": "openSharedExplorer",
                "icon": "ooicon-fawen"
            },
            {
                "title": this.lp.editorFiles,
                "action": "openReceivedExplorer",
                "icon": "ooicon-shouwen"
            },
            {
                "title": this.lp.trashBin,
                "action": "openRecycleExplorer",
                "icon": "ooicon-maintain"
            }
        ];
        naviJson.each( function( d ){
            this.createNaviNode( d );
        }.bind(this))
    },
    openMineExplorer: function(){
        MWF.xDesktop.requireApp("Minder", "MineExplorer", null, false);
        if(this.currentExplorer){
            this.currentExplorer.destroy();
        }
        var options = (this.status && this.status.explorerStatus ) ? this.status.explorerStatus : {};
        this.currentExplorer = new MWF.xApplication.Minder.MineExplorer( this.contentNode, this, options );
        this.currentExplorer.load();
    },
    openSharedExplorer: function(){
        MWF.xDesktop.requireApp("Minder", "SharedExplorer", null, false);
        if(this.currentExplorer){
            this.currentExplorer.destroy();
        }
        var options = (this.status && this.status.explorerStatus ) ? this.status.explorerStatus : {};
        this.currentExplorer = new MWF.xApplication.Minder.SharedExplorer( this.contentNode, this, options );
        this.currentExplorer.load();
    },
    openReceivedExplorer: function(){
        MWF.xDesktop.requireApp("Minder", "ReceivedExplorer", null, false);
        if(this.currentExplorer){
            this.currentExplorer.destroy();
        }
        var options = (this.status && this.status.explorerStatus ) ? this.status.explorerStatus : {};
        this.currentExplorer = new MWF.xApplication.Minder.ReceivedExplorer( this.contentNode, this, options );
        this.currentExplorer.load();
    },
    openRecycleExplorer: function(){
        MWF.xDesktop.requireApp("Minder", "RecycleBinExplorer", null, false);
        if(this.currentExplorer){
            this.currentExplorer.destroy();
        }
        var options = (this.status && this.status.explorerStatus ) ? this.status.explorerStatus : {};
        this.currentExplorer = new MWF.xApplication.Minder.RecycleBinExplorer( this.contentNode, this, options );
        this.currentExplorer.load();
    },
    createNaviNode : function( d ){
        var _self = this;
        var node = new Element("div",{
            styles : this.css.naviItemNode,
            events : {
                click : function( ev ){
                    if( _self.currentAction == d.action )return;
                    node.setStyles( _self.css.naviItemNode_selected );
                    if(_self.currentNaviItemNode)_self.currentNaviItemNode.setStyles( _self.css.naviItemNode ).removeClass( 'mainColor_color' );
                    if(_self.currentNaviItemIconNode)_self.currentNaviItemIconNode.removeClass( 'mainColor_color' );
                    _self.currentNaviItemNode = node;
                    _self.currentNaviItemIconNode = iconNode;
                    _self.currentAction = d.action;
                    _self[ d.action ]();

                    node.addClass('mainColor_color');
                    iconNode.addClass('mainColor_color');
                },
                mouseenter: function (){
                    if( _self.currentNaviItemNode !== node )node.addClass('mainColor_color');
                    if( _self.currentNaviItemNode !== node )iconNode.addClass('mainColor_color');
                },
                mouseleave: function (){
                    if( _self.currentNaviItemNode !== node )node.removeClass('mainColor_color');
                    if( _self.currentNaviItemNode !== node )iconNode.removeClass('mainColor_color');
                }
            }
        }).inject( this.naviNode );
        var iconNode = new Element(`div.${d.icon}`, {
            styles : this.css.naviItemIconNode
        }).inject(node);
        new Element(`div`, {
            text : d.title,
            styles : this.css.naviItemTextNode
        }).inject(node);
        if( this.status && this.status.action && this.status.action == d.action ){
            node.click();
        }else if( this.options.defaultAction == d.action  ){
            node.click();
        }
    },
    recordStatus : function(){
        return {
            action : this.currentAction,
            explorerStatus : this.currentExplorer.recordStatus()
        }
    },
    resizeContent : function(){
        // var size = this.content.getSize();
        // this.naviNode.setStyle("height", size.y);
    },
    getDateDiff : function (publishTime) {
        if(!publishTime)return "";
        var dateTimeStamp = Date.parse(publishTime.replace(/-/gi, "/"));
        var minute = 1000 * 60;
        var hour = minute * 60;
        var day = hour * 24;
        var halfamonth = day * 15;
        var month = day * 30;
        var year = month * 12;
        var now = new Date().getTime();
        var diffValue = now - dateTimeStamp;
        if (diffValue < 0) {
            //若日期不符则弹出窗口告之
            //alert("结束日期不能小于开始日期！");
        }
        var yesterday = new Date().decrement('day', 1);
        var beforYesterday = new Date().decrement('day', 2);
        var yearC = diffValue / year;
        var monthC = diffValue / month;
        var weekC = diffValue / (7 * day);
        var dayC = diffValue / day;
        var hourC = diffValue / hour;
        var minC = diffValue / minute;
        if (yesterday.getFullYear() == dateTimeStamp.getFullYear() && yesterday.getMonth() == dateTimeStamp.getMonth() && yesterday.getDate() == dateTimeStamp.getDate()) {
            result = "昨天 " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
        } else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
            result = "前天 " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
        } else if (yearC > 1) {
            result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
        } else if (monthC >= 1) {
            //result= parseInt(monthC) + "个月前";
            // s.getFullYear()+"年";
            result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
        } else if (weekC >= 1) {
            result = parseInt(weekC) + "周前";
        } else if (dayC >= 1) {
            result = parseInt(dayC) + "天前";
        } else if (hourC >= 1) {
            result = parseInt(hourC) + "小时前";
        } else if (minC >= 1) {
            result = parseInt(minC) + "分钟前";
        } else
            result = "刚刚";
        return result;
    }
});

MWF.xApplication.Minder.MainMobile = new Class({
    Extends: MWF.xApplication.Minder.MainPc,
    options: {
        "style": "mobile"
    },
    loadApplication: function(callback){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.content);
        this.node.loadCss(`../x_component_Minder/$Main/${this.options.style}/style.css`);
        this.loadView();
        if (callback) callback();
    },
    reload: function(){
        this.node.empty();
        this.loadView();
    },
    loadView: function(){
        this.node.loadHtml(
            `../x_component_Minder/$Main/${this.options.style}/main.html`,
            {
                module: this,
                bind: {
                    lp: this.lp
                }
            },
            ()=>{
                this.loadFolder();
            }
        );
    },
    loadFolder: function (){
        var setProperty = (tree)=>{
            tree.text = tree.name;
            tree.selectable = 'yes';
            tree.icon = 'folder-open';
            tree.expanded = true;
            tree.children?.forEach( (child)=>{
                setProperty(child);
            });
            return tree;
        };
        var p = o2.Actions.load('x_mind_assemble_control').MindFolderInfoAction.treeMyFolder();
        p.then((json)=>{
            var rootData = {
                id : "root",
                name : "根目录",
                orderNumber : "1",
                description : "",
                selected: true,
                children: json.data
            };
            var tree = setProperty(rootData);
            this.nav.setMenu([tree]);
        });
        this.nav.addEventListener('select', (e)=>{
            var {text, id} = e.detail.data;
            this.closeFolder();
            this.folderName.set('text', text);
            this.loadList(id);
        });
    },
    switchFolder: function(e){
        if( this.folderArea.offsetParent ){
            this.folderArea.addClass('hide');
            this.mask.addClass('hide');
            this.arrow.addClass('up');
        }else{
            this.folderArea.removeClass('hide');
            this.mask.removeClass('hide');
            this.arrow.removeClass('up');
        }
    },
    closeFolder: function(e){
        this.folderArea.addClass('hide');
        this.mask.addClass('hide');
        this.arrow.addClass('up');
    },
    loadList: function(folderId = 'root'){
        this.listArea.empty();
        var p = o2.Actions.load('x_mind_assemble_control').MindInfoAction.listNextWithFilter('(0)', 100, {"folderId": folderId});
        p.then((json)=>{
            this.listArea.loadHtml(
                `../x_component_Minder/$Main/${this.options.style}/list.html`,
                {
                    module: this,
                    bind: {
                        lp: this.lp,
                        data: json.data
                    }
                },
                ()=>{}
            );
            this.fireEvent("postLoad")
        });
    }
});

if ((layout.mobile || COMMON.Browser.Platform.isMobile)){
    MWF.xApplication.Minder.Main = MWF.xApplication.Minder.MainMobile;
}else{
    MWF.xApplication.Minder.Main = MWF.xApplication.Minder.MainPc;
}


MWF.xApplication.Minder.History = new Class({
    initialize : function( app ){
        this.app = app;
        this.css = app.css;

        this.MAX_HISTORY = 100;

        this.lastSnap;
        this.patchLock;
        this.undoDiffs = [];
        this.redoDiffs = [];

        this.reset();
        this.app.addEvent('patchChange', this.changed.bind(this));
    },
    load : function( container ){
        if( this.node ){
            this.node.inject( container );
        }else{
            this.loadNode( container );
        }
    },
    loadNode : function( container ){
        this.node = new Element("div",{
            styles : this.css.historyNode
        }).inject( container );

        this.undoNode = new Element("div",{ styles : this.css.undoNode }).inject( this.node );

        this.redoNode = new Element("div",{  styles : this.css.redoNode }).inject( this.node );

        this.patchNode = new Element("div", { styles : this.css.patchNode }).inject( this.node );
    },
    reset: function () {
        this.undoDiffs = [];
        this.redoDiffs = [];
        this.lastSnap = "mine/root";
    },

    makeUndoDiff: function( snap ) {
        if (snap != this.lastSnap) {
            this.undoDiffs.push(snap);
            while (this.undoDiffs.length > this.MAX_HISTORY) {
                this.undoDiffs.shift();
            }
            this.lastSnap = snap;
            return true;
        }
    },

    makeRedoDiff:function( snap ) {
        this.redoDiffs.push( snap );
    },

    undo: function() {
        this.patchLock = true;
        var undoDiff = this.undoDiffs.pop();
        if (undoDiff) {
            this.app.applyPatches(undoDiff);
            this.makeRedoDiff( undoDiff );
        }
        this.patchLock = false;
    },

    redo: function() {
        this.patchLock = true;
        var redoDiff = this.redoDiffs.pop();
        if (redoDiff) {
            this.app.applyPatches(redoDiff);
            this.makeUndoDiff( redoDiff );
        }
        this.patchLock = false;
    },

    changed: function( snap ) {
        if (this.patchLock) return;
        if (this.makeUndoDiff( snap )) this.redoDiffs = [];
    },

    hasUndo: function() {
        return !!this.undoDiffs.length;
    },

    hasRedo: function() {
        return !!this.redoDiffs.length;
    }
});




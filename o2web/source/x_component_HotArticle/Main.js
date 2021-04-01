MWF.xApplication.HotArticle = MWF.xApplication.HotArticle || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("HotArticle", "Access", null, false);
//MWF.xDesktop.requireApp("HotArticle", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);

MWF.xApplication.HotArticle.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.HotArticle.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "HotArticle",
        "icon": "icon.png",
        "width": "1220",
        "height": "700",
        "isResize": true,
        "isMax": true,
        "title": MWF.xApplication.HotArticle.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.HotArticle.LP;
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.distinguishedName;

        this.restActions = MWF.Actions.get("x_hotpic_assemble_control"); //new MWF.xApplication.HotArticle.Actions.RestActions();

        this.path = "../x_component_HotArticle/$Main/" + this.options.style + "/";
        this.createNode();
        this.loadApplicationContent();
    },
    loadController: function (callback) {

        //this.access = new MWF.xApplication.HotArticle.Access( this.restActions, this.lp );

        if (callback)callback();
    },
    reload : function(){
        this.clearContent();
        if( this.explorer ){
            this.openSetting( this.explorer.currentNaviItem.retrieve("index") )
        }else{
            this.loadApplicationLayout();
        }
    },
    isAdmin : function(){
      return this.access.isAdmin();
    },
    createNode: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": this.css.node
        }).inject(this.content);
    },
    loadApplicationContent: function () {
        this.loadController(function () {
            this.loadApplicationLayout();
        }.bind(this))
    },
    loadApplicationLayout: function () {
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);
        this.createTopNode();
        this.createContainerNode();

    },
    createTopNode: function () {
        this.topContainerNode = new Element("div.topContainerNode", {
            "styles": this.css.topContainerNode
        }).inject(this.contentContainerNode);

        this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.topContainerNode);

        this.topIconNode = new Element("div", {
            "styles": this.css.topIconNode
        }).inject(this.topNode);

        this.topTextNode = new Element("div", {
            "styles": this.css.topTextNode,
            "text": this.options.title
        }).inject(this.topNode);

        this.topContentNode = new Element("div", {
            "styles": this.css.topContentNode
        }).inject(this.topNode);

        //this.searchDiv = new Element("div.searchDiv",{
        //    "styles" : this.css.searchDiv
        //}).inject(this.topNode)
        //this.searchInput = new Element("input.searchInput",{
        //    "styles" : this.css.searchInput,
        //    "value" : this.lp.searchKey,
        //    "title" : this.lp.searchTitle
        //}).inject(this.searchDiv)
        //var _self = this;
        //this.searchInput.addEvents({
        //    "focus": function(){
        //        if (this.value==_self.lp.searchKey) this.set("value", "");
        //    },
        //    "blur": function(){if (!this.value) this.set("value", _self.lp.searchKey);},
        //    "keydown": function(e){
        //        if (e.code==13){
        //            this.search();
        //            e.preventDefault();
        //        }
        //    }.bind(this)
        //});
        //
        //this.searchAction = new Element("div.searchAction",{
        //    "styles" : this.css.searchAction
        //}).inject(this.searchDiv);
        //this.searchAction.addEvents({
        //    "click": function(){ this.search(); }.bind(this),
        //    "mouseover": function(e){
        //        this.searchAction.setStyles( this.css.searchAction_over2 );
        //        e.stopPropagation();
        //    }.bind(this),
        //    "mouseout": function(){ this.searchAction.setStyles( this.css.searchAction ) }.bind(this)
        //});
        //this.searchDiv.addEvents( {
        //    "mouseover" : function(){
        //        this.searchInput.setStyles( this.css.searchInput_over )
        //        this.searchAction.setStyles( this.css.searchAction_over )
        //    }.bind(this),
        //    "mouseout" : function(){
        //        this.searchInput.setStyles( this.css.searchInput )
        //        this.searchAction.setStyles( this.css.searchAction )
        //    }.bind(this)
        //} )



    },
    createContainerNode: function () {
        this.createContent();
    },
    createContent: function () {

        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.contentContainerNode);


        //MWF.require("MWF.widget.ScrollBar", function () {
        //    this.scrollBar = new MWF.widget.ScrollBar(this.contentContainerNode, {
        //        "indent": false,
        //        "style": "xApp_TaskList",
        //        "where": "before",
        //        "distance": 30,
        //        "friction": 4,
        //        "axis": {"x": false, "y": true},
        //        "onScroll": function (y) {
        //        }
        //    });
        //}.bind(this));

        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.middleNode);

        this.createView();

        this.setContentSizeFun = this.setContentSize.bind(this);
        this.addEvent("resize", this.setContentSizeFun );
        this.setContentSize();

    },
    setContentSize: function () {
       var topSize = this.topNode ? this.topNode.getSize() : {"x": 0, "y": 0};
        //var topSize = {"x": 0, "y": 0};
        var nodeSize = this.node.getSize();
        var pt = this.viewContainerNode.getStyle("padding-top").toFloat();
        var pb = this.viewContainerNode.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y - topSize.y - pt - pb;
        this.viewContainerNode.setStyle("height", "" + height + "px");
    },
    createView: function () {
        var viewContainerNode = this.viewContainerNode = new Element("div.viewContainerNode", {
            "styles": this.css.viewContainerNode
        }).inject(this.contentNode);

        var view = new MWF.xApplication.HotArticle.Main.View(viewContainerNode, this, this, {
            templateUrl: this.path + "listItem.json",
            "scrollEnable" : true
        }, {
            lp: this.lp
        });
        view.load();
    },
    clearContent: function () {
        if (this.explorer)this.explorer.destroy();
        this.explorer = null;
        if(this.setContentSizeFun)this.removeEvent("resize", this.setContentSizeFun );
        if(this.scrollBar && this.scrollBar.scrollVAreaNode)this.scrollBar.scrollVAreaNode.destroy();
        if( this.scrollBar )delete this.scrollBar;
        if (this.contentContainerNode) {
            this.contentContainerNode.destroy();
            //this.middleNode.destroy();
            //this.contentNode.destroy();
        }
    },
    openCategory : function( d ){
        var appId = "HotArticleCategory"+ d.id;
        if (this.desktop.apps[appId]){
            this.desktop.apps[appId].setCurrent();
        }else {
            this.desktop.openApplication(null, "HotArticleCategory", {
                "categoryId": d.id,
                "appId": appId
            });
        }

    },
    recordStatus: function () {
        var status = {};
        if( this.explorer ){
            status = {
                setting : true,
                index : this.explorer.currentNaviItem.retrieve("index")
            };
        }
        return status;
    }
});

MWF.xApplication.HotArticle.Main.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
            return new MWF.xApplication.HotArticle.Main.Document(this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        if(!count)count=9;
        if(!pageNum){
            if( this.pageNum ){
                pageNum = this.pageNum = this.pageNum+1;
            }else{
                pageNum = this.pageNum = 1;
            }
        }else{
            this.pageNum = pageNum;
        }
        this.getPictureUrlHost();

        var filter = this.filterData || {};
        //filter.withTopSubject = false;
        this.actions.listHotPicFilterPage( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function (documentData, all) {
        this.actions.removeHotPic(documentData.id, function(json){
            this.pageNum = 0;
            this.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function () {

    },
    _openDocument: function (documentData) {

    },
    _queryCreateViewNode: function () {

    },
    _postCreateViewNode: function (viewNode) {

    },
    _queryCreateViewHead: function () {

    },
    _postCreateViewHead: function (headNode) {

    },
    getPictureUrlHost: function() {
        var addressObj = layout.serviceAddressList["x_hotpic_assemble_control"];
        this.pictureUrlHost = "http://"+addressObj.host +  ( addressObj.port != 80 ? (":"+ addressObj.port +"/") : "/" );
    }

});

MWF.xApplication.HotArticle.Main.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument : function(itemNode, ev){
        var removeNode = itemNode.getElements("[styles='removeNode']")[0];
        if( removeNode )removeNode.setStyle("opacity",1)
    },
    mouseoutDocument : function(itemNode, ev){
        var removeNode = itemNode.getElements("[styles='removeNode']")[0];
        if( removeNode )removeNode.setStyle("opacity",0)
    },
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {
        //var iconNode = itemNode.getElements("[item='icon']")[0];
        //MWF.getJSON( this.view.pictureUrlHost + iconNode.get("picUrl"), function( json ){
        //    iconNode.set("src", json.data.value);
        //} )
    },
    getRemovePermission: function( d ){
        if( this.app.userName == d.creator ){
            return true;
        }
        //if( d.application == "BBS" && MWF.AC.isBBSManager() ){
        //    return true;
        //}
        //if( d.application == "CMS" && MWF.AC.isCMSManager() ){
        //    return true;
        //}
        if( MWF.AC.isHotPictureManager() ){
            return true;
        }
        return false;
    },
    open: function(  ){
        var data = this.data;
        if( data.application == "BBS" ){
            var appId = "ForumDocument"+data.infoId;
            if (this.app.desktop.apps[appId]){
                this.app.desktop.apps[appId].setCurrent();
            }else {
                this.app.desktop.openApplication(null, "ForumDocument", {
                    "id" : data.infoId,
                    "appId": appId,
                    "isEdited" : false,
                    "isNew" : false
                });
            }
        }else{
            var appId = "cms.Document"+data.infoId;
            if (this.app.desktop.apps[appId]){
                this.app.desktop.apps[appId].setCurrent();
            }else {
                this.app.desktop.openApplication(null, "cms.Document", {
                    "documentId" : data.infoId,
                    "appId": appId,
                    "readonly" : true
                });
            }
        }
    }
});


var getDateDiff = function (publishTime) {
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
        result = MWF.xApplication.HotArticle.LP.yesterday + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
        result = MWF.xApplication.HotArticle.LP.twoDaysAgo + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (yearC > 1) {
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (monthC >= 1) {
        //result= parseInt(monthC) + "个月前";
        // s.getFullYear()+"年";
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (weekC >= 1) {
        result = parseInt(weekC) + MWF.xApplication.HotArticle.LP.weekAgo;
    } else if (dayC >= 1) {
        result = parseInt(dayC) + MWF.xApplication.HotArticle.LP.dayAgo;
    } else if (hourC >= 1) {
        result = parseInt(hourC) +  MWF.xApplication.HotArticle.LP.hourAgo;
    } else if (minC >= 1) {
        result = parseInt(minC) +  MWF.xApplication.HotArticle.LP.minuteAgo;
    } else
        result = MWF.xApplication.HotArticle.LP.publishJustNow;
    return result;
};



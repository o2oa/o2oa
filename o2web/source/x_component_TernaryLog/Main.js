MWF.xApplication.TernaryLog = MWF.xApplication.TernaryLog || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("TernaryLog", "Access", null, false);
//MWF.xDesktop.requireApp("TernaryLog", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);

MWF.xApplication.TernaryLog.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.TernaryLog.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "TernaryLog",
        "icon": "icon.png",
        "width": "1220",
        "height": "700",
        "isResize": true,
        "isMax": true,
        "viewPageNum": 1,
        "title": MWF.xApplication.TernaryLog.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.TernaryLog.LP;
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.distinguishedName;

        this.path = "../x_component_TernaryLog/$Main/" + this.options.style + "/";
        this.createNode();
        this.loadApplicationContent();
    },
    loadController: function (callback) {

        //this.access = new MWF.xApplication.TernaryLog.Access( this.restActions, this.lp );

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

        this.loadFilter();

    },
    createContainerNode: function () {
        this.createContent();
    },
    createContent: function () {

        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.contentContainerNode);

        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.middleNode);

        this.loadView();

        this.setContentSizeFun = this.setContentSize.bind(this);
        this.addEvent("resize", this.setContentSizeFun );
        this.setContentSize();

    },
    getOffsetY : function(node){
        return (node.getStyle("margin-top").toInt() || 0 ) +
            (node.getStyle("margin-bottom").toInt() || 0 ) +
            (node.getStyle("padding-top").toInt() || 0 ) +
            (node.getStyle("padding-bottom").toInt() || 0 )+
            (node.getStyle("border-top-width").toInt() || 0 ) +
            (node.getStyle("border-bottom-width").toInt() || 0 );
    },
    setContentSize: function () {
        var nodeSize = this.node.getSize();
        var h = nodeSize.y - this.getOffsetY(this.node);

        var topY = this.topContainerNode ? ( this.getOffsetY(this.topContainerNode) + this.topContainerNode.getSize().y ): 0;
        h = h - topY;

        h = h - this.getOffsetY(this.viewContainerNode);

         var pageSize = (this.view && this.view.pagingContainerBottom) ? this.view.pagingContainerBottom.getComputedSize() : {totalHeight:0};
         h = h-pageSize.totalHeight;

        // this.viewContainerNode.setStyle("height", "" + h + "px");
        this.view.viewWrapNode.setStyles({
            "height": ""+h+"px",
            "overflow": "auto"
        });
    },
    loadView: function ( filterData ) {
        if(this.view)this.view.destroy();
        this.contentNode.empty();
        var viewContainerNode = this.viewContainerNode = new Element("div.viewContainerNode", {
            "styles": this.css.viewContainerNode
        }).inject(this.contentNode);

        this.view = new MWF.xApplication.TernaryLog.Main.View(viewContainerNode, this, this, {
            templateUrl: this.path + "listItem.json",
            "pagingEnable" : true,
            "wrapView": true,
            // "scrollType": "window",
            "pagingPar" : {
                pagingBarUseWidget: true,
                position : [ "bottom" ],
                style : "blue_round",
                hasReturn : false,
                currentPage : this.options.viewPageNum,
                countPerPage : 15,
                visiblePages : 9,
                hasNextPage : true,
                hasPrevPage : true,
                hasTruningBar : true,
                hasJumper : true,
                returnText : "",
                hiddenWithDisable: false,
                text: {
                    prePage: "",
                    nextPage: "",
                    firstPage: this.lp.firstPage,
                    lastPage: this.lp.lastPage
                },
                onPostLoad: function () {
                    this.setContentSize();
                }.bind(this)
            }
        }, {
            lp: this.lp
        });
        if(filterData)this.view.filterData = filterData;
        this.view.load();
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
    loadFilter: function(){
        var lp = MWF.xApplication.TernaryLog.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles" : this.css.fileterNode
        }).inject(this.topContentNode);

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>"+ //style='width: 900px;'
            "<tr>" +
            "    <td styles='filterTableTitle' lable='person'></td>"+
            "    <td styles='filterTableValue' item='person'></td>" +
            "    <td styles='filterTableTitle' lable='module'></td>"+
            "    <td styles='filterTableValue' item='module'></td>" +
            "    <td styles='filterTableTitle' lable='operation'></td>"+
            "    <td styles='filterTableValue' item='operation'></td>" +
            "    <td styles='filterTableTitle' lable='startTime'></td>"+
            "    <td styles='filterTableValue' item='startTime' style='width: 150px;'></td>" +
            "    <td styles='filterTableTitle' lable='endTime'></td>"+
            "    <td styles='filterTableValue' item='endTime' style='width: 150px;'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "    <td styles='filterTableValue' item='reset'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.fileterNode, {}, {
                style: "attendance",
                isEdited : true,
                itemTemplate : {
                    person : {
                        "text" : lp.person,
                        "type" : "org",
                        "orgType": "identity",
                        "orgOptions": {"resultType":"person"},
                        "style" : {"min-width": "100px" },
                        "orgWidgetOptions":{"disableInfor":true}
                    },
                    module : {
                        "text" : lp.module,
                        "type" : "select",
                        "selectValue" :["","01","02","03","04","05","06","07","08","09","10","11","12"],
                        "event" : {
                            "change" : function( item, ev ){
                                var values = this.getDateSelectValue();
                                item.form.getItem( "date").resetItemOptions( values , values )
                            }.bind(this)
                        }
                    },
                    operation : { text : lp.operation,  "type" : "select", "selectValue" : function(){

                            return [];
                        }.bind(this)
                    },
                    startTime : { text: lp.startTime, "tType" : "datetime", "calendarOptions":{"secondEnable":true, "format":"db"}},
                    endTime : { text: lp.endTime, "tType" : "datetime", "calendarOptions":{"secondEnable":true, "format":"db"} },
                    action : { "value" : lp.query, type : "button", className : "filterButton", event : {
                            click : function(){
                                var result = this.form.getResult(false,null,false,true,false);
                                for(var key in result){
                                    if(!result[key]){
                                        delete result[key];
                                    }else if(key === "person" && result[key].length > 0){
                                        result[key] = result[key][0].split("@")[1];
                                    }
                                }
                                this.loadView( result );
                            }.bind(this)
                        }},
                    reset: { "value" : lp.reset, type : "button", className : "filterButtonGrey", event : {
                            click : function(){
                                this.form.reset();
                                this.loadView();
                            }.bind(this)
                        }},
                }
            }, this, this.css);
            this.form.load();
        }.bind(this), true);
    }
});

MWF.xApplication.TernaryLog.Main.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
            return new MWF.xApplication.TernaryLog.Main.Document(this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if(!count)count=15;
        if(!pageNum){
            if( this.pageNum ){
                pageNum = this.pageNum = this.pageNum+1;
            }else{
                pageNum = this.pageNum = 1;
            }
        }else{
            this.pageNum = pageNum;
        }

        var filter = this.filterData || {};
        o2.Actions.load("x_auditlog_assemble_control").AuditLogAction.listPaging( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function (documentData, all) {

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

    }

});

MWF.xApplication.TernaryLog.Main.Document = new Class({
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
    open: function(  ){

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
        result = MWF.xApplication.TernaryLog.LP.yesterday + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
        result = MWF.xApplication.TernaryLog.LP.twoDaysAgo + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (yearC > 1) {
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (monthC >= 1) {
        //result= parseInt(monthC) + "个月前";
        // s.getFullYear()+"年";
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (weekC >= 1) {
        result = parseInt(weekC) + MWF.xApplication.TernaryLog.LP.weekAgo;
    } else if (dayC >= 1) {
        result = parseInt(dayC) + MWF.xApplication.TernaryLog.LP.dayAgo;
    } else if (hourC >= 1) {
        result = parseInt(hourC) +  MWF.xApplication.TernaryLog.LP.hourAgo;
    } else if (minC >= 1) {
        result = parseInt(minC) +  MWF.xApplication.TernaryLog.LP.minuteAgo;
    } else
        result = MWF.xApplication.TernaryLog.LP.publishJustNow;
    return result;
};



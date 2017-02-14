MWF.xApplication.Forum = MWF.xApplication.Forum || {};
MWF.xApplication.ForumCategory = MWF.xApplication.ForumCategory || {};
MWF.require("MWF.widget.Identity", null, false);
MWF.xDesktop.requireApp("Forum", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Forum", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Forum", "Access", null, false);
MWF.xApplication.ForumCategory.options = {
    multitask: false,
    executable: true
}
MWF.xApplication.ForumCategory.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "ForumCategory",
        "icon": "icon.png",
        "width": "1210",
        "height": "700",
        "isResize": false,
        "isMax": true,
        "title": MWF.xApplication.ForumCategory.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.Forum.LP;
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.name;
        this.restActions = new MWF.xApplication.Forum.Actions.RestActions();

        this.path = "/x_component_ForumCategory/$Main/" + this.options.style + "/";
        this.createNode();
        this.loadApplicationContent();
    },
    loadController: function (callback) {
        this.access = new MWF.xApplication.Forum.Access( this.restActions, this.lp );
        if (callback)callback();
    },
    createNode: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": this.css.node
        }).inject(this.content);
    },
    reload : function(){
        this.status = {
            categoryId : this.options.categoryId,
            noteHidden : this.noteNodeHidden
        };
        this.openMainPage();
    },
    loadApplicationContent: function () {
        if( !this.options.categoryId && this.status && this.status.categoryId ){
            this.options.categoryId = this.status.categoryId;
        }
        this.loadController(function () {
            this.access.login( function(){
                this.loadApplicationLayout();
            }.bind(this))
        }.bind(this))
    },
    loadApplicationLayout: function () {
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);

        this.restActions.getCategory( this.options.categoryId, function (json) {
            this.data = json.data;
            this.setTitle( this.data.forumName )
            this.createTopNode();

            this.middleNode = new Element("div.middleNode", {
                "styles": this.css.middleNode
            }).inject(this.contentContainerNode);

            this.createNoteNode();
            this.createContainerNode();
        }.bind(this))

    },
    createTopNode: function () {
        var forumColor = MWF.xApplication.Forum.ForumSetting[this.options.categoryId].forumColor;

        var topNode = this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.contentContainerNode);
        topNode.setStyle( "border-bottom" , "1px solid "+forumColor );

        var topTitleLeftNode = new Element("div.topTitleLeftNode", {
            "styles": this.css.topTitleLeftNode
        }).inject(topNode);
        topTitleLeftNode.setStyle( "background-color" , forumColor )

        var topTitleMiddleNode = new Element("div.topTitleMiddleNode", {
            "styles": this.css.topTitleMiddleNode
        }).inject(topNode);
        topTitleMiddleNode.setStyle( "background-color" , forumColor )

        var topTitleRightNode = new Element("div.topTitleRightNode", {
            "styles": this.css.topTitleRightNode
        }).inject(topNode);
        topTitleRightNode.setStyle( "background-color" , forumColor )


        var topItemTitleNode = new Element("div.topItemTitleNode", {
            "styles": this.css.topItemTitleNode,
            "text": this.lp.title
        }).inject(topTitleMiddleNode);

        var topItemSepNode = new Element("div.topItemSepNode", {
            "styles": this.css.topItemSepNode
        }).inject(topTitleMiddleNode);
        topItemTitleNode.addEvent("click", function(){
            var appId = "Forum";
            if (this.desktop.apps[appId]){
                this.desktop.apps[appId].setCurrent();
            }else {
                this.desktop.openApplication(null, "Forum", { "appId": appId });
            }
            if( !this.inBrowser ){
                this.close();
            }
        }.bind(this))

        var topItemTitleNode = new Element("div.topItemTitleNode", {
            "styles": this.css.topItemTitleNode,
            "text": this.data.forumName
        }).inject(topTitleMiddleNode);

        if( this.data.forumNotice && this.data.forumNotice!="" ){
            var topRightNode = new Element("div", {
                "styles": this.css.topRightNode
            }).inject(topNode)

            topRightNode.addEvents({
                "click" :function(){
                    if( !this.noteNodeHidden ){
                        this.noteNode.setStyle("display","none");
                        this.topRightIconNode.setStyles(this.css.topRightIconDownNode);
                        this.noteNodeHidden = true;
                    }else{
                        this.noteNode.setStyle("display","");
                        this.topRightIconNode.setStyles(this.css.topRightIconNode);
                        this.noteNodeHidden = false;
                    }
                }.bind(this)
            })

            var topRightIconNode = this.topRightIconNode = new Element("div", {
                "styles": this.css.topRightIconNode
            }).inject(topRightNode)

            if( this.status && this.status.noteHidden ){
                this.topRightIconNode.setStyles(this.css.topRightIconDownNode);
                this.noteNodeHidden = true;
            }

        }

        this.searchDiv = new Element("div.searchDiv",{
            "styles" : this.css.searchDiv
        }).inject(this.topNode)
        this.searchInput = new Element("input.searchInput",{
            "styles" : this.css.searchInput,
            "value" : this.lp.searchKey,
            "title" : this.lp.searchTitle
        }).inject(this.searchDiv)
        this.searchInput.setStyles({
            "border-left" : "1px solid " + forumColor,
            "border-top" : "1px solid " + forumColor,
            "border-bottom" : "1px solid "  + forumColor,
            "border-right" : "0px"
        })
        var _self = this;
        this.searchInput.addEvents({
            "focus": function(){
                if (this.value==_self.lp.searchKey) this.set("value", "");
            },
            "blur": function(){if (!this.value) this.set("value", _self.lp.searchKey);},
            "keydown": function(e){
                if (e.code==13){
                    this.search();
                    e.preventDefault();
                }
            }.bind(this)
        });
        this.searchAction = new Element("div.searchAction",{
            "styles" : this.css.searchAction
        }).inject(this.searchDiv);
        this.searchAction.setStyles({
            "border-right" : "1px solid " + forumColor,
            "border-top" : "1px solid " + forumColor,
            "border-bottom" : "1px solid "  + forumColor,
            "border-left" : "0px"
        })
        this.searchAction.addEvents({
            "click": function(){ this.search(); }.bind(this),
            "mouseover": function(e){
                this.searchAction.setStyles( this.css.searchAction_over2 );
                e.stopPropagation();
            }.bind(this),
            "mouseout": function(){ this.searchAction.setStyles( this.css.searchAction ) }.bind(this)
        });
        this.searchDiv.addEvents( {
            "mouseover" : function(){
                this.searchInput.setStyles( this.css.searchInput_over )
                this.searchAction.setStyles( this.css.searchAction_over )
            }.bind(this),
            "mouseout" : function(){
                this.searchInput.setStyles( this.css.searchInput )
                this.searchAction.setStyles( this.css.searchAction )
            }.bind(this)
        } )


        this._createTopContent();
    },
    search : function(){
        var val = this.searchInput.get("value");
        if( val == "" || val == this.lp.searchKey ){
            this.notice( this.lp.noSearchContentNotice, "error" );
            return;
        }
        var appId = "ForumSearch";
        if (this.desktop.apps[appId] && !this.inBrowser ){
            this.desktop.apps[appId].close();
        };
        this.desktop.openApplication(null, "ForumSearch", {
            "appId": appId,
            "searchContent" : val
        });
    },
    _createTopContent: function () {

    },
    createContainerNode: function () {
        this.createCategory();
    },
    createCategory: function () {

        this.setContentSizeFun = this.setContentSize.bind(this);
        this.addEvent("resize", this.setContentSizeFun );
        this.setContentSize();

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

        this._createCategory( this.data )

    },
    createNoteNode : function(){
        if( !this.data.forumNotice || this.data.forumNotice.trim() =="" ){
            return;
        }
        var noteNode = this.noteNode = new Element("div.noteNode", {
            "styles": this.css.noteNode
        }).inject(this.middleNode);

        var noteTopNode = new Element("div.noteTopNode", {
            "styles": this.css.noteTopNode
        }).inject(noteNode);
        var noteTopContent = new Element("div.noteTopContent", {
            "styles": this.css.noteTopContent
        }).inject(noteTopNode);
        var noteIcon = new Element("div.noteIcon", {
            "styles": this.css.noteIcon
        }).inject(noteTopContent);
        var noteTopText = new Element("div.noteTopText", {
            "styles": this.css.noteTopText,
            "text" : this.lp.forumNotice
        }).inject(noteTopContent);


        var noteContent = new Element("div.noteContent", {
            "styles": this.css.noteContent,
            "html" : this.data.forumNotice
        }).inject(noteNode);

        if( this.status && this.status.noteHidden ){
            noteNode.setStyle("display" , "none");
        }
    },
    setContentSize: function () {
       //var topSize = this.topNode ? this.topNode.getSize() : {"x": 0, "y": 0};
        var topSize = {"x": 0, "y": 0};
        var nodeSize = this.node.getSize();
        var pt = this.contentContainerNode.getStyle("padding-top").toFloat();
        var pb = this.contentContainerNode.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y - topSize.y - pt - pb;
        this.contentContainerNode.setStyle("height", "" + height + "px");
    },
    _createCategory: function (d) {
        var categoryNode = new Element("div.categoryNode", {
            "styles": this.css.categoryNode
        }).inject(this.contentNode);

        //var categoryTopNode = new Element("div.categoryTopNode", {
        //    "styles": this.css.categoryTopNode
        //}).inject(categoryNode);
        //categoryTopNode.setStyle( "border-bottom" , "1px solid "+ d.forumColor || this.lp.defaultForumColor );
        //
        //var categoryTopTitleNode = new Element("div.categoryTopTitleNode", {
        //    "styles": this.css.categoryTopTitleNode,
        //    "text": d.forumName
        //}).inject(categoryTopNode);
        //categoryTopTitleNode.setStyle( "background-color" , d.forumColor || this.lp.defaultForumColor );
        //
        //var categoryTopRightNode = new Element("div.categoryTopRightNode", {
        //    "styles": this.css.categoryTopRightNode,
        //    "text": this.lp.categoryManager + "：" + d.forumManagerName
        //}).inject(categoryTopNode);
        if (d.indexListStyle == "经典") {
            var view = new MWF.xApplication.ForumCategory.Main.ListView(categoryNode, this, this, {
                templateUrl: this.path + "listItemList.json",
                categoryId: d.id
            }, {
                lp: this.lp
            })
            view.load();
        } else if (d.indexListStyle == "图片矩形") {
            var view = new MWF.xApplication.ForumCategory.Main.ImageView(categoryNode, this, this, {
                templateUrl: this.path + "listItemImage.json",
                categoryId: d.id
            }, {
                lp: this.lp
            })
            view.load();
        } else {
            var view = new MWF.xApplication.ForumCategory.Main.TileView(categoryNode, this, this, {
                templateUrl: this.path + "listItemTile.json",
                categoryId: d.id
            }, {
                lp: this.lp
            })
            view.load();
        }
    },
    clearContent: function () {
        if (this.explorer)this.explorer.destroy();
        if(this.setContentSizeFun)this.removeEvent("resize", this.setContentSizeFun );
        if(this.scrollBar && this.scrollBar.scrollVAreaNode)this.scrollBar.scrollVAreaNode.destroy();
        if( this.scrollBar )delete this.scrollBar;
        if (this.contentContainerNode) {
            this.contentContainerNode.destroy();
            //this.middleNode.destroy();
            //this.contentNode.destroy();
        }
    },
    openMainPage : function(){
        this.clearContent();
        //this.createCategory();
        this.loadApplicationLayout();
    },
    recordStatus: function () {
        return {
            categoryId : this.options.categoryId,
            noteHidden : this.noteNodeHidden
        };
    },
    openPerson : function( userName ){
        var appId = "ForumPerson"+userName;
        if (this.desktop.apps[userName]){
            this.desktop.apps[userName].setCurrent();
        }else {
            this.desktop.openApplication(null, "ForumPerson", {
                "personName" : userName,
                "appId": appId
            });
        }
    },
    createPersonNode : function( container, personName ){
        var persons = personName.split(",");
        persons.each( function(userName, i){
            var span = new Element("span", {
                "text" : userName,
                "styles" : this.css.person
            }).inject(container);
            span.addEvents( {
                mouseover : function(){ this.node.setStyles( this.obj.css.person_over )}.bind( {node:span, obj:this} ),
                mouseout : function(){ this.node.setStyles( this.obj.css.person )}.bind( {node:span, obj:this} ),
                click : function(){ this.obj.openPerson( this.userName ) }.bind( {userName:userName, obj:this} )
            })
            if( i != persons.length - 1 ){
                new Element("span", {
                    "text" : ",",
                }).inject(container);
            }
        }.bind(this))
    }
});



MWF.xApplication.ForumCategory.Main.TileView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.ForumCategory.Main.TileDocument(this.viewNode, data, this.explorer, this, null, index);
    },

    _getCurrentPageData: function (callback, count) {
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.filterData || {};
        this.actions.listSection(this.options.categoryId, function (json) {
            if( !json.data )json.data = [];
            if (callback)callback(json);
        }.bind(this))
    },
    _removeDocument: function (documentData, all) {
        //this.actions.deleteSchedule(documentData.id, function(json){
        //    this.reload();
        //    this.app.notice(this.app.lp.deleteDocumentOK, "success");
        //}.bind(this));
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

})

MWF.xApplication.ForumCategory.Main.TileDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument: function () {
        //this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode_over"]);
        //this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode_over"]);
        //this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode_over"]);
    },
    mouseoutDocument: function () {
        //this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode"]);
        //this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode"]);
        //this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode"]);
    },
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {
        var personNode = itemNode.getElements("[item='moderatorNames']")[0];
        this.app.createPersonNode( personNode, itemData.moderatorNames )

        if( (this.index + 1) % 4 == 0 ){
            itemNode.setStyle("margin-right" , "0px" );
        }
        var listNode = itemNode.getElements("[styles='documentItemListNode']")[0];
        if (listNode) {
            this._getListData(function (json) {
                json.data.each(function (d,i) {
                    var div = new Element("div", {
                        "styles": this.css.documentItemListItemNode,
                        "text": d.title,
                        "title": d.title
                    }).inject(listNode)
                    div.addEvents({
                        "mouseover": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode_over)
                        }.bind({node: div, obj: this}),
                        "mouseout": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode)
                        }.bind({node: div, obj: this}),
                        "click" : function(){
                            var appId = "ForumDocument"+this.da.id;
                            if (this.obj.app.desktop.apps[appId]){
                                this.obj.app.desktop.apps[appId].setCurrent();
                            }else {
                                this.obj.app.desktop.openApplication(null, "ForumDocument", {
                                    "sectionId" : this.da.sectionId,
                                    "id" : this.da.id,
                                    "appId": appId,
                                    "isEdited" : false,
                                    "isNew" : false,
                                    "index" : i
                                });
                            }
                        }.bind({da: d, obj: this})
                    })
                }.bind(this))
            }.bind(this), 6)
        }
    },
    _getListData: function (callback, count) {
        if (!count)count = 6;
        var filterData = {
            "sectionId": this.data.id
        }
        this.actions.listSubjectFilterPage(1, count, filterData, function (json) {
            if (!json.data)json.data = [];
            if (callback)callback(json);
        }.bind(this))
    },
    removeCenterWork: function (itemData) {
        //if(isAdmin){
        //    return true;
        //}
        return false;
    },
    openSection : function( el ){
        var appId = "ForumSection"+ this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(el, "ForumSection", {
                "sectionId": this.data.id,
                "appId": appId
            });
        }
    }
})


MWF.xApplication.ForumCategory.Main.ListView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.ForumCategory.Main.ListDocument(this.viewNode, data, this.explorer, this, null, index);
    },

    _getCurrentPageData: function (callback, count) {
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.filterData || {};
        this.actions.listSection(this.options.categoryId, function (json) {
            if( !json.data )json.data = [];
            if (callback)callback(json);
        }.bind(this))
    },
    _removeDocument: function (documentData, all) {
        //this.actions.deleteSchedule(documentData.id, function(json){
        //    this.reload();
        //    this.app.notice(this.app.lp.deleteDocumentOK, "success");
        //}.bind(this));
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

})

MWF.xApplication.ForumCategory.Main.ListDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument: function () {
        //this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode_over"]);
        //this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode_over"]);
        //this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode_over"]);
    },
    mouseoutDocument: function () {
        //this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode"]);
        //this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode"]);
        //this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode"]);
    },
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {
        var personNode = itemNode.getElements("[item='moderatorNames']")[0];
        this.app.createPersonNode( personNode, itemData.moderatorNames )

        var listNode = itemNode.getElements("[styles='documentItemListNode_list']")[0];
        var replyListNode = itemNode.getElements("[styles='documentItemReplyListNode_list']")[0];
        if (listNode) {
            this._getListData(function (json) {
                json.data.each(function (d,i) {
                    var div = new Element("div", {
                        "styles": this.css.documentItemListItemNode_list,
                        "text": d.title,
                        "title": d.title
                    }).inject(listNode)
                    div.addEvents({
                        "mouseover": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode_list_over)
                        }.bind({node: div, obj: this}),
                        "mouseout": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode_list)
                        }.bind({node: div, obj: this}),
                        "click" : function(){
                            var appId = "ForumDocument"+this.da.id;
                            if (this.obj.app.desktop.apps[appId]){
                                this.obj.app.desktop.apps[appId].setCurrent();
                            }else {
                                this.obj.app.desktop.openApplication(null, "ForumDocument", {
                                    "sectionId" : this.da.sectionId,
                                    "id" : this.da.id,
                                    "appId": appId,
                                    "isEdited" : false,
                                    "isNew" : false,
                                    "index" : i
                                });
                            }
                        }.bind({da: d, obj: this})
                    })

                    var replyNode = new Element("div", {
                        "styles": this.css.documentItemReplyListItemNode_list
                    }).inject(replyListNode)

                    var div = new Element("div", {
                        "styles": this.css.documentItemReplyTimeNode_list,
                        "text": this.getDateDiff(d.updateTime),
                        "title": d.updateTime
                    }).inject(replyNode)

                    var div = new Element("div", {
                        "styles": this.css.documentItemReplyPersonNode_list,
                        "text": d.creatorName
                    }).inject(replyNode)
                    div.addEvents({
                        "mouseover": function () {
                            this.node.setStyles(this.obj.css.documentItemReplyPersonNode_list_over)
                        }.bind({node: div, obj: this}),
                        "mouseout": function () {
                            this.node.setStyles(this.obj.css.documentItemReplyPersonNode_list)
                        }.bind({node: div, obj: this}),
                        "click" : function(){
                            this.obj.app.openPerson( this.userName );
                        }.bind( {userName : d.creatorName, obj:this} )
                    })
                }.bind(this))
            }.bind(this), 6)
        }


    },
    _getListData: function (callback, count) {
        if (!count)count = 6;
        var filterData = {
            "sectionId": this.data.id
        }
        this.actions.listSubjectFilterPage(1, count, filterData, function (json) {
            if (!json.data)json.data = [];
            if (callback)callback(json);
        }.bind(this))
    },
    getDateDiff: function (publishTime) {
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
            result = dateTimeStamp.getFullYear() + "年" + (dateTimeStamp.getMonth() + 1) + "月" + dateTimeStamp.getDate() + "日";
        } else if (monthC >= 1) {
            //result= parseInt(monthC) + "个月前";
            // s.getFullYear()+"年";
            result = (dateTimeStamp.getMonth() + 1) + "月" + dateTimeStamp.getDate() + "日";
        } else if (weekC >= 1) {
            result = parseInt(weekC) + "周前";
        } else if (dayC >= 1) {
            result = parseInt(dayC) + "天前";
        } else if (hourC >= 1) {
            result = parseInt(hourC) + "小时前";
        } else if (minC >= 1) {
            result = parseInt(minC) + "分钟前";
        } else
            result = "刚刚发表";
        return result;
    },
    removeCenterWork: function (itemData) {
        //if(isAdmin){
        //    return true;
        //}
        return false;
    },
    openSection : function( el ){
        var appId = "ForumSection"+ this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(el, "ForumSection", {
                "sectionId": this.data.id,
                "appId": appId
            });
        }
    }
})


MWF.xApplication.ForumCategory.Main.ImageView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.ForumCategory.Main.ImageDocument(this.viewNode, data, this.explorer, this, null, index);
    },

    _getCurrentPageData: function (callback, count) {
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.filterData || {};
        this.actions.listSection(this.options.categoryId, function (json) {
            if( !json.data )json.data = [];
            if (callback)callback(json);
        }.bind(this))
    },
    _removeDocument: function (documentData, all) {
        //this.actions.deleteSchedule(documentData.id, function(json){
        //    this.reload();
        //    this.app.notice(this.app.lp.deleteDocumentOK, "success");
        //}.bind(this));
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

})

MWF.xApplication.ForumCategory.Main.ImageDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument: function () {
        //this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode_over"]);
        //this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode_over"]);
        //this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode_over"]);
    },
    mouseoutDocument: function () {
        //this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode"]);
        //this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode"]);
        //this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode"]);
    },
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {
        var personNode = itemNode.getElements("[item='moderatorNames']")[0];
        this.app.createPersonNode( personNode, itemData.moderatorNames )

        var _self = this;
        if( (this.index + 1) % 2 == 0 ){
            itemNode.setStyle("margin-right" , "0px" );
        }

        var imageNode = itemNode.getElements("[styles='documentItemLeftImage']")[0];
        var filterData = {
            "sectionId": this.data.id,
            "needPicture" : true
        }
        this.actions.listSubjectFilterPage(1, 1,  filterData, function( json ){
            if( json.data ){
                var d = json.data[0];
                this.node.set("title", d.title);
                this.node.set("src", d.pictureUrl );
                this.node.addEvents({
                    "click": function () {
                        var appId = "ForumDocument" + this.da.id;
                        if (_self.app.desktop.apps[appId]) {
                            _self.app.desktop.apps[appId].setCurrent();
                        } else {
                            _self.app.desktop.openApplication(null, "ForumDocument", {
                                "sectionId": this.da.sectionId,
                                "id": this.da.id,
                                "appId": appId,
                                "isEdited": false,
                                "isNew": false
                            });
                        }
                    }.bind({da: d})
                })
            }
        }.bind({ node : imageNode }))

        var listNode = itemNode.getElements("[styles='documentItemListNode']")[0];
        if (listNode) {
            this._getListData(function (json) {
                json.data.each(function (d,i) {
                    var div = new Element("div", {
                        "styles": this.css.documentItemListItemNode,
                        "text": d.title,
                        "title": d.title
                    }).inject(listNode)
                    div.addEvents({
                        "mouseover": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode_over)
                        }.bind({node: div, obj: this}),
                        "mouseout": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode)
                        }.bind({node: div, obj: this}),
                        "click" : function(){
                            var appId = "ForumDocument"+this.da.id;
                            if (this.obj.app.desktop.apps[appId]){
                                this.obj.app.desktop.apps[appId].setCurrent();
                            }else {
                                this.obj.app.desktop.openApplication(null, "ForumDocument", {
                                    "sectionId" : this.da.sectionId,
                                    "id" : this.da.id,
                                    "appId": appId,
                                    "isEdited" : false,
                                    "isNew" : false,
                                    "index" : i
                                });
                            }
                        }.bind({da: d, obj: this})
                    })
                }.bind(this))
            }.bind(this), 6)
        }


    },
    _getListData: function (callback, count) {
        if (!count)count = 6;
        var filterData = {
            "sectionId": this.data.id
        }
        this.actions.listSubjectFilterPage(1, count, filterData, function (json) {
            if (!json.data)json.data = [];
            if (callback)callback(json);
        }.bind(this))
    },
    removeCenterWork: function (itemData) {
        //if(isAdmin){
        //    return true;
        //}
        return false;
    },
    openSection : function( el ){
        var appId = "ForumSection"+ this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(el, "ForumSection", {
                "sectionId": this.data.id,
                "appId": appId
            });
        }
    }
})



MWF.xApplication.cms.Xform.widget = MWF.xApplication.cms.Xform.widget || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
var O2CMSComment = MWF.xApplication.cms.Xform.widget.Comment = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "simple",
        "documentId" : "",
        "anonymousAccess" : false,
        "countPerPage" : 10,
        "isAllowPublish" : true
    },
    initialize: function (app, node, options) {
        this.setOptions(options);
        this.app = app;
        this.node = node;
        this.path = "../x_component_cms_Xform/widget/$Comment/";
        this.cssPath = "../x_component_cms_Xform/widget/$Comment/" + this.options.style + "/css.wcss";
        this._loadCss();

        MWF.xDesktop.requireApp("cms.Xform", "lp."+MWF.language, null, false);
        this.lp = MWF.xApplication.cms.Xform.LP;

        this.actions = MWF.Actions.get("x_cms_assemble_control");

    },
    load : function(){
        this.items = [];
        this.documents = {};
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
        this.count = 0;
        this.lineHeight = this.options.mode != "text" ? 32 : 25;

        var countPerPage = this.options.countPerPage;
        if( !countPerPage || countPerPage==0 || isNaN(countPerPage) || parseInt(countPerPage) < 1 ){
            this.options.countPerPage = 10;
        }


        this.container = new Element("div",{styles:this.css.container}).inject( this.node );
        this.loadTitle();
        this.loadContent();
        //this.loadElementList();
        //this.loadBottom();
        if( this.options.isAllowPublish !== false ){
            this.loadEditor();
        }
    },
    loadTitle : function(){
        this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.lp.commentTitle}).inject(this.container);
    },
    loadTotal: function(){
        this.titleCountNode = new Element("div", {
            styles : this.css.titleCountNode,
            text : this.lp.commentCountText.replace("{count}" , this.dataCount)
        }).inject(this.titleNode);
    },
    loadContent : function(){

        this.contentConainer = new Element("div.contentConainer",{
            "styles" : this.css.contentConainer
        }).inject( this.container );

        this.createPagingBar();

        this.viewConainer = new Element("div.viewConainer",{
            //"styles" : this.css.viewConainer
        }).inject( this.contentConainer );

        this.createPagingBar();

        this.createView();
    },
    createPagingBar: function(){
        var pagingArea = new Element("div",{
            styles : this.css.pagingArea
        }).inject(this.contentConainer);
        if( this.pagingBarTop ){
            this.pagingBarBottom = pagingArea;
        }else{
            this.pagingBarTop = pagingArea;
        }

        var pagingContainer = new Element("div").inject(pagingArea);
        if( this.pagingContainerTop ){
            this.pagingContainerBottom = pagingContainer;
        }else{
            this.pagingContainerTop = pagingContainer;
        }
    },
    //loadBottom: function(){
    //   var bottomNode = new Element("div",{
    //        "styles" : this.css.bottomNode
    //    }).inject( this.container );
    //    var resizeNode = new Element("div",{
    //        "styles" : this.css.bottomResizeNode,
    //        "text" : "◢"
    //    }).inject(bottomNode);
    //
    //    var xLimit = this.contentScrollNode.getSize().x;
    //
    //   this.contentScrollNode.makeResizable({
    //        "handle": resizeNode,
    //        "limit": {x:[xLimit, xLimit], y:[50, null]},
    //        "onDrag": function(){
    //            var y = this.contentScrollNode.getSize().y;
    //            if( y > ( this.lineHeight * this.countPerPage - 20 ) ){
    //                this.countPerPage = parseInt( y / this.lineHeight ) + 2
    //            }
    //            this.contentScrollNode.fireEvent("resize");
    //        }.bind(this),
    //        "onComplete": function(){
    //            this.scrollBar.checkScroll();
    //            this.loadElementList();
    //        }.bind(this)
    //    });
    //},
    getShortName : function( dn ){
        if( dn && dn.contains("@") ){
            return dn.split("@")[0];
        }else{
            return dn;
        }
    },
    createView : function( ){

        this.view = new O2CMSComment.View( this.viewConainer, null, this, {
            templateUrl : this.path + this.options.style + "/listItem.json",
            scrollEnable : false,
            pagingEnable : true,
            documentKeyWord : "orderNumber",
            pagingPar : {
                currentPage : 1,
                currentItem : 1,
                hasReturn : false ,
                countPerPage : this.options.countPerPage,
                onPostLoad : function( pagingBar ){
                    if(pagingBar.nextPageNode){
                        pagingBar.nextPageNode.inject( this.pagingBarBottom, "before" );
                    }
                }.bind(this)
            },
            onGotoItem : function( top ){
                //var t = top; // - this.content.getTop();
                //this.contentContainerNode.scrollTo( 0, t );
            }.bind(this),
            onPostCreateViewBody : function(){
                if( this.view.dataCount <= this.options.countPerPage ){
                    this.pagingBarTop.hide();
                    this.pagingBarBottom.hide();
                }else{
                    this.pagingBarTop.show();
                    this.pagingBarBottom.show();
                }
                if( !this.view.dataCount ){
                    this.view.node.hide()
                }else{
                    this.view.node.show();
                }
            }.bind(this),
            onPostDeleteDocument : function(){
                this.view.reload();
            }.bind(this)
        }, {
            app : this.app
        });
        this.view.pagingContainerTop = this.pagingContainerTop;
        this.view.pagingContainerBottom = this.pagingContainerBottom;
        //this.view.data = this.data;
        this.view.filterData = { "documentId" : this.options.documentId, "orderField" : "createTime", "orderType" : "ASC" };
        this.view.load();
    },
    loadEditor : function( ){
        this.editorArea = new Element("div.editorArea",{
            "styles" : this.css.commentArea
        }).inject( this.container );

        this.editor = new O2CMSComment.Editor( this.editorArea, this, {
            style : this.options.style,
            isNew : true,
            onPostOk : function( id ){
                this.postCreateComment( id )
            }.bind(this)
        } );
        this.editor.mainData = this.data;
        this.editor.load();
    },
    postCreateComment : function(){
        this.view.reload();
    },
    isAnonymous : function(){
        return this.options.anonymousAccess;
    },
    getUserIcon: function( name ){
        var icon = "";
        //var url = MWF.Actions.get("x_organization_assemble_personal").getIcon(name);
        //if( url ){
        //    icon = url;
        //}else{
        //    icon = "../x_component_ForumDocument/$Main/default/icon/noavatar_big.png";
        //}
        //return icon;
        this.getUserData( name, function( json ){
            if( json && json.data && json.data.icon ){
                icon = json.data.icon;
            }else{
                icon = "../x_component_cms_Xform/widget/$Comment/"+this.options.style+"/icon/noavatar_big.png";
            }
        });
        return icon;
    },
    getUserData : function( name, callback ){
        if( this.userCache && this.userCache[name] ){
            if( callback )callback( this.userCache[name] );
            return
        }
        if( !this.userCache )this.userCache = {};
        if( this.isAnonymous() ){
            var url = MWF.Actions.get("x_organization_assemble_personal").getIcon(name);
            if( url ){
                var json =  { data : { icon : url } };
                this.userCache[ name ] = json;
                if( callback )callback( json );
            }else{
                var json =  { data : { icon : "../x_component_cms_Xform/widget/$Comment/"+this.options.style+"/icon/noavatar_big.png" } };
                this.userCache[ name ] = json;
                if( callback )callback( json );
            }
        }else{
            MWF.Actions.get("x_organization_assemble_control").getPerson( function( json ){
                if( !json.data )json.data = {};
                var url = MWF.Actions.get("x_organization_assemble_personal").getIcon(name);
                if( url ){
                    if( json.data ){
                        json.data.icon = url;
                        this.userCache[ name ] = json;
                        if( callback )callback( json );
                    }
                }else{
                    if( json.data ){
                        json.data.icon = "../x_component_cms_Xform/widget/$Comment/"+this.options.style+"/icon/noavatar_big.png";
                        this.userCache[ name ] = json;
                        if( callback )callback( json );
                    }
                }
                //MWF.Actions.get("x_organization_assemble_control").getPersonIcon(name, function(url){
                //	if( json.data ){
                //		json.data.icon = url;
                //		this.userCache[ name ] = json;
                //		if( callback )callback( json );
                //	}
                //}.bind(this), function(){
                //	if( json.data ){
                //		json.data.icon = "../x_component_ForumDocument/$Main/"+this.options.style+"/icon/noavatar_big.png";
                //		this.userCache[ name ] = json;
                //		if( callback )callback( json );
                //	}
                //}.bind(this));
            }.bind(this), function(){
                var json =  { data : { icon : "../x_component_cms_Xform/widget/$Comment/"+this.options.style+"/icon/noavatar_big.png" } };
                this.userCache[ name ] = json;
                if( callback )callback( json );
            }.bind(this), name, true )
        }
    }
});

O2CMSComment.getDateDiff = function (publishTime ) {
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
        result =  MWF.xApplication.cms.Xform.LP.yesterday + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
        result = MWF.xApplication.cms.Xform.LP.theDayBeforeYesterday + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (yearC > 1) {
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (monthC >= 1) {
        //result= parseInt(monthC) + "个月前";
        // s.getFullYear()+"年";
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (weekC >= 1) {
        result = MWF.xApplication.cms.Xform.LP.severalWeekAgo.replace("{count}", parseInt(weekC));
    } else if (dayC >= 1) {
        result = MWF.xApplication.cms.Xform.LP.severalDayAgo.replace("{count}", parseInt(dayC));
    } else if (hourC >= 1) {
        result = MWF.xApplication.cms.Xform.LP.severalHourAgo.replace("{count}", parseInt(hourC));
    } else if (minC >= 1) {
        result = MWF.xApplication.cms.Xform.LP.severalMintuesAgo.replace("{count}", parseInt(minC));
    } else
        result = MWF.xApplication.cms.Xform.LP.justNow;
    return result;
};

O2CMSComment.Editor = new Class({
    Implements: [Options , Events],
    options: {
        "style": "default",
        "isNew" : true
    },
    initialize: function(node, comment, options){
        this.setOptions(options);
        this.node = node;
        this.comment = comment;
    },
    load: function(){
        this.comment.actions.getUUID( function( id ){
            this.advanceCommentId = id;
            this._load();
        }.bind(this))
    },
    _load: function(){
        var html = "<div styles='itemNode'>" +
            " <div styles='itemLeftNode'>" +
            "   <div styles='itemUserFace'>" +
            "     <div styles='itemUserIcon' item='userIcon'>" +
            "     </div>" +
            "   </div>" +
            "   <div styles='commentUserName' item='creatorName'>" +
            "   </div>" +
            " </div>" +
            " <div styles='commentRightNode'>" +
            "   <div styles='itemRightMidle'>" +
            "     <div styles='itemBodyComment' item='content'></div>" +
            "     <div styles='itemBodyComment' item='action'></div>" +
            "   </div>" +
            " </div>" +
            "</div>";
        this.node.set("html", html);

        var actionTd = this.node.getElements("[item='action']")[0];
        this.saveCommentAction = new Element("div",{
            styles : this.comment.css.actionNode,
            text: this.comment.lp.saveComment
        }).inject(actionTd);
        this.saveCommentAction.addEvent("click",function(){
            this.saveComment();
        }.bind(this));

        var rtfConfig = {
            //skin : "bootstrapck",
            "resize_enabled": false,
            isSetImageMaxWidth : true,
            reference : this.advanceCommentId,
            referenceType: "forumReply",
            //uiColor : '#9AB8F3',
            //toolbarCanCollapse : true,
            toolbar : [
                //{ name: 'document', items : [ 'Preview' ] },
                //{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
                //{ name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','-','RemoveFormat' ] },
                //{ name: 'paragraph', items : [ 'JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] },
                //{ name: 'styles', items : [ 'Font','FontSize' ] },
                //{ name: 'colors', items : [ 'TextColor','BGColor' ] },
                //{ name: 'links', items : [ 'Link','Unlink' ] },
                { name: 'insert', items : [ 'Image' ] }
                //{ name: 'tools', items : [ 'Maximize','-','About' ] }
            ]
        };
        if( this.comment.options.editorProperties ){
            rtfConfig = Object.merge( rtfConfig, this.comment.options.editorProperties )
        }
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.node, this.data || {}, {
                style: "forum",
                isEdited:  true,
                itemTemplate: {
                    userIcon: { className : "itemUserIcon2", type : "img", value : function(){
                        if( layout.session.user.icon ){
                            return "data:image/png;base64," + layout.session.user.icon
                        }else{
                            return "../x_component_cms_Xform/widget/$Comment/"+this.options.style+"/icon/noavatar_big.png"
                        }
                    }.bind(this)},
                    creatorName: { type : "innerText", value : layout.session.user.name },
                    content: { type : "rtf", RTFConfig : rtfConfig }
                }
            }, this, this.comment.css);
            this.form.load();
        }.bind(this), true);


    },
    saveComment : function(){
        var data = this.form.getResult(true, ",", true, false, true);
        if (data) {
            data.documentId = this.comment.options.documentId;
            data.id = this.advanceCommentId;
            delete data.userIcon;
            this.comment.actions.saveComment(data, function (json) {
                if (json.type == "error") {
                    this.comment.app.notice(json.message, "error");
                } else {
                    this.comment.actions.getUUID( function( id ){
                        this.advanceCommentId = id;
                    }.bind(this));
                    this.comment.app.notice( this.comment.lp.saveCommentSuccess, "ok" );
                    this.form.getItem("content").setValue("");
                    this.fireEvent("postOk", json.data.id);
                }
            }.bind(this))
        }
    }
});

O2CMSComment.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data, index){
        data.index = index;
        return  new O2CMSComment.Document(this.viewNode, data, this.explorer, this, null, data.index );
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if(!count)count=10;
        if(!pageNum)pageNum = 1;
        //page, count,  filterData, success,failure, async
        //if( !this.page ){
        //	this.page = 1;
        //}else{
        //	this.page ++;
        //}
        //var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var filter = this.filterData || {};
        this.actions.listCommentPageWithFilter( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteComment( documentData.id, function(){
            this.reload();
            this.app.notice( this.lp.deleteCommentSuccess, "ok")
        }.bind(this) )
    },
    _create: function(){

    },
    _queryCreateViewNode: function(){
    },
    _postCreateViewNode: function( viewNode ){
    },
    _queryCreateViewHead:function(){
    },
    _postCreateViewHead: function( headNode ){
    }

});

O2CMSComment.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverSubject : function(subjectNode, ev){
        //var removeNode = sectionNode.getElements("[styles='sectionRemoveNode']")[0];
        //if( removeNode )removeNode.setStyle("opacity",1)
    },
    mouseoutSubject : function(subjectNode, ev){
        //var removeNode = sectionNode.getElements("[styles='sectionRemoveNode']")[0];
        //if( removeNode )removeNode.setStyle("opacity",0)
    },
    getUserData : function( name, callback ){
        this.view.explorer.getUserData( name, callback );
    },
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        var userIcon = itemNode.getElements( "[item='userIcon']" )[0];
        //var signatureContainer = itemNode.getElements("[item='signatureContainer']")[0];
        this.getUserData( itemData.creatorName, function(json ){
            userIcon.src = json.data.icon;
            //if( json.data.signature && json.data.signature!="" ){
            //    var signatureNode = signatureContainer.getElements("[item='signature']")[0];
            //    signatureNode.set("text", json.data.signature )
            //}else{
            //    signatureContainer.destroy();
            //}
        }.bind(this) );


        if( itemData.parentId && itemData.parentId != "" ){
            var quoteContainer = itemNode.getElements( "[item='quoteContent']" )[0];
            this.actions.getComment( itemData.parentId, function( json ){
                    var data = this.parentData =  json.data;
                    var quoteContent = new Element("div", {  "styles" : this.css.itemQuote }).inject(quoteContainer);
                    var content = quoteContent.set("html", data.content).get("text");
                    quoteContent.empty();
                    data.contentText = content;

                    new Element( "div", {styles : this.css.quoteLeftBig} ).inject( quoteContent );

                    var quoteArea = new Element( "div", {styles : this.css.quoteAreaBig } ).inject( quoteContent );
                    var quoteInfor = new Element( "div", {
                        styles : this.css.quoteInforBig,
                        text : data.orderNumber + this.lp.floor + "：" + data.creatorName.split('@')[0] + this.lp.publishAt + data.createTime
                    }).inject( quoteArea );
                    quoteInfor.addEvent("click", function(){
                        this.obj.app.gotoComment( this.index )
                    }.bind({obj : this, index : data.orderNumber || (data.index + 2) }));
                    new Element( "div", {
                        styles : this.css.quoteTextBig,
                        text :  content.length > 100 ? (content.substr(0, 100) + "...") : content
                    }).inject( quoteArea );

                    new Element( "div", {styles : this.css.quoteRightBig} ).inject( quoteContent );
                }.bind(this) , function( json ){
                    new Element( "div" , {
                        "styles" : this.css.commentBeinngDelete,
                        "text" : this.lp.quoteCommentBeingDeleted
                    }).inject(quoteContainer)
                }.bind(this)
            )
        }

    },
    sendMessage : function(itemNode, ev ){
        var self = this;
        if (layout.desktop.widgets["IMIMWidget"]) {
            var IM = layout.desktop.widgets["IMIMWidget"];
            IM.getOwner(function(){
                this.openChat(ev, {
                    from : self.data.creatorName
                });
            }.bind(IM));
        }
    },
    createComment : function(itemNode, ev ){ // 对回复进行回复
        if( this.app.access.isAnonymousDynamic() ){
            this.app.openLoginForm( function(){ this.app.reload() }.bind(this) );
        }else{
            var form = new O2CMSComment.CommentForm(this, {}, {
                toMain: false,
                onPostOk: function (id) {
                    this.app.postCreateComment(id)
                }.bind(this)
            });
            this.data.contentText = this.node.getElements("[item='content']")[0].get("text");
            form.mainData = this.app.data;
            form.parentData = this.data;
            form.create()
        }
    },
    editComment : function(itemNode, ev ){	//编辑当前回复
        var form = new O2CMSComment.Form(this, this.data, {
            documentId : this.explorer.options.documentId,
            toMain : (this.data.parentId && this.data.parentId!="") ? false : true,
            onPostOk : function( id ){
                MWF.Actions.get("x_cms_assemble_control").getComment( id, function( json ){
                    var content = this.node.getElements("[item='content']")[0];
                    content.set( "html", json.data.content );
                }.bind(this))
            }.bind(this)
        });
        //form.parentData = this.parentData;
        form.edit()
    },
    deleteComment : function( itemNode, ev ){
        var _self = this;
        this.view.app.confirm("warn", ev, this.lp.deleteCommentTitle, this.lp.deleteCommentText, 350, 120, function(){
            //_self.view._removeDocument(_self.data, false);
            _self.actions.deleteComment( _self.data.id, function(){
                _self.destroy();
                //_self.app.adjustCommentCount( -1 );
                _self.app.notice( _self.lp.deleteCommentSuccess, "ok");
                _self.view.fireEvent("postDeleteDocument");
            }.bind(this) );
            this.close();
        }, function(){
            this.close();
        });
    }
});

O2CMSComment.Form = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "cms_xform",
        "width": "860",
        "height": "400",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": true,
        // "title": MWF.xApplication.cms.Xform.LP.commentFormTitle,
        "draggable": true,
        "closeAction": true,
        "toMain" : true
    },
    _createTableContent: function(){
        this.lp = MWF.xApplication.cms.Xform.LP;
        if( this.formTopTextNode )this.formTopTextNode.set("text", this.lp.commentFormTitle);
        if( this.isNew ){
            MWF.Actions.get("x_cms_assemble_control").getUUID( function(id){
                this.advanceCommentId = id;
                this._createTableContent_();
            }.bind(this) )
        }else{
            this._createTableContent_()
        }
    },
    _createTableContent_: function () {
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            //"<tr>" +
            //"   <td styles='formTableValue14' item='mainSubject'></td>" +
            //"</tr>
            //"<tr>" +
            //"   <td styles='formTableValue' item='mainContent'></td>" +
            //"</tr>" +
            "<tr>" +
            "   <td styles='formTableValue' item='content'></td>" +
            "</tr>"+
            "</table>";
        this.formTableArea.set("html", html);

        //if( !this.options.toMain && this.parentData ){
        //    var mainContentEl = this.formTableArea.getElements("[item='mainContent']")[0];
        //
        //    var quoteTop = new Element( "div", {styles : this.css.quoteTop} ).inject( mainContentEl );
        //    new Element( "div", {styles : this.css.quoteLeft} ).inject( quoteTop );
        //    new Element( "div", {
        //        styles : this.css.quoteInfor,
        //        text : this.parentData.creatorName.split("@")[0] + this.lp.publishAt + this.parentData.createTime
        //    }).inject( quoteTop );
        //
        //    var quoteBottom = new Element( "div", {styles : this.css.quoteBottom} ).inject( mainContentEl );
        //    var text = this.parentData.contentText;
        //    new Element( "div", {
        //        styles : this.css.quoteText,
        //        text :  text.length > 50 ? (text.substr(0, 50) + "...") : text
        //    }).inject( quoteBottom );
        //    new Element( "div", {styles : this.css.quoteRight} ).inject( quoteBottom );
        //}
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "cms",
                isEdited: true,
                itemTemplate: {
                    //mainSubject: { type: "innertext", defaultValue : "RE:" + this.mainData.title },
                    content: { type : "rtf", RTFConfig : {
                        //skin : "bootstrapck",
                        "resize_enabled": false,
                        isSetImageMaxWidth : true,
                        reference : this.advanceCommentId || this.data.id,
                        referenceType: "forumReply",
                        toolbar : [
                            //{ name: 'document', items : [ 'Preview' ] },
                            ////{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
                            //{ name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','-','RemoveFormat' ] },
                            ////{ name: 'paragraph', items : [ 'JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] },
                            //{ name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
                            //{ name: 'colors', items : [ 'TextColor','BGColor' ] },
                            //{ name: 'links', items : [ 'Link','Unlink' ] },
                            { name: 'insert', items : [ 'Image' ] }
                            //{ name: 'tools', items : [ 'Maximize','-','About' ] }
                        ]
                    }}
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },
    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

        if (this.isNew || this.isEdited) {
            this.okActionNode = new Element("div.formOkActionNode", {
                "styles": this.css.formOkActionNode,
                "text": this.lp.ok
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.ok(e);
            }.bind(this));
        }


    },
    ok: function (e) {
        this.fireEvent("queryOk");
        var data = this.form.getResult(true, ",", true, false, true);
        if (data) {
            this._ok(data, function (json) {
                if (json.type == "error") {
                    this.app.notice(json.message, "error");
                } else {
                    if(this.formMaskNode)this.formMaskNode.destroy();
                    this.formAreaNode.destroy();
                    this.app.notice(this.isNew ? this.lp.createCommentSuccess : this.lp.updateSuccess, "success");
                    this.fireEvent("postOk", json.data.id);
                }
            }.bind(this))
        }
    },
    _ok: function (data, callback) {
        data.documentId = this.options.documentId ;
        if( this.advanceCommentId )data.id = this.advanceCommentId;
        delete data.userIcon;
        if( !this.options.toMain ){
            data.parentId = this.parentData.id ;
        }
        MWF.Actions.get("x_cms_assemble_control").saveComment( data, function(json){
            if( callback )callback(json);
        }.bind(this));
    }
});
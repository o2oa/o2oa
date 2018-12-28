MWF.xApplication.Forum = MWF.xApplication.Forum || {};
MWF.xApplication.ForumDocument = MWF.xApplication.ForumDocument || {};
MWF.xDesktop.requireApp("Forum", "Common", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);

MWF.xApplication.ForumDocument.Mobile = new Class({
    Implements: [Options, Events],
    options: {
        "id" : "",
        "sectionId" : "",
        "viewPageNum" : 1,
        "replyIndex" : null,
        "isNew": false,
        "isEdited" : false
    },

    initialize: function (node, app, actions, lp, css, options) {
        this.setOptions(options);
        this.app = app;
        this.node = $(node);
        this.actions = actions;
        this.lp = lp;
        this.css = css;
        this.path = "/x_component_ForumDocument/$Mobile/default/";
    },

    load: function () {
        this.actions.login( {}, function( json ){
            this.actions.listSubjectPermission( this.options.id, function( permission ){
                this.permission = permission.data;
                this.actions.getSubjectView( this.options.id , function( data ){
                    this.data = data.data.currentSubject;
                    this.options.sectionId = this.data.sectionId;
                    this.createMiddleNode();
                }.bind(this))
            }.bind(this))
        }.bind(this))
    },
    createMiddleNode: function(){
        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.node);

        this.subjectConainer = new Element("div.subjectConainer",{
            "styles" : this.css.subjectConainer
        }).inject( this.middleNode );

        if( this.data.typeCategory == this.lp.question ){
            this.satisfiedReplyViewConainer = new Element("div.satisfiedReplyViewConainer",{
                "styles" : this.css.replyViewConainer
            }).inject( this.middleNode );
        }

        this.replyViewConainer = new Element("div.replyViewConainer",{
            "styles" : this.css.replyViewConainer
        }).inject( this.middleNode );

        //this.createPagingBar();

        this.createSubject();
        if( this.data.typeCategory == this.lp.question ) {
            if( this.data.acceptReplyId ){
                this.createSatisfiedReplyView();
            }
        }
        this.createReplyView();
    },
    createSubject : function(){
        this.subjectView = new MWF.xApplication.ForumDocument.Mobile.SubjectView( this.subjectConainer, this.app, this, {
            templateUrl : this.path + "listItemSubject.json",
            scrollEnable : false
        } );
        this.subjectView.data = this.data;
        this.subjectView.load();
    },
    showReply : function( id ){
        this.replyView.showReply( id );
    },
    createReplyView : function(){
        var itemReplyTitleArea = new Element("div.itemReplyTitleArea", {
            styles : this.css.itemReplyTitleArea
        }).inject(this.replyViewConainer);

        new Element("div.itemReplyTitle", {
            styles : this.css.itemReplyTitle,
            text : this.lp.allReply + "(" + this.data.replyTotal + ")"
         }).inject(itemReplyTitleArea);

        this.replyView = new MWF.xApplication.ForumDocument.Mobile.ReplyView( this.replyViewConainer, this.app, this, {
            templateUrl : this.path + "listItemReply.json",
            scrollEnable : false,
            pagingEnable : true,
            documentKeyWord : "orderNumber",
            pagingPar : {
                currentPage : this.options.viewPageNum || 1,
                countPerPage : 10,
                hasPrevPage : true,
                hasTruningBar : false,
                onPostLoad : function(){
                    if( this.replyView.getCurrentPageNum() == 1 ){
                        if( this.replyView.paging && this.replyView.paging.nextPageNode)this.replyView.paging.nextPageNode.setStyle("width","99%");
                    }else if( this.replyView.getCurrentPageNum() == this.replyView.getPageSize() ){
                        if( this.replyView.paging && this.replyView.paging.prevPageNode)this.replyView.paging.prevPageNode.setStyle("width","99%");
                    }else{
                        if( this.replyView.paging && this.replyView.paging.prevPageNode)this.replyView.paging.prevPageNode.setStyle("width","49%");
                        if( this.replyView.paging && this.replyView.paging.nextPageNode)this.replyView.paging.nextPageNode.setStyle("width","49%");
                    }
                }.bind(this)
            }
        } );
        this.replyView.data = this.data;
        this.replyView.filterData = { "subjectId" : this.data.id };
        this.replyView.load();
    },
    createSatisfiedReplyView : function( ){
        var itemReplyTitleArea = new Element("div.itemReplyTitleArea", {
            styles : this.css.itemReplyTitleArea
        }).inject(this.satisfiedReplyViewConainer);

        new Element("div.itemReplyTitle", {
            styles : this.css.itemReplyTitle_accepted,
            text : this.lp.satisfiedAnswer
        }).inject(itemReplyTitleArea);

        this.satisfiedReplyView = new MWF.xApplication.ForumDocument.Mobile.SatisfiedReplyView( this.satisfiedReplyViewConainer, this, this, {
            templateUrl : this.path + "listItemSatisfied.json",
            scrollEnable : false
        } );
        this.satisfiedReplyView.data = this.data;
        this.satisfiedReplyView.load();
    },
    getUserIcon: function( name ){
        var icon = "";
        var url = MWF.Actions.get("x_organization_assemble_personal").getIcon(name);
        if( url ){
            icon = url;
        }else{
            icon = "/x_component_ForumDocument/$Main/default/icon/noavatar_big.gif";
        }
        //MWF.Actions.get("x_organization_assemble_control").getPersonIcon(name, function(url){
        //    icon = url;
        //}.bind(this), function(){
        //    icon = "/x_component_ForumDocument/$Main/default/icon/noavatar_big.gif";
        //});
        return icon;
    }

});


MWF.xApplication.ForumDocument.Mobile.SubjectView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data, index){
        data.index = index;
        return new MWF.xApplication.ForumDocument.Mobile.SubjectDocument(this.viewNode, data, this.explorer, this, null, data.index );
        //this.getUserData( data.creatorName, function(json ){
        //    data.userIcon = json.data ? json.data.icon : "";
        //    return new MWF.xApplication.ForumDocument.Mobile.SubjectDocument(this.viewNode, data, this.explorer, this, null, data.index );
        //}.bind(this) )
    },
    getUserData : function( name, callback ){
        MWF.Actions.get("x_organization_assemble_control").getPerson( function( json ){
            if( callback )callback( json );
        }.bind(this), null, name, true )
    },
    _getCurrentPageData: function(callback, count){
        var json = {
            type: "success",
            count : 1,
            size : 1,
            data : [this.data]
        };
        if (callback)callback(json)
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSection(documentData.id, function(json){
            this.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
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

MWF.xApplication.ForumDocument.Mobile.SubjectDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverSubject : function(subjectNode, ev){
    },
    mouseoutSubject : function(subjectNode, ev){
    },
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        var content = itemNode.getElements( "[item='content']" )[0];
        content.getElements("img").each( function( img ){
            img.setStyles({
                "height": "auto",
                "max-width" : "100%"
            });
        }.bind(this));

        if( this.data.typeCategory == this.lp.vote ){
            var voteArea = itemNode.getElement("[item='vote']");
            MWF.xDesktop.requireApp("ForumDocument", "Vote", function(){
                this.vote = new MWF.xApplication.ForumDocument.Vote(voteArea, this.app, {
                    isNew : false,
                    isEdited : false
                }, this.data);
                this.vote.load();
            }.bind(this), true)
        }
    },
    createReply : function(itemNode, ev ){
        var form = new MWF.xApplication.ForumDocument.ReplyForm(this, {}, {
            "toMain" : true,
            onPostOk : function( id ){
                this.app.postCreateReply( id )
            }.bind(this)
        });
        form.mainData = this.data;
        form.create()
    }
});



MWF.xApplication.ForumDocument.Mobile.ReplyView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    showReply: function( id ){
      this.actions.getReply( id, function( json ){
          new MWF.xApplication.ForumDocument.Mobile.ReplyDocument(this.viewNode, json.data, this.explorer, this, null, null );
      }.bind(this) )
    },
    _createDocument: function(data, index){
        data.index = index;
        return  new MWF.xApplication.ForumDocument.Mobile.ReplyDocument(this.viewNode, data, this.explorer, this, null, data.index );
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if(!count)count=10;
        if(!pageNum)pageNum = 1;
        if( pageNum == 1 ){
            this.explorer.subjectConainer.setStyle("display","block");
            if( this.explorer.satisfiedReplyViewConainer )this.explorer.satisfiedReplyViewConainer.setStyle("display","block");
        }else{
            this.explorer.subjectConainer.setStyle("display","none");
            if( this.explorer.satisfiedReplyViewConainer )this.explorer.satisfiedReplyViewConainer.setStyle("display","none");
        }
        var filter = this.filterData || {};
        this.actions.listReplyFilterPage( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteReply( documentData.id, function(){
            this.reload();
            this.app.notice( this.lp.deleteReplySuccess, "ok")
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

MWF.xApplication.ForumDocument.Mobile.ReplyDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverSubject : function(subjectNode, ev){
    },
    mouseoutSubject : function(subjectNode, ev){
    },
    getUserData : function( name, callback ){
        MWF.Actions.get("x_organization_assemble_control").getPerson( function( json ){
            if( callback )callback( json );
        }.bind(this), null, name, true )
    },
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        var content = itemNode.getElements( "[item='content']" )[0];
        content.getElements("img").each( function( img ){
            img.setStyles({
                "height": "auto",
                "max-width" : "100%"
            });
        }.bind(this));
        if( itemData.parentId && itemData.parentId != "" ){
            var quoteContainer = itemNode.getElements( "[item='quoteContent']" )[0];
            this.actions.getReply( itemData.parentId, function( json ){
                    var data = this.parentData =  json.data;
                    var quoteContent = new Element("div", {  "styles" : this.css.itemQuote }).inject(quoteContainer);
                    var content = quoteContent.set("html", data.content).get("text");
                    quoteContent.empty();
                    data.contentText = content;

                    //new Element( "div", {styles : this.css.quoteLeft} ).inject( quoteContent );

                    var quoteArea = new Element( "div", {styles : this.css.quoteArea } ).inject( quoteContent );
                    var quoteInfor = new Element( "div", {
                        styles : this.css.quoteInfor,
                        text : this.lp.replyTo  + " " + data.orderNumber + this.lp.floor + " " + data.creatorName.split("@")[0] + " " + data.createTime
                    }).inject( quoteArea );

                    new Element( "div", {
                        styles : this.css.quoteText,
                        text :  content.length > 100 ? (content.substr(0, 100) + "...") : content
                    }).inject( quoteArea );

                    //new Element( "div", {styles : this.css.quoteRight} ).inject( quoteContent );
                }.bind(this) , function( json ){
                    new Element( "div" , {
                        "styles" : this.css.replyBeinngDelete,
                        "text" : this.lp.quoteReplyBeingDeleted
                    }).inject(quoteContainer)
                }.bind(this)
            )
        }
        //var userIcon = itemNode.getElements( "[item='userIcon']" )[0];
        //this.getUserData( itemData.creatorName, function(json ){
        //    userIcon.src = "data:image/png;base64,"+json.data.icon;
        //}.bind(this) );
    },
    createReply : function(itemNode, ev ){ // 对回复进行回复
        var ua = navigator.userAgent.toLowerCase();
        if (/iphone|ipad|ipod/.test(ua)) {
            window.webkit.messageHandlers.ReplyAction.postMessage({body:this.data.id});
        } else if (/android/.test(ua)) {
            window.o2bbs.reply( this.data.id );
        }
    }
});


MWF.xApplication.ForumDocument.Mobile.SatisfiedReplyView = new Class({
    Extends: MWF.xApplication.ForumDocument.Mobile.ReplyView,
    _createDocument: function (data, index) {
        data.index = index;
        return new MWF.xApplication.ForumDocument.Mobile.SatisfiedReplyDocument(this.viewNode, data, this.explorer, this, null, data.index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if(!count)count=1;
        if(!pageNum)pageNum = 1;
        var filter = this.filterData || {};
        this.actions.getAcceptedReply( this.data.acceptReplyId, function(json){
            if( !json.data ){
                json.data = [];
            }else if( typeOf( json.data ) == "object" ){
                json.data = [ json.data ];
                json.count = 1;
            }
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    }
});

MWF.xApplication.ForumDocument.Mobile.SatisfiedReplyDocument = new Class({
    Extends: MWF.xApplication.ForumDocument.Mobile.ReplyDocument
});

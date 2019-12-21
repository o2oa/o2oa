MWF.xApplication.CRM = MWF.xApplication.CRM || {};

MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("CRM", "Template", null,false);
MWF.xDesktop.requireApp("Template", "Explorer", null,false);

MWF.require("MWF.widget.Identity", null,false);


MWF.xApplication.CRM.Message = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp.customer;
        this.path = "/x_component_CRM/$Message/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
        debugger
    },
    loadCss: function () {
        this.cssPath = "/x_component_CRM/$Message/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        if(this.formContentArr)this.formContentArr.empty();
        this.formContentArr = [];
        if(this.formMarkArr)this.formMarkArr.empty();
        this.formMarkArr = [];

        this.rightContentDiv = this.app.rightContentDiv;
        this.createHeadContent();
        this.createCustomerContent();

        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));




    },
    reload:function(){
        this.createCustomerContent();
        this.resizeWindow();
    },

    createHeadContent:function(){
        debugger
        if(this.headContentDiv) this.headContentDiv.destroy();
        this.headContentDiv = new Element("div.headContentDiv",{"styles":this.css.headContentDiv}).inject(this.rightContentDiv);
        this.headTitleDiv = new Element("div.headTitleDiv",{
            "styles":this.css.headTitleDiv,
            "text":"待办信息"
        }).inject(this.headContentDiv);

    },
    createToolBarContent:function(){

    },
    createCustomerContent:function(){
        _self = this;
        if(this.contentListDiv) this.contentListDiv.destroy();
        this.contentListDiv = new Element("div.contentListDiv",{"styles":this.css.contentListDiv}).inject(this.rightContentDiv);

        if(this.contentLeftDiv) this.contentLeftDiv.destroy();
        this.contentLeftDiv = new Element("div.contentLeftDiv",{"styles":this.css.contentLeftDiv}).inject(this.contentListDiv);
        if(this.contentLeftInDiv) this.contentLeftInDiv.destroy();
        this.contentLeftInDiv = new Element("div.contentLeftInDiv",{
            "styles":this.css.contentLeftInDiv
        }).inject(this.contentLeftDiv);

        if(this.contentLeftItemDiv) this.contentLeftItemDiv.destroy();
        this.contentLeftItemDiv = new Element("div.contentLeftItemDiv",{
            "styles":this.css.contentLeftInDiv,
            "text":"今日需联系客户"
        }).inject(this.contentLeftInDiv);
        if(this.contentLeftItemDiv2) this.contentLeftItemDiv2.destroy();
        this.contentLeftItemDiv2 = new Element("div.contentLeftItemDiv",{
            "styles":this.css.contentLeftInDiv,
            "text":"分配给我的线索"
        }).inject(this.contentLeftInDiv);
        if(this.contentLeftItemDiv3) this.contentLeftItemDiv3.destroy();
        this.contentLeftItemDiv3 = new Element("div.contentLeftItemDiv",{
            "styles":this.css.contentLeftInDiv,
            "text":"分配给我的客户"
        }).inject(this.contentLeftInDiv);
        if(this.contentLeftItemDiv4) this.contentLeftItemDiv4.destroy();
        this.contentLeftItemDiv4 = new Element("div.contentLeftItemDiv",{
            "styles":this.css.contentLeftInDiv,
            "text":"待进入公海的客户"
        }).inject(this.contentLeftInDiv);


        if(this.contentListInDiv) this.contentListInDiv.destroy();
        this.contentListInDiv = new Element("div.contentListInDiv",{"styles":this.css.contentListInDiv}).inject(this.contentListDiv);

        var size = this.rightContentDiv.getSize();
        if(this.contentListDiv)this.contentListDiv.setStyles({"height":(size.y-this.headContentDiv.getHeight()-8)+"px"});
        if(this.contentListInDiv)this.contentListInDiv.setStyles({"height":this.contentListDiv.getHeight()+"px"});
        if(this.contentLeftDiv)this.contentLeftDiv.setStyles({"height":(this.contentListDiv.getHeight()-80)+"px"});

        //jQuery(that.contentLeftInDiv).children(":first").attr("class","contentLeftItemSelectd");
        jQuery(_self.contentLeftInDiv).find("div").click(function(){
            jQuery(this).attr("class","contentLeftItemSelectd");
            var category = jQuery(this).text();
            jQuery(this).siblings().attr("class","contentLeftItemDiv");
            var templateUrl = _self.path+"messageView.json";
            var filter = {};
            switch (category) {
                case "今日需联系客户":
                    if(_self.customerView) delete _self.customerView;
                    jQuery(".contentListInDiv").empty();
                    _self.customerView =  new MWF.xApplication.CRM.Message.CustomerView(
                        _self.contentListInDiv,
                        _self.openDiv,
                        _self.app,
                        _self,
                        { templateUrl : templateUrl,filterData:filter},
                        {
                            lp:_self.app.lp.customerView,
                            isAdmin:_self.options.isAdmin
                        }
                    );
                    _self.customerView.load();
                    break;
                case "分配给我的线索":
                    if(_self.clueView) delete _self.clueView;
                    _self.clueView =  new MWF.xApplication.CRM.Index.ClueView(
                        _self.contentListInDiv,
                        _self.openDiv,
                        _self.app,
                        _self,
                        { templateUrl : templateUrl,filterData:filter},
                        {
                            lp:_self.app.lp.clueView,
                            isAdmin:_self.options.isAdmin
                        }
                    );
                    _self.clueView.load();
                    break;
                case "分配给我的客户":
                    if(_self.allotCustomerView) delete _self.allotCustomerView;
                    jQuery(".contentListInDiv").empty();
                    _self.allotCustomerView =  new MWF.xApplication.CRM.Message.AllotCustomerView(
                        _self.contentListInDiv,
                        _self.openDiv,
                        _self.app,
                        _self,
                        { templateUrl : templateUrl,filterData:filter},
                        {
                            lp:_self.app.lp.customerView,
                            isAdmin:_self.options.isAdmin
                        }
                    );
                    _self.allotCustomerView.load();
                    break;
                case "待进入公海的客户":
                    if(_self.seaCustomerView) delete _self.seaCustomerView;
                    jQuery(".contentListInDiv").empty();
                    _self.seaCustomerView =  new MWF.xApplication.CRM.Message.SeaCustomerView(
                        _self.contentListInDiv,
                        _self.openDiv,
                        _self.app,
                        _self,
                        { templateUrl : templateUrl,filterData:filter},
                        {
                            lp:_self.app.lp.customerView,
                            isAdmin:_self.options.isAdmin
                        }
                    );
                    _self.seaCustomerView.load();
                    break;
            }
        });
        jQuery(_self.contentLeftInDiv).children(":first").click();

    },

    resizeWindow:function(){
        var size = this.rightContentDiv.getSize();
        if(this.contentListDiv)this.contentListDiv.setStyles({"height":(size.y-this.headContentDiv.getHeight()-8)+"px"});
        if(this.contentListInDiv)this.contentListInDiv.setStyles({"height":(this.contentListDiv.getHeight())+"px"});
        if(this.contentLeftDiv)this.contentLeftDiv.setStyles({"height":(this.contentListDiv.getHeight()-80)+"px"});
    }

});


MWF.xApplication.CRM.Message.CustomerView = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexView,

    _createDocument: function(data){
        return new MWF.xApplication.CRM.Clue.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count, page, searchText,searchType){
        if (!count)count = 15;
        if (!page)page = 1;
        //var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var dt = this.formatDate(new Date());
        var filter = this.options.filterData || {};
        filter = {
            begintime:dt+" 00:00:00",
            endtime:dt+" 23:59:59"
        };
        debugger
        this.actions.listNextTimePaginLike(page, count, filter, function (json) {
            if (callback)callback(json);
        }.bind(this));





    },
    _create: function(){

    },
    _openDocument: function(openId ,openName){

        MWF.xDesktop.requireApp("CRM", "CustomerOpen", function(){
            this.explorer = new MWF.xApplication.CRM.CustomerOpen(this, this.actions,{},{
                "openId":openId,
                "openName":openName,
                "openType":"single",
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))
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

MWF.xApplication.CRM.Index.ClueView = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexView,

    _createDocument: function(data){
        return new MWF.xApplication.CRM.Clue.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count, page, searchText){
        var category = this.category = this.options.category;
        if (!count)count = 15;
        if (!page)page = 1;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        //if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        if(searchText){
            filter = {
                key:searchText
            };
        }
        this.actions.getClueListPage(page, count, filter, function (json) {
            debugger
            if (callback)callback(json);
        }.bind(this));

    },
    _create: function(){

    },
    _openDocument: function(openId ,openName){
        /*MWF.xDesktop.requireApp("CRM", "ClueEdit", function(){
         this.explorer = new MWF.xApplication.CRM.ClueEdit(this, this.actions,{},{
         "clueId":clueId,
         "onReloadView" : function(  ){
         //alert(JSON.stringify(data))
         this.reload();
         }.bind(this)
         });
         this.explorer.load();
         }.bind(this))*/
        MWF.xDesktop.requireApp("CRM", "ContactsOpen", function(){
            this.explorer = new MWF.xApplication.CRM.ContactsOpen(this, this.actions,{},{
                "openId":openId,
                "openName":openName,
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))
    },
    _openOtherDocument: function(openId ,openName){

        MWF.xDesktop.requireApp("CRM", "CustomerOpen", function(){
            this.explorer = new MWF.xApplication.CRM.CustomerOpen(this, this.actions,{},{
                "openId":openId,
                "openName":openName,
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))
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

MWF.xApplication.CRM.Message.AllotCustomerView = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexView,

    _createDocument: function(data){
        return new MWF.xApplication.CRM.Clue.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count, page, searchText,searchType){
        if (!count)count = 15;
        if (!page)page = 1;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        //if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        filter={key: searchText?searchText.trim():"",
            orderFieldName: "updateTime",
            orderType: "desc"
        };
        this.actions.getCustomerListPage(page, count, filter, function (json) {
            if (callback)callback(json);
        }.bind(this));





    },
    _create: function(){

    },
    _openDocument: function(openId ,openName){

        MWF.xDesktop.requireApp("CRM", "CustomerOpen", function(){
            this.explorer = new MWF.xApplication.CRM.CustomerOpen(this, this.actions,{},{
                "openId":openId,
                "openName":openName,
                "openType":"single",
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))
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
MWF.xApplication.CRM.Message.SeaCustomerView = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexView,

    _createDocument: function(data){
        return new MWF.xApplication.CRM.Clue.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count, page, searchText,searchType){
        if (!count)count = 15;
        if (!page)page = 1;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        //if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        filter={key: searchText?searchText.trim():"",
            orderFieldName: "updateTime",
            orderType: "desc"
        };
        this.actions.getCustomerListPage(page, count, filter, function (json) {
            if (callback)callback(json);
        }.bind(this));





    },
    _create: function(){

    },
    _openDocument: function(openId ,openName){

        MWF.xDesktop.requireApp("CRM", "CustomerOpen", function(){
            this.explorer = new MWF.xApplication.CRM.CustomerOpen(this, this.actions,{},{
                "openId":openId,
                "openName":openName,
                "openType":"single",
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))
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




MWF.xApplication.CRM.Customer.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        this.view.template.items.each(function (item,i) {
            if(item.head.width){
                itemNode.getElements("td")[i].set("width",item.head.width);
            }
            if(i == itemNode.getElements("td").length-1){
                itemNode.getElements("td")[i].set("width",this.view.lastTdWidth);
            }
        }.bind(this));

    },
    open: function (e) {
        this.view._openDocument(this.data, this.index);
    },
    edit : function(){
        var appId = "ForumDocument"+this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ForumDocument", {
                "sectionId" : this.data.sectionId,
                "id" : this.data.id,
                "appId": appId,
                "isEdited" : true,
                "isNew" : false,
                "index" : this.index
            });
        }
    }
})
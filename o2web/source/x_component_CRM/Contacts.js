MWF.xApplication.CRM = MWF.xApplication.CRM || {};

MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("CRM", "Template", null,false);
MWF.xDesktop.requireApp("Template", "Explorer", null,false);

MWF.require("MWF.widget.Identity", null,false);
/*MWF.xDesktop.requireApp("Forum", "Actions.RestActions", null, false);*/

MWF.xApplication.CRM.Contacts = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp.contact;
        this.path = "/x_component_CRM/$Contacts/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_CRM/$Contacts/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        //this.testActions = new MWF.xApplication.Forum.Actions.RestActions();
        if(this.formContentArr)this.formContentArr.empty();
        this.formContentArr = [];
        if(this.formMarkArr)this.formMarkArr.empty();
        this.formMarkArr = [];

        this.rightContentDiv = this.app.rightContentDiv;
        this.createHeadContent();
        //this.createToolBarContent();
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
        if(this.headContentDiv) this.headContentDiv.destroy();
        this.headContentDiv = new Element("div.headContentDiv",{"styles":this.css.headContentDiv}).inject(this.rightContentDiv);
        this.headTitleDiv = new Element("div.headTitleDiv",{
            "styles":this.css.headTitleDiv,
            "text":this.lp.head.headTitle
        }).inject(this.headContentDiv);
        //search
        this.headSearchDiv = new Element("div.headSearchDiv",{"styles":this.css.headSearchDiv}).inject(this.headContentDiv);
        this.headSearchTextDiv = new Element("div.headSearchTextDiv",{"styles":this.css.headSearchTextDiv}).inject(this.headSearchDiv);
        this.headSearchImg = new Element("img.headSearchImg",{
            "styles":this.css.headSearchImg,
            "src": this.path+"default/icons/search.png"
        }).inject(this.headSearchTextDiv);
        this.headSearchInput = new Element("input.headSearchInput",{
            "styles":this.css.headSearchInput,
            "placeholder":this.lp.head.searchText
        }).inject(this.headSearchTextDiv);
        this.headSearchInput.addEvents({
            "keyup":function(){
                if(this.headSearchInput.get("value")!=""){
                    this.headSearchRemoveImg.setStyles({"display":"inline-block"})
                }
            }.bind(this)
        });
        this.headSearchRemoveImg = new Element("img.headSearchRemoveImg",{
            "styles":this.css.headSearchRemoveImg,
            "src": this.path+"default/icons/remove.png"
        }).inject(this.headSearchTextDiv);
        this.headSearchRemoveImg.addEvents({
            "click":function(){
                this.headSearchInput.set("value","")
            }.bind(this)
        });
        this.headSearchBottonDiv = new Element("div.headSearchBottonDiv",{
            "styles":this.css.headSearchBottonDiv,
            "text":this.lp.head.search
        }).inject(this.headSearchDiv);
        this.headBottonDiv = new Element("div.headBottonDiv",{"styles":this.css.headBottonDiv}).inject(this.headContentDiv);
        this.headNewBottonDiv = new Element("div.headNewBottonDiv",{
            "styles":this.css.headNewBottonDiv,
            "text" :this.lp.head.create
        }).inject(this.headBottonDiv);
        this.headNewBottonDiv.addEvents({
            "click":function(){
                MWF.xDesktop.requireApp("CRM", "ContactsEdit", function(){
                    this.explorer = new MWF.xApplication.CRM.ContactsEdit(this, this.actions,{},{
                        "isEdited":true,
                        "isNew":true,
                        "onReloadView" : function(  ){
                            //alert(JSON.stringify(data))
                            this.reload();
                        }.bind(this)
                    });
                    this.explorer.load();
                }.bind(this))
            }.bind(this)

        });
        this.headMoreBottonDiv = new Element("div.headMoreBottonDiv",{
            "styles":this.css.headMoreBottonDiv,
            "text" :this.lp.head.moreAction
        }).inject(this.headBottonDiv);
        this.headMoreBottonDiv.addEvents({
            "click":function(){

            }.bind(this)
        });
        this.headMoreImg = new Element("img.headMoreImg",{
            "styles": this.css.headMoreImg,
            "src" : this.path+"default/icons/arrow.png"
        }).inject(this.headMoreBottonDiv);

    },
    createToolBarContent:function(){

    },
    createCustomerContent:function(){
        if(this.contentListDiv) this.contentListDiv.destroy();
        this.contentListDiv = new Element("div.contentListDiv",{"styles":this.css.contentListDiv}).inject(this.rightContentDiv);
        if(this.contentListInDiv) this.contentListInDiv.destroy();
        this.contentListInDiv = new Element("div.contentListInDiv",{"styles":this.css.contentListInDiv}).inject(this.contentListDiv);

        var size = this.rightContentDiv.getSize();
        if(this.contentListDiv)this.contentListDiv.setStyles({"height":(size.y-this.headContentDiv.getHeight()-8)+"px"});
        if(this.contentListInDiv)this.contentListInDiv.setStyles({"height":this.contentListDiv.getHeight()+"px","width":"100%"});

        if(this.contactsView) delete this.contactsView;
        var templateUrl = this.path+"contactsView.json";
        var filter = {};

        ////this.customerView =  new  MWF.xApplication.CRM.Customer.View(this.contentListInDiv, this.app, {lp : this.app.lp.curtomerView, css : this.css, actions : this.actions }, { templateUrl : templateUrl,filterData:filter} );
        this.contactsView =  new MWF.xApplication.CRM.Contacts.View(
            this.contentListInDiv,
            this.openDiv,
            this.app,
            this,
            { templateUrl : templateUrl,filterData:filter},
            {
                lp:this.app.lp.contactsView
            }
        );
        this.contactsView.load();
    },

    resizeWindow:function(){
        var size = this.rightContentDiv.getSize();
        var rSize = this.headTitleDiv.getSize();
        var lSize = this.headBottonDiv.getSize();
        if(this.headSearchDiv){
            var x = this.headSearchDiv.getSize().x;
            this.headSearchDiv.setStyles({"margin-left":(size.x-rSize.x-lSize.x)/2-(x/2)+"px"});
        }
        //alert(JSON.stringify(size))
        if(this.contentListDiv)this.contentListDiv.setStyles({"height":(size.y-this.headContentDiv.getHeight()-8)+"px"});
        if(this.contentListInDiv)this.contentListInDiv.setStyles({"height":this.contentListDiv.getHeight()+"px"});
    }

});


MWF.xApplication.CRM.Contacts.View = new Class({
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
        debugger
        this.actions.getContactsListPage(page, count, filter, function (json) {
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
                "openType":"single",
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


MWF.xApplication.CRM.Contacts.Document = new Class({
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





//
//MWF.xApplication.CRM.Customer.View = new Class({
//    Extends: MWF.xApplication.Template.Explorer.ComplexView,
//
//    _createDocument: function(data){
//        return new MWF.xApplication.CRM.Customer.Document(this.viewNode, data, this.explorer, this);
//    },
//
//    _getCurrentPageData: function(callback, count){
//        var category = this.category = this.options.category;
//
//        if (!count)count = 20;
//        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
//
//        if(id=="(0)")this.app.createShade();
//
//        var filter = this.options.filterData || {};
//
//        this.actions.getCustomerListNext(id, count, filter, function (json) {
//            if (callback)callback(json);
//            this.app.destroyShade();
//        }.bind(this));
//
//    },
//    _create: function(){
//
//    },
//    _openDocument: function( documentData ){
//
//        //if(this.customerRead){
//        //    this.customerRead.load(documentData)
//        //}else{
//            MWF.xDesktop.requireApp("CRM", "CustomerRead", function(){
//                this.customerRead = new MWF.xApplication.CRM.CustomerRead(this.explorer.contentListDiv,this.app, this.explorer,this.actions,{
//                    "width":1000,
//                    "onReloadView" : function(){
//                        this.explorer.reloadCustomerView();
//                    }.bind(this)
//                } );
//                this.customerRead.load(documentData);
//                this.explorer.formContentArr.push(this.customerRead);
//                this.explorer.formMarkArr.push(this.customerRead.formMaskNode);
//
//            }.bind(this));
//        //}
//
//    },
//    _queryCreateViewNode: function(){
//
//    },
//    _postCreateViewNode: function( viewNode ){
//
//    },
//    _queryCreateViewHead:function(){
//
//    },
//    _postCreateViewHead: function( headNode ){
//
//    }
//
//});
//
//MWF.xApplication.CRM.Customer.Document = new Class({
//    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
//
//    "viewActionReturn":function(){
//        return false
//    }
//
//});
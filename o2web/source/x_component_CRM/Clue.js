MWF.xApplication.CRM = MWF.xApplication.CRM || {};

MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("CRM", "Template", null,false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.CRM.Clue = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = this.app.lp.clue;
        this.path = "/x_component_CRM/$Clue/";
        this.loadCss();
        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_CRM/$Clue/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        if(this.formContentArr)this.formContentArr.empty();
        this.formContentArr = [];
        if(this.formMarkArr)this.formMarkArr.empty();
        this.formMarkArr = [];

        this.rightContentDiv = this.app.rightContentDiv;
        this.createHeadContent();
        //this.createToolBarContent();
        this.createClueContent();

        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));

    },

    reload:function(){
        this.createClueContent();
        this.resizeWindow();
    },

    createHeadContent:function(){
        /*if(this.openDiv) this.openDiv.destroy();
        this.openDiv = new Element("div.modal").inject(this.rightContentDiv);
        this.openDiv.setStyles({"display":"none"});*/

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
                MWF.xDesktop.requireApp("CRM", "ClueEdit", function(){
                    this.explorer = new MWF.xApplication.CRM.ClueEdit(this, this.actions,{},{
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
        this.headMoreImg = new Element("img.headMoreImg",{
            "styles": this.css.headMoreImg,
            "src" : this.path+"default/icons/arrow.png"
        }).inject(this.headMoreBottonDiv);

    },
    createToolBarContent:function(){

    },
    createClueContent:function(){
        if(this.contentListDiv) this.contentListDiv.destroy();
        this.contentListDiv = new Element("div.contentListDiv",{"styles":this.css.contentListDiv}).inject(this.rightContentDiv);
        if(this.contentListInDiv) this.contentListInDiv.destroy();
        this.contentListInDiv = new Element("div.contentListInDiv",{"styles":this.css.contentListInDiv}).inject(this.contentListDiv);

        var size = this.rightContentDiv.getSize();
        if(this.contentListDiv)this.contentListDiv.setStyles({"height":(size.y-this.headContentDiv.getHeight()-8)+"px"});
        if(this.contentListInDiv)this.contentListInDiv.setStyles({"height":this.contentListDiv.getHeight()+"px","width":"100%"});

        if(this.clueView) delete this.clueView;
        var templateUrl = this.path+"clueView.json";
        var filter = {};
        ////this.customerView =  new  MWF.xApplication.CRM.Customer.View(this.contentListInDiv, this.app, {lp : this.app.lp.curtomerView, css : this.css, actions : this.actions }, { templateUrl : templateUrl,filterData:filter} );
        this.clueView =  new  MWF.xApplication.CRM.Clue.View(
            this.contentListInDiv,
            this.openDiv,
            this.app,
            this,
            { templateUrl : templateUrl,filterData:filter},
            {
                lp:this.app.lp.clueView,
                isAdmin:this.options.isAdmin
            }
        );
        this.clueView.load();
        //this.app.setScrollBar(this.contentListInDiv.getElement(".contentTableNode"),this.customerView,"crm");
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


MWF.xApplication.CRM.Clue.View = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexView,

    _createDocument: function(data){
        return new MWF.xApplication.CRM.Clue.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count, page, searchText,searchType){
        var category = this.category = this.options.category;
        if (!count)count = 15;
        if (!page)page = 1;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.options.filterData || {};
        /*if(searchText && searchText.trim()!=""){
            filter = {
                key:searchText
            };
        }*/
        filter={key: searchText?searchText.trim():"",
                orderFieldName: "updateTime",
                orderType: "desc"
        };
        debugger
        if (!searchType)searchType = "全部线索";
        if(!this.isAdmin){
            debugger
            if(searchType=="下属的线索"){
                this.actions.ListNestedSubPerson(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
            if(searchType=="我负责的线索"){
                this.actions.ListMyDuty(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
            if(searchType=="已转化的线索"){
                this.actions.ListTransfer(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
            if(searchType=="全部线索"){
                debugger
                this.actions.ListAllMy(page, count, filter, function (json) {
                    debugger
                    if (callback)callback(json);
                }.bind(this));
            }
        }else{
            this.actions.getClueListPage(page, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this));
        }


    },
    _create: function(){

    },
    _openDocument: function(clueId ,clueName){
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
        MWF.xDesktop.requireApp("CRM", "ClueOpen", function(){
            this.explorer = new MWF.xApplication.CRM.ClueOpen(this, this.actions,{},{
                "clueId":clueId,
                "clueName":clueName,
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

MWF.xApplication.CRM.Clue.Document = new Class({
    /*Extends: MWF.xApplication.CRM.Template.ComplexDocument,

    "viewActionReturn":function(){
        return false
    }*/


});
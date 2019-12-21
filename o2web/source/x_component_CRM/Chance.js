MWF.xApplication.CRM = MWF.xApplication.CRM || {};

MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("CRM", "Template", null,false);

MWF.xApplication.CRM.Chance = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, app, actions, options){
        this.setOptions(options);

        this.path = "/x_component_CRM/$Chance/";
        this.cssPath = "/x_component_CRM/$Chance/"+this.options.style+"/css.wcss";
        this.lpPath = "/x_component_CRM/$Chance/"+this.options.style+"/zh-cn.js?v="+o2.version.v;
        this._loadCss();
        /*COMMON.AjaxModule.loadCss( this.path+this.options.style+"/main.css",function(){
            console.log("main.css,load complete");
        })*/
        this.app = app;
        this.container = $(container);
        this.lp={};//this.app.lp.chance;

        if(!this.lp.head){
            console.log("this.lp:::",this.lpPath);
            this.loadLP();
            this.lp = MWF.xApplication.CRM.Chance.LP.chance;
        }else{
           //console.log("222222222222");
        }
        console.log("this is Chance init()");
        this.actions = actions;
    },
    loadLP:function(){
        MWF.xDesktop.requireApp("CRM", "$Chance.default.nzh-cn", null, false);
        /*if(!MWF.xApplication.CRM.Chance.LP){
            MWF.xDesktop.requireApp("CRM", "$Chance.default."+o2.language, {
                "failure": function(){
                    MWF.xDesktop.requireApp("CRM", "$Chance.default.nzh-cn", null, false);
                }.bind(this)
            }, false);
        }*/

    },
    load: function(){

        if(this.formContentArr)this.formContentArr.empty();
        this.formContentArr = [];
        if(this.formMarkArr)this.formMarkArr.empty();
        this.formMarkArr = [];

        this.rightContentDiv = this.app.rightContentDiv;
        this.createHeadContent();
        this.createToolBarContent();
        this.createChanceContent();

        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));
    },
    reload:function(){
        this.createChanceContent();
        this.resizeWindow();
    },

    createHeadContent:function(){
        if(this.headContentDiv) this.headContentDiv.destroy();
        console.log(this.css);
        this.headContentDiv = new Element("div.headContentDiv",{"styles":this.css.headContentDiv}).inject(this.rightContentDiv);

        this.headTitleDiv = new Element("div.headTitleDiv",{
            "styles":this.css.headTitleDiv,
            "text":this.lp.head.headTitle
        }).inject(this.headContentDiv);
        console.log("111111");
        //search
        this.headSearchDiv = new Element("div.headSearchDiv",{"styles":this.css.headSearchDiv}).inject(this.headContentDiv);
        this.headSearchTextDiv = new Element("div.headSearchTextDiv",{"styles":this.css.headSearchTextDiv}).inject(this.headSearchDiv);

        this.headSearchInput = new Element("input.headSearchInput",{
            "styles":this.css.headSearchInput,
            "placeholder":this.lp.head.searchText
        }).inject(this.headSearchTextDiv);
        this.headSearchInput.addEvents({
            "keyup":function(){
                if(this.headSearchInput.get("value")!=""){
                    this.headSearchRemoveImg.setStyles({"display":"inline-block"});
                }
            }.bind(this)
        });
        this.headSearchRemoveImg = new Element("img.headSearchRemoveImg",{
            "styles":this.css.headSearchRemoveImg,
            "src": this.path+"default/icons/remove.png"
        }).inject(this.headSearchTextDiv);
        this.headSearchRemoveImg.addEvents({
            "click":function(){
                this.headSearchInput.set("value","");
                this.headSearchRemoveImg.setStyles({"display":"none"});
            }.bind(this)
        });

        this.headSearchButtonDiv = new Element("div.headSearchBottonDiv",{
            "styles":this.css.headSearchButtonDiv,
            //"text":this.lp.head.search
        }).inject(this.headSearchDiv);

        this.headSearchheadSearchButton= new Element("button.headSearchButton",{
            "styles":this.css.headSearchButton
        }).inject(this.headSearchButtonDiv);

        this.headSearchImg = new Element("img.headSearchImg",{
            "styles":this.css.headSearchImg,
            "src": this.path+"default/icons/search.png"
        }).inject(this.headSearchheadSearchButton);

        this.headButtonDiv = new Element("div.headButtonDiv",{"styles":this.css.headButtonDiv}).inject(this.headContentDiv);
        this.headNewButtonDiv = new Element("div.headNewButtonDiv",{
            "styles":this.css.headNewButtonDiv,
            "text" :this.lp.head.create
        }).inject(this.headButtonDiv);
        this.headNewButtonDiv.addEvents({
            "click":function(){
                MWF.xDesktop.requireApp("CRM", "ChanceEdit", function(){
                    console.log("this.lp",this.lp);
                    var editForm = new MWF.xApplication.CRM.ChanceEdit(null,{},null, {
                        app: this.app,
                        container : this.app.content,
                        lp : this.lp,
                        actions : this.actions,
                        css : {},
                        callback : function () {
                            editForm.create();
                        }
                    });
                    editForm.create();
                }.bind(this))
            }.bind(this)

        });


    },
    createToolBarContent:function(){

    },
    createChanceContent:function(){
        if(this.contentListDiv) this.contentListDiv.destroy();
        this.contentListDiv = new Element("div.contentListDiv",{"styles":this.css.contentListDiv}).inject(this.rightContentDiv);
        if(this.contentListInDiv) this.contentListInDiv.destroy();
        this.contentListInDiv = new Element("div.contentListInDiv",{"styles":this.css.contentListInDiv}).inject(this.contentListDiv);

        var size = this.rightContentDiv.getSize();
        if(this.contentListDiv)this.contentListDiv.setStyles({"height":(size.y-this.headContentDiv.getHeight()-8)+"px","width":(size.x)+"px"});
        if(this.contentListInDiv)this.contentListInDiv.setStyles({"height":this.contentListDiv.getHeight()+"px","width":(this.contentListDiv.getWidth())+"px"});

        if(this.chanceView) delete this.chanceView;
        var templateUrl = this.path+"chanceView.json";
        var filter = {};

        ////this.customerView =  new  MWF.xApplication.CRM.Customer.View(this.contentListInDiv, this.app, {lp : this.app.lp.curtomerView, css : this.css, actions : this.actions }, { templateUrl : templateUrl,filterData:filter} );

        this.chanceView =  new  MWF.xApplication.CRM.Chance.View(
            this.contentListInDiv,
            {},
            this.app,
            this,
            { templateUrl : templateUrl,filterData:filter},
            {
                isAdmin:this.options.isAdmin
            }
        );
        this.chanceView.load();
        //this.app.setScrollBar(this.contentListInDiv.getElement(".contentTableNode"),this.customerView,"crm");
    },
    resizeWindow:function(){
        var size = this.rightContentDiv.getSize();
        var rSize = this.headTitleDiv.getSize();
        var lSize = this.headButtonDiv.getSize();
        if(this.headSearchDiv){
            var x = this.headSearchDiv.getSize().x;
            this.headSearchDiv.setStyles({"margin-left":(size.x-rSize.x-lSize.x)/2-(x/2)+"px"});
        }
        //alert(JSON.stringify(size))
        if(this.contentListDiv)this.contentListDiv.setStyles({"height":(size.y-this.headContentDiv.getHeight()-8)+"px","width":(size.x)+"px"});
        if(this.contentListInDiv)this.contentListInDiv.setStyles({"height":this.contentListDiv.getHeight()+"px","width":(this.contentListDiv.getWidth())+"px"});
    }
});

MWF.xApplication.CRM.Chance.View = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexView,
    initialize: function (container, data, app, explorer, options, para) {
        this.container = container;
        this.data = data||{};
        this.explorer = explorer;
        if( para ){
            this.app = app || para.app || this.explorer.app;
            this.lp = para.lp || this.explorer.lp || this.app.lp;
            this.css = para.css || this.explorer.css || this.app.css;
            this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
            this.isAdmin = para.isAdmin;
        }else{
            this.app = app || this.explorer.app;
            this.lp = this.explorer.lp || this.app.lp;
            this.css = this.explorer.css || this.app.css;
            this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
        }

        /*template 外边传进来*/
        if (!options.templateUrl) {
            options.templateUrl = this.explorer.path + "listItem.json"
        } else if (options.templateUrl.indexOf("/") == -1) {
            options.templateUrl = this.explorer.path + options.templateUrl;
        }
        this.setOptions(options);

    },
    _createDocument: function(data){
        return new MWF.xApplication.CRM.Chance.Document(this.viewNode, data, this.explorer, this);
    },
    ayalyseTemplate: function () {
        MWF.getJSON(this.options.templateUrl, function (json) {
            this.template = json;
            console.log("this is template,",json);
        }.bind(this), false)
    },
    _getCurrentPageData: function(callback, count, page, searchText,searchType){
        var category = this.category = this.options.category;
        if (!count)count = 10;
        if (!page)page = 1;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        //if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        /*if(searchText){
            filter = {
                key:searchText
            };
        }


        this.actions.getChanceByPage(page, count, filter, function (json) {
            if (callback)callback(json);
            //this.app.destroyShade();
        }.bind(this));*/
        filter={key: searchText?searchText.trim():"",
            orderFieldName: "updateTime",
            orderType: "desc"
        };
        if (!searchType)searchType = "全部商机";

        if(!this.isAdmin||searchType!="全部商机"){
            if(searchType=="我负责的商机"){
                this.actions.ListMyDuty_chance(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
            if(searchType=="下属负责的商机"){
                this.actions.ListNestedSubPerson_chance(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
            if(searchType=="我参与的商机"){
                this.actions.ListMyParticipate_chance(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
            if(searchType=="全部商机"){
                this.actions.ListAllMy_chance(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
        }else{
            this.actions.getChanceByPage(page, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this));
        }
    },
    useTablePlugins: function (cpage,searchText,searchType) {
        console.log("this is useTablePlugins  page:"+cpage+";;;;text:"+searchText);
        if(jQuery(".laytable-box").length > 0) jQuery(".laytable-box").remove();
        var that = this;
        var cdata = [];
        var cols = [];
        var col = [];
        var sortField = "";
        var sortType = "";
        var chanceViewObject = this.template;
        var count = 15;
        sortField = chanceViewObject.sortField;
        sortType = chanceViewObject.sortType;
        var csize = this.container.getSize();
        var hsize = this.headTableNode.getSize();
        var tHeight = csize.y-hsize.y-80;

        if (!cpage)cpage = 1;

        /*for ( i in chanceListObject){
            cols.push(chanceListObject[i]);
        }*/
        cols = chanceViewObject.field;
        //cols.push(col);
        this._getCurrentPageData(function (json) {
            json.data.each(function (data ) {
                if(data.owneruser){
                    var owneruser = data.owneruser;
                    var createuser = data.createuser;
                    data.owneruser = owneruser.split("@")[0];
                    data.createuser = createuser.split("@")[0];
                }else{
                    data.owneruser = "";
                    data.createuser = "";
                }
                cdata.push(data);
            }.bind(this));

            cdata = json.data;
            layui.config({
                base: '/x_component_CRM/$Template/plugins/table2/'
            }).use(['table2', "table2"], function () {
                var table = layui.table2;
                console.log(cols);
                var tableIns = table.render({
                    elem: "#contentTable",
                    data: cdata,
                    height: tHeight,
                    width: '100%',
                    page: {
                        align: 'right',
                        groups: 5,//显示连续页码数量
                        curr:1,
                        count: json.count,//总条数
                        limit:10,
                        limits:[10, 20, 30, 40, 50, 60, 70, 80, 90]
                    },
                    initSort: {
                        sortField: sortField,
                        sortType: sortType
                    },
                    cols:[cols]
                });

                that.container.getElements(".chanceId").forEach(
                    function (e,i) {
                        //console.log("this is chanceId click fun:::::",e,i);
                        e.addEvent("click",function(){
                            console.log(this);
                            that._openDocument(this.get("id"),this.text);
                        }.bind(e))
                    }
                );

                that.container.getElements(".customerId").forEach(
                    function (e,i) {
                        //console.log("this is customerid click fun:::::",e,i);
                        e.addEvent("click",function(){
                            console.log(this);
                            that._openCustomer(this.get("id"),this.text);
                        }.bind(e))
                    }
                );
                jQuery(".laytable-page-pagination").find("a").each(function(index,element){
                        jQuery(element).on("click", function () {
                            //cpage = parseInt(jQuery(element).attr("value"))+cpage;
                            var topage = 1;
                            if(jQuery(element).attr("value")=="-1" || jQuery(element).attr("value")=="+1"){
                                topage = parseInt(jQuery(element).attr("value"))+topage;
                            }else{
                                topage = parseInt(jQuery(element).text());
                            }

                            if(jQuery(element).attr("class")!="page-item page-last rayui-disabled" && jQuery(element).attr("class")!="page-item page-prev rayui-disabled"){
                                //that.useTablePlugins(topage);
                                var searchText = jQuery(".headSearchInput").val();
                                var searchType ="";
                                if(jQuery(".headTableNode").find(".se-select-name").length > 0){
                                    searchType = jQuery(".headTableNode").find(".se-select-name").text();
                                }
                                if(searchText!=""){
                                    that.useTablePlugins(topage,searchText,searchType);
                                }else{
                                    that.useTablePlugins(topage,"",searchType);
                                }
                            }

                        });
                    }
                );

                jQuery(".laytable-page-btnok").on("click", function () {
                    var cpage = parseInt(jQuery(".laytable-page-input").val());
                    var searchText = jQuery(".headSearchInput").val();
                    var searchType ="";
                    if(jQuery(".headTableNode").find(".se-select-name").length > 0){
                        searchType = jQuery(".headTableNode").find(".se-select-name").text();
                    }
                    if(searchText!=""){
                        that.useTablePlugins(cpage,searchText,searchType);
                    }else{
                        that.useTablePlugins(cpage,"",searchType);
                    }
                });


                jQuery(".page-item").each(function(index,element){
                    if(jQuery(element).attr("value")==(cpage+"")){
                        jQuery(element).attr("class","page-item page-active");
                    }else{
                        if(jQuery(element).attr("value")!="-1" &&  jQuery(element).attr("value")!="+1"){
                            jQuery(element).attr("class","page-item");
                        }
                    }
                });
                var cCount = jQuery(".page-active").attr("value");
                var firstObj = jQuery(".page-prev").parent().next().find("a")[0];
                var lastObj = jQuery(".page-last").parent().prev().find("a")[0];
                if(parseInt(cCount)>parseInt(jQuery(firstObj).attr("value"))){
                    jQuery(".page-prev").attr("class","page-item page-prev");
                }else{
                    jQuery(".page-prev").attr("class","page-item page-prev rayui-disabled");
                }
                if(parseInt(cCount)==parseInt(jQuery(lastObj).attr("value"))){
                    jQuery(".page-last").attr("class","page-item page-last rayui-disabled");/////
                }else{
                    jQuery(".page-last").attr("class","page-item page-last");
                }
                jQuery(".laytable-page-input").attr("value",cpage+"");

            });


        }.bind(this),count, cpage,searchText,searchType);

    },
    _openDocument: function( id , name ){

        //if(this.customerRead){
        //    this.customerRead.load(documentData)
        //}else{
        MWF.xDesktop.requireApp("CRM", "ChanceOpen", function(){
            this.explorer = new MWF.xApplication.CRM.ChanceOpen(this, this.actions,{},{
                "openId":id,
                "openName":name,
                "openType":"single",
                "lp":this.lp,
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))

    },
    _openCustomer: function(openId ,openName){

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

});

MWF.xApplication.CRM.Chance.Document = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexDocument,

    "viewActionReturn":function(){
        return false
    }

});
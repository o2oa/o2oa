MWF.xApplication.CRM = MWF.xApplication.CRM || {};

MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("CRM", "Template", null,false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.CRM.Customer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp.customer;
        this.path = "/x_component_CRM/$Customer/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_CRM/$Customer/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        if(this.formContentArr)this.formContentArr.empty();
        this.formContentArr = new Array();
        if(this.formMarkArr)this.formMarkArr.empty();
        this.formMarkArr = new Array();

        this.rightContentDiv = this.app.rightContentDiv;
        this.createHeadContent();
        this.createToolBarContent();
        this.createCustomerContent();

        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));

        //if(this.customerView.customerRead) delete this.customerView.customerRead;
        //if(this.app.customerModule.hasMark) delete this.app.customerModule.hasMark;
        //if(this.formContentArr.length) this.formContentArr.empty();


            //this.customerView.formMarkNode.addEvents({
            //    "click": function (e) {
            //        alert(this.app.formContentArr.length)
            //        if(this.app.formContentArr.length){
            //            this.app.formContentArr[this.app.formContentArr.length-1].formContent.destroy();
            //
            //            this.app.formContentArr.splice(this.app.formContentArr.length-1,1);
            //        }
            //
            //        if(this.app.formContentArr.length==1){
            //            this.app.customerModule.hasMark = false;
            //            this.app.content.getElements(".formMarkNode").destroy();
            //
            //        }
            //        if(this.app.formContentArr.length==0){
            //            delete this.customerView.customerRead;
            //        }
            //        e.stopPropagation();
            //        //alert(this.app.formContentArr.length);
            //        //delete this.app.customerModule.currentFormContent;
            //        //
            //        //var obj = $(e.target);
            //        //var targetObj;
            //        //while(obj){
            //        //    if(obj.get("class")=="formContent"){
            //        //        targetObj = obj;
            //        //        break;
            //        //    }else{
            //        //        obj = obj.getParent();
            //        //    }
            //        //}
            //        //
            //        //if(!targetObj){
            //        //    if(this.app.content.getElements(".formContent")){
            //        //        this.app.content.getElements(".formContent").tween("width","0px");
            //        //    }
            //        //    //alert(this.app.content.getElements(".formMarkNode").length)
            //        //    if(this.app.content.getElements(".formMarkNode")){ //alert("destroy mark")
            //        //        this.app.content.getElements(".formMarkNode").destroy();
            //        //        this.app.customerModule.hasMark = false;
            //        //    }
            //        //}else{
            //        //    //alert("in")
            //        //}
            //    }.bind(this)
            //});



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
                this.explorer = new MWF.xApplication.CRM.Customer.CustomerForm(this, this.actions,{},{
                    "isEdited":true,
                    "isNew":true,
                    "onReloadView" : function(  ){
                        //alert(JSON.stringify(data))
                        this.reload();
                    }.bind(this)
                });
                this.explorer.load();
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
    createCustomerContent:function(){
        if(this.contentListDiv) this.contentListDiv.destroy();
        this.contentListDiv = new Element("div.contentListDiv",{"styles":this.css.contentListDiv}).inject(this.rightContentDiv);
        if(this.contentListInDiv) this.contentListInDiv.destroy();
        this.contentListInDiv = new Element("div.contentListInDiv",{"styles":this.css.contentListInDiv}).inject(this.contentListDiv);

        if(this.customerView) delete this.customerView;
        var templateUrl = this.path+"customerView.json";
        var filter = {};

        //this.customerView =  new  MWF.xApplication.CRM.Customer.View(this.contentListInDiv, this.app, {lp : this.app.lp.curtomerView, css : this.css, actions : this.actions }, { templateUrl : templateUrl,filterData:filter} );
        this.customerView =  new  MWF.xApplication.CRM.Customer.View(
            this.contentListInDiv,
            this.app,
            this,
            { templateUrl : templateUrl,filterData:filter},
            {
                lp:this.app.lp.curtomerView

            }
        );
        this.customerView.load();
        this.app.setScrollBar(this.contentListInDiv,this.customerView,"crm");
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
        if(this.contentListDiv)this.contentListDiv.setStyles({"height":(size.y-this.headContentDiv.getHeight()-8)+"px","width":(size.x)+"px"});
        if(this.contentListInDiv)this.contentListInDiv.setStyles({"height":this.contentListDiv.getHeight()+"px","width":(this.contentListDiv.getWidth()-8)+"px"});
    }

});


MWF.xApplication.CRM.Customer.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,

    _createDocument: function(data){
        return new MWF.xApplication.CRM.Customer.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        var category = this.category = this.options.category;

        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        if(id=="(0)")this.app.createShade();

        var filter = this.options.filterData || {};

        this.actions.getCustomerListNext(id, count, filter, function (json) {
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this));

    },
    _create: function(){

    },
    _openDocument: function( documentData ){

        //if(this.customerRead){
        //    this.customerRead.load(documentData)
        //}else{
            MWF.xDesktop.requireApp("CRM", "CustomerRead", function(){
                this.customerRead = new MWF.xApplication.CRM.CustomerRead(this.explorer.contentListDiv,this.app, this.explorer,this.actions,{
                    "width":1000
                } );
                this.customerRead.load(documentData);
                this.explorer.formContentArr.push(this.customerRead);
                this.explorer.formMarkArr.push(this.customerRead.formMarkNode);

            }.bind(this));
        //}























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

    "viewActionReturn":function(){
        return false
    }

});

MWF.xApplication.CRM.Customer.CustomerForm = new Class({
    Extends: MWF.xApplication.Template.Explorer.PopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "800",
        "height": "100%",
        "top" : 0,
        "left" : 0,
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": false,
        "closeAction": true
    },
    createFormNode: function () { //为了修改滚动条样式 "default"
        var _self = this;

        this.formNode = new Element("div.formNode", {
            "styles": this.css.formNode
        }).inject(this.formAreaNode);

        if (this.options.hasTop) {
            this.createTopNode();
        }

        if (this.options.hasIcon) {
            this.formIconNode = new Element("div.formIconNode", {
                "styles": this.isNew ? this.css.formNewNode : this.css.formIconNode
            }).inject(this.formNode);
        }

        this.createContent();
        //formContentNode.set("html", html);

        if (this.options.hasBottom) {
            this.createBottomNode();
        }

        this._setCustom();

        if( this.options.hasScroll ){
            //this.setScrollBar(this.formTableContainer)
            MWF.require("MWF.widget.ScrollBar", function () {
                new MWF.widget.ScrollBar(this.formTableContainer, {
                    "indent": false,
                    "style": "default",
                    "where": "before",
                    "distance": 30,
                    "friction": 4,
                    "axis": {"x": false, "y": true},
                    "onScroll": function (y) {
                        //var scrollSize = _self.viewContainerNode.getScrollSize();
                        //var clientSize = _self.viewContainerNode.getSize();
                        //var scrollHeight = scrollSize.y - clientSize.y;
                        //if (y + 200 > scrollHeight && _self.view && _self.view.loadElementList) {
                        //    if (!_self.view.isItemsLoaded) _self.view.loadElementList();
                        //}
                    }
                });
            }.bind(this));
        }
    },
    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp.customer.customerForm;
        this.path = "/x_component_CRM/$Customer/";
        this.cssPath = this.path + this.options.style + "/customerForm.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};
        this.actions = actions;
    },
    load: function () {
        var Ttype = "customer";
        this.actions.getProfiles(Ttype,function(json){
            this.profileData = json.data;
            this.profileData.customerType = {
                "name":"客户类型",
                "childNodes":[
                    {
                        "configname":"客户"
                    },
                    {
                        "configname":"合作伙伴"
                    }
                ]
            };





            this.allArrowArr = [];
            if (this.options.isNew) {
                this.create();
            } else if (this.options.isEdited) {
                this.edit();
            } else {
                this.open();
            }

            this.formContentNode.addEvents({
                "click": function () {
                    if(this.listContentDiv){
                        this.listContentDiv.destroy();
                    }
                    if(this.allArrowArr.length>0){
                        this.allArrowArr.each(function(d){
                            d.setStyles({
                                "background":"url(/x_component_CRM/$Template/default/icons/arrow.png) no-repeat center"
                            });
                        }.bind(this))
                    }

                }.bind(this)
            });

        }.bind(this));




    },
    getProfiles:function(){

    },
    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.options.title + ( this.data.title ? ("-" + this.data.title ) : "" )
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close();
                }.bind(this))
            }

            this.formTopContentNode = new Element("div", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode);

            this._createTopContent();

        }
    },
    _createTopContent: function () {

    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle'><span lable='TCustomerName'></span><span style='color:#f00'>*</span></td>" +
            "   <td styles='formTableValue' item='TCustomerName'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TCustomerType'></td>" +
            "   <td styles='formTableValue'><div id='TCustomerType'></div></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TCustomerLevel'></td>" +
            "   <td styles='formTableValue'><div id='TCustomerLevel'></div></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TSource'></td>" +
            "   <td styles='formTableValue'><div id='TSource'></div></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TIndustryFirst'></td>" +
            "   <td styles='formTableValue'><div id='TIndustryFirst'></div><div id='TIndustrySecond'></div></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TDistrict'></td>" +
            "   <td styles='formTableValue'><div id='TProvince'></div><div id='TCity'></div><div id='TArea'></div></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TStreet'></td>" +
            "   <td styles='formTableValue' item='TStreet'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TLocation'></td>" +
            "   <td styles='formTableValue'><div style='width:100%;height:30px;'><input type='text' placeholder='"+this.lp.TLocationNotice+"' id='mapLocation' disabled/></div></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle'></td>" +
            "   <td styles='formTableValue'><div id='mapDiv' styles='mapDiv'></div></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TTelphone'></td>" +
            "   <td styles='formTableValue' item='TTelphone'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TFax'></td>" +
            "   <td styles='formTableValue' item='TFax'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TRemark'></td>" +
            "   <td styles='formTableValue' item='TRemark'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TWebSite'></td>" +
            "   <td styles='formTableValue' item='TWebSite'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TEmail'></td>" +
            "   <td styles='formTableValue' item='TEmail'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TCustomerStatus'></td>" +
            "   <td styles='formTableValue'><div id='TCustomerStatus'></div></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='TCustomerGrade'></td>" +
            "   <td styles='formTableValue'><div id='TCustomerGrade'></div></td>" +
            "</tr>" +
            "</table>"
        this.formTableArea.set("html", html);
        this.loadForm();


        this.TCustomerType = this.formTableArea.getElement("#TCustomerType");
        this.TCustomerLevel = this.formTableArea.getElement("#TCustomerLevel");
        this.TSource = this.formTableArea.getElement("#TSource");
        this.TIndustryFirst = this.formTableArea.getElement("#TIndustryFirst");
        this.TIndustrySecond = this.formTableArea.getElement("#TIndustrySecond");
        this.TProvince = this.formTableArea.getElement("#TProvince");
        this.TCity = this.formTableArea.getElement("#TCity");
        this.TArea = this.formTableArea.getElement("#TArea");
        this.TCustomerStatus = this.formTableArea.getElement("#TCustomerStatus");
        this.TCustomerGrade = this.formTableArea.getElement("#TCustomerGrade");

        var size = {"width":230,"height":30};

        this.TIndustryFirst.setStyles({"float":"left"});
        this.TIndustrySecond.setStyles({"float":"left","margin-left":"10px"});

        this.TCustomerTypeSelector =  new MWF.xApplication.CRM.Template.Select(this.TCustomerType,this, this.actions, size);
        this.TCustomerTypeSelector.load(this.profileData.customerType);
        this.TCustomerLevelSelector =  new MWF.xApplication.CRM.Template.Select(this.TCustomerLevel,this, this.actions, size);
        this.TCustomerLevelSelector.load(this.profileData.customerType);
        this.TSourceSelector =  new MWF.xApplication.CRM.Template.Select(this.TSource,this, this.actions, size);
        this.TSourceSelector.load(this.profileData.customerType);
        this.TIndustryFirstSelector =  new MWF.xApplication.CRM.Template.Select(this.TIndustryFirst,this, this.actions, {"width":150,"height":30});
        this.TIndustryFirstSelector.load(this.profileData.customerType,function(){
            if(this.TIndustryFirst.get("value") == this.lp.defaultSelect){
                this.TIndustrySecondSelector.load();
            }else{
                this.TIndustrySecondSelector.setList(this.profileData.customerType);
            }
        }.bind(this));
        this.TIndustrySecondSelector =  new MWF.xApplication.CRM.Template.Select(this.TIndustrySecond,this, this.actions, {"width":150,"height":30,"available":false});
        this.TIndustrySecondSelector.load();


        this.TProvinceSelector =  new MWF.xApplication.CRM.Template.Select(this.TProvince,this, this.actions, {"width":100,"height":30});
        this.TProvinceSelector.load(this.profileData.customerType,function(){

        }.bind(this));
        this.TCitySelector =  new MWF.xApplication.CRM.Template.Select(this.TCity,this, this.actions, {"width":100,"height":30});
        this.TCitySelector.load(this.profileData.customerType,function(){

        }.bind(this));
        this.TAreaSelector =  new MWF.xApplication.CRM.Template.Select(this.TArea,this, this.actions, {"width":100,"height":30});
        this.TAreaSelector.load(this.profileData.customerType,function(){

        }.bind(this));
        this.TProvince.setStyles({"float":"left"});
        this.TCity.setStyles({"float":"left","margin-left":"10px"});
        this.TArea.setStyles({"float":"left","margin-left":"10px"});

        this.TCustomerStatusSelector =  new MWF.xApplication.CRM.Template.Select(this.TCustomerStatus,this, this.actions, size);
        this.TCustomerStatusSelector.load(this.profileData.customerType);
        this.TCustomerGradeSelector =  new MWF.xApplication.CRM.Template.Select(this.TCustomerGrade,this, this.actions, size);
        this.TCustomerGradeSelector.load(this.profileData.customerType);

        this.TMap = this.formTableArea.getElement("#mapDiv");
        this.TMap.addEvents({
            "mousewheel":function(e){
               e.stopPropagation();
            }
        });
        this.mapLocation = this.formTableArea.getElement("#mapLocation");
        this.mapLocation.setStyles({
            "width": "99%",
            "text-indent":"5px",
            "border":"1px solid #999",
            "background-color":"#eee",
            "border-radius": "3px",
            "box-shadow": "0px 0px 6px #eee",
            "height": "26px"
        });


        MWF.xDesktop.requireApp("CRM", "BaiduMap", function(){
            this.bMap = new MWF.xApplication.CRM.BaiduMap(this.TMap,this.app,this,this.actions,{"from":"newCustomer"});
            this.bMap.load();
        }.bind(this));

    },

    loadForm: function(){
        this.form = new MForm(this.formTableArea, this.data, {
            style: "default",
            isEdited: this.isEdited || this.isNew,
            itemTemplate: this.getItemTemplate(this.lp )
        },this.app,this.css);
        this.form.load();
        this.formTableArea.getElements("textarea").setStyles({"height":"100px","overflow":"auto"})

    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            TCustomerName: {
                text: lp.TCustomerName,
                type: "text",
                attr : {placeholder:lp.TCustomerName},
                notEmpty:true
            },
            TCustomerType:{
                text: lp.TCustomerType
            },
            TCustomerLevel: {
                text: lp.TCustomerLevel
            },
            TSource: {
                text: lp.TSource
            },
            TIndustryFirst:{
                text: lp.TIndustryFirst
            },
            TIndustrySecond:{

            },
            TDistrict:{
                text: lp.TDistrict
            },
            TStreet: {
                text: lp.TStreet,
                type: "text"
            },
            TLocation:{
                text: lp.TLocation,
                type : "text",
                attr : {"id":"mapLocation","disabled":true}
            },
            TTelphone: {
                text: lp.TTelphone,
                type: "text"
            },
            TFax: {
                text: lp.TFax,
                type: "text"
            },
            TRemark: {
                text: lp.TRemark,
                name:"TRemark",
                type: "textarea"
            },
            TWebSite: {
                text: lp.TWebSite,
                type: "text"
            },
            TEmail: {
                text: lp.TEmail,
                type: "text"
            },
            TCustomerStatus: {
                text: lp.TCustomerStatus
            },
            TCustomerGrade: {
                text: lp.TCustomerGrade
            }
        }
    },
    _createBottomContent: function () {

        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.actionCancel
        }).inject(this.formBottomNode);
        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.lp.actionConfirm
        }).inject(this.formBottomNode);

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));

    },
    _ok: function (data, callback) {
        var name = this.data.TCustomerName;
        var customertype = this.formTableArea.getElement("#TCustomerTypeValue").get("text") == this.lp.defaultSelect ? "":this.formTableArea.getElement("#TCustomerTypeValue").get("text");
        var level = this.formTableArea.getElement("#TCustomerTypeValue").get("text") == this.lp.defaultSelect ? "":this.formTableArea.getElement("#TCustomerTypeValue").get("text");
        var source = this.formTableArea.getElement("#TSourceValue").get("text") == this.lp.defaultSelect ? "":this.formTableArea.getElement("#TSourceValue").get("text");
        var industryfirst = this.formTableArea.getElement("#TIndustryFirstValue").get("text") == this.lp.defaultSelect ? "":this.formTableArea.getElement("#TIndustryFirstValue").get("text");
        var industrysecond = this.formTableArea.getElement("#TIndustrySecond").get("text") == this.lp.defaultSelect ? "":this.formTableArea.getElement("#TIndustrySecond").get("text");
        var province = this.formTableArea.getElement("#TProvinceValue").get("text") == this.lp.defaultSelect ? "":this.formTableArea.getElement("#TProvinceValue").get("text");
        var city = this.formTableArea.getElement("#TCityValue").get("text") == this.lp.defaultSelect ? "":this.formTableArea.getElement("#TCityValue").get("text");
        var county = this.formTableArea.getElement("#TAreaValue").get("text") == this.lp.defaultSelect ? "":this.formTableArea.getElement("#TAreaValue").get("text");
        var houseno = this.data.TStreet;
        var addresslongitude = this.data.lng;
        var addresslatitude = this.data.lat;
        var telno = this.data.TTelphone;
        var url = this.data.TWebSite;
        var email = this.data.TEmail;
        var state = this.formTableArea.getElement("#TCustomerStatusValue").get("text") == this.lp.defaultSelect ? "":this.formTableArea.getElement("#TCustomerStatusValue").get("text");
        var rank = this.formTableArea.getElement("#TCustomerGradeValue").get("text") == this.lp.defaultSelect ? "":this.formTableArea.getElement("#TCustomerGradeValue").get("text");
        var customerfax = this.data.TFax;
        var remark = this.data.TRemark;
        var qqno = "";
        var webchat = "";

        var saveData = {
            "customername":name,
            "customertype":customertype,
            "level":level,
            "source":source,
            "industryfirst":industryfirst,
            "industrysecond":industrysecond,
            "province":province,
            "city":city,
            "county":county,
            "houseno":houseno,
            "addresslongitude":addresslongitude,
            "addresslatitude":addresslatitude,
            "customerfax":customerfax,
            "telno":telno,
            "url":url,
            "remark":remark,
            "email":email,
            "state":state,
            "rank":rank,
            "qqno":qqno,
            "webchat":webchat
        };

        //alert(JSON.stringify(saveData))
        this.app.createShade();
        this.actions.saveCustomer(saveData,function(json){
            this.app.destroyShade();
            this.app.notice(this.lp.saveSuccess,"success");
            this.close();
            this.fireEvent("reloadView",json);
        }.bind(this),function(xhr,text,error){
            this.app.showErrorMessage(xhr,text,error);
            this.app.destroyShade();
        }.bind(this))
    }
});

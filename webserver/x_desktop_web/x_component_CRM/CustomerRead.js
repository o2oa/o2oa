MWF.xApplication.CRM = MWF.xApplication.CRM || {};

MWF.xDesktop.requireApp("CRM", "Template", null,false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.CRM.CustomerRead = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": 500,
        "height": 800,
        "top": 0,
        "left": 0,
        "hasTop": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasIcon": true,
        "hasScroll" : true,
        "hasBottom": true,
        "hasMask" : true,
        "marked": true,
        "title": "",
        "draggable": false,
        "maxAction" : "false",
        "closeAction": true,
        "relativeToApp" : true,
        "sizeRelateTo" : "app" //desktop
    },

    initialize: function (node,app,explorer, actions, options) {

        this.setOptions(options);
        this.app = app;
        this.explorer = explorer;
        this.lp = app.lp.customer.customerRead;
        this.path = "/x_component_CRM/$CustomerRead/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_CRM/$CustomerRead/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function (data) {
        this.data = data;
        var _self = this;
        var injectFor = this.explorer.formContentArr.length?this.node:this.app.content;
        if( this.options.hasMask ){
            this.formMaskNode = new Element("div.formMark", {
                "styles": this.css.formMaskNode,
                "id":"formmark"+_self.explorer.formContentArr.length,
                "events": {
                    "mouseover": function (e) {
                        e.stopPropagation();
                    },
                    "mouseout": function (e) {
                        e.stopPropagation();
                    },
                    "click": function () {
                        this.destroyForm();

                    }.bind(this)
                }
            }).inject(injectFor);

        }
        this.createForm();
        this.createTop();
        this.createContent();

        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));

    },
    reload:function(navi){
        this.createContent(navi);
    },
    reloadOpener:function(){
        this.fireEvent("reloadView");
    },
    destroyForm:function(){
        this.explorer.formMarkArr[this.explorer.formMarkArr.length-1].destroy(); //删除 遮罩
        var el = this.explorer.formContentArr[this.explorer.formContentArr.length-1].formContent;
        var myFx = new Fx.Tween(el);
        myFx.start("width",el.getWidth(),0).chain(function(){
            el.destroy(); //删除内容
        });

        this.explorer.formContentArr.splice(this.explorer.formContentArr.length-1,1);//去掉遮罩对象
        this.explorer.formMarkArr.splice(this.explorer.formMarkArr.length-1,1);//去掉内容对象
    },
    createForm:function(){

        if(this.formContent){
            //this.formContent.destroy();
            this.formContent.tween('width', this.options.width);
        } else{
            this.formContent = new Element("div.formContent",{
                "styles": this.css.formContent
            }).inject(this.node);
            this.formContent.tween('width', this.options.width);
            this.formContent.setStyles({"border-left":"1px solid #ffffff"});
        }

        var zindex = this.explorer.formContentArr.length?parseInt(this.node.getStyle("z-index"))+1:1;
        this.formContent.setStyles({"z-index":zindex});



        //this.formContent.set("text",this.data.customername);
        //this.testdiv = new Element("div.testdiv").inject(this.formContent);
        //this.testdiv.setStyles({
        //    "width":"200px",
        //    "height":"400px",
        //    "top":"400px",
        //    "left":"200px",
        //    "background-color":"#ff0",
        //    "position":"absolute"
        //
        //});
        //this.testdiv.addEvents({
        //    "click":function(){
        //        this.customerRead = new MWF.xApplication.CRM.CustomerRead(this.formContent,this.app,this.explorer, this.actions,{
        //            "hasMask" : true,
        //            "width":this.formContent.getWidth()-100,
        //            "marked":true
        //        } );
        //        this.customerRead.load({customername:"新窗口"});
        //        this.explorer.formContentArr.push(this.customerRead);
        //        this.explorer.formMarkArr.push(this.customerRead.formMaskNode);
        //
        //    }.bind(this)
        //})

    },
    createTop:function(){
        this.topDiv = new Element("div.topDiv",{"styles":this.css.topDiv}).inject(this.formContent);
        this.topCloseDiv = new Element("div.topCloseDiv",{"styles":this.css.topCloseDiv}).inject(this.topDiv);
        this.topCloseDiv.addEvents({
            "mouseenter":function(){
                this.setStyles({
                    "opacity":"1",
                    "filter":"alpha(opacity=100)"
                })
            },
            "mouseleave":function(){
                this.topCloseDiv.setStyles(this.css.topCloseDiv);
            }.bind(this),
            "click":function(){
                this.destroyForm();


            }.bind(this)
        });
    },
    createContent:function(navi){
        if(this.contentDiv) this.contentDiv.destroy();
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.formContent);
        this.contentDiv.setStyles({
            "height":(this.formContent.getHeight()-this.topDiv.getHeight())+"px",
            "width":"1000px"
        });
        this.slideContentDiv = new Element("div.slideContentDiv",{"styles":this.css.slideContentDiv}).inject(this.contentDiv);
        this.slideContentDiv.setStyles({
            "height":(this.contentDiv.getHeight())+"px",
            "width":(this.contentDiv.getWidth()-40)+"px"
        });
        this.setScrollBar(this.slideContentDiv);

        this.slideContentDiv.set("text","loading...");
        this.actions.getCustomerInfo(this.data.id,function(json){
            this.slideContentDiv.set("text","");
            this.customerData = json.data;
            this.createHeadContent();
            this.createNaviContent(navi);
        }.bind(this));
    },
    createHeadContent:function(){
        this.headContentDiv = new Element("div.headContentDiv",{"styles":this.css.headContentDiv}).inject(this.slideContentDiv);
        this.customerNameDiv = new Element("div.customerNameDiv",{"styles":this.css.customerNameDiv}).inject(this.headContentDiv);
        this.customerNameSpan = new Element("span.customerNameSpan",{
            "styles":this.css.customerNameSpan,
            "text":this.customerData.customername
        }).inject(this.customerNameDiv);
        this.customerMapA = new Element("a.customerMapA",{"styles":this.css.customerMapA,"text":this.lp.mapLocation}).inject(this.customerNameDiv);
        this.customerMapA.addEvents({
            "click":function(){
                MWF.xDesktop.requireApp("CRM", "BaiduMap", function(){
                    this.map = new MWF.xApplication.CRM.BaiduMap(this.app.content,this.app, this.explorer, this.actions );
                    this.map.loadMax({
                        "longitude":this.data.addresslongitude,
                        "latitude":this.data.addresslatitude
                    });

                }.bind(this));

            }.bind(this)
        });

        this.customerStatusDiv = new Element("div.customerStatusDiv",{"styles":this.css.customerStatusDiv}).inject(this.headContentDiv); //float:left
        this.customerStatusInfoDiv = new Element("div.customerStatusInfoDiv",{"styles":this.css.customerStatusInfoDiv}).inject(this.customerStatusDiv);
        this.customerStatusNumSpan = new Element("span.customerStatusNumSpan",{
            "styles":this.css.customerStatusNumSpan,
            "text":this.lp.TCustomerNO+": "+this.customerData.customersequence
        }).inject(this.customerStatusInfoDiv);
        this.customerStatusPersonSpan = new Element("span.customerStatusPersonSpan",{
            "styles":this.css.customerStatusPersonSpan,
            "text":this.lp.responsePerson+": "+this.customerData.customerownername
        }).inject(this.customerStatusInfoDiv);

        this.customerStatusInfo2Div = new Element("div.customerStatusInfo2Div",{"styles":this.css.customerStatusInfoDiv}).inject(this.customerStatusDiv);
        this.customerStatusLevelSpan = new Element("span.customerStatusLevelSpan",{
            "styles":this.css.customerStatusLevelSpan,
            "text":this.lp.TCustomerLevel+": " + this.customerData.level
        }).inject(this.customerStatusInfo2Div);
        this.customerStatusStatusSpan = new Element("span.customerStatusStatusSpan",{
            "styles":this.css.customerStatusStatusSpan,
            "text":this.lp.TCustomerStatus+": 成交"
        }).inject(this.customerStatusInfo2Div);


        this.customerOperationDiv = new Element("div.customerOperationDiv",{"styles":this.css.customerOperationDiv}).inject(this.headContentDiv); //float:right

    },
    createNaviContent:function(navi){
        this.naviDiv = new Element("div.naviDiv",{"styles":this.css.naviDiv}).inject(this.slideContentDiv);
        this.summaryNavi = new Element("span.naviSpan",{
            "styles":this.css.naviSpan,
            "text":this.lp.navi.summary
        }).inject(this.naviDiv);
        this.summaryNavi.addEvents({
            "click":function(){
                this.openTab(this.summaryNavi);
            }.bind(this)
        });
        this.summaryNavi.click();
        this.customerInfoNavi = new Element("span.naviSpan",{
            "styles":this.css.naviSpan,
            "text":this.lp.navi.customerInfo
        }).inject(this.naviDiv);
        this.customerInfoNavi.addEvents({
            "click":function(){
                this.openTab(this.customerInfoNavi);
            }.bind(this)
        });
        this.addressNavi = new Element("span.naviSpan",{
            "styles":this.css.naviSpan,
            "text":this.lp.navi.address
        }).inject(this.naviDiv);
        this.addressNavi.addEvents({
            "click":function(){
                this.openTab(this.addressNavi);
            }.bind(this)
        });
        this.contactNavi = new Element("span.naviSpan",{
            "styles":this.css.naviSpan,
            "text":this.lp.navi.contact
        }).inject(this.naviDiv);
        this.contactNavi.addEvents({
            "click":function(){
                this.openTab(this.contactNavi);
            }.bind(this)
        });
        this.chanceNavi = new Element("span.naviSpan",{
            "styles":this.css.naviSpan,
            "text":this.lp.navi.chance
        }).inject(this.naviDiv);
        this.chanceNavi.addEvents({
            "click":function(){
                this.openTab(this.chanceNavi);
            }.bind(this)
        });
        this.clueNavi = new Element("span.naviSpan",{
            "styles":this.css.naviSpan,
            "text":this.lp.navi.clue
        }).inject(this.naviDiv);
        this.clueNavi.addEvents({
            "click":function(){
                this.openTab(this.clueNavi);
            }.bind(this)
        });
        this.bargainNavi = new Element("span.naviSpan",{
            "styles":this.css.naviSpan,
            "text":this.lp.navi.bargain
        }).inject(this.naviDiv);
        this.bargainNavi.addEvents({
            "click":function(){
                this.openTab(this.bargainNavi);
            }.bind(this)
        });
        this.visitorNavi = new Element("span.naviSpan",{
            "styles":this.css.naviSpan,
            "text":this.lp.navi.visitor
        }).inject(this.naviDiv);
        this.visitorNavi.addEvents({
            "click":function(){
                this.openTab(this.visitorNavi);
            }.bind(this)
        });
        this.attachmentNavi = new Element("span.naviSpan",{
            "styles":this.css.naviSpan,
            "text":this.lp.navi.attachment
        }).inject(this.naviDiv);
        this.attachmentNavi.addEvents({
            "click":function(){
                this.openTab(this.attachmentNavi);
            }.bind(this)
        });
        if(navi=="customerInfo"){
            this.customerInfoNavi.click();
        }

    },
    openTab:function(obj){
        this.naviDiv.getElements("span").setStyles({"border-bottom":"0px"});
        obj.setStyles({"border-bottom":"3px solid #ff8e31"});
        if(obj.get("text")==this.lp.navi.customerInfo){
            this.createCustomerInfo();
        }
    },
    createCustomerInfo:function(){
        if(this.contentInfoDiv) this.contentInfoDiv.destroy();
        this.contentInfoDiv = new Element("div.contentInfoDiv",{"styles":this.css.contentInfoDiv}).inject(this.slideContentDiv);

        this.customerInfoDiv = new Element("div.customerInfoDiv",{"styles":this.css.customerInfoDiv}).inject(this.contentInfoDiv);
        this.customerInfoDiv.setStyles({"min-height":(this.contentDiv.getHeight()-this.headContentDiv.getHeight()-this.naviDiv.getHeight()-30)+"px"});
        this.contentToolbarDiv = new Element("div.contentToolbarDiv",{"styles":this.css.contentToolbarDiv}).inject(this.customerInfoDiv);
        this.contentToolbarSpan = new Element("span.contentToolbarSpan",{
            "styles":this.css.contentToolbarSpan,
            "text":this.lp.customerInfo
        }).inject(this.contentToolbarDiv);
        this.contentToolbarBottonEditDiv = new Element("div.contentToolbarBottonDiv",{
            "styles":this.css.contentToolbarBottonDiv,
            "text":this.lp.editCustomer
        }).inject(this.contentToolbarDiv);
        this.contentToolbarBottonEditDiv.addEvents({
            "click":function(){
                MWF.xDesktop.requireApp("CRM", "CustomerEdit", function(){
                    this.explorerForm = new MWF.xApplication.CRM.CustomerEdit(this, this.actions,{id:this.customerData.id},{
                        "isEdited":true,
                        "isNew":true,
                        "onReloadView" : function( ){
                            this.reload("customerInfo");
                            this.reloadOpener();
                        }.bind(this)
                    });
                    this.explorerForm.load();

                }.bind(this));
            }.bind(this)
        });


        this.customerDetailsDiv = new Element("div.customerDetailsDiv",{"styles":this.css.customerDetailsDiv}).inject(this.customerInfoDiv);
        //客户名称
        var detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        var detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TCustomerName
        }).inject(detailsDiv);
        var detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.customername && this.customerData.customername!=""?this.customerData.customername:"--"
        }).inject(detailsDiv);
        //客户编号
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TCustomerNO
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.customerno && this.customerData.customerno!=""?this.customerData.customerno:"--"
        }).inject(detailsDiv);
        //客户类型
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TCustomerType
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.customertype && this.customerData.customertype!=""?this.customerData.customertype:"--"
        }).inject(detailsDiv);
        //客户级别
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TCustomerLevel
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue", {
            "styles": this.css.detailsValue,
            "text": this.customerData.level && this.customerData.level != "" ? this.customerData.level : "--"
        }).inject(detailsDiv);
        //来源
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TSource
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue", {
            "styles": this.css.detailsValue,
            "text": this.customerData.source && this.customerData.source != "" ? this.customerData.source : "--"
        }).inject(detailsDiv);
        //行业
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TIndustryFirst
        }).inject(detailsDiv);
        var firstId = this.customerData.industryfirst && this.customerData.industryfirst!=""?this.customerData.industryfirst:"--";
        var secondId = this.customerData.industrysecond && this.customerData.industrysecond!=""?this.customerData.industrysecond:"--";
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":firstId + "-"+secondId
        }).inject(detailsDiv);
        //省、市、区
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TDistrict
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.area && this.customerData.area != "" ? this.customerData.area : "--"
        }).inject(detailsDiv);
        //详细地址
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TStreet
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.houseno && this.customerData.houseno != "" ? this.customerData.houseno : "--"
        }).inject(detailsDiv);
        //定位
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TLocation
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.houseno && this.customerData.houseno != "" ? this.customerData.houseno : "--"
        }).inject(detailsDiv);
        //电话
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TTelphone
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.telno && this.customerData.telno != "" ? this.customerData.telno : "--"
        }).inject(detailsDiv);
        //传真
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TFax
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.customerfax && this.customerData.customerfax != "" ? this.customerData.customerfax : "--"
        }).inject(detailsDiv);
        //备注
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TRemark
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.remark && this.customerData.remark != "" ? this.customerData.remark : "--"
        }).inject(detailsDiv);
        //网址
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TWebSite
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.url && this.customerData.url != "" ? this.customerData.url : "--"
        }).inject(detailsDiv);
        //邮件
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TEmail
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.email && this.customerData.email != "" ? this.customerData.email : "--"
        }).inject(detailsDiv);
        //客户状态
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TCustomerStatus
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.state && this.customerData.state != "" ? this.customerData.state : "--"
        }).inject(detailsDiv);
        //客户分级
        detailsDiv = new Element("div.detailsDiv",{"styles":this.css.detailsDiv}).inject(this.customerDetailsDiv);
        detailsTitle = new Element("span.detailsTitle",{
            "styles":this.css.detailsTitle,
            "text":this.lp.TCustomerGrade
        }).inject(detailsDiv);
        detailsValue = new Element("span.detailsValue",{
            "styles":this.css.detailsValue,
            "text":this.customerData.customerrank && this.customerData.customerrank != "" ? this.customerData.customerrank : "--"
        }).inject(detailsDiv);
    },
    resizeWindow:function(){
        var size = this.formContent.getSize();
        if(this.contentDiv)this.contentDiv.setStyles({"height":(size.y-this.topDiv.getHeight())+"px"});
        if(this.slideContentDiv) this.slideContentDiv.setStyles({
            "height":(this.formContent.getHeight()-this.topDiv.getHeight())+"px",
            "width":(this.formContent.getWidth()-40)+"px"
        });
        if(this.customerInfoDiv)this.customerInfoDiv.setStyles({
            "min-height":(this.contentDiv.getHeight()-this.headContentDiv.getHeight()-this.naviDiv.getHeight()-30)+"px"
        });
        //if(this.contentInfoDiv) this.contentInfoDiv.setStyles({
        //    "height":(this.slideContentDiv.getHeight()-this.headContentDiv.getHeight()-this.naviDiv.getHeight()-30)+"px",
        //    "width":(this.slideContentDiv.getWidth()-20)+"px"
        //});
    },
    setScrollBar: function(node, view, style, offset, callback){
        if (!style) style = "default";
        if (!offset){
            offset = {
                "V": {"x": 0, "y": 0},
                "H": {"x": 0, "y": 0}
            };
        }
        MWF.require("MWF.widget.ScrollBar", function(){
            //if(this.scrollbar && this.scrollbar.scrollVAreaNode){
            //    this.scrollbar.scrollVAreaNode.destroy();
            //    delete this.scrollbar;
            //}
            this.scrollbar = new MWF.widget.ScrollBar(node, {
                "style": style||"default",
                "offset": offset,
                "indent": false,
                "distance": 50,
                "onScroll": function (y) {
                    var scrollSize = node.getScrollSize();
                    var clientSize = node.getSize();
                    var scrollHeight = scrollSize.y - clientSize.y;
                    //var view = this.baseView || this.centerView;
                    if (y + 20 > scrollHeight && view && view.loadElementList) {
                        if (! view.isItemsLoaded)view.loadElementList();
                    }
                }.bind(this)
            });
            if (callback) callback();
        }.bind(this));
        return false;
    }

});
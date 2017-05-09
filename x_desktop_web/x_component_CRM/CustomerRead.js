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
        "hasMark" : true,
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
        if( this.options.hasMark ){
            this.formMarkNode = new Element("div.formMarkNode", {
                "styles": this.css.formMarkNode,
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
        //            "hasMark" : true,
        //            "width":this.formContent.getWidth()-100,
        //            "marked":true
        //        } );
        //        this.customerRead.load({customername:"新窗口"});
        //        this.explorer.formContentArr.push(this.customerRead);
        //        this.explorer.formMarkArr.push(this.customerRead.formMarkNode);
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
    createContent:function(){
        if(this.contentDiv) this.contentDiv.destroy();
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.formContent);
        this.contentDiv.setStyles({
            "height":(this.formContent.getHeight()-this.topDiv.getHeight())+"px"
        });
        this.slideContentDiv = new Element("div.slideContentDiv",{"styles":this.css.slideContentDiv}).inject(this.contentDiv);
        this.app.setScrollBar(this.slideContentDiv);

        this.actions.getCustomerInfo(this.data.id,function(json){
            this.customerData = json.data;
            this.createHeadContent();
            this.createNaviContent();
            this.createContentInfo();
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
        this.customerOperationDiv = new Element("div.customerOperationDiv").inject(this.headContentDiv); //float:right

    },
    createNaviContent:function(){

    },
    createContentInfo:function(){

    },
    resizeWindow:function(){
        var size = this.formContent.getSize();
        if(this.contentDiv)this.contentDiv.setStyles({"height":(size.y-this.topDiv.getHeight())+"px"});
    }

});
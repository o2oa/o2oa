MWF.xApplication.Strategy = MWF.xApplication.Strategy || {};

MWF.xDesktop.requireApp("Strategy", "Template", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xDesktop.requireApp("Strategy","Attachment",null,false);

MWF.xApplication.Strategy.ImportTips = new Class({
    Extends: MTooltips,
    Implements: [Options, Events],
    options : {
        style : "", //如果有style，就加载 style/css.wcss
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "auto", //x轴上left center right,  auto 系统自动计算
            y : "auto" //y 轴上top middle bottom, auto 系统自动计算
        },
        priorityOfAuto :{
            x : [ "center", "right", "left" ], //当position x 为 auto 时候的优先级
            y : [ "middle", "bottom", "top" ] //当position y 为 auto 时候的优先级
        },
        isFitToContainer : true, //当position x 不为 auto， y 不为 auto 的时候，自动设置偏移量，使tooltip不超过容器的可见范围
        event : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        hiddenDelay : 1000, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : true,
        isAutoShow : false,
        isAutoHide : true,
        hasCloseAction : true,
        overflow : "hidden", //弹出框超过container的时候怎么处理，hidden 表示超过的隐藏，scroll 表示超过的时候显示滚动条,
        nodeStyles : {
            "min-width" : "300px",
            "min-height":"100px",
            "border-radius" : "4px"
        }
    },
    //_getHtml : function(){
    //
    //    var container = new Element("div");
    //    var divClick = new Element("div.click",{
    //        "styles":{
    //            "width":"50px",
    //            "height":"30px",
    //            "cursor":"pointer"
    //        },
    //        "text":"关闭"
    //    }).inject(container);
    //    divClick.addEvents({
    //        "click":function(){
    //            alert("click")
    //        }.bind(this)
    //    });
    //
    //    var html = container.get("html");
    //
    //    return html;
    //},
    _customNode : function( node, contentNode ){
        var container = new Element("div").inject(contentNode);
        var divClick = new Element("div.click",{
            "styles":{
                "width":"50px",
                "height":"30px",
                "cursor":"pointer"
            },
            "text":"关闭"
        }).inject(container);
        divClick.addEvents({
            "click":function(){
                this.destroy()
            }.bind(this)
        });
    }

});



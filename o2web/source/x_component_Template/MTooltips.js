var MTooltips = new Class({
    Extends: MWF.widget.Common,
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
        offset : {
            x : 0,
            y : 0
        },
        isFitToContainer : true, //当position x 不为 auto， y 不为 auto 的时候，自动设置偏移量，使tooltip不超过容器的可见范围
        event : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : true,
        isAutoShow : true,
        isAutoHide : true,
        hasMask : true,
        hasCloseAction : false,
        hideByClickBody : false,
        overflow : "hidden" //弹出框高宽超过container的时候怎么处理，hidden 表示超过的隐藏，scroll 表示超过的时候显示滚动条
    },
    initialize : function( container, target, app, data, options, targetCoordinates ){
        //可以传入target 或者 targetCoordinates，两种选一
        //传入target,表示触发tooltip的节点，本类根据 this.options.event 自动绑定target的事件
        //传入targetCoordinates，表示 出发tooltip的位置，本类不绑定触发事件
        if( options ){
            this.setOptions(options);
        }
        this.container = container;
        this.target = target;
        this.targetCoordinates = targetCoordinates;
        this.app = app;
        if(app)this.lp = app.lp;
        this.data = data;

        this.path = "/x_component_Template/$MTooltips/";

        if( this.target ){
            this.setTargetEvents();
        }
        this.fireEvent("postInitialize",[this]);
    },
    setTargetEvents : function(){
        if( this.options.event == "click" ){
            if( this.options.isAutoShow ){
                this.targetClickFun = function( ev ){
                    if( this.status === "display" ){
                        this.hide();
                    }else{
                        this.load();
                    }
                    ev.stopPropagation();
                }.bind(this);
                this.target.addEvents({
                    "mousedown" : function(ev){
                        ev.stopPropagation();
                    },
                    "click": this.targetClickFun
                });
            }
        }else{
            if( this.options.isAutoHide || this.options.isAutoShow ){
                this.targetMouseenterFun = function(){
                    if( this.timer_hide ){
                        clearTimeout(this.timer_hide);
                    }
                }.bind(this);
                this.target.addEvents({
                    "mouseenter": this.targetMouseenterFun
                });
            }
            if( this.options.isAutoShow ){
                this.targetMouseenterFun2 = function(){
                    if( this.status != "display" ){
                        this.timer_show = setTimeout( this.load.bind(this),this.options.displayDelay );
                    }
                }.bind(this);
                this.target.addEvents({
                    "mouseenter": this.targetMouseenterFun2
                });
            }

            if( this.options.isAutoHide || this.options.isAutoShow ){
                this.targetMouseleaveFun = function(){
                    if( this.timer_show ){
                        clearTimeout(this.timer_show);
                    }
                }.bind(this);
                this.target.addEvents({
                    "mouseleave" : this.targetMouseleaveFun
                });
            }
            if( this.options.isAutoHide ){
                this.targetMouseleaveFun2 = function(){
                    if( this.status == "display" ){
                        this.timer_hide = setTimeout( this.hide.bind(this),this.options.hiddenDelay );
                    }
                }.bind(this);
                this.target.addEvents({
                    "mouseleave" : this.targetMouseleaveFun2
                });
            }
        }
    },
    load: function(){
        this.fireEvent("queryLoad",[this]);
        if( this.isEnable() ){
            if( this.node ){
                this.show();
            }else{
                this.create();
            }

            if( this.options.event == "click" ) {
                if( this.options.isAutoHide ){
                    if( !this.options.hasMask ){
                        this.containerMousedownFun = function(e){
                            if( this.status === "display" ){
                                this.hide();
                            }
                            e.stopPropagation();
                        }.bind(this);
                        this.container.addEvent("mousedown", this.containerMousedownFun )
                    }
                }
                if( this.options.hideByClickBody ){
                    this.bodyMousedownFun = function(e){
                        if( this.status === "display" ){
                            this.hide();
                        }
                        e.stopPropagation();
                    }.bind(this);
                    $(document.body).addEvent("mousedown", this.bodyMousedownFun )
                }
            }
        }
        this.fireEvent("postLoad",[this]);
    },
    hide: function(){
        if( this.node ){
            this.node.setStyle("display","none");
            this.status = "hidden";
            if( this.maskNode ){
                this.maskNode.setStyle("display","none");
            }

            if( this.containerMousedownFun ){
                this.container.removeEvent("mousedown", this.containerMousedownFun );
                this.containerMousedownFun = null;
            }

            this.fireEvent("hide",[this]);
        }
    },
    show: function(){
        this.status = "display";
        if( this.maskNode ){
            this.maskNode.setStyle("display","");
        }
        this.node.setStyle("display","");
        this.setCoondinates();

        this.fireEvent("show",[this]);
    },
    create: function(){
        this.status = "display";
        this.fireEvent("queryCreate",[this]);
        this.loadStyle();

        this.node = new Element("div.tooltipNode", {
            styles : this.nodeStyles
        }).inject( this.container );

        if( this.contentNode ){
            this.contentNode.inject( this.node );
        }else{
            this.contentNode = new Element("div",{
                styles : this.contentStyles
            }).inject( this.node );
            this.contentNode.set("html", this._getHtml() );
        }
        this._customNode( this.node, this.contentNode );

        if( this.options.hasArrow ){
            this.arrowNode = new Element("div.arrowNode", {
                    "styles": this.arrowStyles
                }
            ).inject(this.node);
        }

        if( this.options.hasCloseAction ){
            this.closeActionNode = new Element("div", {
                styles : this.closeActionStyles,
                events : {
                    click : function(){ this.hide() }.bind(this)
                }
            }).inject( this.node );
        }

        this._loadCustom( function(){
            this.setCoondinates();
        }.bind(this));

        if( this.options.event == "click" ) {
            if( this.options.isAutoHide ){
                if( this.options.hasMask ){
                    this.maskNode = new Element("div.maskNode", {
                        "styles": this.maskStyles,
                        "events": {
                            "mouseover": function (e) {
                                e.stopPropagation();
                            },
                            "mouseout": function (e) {
                                e.stopPropagation();
                            },
                            "click": function (e) {
                                this.hide();
                                e.stopPropagation();
                            }.bind(this)
                        }
                    }).inject( this.container );
                }

                if( this.app ){
                    this.hideFun_resize = this.hide.bind(this);
                    this.app.addEvent( "resize" , this.hideFun_resize );
                }
            }
        }else{
            if( this.options.isAutoHide || this.options.isAutoShow ){
                this.node.addEvents({
                    "mouseenter": function(){
                        if( this.timer_hide )clearTimeout(this.timer_hide);
                    }.bind(this)
                });
            }
            if( this.options.isAutoHide ){
                this.node.addEvents({
                    "mouseleave" : function(){
                        this.timer_hide = setTimeout( this.hide.bind(this),this.options.hiddenDelay );
                    }.bind(this)
                });
            }
        }

        //this.target.addEvent( "mouseleave", function(){
        //    this.timer_hide = setTimeout( this.hide.bind(this), this.options.HiddenDelay );
        //}.bind(this));
        this.fireEvent("postCreate",[this]);
    },
    loadStyle : function(){
        if( this.options.style ){
            this.cssPath = this.path+this.options.style+"/css.wcss";
            this._loadCss();
        }
        this.nodeStyles = {
            "font-size" : "12px",
            "position" : "absolute",
            "max-width" : "500px",
            "min-width" : "260px",
            "z-index" : "11",
            "background-color" : "#fff",
            "padding" : "10px",
            "border-radius" : "8px",
            "box-shadow": "0 0 18px 0 #999999",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        };
        if( this.options.nodeStyles ){ //兼容之前在options里设置nodeStyles
            this.nodeStyles = Object.merge( this.nodeStyles, this.options.nodeStyles );
        }else if( this.css && this.css.nodeStyles ){
            this.nodeStyles = this.css.nodeStyles
        }

        if( this.css && this.css.contentStyles ){
            this.contentStyles = this.css.contentStyles;
        }else{
            this.contentStyles = {
                "width" : "100%",
                "height" : "100%"
            }
        }

        if( this.options.hasArrow ){
            if( this.css && this.css.arrowStyles ){
                this.arrowStyles = this.css.arrowStyles
            }else{
                this.arrowStyles = {
                    "width": this.options.axis == "x" ? "9px" : "17px",
                    "height" : this.options.axis == "x" ? "17px" : "9px",
                    "position":"absolute",
                    "background" : "no-repeat url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAAlCAYAAACgc9J8AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAP9JREFUeNq01oENhCAMBdCWuIgTuP8WOoGj9GhSDaeUfjiuCSEm+iKFVllESGNdVzrPk55xHIfewHnItm1MrVDMG/u+i0aeyWZp3R9CJRaBIWRB5YUHItAL80AEqmI1EIFcrAQ1rwjUxEoQgULsAue+2ayc/W83p5+z6RXggOO1WQGhrsFXP/Oip58tAaQTZ4QMbEbyEB2GCKEBdtpmru4NsC4aHg8PLJ/3lim2xDv02jYDz1kNQsGEQgiYeqAQBPuZQF1jFKqBYTn1RLK1D4+v3E3NGfgNkJlfdKi0XrUZgc1OWyt0Dwz/z37tGhA20s+WR4t+1iBbzTJyaD8CDAB7WgNSzh/AnwAAAABJRU5ErkJggg==)"
                }
            }
        }

        if( this.options.event == "click" && this.options.isAutoHide ) {
            if( this.css && this.css.maskStyles ){
                this.maskStyles = this.css.maskStyles;
            }else{
                this.maskStyles = {
                    "width": "100%",
                    "height": "100%",
                    "opacity": 0,
                    "position": "absolute",
                    "background-color": "#fff",
                    "top": "0px",
                    "left": "0px"
                }
            }
        }

        if( this.options.hasCloseAction ){
            if( this.css && this.css.closeActionStyles ){
                this.closeActionStyles = this.css.closeActionStyles
            }else{
                this.closeActionStyles = {
                    "width": "24px",
                    "height" : "24px",
                    "position" : "absolute",
                    "top": "0px",
                    "right" : "0px",
                    "background": "url(/x_component_Template/$MTooltips/default/icon/off_gray.png) no-repeat center center",
                    "cursor": "pointer"
                }
            }
        }
    },
    isEnable : function(){
        return !this.disable;
    },
    setCoondinates : function(){
        if( this.options.axis == "x" ){
            this.setCoondinates_x();
        }else{
            this.setCoondinates_y();
        }
    },
    setCoondinates_x : function(){
        var targetCoondinates = this.target ? this.target.getCoordinates( this.container ) : this.targetCoordinates ;
        var node = this.node;
        if( this.resetWidth ){
            node.setStyles({
                overflow : "visible",
                width : "auto"
            });
            if(this.arrowNode)this.arrowNode.setStyle("display","");
            this.resetWidth = false;
        }
        var containerScroll = this.container.getScroll();
        var containerSize = this.container.getSize();
        var nodeSize = node.getSize();
        var left;
        var arrowX, arrowY;

        var offsetX = (parseFloat(this.options.offset.x).toString() !== "NaN") ? parseFloat(this.options.offset.x) : 0;
        offsetX += this.options.hasArrow ? 10 : 0;

        if( this.options.position.x == "left" ) {
            left = targetCoondinates.left - nodeSize.x - offsetX;
            this.positionX = "left";
            arrowX = "right";
        }else if( this.options.position.x == "right" ){
            left = targetCoondinates.right + offsetX;
            this.positionX = "right";
            arrowX = "left";
        }else{
            var priorityOfAuto = this.options.priorityOfAuto;
            if( priorityOfAuto && priorityOfAuto.x ){
                for( var i=0; i<priorityOfAuto.x.length; i++ ){
                    if( priorityOfAuto.x[i] == "left" ){
                        if( targetCoondinates.left - containerScroll.x > containerSize.x - targetCoondinates.right){
                            left = targetCoondinates.left - nodeSize.x - offsetX;
                            this.positionX = "left";
                            arrowX = "right";
                            break;
                        }
                    }
                    if( priorityOfAuto.x[i] == "right" ){
                        if( containerSize.x  + containerScroll.x - targetCoondinates.right > nodeSize.x ){
                            left = targetCoondinates.right + offsetX;
                            this.positionX = "right";
                            arrowX = "left";
                            break;
                        }
                    }
                }
            }
            if( !left ){
                if( targetCoondinates.left - containerScroll.x > containerSize.x - targetCoondinates.right){
                    left = targetCoondinates.left - nodeSize.x - offsetX;
                    this.positionX = "left";
                    arrowX = "right";
                }else{
                    left = targetCoondinates.right + offsetX;
                    this.positionX = "right";
                    arrowX = "left";
                }
            }
        }

        var top;
        if( this.options.position.y == "middle" ){
            top = targetCoondinates.top + (targetCoondinates.height/2) - ( nodeSize.y / 2 ) ;
            this.positionY = "middle";
            arrowY = "middle";
        }else if( this.options.position.y == "top" ){
            top = targetCoondinates.bottom - nodeSize.y;
            this.positionY = "top";
            arrowY = "bottom";
        }else if( this.options.position.y == "bottom" ){
            top = targetCoondinates.top;
            this.positionY = "bottom";
            arrowY = "top";
        }else{
            var priorityOfAuto = this.options.priorityOfAuto;
            if( priorityOfAuto && priorityOfAuto.y ){
                for( var i=0; i<priorityOfAuto.y.length; i++ ){
                    if( priorityOfAuto.y[i] == "middle" ){
                        if( targetCoondinates.top + (targetCoondinates.height/2) - ( nodeSize.y / 2 ) > containerScroll.y &&
                            targetCoondinates.bottom - (targetCoondinates.height/2) + ( nodeSize.y / 2 ) - containerScroll.y < containerSize.y ){
                            top = targetCoondinates.top + (targetCoondinates.height/2) - ( nodeSize.y / 2 ) ;
                            this.positionY = "middle";
                            arrowY = "middle";
                            break;
                        }
                    }
                    if( priorityOfAuto.y[i] == "top" ){
                        if( targetCoondinates.top - containerScroll.y > containerSize.y - targetCoondinates.bottom ){
                            top = targetCoondinates.bottom - nodeSize.y;
                            this.positionY = "top";
                            arrowY = "bottom";
                            break;
                        }
                    }
                    if( priorityOfAuto.y[i] == "bottom" ){
                        if( containerSize.y  + containerScroll.y - targetCoondinates.bottom > nodeSize.y ){
                            top = targetCoondinates.top;
                            this.positionY = "bottom";
                            arrowY = "top";
                            break;
                        }
                    }
                }
            }
            if( !top ){
                if( targetCoondinates.top + (targetCoondinates.height/2) - ( nodeSize.y / 2 ) > containerScroll.y &&
                    targetCoondinates.bottom - (targetCoondinates.height/2) + ( nodeSize.y / 2 ) - containerScroll.y < containerSize.y ){
                    top = targetCoondinates.top + (targetCoondinates.height/2) - ( nodeSize.y / 2 ) ;
                    this.positionY = "middle";
                    arrowY = "middle";
                } else if( targetCoondinates.top - containerScroll.y > containerSize.y - targetCoondinates.bottom ){
                    top = targetCoondinates.bottom - nodeSize.y;
                    this.positionY = "top";
                    arrowY = "bottom";
                }else{
                    top = targetCoondinates.top;
                    this.positionY = "bottom";
                    arrowY = "top";
                }
            }
        }

        var arrowOffsetY = 0;
        if( this.options.isFitToContainer ){
            if( top < containerScroll.y ){
                arrowOffsetY = containerScroll.y - top;
                top = containerScroll.y;
            }else if( top + nodeSize.y > containerSize.y  + containerScroll.y ){
                arrowOffsetY = containerSize.y  + containerScroll.y - top - nodeSize.y;
                top = containerSize.y  + containerScroll.y - nodeSize.y;
            }
        }

        if( this.options.overflow == "scroll" ){
            if( left < 0 ){
                node.setStyles({
                    "overflow" : "auto",
                    "width" : nodeSize.x + left - offsetX
                });
                this.resetWidth = true;
                left = 0
            }else if( left + nodeSize.x > containerSize.x  + containerScroll.x ){
                node.setStyles({
                    "overflow" : "auto",
                    "width" : Math.abs( containerSize.x  + containerScroll.x - left + offsetX )
                });
                left = left - offsetX;
                this.resetWidth = true;
            }
        }

        if( this.resetWidth ){
            if( this.arrowNode )this.arrowNode.setStyle("display","none");
        }else if( this.options.hasArrow && this.arrowNode ) {
            if (arrowX == "left") {
                this.arrowNode.setStyles({
                    "left": "-8px",
                    "right": "auto",
                    "background-position": "0px 0px"
                });
            } else {
                this.arrowNode.setStyles({
                    "left": "auto",
                    "right": "-8px",
                    "background-position": "-11px 0px"
                });
            }
            var ah = this.arrowNode.getSize().y / 2;
            //var th = targetCoondinates.height / 2 - ah;
            var h = Math.min(targetCoondinates.height, nodeSize.y) / 2 - ah;
            var radiusDv = 0; //圆角和箭头偏移量的差值
            var radius = 0;
            if (arrowY == "middle") {
                this.arrowNode.setStyles({
                    "top": (nodeSize.y / 2 - ah) - arrowOffsetY + "px",
                    "bottom": "auto"
                })
            } else if (arrowY == "top") {
                radius = this.node.getStyle("border-top-" + arrowX + "-radius");
                radius = radius ? parseInt(radius) : 0;
                if (radius > h) {
                    radiusDv = radius - h;
                }
                this.arrowNode.setStyles({
                    "top": h + radiusDv - arrowOffsetY + "px",
                    "bottom": "auto"
                })
            } else {
                radius = this.node.getStyle("border-bottom-" + arrowX + "-radius");
                radius = radius ? parseInt(radius) : 0;
                if (radius > h) {
                    radiusDv = radius - h;
                }
                this.arrowNode.setStyles({
                    "top": "auto",
                    "bottom": h + radiusDv + arrowOffsetY + "px"
                })
            }

            var t = top;
            if (radiusDv) {
                if (arrowY == "top") {
                    t = t - radiusDv;
                } else if (arrowY == "bottom") {
                    t = t + radiusDv;
                }
            }
        }
        node.setStyles({
            "left" : left,
            "top" : t || top
        });
        this.fireEvent( "postSetCoondinates", [arrowX, arrowY] );
    },
    setCoondinates_y : function(){
        var targetCoondinates = this.target ? this.target.getCoordinates( this.container ) : this.targetCoordinates ;
        var node = this.node;
        if( this.resetHeight ){
            node.setStyles({
                overflow : "visible",
                height : "auto"
            });
            if(this.arrowNode)this.arrowNode.setStyle("display","");
            this.resetHeight = false;
        }
        var containerScroll = this.container.getScroll();
        var containerSize = this.container.getSize();
        var nodeSize = node.getSize();
        var top;
        var arrowX, arrowY;

        var offsetY = (parseFloat(this.options.offset.y).toString() !== "NaN") ? parseFloat(this.options.offset.y) : 0;
        offsetY += this.options.hasArrow ? 10 : 0;

        if( this.options.position.y == "top" ){
            top = targetCoondinates.top - nodeSize.y - offsetY;
            this.positionY = "top";
            arrowY = "bottom";
        }else if( this.options.position.y == "bottom" ){
            top = targetCoondinates.bottom + offsetY;
            this.positionY = "bottom";
            arrowY = "top";
        }else{
            var priorityOfAuto = this.options.priorityOfAuto;
            if( priorityOfAuto && priorityOfAuto.y ){
                for( var i=0; i<priorityOfAuto.y.length; i++ ){
                    if( priorityOfAuto.y[i] == "top" ){
                        if( targetCoondinates.top - containerScroll.y > containerSize.y - targetCoondinates.bottom ){
                            top = targetCoondinates.top - nodeSize.y - offsetY;
                            this.positionY = "top";
                            arrowY = "bottom";
                            break;
                        }
                    }
                    if( priorityOfAuto.y[i] == "bottom" ){
                        if( containerSize.y  + containerScroll.y - targetCoondinates.bottom > nodeSize.y ){
                            top = targetCoondinates.bottom + offsetY;
                            this.positionY = "bottom";
                            arrowY = "top";
                            break;
                        }
                    }
                }
            }
            if( !top ){
                if( targetCoondinates.top - containerScroll.y > containerSize.y - targetCoondinates.bottom){
                    top = targetCoondinates.top - nodeSize.y - offsetY;
                    this.positionY = "top";
                    arrowY = "bottom";
                }else{
                    top = targetCoondinates.bottom + offsetY;
                    this.positionY = "bottom";
                    arrowY = "top";
                }
            }
        }

        var left;
        if( this.options.position.x == "center" ){
            left = targetCoondinates.left + (targetCoondinates.width/2) - ( nodeSize.x / 2 ) ;
            this.positionX = "center";
            arrowX = "center";
        }else if( this.options.position.x == "left" ){
            left = targetCoondinates.right - nodeSize.x;
            this.positionX = "left";
            arrowX = "right";
        }else if( this.options.position.x == "right" ){
            left = targetCoondinates.left;
            this.positionX = "right";
            arrowX = "left";
        }else{
            var priorityOfAuto = this.options.priorityOfAuto;
            if( priorityOfAuto && priorityOfAuto.x ){
                for( var i=0; i<priorityOfAuto.x.length; i++ ){
                    if( priorityOfAuto.x[i] == "center" ){
                        if( targetCoondinates.left + (targetCoondinates.width/2) - ( nodeSize.x / 2 ) > containerScroll.x &&
                            targetCoondinates.right - (targetCoondinates.width/2) + ( nodeSize.x / 2 ) - containerScroll.x < containerSize.x ){
                            left = targetCoondinates.left + (targetCoondinates.width/2) - ( nodeSize.x / 2 ) ;
                            this.positionX = "center";
                            arrowX = "center";
                            break;
                        }
                    }
                    if( priorityOfAuto.x[i] == "left" ){
                        if( targetCoondinates.left - containerScroll.x > containerSize.x - targetCoondinates.right){
                            left = targetCoondinates.right - nodeSize.x;
                            this.positionX = "left";
                            arrowX = "right";
                            break;
                        }
                    }
                    if( priorityOfAuto.x[i] == "right" ){
                        if( containerSize.x + containerScroll.x - targetCoondinates.right > nodeSize.x ){
                            left = targetCoondinates.left;
                            this.positionX = "right";
                            arrowX = "left";
                            break;
                        }
                    }
                }
            }
            if( !left ){
                if( targetCoondinates.left + (targetCoondinates.width/2) - ( nodeSize.x / 2 ) > containerScroll.x &&
                    targetCoondinates.right - (targetCoondinates.width/2) + ( nodeSize.x / 2 ) - containerScroll.x < containerSize.x ){
                    left = targetCoondinates.left + (targetCoondinates.width/2) - ( nodeSize.x / 2 ) ;
                    this.positionX = "center";
                    arrowX = "center";
                } else if( targetCoondinates.left - containerScroll.x > containerSize.x - targetCoondinates.right ){
                    left = targetCoondinates.right - nodeSize.x;
                    this.positionX = "left";
                    arrowX = "right";
                }else{
                    left = targetCoondinates.left;
                    this.positionX = "right";
                    arrowX = "left";
                }
            }
        }

        var arrowOffsetX = 0;
        if( this.options.isFitToContainer ){
            if( left < containerScroll.x ){
                arrowOffsetX = containerScroll.x - left;
                left = containerScroll.x;
            }else if( left + nodeSize.x > containerSize.x  + containerScroll.x ){
                arrowOffsetX = containerSize.x  + containerScroll.x - left - nodeSize.x;
                left = containerSize.x  + containerScroll.x - nodeSize.x;
            }
        }

        if( this.options.overflow == "scroll" ){
            if( top < 0 ){
                node.setStyles({
                    "overflow" : "auto",
                    "height" : nodeSize.y + top - offsetY
                });
                this.resetHeight = true;
                top = 0
            }else if( top + nodeSize.y > containerSize.y  + containerScroll.y ){
                node.setStyles({
                    "overflow" : "auto",
                    "height" : Math.abs( containerSize.y  + containerScroll.y - top + offsetY )
                });
                top = top - offsetY;
                this.resetHeight = true;
            }
        }

        if( this.resetHeight ){
            if( this.arrowNode )this.arrowNode.setStyle("display","none");
        }else if( this.options.hasArrow && this.arrowNode ){
            if( arrowY == "top" ){
                this.arrowNode.setStyles( {
                    "top" : "-8px",
                    "bottom" : "auto",
                    "background-position": "0px -18px"
                });
            }else{
                this.arrowNode.setStyles( {
                    "top" : "auto",
                    "bottom" : "-8px",
                    "background-position": "0px -28px"
                });
            }
            var aw = this.arrowNode.getSize().x / 2 ;
            //var tw = targetCoondinates.width / 2 - aw;
            var w = Math.min( targetCoondinates.width , nodeSize.x )/ 2 - aw;
            var radiusDv = 0; //圆角和箭头偏移量的差值
            var radius = 0; //圆角值
            if( arrowX == "center" ) {
                this.arrowNode.setStyles({
                    "left": (nodeSize.x/2 - aw - arrowOffsetX )+"px",
                    "right": "auto"
                })
            }else if( arrowX == "left" ){
                radius = this.node.getStyle("border-"+arrowY+"-left-radius");
                radius = radius ? parseInt( radius ) : 0;
                if( radius > w ){
                    radiusDv = radius - w;
                }
                this.arrowNode.setStyles({
                    "left" :  w + radiusDv - arrowOffsetX + "px",
                    "right" : "auto"
                })
            }else{
                radius = this.node.getStyle("border-" + arrowY + "-right-radius");
                radius = radius ? parseInt(radius) : 0;
                if( radius > w ){
                    radiusDv = radius - w;
                }
                this.arrowNode.setStyles({
                    "left" : "auto",
                    "right" : w + radiusDv + arrowOffsetX +"px"
                })
            }

            var l = left;
            if( radiusDv ){
                if( arrowX == "left" ){
                    l = l - radiusDv;
                }else if( arrowX == "right" ){
                    l = l + radiusDv;
                }
            }
        }

        node.setStyles({
            "left" : l || left,
            "top" : top
        });
        this.fireEvent( "postSetCoondinates", [arrowX, arrowY] );
    },
    setPosition : function(){
        if( this.options.axis == "x" ){
            this.setPosition_x();
        }else{
            this.setPosition_y();
        }
    },
    setPosition_x : function(){
        var top, left;
        var targetCoondinates = this.target ? this.target.getCoordinates( this.container ) : this.targetCoordinates ;
        var node = this.node;
        var nodeSize = node.getSize();
        var offsetX = (parseFloat(this.options.offset.x).toString() !== "NaN") ? parseFloat(this.options.offset.x) : 0;
        offsetX += this.options.hasArrow ? 10 : 0;
        if( this.positionX === "left" ){
            left = targetCoondinates.left - nodeSize.x - offsetX;
        }else if( this.positionX === "bottom" ){
            left = targetCoondinates.right + offsetX;
        }

        if( this.positionY === "top" ){
            top = targetCoondinates.top - nodeSize.y;
        }else if( this.positionY === "bottom" ){
            top = targetCoondinates.bottom;
        }else if( this.positionX === "middle" ){
            top = targetCoondinates.top + (targetCoondinates.height/2) - ( nodeSize.y / 2 )
        }
        node.setStyles({
            "left" : left,
            "top" : top
        });
    },
    setPosition_y : function(){
        var top, left;
        var targetCoondinates = this.target ? this.target.getCoordinates( this.container ) : this.targetCoordinates ;
        var node = this.node;
        var nodeSize = node.getSize();
        var offsetY = (parseFloat(this.options.offset.y).toString() !== "NaN") ? parseFloat(this.options.offset.y) : 0;
        offsetY += this.options.hasArrow ? 10 : 0;
        if( this.positionY === "top" ){
            top = targetCoondinates.top - nodeSize.y - offsetY;
        }else if( this.positionY === "bottom" ){
            top = targetCoondinates.bottom + offsetY;
        }

        if( this.positionX === "left" ){
            left = targetCoondinates.left - nodeSize.x;
        }else if( this.positionX === "right" ){
            left = targetCoondinates.right;
        }else if( this.positionX === "center" ){
            left = targetCoondinates.left + (targetCoondinates.width/2) - ( nodeSize.x / 2 )
        }
        node.setStyles({
            "left" : left,
            "top" : top
        });
    },
    destroy: function(){
        //if( this.options.event == "click" && this.node ){
        //    this.container.removeEvent("mousedown",this.hideFun );
        //}
        if( this.options.event == "click" && this.app && this.hideFun_resize ){
            this.app.removeEvent("resize",this.hideFun_resize );
        }

        if( this.targetClickFun )this.target.removeEvent( "click", this.targetClickFun );
        if( this.targetMouseenterFun )this.target.removeEvent( "mouseenter", this.targetMouseenterFun );
        if( this.targetMouseenterFun2 )this.target.removeEvent( "mouseenter", this.targetMouseenterFun2 );
        if( this.targetMouseleaveFun )this.target.removeEvent( "mouseleave", this.targetMouseleaveFun );
        if( this.targetMouseleaveFun2 )this.target.removeEvent( "mouseleave", this.targetMouseleaveFun2 );

        if( this.node ){
            this.node.destroy();
            this.node = null;
        }
        this.fireEvent("destroy",[this]);
        MWF.release(this);
    },
    _getHtml : function(){
        //var data = this.data;
        //var titleStyle = "font-size:14px;color:#333";
        //var valueStyle = "font-size:14px;color:#666;padding-right:20px";
        //var persons = [];
        //data.invitePersonList.each( function( p ){
        //    persons.push(p.split("@")[0] )
        //}.bind(this));
        //
        //var html =
        //    "<div style='overflow: hidden;padding:15px 20px 20px 10px;height:16px;line-height:16px;'>" +
        //    "   <div style='font-size: 12px;color:#666; float: right'>"+ this.lp.applyPerson  +":" + data.applicant.split("@")[0] +"</div>" +
        //    "   <div style='font-size: 16px;color:#333;float: left'>"+ this.lp.meetingDetail +"</div>"+
        //    "</div>"+
        //    "<div style='font-size: 18px;color:#333;padding:0px 10px 15px 20px;'>"+ data.subject +"</div>"+
        //    "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
        //    "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
        //    "<tr><td style='"+titleStyle+"' width='70'>"+this.lp.beginTime+":</td>" +
        //    "    <td style='"+valueStyle+"'>" + data.startTime + "</td></tr>" +
        //    "<tr><td style='"+titleStyle+"'>"+this.lp.endTime+":</td>" +
        //    "    <td style='"+valueStyle+"'>" + data.completedTime + "</td></tr>" +
        //    "<tr><td style='"+titleStyle+"'>"+this.lp.selectRoom +":</td>" +
        //    "    <td style='"+valueStyle+"' item='meetingRoom'></td></tr>" +
        //    "<tr><td style='"+titleStyle+"'>"+this.lp.invitePerson2+":</td>" +
        //    "    <td style='"+valueStyle+"' item='invitePerson'>"+persons.join(",")+"</td></tr>" +
        //    "<tr><td style='"+titleStyle+"'>"+this.lp.meetingDescription+":</td>" +
        //    "    <td style='"+valueStyle+"'>"+ data.description +"</td></tr>" +
        //    "<tr><td style='"+titleStyle+"'>"+this.lp.meetingAttachment+":</td>" +
        //    "    <td style='"+valueStyle+"' item='attachment'></td></tr>"+
        //    "</table>";
        return "";
    },
    _customNode : function( node, contentNode ){

    },
    _setContent : function( contentNode ){
        this.contentNode = contentNode;
        if( this.node ){
            this.node.empty();
            this.contentNode.inject( this.node );
        }
    },
    _loadCustom : function( callback ){
        if(callback)callback();
    }
});
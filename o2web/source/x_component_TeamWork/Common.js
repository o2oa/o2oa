MWF.xApplication.TeamWork.Common = MWF.xApplication.TeamWork.Common || {};
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);

MWF.xApplication.TeamWork.Common.Popup = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": 500,
        "height": 450,
        "top": null,
        "left": null,
        "bottom" : null,
        "right" : null,
        "minWidth" : 300,
        "minHeight" : 220,

        "isLimitSize": true,
        "ifFade": false,
        "hasTop": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasIcon": false,
        "hasBottom": false,
        "hasMask" : true,
        "closeByClickMask" : true,
        "hasScroll" : true,
        "scrollType" : "",

        "title": "",
        "draggable": false,
        "resizeable" : false,
        "maxAction" : false,
        "closeAction": false,

        "relativeToApp" : true,
        "sizeRelateTo" : "app", //desktop
        "resultSeparator" : ","
    },
    initialize: function (explorer, data, options, para) {
        this.setOptions(options);
        this.explorer = explorer;
        if( para ){
            if( this.options.relativeToApp ){
                this.app = para.app || this.explorer.app;
                this.container = para.container || this.app.content;
                this.lp = para.lp || this.explorer.lp || this.app.lp;
                this.css = para.css || this.explorer.css || this.app.css;
                this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = para.container;
                this.lp = para.lp || this.explorer.lp;
                this.css = para.css || this.explorer.css;
                this.actions = para.actions || this.explorer.actions;
            }
        }else{
            if( this.options.relativeToApp ){
                this.app = this.explorer.app;
                this.container = this.app.content;
                this.lp = this.explorer.lp || this.app.lp;
                this.css = this.explorer.css || this.app.css;
                this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = window.document.body;
                this.lp = this.explorer.lp;
                this.css = this.explorer.css;
                this.actions = this.explorer.actions;
            }
        }
        this.data = data || {};

        this.cssPath = "/x_component_TeamWork/$Common/"+this.options.style+"/css.wcss";

        this.load();

        if(this.css && this.css.popup)this.css = this.css.popup;   //使用common 样式和传进来的样式
        //if(para.css) this.css = Object.merge(  this.css, para.css );
    },
    close: function (data) {
        this.fireEvent("queryClose");
        this._close();
        //if( this.form ){
        //    this.form.destroy();
        //}
        if(this.setFormNodeSizeFun && this.app && this.app.removeEvent ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        if( this.formAreaNode )this.formAreaNode.destroy();
        this.fireEvent("postClose",data);
        delete this;
    },
    // open: function (e) {
    //     this.fireEvent("queryOpen");
    //     this.isNew = false;
    //     this.isEdited = false;
    //     this._open();
    //     this.fireEvent("postOpen");
    // }


});

MWF.xApplication.TeamWork.Common.Tips = new Class({
    Extends: MTooltips,

    options : {
        style:"default",

        axis: "x",      //箭头在x轴还是y轴上展现
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
        hiddenDelay : 100, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 1000,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : false,
        isAutoShow : true,
        isAutoHide : true,
        hasCloseAction : false,
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
        this.path = "/x_component_TeamWork/$Common/";

        if( this.target ){
            this.setTargetEvents();
        }
        this.fireEvent("postInitialize",[this]);
    },
    hide: function(){
        if( this.node ){
            this.node.setStyle("display","none");
            this.status = "hidden";
            if( this.maskNode ){
                this.maskNode.setStyle("display","none");
            }
            this.fireEvent("hide",[this]);
            //this.node.destroy();
        }
        if( this.maskNode ){
            this.maskNode.destroy();
        }
        //this.close();
    },

    _getHtml : function(){
        var html = this.data["_html"]||"";
        return html;
    }
});

MWF.xApplication.TeamWork.Common.ToolTips = new Class({
    Extends: MTooltips,
    options : {
        // displayDelay : 300,
        style:"default",
        hasArrow:false,
        event:"click"
    },
    initialize : function( container, target, app, data, options, targetCoordinates, explorer ){
        //可以传入target 或者 targetCoordinates，两种选一
        //传入target,表示触发tooltip的节点，本类根据 this.options.event 自动绑定target的事件
        //传入targetCoordinates，表示 出发tooltip的位置，本类不绑定触发事件
        if( options ){
            this.setOptions(options);
        }
        if(explorer) this.explorer = explorer;
        this.container = container;
        this.target = target;
        this.targetCoordinates = targetCoordinates;
        this.app = app;
        //if(app)this.lp = app.lp;
        this.lp = this.app.lp.common.tooltip;
        this.data = data;
        this.actions = this.app.restActions;

        this.path = "/x_component_TeamWork/$Common/";
        if(options.path) this.path = options.path;

        if( this.target ){
            this.setTargetEvents();
        }
        this.fireEvent("postInitialize",[this]);
    },
    load: function(){
        this.fireEvent("queryLoad",[this]);
        this.create();
        this.fireEvent("postLoad",[this]);
    },
    _loadCustom : function( callback ){
        // new Element("div.aaa",{styles:{"background-color":"#ff0000",width:"500px","height":"800px",position:"absolute",
        //     left:"100px",top:"200px"
        //     }}).inject(this.container);



        if(callback)callback();
    },
    hide: function(){
        if( this.node ){
            this.node.setStyle("display","none");
            this.status = "hidden";
            if( this.maskNode ){
                this.maskNode.setStyle("display","none");
            }
            this.fireEvent("hide",[this]);

        }
        if( this.maskNode ){
            this.maskNode.destroy();
        }
        this.close();
    },
    // 增加的方法
    close: function(data){
        if( this.node ){
            this.node.setStyle("display","none");
            this.status = "hidden";
            if( this.maskNode ){
                this.maskNode.setStyle("display","none");
            }
            this.fireEvent("hide",[this]);
            //this.fireEvent("close",[data]);
        }
        if( this.maskNode ){
            this.maskNode.destroy();
        }
        this.fireEvent("close",[data]);
        this.destroy();
    },
    _getHtml : function(){
        // var data = this.data;
        // var titleStyle = "font-size:14px;color:#333";
        // var valueStyle = "font-size:14px;color:#666;padding-right:10px";
        // var html =
        //     "<div style='font-size: 16px;color:#333;padding:10px 10px 10px 20px;'>ddddddddddddddd</div>"+
        //     "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
        //     "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
        //     "<tr><td style='"+titleStyle+";' width='40'>开始:</td>" +
        //     "    <td style='"+valueStyle+"'></td></tr>" +
        //     "<tr><td style='"+titleStyle+"'>结束:</td>" +
        //     "    <td style='"+valueStyle+ "'></td></tr>" +
        //     "<tr><td style='"+titleStyle+"'></td>" +
        //     "    <td style='"+valueStyle+ "'></td></tr>" +
        //     //( this.options.isHideAttachment ? "" :
        //     //"<tr><td style='"+titleStyle+"'>"+this.lp.eventAttachment+":</td>" +
        //     //"    <td style='"+valueStyle+"' item='attachment'></td></tr>"+
        //     //)+
        //     "<tr><td style='"+titleStyle+"'></td>" +
        //     "    <td style='"+valueStyle+ "' item='seeMore'></td></tr>"+
        //     "</table>";
        html = "";
        return html;
    },
    setTargetEvents : function(){
        if( this.options.event == "click" ){
            // if( this.options.isAutoShow ){
            //     this.target.addEvents({
            //         "click": function( ev ){
            //             this.load();
            //             ev.stopPropagation();
            //         }.bind(this)
            //     });
            // }
        }else{
            if( this.options.isAutoHide || this.options.isAutoShow ){
                this.target.addEvents({
                    "mouseenter": function(){
                        if( this.timer_hide ){
                            clearTimeout(this.timer_hide);
                        }
                    }.bind(this)
                });
            }
            if( this.options.isAutoShow ){
                this.target.addEvents({
                    "mouseenter": function(){
                        if( this.status != "display" ){
                            this.timer_show = setTimeout( this.load.bind(this),this.options.displayDelay );
                        }
                    }.bind(this)
                });
            }

            if( this.options.isAutoHide || this.options.isAutoShow ){
                this.target.addEvents({
                    "mouseleave" : function(){
                        if( this.timer_show ){
                            clearTimeout(this.timer_show);
                        }
                    }.bind(this)
                });
            }
            if( this.options.isAutoHide ){
                this.target.addEvents({
                    "mouseleave" : function(){
                        if( this.status == "display" ){
                            this.timer_hide = setTimeout( this.hide.bind(this),this.options.hiddenDelay );
                        }
                    }.bind(this)
                });
            }
        }
    },

    destroy: function(){
        //if( this.options.event == "click" && this.node ){
        //    this.container.removeEvent("mousedown",this.hideFun );
        //}
        if( this.options.event == "click" && this.app && this.hideFun_resize ){
            this.app.removeEvent("resize",this.hideFun_resize );
        }
        if( this.node ){
            this.node.destroy();
            this.node = null;
        }
        this.fireEvent("destroy",[this]);
        MWF.release(this);
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
        var offsetY = this.options.hasArrow ? 10 : 0;

        if( this.options.position.y == "top" ){
            top = targetCoondinates.top - nodeSize.y - offsetY;
            arrowY = "bottom";
        }else if( this.options.position.y == "bottom" ){
            top = targetCoondinates.bottom + offsetY;
            arrowY = "top";
        }else{
            var priorityOfAuto = this.options.priorityOfAuto;
            if( priorityOfAuto && priorityOfAuto.y ){
                for( var i=0; i<priorityOfAuto.y.length; i++ ){
                    if( priorityOfAuto.y[i] == "top" ){
                        if( targetCoondinates.top - containerScroll.y > containerSize.y - targetCoondinates.bottom ){
                            top = targetCoondinates.top - nodeSize.y - offsetY;
                            arrowY = "bottom";
                            break;
                        }
                    }
                    if( priorityOfAuto.y[i] == "bottom" ){
                        if( containerSize.y  + containerScroll.y - targetCoondinates.bottom > nodeSize.y ){
                            top = targetCoondinates.bottom + offsetY;
                            arrowY = "top";
                            break;
                        }
                    }
                }
            }
            if( !top ){
                if( targetCoondinates.top - containerScroll.y > containerSize.y - targetCoondinates.bottom){
                    top = targetCoondinates.top - nodeSize.y - offsetY;
                    arrowY = "bottom";
                }else{
                    top = targetCoondinates.bottom + offsetY;
                    arrowY = "top";
                }
            }
        }

        var left;
        if( this.options.position.x == "center" ){
            left = targetCoondinates.left + (targetCoondinates.width/2) - ( nodeSize.x / 2 ) ;
            arrowX = "center";
        }else if( this.options.position.x == "left" ){
            left = targetCoondinates.right - nodeSize.x;
            arrowX = "right";
        }else if( this.options.position.x == "right" ){
            left = targetCoondinates.left;
            arrowX = "left";
        }else{
            var priorityOfAuto = this.options.priorityOfAuto;
            if( priorityOfAuto && priorityOfAuto.x ){
                for( var i=0; i<priorityOfAuto.x.length; i++ ){
                    if( priorityOfAuto.x[i] == "center" ){
                        if( targetCoondinates.left + (targetCoondinates.width/2) - ( nodeSize.x / 2 ) > containerScroll.x &&
                            targetCoondinates.right - (targetCoondinates.width/2) + ( nodeSize.x / 2 ) - containerScroll.x < containerSize.x ){
                            left = targetCoondinates.left + (targetCoondinates.width/2) - ( nodeSize.x / 2 ) ;
                            arrowX = "center";
                            break;
                        }
                    }
                    if( priorityOfAuto.x[i] == "left" ){
                        if( targetCoondinates.left - containerScroll.x > containerSize.x - targetCoondinates.right){
                            left = targetCoondinates.right - nodeSize.x;
                            arrowX = "right";
                            break;
                        }
                    }
                    if( priorityOfAuto.x[i] == "right" ){
                        if( containerSize.x + containerScroll.x - targetCoondinates.right > nodeSize.x ){
                            left = targetCoondinates.left;
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
                    arrowX = "center";
                } else if( targetCoondinates.left - containerScroll.x > containerSize.x - targetCoondinates.right ){
                    left = targetCoondinates.right - nodeSize.x;
                    arrowX = "right";
                }else{
                    left = targetCoondinates.left;
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
            "top" : top+4
        });
    }
});
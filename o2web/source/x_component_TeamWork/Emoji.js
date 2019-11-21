MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.Emoji = new Class({
    Extends: MTooltips,
    options : {
        "style":"default",
        // displayDelay : 300,
        hasArrow:true,
        event:"click"
    },
    initialize : function( explorer,container, target, app, data, options, targetCoordinates ){
        //可以传入target 或者 targetCoordinates，两种选一
        //传入target,表示触发tooltip的节点，本类根据 this.options.event 自动绑定target的事件
        //传入targetCoordinates，表示 出发tooltip的位置，本类不绑定触发事件
        if( options ){
            this.setOptions(options);
        }
        this.explorer = explorer;
        this.container = container;
        this.target = target;
        this.targetCoordinates = targetCoordinates;
        this.app = app;

        this.lp = this.app.lp.emoji;
        this.data = data;
        this.actions = this.app.restActions;

        this.path = "/x_component_TeamWork/$Emoji/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        if( this.target ){
            this.setTargetEvents();
        }
        this.fireEvent("postInitialize",[this]);
    },
    _loadCustom : function( callback ){
        //this.data
        //this.contentNode
        this.contentNode.setStyles({"margin":"5px"});
        var _self = this;
        var emojiPath = "/x_component_TeamWork/$Emoji/default/icon/";
        for(var item in this.lp){
            var text = this.lp[item];
            var emojiItem = new Element("div.emojiItem",{styles:this.css.emojiItem}).inject(this.contentNode);
            emojiItem.set("title",text);
            emojiItem.setStyles({"background-image":"url("+emojiPath+item+".png)"});
            emojiItem.addEvents({
                mouseover:function(){this.setStyles({"border":"1px solid #dedede"})},
                mouseout:function(){this.setStyles({"border":"1px solid #ffffff"})},
                click:function(){
                    var data = {"value":this.get("title")};
                    _self.close(data)
                }
            })
        }

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
    }

});
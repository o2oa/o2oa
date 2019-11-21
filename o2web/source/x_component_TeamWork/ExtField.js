MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.ExtField = new Class({
    Extends: MTooltips,
    options : {
        "style":"default",
        // displayDelay : 300,
        hasArrow:false,
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

        this.lp = this.app.lp.extField;
        this.data = data;
        this.actions = this.app.restActions;

        this.path = "/x_component_TeamWork/$ExtField/";
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
        var _self = this;
        this.valid = false;
        this.topLayout = new Element("div.topLayout",{styles:this.css.topLayout,text:this.lp.newTip}).inject(this.contentNode);
        this.contentLayout = new Element("div.contentLayout",{styles:this.css.contentLayout}).inject(this.contentNode);

        //名称
        this.nameText = new Element("div.nameText",{styles:this.css.titleText,text:this.lp.name}).inject(this.contentLayout);
        this.nameValueLayout = new Element("div.nameValueLayout",{styles:this.css.valueLayout}).inject(this.contentLayout);
        this.nameValue = new Element("input.nameValue",{styles:this.css.nameValue,placeHolder:this.lp.field+this.lp.name}).inject(this.nameValueLayout);
        this.nameValue.addEvents({
            focus:function(){
                this.nameValueLayout.setStyles({"border":"1px solid #4a90e2"})
            }.bind(this),
            blur:function(){
                this.nameValueLayout.setStyles({"border":"1px solid #dedede"});
                var v = this.nameValue.get("value").trim();
                var next = this.nameValueLayout.getNext();
                if(v=="" && next.get("class")!="inValid"){
                    this.createInValid(this.nameValueLayout)
                }
                this.checkValid();
            }.bind(this),
            keyup:function(){
                var v = this.nameValue.get("value").trim();
                var next = this.nameValueLayout.getNext();
                if(v=="" && next.get("class")!="inValid"){
                    this.createInValid(this.nameValueLayout)
                }else{
                    if(next.get("class")=="inValid") next.destroy()
                }
                this.checkValid();
            }.bind(this)
        });
        //描述
        this.descriptionText = new Element("div.descriptionText",{styles:this.css.titleText,text:this.lp.description}).inject(this.contentLayout);
        this.descriptionLayout = new Element("div.descriptionLayout",{styles:this.css.valueLayout}).inject(this.contentLayout);
        this.descriptionValue = new Element("input.descriptionValue",{styles:this.css.nameValue,placeHolder:this.lp.field+this.lp.description}).inject(this.descriptionLayout);
        this.descriptionValue.addEvents({
            focus:function(){
                this.descriptionLayout.setStyles({"border":"1px solid #4a90e2"})
            }.bind(this),
            blur:function(){
                this.descriptionLayout.setStyles({"border":"1px solid #dedede"})
            }.bind(this)
        });
        //类型
        this.typeText = new Element("div.descriptionText",{styles:this.css.titleText,text:this.lp.type}).inject(this.contentLayout);
        this.typeLayout = new Element("div.typeLayout",{styles:this.css.valueLayout}).inject(this.contentLayout);

        this.typeLayout.addEvents({
            click:function(){
                this.typeLayout.setStyles({"border":"1px solid #4a90e2"});

                var pc = new MWF.xApplication.TeamWork.ExtField.Types(this.container, this.typeLayout, this.app, {}, {
                    css:this.css, lp:this.lp, axis : "y",
                    position : { //node 固定的位置
                        x : "right",
                        y : "middle"
                    },
                    nodeStyles : {
                        "min-width":"150px",
                        "width":this.typeLayout.getWidth()+"px",
                        "padding":"2px",
                        "border-radius":"5px",
                        "box-shadow":"0px 0px 4px 0px #999999",
                        "z-index" : "301"
                    },
                    onPostLoad:function(){
                        pc.node.setStyles({"opacity":"0","top":(pc.node.getStyle("top").toInt())+"px","left":(pc.node.getStyle("left").toInt())+"px"});
                        var fx = new Fx.Tween(pc.node,{duration:400});
                        fx.start(["opacity"] ,"0", "1");
                        if(pc.maskNode){
                            pc.maskNode.setStyles({"z-index":"300"})
                        }
                    },
                    onClose:function(rd){
                        var next = this.typeLayout.getNext();

                        if(!rd) {
                            if(!next && !this.type){
                                this.createInValid(this.typeLayout)
                            }
                            this.rotateArrow(this.typeArrow);
                            this.typeLayout.setStyles({"border":"1px solid #dedede"});
                            this.checkValid();
                            return;
                        }

                        if(next && next.get("class")=="inValid"){
                            next.destroy()
                        }
                        this.rotateArrow(this.typeArrow,function(){
                            this.typeValue.set("text",rd.value);
                            this.type = rd.type;
                            this.typeLayout.setStyles({"border":"1px solid #dedede"});

                            this.contentLayout.getElements(".optionLayout").destroy();
                            if(rd.type == "RADIO" || rd.type=="CHECKBOX"){
                                //增加选项
                                this.createOptions()
                            }
                            this.checkValid();
                        }.bind(this));
                    }.bind(this)
                });
                pc.load();

                this.rotateArrow(this.typeArrow);
            }.bind(this)
        })
        this.typeValue = new Element("div.typeValue",{styles:this.css.typeValue}).inject(this.typeLayout);
        this.typeArrow = new Element("div.typeArrow",{styles:this.css.typeArrow}).inject(this.typeLayout);

        this.bottomLayout = new Element("div.bottomLayout",{styles:this.css.bottomLayout}).inject(this.contentNode)
        this.confirm = new Element("div.confirm",{styles:this.css.confirm,text:this.lp.confirm}).inject(this.bottomLayout);
        this.confirm.addEvents({
            click:function(){
                if(this.valid){
                    var data = {
                        id:this.data.id || "",
                        projectId:this.data.projectId,
                        displayName:this.nameValue.get("value").trim(),
                        displayType:this.type,
                        description:this.descriptionValue.get("value").trim(),
                        optionsData:""
                    };

                    this.actions.projectExtFieldSave(data,function(json){
                        if(json.data.id){
                            this.actions.projectExtFieldGet(json.data.id,function(data){
                                this.explorer.createExtFieldItem(data.data);
                                this.close()
                            }.bind(this))
                        }
                    }.bind(this))
                }
            }.bind(this)
        })

        if(this.data.id){
            this.actions.projectExtFieldGet(this.data.id,function(json){
                this.loadData(json.data);
                this.checkValid()
            }.bind(this))
        }
        if(callback)callback();
    },
    loadData:function(data){
        this.nameValue.set("value",data.displayName);
        this.descriptionValue.set("value",data.description);
        this.type = data.displayType;
        var dt = this.lp.text;
        if(data.displayType=="RICHTEXT") dt = this.lp.richtext;
        else if(data.displayType=="RADIO") dt = this.lp.radio;
        else if(data.displayType=="CHECKBOX") dt = this.lp.checkbox;
        else if(data.displayType=="DATE") dt = this.lp.date;
        else if(data.displayType=="DATETIME") dt = this.lp.datetime;
        else if(data.displayType=="PERSON") dt = this.lp.identity;
        else if(data.displayType=="IDENTITY") dt = this.lp.identity;
        else if(data.displayType=="UNIT") dt = this.lp.unit;
        this.typeValue.set("text",dt);
        if(this.type == "RADIO" || this.type == "CHECKBOX"){

        }
    },
    createOptions:function(){
        var _self = this;
        //this.contentLayout
        var optionLayout = new Element("div.optionLayout",{styles:this.css.optionLayout}).inject(this.contentLayout);
        var optionText = new Element("div.optionText",{styles:this.css.optionText,text:this.lp.option}).inject(optionLayout);
        var optionValue = new Element("div.optionValue",{styles:this.css.optionValue}).inject(optionLayout);
        var optionName = new Element("input.optionName",{styles:this.css.optionName}).inject(optionValue);
        var optionValid = new Element("div.optionValid",{styles:this.css.inValid,text:this.lp.notEmpty}).inject(optionLayout);
        optionValid.setStyles({"display":"none"});
        optionName.addEvents({
            click:function(){
                optionClose.show();
                if(this.get("value").trim()=="") optionValid.show();
                var next = optionLayout.getNext();
                if(!next)_self.createOptions();
                _self.checkValid()
            },
            keyup:function(){
                if(this.get("value").trim()=="") optionValid.show()
                else optionValid.hide()
                _self.checkValid()
            }
        });
        var optionClose = new Element("div.optionClose",{styles:this.css.optionClose}).inject(optionValue);
        optionClose.addEvents({
            click:function(){
                optionLayout.destroy();
                _self.checkValid()
            }.bind(this),
            mouseover:function(){this.setStyles({"background-image":"url(/x_component_TeamWork/$ExtField/default/icon/icon_off_click.png)"})},
            mouseout:function(){this.setStyles({"background-image":"url(/x_component_TeamWork/$ExtField/default/icon/icon_off.png)"})},
        });
    },
    rotateArrow:function(node,callback){
        var status = node.get("rotate") || "down";
        var angle = 0;
        if(status=="down")angle=0
        else angle=180
        var int = window.setInterval(function(){
            if(status == "down"){
                if(angle==180){
                    window.clearInterval(int);
                    node.set("rotate","up");
                    if(callback)callback();
                    return;
                }
                angle = angle + 4;
            }else{
                if(angle==0){
                    window.clearInterval(int);
                    node.set("rotate","down");
                    if(callback)callback();
                    return;
                }
                angle = angle - 4;
            }
            node.setStyles({"transform":"rotate("+angle+"deg)"});
        }.bind(this),1)
    },
    checkValid:function(){
        var name = this.nameValue.get("value").trim();
        if(name!="" && this.type){
            if(this.type == "RADIO" || this.type == "CHECKBOX"){
                var flag = false;
                var options = this.contentLayout.getElements(".optionValid");
                options.each(function(d){
                    if(d.isDisplayed())flag = true
                });
                if(flag){
                    this.valid = false;
                    this.confirm.setStyles(this.css.confirm)
                }else{
                    if(options.length == 1){
                        this.valid = false;
                        this.confirm.setStyles(this.css.confirm)
                    }else{
                        this.valid = true;
                        this.confirm.setStyles({"background-color":"#4a90e2","cursor":"pointer"})
                    }
                }
            }else{
                this.valid = true;
                this.confirm.setStyles({"background-color":"#4a90e2","cursor":"pointer"})
            }
        }else{
            this.valid = false;
            this.confirm.setStyles(this.css.confirm)
        }
    },
    createInValid:function(node){
        var inValid = new Element("div.inValid",{styles:this.css.inValid,text:this.lp.notEmpty}).inject(node,"after");
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

MWF.xApplication.TeamWork.ExtField.Types = new Class({
    Extends: MWF.xApplication.TeamWork.Common.ToolTips,
    options : {
        // displayDelay : 300,
        hasArrow:false,
        event:"click"
    },
    _loadCustom : function( callback ){
        var _self = this;
        this.css = this.options.css;
        this.lp = this.options.lp;
        //this.data
        //this.contentNode

        var container = {
            "cursor":"pointer",
            "height":"40px",
            "width":"100%"
        };
        var text={
            "height":"25px","line-height":"25px","float":"left",
            "margin-left":"6px","margin-top":"8px",
            "font-size":"13px","color":"#666666","border-radius":"2px"
        };
        var icon = {
            "float":"left","width":"24px","height":"24px",
            "margin-top":"8px","margin-left":"4px",
            "background-position":"center",
            "background-repeat":"no-repeat"
        };
        var select = {
            "float":"right","width":"24px","height":"24px",
            "margin-top":"6px","margin-right":"8px",
            "background":"url(/x_component_TeamWork/$ExtField/default/icon/icon_dagou.png) no-repeat center"
        };

        var textContainer = new Element("div.container",{styles:container}).inject(this.contentNode);
        var textIcon = new Element("div",{styles:icon}).inject(textContainer);
        textIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ExtField/default/icon/icon_text.png)"});
        var textText = new Element("div.text",{styles:text,text:this.lp.text,name:"TEXT"}).inject(textContainer);
        var textSelect = new Element("div.select",{styles:select}).inject(textContainer);

        var richtextContainer = new Element("div.container",{styles:container}).inject(this.contentNode);
        var richtextIcon = new Element("div",{styles:icon}).inject(richtextContainer);
        richtextIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ExtField/default/icon/icon_richtext.png)"});
        var richtextText = new Element("div.text",{styles:text,text:this.lp.richtext,name:"RICHTEXT"}).inject(richtextContainer);
        var richtextSelect = new Element("div.select",{styles:select}).inject(richtextContainer);

        var radioContainer = new Element("div.container",{styles:container}).inject(this.contentNode);
        var radioIcon = new Element("div",{styles:icon}).inject(radioContainer);
        radioIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ExtField/default/icon/icon_radio.png)"});
        var radioText = new Element("div.text",{styles:text,text:this.lp.radio,name:"RADIO"}).inject(radioContainer);
        var radioSelect = new Element("div.select",{styles:select}).inject(radioContainer);

        var checkboxContainer = new Element("div.container",{styles:container}).inject(this.contentNode);
        var checkboxIcon = new Element("div",{styles:icon}).inject(checkboxContainer);
        checkboxIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ExtField/default/icon/icon_checkbox.png)"});
        var checkboxText = new Element("div.text",{styles:text,text:this.lp.checkbox,name:"CHECKBOX"}).inject(checkboxContainer);
        var checkboxSelect = new Element("div.select",{styles:select}).inject(checkboxContainer);

        var dateContainer = new Element("div.container",{styles:container}).inject(this.contentNode);
        var dateIcon = new Element("div",{styles:icon}).inject(dateContainer);
        dateIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ExtField/default/icon/icon_date.png)"});
        var dateText = new Element("div.text",{styles:text,text:this.lp.date,name:"DATE"}).inject(dateContainer);
        var dateSelect = new Element("div.select",{styles:select}).inject(dateContainer);

        var personContainer = new Element("div.container",{styles:container}).inject(this.contentNode);
        var personIcon = new Element("div",{styles:icon}).inject(personContainer);
        personIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ExtField/default/icon/icon_person.png)"});
        var personText = new Element("div.text",{styles:text,text:this.lp.identity,name:"IDENTITY"}).inject(personContainer);
        var personSelect = new Element("div.select",{styles:select}).inject(personContainer);

        var unitContainer = new Element("div.container",{styles:container}).inject(this.contentNode);
        var unitIcon = new Element("div",{styles:icon}).inject(unitContainer);
        unitIcon.setStyles({"background-image":"url(/x_component_TeamWork/$ExtField/default/icon/icon_unit.png)"});
        var unitText = new Element("div.text",{styles:text,text:this.lp.unit,name:"UNIT"}).inject(unitContainer);
        var unitSelect = new Element("div.select",{styles:select}).inject(unitContainer);

        this.contentNode.getElements(".select").hide();
        this.contentNode.getElements(".container").addEvents({
            mouseover:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseout:function(){this.setStyles({"background-color":""})},
            click:function(){
                var data = {"value":this.getElement(".text").get("text"),"type":this.getElement(".text").get("name")};
                _self.close(data)
            }
        });


        if(callback)callback();
    }

});
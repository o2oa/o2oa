o2.requireApp("Template", "MDomItem", null, false);

MDomItem.Util = Object.merge(MDomItem.Util, {
    selectPerson: function( container, options, callback  ){
        if( options.type === "custom" ){
            this._selectCustom(container, options, callback);
        }else{
            this._selectPerson(container, options, callback);
        }
    },
    _selectCustom: function (container, options, callback) {
        MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false); //加载资源

        var opt = {
            "title": options.title,
            "count": options.count,
            "values": options.selectedValues || [],
            "expand": typeOf(options.expand) === "boolean" ? options.expand : true,
            "exclude": options.exclude || [],
            "expandSubEnable": typeOf(options.expandSubEnable) === "boolean" ? options.expandSubEnable : true,
            "hasLetter": false, //是否点击字母搜索
            "hasTop": false, //可选、已选的标题
            // "level1Indent" : 0, //第一层的缩进
            // "indent" : 36, //第二层及以上的缩进
            "selectAllEnable": true, //是否允许多选，如果分类可以被选中，blue_flat样式下失效
            "width": "700px", //选中框宽度
            "height": "550px", //选中框高度
            "category": true, //按分类选择
            "noSelectedContainer": false, //是否隐藏右侧已选区域
            "categorySelectable": false, //分类是否可以被选择，如果可以被选择那么执行的是item的事件
            "uniqueFlag": "id", //项目匹配（是否选中）关键字
            "defaultExpandLevel": 1, //默认展开项目，0表示折叠所有分类
            "onComplete": function (array) {
                if (callback) callback(array);
            }.bind(this)
        };
        if (options.orgOptions) {
            opt = Object.merge(opt, options.orgOptions);
        }
        var selector = new MWF.xApplication.Template.Selector.Custom(container, opt);
        selector.load();
    },
    _selectPerson: function (container, options, callback) {
        MWF.xDesktop.requireApp("Selector", "package", null, false);

        var selectType = "", selectTypeList = [];
        var type = options.type;
        if (typeOf(type) == "array") {
            if (type.length > 1) {
                selectTypeList = type;
            } else if (type.length == 0) {
                selectType = "person";
            } else {
                selectType = type[0] || "person";
            }
        } else {
            selectType = type || "person";
        }

        var opt = {
            "type": selectType,
            "types": selectTypeList,
            "title": options.title,
            "count": options.count,
            "values": options.selectedValues || [],
            "units": options.units,
            "unitType": options.unitType,
            "groups": options.groups,
            "expand": options.expand,
            "exclude": options.exclude || [],
            "expandSubEnable": options.expandSubEnable,
            "onComplete": function (array) {
                if (callback) callback(array);
            }.bind(this)
        };
        if (options.orgOptions) {
            opt = Object.merge(opt, options.orgOptions);
        }
        if (opt.types.length === 0) opt.types = null;
        var selector = new MWF.O2Selector(container, opt);
    }
});

MDomItem.implement({
    setOptionList : function( options ){  //目的是使用options里的function异步方法通过 function(callback){ ...获取value; callback( value );  } 来回调设置option
        var callbackNameList = [];
        for(var o in options ){	//允许使用 function 来计算设置, on开头的属性被留作 fireEvent
            if( o != "validRule" && o!="validMessage" && o.substr(0,2)!="on" && typeOf( options[o] )== "function" ){
                var fun = options[o];
                if( fun.length && /\(\s*([\s\S]*?)\s*\)/.exec(fun)[1].split(/\s*,\s*/)[0] == "callback" ){ //如果有行参(fun.length!=0),并且第一形参是callback，注意，funciont不能bind(this),否则不能判断
                    callbackNameList.push( o );
                }else{
                    options[o] = fun( options, this ); //执行fun
                }
            }
        }
        this.setFunOption( options, callbackNameList, true ); //递归执行回调设置options
    },
    checkValid : function( isShowWarning ){
        if( this.options.disable )return true;
        var value = this.getValue();
        var rules = this.options.validRule;
        if( !rules )return true;

        var msgs = [];
        var flag = true;

        //if( value && value != "" && value != " " ){
        var rule, msg, method, valid;
        if( typeOf( rules ) === "object" ){
            for(var r in rules ){
                valid = true;
                rule = rules[r];

                if( typeof rule == "function"){
                    valid = rule.call( this, value, this );
                }else if( this.validMethod[r] ){
                    method = this.validMethod[r];
                    valid = method.call(this, value, rule, this );
                }

                if( !valid && isShowWarning ){
                    msg = this.getValidMessage( r, rule );
                    if( msg != "" )msgs.push( msg );
                }

                if( !valid )flag = false;
            }
        }else if( typeOf( rules ) === "array" ){
            for( var i = 0; i<rules.length; i++ ){
                if( typeof rules[i] == "function"){
                    msg = rules[i].call( this, value, this );
                    if( msg && typeof msg === "string" ){
                        flag = false;
                        if( isShowWarning )msgs.push( msg );
                    }
                }
            }
        }else if( typeOf( rules ) === "function" ){
            msg = rules.call( this, value, this );
            if( msg && typeof msg === "string" ){
                flag = false;
                if( isShowWarning )msgs.push( msg );
            }
        }
        //}

        if( msgs.length > 0 ){
            if( this.options.warningType == "batch" ) {
                this.setWarning(msgs, "invaild");
            }else if( this.options.warningType == "single" ){
                this.setWarning(msgs, "invaild");
            }else{
                if( this.app && this.app.notice ) {
                    if (!this.container.isIntoView()) {
                        var pNode = this.container.getParent();
                        while (pNode && ((pNode.getScrollSize().y - (pNode.getComputedSize().height + 1) <= 0) || pNode.getStyle("overflow") === "visible")) pNode = pNode.getParent();
                        if (!pNode) pNode = document.body;
                        pNode.scrollToNode(this.container, "bottom");
                    }
                    var y = this.container.getSize().y;
                    this.app.notice(msgs.join("\n"), "error", this.container, {"x": "right", "y": "top"}, { x : 10, y : y });
                }
            }
            this.fireEvent("empty", this);
        }else{
            if( this.warningInvalidNode ){
                this.warningInvalidNode.destroy();
                this.warningInvalidNode = null;
            }
            this.fireEvent("unempty", this);
        }

        return flag;

    },
    editMode : function( keep ){
        if(keep)this.save();
        this.options.isEdited = true;
        this.dispose();
        this.items = [];
        this.load();
    }
});

MDomItem.Checkbox.implement({
    loadEdit : function(){
        var _self = this;
        var item;
        var values;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue ;
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var isEdited = this.options.isEdited;
        var parent = this.mElement = this.container ;
        var className = this.getClassName();
        values = typeOf( value ) == "string" ? value.split(this.valSeparator) : value ;
        values = typeOf( value ) == "array" ? value : [value];
        var selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
        var selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);
        for( var i=0;i<selectValues.length;i++){

            item = new Element( "div");
            if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
            if( this.options.clazz )item.addClass( this.options.clazz );
            item.setStyles( styles );

            var input = new Element( "input", {
                "type" : "checkbox",
                "name" : name,
                "value" : selectValues[i],
                "checked" : values.contains( selectValues[i] )
            }).inject( item );
            input.addEvent("click", function (ev) {
                ev.stopPropagation();
            });
            input.set( attr );

            var textNode = new Element( "span", {
                "text" : selectTexts[i]
            }).inject(item);

            item.addEvent("click", function (ev) {
                if( _self.options.attr && _self.options.attr.disabled )return;
                this.input.checked = !this.input.checked;
                var envents = MDomItem.Util.getEvents(_self.options.event);
                if (typeOf(envents) == "object") {
                    if (envents.change) {
                        envents.change.call(this.input, _self.module, ev);
                    }
                    if (envents.click) {
                        envents.click.call(this.input, _self.module, ev);
                    }
                }
                if (_self.options.validImmediately) {
                    _self.module.verify(true);
                }
            }.bind({input: input}));


            if( this.options.validImmediately ){
                item.addEvent("click", function(){ this.module.verify( true ); }.bind(this))
            }
            MDomItem.Util.bindEvent( this, item, event); // ? input or item
            if(parent)item.inject(parent);
            this.items.push( item );
        }
    }
});


o2.requireApp("Template", "MTooltips", null, false);

MTooltips.implement({
    create: function(){
        this.status = "display";
        this.fireEvent("queryCreate",[this]);
        this.loadStyle();

        this.fireEvent("loadStyle",[this]);

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
                    }).inject( this.node, "before" );
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
                        if( this.options.isAutoHide ){
                            this.timer_hide = setTimeout( this.hide.bind(this),this.options.hiddenDelay );
                        }
                    }.bind(this)
                });
            }
        }

        //this.target.addEvent( "mouseleave", function(){
        //    this.timer_hide = setTimeout( this.hide.bind(this), this.options.HiddenDelay );
        //}.bind(this));
        this.fireEvent("postCreate",[this]);
    },
    setCoondinates_y : function(){
        var targetCoondinates = this.target ? this.target.getCoordinates( this.container ) : this.targetCoordinates ;
        // if( o2.typeOf( this.options.zoom ) === "number" && this.options.zoom !== 1 && this.isIOS() ){
        //     targetCoondinates.left = targetCoondinates.left - ( 1 - this.options.zoom ) * targetCoondinates.left;
        //     targetCoondinates.right = targetCoondinates.right - ( 1 - this.options.zoom ) * targetCoondinates.right;
        // }
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

        var obj = {
            "left" : l || left,
            "top" : top
        };

        this.fireEvent( "setCoondinates", [obj] );

        node.setStyles({
            "left" : obj.left,
            "top" : obj.top
        });

        this.fireEvent( "postSetCoondinates", [arrowX, arrowY, obj] );
    }
})

var MDataGrid = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        style: "default",
        isNew: false,
        isEdited: false,
        showNotEmptyFlag : false,
        verifyType : "batch",	//batch一起校验，或alert弹出
        //batch，所有item的错误都校验，每个item所有错误一起校验，错误显示在Item的字段后或指定区域；
        //batchSingle, 所有item的错误都校验，每个item只校验一个错误，错误显示在Item的字段后或指定区域；
        //alert，验证时遇到一个不成功的Item用app.notice()弹出消息后中断,
        //single，验证时遇到一个不成功的Item在字段后面或指定区域显示错误信息后中断

        itemTemplate : null,

        objectId : "",
        hasSequence  : true,
        hasOperation : false,
        isCreateTh : true,
        containerIsTable : false,
        isCreateTrOnNull : true,
        minTrCount : 0,
        maxTrCount : 0,

        tableAttributes : null,
        thAttributes : null,
        tdAttributes : null,

        textOnly : false,

        tableClass : "formTable",
        thClass : "formTableTitle",
        tdClass : "formTableValue",
        thAlign : "center",
        tdAlign : "left",
        sequenceClass : "formTableSequence",
        addActionTdClass : "formTableAddTd",
        removeActionTdClass : "formTableRemoveTd",
        lp : {
            remove : "删除",
            add : "添加",
            sequence: "序号",
            addMaxLimitText: "最多只能添加{count}项"
        }

    },
    initialize: function (container, data, options, app, css) {

        this.setOptions(options);

        this.path = "../x_component_StandingBook/$MCommon/";
        this.cssPath = "../x_component_StandingBook/$MCommon/" + this.options.style + "/css.wcss";
        this._loadCss();
        if (css) {
            this.css = Object.merge(this.css, css)
        }

        this.app = app;
        this.container = $(container);

        this.data = data;

        this.isSourceDataEmpty = false;
        if (!this.data || this.data == "") {
            this.isSourceDataEmpty = true;
            this.data = [{}];
        }

        this.itemTemplate = this.options.itemTemplate;
        this.items = null;

        this.th = null;
        this.trIndex = 0;

        this.trList = [];
        this.trObjs = null;
        this.trObjs_removed = null;
        this.trObjs_new = null;

        this.thTemplate = null;	//属性  lable button_add styles class
        this.trTemplate = null; //属性 item sequence button_remove lable styles class

        this.valSeparator = /,|;|\^\^|\|/g; //如果是多值对象，作为用户选择的多个值的分隔符
    },
    load: function () {
        this.fireEvent("queryLoad");


        //如果itemTemplate没有name赋值Key
        for (var it in this.itemTemplate) {
            if (!this.itemTemplate[it]["name"]) {
                this.itemTemplate[it]["name"] = it;
            }
            this.itemTemplate[it]["key"] = it;
        }

        //如果itemTemplate没有name和Key不一致，那么根据name赋值itemTemplate
        var json = {};
        for (var it in this.itemTemplate) {
            if (it != this.itemTemplate[it]["name"]) {
                json[this.itemTemplate[it]["name"]] = this.itemTemplate[it]
            }
        }
        for (var it in json) {
            this.itemTemplate[it] = json[it];
        }
        this.createTable( this.itemTemplate );
        this.createHead( this.itemTemplate );
        this.trObjs = {};
        this.trObjs_removed = {};
        this.trObjs_new = {};
        this.trList = [];
        if( this.options.textOnly ){
            this.loadTextOnly();
        }else{
            (this.options.isEdited || this.options.isNew) ? this.loadEdit() : this.loadRead();
        }
        this.fireEvent("postLoad", [this]);

    },
    setTrTemplate: function( template ){
        if( typeOf( template ) == "string"){
            this.trTemplate = $(this.string2DOM( template )[0]);
        }else{
            this.trTemplate = $(template);
        }
        this.formatStyles( this.trTemplate );
    },
    setThTemplate : function( template ){
        if( typeOf( template ) == "string"){
            this.thTemplate = $(this.string2DOM( template )[0])
        }else{
            this.thTemplate = $(template);
        }
        this.formatStyles( this.thTemplate );
    },
    loadEdit:function(){
        if( !this.isSourceDataEmpty ){
            if( typeOf( this.data ) != "array" ){
                this.data = [ this.data ]
            }
            for( var i=0; i<this.data.length; i++ ){
                var d = this.data[i];
                var items = Object.clone(this.itemTemplate);
                for (var it in d ){
                    if ( items[ it ] ){
                        items[ it ].value  = d[it];
                    }
                }
                this.createTr( items, false, null, d );
            }
            // for( var it in this.itemTemplate ){
            //     this.itemTemplate[it].value = "";
            // }
        }else if( this.options.isCreateTrOnNull ){
            this.createTr( this.itemTemplate, true );
        }
    },
    loadRead:function(){
        if( !this.isSourceDataEmpty ){
            if( typeOf( this.data ) != "array" ){
                this.data = [ this.data ]
            }
            for( var i=0; i<this.data.length; i++ ){
                var d = this.data[i];
                var items = Object.clone(this.itemTemplate);
                for (var it in d ){
                    if ( items[ it ] ){
                        items[ it ].value  = d[it];
                    }
                }
                this.createTr( items, false, null, d );
            }
            // for( var it in this.itemTemplate ){
            //     this.itemTemplate[it].value = "";
            // }
        }else if( this.options.isCreateTrOnNull ){
            this.createTr( this.itemTemplate, true );
        }
    },
    loadTextOnly : function(){
        if( !this.isSourceDataEmpty ){
            if( typeOf( this.data ) != "array" ){
                this.data = [ this.data ]
            }
            for( var i=0; i<this.data.length; i++ ){
                var d = this.data[i];
                var items = Object.clone(this.itemTemplate);
                for (var it in d ){
                    if ( items[ it ] ){
                        items[ it ].value  = d[it];
                    }
                }
                //this.createTr( items, false );
                this.createTr_textOnly( items );
            }
        }else if( this.options.isCreateTrOnNull ){
            this.createTr_textOnly( this.itemTemplate );
        }
    },
    createTable : function( itemData ){
        if( this.options.containerIsTable ){
            this.table = this.container;
            var labelContainers = this.table.getElements("[lable]");
            labelContainers.each(function( el ) {
                var obj = itemData[el.get("lable")];
                if (!obj)return;
                if(obj.text)el.set("text",obj.text);
            });
            this.fireEvent( "postCreateTable", [this] );
        }else{
            var styles = {};
            if( this.options.tableClass && this.css[this.options.tableClass] )styles = this.css[this.options.tableClass];
            var tableAttr =  this.options.tableAttributes || {};
            this.table = new Element( "table", {
                styles : styles
            }).inject(this.container);
            this.table.set( tableAttr );
            this.fireEvent( "postCreateTable", [this] );
        }
        return this.table;
    },
    createHead : function( itemData ){
        if( !this.options.isCreateTh )return;
        if( this.thTemplate ){
            return this.createHead_byTemplate( itemData );
        }else{
            return this.createHead_noTemplate( itemData );
        }
    },
    createHead_byTemplate : function( itemData ){
        var showNotEmptyFlag = this.options.showNotEmptyFlag;
        var isEdited = this.options.isEdited;
        var th = this.tableHead = this.thTemplate;
        var labelContainers = th.getElements("[lable]");
        labelContainers.each(function(el) {
            var lable = el.get("lable");
            var obj = itemData[lable];
            if (!obj)return;
            if(obj.text)el.set("text", obj.text);
            if( showNotEmptyFlag && itemData[lable].notEmpty && isEdited ){
                new Element( "span" , { styles : { color : "red" }, text : "*" }).inject( el )
            }
        });
        if( this.options.hasOperation && isEdited ){
            var add_button = th.getElement("[button_add]");
            if( add_button )this.createAddButton( add_button );
        }
        th.inject( this.table );
        return th;
    },
    createHead_noTemplate : function( itemData ){
        var tr = this.tableHead = new Element("tr");

        var align = this.options.thAlign == "" ? {} : { align : this.options.thAlign  };
        var styles = (this.options.thClass && this.css[this.options.thClass]) ? this.css[this.options.thClass] : {};

        if( this.options.hasSequence  ){
            var th = new Element("th", { text : this.options.lp.sequence }).inject( tr );
            th.set( align );
            th.setStyles( styles );
        }

        var idx = 1;
        for (var it in this.itemTemplate){
            var thAttr = {};
            if(this.options.thAttributes && this.options.thAttributes["_"+idx] ){
                thAttr = this.options.thAttributes["_"+idx];
            }

            var th = new Element("th").inject(tr);
            if( this.options.showNotEmptyFlag && this.itemTemplate[it].notEmpty && this.options.isEdited ){
                new Element( "span" , { styles : { color : "red", text : "*" } }).inject( th )
            }
            th.set( align );
            th.setStyles( styles );
            if(this.itemTemplate[it].text)th.set("text", this.itemTemplate[it].text );
            idx++;
        }
        if( this.options.hasOperation && this.options.isEdited ){
            var th = new Element("th", { align : "center", styles : { width : "24px", styles : {"text-align" : "center" }} } ).inject(tr);
            if( this.options.addActionTdClass && this.css[this.options.addActionTdClass] )th.setStyles(this.css[this.options.addActionTdClass]);
            this.createAddButton( th );
        }
        tr.inject( this.table );
        this.fireEvent("postCreateHead", [tr] );
        return tr;
    },
    createAddButton : function( container ){
        var button = new Element("div", { title : this.options.lp.add }).inject( container );
        if( this.css.actionAdd )button.setStyles( this.css.actionAdd );
        button.addEvent("click", function( e ){
            this.fireEvent("queryAddTr");
            this.createTr( this.itemTemplate, true );
            this.fireEvent("postAddTr");
        }.bind(this));
        if( this.css.actionAdd && this.css.actionAdd_over){
            button.addEvents({
                "mouseover" : function( e ){ this.node.setStyles( this.obj.css.actionAdd_over ) }.bind({ node : button, obj : this }),
                "mouseout" : function( e ){ this.node.setStyles( this.obj.css.actionAdd ) }.bind({ node : button, obj : this })
            })
        }
        return button;
    },
    addTrs : function( count ){
        if( 100 < count )count = 100;
        var trObj, trObjList = [];
        for( var i=0 ; i<count; i++ ){
            trObj = this.createTr( this.itemTemplate, true );
            trObjList.push( trObj );
        }
        return trObjList;
    },
    appendTr : function( d, isNew, unid, sourceData ){
        var items = Object.clone(this.itemTemplate);
        for (var it in d ){
            if ( items[ it ] ){
                items[ it ].value  = d[it];
            }
        }
        var trObj = this.createTr( items, isNew, unid, sourceData );
        // for( var it in this.itemTemplate ){
        //     this.itemTemplate[it].value = "";
        // }
        return trObj;
    },
    getTrCounts : function(){
        return  this.trList.length;
    },
    createTr : function( itemData, isNew, unid, sourceData ){
        if( this.options.maxTrCount  ){
            if( this.getTrCounts() < this.options.maxTrCount ){
                return this._createTr( itemData, isNew, unid, sourceData )
            }else{
                if( this.app && this.app.notice ){
                    var text = this.options.lp.addMaxLimitText.replace("{count}", this.options.maxTrCount);
                    this.app.notice(text,"error");
                }
            }
        }else{
            return this._createTr( itemData, isNew, unid, sourceData )
        }
    },
    _createTr : function( itemData, isNew, unid, sourceData ){
        this.fireEvent("queryCreateTr", [this]);

        var d;
        if( isNew ){
            this.fireEvent("newData", [this, function( data ){
                d = data;
            }.bind(this) ]);
            if( d ){
                //itemData, isNew, unid, sourceData
                itemData = Object.clone( this.itemTemplate );
                for (var it in d ){
                    if ( itemData[ it ] ){
                        itemData[ it ].value  = d[it];
                    }
                }
                isNew = false;
                sourceData = d;
            }
        }

        this.trIndex ++;
        var trOptions = {
            objectId : unid ? unid : "_"+this.trIndex,
            isEdited : this.options.isEdited,
            id : "_"+this.trIndex,
            index : this.trIndex,
            indexText : (this.maxIndexText ? this.maxIndexText++ : this.trIndex),
            hasSequence : this.options.hasSequence,
            hasOperation : this.options.hasOperation,
            align : this.options.tdAlign,
            isNew : isNew,
            className : this.options.tdClass,
            tdAttributes : this.options.tdAttributes
        };
        var template = null;
        if( this.trTemplate ){
            template = this.trTemplate.clone();
        }
        var trObj = new MDataGridTr(this.table, trOptions, itemData, this, template, sourceData );
        trObj.load();

        this.trObjs[ trOptions.objectId ] = trObj;
        this.trList.push( trObj );

        if( isNew ){
            this.trObjs_new[ trOptions.objectId ] = trObj;
        }

        this.fireEvent("postCreateTr",[this, trObj]);

        return trObj;
    },
    replaceTr : function( oldTrObjOr_Index, data, isNew, unid, sourceData ){
        var oldTrObj;
        if( typeof oldTrObjOr_Index == "string" ){ //如果传入的是  _index
            oldTrObj = this.trObjs[ oldTrObjOr_Index ];
        }else{
            oldTrObj = oldTrObjOr_Index;
        }
        var itemData = Object.clone(this.itemTemplate);
        for (var it in data ){
            if ( itemData[ it ] ){
                itemData[ it ].value  = data[it];
            }
        }
        var trIndex = oldTrObj.options.index;
        var trOptions = {
            objectId : unid ? unid : "_"+trIndex,
            isEdited : this.options.isEdited,
            id : "_"+trIndex,
            index : trIndex,
            indexText : trIndex,
            hasSequence : this.options.hasSequence,
            hasOperation : this.options.hasOperation,
            align : this.options.tdAlign,
            isNew : isNew,
            className : this.options.tdClass,
            tdAttributes : this.options.tdAttributes
        };
        var template = null;
        if( this.trTemplate ){
            template = this.trTemplate.clone();
        }
        var trObj = new MDataGridTr(this.table, trOptions, itemData, this, template, sourceData );
        trObj.load();

        //oldTrObj.mElement.replaceWith( trObj.mElement );
        trObj.mElement.inject( oldTrObj.mElement, "before" );

        var idx = this.trList.indexOf( oldTrObj );
        this.trList[idx] = trObj;

        this.trObjs[ trOptions.objectId ] = trObj;


        if( oldTrObj.options.isNew  ){
            this.trObjs_new[ oldTrObj.options.objectId ] = null;
        }else{
            this.trObjs_removed[ oldTrObj.options.objectId ] = oldTrObj;
        }

        if( isNew ){
            this.trObjs_new[ trOptions.objectId ] = trObj;
        }

        oldTrObj.mElement.destroy();

        // for( var it in this.itemTemplate ){
        //     this.itemTemplate[it].value = "";
        // }

        return trObj;
    },
    createRemoveButton : function( trObj, container ){
        var button = new Element("div", { title : this.options.lp.remove }).inject( container );
        if( this.css.actionRemove )button.setStyles( this.css.actionRemove );
        button.addEvents( {
            "click": function( e ){ this.removeTr( e, e.target, trObj ); }.bind(this)
        });
        if( this.css.actionRemove && this.css.actionRemove_over){
            button.addEvents({
                "mouseover" : function( e ){ this.node.setStyles( this.obj.css.actionRemove_over ) }.bind({ node : button, obj : this }),
                "mouseout" : function( e ){ this.node.setStyles( this.obj.css.actionRemove ) }.bind({ node : button, obj : this })
            })
        }
        return button
    },
    removeTr : function(e, el, trObj ){
        this.fireEvent("queryRemoveTr", [e, el, trObj]);

        var s = trObj.options;
        var id = s.objectId;

        if( ! s.isNew ){
            this.trObjs_removed[ id ] = this.trObjs[ id ];
        }

        this.trList.erase( trObj );

        this.trObjs[ id ] = null;
        //delete this.trObjs[ id ];

        if( this.trObjs_new[ id ] ){
            this.trObjs_new[ id ]= null;
            //delete this.trObjs_new[ id ];
        }

        //this.fireEvent("queryRemoveTr", [e, el.getParent("tr")]);

        trObj.destroy();

        this.adjustSequenceText();
        this.fireEvent("postRemoveTr", [e, el, trObj ]);
    },
    replaceText : function( value, selectValue, selectText ){
        var vals = typeOf( value ) == "array" ? value : value.split( this.valSeparator );
        var selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
        var selectTexts = typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);
        for( var i=0 ;i<vals.length; i++ ){
            for( var j= 0; j<selectValues.length; j++){
                if( vals[i] == selectValues[j] ){
                    vals[i] = selectTexts[j]
                }
            }
        }
        return vals;
    },


    createTr_textOnly : function( itemData ){
        var self = this;
        this.trIndex ++;
        if( this.trTemplate ){
            return this.createTr_textOnly_byTemplate( itemData );
        }else{
            return this.createTr_textOnly_noTemplate( itemData );
        }
    },
    createTr_textOnly_byTemplate : function( itemData ){
        var tr = this.trTemplate.clone();
        tr.set( "data-id", "_"+ this.trIndex );
        var labelContainers = tr.getElements("[lable]");
        var itemContainers = tr.getElements("[item]");
        var sequenceContainers = tr.getElements( "[sequence]" );

        labelContainers.each(function( el ) {
            var obj = itemData[el.get("lable")];
            if (!obj)return;
            if(obj.text)el.set("text", obj.text);
        });

        itemContainers.each(function( el ) {
            var obj = itemData[el.get("item")];
            if (!obj)return;
            var val = obj.value ? obj.value : "";
            var valtype = typeOf( val );
            if( valtype === "string" )val = val.replace(/\n/g,"<br/>");
            if( obj.selectValue && obj.selectText ){
                var vals = this.replaceText( val, obj.selectValue, obj.selectText );
                val = vals.join(",");
            }else{
                if( valtype === "string" )val = val.replace( this.valSeparator,"," );
            }
            el.set("html", val );
        }.bind(this));

        sequenceContainers.set("text", this.trIndex );

        tr.inject( this.table );

        return tr;
    },
    createTr_textOnly_noTemplate : function( itemData ){
        var tr = new Element("tr" , { "data-id" : "_"+this.trIndex });
        if( this.options.hasSequence  ){
            var td = new Element("td", { align : "center", text : this.trIndex }).inject( tr );
            if( this.options.sequenceClass && this.css[this.options.sequenceClass] )td.setStyles(this.css[this.options.sequenceClass]);
        }

        var attr = {};
        if( this.options.tdAlign )attr.align = this.options.tdAlign;

        var idx = 1;
        for (var it in itemData ){
            var tdAttributes = {};
            if(this.options.tdAttributes && this.options.tdAttributes["_"+idx] ){
                tdAttributes = this.options.tdAttributes["_"+idx];
            }
            var obj = itemData[it];
            var val = obj.value || "";
            var valtype = typeOf( val );
            if( valtype === "string" )val = val.replace(/\n/g,"<br/>");
            if( obj.selectValue && obj.selectText ){
                var vals = this.replaceText( val, obj.selectValue, obj.selectText );
                val = vals.join(",");
            }else{
                if( valtype === "string" )val = val.replace( this.valSeparator,"," )
            }
            var td = new Element("td", tdAttributes).inject( tr );
            td.set( "text", val );
            if( this.options.tdAlign )td.set( "align" , this.options.tdAlign);
            if( this.options.tdClass && this.css[this.options.tdClass] )td.setStyles(this.css[this.options.tdClass]);
            idx ++;
        }
        tr.inject( this.table );

        return tr;
    },

    getResult : function( verify, separator, isAlert, onlyModified, keepAllData ){
        var result = [];
        var trObjs = this.trObjs;
        var flag = true;
        for( var tr in trObjs ){
            if( tr && trObjs[tr] ){
                var data = trObjs[tr].getResult( verify, separator, isAlert, onlyModified , keepAllData );
                if( !data ){
                    if( this.options.verifyType == "batch" ){
                        flag= false;
                    }else{
                        return false;
                    }
                }else{
                    result.push( data )
                }
            }
        }
        if( flag ){
            return result;
        }else{
            return false ;
        }
    },
    enableItem : function( itemName ){
        for( var tr in this.trObjs ){
            if( tr && this.trObjs[tr] ){
                this.trObjs[tr].enableItem( itemName );
            }
        }
    },
    disableItem : function( itemName ){
        for( var tr in this.trObjs  ){
            if( tr && this.trObjs[tr] ){
                this.trObjs[tr].disableItem( itemName );
            }
        }
    },
    adjustSequenceText : function(){
        var array = [];
        for( var tr in this.trObjs  ){
            if( tr && this.trObjs[tr] ){
                array.push( this.trObjs[tr] )
            }
        }
        array.sort( function( a, b ){
            return a.options.index - b.options.index
        });
        array.each( function( o, index ){
            o.setSequenceText(index + 1 );
            this.maxIndexText = index + 2;
        }.bind(this))
    },
    string2DOM: function( str, container, callback ){
        var wrapper =	str.test('^<the|^<tf|^<tb|^<colg|^<ca') && ['<table>', '</table>', 1] ||
            str.test('^<col') && ['<table><colgroup>', '</colgroup><tbody></tbody></table>',2] ||
            str.test('^<tr') && ['<table><tbody>', '</tbody></table>', 2] ||
            str.test('^<th|^<td') && ['<table><tbody><tr>', '</tr></tbody></table>', 3] ||
            str.test('^<li') && ['<ul>', '</ul>', 1] ||
            str.test('^<dt|^<dd') && ['<dl>', '</dl>', 1] ||
            str.test('^<le') && ['<fieldset>', '</fieldset>', 1] ||
            str.test('^<opt') && ['<select multiple="multiple">', '</select>', 1] ||
            ['', '', 0];
        if( container ){
            var el = new Element('div', {html: wrapper[0] + str + wrapper[1]}).getChildren();
            while(wrapper[2]--) el = el[0].getChildren();
            el.inject( container );
            if( callback )callback( container );
            return el;
        }else{
            var div = new Element('div', {html: wrapper[0] + str + wrapper[1]});
            div.setStyle("display","none").inject( $(document.body) );
            if( callback )callback( div );
            var el = div.getChildren();
            while(wrapper[2]--) el = el[0].getChildren();
            div.dispose();
            return el;
        }
    },
    formatStyles: function( container ){
        container.getElements("[styles]").each(function(el){
            var styles = el.get("styles");
            if( styles && this.css[styles] ){
                el.setStyles( this.css[styles] )
            }
        }.bind(this));
        container.getElements("[class]").each(function(el){
            var className = el.get("class");
            if( className && this.css[className] ){
                el.setStyles( this.css[className] )
            }
        }.bind(this))
    },
});

var MDataGridTr = new Class({
    Implements: [Options, Events],
    options: {
        objectId : "",
        isEdited : true,
        id : "",
        index : 0,
        indexText : "0",
        hasSequence  : true,
        hasOperation : true,
        align : "left",
        isNew : true,
        className : "",
        tdAttributes : null
    },
    initialize: function (container,options,itemData, parent, template, sourceData) {

        this.setOptions(options);

        this.container = container;
        this.mElement = null;
        this.items = {};
        this.itemData = itemData;
        this.parent = parent;
        this.template = template;
        this.sourceData = sourceData;
        this.css = this.parent.css || {};
        this.app = this.parent.app;
    },
    load:function(){
        //if( this.options.isEdited ){
        //    this.create_Edit();
        //}else{
        //    this.create_Read();
        //}
        this.create();
    },
    reload : function( data ){
        if( !data )data = this.getResult(false, null, false, false, true );
        this.parent.replaceTr( this, data, this.options.isNew, this.options.objectId, this.sourceData )
    },
    //create_Read : function(){
    //    var tr = this.mElement = new Element("tr", { "data-id" : this.options.id }).inject(this.container);
    //
    //    var attr = {};
    //    if( this.options.align )attr.align = this.options.align;
    //
    //    var styles = {};
    //    if( this.options.className && this.css[this.options.className] )styles = this.css[this.options.className];
    //
    //    if( this.options.hasSequence  ){
    //        var td = this.sequenceTd = new Element("td", { align : "center", text : ( this.options.indexText || this.options.index) }).inject( tr );
    //        if( this.parent.options.sequenceClass && this.css[this.parent.options.sequenceClass] )td.setStyles(this.css[this.parent.options.sequenceClass]);
    //    }
    //
    //    var idx = 1;
    //    for (var it in this.itemData ){
    //        var tdAttr = this.options.tdAttributes && this.options.tdAttributes["_"+idx] ? this.options.tdAttributes["_"+idx] : {};
    //        var td = new Element("td", { "text" : this.itemData[it].value }).inject( tr );
    //        td.set( attr );
    //        td.set( tdAttr );
    //        td.setStyle( styles );
    //        idx++;
    //    }
    //},
    create : function(e, el){
        if( this.template ){
            this.create_byTemplate( e, el );
        }else{
            this.create_noTemplate( e, el );
        }
    },
    setSequenceText : function( text ){
        if(this.sequenceTd)this.sequenceTd.set("text",text);
    },
    create_byTemplate : function(){
        this.mElement = this.template;
        this.mElement.set("data-id", this.options.id );

        if( this.options.hasSequence  ){
            this.sequenceTd = this.mElement.getElement( "[sequence]" );
            if(this.sequenceTd)this.sequenceTd.set( "text" , ( this.options.indexText || this.options.index) );
        }

        this.mElement.getElements("[lable]").each(function( el ) {
            var itData = this.itemData[el.get("lable")];
            if (!itData)return;
            if(itData.text)el.set("text", itData.text);
        }.bind(this));

        this.mElement.getElements("[item]").each(function( el ) {
            var itData = this.itemData[el.get("item")];
            if (!itData)return;
            this.createItem( el, itData );
        }.bind(this));

        if( this.options.hasOperation && this.options.isEdited ){
            if( this.parent.options.minTrCount && this.options.index > this.parent.options.minTrCount ){
                var removeContainer = this.mElement.getElement("[button_remove]");
                this.parent.createRemoveButton( this, removeContainer );
            }
        }
        this.mElement.setStyle("display","");
        this.mElement.inject( this.container );
    },
    create_noTemplate : function(){
        this.mElement = new Element("tr", { "data-id" : this.options.id });

        var attr = {};
        if( this.options.align )attr.align = this.options.align;
        var styles = {};
        if( this.options.className && this.css[this.options.className] )styles = this.css[this.options.className];


        if( this.options.hasSequence  ){
            var td = this.sequenceTd = new Element("td", { align : "center", text : ( this.options.indexText || this.options.index) }).inject( this.mElement );
            if( this.parent.options.sequenceClass && this.css[this.parent.options.sequenceClass] )td.setStyles(this.css[this.parent.options.sequenceClass]);
        }

        var idx = 1;
        for (var it in this.itemData){
            var tdAttr = this.options.tdAttributes && this.options.tdAttributes["_"+idx] ? this.options.tdAttributes["_"+idx] : {};
            var td = new Element("td").inject( this.mElement );
            td.set(attr);
            td.set( tdAttr );
            td.setStyles( styles );
            var itData = this.itemData[it];
            this.createItem( td, itData );
            idx++;
        }
        if( this.options.hasOperation && this.options.isEdited ){
            if( this.parent.options.minTrCount && this.options.index > this.parent.options.minTrCount ) {
                var t = new Element("td", {align: "center", style: {width: "30px"}}).inject(this.mElement);
                var className = this.parent.options.removeActionTdClass;
                if( className && this.css[className] )t.setStyles(this.css[className]);
                this.parent.createRemoveButton(this, t);
            }else{
                var t = new Element("td").inject(this.mElement);
                var className = this.parent.options.removeActionTdClass;
                if( className && this.css[className] )t.setStyles(this.css[className]);
            }
        }
        this.mElement.inject( this.container );
    },
    createItem : function( container, itData ) {
        //if( itData.disable )return;
        itData.isEdited = this.options.isEdited;
        itData.objectId = itData.name;
        var item = new MDomItem(container, itData, this, this.app, this.css);
        if (this.options.isEdited){
            if (this.parent.options.verifyType == "batchSingle") {
                item.options.warningType = "single";
            } else {
                item.options.warningType = this.parent.options.verifyType;
            }
        }
        item.options.name = itData.name + "_" + this.options.index;
        item.index = this.options.index;
        item.parent = this;
        item.load();

        this.items[itData.objectId] = item;
    },
    //remove : function(){
    //    this.mElement.destroy();
    //},
    destroy : function(){
        Object.each(this.items, function(item){
            item.destroy();
        }.bind(this));
        this.mElement.destroy();
        MWF.release(this);
    },
    enableItem: function( itemName ){
        if( itemName && this.items[itemName] ){
            var item = this.items[itemName];
            if( item.options.disable ){
                item.enable();
            }
        }
    },
    disableItem: function( itemName ){
        if( itemName && this.items[itemName] ){
            var item = this.items[itemName];
            if( !item.options.disable ){
                item.disable();
            }
        }
    },
    getPrevSilbing : function(){
        var idx = this.parent.trList.indexOf( this );
        if( idx > 0 ){
            return this.parent.trList[ idx -1 ];
        }else{
            return null;
        }
    },
    getNextSilbing : function(){
        var idx = this.parent.trList.indexOf( this );
        if( idx!=-1 && idx < this.parent.trList.length - 1 ){
            return this.parent.trList[ idx + 1 ];
        }else{
            return null;
        }
    },
    verify : function(isShowWarming) {
        var flag = true;
        for (var it in this.items ) {
            if (!this.items[it].verify(isShowWarming)) {
                if (this.parent.options.verifyType == "batch" || this.parent.options.verifyType == "batchSingle") {
                    flag = false;
                } else {
                    return false;
                }
            }
        }
        return flag;
    },

    getItemsKeyValue : function(separator , onlyModified ) {
        //separator 多值合并分隔符
        var key_value = {};
        for (var it in this.items ) {
            var item = this.items[it];
            var value = onlyModified ? item.getModifiedValue() : item.getValue();
            if( value != null ){
                if (typeOf(value) === "array") {
                    key_value[item.options.objectId] = (typeOf(separator) == "string" ? value.join(separator) : value );
                } else {
                    key_value[item.options.objectId] = value;
                }
            }
        }
        return key_value;
    },

    getResult : function(verify, separator, isShowWarming, onlyModified, keepAllData ) {
        if ( !verify || this.verify(isShowWarming)) {
            if( keepAllData && this.sourceData ){
                var result = this.sourceData;
                var map = this.getItemsKeyValue(separator, onlyModified);
                for( var key in map ){
                    result[key] = map[key];
                }
                return result;
            }else{
                return this.getItemsKeyValue(separator, onlyModified);
            }

        } else {
            return false;
        }
    }
});

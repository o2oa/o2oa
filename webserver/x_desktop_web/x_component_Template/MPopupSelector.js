//提供两种方式，一种是传入selectValue,selectText, 另外一种是 用onLoadData 或 loadData 加载数据，
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xDesktop.requireApp("Template", "MSelector", null, false);
var MPopupSelector = new Class({
    Extends: MSelector,
    options: {
        "style": "default",
        "width": "230px",
        "height": "30px",
        "trigger" : "delay", //immediately
        "isChangeOptionStyle" : false,
        "emptyOptionEnable" : true,

        "textField" : "",
        "valueField" : "",

        "value" : "",
        "text" : "",
        "defaultVaue" : "",
        "selectValue" : "",
        "selectText" : "",

        "tooltipsOptions" : {
            axis: "y",      //箭头在x轴还是y轴上展现
            position : { //node 固定的位置
                x : "center", //x轴上left center right,  auto 系统自动计算
                y : "bottom" //y 轴上top middle bottom, auto 系统自动计算
            },
            event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
            hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
            displayDelay : 0   //ms , 有target 且事件类型为 mouseenter 时有效
        }
    },
    initialize: function ( targetCoordinates, options , app, css, dropdownContainer ) {
        this.setOptions(options);

        this.path = "/x_component_Template/$MSelector/";
        this.cssPath = "/x_component_Template/$MSelector/"+this.options.style+"/css.wcss";
        this._loadCss();
        if( css ) {
            this.css = Object.merge(this.css, css);
        }

        this.valSeparator = /,|;|\^\^|\|/; //如果是多值对象，作为用户选择的多个值的分隔符
        this.targetCoordinates = targetCoordinates;
        this.app = app;
        this.dropdownContainer = dropdownContainer || $(dropdownContainer);
    },
    loadEdit:function( callback ){
        this.initPara();

        if( this.data ){
            this.loadContent( this.data );
        }else{
            this._loadData( function( data ){
                this.data = this.parseData( data );
                this.loadContent( this.data );
            }.bind(this))
        }

        //this.node.addEvent( "click" , function( ev ){
        //    this.loadContent();
        //    ev.stopPropagation();
        //}.bind(this));

        if(callback)callback();

    },
    setTargetCoordinates : function( targetCoordinates ){
        this.targetCoordinates = targetCoordinates;
        this.contentTooltip.targetCoordinates = targetCoordinates;
    },
    loadContent : function( data ){
        if( !this.contentTooltip ){
            var width = parseInt(this.options.width)+"px";
            this.css.tooltipNode.width = width;
            this.css.tooltipNode["max-width"] = width;
            var options = Object.merge({
                nodeStyles : this.css.tooltipNode,
                onPostLoad : function(){
                    this.selectArrowNode.setStyles( this.css.selectArrowNode_up );
                    if( this.inputNode ){
                        this.inputNode.focus();
                    }
                }.bind(this),
                onPostInitialize : function(){
                    if(this.options.trigger == "immediately" ){
                        this.contentTooltip.load();
                    }
                }.bind(this)
            }, this.options.tooltipsOptions );
            this.contentTooltip = new MSelector.Tootips( this.dropdownContainer || this.app.content, null, this.app, data, options, this.targetCoordinates );
            this.contentTooltip.selector = this;
        }
    },
    setCurrentItem : function( itemNode ){
        var data = itemNode.retrieve( "data" );
        if( this.currentItemNode ){
            this.currentItemNode.setStyles( this.css.listItemNode );
        }

        this.currentItemNode = itemNode;
        this.currentItemData = data;
        this.currentItemText = itemNode.get("text");

        if( this.options.isChangeOptionStyle )itemNode.setStyles( this.css.listItemNode_current );
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        //if(callback)callback();
        this.fireEvent("loadData",callback );
    },
    _postCreateItem: function(listItemNode, data){

    }
});
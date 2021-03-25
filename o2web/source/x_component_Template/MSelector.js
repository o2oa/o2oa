//提供两种方式，一种是传入selectValue,selectText, 另外一种是 用onLoadData 或 loadData 加载数据，
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
var MSelector = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "230px",
        "height": "30px",
        "defaultOptionLp" : "请选择",
        "trigger" : "delay", //immediately
        "isSetSelectedValue" : true,
        "isChangeOptionStyle" : true,
        "inputEnable" : false,
        "isCreateReadNode" : true, //适应给MDomitem的做法
        "emptyOptionEnable" : true,
        "containerIsTarget" : false,
        "tooltipWhenNoSelectValue" : false,
        "hasScrollBar" : true,

        "hideByClickBody" : false,

        "textField" : "",
        "valueField" : "",

        "value" : "",
        "text" : "",
        "defaultVaue" : "",
        "selectValue" : "",
        "selectText" : "",

        "isEdited" : true,

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
    initialize: function (container, options , app, css, dropdownContainer ) {
        this.setOptions(options);
        if( !this.options.isEdited && !this.options.isCreateReadNode ){
        }else{
            this.path = "../x_component_Template/$MSelector/";
            this.cssPath = "../x_component_Template/$MSelector/"+this.options.style+"/css.wcss";
            this._loadCss();
            if( css ){
                this.css = Object.merge( Object.clone(this.css), css )
            }
        }

        this.valSeparator = /,|;|\^\^|\|/; //如果是多值对象，作为用户选择的多个值的分隔符

        this.app = app;
        this.container = $(container);
        this.dropdownContainer = dropdownContainer || $(dropdownContainer);
    },
    load : function( callback ){
      if( this.options.isEdited ){
          this.loadEdit(callback)
      }else{
          this.loadRead(callback)
      }
    },
    initPara: function(){
        this.itemNodeList = [];
        this.itemNodeObject = {};
        this.value = this.options.value || this.options.text || this.options.defaultVaue;
        this.text = this.options.text || this.options.value || this.options.defaultVaue;
        this.textField = this.options.textField || this.options.valueField;
        this.valueField = this.options.valueField || this.options.textField;
        if( this.options.selectValue || this.options.selectText ){
            this.textField = "text";
            this.valueField = "value";

            var selectValue = this.options.selectValue;
            var selectText = this.options.selectText;

            this.selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
            this.selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);

            this.data = [];
            if( this.options.emptyOptionEnable ){
                this.data.push({
                    value : "",
                    text : this.options.defaultOptionLp || ""
                })
            }
            this.selectValues.each( function( v,i ){
                this.data.push({
                    value : v,
                    text : this.selectTexts[i]
                })
            }.bind(this))
        }
    },
    loadRead: function(callback){
        this.initPara();
        var fun = function(){
            for( var i=0; i<this.data.length; i++ ){
                var d = this.data[i];
                if( this.options.text ){
                    if( d[this.textField] == this.options.text ){
                        this.value = d[this.valueField];
                        this.text = this.options.text;
                        this.currentItemData = d;
                        break;
                    }
                }else if( this.options.value ){
                    if( d[this.valueField] == this.options.value ){
                        this.value = this.options.value;
                        this.text = d[this.textField];
                        this.currentItemData = d;
                        break;
                    }
                }
            }
            this.loadReadNode( this.text );
            if( callback )callback();
        }.bind(this);

        if( this.data ){
            fun()
        }else{
            this._loadData( function( data ) {
                this.data = this.parseData(data);
                fun();
            }.bind(this))
        }
    },
    loadReadNode: function( text ){
        this.fireEvent( "loadReadNode", text );
        if( this.options.isCreateReadNode ){
            if( this.node )this.node.destroy();
            this.node = new Element("div", {
                styles : this.css.readNode,
                text : text
            }).inject( this.container );
        }
    },
    loadEdit:function( callback ){
        this.initPara();
        if( !this.node ){
            if( this.options.containerIsTarget ){
                this.node = this.container
            }else{
                this.node = new Element("div.selectNode", {
                    styles : this.css.selectNode
                }).inject( this.container );

                this.node.setStyles({
                    "width":this.options.width,
                    "height":this.options.height
                });
            }
        }

        if( this.data ){
            this.createDefaultItem();
            this.loadContent( this.data );
            this.fireEvent("postLoad", [this]);
        }else{
            this._loadData( function( data ){
                this.data = this.parseData( data );
                this.createDefaultItem();
                this.loadContent( this.data );
                this.fireEvent("postLoad", [this]);
            }.bind(this))
        }

        //this.node.addEvent( "click" , function( ev ){
        //    this.loadContent();
        //    ev.stopPropagation();
        //}.bind(this));

        if(callback)callback();

    },
    resetOptions : function(){
        if( this.contentTooltip ){
            this.contentTooltip.destroy();
            this.contentTooltip = null;
        }
        if( this.node ){
            this.node.empty()
        }
        //if( this.node && this.options.containerIsTarget ){
        //    var node = this.node;
        //
        //    this.node = new Element("div.selectNode", {
        //        styles : this.css.selectNode
        //    }).inject( node, "after" );
        //
        //    this.node.setStyles({
        //        "width":this.options.width,
        //        "height":this.options.height
        //    });
        //
        //    node.destroy();
        //}
        this.data = null;
        this.load();
    },
    addOption : function(text, value){
        var obj = {};
        obj[this.textField] = text;
        obj[this.valueField] = value;
        this.data.push( obj );
        if( this.contentTooltip ){
            this.contentTooltip.createItem(obj);
        }
    },
    deleteOption : function(value){
        for( var i=0; i<this.itemNodeList.length; i++ ){
            var listItemNode = this.itemNodeList[i];
            var data = listItemNode.retrieve("data");
            if( data[this.valueField] == value ){
                this.itemNodeList.erase(listItemNode);
                listItemNode.destroy();
                break;
            }
        }
        if(this.itemNodeObject[ value ])delete this.itemNodeObject[ value ];

        if(this.data){
            for( var i=0; i<this.data.length; i++ ) {
                var d = this.data[i];
                if( d[this.valueField] == value ){
                    this.data.erase(d);
                }
            }
        }
    },
    loadContent : function( data ){
        if( !this.contentTooltip ){
            var width = parseInt(this.options.width)+"px";
            this.css.tooltipNode.width = width;
            this.css.tooltipNode["max-width"] = width;
            var options = Object.merge({
                hideByClickBody : this.options.hideByClickBody,
                nodeStyles : this.css.tooltipNode,
                onPostLoad : function(){
                    if( this.selectArrowNode )this.selectArrowNode.setStyles( this.css.selectArrowNode_up );
                    if( this.inputNode ){
                        this.inputNode.focus();
                    }

                    var parent = this.node.getParent();
                    var zIndex;
                    while( parent ){
                        zIndex = parent.getStyle("z-index");
                        if( zIndex && parseFloat(zIndex).toString() !== "NaN" ){
                            parent = null;
                        }else{
                            parent = parent.getParent();
                        }
                    }
                    if( zIndex && parseFloat(zIndex).toString() !== "NaN" ){
                        this.contentTooltip.node.setStyle("z-index", parseFloat( zIndex )+1)
                    }else{
                        this.contentTooltip.node.setStyle("z-index", "auto")
                    }

                    parent = this.node.getParent();
                    while( parent ){
                        var overflow = parent.getStyle("overflow");
                        var overflowY = parent.getStyle("overflow-y");
                        if(  overflow === "auto" || overflow === "scroll" || overflowY === "auto" || overflowY === "scroll" ){
                            this.scrollFun = function( e ){
                                this.contentTooltip.setPosition();
                            }.bind(this);
                            this.scrollParentNode = parent;
                            parent.addEvent( "scroll", this.scrollFun );
                            parent = null;
                        }else{
                            parent = parent.getParent();
                        }
                    }
                }.bind(this),
                onPostInitialize : function(){
                    if(this.options.trigger == "immediately" ){
                        this.contentTooltip.load();
                    }
                }.bind(this),
                onHide : function(){
                    this.status = "hidden";
                    if(this.selectArrowNode) this.selectArrowNode.setStyles( this.css.selectArrowNode );
                    //this.node.setStyles(this.css.selectNode);
                    if( this.scrollParentNode && this.scrollFun ){
                        this.scrollParentNode.removeEvent("scroll", this.scrollFun);
                    }
                }.bind(this)
            }, this.options.tooltipsOptions );
            this.contentTooltip = new MSelector.Tootips( this.dropdownContainer || this.app.content, this.node, this.app, data, options );
            this.contentTooltip.selector = this;
        }
    },
    setWidth : function( width ){
        this.options.width = width;
        if( this.contentTooltip ){
            this.contentTooltip.options.nodeStyles.width = width;
            this.contentTooltip.options.nodeStyles["max-width"] = width;
            if( this.contentTooltip.nodeStyles ){
                this.contentTooltip.nodeStyles.width = width;
                this.contentTooltip.nodeStyles["max-width"] = width;
            }
            if(this.contentTooltip.node){
                this.contentTooltip.node.setStyle("width",width);
                this.contentTooltip.node.setStyle("max-width",width);
            }
        }
        if( this.node ){
            this.node.setStyle("width",width);
        }
        this.selectValueNode.setStyle("width",parseInt(width)-25);
    },
    createDefaultItem:function(){
        if( this.options.containerIsTarget )return;
        this.selectValueNode = new Element("div.selectValueNode",{
            "styles":this.css.selectValueNode
        }).inject(this.node);
        this.selectValueNode.setStyles({
            "width":(parseInt(this.options.width)-parseInt(this.options.height)-10)+"px",
            "height":this.options.height,
            "line-height":this.options.height
        });

        var d = this._getData( this.options.value );
        var text = d ? d[ this.textField ] : this.options.value;
        if( this.options.inputEnable ){
            this.inputNode = new Element("input", {
                "value" : text || this.options.defaultOptionLp ,
                "styles" : this.css.inputNode
            }).inject( this.selectValueNode );

            this.inputNode.addEvents( {
                focus : function(){
                    if( this.inputNode.get("value") == this.options.defaultOptionLp ){
                        this.inputNode.set("value", "" );
                    }
                }.bind(this),
                blur : function(){
                    var flag = false;
                    var val = this.inputNode.get("value");
                    if( val == "" ){
                        this.inputNode.set("value", this.options.defaultOptionLp);
                    }else{
                        for( var i=0; i<this.data.length; i++ ){
                            var d = this.data[i];
                            if( d[ this.textField ] == val ){
                                var itemNode = this.itemNodeObject[ d[this.valueField] ];
                                this.setCurrentItem( itemNode );
                                flag = true;
                                break;
                            }
                        }
                        if( !flag ){
                            this.cancelCurrentItem();
                        }
                    }
                }.bind(this)
            } )
        }else{
            this.selectValueNode.set("text", text || this.options.defaultOptionLp);
        }

        this.selectArrowNode = new Element("div.selectArrowNode",{
            "styles":this.css.selectArrowNode
        }).inject(this.node);
        this.selectArrowNode.setStyles({
            "width":this.options.height,
            "height":this.options.height
        });
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

        if( this.options.isSetSelectedValue && this.selectValueNode ){
            if( this.options.inputEnable ){
                this.inputNode.set("value", data[ this.textField ] );
            }else{
                this.selectValueNode.set("text", data[ this.textField ] );
            }
        }
    },
    cancelCurrentItem: function(){
        if( this.currentItemNode ){
            this.currentItemNode.setStyles( this.css.listItemNode );
        }
        this.currentItemNode = null;
        this.currentItemData = null;
        this.currentItemText = null;
    },
    parseData: function( data ){
        if( typeOf( data[0] ) == "string" ){
            var arr = [];
            this.textField = "text";
            this.valueField = "value";
            if(this.options.emptyOptionEnable ){
                arr.push({
                    value : "",
                    text : this.options.defaultOptionLp || ""
                });
            }
            data.each( function(d){
                arr.push({
                    value : d,
                    text : d
                })
            }.bind(this));
            return arr;
        }else{
            if( this.options.emptyOptionEnable && this.textField ){
                var obj = {};
                obj[ this.textField ] = this.options.defaultOptionLp || "";
                if( this.valueField != this.textField  )obj[ this.valueField ] = "";
                data.unshift( obj )
            }
            return data;
        }
    },
    destroy : function(){
        if( this.node )this.node.destroy();
        if( this.contentTooltip )this.contentTooltip.destroy();
    },
    showTooltip : function(){
        this.contentTooltip.load();
    },
    hide : function(){
        this.status = "hidden";
        if(this.selectArrowNode) this.selectArrowNode.setStyles( this.css.selectArrowNode );
        if( this.contentTooltip )this.contentTooltip.hide();
    },
    setValue : function( value ){
        if( this.options.isEdited ){
            var itemNode = this.itemNodeObject[ value ];
            if( itemNode ){
                this.setCurrentItem( itemNode );
            }else if( this.options.inputEnable ) {
                var d = this._getData( value );
                if( d ){
                    this.inputNode.set("value", d[ this.textField ] );
                }else{
                    this.inputNode.set("value", value );
                }
            }else{
                if( this.options.isSetSelectedValue && this.selectValueNode ){
                    var d = this._getData( value );
                    if( d ) {
                        this.selectValueNode.set("text", d[this.textField]);
                    }else{
                        this.selectValueNode.set("text", value || "");
                    }
                    this.value = value;
                }
            }
        }else{
            var d = this._getData( value );
            if( d ){
                this.loadReadNode( d[this.textField] );
            }else{
                this.loadReadNode( value );
            }
        }
    },
    get : function(){
        return {
            "value" : this.getValue(),
            "text" : this.getText()
        }
    },
    getValue : function(){
        if( this.options.isEdited ){
            if( this.currentItemData && this.valueField ) {
                return this.currentItemData[this.valueField]
            }else if( this.inputNode ){
                return this.inputNode.get("value");
            }else{
                return this.value;
            }
        }else{
            return this.value;
        }
    },
    getText : function(){
        if( this.options.isEdited ){
            if( this.currentItemData && this.textField ) {
                return this.currentItemData[this.textField]
            }else if( this.inputNode ){
                var d = this._getData( this.inputNode.get("value"), "text" );
                if( d ){
                    return d[ this.textField ];
                }else{
                    return this.inputNode.get("value");
                }
            }else{
                return this.text;
            }
        }else{
            return this.text;
        }
    },
    getData : function(){
        if( this.currentItemData )return this.currentItemData;
        if( this.inputNode )return this.inputNode.get("value");
        if( !this.options.text || !this.options.value )return null;
        for( var i=0; i<this.data.length; i++ ){
            var d = this.data[i];
            if( this.options.text ){
                if( d[this.textField] == this.options.text ){
                    return d;
                }
            }else if( this.options.value ){
                if( d[this.valueField] == this.options.value ){
                    return d;
                }
            }
        }
        return null;
    },
    _getData : function( vort, type ){
        for( var i=0; i<this.data.length; i++ ){
            var d = this.data[i];
            if( type == "text" ){
                if( d[this.textField] == vort ){
                    return d;
                }
            }else{
                if( d[this.valueField] == vort ){
                    return d;
                }
            }
        }
        return null;
    },
    _selectItem : function( itemNode, itemData, ev ){
        // this.fireEvent("selectItem", [itemNode, itemData, ev] );
    },
    _loadData : function( callback ){
        //if(callback)callback();
        this.fireEvent("loadData",callback );
    },
    _postCreateItem: function(listItemNode, data){

    }
});

MSelector.Tootips = new Class({
    Extends: MTooltips,
    options : {
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "center", //x轴上left center right,  auto 系统自动计算
            y : "bottom" //y 轴上top middle bottom, auto 系统自动计算
        },
        event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : false
    },
    _customNode : function( node, contentNode ){
        //var width = ( parseInt( this.selector.options.width )  )+ "px";
        //node.setStyles({
        //    "width": width,
        //    "max-width": width
        //});
        debugger;
        if( this.data && this.data.length > 0 ){
            this.createItemList( this.data, contentNode )
        }else if( this.selector.options.tooltipWhenNoSelectValue ){
            this.createNoSelectValueNode( contentNode );
        }
    },
    createNoSelectValueNode:function( node ){
        var _selector = this.selector;
        this.css = _selector.css;

        if(_selector.selectArrowNode)_selector.selectArrowNode.setStyles( this.css.selectArrowNode_up );

        _selector.listContentNode = new Element("div.listContentNode",{
            "styles":this.css.listContentNode
        }).inject( node );

        _selector.listNode = new Element("div.listNode",{
            "styles":this.css.listNode
        }).inject(_selector.listContentNode);

        var noTooltipNode = new Element("div.listItemNode",{
            "styles":this.css.listItemNode,
            "text" : _selector.options.tooltipWhenNoSelectValue
        }).inject(_selector.listNode);

        var height = parseFloat(_selector.options.height)+"px";
        noTooltipNode.setStyles({
            "height": height,
            "line-height":height
        });
    },
    createItemList:function(data, node){
        data = data || [];
        var _selector = this.selector;
        this.css = _selector.css;

        if(_selector.selectArrowNode)_selector.selectArrowNode.setStyles( this.css.selectArrowNode_up );

        _selector.listContentNode = new Element("div.listContentNode",{
            "styles":this.css.listContentNode
        }).inject( node );

        //_selector.listContentNode.setStyles({
        //    "width": node.getSize().x+"px"
        //});

        _selector.listNode = new Element("div.listNode",{
            "styles":this.css.listNode
        }).inject(_selector.listContentNode);
        if( _selector.options.hasScrollBar )_selector.setScrollBar(_selector.listNode);

        data.each(function(d){
            this.createItem( d );
        }.bind(this));

    },
    createItem: function( data ){
        var _selector = this.selector;

        if( !_selector.listNode )return;

        var listItemNode = new Element("div.listItemNode",{
            "styles":this.css.listItemNode,
            "text": data[ _selector.textField ]
        }).inject(_selector.listNode);

        listItemNode.setStyles({
            "height":_selector.options.height,
            "line-height":_selector.options.height
        });

        if(data)listItemNode.store("data",data);

        listItemNode.addEvents({
            "mousedown" : function(ev){
                ev.stopPropagation();
            },
            "click":function(ev){
                var _self = this.obj;
                var data = this.itemNode.retrieve( "data" );
                _self.selector.setCurrentItem( this.itemNode );
                _self.selector._selectItem( this.itemNode, data, ev );
                _self.selector.fireEvent("selectItem", [ this.itemNode, data, ev ] );
                _self.hide();
                ev.stopPropagation();
            }.bind({ obj : this, itemNode : listItemNode }),
            "mouseover":function(){
                if( this.obj.selector.currentItemNode != this.itemNode || !this.obj.selector.options.isChangeOptionStyle ){
                    this.itemNode.setStyles( this.obj.selector.css.listItemNode_over );
                }
            }.bind( {obj : this, itemNode : listItemNode }),
            "mouseout":function(){
                if( this.obj.selector.currentItemNode != this.itemNode || !this.obj.selector.options.isChangeOptionStyle ){
                    this.itemNode.setStyles( this.obj.selector.css.listItemNode );
                }
            }.bind( {obj : this, itemNode : listItemNode })
        });
        _selector.itemNodeList.push( listItemNode );

        _selector.itemNodeObject[ data[ _selector.valueField ] ] = listItemNode;

        var isCurrent = false;
        if( _selector.currentItemData ){
            isCurrent = data[ _selector.valueField ] == _selector.currentItemData[ _selector.valueField ];
        }else if( _selector.value ){
            isCurrent = data[ _selector.valueField ] == _selector.value;
        }else if( _selector.text ){
            isCurrent = data[ _selector.textField ] == _selector.text;
        }
        if( isCurrent )_selector.setCurrentItem( listItemNode );

        _selector.fireEvent("postCreateItem", [ listItemNode, data ] );
        _selector._postCreateItem(listItemNode, data)
    }
});
MWF.xApplication.Template = MWF.xApplication.Template || {};
MWF.xApplication.Template.MSelector = MSelector;

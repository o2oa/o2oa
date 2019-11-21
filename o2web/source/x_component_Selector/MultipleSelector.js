MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "lp."+MWF.language, null, false);
//MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);
MWF.xApplication.Selector.MultipleSelector = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "types" : [],
        "count": 0,
        "title": "Select",
        "groups": [], //选person, group, role 时的范围
        "roles": [], //选选person, group, role 时的范围
        "units": [], //选 company, department, duty, identity 时的范围
        "values" : [],

        "zIndex": 1000,
        "expand": true
    },
    initialize: function(container, options){
        this.setOptions(options);

        this.path = "/x_component_Selector/$Selector/";
        this.cssPath = "/x_component_Selector/$Selector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);
        this.lp = MWF.xApplication.Selector.LP;

        this.lastPeople = "";
        this.pageCount = "13";
        this.selectedItems = [];
        this.selectedItemsObject = {};
        this.items = [];
        this.selectors = {};
    },
    load: function(){
        if (layout.mobile){
            this.loadMobile();
        }else{
            this.loadPc();
        }
        this.fireEvent("load");
    },
    loadMobile: function(){
        this.maskRelativeNode = $(document.body);
        this.maskRelativeNode.mask({
            "destroyOnHide": true,
            "style": this.css.maskNode
        });
        this.node = new Element("div", {"styles": this.css.containerNodeMobile});
        this.node.setStyle("z-index", this.options.zIndex.toInt()+1);
        this.node.setStyle("height", ( document.body.getSize().y ) + "px");
        this.titleNode = new Element("div", {
            "styles": this.css.titleNodeMobile
        }).inject(this.node);

        this.titleCancelActionNode = new Element("div", {
            "styles": this.css.titleCancelActionNodeMobile,
            "text": MWF.SelectorLP.back
        }).inject(this.titleNode);
        this.titleOkActionNode = new Element("div", {
            "styles": this.css.titleOkActionNodeMobile,
            "text": MWF.SelectorLP.ok
        }).inject(this.titleNode);

        this.titleTextNode = new Element("div", {
            "styles": {
                "margin": "0px 50px",
                "height": "40px",
                "padding": "0px 10px",
                "color": "#FFF",
                "font-weight": "bold",
                "font-size": "14px",
                "line-height": "40px"
                //"overflow" : "hidden"
            }
            //"text": this.options.title
        }).inject(this.titleNode);

        this.contentNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.node);

        var size = document.body.getSize();
        //var height = size.y-40;
        var height = size.y;
        this.contentNode.setStyle("height", ""+height+"px");
        this.contentNode.setStyle("margin-top", "2px");

        this.loadContent();

        this.node.inject(document.body);
        this.node.setStyles({
            "top": "0px",
            "left": "0px"
        });

        this.setEvent();
    },
    loadPc: function(){
        this.css.maskNode["z-index"] = this.options.zIndex;
        var position = this.container.getPosition(this.container.getOffsetParent());
        this.container.mask({
            "destroyOnHide": true,
            "style": this.css.maskNode,
            "useIframeShim": true,
            "iframeShimOptions": {"browsers": true},
            "onShow": function(){
                this.shim.shim.setStyles({
                    "opacity": 0,
                    "top": ""+position.y+"px",
                    "left": ""+position.x+"px"
                });
            }
        });
        //  this.container.setStyle("z-index", this.options.zIndex);
        this.node = new Element("div", {
            "styles": this.css.containerNode_multiple //this.isSingle() ? this.css.containerNodeSingle_multiple : this.css.containerNode_multiple
        });
        this.node.setStyle("z-index", this.options.zIndex.toInt()+1);
        this.titleNode = new Element("div", {
            "styles": this.css.titleNode
        }).inject(this.node);

        this.titleActionNode = new Element("div", {
            "styles": this.css.titleActionNode
        }).inject(this.titleNode);
        this.titleTextNode = new Element("div", {
            "styles": this.css.titleTextNode,
            "text": this.options.title
        }).inject(this.titleNode);

        this.contentNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.node);

        this.actionNode = new Element("div", {
            "styles": this.css.actionNode
        }).inject(this.node);
        //if ( this.isSingle() ) this.actionNode.setStyle("text-align", "center");
        this.loadAction();

        this.node.inject(this.container);

        if( this.options.width || this.options.height ){
            this.setSize()
        }

        this.loadContent();

        this.node.position({
            relativeTo: this.container,
            position: "center",
            edge: "center"
        });

        var size = this.container.getSize();
        var nodeSize = this.node.getSize();
        this.node.makeDraggable({
            "handle": this.titleNode,
            "limit": {
                "x": [0, size.x-nodeSize.x],
                "y": [0, size.y-nodeSize.y]
            }
        });

        this.setEvent();
    },
    isSingle : function(){
        var single = true;
        var flag = true;
        this.options.types.each( function( type, index ){
            var opt = this.options[ type + "Options" ];
            if( opt ){
                if( Number.convert(opt.count) !== 1 )single = false;
                flag = false;
            }
        }.bind(this));
        if( flag ){
            single = Number.convert( this.options.count ) === 1;
        }
        return single;
    },
    setEvent: function(){
        if (this.titleActionNode){
            this.titleActionNode.addEvent("click", function(){
                this.close();
            }.bind(this));
        }
        if (this.titleCancelActionNode){
            this.titleCancelActionNode.addEvent("click", function(){
                this.close();
            }.bind(this));
        }
        if (this.titleOkActionNode){
            this.titleOkActionNode.addEvent("click", function(){
                this.fireEvent("complete", [this.getSelectedItems(), this.getSelectedItemsObject() ]);
                this.close();
            }.bind(this));
        }
    },
    close: function(){
        this.fireEvent("close");
        this.node.destroy();
        (this.maskRelativeNode || this.container).unmask();
        MWF.release(this);
        delete this;
    },
    loadAction: function(){
        this.okActionNode = new Element("button", {
            "styles": this.css.okActionNode,
            "text": "确定"
        }).inject(this.actionNode);
        this.cancelActionNode = new Element("button", {
            "styles": this.css.cancelActionNode,
            "text": "取消"
        }).inject(this.actionNode);
        this.okActionNode.addEvent("click", function(){
            this.fireEvent("complete", [this.getSelectedItems(), this.getSelectedItemsObject() ]);
            this.close();
        }.bind(this));
        this.cancelActionNode.addEvent("click", function(){this.fireEvent("cancel"); this.close();}.bind(this));
    },
    loadContent: function(){
        if (layout.mobile){
            MWF.require("MWF.widget.Tab", function(){

                this.tab = new MWF.widget.Tab(this.titleTextNode, {"style": "orgMobile" });

                var width = this.container.getSize().x - 160; //160是确定和返回按钮的宽度
                var w = width / this.options.types.length - 2;
                var tabWidth = w < 60 ? w : 60;

                if( this.tab.css.tabNode ){
                    this.tab.css.tabNode["min-width"] = tabWidth+"px";
                }
                if( this.tab.css.tabNodeCurrent ){
                    this.tab.css.tabNodeCurrent["min-width"] = tabWidth+"px";
                }

                this.tab.load();
                this.tab.contentNodeContainer.inject(this.contentNode);
            }.bind(this), false);
        }else{
            MWF.require("MWF.widget.Tab", function(){
                this.tab = new MWF.widget.Tab(this.contentNode, {"style": this.options.tabStyle || "default" });
                this.tab.load();
            }.bind(this), false);
        }

        var isFormWithAction = window.location.href.toLowerCase().indexOf("workmobilewithaction.html") > -1;

        this.options.types.each( function( type, index ){

            var options = Object.clone( this.options );
            if( type.toLowerCase()==="identity" ){
                options.expand = false;
            }

            if( this.options[ type + "Options" ] ){
                options = Object.merge( options, this.options[ type + "Options" ] );
            }

            var pageNode = new Element( "div" ).inject( this.contentNode );

            var tab = this.tab.addTab( pageNode, this.lp[type], false );

            if( index === 0 && this.contentHeight ){
                //this.contentHeight = this.contentHeight - this.getOffsetY( tab.tabNode ) - tab.tabNode.getStyle("height").toInt();
                this.contentHeight = this.contentHeight - this.getOffsetY( tab.tab.tabNodeContainer ) - tab.tab.tabNodeContainer.getStyle("height").toInt();
            }

            var t = type.capitalize();
            if ((type.toLowerCase()==="unit") && ( options.unitType)){
                t = "UnitWithType";
            }
            if ((type.toLowerCase()==="identity") && (( options.dutys) && options.dutys.length && options.categoryType.toLowerCase()==="duty")){
                t = "IdentityWidthDuty";
            }

            MWF.xDesktop.requireApp("Selector", t, function(){
                if( type.toLowerCase()==="identity" && options.resultType && options.resultType === "person" ){
                    options.values = this.getValueByType( options.values, [ type, "person" ] );
                }else{
                    options.values = this.getValueByType( options.values, type );
                }

                //options.values = [];
                //if( options.multipleValues[type] ){
                //    options.values = options.multipleValues[type];
                //}
                //if( options[type+"Values"] && options[type+"Values"].length ){
                //    options.values = options.values.concat( options[type+"Values"] )
                //}
                //
                //options.names = [];
                //if( options.multipleNames[type] ){
                //    options.names = options.multipleNames[type];
                //}
                //if( options[type+"Names"] && options[type+"Names"].length ){
                //    options.names = options.names.concat( options[type+"Names"] )
                //}

                if( this.contentWidth )options.width = this.contentWidth;
                if( this.contentHeight )options.height = this.contentHeight;
                this.selectors[t] = new MWF.xApplication.Selector[t](this.container, options );
                this.selectors[t].loadContent( pageNode );
                this.selectors[t].setSize();
                if( layout.mobile ){

                    var containerSize = this.container.getSize();
                    var bodySize = $(document.body).getSize();

                    var size = {
                        "x" : Math.min( containerSize.x, bodySize.x ),
                        "y" : Math.min( containerSize.y, bodySize.y )
                    };

                    var height;
                    if( isFormWithAction ){
                        height = size.y-40-20-6-20;
                    }else{
                        height = size.y;
                    }
                    if(this.selectors[t].selectNode){
                        this.selectors[t].selectNode.setStyle("height", ""+height+"px");
                    }
                    if( isFormWithAction ){
                        height = size.y-40-20-78 - 20;
                    }else{
                        height = size.y-42-31-40;
                    }
                    var itemAreaScrollNode = this.selectors[t].itemAreaScrollNode;
                    if( itemAreaScrollNode ){
                        itemAreaScrollNode.setStyle("height", ""+height+"px");
                    }

                    if( t.toLowerCase() == "person" || t.toLowerCase() == "group" ){
                        var startY=0, y=0;
                        itemAreaScrollNode.addEvents({
                            'touchstart' : function( ev ){
                                var touch = ev.touches[0]; //获取第一个触点
                                startY = Number(touch.pageY); //页面触点Y坐标
                            }.bind(this),
                            'touchmove' : function(ev){
                                var touch = ev.touches[0]; //获取第一个触点
                                y = Number(touch.pageY); //页面触点Y坐标
                            }.bind(this),
                            'touchend' : function( ev ){
                                if (startY - y > 10) { //向上滑动超过10像素
                                    var obj = this.selectors.Person;
                                    obj._scrollEvent( obj.itemAreaScrollNode.scrollTop + 100 );
                                }
                                startY = 0;
                                y = 0;
                            }.bind(this)
                        })
                    }
                }else{
                    //if( !this.isSingle() && Number.convert( options.count ) === 1 ){
                    //    this.selectors[t].selectNode.setStyles({
                    //        "float" : "none",
                    //        "margin-left" : "auto",
                    //        "margin-right" : "auto"
                    //    })
                    //}
                }
                if( index == 0 )tab.showIm();
            }.bind(this));
        }.bind(this));
    },
    getValueByType : function( values, type ){
        var result = [];
        values = typeOf( values == "array" ) ?  values : [values];
        var types = typeOf( type == "array" ) ?  type : [type];
        values.each( function( data ){
            if( typeOf( data ) == "string" ){
                var dn = data;
            }else{
                var dn = data.distinguishedName;
            }
            if (dn && type ){
                var flag = dn.substr(dn.length-1, 1);
                switch (flag.toLowerCase()){
                    case "i":
                        if( type == "identity" || types.contains( "identity" ) )result.push( data );
                        break;
                    case "p":
                        if( type == "person" || types.contains( "person" ) )result.push( data );
                        break;
                    case "u":
                        if( type == "unit" )result.push( data );
                        break;
                    case "g":
                        if( type == "group" )result.push( data );
                        break;
                    case "r":
                        if( type == "role" )result.push( data );
                        break;
                    default:
                        if( type == "person" )result.push( data );
                        break;
                        //result.push( data );
                }
            }else{
                //result.push( data );
            }
        });
        return result;
    },
    getSelectedItems : function(){
        this.selectedItems = [];
        for( var key in this.selectors ){
            var selector = this.selectors[key];
            if( selector.selectedItems && selector.selectedItems.length > 0 ){
                this.selectedItems = this.selectedItems.concat( selector.selectedItems );
            }
        }
        return this.selectedItems;
    },
    getSelectedItemsObject : function(){
        this.selectedItemsObject = {};
        for( var key in this.selectors ){
            var selector = this.selectors[key];
            if( selector.selectedItems && selector.selectedItems.length > 0 ){
                this.selectedItemsObject[key.toLowerCase()] = selector.selectedItems;
            }
        }
        return this.selectedItemsObject;
    },
    getOffsetX : function(node){
        return (node.getStyle("margin-left").toInt() || 0 )+
            (node.getStyle("margin-right").toInt() || 0 ) +
            (node.getStyle("padding-left").toInt() || 0 ) +
            (node.getStyle("padding-right").toInt() || 0 ) +
            (node.getStyle("border-left-width").toInt() || 0 ) +
            (node.getStyle("border-right-width").toInt() || 0 );
    },
    getOffsetY : function(node){
        return (node.getStyle("margin-top").toInt() || 0 ) +
            (node.getStyle("margin-bottom").toInt() || 0 ) +
            (node.getStyle("padding-top").toInt() || 0 ) +
            (node.getStyle("padding-bottom").toInt() || 0 )+
            (node.getStyle("border-top-width").toInt() || 0 ) +
            (node.getStyle("border-bottom-width").toInt() || 0 );
    },
    setSize : function(){
        if( !this.options.width && !this.options.height )return;

        if( this.options.width && this.options.width === "auto" ){
            //if (this.options.count.toInt() !== 1){
            this.node.setStyle("width", "auto");
            this.contentWidth = "auto";

        }else if( this.options.width && typeOf( this.options.width.toInt() ) === "number" ){
            var nodeWidth = this.options.width.toInt() - this.getOffsetX(this.node);
            this.node.setStyle("width", nodeWidth);

            if( this.contentNode ){
                nodeWidth = nodeWidth - this.getOffsetX( this.contentNode );
            }

            this.contentWidth = nodeWidth;
        }
        if( this.options.height && typeOf( this.options.height.toInt() ) === "number" ){
            var nodeHeight = this.options.height.toInt() - this.getOffsetY(this.node);
            this.node.setStyle("height", nodeHeight);

            if( this.titleNode ){
                nodeHeight = nodeHeight - this.getOffsetY( this.titleNode ) - this.titleNode.getStyle("height").toInt();
            }

            if( this.actionNode ){
                nodeHeight = nodeHeight - this.getOffsetY( this.actionNode ) - this.actionNode.getStyle("height").toInt();
            }

            if( this.contentNode ){
                nodeHeight = nodeHeight - this.getOffsetY( this.contentNode );
            }

            this.contentHeight = nodeHeight;
        }
    }
});

MWF.xApplication.Selector.MultipleSelector.Filter = new Class({
    Implements: [Options, Events],
    options: {
        "types" : [],
        "groups": [], //选person, group, role 时的范围
        "roles": [], //选选person, group, role 时的范围
        "units": [], //选 company, department, duty, identity 时的范围
    },
    initialize: function(value, options){
        this.setOptions(options);
        this.value = value;
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        this.selectors = {};

        this.options.types.each( function( type, index ){

            var opt = Object.clone( this.options );
            if( this.options[ type + "Options" ] ){
                opt = Object.merge( opt, this.options[ type + "Options" ] );
            }

            var t = type.capitalize();
            if ((type.toLowerCase()==="unit") && ( opt.unitType)){
                t = "UnitWithType";
            }
            if ((type.toLowerCase()==="identity") && ((opt.dutys) && opt.dutys.length)){
                t = "IdentityWidthDuty";
            }

            MWF.xDesktop.requireApp("Selector", t, function(){
                this.selectors[t] = new MWF.xApplication.Selector[t].Filter(this.value, opt);
            }.bind(this), false);
        }.bind(this));
    },
    filter: function(value, callback){
        this.value = value;
        var key = this.value;

        this.filterData = [];
        this.filterCount = 0;

        for (i in this.selectors){
            this.selectors[i].filter(value, function(data){
                this.filterData = this.filterData.concat(data);
                this.filterCount++;
                this.endFilter(callback);
            }.bind(this))
        }
        //
        // this.orgAction.listPersonByKey(function(json){
        //     data = json.data;
        //     if (callback) callback(data)
        // }.bind(this), failure, key);
    },
    endFilter: function(callback){
        if (this.filterCount>=Object.keys(this.selectors).length){
            if (callback) callback(this.filterData);
        }
    }
});
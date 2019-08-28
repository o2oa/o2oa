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
        this.container.mask({
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
        debugger;
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
            "styles": (this.options.count.toInt()==1) ? this.css.containerNodeSingle_multiple : this.css.containerNode_multiple
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

        this.loadContent();

        this.actionNode = new Element("div", {
            "styles": this.css.actionNode
        }).inject(this.node);
        if (this.options.count.toInt()==1) this.actionNode.setStyle("text-align", "center");
        this.loadAction();

        this.node.inject(this.container);
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
        this.container.unmask();
        MWF.release(this);
        delete this;
    },
    loadAction: function(){
        this.okActionNode = new Element("button", {
            "styles": this.css.okActionNode,
            "text": "确　定"
        }).inject(this.actionNode);
        this.cancelActionNode = new Element("button", {
            "styles": this.css.cancelActionNode,
            "text": "取 消"
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
                this.tab = new MWF.widget.Tab(this.contentNode, {"style": "default" });
                this.tab.load();
            }.bind(this), false);
        }
        this.options.types.each( function( type, index ){
            var pageNode = new Element( "div" ).inject( this.contentNode );

            var tab = this.tab.addTab( pageNode, this.lp[type], false );

            var t = type.capitalize();
            if ((type.toLowerCase()==="unit") && (this.options.unitType)){
                t = "UnitWithType";
            }
            if ((type.toLowerCase()==="identity") && ((this.options.dutys) && this.options.dutys.length)){
                t = "IdentityWidthDuty";
            }

            MWF.xDesktop.requireApp("Selector", t, function(){
                var options = Object.clone( this.options );
                options.values = this.getValueByType( this.options.values, type );

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

                this.selectors[t] = new MWF.xApplication.Selector[t](this.container, options );
                this.selectors[t].loadContent( pageNode );
                if( layout.mobile ){

                    var size = this.container.getSize();
                    var height = size.y-40-20-6 - 20; //80;
                    if(this.selectors[t].selectNode){
                        this.selectors[t].selectNode.setStyle("height", ""+height+"px");
                    }
                    height = size.y-40-20-78 - 20; //80;
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
                }
                if( index == 0 )tab.showIm();
            }.bind(this));
        }.bind(this));
    },
    getValueByType : function( values, type ){
        var result = [];
        values = typeOf( values == "array" ) ?  values : [values];
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
                        if( type == "identity" )result.push( data );
                        break;
                    case "p":
                        if( type == "person" )result.push( data );
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
            var t = type.capitalize();
            if ((type.toLowerCase()==="unit") && (this.options.unitType)){
                t = "UnitWithType";
            }
            if ((type.toLowerCase()==="identity") && ((this.options.dutys) && this.options.dutys.length)){
                t = "IdentityWidthDuty";
            }

            MWF.xDesktop.requireApp("Selector", t, function(){
                this.selectors[t] = new MWF.xApplication.Selector[t].Filter(this.value, options);
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
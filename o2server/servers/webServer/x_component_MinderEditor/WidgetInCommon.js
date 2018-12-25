MWF.xDesktop.requireApp("Template", "MSelector", null, false);

MWF.xApplication.MinderEditor.ExpandLevel = new Class({
    Extends: MSelector,
    options : {
        "style": "arrow",
        "width": "150px",
        "height": "30px",
        "textField" : "text",
        "valueField" : "value",
        "defaultOptionLp" : "展开节点",
        "isSetSelectedValue" : false,
        "isChangeOptionStyle" : false,
        "emptyOptionEnable" : false,
        "event" : "mouseenter",
        "containerIsTarget" : true,
        "tooltipsOptions" : {
            axis : "x",
            hasArrow : true
        }
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var levelList = [
            {
                value : '1',
                text : "展开到一级节点"
            },{
                value : '2',
                text : "展开到二级节点"
            },{
                value : '3',
                text : "展开到三级节点"
            },{
                value : '4',
                text : "展开到四级节点"
            },{
                value : '5',
                text : "展开到五级节点"
            },{
                value : '6',
                text : "展开到六级节点"
            },{
                value : '999',
                text : "展开全部"
            }
        ];
        if(callback)callback( levelList );
    },
    _postCreateItem: function( itemNode, data ){
        itemNode.setStyles( {
            "font-size" : "14px",
            "min-height" : "26px",
            "line-height" : "26px"
        } );
    }
});

MWF.xApplication.MinderEditor.SelectAll = new Class({
    Extends: MSelector,
    options : {
        "style": "arrow",
        "width": "150px",
        "height": "30px",
        "textField" : "text",
        "valueField" : "value",
        "defaultOptionLp" : "选择节点",
        "isSetSelectedValue" : false,
        "isChangeOptionStyle" : false,
        "emptyOptionEnable" : false,
        "event" : "mouseenter",
        "containerIsTarget" : true,
        "tooltipsOptions" : {
            axis : "x",
            hasArrow : true
        }
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var levelList = [
            {
                value : 'all',
                text : "全选"
            },{
                value : 'revert',
                text : "反选"
            },{
                value : 'siblings',
                text : "选择兄弟节点"
            },{
                value : 'level',
                text : "选择同级节点"
            },{
                value : 'path_',
                text : "选择路径"
            },{
                value : 'tree',
                text : "选择子树"
            }
        ];
        if(callback)callback( levelList );
    },
    _postCreateItem: function( itemNode, data ){
        itemNode.setStyles( {
            "font-size" : "14px",
            "min-height" : "26px",
            "line-height" : "26px"
        } );
    },
    _selectItem : function( itemNode, itemData ){
        this[ itemData.value ]();
    },
    all: function() {
        var selection = [];
        this.app.minder.getRoot().traverse(function(node) {
            selection.push(node);
        });
        this.app.minder.select(selection, true);
        this.app.minder.fire('receiverfocus');
    },
    revert: function() {
        var selected = this.app.minder.getSelectedNodes();
        var selection = [];
        this.app.minder.getRoot().traverse(function(node) {
            if (selected.indexOf(node) == -1) {
                selection.push(node);
            }
        });
        this.app.minder.select(selection, true);
        this.app.minder.fire('receiverfocus');
    },
    siblings: function() {
        var selected = this.app.minder.getSelectedNodes();
        var selection = [];
        selected.forEach(function(node) {
            if (!node.parent) return;
            node.parent.children.forEach(function(sibling) {
                if (selection.indexOf(sibling) == -1) selection.push(sibling);
            });
        });
        this.app.minder.select(selection, true);
        this.app.minder.fire('receiverfocus');
    },
    level: function() {
        var selectedLevel = this.app.minder.getSelectedNodes().map(function(node) {
            return node.getLevel();
        });
        var selection = [];
        this.app.minder.getRoot().traverse(function(node) {
            if (selectedLevel.indexOf(node.getLevel()) != -1) {
                selection.push(node);
            }
        });
        this.app.minder.select(selection, true);
        this.app.minder.fire('receiverfocus');
    },
    path_: function() {
        var selected = this.app.minder.getSelectedNodes();
        var selection = [];
        selected.forEach(function(node) {
            while(node && selection.indexOf(node) == -1) {
                selection.push(node);
                node = node.parent;
            }
        });
        this.app.minder.select(selection, true);
        this.app.minder.fire('receiverfocus');
    },
    tree: function() {
        var selected = this.app.minder.getSelectedNodes();
        var selection = [];
        selected.forEach(function(parent) {
            parent.traverse(function(node) {
                if (selection.indexOf(node) == -1) selection.push(node);
            });
        });
        this.app.minder.select(selection, true);
        this.app.minder.fire('receiverfocus');
    }
});

MWF.xApplication.MinderEditor.Template = new Class({
    Extends: MSelector,
    options : {
        "style": "minderTemplate",
        "width": "195px",
        "height": "30px",
        "defaultOptionLp" : "",
        "isSetSelectedValue" : false,
        "isChangeOptionStyle" : true,
        "emptyOptionEnable" : false,
        "event" : "mouseenter"
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var list = [
            {
                command : "default",
                picture : "default.png",
                title : "思维导图"
            },
            {
                command : "structure",
                picture : "structure.png",
                title : "目录组织图"
            },
            {
                command : "filetree",
                picture : "filetree.png",
                title : "组织结构图"
            },
            {
                command : "right",
                picture : "right.png",
                title : "逻辑结构图"
            },
            {
                command : "fish-bone",
                picture : "fish-bone.png",
                title : "鱼骨图"
            },
            {
                command : "tianpan",
                picture : "tianpan.png",
                title : "天盘"
            }
        ];
        var template = this.app.options.template;
        if( template && template.length > 0 ){
            for( var i = 0; i< list.length; i++){
                if( !template.contains(list[i].command) ){
                    list[i] = null;
                }
            }
        }
        list = list.clean();
        if(callback)callback( list );
    },
    _postCreateItem: function( itemNode, data ){

    },
    loadContent : function( data ){
        if( !this.contentTooltip ){
            var width = parseInt(this.options.width)+"px";
            this.css.tooltipNode.width = width;
            this.css.tooltipNode["max-width"] = width;
            this.options.tooltipsOptions.axis = "x";
            var options = Object.merge({
                nodeStyles : this.css.tooltipNode,
                onPostInitialize : function(){
                    if(this.options.trigger == "immediately" ){
                        this.contentTooltip.load();
                    }
                }.bind(this),
                onHide : function(){
                    this.status = "hidden";
                }.bind(this)
            }, this.options.tooltipsOptions );
            this.contentTooltip = new MWF.xApplication.MinderEditor.Template.Tootips( this.dropdownContainer || this.app.content, this.node, this.app, data, options );
            this.contentTooltip.selector = this;
        }
    }
});

MWF.xApplication.MinderEditor.Template.Tootips = new Class({
    Extends: MSelector.Tootips,
    options : {
        axis: "x",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "center", //x轴上left center right,  auto 系统自动计算
            y : "auto" //y 轴上top middle bottom, auto 系统自动计算
        },
        event : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : true
    },
    _customNode : function( node, contentNode ){
        //var width = ( parseInt( this.selector.options.width )  )+ "px";
        //node.setStyles({
        //    "width": width,
        //    "max-width": width
        //});
        this.createItemList( this.data, contentNode )
    },
    createItemList:function(data, node){
        data = data || [];
        var _selector = this.selector;
        this.css = _selector.css;

        _selector.listContentNode = new Element("div.listContentNode",{
            "styles":this.css.listContentNode
        }).inject( node );

        //_selector.listContentNode.setStyles({
        //    "width": node.getSize().x+"px"
        //});

        _selector.listNode = new Element("div.listNode",{
            "styles":this.css.listNode
        }).inject(_selector.listContentNode);
        _selector.setScrollBar(_selector.listNode);

        data.each(function(d){
            this.createItem( d );
        }.bind(this));

    },
    createItem: function( data ){
        var _selector = this.selector;
        var listItemNode = new Element("div.listItemNode",{
            "styles":this.css.listItemNode,
            "title" : data.title
        }).inject(_selector.listNode);
        listItemNode.setStyles({
            "background": "url("+ _selector.path + _selector.options.style + "/icon/"+data.picture + ") no-repeat center center"
        });

        if(data)listItemNode.store("data",data);

        listItemNode.addEvents({
            "click":function(ev){
                var _self = this.obj;
                var data = this.itemNode.retrieve( "data" );
                _self.selector.setCurrentItem( this.itemNode );
                _self.selector._selectItem( this.itemNode, data );
                _self.selector.fireEvent("selectItem", [ this.itemNode, data ] );
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

        _selector._postCreateItem(listItemNode, data)
    }
});

MWF.xApplication.MinderEditor.Theme = new Class({
    Extends: MSelector,
    options : {
        "style": "minderTheme",
        "width": "195px",
        "height": "30px",
        "defaultOptionLp" : "",
        "isSetSelectedValue" : false,
        "isChangeOptionStyle" : false,
        "emptyOptionEnable" : false,
        "event" : "mouseenter"
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        var list = [
            {
                command : 'classic',
                text : "经典"
            },
            {
                command : 'classic-compact',
                text : "经典紧凑"
            },
            {
                command : 'fresh-blue',
                text : "蓝色"
            },
            {
                command : 'fresh-blue-compat',
                text : "蓝色紧凑"
            },
            {
                command : 'fresh-green',
                text : "绿色"
            },
            {
                command : 'fresh-green-compat',
                text : "绿色紧凑"
            },
            {
                command : 'fresh-pink',
                text : "粉色"
            },
            {
                command : 'fresh-pink-compat',
                text : "粉色紧凑"
            },
            {
                command : 'fresh-purple',
                text : "紫色"
            },
            {
                command : 'fresh-purple-compat',
                text : "紫色紧凑"
            },
            {
                command : 'fresh-red',
                text : "红色"
            },
            {
                command : 'fresh-red-compat',
                text : "红色紧凑"
            },
            {
                command : 'fresh-soil',
                text : "黄色"
            },
            {
                command : 'fresh-soil-compat',
                text : "黄色紧凑"
            },
            {
                command : 'snow',
                text : "冷光"
            },
            {
                command : 'snow-compact',
                text : "冷光紧凑"
            },
            {
                command : 'tianpan',
                text : "天盘"
            },
            {
                command : 'tianpan-compact',
                text : "天盘紧凑"
            },
            {
                command : 'fish',
                text : "鱼骨图"
            },
            {
                command : 'wire',
                text : "线条"
            }
        ];
        var theme = this.app.options.theme;
        if( theme && theme.length > 0 ){
            for( var i = 0; i< list.length; i++){
                if( !theme.contains(list[i].command) ){
                    list[i] = null;
                }
            }
        }
        list = list.clean();
        if(callback)callback( list );
    },
    _postCreateItem: function( itemNode, data ){

    },
    loadContent : function( data ){
        this.options.tooltipsOptions.axis = "x";
        if( !this.contentTooltip ){
            var width = parseInt(this.options.width)+"px";
            this.css.tooltipNode.width = width;
            this.css.tooltipNode["max-width"] = width;
            var options = Object.merge({
                nodeStyles : this.css.tooltipNode,
                onPostInitialize : function(){
                    if(this.options.trigger == "immediately" ){
                        this.contentTooltip.load();
                    }
                }.bind(this),
                onHide : function(){
                    this.status = "hidden";
                }.bind(this)
            }, this.options.tooltipsOptions );
            this.contentTooltip = new MWF.xApplication.MinderEditor.Theme.Tootips( this.dropdownContainer || this.app.content, this.node, this.app, data, options );
            this.contentTooltip.selector = this;
        }
    }
});

MWF.xApplication.MinderEditor.Theme.Tootips = new Class({
    Extends: MSelector.Tootips,
    options : {
        axis: "x",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "center", //x轴上left center right,  auto 系统自动计算
            y : "bottom" //y 轴上top middle bottom, auto 系统自动计算
        },
        event : "mouseenter", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
        displayDelay : 0,   //ms , 有target 且事件类型为 mouseenter 时有效
        hasArrow : true
    },
    _customNode : function( node, contentNode ){
        //var width = ( parseInt( this.selector.options.width )  )+ "px";
        //node.setStyles({
        //    "width": width,
        //    "max-width": width
        //});
        this.createItemList( this.data, contentNode )
    },
    createItemList:function(data, node){
        data = data || [];
        var _selector = this.selector;
        this.css = _selector.css;

        _selector.listContentNode = new Element("div.listContentNode",{
            "styles":this.css.listContentNode
        }).inject( node );

        //_selector.listContentNode.setStyles({
        //    "width": node.getSize().x+"px"
        //});

        _selector.listNode = new Element("div.listNode",{
            "styles":this.css.listNode
        }).inject(_selector.listContentNode);
        _selector.setScrollBar(_selector.listNode);

        data.each(function(d){
            this.createItem( d );
        }.bind(this));

    },
    createItem: function( data ){
        var _selector = this.selector;
        var listItemNode = new Element("div.listItemNode",{
            "styles":this.css.listItemNode,
            "text" : data.text
        }).inject(_selector.listNode);

        if(data)listItemNode.store("data",data);

        listItemNode.addEvents({
            "click":function(ev){
                var _self = this.obj;
                var data = this.itemNode.retrieve( "data" );
                _self.selector.setCurrentItem( this.itemNode );
                _self.selector._selectItem( this.itemNode, data );
                _self.selector.fireEvent("selectItem", [ this.itemNode, data ] );
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

MWF.xApplication.MinderEditor.NotePrviewer = new Class({
    Implements: [Options, Events],
    Extends: MTooltips,
    options: {
        style : "default",
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "auto", //x 轴上left center right, auto 系统自动计算
            y : "auto" //y轴上top middle bottom,  auto 系统自动计算
        },
        overflow : "scroll",
        //event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        nodeStyles : {
            "font-size" : "12px",
            "position" : "absolute",
            "max-width" : "500px",
            "min-width" : "50px",
            "z-index" : "11",
            "background-color" : "#fff",
            "padding" : "10px",
            "border-radius" : "5px",
            "word-break" : "break-all",
            "box-shadow": "0 0 8px 0 #999999",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        }
    },
    initialize : function( editor, options ){
        //可以传入target 或者 targetCoordinates，两种选一
        //传入target,表示触发tooltip的节点，本类根据 this.options.event 自动绑定target的事件
        //传入targetCoordinates，表示 出发tooltip的位置，本类不绑定触发事件
        if( options ){
            this.setOptions(options);
        }
        this.editor = editor;
        this.app = editor;
        this.lp = editor.lp;
        this.container = editor.content;
        this.minder = editor.minder;

        this.minder.on('shownoterequest', function(e) {
            this.previewTimer = setTimeout(function() {
                this.load(e.node, e.keyword);
            }.bind(this), 300);
        }.bind(this));
        this.minder.on('hidenoterequest', function( e ) {
            if(this.previewTimer)clearTimeout(this.previewTimer);
            if( e && e.forceflag )this.hide();
        }.bind(this));

        this.hideFun = this.hide.bind(this);
        this.container.addEvents( {
            'mousedown': this.hideFun,
            'mousewheel': this.hideFun,
            'DOMMouseScroll': this.hideFun
        });
        this.editor.addEvent("resize",this.hideFun );

        this.fireEvent("postInitialize",[this]);
    },
    load: function( node, keyword ){
        this.fireEvent("queryLoad",[this]);
        if( this.isEnable() ){
            if( this.node ){
                this.show( node, keyword );
            }else{
                this.create( node, keyword );
            }
        }
        this.fireEvent("postLoad",[this]);
    },
    show: function(node, keyword){
        this.status = "display";
        this.node.setStyle("display","");
        this.setContent(node, keyword);
        this.fireEvent("show",[this]);
    },
    create: function(node, keyword){
        this.status = "display";
        this.fireEvent("queryCreate",[this]);
        this.node = new Element("div.tooltipNode", {
            styles : this.options.nodeStyles
        }).inject( this.container );

        this.node.addEvents( {
            'mousedown': function(e) { e.stopPropagation(); },
            'mousewheel': function(e) { e.stopPropagation(); },
            'DOMMouseScroll': function(e) { e.stopPropagation(); }
        });

        this.contentNode = new Element("div",{
            styles : {
                width : "100%",
                "height" : "100%"
            }
        }).inject( this.node );

        if( this.options.hasArrow ){
            this.arrowNode = new Element("div.arrowNode", {
                    "styles":  {
                        "width": this.options.axis == "x" ? "9px" : "17px",
                        "height" : this.options.axis == "x" ? "17px" : "9px",
                        "position":"absolute",
                        "background" : "no-repeat url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAAlCAYAAACgc9J8AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAP9JREFUeNq01oENhCAMBdCWuIgTuP8WOoGj9GhSDaeUfjiuCSEm+iKFVllESGNdVzrPk55xHIfewHnItm1MrVDMG/u+i0aeyWZp3R9CJRaBIWRB5YUHItAL80AEqmI1EIFcrAQ1rwjUxEoQgULsAue+2ayc/W83p5+z6RXggOO1WQGhrsFXP/Oip58tAaQTZ4QMbEbyEB2GCKEBdtpmru4NsC4aHg8PLJ/3lim2xDv02jYDz1kNQsGEQgiYeqAQBPuZQF1jFKqBYTn1RLK1D4+v3E3NGfgNkJlfdKi0XrUZgc1OWyt0Dwz/z37tGhA20s+WR4t+1iBbzTJyaD8CDAB7WgNSzh/AnwAAAABJRU5ErkJggg==)"
                    }
                }
            ).inject(this.node);
        }

        this.setContent(node, keyword);

        this.fireEvent("postCreate",[this]);
    },
    setContent : function( node, keyword ){
        this.editor.loadCodeMirror(
            function(){
                //var icon = node.getRenderer('NoteIconRenderer').getRenderShape();
                var b = node.getRenderBox('screen');
                var t = this.editor.Content_Offset_Top;
                b.bottom += t;
                b.cy += t;
                b.top += t;
                b.y += t;
                this.targetCoordinates = b;
                var note = node.getData('note');

                //$previewer[0].scrollTop = 0;

                var html = marked(note);
                if (keyword) {
                    html = html.replace(new RegExp('(' + keyword + ')', 'ig'), '<span class="highlight">$1</span>');
                }
                this.contentNode.set("html", html);

                this.setCoondinates();
            }.bind(this)
        )

    }
});

MWF.xApplication.MinderEditor.Help = new Class({
    Implements: [Options, Events],
    Extends: MTooltips,
    options: {
        style : "default",
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "auto", //x 轴上left center right, auto 系统自动计算
            y : "auto" //y轴上top middle bottom,  auto 系统自动计算
        },
        overflow : "scroll",
        hasCloseAction : true,
        event : "click" //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        //nodeStyles : {
        //    "font-size" : "12px",
        //    "position" : "absolute",
        //    "max-width" : "500px",
        //    "min-width" : "50px",
        //    "z-index" : "11",
        //    "background-color" : "#fff",
        //    "padding" : "10px",
        //    "border-radius" : "5px",
        //    "word-break" : "break-all",
        //    "box-shadow": "0 0 8px 0 #999999",
        //    "-webkit-user-select": "text",
        //    "-moz-user-select": "text"
        //}
    },
    _customNode : function( node, contentNode ){
        var table = new Element("table").inject(contentNode);
        var commands = this.commands.commands;
        for( var name in commands ){
            var command = commands[name];
            if( command.key ){
                var tr = new Element("tr").inject( table );
                new Element("td",{ text : command.locale || command.title }).inject( tr );
                new Element("td",{ text : command.key }).inject( tr );
            }
        }
    }
});

MWF.xApplication.MinderEditor.Preview = new Class({
    options : {
        "show" : "true"
    },
    Implements: [Options, Events],
    initialize: function (container, minder, app, css) {
        this.container = container;
        this.app = app;
        this.lp = MWF.xApplication.MinderEditor.LP;
        this.css = css;
        this.minder = minder;
    },
    load: function (callback) {
        this.previewer = new Element("div",{ "styles" : this.css.previewer  }).inject(this.container);
        this.initPreViewer();
        if( this.options.show ){
            this.toggleOpen( true );
        }
    },
    initPreViewer: function(){
        // 画布，渲染缩略图
        var paper = this.paper = new kity.Paper( this.previewer );

        // 用两个路径来挥之节点和连线的缩略图
        this.nodeThumb = paper.put(new kity.Path());
        this.connectionThumb = paper.put(new kity.Path());

        // 表示可视区域的矩形
        this.visibleRect = paper.put(new kity.Rect(100, 100).stroke('red', '1%'));

        this.contentView = new kity.Box();
        this.visibleView = new kity.Box();

        /**
         * 增加一个对天盘图情况缩略图的处理,
         * @Editor: Naixor line 104~129
         * @Date: 2015.11.3
         */
        this.pathHandler = this.getPathHandler(this.minder.getTheme());
        this.navigate();
    },
    getPathHandler: function (theme) {
        switch (theme) {
            case "tianpan":
            case "tianpan-compact":
                return function(nodePathData, x, y, width, height) {
                    var r = width >> 1;
                    nodePathData.push('M', x, y + r,
                        'a', r, r, 0, 1, 1, 0, 0.01,
                        'z');
                };
            default: {
                return function(nodePathData, x, y, width, height) {
                    nodePathData.push('M', x, y,
                        'h', width, 'v', height,
                        'h', -width, 'z');
                }
            }
        }
    },
    toggleOpen : function( open ) {
        if (open) {
            this.previewer.setStyle("display","block");
            this.bindPreviewerEvent();
            this.updateContentView();
            this.updateVisibleView();
        } else{
            this.previewer.setStyle("display","none");
            this.unbindPreviewerEvent();
        }
    },
    bindPreviewerEvent : function(){
        this.updateContentViewFun = this.updateContentViewFun || this.updateContentView.bind(this);
        this.updateVisibleViewFun = this.updateVisibleViewFun || this.updateVisibleView.bind(this);
        this.minder.on('layout layoutallfinish', this.updateContentViewFun );
        this.minder.on('viewchange', this.updateVisibleViewFun );
    },
    unbindPreviewerEvent : function(){
        this.minder.off('layout layoutallfinish', this.updateContentViewFun );
        this.minder.off('viewchange', this.updateVisibleViewFun );
    },
    moveView: function(center, duration) {
        var box = this.visibleView;
        center.x = -center.x;
        center.y = -center.y;

        var viewMatrix = this.minder.getPaper().getViewPortMatrix();
        box = viewMatrix.transformBox(box);

        var targetPosition = center.offset(box.width / 2, box.height / 2);

        this.minder.getViewDragger().moveTo(targetPosition, duration);
    },
    navigate: function() {
        var _self = this;
        this.dragging = false;

        this.paper.on('mousedown', function(e) {
            _self.dragging = true;
            _self.moveView(e.getPosition('top'), 200);
            _self.previewer.setStyles( _self.css.previewerGrab );
        });

        this.paper.on('mousemove', function(e) {
            if (_self.dragging) {
                _self.moveView(e.getPosition('top'));
            }
        });

        this.paper.on('mouseup', function() {
            _self.dragging = false;
            if(_self.previewer)_self.previewer.setStyles( _self.css.previewerNoGrab );
        });

        this.app.contentNode.addEvent('mouseup', function() {
            _self.dragging = false;
            if(_self.previewer)_self.previewer.setStyles( _self.css.previewerNoGrab );
        });
    },
    updateContentView: function(){
        var view = this.minder.getRenderContainer().getBoundaryBox();
        this.contentView = view;
        var padding = 30;
        this.paper.setViewBox(
            view.x - padding - 0.5,
            view.y - padding - 0.5,
            view.width + padding * 2 + 1,
            view.height + padding * 2 + 1);

        var nodePathData = [];
        var connectionThumbData = [];
        this.minder.getRoot().traverse(function(node) {
            var box = node.getLayoutBox();
            this.pathHandler(nodePathData, box.x, box.y, box.width, box.height);
            if (node.getConnection() && node.parent && node.parent.isExpanded()) {
                connectionThumbData.push(node.getConnection().getPathData());
            }
        }.bind(this));
        this.paper.setStyle('background', this.minder.getStyle('background'));

        if (nodePathData.length) {
            this.nodeThumb
                .fill(this.minder.getStyle('root-background'))
                .setPathData(nodePathData);
        } else {
            this.nodeThumb.setPathData(null);
        }

        if (connectionThumbData.length) {
            this.connectionThumb
                .stroke(this.minder.getStyle('connect-color'), '0.5%')
                .setPathData(connectionThumbData);
        } else {
            this.connectionThumb.setPathData(null);
        }
        this.updateVisibleView();
    },
    updateVisibleView: function(){
        this.visibleView = this.minder.getViewDragger().getView();
        this.visibleRect.setBox(this.visibleView.intersect(this.contentView));
    }
});

MWF.xApplication.MinderEditor.SearchBar = new Class({
    Implements: [Options, Events],
    options : {},
    initialize: function ( container, minder, app, css, options) {
        this.setOptions(options);
        this.container = container;
        this.app = app;
        this.lp = MWF.xApplication.MinderEditor.LP;
        this.css = css;
        this.minder = minder;

    },
    enterSearch : function(){
        if( !this.nodeSequence ){
            this.load();
        }else{
            this.show();
            this.searchInput.focus();
        }
    },
    load: function (callback) {
        this.createSearchBar();

        this.nodeSequence = [];
        this.searchSequence = [];

        //this.minder.on('contentchange', this.makeNodeSequence.bind(this));
        //
        //this.makeNodeSequence();
    },
    show : function(){
        this.container.setStyle("display","block");
        this.node.setStyle("display","block");
        this.fireEvent("show");
    },
    hide: function(){
        this.container.setStyle("display","none");
        //this.minder.execCommand('camera');
        this.node.setStyle("display","none");
        this.fireEvent("hide");
    },
    createSearchBar : function(){
        this.node = new Element("div", { "styles" : this.css.searchBar }).inject(this.container);
        this.searchInput = new Element("input" , {
            "type" : "text",
            "styles" : this.css.searchInput,
            "value" : this.lp.searchText
        }).inject(this.node);
        this.searchInput.addEvents({
            "focus" : function( ev ){
                if( this.searchInput.get("value")==this.lp.searchText){
                    this.searchInput.set("value","")
                }
                this.makeNodeSequence();
            }.bind(this),
            "blur" : function( ev ){
                if( this.searchInput.get("value").trim()==""){
                    this.searchInput.set("value", this.lp.searchText);
                }
            }.bind(this),
            "keyup" : function(){
                this.doSearch(this.searchInput.get("value"),"next");
            }.bind(this)
        });
        //this.searchButton = new Element("div", {"styles" : this.css.searchButton, "text" : this.lp.search } ).inject(this.node);
        this.resultInforNode = new Element("div", {
            "styles" : this.css.resultInforNode,
            "text" : "0/0"
        } ).inject(this.node);

        this.prevButton = new Element("div", {"styles" : this.css.prevButton , "title" : this.lp.prev} ).inject(this.node);
        this.prevButton.addEvent("click",function(){
            this.goPrev();
        }.bind(this));

        this.nextButton = new Element("div", {"styles" : this.css.nextButton  , "title" : this.lp.next } ).inject(this.node);
        this.nextButton.addEvent("click",function(){
            this.goNext();
        }.bind(this));

        this.closeButton = new Element("div", {"styles" : this.css.closeButton , "title" : this.lp.close } ).inject(this.node);
        this.closeButton.addEvent("click",function(){
            this.close();
        }.bind(this))
    },
    goNext : function(){
        this.doSearch( this.searchInput.get("value"),"next" );
    },
    goPrev : function(){
        this.doSearch( this.searchInput.get("value"),"prev" );
    },
    close : function(){
        this.hide();
    },
    makeNodeSequence: function() {
        //console.log( "makeNodeSequence" );
        this.nodeSequence = [];
        this.minder.getRoot().traverse(function(node) {
            this.nodeSequence.push(node);
        }.bind(this));
    },
    makeSearchSequence: function(keyword) {
        this.searchSequence = [];

        for (var i = 0; i < this.nodeSequence.length; i++) {
            var node = this.nodeSequence[i];
            var text = (node.getText() || "").toLowerCase();
            if (text.indexOf(keyword) != -1) {
                this.searchSequence.push({node:node});
            }
            var note = node.getData('note') || "";
            if (note && note.toLowerCase().indexOf(keyword) != -1) {
                this.searchSequence.push({node: node, keyword: keyword});
            }
        }
    },
    doSearch : function(keyword, direction) {
        this.minder.fire('hidenoterequest', { forceflag : true } );

        if (!keyword || !/\S/.exec(keyword)) {
            this.searchInput.focus();
            this.resultInforNode.set("text", 0+"/"+0);
            return;
        }

        // 当搜索不到节点时候默认的选项
        //this.curIndex = 0;
        this.resultNum = 0;


        keyword = keyword.toLowerCase();
        var newSearch = this.lastKeyword != keyword;

        this.lastKeyword = keyword;

        if (newSearch) {
            this.makeSearchSequence(keyword);
        }else if( !this.curIndex ){
            this.makeSearchSequence(keyword);
        }

        this.resultNum = this.searchSequence.length;

        if (this.searchSequence.length) {

            var curIndex = newSearch ? 0 : (direction === 'next' ? this.lastIndex + 1 : this.lastIndex - 1) || 0;
            curIndex = (this.searchSequence.length + curIndex) % this.searchSequence.length;

            if( curIndex == 0 && !newSearch ){
                //this.makeNodeSequence();
                this.makeSearchSequence(keyword);
            }

            this.setSearchResult(this.searchSequence[curIndex].node, this.searchSequence[curIndex].keyword);

            this.lastIndex = curIndex;

            this.curIndex = curIndex + 1;

            this.resultInforNode.set("text", this.curIndex+"/"+this.searchSequence.length);
        }else{
            this.resultInforNode.set("text", 0+"/"+0);
        }
    },
    setSearchResult : function(node, previewKeyword) {
        setTimeout(function () {
            if (previewKeyword) {
                this.minder.fire('shownoterequest', {node: node, keyword: previewKeyword});
            }
            if (!node.isExpanded()){
                this.minder.select(node, true);
                this.minder.execCommand('expand', true);
            }else{
                this.minder.select(node, true);
                this.minder.execCommand('camera', node, 50);
            }
        }.bind(this), 60);
    }
});

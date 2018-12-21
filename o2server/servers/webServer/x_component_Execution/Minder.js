//展现脑图，数据样式如下
//var data = {
//    "root": {
//        "data": {"id": "9f92035021ac",  "text": "软装修"},
//        "children": [{
//            "data": {"id": "b45yogtullsg", "created": 1463069010918, "text": "包阳台"},
//            "children": [{
//                "data": {"id": "3jl3i3j43", "created": 1463069010923, "text": "凤铝"}
//            }, {
//                "data": {"id": "3jl3i3j44", "created": 1463069010923, "text": "断桥"}
//            }]
//        },
//            {
//                "data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "衣柜"}, "children": [
//                {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "主卧"}},
//                {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "次卧"}}
//            ]
//            },
//            {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "床"}, "children": []},
//            {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "餐桌"}, "children": []},
//            {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "灯具"}, "children": []},
//            {"data": {"id": "b45yohdlynco", "created": 1463069012113, "text": "窗帘"}, "children": []}
//        ]
//    }
//};
MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xApplication.Execution.Minder = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "template" : "default", //fish-bone
        "theme": "fresh-blue", //"fresh-blue-compat"
        "hasNavi" : true,
        "align" : "center"
    },
    initialize: function (container, app, data, options) {
        this.setOptions(options);
        this.container = container;

        this.app = app;
        this.lp = this.app.lp.minder;
        this.actions = this.app.restActions;

        this.path = "/x_component_Execution/$Minder/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.data = data;
    },
    load: function () {
        this.loadResource( function(){
            this.loadKityMinder( this.data );
        }.bind(this) );
        this.attachEvent();
    },
    reload : function( data ){
        this.container.empty();
        this.loadKityMinder( data || this.data )
    },
    refresh : function(){
        //this.alreadyBind = false;
        //this.km.execCommand('camera');
        this.moveToCenter();
    },
    destroy: function(){
        if(this.navi){
            this.navi.destroy();
            delete this.navi;
        }
        if(this.km)delete this.km;
        delete this;
    },
    loadResource: function (callback) {
        var kityminderPath = "/x_desktop/res/framework/kityminder/";

        COMMON.AjaxModule.loadCss(kityminderPath + "core/src/kityminder.css", function () {
            COMMON.AjaxModule.load("kity", function () {
                COMMON.AjaxModule.load("kityminder", function () {
                    if (callback)callback();
                }.bind(this));
            }.bind(this))
        }.bind(this))
    },
    loadKityMinder: function ( data ) {
        var _self = this;
        this.isMovingCenter = true;
        // 创建 km 实例
        /* global kityminder */
        var km = this.km = new kityminder.Minder();
        //var target = document.querySelector('#minder-view');
        km.renderTo(this.container);

        data.theme = data.theme || this.options.theme;
        data.template = data.template || this.options.template;

        this.deepestLevel = 0;
        km.on("import", function (e) {
            if ( !_self.alreadyBind ) {
                var nodes = km.getAllNode();
                nodes.forEach(function (node) {
                    var level = node.getLevel();
                    _self.deepestLevel = level > _self.deepestLevel ? level : _self.deepestLevel;

                    _self.fireEvent( "postLoadNode", node );
                });
                _self.alreadyBind = true;
                if( _self.options.hasNavi )_self.loadNavi();
                _self.fireEvent( "postLoad" , _self );
            }
        });
        //km.on("execCommand", function(e){
        //    if (e.commandName === "template"  ) {
        //        _self.moveToCenter();
        //    }
        //})
        km.on("layoutallfinish",function(){
            if( _self.templateChanged || _self.isMovingCenter ){
                _self.moveToCenter();
                _self.templateChanged = false;
                _self.isMovingCenter = false;
            }
        });
        km.importJson(data);
        km.execCommand('hand');
    },
    loadNavi : function( container ) {
        this.navi = new MWF.xApplication.Execution.Minder.Navigation(container || this.container, this, this.km, this.app, this.css);
        this.navi.load();
    },
    moveToCenter: function(){
      //setTimeout( this._moveToCenter.bind(this) , 100 );
        this._moveToCenter();
    },
    _moveToCenter: function(){
        if( this.options.align != "center")return;
        //图形居中
        var minderView = this.km.getRenderContainer().getRenderBox('screen'); //.getBoundaryBox();
        var containerView = this.container.getCoordinates();

        var root = this.km.getRoot();
        var rootView = root.getRenderContainer().getRenderBox('screen'); //getRenderBox('top');
        var rootClientTop = rootView.top - minderView.top;
        var rootClientLeft = rootView.left - minderView.left;
        var rootChildrenLength = root.getChildren().length;

        var template = this.km.queryCommandValue("template");

        var left, top, isCamera = false;
        if( minderView.width > containerView.width ){  //如果图形宽度大于容器宽度
            if( template == "fish-bone" || rootChildrenLength < 2 ){
                left = 50;
            }else{
                isCamera = true;
            }
        }else{
          left = parseInt( ( containerView.width - minderView.width ) / 2 + rootClientLeft + 50 );
        }
        if( minderView.height > containerView.height ){  //如果图形高度大于容器高度
            if( rootClientTop > containerView.height ){
                if( template == "fish-bone" ) {
                    top = containerView.height - rootView.height
                }else if( rootChildrenLength < 2){
                    top = parseInt( containerView.width / 2 );
                }else{
                    isCamera = true;
                }
            }else{
                top = rootClientTop + 50;
            }
        }else{
            top = parseInt( ( containerView.height - minderView.height ) / 2 ) + rootClientTop;
        }
        if( isCamera ){
            this.km.execCommand('camera', this.km.getRoot(), 600);
        }else{
            var dragger = this.km.getViewDragger();
            dragger.moveTo(new kity.Point(left, top) , 300 );
        }
    },
    attachEvent: function(){
        this.container.addEvent("mousewheel", function(ev){ //鼠标滚轮事件
            if( ev.wheel == 1){ //向上滚动，放大
                this.km.execCommand('zoomIn');
                if( this.navi && this.navi.navZoompanIndicator ){
                    var marginTop = parseInt( this.navi.navZoompanIndicator.getStyle("margin-top"));
                    if( marginTop > 0 ){
                        this.navi.navZoompanIndicator.setStyle( "margin-top", (marginTop - 10)  + "px" );
                    }
                }
            }else{  //向下滚动，缩小
                this.km.execCommand('zoomOut');
                if( this.navi && this.navi.navZoompanIndicator ){
                    var totalHeight = parseInt( this.navi.navZoompan.getStyle("height") );
                    var marginTop = parseInt( this.navi.navZoompanIndicator.getStyle("margin-top"));
                    if( marginTop < totalHeight ){
                        this.navi.navZoompanIndicator.setStyle( "margin-top", (marginTop + 10 ) + "px" );
                    }
                }
            }
        }.bind(this))
    }
});

MWF.xApplication.Execution.Minder.Navigation = new Class({
    Implements: [Options, Events],
    initialize: function (container, minder, km, app, css) {
        this.container = container;
        this.app = app;
        this.lp = this.app.lp.minder;
        this.actions = this.app.restActions;
        this.css = css;
        this.minder = minder;
        this.km = km;
    },
    load: function (callback) {
        this.createNavigation();
        //this.navMoveNode.click();
    },
    destroy: function(){
      this.node.destroy();
        delete  this;
    },
    createNavigation: function(){
        this.node = new Element("div",{ "styles" : this.css.nav  }).inject(this.container);

        this.navZoomIn = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.navZoomin }).inject(this.node);
        new Element("div",{ "styles" : this.css.navZoominIcon  }).inject(this.navZoomIn);
        this.navZoomIn.addEvent("click",function(){
            this.km.execCommand('zoomIn');
            var marginTop = parseInt( this.navZoompanIndicator.getStyle("margin-top"));
            if( marginTop > 0 ){
                this.navZoompanIndicator.setStyle( "margin-top", (marginTop - 10)  + "px" );
            }
        }.bind(this));

        this.navZoompan = new Element("div",{ "styles" : this.css.navZoompan  }).inject(this.node);
        this.navZoompanOrigin = new Element("div",{ "styles" : this.css.navZoompanOrigin  }).inject(this.navZoompan);
        this.navZoompanOrigin.addEvent("click",function(){
            this.km.execCommand('zoom', 100);
            this.navZoompanIndicator.setStyle( "margin-top", "30px" );
        }.bind(this));
        this.navZoompanIndicator = new Element("div",{ "styles" : this.css.navZoompanIndicator  }).inject(this.navZoompan);

        this.navZoomout = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.navZoomout }).inject(this.node);
        new Element("div",{ "styles" : this.css.navZoomoutIcon  }).inject(this.navZoomout);
        this.navZoomout.addEvent("click",function(){
            this.km.execCommand('zoomOut');
            var totalHeight = parseInt( this.navZoompan.getStyle("height") );
            var marginTop = parseInt( this.navZoompanIndicator.getStyle("margin-top"));
            if( marginTop < totalHeight ){
                this.navZoompanIndicator.setStyle( "margin-top", (marginTop + 10 ) + "px" );
            }
        }.bind(this));

//
        //this.navCreateMainWork = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.workTask.newTask  }).inject(this.node);
        //new Element("div",{ "styles" : this.css.navCreateMainWorkIcon  }).inject(this.navCreateMainWork);
        //this.navCreateMainWork.addEvent("click",function(){
        //
        //    MWF.xDesktop.requireApp("Execution", "WorkDeploy", function(){
        //        this.explorer = new MWF.xApplication.Execution.WorkDeploy(this, this.actions,{},{"isEdited":true} );
        //        this.explorer.load();
        //    }.bind(this))
        //
        //}.bind(this))



        this.navCameraNode = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.navCamera  }).inject(this.node);
        new Element("div",{ "styles" : this.css.navCameraIcon  }).inject(this.navCameraNode);
        this.navCameraNode.addEvent("click",function(){
            this.km.execCommand('camera', this.km.getRoot(), 600);
            //this.minder.moveToCenter()
        }.bind(this));

        this.navExpandNode = new Element("div",{ "styles" : this.css.navButton , "title" : "展开节点"  }).inject(this.node);
        new Element("div",{ "styles" : this.css.navExpandIcon  }).inject(this.navExpandNode);
        this.navExpandNode.addEvent("click",function(ev){
            this.showExpendNode( ev );
            ev.stopPropagation();
        }.bind(this));

        this.navTrigger = new Element("div",{ "styles" : this.css.navButton  , "title" : this.lp.navTrigger  }).inject(this.node);
        new Element("div",{ "styles" : this.css.navTriggerIcon  }).inject(this.navTrigger);
        this.navTrigger.addEvent( "click", function(){
            this.toggleOpenPreViewer();
        }.bind(this) );
        this.createrPreViewer();

        this.navMoveNode = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.allowDrag  }).inject(this.node);
        this.navMoveNode.setStyles( this.css.navButton_over );
        this.moveOpen = true;
        new Element("div",{ "styles" : this.css.navMoveIcon  }).inject(this.navMoveNode);
        this.navMoveNode.addEvent("click",function(){
            this.moveOpen = !this.moveOpen;
            if( this.moveOpen ){
                this.navMoveNode.setStyles( this.css.navButton_over );
            }else{
                this.navMoveNode.setStyles( this.css.navButton );
            }
            this.km.execCommand('hand');
        }.bind(this));

        this.navTemplateNode = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.changeTemplate  }).inject(this.node);
        new Element("div",{ "styles" : this.css.navTemplateIcon  }).inject(this.navTemplateNode);
        this.navTemplateNode.addEvent("click",function( ev ){
            this.selectTemplate( ev );
            ev.stopPropagation();
        }.bind(this));


        this.navSearchNode = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.search  }).inject(this.node);
        new Element("div",{ "styles" : this.css.navSearchIcon  }).inject(this.navSearchNode);
        this.navSearchNode.addEvent("click",function(){
            if( !this.isShowedSearch ){
                this.showSearch();
            }else{
                this.hideSearch();
            }
        }.bind(this));


        //this.navExpand = new Element("div",{ "styles" : this.css.navButton , "title" : "展开"  }).inject(this.node);
        //new Element("div",{ "styles" : this.css.navExpandIcon  }).inject(this.navExpand);





    },
    toggleOpenPreViewer: function(){
        this.previewOpened = !this.previewOpened;
        if( this.previewOpened ){
            this.navTrigger.setStyles( this.css.navButton_over );
        }else{
            this.navTrigger.setStyles( this.css.navButton );
        }
        this.preview.toggleOpen( this.previewOpened );
    },
    createrPreViewer: function(){
        this.preview = new MWF.xApplication.Execution.Minder.Preview( this, this.node, this.km, this.app, this.css );
        this.preview.load();
    },
    showSearch: function(){
        this.isShowedSearch = true;
        if( !this.searchBar ){
            this.searchBar = new MWF.xApplication.Execution.Minder.SearchBar( this, this.node, this.km, this.app, this.css );
            this.searchBar.load();
        }else{
            this.searchBar.show()
        }
        this.navSearchNode.setStyles( this.css.navButton_over );
    },
    hideSearch: function(){
        this.isShowedSearch = false;
        this.searchBar.hide();
        this.navSearchNode.setStyles( this.css.navButton );
    },
    selectTemplate: function(){
        this.templateOpen = !this.templateOpen;
        if( this.templateOpen ){
            if( this.templateSelectNode ){
                this.templateSelectNode.setStyle("display","block")
            }else{
                this.createTemplateSelectNode();
            }
            this.navTemplateNode.setStyles( this.css.navButton_over );
        }else{
            this.hideTemplateSelectNode();
        }
    },
    hideTemplateSelectNode: function(){
        this.templateOpen = false;
        this.navTemplateNode.setStyles( this.css.navButton );
        if( this.templateSelectNode ){
            this.templateSelectNode.setStyle("display","none");
        }
    },
    createTemplateSelectNode: function(){
        this.templateSelectNode = new Element("div",{
            styles : this.css.templateSelectNode
        }).inject(this.node);

        this.minderTemplate = new Element("div",{
            styles : this.css.minderTemplate,
            title : this.lp.minderTemplate
        }).inject(this.templateSelectNode);
        this.minderTemplate.addEvents({
            "mouseover" : function(){ this.minderTemplate.setStyles( this.css.minderTemplate_over )}.bind(this),
            "mouseout" : function(){ this.minderTemplate.setStyles( this.css.minderTemplate )}.bind(this),
            "click" : function(){
                this.minder.templateChanged = true;
                this.km.execCommand('template', "default");
                this.hideTemplateSelectNode();
                //this.minder.moveToCenter();
            }.bind(this)
        });

        this.fishboneTemplate = new Element("div",{
            styles : this.css.fishboneTemplate,
            title : this.lp.fishBoneTemplate
        }).inject(this.templateSelectNode);
        this.fishboneTemplate.addEvents({
            "mouseover" : function(){ this.fishboneTemplate.setStyles( this.css.fishboneTemplate_over )}.bind(this),
            "mouseout" : function(){ this.fishboneTemplate.setStyles( this.css.fishboneTemplate )}.bind(this),
            "click" : function(){
                //this.km.on("execCommand", function (e) {
                //    if (e.commandName === "template"  ) {
                //        this.minder.moveToCenter();
                //    }
                //}.bind(this));
                this.minder.templateChanged = true;
                this.km.execCommand('template', "fish-bone");
                this.hideTemplateSelectNode();
                //this.minder.moveToCenter();
            }.bind(this)
        });

        this.app.content.addEvent("click",function(){
            this.hideTemplateSelectNode();
        }.bind(this))
    },
    showExpendNode: function(){
        this.expendNodeOpen = !this.expendNodeOpen;
        if( this.expendNodeOpen ){
            if( this.expendArea ){
                this.expendArea.setStyle("display","block")
            }else{
                this.createExpandArea();
            }
            this.navExpandNode.setStyles( this.css.navButton_over );
        }else{
            this.hideExpendArea();
        }
    },
    hideExpendArea: function(){
        this.expendNodeOpen = false;
        this.navExpandNode.setStyles( this.css.navButton );
        if( this.expendArea ){
            this.expendArea.setStyle("display","none");
        }
    },
    createExpandArea: function(){
        var deepestLevel = this.minder.deepestLevel;

        this.expendArea = new Element("div",{
            styles : this.css.expendArea
        }).inject(this.node);
        this.expendArea.setStyle("height",this.css.expendNode.height * (deepestLevel+1));

        var expendAllNode = new Element("div",{
            styles : this.css.expendNode,
            text : "展开所有节点"
        }).inject(this.expendArea);
        expendAllNode.addEvents({
            "mouseover" : function(){ this.node.setStyles( this.navi.css.expendNode_over )}.bind( { navi : this, node : expendAllNode} ),
            "mouseout" : function(){ this.node.setStyles( this.navi.css.expendNode )}.bind({ navi : this, node : expendAllNode}),
            "click" : function(){
                this.navi.km.execCommand('expandtolevel', deepestLevel);
                this.navi.hideExpendArea();
            }.bind({ navi : this, node : expendAllNode})
        });

        for( var i=1; i<=deepestLevel; i++ ){
            var expendNode = new Element("div",{
                styles : this.css.expendNode,
                text : "展开到"+i+"级节点"
            }).inject(this.expendArea);
            expendNode.addEvents({
                "mouseover" : function(){ this.node.setStyles( this.navi.css.expendNode_over )}.bind( { navi : this, node : expendNode} ),
                "mouseout" : function(){ this.node.setStyles( this.navi.css.expendNode )}.bind({ navi : this, node : expendNode}),
                "click" : function(){
                    this.navi.km.execCommand('expandtolevel', this.level);
                    this.navi.hideExpendArea();
                }.bind({ navi : this, node : expendNode, level : i})
            })
        }

        this.app.content.addEvent("click",function(){
            this.hideExpendArea();
        }.bind(this))
    }
});

MWF.xApplication.Execution.Minder.Preview = new Class({
    options : {
      "show" : "true"
    },
    Implements: [Options, Events],
    initialize: function (navi, container, km, app, css) {
        this.navi = navi;
        this.container = container;
        this.app = app;
        this.lp = this.app.lp.minder;
        this.actions = this.app.restActions;
        this.css = css;
        this.km = km;
    },
    load: function (callback) {
        this.navPreviewer = new Element("div",{ "styles" : this.css.navPreviewer  }).inject(this.container);
        this.initPreViewer();
        if( this.options.show )this.navi.toggleOpenPreViewer();
    },
    initPreViewer: function(){
        // 画布，渲染缩略图
        var paper = this.paper = new kity.Paper( this.navPreviewer );

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
        this.pathHandler = this.getPathHandler(this.km.getTheme());
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
            this.navPreviewer.setStyle("display","block");
            this.bindPreviewerEvent();
            this.updateContentView();
            this.updateVisibleView();
        } else{
            this.navPreviewer.setStyle("display","none");
            this.unbindPreviewerEvent();
        }
    },
    bindPreviewerEvent : function(){
        this.updateContentViewFun = this.updateContentViewFun || this.updateContentView.bind(this);
        this.updateVisibleViewFun = this.updateVisibleViewFun || this.updateVisibleView.bind(this);
        this.km.on('layout layoutallfinish', this.updateContentViewFun );
        this.km.on('viewchange', this.updateVisibleViewFun );
    },
    unbindPreviewerEvent : function(){
        this.km.off('layout layoutallfinish', this.updateContentViewFun );
        this.km.off('viewchange', this.updateVisibleViewFun );
    },
    moveView: function(center, duration) {
        var box = this.visibleView;
        center.x = -center.x;
        center.y = -center.y;

        var viewMatrix = this.km.getPaper().getViewPortMatrix();
        box = viewMatrix.transformBox(box);

        var targetPosition = center.offset(box.width / 2, box.height / 2);

        this.km.getViewDragger().moveTo(targetPosition, duration);
    },
    navigate: function() {
        var _self = this;
        this.dragging = false;

        this.paper.on('mousedown', function(e) {
            _self.dragging = true;
            _self.moveView(e.getPosition('top'), 200);
            _self.navPreviewer.setStyles( _self.css.navPreviewerGrab );
        });

        this.paper.on('mousemove', function(e) {
            if (_self.dragging) {
                _self.moveView(e.getPosition('top'));
            }
        });

        $(window).addEvent('mouseup', function() {
            _self.dragging = false;
            if(_self.navPreviewer)_self.navPreviewer.setStyles( _self.css.navPreviewerNoGrab );
        });
    },
    updateContentView: function(){
        var view = this.km.getRenderContainer().getBoundaryBox();
        this.contentView = view;
        var padding = 30;
        this.paper.setViewBox(
            view.x - padding - 0.5,
            view.y - padding - 0.5,
            view.width + padding * 2 + 1,
            view.height + padding * 2 + 1);

        var nodePathData = [];
        var connectionThumbData = [];
        this.km.getRoot().traverse(function(node) {
            var box = node.getLayoutBox();
            this.pathHandler(nodePathData, box.x, box.y, box.width, box.height);
            if (node.getConnection() && node.parent && node.parent.isExpanded()) {
                connectionThumbData.push(node.getConnection().getPathData());
            }
        }.bind(this));
        this.paper.setStyle('background', this.km.getStyle('background'));

        if (nodePathData.length) {
            this.nodeThumb
                .fill(this.km.getStyle('root-background'))
                .setPathData(nodePathData);
        } else {
            this.nodeThumb.setPathData(null);
        }

        if (connectionThumbData.length) {
            this.connectionThumb
                .stroke(this.km.getStyle('connect-color'), '0.5%')
                .setPathData(connectionThumbData);
        } else {
            this.connectionThumb.setPathData(null);
        }
        this.updateVisibleView();
    },
    updateVisibleView: function(){
        this.visibleView = this.km.getViewDragger().getView();
        this.visibleRect.setBox(this.visibleView.intersect(this.contentView));
    }
});

MWF.xApplication.Execution.Minder.SearchBar = new Class({
    Implements: [Options, Events],
    initialize: function (navi, container, km, app, css) {
        this.navi = navi;
        this.container = container;
        this.app = app;
        this.lp = this.app.lp.minder;
        this.actions = this.app.restActions;
        this.css = css;
        this.km = km;
    },
    load: function (callback) {
        this.createSearchBar();

        this.nodeSequence = [];
        this.searchSequence = [];

        this.km.on('contentchange', this.makeNodeSequence.bind(this));

        this.makeNodeSequence();
    },
    show : function(){
      this.node.setStyle("display","block")
    },
    hide: function(){
        //this.km.execCommand('camera');
        this.node.setStyle("display","none")
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
        this.navi.hideSearch();
    },
    makeNodeSequence: function() {
        this.nodeSequence = [];
        this.km.getRoot().traverse(function(node) {
            this.nodeSequence.push(node);
        }.bind(this));
    },
    makeSearchSequence: function(keyword) {
        this.searchSequence = [];

        for (var i = 0; i < this.nodeSequence.length; i++) {
            var node = this.nodeSequence[i];
            var text = node.getText().toLowerCase();
            if (text.indexOf(keyword) != -1) {
                this.searchSequence.push({node:node});
            }
            var note = node.getData('note');
            if (note && note.toLowerCase().indexOf(keyword) != -1) {
                this.searchSequence.push({node: node, keyword: keyword});
            }
        }
    },
    doSearch : function(keyword, direction) {
        this.km.fire('hidenoterequest');

        if (!keyword || !/\S/.exec(keyword)) {
            this.searchInput.focus();
            return;
        }

        // 当搜索不到节点时候默认的选项
        this.curIndex = 0;
        this.resultNum = 0;


        keyword = keyword.toLowerCase();
        var newSearch = this.lastKeyword != keyword;

        this.lastKeyword = keyword;

        if (newSearch) {
            this.makeSearchSequence(keyword);
        }

        this.resultNum = this.searchSequence.length;

        if (this.searchSequence.length) {

            var curIndex = newSearch ? 0 : (direction === 'next' ? this.lastIndex + 1 : this.lastIndex - 1) || 0;
            curIndex = (this.searchSequence.length + curIndex) % this.searchSequence.length;

            this.setSearchResult(this.searchSequence[curIndex].node, this.searchSequence[curIndex].keyword);

            this.lastIndex = curIndex;

            this.curIndex = curIndex + 1;

            this.resultInforNode.set("text", this.curIndex+"/"+this.searchSequence.length);
        }
    },
    setSearchResult : function(node, previewKeyword) {
        setTimeout(function () {
            if (previewKeyword) {
                this.km.fire('shownoterequest', {node: node, keyword: previewKeyword});
            }
            if (!node.isExpanded()){
                this.km.select(node, true);
                this.km.execCommand('expand', true);
            }else{
                this.km.select(node, true);
                this.km.execCommand('camera', node, 50);
            }
        }.bind(this), 60);
    }
});
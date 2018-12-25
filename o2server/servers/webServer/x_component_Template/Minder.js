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
MWF.xDesktop.requireApp("MinderEditor", "LeftToolbar", null, false);
MWF.xApplication.Template = MWF.xApplication.Template || {};
MWF.xApplication.Template.Minder = new Class({
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
        this.content = app.content;
        this.container = this.contentNode =  container;
        this.container.classList.add("km-editor");

        this.app = app;

        this.actions = this.app.restActions;

        this.data = data;
    },
    load: function () {
        this.loadResource( function(){
            this.loadKityMinder( this.data );
        }.bind(this) );
        //this.attachEvent();
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
        //delete this;
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
        var km = this.km = this.minder = new kityminder.Minder();
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
        this.navi = new MWF.xApplication.MinderEditor.LeftToolbar(container || this.container, this, this.km, this.app );
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
    }
    //attachEvent: function(){
    //    this.container.addEvent("mousewheel", function(ev){ //鼠标滚轮事件
    //        if( ev.wheel == 1){ //向上滚动，放大
    //            this.km.execCommand('zoomIn');
    //            if( this.navi && this.navi.navZoompanIndicator ){
    //                var marginTop = parseInt( this.navi.navZoompanIndicator.getStyle("margin-top"));
    //                if( marginTop > 0 ){
    //                    this.navi.navZoompanIndicator.setStyle( "margin-top", (marginTop - 10)  + "px" );
    //                }
    //            }
    //        }else{  //向下滚动，缩小
    //            this.km.execCommand('zoomOut')
    //            if( this.navi && this.navi.navZoompanIndicator ){
    //                var totalHeight = parseInt( this.navi.navZoompan.getStyle("height") );
    //                var marginTop = parseInt( this.navi.navZoompanIndicator.getStyle("margin-top"));
    //                if( marginTop < totalHeight ){
    //                    this.navi.navZoompanIndicator.setStyle( "margin-top", (marginTop + 10 ) + "px" );
    //                }
    //            }
    //        }
    //    }.bind(this))
    //}
});

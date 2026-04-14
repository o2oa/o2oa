
MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.Minder = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "template" : "default", //fish-bone
        "theme": "fresh-blue", //"fresh-blue-compat"
        "hasNavi" : true,
        "align" : "center"

    },
    initialize: function (content, app, data, options) {
        this.setOptions(options);
        this.content = content;

        this.app = app;
        this.lp = this.app.lp.minder;
        this.rootActions = this.app.rootActions;

        this.path = "../x_component_TeamWork/$Minder/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.data = data;
        
        this.projectId = data.id;
        this.projectTitle = data.title;
        // this.data1 = {
        //     "root":{
        //         "data":{
        //             "id":"root",
        //             "text":"服务器服务费服务器服务费服务器服务费"
        //         },
        //         "children":[
        //             {
        //                 "data": {
        //                     "id": "ccxu85jy16o111110",
        //                     //"created": 1626771098258,
        //                     "text": "呵呵呵呵呵呵呵呵咯咯咯二咯咯咯",
        //                     "color":"#ff0000"
        //                     //'font-size', 'font-family', 'font-weight', 'font-style', 'background', 'color'
        //                 },
        //                 "children": [
        //                     {
        //                         "data": {
        //                             "id": "ccxu85jy16o0",
        //                             //"created": 1626771098258,
        //                             "text": "每一天要么明天明天明天明天明天明天明天"
        //                         },
        //                         "children": []
        //                     },
        //                     {
        //                         "data": {
        //                             "id": "ccxu85jy16o0",
        //                             //"created": 1626771098258,
        //                             "text": "微软微软微软微软微软微软微软微软微软为"
        //                         },
        //                         "children": []
        //                     }
        //                 ]
        //             }
        //           
        //         ]
        //     },
        //     "template": "default",
        //     "theme": "fresh-blue",
        //     "version": "1.4.33"
        // }

        //this.transformData(this.data);
        
    },
    close:function(){
        this.maskDiv.destroy();
        if(this.container)this.container.destroy();
        this.fireEvent("close");
        delete this;
    },
    open:function(){
        this.maskDiv = new Element("div",{styles:this.css.maskDiv,title:this.lp.clickClose}).inject(this.content);
        this.maskDiv.addEvents({
            "click":function(){
                this.close()
            }.bind(this)
        })
        this.getData(function(){
            if(this.maskDiv) {
                this.container = new Element("div.bam_mind_container",{styles:this.css.container}).inject(this.content);
                this.computeSize();
                this.load()
            }
        }.bind(this));
        
    },
    load:function(){
        this.container.empty();
        this.loadResource( function(){
            this.loadKityMinder( this.minderData );
        }.bind(this) );
        this.attachEvent();
        
    },
    loadResource: function (callback) {
        var kityminderPath = "../o2_lib/kityminder/";

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
                    
                    _self.handleNode(node);
                    //_self.fireEvent( "postLoadNode", node );
                });
                _self.alreadyBind = true;
                _self.handleKM();
                //_self.fireEvent( "postLoad" , _self );
            }
        });
        //km.on("execCommand", function(e){
        //    if (e.commandName === "template"  ) {
        //        _self.moveToCenter();
        //    }
        //})
        // km.on("layoutallfinish",function(){
        //     if( _self.templateChanged || _self.isMovingCenter ){
        //         _self.moveToCenter();
        //         _self.templateChanged = false;
        //         _self.isMovingCenter = false;
        //     }
        // });
        
        km.importJson(data);
        km.execCommand('hand');
    },
    handleNode:function(minderNode){
        //import单个node完成
        var cNode = minderNode.getRenderContainer().node;
        
        //minderNode.textContent("xxxxxxxxxxx");
        cNode.addEventListener("dblclick", function ( ev ) {
            //alert(22)
        });
        
    },
    handleKM:function(){
        //import完成
        //alert(1)
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
            //console.log(ev.wheel);
            if( ev.wheel > 0){ //向上滚动，放大
                this.km.execCommand('zoomIn');
                //console.log("放大")
                // if( this.navi && this.navi.navZoompanIndicator ){
                //     var marginTop = parseInt( this.navi.navZoompanIndicator.getStyle("margin-top"));
                //     if( marginTop > 0 ){
                //         this.navi.navZoompanIndicator.setStyle( "margin-top", (marginTop - 10)  + "px" );
                //     }
                // }
            }else{  //向下滚动，缩小
                this.km.execCommand('zoomOut');
                //console.log("缩小")
                // if( this.navi && this.navi.navZoompanIndicator ){
                //     var totalHeight = parseInt( this.navi.navZoompan.getStyle("height") );
                //     var marginTop = parseInt( this.navi.navZoompanIndicator.getStyle("margin-top"));
                //     if( marginTop < totalHeight ){
                //         this.navi.navZoompanIndicator.setStyle( "margin-top", (marginTop + 10 ) + "px" );
                //     }
                // }
            }
            this.moveToCenter();
        }.bind(this))
    },
    computeSize:function(){
        var size = this.content.getSize();
        this.container.setStyles({
            "width":(size.x-200)+"px",
            "height":(size.y-200)+"px",
            "left":"100px",
            "top":"100px"
        })
    },
    getData:function(callback){
        this.minderData = {
            "root":{
                "data":{
                    "id":"root",
                    "text":this.projectTitle
                },
                "template": "default",
                "theme": "fresh-blue",
                "version": "1.4.33"
            } 
        };
        var promise = this.rootActions.TaskAction.listAllTaskWithProjectId(this.projectId);
        promise.then(function(json){ 
            if(json.data){
                this.minderData.root.children = json.data;
                this.transformData();
                if(callback)callback()
            }
        }.bind(this))

        //this.transformData();
    },
    transformData:function(){
        //this.minderData;

        this.dealData(this.minderData.root);
    },
    dealData:function(node){ 
        if(node.data){ 
            //node.data.title = node.data.text;
            if(node.data.completed){
                node.data.text = "["+this.lp.completed+"]"+node.data.text;
                node.data.color = "#73bf4d";
            }else if(node.data.deleted){
                node.data.text = "["+this.lp.deleted+"]"+node.data.text;
                node.data.color = "#A9A9A9";
            }else if(node.data.overtime){
                node.data.text = "["+this.lp.overtime+"]"+node.data.text;
                node.data.color = "#e6240e"
            }
        }
        
        var children = node.children;
        if(children && children.length > 0){
            children.each(function(child){ 
                if(child.data && child.data.priority){ 
                    delete child.data.priority
                } 
                this.dealData(child);
            }.bind(this))
        }
    }
    // getChildrenData:function(node){
    //     var result = [];
    //     if(node.children && node.children.length>0){
    //         result = node.children;
    //     }
    //     return result;
    // }

});
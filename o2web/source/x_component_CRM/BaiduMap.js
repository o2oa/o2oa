MWF.xApplication.CRM = MWF.xApplication.CRM || {};

MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("CRM", "Template", null,false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.CRM.BaiduMap = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, explorer, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.explorer = explorer;
        this.lp = app.lp.BaiduMap;
        this.path = "/x_component_CRM/$BaiduMap/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_CRM/$BaiduMap/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function (markerData) {
        //this.node.empty();
        this.markerData = markerData;
        this.mapNode = new Element("div.mapNode", {styles : {
            width : "100%",
            height : "99%",
            "id":"mapMaxNode"
        }}).inject(this.node);
        setTimeout( function(){
            this.loadResource(function(){
                if(this.options.from = "newCustomer"){
                    var _self = this;
                    this.mapLocation = this.explorer.formTableArea.getElement("#mapLocation");
                    this.mapLocation.set("disabled",false);
                    this.mapLocation.setStyles({"background-color":"#ffffff"});
                    this.mapLocation.addEvents({
                        "keyup": function () {
                            if(_self.explorer.adDiv)_self.explorer.adDiv.destroy();
                            _self.explorer.adDiv = new Element("div.adDiv",{
                                "styles":_self.explorer.css.adDiv
                            }).inject(this.getParent());
                            _self.explorer.adDiv.setStyles({
                                "width":this.getWidth()+"px"
                            });
                            var v = this.get("value");
                            if(v!=""){
                                var myJSONP = new Request.JSONP({
                                    url: 'http://map.baidu.com/su?wd='+v,
                                    data: {
                                        "cid":"131",
                                        "type":"0",
                                        "from":"jsapi"
                                    },
                                    onRequest: function(url){
                                        // a script tag is created with a src attribute equal to url
                                    },
                                    onComplete: function(data){
                                        _self.resolve(data)
                                    }
                                }).send();
                            }else{
                                _self.explorer.adDiv.destroy();
                                _self.explorer.data.lat = "";
                                _self.explorer.data.lng = ""
                            }

                        },
                        "blur":function(){
                            //if(_self.explorer.adDiv)_self.explorer.adDiv.hide();
                        }
                    })

                }
            }.bind(this));
        }.bind(this) , 100 )

    },
    loadMax: function (markerData) {
        this.markerData = markerData;

        this.maxMapDiv = new Element("div.maxMapDiv",{"styles":this.css.maxMapDiv}).inject(this.app.content);
        this.maxMapDiv.addEvents({
            "click":function(){
                this.maxMapDiv.destroy();
            }.bind(this)
        });
        this.mapHeadDiv = new Element("div.mapHeadDiv",{"styles":this.css.mapHeadDiv}).inject(this.maxMapDiv);

        this.maxCloseDiv = new Element("div.maxCloseDiv",{"styles":this.css.maxCloseDiv}).inject(this.mapHeadDiv);
        this.maxCloseDiv.addEvents({
            "mouseenter":function(){
                this.setStyles({
                    "opacity":"1",
                    "filter":"alpha(opacity=100)"
                })
            },
            "mouseleave":function(){
                this.maxCloseDiv.setStyles(this.css.maxCloseDiv);
            }.bind(this),
            "click":function(){
                //this.destroyForm();
                this.maxMapDiv.destroy();

            }.bind(this)
        });
        this.maxMapDiv.addEvents({
            "click":function(){
                this.maxMapDiv.destroy();
            }.bind(this)
        });

        this.mapContentDiv = new Element("div.mapContentDiv",{"styles":this.css.mapContentDiv}).inject(this.maxMapDiv);
        this.mapContentDiv.setStyles({
            "height":(this.app.content.getHeight()-this.mapHeadDiv.getHeight()-100)+"px",
            "width":(this.app.content.getWidth()-100)+"px"
        });
        this.mapContentDiv.addEvents({
            "click":function(e){
                e.stopPropagation();
            }
        });
        this.mapNode = new Element("div.mapNode", {styles : {
            width : "100%",
            height : "99%",
            "id":"mapMaxNode"
        }}).inject(this.mapContentDiv);
        setTimeout( function(){
            this.loadResource();
        }.bind(this) , 5 )



    },
    loadResource:function(callback){
        window.BMap_loadScriptTime = (new Date).getTime();
        //var apiPath = "http://api.map.baidu.com/api?v=2.0&ak=Qac4WmBvHXiC87z3HjtRrbotCE3sC9Zg";
        var apiPath = "http://api.map.baidu.com/getscript?v=2.0&ak=Qac4WmBvHXiC87z3HjtRrbotCE3sC9Zg&services=&t=20161219171637";
        if( !window.BDMapApiLoaded ){
            COMMON.AjaxModule.loadDom(apiPath, function () {
                window.BDMapApiLoaded = true;
                if( !window.BDMarkerToolLoaded ){
                    COMMON.AjaxModule.load( "/x_component_CRM/BDMarkerTool.js", function(){
                        window.BDMarkerToolLoaded = true;
                        this._loadMap();
                        if (callback)callback();
                    }.bind(this) );
                }else{
                    this._loadMap();
                    if (callback)callback();
                }
            }.bind(this));
        }else{
            this._loadMap();
            if (callback)callback();
        }
    },
    _loadMap: function(){
        if(this.markerData){
            this.loadMap();
        }else{
            if (navigator.geolocation){
                try{
                    navigator.geolocation.getCurrentPosition(this.loadMap.bind(this), this.loadMap.bind(this),{timeout:500});
                }catch( e ){
                    this.loadMap();
                }
            }else{
                this.loadMap();
            }
        }

    },
    loadMap: function(position){
        this.createMap( position );
    },
    createMap: function( position ) {
        var point = null;
        if (this.markerData.longitude && this.markerData.latitude) {
            point = new BMap.Point(this.markerData.longitude, this.markerData.latitude);
        } else {
            if (position && position.coords) {
                point = new BMap.Point(position.coords.longitude, position.coords.latitude);
            }
            if (!point) {
                point = new BMap.Point(116.404, 39.915);
            }
        }

        this.map = new BMap.Map(this.mapNode);    // 创建Map实例
        var marker = new BMap.Marker(point);  // 创建标注
        this.map.addOverlay(marker);              // 将标注添加到地图中

        this.map.centerAndZoom(point, 14);  // 初始化地图,设置中心点坐标和地图级别
        this.map.panTo(point);
//		map.centerAndZoom(new BMap.Point(116.404, 39.915), 11);  // 初始化地图,设置中心点坐标和地图级别
//		map.setCurrentCity("北京");          // 设置地图显示的城市 此项是必须设置的
        this.map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放

    },
    resolve:function(data){
        var _self = this;
        if(data && data.s){
            data.s.each(function(d,i){
                //if(i>10) return false;
                var vs = d.split("$");
                var city = vs[0];
                var area = vs[1] || city;
                var ad = vs[3] || city;

                if(city!=""){
                    var li = new Element("li.adLi",{
                        "styles":this.explorer.css.adLi,
                        "text":city+"-"+area+"-"+ad,
                        "city":city
                    }).inject(this.explorer.adDiv);
                    li.addEvents({
                        "click":function(e){
                            var city = this.get("city");
                            _self.map.clearOverlays();    //清除地图上所有覆盖物
                            //_self.map.setCurrentCity(city);
                            //_self.map.centerAndZoom(new BMap.Point(120.7, 30.7), 12);  // 初始化地图,设置中心点坐标和地图级别
                            var local = new BMap.LocalSearch(city, { //智能搜索
                                onSearchComplete: function(){
                                    var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
                                    var province = local.getResults().province;
                                    var lat = pp.lat;
                                    var lng = pp.lng;

                                    var val = province==city?city+area+ad:province+city+area+ad;
                                    _self.mapLocation.set("value",val);
                                    _self.explorer.data.lat = lat;
                                    _self.explorer.data.lng = lng;

                                    _self.map.centerAndZoom(pp, 18);
                                    _self.map.addOverlay(new BMap.Marker(pp));    //添加标注

                                    _self.app.confirm("warn",e,_self.app.lp.confirm.customForm.replaceLocation.title,_self.app.lp.confirm.customForm.replaceLocation.content,300,120,function(){
                                        _self.explorer.formTableArea.getElement("#TProvinceValue").set("text",province);
                                        _self.explorer.formTableArea.getElement("#TCityValue").set("text",city);
                                        _self.explorer.formTableArea.getElement("#TAreaValue").set("text",area);
                                        //_self.explorer.form.getItem("TStreet").set("value",province==city?city+area+ad:province+city+area+ad);
                                        _self.explorer.form.getItem("TStreet").set("value",ad);
                                        _self.explorer.adDiv.destroy();

                                        this.close();


                                    },function(){
                                        this.close();
                                        _self.explorer.adDiv.destroy();
                                    });


                                }
                            });

                            local.search(ad);
                        },
                        "mouseover":function(){
                            this.setStyles({"background":"#999999","color":"#ffffff"});
                        },
                        "mouseout":function(){
                            this.setStyles({"background":"#ffffff","color":""});
                        }
                    })
                }



            }.bind(this))
        }
    },
    setPlace:function(v){
        this.map.clearOverlays();    //清除地图上所有覆盖物

        var local = new BMap.LocalSearch(this.map, { //智能搜索
            onSearchComplete: function(){
                var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
                this.map.centerAndZoom(pp, 18);
                this.map.addOverlay(new BMap.Marker(pp));    //添加标注
            }.bind(this)
        });
        local.search(v);
    }

});



MWF.xApplication.CRM.BaiduMap.MaxMap = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "100%",
        "height": "100%"
    },
    initialize: function (explorer,app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.explorer = explorer;
        this.lp = app.lp.BaiduMap;
        this.path = "/x_component_CRM/$BaiduMap/";
        this.loadCss();

        this.actions = actions;
    },
    loadCss: function () {
        this.cssPath = "/x_component_CRM/$BaiduMap/" + this.options.style + "/css.wcss";
        this._loadCss();
    },

    load:function(data,callback){
        this.maxMapDiv = new Element("div.maxMapDiv",{"styles":this.css.maxMapDiv}).inject(this.app.content);
        this.mapHeadDiv = new Element("div.mapHeadDiv",{"styles":this.css.mapHeadDiv}).inject(this.maxMapDiv);

        this.mapContentDiv = new Element("div.mapContentDiv",{"styles":this.css.mapContentDiv}).inject(this.maxMapDiv);
        this.mapContentDiv.setStyles({
            "height":(this.app.content.getHeight()-this.mapHeadDiv.getHeight()-100)+"px",
            "width":(this.app.content.getWidth()-100)+"px"
        });

        setTimeout( function(){
            this.loadResource();
        }.bind(this) , 100 )
    },


    loadResource:function(callback){
        window.BMap_loadScriptTime = (new Date).getTime();
        var apiPath = "http://api.map.baidu.com/getscript?v=2.0&ak=Qac4WmBvHXiC87z3HjtRrbotCE3sC9Zg&services=&t=20161219171637";
        if( !window.BDMapApiLoaded ){
            COMMON.AjaxModule.loadDom(apiPath, function () {
                window.BDMapApiLoaded = true;
                if( !window.BDMarkerToolLoaded ){
                    COMMON.AjaxModule.load( "/x_component_CRM/BDMarkerTool.js", function(){
                        window.BDMarkerToolLoaded = true;
                        this._loadMap();
                        if (callback)callback();
                    }.bind(this) );
                }else{
                    this._loadMap();
                    if (callback)callback();
                }
            }.bind(this));
        }else{
            this._loadMap();
            if (callback)callback();
        }
    },
    _loadMap: function(){
        if (navigator.geolocation){
            try{
                navigator.geolocation.getCurrentPosition(this.loadMap.bind(this), this.loadMap.bind(this));
            }catch( e ){
                this.loadMap();
            }
        }else{
            this.loadMap();
        }
    },
    loadMap: function(position){
        this.createMap( position );
    },
    createMap: function( position ) {
        var point = null;

        if (this.markerData) {
            point = new BMap.Point(this.markerData.longitude, this.markerData.latitude);
        } else {
            if (position && position.coords) {
                point = new BMap.Point(position.coords.longitude, position.coords.latitude);
            }
            if (!point) {
                point = new BMap.Point(116.404, 39.915);
            }
        }


        this.map = new BMap.Map(this.mapNode);    // 创建Map实例
        var marker = new BMap.Marker(point);  // 创建标注
        this.map.addOverlay(marker);              // 将标注添加到地图中
        this.map.panTo(point);
        this.map.centerAndZoom(point, 12);  // 初始化地图,设置中心点坐标和地图级别
//		map.centerAndZoom(new BMap.Point(116.404, 39.915), 11);  // 初始化地图,设置中心点坐标和地图级别
//		map.setCurrentCity("北京");          // 设置地图显示的城市 此项是必须设置的
        this.map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放


    }


});
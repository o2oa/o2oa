MWF.xApplication.BaiduMap.contextRoot = "x_component_BaiduMap";
MWF.xApplication.BaiduMap.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "BaiduMap",
		"icon": "icon.png",
		"width": "1000",
		"height": "600",
		"isResize": true,
		"title": MWF.xApplication.BaiduMap.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.BaiduMap.LP;
		this.mapPageLoaded = false;
		this.windowLoaded = false;
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("iframe", {
			"styles": this.css.contentNode,
			"border": 1,
			"frameBorder": "1",
			"marginHeight": 0,
			"marginHeight": 0,
			"src": "/"+MWF.xApplication.BaiduMap.contextRoot+"/$Main/map.html"
		}).inject(this.content);
		this.mapWindow = this.node.contentWindow;
		this.mapDocument = this.mapWindow.document;
		this.mapDocument.addEventListener("readystatechange", function(){
			if (this.mapDocument.readyState=="complete"){
				this.mapPageLoaded = true;
				this.loadApplicationContent();
			}
		}.bind(this));
	},
	loadApplication: function(callback){
		this.createNode();
		if (!this.options.isRefresh){
			this.maxSize(function(){
				this.windowLoaded = true;
				this.loadApplicationContent();
				if (callback) callback();
			}.bind(this));
		}else{
			window.setTimeout(function(){
				this.windowLoaded = true;
				this.loadApplicationContent();
			}.bind(this), 200);
			if (callback) callback();
		}
	},
	loadApplicationContent: function(){

		if (this.mapPageLoaded  && this.windowLoaded){
			this.mapNode = this.mapDocument.getElementById("map");
			
			if (navigator.geolocation){
				navigator.geolocation.getCurrentPosition(this.loadMap.bind(this), this.loadMap.bind(this));
			}else{
				this.loadMap();
			}
		}
	},
	loadMap: function(position){
		var point = null;
		if (position && position.coords){
			point = new this.mapWindow.BMap.Point(position.coords.longitude, position.coords.latitude);
		}
		var map = new this.mapWindow.BMap.Map(this.mapNode);    // 创建Map实例
		map.centerAndZoom(point, 12);  // 初始化地图,设置中心点坐标和地图级别
//		map.centerAndZoom(new this.mapWindow.BMap.Point(116.404, 39.915), 11);  // 初始化地图,设置中心点坐标和地图级别
//		map.addControl(new this.mapWindow.BMap.MapTypeControl());   //添加地图类型控件
//		map.setCurrentCity("北京");          // 设置地图显示的城市 此项是必须设置的
		map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
	}
});

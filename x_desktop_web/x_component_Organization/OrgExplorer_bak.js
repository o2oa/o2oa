MWF.require("MWF.widget.MWFRaphael", null, false);
MWF.xApplication.Organization.OrgExplorer = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	
	initialize: function(node, actions, options){
		this.setOptions(options);
		
		this.path = "/x_component_Organization/$OrgExplorer/";
		this.cssPath = "/x_component_Organization/$OrgExplorer/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.actions = actions;
		this.node = $(node);
	},
	load: function(){
		this.loadLayout();
		this.loadChart();
	},
	loadLayout: function(){
		this.propertyAreaNode = new Element("div", {"styles": this.css.propertyAreaNode}).inject(this.node);
		this.chartAreaNode = new Element("div", {"styles": this.css.chartAreaNode}).inject(this.node);
		
		this.resizeBarNode = new Element("div", {"styles": this.css.resizeBarNode}).inject(this.propertyAreaNode);
		this.propertyNode = new Element("div", {"styles": this.css.propertyNode}).inject(this.propertyAreaNode);
		
		this.propertyTitleNode = new Element("div", {"styles": this.css.propertyTitleNode}).inject(this.propertyNode);
		this.propertyContentNode = new Element("div", {"styles": this.css.propertyContentNode}).inject(this.propertyNode);
		this.resizePropertyContentNode();
		this.app.addEvent("resize", function(){this.resizePropertyContentNode();}.bind(this));
		
		this.chartNode = new Element("div", {"styles": this.css.chartNode}).inject(this.chartAreaNode);
		
		this.propertyResize = new Drag(this.resizeBarNode,{
			"snap": 1,
			"onStart": function(el, e){
				var x = e.event.clientX;
				var y = e.event.clientY;
				el.store("position", {"x": x, "y": y});
				
				var size = this.propertyAreaNode.getSize();
				el.store("initialWidth", size.x);
			}.bind(this),
			"onDrag": function(el, e){
				var x = e.event.clientX;
//				var y = e.event.y;
				var bodySize = this.node.getSize();
				var position = el.retrieve("position");
				var initialWidth = el.retrieve("initialWidth").toFloat();
				var dx = position.x.toFloat()-x.toFloat();
				
				var width = initialWidth+dx;
				if (width> bodySize.x/1.5) width =  bodySize.x/1.5;
				if (width<40) width = 40;
				this.chartAreaNode.setStyle("margin-right", width+1);
				this.propertyAreaNode.setStyle("width", width);
			}.bind(this)
		});
	},
	resizePropertyContentNode: function(){
		var size = this.node.getSize();
		var tSize = this.propertyTitleNode.getSize();
		var mtt = this.propertyTitleNode.getStyle("margin-top").toFloat();
		var mbt = this.propertyTitleNode.getStyle("margin-bottom").toFloat();
		var mtc = this.propertyContentNode.getStyle("margin-top").toFloat();
		var mbc = this.propertyContentNode.getStyle("margin-bottom").toFloat();		
		var height = size.y-tSize.y-mtt-mbt-mtc-mbc;
		this.propertyContentNode.setStyle("height", height);
	},
	loadChart: function(){
		this.actions.listTopCompany(function(json){
			MWFRaphael.load(function(){
				this.paper = Raphael(this.chartNode, "100%", "99%");
				this.paper.container = this.chartNode;
				this.loadChartContent(json.data, "company");
			}.bind(this));
		}.bind(this));
	},
	loadChartContent: function(data, type){
		var tmpItem = null;
		data.each(function(itemData){
			var item = new MWF.xApplication.Organization.OrgExplorer.Company(itemData, this);
			if (tmpItem) item.prevItem = tmpItem;
			item.load();
			tmpItem = item;
		}.bind(this));
	}
	
//	this.shap = this.createShap();
//	//	this.shadow = this.careteShadow();
//		this.text = this.createText();
//		this.icon = this.createIcon();
//		this.set.push(this.shadow, this.shap, this.text, this.icon);
	
});

MWF.xApplication.Organization.OrgExplorer.Item = new Class({
	initialize: function(data, explorer){
		this.data = data;
		this.explorer = explorer;
		this.paper = this.explorer.paper;
		
		this.prevItem = null;
		this.nextItem = null;
		this.parentItem = null;
		this.children = [];
		this.position = {
			x: 0,
			y: 0,
			maxY: 0,
			center: {
				x: 0,
				y: 0
			}
		};
		
		this.initStyles();
	},
	initStyles: function(){
	//	this.width = 150;
	//	this.margin = 50;
		this.style = this.explorer.css.chart.company;
	},
	load: function(){
		this.getItemPostion();
		
		this.shap = this.createShap();
		this.text = this.createText();
		this.icon = this.createIcon();
		if (!this.set) this.set = this.paper.set();
		this.set.push(this.shap, this.text, this.icon);
	},
	getItemPostion: function(){
		var x = this.style.width*(this.data.level-1)+20;
		var y = 0;
		if (this.prevItem){
			y = this.prevItem.position.maxY+20;
		}else if (this.parentItem){
			y = this.parentItem.position.y;
		}else{
			y = 20;
		}
		this.position.x = x;
		this.position.y = y;
		this.position.maxY = y+this.style.height;
		this.setParentMaxY();
	},
	setParentMaxY: function(){
		var item = this.parentItem;
		if (item){
			if (item.position.maxY<this.position.maxY){
				item.position.maxY = this.position.maxY;
				item.setParentMaxY();
			}
		}
	},
	createShap: function(){
		var shap;
		shap = this.paper.rectPath(this.position.x, this.position.y, this.style.width, this.style.height, this.style.radius);
		shap.attr(this.style.shap);
		shap.data("bind", this);
		
		return shap;
	},
	createText: function(){
		var atts = this.getTextIconPoint();
		text = this.paper.text(atts.tatt.x, atts.tatt.y, this.data.name);
		text.attr(this.style.text);
		if (this.style.text.display=="none"){
			text.hide();
		}
		return text;
	},
	createIcon: function(){
		var atts = this.getTextIconPoint();
		var icon = this.paper.image(this.style.src, atts.iatt.x, atts.iatt.y, 48, 48);
		icon.attr(this.style.icon);
		return icon;
	},
	getTextIconPoint: function(){
		var t_att = {x: this.position.x, y: this.position.y+(this.style.height/2)};
		var i_att = {x: this.position.x+6, y: this.position.y+6};
		return {"tatt": t_att, "iatt": i_att};
	},
});
MWF.xApplication.Organization.OrgExplorer.Company = new Class({
	Extends: MWF.xApplication.Organization.OrgExplorer.Item
});
MWF.xApplication.Organization.OrgExplorer.Department = new Class({
	Extends: MWF.xApplication.Organization.OrgExplorer.Item,
	initStyles: function(){
		this.style = this.explorer.css.chart.department;
	},
});

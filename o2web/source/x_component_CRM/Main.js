MWF.xApplication.CRM = MWF.xApplication.CRM || {};
MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("CRM", "Actions.RestActions", null, false);

MWF.xApplication.CRM.options = {
	multitask: false,
	executable: true
};

MWF.xApplication.CRM.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "CRM",
		"icon": "icon.png",
		"width": "1400",
		"height": "700",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.CRM.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.CRM.LP;
		if(!this.inBrowser){

			this.openInNewBrowserX();
		}else{
			COMMON.AjaxModule.loadCss( this.path+this.options.style+"/main.css",function(){
				console.log("main.css,load complete");
			})
		}
	},
	openInNewBrowserX: function(){
		this.desktop.openBrowserApp = this.options.name;
		this.desktop.openBrowserStatus = (this.recordStatus) ? this.recordStatus() : null;
		var status = (this.desktop.openBrowserStatus) ? JSON.encode(this.desktop.openBrowserStatus) : "";

		var url = "app.html?app="+this.options.name+"&status="+status;
		window.open(url, "_blank");
		if (!this.inBrowser)
			try{
				this.close();
			}catch(e){
				this.taskitem.destroy();
				this.window = null;
				this.taskitem = null;
				this.desktop.closeApp(this);
				this.fireAppEvent("postClose");
				o2.release(this);
			}
	},

	load: function(isCurrent){
		this.fireAppEvent("queryLoad");
		if (!this.inBrowser){
			this.loadWindow(isCurrent);
		}else{
			this.loadInBrowser(isCurrent);
		}
	},
	onPostLoad:function(){
		this.resizeWindow();
	},
	loadApplication: function(callback){
		this.maxSize(function () {
			this.user = layout.desktop.session.user.name;
			this.userGender = layout.desktop.session.user.genderType;
			//this.restActions = new MWF.xApplication.CRM.Actions.RestActions();

			this.actions = new MWF.xApplication.CRM.Actions.RestActions();
			//this.identites = this.getIdentityies();

			if( !this.container ){
				this.content.setStyle("overflow", "hidden");
				this.container = new Element("div.container",{"styles":this.css.container}).inject(this.content)
			}



			this.createTopContent();
			this.createMiddleContent();
			//this.resizeWindow();
			this.addEvent("c", function(){
				this.resizeWindow();
			}.bind(this));

			MWF.xDesktop.requireApp("CRM", "BaiduMap", function(){
				window.BMap_loadScriptTime = (new Date).getTime();
				var apiPath = "http://api.map.baidu.com/getscript?v=2.0&ak=Qac4WmBvHXiC87z3HjtRrbotCE3sC9Zg&services=&t=20161219171637";
				if( !window.BDMapApiLoaded ){
					COMMON.AjaxModule.loadDom(apiPath, function () {
						window.BDMapApiLoaded = true;
						if( !window.BDMarkerToolLoaded ){
							COMMON.AjaxModule.load( "/x_component_CRM/BDMarkerTool.js", function(){
								window.BDMarkerToolLoaded = true;
							});
						}else{

						}
					});
				}
			}.bind(this));
			if(callback) callback();
		}.bind(this));

	},
	reload:function(){
		this.createMiddleContent();
		this.resizeWindow();
	},
	createMiddleContent:function(){

		if(this.middleContentDiv) this.middleContentDiv.destroy();
		this.middleContentDiv = new Element("div.middleContentDiv",{"styles":this.css.middleContentDiv}).inject(this.container);
		this.createLeftContent();
		//this.createRightContent();
	},
	////////////////////Top//////////////////////////////////////
	createTopContent:function(){
		if(this.topContentDiv) this.topContentDiv.destroy();
		this.topContentDiv = new Element("div.topContentDiv",{"styles":this.css.topContentDiv}).inject(this.container);
		this.logoDiv = new Element("div.logoDiv",{"styles":this.css.logoDiv}).inject(this.topContentDiv);
		this.logoImg = new Element("img.logoImg",{
			"styles":this.css.logoImg,
			"src":this.path+"default/icons/crm.png"
		}).inject(this.logoDiv);

		this.logoTitleDiv = new Element("div.logoTitleDiv",{
			"styles":this.css.logoTitleDiv,
			"text":this.lp.main.title
		}).inject(this.logoDiv);

		this.topLeftDiv = new Element("div.topLeftDiv",{"styles":this.css.topLeftDiv}).inject(this.topContentDiv);

		var topLeftLi = new Element("li.topLeftLi",{"styles":this.css.topLeftLi}).inject(this.topLeftDiv);
		var tmpDiv = new Element("div.topLeftLiDiv",{"styles":this.css.topLeftLiDiv,"text":this.user}).inject(topLeftLi);
		var tmpImg = new Element("img.topLeftLiImg",{
			"styles":this.css.topLeftLiImg,
			"src":this.path+"default/icons/arrow.png"
		}).inject(topLeftLi);
	},
	isAdmin: function(){
		return   MWF.AC.isCMSManager() || MWF.AC.isAdministrator()
	},






	////////////////////Top//////////////////////////////////////

	////////////////////left//////////////////////////////////////
	createLeftContent:function(){
		this.leftContentDiv = new Element("div.leftContentDiv",{"styles":this.css.leftContentDiv}).inject(this.middleContentDiv);

		this.quickStartDiv = new Element("div.quickStartDiv",{"styles":this.css.quickStartDiv}).inject(this.leftContentDiv);
		//this.quickStartDiv.toggle();
		this.quickStartImg = new Element("img.quickStartImg",{
			"styles":this.css.quickStartImg,
			"src":this.path+"default/icons/add.png"
		}).inject(this.quickStartDiv);
		this.quickStartTextDiv = new Element("div.quickStartTextDiv",{
			"styles":this.css.quickStartTextDiv,
			"text":this.lp.main.quickStart
		}).inject(this.quickStartDiv);
		this.createNavi();


	},
	createNavi:function(){
		var _self = this;
		this.naviDiv = new Element("div.naviDiv",{
			"styles" : this.css.naviDiv
		}).inject(this.leftContentDiv);
		var jsonUrl = this.path+"navi.json";
		MWF.getJSON(jsonUrl, function(json){
			json.each(function(d){ //menu
				if(d.type=="menu"){
					var naviMenuLi = new Element("div.naviMenuLi",{
						"styles":this.css.naviMenuLi
					}).inject(this.naviDiv);
					var naviMenuImg = new Element("img.naviMenuImg",{
						"styles" : this.css.naviMenuImg,
						"src" : this.path + d.icon
					}).inject(naviMenuLi);
					var naviMenuTxtDiv = new Element("div.naviMenuTxtDiv",{
						"styles" : this.css.naviMenuTxtDiv,
						"text" : d.title
					}).inject(naviMenuLi);

					d.items.each(function(dd){
						var naviItemLi = new Element("div.naviItemLi",{
							"styles":this.css.naviItemLi,
							"id":dd.action,
							"action":dd.action
						}).inject(this.naviDiv);
						var naviItemImg = new Element("img.naviItemImg",{
							"styles" : this.css.naviItemImg,
							"src" : this.path + dd.icon,
							"df":dd.icon,
							"dfFill":dd.iconFill
						}).inject(naviItemLi);
						var naviItemTxtDiv = new Element("div.naviItemTxtDiv",{
							"styles" : this.css.naviItemTxtDiv,
							"text" : dd.title
						}).inject(naviItemLi);
						naviItemLi.addEvents({
							"mouseover":function(){
								if(this.get("action") != _self.curModule){
									this.setStyles({"color":"#ff8e31"});
									var tmp = this.getElement("img");
									if(tmp)tmp.set("src",_self.path + tmp.get("dfFill"))
								}
							},
							"mouseout":function(){
								if(this.get("action") != _self.curModule){
									this.setStyles({"color":"#ffffff"});
									var tmp = this.getElement("img");
									if(tmp)tmp.set("src",_self.path + tmp.get("df"))
								}
							},
							"click":function(){
								//if(_self.curModule != this.get("action")){
									_self.curModule = this.get("action");
									var allLi = _self.naviDiv.getElements(".naviItemLi");
									allLi.setStyles({"color":"#ffffff"});
									allLi.getElement("img").each(function(ddd){
										ddd.set("src",_self.path + ddd.get("df"))
									});

									this.setStyles({"color":"#ff8e31"});
									var tmp = this.getElement("img");
									if(tmp)	tmp.set("src",_self.path + tmp.get("dfFill"));
									_self.openModule(this.get("action"))
								//}

							}
						})
					}.bind(this))
				}
			}.bind(this));
		}.bind(this),false);

		//this.naviDiv.getElementById("homePage").click()
		this.naviDiv.getElements(".naviItemLi").each(function(d){
			if(d.get("action") == "homePage"){
				_self.curModule = "homePage";
				d.setStyles({"color":"#ff8e31"});
				var tmp = d.getElement("img");
				if(tmp)tmp.set("src",_self.path + tmp.get("dfFill"));
                _self.openHomePage();
			}
		})
	},
	openModule:function(action){
		if(action == "homePage"){
			this.openHomePage();
		}else if(action == "message"){
			this.openMessage()
		}else if(action == "clue"){
			this.openClue();
		}else if(action == "customer"){
			this.openCustomer()
		}else if(action == "publicseas"){
			this.openPublicseas()
		} else if(action == "contact"){
			this.openContact()
		}else if(action == "chance"){
			this.openChance()
		}else if(action == "stat"){
			this.openStat()
		}
	},
	openHomePage:function(){
		//首页
		//if(this.rightContentDiv)this.rightContentDiv.empty();
		if(this.indexModule) delete this.indexModule;
		MWF.xDesktop.requireApp("CRM","Index",function(){
			if(this.rightContentDiv) this.rightContentDiv.destroy();
			this.rightContentDiv = new Element("div.rightContentDiv#rightContentDiv",{"styles":this.css.rightContentDiv}).inject(this.middleContentDiv);
			this.resizeWindow();
			this.indexModule = new MWF.xApplication.CRM.Index(this.rightContentDiv,this,this.actions,{"isAdmin":this.isAdmin()});
			this.indexModule.load();
		}.bind(this));
	},
	openMessage:function(){
		//信息
		if(this.rightContentDiv)this.rightContentDiv.empty();
		if(this.messageModule) delete this.messageModule;
		MWF.xDesktop.requireApp("CRM", "Message", function(){
			this.messageModule = new MWF.xApplication.CRM.Message(this.rightContentDiv,this,this.actions,{"isAdmin":this.isAdmin()});
			this.messageModule.load();
		}.bind(this))
	},
	openClue:function(){
		//线索
		if(this.rightContentDiv)this.rightContentDiv.empty();
		if(this.clueModule) delete this.clueModule;
		MWF.xDesktop.requireApp("CRM","Clue",function(){
			this.clueModule = new MWF.xApplication.CRM.Clue(this.rightContentDiv,this,this.actions,{"isAdmin":this.isAdmin()});
			this.clueModule.load();
		}.bind(this));
	},
	openCustomer:function(){
		//客户
		if(this.rightContentDiv)this.rightContentDiv.empty();
		if(this.customerModule) delete this.customerModule;
		MWF.xDesktop.requireApp("CRM", "Customer", function(){
			this.customerModule = new MWF.xApplication.CRM.Customer(this.rightContentDiv,this,this.actions,{"isAdmin":this.isAdmin()});
			this.customerModule.load();
		}.bind(this))
	},
	openPublicseas:function(){
		//客户
		if(this.rightContentDiv)this.rightContentDiv.empty();
		if(this.publicseasModule) delete this.publicseasModule;
		MWF.xDesktop.requireApp("CRM", "Publicseas", function(){
			this.publicseasModule = new MWF.xApplication.CRM.Publicseas(this.rightContentDiv,this,this.actions,{"isAdmin":this.isAdmin()});
			this.publicseasModule.load();
		}.bind(this))
	},
	openContact:function(){
		if(this.rightContentDiv)this.rightContentDiv.empty();
		if(this.contactsModule) delete this.contactsModule;
		MWF.xDesktop.requireApp("CRM", "Contacts", function(){
			this.contactsModule = new MWF.xApplication.CRM.Contacts(this.rightContentDiv,this,this.actions,{"isAdmin":this.isAdmin()});
			this.contactsModule.load();
		}.bind(this))
	},
	openChance: function(){
		//商机
		if(this.rightContentDiv)this.rightContentDiv.empty();
		if(this.chanceModule) delete this.chanceModule;
		MWF.xDesktop.requireApp("CRM", "Chance", function(){
			this.chanceModule = new MWF.xApplication.CRM.Chance(this.rightContentDiv,this,this.actions,{"isAdmin":this.isAdmin()});
			this.chanceModule.load();
		}.bind(this))
	},
	openStat: function(){

	},
	////////////////////left//////////////////////////////////////

	////////////////////right//////////////////////////////////////
	createRightContent:function(){

		if(this.rightContentDiv) this.rightContentDiv.destroy();
		this.rightContentDiv = new Element("div.rightContentDiv",{"styles":this.css.rightContentDiv}).inject(this.middleContentDiv);

	},
	////////////////////right//////////////////////////////////////








	//////////////////////////////////////公用方法///////////////////////////////////////////////
	resizeWindow:function(){
		var size = this.content.getSize();
		if(this.middleContentDiv) this.middleContentDiv.setStyles({"height":(size.y-this.topContentDiv.getSize().y)+"px"});
		if(this.middleContentDiv) var midSize = this.middleContentDiv.getSize();
		if(this.leftContentDiv) this.leftContentDiv.setStyles({"height":midSize.y+"px"});
		if(this.rightContentDiv) this.rightContentDiv.setStyles({
			"height":midSize.y+"px",
			"width":(midSize.x-this.leftContentDiv.getSize().x)+"px",
			"margin-left":this.leftContentDiv.getSize().x+"px"
		})
	},

	recordStatus: function(){
		var status;
		status = {
			identity: this.identity
		};
		return status;
	},



	createShade: function(obj,t){
		var defaultObj = this.content;
		var obj = obj || defaultObj;
		var txt;
		txt = t || "loading...";
		if(this.shadeDiv){ this.shadeDiv.destroy()}
		if(this.shadeTxtDiv)  this.shadeTxtDiv.destroy();
		this.shadeDiv = new Element("div.shadeDiv").inject(obj);
		this.inforDiv = new Element("div.inforDiv",{
			styles:{"height":"16px","display":"inline-block","position":"absolute","background-color":"#336699","border-radius":"3px","padding":"5px 10px"}
		}).inject(this.shadeDiv);
		this.loadImg = new Element("img.loadImg",{
			styles:{"width":"16px","height":"16px","float":"left"},
			src:"/x_component_CRM/$Main/default/icons/loading.gif"
		}).inject(this.inforDiv);

		this.shadeTxtSpan = new Element("span.shadeTxtSpan").inject(this.inforDiv);
		this.shadeTxtSpan.set("text",txt);
		this.shadeDiv.setStyles({
			"width":"100%","height":"100%","position":"absolute","opacity":"0.7","background-color":"#cccccc","z-index":"999"
		});
		this.shadeTxtSpan.setStyles({"color":"#ffffff","font-size":"12px","display":"inline-block","line-height":"16px","padding-left":"5px"});

		var x = obj.getSize().x;
		var y = obj.getSize().y;
		this.shadeDiv.setStyles({
			"left":(obj.getLeft()-defaultObj.getLeft())+"px",
			"top":(obj.getTop()-defaultObj.getTop())+"px",
			"width":x+"px",
			"height":y+"px"
		});
		if(obj.getStyle("position")=="absolute"){
			this.shadeDiv.setStyles({
				"left":"0px",
				"top":"0px"
			})
		}
		this.inforDiv.setStyles({
			"left":(x/2)+"px",
			"top":(y/2)+"px"
		})
	},
	destroyShade : function(){
		if(this.shadeDiv) this.shadeDiv.destroy();
		//if(this.shadeDiv) this.shadeDiv.destroy()
	},
	setScrollBar: function(node, view, style, offset, callback){
		if (!style) style = "default";
		if (!offset){
			offset = {
				"V": {"x": 0, "y": 0},
				"H": {"x": 0, "y": 0}
			};
		}
		MWF.require("MWF.widget.ScrollBar", function(){
			if(this.scrollbar && this.scrollbar.scrollVAreaNode){
				this.scrollbar.scrollVAreaNode.destroy();
				delete this.scrollbar;
			}

			this.scrollbar = new MWF.widget.ScrollBar(node, {
				"style": style||"default",
				"offset": offset,
				"indent": false,
				"distance": 50,
				"onScroll": function (y) {
					var scrollSize = node.getScrollSize();
					var clientSize = node.getSize();
					var scrollHeight = scrollSize.y - clientSize.y;
					//var view = this.baseView || this.centerView;
					if (y + 20 > scrollHeight && view && view.loadElementList) {
						if (! view.isItemsLoaded)view.loadElementList();
					}
				}.bind(this)
			});
			if (callback) callback();
		}.bind(this));
		return false;
	},
	showErrorMessage:function(xhr,text,error){
		var errorText = error;
		if (xhr) errorMessage = xhr.responseText;
		if(errorMessage!=""){
			var e = JSON.parse(errorMessage);
			if(e.message){
				this.notice( e.message,"error");
			}else{
				this.notice( errorText,"error");
			}
		}else{
			this.notice(errorText,"error");
		}

	}
	//////////////////////////////////////公用方法///////////////////////////////////////////////


});

MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.options = {
	multitask: false,
	executable: true
};

MWF.xApplication.TeamWork.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "TeamWork",
		"icon": "appicon.png",
		// "width": "1270",
		// "height": "700",
		// "isResize": false,
		// "isMax": true,
		"title": MWF.xApplication.TeamWork.LP.title
	},
	onQueryLoad: function(){ //111
		this.lp = MWF.xApplication.TeamWork.LP;
	},

	loadApplication: function(callback){
		this.user = layout.desktop.session.user.name;
		this.distinguishedName = layout.desktop.session.user.distinguishedName;

		this.userGender = layout.desktop.session.user.genderType;
		this.department="";

		//this.restActions = MWF.Actions.get("x_teamwork_assemble_control");
		//this.orgActions = MWF.Actions.get("x_organization_assemble_express");

		this.rootActions = MWF.Actions.load("x_teamwork_assemble_control");
		this.orgActions = MWF.Actions.load("x_organization_assemble_express");


		this.path = "/x_component_TeamWork/$Main/";
		if(!this.css){
			this.cssPath = this.path+this.options.style+"/css.wcss";
			this._loadCss();
		}


		MWF.xDesktop.requireApp("TeamWork", "ProjectList", function(){
			this.pl = new MWF.xApplication.TeamWork.ProjectList(this.content,this,this.rootActions,{

			});
			this.pl.load();
		}.bind(this));

		this.addEvent("resize", function(){
			this.resize();
		}.bind(this));
	},
	showTips:function(target,data,opt){
		var opt = Object.merge(  {nodeStyles:this.css.tips.nodeStyles}, opt );
		// if(this.stTimer){
		// 	clearTimeout(this.stTimer);
		// }
		// this.stTimer = window.setTimeout(function(){
		// 	var tt  = new MWF.xApplication.TeamWork.Common.Tips(this.content, target, this.app, data, opt);
		// 	tt.load();
		// }.bind(this),100)
		this.st = new MWF.xApplication.TeamWork.Common.Tips(this.content, target, this.app, data, opt);
		this.st.load();
	},
	tips:function(target,title){
		//if(myTips) delete myTips;
		var myTips = new Tips(target, {
			onShow:function(tip, el){

				console.log("ttt="+title);
				tip.setStyles({
					visibility: 'hidden',
					display: 'block',
					"background-color":"#000000",
					"border-radius":"5px",
					"padding":"5px",
					"color":"#ffffff",
					"offset":{
						x:200,
						y:200
					}
				}).fade('in');

			},
			onHide:function(tip,el){
				myTips.setTitle("");
			},
			title:function(){
				return title
			}
		});

		myTips.setTitle(title);
		//if you want to add this after init
		// myTips.removeEvents('show').addEvent('show', function(tip, el){
		// 	console.log("ttt="+title)
		// 	tip.setStyles({
		// 		visibility: 'hidden',
		// 		display: 'block',
		// 		"background-color":"#000000",
		// 		"border-radius":"5px",
		// 		"padding":"5px",
		// 		"color":"#ffffff",
		// 		"offset":{
		// 			x:200,
		// 			y:200
		// 		},
		// 		title:function(){
		// 			return title
		// 		}
		// 	}).fade('in');
		// });
	},


	selectCalendar : function( target, container, options, callback ){
		var type = options.type;
		var calendarOptions = {
			"style" : "xform",
			"isTime":  type == "time" || type.toLowerCase() == "datetime",
			"timeOnly": type == "time",
			"target": container,
			"onQueryComplate" : function( dateString ,date ){
				var json={
					"action":"ok",
					"dateString":dateString,
					"date":date
				};
				if( callback )callback( json );
			}.bind(this),
			"onClear":function(){
				var json={
					"action":"clear"
				};
				if(callback) callback(json);
				//if(this.calendar) delete this.calendar;
			}.bind(this),
			"onHide":function(){

			}.bind(this)
		};
		if( options.calendarOptions ){
			calendarOptions = Object.merge( calendarOptions, options.calendarOptions )
		}

		MWF.require("MWF.widget.Calendar", function(){
			this.calendar = new MWF.widget.Calendar( target, calendarOptions);
			this.calendar.show();

		}.bind(this));
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
				"style": style,
				"offset": offset,
				"where": "before",
				"indent": false,
				"distance": 100,
				"friction": 4,
				"onScroll": function (y) {
					var scrollSize = node.getScrollSize();
					var clientSize = node.getSize();
					var scrollHeight = scrollSize.y - clientSize.y;
					if (y + 200 > scrollHeight && view && view.loadElementList) {
						if (! view.isItemsLoaded) view.loadElementList()
					}
				}.bind(this)
			});
			if (callback) callback();
		}.bind(this));
		return false;
	},
	setLoading:function(container){
		var _height = container.getHeight();
		var _width = container.getWidth();
		var loading = new Element("img",{styles:this.css.loading,"src":"/x_component_TeamWork/$Main/default/icon/loading.gif"}).inject(container);
		//var loading = new Element("img",{"src":"/x_component_TeamWork/$Main/default/icon/loading.gif"}).inject(container);

		loading.setStyles({
			"margin-left":(_width-loading.getWidth())/2+"px"
		})
	},
	showErrorMessage:function(xhr,text,error){
		var errorText = error;
		var errorMessage;
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

	},
	compareWithNow:function(dstr){
		var result = {};

		try{
			var ct = Date.parse(dstr);
			var intervalDay = 0;
			var now = new Date();
			var sep = now.getTime()-ct.getTime();
			sep = sep/1000; //毫秒
			//一分钟内，刚刚，一小时内，多少分钟前，2小时内，显示一小时前，2小时到今天00：00：00 显示 今天几点，本周内，显示本周几，几点几分，其他显示几月几日
			var cttext = "";
			if(sep<60){
				cttext = "刚刚"
			}else if(sep<3600){
				cttext = Math.floor(sep/60)+"分钟前"
			}else if(sep<7200){
				cttext = "1小时前"
			}else if(sep>7200 && ct.getFullYear() == now.getFullYear() && ct.getMonth()==now.getMonth() && ct.getDate() == now.getDate()){
				cttext = "今天"+(ct.getHours()<10?("0"+ct.getHours()):ct.getHours())+":"+(ct.getMinutes()<10?"0"+ct.getMinutes():ct.getMinutes())
			}else if(ct.getFullYear() == now.getFullYear() && ct.getMonth()==now.getMonth() && ct.getDate() == now.getDate()-1){
				cttext = "昨天"+(ct.getHours()<10?("0"+ct.getHours()):ct.getHours())+":"+(ct.getMinutes()<10?"0"+ct.getMinutes():ct.getMinutes());
			}else{
				cttext = (ct.getMonth()+1) + "月"+ct.getDay()+"日"
			}

			var sepd = ct.getTime() - now.getTime();
			sepd = sepd/1000;

			if(sepd<0){
				intervalDay = -1 //超时
			}else if(sepd /(3600*24)<2){
				intervalDay = 0 //一两天内
			}else{
				intervalDay = 1 //正常
			}

			result.intervalDay = intervalDay;
			result.text = cttext;
		}catch(e){
			result.text = dstr;
		}
		//alert(dstr + "##############" + result.intervalDay)
		return result;

	},
	formatDate:function(dstr,format){
		var result = "";
		try{
			if(dstr && dstr!=""){
				var ct = Date.parse(dstr);
				result = ct.getFullYear()+"年"+(ct.getMonth()+1)+"月"+ct.getDate()+"日"
			}
		}catch(e){}

		return result;
	},
	resize:function(){
		//alert("resize")

		//Project
		if(this.content.getElements(".taskGroupItemContainer").length>0){
			this.content.getElements(".taskGroupItemContainer").each(function(d){
				var pe =  d.getParent();
				var pr_w = pe.getElement(".taskGroupItemTitleContainer").getHeight().toInt();

				var _h = pe.getHeight().toInt() - pr_w -10-10;
				d.setStyles({"height":_h+"px"})

			});
		}

		if(this.content.getElements(".foldIcon").length>0){
			var fo = this.content.getElements(".foldIcon")[0];
			var p = fo.getParent();
			var _margin_height = (p.getHeight())/2 - (fo.getHeight())/2;
			fo.setStyles({"margin-top":_margin_height+"px"});
		}




		//Task
		if(this.content.getElement(".taskInforContainer")){
			var _h = this.content.getElement(".taskInforContainer").getHeight().toInt();
			if(this.content.getElement(".taskInforContent")){
				this.content.getElement(".taskInforContent").setStyle("height",(_h+70)+"px")
			}
		}

		//taskGroupItemContainer
		//taskGroupLayout，taskGroupItemContainer  自定义高度



	},
});

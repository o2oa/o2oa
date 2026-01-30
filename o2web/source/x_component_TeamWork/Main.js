MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.options = {
	multitask: false,
	executable: true
};

/*
* 蓝色 #4a90e2 
* 灰色 #666666 ##
*/

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
	onQueryLoad: function(){ //
		this.lp = MWF.xApplication.TeamWork.LP;
	},
	loadCSSFont:function(){ 
		var path = this.path + "font/iconfont.css";
		o2.loadCss(path,{url:true}) 
	},

	loadApplication: function(callback){
		var isMobile = layout.mobile;
		this.user = layout.desktop.session.user.name;
		this.distinguishedName = layout.desktop.session.user.distinguishedName;

		this.userGender = layout.desktop.session.user.genderType;
		this.department="";

		//this.restActions = MWF.Actions.get("x_teamwork_assemble_control");
		//this.orgActions = MWF.Actions.get("x_organization_assemble_express");

		this.rootActions = MWF.Actions.load("x_teamwork_assemble_control");
		this.orgActions = MWF.Actions.load("x_organization_assemble_express");

		// this.path = "../x_component_TeamWork/$Main/";
		// if(!this.css){
		// 	this.cssPath = this.path+this.options.style+"/css.wcss";
		// 	this._loadCss();
		// }
		
		//初始化一些信息
		//初始化优先级
		this.rootActions.GlobalAction.initConfig();
		this.loadCSSFont();

		if(isMobile){ 
			MWF.xDesktop.requireApp("TeamWork", "Mobile", function(){ 
				new MWF.xApplication.TeamWork.Mobile(this.content,this);
			}.bind(this))
		}else{ 
			this.path = "../x_component_TeamWork/$Main/";
			if(!this.css){
				this.cssPath = this.path+this.options.style+"/css.wcss";
				this._loadCss();
			}
			var _html = this.path + this.options.style+"/view.html";
			this.cssFile = this.path + this.options.style  + "/style.css";
			// this.loadCSSFont();
			
			this.content.loadAll({"html":_html,css:this.cssFile},{"bind": {"lp": this.lp}, "module": this},function(){ 
				this.loadCount();
				
				this.add.addEvents({
					mouseover:function(){ 
						this.tips(this.add,this.lp.ProjectList.content.addProjectBlockText)
					}.bind(this)
				})
	
				//this.itemall.click();
				this.taskmy.click();
				
			}.bind(this))
	
	
			this.addEvent("resize", function(){
				this.resize();
			}.bind(this));
		}

		// var _html = this.path + this.options.style+"/view.html";
		// this.cssFile = this.path + this.options.style  + "/style.css";
		// this.loadCSSFont();
		
        // this.content.loadAll({"html":_html,css:this.cssFile},{"bind": {"lp": this.lp}, "module": this},function(){ 
        //     this.loadCount();
            
		// 	this.add.addEvents({
		// 		mouseover:function(){ 
		// 			this.tips(this.add,this.lp.ProjectList.content.addProjectBlockText)
		// 		}.bind(this)
		// 	})

		// 	//this.itemall.click();
		// 	this.taskmy.click();
			
        // }.bind(this))

		// MWF.xDesktop.requireApp("TeamWork", "ProjectList", function(){
		// 	this.pl = new MWF.xApplication.TeamWork.ProjectList(this.content,this,this.rootActions,{

		// 	});
		// 	this.pl.load();
		// }.bind(this));

		// this.addEvent("resize", function(){
		// 	this.resize();
		// }.bind(this));
	},
	loadCount:function(){
		//导航各分类数量
		this.rootActions.ProjectAction.statiticMyProject(function(json){ 
			var data = json.data; 
			this.countAll.set("text","(" + (data.allCount||0) + ")");
			this.countStar.set("text",data.starCount||0);
			this.countMy.set("text",data.myCount||0);
			this.countDelay.set("text",data.delayCount||0);
			this.countCompleted.set("text",data.completedCount||0);
			this.countArchived.set("text",data.archiveCount||0);
		}.bind(this))
	},
	clickNavi:function(t){
		var itemList = this.navi_layout.getElements('.item');
		itemList.setStyles({
			'background-color':'',
		    'color':''
		})
		itemList.forEach(function(item){ 
			var _icon = item.getElement('.icon')
			var _class = item.get('class')
			if(_icon){ 
				if(_class.indexOf('taskmy') >-1){
					image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_wdxm_xm.png")';
				}else if(_class.indexOf('taskflow') >-1){
					image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_rw_wwc.png")';
				}else if(_class.indexOf('taskdelay') >-1){
					image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_delay.png")';
				}else if(_class.indexOf('taskcanceled') >-1){
					image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_canceled.png")';
				}else if(_class.indexOf('taskcompleted') >-1){
					image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_renwu_ywc.png")';
				}else if(_class.indexOf('itemstar') >-1){
					image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_wdxx_1.png")';
				}else if(_class.indexOf('itemmy') >-1){
					image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_wdxm_xm.png")';
				}else if(_class.indexOf('itemdelay') >-1){
					image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_delay.png")';
				}else if(_class.indexOf('itemcompleted') >-1){
					image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_renwu_ywc.png")';
				}else if(_class.indexOf('itemarchived') >-1){
					image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_archived.png")';
				}
				_icon.setStyles({
					"background-image":image_url
				})
			}
		})
		this.navi_layout.getElements('.'+t).setStyles({
			'background-color':'#f2f5f7',
    		'color':'#4a90e2'
		})
		var icon = this.navi_layout.getElements('.'+t).getElement('.icon')
		if(icon){
			if(t == 'taskmy'){
				image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_wdxm_xm_click.png")';
			}else if(t == 'taskflow'){
				image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_rw_wwc_click.png")';
			}else if(t == 'taskdelay'){
				image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_delay_click.png")';
			}else if(t == 'taskcanceled'){
				image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_canceled_click.png")';
			}else if(t == 'taskcompleted'){
				image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_renwu_ywc_click.png")';
			}else if(t == 'itemstar'){
				image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_wdxx_1_blue.png")';
			}else if(t == 'itemmy'){
				image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_wdxm_xm_click.png")';
			}else if(t == 'itemdelay'){
				image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_delay_click.png")';
			}else if(t == 'itemcompleted'){
				image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_renwu_ywc_click.png")';
			}else if(t == 'itemarchived'){
				image_url = 'url("../x_component_TeamWork/$Main/default/icon/icon_archived_click.png")';
			}
			if(t != 'taskall'){
				icon.setStyles({
					"background-image":image_url
				})
			}
			
		}
		
	},

	openTaskView:function(t){
		this.projectContent.empty();
		var task_view_container = new Element("div.",{styles:{
			"position":"relative",
			"margin":"10px 0px 0px 10px",
			"width":"calc(100% - 10px)",
			"height":"calc(100% - 10px)",
			"background-color":"#ffffff"
		}}).inject(this.projectContent)
		MWF.xDesktop.requireApp("TeamWork", "TaskView", function(){
            var p = new MWF.xApplication.TeamWork.TaskView(task_view_container,this,{"type":t} );
            p.load();
        }.bind(this));
	},

	openNavi:function(t){
		MWF.xDesktop.requireApp("TeamWork", "ProjectListV2", function(){
            var p = new MWF.xApplication.TeamWork.ProjectListV2(this.projectContent,this,{"type":t} );
            p.load();
        }.bind(this));
	},
	
	createProject:function(e){ 
        //创建项目
		var explorer = {};
		explorer.app = this;
        MWF.xDesktop.requireApp("TeamWork", "ProjectCreate", function(){ 
            var np = new MWF.xApplication.TeamWork.ProjectCreate(explorer,{},
                {"width": 600,"height": 700,
                    onPostOpen:function(){
                        np.formAreaNode.setStyles({"top":"10px"});
                        var fx = new Fx.Tween(np.formAreaNode,{duration:200});
                        fx.start(["top"] ,"10px", "100px");

                    }.bind(this),
                    onPostClose:function(json){ 
                        //this.reloadLayoutList();
                        if(json && json.data && json.data.id){
                            //this.actions.projectGet(json.data.id,function(jsonr){
                            this.rootActions.ProjectAction.get(json.data.id,function(jsonr){
                                if(jsonr.data){
                                    this.openProject(jsonr.data.id)
                                }
                            }.bind(this));

                        }
                    }.bind(this)
                }
            );
            np.open();
        }.bind(this));

		e.stopPropagation();
    },
	openProject:function(id){ 
        var d = {
            id:id
        }
        MWF.xDesktop.requireApp("TeamWork", "Project", function(){
            var p = new MWF.xApplication.TeamWork.Project(this.content,this,d);
            p.load();
        }.bind(this));
    },
	openStat:function(){
		MWF.xDesktop.requireApp("TeamWork", "Stat", function(){
            var s = new MWF.xApplication.TeamWork.Stat(this.projectContent,this);
            s.load();
        }.bind(this));
	},


	reload:function(){
		this.content.empty();
		this.loadApplication()
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
				//console.log("ttt="+title);
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
			// onHide:function(tip,el){
			// 	//myTips.setTitle("");
			// 	tip.destroy();
			// },
			title:function(){
				return title
			}
		});

		//if you want to add this after init
		myTips.removeEvents('show').addEvent('show', function(tip, el){
			//console.log("ttt="+title)
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
				},
				title:function(){
					return title
				}
			}).fade('in');
		});
	},
	selectPerson: function( type,count,value,callback ) { 
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        this.fireEvent("querySelect", this);
        var options = {
            "type": type,
            "title": "选人",
            "count": count,
            "values": value || [],
            "onComplete": function (items) { 
                var personList = [];
                items.each(function (item) {
                    personList.push(item.data.distinguishedName);

                });
                if(callback)callback(personList);
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.content, options);
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
		var loading = new Element("img",{styles:this.css.loading,"src":"../x_component_TeamWork/$Main/default/icon/loading.gif"}).inject(container);
		//var loading = new Element("img",{"src":"../x_component_TeamWork/$Main/default/icon/loading.gif"}).inject(container);

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
	formatDateV2:function(dstr,t){
		var date = new Date();
		if(dstr){
			date = new Date(dstr)
		}
		
		var year = date.getFullYear(),
			month = date.getMonth()+1,
			day = date.getDate(),
			hour = date.getHours(),
			min = date.getMinutes(),
			sec = date.getSeconds();
		var preArr = Array.apply(null,Array(10)).map(function(elem, index) {
			return '0'+index;
		});
		
		var newTime = year + '-' +
					(preArr[month]||month) + '-' +
					(preArr[day]||day) + ' ' +
					(preArr[hour]||hour) + ':' +
					(preArr[min]||min) + ':' +
					(preArr[sec]||sec);

		if(t == "date"){ 
			newTime = year + '-' +
			(preArr[month]||month) + '-' +
			(preArr[day]||day)
		}
		return newTime;	
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
			window.setTimeout(function(){
				var _h = this.content.getElement(".taskInforContainer").getHeight().toInt();
				if(this.content.getElement(".taskInforContent")){
					this.content.getElement(".taskInforContent").setStyle("height",(_h)+"px");
					
				}
			}.bind(this),500)
			
		}

		//taskGroupItemContainer
		//taskGroupLayout，taskGroupItemContainer  自定义高度

		//bam
		if(this.content.getElement(".bam_mind_container")){
			var container = this.content.getElement(".bam_mind_container");
			var size = this.content.getSize();
			container.setStyles({
				"width":(size.x-200)+"px",
				"height":(size.y-200)+"px",
				"left":"100px",
				"top":"100px"
			})
		}

	},
});

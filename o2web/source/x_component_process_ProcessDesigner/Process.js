MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.ProcessDesigner = MWF.xApplication.process.ProcessDesigner || {};
MWF.APPPD = MWF.xApplication.process.ProcessDesigner;
MWF.require("MWF.widget.Common", null, false);
MWF.xDesktop.requireApp("process.ProcessDesigner", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("process.ProcessDesigner", "Property", null, false);
MWF.xDesktop.requireApp("process.ProcessDesigner", "Activity", null, false);
MWF.xDesktop.requireApp("process.ProcessDesigner", "Route", null, false);
MWF.xApplication.process.ProcessDesigner.Process = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
        "isView": false
	},
	
	initialize: function(paper, process, designer, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_ProcessDesigner/$Process/";
		this.cssPath = "../x_component_process_ProcessDesigner/$Process/"+this.options.style+"/css.wcss";

		this._loadCss();
		
		this.designer = designer;
		this.process = process;
		this.process.projectionData = (process.project) ? JSON.parse(process.project) : null;
		this.paper = paper;

		if(this.designer.application) this.process.applicationName = this.designer.application.name;
		if(this.designer.application) this.process.application = this.designer.application.id;

	//	if(this.designer.category) this.process.processCategory = this.designer.category.data.id;
		
	//	this.process.starterMode = "assign";
		
		this.activityTemplates = null;
		this.routeTemplates = null;

        //activity list
		this.begin = null;
		this.ends = {};
        this.cancels={};
        this.manuals = {};
		this.conditions = {};
        this.choices = {};
		this.splits = {};
        this.parallels = {};
		this.merges = {};
		this.embeds = {};
        this.delays = {};
		this.invokes = {};
        this.services = {};
        this.agents = {};
        this.messages = {};

		this.activitys=[];
		
		this.selectedActivitys = [];
		this.selectedActivityDatas = [];
		
		this.scripts = {};
		this.routes = {};
		this.routeDatas = {};
		
		this.isGrid = true;

        //activity loaded
		this.loadedBegin = false;
		this.loadedEnds = false;
        this.loadedCancels = false;
		this.loadedConditions = false;
        this.loadedChoices = false;
        this.loadedManuals = false;
        this.loadedSplits = false;
        this.loadedParallels = false;
        this.loadedMerges = false;
		this.loadedEmbeds = false;
        this.loadedDelays = false;
		this.loadedInvokes = false;
        this.loadedServices = false;
        this.loadedAgents = false;
        this.loadedMessages = false;
		
		this.isCreateRoute = false;
		this.currentCreateRoute = null;
		
		this.isCopyRoute = false;
		this.currentCopyRoute = null;
		
		this.isBrokenLine = false;
		
		this.isChangeRouteTo = false;
		this.isChangeRouteFrom = false;
		this.currentChangeRoute = null;

		this.unSelectedEvent = true;
		
		this.panel = null;
		this.property = null;

    //    this.isFocus = false;
		
		this.isNewProcess = (this.process.id) ? false : true;
	},
	
	load : function(){
//		if (this.isNewProcess){
//			this.process.createTime = new Date().format('db');
//			this.process.updateTime = new Date().format('db');
//		}
		this.createPropertyPanel();

		this.loadProcessActivitys(function(){
		//	this.loadProcessActivitys();
			this.loadProcessRoutes();
			this.loadActivityRoutes();
			this.loadProcessScripts();
			this.checkLoadRoutes();
			if (this.isNewProcess) this.checkUUID();

            this.fireEvent("postLoad");
		}.bind(this));
		
		this.setEvent();
		this.setMenu();
		this.showProperty();
		this.showEditionInfor();
	},
	checkLoadRoutes: function(){
		Object.each(this.routes, function(route){
			if (!route.loaded) route.load();
		});
	},
	checkUUID: function(){
		//新流程, 此处处理所有元素的id
		this.process.isNewProcess = true;
		var idCount = (this.process.begin) ? 2 : 1;
		idCount += (this.process.endList) ? this.process.endList.length : 0;
		idCount += (this.process.manualList) ? this.process.manualList.length : 0;
		idCount += (this.process.conditionList) ? this.process.conditionList.length : 0;
        idCount += (this.process.choiceList) ? this.process.choiceList.length : 0;
		idCount += (this.process.parallelList) ? this.process.parallelList.length : 0;
		idCount += (this.process.splitList) ? this.process.splitList.length : 0;
		idCount += (this.process.mergeList) ? this.process.mergeList.length : 0;
		idCount += (this.process.embedList) ? this.process.embedList.length : 0;
		idCount += (this.process.invokeList) ? this.process.invokeList.length : 0;
		idCount += (this.process.cancelList) ? this.process.cancelList.length : 0;
        idCount += (this.process.delayList) ? this.process.delayList.length : 0;
        idCount += (this.process.messageList) ? this.process.messageList.length : 0;
        idCount += (this.process.serviceList) ? this.process.serviceList.length : 0;
        idCount += (this.process.routeList) ? this.process.routeList.length : 0;

		this.designer.actions.getId(idCount, function(ids){
			this.checkUUIDs = ids.data;

			//流程ID
			this.process.id = this.checkUUIDs.pop().id;
			this.process.createTime = new Date().format('db');
			this.process.updateTime = new Date().format('db');
			
			for (var i=0; i<this.activitys.length; i++){
				if (this.activitys[i].type!="begin"){
					delete this[this.activitys[i].type+"s"][this.activitys[i].data.id];
				}
				this.activitys[i].data.id = this.checkUUIDs.pop().id;
				this.activitys[i].data.process = this.process.id;
				
				if (this.activitys[i].type!="begin"){
					this[this.activitys[i].type+"s"][this.activitys[i].data.id] = this.activitys[i];
				}
			}
			for (var i=0; i<this.activitys.length; i++){
				this.activitys[i].checkUUID();
			}
		}.bind(this));
	},
	
	loadProcessScripts: function(){
		if (this.process.scriptList){
			this.process.scriptList.each(function(script){
				this.scripts[script.id] = script;
			}.bind(this));
		}
	},
	
	setStyle: function(style){
		this.options.style = style;
		this.reload(this.process);
	},
	reload: function(process){
		debugger;
		//this.process = process;
		this.panel.destroy();
		this.paper.clear();
		this.initialize(this.paper, process, this.designer, this.options);
		this.createPropertyPanel();
		//return false;
		this.loadProcessActivitys(function(){
			this.loadProcessRoutes();
			this.loadActivityRoutes();
			this.checkLoadRoutes();
		//	this.loadProcessRoutes();
		//	this.loadProcessDecisions();
		//	this.loadActivityDecisions();
		}.bind(this));
		this.showProperty();
		this.showEditionInfor();
		if (process && this.designer.options.id != process.id){
			var app = layout.desktop.apps["process.ProcessDesigner"+this.designer.options.id];
			if (app){
				delete layout.desktop.apps["process.ProcessDesigner"+this.designer.options.id];
				this.designer.appId = "process.ProcessDesigner"+process.id;
				layout.desktop.apps[this.designer.appId] = this.designer;
			}
			this.designer.setOptions({"id": process.id});
		}
	},
	setEvent: function(){
		this.paper.canvas.addEvent("selectstart", function(e){e.preventDefault();e.stopPropagation();});

        if (!this.options.isView){
            this.paper.canvas.addEvent("click", function(e){
                if (this.unSelectedEvent){
                    if (this.currentSelected || this.selectedActivitys.length) this.unSelected(e);
                }else{
                    this.unSelectedEvent = true;
                }
            }.bind(this));

            this.paper.canvas.addEvent("mousedown", function(e){
                this.checkCreateRoute(e);
                this.checkSelectMulti(e);
            }.bind(this));
        }

        //this.paper.canvas.addEvents({
        //    "blur": function(){this.isFocus = false;}.bind(this),
        //    "focus": function(){this.isFocus = true;}.bind(this)
        //});
	},
	
	checkSelectMulti: function(e){
		if (!e.rightClick){
			var x = e.event.offsetX;
			var y = e.event.offsetY;
			var els = this.paper.getElementsByPoint(x, y);
			if (!els.length){
				if (!this.isCreateRoute && !this.isCopyRoute){
					this.checkSelectMultiMouseMoveBind = function(e){
						this.checkSelectMultiMouseMove(e, {"x": x, "y": y});
					}.bind(this);
					this.checkSelectMultiMouseUpBind = function(e){
						this.checkSelectMultiStop(e, {"x": x, "y": y});
					}.bind(this);
					this.paper.canvas.addEvent("mousemove", this.checkSelectMultiMouseMoveBind);
					this.paper.canvas.addEvent("mouseup", this.checkSelectMultiMouseUpBind);
				}
			}
		}
	},
	unSelectedAll: function(){
		if (this.currentSelected) this.currentSelected.unSelected();
        this.property.hide();
		this.selectedActivitys.each(function(a){
			a.unSelectedMulti();
		});
		this.selectedActivitys = [];
		this.selectedActivityDatas = [];
	},
	checkSelectMultiMouseMove: function(e, p){
		var pMove = {"x": e.event.offsetX, "y": e.event.offsetY};
		if (MWFRaphael.getPointDistance(p,pMove)>8){
			this.paper.canvas.removeEvent("mousemove", this.checkSelectMultiMouseMoveBind);
			
			if (!this.isCreateRoute && !this.isCopyRoute && !this.isBrokenLine && !this.isChangeRouteTo && !this.isChangeRouteFrom){
				var x = Math.min(p.x, pMove.x);
				var y = Math.min(p.y, pMove.y);
				var width = Math.abs(pMove.x-p.x);
				var height = Math.abs(pMove.y-p.y);
				
				var selectBox = this.paper.rect(x, y, width, height, 0).attr({
					"fill": "#a8caec",
					"stroke": "#3399ff",
					"stroke-width": "0.8",
					"fill-opacity": 0.5
				});
				
				this.beginSelectMultiMouseMoveBind = function(e){
					this.beginSelectMultiMouseMove(e, p, selectBox);
				}.bind(this);
				this.endSelectMultiMouseMoveBind = function(e){
					return this.endSelectMulti(e, p, selectBox);
				}.bind(this);
				
				
				this.unSelectedAll();
				
				this.paper.canvas.addEvent("mousemove", this.beginSelectMultiMouseMoveBind);
				this.paper.canvas.addEvent("mouseup", this.endSelectMultiMouseMoveBind);
			}
		}
	},
	checkSelectMultiStop: function(){
		this.paper.canvas.removeEvent("mousemove", this.checkSelectMultiMouseMoveBind);
	},
	beginSelectMultiMouseMove: function(e, p, rect){
		var pMove = {"x": e.event.offsetX, "y": e.event.offsetY};
		var x = Math.min(p.x, pMove.x);
		var y = Math.min(p.y, pMove.y);
		var width = Math.abs(pMove.x-p.x);
		var height = Math.abs(pMove.y-p.y);
		
//		rect.attr("path", MWFRaphael.getRectPath(x, y, width, height, 0));
		rect.attr({
			//"path", MWFRaphael.getRectPath(x, y, width, height, 0)
			"x": x,
			"y": y,
			"width": width,
			"height": height
		});
		
		this.checkSelectActivity(e, p, rect);
	},
	endSelectMulti: function(e, p, rect){
		rect.remove();
		if (this.selectedActivityDatas.length){
			this.panel.data = this.selectedActivityDatas;
		}
		this.paper.canvas.removeEvent("mousemove", this.beginSelectMultiMouseMoveBind);
		this.paper.canvas.removeEvent("mouseup", this.endSelectMultiMouseMoveBind);
		if (this.selectedActivitys.length){
			this.unSelectedEvent = false;
			window.setTimeout(function(){this.unSelectedEvent = true;}.bind(this), 300);
		}
		return false;
	},
	checkSelectActivity: function(e, p, rect){
		var pMove = {"x": e.event.offsetX, "y": e.event.offsetY};
		var x = Math.min(p.x, pMove.x);
		var y = Math.min(p.y, pMove.y);
		var toX = Math.max(p.x, pMove.x);
		var toY = Math.max(p.y, pMove.y);
		
		this.activitys.each(function(activity){
			var ax = activity.center.x;
			var ay = activity.center.y;
			if (ax>x && ax<toX && ay>y && ay<toY){
				if (!activity.selectedMultiStatus) activity.selectedMulti();
			}else{
				this.selectedActivitys.erase(activity);
				this.selectedActivityDatas.erase(activity.data);
				activity.unSelectedMulti();
			}
		}.bind(this));
		if (this.selectedActivityDatas.length){
			if (this.property) this.property.showMultiActivity(this.selectedActivitys);
			this.panel.propertyTabPage.showTabIm();
			this.panel.data = this.selectedActivityDatas;
		}else{
			this.unSelectedAll();
			this.showProperty();
		}
	},

	showProperty: function(){
		if (!this.property){
			this.property = new MWF.APPPD.Process.Property(this, {
				"onPostLoad": function(){
					this.property.show();
				}.bind(this)
			});
			this.property.load();
		}else{
			this.property.show();
		}
    //    this.isFocus = true;
	},
	showEditionInfor: function(){
		if (this.process.edition){
			if (this.designer.processEditionNode){
				this.designer.processEditionNode.removeEvents("click");
				if (this.process.editionEnable){
					this.designer.processEditionNode.set("text", this.designer.lp.enable);
					this.designer.processEditionNode.addClass("mainColor_bg");
				}else{
					this.designer.processEditionNode.set("text", this.designer.lp.notEnable);
					this.designer.processEditionNode.removeClass("mainColor_bg");

					this.designer.processEditionNode.addEvent("click", function(e){
						this.enableCurrentEdition(e);
					}.bind(this));
				}
			}
			if (this.designer.processEditionInforNode){
				var text = this.designer.lp.currentEdition+": <span class='mainColor_color'>"+this.process.editionNumber+"</span> "+this.designer.lp.editionUpdate+": <span class='mainColor_color'>"+o2.name.cn(this.process.lastUpdatePerson)+" ("+this.process.updateTime+")</span>";
				this.designer.processEditionInforNode.set("html", text);

				this.designer.processEditionInforNode.addEvent("click", function(e){
					this.listEdition(e);
				}.bind(this));
			}
		}
	},
	enableCurrentEdition: function(e){
		var _self = this;
		this.designer.confirm("infor", e, this.designer.lp.edition_list.enabledProcessTitle, {"html": this.designer.lp.edition_list.enabledProcessInfor}, 600, 120, function(){
			_self.save(function(){
				var actions = o2.Actions.load("x_processplatform_assemble_designer").ProcessAction;
				actions.enableProcess(this.process.id, function(json){
					actions.get(this.process.id, function(json){
						this.reload(json.data);
					}.bind(this))
				}.bind(this));
			}.bind(_self));
			this.close();
		},function(){this.close();})

	},

	unSelected: function(e){
		//var els = this.paper.getElementsByPoint(e.event.layerX, e.event.layerY);
		var els = this.paper.getElementsByPoint(e.event.offsetX, e.event.offsetY);
		if (!els.length){
			this.unSelectedAll();
			this.showProperty();
	//		if (this.currentSelected){
	//			this.currentSelected.unSelected();
	//		} 
		}
	},
	setMenu: function(){
		MWF.require("MWF.widget.Menu", function(){
			this.menu = new MWF.widget.Menu(this.paper.canvas, {
				"onQueryShow": function(e){
					//var obj = this.getPointElement(e.event.layerX, e.event.layerY);
					var obj = this.getPointElement(e.event.offsetX, e.event.offsetY);
					switch (obj.type){
						case "activity": 
							this.addActivityMenu(obj.bind);
							break;
						case "route": 
							this.addRouteMenu(obj.bind);
							break;
						default: 
							this.addProcessMenu();
					};
					
				}.bind(this)
			});
			this.menu.load();
		}.bind(this));
	},
	
	addPublicMenu: function(bind, newRoute){
		var newRouteFun = newRoute;
		if (!newRouteFun) newRouteFun = this.createRoute.bind(this);
		
		this.menu.addMenuItem(MWF.APPPD.LP.menu.newRoute, "click", newRouteFun, this.designer.path+""+this.options.style+"/toolbarIcon/"+"newRouter.gif");
		
		if (!this.newActivityMenu){
			MWF.require("MWF.widget.Menu", null, false);
			this.newActivityMenu = new MWF.widget.Menu(this.paper.canvas, {"event": null});
			this.newActivityMenu.load();
			this.newActivityMenu.addMenuItem(MWF.APPPD.LP.menu.newActivityType.manual, "click", this.createManualActivity.bind(this), this.designer.path+""+this.designer.options.style+"/toolbarIcon/"+"manual.gif");
			this.newActivityMenu.addMenuItem(MWF.APPPD.LP.menu.newActivityType.condition, "click", this.createConditionActivity.bind(this), this.designer.path+""+this.designer.options.style+"/toolbarIcon/"+"condition.gif");
			this.newActivityMenu.addMenuItem(MWF.APPPD.LP.menu.newActivityType.auto, "click", this.createAutoActivity.bind(this), this.designer.path+""+this.designer.options.style+"/toolbarIcon/"+"auto.gif");
			this.newActivityMenu.addMenuItem(MWF.APPPD.LP.menu.newActivityType.split, "click", this.createSplitActivity.bind(this), this.designer.path+""+this.designer.options.style+"/toolbarIcon/"+"split.gif");
			this.newActivityMenu.addMenuItem(MWF.APPPD.LP.menu.newActivityType.merge, "click", this.createMergeActivity.bind(this), this.designer.path+""+this.designer.options.style+"/toolbarIcon/"+"merge.gif");
			this.newActivityMenu.addMenuItem(MWF.APPPD.LP.menu.newActivityType.embed, "click", this.createEmbedActivity.bind(this), this.designer.path+""+this.designer.options.style+"/toolbarIcon/"+"embed.gif");
			this.newActivityMenu.addMenuItem(MWF.APPPD.LP.menu.newActivityType.invoke, "click", this.createInvokesActivity.bind(this), this.designer.path+""+this.designer.options.style+"/toolbarIcon/"+"invoke.gif");
			this.newActivityMenu.addMenuItem(MWF.APPPD.LP.menu.newActivityType.begin, "click", this.createBeginActivity.bind(this), this.designer.path+""+this.designer.options.style+"/toolbarIcon/"+"begin.gif");
			this.newActivityMenu.addMenuItem(MWF.APPPD.LP.menu.newActivityType.end, "click", this.createEndActivity.bind(this), this.designer.path+""+this.designer.options.style+"/toolbarIcon/"+"end.gif");
		}
		this.menu.addMenuMenu(MWF.APPPD.LP.menu.newActivity, this.designer.path+""+this.options.style+"/toolbarIcon/"+"newActivity.gif", this.newActivityMenu);
	},
	addActivityMenu: function(bind){
		this.menu.clearItems();
		
		var newRoute = function(){bind.quickCreateRoute();};
		this.addPublicMenu(bind, newRoute);
		
		this.menu.addMenuLine();
		this.menu.addMenuItem(MWF.APPPD.LP.menu.copyActivity, "click", function(e){this.copyActivity(bind);}.bind(this), this.designer.path+""+this.options.style+"/toolbarIcon/"+"copy.png");
		this.menu.addMenuLine();
		this.menu.addMenuItem(MWF.APPPD.LP.menu.deleteActivity, "click", function(e){this.deleteActivity(e, bind);}.bind(this), this.designer.path+""+this.options.style+"/toolbarIcon/"+"deleteActivity.gif");
	},
	addRouteMenu: function(bind){
		this.menu.clearItems();
		
		this.addPublicMenu();
		
		this.menu.addMenuLine();
		this.menu.addMenuItem(MWF.APPPD.LP.menu.deleteRoute, "click", function(e){this.deleteRoute(e, bind);}.bind(this), this.designer.path+""+this.options.style+"/toolbarIcon/"+"deleteRouter.gif");
	},
	addProcessMenu: function(){
		var process = this;
		this.menu.clearItems();
	
		this.addPublicMenu();
	
		this.menu.addMenuLine();
		this.menu.addMenuItem(MWF.APPPD.LP.menu.saveProcess, "click", this.save.bind(this), this.designer.path+""+this.options.style+"/toolbarIcon/"+"save.gif");
		this.menu.addMenuItem(MWF.APPPD.LP.menu.saveProcessNew, "click", this.saveNew.bind(this), this.designer.path+""+this.options.style+"/toolbarIcon/"+"saveNew.gif", true);

		this.menu.addMenuLine();
		
		if (this.isGrid){
			this.menu.addMenuItem(MWF.APPPD.LP.menu.hideGrid, "click", function(){process.switchGrid(this);}, this.designer.path+""+this.options.style+"/toolbarIcon/"+"gridding.gif");
		}else{
			this.menu.addMenuItem(MWF.APPPD.LP.menu.showGrid, "click", function(){process.switchGrid(this);}, this.designer.path+""+this.options.style+"/toolbarIcon/"+"gridding.gif");
		}
		
		this.menu.addMenuLine();
		this.menu.addMenuItem(MWF.APPPD.LP.menu.checkProcess, "click", this.checkProcess.bind(this), this.designer.path+""+this.options.style+"/toolbarIcon/"+"checkProcess.gif", true);
		this.menu.addMenuItem(MWF.APPPD.LP.menu.exportProcess, "click", this.exportProcess.bind(this), this.designer.path+""+this.options.style+"/toolbarIcon/"+"processExplode.gif", true);
		this.menu.addMenuLine();
		this.menu.addMenuItem(MWF.APPPD.LP.menu.printProcess, "click", this.printProcess.bind(this), this.designer.path+""+this.options.style+"/toolbarIcon/"+"print.gif", true);
		
	},
	saveNew: function(e){
		//unrealized
		this.designer.alert("error", e, "", MWF.APPPD.LP.unrealized, 220, 100);
		//layout.notice("error", {x: "right", y:"top"}, MWF.APPPD.LP.unrealized);
	},
	checkProcess: function(e){
		//unrealized
		this.designer.alert("error", e, "", MWF.APPPD.LP.unrealized, 220, 100);
	},
	exportProcess: function(e){
		//unrealized
		this.designer.alert("error", e, "", MWF.APPPD.LP.unrealized, 220, 100);
	},
	printProcess: function(e){
		//unrealized
		this.designer.alert("error", e, "", MWF.APPPD.LP.unrealized, 220, 100);
	},
	saveNewEdition: function(e){
		if (this.process.isNewProcess){
			this.save();
		}else{
			var node = new Element("div", {"styles":this.designer.css.saveNewEditionNode});
			var inforNode = new Element("div", {"html":this.designer.lp.upgradeInfor}).inject(node);
			var descriptionNode = new Element("div", {"styles": this.designer.css.editionDescriptionNode}).inject(node);
			var descriptionTitleNode = new Element("div", {"styles": this.designer.css.descriptionTitleNode, "text": this.designer.lp.editionDiscription}).inject(descriptionNode);
			var descriptionTextAreaNode = new Element("textarea", {"styles": this.designer.css.descriptionTextAreaNode}).inject(descriptionNode);

			var _self = this;
			o2.DL.open({
				"content": node,
				"title": this.designer.lp.upgradeConfirm,
				"offset": {"y": -100},
				"height": 340,
				"width": 580,
				"buttonList": [{
					"type": "ok",
					"text": this.designer.lp.ok,
					"action": function(){
						var textarea = this.content.getElement("textarea");
						var discription = textarea.get("value");
						if (!discription) {
							_self.designer.notice(_self.designer.lp.inputDiscription, "error", descriptionNode);
						}else{
							var checkbox = this.content.getElement("input");
							var enable = (!!checkbox && checkbox.get("checked"));

							_self.doSaveNewEdition(enable, discription);
							this.close();
						}
					}
				},{
					"type": "cancel",
					"text": this.designer.lp.cancel,
					"action": function(){
						this.close();
					}
				}]
			});

			// var _self = this;
			// this.designer.confirm("infor", e, this.designer.lp.upgradeConfirm, {"html": this.designer.lp.upgradeInfor}, 520, 210, function(){
			// 	var checkbox = this.content.getElement("input");
			// 	var enable = (!!checkbox && checkbox.get("checked"));
			// 	_self.doSaveNewEdition(enable);
			// 	this.close();
			// }, function(){
			// 	this.close();
			// });
		}
	},
	doSaveNewEdition: function(enable, description){
		debugger;
		var process = Object.clone(this.process);
		process.editionDes = description;
		var oldIds = [];
		oldIds.push(process.id);
		if (process.begin) oldIds.push(process.begin.id);
		if (process.endList) process.endList.each(function(a){oldIds.push(a.id);});
		if (process.agentList) process.agentList.each(function(a){oldIds.push(a.id);});
		if (process.manualList) process.manualList.each(function(a){oldIds.push(a.id);});
		if (process.conditionList) process.conditionList.each(function(a){oldIds.push(a.id);});
		if (process.choiceList) process.choiceList.each(function(a){oldIds.push(a.id);});
		if (process.parallelList) process.parallelList.each(function(a){oldIds.push(a.id);});
		if (process.splitList) process.splitList.each(function(a){oldIds.push(a.id);});
		if (process.mergeList) process.mergeList.each(function(a){oldIds.push(a.id);});
		if (process.embedList) process.embedList.each(function(a){oldIds.push(a.id);});
		if (process.invokeList) process.invokeList.each(function(a){oldIds.push(a.id);});
		if (process.cancelList) process.cancelList.each(function(a){oldIds.push(a.id);});
		if (process.delayList) process.delayList.each(function(a){oldIds.push(a.id);});
		if (process.messageList) process.messageList.each(function(a){oldIds.push(a.id);});
		if (process.serviceList) process.serviceList.each(function(a){oldIds.push(a.id);});
		if (process.routeList) process.routeList.each(function(a){oldIds.push(a.id);});

		var actions = o2.Actions.load("x_processplatform_assemble_designer");
		this.designer.actions.getId(oldIds.length, function(ids) {
			var checkUUIDs = ids.data;
			var processStr = JSON.encode(process);
			oldIds.each(function(oid, i){
				var reg = new RegExp(oid, "ig");
				processStr = processStr.replace(reg, checkUUIDs[i].id);
			}.bind(this));
			process = JSON.decode(processStr);
			actions.ProcessAction.upgrade(this.process.id, process, function(json){
				var processId = json.data.id;
				if (enable){
					actions.ProcessAction.enableProcess(processId, function(processJson){
						actions.ProcessAction.get(processId, function(processJson){
							this.reload(processJson.data);
						}.bind(this))
					}.bind(this))
				}else{
					actions.ProcessAction.get(processId, function(processJson){
						this.reload(processJson.data);
					}.bind(this))
				}
			}.bind(this));
		}.bind(this));
	},

	listEdition: function(){
		if (this.process.edition){
			if (!this.editionListDlg){
				MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.EditionList", function(){
					this.editionListDlg = new MWF.xApplication.process.ProcessDesigner.widget.EditionList(this.process.application, this.process.edition, this);
					this.editionListDlg.load();
				}.bind(this));
			}else{
				this.editionListDlg.show();
			}
		}else{
			this.designer.notice("infor", this.designer.lp.save_process);
		}
	},
	listEditionDlg: function(editionList){

		//var node = new Element("div", )
	},

	switchGrid: function(item){
		if (this.isGrid){
			this.hideGrid();
		}else{
			this.showGrid();
		}
	},
	
	showGrid: function(){
		this.designer.paperNode.setStyle("background-image", "url("+MWF.defaultPath+"/process/ProcessChart/$Process/"+this.options.style+"/griddingbg.gif)");
		
//		if (this.GridSet){
//			this.GridSet.show();
//		}else{
//			var paperSize = $(this.paper.canvas).getParent().getSize();
//			this.GridSet = this.paper.set();
//			for (var i=20; i<paperSize.x; i=i+20){
//				var lineV = this.paper.path("M"+i+",0L"+i+","+paperSize.y).attr({
//					"stroke": "#ccc",
//					"stroke-dasharray": ".",
//					"stroke-width": 1
//				});
//				lineV.toBack();
//				this.GridSet.push(lineV);
//			}
//			for (var j=20; j<paperSize.y; j=j+20){
//				var lineL = this.paper.path("M0,"+j+"L"+paperSize.x+","+j).attr({
//					"stroke": "#ccc",
//					"stroke-dasharray": ".",
//					"stroke-width": 1
//				});
//				lineL.toBack();
//				this.GridSet.push(lineL);
//			}
//		}
		this.isGrid = true;
	},
	hideGrid: function(){
		this.designer.paperNode.setStyle("background-image", "");
//		if (this.GridSet){
//			this.GridSet.hide();
//		}
		this.isGrid = false;
	},
	
	getPointElement: function(x, y){
		var els = this.paper.getElementsByPoint(x, y);
		var bindObject = null;
		var bindType = "none";
		
		if (els.length){
			
			for (var i=0; i<els.length; i++){
				var bind = els[i].data("bind");
				if (bind){
					if (instanceOf(bind, MWF.APPPD.Activity)){
						bindObject = bind;
						bindType = "activity";
						break;
					}
					if (instanceOf(bind, MWF.APPPD.Route)){
						bindObject = bind;
						bindType = "route";
					}
				};
			}
		}
		return {"bind": bindObject, "type": bindType};
	},
//	loadProcessDecisions: function(){
//		if (this.process.decisionList){
//			this.process.decisionList.each(function(d){
//				this.decisionDatas[d.id] = d;
//			}.bind(this));
//		}
//	},
	loadActivityRoutes: function(){
		this.activitys.each(function(activity){
			activity.loadRoutes();
		});
	},
//	loadActivityDecisions: function(){
//		if (this.begin) this.begin.loadDecisions();
//		for (a in this.ends){
//			this.ends[a].loadDecisions();
//		};
//		for (a in this.manuals){
//			this.manuals[a].loadDecisions();
//		};
//		for (a in this.autos){
//			this.autos[a].loadDecisions();
//		};
//		for (a in this.conditions){
//			this.conditions[a].loadDecisions();
//		};
//		for (a in this.embeds){
//			this.embeds[a].loadDecisions();
//		};
//		for (a in this.invokes){
//			this.invokes[a].loadDecisions();
//		};
//	},
	loadProcessRoutes: function(){

		this.process.routeList.each(function(item){
			this.routes[item.id] = new MWF.APPPD.Route(item, this);
			this.routeDatas[item.id] = item;
		//	this.routes[item.id].load();
		}.bind(this));
	},
	
	createPropertyPanel: function(){
        if (!this.options.isView){
            this.panel = new MWF.APPPD.Process.Panel(this);
            this.panel.load();
        }
	},
	
	loadedActivitys: function(callback){
		if (this.loadedBegin && this.loadedEnds && this.loadedCancels && this.loadedConditions && this.loadedChoices && this.loadedSplits && this.loadedParallels && this.loadedMerges && this.loadedManuals && this.loadedEmbeds && this.loadedDelays && this.loadedInvokes && this.loadedServices && this.loadedAgents && this.loadedMessages){
			if (callback) callback();
		}
	},
	loadProcessActivitys: function(callback){
		this.loadBegin(function(){this.loadedBegin = true; this.loadedActivitys(callback);}.bind(this));
		this.loadEndList(function(){this.loadedEnds = true; this.loadedActivitys(callback);}.bind(this));
        this.loadCancelList(function(){this.loadedCancels = true; this.loadedActivitys(callback);}.bind(this));

		this.loadManualList(function(){this.loadedManuals = true; this.loadedActivitys(callback);}.bind(this));
		this.loadConditionList(function(){this.loadedConditions = true; this.loadedActivitys(callback);}.bind(this));
        this.loadChoiceList(function(){this.loadedChoices = true; this.loadedActivitys(callback);}.bind(this));

        this.loadSplitList(function(){this.loadedSplits = true; this.loadedActivitys(callback);}.bind(this));
        this.loadParallelList(function(){this.loadedParallels = true; this.loadedActivitys(callback);}.bind(this));
        this.loadMergeList(function(){this.loadedMerges = true; this.loadedActivitys(callback);}.bind(this));

        this.loadEmbedList(function(){this.loadedEmbeds = true; this.loadedActivitys(callback);}.bind(this));

		this.loadDelayList(function(){this.loadedDelays = true; this.loadedActivitys(callback);}.bind(this));
		this.loadInvokeList(function(){this.loadedInvokes = true; this.loadedActivitys(callback);}.bind(this));
        this.loadServiceList(function(){this.loadedServices = true; this.loadedActivitys(callback);}.bind(this));
        this.loadAgentList(function(){this.loadedAgents = true; this.loadedActivitys(callback);}.bind(this));
        this.loadMessageList(function(){this.loadedMessages = true; this.loadedActivitys(callback);}.bind(this));
	},
	loadBegin: function(callback){
		var data = this.process["begin"];
		if (data){
			this.begin = new MWF.APPPD.Activity.Begin(data, this);
			this.begin.load(callback);
		}else{
			if (callback) callback();;
		}
		this.activitys.push(this.begin);
	},
	loadEndList: function(callback){
		this.loadActivitys("End", "endList", this.ends, callback);
	},
    loadCancelList: function(callback){
        this.loadActivitys("Cancel", "cancelList", this.cancels, callback);
    },
	loadManualList: function(callback){
		this.loadActivitys("Manual", "manualList", this.manuals, callback);
	},
	loadConditionList: function(callback){
		this.loadActivitys("Condition", "conditionList", this.conditions, callback);
	},
    loadChoiceList: function(callback){
        this.loadActivitys("Choice", "choiceList", this.choices, callback);
    },
	loadSplitList: function(callback){
		this.loadActivitys("Split", "splitList", this.splits, callback);
	},
    loadParallelList: function(callback){
        this.loadActivitys("Parallel", "parallelList", this.parallels, callback);
    },
	loadMergeList: function(callback){
		this.loadActivitys("Merge", "mergeList", this.merges, callback);
	},

	loadEmbedList: function(callback){
		this.loadActivitys("Embed", "embedList", this.embeds, callback);
	},

    loadDelayList: function(callback){
        this.loadActivitys("Delay", "delayList", this.delays, callback);
    },
	loadInvokeList: function(callback){
		this.loadActivitys("Invoke", "invokeList", this.invokes, callback);
	},
    loadServiceList: function(callback){
        this.loadActivitys("Service", "serviceList", this.services, callback);
    },
    loadAgentList: function(callback){
        this.loadActivitys("Agent", "agentList", this.agents, callback);
    },
    loadMessageList: function(callback){
        this.loadActivitys("Message", "messageList", this.messages, callback);
    },
	loadActivitys: function(c, p, children, callback){
		var datas = this.process[p];
		if (datas){
			var count = datas.length;
			var loadedCount = 0;
			if (count){
				datas.each(function(data){
                    this.loadActivity(c, data, children, function(){
                        loadedCount++;
                        if (loadedCount==count){
                            if (callback) callback();
                        }
                    }.bind(this));
				}.bind(this));
			}else{
				if (callback) callback();
			}
		}else{
			if (callback) callback();
		}
	},
    loadActivity: function(c, data, children, callback){
        activity = new MWF.APPPD.Activity[c](data, this);
        activity.load(callback);
		if (c==="begin"){
			this.begin = activity;
		}else{
			children[data.id] = activity;
		}

        this.activitys.push(activity);
    },
	destroy: function(){
		this.paper.remove();
	},
	checkActivityEmptyRouteList: function(activitys){
		if (activitys && activitys.length){
			activitys.each(function(a){
				if (a.routeList) a.routeList = a.routeList.filter(function(n){return !!n;});
			});
		}
	},
	checkEmptyRouteList: function(){
		this.checkActivityEmptyRouteList(this.process.endList);
		this.checkActivityEmptyRouteList(this.process.cancelList);
		this.checkActivityEmptyRouteList(this.process.manualList);
		this.checkActivityEmptyRouteList(this.process.conditionList);
		this.checkActivityEmptyRouteList(this.process.choiceList);
		this.checkActivityEmptyRouteList(this.process.splitList);
		this.checkActivityEmptyRouteList(this.process.parallelList);
		this.checkActivityEmptyRouteList(this.process.mergeList);
		this.checkActivityEmptyRouteList(this.process.embedList);
		this.checkActivityEmptyRouteList(this.process.delayList);
		this.checkActivityEmptyRouteList(this.process.invokeList);
		this.checkActivityEmptyRouteList(this.process.serviceList);
		this.checkActivityEmptyRouteList(this.process.agentList);
		this.checkActivityEmptyRouteList(this.process.messageList);
	},
	save: function(callback){
        if (!this.isSave){
            this.isSave = true;
            //check empty routeList
			this.checkEmptyRouteList();
			var reload = !!this.process.isNewProcess;
            this.designer.actions.saveProcess(this.process, function(responseJSON){
                this.isSave = false;
                this.process.isNewProcess = false;
                this.designer.notice(MWF.APPPD.LP.notice["save_success"], "ok", null, {x: "left", y:"bottom"} );
                this.isNewProcess = false;
                this.designer.options.id = responseJSON.data.id;
				if (reload){
					this.designer.actions.getProcess(responseJSON.data.id, function(json){
						this.reload(json.data);
						if (callback) callback();
					}.bind(this));
				}else{
					if (callback) callback();
				}
            }.bind(this), function(xhr, text, error){
                this.isSave = false;

                var errorText = error+":"+text;
                if (xhr) errorText = xhr.responseText;
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
            }.bind(this));
        }else{
            MWF.xDesktop.notice("info", {x: "right", y:"top"}, this.designer.lp.isSave);
        }

		//this.process
	},
	
	getActivityTemplate: function(callback){
		if (this.activityTemplates){
			if (callback) callback();
		}else{
			var activityTemplateUrl = this.path+"activity.json";
			var r = new Request.JSON({
				url: activityTemplateUrl,
				secure: false,
				async: false,
				method: "get",
				noCache: true,
				onSuccess: function(responseJSON, responseText){
					this.activityTemplates = responseJSON;
					if (callback) callback();
				}.bind(this),
				onError: function(text, error){
					alert(error);
				}
			});
			r.send();
		}
	},
	getRouteTemplates: function(callback){
		if (this.routeTemplates){
			if (callback) callback();
		}else{
			var routeTemplateUrl = this.path+"route.json";
			var r = new Request.JSON({
				url: routeTemplateUrl,
				secure: false,
				async: false,
				method: "get",
				noCache: true,
				onSuccess: function(responseJSON, responseText){
					this.routeTemplates = responseJSON;
					if (callback) callback();
				}.bind(this),
				onError: function(text, error){
					alert(error);
				}
			});
			r.send();
		}
	},
	
	createActivity: function(d, c, position){
		if (d=="begin"){
			if (this.begin){
				this.designer.notice(MWF.APPPD.LP.notice["one_begin"], "error", null, {x: "right", y:"top"});
				return false;
			}
		}
        var activity = null;
		this.getActivityTemplate(function(){
            var activityData = Object.clone(this.activityTemplates[d]);
            //MWF.require("MWF.widget.UUID", function(){
            //activityData.id = (new MWF.widget.UUID()).toString();

            this.designer.actions.getUUID(function(id){activityData.id = id;});

            activityData.process = this.process.id;
            activityData.createTime = new Date().format('db');
            activityData.updateTime = new Date().format('db');

			activityData.position = position.x+","+position.y;
            activity = new MWF.APPPD.Activity[c](activityData, this);
            activity.create(position);

            if (d=="begin"){
                this.begin = activity;
                this.process.begin = activityData;
            }else{
                this[d+"s"][activityData.id] = activity;
                if (!this.process[d+"List"]){
                    this.process[d+"List"] = [];
                }
                this.process[d+"List"].push(activityData);
            }
            this.activitys.push(activity);

            //		}.bind(this));
		}.bind(this));
        return activity;
	},
	
	createManualActivity: function(){
        this.createActivity("manual", "Manual");
	},
	createConditionActivity: function(){
		this.createActivity("condition", "Condition");
	},
	createAutoActivity: function(){
		this.createActivity("auto", "Auto");
	},
	createSplitActivity: function(){
		this.createActivity("split", "Split");
	},
	createMergeActivity: function(){
		this.createActivity("merge", "Merge");
	},
	createEmbedActivity: function(){
		this.createActivity("embed", "Embed");
	},
	createInvokesActivity: function(){
		this.createActivity("invoke", "Invoke");
	},
	createBeginActivity: function(){
		this.createActivity("begin", "Begin");
	},
	createEndActivity: function(){
		this.createActivity("end", "End");
	},
	
	createRoute: function(){
		if (!this.isCopyRoute && !this.isCreateRoute){
			this.getRouteTemplates(function(){
				var routerData = Object.clone(this.routeTemplates.route);
	
				//routerData.id = this.designer.actions.getUUID();
                this.designer.actions.getUUID(function(id){routerData.id = id;});

				routerData.process = this.process.id;
				routerData.createTime = new Date().format('db');
				routerData.updateTime = new Date().format('db');
				var route = new MWF.APPPD.Route(routerData, this);
                route.isBack = true;
				route.load();
                route.set.toBack();
				
				this.beginRouteCreate(route);
	
			}.bind(this));
		}
		
	},
	beginRouteCreate: function(route){
		this.isCreateRoute  = true;
		this.currentCreateRoute = route;
		this.designer.setToolBardisabled("createRoute");
		
		//route.set.toFront();
		
		this.routeCreateFromMouseMoveBind = function(e){
			this.routeCreateFromMouseMove(e);
		}.bind(this);
		this.paper.canvas.addEvent("mousemove", this.routeCreateFromMouseMoveBind);
	},
	routeCreateFromMouseMove: function(e){
		//var x = e.event.layerX.toFloat();
		//var y = e.event.layerY.toFloat();
		var x = e.event.offsetX.toFloat();
		var y = e.event.offsetY.toFloat();
		
		var dx = x - this.currentCreateRoute.beginPoint.x-5;
		var dy = y - this.currentCreateRoute.beginPoint.y+5;
		this.currentCreateRoute.set.transform("t"+dx+","+dy);
	},
	routeCreateFromActivity: function(activity){
		this.paper.canvas.removeEvent("mousemove", this.routeCreateFromMouseMoveBind);
		var route = this.currentCreateRoute;
		route.setActivity(null, activity);
		route.reload();
		
		this.routeCreateToMouseMoveBind = function(e){
			this.routeCreateToMouseMove(e);
		}.bind(this);
		this.paper.canvas.addEvent("mousemove", this.routeCreateToMouseMoveBind);
	},
	routeCreateToMouseMove: function(e){
		//var x = e.event.layerX.toFloat();
		//var y = e.event.layerY.toFloat();
		var x = e.event.offsetX.toFloat();
		var y = e.event.offsetY.toFloat();
		
		var route = this.currentCreateRoute;
		route.tmpEndPoint = {"x": x-3, "y": y-3};
		route.reload();
	},
	routeCreateToActivity: function(activity){
		this.paper.canvas.removeEvent("mousemove", this.routeCreateToMouseMoveBind);
		var route = this.currentCreateRoute;
		route.tmpEndPoint = null;
		route.tmpBeginPoint = null;
		route.setActivity(activity, null);

		route.isBack = false;
		route.reload();
		
		activity.shap.attr(activity.style.shap);
		
		this.endRouteCreate();
	},
	endRouteCreate: function(){
		var route = this.currentCreateRoute;
		route.selected();
		
		this.isCreateRoute  = false;
		this.currentCreateRoute = null;

		route.setListItemData();
		this.designer.setToolBardisabled("decision");
		
		this.setNewRouteProcessData(route);
	},
	setNewRouteProcessData: function(route){
		this.routes[route.data.id] = route;
		this.process.routeList.push(route.data);
		
		route.fromActivity.setRouteData(route.data.id);
//		if (!route.fromActivity.data.routeList) route.fromActivity.data.routeList = [];
//		route.fromActivity.data.routeList.push(route.data.id);
		
		route.data.activity = route.toActivity.data.id;
		route.data.activityType = route.toActivity.type;
	},
	routeCreateCancel: function(){
		var route = this.currentCreateRoute;
		if (route.fromActivity){
			route.fromActivity.routes.erase(route);
		}
		route.destroy();
		delete route;
		
		this.isCreateRoute  = false;
		this.currentCreateRoute = null;
		
		if (this.routeCreateFromMouseMoveBind) this.paper.canvas.removeEvent("mousemove", this.routeCreateFromMouseMoveBind);
		if (this.routeCreateToMouseMoveBind) this.paper.canvas.removeEvent("mousemove", this.routeCreateToMouseMoveBind);
	},
	checkCreateRoute: function(e){
		if (this.isCreateRoute || this.isCopyRoute){
			if (e.rightClick){
				if (this.isCreateRoute){
					this.routeCreateCancel();
				}
				if (this.isCopyRoute){
					this.routeAddCancel();
				}
			}
			if (this.menu) this.menu.pause(1);
		}
	},
	
	clearSelected: function(){
		this.begin.unSelectActivity();
		for (a in this.ends) this.ends[a].unSelected();
		for (a in this.conditions) this.conditions[a].unSelected();
		for (a in this.autos) this.autos[a].unSelected();
		for (a in this.manuals) this.manuals[a].unSelected();
		for (a in this.embeds) this.embeds[a].unSelected();
		for (a in this.invokes) this.invokes[a].unSelected();
	},
	
	copyRoute: function(route){
		if (!this.isCopyRoute && !this.isCreateRoute){
			var newRouteData = Object.clone(route.data);
			
			//newRouteData.id = Raphael.createUUID();
			//newRouteData.id = this.designer.actions.getUUID();
            this.designer.actions.getUUID(function(id){newRouteData.id = id;});

			var route = new MWF.APPPD.Route(newRouteData, this);
			route.load();
			route.isBack = true;
				
			this.isCopyRoute = true;
			this.currentCopyRoute = route; 
			
			this.beginRouteCopy(route);
		}
	},
	
	beginRouteCopy: function(route){
		this.routeCopyMouseMoveBind = function(e){
			this.copyRouteMouseMove(e, route);
		}.bind(this);
		this.paper.canvas.addEvent("mousemove", this.routeCopyMouseMoveBind);
	},
	copyRouteMouseMove: function(e, route){
		route.tmpBeginPoint = {"x": e.event.offsetX-5, "y": e.event.offsetY-5};
		route.reload();
	},
	routeAddFromActivity: function(activity){
		var route = this.currentCopyRoute;
		this.paper.canvas.removeEvent("mousemove", this.routeCopyMouseMoveBind);
		route.setActivity(null, activity);
		
		route.isBack = false;
		route.reload();
		
		activity.shap.attr(activity.style.shap);

		this.endRouteCopy();
	},
	endRouteCopy: function(){
		var route = this.currentCopyRoute;
		route.selected();
		
		this.isCopyRoute  = false;
		this.currentCopyRoute = null;

		route.setListItemData();
		this.designer.setToolBardisabled("decision");
		
		this.setCopyRouteProcessData(route);
	},
	routeAddCancel: function(){
		var route = this.currentCopyRoute;
		route.destroy();
		delete route;
		
		this.isCopyRoute  = false;
		this.currentCopyRoute = null;
		
		if (this.routeAddMouseMoveBind) this.paper.canvas.removeEvent("mousemove", this.routeAddMouseMoveBind);
	},
	setCopyRouteProcessData: function(route){
		this.process.routeList.push(route.data);
		
		route.fromActivity.setRouteData(route.data.id);
	//	if (!route.fromActivity.data.routeList) route.fromActivity.data.routeList = [];
	//	route.fromActivity.data.routeList.push(route.data.id);
		
		this.routeDatas[route.data.id] = route.data;
	},
	deleteRoute: function(e, route){
        var _self = this;
        this.designer.shortcut = false;
		this.designer.confirm("warn", e, MWF.APPPD.LP.notice.deleteRouteTitle, MWF.APPPD.LP.notice.deleteRoute, 300, 120, function(){
			route.destroy();
    		delete route;
            _self.designer.shortcut = true;
    		this.close();
		}, function(){
            _self.designer.shortcut = true;
			this.close();
		}, null);
	},
	copyActivity: function(activity){
		var activityData = Object.clone(activity.data);
		var type = activity.type;
		var c = type.capitalize();

		//activityData.id = this.designer.actions.getUUID();
        this.designer.actions.getUUID(function(id){activityData.id = id;});

		activityData.process = this.process.id;
		
		activity = new MWF.APPPD.Activity[c](activityData, this);
		activity.create();
		activity.selected();
		
		if (type=="begin"){
			this.begin = activity;
			this.process.begin = activityData;
		}else{
			this[type+"s"][activityData.id] = activity;
			if (!this.process[type+"List"]){
				this.process[type+"List"] = [];
			}
			this.process[type+"List"].push(activityData);
		}

	},
	deleteActivity: function(e, activity){
        var _self = this;
        this.designer.shortcut = false;
		this.designer.confirm("warn", e, MWF.APPPD.LP.notice.deleteActivityTitle, MWF.APPPD.LP.notice.deleteActivity, 300, 120, function(){
			activity.destroy();
    		delete activity;
            _self.designer.shortcut = true;
    		this.close();
		}, function(){
            _self.designer.shortcut = true;
			this.close();
		}, null);
	},
    explode: function(){
    //    this._getFormData();
        MWF.require("MWF.widget.Base64", null, false);
        var data = MWF.widget.Base64.encode(JSON.encode(this.process));

        MWF.require("MWF.widget.Panel", function(){
            var node = new Element("div");
            //var size = this.designer.formNode.getSize();
            var position = this.designer.paperNode.getPosition(this.designer.paperNode.getOffsetParent());

            var textarea = new Element("textarea", {
                "styles": {
                    "border": "1px solid #999",
                    "width": "770px",
                    "margin-left": "14px",
                    "margin-top": "14px",
                    "height": "580px"
                },
                "text": JSON.encode(this.process)
            }).inject(node);


            this.explodePanel = new MWF.widget.Panel(node, {
                "style": "form",
                "isResize": false,
                "isMax": false,
                "title": "",
                "width": 800,
                "height": 660,
                "top": position.y,
                "left": position.x+3,
                "isExpand": false,
                "target": this.designer.node
            });

            this.explodePanel.load();
        }.bind(this));

    }
	
});

MWF.xApplication.process.ProcessDesigner.Process.Panel = new Class({
	initialize: function(process){
		this.process = process;
		this.width = 370;
		this.top = 0;

		var paperSize = this.process.designer.paperNode.getSize();
		this.left = (paperSize.x.toFloat())-376;
		
		this.height = (paperSize.y.toFloat())-6;
		
		this.stopParseJson = false;
	},
	load: function(){
		this.panelNode = new Element("div");
		this.createModuleListTab();
		this.createPropertyTab();
		this.createPanelResizeNode();
		
		this.moduleTabContent.inject(this.panelNode);
		this.panelResizeNode.inject(this.panelNode);
		this.propertyTabContent.inject(this.panelNode);
		
		MWF.require("MWF.widget.Panel", function(){
			
			this.modulePanel = new MWF.widget.Panel(this.panelNode, {
				"title": MWF.APPPD.LP.property,
				"isClose": false,
				"target": this.process.designer.paperNode,
				"height": this.height,
				"width": this.width,
				"left": this.left,
				"top": this.top,
				"transition": Fx.Transitions.linear.easeIn,
				"transitionOut": Fx.Transitions.linear.easeOut,
				"duration": 100,
				"onResize": function(){
					this.setPanelSize(this.panelModulePercent);
				}.bind(this)
			});
			this.modulePanel.load();
			
			this.setPanelSize(this.panelModulePercent);
			
		}.bind(this));
		
	},
	
	setPanelSize: function(percent){
		var contentSize = this.modulePanel.content.getSize();
		var resizeSize = this.panelResizeNode.getSize();
		var resizeMarginTop = this.panelResizeNode.getStyle("margin-top");
		var resizeMarginBottom = this.panelResizeNode.getStyle("margin-bottom");
		
		var useHeight = (contentSize.y.toFloat()) - (resizeSize.y.toFloat()) - (resizeMarginTop.toFloat()) - (resizeMarginBottom.toFloat());
		
		var p = percent;
		if (!p) p = 0.3;
		var moduleHeight = useHeight*p;
		var propertyHeight = useHeight - moduleHeight;
		
		this.moduleListContent.setStyle("height", moduleHeight);
		if (!this.propertyPanel) this.propertyListContent.setStyle("height", propertyHeight);
		
		var moduleListTabSize = this.moduleListTab.tabNodeContainer.getSize();
		
		this.moduleListTab.pages.each(function(page){
			var topMargin = page.contentNodeArea.getStyle("margin-top").toFloat();
			var bottomMargin = page.contentNodeArea.getStyle("margin-bottom").toFloat();
			
			var tabContentNodeAreaHeight = moduleHeight - topMargin - bottomMargin - moduleListTabSize.y.toFloat()-2;
			page.contentNodeArea.setStyle("height", tabContentNodeAreaHeight);
		}.bind(this));
		
		if (!this.propertyPanel) {
			var propertyListTabSize = this.propertyListTab.tabNodeContainer.getSize();
			this.propertyListTab.pages.each(function(page){
				var topMargin = page.contentNodeArea.getStyle("margin-top").toFloat();
				var bottomMargin = page.contentNodeArea.getStyle("margin-bottom").toFloat();
				
				var tabContentNodeAreaHeight = propertyHeight - topMargin - bottomMargin - propertyListTabSize.y.toFloat()-2;
				page.contentNodeArea.setStyle("height", tabContentNodeAreaHeight);
			}.bind(this));
		}
//		var contentNodeContainerHeight = moduleHeight - moduleListTabSize.y.toFloat()-2;
//		this.moduleListTab.contentNodeContainer.setStyle("height", contentNodeContainerHeight);
//		
//		contentNodeContainerHeight = propertyHeight - propertyListTabSize.y.toFloat()-2;
//		this.propertyListTab.contentNodeContainer.setStyle("height", contentNodeContainerHeight);
		
		if (this.jsonStringConfirmNode)this.setJsonStringConfirmNodePosition();
	},
	createPanelResizeNode: function(){
		this.panelResizeNode = new Element("div", {
			"styles": this.process.css.panelResizeNode
		});
		
		this.panelResizeNode.addEvent("mousedown", function(e){
			this.beginPanelResize(e);
		}.bind(this));
	},
	beginPanelResize: function(){
		this.panelResizeMouseMoveBind = function(e){
			this.panelResize(e);
		}.bind(this);
		this.panelResizeMouseUpBind = function(){
			$(document.body).removeEvent("selectstart",this.panelResizeSelecttBind);
			$(document.body).removeEvent("mousemove",this.panelResizeMouseMoveBind);
			$(document.body).removeEvent("mouseup",this.panelResizeMouseUpBind);
		}.bind(this);
		this.panelResizeSelecttBind = function(){
			return false;
		}.bind(this);
		
		$(document.body).addEvent("selectstart",this.panelResizeSelecttBind);
		$(document.body).addEvent("mousemove",this.panelResizeMouseMoveBind);
		$(document.body).addEvent("mouseup",this.panelResizeMouseUpBind);
	},
	panelResize: function(e){
		var y = e.event.pageY;
		var modulePosition = this.moduleListContent.getPosition();

		var moduleHeight = (y.toFloat()) - (modulePosition.y.toFloat());
		if (moduleHeight<40) moduleHeight = 40;
		
		var contentSize = this.modulePanel.content.getSize();
		var resizeSize = this.panelResizeNode.getSize();
		var resizeMarginTop = this.panelResizeNode.getStyle("margin-top");
		var resizeMarginBottom = this.panelResizeNode.getStyle("margin-bottom");
		
		var useHeight = (contentSize.y.toFloat()) - (resizeSize.y.toFloat()) - (resizeMarginTop.toFloat()) - (resizeMarginBottom.toFloat());
		
		var propertyHeight = useHeight - moduleHeight;
		
		if (propertyHeight<40){
			propertyHeight = 40;
			moduleHeight = useHeight - propertyHeight;
		} 
		this.moduleListContent.setStyle("height", moduleHeight);
		this.propertyListContent.setStyle("height", propertyHeight);
		
		var moduleListTabSize = this.moduleListTab.tabNodeContainer.getSize();
		var propertyListTabSize = this.propertyListTab.tabNodeContainer.getSize();
		
		this.moduleListTab.pages.each(function(page){
			var topMargin = page.contentNodeArea.getStyle("margin-top").toFloat();
			var bottomMargin = page.contentNodeArea.getStyle("margin-bottom").toFloat();
			
			var tabContentNodeAreaHeight = moduleHeight - topMargin - bottomMargin - (moduleListTabSize.y.toFloat())-2;
			page.contentNodeArea.setStyle("height", tabContentNodeAreaHeight);
		}.bind(this));
		
		this.propertyListTab.pages.each(function(page){
			var topMargin = page.contentNodeArea.getStyle("margin-top").toFloat();
			var bottomMargin = page.contentNodeArea.getStyle("margin-bottom").toFloat();
			
			var tabContentNodeAreaHeight = propertyHeight - topMargin - bottomMargin - propertyListTabSize.y.toFloat()-2;
			page.contentNodeArea.setStyle("height", tabContentNodeAreaHeight);
		}.bind(this));
		
//		var contentNodeContainerHeight = moduleHeight - moduleListTabSize.y.toFloat()-2;
//		this.moduleListTab.contentNodeContainer.setStyle("height", contentNodeContainerHeight);
//		
//		contentNodeContainerHeight = propertyHeight - propertyListTabSize.y.toFloat()-2;
//		this.propertyListTab.contentNodeContainer.setStyle("height", contentNodeContainerHeight);
		
		if (this.jsonStringConfirmNode)this.setJsonStringConfirmNodePosition();
		
		this.panelModulePercent = moduleHeight.toFloat()/useHeight.toFloat();
	},
	loadJson: function(json){
	//	MWF.require("MWF.widget.MaskNode", function(){
	//		this.jsonMarkNode = new MWF.widget.MaskNode(this.jsonObjectNode, {"zIndex": 30000});
	//		this.jsonMarkNode.load();
        MWF.require("MWF.widget.JsonParse", function(){
            this.jsonParse = new MWF.widget.JsonParse(json, this.jsonObjectNode, this.jsonStringNode);
            this.jsonParse.load();
        }.bind(this));

			// this.jsonParse = new MWF.APPPD.Process.JsonParse(json, this.jsonObjectNode, this.jsonStringNode);
			// window.setTimeout(function(){
			// 	this.jsonParse.load();
			// }.bind(this), 1);
	//	}.bind(this));
	},
    loadJsonString: function(json){
        // o2.load("JSBeautifier_html", function(){
        //     htmlNode.set("text", html_beautify(copy.outerHTML, {"indent_size":1}));
        // }.bind(this));

        this.jsonStringNode.set("text", JSON.stringify(json,null,2));

		o2.require("o2.widget.ace", function(){
			MWF.widget.ace.load(function(){
				COMMON.AjaxModule.loadDom("../o2_lib/ace/src-min-noconflict/ext-static_highlight.js", function(){
					var highlight = ace.require("ace/ext/static_highlight");
					highlight(this.jsonStringNode, {mode: "ace/mode/json", theme: "ace/theme/tomorrow", "fontSize": 16});
				}.bind(this));
			}.bind(this));
		}.bind(this));
	},
	clearJson: function(){
		this.json = null;
		this.jsonString = "";
		this.jsonStringNode.set("text", "");
		this.jsonObjectNode.empty();
		if (this.jsonParse) this.jsonParse = null;
		
	//	if (this.jsonMarkNode) this.jsonMarkNode.hide();
	},
	createJsonStringNode: function(){
		var jsonStringNode = new Element("div", {
		//	"readonly": true,
			"styles": {
				"width": "100%",
				"height": "100%",
				"overflow": "auto",
				"border": "0px"
			}
		});
	//	jsonStringNode.set("text", this.jsonString);
		return jsonStringNode;
	},
	createJsonObjectNode: function(){
		this.jsonObjectNode = new Element("div", {
			"styles": {
				//"overflow": "hideen",
				"margin-top": "0px",
				"height": "auto"
			}
		});
	//	this.loadObjectTree();
		return this.jsonObjectNode;
	},
	
	createJsonStringConfirmNode: function(){
		this.jsonStringConfirmNode = new Element("div", {
			"styles": {
				"width": "20px",
				"height": "20px",
				"background-color": "#EEE",
				"background": "url("+MWF.defaultPath+"/process/ProcessChart/$Process/"+this.process.options.style+"/checkmark.png"+") no-repeat center center",
				"position": "absolute",
				"cursor": "pointer",
				"display": "none"
			},
			"events": {
				"mouseover": function(){
					this.store("flag", true);
				},
				"mouseout": function(){
					this.store("flag", false);
				},
				"click": function(){
					this.checkJsonStringAndReload();
				}.bind(this)
			}
		}).inject(this.jsonStringNode, "after");
	},
	checkJsonStringAndReload: function(){
		if (!this.process.selectedActivitys.length){
			try {
				var data = JSON.decode(this.jsonStringNode.value);
				if (data){
					if (this.process.currentSelected){
						Object.copy(data, this.process.currentSelected.data);
						this.process.currentSelected.redraw();
					}else{
						data.id = this.process.process.id;
						data.processCategory = this.process.process.processCategory;
						this.process.reload(data);
					}
				}
			}catch(e){
				this.designer.notice(e.message, "error", this.jsonStringNode.getParent(), {x: "left", y:"top"});
			}
		}
	},
	setJsonStringConfirmNodePosition: function(){
		var p = this.jsonStringNode.getPosition(this.panelNode);
		var size = this.panelNode.getSize();
		this.jsonStringConfirmNode.setStyles({
			"display": "block",
			"top" : p.y+4,
			"left" : size.x-26
		});
	},
	
	createPropertyTab: function(){
		this.propertyTabContent = new Element("div");
		this.propertyListContent = new Element("div", {
			"styles": this.process.css.propertyListContent
		}).inject(this.propertyTabContent);
		this.propertyListNode = new Element("div", {
			"styles": this.process.css.propertyListNode
		});
		this.process.propertyListNode = this.propertyListNode;
		
		this.jsonObjectNode = this.createJsonObjectNode();
		this.jsonStringNode = this.createJsonStringNode();
		this.jsonStringNode.addEvents({
			"focus": function(){
				if (!this.process.selectedActivitys.length){
					if (!this.jsonStringConfirmNode) this.createJsonStringConfirmNode();
					this.setJsonStringConfirmNodePosition();
				}
			}.bind(this),
			"blur": function(e){
				if (this.jsonStringConfirmNode){
					if (!this.jsonStringConfirmNode.retrieve("flag")) this.jsonStringConfirmNode.setStyle("display", "none");
				} 
			}.bind(this)
		});

		MWF.require("MWF.widget.Tab", function(){
			this.propertyListTab = new MWF.widget.Tab(this.propertyListContent, {"style": "moduleList"});
			this.propertyListTab.load();
			this.propertyTabPage = this.propertyListTab.addTab(this.propertyListNode, MWF.APPPD.LP.property, false);
			this.objectTabPage = this.propertyListTab.addTab(this.jsonObjectNode, "JSON", false);
			this.stringTabPage = this.propertyListTab.addTab(this.jsonStringNode, "Text", false);

			var div = new Element("div", {
				"styles": {"float": "right", "margin-right": "10px"},
				"html": "<span>"+MWF.APPPD.LP.showAdvanced+"</span>"
			}).inject(this.propertyListTab.tabNodeContainer);
			div.getElement("span").addEvents({
				"mousedown": function(e){ e.stopPropagation(); },
				"click": function(e){
					this.showAdvanced.click();
					e.stopPropagation();
				}.bind(this)
			});

			o2.UD.getDataJson("process-show-advanced", function(json){
				this.showAdvanced = new Element("input", {
					"type": "checkbox",
					"checked": (!json) ? false : json.show,
					"events": {
						"mousedown": function(e){ e.stopPropagation(); },
						"change": function(){
							if (this.showAdvanced.checked){
								var advs = this.propertyListNode.querySelectorAll("*[data-o2-advanced=\"yes\"]");
								if (advs && advs.length){
									for (var i=0; i<advs.length; i++){
										advs[i].show();
									}
								}
							}else{
								var advs = this.propertyListNode.querySelectorAll("*[data-o2-advanced=\"yes\"]");
								if (advs && advs.length){
									for (var i=0; i<advs.length; i++){
										advs[i].hide();
									}
								}
							}
							o2.UD.putData("process-show-advanced", {"show": !!this.showAdvanced.checked})
						}.bind(this)
					}
				}).inject(div, "top");
			}.bind(this));


			// this.propertyListTab.tabNodeContainerArea
			// showAdvanced
			
			this.process.setScrollBar(this.propertyTabPage.contentNodeArea, "small", null, null);
			this.process.setScrollBar(this.objectTabPage.contentNodeArea, "small", null, null);
			this.process.setScrollBar(this.stringTabPage.contentNodeArea, "small", null, null);
			
			this.objectTabPage.setOptions({
				"onShow": function(){
					this.loadJson(this.data);
				}.bind(this),
				"onHide": function(){
					this.clearJson();
				}.bind(this)
			});
			this.stringTabPage.setOptions({
				"onShow": function(){
					this.loadJsonString(this.data);
				}.bind(this),
				"onHide": function(){
					this.clearJson();
				}.bind(this)
			});

			this.propertyTabPage.showTab();

			this.propertyListTab.tabNodeContainer.addEvent("mousedown", function(event){
				//event.stop();
				this.propertyTabMove(event);
			}.bind(this));
			
			
		//	this.propertyDrag = new Drag(this.propertyTabContent, {
		//		"handle": this.propertyListTab.tabNodeContainer,
		//		"snap": 10,
		//		"onStart": function(el, e){
					
		//		}.bind(this)
		//	});
			
			
		}.bind(this), false);
		
	//	this.propertyListContent.setStyle("height", 300);
	},
	
	propertyTabMove: function(event){
//		var tmpContent = this.propertyListContent.clone().setStyles(this.propertyListContent.getCoordinates()).setStyles({
//			"opacity": 0.7,
//			"border": "1px dashed #CCC",
//			"z-index": this.modulePanel.container.getStyle("z-index").toInt()+1,
//			"position": "absolute"
//	    }).inject(this.process.designer.paperNode);
		
		var size = this.propertyListContent.getSize();
		var tmpContent = new Element("div", {
			"styles": {
				"opacity": 0.7,
				"border": "1px dashed #CCC",
				"z-index": this.modulePanel.container.getStyle("z-index").toInt()+1,
				"width": size.x,
				"height": size.y,
				"background-color": "#EEE",
				"position": "absolute"
			}			
		}).inject(this.process.designer.paperNode);
		tmpContent.position({
			relativeTo: this.propertyListContent,
		    position: 'upperLeft',
		    edge: 'upperLeft'
		});
		
		var drag = new Drag.Move(tmpContent, {
			"droppables": [this.process.designer.paperNode, this.panelNode],
			"onEnter": function(dragging, inObj){
				if (this.propertyPanel){
					if (this.panelNode==inObj){
						dragging.tween('border', "4px dashed #666");
					}else{
						dragging.tween('border', "1px dashed #CCC");
					}
				}else{
					if (this.panelNode==inObj){
						dragging.tween('border', "1px dashed #CCC");
					}else{
						dragging.tween('border', "4px dashed #666");
					}
				}
			}.bind(this),
			"onLeave": function(dragging, paper){
				dragging.tween('border', "1px dashed #CCC");
			},
			"onDrop": function(dragging, inObj){
				if (this.panelNode!=inObj){
					this.propertyOut(dragging);
				}else{
					this.propertyIn();
				}
				dragging.destroy();
			}.bind(this),
			"onCancel": function(dragging){
				dragging.destroy();
			}
		});
		
		drag.start(event);
	},
	
	propertyOut: function(dragging){
		if (!this.propertyPanel){
			var coordinates = dragging.getCoordinates();		
			var p = this.process.designer.paperNode.getPosition();
			
			var propertyPanelNode = new Element("div");
			this.propertyListContent.inject(propertyPanelNode);
			
			MWF.require("MWF.widget.Panel", function(){
				
				this.propertyPanel = new MWF.widget.Panel(propertyPanelNode, {
					"title": MWF.APPPD.LP.property,
					"isClose": false,
					"target": this.process.designer.paperNode,
					"height": coordinates.height,
					"width": coordinates.width,
					"left": coordinates.left.toFloat() - p.x.toFloat(),
					"top": coordinates.top.toFloat() - p.y.toFloat(),
					"onResize": function(){
						this.setPropertyPanelSize();
					}.bind(this)
				});
				this.propertyPanel.load();
				
				this.propertyOutSetHeight();
				
				this.setPropertyPanelSize(this.panelModulePercent);
				
			}.bind(this));
		};
	},
	setPropertyPanelSize: function(){
		var contentSize = this.propertyPanel.content.getSize();
		var propertyHeight = contentSize.y;
		this.propertyListContent.setStyle("height", propertyHeight);

		var propertyListTabSize = this.propertyListTab.tabNodeContainer.getSize();
		this.propertyListTab.pages.each(function(page){
			var topMargin = page.contentNodeArea.getStyle("margin-top").toFloat();
			var bottomMargin = page.contentNodeArea.getStyle("margin-bottom").toFloat();
			
			var tabContentNodeAreaHeight = propertyHeight - topMargin - bottomMargin - propertyListTabSize.y.toFloat()-2;
			page.contentNodeArea.setStyle("height", tabContentNodeAreaHeight);
		}.bind(this));
	},
	propertyOutSetHeight: function(){
		this.panelResizeNode.setStyles(this.process.css.panelResizeNodeHide);
		this.panelModulePercent = "1";
		this.setPanelSize(this.panelModulePercent);
	},
	
	propertyIn: function(){
		this.propertyListContent.inject(this.propertyTabContent);
		if (this.propertyPanel) this.propertyPanel.closePanel();
		this.propertyPanel = null;
		this.propertyInSetHeight();
	},
	
	propertyInSetHeight: function(){
		this.panelResizeNode.setStyles(this.process.css.panelResizeNode);
		this.panelModulePercent = "0.3";
		this.setPanelSize(this.panelModulePercent);
	},
	
	createModuleListTab: function(){
		this.moduleTabContent = new Element("div");
		this.moduleListContent = new Element("div", {
			"styles": this.process.css.moduleListContent
		}).inject(this.moduleTabContent);
		
		this.activityListNode = new Element("div", {
			"styles": this.process.css.activityListNode
		});
		this.process.activityListNode = this.activityListNode;
		this.activityTable = new HtmlTable({
		    "properties": this.process.css.activityListTable
		}).inject(this.activityListNode);
		this.process.activityTable = this.activityTable;

		this.routeListNode = new Element("div", {
			"styles": this.process.css.routeListNode
		});
		this.process.routeListNode = this.routeListNode;
		this.routeTable = new HtmlTable({
		    "properties": this.process.css.routeListTable
		}).inject(this.routeListNode);
		this.process.routeTable = this.routeTable;
		
		MWF.require("MWF.widget.Tab", function(){
			this.moduleListTab = new MWF.widget.Tab(this.moduleListContent, {"style": "moduleList"});
			this.moduleListTab.load();
			
		//	this.process.setScrollBar(this.moduleListTab.contentNodeContainer, null, null, null);

			var activityTabPage = this.moduleListTab.addTab(this.activityListNode, MWF.APPPD.LP.activity, false);
			this.process.setScrollBar(activityTabPage.contentNodeArea, "small", null, null);
			
			var routeTabPage = this.moduleListTab.addTab(this.routeListNode, MWF.APPPD.LP.route, false);
			this.process.setScrollBar(routeTabPage.contentNodeArea, "small", null, null);
			
			activityTabPage.showTab();

		}.bind(this), false);
	},
	destroy: function(){
		if (this.modulePanel) this.modulePanel.destroy();
		if (this.propertyPanel) this.propertyPanel.destroy();
	}
});

MWF.xApplication.process.ProcessDesigner.Process.Property = new Class({
	Implements: [Options, Events],
	Extends: MWF.APPPD.Property,
	initialize: function(process, options){
		this.setOptions(options);
		this.process = process;
		this.paper = this.process.paper;
		this.data = process.process;
		this.htmlPath = "../x_component_process_ProcessDesigner/$Process/process.html";
	}
});

MWF.xApplication.process.ProcessDesigner.Process.JsonParse = new Class({
	initialize: function(json, jsonObjectNode, jsonStringNode){
		this.json = json;
		this.jsonObjectNode = jsonObjectNode;
		this.jsonStringNode = jsonStringNode;
		this.stopParseJson = false;
	},
	load: function(){
		this.jsonString = JSON.encode(this.json);
	//	this.jsonStringNode.set("text", JSON.format(this.json));
		this.loadObjectTree();
	},
	loadObjectTree: function(){
		if (this.objectTree){
			this.objectTree.node.destroy();
			this.objectTree = null;
		} 
		MWF.require("MWF.widget.Tree", function(){
			this.objectTree = new MWF.widget.Tree(this.jsonObjectNode, {"style": "jsonview"});
			this.objectTree.load();
			
			var str = this.parseJsonObject(0, this.objectTree, "",  "JSON", this.json, true);
			var jsonStr = str.substring(0, str.length-2);
			if (!this.stopParseJson){
				this.jsonStringNode.set("text", jsonStr);
			}else{
				this.stopParseJson = false;
			}
			
		}.bind(this));
	},
	
	parseJsonObject: function(level, treeNode, title, p, v, expand){
		if (this.stopParseJson){
		//	alert(this.stopParseJson);
			return false;
		}
		var o = {
			"expand": expand,
			"title": "",
			"text": "",
			"action": "",
			"icon": ""
		};
		var tab = "";
		for (var i=0; i<level; i++) tab+="\t";
		//var title = title;
		if (title) title="\""+title+"\": ";
		var jsonStr = "";
		var nextLevel = level+1;
		
		switch (typeOf(v)){
			case "object":
				o.text = p;
				o.icon = "object.png";
				var node = treeNode.appendChild(o);
								
				var jsonStrBegin = tab+title+"{";
				var jsonStrEnd = tab+"}";
				for (i in v){
					jsonStr += this.parseJsonObject(nextLevel, node, i, i, v[i], false);
				}
				jsonStr = jsonStrBegin+"\n"+jsonStr.substring(0, jsonStr.length-2)+"\n"+jsonStrEnd+",\n";
				break;
				
			case "array":
				o.text = p;
				o.icon = "array.png";
				var node = treeNode.appendChild(o);
				
				var jsonStrBegin = tab+title+"[";
				var jsonStrEnd = tab+"]";
				
				v.each(function(item, idx){
					jsonStr += this.parseJsonObject(nextLevel, node, "", "["+idx+"]", item, false);
				}.bind(this));
				
				jsonStr = jsonStrBegin+"\n"+jsonStr.substring(0, jsonStr.length-2)+"\n"+jsonStrEnd+",\n";
				break;
				
			case "string":	
				jsonStr += tab+title+"\""+v+"\",\n";
				
				o.text = p + " : \""+v+"\"";
				o.icon = "string.png";
				//var node = 
				treeNode.appendChild(o);
				
				break;
			case "date":	
				jsonStr += tab+title+"\""+v+"\",\n";
				o.text = p + " : \""+v+"\"";
				o.icon = "string.png";
				//var node = 
				treeNode.appendChild(o);
				break;
				
			default: 
				jsonStr += tab+title+v+",\n";
				o.text = p + " : "+v;
				o.icon = "string.png";
				//var node = 
				treeNode.appendChild(o);
		}
		return jsonStr;
	}
});


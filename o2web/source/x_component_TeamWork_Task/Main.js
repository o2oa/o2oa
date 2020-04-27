MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.Task = MWF.xApplication.TeamWork.Task || {};
MWF.xApplication.TeamWork.Task.options.multitask = true;
// MWF.xDesktop.requireApp("TeamWork", "Task", null, false);
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);
MWF.xApplication.TeamWork.Task.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "TeamWork.Task",
		"icon": "icon.png",
		"width": "1000",
		"height": "700",
		"isResize": false,
		"isMax": false,
		"title": MWF.xApplication.TeamWork.Task.LP.title
	},

	onQueryLoad: function(){
		this.lp = MWF.xApplication.TeamWork.Task.LP;
		//this.lp = MWF.xApplication.TeamWork.LP; debugger;
		this.cssPath = "/x_component_TeamWork/$Task/"+this.options.style+"/css.wcss";


		if (!this.status) {
		} else {
			this.options.workId = this.status.workId;
			this.options.workCompletedId = this.status.workCompletedId;
			this.options.jobId = this.status.jobId;
			this.options.priorityWork = this.status.priorityWork;
			this.options.readonly = (this.status.readonly === "true");
		}


		this.taskId = this.options.taskId || "";
		this.projectId = this.options.project ||"";

		MWF.xDesktop.requireApp("TeamWork", "lp.zh-cn", {
			"onRequestFailure": function(){
				//alert("fail")
			}.bind(this),
			"onSuccess": function(json){
				this.lp = MWF.xApplication.TeamWork.LP;
			}.bind(this)
		}, false);

	},
	loadApplication: function(callback) {

		this.rootActions = MWF.Actions.load("x_teamwork_assemble_control");
		this.orgActions = MWF.Actions.load("x_organization_assemble_express");
		//this.actions = this.rootActions.TaskAction;


		//http://dev.o2oa.net/x_desktop/app.html?app=TeamWork.Task&status={"taskId":"abb0621d-c35c-4010-9cd3-49a439b36a09"}
		this.node = new Element("div").inject(this.content);
		//alert(this.taskId)


		MWF.xDesktop.requireApp("TeamWork", "Task", function(){
			//alert("loaded")
			var data = {
				taskId:this.taskId
			};
			var opt={
				"type":"window"
			};
			var task = new MWF.xApplication.TeamWork.Task(this,data,opt);
			task.open();
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
		var loading = new Element("img",{styles:{
				"margin-top":"10px",
				"margin-bottom":"10px",
				"width":"100px",
				"height":"20px"
			},"src":"/x_component_TeamWork/$Main/default/icon/loading.gif"}).inject(container);
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

});

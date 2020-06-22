MWF.xApplication.Homepage.options.multitask = false;
o2.requireApp("Homepage", "TaskContent", null, false);
MWF.xApplication.Homepage.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"mvcStyle": "style.css",
		"name": "Homepage",
		"icon": "icon.png",
		"width": "1200",
		"height": "800",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.Homepage.LP.title,
		"minHeight": 700
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Homepage.LP;
		this.viewPath = this.path+this.options.style+"/view.html";
	},
	loadApplication: function(callback){
		this.content.loadHtml(this.viewPath, {"bind": {"lp": this.lp}, "module": this}, function(){
			if (!this.options.isRefresh){
				this.maxSize(function(){
					this.loadApp(callback);
				}.bind(this));
			}else{
				this.loadApp(callback);
			}
		}.bind(this));
	},
	loadApp: function(callback){
		//this.initNode();
		this.initNodeSize();

		this.taskLoaded = false;
		this.inforLoaded = false;
		this.meetingLoaded = false;
		this.fileLoaded = false;
		this.calenderLoaded = false;

		this.loadTaskContent(function(){ this.taskLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadInforContent(function(){ this.inforLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadMeetingContent(function(){ this.meetingLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadFileContent(function(){ this.fileLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadCalendarContent(function(){ this.calenderLoaded = true; this.checkAppLoaded(callback); }.bind(this));
	},
	checkAppLoaded: function(callback){
		if (this.taskLoaded && this.inforLoaded && this.meetingLoaded && this.fileLoaded && this.calenderLoaded){
			if (callback) callback();
		}
	},
	// initNode: function(){
	// 	this.node = this.content.getElement(".o2_homepage_content");
	// 	this.calendarContentNode = this.content.getElement(".o2_homepage_calendar_content");
	// 	this.taskContentNode = this.content.getElement(".o2_homepage_task_content");
	// 	this.meetingContentNode = this.content.getElement(".o2_homepage_meeting_content");
	// 	this.inforContentNode = this.content.getElement(".o2_homepage_infor_content");
	// 	this.fileContentNode = this.content.getElement(".o2_homepage_file_content");
	//
	// 	this.rightLayout = this.content.getElement(".o2_homepage_layout_right");
	// 	this.ltlLayout = this.content.getElement(".o2_homepage_layout_leftTopLeft");
	// 	this.ltrLayout = this.content.getElement(".o2_homepage_layout_leftTopRight");
	// 	this.lblLayout = this.content.getElement(".o2_homepage_layout_leftBottomLeft");
	// 	this.lbrLayout = this.content.getElement(".o2_homepage_layout_leftBottomRight");
	// },
	initNodeSize: function(){
		this.resizeNodeSize();
		this.addEvent("resize", this.resizeNodeSize.bind(this));
	},
	resizeNodeSize: function(){
		var size = this.content.getSize();
		var edge = this.node.getEdgeHeight();
		var height = size.y - edge;
		if (height<this.options.minHeight) height = this.options.minHeight;
		this.node.setStyle("height", ""+height+"px");


		var rightHeight = height - this.calendarContentNode.getEdgeHeight();
		var leftHeight = this.ltlLayout.getSize().y - this.taskContentNode.getEdgeHeight();

		this.calendarContentNode.setStyle("height", ""+rightHeight+"px");
		this.taskContentNode.setStyle("height", ""+leftHeight+"px");
		this.meetingContentNode.setStyle("height", ""+leftHeight+"px");
		this.inforContentNode.setStyle("height", ""+leftHeight+"px");
		this.fileContentNode.setStyle("height", ""+leftHeight+"px");
	},
	loadTaskContent: function(callback){
		this.taskContent = new MWF.xApplication.Homepage.TaskContent(this, this.taskContentNode, {
			"onLoad": function(){if (callback) callback();}
		});
	},
	loadInforContent: function(callback){
		o2.requireApp("Homepage", "InforContent", function(){
			this.inforContent = new MWF.xApplication.Homepage.InforContent(this, this.inforContentNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
	},
	loadMeetingContent: function(callback){
		o2.requireApp("Homepage", "MeetingContent", function(){
			this.inforContent = new MWF.xApplication.Homepage.MeetingContent(this, this.meetingContentNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
	},
	loadFileContent: function(callback){
		o2.requireApp("Homepage", "FileContent", function(){
			this.inforContent = new MWF.xApplication.Homepage.FileContent(this, this.fileContentNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
	},
	loadCalendarContent: function(callback){
		o2.requireApp("Homepage", "CalendarContent", function(){
			this.inforContent = new MWF.xApplication.Homepage.CalendarContent(this, this.calendarContentNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
	}
});




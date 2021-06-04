var MWFCalendar = MWF.xApplication.Calendar  = MWF.xApplication.Calendar || {};
//MWF.xApplication.Calendar.widget = MWF.xApplication.Calendar.widget || {};
MWF.xDesktop.requireApp("Calendar", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Calendar", "Common", null, false);
o2.widget.Schedule = o2.Schedule = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default",
		"weekBegin" : 0,
		"date" : "",
		"title" : ""
	},
	initialize: function(container, app, options){
		this.setOptions(options);
		this.container = container;
		this._app = app;

		this.path = this.options.path || "../x_component_Calendar/widget/$Schedule/";

		this.cssPath = "../x_component_Calendar/widget/$Schedule/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.fireEvent("init");

		this.load() ;

	},
	load: function(){
		this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
		var date = "";
		if( this.options.date ){
			date = Date.parse( this.options.date )
		}


		this.app = {
			actions : MWF.Actions.get("x_calendar_assemble_control"),
			lp : MWF.xApplication.Calendar.LP,
			content : this._app.content
		};

		this.calendar = new o2.Schedule.Calendar(this, date  );
	},
	reload: function(){
		if (this.calendar) this.calendar.reLoadCalendar();
	},
	destroy: function(){
		if (this.calendar){
			this.calendar.destroy();
		}
		this.node.destroy();
	}
});

o2.Schedule.Calendar = new Class({
	Implements: [Events],
	initialize: function(view, date){
		this.view = view;
		this.css = this.view.css;
		this.container = this.view.node;
		this.date = date || new Date();
		this.today = new Date();
		this.days = {};
		this.app = this.view.app;
		this.lp = MWF.xApplication.Calendar.LP;
		this.weekBegin = this.view.options.weekBegin;
		this.load();
	},
	load: function(){
		this.titleNode = new Element("div", {"styles": this.css.calendarTitleNode}).inject(this.container);

		this.contentNode = new Element("div", {"styles": { "overflow" : "hidden" }}).inject(this.container);
		this.leftContentNode = new Element("div", {"styles": { "float" : "left" }}).inject(this.contentNode);
		this.rightContentNode = new Element("div", {"styles": this.css.rightContentNode }).inject(this.contentNode);

		this.titleTableContainer = new Element("div", {"styles": this.css.calendarTitleTableContainer}).inject(this.leftContentNode);

		//this.scrollNode = new Element("div", {
		//	"styles": this.css.scrollNode
		//}).inject(this.container);
		//this.contentWarpNode = new Element("div", {
		//	"styles": this.css.contentWarpNode
		//}).inject(this.scrollNode);
        //
		//this.contentContainerNode = new Element("div",{
		//	"styles" : this.css.contentContainerNode
		//}).inject(this.contentWarpNode);
		this.bodyNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.leftContentNode);
		//this.bodyNode.setStyle("position","relative");

		//this.bodyNode = new Element("div", {"styles": this.css.calendarBodyNode}).inject(this.container);

		this.setTitleNode();


		this.setTitleTableNode();
		this.setBodyNode();

		//this.app.addEvent("resize", this.resetBodySize.bind(this));

	},
	resetBodySize: function(){
		var size = this.view.container.getSize();
		var titleSize = this.titleNode.getSize();
		var titleTableSize = this.titleTable.getSize();
		var y = size.y-titleSize.y-titleTableSize.y;

		this.leftContentNode.setStyle( "width", size.x - 160 + "px" );

		this.rightContentNode.setStyle("height", size.y - titleSize.y - 35 + "px" );

		o2.Schedule.TdHeight = y / 6;
		o2.Schedule.TdWidth = (size.x - 160 )/ 7;

		//this.bodyNode.setStyle("height", ""+y+"px");

		//var size = this.container.getSize();

		//this.scrollNode.setStyle("height", ""+y+"px");

		//this.titleTableContainer.setStyles({
		//	"width": (size.x - 40) +"px"
		//});

		//if (this.contentWarpNode){
		//	this.contentWarpNode.setStyles({
		//		"width": (size.x - 40) +"px"
		//	});
		//}

		//var tableSize = this.calendarTable.getSize();
		//o2.Schedule.WeekWidth = tableSize.x;
		//o2.Schedule.DayWidth = tableSize.x / 7;
		//this.dataTdList.each( function( td ){
		//	td.setStyle("width", o2.Schedule.WeekWidth)
		//});

		//if( this.wholeDayDocumentList && this.wholeDayDocumentList.length ){
		//	this.wholeDayDocumentList.each( function( doc ){
		//		doc.resize();
		//	}.bind(this))
		//}
        //
		//if( this.oneDayDocumentList && this.oneDayDocumentList.length ){
		//	this.oneDayDocumentList.each( function( doc ){
		//		doc.resize();
		//	}.bind(this))
		//}

		//var top = 30;
		//var trs = this.calendarTable.getElements("tr");
		//this.calendarTrHeight = [];
		//for( var key in this.usedYIndex ){
		//	var idx = this.usedYIndex[key];
		//	var maxLength = Math.max( idx[0].length, idx[1].length, idx[2].length, idx[3].length, idx[4].length, idx[5].length, idx[6].length );
		//	if( maxLength > 4 ){
		//		this.dataTableList[key].setStyle("top", top );
		//		var height = 30 + maxLength * (22 + 2);
		//		top =  top + height;
		//		trs[ parseInt(key) ].getElements("td").each( function(td){
		//			td.setStyle("height", height )
		//		});
		//		this.calendarTrHeight.push( height );
		//	}else{
		//		this.dataTableList[key].setStyle("top", top );
		//		top =  top + o2.Schedule.WeekHeight + 1;
		//		trs[ parseInt(key) ].getElements("td").each( function(td){
		//			td.setStyle("height", o2.Schedule.WeekHeight )
		//		});
		//		this.calendarTrHeight.push( o2.Schedule.WeekHeight );
		//	}
		//}


		//var tdy = (y-30)/6;
		//tdy = tdy-34;
		//var tds = this.calendarTable.getElements("td");
		//tds.each(function(td){
		//    var yy = tdy;
		//    var node = td.getLast("div");
		//    if (node.childNodes.length>=4){
		//        if (yy<92) yy = 69;
		//    }
		//    node.setStyle("height", ""+yy+"px");
		//}.bind(this));


	},
	setTitleNode: function(){
		//this.view.titleContainer.getElements("div:only-child").setStyle("display","none");
		//if( this.titleNode ){
		//    this.titleNode.setStyle("display","")
		//}
		//this.titleNode = new Element("div").inject(this.view.titleContainer);

		if( this.view.options.title ){
			new Element("div", {"styles": this.css.titleNameNode, "text" : this.view.options.title }).inject(this.titleNode);
		}



		var text = this.date.format(this.lp.dateFormatDay);
		this.titleTextNode = new Element("div", {"styles": this.css.calendarTitleTextNode, "text": text}).inject(this.titleNode);
		this.nextMonthNode =  new Element("div", {"styles": this.css.calendarNextMonthNode}).inject(this.titleNode);
		this.prevMonthNode =  new Element("div", {"styles": this.css.calendarPrevMonthNode}).inject(this.titleNode);



		this.prevMonthNode.addEvents({
			"mouseover": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode_over);}.bind(this),
			"mouseout": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode);}.bind(this),
			"mousedown": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode_down);}.bind(this),
			"mouseup": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode_over);}.bind(this),
			"click": function(){this.changeMonthPrev();}.bind(this)
		});
		this.nextMonthNode.addEvents({
			"mouseover": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode_over);}.bind(this),
			"mouseout": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode);}.bind(this),
			"mousedown": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode_down);}.bind(this),
			"mouseup": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode_over);}.bind(this),
			"click": function(){this.changeMonthNext();}.bind(this)
		});
		this.titleTextNode.addEvents({
			"mouseover": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_over);}.bind(this),
			"mouseout": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode);}.bind(this),
			"mousedown": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_down);}.bind(this),
			"mouseup": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_over);}.bind(this),
			"click": function(){this.changeMonthSelect();}.bind(this)
		});
	},
	changeMonthPrev: function(){
		this.date.decrement("month", 1);
		var text = this.date.format(this.lp.dateFormatDay);
		this.titleTextNode.set("text", text);
		this.reLoadCalendar();
	},
	changeMonthNext: function(){
		this.date.increment("month", 1);
		var text = this.date.format(this.lp.dateFormatDay);
		this.titleTextNode.set("text", text);
		this.reLoadCalendar();
	},
	changeMonthSelect: function(){
		if (!this.monthSelector) this.createMonthSelector();
		this.monthSelector.show();
	},
	createMonthSelector: function(){
		this.monthSelector = new o2.Schedule.MonthSelector(this.date, this);
	},
	changeMonthTo: function(d){
		this.date = d;
		var text = this.date.format(this.lp.dateFormatDay);
		this.titleTextNode.set("text", text);
		this.reLoadCalendar();
	},

	setTitleTableNode : function(){
		if( this.weekBegin == "1" ){
			var html = "<tr><th>"+this.lp.weeksShort.Mon+"</th><th>"+this.lp.weeksShort.Tues+"</th><th>"+this.lp.weeksShort.Wed+"</th>" +
				"<th>"+this.lp.weeksShort.Thur+"</th><th>"+this.lp.weeksShort.Fri+"</th><th>"+this.lp.weeksShort.Sat+"</th><th>"+this.lp.weeksShort.Sun+"</th></tr>";
		}else{
			var html = "<tr><th>"+this.lp.weeksShort.Sun+"</th><th>"+this.lp.weeksShort.Mon+"</th><th>"+this.lp.weeksShort.Tues+"</th><th>"+this.lp.weeksShort.Wed+"</th>" +
				"<th>"+this.lp.weeksShort.Thur+"</th><th>"+this.lp.weeksShort.Fri+"</th><th>"+this.lp.weeksShort.Sat+"</th></tr>";
		}
		this.titleTable = new Element("table", {
			"styles": this.css.calendarTable,
			"height": "100%",
			"border": "0",
			"cellPadding": "0",
			"cellSpacing": "0",
			"html": html
		}).inject(this.titleTableContainer);
		this.calendarTableTitleTr = this.titleTable.getElement("tr");
		this.calendarTableTitleTr.setStyles(this.css.calendarTableTitleTr);
		var ths = this.calendarTableTitleTr.getElements("th");
		ths.setStyles(this.css.calendarTableTh);
	},
	setBodyNode: function(){
		var html = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		this.calendarTable = new Element("table", {
			"styles": this.css.calendarTable,
			"height": "100%",
			"border": "0",
			"cellPadding": "0",
			"cellSpacing": "0",
			"html": html
		}).inject(this.bodyNode);

		//var tds = this.calendarTable.getElements("td");
		//tds.setStyles(this.css.calendarTableCell);

		this.loadCalendar();
	},
	reLoadCalendar: function(){
		//if( this.wholeDayDocumentList && this.wholeDayDocumentList.length ){
		//	this.wholeDayDocumentList.each( function( doc ){
		//		doc.destroy();
		//	}.bind(this))
		//}
		//this.wholeDayDocumentList = [];
		//
		//if( this.oneDayDocumentList && this.oneDayDocumentList.length ){
		//	this.oneDayDocumentList.each( function( doc ){
		//		doc.destroy();
		//	}.bind(this))
		//}
		//this.oneDayDocumentList = [];

		this.loadCalendar();
	},
	calculateMonthRange : function(){
		var date =  this.date.clone();

		var start = new Date( date.get("year"), date.get("month"), 1, 0, 0, 0 );
		var week = start.getDay();
		if( this.weekBegin == "1" ){
			var decrementDay = ((week-1)<0) ? 6 : week-1;
		}else{
			var decrementDay = week;
		}
		start.decrement("day", decrementDay);
		this.monthStart = start;
		this.monthStartStr = this.monthStart.format("db");

		var end = start.clone();
		end.increment("day", 41);
		this.monthEnd =  new Date( end.get("year"), end.get("month"), end.get("date"), 23, 59, 59 );
		this.monthEndStr = this.monthEnd.format("db");

		//this.calculateWeekRange();
	},
	//calculateWeekRange: function(){
	//	this.weekRangeList = [];
	//	var start = this.monthStart.clone();
	//	var end;
	//	for( var i=0; i<6; i++ ){
	//		end = start.clone().increment("day", 6);
	//		end = new Date( end.get("year"), end.get("month"), end.get("date"), 23, 59, 59 );
	//		this.weekRangeList.push( {
	//			start : start,
	//			end : end
	//		});
	//		start = end.clone().increment("second",1);
	//	}
    //
	//	this.weekDaysList = [];
	//	start = this.monthStart.clone();
	//	for( var i=0; i<this.weekRangeList.length; i++ ){
	//		var j =0;
	//		var days = [];
	//		while( j<7 ){
	//			days.push( start.format("%Y-%m-%d") );
	//			start.increment("day",1);
	//			j++;
	//		}
	//		this.weekDaysList.push(days);
	//	}
    //
    //
	//	this.usedYIndex = {};
	//	for( var i=0; i<this.weekRangeList.length; i++ ){
	//		this.usedYIndex[i] = {};
	//		var j =0;
	//		while( j<7 ){
	//			this.usedYIndex[i][j] = [];
	//			j++;
	//		}
	//	}
	//},
	//getDateIndexOfWeek : function( weekIndex, days ){
	//	var weekDays = this.weekDaysList[weekIndex];
	//	var indexs = [];
	//	for( var i=0; i<days.length;i++ ){
	//		indexs.push( weekDays.indexOf( days[i] ) );
	//	}
	//	return indexs;
	//},
	inCurrentMonth : function( time ){
		return time > this.monthStart && time < this.monthEnd;
	},
	//getTimeRange : function( bDate, eDate ){
	//	if( bDate > this.monthEnd || eDate < this.monthStart  )return null;
	//	var range = {
	//		startTime : bDate,
	//		endTime : eDate,
	//		start: ( bDate <= this.monthStart  ) ? this.monthStart.clone() : bDate.clone(),
	//		end: ( this.monthEnd <= eDate ) ? this.monthEnd.clone() : eDate.clone()
	//	};
	//	range.firstDay = range.start.clone().clearTime();
	//	range.diff = range.start - range.end;
	//	range.weekInforList = this.getWeekInfor(bDate, eDate);
	//	return range;
	//},
	//getWeekInfor : function( startTime, endTime ){
	//	if( startTime > this.monthEnd || endTime < this.monthStart  )return null;
	//	var rangeWeekInfor = {};
	//	for( var i=0 ; i<this.weekRangeList.length; i++ ){
	//		var range = this.weekRangeList[i];
	//		if(startTime > range.end || endTime < range.start )continue;
	//		var isStart = startTime >= range.start;
	//		var isEnd =  range.end >= endTime;
	//		var start =  isStart ? startTime : range.start;
	//		var end =  isEnd ? endTime : range.end;
	//		var diff = end - start;
	//		var left = start - range.start;
	//		var days = this.getDaysByRange(start, end);
	//		var daysIndex = this.getDateIndexOfWeek( i, days );
	//		rangeWeekInfor[i] = {
	//			index : i,
	//			isEventStart : isStart,
	//			isEventEnd : isEnd,
	//			start : start,
	//			end : end,
	//			diff : diff,
	//			days : days,
	//			left : left,
	//			daysIndex : daysIndex
	//		};
	//		if( isEnd )break;
	//	}
	//	return rangeWeekInfor;
	//},
	//getDaysByRange : function( startTime, endTime ){
	//	var start = startTime.clone();
	//	var end = endTime;
	//	var days = [];
	//	while( start < end ){
	//		days.push( start.clone().format("%Y-%m-%d") );
	//		start.increment()
	//	}
	//	return days;
	//},
	loadCalendar: function(){
		//this.app.currentDate = this.date.clone();

		this.calculateMonthRange();
		this.cancelCurrentTd();
		this.loadData( function(){
			this.resetBodySize();
			this._loadCalendar( false );
			this.loadDayContent( this.date );
			//this.loadWholeDay( this.wholeDayData );
			//this.loadOneDay( this.inOneDayEvents );
		}.bind(this));
	},
	_loadCalendar : function( isCreate ){
		var date = this.date.clone();
		date.set("date", 1);
		var week = date.getDay();
		if( this.weekBegin == "1" ){
			var decrementDay = ((week-1)<0) ? 6 : week-1;
		}else{
			var decrementDay = week;
		}

		date.decrement("day", decrementDay);
		var tds = this.calendarTable.getElements("td");
		tds.each(function(td){
			this.loadDay(td, date, isCreate);
			date.increment();
		}.bind(this));
	},
	loadData : function( callback ){
		this.app.actions.listMyCalendar( function( json ){
			var ids = [];
			json.data.myCalendars.each( function( d ){ ids.push( d.id ) });
			json.data.unitCalendars.each( function( d ){ ids.push( d.id ) });
			json.data.followCalendars.each( function( d ){ ids.push( d.id ) });

			this.app.actions.listEventWithFilter( {
				calendarIds : ids,
				startTime : this.monthStartStr,
				endTime : this.monthEndStr //,
				//createPerson : this.app.userName
			}, function(json){
				this.wholeDayData = ( json.data  && json.data.wholeDayEvents ) ? json.data.wholeDayEvents : [];
				this.wholeDayData.each( function(d){
					d.start = Date.parse( d.startTimeStr );
					d.end = Date.parse( d.endTimeStr );
				});
				this.inOneDayEventMap = {};
				var array = ( json.data  && json.data.inOneDayEvents ) ? json.data.inOneDayEvents : [];
				array.each( function(d){
					if( d.inOneDayEvents && d.inOneDayEvents.length ){
						this.inOneDayEventMap[d.eventDate] = d.inOneDayEvents;
					}
				}.bind(this));
				//(( json.data && json.data.inOneDayEvents) ? json.data.inOneDayEvents : []).each( function( d ){
				//	if(d.inOneDayEvents.length > 0 ){
				//		this.inOneDayEvents.push( d );
				//	}
				//}.bind(this));
				if(callback)callback();
			}.bind(this));

		}.bind(this));
	},
	isDateHasData : function( date ){
		debugger;
		var dStr = typeOf( date ) === "string" ? date : date.format("%Y-%m-%d");
		var array = [];
		if( this.inOneDayEventMap[ dStr ] && this.inOneDayEventMap[ dStr].length )return true;
		var start = Date.parse( dStr + " 00:00:00" );
		var end = Date.parse( dStr + " 23:59:59" );
		for( var i=0; i<this.wholeDayData.length; i++ ){
			var data = this.wholeDayData[i];
			if( data.start <= start && data.end >= end )return true;
		}
		return false;
	},
	getDataByDate : function( date ){
		var dStr = typeOf( date ) === "string" ? date : date.format("%Y-%m-%d");
		var array = [];
		if( this.inOneDayEventMap[ dStr ] && this.inOneDayEventMap[ dStr].length )array = this.inOneDayEventMap[ dStr].clone();
		var start = Date.parse( dStr + " 00:00:00" );
		var end = Date.parse( dStr + " 23:59:59" );
		this.wholeDayData.each( function( data ){
			if( data.start <= start && data.end >= end )array.push( data );
		}.bind(this));
		return array;
	},
	loadDay: function(td, date, isCreate){
		debugger;
		var _self = this;
		td.empty();
		var type = "thisMonth";
		var m = date.get("month");
		var y = date.get("year");
		var d = date.get("date");
		var mm = this.date.get("month");
		var yy = this.date.get("year");
		var dd = this.date.get("date");
		var mmm = this.today.get("month");
		var yyy = this.today.get("year");
		var ddd = this.today.get("date");

		if ((m==mmm) && (y==yyy) && (d==ddd)){
			type = "today";
		}else if ((m==mm) && (y==yy)){
			type = "thisMonth";
		}else{
			type = "otherMonth";
		}

		var hasData = this.isDateHasData( date );

		//var node = new Element("div", {
		//    "styles" : this.css["calendarTableCell_"+type]
		//}).inject( td );
		//td.set( "valign","top");
		td.set( "align","center");
		td.set("height", o2.Schedule.TdHeight );

		//td.set("line-height","40px");
		td.setStyles( this.css["calendarTableCell_"+type] );
		td.store("dateStr",date.format("%Y-%m-%d"));
		td.store("type", type );


		td.addEvent("click", function(ev){
			_self.setCurrentTd( this.td );
			_self.loadDayContent( this.date  );
		}.bind({ td : td, date : date.format("%Y-%m-%d") }));

		td.addEvent("dblclick", function(ev){
			//_self.cancelCurrentTd();
			//var form = new MWF.xApplication.Calendar.EventForm(_self,{}, {
			//	startTime : Date.parse( this.retrieve("dateStr") + " 08:00") ,
			//	endTime : Date.parse( this.retrieve("dateStr") + " 09:00")
			//}, {app:_self.app});
			//form.view = _self;
			//form.create();
			_self.toDay( this.retrieve("dateStr") );
		}.bind(td));

		//var titleNode = new Element("div", {"styles": this.css["dayTitle_"+ type]}).inject(td);

		var titleDayNode = new Element("div", {"styles": this.css["dayTitleDay_"+ type], "text": d }).inject(td);

		td.store("titleDayNode", titleDayNode );

		if( hasData ){
			new Element("div", {"styles": type === "today" ? this.css.dayHasData_today : this.css.dayHasData }).inject(titleDayNode);
		}

		if( type === "today" ){
			var size = Math.min(  o2.Schedule.TdHeight, o2.Schedule.TdWidth );
			size = size > 26 ? 26 : size;
			titleDayNode.setStyles({
				"width" : size+"px",
				"height" : size+"px",
				"line-height" : size+"px"
			})
		}else if( (m==mm) && (y==yy) && (d==dd) ){
			this.setCurrentTd( td )
		}



		//titleDayNode.addEvent("click", function(){
		//	_self.setCurrentTd();
		//	_self.loadDayContent( this.date  );
		//}.bind({ date : date.format("%Y-%m-%d") }));

		//var contentNode = new Element("div", {"styles": this.css.dayContentNode}).inject(node);

	},
	create : function( dateStr ){
		var _self = this;
		var form = new MWF.xApplication.Calendar.EventForm(_self,{}, {
			startTime : dateStr + " 08:00",
			endTime : dateStr + " 09:00"
		}, {app:_self.app});
		form.view = _self;
		form.create();
	},
	isEditable: function(){
		if( MWF.AC.isAdministrator() )return true;
		if( (this.data.manageablePersonList || []).contains( layout.desktop.session.user.distinguishedName ) )return true;
		if( this.data.createPerson === layout.desktop.session.user.distinguishedName )return true;
		return false;
	},
	openEventForm : function(data){
		var form = new MWF.xApplication.Calendar.EventForm(this, data, {
			isFull : true
		}, {app:this.app});
		form.view = this.view;
		this.isEditable(data) ? form.edit() : form.open();
	},
	toDay : function( date ){
		var appId = "Calendar";
		if (layout.desktop.apps[appId]){
			var app = layout.desktop.apps[appId];
			app.status = {
				options : { "date" : date }
			};
			app.toMonth();
			app.setCurrent();
		}else {
			var options = {
				"defaultAction": "toMonth",
				onQueryLoad : function(){
					this.status = {
						options : { "date" : date }
					}
				}
			};
			layout.desktop.openApplication(null, "Calendar", options);
		}
	},
	setCurrentTd : function(td){
		this.cancelCurrentTd();
		var type = td.retrieve("type");
		if( type != "today" ){
			td.retrieve( "titleDayNode" ).setStyles( this.css["dayTitleDay_current"] )
		}
		this.currentSelectedTd = td;
	},
	cancelCurrentTd : function(){
		if( this.currentSelectedTd ){
			var type = this.currentSelectedTd.retrieve("type");
			if( type != "today" ){
				this.currentSelectedTd.retrieve( "titleDayNode" ).setStyles( this.css["dayTitleDay_"+ type] )
			}
		}
		this.currentSelectedTd = null;
	},
	reload : function(){
		this.view.reload();
	},
	destroy: function(){
		Object.each(this.days, function(day){
			day.destroy();
		}.bind(this));
		this.container.empty();
	},
	loadDayContent : function( date ){
		date = typeOf( date ) === "string" ? date : date.format("%Y-%m-%d");
		var _self = this;
		this.rightContentNode.empty();

		var text = Date.parse(date).format(this.lp.dateFormatDay);
		this.titleTextNode.set("text", text);

		var data = this.getDataByDate( date );
		if( data.length == 0 ){
			var titleNode = new Element("div", { styles : this.css.rightContentTitle }).inject( this.rightContentNode );
			new Element("div", { styles : this.css.rightContentTitle_text, text : this.lp.schedule.noEvent }).inject( titleNode );

			var itemContainer = new Element("div", { styles : this.css.rightContentItemContainer }).inject( this.rightContentNode );
		}else{
			var titleNode = new Element("div", { styles : { overflow : "hidden" } }).inject( this.rightContentNode );
			new Element("div", { styles : this.css.rightContentTitle_text, text : this.lp.schedule.current }).inject( titleNode );
			new Element("div", { styles : this.css.rightContentTitle_count, text : data.length }).inject( titleNode );
			new Element("div", { styles : this.css.rightContentTitle_text, text : this.lp.schedule.someEvent }).inject( titleNode );

			var itemContainer = new Element("div", { styles : this.css.rightContentItemContainer }).inject( this.rightContentNode );
			for( var i=0; i<data.length && i<2; i++ ){
				var d = data[i];
				var itemNode = new Element("div", { styles : this.css.rightContentItem }).inject( itemContainer );
				itemNode.addEvent("click", function(){
					_self.openEventForm( this.data );
				}.bind({ data : d }));
				new Element("div", { styles : this.css.rightContentItemDot }).inject( itemNode );
				new Element("div", { styles : this.css.rightContentItemText, text : d.title }).inject( itemNode );

				var start = Date.parse( d.startTime );
				var end = Date.parse( d.endTime );
				var startDateOnly = start.format("%Y-%m-%d");
				var endDateOnly = end.format("%Y-%m-%d");
				var timeText ;
				if( startDateOnly === endDateOnly ){
					timeText =start.format("%H:%M") +  this.lp.to + end.format("%H:%M");
				}else{
					timeText = start.format("%m-%d") + this.lp.to + end.format("%m-%d");
				}
				new Element("div", { styles : this.css.rightContentItemTime, text : timeText }).inject( itemNode );
			}
		}

		var rightToolbar = new Element("div", { styles : this.css.rightToolbar }).inject( this.rightContentNode );
		var rightTool_add = new Element("div", { styles : this.css.rightTool_add, "text" : this.lp.add }).inject( this.rightContentNode );
		rightTool_add.addEvent( "click", function(){
			this.create( date )
		}.bind(this));
		var rightTool_more = new Element("div", { styles : this.css.rightTool_more, "text" : this.lp.more }).inject( this.rightContentNode );
		rightTool_more.addEvent( "click", function(){
			this.toDay( date )
		}.bind(this))
	}

});

o2.Schedule.TdHeight = 30;
o2.Schedule.TdWidth = 40;

o2.Schedule.MonthSelector = new Class({
	Implements: [Events],
	initialize: function(date, calendar){
		this.calendar = calendar;
		this.css = this.calendar.css;
		this.app = this.calendar.app;
		this.lp = this.app.lp;
		this.date = date;
		this.year = this.date.get("year");
		this.load();
	},
	load: function(){
		this.monthSelectNode = new Element("div", {"styles": this.css.calendarMonthSelectNode}).inject(this.calendar.container);
		this.monthSelectNode.position({
			relativeTo: this.calendar.titleTextNode,
			position: 'bottomCenter',
			edge: 'upperCenter'
		});
		this.monthSelectNode.addEvent("mousedown", function(e){e.stopPropagation();});

		this.monthSelectTitleNode = new Element("div", {"styles": this.css.calendarMonthSelectTitleNode}).inject(this.monthSelectNode);
		this.monthSelectPrevYearNode = new Element("div", {"styles": this.css.calendarMonthSelectTitlePrevYearNode}).inject(this.monthSelectTitleNode);
		this.monthSelectNextYearNode = new Element("div", {"styles": this.css.calendarMonthSelectTitleNextYearNode}).inject(this.monthSelectTitleNode);
		this.monthSelectTextNode = new Element("div", {"styles": this.css.calendarMonthSelectTitleTextNode}).inject(this.monthSelectTitleNode);
		this.monthSelectTextNode.set("text", this.year);

		var html = "<tr><td></td><td></td><td></td></tr>";
		html += "<tr><td></td><td></td><td></td></tr>";
		html += "<tr><td></td><td></td><td></td></tr>";
		html += "<tr><td></td><td></td><td></td></tr>";
		this.monthSelectTable = new Element("table", {
			"styles": {"margin-top": "10px"},
			"height": "200px",
			"width": "90%",
			"align": "center",
			"border": "0",
			"cellPadding": "0",
			"cellSpacing": "0", //5
			"html": html
		}).inject(this.monthSelectNode);

		//this.loadMonth();

		this.monthSelectBottomNode = new Element("div", {"styles": this.css.calendarMonthSelectBottomNode, "text": this.lp.today}).inject(this.monthSelectNode);

		this.setEvent();
	},
	loadMonth: function(){
		this.monthSelectTextNode.set("text", this.year);
		var d = new Date();
		var todayY = d.get("year");
		var todayM = d.get("month");

		var thisY = this.date.get("year");
		var thisM = this.date.get("month");

		var _self = this;
		var tds = this.monthSelectTable.getElements("td");
		tds.each(function(td, idx){
			td.empty();
			td.removeEvents("mouseover");
			td.removeEvents("mouseout");
			td.removeEvents("mousedown");
			td.removeEvents("mouseup");
			td.removeEvents("click");

			var m = idx+1;
			td.store("month", m);
			td.setStyles(this.css.calendarMonthSelectTdNode);

			td.setStyle("background-color", "#FFF");
			if ((this.year == todayY) && (idx == todayM)){
				new Element("div", {
					styles : _self.css.calendarMonthSelectTodayNode,
					text : ""+m+this.lp.month
				}).inject( td );
			}else if ((this.year == thisY) && (idx == thisM)){
				//td.setStyle("background-color", "#EEE");
				new Element("div", {
					styles : _self.css.calendarMonthSelectCurrentNode,
					text : ""+m+this.lp.month
				}).inject( td );
			}else{
				td.set("text", ""+m+this.lp.month);
			}

			td.addEvents({
				"mouseover": function(){this.setStyles(_self.css.calendarMonthSelectTdNode_over);},
				"mouseout": function(){this.setStyles(_self.css.calendarMonthSelectTdNode);},
				"mousedown": function(){this.setStyles(_self.css.calendarMonthSelectTdNode_down);},
				"mouseup": function(){this.setStyles(_self.css.calendarMonthSelectTdNode_over);},
				"click": function(){
					_self.selectedMonth(this);
				}
			});
		}.bind(this));
	},
	setEvent: function(){
		this.monthSelectPrevYearNode.addEvent("click", function(){
			this.prevYear();
		}.bind(this));

		this.monthSelectNextYearNode.addEvent("click", function(){
			this.nextYear();
		}.bind(this));

		this.monthSelectBottomNode.addEvents({
			"mouuseover" : function(){ this.monthSelectBottomNode.setStyles( this.css.calendarMonthSelectBottomNode_over ); }.bind(this),
			"mouuseout" : function(){ this.monthSelectBottomNode.setStyles( this.css.calendarMonthSelectBottomNode ); }.bind(this),
			"click" : function(){ this.todayMonth(); }.bind(this)
		});
	},
	prevYear: function(){
		this.year--;
		if (this.year<1900) this.year=1900;
		this.monthSelectTextNode.set("text", this.year);
		this.loadMonth();
	},
	nextYear: function(){
		this.year++;
		//if (this.year<1900) this.year=1900;
		this.monthSelectTextNode.set("text", this.year);
		this.loadMonth();
	},
	todayMonth: function(){
		var d = new Date();
		this.calendar.changeMonthTo(d);
		this.hide();
	},
	selectedMonth: function(td){
		var m = td.retrieve("month");
		var d = Date.parse(this.year+"/"+m+"/1");
		this.calendar.changeMonthTo(d);
		this.hide();
	},

	show: function(){
		this.date = this.calendar.date;
		this.year = this.date.get("year");
		this.loadMonth();
		this.monthSelectNode.setStyle("display", "block");
		this.hideFun = this.hide.bind(this);
		document.body.addEvent("mousedown", this.hideFun);
	},
	hide: function(){
		this.monthSelectNode.setStyle("display", "none");
		document.body.removeEvent("mousedown", this.hideFun);
	},

	destroy: function(){
		//this.titleNode.destroy();
		//this.titleNode = null;
		//this.titleDayNode = null;
		//this.titleInforNode = null;
		//
		//delete this.calendar.days[this.key];
		//
		//this.node.empty();
		//MWF.release(this);
	}

});

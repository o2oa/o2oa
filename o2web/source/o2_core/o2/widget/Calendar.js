o2.widget = o2.widget || {};
o2.widget.Calendar = o2.Calendar = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default",
		"path": o2.session.path+"/widget/$Calendar/" ,

		"defaultView": "day", //day, month, year
		"baseDate": new Date(),
		"secondEnable" : false,
		"timeSelectType" : "slider",
		"isTime": false,
		"isMulti": false,
		"before": null,
		"after": null,
		"timeOnly": false,
		"yearOnly" : false,
		"monthOnly": false,
		"defaultDate": null,

		"beforeCurrent": true,
		"clearEnable": true,

		"range": false,
		"rangeNodes": [],
		"rangeRule": "asc",  //asc + ,  des -
		"target": null,


		// "enableDate": function (date) {  //一个function，参数为日期，return true不可选
		// 	return date > new Date();
		// },
		// "enableHours": function (date) {
		// 	if( date > new Date() ){
		// 		return [];
		// 	}else{
		// 		return [[0, 8], [16, 24]];
		// 	}
		//}, //一个function, 参数为日期
		// "enableMinutes": null,  //一个function, 参数为日期，hour
		// "enableSeconds": null,  //一个function, 参数为日期，hour，minutes
	},
	initialize: function(node, options){
		Locale.use("zh-CHS");
		this.setOptions(options);
		this.options.defaultTime = ""+this.options.baseDate.getHours()+":"+this.options.baseDate.getMinutes()+":"+this.options.baseDate.getSeconds();

		this.path = o2.session.path+"/widget/$Calendar/";
		this.cssPath = o2.session.path+"/widget/$Calendar/"+this.options.style+"/css.wcss";

		this._loadCss();
		//	this.options.containerPath = this.path+this.style+"/container.html";
		//	this.options.dayPath = this.path+this.style+"/day.html";
		//	this.options.monthPath = this.path+this.style+"/month.html";
		//	this.options.yearPath = this.path+this.style+"/year.html";
		//	this.options.timePath = this.path+this.style+"/time.html";

		if (!this.options.format){
			if (this.options.isTime){
				//this.options.format = Locale.get("Date").shortDate + " " + Locale.get("Date").shortTime;
				if(this.options.timeOnly){
					this.options.format="%H:%M";
				}
				else{
					this.options.format = Locale.get("Date").shortDate + " " + "%H:%M";
				}
			}else{
				this.options.format = Locale.get("Date").shortDate;
			}
		}

		this.options.containerPath = this.options.path+this.options.style+"/container.html";
		this.options.dayPath = this.options.path+this.options.style+"/day.html";
		this.options.monthPath = this.options.path+this.options.style+"/month.html";
		this.options.yearPath = this.options.path+this.options.style+"/year.html";
		this.options.timePath = this.options.path+this.options.style+"/time.html";

		this.today = new Date();

		this.currentView = this.options.defaultView;

		this.node = $(node);

		this.visible = false;

		this.setRange();

		this.container = this.createContainer();


		this.container.inject((this.options.target) || $(document.body));

		this.contentTable = this.createContentTable();
		this.contentTable.inject(this.contentDateNode);

		this.addEvents();
		this.container.set({
			styles: {
				"display": "none",
				"opacity": 1
			}
		});
		this.fireEvent("init");

		//this.move = true;
		//this.containerDrag = new Drag.Move(this.container);

		if( layout.mobile ){
			this.maskNode = new Element("div.maskNode",{
				styles : {
					"width": "100%",
					"height": "100%",
					"opacity": 0.6,
					"position": "absolute",
					"background-color": "#CCC",
					"top": "0px",
					"left": "0px",
					"z-index" : 150,
					"-webkit-user-select": "none",
					"-moz-user-select": "none",
					"user-select" : "none"
				},
				events : {
					"touchmove" : function(ev){
						ev.stopPropagation();
						ev.preventDefault();
					}
				}
			}).inject((this.options.target) || $(document.body));
			this.container.addEvents({
				"touchmove" : function(ev){
					ev.stopPropagation();
					ev.preventDefault();
				},
				"touchend" : function(ev){
					ev.stopPropagation();
					//ev.preventDefault();
				}
			})
		}

	},
	addEvents: function(){
		this.node.addEvent("focus", function(){
			this.show();
		}.bind(this));
		this.node.addEvent("click", function(){
			this.show();
		}.bind(this));

		this.prevNode.addEvent("click", function(){
			this.getPrev();
		}.bind(this));

		this.nextNode.addEvent("click", function(){
			this.getNext();
		}.bind(this));

		this.currentTextNode.addEvent("click", function(){
			this.changeView();
		}.bind(this));

		if( !layout.mobile ){
			this.titleNode.addEvent("mousedown", function(){
				this.move();
			}.bind(this));
			this.titleNode.addEvent("mouseup", function(){
				this.unmove();
			}.bind(this));
		}

		document.addEvent('mousedown', this.outsideClick.bind(this));
	},

	move: function(){
		this.containerDrag = new Drag.Move(this.container, {
			"onDrag": function(e){
				if (this.iframe){
					var p = this.container.getPosition();
					this.iframe.setStyles({
						"top": ""+p.y+"px",
						"left": ""+p.x+"px"
					});
				}
			}.bind(this)
		});
	},
	unmove: function(){
		this.container.removeEvents("mousedown");
		this.titleNode.addEvent("mousedown", function(){
			this.move();
		}.bind(this));
	},
	changeView: function(){
		var view = "day";
		switch (this.currentView) {
			case "day" :
				this.changeViewToMonth();
				break;
			case "month" :
				this.changeViewToYear();
				break;
			case "year" :
				if( this.options.yearOnly ){
					break;
				}else if( this.options.monthOnly ){
					this.changeViewToMonth();
				}else{
					this.changeViewToDay();
				}
				break;
			case "time" :
				this.changeViewToDay();
				//this.changeViewToDay();
				break;
			default :
			//nothing;
		}
	},
	changeViewToMonth: function(year){
		this.currentView = "month";

		if (!this.contentMonthTable){
			this.contentMonthTable = this.createContentTable();
			this.contentMonthTable.inject(this.contentDateNode);
		}
		if (this.contentTable) this.contentTable.setStyle("display", "none");
		if (this.contentYearTable) this.contentYearTable.setStyle("display", "none");
		if (this.contentTimeTable) this.contentTimeTable.setStyle("display", "none");
		//	if (this.contentMonthTable) this.contentMonthTable.setStyle("display", "block");
		if (this.contentMonthTable) this.contentMonthTable.setStyle("display", "table");

		var year = (year!=undefined) ? year : this.currentTextNode.retrieve("year");
		var month = this.currentTextNode.retrieve("month");

		this.showMonth(year, month);
		this.fireEvent("changeViewToMonth");
	},
	changeViewToYear: function(year){
		this.currentView = "year";

		if (!this.contentYearTable){
			this.contentYearTable = this.createContentTable();
			this.contentYearTable.inject(this.contentDateNode);
		}
		if (this.contentTable) this.contentTable.setStyle("display", "none");
		if (this.contentMonthTable) this.contentMonthTable.setStyle("display", "none");
		if (this.contentTimeTable) this.contentTimeTable.setStyle("display", "none");
		//	if (this.contentYearTable) this.contentYearTable.setStyle("display", "block");
		if (this.contentYearTable) this.contentYearTable.setStyle("display", "table");

		this.showYear(year);
		this.fireEvent("changeViewToYear");
	},
	changeViewToDay: function(year, month){
		this.currentView = "day";

		if (!this.contentTable){
			this.contentTable = this.createContentTable();
			this.contentTable.inject(this.contentDateNode);
		}

		if (this.contentMonthTable) this.contentMonthTable.setStyle("display", "none");
		if (this.contentYearTable) this.contentYearTable.setStyle("display", "none");
		if (this.contentTimeTable) this.contentTimeTable.setStyle("display", "none");
		//	if (this.contentTable) this.contentTable.setStyle("display", "block");
		if (this.contentTable) this.contentTable.setStyle("display", "table");

		this.showDay(year, month);

		this.showMonthYearButton();

		this.fireEvent("changeViewToDay");
	},
	hideMonthYearButton : function(){
		if(this.clearButton_month){
			this.clearButton_month.hide();
		}
	},
	showMonthYearButton : function(){
		if( this.options.clearEnable && this.buttonArea && !this.clearButton_month ){
			this.container.setStyle("height","auto");
			this.clearButton_month = new Element("div", {"text": o2.LP.widget.clear }).inject(this.buttonArea);
			this.clearButton_month.addEvent("click", function(){
				var t = this.node.get("value");
				this.node.set("value", "");
				if( t )this.fireEvent("change");
				this.fireEvent("clear");
				this.hide();
			}.bind(this));
			this.clearButton_month.setStyles(this.css.calendarMonthActionButton);
		}
		if(this.clearButton_month){
			this.clearButton_month.show();
		}
	},
	getNext: function(){
		switch (this.currentView) {
			case "time" :
				this.getNextDate();
				break;
			case "day" :
				this.getNextDay();
				break;
			case "month" :
				this.getNextMonth();
				break;
			case "year" :
				this.getNextYear();
				break;
			default :
			//nothing
		}
	},

	getPrev: function(){
		switch (this.currentView) {
			case "time" :
				this.getPrevDate();
				break;
			case "day" :
				this.getPrevDay();
				break;
			case "month" :
				this.getPrevMonth();
				break;
			case "year" :
				this.getPrevYear();
				break;
			default :
			//nothing
		}
	},
	getNextDate: function(){
		var date = this.currentTextNode.retrieve("date");
		if( this.currentView === "time" && this.options.enableDate ){
			var d = date.clone().increment("day", 1);
			if( !this.isEnableDate(d) )return;
		}
		// var year = this.currentTextNode.retrieve("year");
		// var month = this.currentTextNode.retrieve("month");
		// month--;
		// var day = this.currentTextNode.retrieve("day");
		// var date = new Date(year, month, day);
		date.increment("day", 1);
		this._setTimeTitle(null, date);
		if( this.options.enableHours || this.options.enableMinutes || this.options.enableSeconds ){
			this._resetTimeDate();
		}
	},
	getPrevDate: function(){
		var date = this.currentTextNode.retrieve("date");
		if( this.currentView === "time" && this.options.enableDate ){
			var d = date.clone().increment("day", -1);
			if( !this.isEnableDate(d) )return;
		}
		date.increment("day", -1);
		this._setTimeTitle(null, date);
		if( this.options.enableHours || this.options.enableMinutes || this.options.enableSeconds ){
			this._resetTimeDate();
		}
	},
	getNextDay: function(){
		var year = this.currentTextNode.retrieve("year");
		var month = this.currentTextNode.retrieve("month");
		month--;
		var date = new Date(year, month, 1);
		date.increment("month", 1);

		var thisYear = date.getFullYear();
		var thisMonth = date.getMonth();

		this._setDayTitle(null, thisYear, thisMonth);
		this._setDayDate(null,thisYear, thisMonth);

		this.fireEvent("changeViewToDay");
	},

	getPrevDay: function(){
		var year = this.currentTextNode.retrieve("year");
		var month = this.currentTextNode.retrieve("month");
		month--;
		var date = new Date(year, month, 1);
		date.increment("month", -1)

		var thisYear = date.getFullYear();
		var thisMonth = date.getMonth();

		this._setDayTitle(null, thisYear, thisMonth);
		this._setDayDate(null,thisYear, thisMonth);
		this.fireEvent("changeViewToDay");
	},

	getNextMonth: function(){
		var year = this.currentTextNode.retrieve("year");
		var date = new Date(year, 1, 1);
		date.increment("year", 1)

		var thisYear = date.getFullYear();

		this.showMonth(thisYear);
		this.fireEvent("changeViewToMonth");
	},
	getPrevMonth: function(){
		var year = this.currentTextNode.retrieve("year");
		var date = new Date(year, 1, 1);
		date.increment("year", -1)

		var thisYear = date.getFullYear();

		this.showMonth(thisYear);
		this.fireEvent("changeViewToMonth");
	},
	getNextYear: function(){
		var year = this.currentTextNode.retrieve("year");
		var date = new Date(year, 1, 1);
		date.increment("year", this.yearLength)

		var thisYear = date.getFullYear();

		this.showYear(thisYear);
		this.fireEvent("changeViewToYear");
	},
	getPrevYear: function(){
		var year = this.currentTextNode.retrieve("year");
		var date = new Date(year, 1, 1);

		date.increment("year", 0-this.yearLength)

		var thisYear = date.getFullYear();

		this.showYear(thisYear);
		this.fireEvent("changeViewToYear");
	},

	outsideClick: function(e) {
		if(this.visible) {
			var elementCoords, targetCoords, page;
			if ( !layout.inBrowser && layout.userLayout && layout.userLayout.scale && layout.userLayout.scale!==1){
				elementCoords = this.container.getCoordinates( this.options.target );
				targetCoords  = this.node.getCoordinates( this.options.target );
				var containerSize = this.options.target.getPosition();
				page = e.page;

				// elementCoords.left = elementCoords.left * layout.userLayout.scale;
				// elementCoords.top = elementCoords.top * layout.userLayout.scale;
				//
				// targetCoords.left = targetCoords.left * layout.userLayout.scale;
				// targetCoords.top = targetCoords.top * layout.userLayout.scale;
				//
				// containerSize.x = containerSize.x * layout.userLayout.scale;
				// containerSize.y = containerSize.y * layout.userLayout.scale;

				page.x = page.x / layout.userLayout.scale - containerSize.x;
				page.y = page.y / layout.userLayout.scale - containerSize.y;

			}else{
				elementCoords = this.container.getCoordinates();
				targetCoords  = this.node.getCoordinates();
				page = e.page;
			}


			if(((page.x < elementCoords.left || page.x > (elementCoords.left + elementCoords.width)) ||
				(page.y < elementCoords.top || page.y > (elementCoords.top + elementCoords.height))) &&
				((page.x < targetCoords.left || page.x > (targetCoords.left + targetCoords.width)) ||
					(page.y < targetCoords.top || page.y > (targetCoords.top + targetCoords.height))) ) this.hide();

		}
	},

	hide: function(){
		if (this.visible){
//			if (!this.morph){
//				this.morph = new Fx.Morph(this.container, {"duration": 200});
//			}
			this.visible = false;
			//		this.changeViewToDay();
//			this.morph.start({"opacity": 0}).chain(function(){
			this.container.setStyle("display", "none");
			if (this.iframe) this.iframe.destroy();
			if (layout.desktop.offices){
				Object.each(layout.desktop.offices, function(office){
					office.show();
				});
			}
			if( this.maskNode ){
				this.maskNode.hide();
			}
//			}.bind(this));
			this.fireEvent("hide");
		}
	},
	show: function(){
		if (!this.visible){
			var dStr = this.node.get("value");
			if (dStr && Date.isValid(dStr)){
				this.options.baseDate = Date.parse(dStr.substr(0,10));
			}
			if(this.options.timeOnly){
				this.currentView = "time";
			}
			else{
				this.currentView = this.options.defaultView;
			}

			switch (this.currentView) {
				case "day" :
					this.changeViewToDay();
					break;
				case "month" :
					//this.showMonth();
					this.changeViewToMonth();
					break;
				case "year" :
					if( this.options.yearOnly ){
						this.changeViewToYear()
					}else{
						this.showYear();
					}
					break;
				case "time" :
					//this.showTime(this.options.baseDate);
					this.changeViewToTime(this.options.defaultDate);
					//this.changeViewToTime(this.options.baseDate);
					break;
				default :
					this.showDay();
			}

//			if (!this.morph){
//				this.morph = new Fx.Morph(this.container, {"duration": 200});
//			}
			this.container.setStyle("display", "block");

			this.setPosition();
			// var p = this.container.getPosition();
			// var s = this.container.getSize();
			// var zidx = this.container.getStyle("z-index");
			// this.iframe = new Element("iframe", {"styles":{
			//     "border": "0px",
			//     "margin": "0px",
			//     "padding": "0px",
			//     "opacity": 0,
			// "z-index": (zidx) ? zidx-1 : 0,
			// "top": ""+p.y+"px",
			//     "left": ""+p.x+"px",
			//     "width": ""+s.x+"px",
			//     "height": ""+s.y+"px",
			// "position": "absolute"
			// }}).inject(this.container, "before");

			if (layout.desktop.offices){
				Object.each(layout.desktop.offices, function(office){
					if (this.container.isOverlap(office.officeNode)){
						office.hide();
					}
				}.bind(this));
			}
			if( this.maskNode ){
				this.maskNode.show();
			}

//			this.morph.start({"opacity": 1}).chain(function(){
			this.visible = true;
//			}.bind(this));
			this.fireEvent("show");
		}
	},
	setPosition: function(){
		//if (this.container.position && (!layout || !layout.userLayout || !layout.userLayout.scale || layout.userLayout.scale===1) ){
			var postY = "bottom";
			var postX = "left";
			this.container.position({
				relativeTo: this.node,
				position: 'bottomLeft',
				edge: 'upperLeft'
			});
			//               var offsetPNode = this.node.getOffsetParent();

			var cp = this.container.getPosition(this.options.target || null);
			var cSize = this.container.getSize();
			//var fp = (this.options.target) ? this.options.target.getPosition() : $(document.body).getPosition()
			var fsize = (this.options.target) ? this.options.target.getSize() : $(document.body).getSize();

			//if (cp.y+cSize.y>fsize.y+fp.y){
			if (cp.y+cSize.y>fsize.y){
				// this.container.position({
				// 	relativeTo: this.node,
				// 	position: 'upperLeft',
				// 	edge: 'bottomLeft'
				// });
				postY = "upper";
			}

			if( cp.x+cSize.x>fsize.x ){
				postX = "right";
			}

			if( postY === "upper" && postX === "left" ){
				this.container.position({
					relativeTo: this.node,
					position: 'upperLeft',
					edge: 'bottomLeft'
				});
			}else if( postX === "right" ){
				if( postY === "bottom" ){
					this.container.position({
						relativeTo: this.node,
						position: 'bottomRight',
						edge: 'upperRight'
					});
				}else{
					this.container.position({
						relativeTo: this.node,
						position: 'upperRight',
						edge: 'bottomRight'
					});
				}
			}
			this.postY = postY;
			this.postX = postX;
		// }else{
		// 	var p = this.node.getPosition(this.options.target || null);
		// 	var size = this.node.getSize();
		// 	var containerSize = this.container.getSize();
		// 	var bodySize = (this.options.target) ? this.options.target.getSize() : $(document.body).getSize(); //$(document.body).getSize();
		//
		// 	bodySize.x = bodySize.x * layout.userLayout.scale;
		// 	bodySize.y = bodySize.y * layout.userLayout.scale;
		//
		// 	var left = p.x;
		// 	left = left * layout.userLayout.scale;
		// 	if ((left + containerSize.x + 40) > bodySize.x){
		// 		left = bodySize.x - containerSize.x - 40;
		// 	}
		//
		// 	var top = p.y+size.y+2;
		// 	top = top * layout.userLayout.scale;
		// 	if( top + containerSize.y > bodySize.y ){
		// 		top = bodySize.y - containerSize.y ;
		// 	}
		//
		//
		// 	this.container.setStyle("top", top);
		// 	this.container.setStyle("left", left);
		// }
	},
	showYear: function(year){
		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();

		var date = new Date(thisYear, 1, 1);
		date.increment("year", -2);
		var beginYear = date.getFullYear();
		date.increment("year", this.yearLength-1);
		var endYear = date.getFullYear();

		this._setYearTitle(null, beginYear, endYear, thisYear);
		this._setYearDate(null, beginYear, endYear, thisYear);

		this.showMonthYearButton();

		//	if (!this.move){
		//		this.move = true;
		//		this.containerDrag = new Drag.Move(this.container);
		//	}
	},
	_setYearTitle:function(node, beginYear, endYear, thisYear){
		var thisNode = node || this.currentTextNode;
		thisNode.set("text", beginYear+"-"+endYear);
		thisNode.store("year", thisYear);
		this.setTitleStyle();
	},
	_setYearDate: function(table, beginYear, endYear, year){
		var yearTable = table || this.contentYearTable;

		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();

		var tbody = yearTable.getElement("tbody");
		var tds = tbody.getElements("td");

		tds.each(function(item, idx){
			var y = beginYear+idx;
			item.set("text", y);
			item.store("year", y);
			if (y==this.options.baseDate.getFullYear()){
				item.addClass("current_"+this.options.style);
			}else{
				item.removeClass("current_"+this.options.style);
			}

			if( this.options.enableDate ){
				if( !this.isEnableDate(y+"-01-01") && !this.isEnableDate(y+"-12-12") ){
					// item.addClass("disable_"+this.options.style);
					item.setStyles(this.css["disable_"+this.options.style]);
				}else{
					// item.removeClass("disable_"+this.options.style);
					item.setStyles(this.css["notdisable_"+this.options.style]);
				}
			}
		}.bind(this));
	},
	showMonth: function(year, month){
		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();
		var thisMonth = (month!=undefined) ? month : this.options.baseDate.getMonth();

		this._setMonthTitle(null, thisYear, thisMonth);
		this._setMonthDate(null, thisYear, thisMonth);

		this.showMonthYearButton();

		//	if (!this.move){
		//		this.move = true;
		//		this.containerDrag = new Drag.Move(this.container);
		//	}
	},
	_setMonthTitle:function(node, year){
		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();
		var thisNode = node || this.currentTextNode;
		thisNode.set("text", thisYear);
		thisNode.store("year", thisYear);
		this.setTitleStyle();
	},
	_setMonthDate: function(table, year, month){
		//var months = Locale.get("Date").months;
		var months = o2.LP.widget.months;
		var monthTable = table || this.contentMonthTable;

		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();
		var thisMonth = (month!=undefined) ? month : this.options.baseDate.getMonth();

		var tbody = monthTable.getElement("tbody");
		var tds = tbody.getElements("td");

		tds.each(function(item, idx){
			item.set("text", months[idx].substr(0,2));
			item.store("year", thisYear);
			item.store("month", idx);
			if ((thisYear==this.options.baseDate.getFullYear()) && (idx==this.options.baseDate.getMonth())){
				item.addClass("current_"+this.options.style);
			}else{
				item.removeClass("current_"+this.options.style);
			}

			if( this.options.enableDate ){
				var flag = false;
				var m = this.addZero(idx + 1, 2 );
				if (this.isEnableDate(thisYear+"-"+m+"-01")){
					flag = true;
				}else if(this.isEnableDate(thisYear+"-"+m+"-"+new Date(thisYear+"-"+m+"-01").get('lastdayofmonth'))){
					flag = true;
				}
				if( !flag ){
					// item.addClass("disable_"+this.options.style);
					item.setStyles(this.css["disable_"+this.options.style]);
				}else{
					// item.removeClass("disable_"+this.options.style);
					item.setStyles(this.css["notdisable_"+this.options.style]);
				}
			}
		}.bind(this));
	},

	showDay: function(year, month){
		this._setDayTitle(null, year, month);
		this._setDayWeekTitleTh();
		this._setDayDate(null, year, month);

		//	if (!this.move){
		//		this.move = true;
		//		this.containerDrag = new Drag.Move(this.container);
		//	}
	},
	_setDayTitle: function(node, year, month){
		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();
		var thisMonth = (month!=undefined) ? month : this.options.baseDate.getMonth();
		thisMonth++;

		var text = thisYear+ o2.LP.widget.year +thisMonth+ o2.LP.widget.month;
		var thisNode = node || this.currentTextNode;
		thisNode.set("text", text);

		thisNode.store("year", thisYear);
		thisNode.store("month", thisMonth);
		this.setTitleStyle();
	},
	_setDayDate: function(table, year, month){
		var dayTable = table || this.contentTable;
		var baseDate = this.options.baseDate;
		if ((year!=undefined) && (month!=undefined)){
			baseDate = new Date();
			baseDate.setDate(1);
			baseDate.setFullYear(year);
			baseDate.setMonth(month);
			baseDate.setHours( this.options.baseDate.getHours() );
			baseDate.setMinutes( this.options.baseDate.getMinutes() );
			baseDate.setSeconds( this.options.baseDate.getSeconds() );
		}

		var tbody = dayTable.getElement("tbody");
		var tds = tbody.getElements("td");

		var firstDate = baseDate.clone();
		firstDate.setDate(1);
		var day = firstDate.getDay();

		var tmpDate = firstDate.clone();
		for (var i=day-1; i>=0; i--){

			if( this.options.enableDate ){
				if( !this.isEnableDate(firstDate) ){
					tds[i].addClass("disable_"+this.options.style);
					tds[i].setStyles(this.css["disable_"+this.options.style]);
				}else{
					tds[i].removeClass("disable_"+this.options.style);
					tds[i].setStyles(this.css["notdisable_"+this.options.style]);
				}
			}

			tmpDate.increment("day", -1);
			tds[i].set("text", tmpDate.getDate());
			tds[i].addClass("gray_"+this.options.style);
			tds[i].setStyles(this.css["gray_"+this.options.style]);
			tds[i].store("dateValue", tmpDate.toString());
			tds[i].removeClass("today_"+this.options.style);
			tds[i].removeClass("current_"+this.options.style);
			tds[i].removeClass("past_"+this.options.style);
			if( this.options.todayClass ){
				tds[i].removeClass( this.options.todayClass );
			}
		}

		for (var i=day; i<tds.length; i++){
			tds[i].set("text", firstDate.getDate());

			if( this.options.enableDate ){
				if( !this.isEnableDate(firstDate) ){
					// tds[i].addClass("disable_"+this.options.style);
					tds[i].setStyles(this.css["disable_"+this.options.style]);
				}else{
					// tds[i].removeClass("disable_"+this.options.style);
					tds[i].setStyles(this.css["notdisable_"+this.options.style]);
				}
			}

			if (firstDate.toString() == this.options.baseDate.toString()){
				tds[i].addClass("current_"+this.options.style);
				tds[i].setStyles(this.css["current_"+this.options.style]);
				tds[i].removeClass("past_"+this.options.style);
				tds[i].removeClass("gray_"+this.options.style);
				tds[i].setStyle("border", "1px solid #FFF");
			}else if (firstDate.getMonth()!=baseDate.getMonth()){
				tds[i].addClass("gray_"+this.options.style);
				tds[i].setStyles(this.css["gray_"+this.options.style]);
				tds[i].removeClass("current_"+this.options.style);
				tds[i].removeClass("past_"+this.options.style);
				tds[i].setStyle("border", "1px solid #FFF");
			}else{
				tds[i].setStyles(this.css["normal_"+this.options.style]);
				tds[i].removeClass("current_"+this.options.style);
				tds[i].removeClass("gray_"+this.options.style);
				tds[i].removeClass("past_"+this.options.style);
				tds[i].setStyle("border", "1px solid #FFF");
			}
			var tmp = firstDate.clone();
			if (tmp.clearTime().toString() == this.today.clearTime().toString()){
				tds[i].addClass("today_"+this.options.style);
				tds[i].removeClass("past_"+this.options.style);
				tds[i].setStyles(this.css["today_"+this.options.style]);
				tds[i].setStyle("border", "0px solid #AAA");
				if( this.options.todayClass ){
					tds[i].addClass( this.options.todayClass );
				}
			}else{
				tds[i].removeClass("today_"+this.options.style);
				if( this.options.todayClass ){
					tds[i].removeClass( this.options.todayClass );
				}
			}
			if (tmp.diff(this.today)>0){
				if (this.css["past_"+this.options.style]) tds[i].setStyles(this.css["past_"+this.options.style]);
				tds[i].addClass("past_"+this.options.style);
			}


			tds[i].store("dateValue", firstDate.toString());
			firstDate.increment("day", 1);
		}
	},
	changeViewToTime: function(date){
		this.currentView = "time";

		if (!this.contentTimeTable){
			this.contentTimeTable = this.createContentTable();
			this.contentTimeTable.inject(this.contentDateNode);
		}
		if (this.contentTable) this.contentTable.setStyle("display", "none");
		if (this.contentYearTable) this.contentYearTable.setStyle("display", "none");
		if (this.contentMonthTable) this.contentMonthTable.setStyle("display", "none");
		if (this.contentTimeTable) this.contentTimeTable.setStyle("display", "block");
		//	if (this.contentTimeTable) this.contentTimeTable.setStyle("display", "table");

		var thisDate = date || this.options.baseDate;

		this.showTime(thisDate);

		this.hideMonthYearButton();
	},

	showTime: function(date){
		var thisHour = (date || this.options.baseDate).getHours();
		var thisMinutes = (date || this.options.baseDate).getMinutes();
		var thisSeconds = (date || this.options.baseDate).getSeconds();
		// var times = this.options.defaultTime.split(":");
		//
		// var thisHour = (times[0]) ? times[0] : "0";
		// var thisMinutes = (times[1]) ? times[1] : "0";
		// var thisSeconds = (times[2]) ? times[2] : "0";

		this._setTimeTitle(null, date);

		if( this.options.style.indexOf("mobile") > -1 ){
			this._setTimeDate_mobile(null, thisHour, thisMinutes, thisSeconds);
		}else{
			this._setTimeDate(null, thisHour, thisMinutes, thisSeconds);
		}


		//	if (this.move){
		//		this.move = false;
		//		this.container.removeEvents("mousedown");
		//	}
	},

	_setTimeTitle: function(node, date){
		var thisDate = date || this.options.baseDate;
		var thisNode = node || this.currentTextNode;

		var y = thisDate.getFullYear();
		var m = thisDate.getMonth()+1;
		var d = thisDate.getDate();
		var text = "" + y + o2.LP.widget.year + m + o2.LP.widget.month + d + o2.LP.widget.date;

		if (this.options.timeOnly){
			thisNode.hide();
			if (this.prevNode) this.prevNode.hide();
			if (this.nextNode) this.nextNode.hide();
		}
		thisNode.set("text", text);
		thisNode.store("date", date);
		this.cDate = date;
		this.setTitleStyle();
	},
	_setTimeDate_mobile: function(node, h, m, s){
		var _self = this;

		this.itmeHNode = this.contentTimeTable.getElement(".MWF_calendar_time_h");
		this.itmeMNode = this.contentTimeTable.getElement(".MWF_calendar_time_m");
		this.itmeSNode = this.contentTimeTable.getElement(".MWF_calendar_time_s");


		this.showActionNode = this.contentTimeTable.getElement(".MWF_calendar_action_show");

		var calendar = this;
		var div, items;

		this.calculateCurrentHour(h);
		if( this.hMobileSelect ){
			this.itmeHNode.empty();
			this.hMobileSelect = null;
		}
		items = [];
		for( var i=0; i<24; i++ ) {
			div = new Element("div.hselect", {
				"text": this.addZero(i, 2),
				"styles": this.css.calendarTimeSelectItem_mobile
			}).inject(this.itmeHNode);
			div.store("d", i);
			if (!this.isEnableHour(this.cDate, i)){
				div.hide();
			}else{
				items.push(i);
			}
		}
		this.selectedHour = this.cHour; //this.addZero(h, 2 );
		this.hMobileSelect = new o2.Calendar.MobileSelect( this.itmeHNode.getParent(), {
			"lineHeight" : 40,
			"items" : items, //24,
			"currentItem" : parseInt( this.cHour ),
			"onChange": function(value){
				this.selectedHour = value; //this.addZero(value, 2 );
				this.cHour = this.selectedHour;
				if( this.options.enableMinutes )this._resetMinuteSelect_mobile();
				if( this.options.enableSeconds )this._resetSecondSelect_mobile();
				//this.showHNode.set("text", this.addZero(i, 2 ));
				//this.itmeHNode.getFirst().set("text", this.addZero(i, 2 ));
			}.bind(this)
		});
		this.hMobileSelect.load();

		this.calculateCurrentMinute(m);
		if( this.mMobileSelect ){
			this.itmeMNode.empty();
			this.mMobileSelect = null;
		}
		items = [];
		for (var i = 0; i < 60; i++) {
			div = new Element("div.mselect", {
				"text": this.addZero(i, 2),
				"styles": this.css.calendarTimeSelectItem_mobile
			}).inject(this.itmeMNode);
			div.store("d", i);
			if (!this.isEnableMinute(this.cDate, this.cHour, i)) {
				div.hide();
			}else{
				items.push(i);
			}
		}
		this.selectedMinute = this.cMinute; //this.addZero(m, 2);
		this.mMobileSelect = new o2.Calendar.MobileSelect(this.itmeMNode.getParent(), {
			"lineHeight": 40,
			"items": items, //60,
			"currentItem": parseInt(this.cMinute),
			"onChange": function (value) {
				this.selectedMinute = value; //this.addZero(value, 2);
				//this.showHNode.set("text", this.addZero(i, 2 ));
				//this.itmeHNode.getFirst().set("text", this.addZero(i, 2 ));
				this.cMinute = this.selectedMinute;
				if( this.options.enableSeconds )this._resetSecondSelect_mobile();
			}.bind(this)
		});
		this.mMobileSelect.load();

		if(this.options.secondEnable ){
			this.calculateCurrentSecond(s);
			if( this.sMobileSelect ){
				this.itmeSNode.empty();
				this.sMobileSelect = null;
			}
			items = [];
			for( var i=0; i<60; i++ ){
				div = new Element("div.sselect",{
					"text" : this.addZero(i, 2 ),
					"styles" : this.css.calendarTimeSelectItem_mobile
				}).inject( this.itmeSNode );
				div.store("d", i);
				if (!this.isEnableMinute(this.cDate, this.cHour, this.cMinute, i)) {
					div.hide();
				}else{
					items.push(i);
				}
			}
			this.selectedSecond = this.cSecond; //this.addZero(s, 2 );
			this.sMobileSelect = new o2.Calendar.MobileSelect( this.itmeSNode.getParent(), {
				"lineHeight" : 40,
				"items" : items, //60,
				"currentItem" : parseInt(this.cSecond),
				"onChange": function(value){
					this.selectedSecond = value; //this.addZero(value, 2 );
					//this.showHNode.set("text", this.addZero(i, 2 ));
					//this.itmeHNode.getFirst().set("text", this.addZero(i, 2 ));
					this.cSecond = this.selectedSecond;
				}.bind(this)
			});
			this.sMobileSelect.load();
		}else{
			this.itmeSNode.hide();

		}


		if( this.options.secondEnable ){
			this.contentTimeTable.getElements(".calendarTimeWheel_mobile").setStyle("width","33.3%");
		}else{
			this.contentTimeTable.getElements(".calendarTimeWheel_mobile").setStyle("width","50%");
		}

		if( this.options.secondEnable && this.showSNode ){
			this.showSNode.set("text", this.addZero( s.toInt(), 2) );
		}

		if (!this.okButton){
			this.okButton = new Element("button.mainColor_bg", {"text": o2.LP.widget.ok }).inject(this.showActionNode);
			this.okButton.addEvent("click", function(){
				this._selectTime();
				this.hide();
			}.bind(this));
			this.okButton.setStyles(this.css.calendarActionShowButton_mobile_ok);
		}

		if (!this.clearButton && this.options.clearEnable){
			this.clearButton = new Element("button", {"text": o2.LP.widget.clear }).inject(this.showActionNode);
			this.clearButton.addEvent("click", function(){
				var t = this.node.get("value");
				this.node.set("value", "");
				if( t )this.fireEvent("change");
				this.fireEvent("clear");
				this.hide();
			}.bind(this));
			this.clearButton.setStyles(this.css.calendarActionShowButton_mobile_cancel);
		}
	},
	_setTimeDate: function(node, h, m, s){
		if( !this.options.secondEnable ){
			var div = this.contentTimeTable.getElement(".MWF_calendar_time_s");
			if( div )div.hide();
			div = this.contentTimeTable.getElement(".MWF_calendar_time_show_s");
			if( div )div.hide();
		}
		this.itmeHNode = this.contentTimeTable.getElement(".MWF_calendar_time_h_slider");
		this.itmeMNode = this.contentTimeTable.getElement(".MWF_calendar_time_m_slider");
		this.itmeSNode = this.contentTimeTable.getElement(".MWF_calendar_time_s_slider");


		this.timeShowNode = this.contentTimeTable.getElement(".MWF_calendar_time_show");

		this.timeShowNode.addEvent("click", function(){
			this._selectTime();
		}.bind(this));

		this.showHNode = this.contentTimeTable.getElement(".MWF_calendar_time_show_h");
		this.showMNode = this.contentTimeTable.getElement(".MWF_calendar_time_show_m");
		this.showSNode = this.contentTimeTable.getElement(".MWF_calendar_time_show_s");

		this.showActionNode = this.contentTimeTable.getElement(".MWF_calendar_action_show");

		var calendar = this;

		if ( COMMON.Browser.Platform.isMobile ){
			this.showHNode.set("text", this.addZero( h.toInt(), 2) );
			this.showMNode.set("text", this.addZero( m.toInt(), 2));
			if( this.options.secondEnable && this.showSNode ){
				this.showSNode.set("text", this.addZero( s.toInt(), 2) );
			}
		}else{
			if(this.options.timeSelectType === "select"){
				this.loadHourSelect(h);
				this.loadMinuteSelect(m);
				this.loadSecondSelect(s);
			}else {
				this.calculateCurrentHour(h);
				this.createDisabledNodes(this.itmeHNode, 24, "h");
				this.hSlider = new Slider(this.itmeHNode, this.itmeHNode.getFirst(), {
					range: [0, 23],
					initialStep: this.cHour,
					onChange: function(value){
						var v = value.toInt();
						if( this.isEnableHour(this.cDate, v) ){
							this.selectedHour = v;
							this.cHour = v;
							this.showHNode.set("text", this.addZero(v, 2));
							this.itmeHNode.getFirst().set("text", this.addZero(v, 2));

							if( this.options.enableMinutes ){
								this.calculateCurrentMinute();
								this.createDisabledNodes(this.itmeMNode, 60, "m");
								this.mSlider.set( this.cMinute );
								this.itmeMNode.getFirst().set("text", this.addZero( this.cMinute, 2));
								this.showMNode.set("text", this.addZero( this.cMinute, 2));
							}
							if( this.options.enableSeconds && this.sSlider ){
								this.calculateCurrentSecond();
								this.createDisabledNodes(this.itmeSNode, 60, "s");
								this.sSlider.set( this.cSecond );
								this.itmeSNode.getFirst().set("text", this.addZero( this.cSecond, 2));
								this.showSNode.set("text", this.addZero( this.cSecond, 2) );
							}
						}
					}.bind(this)
				});
				// if( this.options.enableHours ){
				// 	this.hSlider.setRange( this.getEnableHourStartEnd(this.cDate) );
				// }
				this.itmeHNode.getFirst().set("text", this.addZero( this.cHour, 2));
				this.showHNode.set("text", this.addZero( this.cHour, 2) );

				this.calculateCurrentMinute(m);
				this.createDisabledNodes(this.itmeMNode, 60, "m");
				this.mSlider = new Slider(this.itmeMNode, this.itmeMNode.getFirst(), {
					range: [0, 59],
					initialStep: this.cMinute,
					onChange: function(value){
						var v = value.toInt();
					    if( this.isEnableMinute(this.cDate, this.cHour, v) ){
                            this.selectedMinute = v;
                            this.cMinute = v;
                            this.showMNode.set("text", this.addZero( v, 2));
                            this.itmeMNode.getFirst().set("text", this.addZero( v, 2));

							if( this.options.enableSeconds && this.sSlider ){
								this.calculateCurrentSecond();
								this.createDisabledNodes(this.itmeSNode, 60, "s");
								this.sSlider.set( this.cSecond );
								this.itmeSNode.getFirst().set("text", this.addZero( this.cSecond, 2));
								this.showSNode.set("text", this.addZero( this.cSecond, 2) );
							}
						}
					}.bind(this)
				});
				this.itmeMNode.getFirst().set("text", this.addZero( this.cMinute, 2));
				this.showMNode.set("text", this.addZero( this.cMinute, 2));

				if( this.options.secondEnable && this.itmeSNode ){
					this.calculateCurrentSecond(s);
					this.createDisabledNodes(this.itmeSNode, 60, "s");
					this.sSlider = new Slider(this.itmeSNode, this.itmeSNode.getFirst(), {
						range: [0, 59],
						initialStep: this.cSecond,
						onChange: function(value){
							var v = value.toInt();
                            if( this.isEnableSecond(this.cDate, this.cHour, this.cMinute, v) ){
                                this.selectedSecond = v;
                                this.cSecond = v;
                                this.showSNode.set("text", this.addZero( v, 2));
                                this.itmeSNode.getFirst().set("text", this.addZero( v, 2));
						    }
						}.bind(this)
					});
					this.itmeSNode.getFirst().set("text", this.addZero( this.cSecond, 2));
					this.showSNode.set("text", this.addZero( this.cSecond, 2) );
				}

				// this.showHNode.set("text", this.addZero( h.toInt(), 2) );
				// this.showMNode.set("text", this.addZero( m.toInt(), 2));
				// if( this.options.secondEnable && this.showSNode ){
				// 	this.showSNode.set("text", this.addZero( s.toInt(), 2) );
				// }
			}

		}

		if (!this.okButton){
			this.okButton = new Element("button", {"text": o2.LP.widget.ok}).inject(this.showActionNode);
			this.okButton.addEvent("click", function(){
				this._selectTime();
				this.hide();
			}.bind(this));
			this.okButton.setStyles(this.css.calendarActionShowButton);
		}

		if (!this.clearButton && this.options.clearEnable){
			this.clearButton = new Element("button", {"text": o2.LP.widget.clear }).inject(this.showActionNode);
			this.clearButton.addEvent("click", function(){
				var t = this.node.get("value");
				this.node.set("value", "");
				if( t )this.fireEvent("change");
				this.fireEvent("clear");
				this.hide();
			}.bind(this));
			this.clearButton.setStyles(this.css.calendarActionShowButton);
		}
	},
	_resetTimeDate: function(){
		if( this.options.style.indexOf("mobile") > -1 ){
			if( this.options.enableHours )this._resetHourSelect_mobile();
            if( this.options.enableMinutes )this._resetMinuteSelect_mobile();
			if( this.options.enableSeconds )this._resetSecondSelect_mobile();
		}else{
			if(this.options.timeSelectType === "select"){
				if( this.options.enableHours )this.loadHourSelect();
				if( this.options.enableMinutes )this.loadMinuteSelect();
				if( this.options.enableSeconds )this.loadSecondSelect();
			}else {
				if( this.options.enableHours ){
					this.calculateCurrentHour();
					this.createDisabledNodes(this.itmeHNode, 24, "h");
					this.hSlider.set( this.cHour );
					this.itmeHNode.getFirst().set("text", this.addZero( this.cHour, 2));
					this.showHNode.set("text", this.addZero( this.cHour, 2) );
				}
				if( this.options.enableMinutes ){
					this.calculateCurrentMinute();
					this.createDisabledNodes(this.itmeMNode, 60, "m");
					this.mSlider.set( this.cMinute );
					this.itmeMNode.getFirst().set("text", this.addZero( this.cMinute, 2));
					this.showMNode.set("text", this.addZero( this.cMinute, 2));
				}
				if( this.options.enableSeconds && this.sSlider ){
					this.calculateCurrentSecond();
					this.createDisabledNodes(this.itmeSNode, 60, "s");
					this.sSlider.set( this.cSecond );
					this.itmeSNode.getFirst().set("text", this.addZero( this.cSecond, 2));
					this.showSNode.set("text", this.addZero( this.cSecond, 2) );
				}
			}
		}

	},
	_resetHourSelect_mobile: function(){
		if( this.options.enableHours ){
			this.calculateCurrentHour();
			var items = [];
			this.itmeHNode.getElements(".hselect").each(function(div, i){
				if( this.isEnableHour(this.cDate, div.retrieve("d")) ){
					items.push(i);
					div.show();
				}else{
					div.hide();
				}
			}.bind(this));
			this.selectedHour = this.cHour;
			this.hMobileSelect.resetItems(items, parseInt(this.cHour));
		}
	},
	_resetMinuteSelect_mobile: function(){
		if( this.options.enableMinutes ) {
			this.calculateCurrentMinute();
			var items = [];
			this.itmeMNode.getElements(".mselect").each(function(div, i){
				if( this.isEnableMinute(this.cDate, this.cHour, div.retrieve("d")) ){
					items.push(i);
					div.show();
				}else{
					div.hide();
				}
			}.bind(this));
			this.selectedMinute = this.cMinute;
			this.mMobileSelect.resetItems(items, parseInt(this.cMinute));
		}
	},
	_resetSecondSelect_mobile: function(){
		if( this.options.enableSeconds && this.options.secondEnable ){
			this.calculateCurrentSecond();
			var items = [];
			this.itmeSNode.getElements(".sselect").each(function(div, i){
				if( this.isEnableSecond(this.cDate, this.cHour, this.cMinute, div.retrieve("d")) ){
					items.push(i);
					div.show();
				}else{
					div.hide();
				}
			}.bind(this));
			this.selectedSecond = this.cSecond;
			if(this.sMobileSelect)this.sMobileSelect.resetItems(items, parseInt(this.cSecond));
		}
	},
	loadHourSelect: function (h) {
		this.itmeHNode = this.contentTimeTable.getElement(".MWF_calendar_time_h").empty();
		new Element("span",{"text": o2.LP.widget.hour + "："}).inject(this.itmeHNode);
		this.itmeSelectHNode = new Element("select").inject(this.itmeHNode);
		this.calculateCurrentHour(h);
		for( var i=0; i<24; i++ ){
			if( this.isEnableHour(this.cDate, i) ){
				var opt = new Element("option",{
					"text" : this.addZero(i, 2 ),
					"value" : this.addZero(i, 2 ),
					"styles" : this.css.calendarTimeSelectItem_mobile
				}).inject( this.itmeSelectHNode );
				if (Browser.name === "firefox") {
					opt.addEvent( "mousedown", function(e){ e.stopPropagation(); });
				}
				if( i === this.cHour )opt.set("selected", true);
			}
		}
		// this.itmeSelectHNode.set("value",this.addZero( this.cHour, 2));
		this.showHNode.set("text", this.addZero( this.cHour, 2) );
		this.itmeSelectHNode.addEvent("change",function(){
			this.cHour = this.itmeSelectHNode.get("value").toInt();
			this.selectedHour = this.cHour;
			this.showHNode.set("text", this.itmeSelectHNode.get("value") );
			if( this.options.enableMinutes )this.loadMinuteSelect();
			if( this.options.enableSeconds )this.loadSecondSelect();
		}.bind(this));
	},
	loadMinuteSelect: function (m) {
		this.itmeMNode = this.contentTimeTable.getElement(".MWF_calendar_time_m").empty();
		new Element("span", {"text": o2.LP.widget.minute + "："}).inject(this.itmeMNode);
		this.itmeSelectMNode = new Element("select").inject(this.itmeMNode);
		this.calculateCurrentMinute(m);
		for (var i = 0; i < 60; i++) {
			if (this.isEnableMinute(this.cDate, this.cHour || 0, i)) {
				var opt = new Element("option", {
					"text": this.addZero(i, 2),
					"value": this.addZero(i, 2),
					"styles": this.css.calendarTimeSelectItem_mobile
				}).inject(this.itmeSelectMNode);
				if (Browser.name === "firefox") {
					opt.addEvent( "mousedown", function(e){ e.stopPropagation(); });
				}
				if( i === this.cMinute )opt.set("selected", true);
			}
		}
		// this.itmeSelectMNode.set("value", this.addZero(this.cMinute, 2));
		this.showMNode.set("text", this.addZero( this.cMinute, 2) );
		this.itmeSelectMNode.addEvent("change", function () {
			this.cMinute = this.itmeSelectMNode.get("value").toInt();
			this.selectedMinute = this.cMinute;
			this.showMNode.set("text", this.itmeSelectMNode.get("value"));
			if( this.options.enableSeconds )this.loadSecondSelect();
		}.bind(this));
	},
	loadSecondSelect: function (s) {
		this.itmeSNode = this.contentTimeTable.getElement(".MWF_calendar_time_s").empty();
		this.cSecond = typeOf(s) !== "null" ? s.toInt() : 0;
		if( this.options.secondEnable && this.itmeSNode ){
			new Element("span",{"text":o2.LP.widget.second + "："}).inject(this.itmeSNode);
			this.itmeSelectSNode = new Element("select").inject(this.itmeSNode);
			this.calculateCurrentSecond(s);
			for( var i=0; i<60; i++ ){
				if( this.isEnableSecond(this.cDate, this.cHour || 0, this.cMinute || 0, i) ){
					var opt = new Element("option",{
						"text" : this.addZero(i, 2 ),
						"value" : this.addZero(i, 2 ),
						"styles" : this.css.calendarTimeSelectItem_mobile,
						"events": { click: function(e){ e.stopPropagation(); e.preventDefault(); } }
					}).inject( this.itmeSelectSNode );
					if (Browser.name === "firefox") {
						opt.addEvent( "mousedown", function(e){ e.stopPropagation(); });
					}
					if( i === this.cSecond )opt.set("selected", true);
				}
			}
			// this.itmeSelectSNode.set("value",this.addZero( this.cSecond, 2));
			this.showSNode.set("text", this.addZero( this.cSecond, 2) );
			this.itmeSelectSNode.addEvent("change",function(){
				this.cSecond = this.itmeSelectSNode.get("value").toInt();
			    this.selectedSecond = this.cSecond;
				this.showSNode.set("text", this.itmeSelectSNode.get("value") );
			}.bind(this));
		}
	},
	calculateCurrentHour: function(h){
		if( typeOf(h) !== "null" ){
			this.cHour = h.toInt();
		}else if( typeOf(this.selectedHour) !== "null" ){
			this.cHour = this.selectedHour;
		}else{
			this.cHour = 0;
		}
		if( !this.isEnableHour(this.cDate, this.cHour) ) {
			var eHours = this.getEnableHours(this.cDate);
			if( eHours.length ){
				this.cHour = typeOf( eHours[0] ) === "array" ? eHours[0][0] : eHours[0];
			}else{
				this.cHour = 0;
			}
		}
		this.selectedHour = this.cHour;
		return this.cHour;
	},
	calculateCurrentMinute: function(m){
		if( typeOf(m) !== "null" ){
			this.cMinute = m.toInt();
		}else if( typeOf(this.selectedMinute) !== "null" ){
			this.cMinute = this.selectedMinute;
		}else{
			this.cMinute = 0;
		}
		if( !this.isEnableMinute(this.cDate, this.cHour, this.cMinute) ){
			var eMinutes = this.getEnableMinutes(this.cDate, this.cHour);
			if( eMinutes.length ){
				this.cMinute = typeOf( eMinutes[0] ) === "array" ? eMinutes[0][0] : eMinutes[0]
			}else{
				this.cMinute = 0;
			}
		}
		this.selectedMinute = this.cMinute;
		return this.cMinute;
	},
	calculateCurrentSecond: function(s){
		if( typeOf(s) !== "null" ){
			this.cSecond = s.toInt();
		}else if( typeOf(this.selectedSecond) !== "null" ){
			this.cSecond = this.selectedSecond;
		}else{
			this.cSecond = 0;
		}
		if( !this.isEnableSecond(this.cDate, this.cHour, this.cMinute, this.cSecond) ){
			var eSeconds = this.getEnableSeconds(this.cDate, this.cHour, this.cMinute);
			if( eSeconds.length ){
				this.cSecond = typeOf( eSeconds[0] ) === "array" ? eSeconds[0][0] : eSeconds[0]
			}else{
				this.cSecond = 0;
			}
		}
		this.selectedSecond = this.cSecond;
		return this.cSecond;
	},
	createDisabledNodes: function(area, length, type){
		area.getElements(".disable_node").destroy();
		var array;
		switch(type){
			case "h": array = this.getDisabledHours(this.cDate); break;
			case "m": array = this.getDisabledMinutes(this.cDate, this.cHour || 0); break;
			case "s": array = this.getDisabledSeconds(this.cDate, this.cHour || 0, this.cMinute || 0); break;
		}
		if( !array || !array.length )return false;
		area.setStyle("position", "relative");
		if( typeOf(array[0]) === "array" ){
			for( var i=0; i< array.length; i++ ){
				this.createDisabledNode( area, length, array[i] );
			}
		}else{
			this.createDisabledNode( area, length, array );
		}
	},
	createDisabledNode: function(area, length, range){
		var s = area.getSize();
		var width = s.x / length * (range[1] - range[0] + 1);
		var left = s.x / length * range[0];
		new Element("div.disable_node", {
			styles: {
				"position": "absolute",
				"background": "#ccc",
				"width": width+"px",
				"height": s.y,
				"left": left,
				"top": "0px"
			}
		}).inject(area);
	},
	addZero : function( str, length ){
		var zero = "";
		str = str.toString();
		for( var i=0; i<length; i++ ){
			zero = zero + "0";
		}
		var s = zero + str;
		return s.substr(s.length - length, length );
	},
	_selectTime: function(){
		var date = this.currentTextNode.retrieve("date");

		var h = typeOf(this.selectedHour) !== "null" ?  this.selectedHour : this.showHNode.get("text");
		var m = typeOf(this.selectedMinute) !== "null" ?  this.selectedMinute : this.showMNode.get("text");
		date.setHours(h);
		date.setMinutes(m);

		if( this.options.secondEnable && ( typeOf(this.selectedSecond) !== "null" || this.showSNode) ){
			var s = typeOf(this.selectedSecond) !== "null" ?  this.selectedSecond : this.showSNode.get("text");
			date.setSeconds(s);
		}

		if (!this.options.beforeCurrent){
			var now = new Date();
			if (date.getTime()-now.getTime()<0){
				alert( o2.LP.widget.dateGreaterThanCurrentNotice );
				this.node.focus();
				return false;
			}
		}

		var dv = date.format(this.options.format);

		if (this.fireEvent("queryComplate", [dv, date])){
			var t = this.node.get("value");

			this.options.defaultTime = ""+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();

			this.node.set("value", dv);
			//	this.node.focus();
			this.hide();
			if (t!=dv) this.fireEvent("change", [dv, date, t]);
			this.fireEvent("complate", [dv, date]);
		}
	},
	_selectDate: function(dateStr){
		if( this.options.yearOnly ){
			dateStr = dateStr+"-01-01"
		}else if( this.options.monthOnly ){
			dateStr = dateStr+"-01"
		}
		var date = new Date(dateStr);
		this.options.baseDate = date;
		var dv = date.format(this.options.format);
		if (this.options.isTime){
			this.changeViewToTime(date);
		}else{
			if (!this.options.beforeCurrent){
				var now = new Date();
				date.setHours(23,59,59);
				if (date.getTime()-now.getTime()<0){
					alert( o2.LP.widget.dateGreaterThanCurrentNotice );
					this.node.focus();
					return false;
				}
			}
			if (this.fireEvent("queryComplate", [dv, date])){
				var t = this.node.get("value");
				this.node.set("value", dv);
				this.hide();
				if (t!=dv) this.fireEvent("change", [dv, date, t]);
				this.fireEvent("complate", [dv, date, t]);
			}
		}
	},

	_setDayWeekTitleTh: function(table){
		var dayTable = table || this.contentTable;

		var thead = dayTable.getElement("thead");
		var cells = thead.getElements("th");

		if (this.css.calendarDaysContentTh) cells.setStyles(this.css.calendarDaysContentTh);

		//var days_abbr = Locale.get("Date").days_abbr;
		var days_abbr = o2.LP.widget.days_abbr;
		cells.each(function(item, idx){
			item.set("text", days_abbr[idx]);
		});
		return cells;
	},
	setTitleStyle: function(){
		if( this.options.enableDate ){
			if( this.currentView === "time" ){
				var date = this.currentTextNode.retrieve("date");
				if( this.isEnableDate(date.clone().decrement()) ){
					this.prevNode.setStyles(this.css["notdisable_"+this.options.style]);
				}else{
					this.prevNode.setStyles(this.css["disable_"+this.options.style]);
				}
				if( this.isEnableDate(date.clone().increment()) ){
					this.nextNode.setStyles(this.css["notdisable_"+this.options.style]);
				}else{
					this.nextNode.setStyles(this.css["disable_"+this.options.style]);
				}
			}else{
				this.prevNode.setStyles(this.css["notdisable_"+this.options.style]);
				this.nextNode.setStyles(this.css["notdisable_"+this.options.style]);
			}
		}
	},

	createContainer: function(){
		var div = null;
		var request = new Request.HTML({
			url: this.options.containerPath,
			method: "GET",
			async: false,
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				div = responseTree[0];
			}
		});
		request.send();

		//this.containerNode = div.getElement(".MWF_calendar_container");
		this.titleNode = div.getElement(".MWF_calendar_title");
		this.prevNode = div.getElement(".MWF_calendar_prev");
		this.currentNode = div.getElement(".MWF_calendar_current");
		this.currentTextNode = div.getElement(".MWF_calendar_currentText");
		this.nextNode = div.getElement(".MWF_calendar_next");
		this.contentNode = div.getElement(".MWF_calendar_content");
		this.contentDateNode = div.getElement(".MWF_calendar_content_date");
		this.contentTimeNode = div.getElement(".MWF_calendar_content_time");
		this.buttonArea = div.getElement(".MWF_calendar_button_area");
		this.bottomNode = div.getElement(".MWF_calendar_bottom");

		div.setStyles(this.css.container);
		this.titleNode.setStyles(this.css.dateTitle);
		this.prevNode.setStyles(this.css.datePrev);
		this.currentNode.setStyles(this.css.dateCurrent);
		debugger;
		this.currentTextNode.setStyles(this.css.dateCurrentText);
		this.nextNode.setStyles(this.css.dateNext);
		this.contentNode.setStyles(this.css.calendarContent);
		if(this.buttonArea)this.buttonArea.setStyles(this.css.buttonArea);
		this.bottomNode.setStyles(this.css.dateBottom);

		return div;
	},

	createContentTable: function(){
		var table = null;
		var request = new Request.HTML({
			url: this.options[this.currentView+"Path"],
			method: "GET",
			async: false,
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				table = responseTree[0];
			}
		});
		request.send();

		var tbody = table.getElement("tbody");
		if (tbody){
			var tds = tbody.getElements("td");

			var calendar = this;
			tds.addEvent("click", function(){
				switch (calendar.currentView) {
					case "day" :
						var d = this.retrieve("dateValue");
						if( calendar.isEnableDate(d) ){
							calendar._selectDate(d, this);
						}
						break;
					case "month" :
					    var flag = true;
					    if( calendar.options.enableDate ){
					        flag = false;
					        d = calendar.addZero(this.retrieve("month").toInt() + 1, 2 );
							var y = this.retrieve("year");
							if (calendar.isEnableDate(y+"-"+d+"-01")){
								flag = true;
							}else if(calendar.isEnableDate(y+"-"+d+"-"+new Date(y+"-"+d+"-01").get('lastdayofmonth'))){
								flag = true;
							}
					    }
					    if( flag ){
                            if( calendar.options.monthOnly ){
                                var m = calendar.addZero(this.retrieve("month").toInt() + 1, 2 );
                                calendar._selectDate(this.retrieve("year")+"-"+ m , this);
                            }else{
                                calendar.changeViewToDay(this.retrieve("year"), this.retrieve("month"));
                            }
					    }
						break;
					case "year" :
					    d = this.retrieve("year");
					    if (calendar.isEnableDate(d+"-01-01") || calendar.isEnableDate(d+"-12-12")){
                            if( calendar.options.yearOnly ){
                                calendar._selectDate(this.retrieve("year"), this);
                            }else{
                                calendar.changeViewToMonth(this.retrieve("year"));
                            }
					    }
						break;
					case "time" :
						//nothing
						break;
					default :
					//nothing;
				}

			});


			switch (this.currentView) {
				case "day" :
					if (!table.display) table.display="";
					if (!table.style.display) table.style.display="";

					table.setStyles(this.css.calendarDaysContent);
					tds.setStyles(this.css.calendarDaysContentTd);
					break;

				case "month" :
					table.setStyles(this.css.calendarMonthsContent);
					tds.setStyles(this.css.calendarMonthsContentTd);
					break;
				case "year" :
					this.yearLength = tds.length;
					table.setStyles(this.css.calendarYearsContent);
					tds.setStyles(this.css.calendarYearsContentTd);
					break;
				case "time" :
					if( this.options.style.indexOf("mobile") > -1 ){

						var nodes = table.getElements(".calendarTimeContent_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeContent_mobile);

						nodes = table.getElements(".calendarTimeFixWidthNode_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeFixWidthNode_mobile);

						nodes = table.getElements(".calendarTimeWheels_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeWheels_mobile);

						nodes = table.getElements(".calendarTimeWheel_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeWheel_mobile);

						nodes = table.getElements(".calendarTimeSelectContainer_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeSelectContainer_mobile);

						nodes = table.getElements(".calendarTimeSelectLine_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeSelectLine_mobile);

						nodes = table.getElements(".calendarTimeShadowMask_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeShadowMask_mobile);

						var node = table.getElement(".MWF_calendar_action_show");
						if (node){
							node.setStyles(this.css.calendarActionShow);
							//var buttons = node.getElements("button");
							//buttons.setStyles(this.css.calendarActionShowButton);
						}

					}else{
						var nodes = table.getElements(".calendarTimeArea");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeArea);

						nodes = table.getElements(".calendarTimeSlider");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeSlider);

						nodes = table.getElements(".calendarTimeSliderKnob");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeSliderKnob);

						nodes = table.getElements(".calendarTimeShow");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeShow);

						nodes = table.getElements(".calendarTimeShowItem");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeShowItem);

						var node = table.getElement(".MWF_calendar_action_show");
						if (node){
							node.setStyles(this.css.calendarActionShow);
							var buttons = node.getElements("button");
							buttons.setStyles(this.css.calendarActionShowButton);
						}

					}

					break;
				default :
				//nothing;
			}

			tds.addEvent("mouseover", function(){
				var d;
				switch (calendar.currentView) {
					case "day" :
						d = this.retrieve("dateValue");
						if (calendar.isEnableDate(d)) this.setStyle("border", "1px solid #999999");
						break;
					case "month" :
						if( calendar.options.enableDate ){
							d = calendar.addZero(this.retrieve("month").toInt() + 1, 2 );
							var y = this.retrieve("year");
							if (calendar.isEnableDate(y+"-"+d+"-01")){
								this.setStyle("border", "1px solid #999999");
							}else if(calendar.isEnableDate(y+"-"+d+"-"+new Date(y+"-"+d+"-01").get('lastdayofmonth'))){
								this.setStyle("border", "1px solid #999999");
							}
						}else{
							this.setStyle("border", "1px solid #999999");
						}
						break;
					case "year" :
						d = this.retrieve("year");
						if (calendar.isEnableDate(d+"-01-01") || calendar.isEnableDate(d+"-12-12")) this.setStyle("border", "1px solid #999999");
						break;
					default:
						this.setStyle("border", "1px solid #999999");
						break;
				}
			});
			tds.addEvent("mouseout", function(){
				this.setStyle("border", "1px solid #FFF");
				// var d;
				// switch (calendar.currentView) {
				// 	case "day" :
				// 		d = this.retrieve("dateValue");
				// 		if (calendar.isEnableDate(d)) this.setStyle("border", "1px solid #FFF");
				// 		break;
				// 	default:
				// 		this.setStyle("border", "1px solid #FFF");
				// 		break;
				// }
			});
		}else{
			switch (this.currentView) {
				case "day" :
					table.setStyles(this.css.calendarDaysContent);
					tds.setStyles(this.css.calendarDaysContentTd);
					break;
				case "month" :
					table.setStyles(this.css.calendarMonthsContent);
					tds.setStyles(this.css.calendarMonthsContentTd);
					break;
				case "year" :
					this.yearLength = tds.length;
					table.setStyles(this.css.calendarYearsContent);
					tds.setStyles(this.css.calendarYearsContentTd);
					break;
				case "time" :
					if( this.options.style.indexOf("mobile") > -1 ){
						var nodes = table.getElements(".calendarTimeContent_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeContent_mobile);

						nodes = table.getElements(".calendarTimeFixWidthNode_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeFixWidthNode_mobile);

						nodes = table.getElements(".calendarTimeWheels_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeWheels_mobile);

						nodes = table.getElements(".calendarTimeWheel_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeWheel_mobile);

						nodes = table.getElements(".calendarTimeSelectContainer_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeSelectContainer_mobile);

						nodes = table.getElements(".calendarTimeSelectLine_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeSelectLine_mobile);

						nodes = table.getElements(".calendarTimeShadowMask_mobile");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeShadowMask_mobile);

						var node = table.getElement(".MWF_calendar_action_show");
						if (node){
							node.setStyles(this.css.calendarActionShow);
							//var buttons = node.getElements("button");
							//buttons.setStyles(this.css.calendarActionShowButton);
						}
					}else{
						var nodes = table.getElements(".calendarTimeArea");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeArea);

						nodes = table.getElements(".calendarTimeSlider");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeSlider);

						nodes = table.getElements(".calendarTimeSliderKnob");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeSliderKnob);

						nodes = table.getElements(".calendarTimeShow");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeShow);

						nodes = table.getElements(".calendarTimeShowItem");
						if (nodes.length) nodes.setStyles(this.css.calendarTimeShowItem);

						var node = table.getElement(".MWF_calendar_action_show");
						if (node){
							node.setStyles(this.css.calendarActionShow);
							var buttons = node.getElements("button");
							buttons.setStyles(this.css.calendarActionShowButton);
						}
					}

					break;
				default :
				//nothing;
			}

		}

		return table;
	},

	setRange: function(){
		if( this.options.datetimeRange && this.options.datetimeRange.length ){
			this.setDatetimeRange();
		}else{
			if(this.options.dateRange && this.options.dateRange.length){
				this.setDateRange();
			}
			if(this.options.timeRange && this.options.timeRange.length){
				this.setTimeRange();
			}
		}
	},
	setDatetimeRange: function(){
		var arr = this.options.datetimeRange;
		var _2dArray = typeOf(arr[0]) !== "array" ? [arr] : arr;
		if( !_2dArray[0][0] && !_2dArray[0][1] )return;
		this.datetimeRange = [];
		this.dateRange = [];
		this.dateOnlyRange = [];
		_2dArray.each(function(a){
			var ds, de, ds1, de1, de2;
			if(a[0])ds = typeOf(a[0]) === "date" ? a[0] : Date.parse(a[0]);
			if(a[1])de = typeOf(a[1]) === "date" ? a[1] : Date.parse(a[1]);
			this.datetimeRange.push([ds, de]);
			if(ds){
				ds1 = ds.clone().clearTime();
			}
			if(de){
				de1 = de.clone().set({hr:23, min:59, sec:59});
				de2 = de.clone().clearTime();
			}
			this.dateRange.push([ds1, de1]);
			this.dateOnlyRange.push([ds1, de2]);
		}.bind(this));

		this.options.enableDate = function (date) {
			var d = typeOf(date) === "string" ? Date.parse(date) : date.clone();
			d.clearTime();
			for( var i=0; i<this.dateRange.length; i++ ){
				var ar = this.dateRange[i];
				if( !ar[0] && this.isLessEquals(d, ar[1]) )return true;
				if( !ar[1] && this.isLessEquals(ar[0], d) )return true;
				if( this.isLessEquals(ar[0], d) && this.isLessEquals(d, ar[1]) )return true;
			}
			return false;
		}.bind(this);

		this.options.enableHours = function (date) {
			var d = typeOf(date) === "string" ? Date.parse(date) : date.clone();
			d.clearTime();
			var hours = [];
			for( var i=0; i<this.dateOnlyRange.length; i++ ){
				var ar = this.dateOnlyRange[i];
				var equal1 = this.isEquals(ar[0], d), equal2 = this.isEquals(d, ar[1]);
				if( equal1 || equal2){
					var s = equal1 ? this.datetimeRange[i][0].get("hr") : 0;
					var e = equal2 ? this.datetimeRange[i][1].get("hr") : 23;
					hours.push( [s, e] );
				}
			}
			return hours.length ? o2.Calendar.RangeArrayUtils.union(hours) : [0, 23];
		}.bind(this);

		this.options.enableMinutes = function (date, hour) {
			var d = typeOf(date) === "string" ? Date.parse(date) : date.clone();
			d.clearTime();
			var minutes = [];
			for( var i=0; i<this.dateOnlyRange.length; i++ ){
				var ar = this.dateOnlyRange[i];
				var ardt = this.datetimeRange[i];
				var equal1 = (this.isEquals(ar[0], d) && hour === ardt[0].get("hr"));
				var equal2 = (this.isEquals(d, ar[1]) && hour === ardt[1].get("hr"));
				if( equal1 || equal2 ){
					var s = equal1 ? ardt[0].get("min") : 0;
					var e = equal2 ? ardt[1].get("min") : 59;
					minutes.push( [s, e] );
				}
			}
			return minutes.length ? o2.Calendar.RangeArrayUtils.union(minutes) : [0, 59];
		}.bind(this);

		this.options.enableSeconds = function (date, hour, minute) {
			var d = typeOf(date) === "string" ? Date.parse(date) : date.clone();
			d.clearTime();
			var seconds = [];
			for( var i=0; i<this.dateOnlyRange.length; i++ ){
				var ar = this.dateOnlyRange[i];
				var ardt = this.datetimeRange[i];
				var equal1 = (this.isEquals(ar[0], d) && hour === ardt[0].get("hr") && minute === ardt[0].get("min"));
				var equal2 = (this.isEquals(d, ar[1]) && hour === ardt[1].get("hr") && minute === ardt[1].get("min"));
				if( equal1 || equal2 ){
					var s = equal1 ? ardt[0].get("sec") : 0;
					var e =  equal2 ? ardt[1].get("sec") : 59;
					seconds.push( [s, e] );
				}
			}
			return seconds.length ? o2.Calendar.RangeArrayUtils.union(seconds) : [0, 59];
		}.bind(this);
	},
	setDateRange: function(){
		var arr = this.options.dateRange;
		var _2dArray = typeOf(arr[0]) !== "array" ? [arr] : arr;
		if( !_2dArray[0][0] && !_2dArray[0][1] )return;
		this.dateRange = [];
		_2dArray.each(function(a){
			var ds, de, ds1, de1;
			if(a[0])ds = typeOf(a[0]) === "date" ? a[0] : Date.parse(a[0]);
			if(a[1])de = typeOf(a[1]) === "date" ? a[1] : Date.parse(a[1]);
			if(ds)ds1 = ds.clearTime();
			if(de)de1 = de.set({hr:23, min:59, sec:59});
			this.dateRange.push([ds1, de1]);
		}.bind(this));

		this.options.enableDate = function (date) {
			var d = typeOf(date) === "string" ? Date.parse(date) : date.clone();
			d.clearTime();
			for( var i=0; i<this.dateRange.length; i++ ){
				var ar = this.dateRange[i];
				if( !ar[0] && this.isLessEquals(d, ar[1]) )return true;
				if( !ar[1] && this.isLessEquals(ar[0], d) )return true;
				if( this.isLessEquals(ar[0], d) && this.isLessEquals(d , ar[1]) )return true;
			}
			return false;
		}.bind(this);
	},
	setTimeRange: function(){
		var arr = this.options.timeRange;
		var _2dArray = typeOf(arr[0]) !== "array" ? [arr] : arr;
		if( !_2dArray[0][0] && !_2dArray[0][1] )return;
		this.datetimeRange2 = [];
		_2dArray.each(function(a){
			var ds, de, ds1, de1;
			if(a[0])ds = Date.parse("2020-01-01 "+a[0]);
			if(a[1])de = Date.parse("2020-01-01 "+a[1]);
			this.datetimeRange2.push([ds, de]);
		}.bind(this));

		this.options.enableHours = function (date) {
			// var d = typeOf(date) === "string" ? Date.parse(date) : date;
			if(this.hourRange)return this.hourRange;
			var hours = [];
			for( var i=0; i<this.datetimeRange2.length; i++ ){
				var ar = this.datetimeRange2[i];
				var s = ar[0] ? ar[0].get("hr") : 0;
				var e = ar[1] ? ar[1].get("hr") : 23;
				hours.push( [s, e] );
			}
			this.hourRange = o2.Calendar.RangeArrayUtils.union(hours);
			return this.hourRange;
		}.bind(this);

		this.options.enableMinutes = function (date, hour) {
			// var d = typeOf(date) === "string" ? Date.parse(date) : date;
			var minutes = [];
			for( var i=0; i<this.datetimeRange2.length; i++ ){
				var ar = this.datetimeRange2[i];
				var equal1 = (ar[0] && hour === ar[0].get("hr"));
				var equal2 = (ar[1] && hour === ar[1].get("hr"));
				if( equal1 || equal2 ){
					var s = equal1 ? ar[0].get("min") : 0;
					var e = equal2 ? ar[1].get("min") : 59;
					minutes.push( [s, e] );
				}
			}
			return minutes.length ? o2.Calendar.RangeArrayUtils.union(minutes) : [0, 59];
		}.bind(this);

		this.options.enableSeconds = function (date, hour, minute) {
			// var d = typeOf(date) === "string" ? Date.parse(date) : date;
			var seconds = [];
			for( var i=0; i<this.datetimeRange2.length; i++ ){
				var ar = this.datetimeRange2[i];
				var equal1 = (ar[0] && hour === ar[0].get("hr") && minute === ar[0].get("min"));
				var equal2 = (ar[1] && hour === ar[1].get("hr") && minute === ar[1].get("min"));
				if( equal1 || equal2 ){
					var s = equal1 ? ar[0].get("sec") : 0;
					var e = equal2 ? ar[1].get("sec") : 59;
					seconds.push( [s, e] );
				}
			}
			return seconds.length ? o2.Calendar.RangeArrayUtils.union(seconds) : [0, 59];
		}.bind(this);
	},
	isGreatEquals: function( d1, d2 ){
		return (d1 > d2) || ((d1 - d2) === 0);
	},
	isLessEquals: function( d1, d2 ){
		return (d1 < d2) || ((d1 - d2) === 0);
	},
	isEquals: function(d1, d2){
		return (d1 - d2) === 0;
	},
	isEnableDate: function(date){
		var fun = this.options.enableDate;
		if( fun && typeOf(fun) === "function" ){
			var d = typeOf( date ) === "string" ? new Date(date) : date;
			if( fun( d ) === false ){
				return false;
			}
		}
		return true;
	},
	getEnableHours: function(date){
		var fun = this.options.enableHours;
		if( fun && typeOf(fun) === "function" ){
			var d = typeOf( date ) === "string" ? new Date(date) : date;
			return fun( d );
		}
		return [0, 23];
	},
	getDisabledHours: function(date){
		var ar = this.getEnableHours(date);
		if( !ar || !ar.length || (ar[0] === 0 && ar[1] === 23) )return [];
		if( typeOf(ar[0]) !== "array" )ar = [ar];
		return o2.Calendar.RangeArrayUtils.complementary([0, 23], ar, null, 1);
	},
	// getEnableHourStartEnd: function(date){
	// 	var ar = this.getEnableHours(date);
	// 	return this._getEnableStartEnd( ar, 0, 23 );
	// },
	isEnableHour: function (thisDate, hour) {
		var hs = this.getEnableHours( thisDate );
		if( !hs || !hs.length || (hs[0] === 0 && hs[1] === 23) )return true;
		if( typeOf(hs[0]) === "array" ){
			for( var i=0; i< hs.length; i++ ){
				var dhs = hs[i];
				if(  dhs[0] <= hour && hour <= dhs[1] )return true;
			}
		}else{
			if(  hs[0] <= hour && hour <= hs[1] )return true;
		}
		return false;
	},
	getEnableMinutes: function(date, h){
		var fun = this.options.enableMinutes;
		if( fun && typeOf(fun) === "function" ){
			var d = typeOf( date ) === "string" ? new Date(date) : date;
			return fun( d, h );
		}
		return [0, 59];
	},
	getDisabledMinutes: function(date, h){
		var ar = this.getEnableMinutes(date, h);
		if( !ar || !ar.length || (ar[0] === 0 && ar[1] === 59))return [];
		if( typeOf(ar[0]) !== "array" )ar = [ar];
		return o2.Calendar.RangeArrayUtils.complementary([0, 59], ar, null, 1);
	},
	isEnableMinute: function (thisDate, hour, minute) {
		var ms = this.getEnableMinutes( thisDate, hour );
		if( !ms || !ms.length || (ms[0] === 0 && ms[1] === 59))return true;
		if( typeOf(ms[0]) === "array" ){
			for( var i=0; i< ms.length; i++ ){
				var dms = ms[i];
				if(  dms[0] <= minute && minute <= dms[1] )return true;
			}
		}else{
			if(  ms[0] <= minute && minute <= ms[1] )return true;
		}
		return false;
	},
	getEnableSeconds: function(date, h, m){
		var fun = this.options.enableSeconds;
		if( fun && typeOf(fun) === "function" ){
			var d = typeOf( date ) === "string" ? new Date(date) : date;
			return fun( d, h, m );
		}
		return [0, 59];
	},
	getDisabledSeconds: function(date, h, m){
		var ar = this.getEnableSeconds(date, h, m);
		if( !ar || !ar.length || (ar[0] === 0 && ar[1] === 59))return [];
		if( typeOf(ar[0]) !== "array" )ar = [ar];
		return o2.Calendar.RangeArrayUtils.complementary([0, 59], ar, null, 1);
	},
	isEnableSecond: function (thisDate, hour, minute, second) {
		var ss = this.getEnableSeconds( thisDate, hour, minute );
		if( !ss || !ss.length || (ss[0] === 0 && ss[1] === 59))return true;
		if( typeOf(ss[0]) === "array" ){
			for( var i=0; i< ss.length; i++ ){
				var dss = ss[i];
				if(  dss[0] <= second && second <= dss[1] )return true;
			}
		}else{
			if(  ss[0] <= second && second <= ss[1] )return true;
		}
		return false;
	}
	// _getEnableStartEnd: function( ar, start, end ){
	// 	if( !ar || !ar.length || (ar[0] === start && ar[1] === 23) )return [start, 23];
	// 	var s, e;
	// 	if( typeOf( ar[0] === "array" ) ){
	// 		ar.each(function(a, i){
	// 			s = i === 0 ? (a[0] || start ) : Math.min( s , a[0] || start );
	// 			e = i === 0 ? (a[1] || end ) : Math.max( e, a[1] || end );
	// 		})
	// 	}else{
	// 		s = ar[0] || start;
	// 		e = ar[1] || end;
	// 	}
	// 	return [s, e];
	// }

});


o2.Calendar.MobileSelect = new Class({
	Implements: [Options, Events],
	options: {
		"lineHeight" : 40, //每个item的高度
		"items": [],
		"ratio" : 1, //滑动距离倍率
		"currentItem" : 0
	},
	initialize: function (wheelNode, options) {
		this.setOptions( options );
		this.wheelNode = wheelNode;
		this.sliderNode = wheelNode.getFirst();
		this.options.itemIndex = this.options.items.indexOf(this.options.currentItem);
		if( this.options.itemIndex < 0 )this.options.itemIndex = 0;
	},
	load : function(){
		var _this = this;
		this.curDistance = 0;
		this.sliderNode.style.transform = "translate3d(0px, 80px, 0px)";
		this.wheelNode.addEventListener('touchstart', function (event) {
			_this.touch(event);
		}, false);
		this.wheelNode.addEventListener('touchend', function (event) {
			_this.touch(event);
		}, false);
		this.wheelNode.addEventListener('touchmove', function (event) {
			_this.touch(event);
		}, false);
		this.locatePostion( this.options.itemIndex );
	},
	touch: function (ev) {
		var sliderNode = this.sliderNode;
		ev = ev || window.event;
		switch (ev.type) {
			case "touchstart":
				this.startTime = new Date();
				this.startY = event.touches[0].clientY;
				this.oldMoveY = this.startY;
				break;
			case "touchend":
				this.moveEndY = event.changedTouches[0].clientY;

				this.overTime = new Date();
				var speed = (this.moveEndY - this.startY) / ( this.overTime - this.startTime );
				var ratio = 1;
				if( Math.abs(speed) > 0.7 ){
					ratio = 5;
				}else if( Math.abs(speed) < 0.2 ){
					ratio = 0.7
				}

				this.offsetSum = ( this.moveEndY - this.startY ) * this.options.ratio * ratio;
				this.updateCurDistance();
				this.curDistance = this.fixPosition(this.curDistance);
				this.movePosition( this.curDistance );
				this.oversizeBorder = - ( this.options.items.length - 3) * this.options.lineHeight;
				if (this.curDistance + this.offsetSum > 2 * this.options.lineHeight) {
					this.curDistance = 2 * this.options.lineHeight;
					setTimeout(function () {
						this.movePosition( this.curDistance );
					}.bind(this), 100);
				} else if (this.curDistance + this.offsetSum < this.oversizeBorder) {
					this.curDistance = this.oversizeBorder;
					setTimeout(function () {
						this.movePosition( this.curDistance );
					}.bind(this), 100);
				}
				var idx = this.getCurIndex();
				this.fireEvent( "change", [this.options.items[idx]]);
				break;
			case "touchmove":
				ev.preventDefault();
				this.moveY = event.touches[0].clientY;
				this.overTime = new Date();
				var speed = (this.moveY - this.oldMoveY) / ( this.overTime - this.oldOverTime );
				var ratio = 1;
				if( Math.abs(speed) > 0.7 ){
					ratio = 5;
				}else if( Math.abs(speed) < 0.2 ){
					ratio = 0.7
				}

				this.offset = ( this.moveY - this.oldMoveY ) * this.options.ratio * ratio;
				this.updateCurDistance();
				this.curDistance = this.curDistance + this.offset;
				this.movePosition( this.curDistance );
				this.oldMoveY = this.moveY;
				this.oldOverTime = this.overTime;
				break;
		}
	},
	resetItems: function( items, currentItem ){
		this.options.items = items;
		this.options.currentItem = currentItem;
		this.options.itemIndex = this.options.items.indexOf(this.options.currentItem);
		if( this.options.itemIndex < 0 )this.options.itemIndex = 0;
		this.oversizeBorder = - ( this.options.items.length - 3) * this.options.lineHeight;
		this.locatePostion( this.options.itemIndex );
	},
	calcDistance: function (index) {
		return 2 * this.options.lineHeight - index * this.options.lineHeight;
	},
	setCurDistance: function ( index ) {
		this.curDistance = this.calcDistance( index );
		this.movePosition( this.curDistance );
	},
	fixPosition: function (distance) {
		return -(this.getIndex(distance) - 2) * this.options.lineHeight;
	},
	getCurIndex : function(){
		return this.getIndex( this.curDistance );
	},
	getIndex: function (distance) {
		return Math.round((2 * this.options.lineHeight - distance) / this.options.lineHeight);
	},
	movePosition: function ( distance) {
		this.sliderNode.style.webkitTransform = 'translate3d(0,' + distance + 'px, 0)';
		this.sliderNode.style.transform = 'translate3d(0,' + distance + 'px, 0)';
	},
	locatePostion: function ( index ) {
		this.curDistance = this.calcDistance(index);
		this.movePosition( this.curDistance );
	},
	updateCurDistance: function () {
		this.curDistance = parseInt(this.sliderNode.style.transform.split(',')[1]);
	},
	getDistance: function () {
		return parseInt(this.sliderNode.style.transform.split(',')[1]);
	}
});


o2.Calendar.RangeArrayUtils = {
	//补集 range [ start, end ]  rangeList  [ [start1, end1], [ start2, end2 ] ... ]
	complementary : function( range, rangeList, type, offset ){
		if( !range )return range;
		var r = this.getRangeObject( range );
		if( !rangeList || rangeList.length == 0 )return this.parse( [r] , type);
		var unitedList = this.union( rangeList );

		var newRange = {};
		if( unitedList[0][0] > r.start ){
			newRange.start = r.start;
		}else if( r.end > unitedList[0][1] ){
			newRange.start = unitedList[0][1];
			unitedList.shift();
		}else{
			return [];
		}
		var newList = [];
		while( unitedList.length > 0 ){
			if( unitedList[0][0] >= r.end ){
				newRange.end = r.end;
				newList.push( Object.clone(newRange) );
				return this.parse( newList , type);
			}else if( r.end <= unitedList[0][1] ){
				newRange.end = unitedList[0][0];
				newList.push( Object.clone(newRange) );
				return this.parse( newList, type );
			}else{
				newRange.end = unitedList[0][0];
				newList.push( Object.clone(newRange) );
				newRange.start = unitedList[0][1];
				unitedList.shift();
			}
		}
		newRange.end = r.end;
		newList.push( Object.clone(newRange ));
		var array = this.parse( newList, type );
		if( offset ){
			return array.map(function (a) {
				if( a[0] !== r.start )a[0] = a[0] + offset;
				if( a[1] !== r.end )a[1] = a[1] - offset;
				return a;
			})
		}else{
			return array;
		}
	},
	//取区域并集rangeList  [ [start1, end1], [ start2, end2 ] ... ]
	union : function( ranges, type ){
		if( !ranges || ranges.length == 0)return ranges; //this.parse(this.getRangeObject( ranges ) ) ;
		var rangeList = Array.clone( ranges );
		for( var i=0; i<rangeList.length; i++ ){
			rangeList[i] = this.getRangeObject( rangeList[i] );
		}
		rangeList.sort( function( a, b ){
			return a.start - b.start;
		});

		var newRangeList = [];
		var newRange = rangeList.shift();
		while( rangeList.length > 0 ){
			var nextRange = rangeList.shift();
			if( this.isIntersection( newRange, nextRange ) ){
				newRange.end =  Math.max( newRange.end, nextRange.end );
			}else{
				newRangeList.push(  Object.clone( newRange ) );
				newRange = nextRange;
			}
		}
		if( !nextRange ){
			newRangeList.push(  Object.clone( newRange ) );
		}else if( this.isIntersection( newRange, nextRange ) ){
			newRange.end = Math.max( newRange.end, nextRange.end );
			newRangeList.push(  Object.clone( newRange ) );
		}else{
			newRangeList.push(  Object.clone( nextRange ) );
		}

		return this.parse( newRangeList, type );
	},
	//取区域交集rangeList  [ [start1, end1], [ start2, end2 ] ... ]，需要测试
	intersection: function( ranges, type ){
		if( !ranges || ranges.length == 0 )return ranges; //this.parse(this.getRangeObject( ranges ) ) ;
		if( ranges.length === 1 )return ranges[1];
		var rangeList = Array.clone( ranges );
		for( var i=0; i<rangeList.length; i++ ){
			rangeList[i] = this.getRangeObject( rangeList[i] );
		}
		rangeList.sort( function( a, b ){
			return a.start - b.start;
		});

		var newRange = rangeList.shift();
		while( rangeList.length > 0 ){
			var nextRange = rangeList.shift();
			if( this.isIntersection( newRange, nextRange ) ){
				newRange.start = nextRange.start;
				newRange.end =  Math.min( newRange.end, nextRange.end );
			}else{
				return [];
			}
		}

		if( type && type === "date" ){
			return [ Date.parse(newRange.start), Date.parse(newRange.end) ];
		}else{
			return [newRange.start, newRange.end];
		}
	},
	//区域是否相交
	isIntersection : function( range1, range2 ){
		var r1 = typeOf( range1 ) === "object" ? range1 : this.getRangeObject( range1 );
		var r2 = typeOf( range2 ) === "object" ? range2 : this.getRangeObject( range2 );
		if( r1.start > r2.end )return false;
		if( r2.start > r1.end )return false;
		return true;
	},
	parse: function( objectList, type ){
		var list = [];
		for( var i=0; i<objectList.length; i++ ){
			var range = objectList[i];
			if( type && type == "date" ){
				list.push(  [ Date.parse(range.start), Date.parse(range.end) ] );
			}else{
				list.push(  [range.start, range.end] );
			}
		}
		return list;
	},
	getRangeObject: function( range ){
		return {
			start : Math.min( range[0], range[1] ),
			end : Math.max( range[0], range[1]  )
		}
	}
};

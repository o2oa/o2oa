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
		// "disabledDate": function (date) {  //一个function，参数为日期，return true不可选
		// 	return date > new Date();
		// },
		// "disabledHours": function (date) {
		// 	if( date > new Date() ){
		// 		return [];
		// 	}else{
		// 		return [[0, 8], [16, 24]];
		// 	}
		//}, //一个function, 参数为日期
		// "disabledMinutes": null,  //一个function, 参数为日期，hour
		// "disabledSeconds": null,  //一个function, 参数为日期，hour，minutes
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
		if( this.options.disabledDate ){
			var d = date.clone().increment("day", 1);
			if( this.isDisabledDate(d) )return;
		}
		// var year = this.currentTextNode.retrieve("year");
		// var month = this.currentTextNode.retrieve("month");
		// month--;
		// var day = this.currentTextNode.retrieve("day");
		// var date = new Date(year, month, day);
		date.increment("day", 1);
		this._setTimeTitle(null, date);
		if( this.options.disabledHours || this.options.disabledMinutes || this.options.disabledSeconds ){
			this._resetTimeDate();
		}
	},
	getPrevDate: function(){
		var date = this.currentTextNode.retrieve("date");
		if( this.options.disabledDate ){
			var d = date.clone().increment("day", -1);
			if( this.isDisabledDate(d) )return;
		}
		date.increment("day", -1);
		this._setTimeTitle(null, date);
		if( this.options.disabledHours || this.options.disabledMinutes || this.options.disabledSeconds ){
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
			var elementCoords = this.container.getCoordinates();
			var targetCoords  = this.node.getCoordinates();
			var page = e.page;
			if (layout.userLayout && layout.userLayout.scale && layout.userLayout.scale!==1){
				page.x = page.x/layout.userLayout.scale;
				page.y = page.y/layout.userLayout.scale;
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
		if (this.container.position && (!layout || !layout.userLayout || !layout.userLayout.scale || layout.userLayout.scale===1) ){
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
		}else{
			var p = this.node.getPosition(this.options.target || null);
			var size = this.node.getSize();
			var containerSize = this.container.getSize();
			var bodySize = (this.options.target) ? this.options.target.getSize() : $(document.body).getSize(); //$(document.body).getSize();

			var left = p.x;
			if ((left + containerSize.x + 40) > bodySize.x){
				left = bodySize.x - containerSize.x - 40;
			}

			var top = p.y+size.y+2;
			if( top + containerSize.y > bodySize.y ){
				top = bodySize.y - containerSize.y ;
			}

			this.container.setStyle("top", top);
			this.container.setStyle("left", left);

		}
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

			if( this.options.disabledDate ){
				if( this.isDisabledDate(firstDate) ){
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
			tds[i].store("dateValue", tmpDate.toString())
		}

		for (var i=day; i<tds.length; i++){
			tds[i].set("text", firstDate.getDate());

			if( this.options.disabledDate ){
				if( this.isDisabledDate(firstDate) ){
					tds[i].addClass("disable_"+this.options.style);
					tds[i].setStyles(this.css["disable_"+this.options.style]);
				}else{
					tds[i].removeClass("disable_"+this.options.style);
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
			}
			if (tmp.diff(this.today)>0){
				if (this.css["past_"+this.options.style]) tds[i].setStyles(this.css["past_"+this.options.style]);
				tds[i].addClass("past_"+this.options.style);
			}


			tds[i].store("dateValue", firstDate.toString());
			firstDate.increment("day", 1);
		}
	},
	isDisabledDate: function(date){
		var fun = this.options.disabledDate;
		if( fun && typeOf(fun) === "function" ){
			var d = typeOf( date ) === "string" ? new Date(date) : date;
			if( fun( d ) === true ){
				return true;
			}
		}
		return false;
	},
	getDisabledHours: function(date){
		var fun = this.options.disabledHours;
		if( fun && typeOf(fun) === "function" ){
			var d = typeOf( date ) === "string" ? new Date(date) : date;
			return fun( d );
		}
		return null;
	},
	getEnableHours: function(date){
		var range = [0, 23];
		var ar = this.getDisabledHours(date);
		if( !ar || !ar.length )return [range];
		if( typeOf(ar[0]) !== "array" )ar = [ar];
		return o2.Calendar.RangeArrayUtils.complementary(range, ar, null, 1);
	},
	isDisabledHour: function (thisDate, hour) {
		var hs = this.getDisabledHours( thisDate );
		if( !hs || !hs.length )return false;
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
	getDisabledMinutes: function(date, h){
		var fun = this.options.disabledMinutes;
		if( fun && typeOf(fun) === "function" ){
			var d = typeOf( date ) === "string" ? new Date(date) : date;
			return fun( d, h );
		}
		return null;
	},
	getEnableMinutes: function(date, h){
		var range = [0, 59];
		var ar = this.getDisabledMinutes(date, h);
		if( !ar || !ar.length )return [range];
		if( typeOf(ar[0]) !== "array" )ar = [ar];
		return o2.Calendar.RangeArrayUtils.complementary(range, ar, null, 1);
	},
	isDisabledMinute: function (thisDate, hour, minute) {
		var ms = this.getDisabledMinutes( thisDate, hour );
		if( !ms || !ms.length )return false;
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
	getDisabledSeconds: function(date, h, m){
		var fun = this.options.disabledSeconds;
		if( fun && typeOf(fun) === "function" ){
			var d = typeOf( date ) === "string" ? new Date(date) : date;
			return fun( d, h, m );
		}
		return null;
	},
	getEnableSeconds: function(date, h, m){
		var range = [0, 59];
		var ar = this.getDisabledSeconds(date, h, m);
		if( !ar || !ar.length )return [range];
		if( typeOf(ar[0]) !== "array" )ar = [ar];
		return o2.Calendar.RangeArrayUtils.complementary(range, ar, null, 1);
	},
	isDisabledSecond: function (thisDate, hour, minute, second) {
		var ss = this.getDisabledSeconds( thisDate, hour, minute );
		if( !ss || !ss.length )return false;
		if( typeOf(ss[0]) === "array" ){
			for( var i=0; i< ss.length; i++ ){
				var dss = ss[i];
				if(  dss[0] <= second && second <= dss[1] )return true;
			}
		}else{
			if(  ss[0] <= second && second <= ss[1] )return true;
		}
		return false;
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
	},
	_setTimeDate_mobile: function(node, h, m, s){
		var _self = this;

		this.itmeHNode = this.contentTimeTable.getElement(".MWF_calendar_time_h");
		this.itmeMNode = this.contentTimeTable.getElement(".MWF_calendar_time_m");
		this.itmeSNode = this.contentTimeTable.getElement(".MWF_calendar_time_s");


		this.showActionNode = this.contentTimeTable.getElement(".MWF_calendar_action_show");

		var calendar = this;

		if( !this.hMobileSelect ){
			for( var i=0; i<24; i++ ){
				new Element("div",{
					"text" : this.addZero(i, 2 ),
					"styles" : this.css.calendarTimeSelectItem_mobile
				}).inject( this.itmeHNode );
			}
			this.selectedHour = this.addZero(h, 2 );
			this.hMobileSelect = new o2.Calendar.MobileSelect( this.itmeHNode.getParent(), {
				"lineHeight" : 40,
				"itemSize" : 24,
				"itemIndex" : parseInt(h),
				"onChange": function(value){
					this.selectedHour = this.addZero(value, 2 );
					//this.showHNode.set("text", this.addZero(i, 2 ));
					//this.itmeHNode.getFirst().set("text", this.addZero(i, 2 ));
				}.bind(this)
			});
			this.hMobileSelect.load();
		}

		if( !this.mMobileSelect ) {
			for (var i = 0; i < 60; i++) {
				new Element("div", {
					"text": this.addZero(i, 2),
					"styles": this.css.calendarTimeSelectItem_mobile
				}).inject(this.itmeMNode);
			}
			this.selectedMinute = this.addZero(m, 2);
			this.mMobileSelect = new o2.Calendar.MobileSelect(this.itmeMNode.getParent(), {
				"lineHeight": 40,
				"itemSize": 60,
				"itemIndex": parseInt(m),
				"onChange": function (value) {
					this.selectedMinute = this.addZero(value, 2);
					//this.showHNode.set("text", this.addZero(i, 2 ));
					//this.itmeHNode.getFirst().set("text", this.addZero(i, 2 ));
				}.bind(this)
			});
			this.mMobileSelect.load();
		}

		if(this.options.secondEnable ){
			if(!this.sMobileSelect){
				for( var i=0; i<60; i++ ){
					new Element("div",{
						"text" : this.addZero(i, 2 ),
						"styles" : this.css.calendarTimeSelectItem_mobile
					}).inject( this.itmeSNode );
				}
				this.selectedSecond = this.addZero(s, 2 );
				this.sMobileSelect = new o2.Calendar.MobileSelect( this.itmeSNode.getParent(), {
					"lineHeight" : 40,
					"itemSize" : 60,
					"itemIndex" : parseInt(s),
					"onChange": function(value){
						this.selectedSecond = this.addZero(value, 2 );
						//this.showHNode.set("text", this.addZero(i, 2 ));
						//this.itmeHNode.getFirst().set("text", this.addZero(i, 2 ));
					}.bind(this)
				});
				this.sMobileSelect.load();
			}
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
			this.okButton = new Element("button", {"text": o2.LP.widget.ok }).inject(this.showActionNode);
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
						if( !this.isDisabledHour(this.cDate, v) ){
							this.selectedHour = v;
							this.cHour = v;
							this.showHNode.set("text", this.addZero(v, 2));
							this.itmeHNode.getFirst().set("text", this.addZero(v, 2));

							if( this.options.disabledMinutes ){
								this.calculateCurrentMinute();
								this.createDisabledNodes(this.itmeMNode, 60, "m");
								this.mSlider.set( this.cMinute );
								this.itmeMNode.getFirst().set("text", this.addZero( this.cMinute, 2));
								this.showMNode.set("text", this.addZero( this.cMinute, 2));
							}
							if( this.options.disabledSeconds && this.sSlider ){
								this.calculateCurrentSecond();
								this.createDisabledNodes(this.itmeSNode, 60, "s");
								this.sSlider.set( this.cSecond );
								this.itmeSNode.getFirst().set("text", this.addZero( this.cSecond, 2));
								this.showSNode.set("text", this.addZero( this.cSecond, 2) );
							}
						}
					}.bind(this)
				});
				this.itmeHNode.getFirst().set("text", this.addZero( this.cHour, 2));
				this.showHNode.set("text", this.addZero( this.cHour, 2) );

				this.calculateCurrentMinute(m);
				this.createDisabledNodes(this.itmeMNode, 60, "m");
				this.mSlider = new Slider(this.itmeMNode, this.itmeMNode.getFirst(), {
					range: [0, 59],
					initialStep: this.cMinute,
					onChange: function(value){
						var v = value.toInt();
					    if( !this.isDisabledMinute(this.cDate, this.cHour, v) ){
                            this.selectedMinute = v;
                            this.cMinute = v;
                            this.showMNode.set("text", this.addZero( v, 2));
                            this.itmeMNode.getFirst().set("text", this.addZero( v, 2));

							if( this.options.disabledSeconds && this.sSlider ){
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
                            if( !this.isDisabledSecond(this.cDate, this.cHour, this.cMinute, v) ){
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
		if(this.options.timeSelectType === "select"){
			if( this.options.disabledHours )this.loadHourSelect();
			if( this.options.disabledMinutes )this.loadMinuteSelect();
			if( this.options.disabledSeconds )this.loadSecondSelect();
		}else {
			if( this.options.disabledHours ){
				this.calculateCurrentHour();
				this.createDisabledNodes(this.itmeHNode, 24, "h");
				this.hSlider.set( this.cHour );
				this.itmeHNode.getFirst().set("text", this.addZero( this.cHour, 2));
				this.showHNode.set("text", this.addZero( this.cHour, 2) );
			}
			if( this.options.disabledMinutes ){
				this.calculateCurrentMinute();
				this.createDisabledNodes(this.itmeMNode, 60, "m");
				this.mSlider.set( this.cMinute );
				this.itmeMNode.getFirst().set("text", this.addZero( this.cMinute, 2));
				this.showMNode.set("text", this.addZero( this.cMinute, 2));
			}
			if( this.options.disabledSeconds && this.sSlider ){
				this.calculateCurrentSecond();
				this.createDisabledNodes(this.itmeSNode, 60, "s");
				this.sSlider.set( this.cSecond );
				this.itmeSNode.getFirst().set("text", this.addZero( this.cSecond, 2));
				this.showSNode.set("text", this.addZero( this.cSecond, 2) );
			}
		}
	},
	loadHourSelect: function (h) {
		this.itmeHNode = this.contentTimeTable.getElement(".MWF_calendar_time_h").empty();
		new Element("span",{"text": o2.LP.widget.hour + "："}).inject(this.itmeHNode);
		this.itmeSelectHNode = new Element("select").inject(this.itmeHNode);
		this.calculateCurrentHour(h);
		for( var i=0; i<24; i++ ){
			if( !this.isDisabledHour(this.cDate, i) ){
				var opt = new Element("option",{
					"text" : this.addZero(i, 2 ),
					"value" : this.addZero(i, 2 ),
					"styles" : this.css.calendarTimeSelectItem_mobile
				}).inject( this.itmeSelectHNode );
				if( i === this.cHour )opt.set("selected", true);
			}
		}
		// this.itmeSelectHNode.set("value",this.addZero( this.cHour, 2));
		this.showHNode.set("text", this.addZero( this.cHour, 2) );
		this.itmeSelectHNode.addEvent("change",function(){
			this.cHour = this.itmeSelectHNode.get("value").toInt();
			this.selectedHour = this.cHour;
			this.showHNode.set("text", this.itmeSelectHNode.get("value") );
			if( this.options.disabledMinutes )this.loadMinuteSelect();
			if( this.options.disabledSeconds )this.loadSecondSelect();
		}.bind(this));
	},
	loadMinuteSelect: function (m) {
		this.itmeMNode = this.contentTimeTable.getElement(".MWF_calendar_time_m").empty();
		new Element("span", {"text": o2.LP.widget.minute + "："}).inject(this.itmeMNode);
		this.itmeSelectMNode = new Element("select").inject(this.itmeMNode);
		this.calculateCurrentMinute(m);
		for (var i = 0; i < 60; i++) {
			if (!this.isDisabledMinute(this.cDate, this.cHour || 0, i)) {
				var opt = new Element("option", {
					"text": this.addZero(i, 2),
					"value": this.addZero(i, 2),
					"styles": this.css.calendarTimeSelectItem_mobile
				}).inject(this.itmeSelectMNode);
				if( i === this.cMinute )opt.set("selected", true);
			}
		}
		// this.itmeSelectMNode.set("value", this.addZero(this.cMinute, 2));
		this.showMNode.set("text", this.addZero( this.cMinute, 2) );
		this.itmeSelectMNode.addEvent("change", function () {
			this.cMinute = this.itmeSelectMNode.get("value").toInt();
			this.selectedMinute = this.cMinute;
			this.showMNode.set("text", this.itmeSelectMNode.get("value"));
			if( this.options.disabledSeconds )this.loadSecondSelect();
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
				if( !this.isDisabledSecond(this.cDate, this.cHour || 0, this.cMinute || 0, i) ){
					var opt = new Element("option",{
						"text" : this.addZero(i, 2 ),
						"value" : this.addZero(i, 2 ),
						"styles" : this.css.calendarTimeSelectItem_mobile
					}).inject( this.itmeSelectSNode );
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
		debugger;
		if( typeOf(h) !== "null" ){
			this.cHour = h.toInt();
		}else if( typeOf(this.selectedHour) !== "null" ){
			this.cHour = this.selectedHour;
		}else{
			this.cHour = 0;
		}
		if( this.isDisabledHour(this.cDate, this.cHour) ) {
			var eHours = this.getEnableHours(this.cDate);
			this.cHour = eHours.length ? eHours[0][0] : 0;
		}
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
		if( this.isDisabledMinute(this.cDate, this.cHour, this.cMinute) ){
			var eMinutes = this.getEnableMinutes(this.cDate, this.cHour);
			this.cMinute = eMinutes.length ? eMinutes[0][0] : 0;
		}
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
		if( this.isDisabledSecond(this.cDate, this.cHour, this.cMinute, this.cSecond) ){
			var eSeconds = this.getEnableSeconds(this.cDate, this.cHour, this.cMinute);
			this.cSecond = eSeconds.length ? eSeconds[0][0] : 0;
		}
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

		var h = this.selectedHour || this.showHNode.get("text");
		var m = this.selectedMinute || this.showMNode.get("text");
		date.setHours(h);
		date.setMinutes(m);

		if( this.options.secondEnable && ( this.selectedSecond || this.showSNode) ){
			var s = this.selectedSecond || this.showSNode.get("text");
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
						if( !calendar.isDisabledDate(d) ){
							calendar._selectDate(d, this);
						}
						break;
					case "month" :
						debugger;
						if( calendar.options.monthOnly ){
							var m = calendar.addZero(this.retrieve("month").toInt() + 1, 2 );
							calendar._selectDate(this.retrieve("year")+"-"+ m , this);
						}else{
							calendar.changeViewToDay(this.retrieve("year"), this.retrieve("month"));
						}
						break;
					case "year" :
						if( calendar.options.yearOnly ){
							calendar._selectDate(this.retrieve("year"), this);
						}else{
							calendar.changeViewToMonth(this.retrieve("year"));
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
				switch (calendar.currentView) {
					case "day" :
						var d = this.retrieve("dateValue");
						if (!calendar.isDisabledDate(d)) this.setStyle("border", "1px solid #999999");
						break;
					default:
						this.setStyle("border", "1px solid #999999");
						break;
				}
			});
			tds.addEvent("mouseout", function(){
				switch (calendar.currentView) {
					case "day" :
						var d = this.retrieve("dateValue");
						if (!calendar.isDisabledDate(d)) this.setStyle("border", "1px solid #FFF");
						break;
					default:
						this.setStyle("border", "1px solid #FFF");
						break;
				}
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
	}

});


o2.Calendar.MobileSelect = new Class({
	Implements: [Options, Events],
	options: {
		"lineHeight" : 40, //每个item的高度
		"itemSize" : 0, //item数量
		"ratio" : 1, //滑动距离倍率
		"itemIndex" : 0
	},
	initialize: function (wheelNode, options) {
		this.setOptions( options );
		this.wheelNode = wheelNode;
		this.sliderNode = wheelNode.getFirst();
	},
	load : function(){
		var _this = this;
		this.curDistance = 0;
		this.sliderNode.style.transform = "translate3d(0px, 80px, 0px)";
		this.wheelNode.addEventListener('touchstart', function () {
			_this.touch(event);
		}, false);
		this.wheelNode.addEventListener('touchend', function () {
			_this.touch(event);
		}, false);
		this.wheelNode.addEventListener('touchmove', function () {
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
				this.oversizeBorder = - ( this.options.itemSize - 3) * this.options.lineHeight;
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
				this.fireEvent( "change", [this.getCurIndex()] );
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

		if(offset){
			for( var i=0; i<unitedList.length; i++ ){
				unitedList[i][0] = unitedList[i][0] - offset;
				unitedList[i][1] = unitedList[i][1] + offset;
			}
		}

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
		return this.parse( newList, type );
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

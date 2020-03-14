o2.widget = o2.widget || {};
o2.require("o2.widget.Calendar", null, false);
o2.widget.CalendarPage = o2.CalendarPage = new Class({
	Extends: o2.widget.Calendar,
	Implements: [Options, Events],

	initialize: function(node, options){

		Locale.use("zh-CHS");
		this.setOptions(options);

		this.path = o2.session.path+"/widget/$Calendar/";
		this.cssPath = o2.session.path+"/widget/$Calendar/"+this.options.style+"/css.wcss";
		
		this._loadCss();
	//	this.options.containerPath = this.path+this.style+"/container.html";
	//	this.options.dayPath = this.path+this.style+"/day.html";
	//	this.options.monthPath = this.path+this.style+"/month.html";
	//	this.options.yearPath = this.path+this.style+"/year.html";
	//	this.options.timePath = this.path+this.style+"/time.html";

		if (this.options.isTime){
			//this.options.format = Locale.get("Date").shortDate + " " + Locale.get("Date").shortTime;
			this.options.format = Locale.get("Date").shortDate + " " + "%H:%M";
		}else{
			this.options.format = Locale.get("Date").shortDate;
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
		this.container.inject(this.node);
		this.contentTable = this.createContentTable();
		this.contentTable.inject(this.contentDateNode);

		this.addEvents();

		this.fireEvent("init");
	},
	addEvents: function(){
		this.prevNode.addEvent("click", function(){
			this.getPrev();
		}.bind(this));

		this.nextNode.addEvent("click", function(){
			this.getNext();
		}.bind(this));

		this.currentTextNode.addEvent("click", function(){
			this.changeView();
		}.bind(this));
	},
	show: function(){
		if (!this.visible){
			var dStr = this.node.get("value");
			if (dStr){
				this.options.baseDate = Date.parse(dStr.substr(0,10));
			}
			this.currentView = this.options.defaultView;

			switch (this.currentView) {
				case "day" :
					this.changeViewToDay();
					break;
				case "month" :
					this.showMonth();
					break;
				case "year" :
					this.showYear();
					break;
				case "time" :
					this.showTime(this.options.baseDate);
					break;
				default :
					this.showDay();
			}

//			if (!this.morph){
//				this.morph = new Fx.Morph(this.container, {"duration": 200});
//			}
			this.container.setStyle("display", "block");
			this.visible = true;

			this.fireEvent("show");
		}
	},
	_selectDate: function(dateStr, el){
		var date = new Date(dateStr);
		var dv = date.format(this.options.format);
		if (this.options.isTime){
			this.changeViewToTime(date);
		}else{
			if (this.fireEvent("queryComplate", dateStr)){
				
				var thisYaer = this.currentTextNode.retrieve("year");
				var thisMonth = this.currentTextNode.retrieve("month");
				
				baseDate = new Date();
				baseDate.setFullYear(thisYaer);
				baseDate.setMonth(thisMonth-1);
				
				var selectDate =new Date(dateStr);

				var tbody;
				if (el){
					tbody = el.getParent("tbody");
				}else{
					tbody = this.contentTable.getElement("tbody");
				}

				var tds = tbody.getElements("td");

				for (var i=0; i<tds.length; i++){
					var thisDate = new Date(tds[i].retrieve("dateValue"));
					thisDate.clearTime();
					if (thisDate.clearTime().toString() == this.today.clearTime().toString()){
						tds[i].setStyles(this.css["today_"+this.options.style]);
						tds[i].setStyle("border", "0px solid #AAA");
					}else if (thisDate.clearTime().toString() == selectDate.clearTime().toString()){
						tds[i].addClass("current_"+this.options.style);
						tds[i].setStyles(this.css["current_"+this.options.style]);
						tds[i].removeClass("gray_"+this.options.style);
						tds[i].removeClass("past_"+this.options.style);
						tds[i].setStyle("border", "1px solid #FFF");
					}else if(baseDate.getMonth()!=thisDate.getMonth()){
						tds[i].addClass("gray_"+this.options.style);
						tds[i].setStyles(this.css["gray_"+this.options.style]);
						tds[i].removeClass("current_"+this.options.style);
						tds[i].removeClass("past_"+this.options.style);
						tds[i].setStyle("border", "1px solid #FFF");
					}else if (thisDate.diff(this.today)>0){
						if (this.css["past_"+this.options.style]) tds[i].setStyles(this.css["past_"+this.options.style]);
						tds[i].addClass("past_"+this.options.style);
						tds[i].removeClass("current_"+this.options.style);
						tds[i].removeClass("gray_"+this.options.style);
					}else{
						tds[i].setStyles(this.css["normal_"+this.options.style]);
						tds[i].addClass("normal_"+this.options.style);
						tds[i].removeClass("current_"+this.options.style);
						tds[i].removeClass("gray_"+this.options.style);
						tds[i].removeClass("past_"+this.options.style);
						tds[i].setStyle("border", "1px solid #FFF");
					}

				}
				//el.setStyles(this.css["current_"+this.options.style]);
			//	this.node.set("value", dv);
			//	this.hide();
				this.fireEvent("complate");
			}
		}
	}
	
});

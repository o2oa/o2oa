MWF.require("MWF.widget.JsonTemplate", null, false);
MWF.xApplication.Selector.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "Selector",
		"icon": "icon.png",
		"width": "800",
		"height": "700",
		"isResize": false,
		"isMax": false,
		"title": MWF.xApplication.Selector.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Selector.LP;
	},
	loadApplication: function(callback){
        //this.content

		this.node = new Element("div", {"styles": {
			"padding": "20px",
			"text-align": "center"
		}}).inject(this.content);

		this.optionsNode = new Element("textarea", {"styles": {
            "width": "90%",
			"display": "block",
			"height": "160px"
        }}).inject(this.node);

        var buttonArea = new Element("div", {"styles": {
            "height": "30px",
            "margin-top": "10px",
			"text-align": "left"
        }}).inject(this.node);

        this.buttonNode = new Element("button", {"text": "select", "styles": {
            "width": "150px",
            "height": "30px"
        }}).inject(buttonArea);
        this.buttonMobileNode = new Element("button", {"text": "selectMobile", "styles": {
            "width": "150px",
            "height": "30px"
        }}).inject(buttonArea);

        this.resaultNode = new Element("div", {"styles": {
            "height": "410px",
            "overflow": "auto",
            "margin-top": "10px",
            "background-color": "#ffffff",
            "border": "1px solid #666666",
            "display": "block"
        }}).inject(this.node);

		var str = "{\n\t\"count\": 0, \n\t\"type\": \"identity\", \n\t\"title\": \"Select Person\",\n\t\"groups\": [], \n\t\"roles\": [],\n\t\"dutys\" : [], \n\t\"units\": [],\n\t\"unitType\": \"\",\n\t\"values\": []\n}"
        this.optionsNode.set("value", str);

        this.buttonNode.addEvent("click", function(){
        	this.select();
		}.bind(this));
        this.buttonMobileNode.addEvent("click", function(){
            this.selectMobile();
        }.bind(this));
	},
    select: function(callback){
        var str = this.optionsNode.get("value");
        var options = JSON.decode(str);
        if (!options.values.length) options.values = this.values || [];
        options.onComplete = function(items){
        	var json = [];
        	this.values = [];
            items.each(function(item){
                this.values.push(item.data.distinguishedName || item.data.name);
				json.push(item.data);
			}.bind(this));

            this.resaultNode.empty();
            MWF.require("MWF.widget.JsonParse", function(){
                var jsonNode = new MWF.widget.JsonParse(json, this.resaultNode, null);
                jsonNode.load();
            }.bind(this));
            
            if (callback) callback();
		}.bind(this);

        options.onCancel = function(){
            if (callback) callback();
		};
        MWF.xDesktop.requireApp("Selector", "package", function(){
			new MWF.O2Selector(this.content, options)
		}.bind(this));
	},
    selectMobile: function(){
    	if (!layout.mobile) layout.mobile = true;
        this.select(function(){layout.mobile = false;}.bind(this));
	}

});
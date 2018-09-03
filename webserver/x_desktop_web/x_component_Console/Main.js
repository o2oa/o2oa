MWF.xApplication.Console.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Console",
		"icon": "icon.png",
		"width": "800",
		"height": "600",
		"title": MWF.xApplication.Console.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Console.LP;
	},
	loadApplication: function(callback){
        this.status = "stop";

        this.node = new Element("div", {"styles": this.css.contentNode}).inject(this.content);

        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode}).inject(this.node);
        this.screenNode = new Element("div", {"styles": this.css.screenNode}).inject(this.node);
        this.bottomNode = new Element("div", {"styles": this.css.bottomNode}).inject(this.node);

        this.loadToolbar();
        this.loadScreen();
	},

    loadToolbar: function(){
        this.beginButton = new Element("div", {"styles": this.css.toolbarButton}).inject(this.toolbarNode);
        this.pauseButton = new Element("div", {"styles": this.css.toolbarButton}).inject(this.toolbarNode);
        this.stopButton = new Element("div", {"styles": this.css.toolbarButton}).inject(this.toolbarNode);
        this.beginButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/play.png)");
        this.pauseButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/pause_gray.png)");
        this.stopButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/stop_gray.png)");

        this.beginButton.addEvents({
            "mouseover": function(){if (this.status != "begin") this.beginButton.setStyles(this.css.toolbarButton_over);}.bind(this),
            "mousedown": function(){if (this.status != "begin") this.beginButton.setStyles(this.css.toolbarButton_down);}.bind(this),
            "mouseup": function(){if (this.status != "begin") this.beginButton.setStyles(this.css.toolbarButton_over);}.bind(this),
            "mouseout": function(){this.beginButton.setStyles(this.css.toolbarButton);}.bind(this),
            "click": function(){if (this.status != "begin") this.begin();}.bind(this)
        });

        this.pauseButton.addEvents({
            "mouseover": function(){if (this.status == "begin") this.pauseButton.setStyles(this.css.toolbarButton_over);}.bind(this),
            "mousedown": function(){if (this.status == "begin") this.pauseButton.setStyles(this.css.toolbarButton_down);}.bind(this),
            "mouseup": function(){if (this.status == "begin") this.pauseButton.setStyles(this.css.toolbarButton_over);}.bind(this),
            "mouseout": function(){this.pauseButton.setStyles(this.css.toolbarButton);}.bind(this),
            "click": function(){if (this.status == "begin") this.pause();}.bind(this)
        });

        this.stopButton.addEvents({
            "mouseover": function(){if (this.status != "stop") this.stopButton.setStyles(this.css.toolbarButton_over);}.bind(this),
            "mousedown": function(){if (this.status != "stop") this.stopButton.setStyles(this.css.toolbarButton_down);}.bind(this),
            "mouseup": function(){if (this.status != "stop") this.stopButton.setStyles(this.css.toolbarButton_over);}.bind(this),
            "mouseout": function(){this.stopButton.setStyles(this.css.toolbarButton);}.bind(this),
            "click": function(){if (this.status != "stop") this.stop();}.bind(this)
        });

    },
    begin: function(){

        this.beginButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/play_gray.png)");
        this.pauseButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/pause.png)");
        this.stopButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/stop.png)");
        this.status = "begin";
    },
    pause: function(){

        this.beginButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/play.png)");
        this.pauseButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/pause_gray.png)");
        this.stopButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/stop.png)");
        this.status = "pause";
    },
    stop: function(){

        this.beginButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/play.png)");
        this.pauseButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/pause_gray.png)");
        this.stopButton.setStyle("background-image", "url("+"/x_component_Console/$Main/default/stop_gray.png)");
        this.status = "stop";
    },

    loadScreen: function(){
        this.screenScrollNode = new Element("div", {"styles": this.css.screenScrollNode}).inject(this.screenNode);
        this.screenInforAreaNode = new Element("div", {"styles": this.css.screenInforAreaNode}).inject(this.screenScrollNode);

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.screenScrollNode, {
                "style":"xApp_console", "where": "before", "indent": false, "distance": 50, "friction": 6,	"axis": {"x": false, "y": true}
            });
        }.bind(this));

        this.setScreenHeight();
        this.addEvent("resize", this.setScreenHeight.bind(this));
    },

    setScreenHeight: function(){
        var size = this.node.getSize();
        var toolbarSize = this.toolbarNode.getSize();
        var bottomSize = this.bottomNode.getSize();
        var y = size.y-toolbarSize.y-bottomSize.y;
        this.screenNode.setStyle("height", ""+y+"px");
    }

});

MWF.xApplication.SmartOfficeRoom.options.multitask = false;
MWF.xApplication.SmartOfficeRoom.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "SmartOfficeRoom",
		"icon": "icon.png",
		"width": "420",
		"height": "680",
		"isResize": false,
		"isMax": false,
		"title": MWF.xApplication.SmartOfficeRoom.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.SmartOfficeRoom.LP;
        this.action = MWF.Actions.get("x_smartoffice_control");
	},
    reload: function(){
        this.deviceContent.setStyle("display", "block");
        if (this.selectDeviceContent) this.selectDeviceContent.setStyle("display", "none");
    },
	loadApplication: function(callback){
        this.devices = [];
        this.size = this.content.getSize();
        var itemWidth = (this.size.x/2).toInt()-22;
        this.css.itemNode.width = ""+itemWidth+"px";

		this.loadDeviceContent();
		if (callback) callback();
	},
    loadDeviceContent: function(){
		this.deviceContent = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
        this.loadDevices();
        this.loadConfigButton();

	},
    loadDevices: function(){
        this.action.listDevice(function(json){
            if (callback) callback();
        });
    },
    loadConfigButton: function(){
    	this.addActionNode = new Element("div", {"styles": this.css.itemNode}).inject(this.deviceContent);
		this.addActionTextNode = new Element("div", {"styles": this.css.addActionTextNode, "text": this.lp.add}).inject(this.addActionNode);
        this.addActionIconNode = new Element("div", {"styles": this.css.addActionIconNode}).inject(this.addActionNode);
        this.addActionIconNode.addEvents({
            "mouseover": function(){this.addActionIconNode.setStyles(this.css.addActionIconNode_over); this.addActionTextNode.setStyle("color", "#3498db");}.bind(this),
            "mouseout": function(){this.addActionIconNode.setStyles(this.css.addActionIconNode); this.addActionTextNode.setStyle("color", "#999");}.bind(this),
            "click": function(e){
                this.addActionIconNode.setStyles(this.css.addActionIconNode); this.addActionTextNode.setStyle("color", "#999");
                this.createNewDevice(e);
            }.bind(this)
        });
    },
    createNewDevice: function(){
        this.deviceContent.setStyle("display", "none");
        if (!this.selectDeviceContent) this.createSelectDeviceContent();
        this.selectDeviceContent.setStyle("display", "block");
	},
    createSelectDeviceContent: function(){
    	var _self = this;
        this.selectDeviceContent = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
        MWF.getJSON(this.path+"device.json", function(json){
			Object.each(json, function(dev){
				var node = new Element("div", {"styles": this.css.selectDeviceNode}).inject(this.selectDeviceContent);
				node.store("dev", dev);
				var iconNode = new Element("div", {"styles": this.css.selectDeviceIconNode}).inject(node);
                iconNode.setStyle("background-image", "url("+this.path+this.options.style+"/device/"+dev.icon+"-off-64.png)");
                var flagNode = new Element("div", {"styles": this.css.selectDeviceFlagNode}).inject(node);

                var contentNode = new Element("div", {"styles": this.css.selectDeviceContentNode}).inject(node);
                var textNode = new Element("div", {"styles": this.css.selectDeviceTextNode, "text": dev.name}).inject(contentNode);
                var desNode = new Element("div", {"styles": this.css.selectDeviceDesNode, "text": dev.description}).inject(contentNode);

                node.addEvents({
                    "mouseover": function(){this.setStyles(_self.css.selectDeviceNode_over);},
                    "mouseout": function(){this.setStyles(_self.css.selectDeviceNode);},
                    "click": function(e){_self.createDevice(this)}
                });
			}.bind(this))
		}.bind(this));
	},
    createDevice: function(node){
		var dev = node.retrieve("dev");
		if (dev && dev["class"]){
			var device = new MWF.xApplication.SmartOfficeRoom.Device[dev["class"]](this);
            device.create();
		}
	}

});

MWF.xApplication.SmartOfficeRoom.Device = new Class({
    initialize: function(app, data){
        this.app = app;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.data = data;
        this.init();
    },
    init: function(){},
	load: function(){},
	getNewData: function(){
		return {
            "name": "",
            "sn": "",
            "description": ""
		}
	},
	create: function(){
		this.data = this.getNewData();
		this.createAreaNode();
	},
    createAreaNode: function(){
		this.areaNode = new Element("div", {"styles": this.css.createContentNode}).inject(this.app.content);
        var size = this.app.content.getSize();
        this.areaNode.setStyle("left", ""+size.x+"px");

        this.createConfigTitle();
        this.createConfigAction();
        this.createConfigContent();

        new Fx.Morph(this.areaNode, {
            "duration": "200"
        }).start({"left": "0px"});
	},
    createConfigTitle: function(){
        this.configTitleNode = new Element("div", {"styles": this.css.configContentTitleNode}).inject(this.areaNode);
        this.configIconNode = new Element("div", {"styles": this.css.configContentIconNode}).inject(this.configTitleNode);
        this.configTextNode = new Element("div", {"styles": this.css.configContentTextNode}).inject(this.configTitleNode);
    },
    createConfigAction: function(){
        this.configActionAreaNode = new Element("div", {"styles": this.css.configActionAreaNode}).inject(this.areaNode);
        this.configSaveActionNode = new Element("div", {"styles": this.css.configSaveActionNode, "text": this.lp.save}).inject(this.configActionAreaNode);
        this.configCancelActionNode = new Element("div", {"styles": this.css.configCancelActionNode, "text": this.lp.cancel}).inject(this.configActionAreaNode);

        this.configSaveActionNode.addEvent("click", this.saveDevice.bind(this));
        this.configCancelActionNode.addEvent("click", this.cancelDevice.bind(this));
    },
    createConfigContent: function(){
        this.configContentNode = new Element("div", {"styles": this.css.configContentNode}).inject(this.areaNode);
        var size = this.app.content.getSize();
        var titleSize = this.configTitleNode.getSize();
        var actionSize = this.configActionAreaNode.getSize();
        var h = size.y-titleSize.y-actionSize.y;
        this.configContentNode.setStyle("height", ""+h+"px");

        this.createConfigInputContent()
    },
    cancelDevice: function(){
        var size = this.app.content.getSize();
        this.app.reload();
        new Fx.Morph(this.areaNode, {
            "duration": "200"
        }).start({"left": ""+size.x+"px"}).chain(function(){
            this.destroy();
        }.bind(this));
    },
    destroy: function(){
        this.areaNode.destroy();
        MWF.release(this);
    },
    saveDevice: function(){
        this.saveDeviceAction(function(){
            this.cancelDevice();
        }.bind(this));
    },
    saveDeviceAction: function(callback){
        if (callback) callback();
    }

});

MWF.xApplication.SmartOfficeRoom.Device.Light = new Class({
    Extends: MWF.xApplication.SmartOfficeRoom.Device,

    createConfigInputContent: function(){
        this.configIconNode.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/device/light-32.png)");
        this.configTextNode.set("text", this.data.name || this.lp.newLight);

        var nameTitleNode = new Element("div", {"styles": this.css.configContentItemTitleNode, "text": this.lp.inputName}).inject(this.configContentNode);
        var nameInputAreaNode = new Element("div", {"styles": this.css.configContentItemInputAreaNode}).inject(this.configContentNode);
        this.nameInputNode = new Element("input", {"styles": this.css.configContentItemInputNode}).inject(nameInputAreaNode);

        var snTitleNode = new Element("div", {"styles": this.css.configContentItemTitleNode, "text": this.lp.inputSn}).inject(this.configContentNode);
        var snInputAreaNode = new Element("div", {"styles": this.css.configContentItemInputAreaNode}).inject(this.configContentNode);
        this.snInputNode = new Element("input", {"styles": this.css.configContentItemInputNode}).inject(snInputAreaNode);

        var descriptionTitleNode = new Element("div", {"styles": this.css.configContentItemTitleNode, "text": this.lp.inputDescription}).inject(this.configContentNode);
        var descriptionInputAreaNode = new Element("div", {"styles": this.css.configContentItemTextareaAreaNode}).inject(this.configContentNode);
        this.descriptionInputNode = new Element("textarea", {"styles": this.css.configContentItemTextareaNode}).inject(descriptionInputAreaNode);
    },
    saveDeviceAction: function(callback){

        var name = this.nameInputNode.get("value");
        var sn = this.snInputNode.get("value");
        var description = this.descriptionInputNode.get("value");
        if (!name){
            this.app.notice(this.lp.noname, "error");
            return false;
        }
        if (!sn){
            this.app.notice(this.lp.nosn, "error");
            return false;
        }
        var data = {
            "type": "light",
            "name": name,
            "sn": sn,
            "description": description
        };
        this.app.action.saveDevice(data, function(){
            if (callback) callback();
        });
    }
});
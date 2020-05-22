MWF.xApplication.DesignCenter.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "DesignCenter",
		"icon": "icon.png",
		"width": "800",
        "isResize": false,
        "isMax": false,
		"height": "280",
		"title": MWF.xApplication.DesignCenter.LP.title
	},
    loadWindow: function(isCurrent){
        this.fireAppEvent("queryLoadWindow");
        this.window = new MWF.xDesktop.WindowTransparent(this, {"container": this.desktop.node});
        this.fireAppEvent("loadWindow");
        this.window.show();
        this.content = this.window.content;

        if (isCurrent) this.setCurrent();
        this.fireAppEvent("postLoadWindow");
        this.fireAppEvent("queryLoadApplication");
        this.loadApplication(function(){
            this.fireAppEvent("postLoadApplication");
        }.bind(this));
    },
    setNodeResize: function(){
        this.setNodePositionAndSizeFun = this.setNodePositionAndSize.bind(this);
        this.setNodePositionAndSizeFun();
        $(window).addEvent("resize", this.setNodePositionAndSizeFun);
        this.addEvent("queryClose", function(){
            $(window).removeEvent("resize", this.setNodePositionAndSizeFun);
        }.bind(this));
    },
    setNodePositionAndSize: function(){
        if (this.status && this.status.size){
            this.node.setStyles({
                "top": this.status.size.y,
                "left": this.status.size.x
            });
        }else{
            this.node.position({
                relativeTo: this.desktop.node,
                position: 'center',
                edge: 'center',
                "offset": {
                    "y": "-120"
                }
            });
        }
    },
    recordStatus: function(){
        return {"size": this.node.getPosition()};
    },
	onQueryLoad: function(){
		this.lp = MWF.xApplication.DesignCenter.LP;
	},
	loadApplication: function(callback){
        this.layout = this.desktop;
        this.apps = [];

        if (this.options.event){
            this.css.contentNode.left = this.options.event.page.x;
            this.css.contentNode.top = this.options.event.page.y;
            // options.fromTop = this.options.event.page.y;
            // options.fromLeft = this.options.event.page.x;
        }

        this.node = new Element("div", {"styles": this.css.contentNode}).inject(this.content);

        var morph = new Fx.Morph(this.node, {
            "duration": "100",
            "transition": Fx.Transitions.Sine.easeOut
        });
        this.css.contentNodeTo.width = ""+this.options.width+"px";
        morph.start(this.css.contentNodeTo).chain(function(){
            this.setNodeResize();
            this.node.addEvent("selectstart", function(e){e.target.setStyle("-webkit-user-select", "none")}.bind(this));

            this.titleAreaNode = new Element("div", {"styles": this.css.titleAreaNode}).inject(this.node);
            this.closeNode = new Element("div", {"styles": this.css.closeNode}).inject(this.titleAreaNode);
            this.closeNode.addEvent("click", function(){this.close();}.bind(this));
            this.titleNode = new Element("div", {"styles": this.css.titleAreaTextNode, "text": this.lp.titleInfor}).inject(this.titleAreaNode);

            this.contentNode = new Element("div", {"styles": this.css.contentAreaNode}).inject(this.node);
            this.loadApplications();
        }.bind(this));

        //this.loadTitle();
		//this.content.setStyle("background-color", "#555555");
        // this.node = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
        // this.titleNode = new Element("div", {"styles": this.css.titleAreaNode, "text": this.lp.titleInfor}).inject(this.node);
        // this.contentNode = new Element("div", {"styles": this.css.contentAreaNode}).inject(this.node);
        // // this.setContentSize();
        // // this.addEvent("resize", this.setContentSize);
        // this.loadApplications();
	},

    loadTitle: function(){
        this.titleBar = new Element("div", {"styles": this.css.titleBar}).inject(this.content);
        this.taskTitleTextNode = new Element("div", {"styles": this.css.titleTextNode,"text": this.lp.title}).inject(this.titleBar);
    },
    setContentSize: function(){
        var size = this.node.getSize();
        var w = size.x*0.9;
        var count = (w/120).toInt();
        w = Math.min(count, this.apps.length)*120;
        this.contentNode.setStyles({"width": ""+w+"px"});
    },
    loadApplications: function(){
    	COMMON.JSON.get(this.path+"applications.json", function(catalog){
            var user = this.layout.session.user;
            var currentNames = [user.name, user.distinguishedName, user.id, user.unique];
            if (user.roleList) currentNames = currentNames.concat(user.roleList);
            if (user.groupList) currentNames = currentNames.concat(user.groupList);

            catalog.each(function(value, key){
                var isAllow = true;
                if (value.allowList) isAllow = (value.allowList.length) ? (value.allowList.isIntersect(currentNames)) : true;
                var isDeny = false;
                if (value.denyList) isDeny = (value.denyList.length) ? (value.denyList.isIntersect(currentNames)!==-1) : false;
                if ((!isDeny && isAllow) || MWF.AC.isAdministrator()){
                    this.apps.push({"value":value, "key":key});
                    this.createApplicationMenu(value, key);
                }
                this.setContentSize();
                this.addEvent("resize", this.setContentSize);
            }.bind(this));

		}.bind(this));
	},
    createApplicationMenu: function(value, key){
        var applicationMenuNode = new Element("div", {
            "styles": this.css.applicationMenuNode,
            "title": value.title
        }).inject(this.contentNode);

        var applicationMenuIconNode = new Element("div", {
            "styles": this.css.applicationMenuIconNode
        }).inject(applicationMenuNode);

        var icon = "../x_component_"+value.path.replace(/\./g, "_")+"/$Main/"+value.iconPath;
        applicationMenuIconNode.setStyle("background-image", "url("+icon+")");

        new Element("div", {
            "styles": this.css.applicationMenuTextNode,
            "text": value.title
        }).inject(applicationMenuNode);

        applicationMenuNode.addEvent("click", function(e){
            this.layout.openApplication(e, value.path);

            //this.closeApplicationMenu();
        }.bind(this));
        applicationMenuNode.makeLnk({
            "par": {"icon": icon, "title": value.title, "par": value.path},
            "onStart": function(){
                this.applicationMenuAreaMark.fade("out");
                this.applicationMenuArea.fade("out");
            }.bind(this),
            "onComplete": function(){
                //this.showApplicationMenu();
            }.bind(this)
        });

        var appName = value.path;
    }


});

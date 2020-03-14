MWF.xApplication.ControlPanel.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "ControlPanel",
		"icon": "icon.png",
		"width": "840",
        "isResize": false,
        "isMax": false,
		"height": "280",
		"title": MWF.xApplication.ControlPanel.LP.title
	},
    loadWindow: function(isCurrent){
	    debugger;
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
    loadWindowFlat: function (isCurrent) {
        this.window = {
            "isHide": false,
            "isMax": true,
            "maxSize": function () { },
            "restore": function () { },
            "setCurrent": function () {
                //this.content.show();
                this.content.removeClass("layout_component_content_hide");
                this.content.addClass("layout_component_content_show");
            }.bind(this),
            "setUncurrent": function () {
                //this.content.hide();
                debugger;
                this.content.addClass("layout_component_content_hide");
                this.content.removeClass("layout_component_content_show");
            }.bind(this),
            "hide": function () { },
            "maxOrRestoreSize": function () { },
            "restoreSize": function () { },
            "close": function (callback) {
                this.content.destroy();
                if (callback) callback();
            }.bind(this)
        };

        this.window.content = new Element("div.layout_component_content").inject(layout.desktop.contentNode);
        this.content = this.window.content;

        layout.desktop.addEvent("resize", function () {
            this.fireAppEvent("resize");
        }.bind(this));

        if (isCurrent){
            this.setCurrent();
        }else{
            this.setUncurrent();
        }
        this.fireAppEvent("postLoadWindow");
        this.fireAppEvent("queryLoadApplication");

        //load css
        if (this.stylePath) this.content.loadCss(this.stylePath);

        this.setContentEvent();

        this.loadApplication(function () {
            this.fireAppEvent("postLoadApplication");
        }.bind(this));

        this.fireAppEvent("postLoad");
    },
    setNodeResize: function(){
        this.setNodePositionAndSizeFun = this.setNodePositionAndSize.bind(this);
        this.setNodePositionAndSizeFun();

        this.addEvent("resize", this.setNodePositionAndSizeFun);

        // $(window).addEvent("resize", this.setNodePositionAndSizeFun);
        // this.addEvent("queryClose", function(){
        //     $(window).removeEvent("resize", this.setNodePositionAndSizeFun);
        // }.bind(this));
    },
    setNodePositionAndSize: function(){
        // if (this.status && this.status.size){
        //     this.node.setStyles({
        //         "top": this.status.size.y,
        //         "left": this.status.size.x
        //     });
        // }else{
            this.node.position({
                relativeTo: this.desktop.node,
                position: 'center',
                edge: 'center',
                "offset": {
                    "y": "-120"
                }
            });
        // }
    },

    recordStatus: function(){
        return {"size": this.node.getPosition()};
    },
	onQueryLoad: function(){
		this.lp = MWF.xApplication.ControlPanel.LP;
	},
	loadApplication: function(callback){
        this.layout = this.desktop;
        this.apps = [];
        //this.loadTitle();
		//this.content.setStyle("background-color", "#555555");

        if (layout.viewMode==="Default") this.css.contentNode = this.css.contentNodeFlat;
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
        debugger;
        morph.start(this.css.contentNodeTo).chain(function(){
            this.setNodeResize();
            this.node.addEvent("selectstart", function(e){e.target.setStyle("-webkit-user-select", "none")}.bind(this));

            if (layout.viewMode==="Layout"){
                this.titleAreaNode = new Element("div", {"styles": this.css.titleAreaNode}).inject(this.node);
                this.closeNode = new Element("div", {"styles": this.css.closeNode}).inject(this.titleAreaNode);
                this.closeNode.addEvent("click", function(){this.close();}.bind(this));
                this.titleNode = new Element("div", {"styles": this.css.titleAreaTextNode, "text": this.lp.titleInfor}).inject(this.titleAreaNode);
            }
            this.contentNode = new Element("div", {"styles": this.css.contentAreaNode}).inject(this.node);
            this.loadApplications();
        }.bind(this));


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
            if (user.roleList){
                user.roleList.each(function(role){
                    currentNames.push(MWF.name.cn(role));
                    currentNames.push(role);
                });
            }
            //currentNames = currentNames.concat(user.roleList);
            if (user.groupList){
                user.groupList.each(function(group){
                    currentNames.push(MWF.name.cn(group));
                    currentNames.push(group);
                });

            }
            //currentNames = currentNames.concat(user.groupList);
            catalog.each(function(value, key){
                var isAllow = true;
                if (value.allowList) isAllow = (value.allowList.length) ? (value.allowList.isIntersect(currentNames)) : true;
                var isDeny = false;
                if (value.denyList) isDeny = (value.denyList.length) ? (value.denyList.isIntersect(currentNames)!==-1) : false;
                if ((!isDeny && isAllow)){
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

        var icon = "/x_component_"+value.path.replace(/\./g, "_")+"/$Main/"+value.iconPath;
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

MWF.xApplication.Meeting.ListView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date": null
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Meeting/$ListView/";
        this.cssPath = "/x_component_Meeting/$ListView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.date = this.options.date || new Date();
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        this.leftNode = new Element("div", {"styles": this.css.leftNode}).inject(this.node);
        this.contentAreaNode  = new Element("div", {"styles": this.css.contentAreaNode}).inject(this.node);
        this.contentNode  = new Element("div", {"styles": this.css.contentNode}).inject(this.contentAreaNode);

        this.resetNodeSize();
        this.app.addEvent("resize", this.resetNodeSize.bind(this));

        this.loadLeftNavi();

        this.toApplyWait();
        //this.dateNode = new Element("div", {"styles": this.css.dateNode}).inject(this.node);

        //this.loadCalendar();
    },
    resetNodeSize: function(){
        var size = this.container.getSize();
        if (this.app.meetingConfig.hideMenu=="static"){
            var y = size.y-120;
            this.node.setStyle("height", ""+y+"px");
            this.node.setStyle("margin-top", "60px");
        }else{
            var y = size.y-20;
            this.node.setStyle("height", ""+y+"px");
        }

        //var size = this.container.getSize();
        //var y = size.y-20;
        //this.node.setStyle("height", ""+y+"px");
    },
    loadLeftNavi: function(){
        var menuNode = new Element("div", {"styles": this.css.menuNode, "text": this.app.lp.listNavi.myApply}).inject(this.leftNode);
        this.loadNaviItem(this.app.lp.listNavi.wait, "toApplyWait");
        this.loadNaviItem(this.app.lp.listNavi.processing, "toApplyProcessing");
        this.loadNaviItem(this.app.lp.listNavi.completed, "toApplyCompleted");

        var menuNode = new Element("div", {"styles": this.css.menuNode, "text": this.app.lp.listNavi.myMeeting}).inject(this.leftNode);
        this.loadNaviItem(this.app.lp.listNavi.wait, "toMeetingWait");
        this.loadNaviItem(this.app.lp.listNavi.processing, "toMeetingProcessing");
        this.loadNaviItem(this.app.lp.listNavi.completed, "toMeetingCompleted");
        this.loadNaviItem(this.app.lp.listNavi.reject, "toMeetingReject");

        //var menuNode = new Element("div", {"styles": this.css.menuNode, "text": this.app.lp.listNavi.room}).inject(this.leftNode);
    },
    loadNaviItem: function(text, action){
        var itemNode = new Element("div", {"styles": this.css.menuItemNode, "text": text}).inject(this.leftNode);
        var _self = this;
        itemNode.addEvents({
            "mouseover": function(){if (_self.currentNavi != this) this.setStyles(_self.css.menuItemNode_over);},
            "mouseout": function(){if (_self.currentNavi != this) this.setStyles(_self.css.menuItemNode);},
            "click": function(){
                if (_self.currentNavi) _self.currentNavi.setStyles(_self.css.menuItemNode);
                _self.currentNavi = this;
                this.setStyles(_self.css.menuItemNode_current);
                if (_self[action]) _self[action]();
            }
        })
    },

    toApplyWait: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Meeting.ListView.ApplyWait(this);
    },
    toApplyProcessing: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Meeting.ListView.ApplyProcessing(this);
    },
    toApplyCompleted: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Meeting.ListView.ApplyCompleted(this);
    },
    toMeetingWait: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Meeting.ListView.MeetingWait(this);
    },
    toMeetingProcessing: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Meeting.ListView.MeetingProcessing(this);
    },
    toMeetingCompleted: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Meeting.ListView.MeetingCompleted(this);
    },
    toMeetingReject: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Meeting.ListView.MeetingReject(this);
    },




    hide: function(){
        var fx = new Fx.Morph(this.node, {
            "duration": "300",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            "opacity": 0
        }).chain(function(){
            this.node.setStyle("display", "none");
        }.bind(this));

    },
    show: function(){
        this.node.setStyles(this.css.node);
        var fx = new Fx.Morph(this.node, {
            "duration": "800",
            "transition": Fx.Transitions.Expo.easeOut
        });
        this.app.fireAppEvent("resize");
        fx.start({
            "opacity": 1,
            "left": "0px"
        }).chain(function(){
            this.node.setStyles({
                "position": "static",
                "width": "auto"
            });
        }.bind(this));
    },
    reload: function(){

    }

});

MWF.xApplication.Meeting.ListView.View = new Class({
    initialize: function(view, action){
        this.view = view;
        this.css = this.view.css;
        this.container = this.view.contentNode;
        this.app = this.view.app;
        this.items = [];
        this.load();
    },
    load: function(){
        this.loadHead();

        MWF.require("MWF.widget.Mask", function(){
            this.mask = new MWF.widget.Mask({"style": "desktop"});
            this.mask.loadNode(this.view.contentAreaNode);
        }.bind(this));


        this.loadList();
    },
    loadHead: function(){
        this.table = new Element("table", {
            "styles": this.css.listViewTable,
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0",
            "html": "<tr><th>"+this.app.lp.applyPerson+"</th><th>"+this.app.lp.beginDate+"</th><th>"+this.app.lp.time+"</th><th>"+this.app.lp.subject+"</th><th>"+this.app.lp.room+"</th></tr>"
        }).inject(this.container);
        this.table.getElements("th").setStyles(this.css.listViewTableTh);
    },
    loadList: function() {
        this.app.actions.listMeetingApplyWait(function (json) {
            this.loadLines(json.data);
        }.bind(this));
    },
    loadLines: function(items){
        items.each(function(item){
            this.loadLine(item);
        }.bind(this));
        if (this.mask){
            this.mask.hide(function(){
                MWF.release(this.mask);
                this.mask = null;
            }.bind(this));
        }
    },
    loadLine: function(item){
        this.items.push(new MWF.xApplication.Meeting.ListView.View.Line(this, item));
    },
    destroy: function(){
        this.items.each(function(item){
            item.destroy();
        });
        this.items = [];
        this.view.currentView = null;
        this.table.destroy();
        MWF.release(this);
    }

});


MWF.xApplication.Meeting.ListView.ApplyWait = new Class({
    Extends: MWF.xApplication.Meeting.ListView.View
});
MWF.xApplication.Meeting.ListView.ApplyProcessing = new Class({
    Extends: MWF.xApplication.Meeting.ListView.View,
    loadList: function() {
        this.app.actions.listMeetingApplyProcessing(function (json){this.loadLines(json.data);}.bind(this));
    },
});
MWF.xApplication.Meeting.ListView.ApplyCompleted = new Class({
    Extends: MWF.xApplication.Meeting.ListView.View,
    loadList: function() {
        this.app.actions.listMeetingApplyCompleted(function (json){this.loadLines(json.data);}.bind(this));
    },
});

MWF.xApplication.Meeting.ListView.MeetingWait = new Class({
    Extends: MWF.xApplication.Meeting.ListView.View,
    loadList: function() {
        this.app.actions.listMeetingInvitedWait(function (json){this.loadLines(json.data);}.bind(this));
    },
});
MWF.xApplication.Meeting.ListView.MeetingProcessing = new Class({
    Extends: MWF.xApplication.Meeting.ListView.View,
    loadList: function() {
        this.app.actions.listMeetingInvitedProcessing(function (json){this.loadLines(json.data);}.bind(this));
    },
});
MWF.xApplication.Meeting.ListView.MeetingCompleted = new Class({
    Extends: MWF.xApplication.Meeting.ListView.View,
    loadList: function() {
        this.app.actions.listMeetingInvitedCompleted(function (json){this.loadLines(json.data);}.bind(this));
    },
});
MWF.xApplication.Meeting.ListView.MeetingReject = new Class({
    Extends: MWF.xApplication.Meeting.ListView.View,
    loadList: function() {
        this.app.actions.listMeetingInvitedRejected(function (json){this.loadLines(json.data);}.bind(this));
    },
});

MWF.xApplication.Meeting.ListView.View.Line = new Class({
    initialize: function(table, item){
        this.table = table;
        this.view = this.table.view;
        this.css = this.view.css;
        this.container = this.table.table;
        this.app = this.view.app;
        this.data = item;
        this.load();
    },
    load: function(){
        var sTime = Date.parse(this.data.startTime);

        var bdate = sTime.format(this.app.lp.dateFormatDay);

        var btime = sTime.format("%H:%M");
        var etime = Date.parse(this.data.completedTime).format("%H:%M");

        this.app.actions.getRoom(this.data.room, function (json){
            this.app.actions.getBuilding(json.data.building, function (bjson){
                var room = json.data.name+"("+bjson.data.name+((json.data.roomNumber) ? " #"+json.data.roomNumber : "")+")";

                this.node = new Element("tr",{
                    "html": "<td></td><td>"+bdate+"</td><td>"+btime+"-"+etime+"</td><td>"+this.data.subject+"</td><td>"+room+"</td>"
                }).inject(this.container);

                this.personNode = this.node.getFirst("td");
                if (this.data.applicant){
                    var explorer = {
                        "actions": this.app.personActions,
                        "app": {
                            "lp": this.app.lp
                        }
                    }
                    MWF.require("MWF.widget.Identity", function(){
                        var person = new MWF.widget.Person({"name": this.data.applicant}, this.personNode, explorer, false, null, {"style": "room"});;
                    }.bind(this));
                }

                this.node.getElements("td").setStyles(this.css.listViewTableTd);

                this.node.addEvent("click", function(e){
                    this.openMeeting(e);
                }.bind(this));

            }.bind(this));
        }.bind(this));
    },
    openMeeting: function(e){
        if (!this.document){
            if (this.view.currentDocument) this.view.currentDocument.closeDocument();
            this.document = new MWF.xApplication.Meeting.ListView.View.Document(this, e.target);
            this.view.currentDocument = this.document;
        }
    },
    destroy: function(){
        if (this.document) this.document.closeDocument();
        if (this.node) this.node.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Meeting.ListView.View.Document = new Class({
    Extends: MWF.xApplication.Meeting.MeetingView.Document,
    initialize: function(item){
        this.item = item;
        this.view = this.item.view
        this.container = this.view.contentAreaNode;
        this.app = this.view.app;
        this.path = "/x_component_Meeting/$MeetingView/";
        this.cssPath = "/x_component_Meeting/$MeetingView/default/css.wcss";
        this._loadCss();

        this.app.actions.getMeeting(this.item.data.id, function(json){
            this.data = json.data;
            this.isEdit = (this.data.applicant == this.app.desktop.session.user.name);
            this.load();
        }.bind(this));
    },
    _loadCss: function(){
        var key = encodeURIComponent(this.cssPath);
        if (MWF.widget.css[key]){
            this.css = MWF.widget.css[key];
        }else{
            var r = new Request.JSON({
                url: this.cssPath,
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function(responseJSON, responseText){
                    this.css = responseJSON;
                    MWF.widget.css[key] = responseJSON;
                }.bind(this),
                onError: function(text, error){
                    alert(error + text);
                }
            });
            r.send();
        }
    },
    closeDocument: function(callback){
        //this.saveDocument(true, true);

        if (this.setDescriptionNodeSizeFun) this.app.removeEvent("resize", this.setDescriptionNodeSizeFun);
        if (this.setNodeSizeFun) this.app.removeEvent("resize", this.setNodeSizeFun);

        var size = this.item.node.getSize();
        var position = this.item.node.getPosition();

        var fx = new Fx.Morph(this.node, {
            "duration": "500",
            "transition": Fx.Transitions.Expo.easeOut
        });
        this.node.empty();
        this.view.currentDocument = null;
        fx.start({
            "opacity": 0,
            "width": ""+ size.x+"px",
            "height": ""+ size.y+"px",
            "left": ""+ position.x+"px",
            "top": ""+ position.y+"px",
        }).chain(function(){
            this.destroy();
            if (callback) callback();
        }.bind(this));
    },
});
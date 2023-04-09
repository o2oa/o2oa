MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.require("MWF.widget.O2Identity", null, false);

MWF.xApplication.process.FormDesigner.widget.DictItemSelector = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default"
    },
    initialize: function(node, property, options){
        this.setOptions(options);
        this.node = $(node);
        this.app = property.designer;
        this.data = property.data;

        this.path = "../x_component_process_ProcessDesigner/widget/$PersonSelector/";
        this.cssPath = "../x_component_process_ProcessDesigner/widget/$PersonSelector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.widgetList = [];

        this.title = this.node.get("title");
        this.name = this.node.get("name");
        this.load();

    },
    load: function(){
        this.node.setStyles(this.css.node);
        this.createAddNode();
        var path = this.data[this.name];
        this.loadOrgWidget(path);
    },
    reload: function(){
        this.destroyWidget();
        this.node.empty();
        var path = this.data[this.name];
        this.loadOrgWidget(path);
    },
    getDict: function(){
        var dictField = this.node.dataset["dict"];
        if( !this.data[dictField] || !this.data[dictField].length )return null;
        return this.data[dictField][0];
    },
    destroyWidget: function(){
        this.widgetList.each(function (widget) {
            widget.destroy();
        });
        this.widgetList = [];
    },
    loadOrgWidget: function( path ){
        var widget = new MWF.widget.O2DictItem( this.getDict(), path, this.node, {
            "style": "xform","lazy":true,"disableInfor" : false
        });
        this.widgetList.push(widget);
    },
    createAddNode: function(){
        this.addNode = new Element("div", {"styles": this.css.addPersonNode}).inject(this.node, "before");
        this.addNode.addEvent("click", function(e){

            var dict = this.getDict();
            var action = this.getAction( dict );
            var p = dict.appType === "service" ? action.getData(dict.id) : action.getData(dict.id, dict.appId);
            p.then(function (json) {
                this.loadJsonSelector(json.data, this.title, function ( path ) {

                    this.destroyWidget();

                    this.loadOrgWidget(path);

                    this.fireEvent("change", [path]);

                }.bind(this));
            }.bind(this));
        }.bind(this));
    },
    getAction: function(dict){
        switch (dict.appType) {
            case "process":
                return o2.Actions.load("x_processplatform_assemble_surface").ApplicationDictAction;
            case "cms":
                return o2.Actions.load("x_cms_assemble_control").AppDictAction;
            case "portal":
                return o2.Actions.load("x_portal_assemble_surface").DictAction;
            case "service":
                return o2.Actions.load("x_program_center").DictAction;
        }
    },
    loadJsonSelector: function(data, title, callback){
        var width = "770";
        var height = "580";
        width = width.toInt();
        height = height.toInt();

        var size = this.app.content.getSize();
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<0) y = 0;

        var _self = this;
        var jsonParse;
        MWF.require("MWF.xDesktop.Dialog", function() {
            var dlg = new MWF.xDesktop.Dialog({
                "title": title,
                "style": "user",
                "top": y,
                "left": x - 20,
                "fromTop": y,
                "fromLeft": x - 20,
                "width": width,
                "height": height,
                "html": "<div></div>",
                "maskNode": this.app.content,
                "container": this.app.content,
                "buttonList": [
                    {
                        "text": MWF.LP.process.button.ok,
                        "action": function () {
                            if( !jsonParse.objectTree.currentNode ){
                                _self.app.notice(MWF.APPFD.LP.mustSelect, "error");
                                return;
                            }else{
                                var path = jsonParse.objectTree.currentNode.getPath();
                                if( path.contains(":") )path = path.split(":")[0];
                                if(callback)callback(path);
                            }
                            this.close();
                        }
                    },
                    {
                        "text": MWF.LP.process.button.cancel,
                        "action": function () {
                            this.close();
                        }
                    }
                ]
            });
            dlg.show();

            MWF.require("MWF.widget.JsonParse", function(){
                jsonParse = new MWF.widget.JsonParse(data, dlg.content.getFirst(), null, {
                    topkey: "root"
                });
                jsonParse.load();
            }.bind(this));

        }.bind(this))
    }
});

MWF.widget.O2DictItem = new Class({
    Extends: MWF.widget.O2Group,
    options: {
        "style": "default",
        "canRemove": false,
        "lazy": false,
        "disableInfor" : false,
        "styles": ""
    },
    initialize: function(dict, path, container, options){

        this.setOptions(options);
        this.loadedInfor = false;

        this.path = o2.session.path+"/widget/$O2Identity/";
        this.cssPath = o2.session.path+"/widget/$O2Identity/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);
        this.dict = dict;
        this.path = path;
        this.style = this.css;

        this.load();

        //o2.widget.O2Identity.iditems.push(this);
    },
    getPersonData: function(){},
    setText: function(){
        this.node.set("text", this.path);
    },
    createInforNode: function(callback){
        this.loadedInfor = true;

        var path = this.path;
        var dict = this.dict;

        var action;
        if( dict.appType === "cms" ) {
            action = MWF.Actions.get("x_cms_assemble_control");
        }else if( dict.appType === "portal" ){
            action = MWF.Actions.get("x_portal_assemble_surface");
        }else if( dict.appType === "service" ){
            action = MWF.Actions.get("x_program_center");
        }else{
            action = MWF.Actions.get("x_processplatform_assemble_surface");
        }

        var encodePath = function( path ){
            var arr = path.split(/\//g);
            if( arr[0] === "root" )arr.splice(0, 1);
            // var ar = arr.map(function(v){ return encodeURIComponent(v); });
            return ( dict.appType === "portal" || dict.appType === "service" ) ? arr.join(".") : arr.join("/");
        };

        var p;
        debugger;
        if( dict.appType === "service" ){
            p = path === "root" ? action.getDictRoot(dict.id) : action.getDictData(dict.id, encodePath( path ));
        }else{
            p = path === "root" ? action.getDictRoot(dict.id, dict.appId) : action.getDictData(dict.id, dict.appId, encodePath( path ));
        }

        p.then(function (json) {

            this.inforNode = new Element("div", {
                style: "max-width:300px;white-space:pre-wrap; overflow-wrap:break-word; word-break:break-all;",
                text: JSON.stringify(json.data || "", null, 4)
            });

            this.tooltip = new mBox.Tooltip({
                content: this.inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: this.node,
                transition: 'flyin'
            });
            if( this.options.lazy ){
                this.tooltip.open();
            }
        }.bind(this))
        if (callback) callback();
    }
});


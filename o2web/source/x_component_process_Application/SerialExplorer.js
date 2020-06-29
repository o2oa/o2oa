//MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.Mask", null, false);
MWF.require("MWF.widget.Identity", null,false);
//MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xDesktop.requireApp("process.ProcessManager", "DictionaryExplorer", null, false);
MWF.xApplication.process.Application.SerialExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer,
	Implements: [Options, Events],

    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "../x_component_process_Application/$SerialExplorer/";
        this.cssPath = "../x_component_process_Application/$SerialExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.items=[];
    },
    load: function(){
        this.loadToolbar();
    //    this.loadFilterNode();
    //    this.loadFilterConditionNode();
        this.loadContentNode();

        this.setNodeScroll();

        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.node);

        this.loadElementList();
    },
    clearWorks: function(){
        MWF.release(this.items);
        this.works = null;
        this.items=[];
        this.elementContentListNode.empty();
    },

    reloadWorks: function(){
        this.clearWorks();
        this.createWorkListHead();
        this.loadElementList();
    },

    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentListNode = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        this.createWorkListHead();
        this.setContentSize();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
    },
    setNodeScroll: function(){
        //MWF.require("MWF.widget.DragScroll", function(){
        //    new MWF.widget.DragScroll(this.elementContentNode);
        //}.bind(this));
        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.elementContentNode, {"indent": false});
        //}.bind(this));

        var _self = this;
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.elementContentNode, {
                "indent": false,"style":"xApp_TaskList", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));
    },

    createWorkListHead: function(){
        var headNode = new Element("div", {"styles": this.css.workItemHeadNode}).inject(this.elementContentListNode);
        var html = "<div class='emptyAreaHeadNode'></div><div class='processAreaHeadNode'>"+this.app.lp.process+"</div>" +
            "<div class='keyAreaHeadNode'>"+this.app.lp.key+"</div><div class='numberAreaHeadNode'>"+this.app.lp.serialNumber+"</div><div class='emptyAreaHeadNode'></div>";
        headNode.set("html", html);

        headNode.getElement(".processAreaHeadNode").setStyles(this.css.processAreaHeadNode);
        headNode.getElement(".keyAreaHeadNode").setStyles(this.css.keyAreaHeadNode);
        headNode.getElement(".numberAreaHeadNode").setStyles(this.css.numberAreaHeadNode);
        headNode.getElements(".emptyAreaHeadNode").setStyles(this.css.emptyAreaHeadNode);
    },

    createCreateElementNode: function(){},
    setContentSize: function(){
        var toolbarSize = this.toolbarNode.getSize();
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y-toolbarSize.y-pt-pb;
        this.elementContentNode.setStyle("height", ""+height+"px");

        this.pageCount = (height/40).toInt()+5;

        if (this.options.noCreate) this.createElementNode.destroy();
    },

    loadElementList: function(count){
        this.actions.listSerialNumber(this.app.options.id, function(json){
            json.data.each(function(data){
                var item = this._createItem(data);
                this.items.push(item);
            }.bind(this));
            this.mask.hide();
        }.bind(this), function () {
            if(this.mask)this.mask.hide();
        }.bind(this));
    },
    _createItem: function(data){
        return new MWF.xApplication.process.Application.SerialExplorer.Item(data, this);
    },
    removeSerial: function(serial, all){
        this.actions.deleteSerialNumber(serial.data.id, function(json){
            this.items.erase(serial);
            serial.destroy();
            MWF.release(serial);
        }.bind(this));
    }
});

MWF.xApplication.process.Application.SerialExplorer.Item = new Class({
    initialize: function(data, explorer){
        this.explorer = explorer;
        this.data = data;
        this.container = this.explorer.elementContentListNode;

        this.css = this.explorer.css;

        this.load();
    },

    load: function(){
        this.node = new Element("div", {"styles": this.css.workItemNode});
        this.node.inject(this.container);
        this.workAreaNode =  new Element("div", {"styles": this.css.workItemWorkNode}).inject(this.node);
        //this.otherWorkAreaNode =  new Element("div", {"styles": this.css.workItemWorkNode}).inject(this.node);

        var html = "<div class='emptyAreaNode'></div><div class='processAreaNode'>"+this.data.processName+"</div>" +
            "<div class='keyAreaNode'>"+this.data.name+"</div><div class='numberAreaNode'>"+this.data.serial+"</div><div class='actionAreaNode'></div>";

        this.workAreaNode.set("html", html);

        this.workAreaNode.getElement(".processAreaNode").setStyles(this.css.processAreaNode);
        this.workAreaNode.getElement(".keyAreaNode").setStyles(this.css.keyAreaNode);
        this.numberAreaNode = this.workAreaNode.getElement(".numberAreaNode");
        this.numberAreaNode.setStyles(this.css.numberAreaNode);
        this.workAreaNode.getElement(".emptyAreaNode").setStyles(this.css.emptyAreaNode);
        this.actionAreaNode = this.workAreaNode.getElement(".actionAreaNode");
        this.actionAreaNode.setStyles(this.css.emptyAreaNode);



        //if (!this.data.control.allowRead){
        //    this.node.setStyles(this.css.workItemNode_noread)
        //    this.checkAreaNode.setStyles(this.css.actionStopWorkNode);
        //    this.actionAreaNode.setStyles(this.css.actionStopWorkActionNode);
        //}
        //
        //this.iconAreaNode.setStyles(this.css.iconWorkNode);
        //this.titleAreaNode.setStyles(this.css.titleWorkNode);
        //this.setPersonData();
        //this.setStatusData();
        //
        this.setActions();
        //
        this.setEvents();
        //
        //if (!this.relative) this.listRelatives();
    },
    setActions: function(){
        if (this.explorer.app.options.application.allowControl){
            this.editNode = new Element("div", {"styles": this.css.actionEditNode, "title": this.explorer.app.lp.edit}).inject(this.actionAreaNode);
            this.deleteNode = new Element("div", {"styles": this.css.actionDeleteNode, "title": this.explorer.app.lp.delete}).inject(this.actionAreaNode);
        }
    },

    setEvents: function(){
        if (this.deleteNode){
            this.deleteNode.addEvents({
                "mouseover": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_over);}.bind(this),
                "mouseout": function(){this.deleteNode.setStyles(this.css.actionDeleteNode);}.bind(this),
                "mousedown": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_down);}.bind(this),
                "mouseup": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_over);}.bind(this),
                "click": function(e){
                    this.remove(e);
                }.bind(this)
            });
        }
        if (this.editNode){
            this.editNode.addEvents({
                "mouseover": function(){this.editNode.setStyles(this.css.actionEditNode_over);}.bind(this),
                "mouseout": function(){this.editNode.setStyles(this.css.actionEditNode);}.bind(this),
                "mousedown": function(){this.editNode.setStyles(this.css.actionEditNode_down);}.bind(this),
                "mouseup": function(){this.editNode.setStyles(this.css.actionEditNode_over);}.bind(this),
                "click": function(e){
                    this.editNumber(e);
                }.bind(this)
            });
        }
    },
    editNumber: function(){
        //this.numberAreaNode
        this.editAreaNode = new Element("div", {"styles": this.css.editAreaNode}).inject(this.node);
        this.editNumberInputNode = new Element("input", {"type": "number", "styles": this.css.editNumberInputNode}).inject(this.editAreaNode);
        this.editOkActionNode = new Element("div", {"styles": this.css.editOkActionNode, "text": this.explorer.app.lp.ok}).inject(this.editAreaNode);
        this.editCancelActionNode = new Element("div", {"styles": this.css.editCancelActionNode, "text": this.explorer.app.lp.cancel}).inject(this.editAreaNode);
        this.editNumberInputNode.set("value", this.data.serial);
        this.editNumberInputNode.focus();

        this.editCancelActionNode.addEvent("click", function(){
            this.cancelEdit();
        }.bind(this));
        this.editOkActionNode.addEvent("click", function(){
            this.okEdit();
        }.bind(this));

        this.setEditAreaSize();
        this.explorer.app.addEvent("resize", function(){
            this.setEditAreaSize();
        }.bind(this));
    },
    setEditAreaSize: function(){
        if (this.editAreaNode){
            var width = this.numberAreaNode.getSize().x + this.actionAreaNode.getSize().x-2;
            this.editAreaNode.setStyle("width", ""+width+"px");

            var inputWidth = width - 26 - 50 - 50;
            this.editNumberInputNode.setStyle("width", ""+inputWidth+"px");

            this.editAreaNode.position({
                relativeTo: this.numberAreaNode,
                position: 'topLeft',
                edge: 'topLeft'
            });
        }
    },
    okEdit: function(){
        var serial = this.editNumberInputNode.get("value");
        if (!serial) serial = 0;
        this.data.serial = serial;
        this.explorer.actions.updateSerialNumber(this.data.id, this.data, function(json){
            this.cancelEdit();
            this.numberAreaNode.set("text", this.data.serial);
        }.bind(this));

    },
    cancelEdit: function(){
        if (this.editAreaNode){
            this.editCancelActionNode.destroy();
            this.editOkActionNode.destroy();
            this.editNumberInputNode.destroy();
            this.editAreaNode.destroy();

            this.editCancelActionNode = null;
            this.editOkActionNode = null;
            this.editNumberInputNode = null;
            this.editAreaNode = null;
        }
    },
    remove: function(e){
        var lp = this.explorer.app.lp;
        var text = lp.deleteSerial.replace(/{key}/g, this.data.name);
        var _self = this;

        this.workAreaNode.setStyles(this.css.workItemWorkNode_remove);
        this.readyRemove = true;
        this.explorer.app.confirm("warn", e, lp.deleteSerialTitle, text, 350, 120, function(){
            _self.explorer.removeSerial(_self, true);
            this.close();
        }, function(){
            _self.workAreaNode.setStyles(_self.css.workItemWorkNode);
            _self.readyRemove = false;
            this.close();
        });
    },
    destroy: function(){
        this.node.destroy();
    },

});
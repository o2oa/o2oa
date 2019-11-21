MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
//MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.ScriptText",null,false);
MWF.xApplication.process.ProcessDesigner.widget.ProjectionEditor = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default",
        "maxTypeCount": {
            "string": 10,
            "long": 5,
            "double": 5,
            "boolean": 2,
            "date":2,
            "time": 2,
            "datetime": 5
        }
	},
	initialize: function(node, text, options){
		this.setOptions(options);
		this.node = $(node);
        this.data = (text) ? JSON.decode(text) : [];
        this.name = node.get("name");
		this.path = "/x_component_process_ProcessDesigner/widget/$ProjectionEditor/";
		this.cssPath = "/x_component_process_ProcessDesigner/widget/$ProjectionEditor/"+this.options.style+"/css.wcss";
		this._loadCss();
        this.selectedItems = [];
        this.items = {};
	},
    getData: function(){
	    return this.data;
    },
    load: function(){
        this.titleNode = this.node.getFirst("div").setStyles(this.css.titleNode).set("text", MWF.xApplication.process.ProcessDesigner.LP.projectionTitle);
        // this.titleNode = new Element("div", {"styles": this.css.titleNode}).inject(this.node);
        // this.titleNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.projectionTitle);
        this.tableArea = this.node.getLast("div");
        this.actionNode = this.tableArea.getPrevious().setStyles(this.css.actionNode).set("text", MWF.xApplication.process.ProcessDesigner.LP.projectionActionNode_add);

        var inputs = this.node.getElements("input");
        this.nameInput = inputs[0];
        this.pathInput = inputs[1];
        this.typeSelect = this.node.getElement("select");


        // this.tableArea = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);
        var html = "<table cellspacing='0' cellpadding='3px' width='100%' border='0'><tr>" +
            "<th>"+MWF.xApplication.process.ProcessDesigner.LP.projectionDataName+"</th>" +
            "<th>"+MWF.xApplication.process.ProcessDesigner.LP.projectionPath+"</th>" +
            "<th>"+MWF.xApplication.process.ProcessDesigner.LP.projectionType+"</th>" +
            "<th></th>" +
            "</tr></table>";
        this.tableArea.set("html", html);
        this.table = this.tableArea.getElement("table").setStyles(this.css.projectionTable);
        this.tableArea.getElements("th").setStyles(this.css.projectionTableTitle);

        this.runAction = new Element("div", {"styles": this.css.actionNode, "text": MWF.xApplication.process.ProcessDesigner.LP.projectionRunActionNode}).inject(this.node);

        this.loadProjectionList();
        // //this.loadProjectionCreate();
        //
        //
        // this.actionNode = new Element("div", {"styles": this.css.actionNode, "text": MWF.xApplication.process.ProcessDesigner.LP.projectionActionNode}).inject(this.node);
        this.actionNode.addEvent("click", this.changeProjectionItem.bind(this));

        this.runAction.addEvent("click", function(e){
            var _self = this;
            MWF.xDesktop.confirm("infor", e, MWF.xApplication.process.ProcessDesigner.LP.projectionRunTitle, MWF.xApplication.process.ProcessDesigner.LP.projectionRunText, 300, 120, function(){
                _self.runProjection();
                this.close();
            }, function(){
                this.close();
            }, null, null, "o2");

        }.bind(this));


        //
        // this.selectNode = new Element("div", {"styles": this.css.selectNode}).inject(this.node);
        //
        // this.downNode = new Element("div", {"styles": this.css.downNode}).inject(this.node);
        //
        // this.previewNode = new Element("div", {"styles": this.css.previewNode}).inject(this.node);
        // this.showNode = new Element("div", {"styles": this.css.showNode}).inject(this.node);
        //
        // this.propertyNode = new Element("div", {"styles": this.css.propertyNode}).inject(this.node);
        //
        // this.loadSelectNode();
    //    this.loadSerialActivity();
    },
    runProjection: function(){
	    o2.Actions.get("x_processplatform_assemble_designer").executeProjection(this.options.process, null, function(json){
	        if (json.data.value){
                o2.xDesktop.notice("success", {x: "right", y:"top"}, MWF.xApplication.process.ProcessDesigner.LP.projectionRunSuccess, this.node);
            }else{
                o2.xDesktop.notice("error", {x: "right", y:"top"}, MWF.xApplication.process.ProcessDesigner.LP.projectionRunError, this.node);
            }
        });
    },

    changeProjectionItem: function(){
	    if (this.currentItem) {
	        this.modifyProjectionItem();
        }else{
	        this.addProjectionItem();
        }
	},
    checkItemData: function(name, path, type){
        if (!name || !path){
            o2.xDesktop.notice("error", {x: "right", y:"top"}, MWF.xApplication.process.ProcessDesigner.LP.projectionInputError, this.node);
            return false;
        }
        var count = 0;
        for (var i=0; i<this.data.length; i++){
            if (this.data[i].type===type) count++;
            if (count>=this.options.maxTypeCount[type]){
                var txt = MWF.xApplication.process.ProcessDesigner.LP.projectionTypeCountError;
                txt = txt.replace(/{type}/g, type);
                txt = txt.replace(/{count}/g, this.options.maxTypeCount[type]);
                o2.xDesktop.notice("error", {x: "right", y:"top"}, txt, this.node);
                return false;
            }

            if (this.data[i].name===name && (!this.currentItem || this.data[i]!=this.currentItem.data)) {
                o2.xDesktop.notice("error", {x: "right", y:"top"}, MWF.xApplication.process.ProcessDesigner.LP.projectionSameNameError, this.node);
                return false;
            }
        }
        return true;
    },
    modifyProjectionItem: function(){
        var name = this.nameInput.get("value");
        var path = this.pathInput.get("value");
        var type = this.typeSelect.options[this.typeSelect.selectedIndex].value;

        if (this.checkItemData(name, path, type)){
            this.currentItem.data.name = name;
            this.currentItem.data.path = path;
            this.currentItem.data.type = type;
            this.currentItem.refresh();
            this.currentItem.unSelected();
            this.fireEvent("change");
            this.fireEvent("modifyItem");
        }
    },
    addProjectionItem: function(){
	    var name = this.nameInput.get("value");
        var path = this.pathInput.get("value");
        var type = this.typeSelect.options[this.typeSelect.selectedIndex].value;

        if (this.checkItemData(name, path, type)){
            var o = { "name": name, "path": path, "type": type };
            this.data.push(o);
            new MWF.xApplication.process.ProcessDesigner.widget.ProjectionEditor.Item(o, this);
            this.fireEvent("change");
            this.fireEvent("addItem");
        }
    },
    loadProjectionList: function(){
        this.data.each(function(d){
            new MWF.xApplication.process.ProcessDesigner.widget.ProjectionEditor.Item(d, this);
        }.bind(this));
    }

});
MWF.xApplication.process.ProcessDesigner.widget.ProjectionEditor.Item = new Class({
    initialize: function(data, editor){
        this.editor = editor;
        this.data = data;
        this.table = this.editor.table;
        this.css = this.editor.css;
        this.load();
    },
    load: function(){
        this.tr = new Element('tr').inject(this.table);
        var td = this.tr.insertCell().setStyles(this.css.projectionTableTd).set("text", this.data.name);
        td = this.tr.insertCell().setStyles(this.css.projectionTableTd).set("text", this.data.path);
        td = this.tr.insertCell().setStyles(this.css.projectionTableTd).set("text", this.data.type);

        td = this.tr.insertCell().setStyles(this.css.projectionTableTd);
        this.delAction = new Element("div", {"styles": this.css.projectionItemAction}).inject(td);

        this.setEvent();
    },
    setEvent: function(){
        this.delAction.addEvent("click", function(e){
            var txt = MWF.xApplication.process.ProcessDesigner.LP.projectionDeleteItem;
            txt = txt.replace(/{name}/g, this.data.name);
            txt = txt.replace(/{path}/g, this.data.path);
            var _self = this;
            MWF.xDesktop.confirm("infor", e, MWF.xApplication.process.ProcessDesigner.LP.projectionDeleteItemTitle, txt, 300, 120, function(){
                _self.destroy();
                this.close();
            }, function(){
                this.close();
            }, null, null, "o2");
        }.bind(this));

        this.tr.addEvents({
            "click": function(){
                if (this.editor.currentItem) this.editor.currentItem.unSelected();
                this.selected();
            }.bind(this)
        })
    },
    selected: function(){
        this.editor.currentItem = this;
        this.tr.setStyles(this.css.projectionTableTr_selected);
        this.editor.nameInput.set("value", this.data.name);
        this.editor.pathInput.set("value", this.data.path);
        var ops = this.editor.typeSelect.options;
        for (var i=0; i<ops.length; i++){
            if (ops[i].value===this.data.type){
                ops[i].set("selected", true);
                break;
            }
        }
        this.editor.actionNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.projectionActionNode_modify);
    },
    unSelected: function(){
        this.editor.currentItem = null;
        this.tr.setStyles(this.css.projectionTableTr);
        this.editor.actionNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.projectionActionNode_add);
    },
    refresh: function(){
        var tds = this.tr.getElements("td");
        tds[0].set("text", this.data.name);
        tds[1].set("text", this.data.path);
        tds[2].set("text", this.data.type);
    },
    destroy: function(){
        this.tr.destroy();
        this.editor.data.erase(this.data);
        this.editor.fireEvent("change");
        this.editor.fireEvent("deleteItem");
        o2.release(this);
    }
});

MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
//MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.ScriptText",null,false);
MWF.xApplication.process.ProcessDesigner.widget.QueryTablePublisher = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default"
	},
	initialize: function(node, text, options, designer){
		this.setOptions(options);
		this.node = $(node);
        this.data = (text) ? JSON.decode(text) : [];
        this.name = node.get("name");
        this.designer = designer;
		this.path = "../x_component_process_ProcessDesigner/widget/$QueryTablePublisher/";
		this.cssPath = "../x_component_process_ProcessDesigner/widget/$QueryTablePublisher/"+this.options.style+"/css.wcss";
		this._loadCss();
        this.selectedItems = [];
        this.items = {};
	},
    getData: function(){
	    return this.data;
    },
    load: function(){
        this.tableArea = this.node.getFirst("div");
        this.actionNode = this.tableArea.getNext().setStyles(this.css.actionNode).set("text", MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.querytableActionNode_add);

        debugger;
        this.tableNameSelector = this.node.getElement("[name='tableName']").retrieve("selector");
        this.dataByRadioList = this.node.getElements("input[type='radio']");
        this.dataPathInput = this.node.getElement("[name='queryTableDataPath']");
        this.dataScriptSelector = this.node.getElement("[name='queryTableAssignDataScript']").retrieve("selector");
        this.dataScriptTextEditor = this.node.getElement("[name='queryTableAssignDataScriptText']").retrieve("editor");

        debugger;
        // this.tableArea = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);
        var html = "<table cellspacing='0' cellpadding='3px' width='100%' border='0'><tr>" +
            "<th>"+MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.querytable+"</th>" +
            "<th>"+MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.queryTableDataBy+"</th>" +
            "<th>"+MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.dataPath+"</th>" +
            "<th></th>" +
            "</tr></table>";
        this.tableArea.set("html", html);
        this.table = this.tableArea.getElement("table").setStyles(this.css.projectionTable);
        this.tableArea.getElements("th").setStyles(this.css.projectionTableTitle);

        this.loadQuerytableList();
        this.actionNode.addEvent("click", this.changeQuerytableItem.bind(this));

    },

    changeQuerytableItem: function(){
	    if (this.currentItem) {
	        this.modifyQuerytableItem();
        }else{
	        this.addQuerytableItem();
        }
        this.tableNameSelector.setData( "" );
        this.dataByRadioList.each(function (radio) {
            if( radio.value === "dataPath" ){
                radio.checked;
                radio.click();
            }
        });
        this.dataPathInput.set("value", "");
        if(this.dataScriptSelector)this.dataScriptSelector.loadValue( "" );
        if( this.dataScriptTextEditor && this.dataScriptTextEditor.editor ){
            this.dataScriptTextEditor.editor.setValue( "" );
        }

        this.designer.data.tableName = "";
        this.designer.data.queryTableDataBy = "dataPath";
        this.designer.data.queryTableDataPath = "";
        this.designer.data.queryTableAssignDataScript = "";
        this.designer.data.queryTableAssignDataScriptText = "";
	},
    checkItemData: function(obj){
        if (!obj.tableName ){
            o2.xDesktop.notice("error", {x: "right", y:"top"}, MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.querytableInputError, this.node);
            return false;
        }
        if( obj.queryTableDataBy === "script" && !obj.targetAssignDataScriptText ){
            o2.xDesktop.notice("error", {x: "right", y:"top"}, MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.queryTableAssignDataScriptError, this.node);
            return false;
        }
        return true;
    },
    getCurrentData: function(){
        var obj = {
            tableName: this.designer.data.tableName,
            queryTableDataBy: this.designer.data.queryTableDataBy || "dataPath"
        };
        if( obj.queryTableDataBy === "script" ){
            obj.targetAssignDataScript = this.designer.data.queryTableAssignDataScript;
            obj.targetAssignDataScriptText = this.designer.data.queryTableAssignDataScriptText;
        }else{
            obj.queryTableDataPath = this.designer.data.queryTableDataPath || "";
        }
        return obj;
    },
    modifyQuerytableItem: function(){
	    var obj = this.getCurrentData();
        if (this.checkItemData(obj)){
            ["tableName","queryTableDataBy", "queryTableDataPath","targetAssignDataScript","targetAssignDataScriptText"].each(function(key){
                if( obj.hasOwnProperty(key) ){
                    this.currentItem.data[key] = obj[key];
                }else{
                    delete this.currentItem.data[key];
                }
            }.bind(this));

            this.currentItem.refresh();
            this.currentItem.unSelected();
            this.fireEvent("change");
            this.fireEvent("modifyItem");
        }
    },
    addQuerytableItem: function(){
        var obj = this.getCurrentData();

        if (this.checkItemData(obj)){
            this.data.push(obj);
            new MWF.xApplication.process.ProcessDesigner.widget.QueryTablePublisher.Item(obj, this);
            this.fireEvent("change");
            this.fireEvent("addItem");
        }
    },
    loadQuerytableList: function(){
        this.data.each(function(d){
            new MWF.xApplication.process.ProcessDesigner.widget.QueryTablePublisher.Item(d, this);
        }.bind(this));
    }

});
MWF.xApplication.process.ProcessDesigner.widget.QueryTablePublisher.Item = new Class({
    initialize: function(data, editor){
        this.editor = editor;
        this.data = data;
        this.table = this.editor.table;
        this.css = this.editor.css;
        this.load();
    },
    load: function(){
        var lp = MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate;
        this.tr = new Element('tr').inject(this.table);
        var td = this.tr.insertCell().setStyles(this.css.projectionTableTd).set("text", this.data.tableName);
        td = this.tr.insertCell().setStyles(this.css.projectionTableTd).set("text", this.data.queryTableDataBy === "script" ? lp.dataScript : lp.dataPath );
        td = this.tr.insertCell().setStyles(this.css.projectionTableTd).set("text", this.data.queryTableDataPath);

        td = this.tr.insertCell().setStyles(this.css.projectionTableTd);
        this.delAction = new Element("div", {"styles": this.css.projectionItemAction}).inject(td);

        this.setEvent();
    },
    setEvent: function(){
        this.delAction.addEvent("click", function(e){
            var txt = MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.querytableDeleteItem;
            var _self = this;
            MWF.xDesktop.confirm("infor", e, MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.querytableDeleteItemTitle, txt, 300, 120, function(){
                _self.destroy();
                this.close();
            }, function(){
                this.close();
            }, null, null, "o2");
            e.stopPropagation();
        }.bind(this));

        this.tr.addEvents({
            "click": function(){
                if (this.editor.currentItem) this.editor.currentItem.unSelected();
                this.selected();
            }.bind(this)
        });
    },
    selected: function(){
        this.editor.currentItem = this;
        this.tr.setStyles(this.css.projectionTableTr_selected);

        var eData = this.editor.designer.data;
        eData.tableName = this.data.tableName;
        eData.queryTableDataBy = this.data.queryTableDataBy || "dataPath";
        eData.queryTableDataPath = this.data.queryTableDataPath || "";
        eData.queryTableAssignDataScript = this.data.targetAssignDataScript;
        eData.queryTableAssignDataScriptText = this.data.targetAssignDataScriptText;

        var editor = this.editor;
        editor.tableNameSelector.setData( eData.tableName );
        editor.dataByRadioList.each(function (radio) {
            if( radio.value === eData.queryTableDataBy ){
                radio.checked = true;
                radio.click();
            }
        });
        editor.dataPathInput.set("value", eData.queryTableDataPath || "");
        editor.dataScriptSelector.loadValue( eData.queryTableAssignDataScript || "");
        if( editor.dataScriptTextEditor && editor.dataScriptTextEditor.editor ){
            editor.dataScriptTextEditor.editor.setValue( eData.queryTableAssignDataScriptText || "" );
        }

        this.editor.actionNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.querytableActionNode_modify);
    },
    unSelected: function(){
        this.editor.currentItem = null;
        this.tr.setStyles(this.css.projectionTableTr);
        this.editor.actionNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.querytableActionNode_add);
    },
    refresh: function(){
        var lp = MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate;
        var tds = this.tr.getElements("td");
        tds[0].set("text", this.data.tableName || "");
        tds[1].set("text", this.data.queryTableDataBy === "script" ? lp.dataScript : lp.dataPath );
        tds[2].set("text", this.data.queryTableDataPath || "");
    },
    destroy: function(){
        this.tr.destroy();
        this.editor.data.erase(this.data);
        this.editor.currentItem = null;
        this.editor.actionNode.set("text", MWF.xApplication.process.ProcessDesigner.LP.propertyTemplate.querytableActionNode_add);
        this.editor.fireEvent("change");
        this.editor.fireEvent("deleteItem");
        o2.release(this);
    }
});

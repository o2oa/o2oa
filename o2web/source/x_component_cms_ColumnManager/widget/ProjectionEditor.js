MWF.xApplication.cms.ColumnManager.widget = MWF.xApplication.cms.ColumnManager.widget || {};
MWF.xDesktop.requireApp("cms.ColumnManager", "lp."+o2.language,null,false);
MWF.xApplication.cms.ColumnManager.widget.ProjectionEditor = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default",
        "maxTypeCount": {
            "string": 10,
            "long": 2,
            "double": 2,
            "dateTime": 3
        }
	},
	initialize: function(node, text, options){
		this.setOptions(options);
		this.node = $(node);
        this.data = (text) ? JSON.decode(text) : [];
        this.name = node.get("name");
		this.path = "../x_component_cms_ColumnManager/widget/$ProjectionEditor/";
		this.cssPath = "../x_component_cms_ColumnManager/widget/$ProjectionEditor/"+this.options.style+"/css.wcss";
		this._loadCss();
        this.selectedItems = [];
        this.items = {};
	},
    getData: function(){
	    return this.data;
    },
    load: function(){

	    this.node.set("html", this.getHtml());
        this.node.setStyles({
            "margin": "0px 40px"
        });

        this.titleNode = this.node.getFirst("div").setStyles(this.css.titleNode).set("text", MWF.xApplication.cms.ColumnManager.LP.projectionTitle);
        this.runAction = new Element("div", {"styles": this.css.runAction, "text": MWF.xApplication.cms.ColumnManager.LP.projectionRunActionNode}).inject(this.titleNode);
        this.runAction.addEvent("click", function(e){
            var _self = this;
            MWF.xDesktop.confirm("infor", e, MWF.xApplication.cms.ColumnManager.LP.projectionRunTitle, MWF.xApplication.cms.ColumnManager.LP.projectionRunText, 300, 120, function(){
                _self.runProjection();
                this.close();
            }, function(){
                this.close();
            }, null, null, "o2");

        }.bind(this));

        // this.titleNode = new Element("div", {"styles": this.css.titleNode}).inject(this.node);
        // this.titleNode.set("text", MWF.xApplication.cms.ColumnManager.LP.projectionTitle);
        this.contentNode = this.titleNode.getNext();
        this.tableArea = this.contentNode.getLast("div");
        this.actionNode = this.tableArea.getPrevious().setStyles(this.css.actionNode).set("text", "↓ "+MWF.xApplication.cms.ColumnManager.LP.projectionActionNode_add);

        var inputs = this.node.getElements("input");
        this.nameInput = inputs[0];
        this.pathInput = inputs[1];
        this.typeSelect = this.node.getElement("select");


        // this.tableArea = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);
        var html = "<table cellspacing='0' cellpadding='3px' width='100%' border='0'><tr>" +
            "<th>"+MWF.xApplication.cms.ColumnManager.LP.projectionDataName+"</th>" +
            "<th>"+MWF.xApplication.cms.ColumnManager.LP.projectionPath+"</th>" +
            "<th>"+MWF.xApplication.cms.ColumnManager.LP.projectionType+"</th>" +
            "<th>"+MWF.xApplication.cms.ColumnManager.LP.projectionColumnName+"</th>" +
            "<th></th>" +
            "</tr></table>";
        this.tableArea.set("html", html);
        this.table = this.tableArea.getElement("table").setStyles(this.css.projectionTable);
        this.tableArea.getElements("th").setStyles(this.css.projectionTableTitle);



        this.loadProjectionList();
        this.actionNode.addEvent("click", this.changeProjectionItem.bind(this));

    },
    getHtml: function(){
        var html =
            '<div>'+MWF.xApplication.cms.ColumnManager.LP.mappingData+'</div>'+
            '<div style="max-width: 800px;margin-bottom: 40px;">'+
            '   <table width="100%" border="0" cellpadding="2" cellspacing="0" class="editTable">'+
            '       <tr>'+
            '           <td class="editTableTitle">'+MWF.xApplication.cms.ColumnManager.LP.dataName+':</td>'+
            '           <td class="editTableValue"><input type="text" value="" class="editTableInput" style="width:90%;"/></td>'+
            // '    </tr>'+
            // '    <tr>'+
            '           <td class="editTableTitle">'+MWF.xApplication.cms.ColumnManager.LP.dataPath+':</td>'+
            '           <td class="editTableValue"><input type="text" value="" class="editTableInput"  style="width:90%;"/></td>'+
            // '</tr>'+
            // '<tr>'+
            '           <td class="editTableTitle">'+MWF.xApplication.cms.ColumnManager.LP.dataType+':</td>'+
            '           <td class="editTableValue"><select>'+
            '               <option value="string">string</option>'+
            '               <option value="long">long</option>'+
            '               <option value="double">double</option>'+
            '               <option value="dateTime">dateTime</option>'+
            '           </select></td>'+
            '       </tr>'+
            '   </table>'+
            '   <div></div>'+
            '   <div></div>'+
            '<div>';
        return html;
    },
    runProjection: function(){
	    o2.Actions.load("x_cms_assemble_control").CategoryInfoAction.executeProjection(this.options.category, null, function(json){
	        if (json.data.value){
                o2.xDesktop.notice("success", {x: "right", y:"top"}, MWF.xApplication.cms.ColumnManager.LP.projectionRunSuccess, this.node);
            }else{
                o2.xDesktop.notice("error", {x: "right", y:"top"}, MWF.xApplication.cms.ColumnManager.LP.projectionRunError, this.node);
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
    checkItemData: function(name, path, type, operation){
        if (!name || !path){
            o2.xDesktop.notice("error", {x: "right", y:"top"}, MWF.xApplication.cms.ColumnManager.LP.projectionInputError, this.node);
            return false;
        }
        var count = 0;
        for (var i=0; i<this.data.length; i++){
            if (this.data[i].type===type) count++;
            var flag = false;
            if( operation === "add" && count>=this.options.maxTypeCount[type]  ){
                flag = true;
            }
            if( operation === "modify" && count>this.options.maxTypeCount[type]  ){
                flag = true;
            }
            if (flag){
                var txt = MWF.xApplication.cms.ColumnManager.LP.projectionTypeCountError;
                txt = txt.replace(/{type}/g, type);
                txt = txt.replace(/{count}/g, this.options.maxTypeCount[type]);
                o2.xDesktop.notice("error", {x: "right", y:"top"}, txt, this.node);
                return false;
            }

            if (this.data[i].name===name && (!this.currentItem || this.data[i]!=this.currentItem.data)) {
                o2.xDesktop.notice("error", {x: "right", y:"top"}, MWF.xApplication.cms.ColumnManager.LP.projectionSameNameError, this.node);
                return false;
            }
        }
        return true;
    },
    checkItemColumn: function(){
        var columnNames = {
            "string": 0,
            "long": 0,
            "double": 0,
            "boolean": 0,
            "date":0,
            "time": 0,
            "dateTime": 0
        };
        var rows = this.table.rows;
        for (var i=0; i<this.data.length; i++){
            columnNames[this.data[i].type]++;
            var n = columnNames[this.data[i].type] || 1;
            var c = this.data[i].type+"Value"+(n>=10 ? n : "0"+n);
            rows[i+1].cells[3].set("text", c);
        }
    },
    modifyProjectionItem: function(){
        var name = this.nameInput.get("value");
        var path = this.pathInput.get("value");
        var type = this.typeSelect.options[this.typeSelect.selectedIndex].value;

        if (this.checkItemData(name, path, type, "modify")){
            this.currentItem.data.name = name;
            this.currentItem.data.path = path;
            this.currentItem.data.type = type;
            this.currentItem.refresh();
            this.currentItem.unSelected();

            this.checkItemColumn();

            this.fireEvent("change");
            this.fireEvent("modifyItem");
        }
    },
    addProjectionItem: function(){
	    var name = this.nameInput.get("value");
        var path = this.pathInput.get("value");
        var type = this.typeSelect.options[this.typeSelect.selectedIndex].value;

        if (this.checkItemData(name, path, type, "add")){
            var o = { "name": name, "path": path, "type": type };
            this.data.push(o);
            new MWF.xApplication.cms.ColumnManager.widget.ProjectionEditor.Item(o, this);

            this.checkItemColumn();

            this.fireEvent("change");
            this.fireEvent("addItem");
        }
    },
    loadProjectionList: function(){
        this.data.each(function(d, i){
            new MWF.xApplication.cms.ColumnManager.widget.ProjectionEditor.Item(d, this);
        }.bind(this));
        this.checkItemColumn();
    }

});
MWF.xApplication.cms.ColumnManager.widget.ProjectionEditor.Item = new Class({
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

        td = this.tr.insertCell().setStyles(this.css.projectionTableTd).set("text", this.data.type);

        td = this.tr.insertCell().setStyles(this.css.projectionTableTd);
        this.delAction = new Element("div", {"styles": this.css.projectionItemAction}).inject(td);

        this.setEvent();
    },
    setEvent: function(){
        this.delAction.addEvent("click", function(e){
            var txt = MWF.xApplication.cms.ColumnManager.LP.projectionDeleteItem;
            txt = txt.replace(/{name}/g, this.data.name);
            txt = txt.replace(/{path}/g, this.data.path);
            var _self = this;
            MWF.xDesktop.confirm("infor", e, MWF.xApplication.cms.ColumnManager.LP.projectionDeleteItemTitle, txt, 300, 120, function(){
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
        this.editor.actionNode.set("text", "↓ "+MWF.xApplication.cms.ColumnManager.LP.projectionActionNode_modify);
    },
    unSelected: function(){
        this.editor.currentItem = null;
        this.tr.setStyles(this.css.projectionTableTr);
        this.editor.actionNode.set("text", "↓ "+MWF.xApplication.cms.ColumnManager.LP.projectionActionNode_add);
    },
    refresh: function(){
        var tds = this.tr.getElements("td");
        tds[0].set("text", this.data.name);
        tds[1].set("text", this.data.path);
        tds[2].set("text", this.data.type);
    },
    destroy: function(){
        if( this.editor.currentItem === this ){
           this.unSelected();
        }
        this.tr.destroy();
        this.editor.data.erase(this.data);

        this.editor.checkItemColumn();

        this.editor.fireEvent("change");
        this.editor.fireEvent("deleteItem");
        o2.release(this);
    }
});

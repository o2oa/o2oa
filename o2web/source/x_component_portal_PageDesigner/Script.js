MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.PageDesigner = MWF.xApplication.portal.PageDesigner || {};
MWF.xDesktop.requireApp("portal.PageDesigner", "lp."+MWF.language, null, false);
MWF.require("MWF.widget.JavascriptEditor", null, false);

MWF.xApplication.portal.PageDesigner.Script = new Class({
    Implements: [Options, Events],

    options: {
        "style": "default"
    },

    initialize: function(designer, content, data, options){
        this.setOptions(options);
        this.path = "../x_component_portal_PageDesigner/$Script/";
        this.stylePath = "../x_component_portal_PageDesigner/$Script/"+this.options.style+"/style.css";
        this.viewPath = "../x_component_portal_PageDesigner/$Script/"+this.options.style+"/view.html";

        this.designer = designer;
        this.content = content;
        this.data = data;
        this.items = [];
        //this.categorys = {};
        this.moduleCategorys = {};
        this.pathCategorys = {};

        this.loadView();
    },
    loadView: function(){
        this.content.show();
        this.content.set("load", {"onSuccess": function(){
            this.node = this.content.getElement(".node");
            this.listNode = this.content.getElement(".listNode");
            this.listActionNode = this.content.getElement(".listActionNode");
            this.actionButtons = this.content.getElements(".listActionButton");
            this.listContentNode = this.content.getElement(".listContentNode");
            this.separatorNode = this.content.getElement(".separatorNode");
            this.scriptNode = this.content.getElement(".scriptNode");
            this.scriptAreaNode = this.content.getElement(".scriptAreaNode");
            this.listAreaNode = this.content.getElement(".listAreaNode");
            this.scriptTabNode = this.content.getElement(".scriptTabNode");
            o2.loadCss(this.stylePath, this.content, function(){
                this.load();
            }.bind(this));
        }.bind(this)}).load(this.viewPath);
    },
    load: function(){
        this.actionButtons[0].set("text", this.designer.lp.byModule);
        this.actionButtons[1].set("text", this.designer.lp.byPath);
        this.categoryType = "module";

        MWF.require("MWF.widget.Tab", null, false);
        this.scriptTab = new MWF.widget.Tab(this.scriptTabNode, {"style": "script"});
        this.scriptTab.load();

        this.setEvent();

        //form, page
        if (!this.data.jsheader) this.data.jsheader = {"code": "", "html": ""};
        this.addScriptItem(this.data.jsheader, "code", this.data, "jsheader");
        this.addScriptItem(this.data.validationOpinion, "code", this.data, "validationOpinion");
        this.addScriptItem(this.data.validationRoute, "code", this.data, "validationRoute");
        this.addScriptItem(this.data.validationFormCustom, "code", this.data, "validationFormCustom");
        Object.each(this.data.events, function(event, key){
            this.addScriptItem(event, "code", this.data, this.designer.lp.events+"."+key);
        }.bind(this));

        Object.each(this.data.moduleList, function(v){
            this.createModuleScript(v);
        }.bind(this));
    },
    createModuleScript: function(v){
        switch (v.type){
            case "Button":
                this.loadButtonScript(v); break;
            case "Calendar":
                this.loadCalendarScript(v); break;
            case "Checkbox":
                this.loadCheckboxScript(v); break;
            case "Div":
                this.loadDivScript(v); break;
            // case "Html":
            //     this.loadHtmlScript(v); break;
            case "Iframe":
                this.loadIframeScript(v); break;
            case "Image":
                this.loadImageScript(v); break;
            case "Label":
                this.loadLabelScript(v); break;
            case "Personfield":
                this.loadPersonfieldScript(v); break;
            case "Org":
                this.loadOrgScript(v); break;
            case "Radio":
                this.loadRadioScript(v); break;
            case "Select":
                this.loadSelectScript(v); break;
            case "Source":
                this.loadSourceScript(v); break;
            case "SourceText":
                this.loadSourceTextScript(v); break;
            case "Stat":
                this.loadStatScript(v); break;
            case "SubSource":
                this.loadSubSourceScript(v); break;
            case "Tab$Content":
                this.loadTab$ContentScript(v); break;
            case "Tab$Page":
                this.loadTab$PageScript(v); break;
            case "Tab":
                this.loadTabScript(v); break;
            case "Table$Td":
                this.loadTable$TdScript(v); break;
            case "Table":
                this.loadTableScript(v); break;
            case "Textarea":
                this.loadTextareaScript(v); break;
            case "Textfield":
                this.loadTextfieldScript(v); break;
            case "Tree":
                this.loadTreeScript(v); break;
            case "View":
                this.loadViewScript(v); break;
            case "Actionbar":
                this.loadActionbarScript(v); break;
            case "Address":
                this.loadAddressScript(v); break;
            case "Attachment":
                this.loadAttachmentScript(v); break;
            case "Combox":
                this.loadComboxScript(v); break;
            case "Datagrid":
                this.loadDatagridScript(v); break;
            case "Datagrid$Data":
                this.loadDatagrid$DataScript(v); break;
            case "Datagrid$Title":
                this.loadDatagrid$TitleScript(v); break;
            case "Datatable":
                this.loadDatatableScript(v); break;
            case "Datatable$Data":
                this.loadDatatable$DataScript(v); break;
            case "Datatable$Title":
                this.loadDatatable$TitleScript(v); break;
            case "Datatemplate":
                this.loadDatatemplateScript(v); break;
            case "Htmleditor":
                this.loadHtmleditorScript(v); break;
            case "ImageClipper":
                this.loadImageClipperScript(v); break;
            case "Log":
                this.loadLogScript(v); break;
            case "Monitor":
                this.loadMonitorScript(v); break;
            case "Number":
                this.loadNumberScript(v); break;
            case "Office":
                this.loadOfficeScript(v); break;
            case "Opinion":
                this.loadOpinionScript(v); break;
            case "Orgfield":
                this.loadOrgfieldScript(v); break;
            case "Sidebar":
                this.loadSidebarScript(v); break;
            case "Subform":
                this.loadSubformScript(v); break;
            case "ViewSelector":
                this.loadViewSelectorScript(v); break;
            case "Statement":
                this.loadStatementScript(v); break;
            case "StatementSelector":
                this.loadStatementSelectorScript(v); break;
            case "Importer":
                this.loadImporterScript(v); break;
        }
        this.bindDataId(v);
    },

    createCategory: function(data, path, type){
        var category;
        var cType = type || this.categoryType;
        if (cType==="module"){
            if (data.type==="Form" || data.type==="Page"){
                category = new MWF.xApplication.portal.PageDesigner.Script.Category(this, data.type, 0, data, "module");
                this.moduleCategorys[data.type] = category;
            }else{
                category = new MWF.xApplication.portal.PageDesigner.Script.Category(this, "("+data.type+")-"+data.id, 0, data, "module");
                this.moduleCategorys[data.id] = category;
            }
        }else{
            category = new MWF.xApplication.portal.PageDesigner.Script.Category(this, path, 0, data, "path");
            this.pathCategorys[path] = category;
        }
        return category;
    },
    bindDataId: function(data){
        var id = data.id;
        Object.defineProperty(data, "id", {
            configurable : true,
            enumerable : true,
            "get": function(){return id;},
            "set": function(v){
                // this.items.each(function(item){
                //     if (item.module.id===data.id) item.resetText(v);
                // });

                Object.each(this.moduleCategorys, function(category){
                    if (category.module.id === data.id){
                        category.resetName("("+data.type+")-"+v, v);
                    }
                });
                id = v;
                //category.resetName(category.name.replace(/\-.*/, "-"+v));

            }.bind(this)
        });
    },

    loadButtonScript: function(data){
        this.loadEventsScript(data);
    },
    loadCalendarScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadCheckboxScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.itemScript, "code", data, "itemScript");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadDivScript: function(data){
        this.loadEventsScript(data);
    },
    loadIframeScript: function(data){
        this.addScriptItem(data.script, "code", data, "iframeScript");
        this.loadEventsScript(data);
    },
    loadImageScript: function(data){
        this.loadEventsScript(data);
    },
    loadLabelScript: function(data){
        this.addScriptItem(data.script, "code", data, "labelScript");
        this.loadEventsScript(data);
    },
    loadPersonfieldScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.rangeKey, "code", data, "rangeKey");
        this.addScriptItem(data.rangeDutyKey, "code", data, "rangeDutyKey");
        this.addScriptItem(data.exclude, "code", data, "exclude");
        this.addScriptItem(data.rangeKey, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadOrgScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.identityRangeKey, "code", data, "identityRangeKey");
        this.addScriptItem(data.unitRangeKey, "code", data, "unitRangeKey");
        this.addScriptItem(data.rangeDutyKey, "code", data, "rangeDutyKey");
        this.addScriptItem(data.exclude, "code", data, "exclude");
        this.addScriptItem(data.rangeKey, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadRadioScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.itemScript, "code", data, "itemScript");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadSelectScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.itemScript, "code", data, "itemScript");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadSourceScript: function(data){
        this.addScriptItem(data.cookies, "code", data, "cookies");
        this.addScriptItem(data.requestBody, "code", data, "requestBody");
        this.loadEventsScript(data);
    },
    loadSourceTextScript: function(data){
        this.addScriptItem(data.jsonText, "code", data, "jsonText");
        this.loadEventsScript(data);
    },
    loadStatScript: function(data){
        this.loadEventsScript(data);
    },
    loadSubSourceScript: function(data){
        this.loadEventsScript(data);
    },
    loadTabScript: function(data){
        this.loadEventsScript(data);
    },
    loadTab$PageScript: function(data){
        this.loadEventsScript(data);
    },
    loadTab$ContentScript: function(data){
        this.loadEventsScript(data);
    },
    loadTableScript: function(data){
        this.loadEventsScript(data);
    },
    loadTable$TdScript: function(data){
        this.loadEventsScript(data);
    },
    loadTextareaScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadTextfieldScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadTreeScript: function(data){
        this.addScriptItem(data.dataScript, "code", data, "dataScript");
        this.loadEventsScript(data);
    },
    loadViewScript: function(data){
        this.loadEventsScript(data);
    },
    loadImporterScript: function(data){
        this.addScriptItem(data.excelName, "code", data, "excelName");
        this.loadEventsScript(data);
    },

    loadActionbarScript: function(data){
        if (data.tools){
            data.tools.each(function(tool){
                var item = this.addScriptItem(tool, "actionScript", data, "action.tools", tool.text);
                this.bindActionbarToolText(tool, item);
            }.bind(this));
        }
    },
    bindActionbarToolText: function(tool, item){
        var toolItem = item;
        var text = tool.text;
        Object.defineProperty(tool, "text", {
            configurable : true,
            enumerable : true,
            "get": function(){return text;},
            "set": function(v){
                if (toolItem){
                    toolItem.par = v;
                    toolItem.resetText();
                }
                text = v;

            }.bind(this)
        });
    },

    loadAddressScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadAttachmentScript: function(data){
        this.addScriptItem(data.validation, "code", data, "validation");
        this.loadEventsScript(data);
    },
    loadComboxScript: function(data){
        this.addScriptItem(data.itemScript, "code", data, "itemScript");
        this.addScriptItem(data.itemDynamic, "code", data, "itemDynamic");
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadDatagridScript: function(data){
        this.addScriptItem(data.defaultData, "code", data, "defaultData");
        this.addScriptItem(data.editableScript, "code", data, "editableScript");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.addScriptItem(data.excelName, "code", data, "excelName");
        this.loadEventsScript(data);
    },
    loadDatagrid$DataScript: function(data){  this.loadEventsScript(data); },
    loadDatagrid$TitleScript: function(data){  this.loadEventsScript(data); },

    loadDatatableScript: function(data){
        this.addScriptItem(data.defaultData, "code", data, "defaultData");
        this.addScriptItem(data.editableScript, "code", data, "editableScript");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.addScriptItem(data.excelName, "code", data, "excelName");
        this.loadEventsScript(data);
    },
    loadDatatable$DataScript: function(data){  this.loadEventsScript(data); },
    loadDatatable$TitleScript: function(data){  this.loadEventsScript(data); },

    loadDatatemplateScript: function(data){
        this.addScriptItem(data.defaultData, "code", data, "defaultData");
        this.addScriptItem(data.editableScript, "code", data, "editableScript");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.addScriptItem(data.excelName, "code", data, "excelName");
        this.loadEventsScript(data);
    },



    loadHtmleditorScript: function(data){
        this.addScriptItem(data.config, "code", data, "config");
        this.loadEventsScript(data);
    },
    loadImageClipperScript: function(data){  this.loadEventsScript(data); },

    loadLogScript: function(data){
        this.addScriptItem(data.filterScript, "code", data, "filterScript");
        this.loadEventsScript(data);
    },
    loadMonitorScript: function(data){  this.loadEventsScript(data); },

    loadNumberScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.sectionByScript, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadOfficeScript: function(data){
        this.addScriptItem(data.readScript, "code", data, "readScript");
        this.addScriptItem(data.fileSite, "code", data, "fileSite");
        this.loadEventsScript(data);
    },
    loadOpinionScript: function(data){
        this.addScriptItem(data.validation, "code", data, "validation");
        this.loadEventsScript(data);
    },
    loadOrgfieldScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.rangeKey, "code", data, "rangeKey");
        this.addScriptItem(data.exclude, "code", data, "exclude");
        this.addScriptItem(data.rangeKey, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadSidebarScript: function(data){
        if (data.tools){
            data.tools.each(function(tool){
                var item = this.addScriptItem(tool, "actionScript", data, "action.tools", tool.text);
                this.bindActionbarToolText(tool, item);
            }.bind(this));
        }
    },
    loadSubformScript: function(data){
        this.addScriptItem(data.subformScript, "code", data, "subformScript");
        this.loadEventsScript(data);
    },
    loadViewSelectorScript: function(data){
        this.addScriptItem(data.selectedScript, "code", data, "selectedScript");
        this.loadEventsScript(data);
    },

    loadStatementScript: function(data){
        this.loadEventsScript(data);
    },
    loadStatementSelectorScript: function(data){
        this.addScriptItem(data.selectedScript, "code", data, "selectedScript");
        this.loadEventsScript(data);
    },


    loadEventsScript: function(data){
        Object.each(data.events, function(event, key){
            this.addModuleEventScriptItem(event, "code", key, data);
        }.bind(this));
    },

    addScriptItem: function(data, key, module, path, par){
        if (!data) return null;
        var item = new MWF.xApplication.portal.PageDesigner.Script.Item(this, data, key, module, path, par);
        this.items.push(item);
        return item;
    },
    addModuleEventScriptItem: function(event, key, eventName, data, par){
        var item = new MWF.xApplication.portal.PageDesigner.Script.Item(this, event, key, data, this.designer.lp.events+"."+eventName, par);
        this.items.push(item);
        return item;
    },
    deleteScriptItem: function(module, path, par){
        var category = this.moduleCategorys[module.id];
        if (category){
            var count = category.items.length;
            for (var i=0; i<count; i++){
                var item = category.items[i];
                if (item.module.id===module.id && item.path===path && ((par) ? item.par===par : true) ){
                    item.destroy();
                    i--; count--;
                }
            }
        }
    },

    setSize: function(){
        var size = this.content.getSize();
        var paddings = this.listAreaNode.getStyles("padding-top", "padding-bottom");
        var margins = this.listNode.getStyles("margin-top", "margin-bottom");
        var actionSize = this.listActionNode.getSize();
        var actionMargins = this.listActionNode.getStyles("margin-top", "margin-bottom");

        var h = size.y-paddings["padding-top"].toFloat()-paddings["padding-bottom"].toFloat()-margins["margin-top"].toFloat()-margins["margin-bottom"].toFloat()-5;
        this.separatorNode.setStyle("height", ""+h+"px");

        h = h-actionSize.y-actionMargins["margin-top"].toFloat()-actionMargins["margin-bottom"].toFloat();
        this.listNode.setStyle("height", ""+h+"px");

        //var scriptSize = this.scriptAreaNode.getSize();
        paddings = this.scriptAreaNode.getStyles("padding-top", "padding-bottom");
        margins = this.scriptNode.getStyles("margin-top", "margin-bottom");
        h = size.y-paddings["padding-top"].toFloat()-paddings["padding-bottom"].toFloat()-margins["margin-top"].toFloat()-margins["margin-bottom"].toFloat();
        this.scriptNode.setStyle("height", ""+h+"px");

        if (this.scriptTab) this.scriptTab.resize();
        // var tabSize = this.scriptTab.tabNodeContainer.getComputedSize();
        // var tabMarginTop = this.scriptTab.tabNodeContainer.getStyle("margin-top").toFloat();
        // var tabMarginBottom = this.scriptTab.tabNodeContainer.getStyle("margin-bottom").toFloat();
        // h = h-tabSize.totalHeight-tabMarginTop-tabMarginBottom;
        // this.scriptTab.contentNodeContainer.setStyle("height", ""+h+"px");
    },
    changeCategoryType: function(e){
        if (!e.target.hasClass("listActionButton_select")){
            this.actionButtons.removeClass("listActionButton_select");
            e.target.addClass("listActionButton_select");
            if (this.categoryType === "module"){
                this.changeCategoryTypeToPath();
            }else{
                this.changeCategoryTypeToModule();
            }
        }
    },
    changeCategoryTypeToPath: function(){
        this.categoryType = "path";
        this.items.each(function(item){ item.relocation(); }.bind(this));
    },
    changeCategoryTypeToModule: function(){
        this.categoryType = "module";
        this.items.each(function(item){ item.relocation(); }.bind(this));
    },

    setEvent: function(){
        this.setSize();
        this.setSizeFun = this.setSize.bind(this);
        this.designer.addEvent("resize", this.setSizeFun);

        this.actionButtons.addEvents({
            "click": function(e){
                this.changeCategoryType(e);
            }.bind(this)
        });

        new Drag(this.separatorNode, {
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name==="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name==="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.listNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "onDrag": function(el, e){
                var x = (Browser.name==="firefox") ? e.event.clientX : e.event.x;
//				var y = e.event.y;
                var bodySize = this.content.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx =x.toFloat()- position.x.toFloat();

                var width = initialWidth+dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<40) width = 40;
                this.scriptAreaNode.setStyle("margin-left", width+3);
                this.listAreaNode.setStyle("width", width);

                if (this.jsEditor) this.jsEditor.resize();
            }.bind(this)
        });
    },
    createScriptEditor: function(callback){
        this.jsEditor = new MWF.widget.JavascriptEditor(this.scriptNode,{
            "option": {
                "value": "",
                "lineNumbers": true
            },
            "onPostLoad": function(){
                this.editor = this.jsEditor.editor;

                this.jsEditor.addEditorEvent("change", function() {
                    this.fireEvent("change");
                }.bind(this));
                this.jsEditor.addEditorEvent("blur", function() {
                    this.fireEvent("blur");
                }.bind(this));

                // this.editor.on("change", function() {
                //     this.fireEvent("change");
                // }.bind(this));
                this.jsEditor.resize();
                if (callback) callback();
                this.fireEvent("postLoad");
            }.bind(this),
            "onSave": function(){
                this.fireEvent("change");
                this.fireEvent("save");
            }.bind(this)
        });
        this.jsEditor.load();
    },

    removeModule: function(data){
        var count = this.items.length;
        for (var i=0; i<count; i++){
            var item = this.items[i];
            if (item.module.id===data.id){
                item.destroy();
                i--; count--;
            }
        }

        var keys = Object.keys(this.moduleCategorys);
        keys.each(function(k){
            var category = this.moduleCategorys[k];
            if (category) if (category.module.id===data.id) category.destroy();
        }.bind(this));

        keys = Object.keys(this.pathCategorys);
        keys.each(function(k){
            var category = this.pathCategorys[k];
            if (category) if (!category.childrenNode.getFirst()) category.destroy();
        }.bind(this));

        // Object.each(this.pathCategorys, function(category){
        //     if (!category.childrenNode.getFirst()) category.destroy();
        // }.bind(this));
    },
    checkCategorys: function(){
        var keys = Object.keys(this.moduleCategorys);
        keys.each(function(k){
            var category = this.moduleCategorys[k];
            if (category) if (!category.items.length) category.destroy();
        }.bind(this));

        keys = Object.keys(this.pathCategorys);
        keys.each(function(k){
            var category = this.pathCategorys[k];
            if (category) if (!category.childrenNode.getFirst()) category.destroy();
        }.bind(this));
    }
});

MWF.xApplication.portal.PageDesigner.Script.Category = new Class({
    initialize: function(script, name, level, module, type){
        this.script = script;
        this.name = name;
        this.module = module;
        this.level = level;
        this.type = type;
        this.items = [];
        this.load();
    },
    load: function(){
        var name = "";
        if (this.type==="path"){
            name = this.script.designer.lp.scriptTitle[this.name] || this.name;
        }else{
            var t;
            if (this.module.type==="Form" || this.module.type==="Page"){
                t =  this.script.designer.lp.pageform;
            }else{
                t = this.module.type.toLowerCase();
                var tool = this.script.designer.toolsData[t];
                t = (tool) ? tool.text : t;
            }
            name = this.name.replace(/\(.*\)/, "("+t+")");
        }

        this.script.listContentNode.appendHTML("<div class='itemCategory'><div class='itemCategoryTitle'>" +
            "<div class='itemCategoryTitleIcon'></div><div class='itemCategoryTitleName' title='"+this.name+"'>"+name+"</div>" +
            "</div><div class='itemCategoryChildren'></div></div>");
        this.node = this.script.listContentNode.getLast();
        //this.blankNode = this.node.getElement(".itemCategoryTitleBlank");
        this.titleNode = this.node.getElement(".itemCategoryTitle");
        this.iconNode = this.node.getElement(".itemCategoryTitleIcon");
        this.nameNode = this.node.getElement(".itemCategoryTitleName");
        this.childrenNode = this.node.getElement(".itemCategoryChildren");

        var marginLeft = this.level.toInt()*10;
        this.node.setStyle("margin-left", ""+marginLeft+"px");

        this.titleNode.addEvents({
            "mouseover": function(){this.addClass("itemCategoryTitle_over");},
            "mouseout": function(){this.removeClass("itemCategoryTitle_over");},
            "click": function(){ this.toggle(); }.bind(this)
        });
    },
    hide: function(){
        this.node.hide();
    },
    show: function(){
        this.node.show();
    },
    toggle: function(){
        ((this.isExpand) ? this.collapse : this.expand).apply(this);
    },
    expand: function(){
        this.childrenNode.show();
        this.iconNode.addClass("itemCategoryTitleIcon_expand");
        this.isExpand = true;
    },
    collapse: function(){
        this.childrenNode.hide();
        this.iconNode.removeClass("itemCategoryTitleIcon_expand");
        this.isExpand = false;
    },
    resetName: function(name, id){
        delete this.script.moduleCategorys[this.module.id];
        this.script.moduleCategorys[id] = this;
        this.name = name;
        this.nameNode.set({"text": this.name, "title": this.name});
        this.items.each(function(item){
            item.resetText(id);
        }.bind(this));
    },
    destroy: function(){
        delete this.script.moduleCategorys[this.module.id];
        delete this.script.pathCategorys[this.name];
        this.node.destroy();
        this.script.checkCategorys();
        MWF.release(this);
    }
});


MWF.xApplication.portal.PageDesigner.Script.Item = new Class({
    initialize: function(script, data, key, module, path, par){
        this.script = script;
        this.data = data;
        this.key = key;
        this.module = module;
        this.path = path;
        this.text = path;
        this.par = par;
        this.bind();
        if (this.data[this.key]) this.createNode();
    },
    reload: function(){
        // if (this.value){
        if (!this.node) this.createNode();
            //if (this.isShow)
        if (this.jsEditor){
            this.jsEditor.setValue(this.value);
            //this.editor.session.setValue(this.value);
            //this.jsEditor.node.show();
        }
        // }else{
        //     if (this.node) this.node.destroy();
        //     if (this.isShow){
        //         if (this.script.editor){
        //             this.script.editor.setValue("");
        //             this.script.jsEditor.node.hide();
        //         }
        //         this.isShow = false;
        //     }
        // }
    },
    unShow: function(){
        if (this.script.editor){
            this.value.code = this.script.editor.getValue()
        }
        this.isShow = false;
        this.script.currentItem = null;

    },
    getCategory: function(){
        var category = null;
        if (this.script.categoryType==="module"){
            category = this.script.moduleCategorys[(this.module.type==="Form" || this.module.type==="Page") ? this.module.type : this.module.id];
        }else{
            category = this.script.pathCategorys[this.path];
        }
        return category || this.script.createCategory(this.module, this.path);
    },
    getText: function(id){
        var moduleId = id || this.module.id;
        var text =  this.script.designer.lp.scriptTitle[this.path] || this.path;
        text = ((this.module.type==="Form" || this.module.type==="Page") ? this.module.type : moduleId) + ":" + text;
        if (this.par) text = text+"."+this.par;
        return text;
    },

    relocation: function(){
        if (this.node){
            if (this.category) this.category.hide();
            this.category = this.getCategory();
            this.category.show();
            this.text = this.getText();
            this.node.inject(this.category.childrenNode);
        }
    },
    createNode: function(){
        this.category = this.getCategory();
        this.category.show();
        this.text = this.getText();
        this.category.childrenNode.appendHTML("<div class='item'><div class='itemIcon'></div><div class='itemText' title='"+this.text+"'>"+this.text +"</div></div>");
        this.node = this.category.childrenNode.getLast();
        this.node.addEvents({
            "mouseover": function(){this.addClass("item_over");},
            "mouseout": function(){this.removeClass("item_over");},
            "click": function(){ this.selected(); }.bind(this)
        });
        if (this.script.categoryType==="module"){
            this.category.items.push(this);
        }else{
            var category = this.script.moduleCategorys[(this.module.type==="Form" || this.module.type==="Page") ? this.module.type : this.module.id];
            if (!category) category = this.script.createCategory(this.module, this.path, "module");
            category.items.push(this);
        }
    },
    resetText: function(id){
        this.text = this.getText(id);
        if (this.node) this.node.getLast().set({"text": this.text, "title": this.text});
        if (this.scriptPage){
            this.scriptPage.options.title = this.text;
            var title = this.scriptPage.textNode.get("text");
            var text = this.text;
            if (title.substr(0,1)==="*") text = "*"+text;
            this.scriptPage.textNode.set("text", text);
        }
    },
    unselected: function(){
        this.node.removeClass("item_select");
        this.node.getLast().removeClass("itemText_select");
        this.isShow = false;
        this.script.currentItem = null;
    },
    selected: function(){
        if (this.script.currentItem) this.script.currentItem.unselected();
        this.node.addClass("item_select");
        this.node.getLast().addClass("itemText_select");
        this.isShow = true;
        this.script.currentItem = this;
        this.showScript();

        if (this.module.type==="Form" || this.module.type==="Page"){
            (this.script.designer.form || this.script.designer.page).selected();
        }else{
            var list = (this.script.designer.form || this.script.designer.page).moduleList;
            var module = null;
            for (var i=0; i<list.length; i++){
                if (list[i].json.id===this.module.id){
                    module = list[i];
                    break;
                }
            }
            if (module) module.selected();
        }
    },
    save: function(){
        this.script.designer.saveForm();
    },
    change: function(){
        this.data[this.key] = this.jsEditor.getValue();
    },
    setSize: function(){
        var size = this.script.scriptTabNode.getComputedSize();
        var tabSize = this.script.scriptTab.tabNodeContainer.getComputedSize();
        var tabMarginTop = this.script.scriptTab.tabNodeContainer.getStyle("margin-top").toFloat();
        var tabMarginBottom = this.script.scriptTab.tabNodeContainer.getStyle("margin-bottom").toFloat();
        var h = size.height-tabSize.totalHeight-tabMarginTop-tabMarginBottom;
        this.scriptPageNode.setStyle("height", ""+h+"px");
        if (this.jsEditor) this.jsEditor.resize();
    },
    createScriptEditor: function(){
        this.scriptPageNode = new Element("div");
        this.setSizeFun = this.setSize.bind(this);
        this.setSizeFun();
        this.script.designer.addEvent("resize", this.setSizeFun);

        var text = this.getText();
        this.scriptPage = this.script.scriptTab.addTab(this.scriptPageNode, text, true);
        this.scriptPage.scriptItem = this;
        this.scriptContentNode = new Element("div.scriptContentNode").inject(this.scriptPageNode);

        this.jsEditor = new MWF.widget.JavascriptEditor(this.scriptContentNode,{
            "option": {
                "value": this.data[this.key],
                "lineNumbers": true
            },
            "onPostLoad": function(){
                this.editor = this.jsEditor.editor;
                this.editor.id = "1";

                this.jsEditor.addEditorEvent("change", function() {
                    var text = this.scriptPage.textNode.get("text");
                    if (text.substr(0,1)!=="*") this.scriptPage.textNode.set("text","*"+ text);
                    this.change();
                }.bind(this));
                this.jsEditor.addEditorEvent("blur", function() {
                    this.change();
                }.bind(this));

                // this.editor.on("change", function() {
                //     var text = this.scriptPage.textNode.get("text");
                //     if (text.substr(0,1)!=="*") this.scriptPage.textNode.set("text","*"+ text);
                //     this.change();
                // }.bind(this));
                this.jsEditor.resize();
            }.bind(this),
            "onSave": function(){
                this.save();
                var text = this.scriptPage.textNode.get("text");
                if (text.substr(0,1)==="*"){
                    this.scriptPage.textNode.set("text", text.substr(1, text.length));
                }
            }.bind(this)
        });
        this.jsEditor.load();

        this.scriptPage.addEvent("show", function(){
            this.scriptPage.scriptItem.selected();
            if (this.jsEditor) this.jsEditor.focus();
        }.bind(this));
        this.scriptPage.addEvent("queryClose", function(){
            var idx = this.tab.pages.indexOf(this);
            if (idx!==-1){
                if (idx===0){ idx = 1; }else{ idx--; }
                if (this.tab.pages[idx]){ this.tab.pages[idx].scriptItem.selected();} else {this.scriptItem.unselected();}
            }else{
                this.scriptItem.unselected();
            }
            this.scriptItem.scriptPage = null;
            this.scriptItem.jsEditor = null;
        });
        //this.scriptPage.tabNode.addEvent("dblclick")
    },
    showScript: function(){
        if (!this.scriptPage) this.createScriptEditor();
        this.scriptPage.showTabIm();
        // if (!this.script.jsEditor){
        //     this.script.createScriptEditor(function(){
        //         this.script.editor.setValue(this.value);
        //         this.script.jsEditor.node.show();
        //     }.bind(this));
        // }else{
        //     if (this.script.currentItem) this.script.currentItem.unShow();
        //     this.script.editor.setValue(this.value);
        //     this.script.jsEditor.node.show();
        // }
    },
    destroy: function(){
        this.script.items.erase(this);
        var category = this.script.moduleCategorys[(this.module.type==="Form" || this.module.type==="Page") ? this.module.type : this.module.id];
        if (category) category.items.erase(this);
        if (this.scriptPage) this.scriptPage.closeTab();
        if (this.node) this.node.destroy();
        this.script.checkCategorys();
        MWF.release(this);
    },

    //绑定aec需要修改ace源码。
    bind: function(){
        this.value = this.data[this.key];
        if (this.data.editors){
            this.data.editors.push(this);
        }else{
            Object.defineProperty(this.data, "editors", {
                configurable : false,
                enumerable : false,
                writable: true,
                "value": []
            });
            this.data.editors.push(this);
            Object.defineProperty(this.data, this.key, {
                configurable : true,
                enumerable : true,
                "get": function(){return this.value;}.bind(this),
                "set": function(v){
                    this.data.editors.each(function(editor){
                        if (editor.editor){
                            if (v!==editor.editor.getValue()) editor.editor.setValue(v,1);
                        }else{
                            editor.reload();
                        }
                    });
                    this.value = v;
                }.bind(this)
            });
        }

        // Object.defineProperty(this.data, this.key, {
        //     configurable : true,
        //     enumerable : true,
        //     "get": function(){return this.value;}.bind(this),
        //     "set": function(v){
        //         this.value = v;
        //         this.reload();
        //     }.bind(this)
        // });
    }
});

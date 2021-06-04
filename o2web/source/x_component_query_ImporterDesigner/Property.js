MWF.xDesktop.requireApp("query.ViewDesigner", "Property", null, false);
MWF.xApplication.query.ImporterDesigner.Property = new Class({
    Extends: MWF.FVProperty,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "path": "../x_component_query_FormDesigner/property/property.html"
    },

    initialize: function (module, propertyNode, designer, options) {
        this.setOptions(options);
        this.module = module;
        this.importer = module.importer || module.view;
        this.view = module.importer || module.view;
        this.data = module.json;
        this.data.vid = this.view.json.id;
        this.data.vtype = this.view.json.type;
        this.data.pid = this.view.json.id + this.data.id;
        this.htmlPath = this.options.path;

        this.maplists = {};

        this.designer = designer;

        this.propertyNode = propertyNode;
    },
    show: function () {
        if (!this.propertyContent) {
            this.getHtmlString(function () {
                if (this.htmlString) {
                    this.htmlString = o2.bindJson(this.htmlString, {"lp": MWF.xApplication.query.ImporterDesigner.LP.propertyTemplate});
                    this.JsonTemplate = new MWF.widget.JsonTemplate(this.data, this.htmlString);
                    this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.propertyNode);
                    this.propertyContent.set("html", this.JsonTemplate.load());

                    this.setEditNodeEvent();
                    this.setEditNodeStyles(this.propertyContent);
                    this.loadPropertyTab();
                    this.loadPersonInput();
                    this.loadPersonSelectInput();
                    this.loadViewFilter();
                    this.loadScriptArea();

                    this.loadColumnExportEditor();

                    this.loadJSONArea();

                    this.loadEventsEditor();

                    this.loadMaplist();

                    this.loadSelectField();
                    this.loadFormSelect();
                }
            }.bind(this));
        } else {
            this.propertyContent.setStyle("display", "block");
        }


    },
    loadSelectField: function(){
        var selectFieldNodes = this.propertyContent.getElements(".MWFSelectField");
        selectFieldNodes.each(function(node){
            node.empty();
            var select = new Element("select", {
                "style": "width:200px;"
            }).inject(node);

            select.addEvent("change", function(e){
                this.setSelectValue(e.target.getParent("div").get("name"), select);
            }.bind(this));
            this.setFieldSelectOptions(node, select);
        }.bind(this))
    },
    setFieldSelectOptions: function(node, select){
        debugger;
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"text": "none"}).inject(select);
        var d = this.data;
        Array.each(name.split("."), function (n) {
            if (d) d = d[n];
        });

        (this.view.json.data.columnList || []).each(function(column){

                var option = new Element("option", {
                    "text": column.displayName + " - "+column.path,
                    "value": column.path,
                    "selected": (d===column.path)
                }).inject(select);

        }.bind(this));
        (this.view.json.data.calculateFieldList || []).each(function(column){

                var option = new Element("option", {
                    "text": column.displayName + " - "+column.path,
                    "value": column.path,
                    "selected": (d===column.path)
                }).inject(select);

        }.bind(this));
    },

    loadFormSelect: function(){
        var formNodes = this.propertyContent.getElements(".MWFFormSelect");
        if (formNodes.length){
            this.getFormList(function(){
                formNodes.each(function(node){
                    node.empty();
                    var select = new Element("select", {"style": "width:200px;"}).inject(node);
                    select.addEvent("change", function(e){
                        this.setValue(e.target.getParent("div").get("name"), e.target.options[e.target.selectedIndex].value);
                    }.bind(this));
                    this.setFormSelectOptions(node, select);

                    var refreshNode = new Element("div", {"styles": this.view.css.propertyRefreshFormNode}).inject(node);
                    refreshNode.addEvent("click", function(e){
                        this.getFormList(function(){
                            this.setFormSelectOptions(node, select);
                        }.bind(this), true);
                    }.bind(this));
                }.bind(this));
            }.bind(this), true);
        }
    },
    setFormSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();

        var d = this.data;
        Array.each(name.split("."), function (n) {
            if (d) d = d[n];
        });

        if(this.forms){
            var option = new Element("option", {"text": "none"}).inject(select);
            this.forms.each(function(form){
                var option = new Element("option", {
                    "text": form.name,
                    "value": form.id,
                    "selected": (d===form.id)
                }).inject(select);
            }.bind(this));
        }else{
            new Element("option", {
                "text": this.view.designer.lp.propertyTemplate.selectProcess1,
                "value": ""
            }).inject(select);
        }
    },
    getFormList: function(callback, refresh){
        if (this.view.json.data.process && this.view.json.data.process.application && (!this.forms || refresh)){
            var action = o2.Actions.load("x_processplatform_assemble_designer");
            action.FormAction.listWithApplication(this.view.json.data.process.application, function(json){
                this.forms = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    loadPersonSelectInput: function () {
        var personNodes = this.propertyContent.getElements(".MWFSelectPerson");
        var identityNodes = this.propertyContent.getElements(".MWFSelectIdentity");
        var personUnitNodes = this.propertyContent.getElements(".MWFSelectUnit");

        // var cmsapplicationNodes = this.propertyContent.getElements(".MWFSelectCMSApplication");
        var cmscategoryNodes = this.propertyContent.getElements(".MWFSelecCMStCategory");
        var querytableNodes = this.propertyContent.getElements(".MWFSelecQueryTable");
        // var applicationNodes = this.propertyContent.getElements(".MWFSelectApplication");
        var processNodes = this.propertyContent.getElements(".MWFSelectProcess");


        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function () {
            // applicationNodes.each(function (node) {
            //     new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
            //         "type": "application",
            //         "names": (this.data.data.where) ? this.data.data.where.applicationList : [],
            //         "onChange": function (ids) {
            //             this.savePersonSelectItem(node, ids);
            //         }.bind(this)
            //     });
            // }.bind(this));
            personUnitNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "unit",
                    "names": (this.data.data.where) ? this.data.data.where.creatorUnitList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            personNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "person",
                    "names": (this.data.data.where) ? this.data.data.where.creatorPersonList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            identityNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "identity",
                    "names": (this.data.data.where) ? this.data.data.where.creatorIdentityList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            // cmsapplicationNodes.each(function (node) {
            //     new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
            //         "type": "CMSApplication",
            //         "names": (this.data.data.where) ? this.data.data.where.appInfoList : [],
            //         "onChange": function (ids) {
            //             this.savePersonSelectItem(node, ids);
            //         }.bind(this)
            //     });
            // }.bind(this));

            cmscategoryNodes.each(function (node) {
                var count = node.get("count") ? node.get("count").toInt() : 0;
                var name = (this.data.data) ? this.data.data.category : {};
                var names = o2.typeOf(name) === "object" ? [name] : name;
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "CMSCategory",
                    "names": names,
                    "count" : count,
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids, count);
                    }.bind(this)
                });
            }.bind(this));

            querytableNodes.each(function (node) {
                var count = node.get("count") ? node.get("count").toInt() : 0;
                var name = (this.data.data) ? this.data.data.dynamicTable : {};
                var names = o2.typeOf(name) === "object" ? [name] : name;
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "QueryTable",
                    "names": names,
                    "count" : count,
                    "onChange": function (ids) {
                        debugger;
                        this.savePersonSelectItem(node, ids, count);
                    }.bind(this)
                });
            }.bind(this));

            processNodes.each(function (node) {
                var count = node.get("count") ? node.get("count").toInt() : 0;
                var name = (this.data.data) ? this.data.data.process : {};
                var names = o2.typeOf(name) === "object" ? [name] : name;
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "process",
                    "names": names,
                    "count" : count,
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids, count);
                    }.bind(this)
                });
            }.bind(this));

        }.bind(this));
    },
    savePersonSelectItem: function (node, ids, count) {
        debugger;
        //this.initWhereEntryData();
        var values = [];
        ids.each(function (id) {
            var obj = {"name": (id.data.distinguishedName || id.data.name), "id": id.data.id};
            if( id.data.application )obj.application =  id.data.application;
            if( id.data.applicationName )obj.applicationName =  id.data.applicationName;
            values.push(obj);
            //values.push((id.data.distinguishedName || id.data.id || id.data.name));
        }.bind(this));
        var name = node.get("name");

        var key = name.split(".");

        var oldValue = this.data;
        for (var idx = 0; idx < key.length; idx++) {
            if (!oldValue[key[idx]]) {
                oldValue = null;
                break;
            } else {
                oldValue = oldValue[key[idx]];
            }
        }

        var o = this.data;
        var len = key.length - 1;
        key.each(function (n, i) {
            if (!o[n]) o[n] = {};
            if (i < len) o = o[n];
        }.bind(this));
        o[key[len]] = count === 1 ? (values[0] || {}) : values;

        this.changeData(name, node, oldValue);

        //this.data.data.restrictWhereEntry[node.get("name")] = values;
    }
});
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
                }
            }.bind(this));
        } else {
            this.propertyContent.setStyle("display", "block");
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
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "CMSCategory",
                    "names": (this.data.data) ? this.data.data.category : {},
                    "count" : count,
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids, count);
                    }.bind(this)
                });
            }.bind(this));

            querytableNodes.each(function (node) {
                var count = node.get("count") ? node.get("count").toInt() : 0;
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "QueryTable",
                    "names": (this.data.data) ? this.data.data.querytable : {},
                    "count" : count,
                    "onChange": function (ids) {
                        debugger;
                        this.savePersonSelectItem(node, ids, count);
                    }.bind(this)
                });
            }.bind(this));

            processNodes.each(function (node) {
                var count = node.get("count") ? node.get("count").toInt() : 0;
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "process",
                    "names": (this.data.data) ? this.data.data.process: {},
                    "count" : count,
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids, count);
                    }.bind(this)
                });
            }.bind(this));

        }.bind(this));
    },
    savePersonSelectItem: function (node, ids, count) {
        //this.initWhereEntryData();
        var values = [];
        ids.each(function (id) {
            values.push({"name": (id.data.distinguishedName || id.data.name), "id": id.data.id});
            //values.push((id.data.distinguishedName || id.data.id || id.data.name));
        }.bind(this));
        var name = node.get("name");

        key = name.split(".");
        var o = this.data;
        var len = key.length - 1;
        key.each(function (n, i) {
            if (!o[n]) o[n] = {};
            if (i < len) o = o[n];
        }.bind(this));
        o[key[len]] = count === 1 ? (values[0] || {}) : values;

        //this.data.data.restrictWhereEntry[node.get("name")] = values;
    }
});
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
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "CMSCategory",
                    "names": (this.data.data) ? this.data.data.category : {},
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            querytableNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "QueryTable",
                    "names": (this.data.data) ? this.data.data.querytable : {},
                    "onChange": function (ids) {
                        debugger;
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            processNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "process",
                    "names": (this.data.data) ? this.data.data.process: {},
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

        }.bind(this));
    }
});
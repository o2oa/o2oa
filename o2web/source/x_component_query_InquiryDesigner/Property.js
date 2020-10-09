MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.widget.JsonTemplate", null, false);

MWF.xDesktop.requireApp("query.ViewDesigner", "Property", null, false);
MWF.xApplication.query.InquiryDesigner.Property = MWF.FIProperty = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.Property,
    Implements: [Options, Events],
    options: {
        "style": "default"
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
                    this.loadViewStylesArea();
                    this.loadPagingStylesArea();
                    this.loadActionStylesArea();
                    this.loadActionArea();
                    this.loadStylesList();
                    this.loadMaplist();
                }
            }.bind(this));
        } else {
            this.propertyContent.setStyle("display", "block");
        }
    }
});
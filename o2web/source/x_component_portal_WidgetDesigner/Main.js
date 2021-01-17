MWF.APPWD = MWF.xApplication.portal.WidgetDesigner;
MWF.APPWD.options = {
    "multitask": true,
    "executable": false
};

MWF.xDesktop.requireApp("portal.PageDesigner", "lp." + MWF.language, null, false);
MWF.xDesktop.requireApp("portal.PageDesigner", "", null, false);
MWF.xApplication.portal.WidgetDesigner.Main = new Class({
    Extends: MWF.xApplication.portal.PageDesigner.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "template": "page.json",
        "templateId": "",
        "name": "portal.WidgetDesigner",
        "icon": "icon.png",
        "title": MWF.APPWD.LP.title,
        "appTitle": MWF.APPWD.LP.title,
        "id": "",
        "actions": null,
        "category": null,
        "processData": null
    },
    //loadPage: function(){
    //        this.getPageData(function(){
    //                this.pcPage = new MWF.PCWidget(this, this.designNode);
    //                this.pcPage.load(this.pageData);
    //
    //                this.page = this.pcPage;
    //        }.bind(this));
    //},
    getPageTemplate: function (templateId, callback) {
        this.actions.getWidgetTemplate(templateId, function (page) {
            if (callback) callback(page);
        }.bind(this));
    },
    getPage: function (id, callback) {
        this.actions.getWidget(id, function (page) {
            if (callback) callback(page);
        }.bind(this));
    },
    loadPage: function () {
        this.getPageData(function () {
            this.pcPage = new MWF.PCPage(this, this.designNode, {
                "propertyPath": "../x_component_portal_WidgetDesigner/Module/Page/page.html",
                "onPostLoad": function(){
                    this.fireEvent("postWidgetLoad");
                }.bind(this)

            });
            this.pcPage.load(this.pageData);

            this.page = this.pcPage;
        }.bind(this));
    },
    setCategorySelect: function (categorySelect) {
        if (categorySelect) {
            new Element("option", {"value": "$newCategory", "text": this.lp.newCategory}).inject(categorySelect);
            this.actions.listWidgetTemplateCategory(function (json) {
                json.data.each(function (category) {
                    new Element("option", {"value": category.name, "text": category.name}).inject(categorySelect);
                }.bind(this));
            }.bind(this));
        }
    },
    _savePage: function (pcData, mobileData, fieldList, success, failure) {
        this.actions.saveWidget(pcData, mobileData, fieldList, function (responseJSON) {
            success(responseJSON)
        }.bind(this), function (xhr, text, error) {
            failure(xhr, text, error)
        }.bind(this));
    },
    addPageTemplate: function (pcData, mobileData, data, success, failure) {
        this.actions.addWidgetTemplate(pcData, mobileData, data, function (json) {
            if (success) success(json);
        }.bind(this), function (xhr, text, error) {
            if (failure) failure(xhr, text, error);
        }.bind(this))
    },
    loadNewPageData: function (callback) {
        var url = "../x_component_portal_PageDesigner/Module/Page/template/" + this.options.template;
        MWF.getJSON(url, {
            "onSuccess": function (obj) {
                this.pageData = obj.pcData;
                this.pageData.id = "";
                this.pageData.isNewPage = true;
                this.pageData.json.name = MWF.APPWD.LP.newPage;

                this.pageMobileData = obj.mobileData;
                this.pageMobileData.id = "";
                this.pageMobileData.isNewPage = true;
                if (callback) callback();
            }.bind(this),
            "onerror": function (text) {
                this.notice(text, "error");
            }.bind(this),
            "onRequestFailure": function (xhr) {
                this.notice(xhr.responseText, "error");
            }.bind(this)
        });
    },
    previewPage: function () {
        this.savePage();
        //this.page.preview();
        var url = "../x_desktop/portal.html?id=" + this.application.id + "&widget=" + this.page.json.id;
        window.open(o2.filterUrl(url));
    }
});

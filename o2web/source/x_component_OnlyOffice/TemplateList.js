MWF.xAction.RestActions.Action["x_onlyofficefile_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action
});
MWF.xDesktop.requireApp("OnlyOffice", "DocumentList", null, false);
MWF.xApplication.OnlyOffice.TemplateList = new Class({
    Extends: MWF.xApplication.OnlyOffice.DocumentList
});
MWF.xApplication.OnlyOffice.TemplateList.View = new Class({
    Extends: MWF.xApplication.OnlyOffice.DocumentList.View,
    _getCurrentPageData: function (callback, count, pageNum) {
        this.clearBody();
        if (!count) count = 15;
        if (!pageNum) {
            if (this.pageNum) {
                pageNum = this.pageNum = this.pageNum + 1;
            } else {
                pageNum = this.pageNum = 1;
            }
        } else {
            this.pageNum = pageNum;
        }

        var filter = this.filterData || {};

        filter.category = "template";

        this.app.action.OnlyofficeAction.listPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            this._fixData(json.data);
            if (callback) callback(json);
        }.bind(this))

    },
});
MWF.xApplication.OnlyOffice.TemplateList.Document = new Class({
    Extends: MWF.xApplication.OnlyOffice.DocumentList.Document,
});

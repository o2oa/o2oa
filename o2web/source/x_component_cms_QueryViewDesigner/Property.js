MWF.xDesktop.requireApp("process.ViewDesigner", "Property", null, false);
MWF.xApplication.cms.QueryViewDesigner.Property = MWF.CMSQVDProperty = new Class({
    Extends: MWF.xApplication.process.ViewDesigner.Property,
    options: {
        "style": "default",
        "path": "/x_component_cms_FormDesigner/property/property.html"
    },
    loadColumnExportEditor: function(){
        var _self = this;
        var nodes = this.propertyContent.getElements(".MWFColumnExport");
        nodes.each(function(node){
            //if (!this.data.export) this.data.export = {};
            //var sort = this.data.export.sort || "";
            //var sortOrder = this.data.export.sortOrder || "1";
            var select = node.getElement("select");
            var sortList = this.view.data.data.orderEntryList;
            sortList.each(function(order){
                if (order.column==this.data.column){
                    if (order.orderType=="asc") select.options[1].set("selected", true);
                    if (order.orderType=="desc") select.options[1].set("selected", false);
                }
            }.bind(this));
            select.addEvent("change", function(e){
                var v = select.options[select.selectedIndex].value;
                if (v!="none"){
                    var flag = false;
                    sortList.each(function(order){
                        if (order.column==this.data.column){
                            flag = true;
                            order.orderType=select.options[select.selectedIndex].value;
                        }
                    }.bind(this));
                    if (!flag) sortList.push({"column": this.data.column, "orderType": select.options[select.selectedIndex].value});
                }else{
                    var deleteItem = null;
                    sortList.each(function(order){
                        if (order.column==this.data.column){
                            deleteItem = order;
                        }
                    }.bind(this));
                    if (deleteItem) sortList.erase(deleteItem);
                }

            }.bind(this));

            var radios = node.getElements("input");
            var group = this.view.data.data.groupEntry	;
            if (group.column==this.data.column) radios[0].set("checked", true);
            radios.addEvent("click", function(e){
                if (this.checked){
                    if (this.value=="true") {
                        _self.view.data.data.group.column = _self.data.column;
                        _self.view.items.each(function(col){
                            if (col.property){
                                var groupRadios = col.property.propertyContent.getElement(".MWFColumnExportGroup").getElements("input");
                                groupRadios.each(function(r){
                                    if (r.value=="true") r.set("checked", false);
                                    if (r.value=="false") r.set("checked", true);
                                });
                            }
                        });
                        this.set("checked", true);
                    }else{
                        if (group.column ==_self.data.column) _self.view.data.data.group = {};
                    }
                }
            });
        }.bind(this));

    },
    loadPersonSelectInput: function () {
        var applicationNodes = this.propertyContent.getElements(".MWFSelectApplication");
        var categoryNodes = this.propertyContent.getElements(".MWFSelectCategory");
        var unitNodes = this.propertyContent.getElements(".MWFSelectUnit");
        var personNodes = this.propertyContent.getElements(".MWFSelectPerson");
        var identityNodes = this.propertyContent.getElements(".MWFSelectIdentity");

        MWF.xDesktop.requireApp("cms.QueryViewDesigner", "widget.PersonSelector", function () {
            applicationNodes.each(function (node) {
                new MWF.xApplication.cms.QueryViewDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "application",
                    "names": (this.data.data.restrictWhereEntry) ? this.data.data.restrictWhereEntry.appInfoList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));
            categoryNodes.each(function (node) {
                new MWF.xApplication.cms.QueryViewDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "category",
                    "names": (this.data.data.restrictWhereEntry) ? this.data.data.restrictWhereEntry.categoryList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            unitNodes.each(function (node) {
                new MWF.xApplication.cms.QueryViewDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "unit",
                    "names": (this.data.data.restrictWhereEntry) ? this.data.data.restrictWhereEntry.unitList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            personNodes.each(function (node) {
                new MWF.xApplication.cms.QueryViewDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "person",
                    "names": (this.data.data.restrictWhereEntry) ? this.data.data.restrictWhereEntry.personList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            identityNodes.each(function (node) {
                new MWF.xApplication.cms.QueryViewDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "identity",
                    "names": (this.data.data.restrictWhereEntry) ? this.data.data.restrictWhereEntry.identityList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));
        }.bind(this));
    }
});
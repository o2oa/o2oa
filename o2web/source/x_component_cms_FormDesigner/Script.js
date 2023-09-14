MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.FormDesigner = MWF.xApplication.cms.FormDesigner || {};

MWF.xDesktop.requireApp("portal.PageDesigner", "Script", null, false);

MWF.xApplication.cms.FormDesigner.Script = new Class({
    Extends : MWF.xApplication.portal.PageDesigner.Script,
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
            case "TinyMCEEditor":
                this.loadTinyMCEEditorScript(v); break;
            case "ImageClipper":
                this.loadImageClipperScript(v); break;
            case "WritingBoard":
                this.loadWritingBoardScript(v); break;
            case "Log":
                this.loadLogScript(v); break;
            case "Monitor":
                this.loadMonitorScript(v); break;
            case "Number":
                this.loadNumberScript(v); break;
            case "Currency":
                this.loadCurrencyScript(v); break;
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
            case "Authorfield":
                this.loadAuthorfieldScript(v); break;
            case "Readerfield":
                this.loadReaderfieldScript(v); break;
            case "Org":
                this.loadOrgScript(v); break;
            case "Author":
                this.loadAuthorScript(v); break;
            case "Reader":
                this.loadReaderScript(v); break;
            case "Statement":
                this.loadStatementScript(v); break;
            case "StatementSelector":
                this.loadStatementSelectorScript(v); break;
            case "Importer":
                this.loadImporterScript(v); break;
            case "Relatedlink":
                this.loadRelatedlinkScript(v); break;
            case "AssociatedDocument":
                this.loadAssociatedDocumentScript(v); break;
            case "WpsOffice":
                this.addScriptItem(v.readScript, "code", v, "readScript");
                this.loadEventsScript(v);
                break;
            case "YozoOffice":
                this.addScriptItem(v.readScript, "code", v, "readScript");
                this.loadEventsScript(v);
                break;

            case "Elautocomplete":
                this.loadVueElementScript(v, true);
                this.addScriptItem(v.itemScript, "code", v, "itemScript");
                break;
            case "Elbutton":
                this.loadVueElementScript(v); break;
            case "Elcheckbox":
            case "Elradio":
                this.loadVueElementScript(v, true);
                this.addScriptItem(v.itemScript, "code", v, "itemScript");
                break;
            case "Elcommon":
                this.addScriptItem(v.vueTemplate, "code", v, "vueTemplate");
                this.addScriptItem(v.vueApp, "code", v, "vueApp");
                this.addScriptItem(v.vueCss, "code", v, "vueCss");
                this.loadEventsScript(v);
                break;
            case "Elcontainer":
            case "Elcontainer$Main":
            case "Elcontainer$Aside":
            case "Elcontainer$Footer":
            case "Elcontainer$Header":
            case "Elicon":
                this.loadEventsScript(v); break;
            case "Elinput":
            case "Elnumber":
                this.loadVueElementScript(v, true); break;
            case "Elselect":
                this.loadVueElementScript(v);
                this.addScriptItem(v.itemScript, "code", v, "itemScript");
                this.addScriptItem(v.itemGroupScript, "code", v, "itemGroupScript");
                this.addScriptItem(v.filterMethod, "code", v, "filterMethod");
                this.addScriptItem(v.remoteMethod, "code", v, "remoteMethod");
                break;

            case "Elslider":
                this.loadVueElementScript(v, true);
                this.addScriptItem(v.marksScript, "code", v, "marksScript");
                this.addScriptItem(v.formatTooltip, "code", v, "formatTooltip");
                break;
            case "Elswitch":
                this.loadVueElementScript(v, true); break;
            case "Eltime":
                this.loadVueElementScript(v, true);
                this.addScriptItem(v.selectableRange, "code", v, "selectableRange");
                break;
            case "Eldate":
                this.loadVueElementScript(v, true);
                this.addScriptItem(v.disabledDate, "code", v, "disabledDate");
                break;
            case "Eldatetime":
                this.loadVueElementScript(v, true);
                this.addScriptItem(v.disabledDate, "code", v, "disabledDate");
                break;
            case "Elrate":
                this.loadVueElementScript(v, true);
                break;
            case "Elcolorpicker":
                this.loadVueElementScript(v, true);
                break;
            case "Eltree":
                this.loadVueElementScript(v, false);
                this.addScriptItem(v.currentNodeKey, "code", v, "currentNodeKey");
                this.addScriptItem(v.defaultExpandedKeys, "code", v, "defaultExpandedKeys");
                this.addScriptItem(v.defaultCheckedKeys, "code", v, "defaultCheckedKeys");
                this.addScriptItem(v.allowDrag, "code", v, "allowDrag");
                this.addScriptItem(v.allowDrop, "code", v, "allowDrop");
                break;
            case "Eldropdown":
                this.loadVueElementScript(v, false);
                break;
            case "Elcarousel":
                this.loadVueElementScript(v, false);
                this.addScriptItem(v.dataScript, "code", v, "dataScript");
                this.addScriptItem(v.filterScript, "code", v, "filterScript");
                this.addScriptItem(v.requestBody, "code", v, "requestBody");
                break;
        }
        this.bindDataId(v);
    },
    loadAuthorfieldScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.rangeKey, "code", data, "rangeKey");
        this.addScriptItem(data.exclude, "code", data, "exclude");
        this.addScriptItem(data.rangeKey, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadReaderfieldScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.rangeKey, "code", data, "rangeKey");
        this.addScriptItem(data.exclude, "code", data, "exclude");
        this.addScriptItem(data.rangeKey, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadAuthorScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.identityRangeKey, "code", data, "identityRangeKey");
        this.addScriptItem(data.unitRangeKey, "code", data, "unitRangeKey");
        this.addScriptItem(data.rangeDutyKey, "code", data, "rangeDutyKey");
        this.addScriptItem(data.exclude, "code", data, "exclude");
        this.addScriptItem(data.rangeKey, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    },
    loadReaderScript: function(data){
        this.addScriptItem(data.defaultValue, "code", data, "defaultValue");
        this.addScriptItem(data.validation, "code", data, "validation");
        this.addScriptItem(data.identityRangeKey, "code", data, "identityRangeKey");
        this.addScriptItem(data.unitRangeKey, "code", data, "unitRangeKey");
        this.addScriptItem(data.rangeDutyKey, "code", data, "rangeDutyKey");
        this.addScriptItem(data.exclude, "code", data, "exclude");
        this.addScriptItem(data.rangeKey, "code", data, "sectionByScript");
        this.loadEventsScript(data);
    }
});

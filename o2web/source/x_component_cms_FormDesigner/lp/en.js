MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.FormDesigner.LP = Object.merge( {}, MWF.xApplication.process.FormDesigner.LP, {
    "selectApplication" : "Select App",
    "formType": {
        "empty": "Empty form",
        "publishEdit": "Publish Edit Form",
        "publishRead": "Publish Reading Form",
        "dataInput": "Data Input Form"
    },
    "validation" : {
        "publish" : "When publishing"
    },
    "modules": {
        "reader": "Reader",
        "author": "Author",
        "log": "Readed Log",
        "comment": "Comment"
    },
    "formStyle":{
        "noneStyle": "None",
        "defaultStyle": "Default",
        "redSimple": "Red Simple",
        "blueSimple": "Blue Simple",
        "defaultMobileStyle": "Mobile",
        "banner": "Banner",
        "title": "Title",
        "sectionTitle": "Section Title",
        "section": "section"
    },
    "propertyTemplate": {
        "setPopular": "set Popular",

        "commentPerPage": "Number of comments per page",
        "tiao": "",
        "allowModifyComment": "Allow modification after publication",
        "allowComment": "Allow Comment",
        "editor": "Editor",
        "editorTitle": "CKEditor Config script",
        "editorConfigNote": "Returns the Config object of CKEditor for editor initialization",
        "editorConfigLinkNote": "For more help, please see",

        "table": "Table",
        "text": "Text",
        "format": "Format",

        "validationSave": "Save validation",
        "validationPublish": "Publish Verification"
    },
    "actionBar": {
        "close":"Close",
        "closeTitle": "Close Document",
        "edit": "Edit",
        "editTitle": "Edit Document",
        "save": "Save",
        "saveTitle": "Save Document",
        "publish": "Publish",
        "publishTitle": "Publish Document",
        "saveDraft": "Save Draft",
        "saveDraftTitle": "Save Draft",
        "popular": "Set Popular",
        "popularTitle": "Set as Popular Document",
        "delete": "Delete",
        "deleteTitle": "Delete Document",
        "print": "Print",
        "printTitle": "Print Document"
    }
});
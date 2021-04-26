MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.FormDesigner.LP = Object.merge( MWF.xApplication.process.FormDesigner.LP, {
    "validation" : {
        "publish" : "When publishing"
    },
    "propertyTemplate": {

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
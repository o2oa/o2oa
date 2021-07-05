MWF.xApplication.process.Xform = MWF.xApplication.process.Xform || {};
MWF.xApplication.process.Xform.Package = true;
MWF.require("MWF.xScript.Macro", null, false);
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);

MWF.xApplication.process.Xform.require = function(callback){
    var modules = [
       // ["process.Xform", "Form"],
        ["process.Xform", "Label"],
        ["process.Xform", "Textfield"],
        ["process.Xform", "Number"],
        ["process.Xform", "Personfield"],
        ["process.Xform", "Orgfield"],
        ["process.Xform", "Org"],
        ["process.Xform", "Calendar"],
        ["process.Xform", "Textarea"],
        ["process.Xform", "Opinion"],
        ["process.Xform", "Select"],
        ["process.Xform", "Radio"],
        ["process.Xform", "Checkbox"],
        ["process.Xform", "Button"],
        ["process.Xform", "Combox"],
        ["process.Xform", "Address"],
        ["process.Xform", "Table"],
        ["process.Xform", "Datagrid"],
        ["process.Xform", "Datatable"],
        ["process.Xform", "Datatemplate"],
        ["process.Xform", "Tab"],
        ["process.Xform", "Tree"],
        ["process.Xform", "Iframe"],
        ["process.Xform", "Htmleditor"],
        ["process.Xform", "Office"],
        ["process.Xform", "IWebOffice"],
        ["process.Xform", "Attachment"],
        ["process.Xform", "Actionbar"],
        ["process.Xform", "Sidebar"],
        ["process.Xform", "Log"],
        ["process.Xform", "Monitor"],
        ["process.Xform", "View"],
        ["process.Xform", "ViewSelector"],
        ["process.Xform", "Stat"],
        ["process.Xform", "ImageClipper"],
        ["process.Xform", "Subform"],
        ["process.Xform", "Widget"],
        ["process.Xform", "Source"],
        ["process.Xform", "SourceText"],
        ["process.Xform", "SubSource"],
        ["process.Xform", "Div"],
        ["process.Xform", "Common"],
        ["process.Xform", "Image"],
        ["process.Xform", "Html"],
        ["process.Xform", "Statement"],
        ["process.Xform", "StatementSelector"],
        ["process.Xform", "Importer"],
        ["process.Xform", "ReadLog"]
    ];
    MWF.xDesktop.requireApp(modules, null, function(){
        if (callback) callback();
    });
};




MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "DatatablePC", null, false);
MWF.xDesktop.requireApp("process.Xform", "DatatableMobile", null, false);
MWF.xDesktop.requireApp("process.Xform", "DatatableV10", null, false);
//if (COMMON.Browser.Platform.isMobile){

// if ((layout.mobile || COMMON.Browser.Platform.isMobile) && (o2.version["dev"] && o2.version["dev"]>=10)){
//     MWF.xApplication.process.Xform.Datatable = MWF.APPDatatable = MWF.xApplication.process.Xform.DatatableV10;
//     MWF.xApplication.process.Xform.Datatable$Title = MWF.APPDatatable$Title = MWF.xApplication.process.Xform.DatatablePC$Title;
//     MWF.xApplication.process.Xform.Datatable$Data = MWF.APPDatatable$Data = MWF.xApplication.process.Xform.DatatablePC$Data;
// }else
//为了适配历史移动端表单。DatatableV10的判断放在表单的 checkDatatableClass 里了。
if ((layout.mobile || COMMON.Browser.Platform.isMobile)){
    MWF.xApplication.process.Xform.Datatable = MWF.APPDatatable = MWF.xApplication.process.Xform.DatatableMobile;
    MWF.xApplication.process.Xform.Datatable$Title = MWF.APPDatatable$Title = MWF.xApplication.process.Xform.DatatableMobile$Title;
    MWF.xApplication.process.Xform.Datatable$Data = MWF.APPDatatable$Data = MWF.xApplication.process.Xform.DatatableMobile$Data;
}else{
    MWF.xApplication.process.Xform.Datatable = MWF.APPDatatable = MWF.xApplication.process.Xform.DatatablePC;
    MWF.xApplication.process.Xform.Datatable$Title = MWF.APPDatatable$Title = MWF.xApplication.process.Xform.DatatablePC$Title;
    MWF.xApplication.process.Xform.Datatable$Data = MWF.APPDatatable$Data = MWF.xApplication.process.Xform.DatatablePC$Data;
}

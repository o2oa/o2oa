MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "DatagridPC", null, false);
MWF.xDesktop.requireApp("process.Xform", "DatagridMobile", null, false);
//if (COMMON.Browser.Platform.isMobile){
if (layout.mobile || COMMON.Browser.Platform.isMobile){
    MWF.xApplication.process.Xform.Datagrid = MWF.APPDatagrid = MWF.xApplication.process.Xform.DatagridMobile;
    MWF.xApplication.process.Xform.Datagrid$Title = MWF.APPDatagrid$Title = MWF.xApplication.process.Xform.DatagridMobile$Title;
    MWF.xApplication.process.Xform.Datagrid$Data = MWF.APPDatagrid$Data = MWF.xApplication.process.Xform.DatagridMobile$Data;
}else{
    MWF.xApplication.process.Xform.Datagrid = MWF.APPDatagrid = MWF.xApplication.process.Xform.DatagridPC;
    MWF.xApplication.process.Xform.Datagrid$Title = MWF.APPDatagrid$Title = MWF.xApplication.process.Xform.DatagridPC$Title;
    MWF.xApplication.process.Xform.Datagrid$Data = MWF.APPDatagrid$Data = MWF.xApplication.process.Xform.DatagridPC$Data;
}

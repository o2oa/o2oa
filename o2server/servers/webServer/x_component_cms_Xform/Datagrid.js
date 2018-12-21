MWF.xDesktop.requireApp("process.Xform", "Datagrid", null, false);
MWF.xApplication.cms.Xform.Datagrid = MWF.CMSDatagrid =  new Class({
    Extends: MWF.APPDatagrid,
    validationConfigItem: function(routeName, data){
        var flag = (data.status=="all") ? true: (routeName == "publ" || routeName == "publish");
        if (flag){
            var n = this.getData();
            var v = (data.valueType=="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if (!v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
});

MWF.xApplication.cms.Xform.Datagrid$Title = MWF.CMSDatagrid$Title =  new Class({
    Extends: MWF.APPDatagrid$Title
});

MWF.xApplication.cms.Xform.Datagrid$Data = MWF.CMSDatagrid$Data =  new Class({
    Extends: MWF.APPDatagrid$Data
});
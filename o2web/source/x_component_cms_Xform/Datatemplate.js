MWF.xDesktop.requireApp("process.Xform", "Datatemplate", null, false);
MWF.xApplication.cms.Xform.Datatemplate = MWF.CMSDatatemplate =  new Class({
    Extends: MWF.APPDatatemplate,
    saveData: function(body){
        if( this.isMergeRead ){ //合并且只读，不处理
            return;
        }
        var bundle = this.form.businessData.document.id;
        o2.Actions.load('x_cms_assemble_control').DataAction.updateArrayDataWithDocument(bundle, body, null, null, false);
    },
    saveFullData: function(data){
        if( this.isMergeRead ){ //合并且只读，不处理
            return;
        }
        var bundle = this.form.businessData.document.id;
        o2.Actions.load('x_cms_assemble_control').DataAction.updateWithDocument(bundle, data, null, null, false);
    },
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
MWF.xDesktop.requireApp("process.Xform", "Number", null, false);
MWF.xApplication.cms.Xform.Number = MWF.CMSNumber =  new Class({
    Extends: MWF.APPNumber,
    validationConfigItem: function(routeName, data){

        var flag = (data.status=="all") ? true: (routeName == "publish");
        if (flag){
            var n = this.getInputData();
            var strN = n.toString();

            if( n === "" && this.json.emptyValue === "string" )n = 0;

            var v = (data.valueType=="value") ? n : strN.length;
            var strV = (data.valueType=="value") ? strN : strN.length;
            switch (data.operateor){
                case "isnull":
                    if (!strV && strV.toString()!=='0'){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (strV){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>parseFloat(data.value)){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<parseFloat(data.value)){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==parseFloat(data.value)){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=parseFloat(data.value)){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (strV.toString().indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (strV.toString().indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
});
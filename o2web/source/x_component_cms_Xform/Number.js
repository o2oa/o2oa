MWF.xDesktop.requireApp("process.Xform", "Number", null, false);
MWF.xApplication.cms.Xform.Number = MWF.CMSNumber =  new Class({
    Extends: MWF.APPNumber,
    validationConfigItem: function(routeName, data){

        var flag = (data.status=="all") ? true: (routeName == "publish");
        if (flag){
            var n = this.getInputData();
            var originN = this.getInputData( true );

            if( n === "" && this.json.emptyValue === "string" )n = 0;

            var v = (data.valueType=="value") ? n : n.length;
            var originV = (data.valueType=="value") ? originN : originN.length;
            switch (data.operateor){
                case "isnull":
                    if (!originV && originV.toString()!=='0'){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (originV){
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
                    if (originV.toString().indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (originV.toString().indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
});
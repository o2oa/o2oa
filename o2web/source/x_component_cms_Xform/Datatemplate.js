MWF.xDesktop.requireApp("process.Xform", "Datatemplate", null, false);
MWF.xApplication.cms.Xform.Datatemplate = MWF.CMSDatatemplate =  new Class({
    Extends: MWF.APPDatatemplate,
    saveArrayData: function(type, index, toIndex, data){
        return;

        if( this.isMergeRead ){ //合并且只读，不处理
            return;
        }
        var method = ['insertLine','addLine'].contains(type) ? 'add' : type;

        var originalData = this.getOriginalDataById();
        if( !originalData ){
            if( method === 'add' ){
                //this.saveFormData();
                this.saveDataById();
            }
            return;
        }

        o2.Actions.load('x_cms_assemble_control').DataAction.updateArrayDataWithDocument(
            this.form.businessData.document.id,
            {
                method: method,
                index: index,
                toIndex: toIndex,
                data: data,
                path: this.json.id.split('..').join('.')
            },
            null, null, false
        );

        switch (type){
            case 'addLine':
                originalData.push(Object.clone(data));
                break;
            case 'insertLine':
                originalData.splice(index, 0, Object.clone(data));
                break;
            case 'delete':
                originalData.splice(index, 1);
                break;
        }
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
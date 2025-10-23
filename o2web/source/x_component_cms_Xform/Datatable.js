MWF.xDesktop.requireApp("process.Xform", "Datatable", null, false);
MWF.xApplication.cms.Xform.Datatable = MWF.CMSDatatable =  new Class({
    Extends: MWF.APPDatatable,
    deleteAttachment: function( attId ){
        this.form.documentAction.deleteAttachment(attId, this.form.businessData.work.id);
    },
    saveFormData: function(){
        this.form.saveFormData();
    },
    saveArrayData: function(type, index, toIndex, data){
        if( this.isMergeRead ){ //合并且只读，不处理
            return;
        }
        var method = ['insertLine','addLine'].contains(type) ? 'add' : type;

        var originalData = this.getOriginalDataById();
        if( !originalData || !originalData.data){
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
                path: this.json.id.split('..').join('.') +'.data'
            },
            ()=>{
                switch (type){
                    case 'addLine':
                        originalData.data.push(Object.clone(data));
                        break;
                    case 'insertLine':
                        originalData.data.splice(index, 0, Object.clone(data));
                        break;
                    case 'delete':
                        originalData.data.splice(index, 1);
                        break;
                    case 'move':
                        var upData = originalData.data[toIndex];
                        var curData = originalData.data[index];
                        originalData.data[index] = upData;
                        originalData.data[toIndex] = curData;
                        break;
                }
            }, null, false
        );
    },
    validationConfigItem: function(routeName, data){
        var flag = (data.status=="all") ? true: (routeName == "publ" || routeName == "publish");
        if (flag){
            var n = this.getInputData();
            if( o2.typeOf(n)==="object"){
                var arr = [];
                Object.each( n, function (d, key) {
                    if(o2.typeOf(d) === "array")arr = arr.concat(d);
                });
                n = arr;
            }
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

MWF.xApplication.cms.Xform.Datatable$Title = MWF.CMSDatatable$Title =  new Class({
    Extends: MWF.APPDatatable$Title
});

MWF.xApplication.cms.Xform.Datatable$Data = MWF.CMSDatatable$Data =  new Class({
    Extends: MWF.APPDatatable$Data
});
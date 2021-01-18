MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);

if( !MWF.CMS$Input_Process ){
    MWF.CMS$Input_Process = {
        validationConfigItem : MWF.xApplication.process.Xform.$Input.prototype.validationConfigItem.$origin,
        _loadStyles : MWF.xApplication.process.Xform.$Input.prototype._loadStyles.$origin

    };

    MWF.xApplication.process.Xform.$Input.implement({
        _loadStyles: function(){
            //var isCMS = layout.desktop.currentApp.options.name.toLowerCase().contains("cms");
            var isCMS = this.form.app.options.name.toLowerCase().contains("cms");
            if( isCMS ){
                return this._loadStyles_CMS();
            }else{
                return this._loadStyles_Process();
            }
        },
        _loadStyles_Process : MWF.CMS$Input_Process._loadStyles,
        _loadStyles_CMS: function(){
            if (this.json.styles) this.node.setStyles(this.json.styles);
            if( this.readonly ){
                var parent = this.node.parentNode;
                if( parent.tagName.toLowerCase() == "td" ){
                    var border = parent.getStyle("borderBottomWidth");
                    if( border.toInt() > 0 ){
                        this.node.setStyle("border","0px");
                    }
                }
            }
            if (this.json.inputStyles) if (this.node.getFirst()) this.node.getFirst().setStyles(this.json.inputStyles);
            if (this.iconNode && this.iconNode.offsetParent !== null  ){
                var size = this.node.getSize();
                this.iconNode.setStyle("height", ""+size.y+"px");
            }
        },
        validationConfigItem: function(routeName, data){
            //var isCMS = layout.desktop.currentApp.options.name.toLowerCase().contains("cms");
            var isCMS = this.form.app.options.name.toLowerCase().contains("cms");
            if( isCMS ){
                return this.validationConfigItem_CMS( routeName, data );
            }else{
                return this.validationConfigItem_Process( routeName, data );
            }
        },
        validationConfigItem_Process : MWF.CMS$Input_Process.validationConfigItem,
        validationConfigItem_CMS: function(routeName, data){
            var flag = (data.status=="all") ? true: ( routeName == "publish");
            if (flag){
                var n = this.getInputData();
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

}

MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Xform = MWF.xApplication.cms.Xform || {};
MWF.xApplication.cms.Xform.ModuleImplements = {};
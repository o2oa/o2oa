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
                data.decision = "publish";
            }
            return MWF.CMS$Input_Process.validationConfigItem.apply(this, [routeName, data]);
        }
    });

}

MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Xform = MWF.xApplication.cms.Xform || {};
MWF.xApplication.cms.Xform.ModuleImplements = {};
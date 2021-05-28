MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("cms.Xform", "widget.Comment", null, false);
/** @class Comment 评论组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var comment = this.form.get("name"); //获取组件
 * //方法2
 * var comment = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {CMS}
 * @hideconstructor
 */
MWF.xApplication.cms.Xform.Comment = MWF.CMSComment =  new Class(
    /** @lends MWF.xApplication.process.Xform.Comment# */
    {
	Extends: MWF.APP$Module,
	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");

        debugger;

        var config = {};
        if(this.json.editorProperties){
            config = Object.clone(this.json.editorProperties);
        }
        if (this.json.config){
            if (this.json.config.code){
                var obj = this.form.Macro.exec(this.json.config.code, this);
                Object.each(obj, function(v, k){
                    config[k] = v;
                });
            }
        }

        /**
         * @summary 评论组件使用this.comment实现功能
         * @member {MWF.xApplication.cms.Xform.widget.Comment}
         * @example
         *  //可以在脚本中获取该组件
         * var field = this.form.get("fieldId"); //获取组件对象
         * var items = field.comment.editor; //获取评论的编辑器对象
         */
        this.comment = new MWF.xApplication.cms.Xform.widget.Comment( this.form.app, this.node, {
            "documentId" : this.form.businessData.document.id,
            "countPerPage" : this.json.countPerPage || 10,
            "isAllowModified" : this.json.isAllowModified,
            "isAllowPublish" : this.json.isAllowPublish,
            "isAdmin" : this.form.app.isAdmin,
            "editorProperties" : config
        });
        this.comment.load();
	}
}); 
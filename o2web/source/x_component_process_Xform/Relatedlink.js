MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Relatedlink 相关链接组件，本组件根据其当前流程实例的相关性分数列式其他流程实例（带权限）。
 * @o2cn 相关链接组件
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var relatedlink = this.form.get("relatedlink"); //获取组件
 * //方法2
 * var relatedlink = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @since v7.3
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Relatedlink = MWF.APPRelatedlink =  new Class(
    /** @lends MWF.xApplication.process.Xform.Relatedlink# */
    {
	Implements: [Events],
    Extends: MWF.xApplication.process.Xform.$Module,
    options: {
        /**
         * 加载数据后事件。
         * @event MWF.xApplication.process.Xform.Relatedlink#postLoadData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         * @example
         * //触发该事件的时候可以获取到链接数据linkData
         * var linkData = this.target.linkData;
         * //可以修改linkData达到定制化链接数据的效果
         * do something
         */
        /**
         * 创建每行需要导入的数据后触发，this.event指向当前链接对象。
         * @event MWF.xApplication.process.Xform.Relatedlink#postLoadLink
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         * @example
         * <caption>this.event格式如下：</caption>
         * {
         *  node: DomElement,
         *  data: {
         *   "title", "标题一",
         *    ...
         *  }
         * }
         */
        "moduleEvents": ["queryLoad","postLoad", "afterLoad", "postLoadData", "postLoadLink"]
    },
    _loadUserInterface: function(){
        this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");
        this.node.setStyles(this.form.css.logActivityNode_record);

        o2.Actions.load("x_query_assemble_surface").MoreLikeThisAction.post({
            flag: this.getFlag(),
            category: this.getCategory(),
            count: (this.json.count || 6).toInt()
        }, function(json){
            this.linkData = json.data.moreLikeThisList;
            this.fireEvent("postLoadData");
            this.loadLinks();
        }.bind(this));
    },
    getFlag: function(){
        return this.form.businessData.work.id;
        // if (this.form.businessData.work && !this.form.businessData.work.completedTime) {
        //     return this.form.businessData.work.id;
        // } else {
        //     return this.form.businessData.workCompleted.id;
        // }
    },
    getCategory: function(){
        return "processPlatform";
    },
    loadLinks: function(){
        switch (this.json.mode){
            case "script":
                this.loadLinkByScript();
                break;
            default:
                this.loadLinkDefault();
        }
    },
    loadLinkByScript: function(){
        if (this.json.displayScript && this.json.displayScript.code){
            var code = this.json.displayScript.code;
            this.linkData.each(function(log){
                this.form.Macro.environment.log = log;
                this.form.Macro.environment.list = null;
                var r = this.form.Macro.exec(code, this);

                var t = o2.typeOf(r);
                if (t==="string"){
                    this.node.appendHTML(r);
                }else if (t==="element"){
                    this.node.appendChild(r);
                }
                var node = this.node.getLast();
                this.setOpenEvent(node, log);
                this.fireEvent("postLoadLink", [{
                    node: node,
                    data: log
                }]);
            }.bind(this));
        }
    },
    loadLinkDefault: function(){
        var text = this.json.textStyle;
        var readPersons = [];
        this.lineClass = "logTaskNode";
        this.linkData.each(function(log, i){
            var div = new Element("div", {styles: this.form.css[this.lineClass]}).inject(this.node);
            var leftDiv = new Element("div", {styles: this.form.css.logTaskIconNode_record}).inject(div);
            var rightDiv = new Element("div", {styles: this.form.css.logTaskTextNode}).inject(div);
            var html = text.replace(/{flag}/g, log.flag)
                .replace(/{createDate}/g, log.createTime.substring(0.10))
                .replace(/{createTime}/g, log.createTime)
                .replace(/{updateDate}/g, log.updateTime.substring(0.10))
                .replace(/{updateTime}/g, log.updateTime)
                .replace(/{creatorUnit}/g, o2.name.cn(log.creatorUnit))
                .replace(/{creatorPerson}/g, o2.name.cn(log.creatorPerson))
                .replace(/{title}/g, log.title || "");
            rightDiv.appendHTML(html);

            if (this.lineClass === "logTaskNode"){
                this.lineClass = "logTaskNode_even";
            }else{
                this.lineClass = "logTaskNode";
            }
            this.setOpenEvent(div, log);
            this.fireEvent("postLoadLink", [{
                node: div,
                data: log
            }]);
        }.bind(this));
    },
    setOpenEvent: function(node, data){
        node.setStyle("cursor", "pointer");
        if (data.category==="cms"){
            node.addEvent("click", function(ev){
                this.openCMSDocument( data, false);
                ev.stopPropagation();
            }.bind(this));
        }else{
            node.addEvent("click", function(ev){
                this.openWork(data);
                ev.stopPropagation();
            }.bind(this));
        }
    },
    openWork: function(data){
        var appId = "process.Work"+data.flag;
        // if (layout.desktop.apps[appId]){
        //     if (!layout.desktop.apps[appId].window){
        //         layout.desktop.apps[appId] = null;
        //         layout.openApplication(null, layout.desktop.apps[appId].options.name, layout.desktop.apps[appId].options, layout.desktop.apps[appId].options.app, false, this, false);
        //     }else{
        //         layout.desktop.apps[appId].setCurrent();
        //     }
        // }else {
            var op = {
                "jobId": data.flag,
                "appId": appId
            };
            return layout.desktop.openApplication(this.event, "process.Work", op);
        // }
    },
    openCMSDocument : function( data, isEdited ){
        var appId = "cms.Document"+data.flag;
        // if (layout.desktop.apps[appId]){
        //     if (!layout.desktop.apps[appId].window){
        //         layout.desktop.apps[appId] = null;
        //         layout.openApplication(null, layout.desktop.apps[appId].options.name, layout.desktop.apps[appId].options, layout.desktop.apps[appId].options.app, false, this, false);
        //     }else{
        //         layout.desktop.apps[appId].setCurrent();
        //     }
        // }else {
            var options = {
                "documentId": data.flag,
                "appId": appId,
                "readonly" : !isEdited
            };
            layout.desktop.openApplication(null, "cms.Document", options);
        // }
    }
	
}); 

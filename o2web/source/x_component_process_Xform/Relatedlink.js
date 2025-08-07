MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Relatedlink 相关推荐组件，本组件根据其当前流程实例的相关性分数列式其他流程实例（带权限）。
 * @o2cn 相关推荐组件
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

        if (!this.isReadable){
            this.node.setStyle('display', 'none');
            return '';
        }

        this.node.setStyle("-webkit-user-select", "text");

        switch (this.json.activeType) {
            case "click":
                this.loadButton();
                break;
            case "delay":
                break;
            case "immediately":
                this.loadContent();
                break;
        }
    },
    loadButton: function(){
        this.buttonNode = new Element("div.rlbutton", {
            "styles": this.json.buttonStyles || {},
            "text": this.json.buttonText,
            "events":{
                click: function () {
                    this.active();
                    this.buttonNode.destroy();
                    this.buttonNode = null;
                }.bind(this)
            }
        }).inject( this.node );
    },
    /**
     * @summary 当相关推荐被设置为延迟激活，通过active方法激活
     * @param {Function} callback 激活后的回调
     * @example
     * var relativeLink = this.form.get("fieldId");
     * relativeLink.active(function(){
     *     //do someting
     * })
     */
    active: function( callback ){
	    if( !this.loaded ){
	        this.loadContent( callback );
	    }else{
	        if(callback)callback();
        }
    },
    loadContent: function( callback ){
        if (!this.isReadable){
            this.node.setStyle('display', 'none');
            return '';
        }
        o2.Actions.load("x_query_assemble_surface").MoreLikeThisAction.post({
            flag: this.getFlag(),
            category: this.getCategory(),
            count: (this.json.count || 6).toInt()
        }, function(json){
            this.linkData = json.data.moreLikeThisList;
            this.fireEvent("postLoadData");
            this.loadLinks();
            this.loaded = true;
            this.fireEvent("afterLoad");
            if(callback)callback();
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
            case "text":
                this.loadLinkDefault();
                break;
            default:
                this.loadLinkTable();
        }
    },
    loadLinkTable: function(){
        var table = new Element("table", {
            "styles": this.json.tableStyles || this.form.css.relatedlinkTable,
            "border": "0",
            "cellSpacing": "0",
            "cellpadding": "3px",
            "width": "100%"
        }).inject(this.node);
        var tr = table.insertRow(0); //.setStyles( this.form.css.relatedlinkTableTitleLine );

        var lp = MWF.xApplication.process.Xform.LP;
        var thStyle = this.json.tableTitleCellStyles || this.form.css.relatedlinkTableTitle;
        var td = tr.insertCell(0).setStyles(thStyle);
        td.set("text", lp.title);
        td = tr.insertCell(1).setStyles(thStyle);
        td.set("text", lp.creatorPerson);
        td = tr.insertCell(2).setStyles(thStyle);
        td.set("text", lp.creatorUnit);
        td = tr.insertCell(3).setStyles(thStyle);
        td.set("text", lp.createTime);
        td = tr.insertCell(4).setStyles(thStyle);
        td.set("text", lp.updateTime);
        td = tr.insertCell(5).setStyles(thStyle);
        td.set("text", lp.score);

        var tdStyle = this.json.tableContentCellStyles || this.form.css.relatedlinkTableCell;
        this.linkData.each(function (log, idx) {
            var tr = table.insertRow(table.rows.length);
            tr.setStyles( this.json.tableContentLineStyles || this.form.css.relatedlinkTableLine );
            tr.addEvents({
                "mouseover": function () {
                    tr.setStyles( this.json.tableContentLineStyles_over || this.form.css.relatedlinkTableLine_over )
                }.bind(this),
                "mouseout": function () {
                    tr.setStyles( this.json.tableContentLineStyles || this.form.css.relatedlinkTableLine )
                }.bind(this)
            })

            var td = tr.insertCell(0).setStyles( tdStyle );
            td.set("text", log.title);
            td.setStyles( this.json.tableContentCellStyles_title || this.form.css.relatedlinkTableCell_title );

            td = tr.insertCell(1).setStyles(tdStyle);
            td.set("text", log.creatorPerson);
            td = tr.insertCell(2).setStyles(tdStyle);
            td.set("text", log.creatorUnit);
            td = tr.insertCell(3).setStyles(tdStyle);
            td.set("text", log.createTime);
            td = tr.insertCell(4).setStyles(tdStyle);
            td.set("text", log.updateTime);
            td = tr.insertCell(5).setStyles(tdStyle);
            td.set("text", log.score);

            this.setOpenEvent(tr, log);

            this.fireEvent("postLoadLink", [{
                node: tr,
                data: log
            }]);
        }.bind(this))
    },
    loadLinkByScript: function(){
        if (this.json.displayScript && this.json.displayScript.code){
            var code = this.json.displayScript.code;
            this.linkData.each(function(log){
                this.form.Macro.environment.link = log;
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
        this.lineClass = "relatedlinkNode";
        this.linkData.each(function(log, i){
            var div = new Element("div", {styles: this.form.css[this.lineClass]}).inject(this.node);
            var rightDiv = new Element("div", {styles: this.form.css.relatedlinkTextNode}).inject(div);
            var html = text.replace(/{flag}/g, log.flag)
                .replace(/{createDate}/g, log.createTime.substring(0.10))
                .replace(/{createTime}/g, log.createTime)
                .replace(/{updateDate}/g, log.updateTime.substring(0.10))
                .replace(/{updateTime}/g, log.updateTime)
                .replace(/{creatorUnit}/g, o2.name.cn(log.creatorUnit))
                .replace(/{creatorPerson}/g, o2.name.cn(log.creatorPerson))
                .replace(/{title}/g, log.title || "");
            rightDiv.appendHTML(html);

            if (this.lineClass === "relatedlinkNode"){
                this.lineClass = "relatedlinkNode_even";
            }else{
                this.lineClass = "relatedlinkNode";
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

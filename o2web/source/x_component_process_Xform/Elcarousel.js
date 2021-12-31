o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elcarousel 基于Element UI的输入框组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var input = this.form.get("name"); //获取组件
 * //方法2
 * var input = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|Element UI Tree 树形控件}
 */
MWF.xApplication.process.Xform.Elcarousel = MWF.APPElcarousel =  new Class(
    /** @lends o2.xApplication.process.Xform.Elcarousel# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 幻灯片切换时触发。this.event[0]为目前激活的幻灯片的索引；
         * @event MWF.xApplication.process.Xform.Elcarousel#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/carousel|幻灯片组件的Events章节}
         */
        "elEvents": ["change"]
    },
    _loadNode: function(){
        this._loadNodeEdit();
    },
    _loadNodeEdit: function(){
        this._createElementHtml().then(function(html){
            this.json["$id"] = (this.json.id.indexOf("..")!==-1) ? this.json.id.replace(/\.\./g, "_") : this.json.id;
            this.node.appendHTML(html, "before");
            var input = this.node.getPrevious();

            this.node.destroy();
            this.node = input;
            this.node.set({
                "id": this.json.id,
                "MWFType": this.json.type
            });
            this.node.addClass("o2_vue");
            this._createVueApp();
        }.bind(this));
    },
    _appendVueData: function(){
        if (!this.json.heightText) this.json.heightText = this.json.height+"px";
        if( !this.json.initialIndex )this.json.initialIndex = 0;
        if( !this.json.carouselType )this.json.carouselType = "";
        // if (this.json.dataType === "script"){
        //     this.json.data = this.form.Macro.exec(((this.json.dataScript) ? this.json.dataScript.code : ""), this);
        // }else{
        //     this.json.data = this.json.dataJson;
        // }
        // this.parseData();
    },
    _createElementHtml: function() {
        return this.getItemHtml().then(function ( itemHtml ) {
            var html = "<el-carousel";
            html += " :height=\"heightText\"";
            html += " :initial-index=initialIndex";
            html += " :trigger=\"trigger\"";
            html += " :autoplay=\"autoplay\"";
            html += " :interval=interval";
            html += " :indicator-position=\"indicatorPosition\"";
            html += " :arrow=\"arrow\"";
            html += " :type=\"carouselType\"";
            html += " :loop=\"loop\"";
            html += " :direction=\"direction\"";

            this.options.elEvents.forEach(function(k){
                html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
            });

            if (this.json.elProperties){
                Object.keys(this.json.elProperties).forEach(function(k){
                    if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
                }, this);
            }

            if (this.json.elStyles) html += " :style=\"elStyles\"";

            html += ">";

            if (this.json.vueSlot)html += this.json.vueSlot;

            html += itemHtml;

            html += "</el-carousel>";
            return html;
        }.bind(this))
    },
    getItemHtml: function(){
        if( this.json.dataType === "hotpicture" ){
            var obj;
            if( this.json.filterScript && this.json.filterScript.code){
                obj = this.form.Macro.exec(((this.json.filterScript) ? this.json.filterScript.code : ""), this);
            }
            if(obj)obj = {};
            var action = o2.Actions.load("x_hotpic_assemble_control").HotPictureInfoAction.listForPage( this.json.page, this.json.count, obj );
            return action.then(function (json) {
                // var lineHeight = this.json.height ? ( "line-height:"+this.json.height + "px;") : "";
                var html = "";
                this.json.items = json.data;
                html += "<el-carousel-item v-for='item in items'>";
                html +=     "<img :src=\"getImageSrc(item.picId)\"  @click=\"clickHotpictureItem($event, item)\" />";
                html +=     "<div>{{item.text}}</div>";
                html += "</el-carousel-item>";
                return html;
            }.bind(this))
        }
    },
    _afterCreateVueExtend: function (app) {
        var _self = this;
        app.methods.clickHotpictureItem = function (ev, item) {
            if( item.application === "CMS" ){
                _self.openDocument(item.infoId, item.title)
            }else if( item.application === "BBS" ){
                _self.openBBSDocument(item.infoId);
            }
        }
        app.methods.getImageSrc = function (picId) {
            return o2.xDesktop.getImageSrc(picId);
        }
    },
    openDocument: function(id, title, options){
        var op = options || {};
        op.documentId = id;
        op.docTitle = title;
        op.appId = (op.appId) || ("cms.Document"+id);
        return layout.desktop.openApplication(null, "cms.Document", op);
    },
    openBBSDocument: function(id){
        var appId = "ForumDocument"+id;
        if (layout.desktop.apps && layout.desktop.apps[appId]){
            layout.desktop.apps[appId].setCurrent();
        }else {
            return layout.desktop.openApplication(null, "ForumDocument", {
                "id" : id,
                "appId": appId,
                "isEdited" : false,
                "isNew" : false
            });
        }
    }

}); 

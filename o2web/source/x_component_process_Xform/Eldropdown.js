o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Eldropdown 基于Element UI的下拉菜单组件。本组件不往后台存数据。
 * @o2cn 下拉菜单
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/dropdown|Element UI Dropdown 下拉菜单}
 */
MWF.xApplication.process.Xform.Eldropdown = MWF.APPEldropdown =  new Class(
    /** @lends o2.xApplication.process.Xform.Eldropdown# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * split-button 为 true 时，点击左侧按钮的回调
         * @event MWF.xApplication.process.Xform.Eldropdown#click
         * @see {@link https://element.eleme.cn/#/zh-CN/component/dropdown|下拉菜单组件的Events章节}
         */
        /**
         * 点击菜单项触发的事件回调。this.event[0]指向dropdown-item 的指令（选项的command值）
         * @event MWF.xApplication.process.Xform.Eldropdown#focus
         * @see {@link https://element.eleme.cn/#/zh-CN/component/dropdown|下拉菜单组件的Events章节}
         */
        /**
         * 下拉框出现/隐藏时触发。出现则this.event[0]为 true，隐藏则this.event[0]为 false
         * @event MWF.xApplication.process.Xform.Eldropdown#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/dropdown|下拉菜单组件的Events章节}
         */
        "elEvents": ["click", "command", "visible-change"]
    },
    __setReadonly: function(data){
        if (this.isReadonly()) {
            this.node.set("text", data);
            if( this.json.elProperties ){
                this.node.set(this.json.elProperties );
            }
            if (this.json.elStyles){
                this.node.setStyles( this._parseStyles(this.json.elStyles) );
            }
        }
    },
    _loadNode: function(){
        if (this.isReadonly()) this.json.disabled = true;
        this._loadNodeEdit();
    },
    _loadNodeEdit: function(){
        var id = (this.json.id.indexOf("..")!==-1) ? this.json.id.replace(/\.\./g, "_") : this.json.id;
        this.json["$id"] = (id.indexOf("-")!==-1) ? id.replace(/-/g, "_") : id;
         this.node.appendHTML(this._createElementHtml(), "before");
        var input = this.node.getPrevious();

        this.node.destroy();
        this.node = input;
        this.node.setStyle("opacity", 0);
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
        this.node.addClass("o2_vue");
        this._createVueApp();
    },
    _appendVueData: function(){
        if (!this.json.size) this.json.size = "";
        if (!this.json.placement) this.json.placement = "bottom-end";
        if (!this.json.trigger) this.json.trigger = "hover";
        if (o2.typeOf(this.json.hideOnClick)!=="boolean") this.json.hideOnClick = true;
        if (o2.typeOf(this.json.splitButton)!=="boolean") this.json.splitButton = false;
        if (!this.json.buttonType) this.json.buttonType = "";
        if (!this.json.showTimeout) this.json.showTimeout = 250;
        if (!this.json.hideTimeout) this.json.hideTimeout = 150;
        // if(o2.typeOf(this.json.disabled)!=="boolean")this.json.disabled = this.isReadonly();
    },
    _afterMounted: function(el){
        this.node = el;
        this.node.setStyle("opacity", 1);
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
        this._loadVueCss();
        this._loadDomEvents();
        this._afterLoaded();
        this.fireEvent("postLoad");
        this.fireEvent("load");
    },
    _createElementHtml: function() {
        var html = "<el-dropdown";
        // html += " :readonly=\"readonly\"";
        html += " :placement=\"placement\"";
        html += " :trigger=\"trigger\"";
        html += " :hide-on-click=\"hideOnClick\"";
        html += " :size=\"size\"";
        html += " :disabled=\"disabled\"";

        if( this.json.showButton && this.json.splitButton ){
            html += " :split-button=\"splitButton\"";
            html += " :type=\"buttonType\"";
        }

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

        if (this.json.vueSlot){
            html += this.json.vueSlot;
        }else{
            html += this.getButtonHtml();
        }

        html += this.getItemHtml();

        html += "</el-dropdown>";
        return html;
    },
    getItemHtml: function(){
        var html = " <el-dropdown-menu slot=\"dropdown\">";

        var datToHtml = function (data) {
            var command = data.command ? (" command=\""+data.command+"\"") : "";
            var disabled = ( data.disabled && (data.disabled==="true" || data.disabled===true) ) ? " disabled" : "";
            var divided = ( data.divided && (data.divided==="true" || data.divided===true) ) ? " divided" : "";
            var icon = data.icon ? (" icon=\""+data.icon+"\"") : "";
            html += "	<el-dropdown-item" + command + disabled + divided + icon + ">"+data.label+"</el-dropdown-item>";
        };

        if(this.json.dataType === "json"){
            this.json.dataJson.each(function(d){
                datToHtml(d)
            })
        }else if(this.json.dataScript && this.json.dataScript.code){
            var data = this.form.Macro.exec(((this.json.dataScript) ? this.json.dataScript.code : ""), this);
            if( data && o2.typeOf(data) !== "array" )data = [data];
            if( data )data.each(function(d){
                datToHtml(d)
            })
        }

        html += "</el-dropdown-menu>";
        return html;
    },
    getButtonHtml: function(){
        if( this.json.showButton ){
            if( this.json.splitButton ) {
                return this.json.text || MWF.xApplication.process.Xform.LP.pleaseSelect;
            }else{
                return "<el-button type=\""+this.json.buttonType+"\" size=\""+this.json.size+"\">"+ ( this.json.text || MWF.xApplication.process.Xform.LP.pleaseSelect ) +
                		"<i class=\"el-icon-arrow-down el-icon--right\"></i>"+
                	"</el-button>";
            }
        }else{
            return " <span class=\"el-dropdown-link\">"+ ( this.json.text || MWF.xApplication.process.Xform.LP.pleaseSelect ) +
                "<i class=\"el-icon-arrow-down el-icon--right\"></i>"+
                "</span>";
        }
    }
}); 

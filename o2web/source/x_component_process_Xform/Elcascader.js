o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elcascader 基于Element UI的级联选择框组件。
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
 */
MWF.xApplication.process.Xform.Elcascader = MWF.APPElcascader =  new Class(
    /** @lends o2.xApplication.process.Xform.Elcascader# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        "elEvents": ["focus", "blur", "change", "visible-change", "remove-tag", "expand-change", "before-filter"]
    },
    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        if (!this.json.options) this.json.options = [];
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.popperClass) this.json.popperClass = "";
        if (this.json.showAllLevels!==false) this.json.showAllLevels = true;
        if (!this.json.separator) this.json.separator = "/";
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.description) this.json.description = "";
        if (!this.json.filterable) this.json.filterable = false;
        if (!this.json.collapseTags) this.json.collapseTags = false;
        if (!this.json.props) this.json.props = {};

        if (!this.json.props.expandTrigger) this.json.props.expandTrigger = "click";
        if (!this.json.props.multiple) this.json.props.multiple = false;
        if (this.json.props.emitPath!==false) this.json.props.emitPath = true;
        if (!this.json.props.lazy) this.json.props.lazy = false;
        if (!this.json.props.lazyLoad) this.json.props.lazyLoad = null;
        if (!this.json.props.value) this.json.props.value = "value";
        if (!this.json.props.label) this.json.props.label = "label";
        if (!this.json.props.children) this.json.props.children = "children";
        if (!this.json.props.disabled) this.json.props.disabled = "disabled";
        if (!this.json.props.leaf) this.json.props.leaf = "leaf";

        this._loadOptions();

        if (this.json.props.multiple===true) if (!this.json[this.json.id] || !this.json[this.json.id].length) this.json[this.json.id] = [];
    },
    appendVueMethods: function(methods){
        if (this.json.filterMethod && this.json.filterMethod.code){
            var fn = this.form.Macro.exec(this.json.filterMethod.code, this);
            methods.$filterMethod = function(){
                fn.apply(this, arguments);
            }.bind(this);
        }
        if (this.json.lazyLoadScript && this.json.lazyLoadScript.code){
            var fn = this.form.Macro.exec(this.json.lazyLoadScript.code, this);
            this.json.props.lazyLoad = function(){
                fn.apply(this, arguments);
            }.bind(this);
        }
        if (this.json.beforeFilter && this.json.beforeFilter.code){
            var fn = this.form.Macro.exec(this.json.beforeFilter.code, this);
            methods.$beforeFilter = function(){
                fn.apply(this, arguments);
            }.bind(this);
        }
    },

    _setOptionsWithCode: function(code){
        var v = this.form.Macro.exec(code, this);
        if (v.then){
            v.then(function(o){
                if (o2.typeOf(o)==="array"){
                    this.json.options = o;
                    this.json.$options = o;
                }
            }.bind(this));
        }else if (o2.typeOf(v)==="array"){
            this.json.options = v;
            this.json.$options = v;
        }
    },
    _loadOptions: function(){
        if (this.json.itemsScript && this.json.itemsScript.code)  this._setOptionsWithCode(this.json.itemsScript.code);
    },
    _createElementHtml: function(){

        if (!this.json.options) this.json.options = [];
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.popperClass) this.json.popperClass = "";
        if (this.json.showAllLevels!==false) this.json.showAllLevels = true;
        if (!this.json.separator) this.json.separator = "/";
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.description) this.json.description = "";
        if (!this.json.filterable) this.json.filterable = false;
        if (!this.json.props) this.json.props = {};

        var html = "<el-cascader ";
        html += " v-model=\""+this.json.id+"\"";
        html += " :clearable=\"clearable\"";
        html += " :size=\"size\"";
        html += " :filterable=\"filterable\"";
        html += " :disabled=\"disabled\"";
        html += " :placeholder=\"description\"";
        html += " :options=\"options\"";
        html += " :collapse-tags=\"collapseTags\"";
        html += " :show-all-levels=\"showAllLevels\"";
        html += " :separator=\"separator\"";
        html += " :popper-class=\"popperClass\"";
        html += " :props=\"props\"";

        if (this.json.filterMethod && this.json.filterMethod.code){
            html += " :filter-method=\"$filterMethod\"";
        }
        if (this.json.beforeFilter && this.json.beforeFilter.code){
            html += " :before-filter=\"$beforeFilter\"";
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

        if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-cascader >";
        return html;
    },
    __setReadonly: function(data){},
    getCheckedNodes: function(leafOnly){
        return (this.vm) ? this.vm.getCheckedNodes(leafOnly) : null;
    }
}); 

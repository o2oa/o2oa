o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Eltree 基于Element UI的输入框组件。
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
MWF.xApplication.process.Xform.Eltree = MWF.APPEltree =  new Class(
    /** @lends o2.xApplication.process.Xform.Eltree# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        "elEvents": ["focus", "blur", "change", "input", "clear"]
    },
    // _queryLoaded: function(){
    //
    // },
    _appendVueData: function(){
        if (!this.json.emptyText) this.json.emptyText = "";
        if (!this.json.renderAfterExpand) this.json.renderAfterExpand = true;
        if (!this.json.highlightCurrent) this.json.highlightCurrent = false;
        if (!this.json.defaultExpandAll) this.json.defaultExpandAll = false;
        if (!this.json.expandOnClickNode) this.json.expandOnClickNode = true;
        if (!this.json.accordion) this.json.accordion = false;
        if (!this.json.indent) this.json.indent = 16;

        if (this.json.dataType === "script"){
            this.json.data = this.form.Macro.exec(((this.json.dataScript) ? this.json.dataScript.code : ""), this);
        }else{
           this.json.data = this.json.dataJson;
        }

        if (this.json.currentNodeKey && this.json.currentNodeKey.code){
            this.json.currentNodeKey = this.form.Macro.fire(this.json.currentNodeKey.code, this);
        }
        if( !this.json.defaultExpandAll && this.json.defaultExpandedKeys && this.json.defaultExpandedKeys.code ){
            this.json.defaultExpandedKeys = this.form.Macro.fire(this.json.defaultExpandedKeys.code, this);
        }
        if( this.json.showCheckbox && this.json.defaultCheckedKeys && this.json.defaultCheckedKeys.code ){
            this.json.defaultCheckedKeys = this.form.Macro.fire(this.json.defaultCheckedKeys.code, this);
        }
        if( this.json.lazy && this.json.loadFun && this.json.loadFun.code  ){
            this.json.lazyLoadFun = function(node, resolve){
                return this.form.Macro.fire(this.json.loadFun.code, this, {
                    node: node,
                    resolve: resolve
                });
            }.bind(this)
        }
        if( this.json.draggable ){
            if( this.json.allowDrag && this.json.allowDrag.code ){
                this.json.allowDragFun = function(node){
                    return this.form.Macro.fire(this.json.allowDrag.code, this, node);
                }.bind(this)
            }
            if( this.json.allowDrop && this.json.allowDrop.code ){
                this.json.allowDropFun = function(node){
                    return this.form.Macro.fire(this.json.allowDrop.code, this, node);
                }.bind(this)
            }
        }

    },
    _createElementHtml: function() {
        var html = "<el-tree";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :data=\"data\"";
        html += " :empty-text=\"emptyText\"";
        html += " :props=\"defaultProps\"";
        html += " :render-after-expand=\"renderAfterExpand\"";
        html += " :highlight-current=\"highlightCurrent\"";
        html += " :default-expand-all=\"defaultExpandAll\"";
        html += " :expand-on-click-node=\"expandOnClickNode\"";
        html += " :accordion=\"accordion\"";
        html += " :indent=\"indent\"";


        if( this.json.nodeKey ){
            html += " :node-key=\"nodeKey\"";
        }
        if( this.json.currentNodeKey && this.json.currentNodeKey.code ){
            html += " :current-node-key=\"currentNodeKey\"";
        }
        if( !this.json.defaultExpandAll && this.json.defaultExpandedKeys && this.json.defaultExpandedKeys.code ){
            html += " :default-expanded-keys=\"defaultExpandedKeys\"";
        }
        if( this.json.showCheckbox ){
            html += " :show-checkbox=\"showCheckbox\"";
            html += " :check-on-click-node=\"checkOnClickNode\"";
            html += " :check-strictly=\"checkStrictly\"";
            if( this.json.defaultCheckedKeys && this.json.defaultCheckedKeys.code ){
                html += " :default-checked-keys=\"defaultCheckedKeys\"";
            }
        }
        if( this.json.lazy ){
            html += " :lazy=\"lazy\"";
            if( this.json.loadFun && this.json.loadFun.code ){
                html += " :load=\"lazyLoadFun\"";
            }
        }
        if( this.json.draggable ){
            html += " :draggable=\"draggable\"";
            if( this.json.allowDrag && this.json.allowDrag.code ){
                html += " :allow-drag=\"allowDragFun\"";
            }
            if( this.json.allowDrop && this.json.allowDrop.code ){
                html += " :allow-drop=\"allowDropFun\"";
            }
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

        html += "</el-tree>";
        return html;
    }
}); 

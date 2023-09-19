o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Eltree 基于Element UI的树形控件。
 * @o2cn 树形控件
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
MWF.xApplication.process.Xform.Eltree = MWF.APPEltree =  new Class(
    /** @lends o2.xApplication.process.Xform.Eltree# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 节点被点击时的回调。this.event[0]指向传递给 data 属性的数组中该节点所对应的对象；
         * this.event[1]为节点对应的 Node；this.event[2]为节点组件本身
         * @event MWF.xApplication.process.Xform.Eltree#node-click
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 当某一节点被鼠标右键点击时会触发该事件。this.event[0]指向Event；
         * this.event[1]为传递给 data 属性的数组中该节点所对应的对象；this.event[2]为节点对应的 Node;
         * this.event[3]为节点组件本身
         * @event MWF.xApplication.process.Xform.Eltree#node-contextmenu
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 节点选中状态发生变化时的回调。this.event[0]为传递给 data 属性的数组中该节点所对应的对象；
         * this.event[1]为节点本身是否被选中；this.event[2]为节点的子树中是否有被选中的节点
         * @event MWF.xApplication.process.Xform.Eltree#check-change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 当复选框被点击的时候触发。this.event[0]为传递给 data 属性的数组中该节点所对应的对象；
         * this.event[1]为树目前的选中状态对象，包含 checkedNodes、checkedKeys、halfCheckedNodes、halfCheckedKeys 四个属性
         * @event MWF.xApplication.process.Xform.Eltree#check
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 当前选中节点变化时触发的事件。this.event[0]为当前节点的数据；
         * this.event[1]为当前节点的 Node 对象
         * * @event MWF.xApplication.process.Xform.Eltree#current-change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 节点被展开时触发的事件。this.event[0]为传递给 data 属性的数组中该节点所对应的对象；
         * this.event[1]为节点对应的 Node；this.event[2]为节点组件本身
         * * @event MWF.xApplication.process.Xform.Eltree#node-expand
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 节点被关闭时触发的事件。this.event[0]为传递给 data 属性的数组中该节点所对应的对象；
         * this.event[1]为节点对应的 Node；this.event[2]为节点组件本身
         * * @event MWF.xApplication.process.Xform.Eltree#node-collapse
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 节点开始拖拽时触发的事件。this.event[0]为被拖拽节点对应的 Node；
         * this.event[1]为Event
         * * @event MWF.xApplication.process.Xform.Eltree#node-drag-start
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 拖拽进入其他节点时触发的事件。this.event[0]为被拖拽节点对应的 Node；
         * this.event[1]为所进入节点对应的 Node；this.event[2]为event
         * * @event MWF.xApplication.process.Xform.Eltree#node-drag-enter
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 拖拽离开某个节点时触发的事件。this.event[0]为被拖拽节点对应的 Node；
         * this.event[1]为所进入节点对应的 Node；this.event[2]为event
         * * @event MWF.xApplication.process.Xform.Eltree#node-drag-leave
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 在拖拽节点时触发的事件（类似浏览器的 mouseover 事件）	。this.event[0]为被拖拽节点对应的 Node；
         * this.event[1]为所进入节点对应的 Node；this.event[2]为event
         * * @event MWF.xApplication.process.Xform.Eltree#node-drag-over
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 拖拽结束时（可能未成功）触发的事件。this.event[0]为被拖拽节点对应的 Node；
         * this.event[1]为结束拖拽时最后进入的节点（可能为空）；
         * this.event[2]为被拖拽节点的放置位置（before、after、inner）
         * this.event[3]为event
         * @event MWF.xApplication.process.Xform.Eltree#node-drag-end
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        /**
         * 拖拽成功完成时触发的事件。this.event[0]为被拖拽节点对应的 Node；
         * this.event[1]为结束拖拽时最后进入的节点；
         * this.event[2]为被拖拽节点的放置位置（before、after、inner）
         * this.event[3]为event
         * @event MWF.xApplication.process.Xform.Eltree#node-drop
         * @see {@link https://element.eleme.cn/#/zh-CN/component/tree|树组件的Events章节}
         */
        "elEvents": ["node-click", "node-contextmenu", "check-change", "check", "current-change","node-expand",
            "node-collapse","node-drag-start","node-drag-enter","node-drag-leave","node-drag-over","node-drag-end","node-drop"]
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
        // if (this.isReadonly()) this.json.disabled = true;
        this._loadNodeEdit();
    },
    _appendVueData: function(){
        if (!this.json.emptyText) this.json.emptyText = "";
        // if (!this.json.renderAfterExpand) this.json.renderAfterExpand = true;
        if (!this.json.highlightCurrent) this.json.highlightCurrent = false;
        if (!this.json.defaultExpandAll) this.json.defaultExpandAll = false;
        // if (!this.json.expandOnClickNode) this.json.expandOnClickNode = true;
        if (!this.json.accordion) this.json.accordion = false;
        if (!this.json.indent) this.json.indent = 16;

        if (this.json.currentNodeKey && this.json.currentNodeKey.code){
            this.json.currentNodeKey = this.form.Macro.fire(this.json.currentNodeKey.code, this);
        }
        if( !this.json.defaultExpandAll && this.json.defaultExpandedKeys && this.json.defaultExpandedKeys.code ){
            this.json.defaultExpandedKeys = this.form.Macro.fire(this.json.defaultExpandedKeys.code, this);
        }
        if( this.json.showCheckbox && this.json.defaultCheckedKeys && this.json.defaultCheckedKeys.code ){
            this.json.defaultCheckedKeys = this.form.Macro.fire(this.json.defaultCheckedKeys.code, this);
        }
        // if( this.json.lazy && this.json.loadFun && this.json.loadFun.code  ){
        //     this.json.lazyLoadFun = function(node, resolve){
        //         return this.form.Macro.fire(this.json.loadFun.code, this, {
        //             node: node,
        //             resolve: resolve
        //         });
        //     }.bind(this)
        // }
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

        if (this.json.dataType === "script"){
            this.json.data = this.form.Macro.exec(((this.json.dataScript) ? this.json.dataScript.code : ""), this);
        }else{
            this.json.data = this.json.dataJson;
        }
        this.parseData();
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
        // if( this.json.lazy ){
        //     html += " :lazy=\"lazy\"";
        //     if( this.json.loadFun && this.json.loadFun.code ){
        //         html += " :load=\"lazyLoadFun\"";
        //     }
        // }
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
    },
     parseData: function ( data ) {
        if( !data )data = this.json.data;
        var config = {};
         //label和children转成defaultOptions的数组
         if( this.json.defaultProps ){
             var p = this.json.defaultProps;
             if( p.children !== "children" && o2.typeOf(p.children)==="string") config.children = p.children;
             if( p.label !== "label" && o2.typeOf(p.label)==="string" )config.label = p.label;
             if( p.disabled !== "disabled" && o2.typeOf(p.disabled)==="string")config.disabled = p.disabled;
             if( p.label !== "isLeaf" && o2.typeOf(p.isLeaf)==="string")config.isLeaf = p.isLeaf;
         }
        //把id转成成node-key,
        if( this.json.nodeKey && this.json.nodeKey !== "id" )config.id = this.json.nodeKey;
        if( Object.keys(config).length > 0 ){
            this._parseData(data, config);
        }
     },
     _parseData: function ( data, config ) {
         if( o2.typeOf(data) === "array" ){
             data.each(function(d){ this._parse(d, config) }.bind(this))
         }else{
             this._parse(data, config);
         }
     },
     _parse: function (data, config) {
         Object.each(config, function (value, key) {
             if( data[key] )data[value] = data[key];
         });
         var children = data[ config.children || "children" ];
         if(children && o2.typeOf(children)==="array" )this._parseData( children, config );
     }

}); 

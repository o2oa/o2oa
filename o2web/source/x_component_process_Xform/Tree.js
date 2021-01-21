MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/**树组件数据结构
 * @typedef {Object} TreeData
 * @example
 * [
 * {
 *    "expand": true, //是否默认展开
 *    "title": "", //鼠标移上叶子节点的文字
 *    "text": "根节点", //叶子节点的文字
 *    "action": "", //执行的脚本
 *    "default": true, //是否默认选中
 *    "icon": "folder.png", //图标
 *    "sub": [ //该节点的子节点
 *      {
 *        "expand": true,
 *        "title": "",
 *        "text": "[none]",
 *        "action": "",
 *        "default": false,
 *        "icon": "folder.png",
 *        "sub": []
 *      },
 *      ...
 *    ]
 *  }
 * ]
 */

/** @class Tree 树组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var datagrid = this.form.get("name"); //获取组件
 * //方法2
 * var datagrid = this.target; //在组件事件脚本中获取
 * @see {@link TreeData|树组件数据结构}
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Tree = MWF.APPTree =  new Class(
	/** @lends MWF.xApplication.process.Xform.Tree# */
{
	Extends: MWF.APP$Module,
	options: {
		/**
		 * 异步加载树前执行。this.target指向当前组件。
		 * @event MWF.xApplication.process.Xform.Tree#beforeLoadTree
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
		 */
		/**
		 * 异步加载树后执行。this.target指向当前组件。
		 * @event MWF.xApplication.process.Xform.Tree#afterLoadTree
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
		 */
		/**
		 * 加载树的叶子前执行。this.target指向加载的叶子。
		 * @event MWF.xApplication.process.Xform.Tree#beforeLoadTreeNode
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
		 */
		/**
		 * 加载树的叶子后执行。this.target指向加载的叶子。
		 * @event MWF.xApplication.process.Xform.Tree#afterLoadTreeNode
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
		 */
		/**
		 * 加载树的叶子后执行。this.target指向加载的叶子。
		 * @event MWF.xApplication.process.Xform.Tree#expand
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
		 */
		/**
		 * 折叠节点的时候执行。this.target指向被折叠的节点。
		 * @event MWF.xApplication.process.Xform.Tree#collapse
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
		 */
		/**
		 * 选中节点前执行。此时原来被选中的节点还未取消。this.target指向选中的节点。
		 * @event MWF.xApplication.process.Xform.Tree#beforeSelect
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
		 */
		/**
		 * 选中节点后执行。this.target指向选中的节点。
		 * @event MWF.xApplication.process.Xform.Tree#afterSelect
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
		 */
		"moduleEvents": ["load", "queryLoad", "postLoad", "beforeLoadTree", "afterLoadTree", "beforeLoadTreeNode", "afterLoadTreeNode", "expand", "collapse", "beforeSelect", "afterSelect"]
	},

	_loadUserInterface: function(){
		this.node.empty();

		MWF.require("MWF.widget.Tree", function(){
			var options = {"style":"form"};
			if( this.json.events && typeOf(this.json.events) === "object" ){
				[
					{ "beforeLoadTree" :  "onQueryLoad" },
					{ "afterLoadTree" :  "onPostLoad" },
					{ "beforeLoadTreeNode" :  "onBeforeLoadTreeNode" },
					{ "afterLoadTreeNode" :  "onAfterLoadTreeNode" },
					{ "expand" :  "onPostExpand" },
					{ "collapse" :  "onPostCollapse" },
					{ "beforeSelect" : "onBeforeSelect" },
					{ "afterSelect" : "onAfterSelect" }
				].each( function (obj) {
					var moduleEvent = Object.keys(obj)[0];
					var treeEvent = obj[moduleEvent];
					if( this.json.events[moduleEvent] && this.json.events[moduleEvent].code ){
						options[treeEvent] = function( target ){
							return this.form.Macro.fire(this.json.events[moduleEvent].code, target || this);
						}.bind(this)
					}
				}.bind(this));
			}

			/**
			 * @summary 树组件，平台使用该组件实现树的功能，该组件为异步加载
			 * @member {o2.widget.Tree}
			 * @example
			 *  //可以在脚本中获取该组件
			 * var tree = this.form.get("fieldId").tree; //获取组件对象
			 * var children = tree.children[]; //获取第一层树叶
			 * tree.reLoad( json ); //给整颗树重新赋数据，并重新加载
			 */
			this.tree = new MWF.widget.Tree(this.node, options);
			this.tree.form = this.form;

			this._setTreeWidgetStyles();


			var treeData = this.json.data;
			if (this.json.dataType == "script") treeData = this.form.Macro.exec(((this.json.dataScript) ? this.json.dataScript.code : ""), this);

			this.tree.load(treeData);
		}.bind(this));
	},
	_setTreeWidgetStyles: function(){
		this.tree.css.areaNode = this.json.areaNodeStyle;
		this.tree.css.treeItemNode = this.json.treeItemNodeStyle;
		this.tree.css.textDivNode = this.json.textDivNodeStyle;
		this.tree.css.textDivNodeSelected = this.json.textDivNodeSelectedStyle;
	}
}); 

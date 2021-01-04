MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.require("MWF.widget.Tab", null, false);
/** @class Tab 分页组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var tab = this.form.get("fieldId"); //获取组件
 * //方法2
 * var tab = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Tab = MWF.APPTab =  new Class(
    /** @lends MWF.xApplication.process.Xform.Tab# */
{
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
        this.elements = [];
        this.containers = [];

        var style = "form";
        if (layout.mobile) style = "mobileForm";

        /**
         * @summary tab组件，平台使用该组件实现分页组件的功能
         * @member {MWF.widget.Tab}
         * @example
         *  //可以在脚本中获取该组件
         * var tab = this.form.get("fieldId").tab; //获取组件对象
         * var pages = tab.pages //获取每个分页
         * pages[1].addEvent("queryShow", function(){
         *     //添加显示分页前事件
         * })
         * pages[1].addEvent("postShow", function(){
         *     //添加显示分页后事件
         * })
         * pages[1]._showTab(); //显示第2个分页
         */
		this.tab = new MWF.widget.Tab(this.node, {"style": style});
		
		this._setTabWidgetStyles();

		this.tab.tabNodeContainer = this.node.getFirst("div");
		this.tab.contentNodeContainer = this.tab.tabNodeContainer.getNext("div");

		var lastNode = this.tab.tabNodeContainer.getLast();
		var tabs;
		if (lastNode && lastNode.hasClass("tabNodeContainerLeft")){
            this.tab.tabNodeContainerRight = this.tab.tabNodeContainer.getFirst();
            this.tab.tabNodeContainerLeft = lastNode;
            this.tab.tabNodeContainerArea = lastNode.getFirst();

            var menuNode = this.node.getElement(".MWFMenu");
            if (menuNode) menuNode.destroy();
            this.tab.load();
            tabs = this.tab.tabNodeContainerArea.getChildren("div");
        }else{
            tabs = this.tab.tabNodeContainer.getChildren("div");
            this.tab.load();
        }

		var contents = this.tab.contentNodeContainer.getChildren("div");
		tabs.each(function(tab, idx){
			this.tab.rebuildTab(contents[idx], contents[idx].getFirst(), tab);
		}.bind(this));
		
		this.tab.pages[0]._showTab();
        this.loadSubModule();
	},
    loadSubModule: function(){
        this.tab.pages.each(function(page){
            var node = page.tabNode;
            var json = this.form._getDomjson(node);
            var tab = this;
            var module = this.form._loadModule(json, node, function(){
                this.tab = tab;
            });
            this.elements.push(module);
            this.form.modules.push(module);

            if (page.isShow){
                this.showContentModule.call(page, this);
            }else{
                if (this.json.isDelay){
                    var _self = this;
                    page.showContentModuleFun = function(){_self.showContentModule.call(page, _self)};
                    page.addEvent("show", page.showContentModuleFun);
                }else{
                    this.showContentModule.call(page, this);
                }
            }
        }.bind(this));
    },
    showContentModule: function(_self){
        var node = this.contentNode;
        node.isLoadModule = true;
        json = _self.form._getDomjson(node);
        tab = _self;
        module = _self.form._loadModule(json, node, function(){
            this.tab = tab;
        });
        _self.containers.push(module);
        _self.form.modules.push(module);

        if (this.showContentModuleFun) this.removeEvent("show", this.showContentModuleFun);
    },

	_setTabWidgetStyles: function(){
        if (this.json.tabNodeContainer) this.tab.css.tabNodeContainer = Object.clone(this.json.tabNodeContainer);
        if (this.json.contentNodeContainer) this.tab.css.contentNodeContainer = Object.clone(this.json.contentNodeContainer);
		this.tab.css.tabNode = Object.clone(this.json.tabStyles);
		this.tab.css.tabTextNode = Object.clone(this.json.tabTextStyles);
		this.tab.css.tabNodeCurrent = Object.clone(this.json.tabCurrentStyles);
		this.tab.css.tabTextNodeCurrent = Object.clone(this.json.tabTextCurrentStyles);
	}
});
MWF.xApplication.process.Xform.tab$Page = MWF.APPTab$Page =  new Class({
    Extends: MWF.APP$Module
});
MWF.xApplication.process.Xform.tab$Content = MWF.APPTab$Content =  new Class({
    Extends: MWF.APP$Module,
    _loadUserInterface: function(){
        this.form._loadModules(this.node);
    }
});

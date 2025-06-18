MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.require("MWF.widget.Tab", null, false);
//MWF.xDesktop.requireApp("Selector", "lp."+MWF.language, null, false);
//MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);
if(!MWF.O2Selector)MWF.O2Selector = {};
MWF.xApplication.Selector.MultipleSelector = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "types" : [],
        "count": 0,
        "title": "",
        "groups": [], //选person, group, role 时的范围
        "roles": [], //选选person, group, role 时的范围
        "units": [], //选 company, department, duty, identity 时的范围
        "values" : [],

        "zIndex": 1000,
        "expand": true,

        "contentUrl" : "", //和默认的页面布局不一样的话，可以传入页面布局HTML URL
        "injectToBody" : false //当传入HTML URL的时候是否插入到document.body, false的时候插入到this.container
    },
    initialize: function(container, options){
        this.active = true;
        if (!options.title)  options.title = MWF.xApplication.Selector.LP.multiSelectTitle;
        this.setOptions(options);

        this.optionsArg = options;

        this.path = "../x_component_Selector/$Selector/";
        this.cssPath = "../x_component_Selector/$Selector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);

        if( this.options.style.endsWith("flow")  ){
            this.options.contentUrl = this.path + this.options.style + "/"+( this.options.embedded ? "selector_embedded":"selector" )+".html";
            this.options.level1Indent = 0;
            this.options.indent = 10;
            this.options.tabStyle = "blue_flat";
        }else if(this.options.useO2Load && this.options.embedded ){
            this.options.contentUrl = this.options.contentUrl.replace('selector.html', 'selector_embedded.html');
        }

        this.lp = MWF.xApplication.Selector.LP;

        this.lastPeople = "";
        this.pageCount = "13";
        this.selectedItems = [];
        this.selectedItemsObject = {};
        this.items = [];
        this.selectors = {};
    },
    load: function(){
        var ps = [
            this.loadItemHtml(),
            this.loadCategoryHtml()
        ];
        !!this.options.contentUrl && ps.push( this.loadWithUrl());
        Promise.all(ps).then(function(){
            if( this.options.contentUrl ){
                this.loadContentWithHTML();
            }else{
                layout.mobile ? this.loadMobile() : this.loadPc();
            }
            this.fireEvent("load");
        }.bind(this));
    },
    loadWithUrl : function(){
        return new Promise(function(resolve, reject){
            var request = new Request.HTML({
                url: this.options.contentUrl,
                method: "GET",
                async: false,
                onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
                    if( this.options.style.endsWith("flow") || this.options.useO2Load ){
                        this.nodeHTML = responseHTML;
                        var node = new Element("div");
                        node.loadHtmlText( responseHTML, {
                            "bind": { "lp": MWF.xApplication.Selector.LP, "options": this.options, "_selectType": this.selectType },
                            "module": this
                        });
                        this.node = node.getFirst();
                        this.node.loadCss("../x_component_Selector/$Selector/"+this.options.style+"/style.css");
                    }else{
                        this.node = responseTree[0];
                    }
                    if(resolve)resolve();
                }.bind(this),
                onFailure: function(xhr){
                    alert(xhr);
                }
            });
            request.send();
        }.bind(this));
    },
    loadContentWithHTML : function(){
        (this.wrapNode || this.content).addClass('multiple_mode');
        var container = this.options.injectToBody ? $(document.body) : this.container;
        if( !this.options.embedded ){
            this.css.maskNode["z-index"] = this.options.zIndex;
            this.maskRelativeNode = container;
            this.maskRelativeNode.mask({
                "destroyOnHide": true,
                "style": this.css.maskNode
            });
            if( this.options.style === 'v10_mobile' ){
                this.maskRelativeNode.get('mask').addEvent('click', function () {
                    debugger;
                    if( this.selectedMode ){
                        this.switchSelectedMode();
                    }else{
                        this.close();
                    }
                }.bind(this));
            }
        }

        if( !this.options.embedded ) {
            this.node.setStyles( layout.mobile ? this.css.containerNodeMobile : this.css.containerNode );
            this.node.setStyle("z-index", this.options.zIndex.toInt() + 1);
        }
        if( layout.mobile ){
            if( this.options.style !== 'v10_mobile' ){
                this.node.setStyle("height", ( container.getSize().y ) + "px");
            }else {
                window.setTimeout(function () {
                    this.node.setStyles(this.css.containerNodeMobile_show);
                }.bind(this), 1)
            }
        }

        if( !this.options.useO2Load ){
            this._setElements();
        }

        this.node.inject( container );

        var size;
        if( this.options.injectToBody ){
            size = $(document.body).getSize();
        }else if( layout.mobile ){
            if( this.options.style !== 'v10_mobile' ) {

                var containerSize = this.container.getSize();
                var bodySize = $(document.body).getSize();
                if (containerSize.y === 0) {
                    containerSize.y = bodySize.y
                }

                size = {
                    "x": Math.min(containerSize.x, bodySize.x),
                    "y": Math.min(containerSize.y, bodySize.y)
                };

                var zoom = this.node.getStyle("zoom").toInt();
                zoom = zoom ? (zoom * 100) : 0;
                if (zoom) {
                    size.x = size.x * 100 / zoom;
                    size.y = size.y * 100 / zoom;
                }
                this.node.setStyles({
                    "width": size.x + "px",
                    "height": size.y + "px"
                });
            }else{
                window.setTimeout(function () {
                    this.node.setStyles(this.css.containerNodeMobile_show);
                }.bind(this), 1)
            }
        }else{
            if( this.options.width || this.options.height ){
                this.setSize()
            }
            if (this.options.style !== "v10"){
                this.node.position({
                    relativeTo: this.container,
                    position: "center",
                    edge: "center"
                });
            }

            size = this.container.getSize();
            var nodeSize = this.node.getSize();
            this.node.makeDraggable({
                "handle": this.titleNode,
                "limit": {
                    "x": [0, size.x - nodeSize.x],
                    "y": [0, size.y - nodeSize.y]
                }
            });
        }

        if( !this.options.useO2Load ){
            var height = this.node.getSize().y-this.getOffsetY( this.contentNode );
            if( this.tabContainer ){
                height = height - this.getOffsetY( this.tabContainer ) - ( this.tabContainer.getStyle("height").toInt() || 0 )
            }
            if( this.titleNode ){
                height = height - this.getOffsetY( this.titleNode ) - ( this.titleNode.getStyle("height").toInt() || 0 )
            }
            if( this.actionNode ){
                height = height - this.getOffsetY( this.actionNode ) - ( this.actionNode.getStyle("height").toInt() || 0 )
            }
            this.contentNode.setStyle("height", ""+height+"px");

            this.selectNode.setStyle("height", ""+height+"px");

            this.contentHeight = height;
        }

        this.contentHTML = this.options.useO2Load ? this.getContentHtml() : this.contentNode.get("html");

        this.contentNode.empty();


        this.loadContent();
        if( this.actionNode ){
            this.loadAction();
        }

        if( !this.options.embedded && layout.mobile ){
            if( this.options.style !== 'v10_mobile' ){
                this.node.setStyles({
                    "top": "0px",
                    "left": "0px"
                });
            }
        }

        this.setEvent();
    },
    getContentHtml: function() {
        var parser = new DOMParser();
        var doc = parser.parseFromString(this.nodeHTML, 'text/html');
        var node = doc.querySelector('[data-o2-element="contentNode"]');
        return node.innerHTML;
    },
    _setElements: function (){
        this.titleNode = this.node.getElement(".MWF_selector_titleNode");
        this.titleTextNode = this.node.getElement(".MWF_selector_titleTextNode");
        this.titleCancelActionNode = this.node.getElement(".MWF_selector_titleCancelActionNode");
        this.titleOkActionNode = this.node.getElement(".MWF_selector_titleOkActionNode");

        this.titleActionNode = this.node.getElement(".MWF_selector_titleActionNode");

        this.tabContainer = this.node.getElement(".MWF_selector_tabContainer");
        this.tabContainer.show();

        this.contentNode = this.node.getElement(".MWF_selector_contentNode");

        this.selectNode = this.node.getElement(".MWF_selector_selectNode");
        this.selectTopNode = this.node.getElement(".MWF_selector_selectTopNode");
        this.selectTopTextNode = this.node.getElement(".MWF_selector_selectTopTextNode");
        this.searchInputDiv = this.node.getElement(".MWF_selector_searchInputDiv");
        this.searchInput = this.node.getElement(".MWF_selector_searchInput");
        this.searchCancelAction = this.node.getElement(".MWF_selector_searchCancelAction");
        this.letterActionNode = this.node.getElement(".MWF_selector_letterActionNode");

        this.flatCategoryScrollNode = this.node.getElement(".MWF_selector_flatCategoryScrollNode");
        this.flatCategoryNode = this.node.getElement(".MWF_selector_flatCategoryNode");

        this.letterAreaNode = this.node.getElement(".MWF_selector_letterAreaNode");

        this.itemAreaScrollNode = this.node.getElement(".MWF_selector_itemAreaScrollNode");
        this.itemAreaNode = this.node.getElement(".MWF_selector_itemAreaNode");

        this.itemSearchAreaScrollNode = this.node.getElement(".MWF_selector_itemSearchAreaScrollNode");
        this.itemSearchAreaNode = this.node.getElement(".MWF_selector_itemSearchAreaNode");

        this.selectedContainerNode = this.node.getElement(".MWF_selector_selectedContainerNode");

        this.selectedTopNode = this.node.getElement(".MWF_selector_selectedTopNode");
        this.selectedTopTextNode = this.node.getElement(".MWF_selector_selectedTopTextNode");
        this.emptySelectedNode = this.node.getElement(".MWF_selector_emptySelectedNode");

        this.selectedScrollNode = this.node.getElement(".MWF_selector_selectedScrollNode");
        this.selectedNode = this.node.getElement(".MWF_selector_selectedNode");
        this.selectedItemSearchAreaNode = this.node.getElement(".MWF_selector_selectedItemSearchAreaNode");

        this.actionNode = this.node.getElement(".MWF_selector_actionNode");
        this.okActionNode = this.node.getElement(".MWF_selector_okActionNode");
        this.cancelActionNode = this.node.getElement(".MWF_selector_cancelActionNode");

        if (this.titleNode) this.titleNode.setStyles( layout.mobile ? this.css.titleNodeMobile : this.css.titleNode );
        if (this.titleTextNode){
            this.titleTextNode.setStyles(layout.mobile ? this.css.titleTextNodeMobile : this.css.titleTextNode);
            if(this.options.title)this.titleTextNode.set("text", this.options.title);
        }
        if (this.titleActionNode)this.titleActionNode.setStyles(this.css.titleActionNode);
        if (this.titleCancelActionNode) this.titleCancelActionNode.setStyles(this.css.titleCancelActionNodeMobile);
        if (this.titleOkActionNode) this.titleOkActionNode.setStyles(this.css.titleOkActionNodeMobile);

        if (this.tabContainer) this.tabContainer.setStyles(this.css.tabContainer);

        if (this.contentNode) this.contentNode.setStyles(this.css.contentNode);

        if (this.selectNode) this.selectNode.setStyles( layout.mobile ? this.css.selectNodeMobile : this.css.selectNode);
        if (this.selectTopNode)this.selectTopNode.setStyles(this.css.selectTopNode);
        if (this.selectTopTextNode)this.selectTopTextNode.setStyles(this.css.selectTopTextNode);
        if (this.searchInputDiv) this.searchInputDiv.setStyles(this.css.searchInputDiv);
        if (this.searchInput) this.searchInput.setStyles( (this.options.count.toInt()===1) ? this.css.searchInputSingle : this.css.searchInput );
        if (this.searchCancelAction) this.searchCancelAction.setStyles(this.css.searchCancelAction);
        if (this.letterActionNode) this.letterActionNode.setStyles(this.css.letterActionNode);
        if (this.letterAreaNode) this.letterAreaNode.setStyles(this.css.letterAreaNode);
        if (this.itemAreaScrollNode) this.itemAreaScrollNode.setStyles(this.css.itemAreaScrollNode);
        if (this.itemAreaNode) this.itemAreaNode.setStyles(this.css.itemAreaNode);

        if (this.itemSearchAreaScrollNode) this.itemSearchAreaScrollNode.setStyles(this.css.itemSearchAreaScrollNode);
        if (this.itemSearchAreaNode) this.itemSearchAreaNode.setStyles(this.css.itemAreaNode);

        if (this.selectedContainerNode)this.selectedContainerNode.setStyles(this.css.selectedContainerNode);
        if (this.selectedTopNode)this.selectedTopNode.setStyles(this.css.selectedTopNode);
        if (this.selectedTopTextNode)this.selectedTopTextNode.setStyles(this.css.selectedTopTextNode);
        if (this.emptySelectedNode)this.emptySelectedNode.setStyles(this.css.selectedTopActionNode);

        if (this.selectedScrollNode) this.selectedScrollNode.setStyles(this.css.selectedScrollNode);
        if (this.selectedNode) this.selectedNode.setStyles(this.css.selectedNode);
        if (this.selectedItemSearchAreaNode) this.selectedItemSearchAreaNode.setStyles(this.css.itemAreaNode);

        if (this.actionNode) this.actionNode.setStyles(this.css.actionNode);
        if (this.okActionNode) {
            this.okActionNode.setStyles(this.css.okActionNode);
            this.okActionNode.set("text", MWF.SelectorLP.ok);
        }
        if (this.cancelActionNode) {
            this.cancelActionNode.setStyles(this.css.cancelActionNode);
            this.cancelActionNode.set("text", MWF.SelectorLP.cancel);
        }
    },
    loadMobile: function(){
        this.maskRelativeNode = $(document.body);
        this.maskRelativeNode.mask({
            "destroyOnHide": true,
            "style": this.css.maskNode
        });
        this.node = new Element("div", {"styles": this.css.containerNodeMobile});
        this.node.setStyle("z-index", this.options.zIndex.toInt()+1);
        this.node.setStyle("height", ( $(document.body).getSize().y ) + "px");
        this.titleNode = new Element("div", {
            "styles": this.css.titleNodeMobile
        }).inject(this.node);

        this.titleCancelActionNode = new Element("div", {
            "styles": this.css.titleCancelActionNodeMobile,
            "text": MWF.SelectorLP.back
        }).inject(this.titleNode);
        this.titleOkActionNode = new Element("div", {
            "styles": this.css.titleOkActionNodeMobile,
            "text": MWF.SelectorLP.ok
        }).inject(this.titleNode);

        this.titleTextNode = new Element("div", {
            "styles": {
                "margin": "0px 50px",
                "height": "40px",
                "padding": "0px 10px",
                "color": "#FFF",
                "font-weight": "bold",
                "font-size": "14px",
                "line-height": "40px"
                //"overflow" : "hidden"
            }
            //"text": this.options.title
        }).inject(this.titleNode);

        this.contentNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.node);

        this.loadSelectedNodeMobile();

        this.loadSelectedCountNode();

        this.node.inject($(document.body));

        var size = $(document.body).getSize();
        //var height = size.y-40;
        var height = size.y - this.selectedCountNode.getSize().y - this.getOffsetY(this.selectedCountNode);
        height = height - this.titleNode.getSize().y - this.getOffsetY(this.titleNode);
        this.contentNode.setStyle("height", ""+height+"px");
        this.contentNode.setStyle("margin-top", "2px");

        this.loadContent();
        this.node.setStyles({
            "top": "0px",
            "left": "0px"
        });

        this.setEvent();
    },
    loadSelectedNodeMobile: function(){
        this.css.selectedWrapNodeMobile["z-index"] = this.options.zIndex + 2;
        this.selectedWrapNode = new Element("div.selectedWrapNode", {
            "styles": this.css.selectedWrapNodeMobile
        }).inject(this.contentNode);

        this.selectedTitleNode = new Element("div.selectedTitleNodeMobile", {
            "styles": this.css.selectedTitleNodeMobile
        }).inject(this.selectedWrapNode);
        this.selectedTitleLabelNode = new Element("span", {
            "style": "font-weight:bold; padding-right:5px;",
            "text": MWF.SelectorLP.selected2
        }).inject(this.selectedTitleNode);
        // this.selectedTitleCountNode = new Element("span", {
        //     "text": "(0)"
        // }).inject(this.selectedTitleNode);


        this.selectedScrollNode = new Element("div.selectedScrollNode", {
            "styles": this.css.selectedScrollNodeMobile
        }).inject(this.selectedWrapNode);


        // this.selectedNode = new Element("div.selectedNode", {
        //     "styles": this.css.selectedNodeMobile
        // }).inject(this.selectedScrollNode);

        this.selectedWrapNode.setStyle("display", "none");
    },
    loadSelectedCountNode: function(){
        this.selectedCountNode = new Element("div.selectedCountNode", {
            "styles": this.css.selectedCountNodeMobile,
            "events":{
                "click": function () {
                    this.css.selectedMaskNodeMobile["z-index"] = this.options.zIndex + 2;
                    this.selectedMaskNode = new Element("div", {
                        "styles": this.css.selectedMaskNodeMobile,
                        "events":{
                            "click": function () {
                                this.selectedWrapNode.hide();
                                this.selectedMaskNode.destroy();
                            }.bind(this)
                        }
                    }).inject( this.node );
                    this.selectedWrapNode.show().inject( this.node );
                }.bind(this)
            }
        }).inject(this.node);

        this.selectedCountLabelNode = new Element("div", {
            "styles": this.css.selectedCountLabelNodeMobile,
            "text": MWF.SelectorLP.selected2
        }).inject( this.selectedCountNode );
    },
    loadPc: function(){
        this.css.maskNode["z-index"] = this.options.zIndex;
        var position = this.container.getPosition(this.container.getOffsetParent());
        this.container.mask({
            "destroyOnHide": true,
            "style": this.css.maskNode,
            "useIframeShim": true,
            "iframeShimOptions": {"browsers": true},
            "onShow": function(){
                this.shim.shim.setStyles({
                    "opacity": 0,
                    "top": ""+position.y+"px",
                    "left": ""+position.x+"px"
                });
            }
        });
        //  this.container.setStyle("z-index", this.options.zIndex);
        this.node = new Element("div", {
            "styles": this.css.containerNode_multiple //this.isSingle() ? this.css.containerNodeSingle_multiple : this.css.containerNode_multiple
        });
        this.node.setStyle("z-index", this.options.zIndex.toInt()+1);
        this.titleNode = new Element("div", {
            "styles": this.css.titleNode
        }).inject(this.node);

        this.titleActionNode = new Element("div", {
            "styles": this.css.titleActionNode
        }).inject(this.titleNode);
        this.titleTextNode = new Element("div", {
            "styles": this.css.titleTextNode,
            "text": this.options.title
        }).inject(this.titleNode);

        this.contentNode = new Element("div", {
            "styles": this.css.contentNode_multiple || this.css.contentNode
        }).inject(this.node);

        this.actionNode = new Element("div", {
            "styles": this.css.actionNode
        }).inject(this.node);
        //if ( this.isSingle() ) this.actionNode.setStyle("text-align", "center");
        this.loadAction();

        this.node.inject(this.container);

        if( this.options.width || this.options.height ){
            this.setSize();
        }

        this.loadContent();

        if (this.options.style !== "v10"){
            this.node.position({
                relativeTo: this.container,
                position: "center",
                edge: "center"
            });
        }

        var size = this.container.getSize();
        var nodeSize = this.node.getSize();
        this.node.makeDraggable({
            "handle": this.titleNode,
            "limit": {
                "x": [0, size.x-nodeSize.x],
                "y": [0, size.y-nodeSize.y]
            }
        });

        this.setEvent();
    },
    isSingle : function(){
        var single = true;
        var flag = true;
        this.options.types.each( function( type, index ){
            var opt = this.options[ type + "Options" ];
            if( opt ){
                if( Number.convert(opt.count) !== 1 )single = false;
                flag = false;
            }
        }.bind(this));
        if( flag ){
            single = Number.convert( this.options.count ) === 1;
        }
        return single;
    },
    setEvent: function(){
        if (this.titleActionNode){
            this.titleActionNode.addEvent("click", function(){
                this.close();
            }.bind(this));
        }
        if (this.titleCancelActionNode){
            this.titleCancelActionNode.addEvent("click", function(){
                this.close();
            }.bind(this));
        }
        if (this.titleOkActionNode){
            this.titleOkActionNode.addEvent("click", function(){
                this.fireEvent("complete", [this.getSelectedItems(), this.getSelectedItemsObject() ]);
                this.close();
            }.bind(this));
        }
    },
    close: function (){
        if( this.options.style === 'v10_mobile' ){
            this.node.setStyles(this.css.containerNodeMobile_hide);
            window.setTimeout(function () {
                this._close()
            }.bind(this), 200)
        }else{
            this._close();
        }
    },
    _close: function(){
        this.fireEvent("close");
        this.clearTooltip();
        this.node.destroy();
        (this.maskRelativeNode || this.container).unmask();
        this.active = false;
        MWF.release(this);
        delete this;
    },
    clearTooltip: function(){
        for( var t in this.selectors ){
            var selector = this.selectors[t];
            if( selector.tooltips && selector.tooltips.length ){
                selector.tooltips.each(function (tooltip) {
                    if(tooltip.destroy)tooltip.destroy();
                })
                selector.tooltips = [];
            }
        }
    },
    switchSelectedMode: function (){
        var node = this.wrapNode || this.contentNode;
        !!this.selectedMode ? node.removeClass('selected_mode') : node.addClass('selected_mode');
        this.selectedMode = !this.selectedMode;
    },
    loadAction: function(){
        if( !this.okActionNode ) {
            this.okActionNode = new Element("button", {
                "styles": this.css.okActionNode,
                "text": MWF.SelectorLP.ok
            }).inject(this.actionNode);
        }
        if( !this.cancelActionNode ) {
            this.cancelActionNode = new Element("button", {
                "styles": this.css.cancelActionNode,
                "text": MWF.SelectorLP.cancel
            }).inject(this.actionNode);
        }
        this.okActionNode.addEvent("click", function(){
            this.fireEvent("complete", [this.getSelectedItems(), this.getSelectedItemsObject() ]);
            this.close();
        }.bind(this));
        this.cancelActionNode.addEvent("click", function(){this.fireEvent("cancel", this); this.close();}.bind(this));
    },
    _setContentElement: function(selector, pageNode){
        pageNode.set("html", this.contentHTML);
        pageNode.setStyle("height", this.contentHeight);
        selector.selectNode = pageNode.getElement(".MWF_selector_selectNode");
        selector.selectTopNode = pageNode.getElement(".MWF_selector_selectTopNode");
        selector.selectTopTextNode = pageNode.getElement(".MWF_selector_selectTopTextNode");
        selector.searchInputDiv = pageNode.getElement(".MWF_selector_searchInputDiv");
        selector.searchInput = pageNode.getElement(".MWF_selector_searchInput");
        selector.searchCancelAction = pageNode.getElement(".MWF_selector_searchCancelAction");
        selector.letterActionNode = pageNode.getElement(".MWF_selector_letterActionNode");

        selector.flatCategoryScrollNode = pageNode.getElement(".MWF_selector_flatCategoryScrollNode");
        selector.flatCategoryNode = pageNode.getElement(".MWF_selector_flatCategoryNode");
        if( this.options.flatCategory && selector.flatCategoryScrollNode ){
            selector.isFlatCategory = true;
            selector.flatSubCategoryNodeList = [];
        }

        selector.letterAreaNode = pageNode.getElement(".MWF_selector_letterAreaNode");

        selector.itemAreaScrollNode = pageNode.getElement(".MWF_selector_itemAreaScrollNode");
        selector.itemAreaNode = pageNode.getElement(".MWF_selector_itemAreaNode");

        selector.itemSearchAreaScrollNode = pageNode.getElement(".MWF_selector_itemSearchAreaScrollNode");
        selector.itemSearchAreaNode = pageNode.getElement(".MWF_selector_itemSearchAreaNode");

        selector.selectedContainerNode = pageNode.getElement(".MWF_selector_selectedContainerNode");

        selector.selectedTopNode = pageNode.getElement(".MWF_selector_selectedTopNode");
        selector.selectedTopTextNode = pageNode.getElement(".MWF_selector_selectedTopTextNode");
        selector.emptySelectedNode = pageNode.getElement(".MWF_selector_emptySelectedNode");

        selector.selectedScrollNode = pageNode.getElement(".MWF_selector_selectedScrollNode");
        selector.selectedNode = pageNode.getElement(".MWF_selector_selectedNode");
        selector.selectedItemSearchAreaNode = pageNode.getElement(".MWF_selector_selectedItemSearchAreaNode");
    },
    _setTabWcssWidth: function(){
        var width, tabWidth;
        if( this.tabContainer ){
            var borderWidth = 0;
            if( this.tab.css.tabNode ){
                if( this.tab.css.tabNode["border-left"] )borderWidth += this.tab.css.tabNode["border-left"].toInt();
                if( this.tab.css.tabNode["border-right"] )borderWidth += this.tab.css.tabNode["border-right"].toInt();
            }

            tabWidth = "calc("+( 100 / this.options.types.length ) +"% - " + (borderWidth+"px")+")";

            if( this.tab.css.tabNode ){
                this.tab.css.tabNode["width"] = tabWidth;
            }
            if( this.tab.css.tabNodeCurrent ){
                this.tab.css.tabNodeCurrent["width"] = tabWidth;
            }
        }else{
            width = this.container.getSize().x - 160; //160是确定和返回按钮的宽度
            var w = width / this.options.types.length - 2;
            tabWidth = w < 60 ? w : 60;
            if( this.tab.css.tabNode ){
                this.tab.css.tabNode["min-width"] = tabWidth+"px";
            }
            if( this.tab.css.tabNodeCurrent ){
                this.tab.css.tabNodeCurrent["min-width"] = tabWidth+"px";
            }
        }
    },
    _setTouchEvents: function (type, selector) {
        if( !layout.mobile )return;
        if( ["person", "group"].contains(type.toLowerCase()) ){
            var startY=0, y=0;
            var itemAreaScrollNode = selector.itemAreaScrollNode;
            itemAreaScrollNode.addEvents({
                'touchstart' : function( ev ){
                    var touch = ev.touches[0]; //获取第一个触点
                    startY = Number(touch.pageY); //页面触点Y坐标
                }.bind(this),
                'touchmove' : function(ev){
                    var touch = ev.touches[0]; //获取第一个触点
                    y = Number(touch.pageY); //页面触点Y坐标
                }.bind(this),
                'touchend' : function( ev ){
                    if (startY - y > 10) { //向上滑动超过10像素
                        if(selector._scrollEvent){
                            selector._scrollEvent( itemAreaScrollNode.scrollTop + 100 );
                        }
                    }
                    startY = 0;
                    y = 0;
                }.bind(this)
            })
        }
    },
    loadContent: function(){
        if( this.options.contentUrl ){
            this.tab = new MWF.widget.Tab( this.tabContainer || this.titleTextNode, {"style": this.options.tabStyle || "default" });
            if( layout.mobile && !this.options.useO2Load ){
                this._setTabWcssWidth();
            }
            this.tab.load();
            this.tab.contentNodeContainer.inject(this.contentNode);
        }else if (layout.mobile){
            this.tab = new MWF.widget.Tab( this.tabContainer || this.titleTextNode, {"style": "orgMobile" });
            this._setTabWcssWidth();
            this.tab.load();
            this.tab.contentNodeContainer.inject(this.contentNode);
        }else{
            this.tab = new MWF.widget.Tab(this.contentNode, {"style": this.options.tabStyle || "default" });
            this.tab.load();
        }

        if( !MWF.O2Selector.selectedIndex )MWF.O2Selector.selectedIndex = 1;
        var selectedIndexMap = {};
        var firstType = this.options.types[0];
        var values = this.options[firstType+'Options'] ? this.options[firstType+'Options'].values : [];
        if(!values)values = [];
        values = typeOf(values) === "array" ?  values : [values];
        values.each(function(e, i){
            if( !e )return;
            var key = typeOf( e ) === "string" ? e : ( e.distinguishedName || e.unique || e.employee || e.levelName || e.id );
            selectedIndexMap[key] = MWF.O2Selector.selectedIndex++;
        });

        var lastSelectedNode, lastselectedCountTextNode;
        this.options.types.each( function( type, index ){

            var t = type.capitalize(), lt = type.toLowerCase();

            var options = Object.clone( this.options );

            for (var key in this.optionsArg){
                if (typeOf(this.optionsArg[key]) === 'function' && (/^on[A-Z]/).test(key)){
                    options[key] = this.optionsArg[key];
                }
            }

            if( lt ==="identity" ){
                options.expand = false;
            }

            if( this.options[ type + "Options" ] ){
                options = Object.merge( options, this.options[ type + "Options" ] );
            }


            if ( lt==="unit" && options.unitType ){
                t = "UnitWithType";
            }
            if ( lt==="identity" && options.dutys && options.dutys.length ){
                t = options.categoryType.toLowerCase()==="duty" ? "IdentityWidthDuty" : "IdentityWidthDutyCategoryByUnit";
            }

            options.values = this.getValueByType(
                options.values,
                (lt === "identity" && options.resultType === "person") ? [ type, "person" ] : lt
            );

            var pageNode = new Element( "div" ).inject( this.contentNode );
            if( this.options.style === 'v10' ){
                pageNode.setStyles(this.css.contentNode);
            }

            var tab = this.tab.addTab( pageNode, this.lp[type], false );

            if( index === 0 && this.contentHeight ){
                if( !this.tabContainer ){
                    this.contentHeight = this.contentHeight - this.getOffsetY( tab.tab.tabNodeContainer ) - tab.tab.tabNodeContainer.getStyle("height").toInt();
                }
                if( this.selectedCountNode ){
                    this.contentHeight = this.contentHeight - this.getOffsetY( this.selectedCountNode ) - this.selectedCountNode.getStyle("height").toInt();
                }
            }

            MWF.xDesktop.requireApp("Selector", t, function(){
                this.selectors[t] = new MWF.xApplication.Selector[t](this.container, options );
                var selector = this.selectors[t];
                selector.selectedIndexMap = selectedIndexMap;
                selector.inMulitple = true;
                tab.selector = selector;

                if( this.options.contentUrl ){
                    if( this.options.useO2Load ){
                        if(layout.mobile)selector.overrideSelectedItems();
                        selector.contentNode = pageNode;

                        //如果已选在content节点中，说明每种类型都有自己的已选节点
                        var selectedNode = this.contentNode.querySelector('[data-o2-element="selectedNode"]');
                        if( !selectedNode && this.selectedNode){
                            //否则在当前节点中获取
                            selector.selectedNode = this.selectedNode.clone().inject(lastSelectedNode || this.selectedNode, 'after');
                            lastSelectedNode = selector.selectedNode;
                        }

                        //如果有已选数量在content节点中，说明每种类型都有自己的已选节点
                        var selectedCountTextNode = this.contentNode.querySelector('[data-o2-element="selectedCountTextNode"]');
                        if( !selectedCountTextNode && this.selectedCountTextNode){
                            //否则在当前节点中获取
                            this.selectedCountTextNode.hide();
                            selector.selectedCountTextNode = this.selectedCountTextNode.clone().show().
                            inject(lastselectedCountTextNode || this.selectedCountTextNode, 'after');
                            // selector.selectedCountTextNode.textContent = (MWF.SelectorLP.quantifier[ selector.selectType ] || "") + ":0";
                            lastselectedCountTextNode = selector.selectedCountTextNode;
                        }

                        selector.o2loadInMultiple(pageNode, this.contentHTML, function (){
                            selector.loadContent( pageNode, true );
                            this._setTouchEvents(type, selector);
                        }.bind(this));
                    }else{
                        this._setContentElement(selector, pageNode);
                        selector.loadContent( pageNode, true );
                        this._setTouchEvents(type, selector);
                    }


                }else{
                    // if( this.contentWidth )options.width = this.contentWidth;
                    // if( this.contentHeight )options.height = this.contentHeight;

                    if( this.contentWidth )selector.options.width = this.contentWidth;
                    if( this.contentHeight )selector.options.height = this.contentHeight;

                    if( layout.mobile ){
                        selector.overrideSelectedItems();

                        selector.selectedCountTextNode = new Element("div", {
                            "styles": this.css.selectedCountTextNodeMobile,
                            "text": (MWF.SelectorLP.quantifier[ selector.selectType ] || "") + ":0"
                        }).inject( this.selectedCountNode );

                        if( index === 0 ){
                            new Element("span", { "text": "(" }).inject(this.selectedTitleNode);
                            selector.selectedTitleCountNode = new Element("span", {
                                "style": "padding-right:5px;",
                                "text": (MWF.SelectorLP.quantifier[ selector.selectType ] || "") + ":0"
                            }).inject(this.selectedTitleNode);
                        }else if( index === this.options.types.length - 1 ){
                            selector.selectedTitleCountNode = new Element("span", {
                                "text": (MWF.SelectorLP.quantifier[ selector.selectType ] || "") + ":0"
                            }).inject(this.selectedTitleNode);
                            new Element("span", { "text": ")" }).inject(this.selectedTitleNode);
                        }else{
                            selector.selectedTitleCountNode = new Element("span", {
                                "style": "padding-right:5px;",
                                "text": (MWF.SelectorLP.quantifier[ selector.selectType ] || "") + ":0"
                            }).inject(this.selectedTitleNode);
                        }

                        selector.selectedNode = new Element("div.selectedNode", {
                            "styles": this.css.selectedNodeMobile
                        }).inject(this.selectedScrollNode);
                    }

                    selector.loadContent( pageNode );
                    if( !layout.mobile )selector.setSize();

                    if( layout.mobile ){

                        tab.addEvent("postShow", function () {
                            this.setSelectNodeSizeMobile(t, index, tab);
                        }.bind(this))

                        this._setTouchEvents(type, selector);
                    }else{
                        //if( !this.isSingle() && Number.convert( options.count ) === 1 ){
                        //    this.selectors[t].selectNode.setStyles({
                        //        "float" : "none",
                        //        "margin-left" : "auto",
                        //        "margin-right" : "auto"
                        //    })
                        //}
                    }
                }

                if( index == 0 )tab.showIm();
            }.bind(this));
        }.bind(this));
    },
    setSelectNodeSizeMobile: function(t, index, tab){
        if( tab.isSetSize )return;
        tab.isSetSize = true;
        if( index === 0 ){
            var containerSize = this.container.getSize();
            var bodySize = $(document.body).getSize();

            var size = {
                "x" : Math.min( containerSize.x, bodySize.x ),
                "y" : Math.min( containerSize.y, bodySize.y )
            };

            this.contentHeight = size.y;
            if( !this.tabContainer ){
                this.contentHeight = this.contentHeight - this.getOffsetY( tab.tab.tabNodeContainer ) - tab.tab.tabNodeContainer.getStyle("height").toInt();
            }
            if( this.selectedCountNode ){
                this.contentHeight = this.contentHeight - this.getOffsetY( this.selectedCountNode ) - this.selectedCountNode.getStyle("height").toInt();
            }
            var formActionY = 0;
            // height = height - formActionY - this.titleNode.getSize().y - this.getOffsetY(this.titleNode);
            // if(this.selectedCountNode)height = height - this.selectedCountNode.getSize().y - this.getOffsetY(this.selectedCountNode);
            // this.contentHeight = height;

            var sel = this.selectors[t];
            var offsetY = this.getOffsetY(sel.itemAreaScrollNode);
            if(sel.searchInputDiv)offsetY = offsetY + sel.searchInputDiv.getSize().y + this.getOffsetY(sel.searchInputDiv);
            if(sel.letterAreaNode)offsetY = offsetY + sel.letterAreaNode.getSize().y + this.getOffsetY(sel.letterAreaNode);

            this.itemAreaHeight = this.contentHeight - offsetY;
        }


        // if( isFormWithAction ){
        //     height = size.y-40-20-6-20;
        // }else{
        //     height = size.y;
        // }
        if(this.selectors[t].selectNode){
            this.selectors[t].selectNode.setStyle("height", ""+this.contentHeight+"px");
        }

        // if( isFormWithAction ){
        //     height = size.y-40-20-78 - 20;
        // }else{
        //     height = size.y-42-31-40;
        // }
        //height = height - 5;

        var itemAreaScrollNode = this.selectors[t].itemAreaScrollNode;
        if( itemAreaScrollNode ){
            itemAreaScrollNode.setStyle("height", ""+this.itemAreaHeight+"px");
        }
    },
    getValueByType : function( values, type ){
        var result = [];
        values = typeOf(values) == "array" ?  values : [values];
        var types = typeOf(type)== "array"  ?  type : [type];
        values.each( function( data ){
            if( !data )return;
            if( typeOf( data ) == "string" ){
                var dn = data;
            }else{
                var dn = data.distinguishedName;
            }
            if (dn && type ){
                var flag = dn.split('@').getLast();
                switch (flag.toLowerCase()){
                    case "i":
                        if( type == "identity" || types.contains( "identity" ) )result.push( data );
                        break;
                    case "p":
                        if( type == "person" || types.contains( "person" ) )result.push( data );
                        break;
                    case "u":
                        if( type == "unit" )result.push( data );
                        break;
                    case "ud":
                        if( type == "unitduty" )result.push( data );
                        break;
                    case "g":
                        if( type == "group" )result.push( data );
                        break;
                    case "r":
                        if( type == "role" )result.push( data );
                        break;
                    default:
                        if( type == "person" )result.push( data );
                        break;
                        //result.push( data );
                }
            }else{
                //result.push( data );
            }
        });
        return result;
    },
    loadCategoryHtml: function (){
        return new Promise(function (resolve, reject) {
            if( this.options.categoryUrl ){
                var request = new Request.HTML({
                    url: this.options.categoryUrl,
                    method: "GET",
                    async: true,
                    onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
                        this.categoryHtml = responseHTML;
                        if(resolve)resolve();
                    }.bind(this),
                    onFailure: function(xhr){ reject(xhr); }
                });
                request.send();
            }else{
                if(resolve)resolve();
            }
        }.bind(this))
    },
    loadItemHtml: function (){
        return new Promise(function (resolve, reject) {
            if( this.options.itemUrl ){
                var request = new Request.HTML({
                    url: this.options.itemUrl,
                    method: "GET",
                    async: true,
                    onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
                        this.itemHtml = responseHTML;
                        if(resolve)resolve();
                    }.bind(this),
                    onFailure: function(xhr){ reject(xhr); }
                });
                request.send();
            }else{
                if(resolve)resolve();
            }
        }.bind(this))
    },
    emptySelectedItems : function(){
        for( var key in this.selectors ){
            var selector = this.selectors[key];
            if( selector.selectedItems && selector.selectedItems.length > 0 ){
                selector.emptySelectedItems();
            }
        }
    },
    getSelectedItems : function(){
        this.selectedItems = [];
        for( var key in this.selectors ){
            var selector = this.selectors[key];
            if( selector.selectedItems && selector.selectedItems.length > 0 ){
                this.selectedItems = this.selectedItems.concat( selector.selectedItems );
            }
        }
        this.selectedItems.sort(function (a, b){
            return (a.selectedIndex || 9999999) - (b.selectedIndex || 9999999);
        });
        return this.selectedItems;
    },
    getSelectedItemsObject : function(){
        this.selectedItemsObject = {};
        for( var key in this.selectors ){
            var selector = this.selectors[key];
            if( selector.selectedItems && selector.selectedItems.length > 0 ){
                selector.selectedItems.sort(function (a, b){
                    return a.selectedIndex - b.selectedIndex;
                });
                this.selectedItemsObject[key.toLowerCase()] = selector.selectedItems;
            }
        }
        return this.selectedItemsObject;
    },
    getOffsetX : function(node){
        return (node.getStyle("margin-left").toInt() || 0 )+
            (node.getStyle("margin-right").toInt() || 0 ) +
            (node.getStyle("padding-left").toInt() || 0 ) +
            (node.getStyle("padding-right").toInt() || 0 ) +
            (node.getStyle("border-left-width").toInt() || 0 ) +
            (node.getStyle("border-right-width").toInt() || 0 );
    },
    getOffsetY : function(node){
        return (node.getStyle("margin-top").toInt() || 0 ) +
            (node.getStyle("margin-bottom").toInt() || 0 ) +
            (node.getStyle("padding-top").toInt() || 0 ) +
            (node.getStyle("padding-bottom").toInt() || 0 )+
            (node.getStyle("border-top-width").toInt() || 0 ) +
            (node.getStyle("border-bottom-width").toInt() || 0 );
    },
    setSize : function(){
        if( !this.options.width && !this.options.height )return;

        if( this.options.width && this.options.width === "auto" ){
            //if (this.options.count.toInt() !== 1){
            this.node.setStyle("width", "auto");
            this.contentWidth = "auto";

        }else if( this.options.width && typeOf( this.options.width.toInt() ) === "number" ){
            var nodeWidth = this.options.width.toInt() - this.getOffsetX(this.node);
            this.node.setStyle("width", nodeWidth);

            if( this.contentNode ){
                nodeWidth = nodeWidth - this.getOffsetX( this.contentNode );
            }

            this.contentWidth = nodeWidth;
        }
        if( this.options.height && typeOf( this.options.height.toInt() ) === "number" ){
            var nodeHeight = this.options.height.toInt() - this.getOffsetY(this.node);
            this.node.setStyle("height", nodeHeight);

            if( this.titleNode ){
                nodeHeight = nodeHeight - this.getOffsetY( this.titleNode ) - this.titleNode.getStyle("height").toInt();
            }

            if( this.actionNode ){
                nodeHeight = nodeHeight - this.getOffsetY( this.actionNode ) - this.actionNode.getStyle("height").toInt();
            }

            if( this.contentNode ){
                nodeHeight = nodeHeight - this.getOffsetY( this.contentNode );
            }

            this.contentHeight = nodeHeight;
        }
    },
    handleBreadcrumbsBack: function (){
        var flag = false;
        var showingPages = this.tab.pages.filter(function(page){ return !!page.isShow; });
        if(showingPages.length && showingPages[0].selector){
            flag = showingPages[0].selector.handleBreadcrumbsBack();
        }
        if( !flag ){
            this.close();
        }
    }
});

MWF.xApplication.Selector.MultipleSelector.Filter = new Class({
    Implements: [Options, Events],
    options: {
        "types" : [],
        "groups": [], //选person, group, role 时的范围
        "roles": [], //选选person, group, role 时的范围
        "units": [] //选 company, department, duty, identity 时的范围
    },
    initialize: function(value, options){
        this.setOptions(options);
        this.value = value;
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        this.selectors = {};

        this.options.types.each( function( type, index ){

            var opt = Object.clone( this.options );
            if( this.options[ type + "Options" ] ){
                opt = Object.merge( opt, this.options[ type + "Options" ] );
            }

            var t = type.capitalize();
            if ((type.toLowerCase()==="unit") && ( opt.unitType)){
                t = "UnitWithType";
            }
            if ((type.toLowerCase()==="identity") && ((opt.dutys) && opt.dutys.length)){
                t = "IdentityWidthDuty";
            }

            MWF.xDesktop.requireApp("Selector", t, function(){
                this.selectors[t] = new MWF.xApplication.Selector[t].Filter(this.value, opt);
            }.bind(this), false);
        }.bind(this));
    },
    filter: function(value, callback){
        this.value = value;
        var key = this.value;

        this.filterData = [];
        this.filterCount = 0;

        for (i in this.selectors){
            this.selectors[i].filter(value, function(data){
                this.filterData = this.filterData.concat(data);
                this.filterCount++;
                this.endFilter(callback);
            }.bind(this))
        }
        //
        // this.orgAction.listPersonByKey(function(json){
        //     data = json.data;
        //     if (callback) callback(data)
        // }.bind(this), failure, key);
    },
    endFilter: function(callback){
        if (this.filterCount>=Object.keys(this.selectors).length){
            if (callback) callback(this.filterData);
        }
    }
});

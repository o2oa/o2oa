MWF.xApplication.Organization = MWF.xApplication.Organization || {};
MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};
MWF.xDesktop.requireApp("Organization", "lp.zh-cn", null, false);
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
MWF.xApplication.Organization.Selector.MultipleSelector = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "types" : [],
        "count": 0,
        "title": "Select",
        "groups": [], //选person, group, role 时的范围
        "roles": [], //选选person, group, role 时的范围
        "companys": [], //选 company, department, duty, identity 时的范围
        "departments": [], //选 company, department, duty, identity 时的范围

        "multipleValues" : {}, //已选id， 和 groupValues，companyValues，departmentValues，identityValues，personValues 二选一， 样例 { "group" : ["xx群组id"], "department" : ["xx部门1id":"xx部门2id"] ...  }
        "groupValues" : [], // group 的已选id
        "companyValues" : [], // company 的已选id
        "departmentValues" : [], // department 的已选id
        "identityValues" : [], // identity 的已选id
        "personValues" : [], // person 的已选id,

        "multipleNames": {}, //已选name， 和 groupNames，companyNames，departmentNames，identityNames，personNames 二选一， 样例 { "group" : ["xx群组"], "department" : ["xx部门1":"xx部门2"] ...  }
        "groupNames" : [], // group 的已选选值
        "companyNames" : [], // company 的已选名称
        "departmentNames" : [], // department 的已选名称
        "identityNames" : [], // identity 的已选名称
        "personNames" : [], // person 的已选名称

        "zIndex": 1000,
        "expand": true
    },
    initialize: function(container, options){
        this.setOptions(options);

        this.path = "/x_component_Organization/Selector/$Selector/";
        this.cssPath = "/x_component_Organization/Selector/$Selector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);
        this.action = new MWF.xApplication.Organization.Actions.RestActions();
        this.lp = MWF.xApplication.Organization.LP;

        this.lastPeople = "";
        this.pageCount = "13";
        this.selectedItems = [];
        this.selectedItemsObject = {};
        this.items = [];
        this.selectors = {};
    },
    load: function(){
        if (layout.mobile){
            this.loadMobile();
        }else{
            this.loadPc();
        }
        this.fireEvent("load");
    },
    loadMobile: function(){
        this.container.mask({
            "destroyOnHide": true,
            "style": this.css.maskNode
        });
        this.node = new Element("div", {"styles": this.css.containerNodeMobile});
        this.node.setStyle("z-index", this.options.zIndex.toInt()+1);
        this.titleNode = new Element("div", {
            "styles": this.css.titleNodeMobile
        }).inject(this.node);

        this.titleCancelActionNode = new Element("div", {
            "styles": this.css.titleCancelActionNodeMobile,
            "text": MWF.xApplication.Organization.LP.back
        }).inject(this.titleNode);
        this.titleOkActionNode = new Element("div", {
            "styles": this.css.titleOkActionNodeMobile,
            "text": MWF.xApplication.Organization.LP.ok
        }).inject(this.titleNode);

        this.titleTextNode = new Element("div", {
            "styles": this.css.titleTextNodeMobile,
            "text": this.options.title
        }).inject(this.titleNode);

        this.contentNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.node);
        var size = this.container.getSize();
        var height = size.y-40;
        this.contentNode.setStyle("height", ""+height+"px");


        this.loadContent();

        this.node.inject(this.container);
        this.node.setStyles({
            "top": "0px",
            "left": "0px"
        });

        this.setEvent();
    },
    loadPc: function(){
        this.css.maskNode["z-index"] = this.options.zIndex;
        this.container.mask({
            "destroyOnHide": true,
            "style": this.css.maskNode
        });
        //  this.container.setStyle("z-index", this.options.zIndex);
        this.node = new Element("div", {
            "styles": (this.options.count.toInt()==1) ? this.css.containerNodeSingle_multiple : this.css.containerNode_multiple
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
            "styles": this.css.contentNode
        }).inject(this.node);

        this.loadContent();

        this.actionNode = new Element("div", {
            "styles": this.css.actionNode
        }).inject(this.node);
        if (this.options.count.toInt()==1) this.actionNode.setStyle("text-align", "center");
        this.loadAction();

        this.node.inject(this.container);
        this.node.position({
            relativeTo: this.container,
            position: "center",
            edge: "center"
        });

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
    close: function(){
        this.fireEvent("close");
        this.node.destroy();
        this.container.unmask();
        MWF.release(this);
        delete this;
    },
    loadAction: function(){
        this.okActionNode = new Element("button", {
            "styles": this.css.okActionNode,
            "text": "确　定"
        }).inject(this.actionNode);
        this.cancelActionNode = new Element("button", {
            "styles": this.css.cancelActionNode,
            "text": "取 消"
        }).inject(this.actionNode);
        this.okActionNode.addEvent("click", function(){
            this.fireEvent("complete", [this.getSelectedItems(), this.getSelectedItemsObject() ]);
            this.close();
        }.bind(this));
        this.cancelActionNode.addEvent("click", function(){this.fireEvent("cancel"); this.close();}.bind(this));
    },
    loadContent: function(){
        if (layout.mobile){
            var tabStyle = "mobileForm"
        }else{
            var tabStyle = "default"
        }
        MWF.require("MWF.widget.Tab", function(){
            this.tab = new MWF.widget.Tab(this.contentNode, {"style": tabStyle });
            this.tab.load();
        }.bind(this), false);

        this.options.types.each( function( type, index ){
            var pageNode = new Element( "div" ).inject( this.contentNode );

            var tab = this.tab.addTab( pageNode, this.lp[type], false );

           var t = type.capitalize();
            MWF.xDesktop.requireApp("Organization", "Selector."+t, function(){
                var options = Object.clone( this.options );

                options.values = [];
                if( options.multipleValues[type] ){
                    options.values = options.multipleValues[type];
                }
                if( options[type+"Values"] && options[type+"Values"].length ){
                    options.values = options.values.concat( options[type+"Values"] )
                }

                options.names = [];
                if( options.multipleNames[type] ){
                    options.names = options.multipleNames[type];
                }
                if( options[type+"Names"] && options[type+"Names"].length ){
                    options.names = options.names.concat( options[type+"Names"] )
                }

                this.selectors[t] = new MWF.xApplication.Organization.Selector[t](this.container, options );
                this.selectors[t].loadContent( pageNode );
                if( index == 0 )tab.showIm();
            }.bind(this));
        }.bind(this));
    },
    getSelectedItems : function(){
        this.selectedItems = [];
        for( var key in this.selectors ){
            var selector = this.selectors[key];
            if( selector.selectedItems && selector.selectedItems.length > 0 ){
                this.selectedItems = this.selectedItems.concat( selector.selectedItems );
            }
        }
        return this.selectedItems;
    },
    getSelectedItemsObject : function(){
        this.selectedItemsObject = {};
        for( var key in this.selectors ){
            var selector = this.selectors[key];
            if( selector.selectedItems && selector.selectedItems.length > 0 ){
                this.selectedItemsObject[key.toLowerCase()] = selector.selectedItems;
            }
        }
        return this.selectedItemsObject;
    }
});
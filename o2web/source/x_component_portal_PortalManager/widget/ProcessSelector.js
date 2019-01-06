MWF.xDesktop.requireApp("process.ProcessManager", "ProcessExplorer",null,false);
MWF.xDesktop.requireApp("process.ProcessManager", "widget.ApplicationSelector",null,false);
MWF.xApplication.process.ProcessManager.widget = MWF.xApplication.process.ProcessManager.widget || {};
MWF.xApplication.process.ProcessManager.widget.ProcessSelector = new Class({
    Implements: [Options, Events],
    Extends: MWF.xApplication.process.ProcessManager.widget.ApplicationSelector,
    options: {
        "style": "default",
        "multi": true,
        "maskNode": $(document.body)
    },
    initialize: function(app, options){
        this.setOptions(options);
        //this.node = $(node);
        this.app = app;
        this.processes = [];
        this.path = "/x_component_process_ProcessManager/widget/$ProcessSelector/";
        this.cssPath = "/x_component_process_ProcessManager/widget/$ProcessSelector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.selectedItems = [];
    },
    load: function(ids, callback){
        this.applicationIds = ids;
        this.callback = callback || null;
        this.loadWindow();
        this.loadContent();
    },
    loadContent: function(){
        //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            if (!this.restActions) this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");

            this.applicationIds.each(function(id){

                this.restActions.listProcess(id, function(json){
                    json.data.each(function(data){
                        var process = new MWF.xApplication.process.ProcessManager.widget.ProcessSelector.Process(this.app, this, data);
                        process.load();
                        this.processes.push(process);
                    }.bind(this));
                }.bind(this));

            }.bind(this));

        //}.bind(this));
    },
    clearSelected: function(){
        this.processes.each(function(pro){
            if (pro.selected) pro.unSelectedItem();
        }.bind(this));
    },
    close: function(){
        //if (this.callback) this.callback();
        this.selectedItems = [];
        this.windowNode.destroy();
        this.options.maskNode.unmask();
    }
});

MWF.xApplication.process.ProcessManager.widget.ProcessSelector.Process = new Class({
    Extends: MWF.xApplication.process.ProcessManager.ProcessExplorer.Process,
    Implements: [Options, Events],

    initialize: function(app, selector, data, options){
        this.explorer = selector;
        this.data = data;
        this.container = selector.windowContentNode;
        this.css = selector.css;

        this.icon = this._getIcon();
    },
    load: function(){
        this.createNode();
        this.createIconNode();
        this.loadSelectIcon();
        this.createTextNodes();
    },

    createNode: function(){
        this.node = new Element("div", {
            "styles": this.css.itemNode,
            "events": {
                "click": function(e){
                    this.selected();
                    e.stopPropagation();
                }.bind(this)
            }
        }).inject(this.container);
    },
    createIconNode: function(){
        if (this.data.name.icon) this.icon = this.data.name.icon;
        var iconUrl = this.explorer.path+""+this.explorer.options.style+"/processIcon/"+this.icon;

        var itemIconNode = new Element("div", {
            "styles": this.css.itemIconNode
        }).inject(this.node);
        itemIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");
    },
    loadSelectIcon: function(){
        this.selectIconNode = new Element("div", {
            "styles": this.css.processItemSelectIcon
        }).inject(this.node);
    },
    _open: function(e){},

    selected: function(){
        if (!this.isSelected){
            if (!this.explorer.options.multi) this.explorer.clearSelected();
            this.selectedItem();
        }else{
            this.unSelectedItem();
        }
    },
    selectedItem: function(){
        this.selectIconNode.setStyles(this.css.processItemSelectIcon_selected);
        this.node.setStyles(this.css.itemNode_select);
        this.explorer.selectedItems.push(this.data);
        this.isSelected = true;
    },
    unSelectedItem: function(){
        this.selectIconNode.setStyles(this.css.processItemSelectIcon);
        this.node.setStyles(this.css.itemNode);
        this.explorer.selectedItems.erase(this.data);
        this.isSelected = false;
    }
});
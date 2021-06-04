MWF.xApplication.process.FormDesigner.widget.FiledConfigurator = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default"
    },
    initialize: function(node, designer, options, data){

        this.setOptions(options);
        this.node = $(node);
        this.app = designer;

        this.data = data || [];

        this.path = "../x_component_process_FormDesigner/widget/$FiledConfigurator/";
        this.cssPath = "../x_component_process_FormDesigner/widget/$FiledConfigurator/"+this.options.style+"/css.wcss";
        this._loadCss();
    },
    load: function(){
        this.items = [];

        this.table = new Element("table").inject(this.node);

        var tr = new Element("tr").inject(this.table);
        new Element("th", {"text": this.app.lp.filedConfigurator.sequence, "width":"10%"}).inject(tr);
        new Element("th", {"text": this.app.lp.filedConfigurator.fieldTitle, "width":"35%"}).inject(tr);
        new Element("th", {"text": this.app.lp.filedConfigurator.fieldId, "width":"35%"}).inject(tr);
        var td = new Element("th", {"text": this.app.lp.filedConfigurator.action, "width":"20%"}).inject(tr);

        this.addNewItemAction = new Element("div", {"styles": this.css.addNewItemAction, "text": "+"}).inject(this.node);
        this.addNewItemAction.addEvent("click", function(){
            this.addItem();
            this.addNewItemAction.hide();
        }.bind(this));

        if(this.data.length > 0)this.addNewItemAction.hide();


        // if( this.data.length === 0 ){
        //     this.addItem()
        // }else{
            Array.each(this.data, function(obj, i){
                var item = new MWF.xApplication.process.FormDesigner.widget.FiledConfigurator.Item(this);
                item.load(obj, i);
                this.items.push(item);
            }.bind(this));
        // }

    },
    getData: function(){
        return this.data;
    },
    addItem: function(){
        var item = new MWF.xApplication.process.FormDesigner.widget.FiledConfigurator.Item(this);
        item.load("", this.items.length);
        this.items.push(item);
        this.fireEvent("change");
    },
    insertItem: function(beforeItem){

        var index = beforeItem.index+1;
        this.data.splice(index, 0, {"field": "", "title":""});

        this.node.empty();
        this.load();
        this.fireEvent("change");

        // var item = new MWF.xApplication.process.FormDesigner.widget.FiledConfigurator.Item(this);
        // item.load("", beforeItem.index+1);
        // this.items.push(item);
    },
    deleteItem: function(item){
        this.items.erase(item);

        if (this.data[item.index]){
            this.data.splice(item.index, 1);
        }

        if (item.tr){
            item.tr.destroy();
        }
        this.setItemsSequence();
        if (this.data.length === 0){
            if (this.addNewItemAction) this.addNewItemAction.setStyle("display", "block");
        }
        this.fireEvent("change");
    },
    upItem: function(item){
        if( item.index === 0)return;

        var beforeData = this.data[item.index-1];
        this.data[item.index-1] = this.data[item.index];
        this.data[item.index] = beforeData;

        this.node.empty();
        this.load();
        this.fireEvent("change");
    },
    setItemsSequence: function () {
        this.items.each(function (item, i) {
            item.index = i;
            item.sequenceNode.set("text", i+1)
        })
    }

});

MWF.xApplication.process.FormDesigner.widget.FiledConfigurator.Item = new Class({
    initialize: function(configurator){
        this.configurator = configurator;
    },
    load: function(data, i){
        this.index = i;
        if (!data){
            this.data = {"field": "", "title":""};
            this.configurator.data.push(this.data);
        }else{
            this.data = data;
        }

        this.create();
    },
    create: function () {
        var lp = this.configurator.app.lp.filedConfigurator;

        this.tr = new Element("tr").inject(this.configurator.table);
        var td;

        this.sequenceNode = new Element("td", {
            text : this.index+1,
            styles: this.configurator.css.sequenceNode,
        }).inject(this.tr);

        td = new Element("td").inject(this.tr);
        this.titleInput = new Element("input", {
            value: this.data.title,
            styles: this.configurator.css.inputNode,
            events: {
                blur: function (){
                    this.data.title = this.titleInput.get("value");
                    this.configurator.fireEvent("change");
                }.bind(this)
            }
        }).inject(td);

        td = new Element("td").inject(this.tr);
        this.fieldInput = new Element("input", {
            value: this.data.field,
            styles: this.configurator.css.inputNode,
            events: {
                blur: function (){
                    this.data.field = this.fieldInput.get("value");
                    this.configurator.fireEvent("change");
                }.bind(this)
            }
        }).inject(td);

        td = new Element("td").inject(this.tr);

        this.upAction = new Element("div", {
            text : "↑",
            "title": lp.moveup,
            styles: this.configurator.css.addAction,
            events: {
                click: function (ev) {
                    this.up(ev)
                }.bind(this)
            }
        }).inject(td);

        this.delectAction = new Element("div", {
            text : "-",
            "title": lp.deleteRow,
            styles: this.configurator.css.delectAction,
            events: {
                click: function (ev) {
                    this.delect(ev)
                }.bind(this)
            }
        }).inject(td);

        this.addAction = new Element("div", {
            text : "+",
            "title": lp.insertRow,
            styles: this.configurator.css.addAction,
            events: {
                click: function (ev) {
                    this.add(ev)
                }.bind(this)
            }
        }).inject(td);
    },
    add: function(){
        this.configurator.insertItem(this)
    },
    delect: function () {
        this.configurator.deleteItem(this);
    },
    up: function () {
        this.configurator.upItem(this);
    }
});


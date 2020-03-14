MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "lp."+MWF.language, null, false);
//MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);
MWF.O2Selector = new Class({
    Implements: [Options],
    options: {
        "count": 0,
        "type": "person",
        "title": "Select Person",
        "groups": [],
        "roles": [],
        "units": [],
        "unitType": "",
        "values": [],
        "exclude" : [],
        "categoryType": "unit",
        "dutyUnitLevelBy" : "duty"
    },
    initialize: function(container, options){
        //MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);
        this.setOptions(options);
        this.container = container;

        if( this.options.types && typeOf(this.options.types) === "array" && this.options.types.length > 0 ){
            MWF.xDesktop.requireApp("Selector", "MultipleSelector", function() {
                this.selector = new MWF.xApplication.Selector.MultipleSelector(this.container, this.options );
                this.selector.load();
            }.bind(this));
        }else{
            var type = typeOf(this.options.type) === "string" ? this.options.type.capitalize() : this.options.type;
            if (type){
                if ((type.toLowerCase()==="unit") && (this.options.unitType)){
                    MWF.xDesktop.requireApp("Selector", "UnitWithType", function(){
                        this.selector = new MWF.xApplication.Selector.UnitWithType(this.container, options);
                        this.selector.load();
                    }.bind(this));
                }else if ((type.toLowerCase()==="identity") && ((this.options.dutys) && this.options.dutys.length) && this.options.categoryType.toLowerCase()==="duty"){
                    MWF.xDesktop.requireApp("Selector", "IdentityWidthDuty", function(){
                        this.selector = new MWF.xApplication.Selector.IdentityWidthDuty(this.container, options);
                        this.selector.load();
                    }.bind(this));
                }else if ((type.toLowerCase()==="identity") && ((this.options.dutys) && this.options.dutys.length) && this.options.categoryType.toLowerCase()==="unit"){
                    MWF.xDesktop.requireApp("Selector", "IdentityWidthDutyCategoryByUnit", function(){
                        this.selector = new MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit(this.container, options);
                        this.selector.load();
                    }.bind(this));
                }else{
                    MWF.xDesktop.requireApp("Selector", type, function(){
                        this.selector = new MWF.xApplication.Selector[type](this.container, options);
                        this.selector.load();
                    }.bind(this));
                }
            }
        }
    }
});

MWF.O2SelectorFilter = new Class({
    Implements: [Options],
    options: {
        "count": 0,
        "type": "person",
        "title": "Select Person",
        "groups": [],
        "roles": [],
        "units": [],
        "unitType": "",
        "values": []
    },
    initialize: function(value, options){
        //MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);
        this.setOptions(options);
        this.value = value;
        //var type = this.options.type.capitalize();

        if( this.options.types && typeOf(this.options.types) === "array" && this.options.types.length > 0 ){
            MWF.xDesktop.requireApp("Selector", "MultipleSelector", function() {
                this.selectFilter = new MWF.xApplication.Selector.MultipleSelector.Filter(this.container, this.options );
            }.bind(this), false);
        }else{
            var type = typeOf(this.options.type) === "string" ? this.options.type.capitalize() : this.options.type;
            if (type){
                if ((type.toLowerCase()==="unit") && (this.options.unitType)){
                    MWF.xDesktop.requireApp("Selector", "UnitWithType", function(){
                        this.selector = new MWF.xApplication.Selector.UnitWithType.Filter(this.container, options);
                        this.selector.load();
                    }.bind(this));
                }else if ((type.toLowerCase()==="identity") && ((this.options.dutys) && this.options.dutys.length) && this.options.categoryType.toLowerCase()==="duty"){
                    MWF.xDesktop.requireApp("Selector", "IdentityWidthDuty", function(){
                        this.selectFilter = new MWF.xApplication.Selector.IdentityWidthDuty.Filter(this.value, options);
                    }.bind(this), false);
                }else if ((type.toLowerCase()==="identity") && ((this.options.dutys) && this.options.dutys.length) && this.options.categoryType.toLowerCase()==="unit"){
                    MWF.xDesktop.requireApp("Selector", "IdentityWidthDutyCategoryByUnit", function(){
                        this.selectFilter = new MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.Filter(this.value, options);
                    }.bind(this));
                }else{
                    MWF.xDesktop.requireApp("Selector", type, function(){
                        this.selectFilter = new MWF.xApplication.Selector[type].Filter(this.value, options);
                    }.bind(this), false);
                }
            }
        }

        //if (type){
        //    if ((type.toLowerCase()==="unit") && (this.options.unitType)){
        //        MWF.xDesktop.requireApp("Selector", "UnitWithType", function(){
        //            this.selector = new MWF.xApplication.Selector.UnitWithType.Filter(this.container, options);
        //            this.selector.load();
        //        }.bind(this));
        //    }else if ((type.toLowerCase()==="identity") && ((this.options.dutys) && this.options.dutys.length)){
        //        MWF.xDesktop.requireApp("Selector", "IdentityWidthDuty", function(){
        //            this.selectFilter = new MWF.xApplication.Selector.IdentityWidthDuty.Filter(this.value, options);
        //        }.bind(this), false);
        //    }else{
        //        MWF.xDesktop.requireApp("Selector", type, function(){
        //            this.selectFilter = new MWF.xApplication.Selector[type].Filter(this.value, options);
        //        }.bind(this), false);
        //     }
        //}else{
        //    MWF.xDesktop.requireApp("Selector", "MultipleSelector", function() {
        //        this.selectFilter = new MWF.xApplication.Selector.MultipleSelector.Filter(this.container, this.options );
        //    }.bind(this), false);
        //}

    },
    filter: function(value, callback){
        return this.selectFilter.filter(value, callback);
    }
});

(function(){
    var _createEl = function(data, node){
        var dname;
        if (typeOf(data)==="string"){
            data = {"id": data};
            dname = data.id;
        }else{
            dname = data.distinguishedName || data.name || data.id
        }
        var len = dname.length;
        var flag = dname.substring(len-1,len);
        switch (flag){
            case "U":
                new o2.widget.O2Unit(data, node, {"style": "xform"});
                break;
            case "I":
                new o2.widget.O2Identity(data, node, {"style": "xform"});
                break;
            case "G":
                new o2.widget.O2Group(data, node, {"style": "xform"});
                break;
            case "P":
                new o2.widget.O2Person(data, node, {"style": "xform"});
                break;
            case "R":
                new o2.widget.O2Role(data, node, {"style": "xform"});
                break;
        }
    };

    //Element.implement({
    //    setSelectPerson : function(container, options){
    //        if (options.types) options.type = "";
    //        options.onComplete = function(items){
    //
    //            debugger;
    //
    //            o2.require("o2.widget.O2Identity", function(){
    //                options.values = [];
    //                this.empty();
    //                items.each(function(item){
    //                    options.values.push(item.data);
    //                    _createEl(item.data, this);
    //                    if (options.selectItem) options.selectItem(item);
    //                }.bind(this));
    //                this.store("data-value", options.values);
    //            }.bind(this));
    //        }.bind(this);
    //
    //        if (options.values){
    //            options.values.each(function(v){
    //                this.store("data-value", options.values);
    //                _createEl(v, this);
    //            }.bind(this));
    //        }
    //        this.addEvent("click", function(){
    //            var i = this.getZIndex();
    //            options.zIndex = i+1;
    //            new MWF.O2Selector(container, options);
    //        }.bind(this));
    //    }
    //});

    Element.prototype.setSelectPerson = function(container, options){
        if (options.types) options.type = "";
        options.onComplete = function(items){
            o2.require("o2.widget.O2Identity", function(){
                options.values = [];
                this.empty();
                items.each(function(item){
                    options.values.push(item.data);
                    _createEl(item.data, this);
                    if (options.selectItem) options.selectItem(item);
                }.bind(this));
                this.store("data-value", options.values);
            }.bind(this));
        }.bind(this);

        if (options.values){
            options.values.each(function(v){
                this.store("data-value", options.values);
                _createEl(v, this);
            }.bind(this));
        }
        this.addEvent("click", function(){
            var i = this.getZIndex();
            options.zIndex = i+1;
            new MWF.O2Selector(container, options);
            //var selector = new MWF.O2Selector(container, options);
            //this.store("data-selector", selector)
        }.bind(this));
    };
})();

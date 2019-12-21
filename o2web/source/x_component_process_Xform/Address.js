MWF.require("MWF.widget.PinYin", null, false);
MWF.xDesktop.requireApp("process.Xform", "Combox", null, false);
MWF.xApplication.process.Xform.Address = MWF.APPAddress =  new Class({
	Implements: [Events],
	Extends: MWF.APPCombox,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad", "commitInput", "change"]
    },

    initialize: function(node, json, form){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
    },
    _loadNode: function(){
        if (this.readonly){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
        }
    },
    _loadNodeRead: function(){
        this.node.empty();
        //new Element("select").inject(this.node);
    },
    _loadNodeEdit: function(){
        this.node.empty();

        MWF.require("MWF.widget.Combox", function(){
            this.combox = new MWF.widget.Combox({
                "style": "blue",
                "onlySelect": true,
                "count": 4,
                "focusList": true,
                "onCommitInput": function(){
                    this.fireEvent("commitInput");
                }.bind(this),
                "onChange": function(e, oldValues){
                    var thisValues = this.combox.values.map(function(v){ return v.data || v.value});
                    if ((oldValues && (oldValues.join() !== thisValues.join()))){
                        while (this.combox.values.length-1>e.index){
                            this.combox.deleteItem(this.combox.values[this.combox.values.length-1])
                        }
                        this.fireEvent("change");
                    }
                }.bind(this),
                "optionsMethod": this._searchOptions.bind(this)
            });
            this.combox.intoEdit = function(e){
                if (this.options.count){
                    if (this.values.length>=this.options.count){
                        // if (this.input) this.input.noBlur = true;
                        if (this.input) this.input.node.hide();
                        // this.getLast().edit();
                        return false;
                    }
                }
                if (!this.input){
                    this.input = new MWF.widget.Combox.Input(this, this, "");
                    this.input.node.inject(this.node);
                    this.input.node.setStyle("width", "1px");
                }
                this.input.node.show();
                this.input.setInputNodeStyles();
                //this.input.node.set("value", "111");
                this.input.node.focus();
                this.input.setInputPosition();
                if (this.options.focusList) this.input.searchItems();
            }
        }.bind(this), false);

        this.combox.inject(this.node);
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });

        this.combox.addEvent("change", function(){
            this.validationMode();
            if (this.validation()) this._setBusinessData(this.getInputData("change"));
        }.bind(this));
    },
    _searchOptions: function(value, callback){
        value = value.toLowerCase();
        var i = (this.combox.editItem) ? this.combox.editItem.getItemPosition() : this.combox.values.length;
        switch (i) {
            case 0: //省
                o2.Actions.get("x_general_assemble_control").listProvince(function(json){
                    var list = [];
                    json.data.each(function(text){
                        var k = text.name;
                        var keyword = k+MWF.widget.PinYin.toPY(k).toLowerCase()+MWF.widget.PinYin.toPYFirst(k).toLowerCase();
                        if (value){
                            //if (keyword.indexOf(value)!==-1)
                                list.push({"text": k, "value": k});
                        }else{
                            list.push({"text": k, "value": k});
                        }
                    }.bind(this));
                    if (list.length) if (callback) callback(list);
                }.bind(this));
                // MWF.UD.getPublicData("addr_province", function(json){
                //     var list = [];
                //     json.each(function(text){
                //         var keyword = text+MWF.widget.PinYin.toPY(text).toLowerCase()+MWF.widget.PinYin.toPYFirst(text).toLowerCase();
                //         if (value){
                //             if (keyword.indexOf(value)!==-1) list.push({"text": text, "value": text});
                //         }else{
                //             list.push({"text": text, "value": text});
                //         }
                //
                //     }.bind(this));
                //     if (list.length) if (callback) callback(list);
                // });
                break;
            case 1: //市
                var item = this.combox.getFirst();

                o2.Actions.get("x_general_assemble_control").listCity(item.data || item.value, function(json){
                    var list = [];
                    json.data.each(function(text){
                        var k = text.name;
                        var keyword = k+MWF.widget.PinYin.toPY(k).toLowerCase()+MWF.widget.PinYin.toPYFirst(k).toLowerCase();
                        if (value){
                            //if (keyword.indexOf(value)!==-1)
                                list.push({"text": k, "value": k});
                        }else{
                            list.push({"text": k, "value": k});
                        }
                    }.bind(this));
                    if (list.length) if (callback) callback(list);
                }.bind(this));


                // MWF.UD.getPublicData("addr_city_"+item.data, function(json){
                //     var list = [];
                //     json.each(function(text){
                //         var keyword = text+MWF.widget.PinYin.toPY(text).toLowerCase()+MWF.widget.PinYin.toPYFirst(text).toLowerCase();
                //         if (value){
                //             if (keyword.indexOf(value)!==-1) list.push({"text": text, "value": text});
                //         }else{
                //             list.push({"text": text, "value": text});
                //         }
                //     }.bind(this));
                //     if (list.length) if (callback) callback(list);
                // });
                break;
            case 2: //区
                var f = this.combox.getFirst();
                var p = f.data || f.value;
                var item = this.combox.getFirst().getNextItem();

                o2.Actions.get("x_general_assemble_control").listDistrict(p, item.data||item.value, function(json){
                    var list = [];
                    json.data.each(function(text){
                        var k = text.name;
                        var keyword = k+MWF.widget.PinYin.toPY(k).toLowerCase()+MWF.widget.PinYin.toPYFirst(k).toLowerCase();
                        if (value){
                            //if (keyword.indexOf(value)!==-1)
                                list.push({"text": k, "value": k});
                        }else{
                            list.push({"text": k, "value": k});
                        }
                    }.bind(this));
                    if (list.length) if (callback) callback(list);
                }.bind(this));


                // MWF.UD.getPublicData("addr_district_"+item.data, function(json){
                //     var list = [];
                //     json.each(function(text){
                //         var keyword = text+MWF.widget.PinYin.toPY(text).toLowerCase()+MWF.widget.PinYin.toPYFirst(text).toLowerCase();
                //         if (value){
                //             if (keyword.indexOf(value)!==-1) list.push({"text": text, "value": text});
                //         }else{
                //             list.push({"text": text, "value": text});
                //         }
                //     }.bind(this));
                //     if (list.length) if (callback) callback(list);
                // });
                break;
            default:
                if (callback) callback([]);
        }
    }
}); 
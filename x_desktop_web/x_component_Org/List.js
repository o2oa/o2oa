MWF.xApplication.Org.List = new Class({
    Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
        "action": false,
        "data": {
            "name": "",
            "attributeList": [""]
        },
        "attr": []
	},
    _loadPath: function(){
        this.path = "/x_component_Org/$List/";
        this.cssPath = "/x_component_Org/$List/"+this.options.style+"/css.wcss";
    },
    initialize: function(node, content, options){
        this.setOptions(options);

        this._loadPath();
        this._loadCss();

        this.content = content;
        this.contentNode = $(node);

        this.items = [];
        this.selectedItems = [];
    },
    load: function(headers){
        //this.node = new Element("div", {"styles": this.css.node}).inject(this.contentNode);

        var html = "<table cellspacing='0' cellpadding='5' border='0' width='95%' align='center' style='line-height:normal'>";
        html += "<tr><th style='width:20px'></th>";
        headers.each(function(title){
            html += "<th style='"+title.style+"'>"+title.text+"</th>";
        }.bind(this));
        html += "</table>";
        this.contentNode.set("html", html);

        this.loadAction();

        this.table = new HtmlTable(this.contentNode.getFirst("table"));
        this.contentNode.getElements("th").setStyles(this.css.listTitle);
    },
    loadAction: function(){
        this.actionAreaNode = new Element("div", {"styles": this.css.actionAreaNode}).inject(this.contentNode, "top");
        if (this.options.action){
            this.actionNode = new Element("div", {"styles": this.css.actionNode}).inject(this.actionAreaNode);
            this.deleteAction = new Element("div", {"styles": this.css.deleteActionNode_disabled, "text": this.content.explorer.app.lp.delete}).inject(this.actionNode);
            this.addAction = new Element("div", {"styles": this.css.addActionNode, "text": this.content.explorer.app.lp.add}).inject(this.actionNode);

            this.addAction.addEvent("click", function(){
                this.addItem();
            }.bind(this));
            this.deleteAction.addEvent("click", function(e){
                this.deleteItem(e);
            }.bind(this));
        }
    },
    addItem: function(){
        var data = Object.clone(this.options.data);
        var tr = new MWF.xApplication.Org.List.Item(data, this.options.attr, this);
        this.items.push(tr);
        tr.edit(tr.tr.tds[1]);
    },
    deleteItem: function(e){
        if (this.selectedItems.length){
            var _self = this
            this.content.explorer.app.confirm("infor", e, this.content.explorer.app.lp.deleteAttributeTitle, this.content.explorer.app.lp.deleteAttribute, 350, 120, function(){
                this.close();

                var delCount = 0;
                var deleteCompleted = function(){
                    if (delCount === _self.selectedItems.length){
                        while (_self.selectedItems.length){
                            var tr = _self.selectedItems[0];
                            tr.destroy();
                        }
                    }
                };
                _self.selectedItems.each(function(item){
                    item["delete"](function(){
                        delCount++;
                        deleteCompleted();
                    });
                }.bind(this));
            }, function(){this.close();});
        }
    },

    push: function(data){
        // var rows = [""];
        // attr.each(function(n){
        //     if (typeOf(n)==="function"){
        //         rows.push(n.apply(data));
        //     }else if(typeOf(n)==="string"){
        //         rows.push(data[n]);
        //     }else{
        //         rows.push("");
        //     }
        // }.bind(this));
        // var tr = this.table.push(rows, {"styles": this.css.contentTrNode});
        // tr.tds.each(function(td){td.setStyles(this.css.contentTdNode);}.bind(this));
        // tr.tds[0].setStyles(this.css.selectTdNode);

        this.items.push(new MWF.xApplication.Org.List.Item(data, this.options.attr, this));
    },
    setAction: function(){
        if (this.selectedItems.length){
            this.deleteAction.setStyles(this.css.deleteActionNode);
        }else{
            this.deleteAction.setStyles(this.css.deleteActionNode_disabled);
        }
    }

});
MWF.xApplication.Org.List.Item = new Class({
    initialize: function(data, attr, list){
        this.data = data;
        this.attr = attr;
        this.list = list;
        this.css = this.list.css;
        this.load();
    },
    load: function(){
        var rows = [""];
        this.attr.each(function(n){
            if (typeOf(n)==="object"){
                if (n.get){
                    rows.push(n.get.apply(this.data));
                }else{
                    rows.push("");
                }
            }else if(typeOf(n)==="string"){
                rows.push(this.data[n]);
            }else{
                rows.push("");
            }
        }.bind(this));
        this.tr = this.list.table.push(rows, {"styles": this.css.contentTrNode});

        var _self = this;
        this.tr.tds.each(function(td, i){
            td.setStyles(this.css.contentTdNode);
            if (i===0) this.selectTd = td;
            if (i>0){
                if (this.list.options.action){
                    td.store("attr", this.attr[i-1]);
                    td.addEvent("click", function(){
                        _self.edit(this);
                    });
                }
            }
        }.bind(this));

        if (this.list.options.action){
            this.selectTd.setStyles(this.css.selectTdNode);
            this.selectTd.addEvent("click", function(){
                if (!this.isSelected){
                    this.selected();
                }else{
                    this.unSelected();
                }
            }.bind(this));
        }else{
            this.selectTd.setStyles(this.css.blankTdNode);
        }
    },
    edit: function(td){
        var attr = td.retrieve("attr");
        var text = td.get("text");
        td.empty();
        var input = new Element("input", {"styles": this.css.inputNode, "value": text}).inject(td);
        td.removeEvents("click");
        var _self = this;
        input.focus();
        input.addEvents({
            "blur": function(){
                var value = this.get("value");
                if (value){
                    if (typeOf(attr)==="object"){
                        if (attr.set){
                            attr.set.apply(_self.data, [value]);
                        }
                    }else if(typeOf(attr)==="string") {
                        _self.data[attr] = value
                    }
                }
                _self.editCompleted(td, value, text);
            }
        });
    },
    editCompleted: function(td, value, text){
        td.empty();
        if (!value && !text){
            if (td.cellIndex===1){
                this.destroy();
            }
        }else if (!value){
            if (td.cellIndex===1){
                td.set("text", text);
            }else{
                td.set("text", value);
                if (value!=text) this.save();
            }
        }else{
            td.set("text", value);
            if (value!=text) this.save();
        }
        var _self = this;
        td.addEvent("click", function(){
            _self.edit(this);
        });
    },
    "delete": function(callback){
        this.list.content.explorer.actions.deletePersonAttribute(this.data.id, function(){
            if (callback) callback();
            //this.destroy();
        }.bind(this));

    },
    destroy: function(){
        debugger;
        this.list.items.erase(this);
        if (this.isSelected) this.unSelected();
        this.list.setAction();
        this.tr.tr.destroy();
        MWF.release(this);
    },

    save: function(){
        this.list.content.explorer.actions.savePersonAttribute(this.data, function(json){
            this.data.id = json.data.id;
        }.bind(this), function(xhr, text, error){
            if (xhr) errorText = xhr.responseText;
            this.list.content.explorer.app.notice("request json error: "+errorText, "error");
        }.bind(this));
    },

    selected: function(){
        this.selectTd.setStyles(this.css.selectTdNode_selected);
        this.tr.tr.setStyles(this.css.contentTrNode_selected);
        this.list.selectedItems.push(this);
        this.isSelected = true;
        this.list.setAction();
    },
    unSelected: function(){
        this.selectTd.setStyles(this.css.selectTdNode);
        this.tr.tr.setStyles(this.css.contentTrNode);
        this.list.selectedItems.erase(this);
        this.isSelected = false;
        this.list.setAction();
    }


});
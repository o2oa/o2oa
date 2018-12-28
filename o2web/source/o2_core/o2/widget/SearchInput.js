o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.SearchInput = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default",
        "width": 800,
        "height": 40
	},
	initialize: function(options){
        this.setOptions(options);
        this.path = o2.session.path+"/widget/$SearchInput/";
        this.cssPath = o2.session.path+"/widget/$SearchInput/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.load();
	},
    load: function(){
        if (this.fireEvent("queryLoad")){
            this.node = new Element("div", {"styles": this.css.searchNode});
            this.searchBarNode = new Element("div", {"styles": this.css.searchBarNode}).inject(this.node);
            this.searchAction = new Element("div", {"styles": this.css.searchAction}).inject(this.searchBarNode);
            this.searchInputArea = new Element("div", {"styles": this.css.searchInputArea}).inject(this.searchBarNode);
            this.input = new Element("input", {"type":"text", "styles": this.css.searchInputNode}).inject(this.searchInputArea);

            this.copyPrototype();
            this.setEvent();
            this.fireEvent("postLoad");
        }
    },
    copyPrototype: function(){
        this.inject = this.node.inject.bind(this.node);
        this.setStyle = this.node.setStyle.bind(this.node);
        this.setStyles = this.node.setStyles.bind(this.node);
        this.set = this.node.set.bind(this.node);
    },
    setEvent: function(){
        this.node.addEvent("mousedown", function(e){e.stopPropagation();});

        this.searchAction.addEvent("click", function(){
            this.doSearch();
        }.bind(this));
        o2.UD.getDataJson("searchKeys", function(json){
            this.keys = json || [];
            this.input.addEvents({
                "input": function(){
                    if (this.keys && this.keys.length){
                        var value = this.input.get("value");
                        var keys = this.keys.filter(function(d){ return d.indexOf(value)!==-1; });
                        keys = keys.slice(0,9);
                        this.showAutoCompleted(keys);
                    }
                }.bind(this),
                "keydown": function(e){
                    if (e.code===13){
                        this.hideAutoCompleted();
                        this.doSearch();
                    }
                    if (e.code===38){   //up
                        if (this.autoCompletedNode){
                            var node = (this.currentKey) ? this.currentKey.getPrevious() : this.autoCompletedNode.getLast();
                            if (!node) node = this.autoCompletedNode.getLast();
                            if (node){
                                this.setCurrentKey(node);
                                this.input.set("value", node.get("text"));
                            }
                        }
                    }
                    if (e.code===40){   //down
                        if (this.autoCompletedNode){
                            var node = (this.currentKey) ? this.currentKey.getNext() : this.autoCompletedNode.getFirst();
                            if (!node) node = this.autoCompletedNode.getFirst();
                            if (node){
                                this.setCurrentKey(node);
                                this.input.set("value", node.get("text"));
                            }
                        }
                    }
                }.bind(this)
            });
        }.bind(this));
    },
    doSearch: function(){
        var value = this.input.get("value");
        if (value){
            if (this.keys){
                var idx = this.keys.indexOf(value);
                if (idx===-1){
                    if (this.keys.length>=50) json.shift();
                }else{
                    this.keys.splice(idx, 1);
                }
                this.keys.push(value);
                o2.UD.putData("searchKeys", this.keys);
            }
            this.fireEvent("search", [value]);
        }
    },
    showAutoCompleted: function(keys){
        if (keys.length){
            var _self = this;
            if (!this.autoCompletedNode){
                this.autoCompletedNode = new Element("div", {"styles": this.css.autoCompletedNode}).inject(this.node);
                var s = this.node.getSize();
                this.autoCompletedNode.setStyle("width", ""+s.x+"px");
            }
            this.autoCompletedNode.empty();
            this.autoCompletedNode.show();

            this.hideAutoCompletedFun = this.hideAutoCompleted.bind(this);
            $(document.body).addEvent("mousedown", this.hideAutoCompletedFun);

            keys.each(function(key){
                var node = new Element("div", {"styles": this.css.searchKeyItem, "text": key}).inject(this.autoCompletedNode);
                node.addEvents({
                    "mouseover": function(){_self.setCurrentKey(this);},
                    "mouseout": function(){_self.removeCurrentKey(this);},
                    "click": function(){
                        _self.hideAutoCompleted();
                        _self.input.set("value", this.get("text"));
                        _self.doSearch();
                    }
                });
            }.bind(this));
        }else{
            this.hideAutoCompleted();
        }
    },
    setCurrentKey: function(node){
        if (this.currentKey) this.removeCurrentKey(this.currentKey);
        this.currentKey = node;
        node.setStyles(this.css.searchKeyItem_over)
    },
    removeCurrentKey: function(node){
        if (this.currentKey==node) this.currentKey = null;
        node.setStyles(this.css.searchKeyItem)
    },
    hideAutoCompleted: function(){
        if (this.hideAutoCompletedFun) $(document.body).removeEvent("mousedown", this.hideAutoCompletedFun);
        if (this.autoCompletedNode){
            this.autoCompletedNode.empty();
            this.autoCompletedNode.hide();
        }
    },
    getValue: function(){
        return this.input.get("value");
    },
    setValue: function(value){
        this.input.set("value", value);
    }

});

MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.Activity = new Class({
	Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "values": [],
        "names": [],
        "expand": false,
        "forceSearchInItem" : true,
        "application": "",
        "process": ""
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectProcess});
    },
    _init : function(){
        this.selectType = "Activity";
        this.className = "Activity";
    },
    loadSelectItems: function(addToNext){
        if (this.options.process){
            this.designerAction.getProcess(this.options.process, function(json){
                var category = this._newItemCategory(json.data, this, this.itemAreaNode);
            }.bind(this));
        }else{
            this.designerAction.listProcess(this.options.application, function(json){
                var processes = json.data;
                if (processes.length){
                    processes.each(function(data){
                        var category = this._newItemCategory(data, this, this.itemAreaNode);

                    }.bind(this));
                }
            }.bind(this));
        }

        
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(data){
        return data.processList || [];
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.Activity.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        if( !id )return;
        const [, aid, unique, process, type] = id.split('@');
        if (type==='draft'){
            this.designerAction.getProcess(process, function(processJson){
                var d = {
                    processName: processJson.data.name,
                    process: process,
                    type: 'draft',
                    name: "草稿",
                    unique: aid,
                    id: aid,
                    distinguishedName: '草稿'+'@'+'draft-'+aid+'@'+'draft-'+aid+'@'+process+'@draft@A'
                }
                if (callback) callback.apply(this, [{data: d}]);
            });
        }else{
            o2.Actions.load('x_processplatform_assemble_designer').ProcessAction.getActivity(unique, type, function(json){
                const data = (json.data.activity) ? json.data.activity : json.data;
                this.designerAction.getProcess(data.process, function(processJson){
                    var d = {
                        processName: processJson.data.name,
                        process: data.process,
                        id: data.id,
                        name: data.name,
                        alias: data.alias,
                        type: type,
                        unique: data.unique,
                        distinguishedName: data.name+'@'+data.id+'@'+data.unique+'@'+data.process+'@'+data.type+'@A'
                    }

                    if (callback) callback.apply(this, [{data: d}]);
                }.bind(this));
            }.bind(this), failure, async);
        }
        
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.Activity.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Activity.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.Activity.Item = new Class({
	Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.alias ? this.data.name +"("+this.data.alias+")"  : this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/activities.png)");
    },
    _getTtiteText: function(){
        return this.data.alias ? this.data.name +"("+this.data.alias+")"  : this.data.name;
    },
    loadSubItem: function(){
        return false;
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object"){
                // return (this.data.id === item.id);
                if( this.data.id && item.id ){
                    return this.data.id === item.id;
                }else{
                    return this.data.name === item.name;
                }
            }
            if (typeOf(item)==="string") return (this.data.id === item) || (this.data.name === item);
            return false;
        }.bind(this));
        if (selectedItem.length){
            this.selectedSingle();
        }
    },
    checkSelected: function(){
        var selectedItem = this.selector.selectedItems.filter(function(item, index){
            return item.data.distinguishedName === this.data.distinguishedName || item.data.unique === this.data.unique;
        }.bind(this));
        if (selectedItem.length){
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    }
});

MWF.xApplication.Selector.Activity.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        var name =  this.data.alias ? this.data.name +"("+this.data.alias+")"  : this.data.name;
        return this.data.processName + ' - '+name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/activities.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                return item.data.distinguishedName === this.data.distinguishedName || item.data.unique === this.data.unique;
            }.bind(this));
            this.items = items;
            if (items.length){
                items.each(function(item){
                    item.selectedItem = this;
                    item.setSelected();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.Selector.Activity.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,
    _getShowName: function(){
        return this.data.name;
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/processicon.png)");
    },
    loadSub: function(callback){
        if (!this.loaded){
            this.selector.designerAction.getProcess(this.data.id, function(data){
                var processData = data.data;
                var activityList = [].concat(
                    processData.manualList || [],
                    processData.conditionList || [],
                    processData.choiceList || [],
                    processData.parallelList || [],
                    processData.splitList || [],
                    processData.mergeList || [],
                    processData.embedList || [],
                    processData.publishList || [],
                    processData.invokeList || [],
                    processData.cancelList || [],
                    processData.delayList || [],
                    processData.messageList || [],
                    processData.serviceList || [],
                    processData.endList || []
                );
                activityList.unshift({
                    processName: this.data.name,
                    process: this.data.id,
                    type: 'draft',
                    name: "草稿",
                    unique: 'draft-'+this.data.id,
                    id: 'draft-'+this.data.id,
                    distinguishedName: '草稿'+'@'+'draft-'+this.data.id+'@'+'draft-'+this.data.id+'@'+this.data.id+'@draft@A'
                });
                activityList.forEach(function(data){
                    var d = {
                        processName: this.data.name,
                        process: this.data.id,
                        id: data.id,
                        name: data.name,
                        alias: data.alias,
                        type: data.type,
                        unique: data.unique,
                        distinguishedName: data.name+'@'+data.id+'@'+data.unique+'@'+data.process+'@'+data.type+'@A'
                    }
                    var item = this.selector._newItem(d, this.selector, this.children);
                    this.selector.items.push(item);
                }.bind(this));

                if (callback) callback();
            }.bind(this));
            this.loaded = true;
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        return true;
    },
    check: function(){}
});

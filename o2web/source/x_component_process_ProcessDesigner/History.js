// MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xApplication.process.ProcessDesigner.History = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        style: "default",
        maxLength: 20 //最大的item数，0表示不限制
    },
    initialize: function(process, container, options){
        this.setOptions(options);
        this.process = process;
        this.designer = process.designer;
        this.container = container;
        //this.root = this.form.node;
        this.path = "../x_component_process_FormDesigner/$History/";
        this.iconPath = this.path+this.options.style+"/icon/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();
    },
    load: function(data) {
        //存储当前表面状态数组-上一步
        this.preArray = [];
        //存储当前表面状态数组-下一步
        this.nextArray = [];

        this.node = new Element("div", {"style":"font-size:12px;"}).inject(this.container);
        this.add({
            "operation": "open", //操作 create, copy, move, delete
            "type": "process", //property
            "moduleId": (this.process.process.name || "process" )
        });
    },
    add: function(log, module) {
        // var log = { //也有可能是对象数组
        //     "operation": "create", //操作 create, copy, move, delete, cut, paste
        //     "type": "module", //property 组件变化或属性变化
        //     "moduleType": "", //模块类型
        //     "moduleId": "", //模块id
        //      "fromList": [{  //原始数据
        //          "json": {},  //原始json
        //         "type": {}, //模块类型
        //      }],
        //      "toList": [{  //结束数据
        //          "json": {},  //原始json
        //         "type": {}, //模块类型
        //      }]
        // };
        var item;
        debugger;
        if( log.type === "activity" ) {
            switch (log.moduleType) {
                // case "Table$Td":
                //     item = new MWF.PDHistory.ModuleTableTdItem(this, log);
                //     break;
                // case "Datatable$Title":
                // case "Datatable$Data":
                //     item = new MWF.PDHistory.ModuleDatatableTdItem(this, log);
                //     break;
                // case "Tab$Page":
                //     item = new MWF.PDHistory.ModuleTabpageItem(this, log);
                //     break;
                default:
                    item = new MWF.PDHistory.ActivityItem(this, log);
            }
        }else if( log.type === "route" ){

        }else{
            item = new MWF.PDHistory.Item(this, log);
        }
        item.load();

        this.addItem(item);
    },
    checkProperty: function(log, module){
        // var log = {
        //     "type": "property",
        //     "force": false,
        //     "title": "",
        //     "moduleId": this.json.id,
        //     "moduleType": "",
        //     "notSetEditStyle": false
        //     "changeList": [
        //          {
        //              "name": name,
        //              "compareName": compareName, //对比名称，value对应的是name, 但是对比名称和name不一样时可以传这个
        //              "fromValue": oldValue,
        //              "toValue": this.json[name]
        //          }
        //      ]
        // };

        var isModified = false;
        for( var i=0; i<log.changeList.length; i++ ){
            var c = log.changeList[i];
            if( !c.fromValue && !c.toValue )continue;
            if( this.compareObjects( c.fromValue, c.toValue ) )continue;
            isModified = true;
            break;
        }
        if( !isModified )return;

        // console.log( log );
        debugger;

        var flag = false;
        if( this.preArray.length ){
            var lastItem = this.preArray.getLast();
            var lastSubItem;
            if( lastItem.data.type === "property" ) {
                var change = log.changeList[0];
                if (lastItem.moduleIdList.contains(log.moduleId) || (change.name === "id" && lastItem.moduleIdList.contains(change.fromValue))) {
                    if (change.name === "id") lastItem.moduleIdList.push(change.toValue);
                    lastSubItem = lastItem.getLastSubItem();

                    var it;
                    while( this.nextArray.length ){
                        it = this.nextArray.pop();
                        it.destroy();
                    }

                    while( lastItem.nextArray && lastItem.nextArray.length ){
                        it = lastItem.nextArray.pop();
                        it.destroy();
                    }

                    var lastChangeList = lastSubItem.data.changeList;
                    if ( log.force ) {
                        lastItem.addSubItem(log);
                    }else if( change.compareName ){
                        if (lastChangeList.length === 1 && log.changeList.length === 1  && lastChangeList[0].compareName === change.compareName) {
                            lastChangeList[0].toValue = change.toValue;
                        }else{
                            lastItem.addSubItem(log);
                        }
                    }else{
                        if (lastChangeList.length === 1 && log.changeList.length === 1  && lastChangeList[0].name === change.name) {
                            // if( lastSubItem.data.fromValue === change.toValue ){ //回到最初的值了
                            //     if( lastItem.preArray.length === 1 ){
                            //         this.destroyItem( lastItem );
                            //     }else{
                            //         lastItem.destroySubItem(lastSubItem);
                            //     }
                            // }else{
                            lastChangeList[0].toValue = change.toValue;
                            // }
                        } else {
                            lastItem.addSubItem(log);
                        }
                    }

                    flag = true;
                }
            }
        }
        if( !flag ){
            var item = new MWF.PDHistory.PropertySingleItem(this, log);
            item.load( module );
            this.addItem(item);
        }
    },
    checkMultiProperty: function(log, modules){
        var flag = false;
        if( this.preArray.length ){
            var lastItem = this.preArray.getLast();
            if( lastItem.data.type === "multiProperty" ) {
                var change = log.changeList[0];
                var lastChangeList = lastItem.data.changeList;
                if (lastChangeList[0].name === change.name) {
                    var moduleIdList = log.changeList.map(function (c) {
                        return c.module.json.id;
                    });
                    if( this.compareObjects( lastItem.moduleIdList, moduleIdList ) ){
                        var it;
                        while( this.nextArray.length ){
                            it = this.nextArray.pop();
                            it.destroy();
                        }

                        lastItem.data.changeList.each(function ( c, i ) {
                            c.toValue = log.changeList[i].toValue;
                        });

                        flag = true;
                    }
                }
            }
        }
        if( !flag ) {
            //console.log(log);
            var item = new MWF.PDHistory.PropertyMultiItem(this, log);
            item.load(modules);
            this.addItem(item);
        }
    },
    addItem: function(item){
        var it;
        while( this.nextArray.length ){
            it = this.nextArray.pop();
            it.destroy();
        }

        //删除上一个property的已经undo的subItem
        if( this.preArray.length ){
            it = this.preArray.getLast();
            if( it.data.type === "property" ){
                while( it.nextArray.length ){
                    var subit = it.nextArray.pop();
                    subit.destroy();
                }
            }
        }

        this.preArray.push(item);

        //大于最大条目数
        if( this.options.maxLength > 0 && this.preArray.length > this.options.maxLength ){
            this.destroyItem(this.preArray[0]);
        }
    },
    destroyItem: function(item){
        this.preArray.erase(item);
        item.destroy();
    },
    goto: function(item, notRedoItem){
        var it;
        if( item.status === "pre" ){
            it = this.preArray.getLast();
            while (it && item !== it){
                it.undo();
                this.nextArray.unshift(it); //插入到灰显数组前面
                this.preArray.pop(); //删除preArray最后一个
                it = this.preArray.getLast();
            }
            item.selectModule("undo");
        }else if( item.status === "next" ){
            if( this.preArray.length ){  //上一个property的subItem要redo一下
                it = this.preArray.getLast();
                if( it.data.type === "property" )it.redo();
            }
            it = this.nextArray[0];
            while (it && item !== it){
                it.redo();
                this.preArray.push(it); //插入到preArray数组最后
                this.nextArray.shift();
                it = this.nextArray[0];
            }
            item.redo( notRedoItem );
            this.preArray.push(item); //插入到preArray数组最后
            this.nextArray.shift();
            item.selectModule("redo");
        }
    },
    compareObjects: function(o, p, deep){
        if( o === p )return true;
        return JSON.stringify(o) === JSON.stringify(p);
    }
});

MWF.PDHistory = MWF.xApplication.process.ProcessDesigner.History;

MWF.PDHistory.Item = new Class({
    Implements: [Options, Events],
    options: {},
    initialize: function (history, log) {
        this.history = history;
        this.data = log;
        this.status = "pre";
        this.process = this.history.process;
        this.root = this.history.root;
    },
    load: function (module) {
        this.node = new Element("div", {
            styles : this._getItemStyle(),
            text: this._getText(),
            events: {
                click: this.comeHere.bind(this)
            }
        }).inject( this.history.node );
        this.node.setStyle("background-image", "url("+this.history.iconPath+ this.data.operation +".png)");
        this._afterLoad(module);
    },
    _afterLoad: function(){

    },
    _getItemStyle: function(){
        return this.history.css.itemNode;
    },
    _getText: function () {
        if( this.data.title )return this.data.title;
        var lp = MWF.APPPD.LP.processAction;
        var type = this.getTypeText();
        type = type ?  (" <" + type + "> ") : " ";
        return  ( lp[this.data.operation] || this.data.operation ) + type + (this.data.moduleId || "");
    },
    getType: function(){
        var type = (this.data.type || "").toLowerCase();
        switch ( type ) {
            case "activity": case "property":
                return (this.data.moduleType || "").capitalize();
            case "route":
                return (this.data.moduleType || "").capitalize();
            default:
                return type.capitalize();
        }
    },
    getTypeText: function(){
        var type = (this.data.type || "").toLowerCase();
        switch ( type ) {
            case "process": return MWF.APPPD.LP.process;
            case "route": return MWF.APPPD.LP.route;
            case "activity":
                var moduleType = (this.data.moduleType || "").toLowerCase();
                var tool = MWF.APPPD.LP.menu.newActivityType[moduleType];
                return tool || moduleType.capitalize();
            default:
                return "";
        }
    },
    comeHere: function ( e, notRedoThis ) {
        this.history.goto(this, notRedoThis);
    },
    undo: function () { //回退
        this.status = "next";
        this.node.setStyles( this.history.css.itemNode_undo );
        this._undo();
    },
    redo: function(){ //重做
        this.status = "pre";
        this.node.setStyles(this.history.css.itemNode_redo);
        this._redo();
    },
    _undo: function(){
    },
    _redo: function(){
    },

    destroy: function () {
        this.node.destroy();
        MWF.release(this);
    },
    changeJsonDate: function(json, name, value){
        var key = name.split(".");
        var len = key.length-1;
        key.each(function(n, i){
            if (i<len) {
                if (!json.hasOwnProperty(n)) json[n] = {};
                json = json[n];
            }
        }.bind(this));
        if( typeOf(value) === "null" ){
            delete json[key[len]];
        }else{
            json[key[len]] = value;
        }
    },
    getModuleById: function (id, type) {
        if( type === "route" ){

        }else{
            var modules = this.process[type+"s"];
            if( !modules )return null;
            return modules[id];
        }
    },
    getModule: function( log ){
        var type = log.type || log.json.type;
        if( type === "route" ){

        }else{
            var modules = this.process[type+"s"];
            if( !modules )return null;
            return modules[log.json.id];
        }
    },
    unselectModule: function () {
        this.process.unSelectedAll();
    },
    selectModule: function(type){
        this.process.unSelectedAll();
        this.process.showProperty();
    },
    deleteModuleList: function(){
        for( var i=this.data.toList.length-1; i>-1; i-- ){
            var to = this.data.toList[i];
            this._deleteModule( to );
        }
    },
    _deleteModule: function( log ){
        var module = this.getModule( log );
        if(module){
            if(module.property)module.property.destroy();
            module.destroy();
        }
    },
    loadModuleList: function(){
        for( var i=0; i<this.data.toList.length; i++ ) {
            var to = this.data.toList[i];
            this._loadModule( to );
        }
    },
    _loadModule: function( log ){
        var type = log.type || log.json.type;
        if( type === "route" ){

        }else{
            this.process.createActivityByData(log.json, log.type);
        }
    }
});

MWF.PDHistory.ActivityItem = new Class({
    Extends: MWF.PDHistory.Item,
    _afterLoad: function () {
        // if( this.data.toList && this.data.toList.length > 1 ){
        //     this.sortByPath(this.data.toList);
        // }
        // if( this.data.fromList && this.data.fromList.length > 1 ){
        //     this.sortByPath(this.data.fromList);
        // }
    },
    _getText: function () {
        if( this.data.title )return this.data.title;
        var lp = MWF.APPPD.LP.processAction;
        var type = this.getTypeText();
        type = type ?  (" <" + type + "> ") : " ";
        return  ( lp[this.data.operation] || this.data.operation ) + type + (this.data.name || "");
    },
    _undo: function(){
        switch (this.data.operation) {
            case "create":
                this.deleteModuleList();
                break;
            case "copy":
                this.deleteModuleList();
                break;
            case "move":
                this.moveModuleList(this.data.toList, this.data.fromList);
                break;
            case "delete":
                this.loadModuleList();
                break;
            case "cut":
                this.loadModuleList();
                break;
            case "paste":
                this.deleteModuleList();
                break;
        }
        this.unselectModule();
    },
    _redo: function(){
        switch (this.data.operation) {
            case "create":
                this.loadModuleList();
                break;
            case "copy":
                this.loadModuleList();
                break;
            case "move":
                this.moveModuleList(this.data.fromList, this.data.toList);
                break;
            case "delete":
                this.deleteModuleList();
                break;
            case "cut":
                this.deleteModuleList();
                break;
            case "paste":
                this.loadModuleList();
                break;
        }
        this.unselectModule();
    },
    selectModule: function(){
        var module = this.getModule( this.data.toList[0] );
        if( module ){
            module.selected();
        }else{
            this.process.unSelectedAll();
            this.process.showProperty();
        }
    },
    moveModuleList: function( starts, ends ){
        this.process.selectedActivitys = starts.map(function (log) {
            this.getModule(log);
        }.bind(this));
        for( var i=0; i<starts.length; i++ ){
            var module = this.getModule(starts[i]);
            if( module && ends[i] ){
                var endPos = ends[i].json.position.split(",").map( function (j) { return j.toFloat(); });
                var startPos = starts[i].json.position.split(",").map( function (j) { return j.toFloat(); });
                // module.activityMove(dx, dy, tox, toy);
                module.activityMoveStart();
                module.activityMove(endPos[0]-startPos[0], endPos[1]-startPos[1], endPos[0], endPos[1]);
                module.activityMoveEnd();
            }
        }
        this.process.selectedActivitys = [];
    }
});

MWF.PDHistory.PropertySingleItem = new Class({
    Extends: MWF.PDHistory.Item,
    load: function (module) {
        this.module = module;
        this.node = new Element("div", {
            styles : this._getItemStyle(),
            text: this._getText()
            // events: {
            //     click: this.comeHere.bind(this)
            // }
        }).inject( this.history.node );
        this.node.setStyle("background-image", "url("+this.history.iconPath+ "property.png)");
        this._afterLoad(module);
    },
    _getItemStyle: function(){
        return this.history.css.itemNode_property;
    },
    _afterLoad: function ( module ) {
        this.moduleIdList = [ this.data.moduleId ];
        this.nextArray = [];
        this.preArray = [];
        // this.path = this.data.path || this.history.getPath( module.node );
        this.addSubItem( this.data );
    },
    getModule: function(){
        var module, dom = this.getDomByPath( this.path );
        if(dom)module = dom.retrieve("module");
        if( !module && this.module && this.process.moduleList.contains(this.module) ){
            module = this.module;
        }
        return module;
    },
    _getText: function () {
        if( this.data.title )return this.data.title;
        var lp = MWF.APPPD.LP.processAction;
        var type = this.getType();
        type = type ?  (" <" + type + "> ") : " ";
        return lp.property + " " + type + this.data.moduleId;
    },
    destroy: function () {
        var si = this.preArray.pop();
        while (si){
            si.destroy();
            si = this.preArray.pop(); //删除preArray最后一个
        }

        si = this.nextArray.pop();
        while (si){
            si.destroy();
            si = this.nextArray.pop(); //删除nextArray最后一个
        }

        this.node.destroy();
        MWF.release(this);
    },
    undo: function () { //回退
        this.status = "next";
        this.node.setStyles( this.history.css.itemNode_property_undo );
        this._undo();
    },
    redo: function( notRedoItem ){ //重做
        this.status = "pre";
        this.node.setStyles(this.history.css.itemNode_property_redo);
        if( !notRedoItem )this._redo();
    },
    _undo: function () {
        // for( var i=this.subItemList.length-1; i > -1; i-- ){
        //     var subItem = this.subItemList.length[i];
        //     subItem.undo();
        // }
        var si = this.preArray.getLast();
        var flag = false;
        while (si){
            si.undo();
            flag = true;
            this.nextArray.unshift(si); //插入到灰显数组前面
            this.preArray.pop(); //删除preArray最后一个
            si = this.preArray.getLast();
        }
        if(flag){
            var module = this.getModule();
            if( module && module.property ){
                module.property.reset();
            }
        }
    },
    _redo: function () {
        // for( var i=0; i < this.subItemList.length; i++ ){
        //     var subItem = this.subItemList.length[i];
        //     subItem.redo();
        // }
        var si = this.nextArray[0];
        var flag = false;
        while (si){
            si.redo();
            flag = true;
            this.preArray.push(si); //插入到preArray数组最后
            this.nextArray.shift();
            si = this.nextArray[0];
        }
        if(flag){
            var module = this.getModule();
            if( module && module.property ){
                module.property.reset();
            }
        }
    },
    getLastSubItem: function(){
        return this.preArray.getLast();
    },
    destroySubItem: function(subItem){
        this.preArray.erase( subItem );
        subItem.destroy();
    },
    addSubItem: function ( data ) {
        var subItem = new MWF.PDHistory.PropertySingleItem.SubItem(this, data);
        subItem.load();
        this._addSubItem(subItem);
    },
    _addSubItem: function(subItem){
        var si;
        while( this.nextArray.length ){
            si = this.nextArray.pop();
            si.destroy();
        }

        this.preArray.push(subItem);
    },
    getNext: function(subItem){
        var index = this.nextArray.indexOf(subItem);
        if( index < 0 )return null;
        if( index === this.nextArray.length - 1 )return null;
        return this.nextArray[ index + 1 ];
    },
    goto: function(subItem){
        var si, si_next, notSetEditStyle = false;
        if( subItem.status === "pre" ){
            si = this.preArray.getLast();
            while (si && subItem !== si){
                //如果下一个subitem的名称和现在一样，不设置SetEditStyle
                si_next = this.preArray.length>1 ? this.preArray[this.preArray.length-2] : null;
                notSetEditStyle = si_next && (subItem !== si_next) && ( si_next.data.changeList[0].name === si.data.changeList[0].name );

                si.undo( notSetEditStyle );
                this.nextArray.unshift(si); //插入到灰显数组前面
                this.preArray.pop(); //删除preArray最后一个
                si = this.preArray.getLast();
            }
        }else if( subItem.status === "next" ){
            var subItemNext = this.getNext(subItem);
            si = this.nextArray[0];
            while (si && subItem !== si){
                //如果下一个subitem的名称和现在一样，不设置SetEditStyle
                si_next = this.nextArray.length>1 ? this.nextArray[1] : null;
                notSetEditStyle = si_next && (subItemNext !== si_next) && ( si_next.data.changeList[0].name === si.data.changeList[0].name );

                si.redo( notSetEditStyle );
                this.preArray.push(si); //插入到preArray数组最后
                this.nextArray.shift();
                si = this.nextArray[0];
            }
            subItem.redo();
            this.preArray.push(subItem); //插入到preArray数组最后
            this.nextArray.shift();
        }
        var module = this.getModule();
        if( module && module.property ){
            module.property.reset();
        }
    },
    selectModule: function(){
        this._selectModule(this.path);
    }
});

MWF.PDHistory.PropertySingleItem.SubItem = new Class({
    Extends: MWF.PDHistory.Item,
    initialize: function (item, log) {
        this.parentItem = item;
        this.history = item.history;
        this.data = log;
        this.status = "pre";
        this.process = this.history.process;
        this.root = this.history.root;
    },
    load: function () {
        this.node = new Element("div", {
            styles: this.history.css.subItemNode,
            text: this.getText(),
            events: {
                click: this.comeHere.bind(this)
            }
        }).inject(this.history.node);
    },
    getText: function () {
        return this.data.changeList[0].compareName || this.data.changeList[0].name;
    },
    comeHere: function (e) {
        this.parentItem.comeHere( null, true );
        this.parentItem.goto( this );
    },
    undo: function ( notSetEditStyle ) { //回退
        this.status = "next";
        this.node.setStyles( this.history.css.subItemNode_undo );
        this._undo( notSetEditStyle );
    },
    redo: function( notSetEditStyle ){ //重做
        this.status = "pre";
        this.node.setStyles( this.history.css.subItemNode_redo );
        this._redo( notSetEditStyle );
    },
    _undo: function ( notSetEditStyle ) {
        //console.log( "_undo", this.data);
        var module = this.parentItem.getModule();
        if (module) {
            var json = module.json;
            for( var i=this.data.changeList.length-1; i>-1; i-- ){
                var change = this.data.changeList[i];
                if (change.name === "id") {
                    json.id = change.fromValue;
                    this.process.json.moduleList[change.fromValue] = json;
                    delete this.process.json.moduleList[change.toValue];
                }else{
                    // json[change.name] = change.fromValue;
                    this.changeJsonDate(json, change.name, change.fromValue);
                    module.setPropertiesOrStyles(change.name, change.toValue);
                    this.setScriptJsEditor(module, change.name, change.fromValue);
                }
                if(!notSetEditStyle && !this.data.notSetEditStyle){
                    //console.log("change.name")
                    module._setEditStyle(change.name, null, change.toValue);
                }
            }
        }
    },
    _redo: function (notSetEditStyle) {
        //console.log( "_redo", this.data);
        var module = this.parentItem.getModule();
        if (module) {
            var json = module.json;
            for( var i=0; i<this.data.changeList.length; i++ ){
                var change = this.data.changeList[i];
                if( change.name === "id" ){
                    json.id = change.toValue;
                    this.process.json.moduleList[ change.toValue ] = json;
                    delete this.process.json.moduleList[ change.fromValue ];
                }else{
                    // json[change.name] = change.toValue;
                    this.changeJsonDate(json, change.name, change.toValue);
                    module.setPropertiesOrStyles(change.name, change.fromValue);
                    this.setScriptJsEditor(module, change.name, change.toValue);
                }
                if(!notSetEditStyle && !this.data.notSetEditStyle){
                    module._setEditStyle(change.name, null, change.fromValue);
                }
            }
        }
    },
    setScriptJsEditor: function (module, name, value) {
        if( module )return;
        var jsEditor = module.getScriptJsEditor(name);
        if(jsEditor){
            var v = "";
            switch ( o2.typeOf(value)) {
                case "object":
                    if( o2.typeOf(value.code) === "string" ){
                        v = value.code;
                    }else if( o2.typeOf(value.actionScript) === "string" ){
                        v = value.actionScript;
                    }
                    break;
                case "string":
                    v = value;
                    break;
            }
            jsEditor.setValue( v, true );
        }
    }
});

MWF.PDHistory.PropertyMultiItem = new Class({
    Extends: MWF.PDHistory.Item,
    load: function () {
        this.node = new Element("div", {
            styles: this._getItemStyle(),
            text: this._getText(),
            events: {
                click: this.comeHere.bind(this)
            }
        }).inject(this.history.node);
        this.node.setStyle("background-image", "url(" + this.history.iconPath + "property.png)");

        this.modules = [];
        this.moduleIdList = [];
        this.data.changeList.each(function (log) {
            // log.path = this.history.getPath(log.module.node);
            this.modules.push( log.module );
            this.moduleIdList.push( log.module.json.id );
        }.bind(this))
    },
    _getItemStyle: function () {
        return this.history.css.itemNode;
    },
    getModule: function ( log ) {
        var module, dom = this.getDomByPath(log.path);
        if (dom) module = dom.retrieve("module");
        if( !module && log.module ){
            module = log.module;
        }
        return module;
    },
    _getText: function () {
        if (this.data.title) return this.data.title;
        var lp = MWF.APPPD.LP.processAction;
        return lp.batchModify + this.data.changeList[0].name + lp.property;
    },
    _undo: function () {
        //console.log( "_undo", this.data);
        for( var i=this.data.changeList.length-1; i>-1; i-- ){
            var change = this.data.changeList[i];
            var module = this.getModule( change );
            if( module ){
                var json = module.json;
                this.changeJsonDate(json, change.name, change.fromValue);
                module.setPropertiesOrStyles(change.name, change.toValue);
                module._setEditStyle(change.name, null, change.toValue);
                if( module.property )module.property.reset();
            }
        }
    },
    _redo: function () {
        //console.log( "_redo", this.data);
        for( var i=0; i<this.data.changeList.length; i++ ){
            var change = this.data.changeList[i];
            var module = this.getModule( change );
            if( module ) {
                var json = module.json;
                this.changeJsonDate(json, change.name, change.toValue);
                module.setPropertiesOrStyles(change.name, change.fromValue);
                module._setEditStyle(change.name, null, change.fromValue);
                if( module.property )module.property.reset();
            }
        }
    }
});

o2.xDesktop.requireApp("process.Xform", "$ElModule", null, false);
o2.xDesktop.requireApp("process.Xform", "$Input", null, false);

o2.xApplication.process.Xform.$Elinput = o2.APP$Elinput = new Class({
    Implements: [Events],
    Extends: MWF.APP$ElModule,
});
Object.assign(o2.APP$Elinput.prototype, o2.APP$Input.prototype);

Object.assign(o2.APP$Elinput.prototype, {
    isReadonly : function(){
        return !!(this.readonly || this.json.isReadonly || this.form.json.isReadonly || this.isSectionMergeRead() );
    },
    reload: function(){
        if (!this.vm) return;

        var node = this.vm.$el;
        this.vm.$destroy();
        node.empty();

        this.vm = null;

        this.vueApp = null;

        this._loadUserInterface();
    },
    __setValue: function(value){
        this.moduleValueAG = null;
        this._setBusinessData(value);
        this.json[this.json.$id] = value;
        this.__setReadonly(value);
        this.fieldModuleLoaded = true;
        return value;
    },
    __setData: function(data){
        var old = this.getInputData();
        this._setBusinessData(data);
        this.json[this.json.$id] = data;
        this.__setReadonly(data);
        if (old!==data) this.fireEvent("change");
        this.moduleValueAG = null;
        this.validationMode();
    },
    __setReadonly: function(data){
        if (this.isReadonly()) {
            this.node.set("text", data);
            if( this.json.elProperties ){
                this.node.set(this.json.elProperties );
            }
            if (this.json.elStyles){
                this.node.setStyles( this._parseStyles(this.json.elStyles) );
            }

            this.fireEvent("postLoad");
            if( this.moduleSelectAG && typeOf(this.moduleSelectAG.then) === "function" ){
                this.moduleSelectAG.then(function () {
                    this.fireEvent("load");
                    this.isLoaded = true;
                }.bind(this));
            }else{
                this.fireEvent("load");
                this.isLoaded = true;
            }

        }
    },
    getInputData: function(){
        return this.json[this.json.$id];
    },
    // _getVueModelBindId: function(){
    //     if (this.json.id.indexOf("..")!==-1){
    //         this.json["$id"] ="__"+this.json.id.replace(/\.\./g, "_")
    //     }else{
    //         this.json["$id"] = this.json.id;
    //     }
    // },
    _loadNodeEdit: function(){
        var id = (this.json.id.indexOf("..")!==-1) ? this.json.id.replace(/\.\./g, "_") : this.json.id;
        this.json["$id"] = (id.indexOf("-")!==-1) ? id.replace(/-/g, "_") : id;
        this.node.appendHTML(this._createElementHtml(), "before");
        var input = this.node.getPrevious();

        this.node.destroy();
        this.node = input;
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
        this.node.addClass("o2_vue");
        this._createVueApp();
    },
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1 && this.options.elEvents.indexOf(key)===-1){
                    this.node.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    getValue: function(){
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = this._getBusinessData();
        if (value || value===false || value===0){
            return value;
        }else{
            value = this._computeValue();
            return (o2.typeOf(value)!=="null") ? value : "";
        }
        // if (!value) value = this._computeValue();
        // return (o2.typeOf(value)!=="null") ? value : "";
        //return value || "";
    },
    _afterLoaded: function(){}
})

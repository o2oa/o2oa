o2.xDesktop.requireApp("process.Xform", "$ElModule", null, false);
o2.xDesktop.requireApp("process.Xform", "$Input", null, false);

o2.xApplication.process.Xform.$Elinput = o2.APP$Elinput = new Class({
    Implements: [Events],
    Extends: MWF.APP$ElModule,
});
Object.assign(o2.APP$Elinput.prototype, o2.APP$Input.prototype);

Object.assign(o2.APP$Elinput.prototype, {
    __setValue: function(value){
        this.moduleValueAG = null;
        this._setBusinessData(value);
        this.json[this.json.id] = value;
        if (this.readonly || this.json.isReadonly) this.node.set("text", value);
        this.fieldModuleLoaded = true;
        return value;
    },
    __setData: function(data){
        var old = this.getInputData();
        this._setBusinessData(data);
        this.json[this.json.id] = data;
        if (this.readonly || this.json.isReadonly) this.node.set("text", value);
        if (old!==data) this.fireEvent("change");
        this.moduleValueAG = null;
        this.validationMode();
    },
    getInputData: function(){
        return this.json[this.json.id];
    },
    _loadNodeEdit: function(){
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
    _afterLoaded: function(){}
})

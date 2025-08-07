MWF.xDesktop.requireApp('process.Xform', '$Module', null, false);
MWF.xApplication.process.Xform.OOPagination = MWF.APPOOPagination = new Class({
    Implements: [Events],
    Extends: MWF.APP$Module,
    iconStyle: 'textFieldIcon',

    _loadUserInterface: function () {
        if (!this.isReadable){
            this.node.setStyle('display', 'none');
            return '';
        }
        
        this.node.set({
            id: this.json.id,
            MWFType: this.json.type,
        });
        if (this.json.properties) {
            this.node.set(this.json.properties);
        }
        if (this.json.styles) {
            this.node.setStyles(this.json.styles);
        }
        this.node.setAttribute('current', "1");
        this.node.setAttribute('first', this.json.first);
        this.node.setAttribute('last', this.json.last);
        this.node.setAttribute('pages', this.json.pages);
        this.node.setAttribute('jumper', this.json.jumper);
        this.node.setAttribute('jumper-text', this.json.jumperText);
    },
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    this.node.addEventListener(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this))
                    // this.node.addEvent(key, function(event){
                    //     return this.form.Macro.fire(e.code, this, event);
                    // }.bind(this));
                }
            }
        }.bind(this));
    },
});

MWF.xDesktop.requireApp('process.Xform', '$Module', null, false);
MWF.xApplication.process.Xform.OOPagination = MWF.APPOOPagination = new Class({
    Implements: [Events],
    Extends: MWF.APP$Module,
    iconStyle: 'textFieldIcon',

    _loadUserInterface: function () {
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
        this.node.setAttribute('first', this.json.first);
        this.node.setAttribute('last', this.json.last);
        this.node.setAttribute('pages', this.json.pages);
        this.node.setAttribute('jumper', this.json.jumper);
        this.node.setAttribute('jumper-text', this.json.jumperText);
    },
});

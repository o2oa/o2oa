MWF.xDesktop.requireApp('process.Xform', 'Button', null, false);
MWF.xApplication.process.Xform.OOButton = MWF.APPOOButton = new Class({
    Implements: [Events],
    Extends: MWF.APPButton,
    iconStyle: 'textFieldIcon',

    _loadUserInterface: function () {
        // var button = new Element('oo-button');
        // button.inject(this.node, "after");
        // this.node.destroy();
        // this.node = button;

        if (!this.isReadable || !this.isEditable) {
            this.node.setStyle('display', 'none');
        } else {
            this.node.set({
                id: this.json.id,
                MWFType: this.json.type,
            });
            this.node.setAttribute('text', this.json.name || this.json.id);
            if (this.json.properties) {
                this.node.set(this.json.properties);
            }
            if (this.json.styles) {
                this.node.setStyles(this.json.styles);
            }
            this.node.setAttribute('type', this.json.appearance || 'default');
            if (this.json.leftIcon) this.node.setAttribute('left-icon', this.json.leftIcon);
            if (this.json.rightIcon) this.node.setAttribute('right-icon', this.json.rightIcon);
            if (this.json.disabled) this.node.setAttribute('disabled', this.json.disabled);
        }
    },
});

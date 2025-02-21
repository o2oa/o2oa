MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp('process.FormDesigner', 'Module.$Element', null, false);
MWF.xApplication.process.FormDesigner.Module.OOPagination = MWF.FCOOPagination = new Class({
    Extends: MWF.FC$Element,
    Implements: [Options, Events],
    options: {
        style: 'default',
        type: 'OOPagination',
        path: '../x_component_process_FormDesigner/Module/OOPagination/',
        propertyPath: '../x_component_process_FormDesigner/Module/OOPagination/OOPagination.html',
    },
    initialize: function (form, options) {
        this.setOptions(options);

        this.path = this.options.path;
        this.cssPath = this.path + this.options.style + '/css.wcss';

        this._loadCss();
        this.moduleType = 'element';
        this.moduleName = this.options.type;

        this.form = form;
        this.container = null;
        this.containerNode = null;
    },
    _createMoveNode: function () {
        this.moveNode = new Element('oo-pagination', {
            MWFType: 'OOPagination',
            id: this.json.id,
            total: '300',
            jumper: this.json.jumper,
            first: this.json.first,
            last: this.json.last,
            'jumper-text': this.json.jumperText,
            styles: this.css.moduleNodeMove,
            events: {
                selectstart: function () {
                    return false;
                },
            },
        }).inject(this.form.container);
        this.moveNode.setAttribute('text', this.json.name || this.json.id);
    },
    _setEditStyle_custom: function (name) {
        if (name === 'first') {
            this.node.setAttribute('first', this.json.first);
        }
        if (name === 'last') {
            this.node.setAttribute('last', this.json.last);
        }
        if (name === 'jumper') {
            this.node.setAttribute('jumper', this.json.jumper);
        }
        if (name === 'jumperText') {
            this.node.setAttribute('jumper-text', this.json.jumperText);
        }
        if (name === 'pages') {
            this.node.setAttribute('pages', this.json.pages);
        }
    },
});

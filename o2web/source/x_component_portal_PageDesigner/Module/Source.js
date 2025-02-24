MWF.xApplication.portal.PageDesigner.Module.Source = MWF.PCSource = new Class({
	Extends: MWF.FCDiv,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Source/source.html"
	},

	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_portal_PageDesigner/Module/Source/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Source/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "Source";

		this.Node = null;
		this.form = form;
	},
    _createMoveNode: function(){
        this.moveNode = new Element("div", {
            "MWFType": "source",
            "id": this.json.id,
            "styles": this.css.moduleNodeMove,
            "events": {
                "selectstart": function(){
                    return false;
                }
            }
        }).inject(this.form.container);
    },
	_dragIn: function(module){
		module.onDragModule = this;
		if (!this.Component) module.inContainer = this;
		module.parentContainer = this;
		module.nextModule = null;

		this.node.setStyles({"border": "1px solid #ffa200"});

		if (module.controlMode){
			if (module.copyNode) module.copyNode.hide();
		}else{
			var copyNode = module._getCopyNode(this);
			copyNode.show();
			if( this.pagination ){
				copyNode.inject(this.pagination, 'before');
			}else{
				copyNode.inject(this.node);
			}
		}
	},
	_resetModuleDomNode: function(){
		this.checkPagination();
	},
	_setEditStyle_custom: function (name, obj, oldValue) {
		if (name==="templateType"){
			if (this.form.templateStyles){
				var moduleStyles = this.form.templateStyles[this.moduleName];
				if (moduleStyles) {
					if (oldValue){
						if (moduleStyles[oldValue]){
							this.removeStyles(moduleStyles[oldValue].styles, "styles");
							this.removeStyles(moduleStyles[oldValue].styles, "properties");
						}
					}

					if (moduleStyles[this.json.templateType]){
						if (moduleStyles[this.json.templateType].styles) this.copyStyles(moduleStyles[this.json.templateType].styles, "styles");
						if (moduleStyles[this.json.templateType].styles) this.copyStyles(moduleStyles[this.json.templateType].properties, "properties");
					}

					this.setPropertiesOrStyles("styles");
					this.setPropertiesOrStyles("properties");

					this.reloadMaplist();
				}
			}
		}
		if( name === 'usePagination' ) {
			this.checkPagination();
		}
		if (name === 'first') {
			this.pagination && this.pagination.setAttribute('first', this.json.first);
		}
		if (name === 'last') {
			this.pagination && this.pagination.setAttribute('last', this.json.last);
		}
		if (name === 'jumper') {
			this.pagination && this.pagination.setAttribute('jumper', this.json.jumper);
		}
		if (name === 'jumperText') {
			this.pagination && this.pagination.setAttribute('jumper-text', this.json.jumperText);
		}
		if (name === 'pages') {
			this.pagination && this.pagination.setAttribute('pages', this.json.pages);
		}
		if(name === 'pagnationStyles'){
			this.pagination && this.pagination.setStyles(this.json.pagnationStyles);
		}
		if(name === 'pagnationProperties'){
			this.pagination && this.pagination.set(this.json.pagnationProperties);
		}
	},
	checkPagination: function (){
		this.pagination = this.node.getElement('oo-pagination');
		if( this.json.usePagination ){
			if(!this.pagination)this.pagination = new Element('oo-pagination', {
				MWFType: 'OOPagination',
				total: '300',
				jumper: this.json.jumper,
				first: this.json.first,
				last: this.json.last,
				'jumper-text': this.json.jumperText,
				events: {
					selectstart: function () {
						return false;
					},
				},
			}).inject(this.node);
			this.pagination.setStyles(this.json.pagnationStyles);
			this.pagination.set(this.json.pagnationProperties);
		}else{
			if( this.pagination ){
				this.pagination.destroy();
				this.pagination = null;
			}
		}
	}
});

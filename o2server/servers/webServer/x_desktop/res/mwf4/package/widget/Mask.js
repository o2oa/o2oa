MWF.widget = MWF.widget || {};
MWF.require("MWF.widget.Common", null, false);
MWF.widget.Mask = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default",
		"zIndex": 100,
		"loading": true
	},
	initialize: function(options){
		this.setOptions(options);
		this.path = MWF.defaultPath+"/widget/$Mask/";
		this.cssPath = MWF.defaultPath+"/widget/$Mask/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this._createMaskNodes();
	},

	_createMaskNodes: function(){
		this.container = new Element("div", {
			"styles": this.css.container
		});
		this.container.setStyle("z-index", this.options.zIndex);

		this.maskBar = new Element("iframe", {
			"styles": this.css.mask
		});
		this.maskBar.setStyle("z-index", this.options.zIndex);

		this.backgroundBar = new Element("div", {
			"styles": this.css.backgroundBar
		});
		this.backgroundBar.setStyle("z-index", this.options.zIndex+1);
		

		this.loadBar = new Element("div", {
			"styles": this.css.loadingBar,
			"html": "<table width=\"80%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr valign=\"middle\" height=\"30\"><td align=\"right\"><img src=\""+MWF.defaultPath+"/widget/$Mask/"+this.options.style+"/loading.gif\" /></td><td align=\"center\">loading...</td></tr></table>"
		});
		this.loadBar.setStyle("z-index", this.options.zIndex+2);

		this.maskBar.inject(this.container);
		this.backgroundBar.inject(this.container);
		this.loadBar.inject(this.container);
	},
	
	hide: function(callBack){
		var morph = new Fx.Morph(this.container, {duration: 500});

		morph.start({
			"opacity": 0
		}).chain(function(){
			this.container.destroy();
            if (callBack) callBack();
		}.bind(this));
	},
	load: function(){
		if (this.fireEvent("queryLoad")){
			
			this.container.inject($(document.body));

			if (!this.options.loading){
				this.loadBar.setStyle("display", "none");
			}else{
				this.loadBar.setStyle("display", "block");
				
				//var size = $(window).getSize();
                var size = this.container.getSize();
				var tmpLeft = (size.x-120)/2;
				var tmpTop = (size.y-30)/2;
				if (tmpTop<=0) tmpTop = (window.screen.height-30)/2;
				this.loadBar.setStyle("left", ""+tmpLeft+"px");
				this.loadBar.setStyle("top", ""+tmpTop+"px");
			}
			this.fireEvent("postLoad");
		}
	},
    loadNode: function(node){
        if (this.fireEvent("queryLoad")){

            this.container.inject($(node));

            if (!this.options.loading){
                this.loadBar.setStyle("display", "none");
            }else{
                this.loadBar.setStyle("display", "block");

                var size = $(node).getSize();
                //var tmpLeft = (size.x-120)/2;
                var tmpLeft = (size.x)/2;
                var tmpTop = (size.y-30)/2;
                if (tmpTop<=0) tmpTop = (window.screen.height-30)/2-100;
                this.loadBar.setStyle("left", ""+tmpLeft+"px");
                this.loadBar.setStyle("top", ""+tmpTop+"px");
            }
            this.fireEvent("postLoad");
        }
    }
});
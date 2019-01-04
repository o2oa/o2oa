o2.widget = o2.widget || {};
o2.require("o2.widget.Mask", null, false);
o2.widget.MaskNode = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Mask,
	options: {
		"style": "node"
	},
	node: null,
	initialize: function(node, options){
		this.parent(options);
		this.node = node;
	},
	load: function(){
		if (this.fireEvent("queryLoad")){
			
			this.container.inject($(this.node));

            var size = $(this.node).getSize();
            var position = $(this.node).getPosition();
            var markPosition = this.container.getPosition();


            this.container.setStyles({
                "width": size.x,
                "height": size.y,
                "top": position.y - markPosition.y+"px"
            });

            this.maskBar.setStyles({
                "width": size.x,
                "height": size.y
            });
            this.backgroundBar.setStyles({
                "width": size.x,
                "height": size.y
            });

			if (!this.options.loading){
				this.loadBar.setStyle("display", "none");
			}else{
				this.loadBar.setStyle("display", "block");

				var tmpLeft = (size.x-120)/2;
				var tmpTop = (size.y-30)/2;
				this.loadBar.setStyle("left", ""+tmpLeft+"px");
				this.loadBar.setStyle("top", ""+tmpTop+"px");
            }
			this.fireEvent("postLoad");
		}
	}
});
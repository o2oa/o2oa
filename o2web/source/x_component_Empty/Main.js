MWF.xApplication.Empty.options.multitask = true;
MWF.xApplication.Empty.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "Empty",
		"icon": "icon.png",
		"width": "400",
		"height": "700",
		"isResize": false,
		"isMax": false,
		"title": MWF.xApplication.Empty.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Empty.LP;
	},
	loadApplication: function(callback){
        //for (var i=0; i<3000; i++){
        //    var node = new Element("div", {"text": i}).inject(this.content);
        //    this["x"+i] = node;
        //    node.tmp = this["x"+i];
        //}

        //this.content

		this.content.set("html", "<div style='color:#ff0000; padding: 30px; text-align: center; font-size: 24px;'>hello word</div>");
        var d = new Date();
        console.log(d.getTime());
	}
});

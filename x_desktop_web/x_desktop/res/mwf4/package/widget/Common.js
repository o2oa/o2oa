MWF.widget = MWF.widget || {};
MWF.widget.css = {};
MWF.widget.Common = new Class({
	Implements: [Options, Events],
	options: {},
	initialize: function(options){
		this.setOptions(options);
	},
	_loadCss: function(){
        var key = encodeURIComponent(this.cssPath);
        if (MWF.widget.css[key]){
            this.css = MWF.widget.css[key];
        }else{
            this.cssPath = (this.cssPath.indexOf("?")!=-1) ? this.cssPath+"&v="+COMMON.version : this.cssPath+"?v="+COMMON.version;
            var r = new Request.JSON({
                url: this.cssPath,
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function(responseJSON, responseText){
                    this.css = responseJSON;
                    MWF.widget.css[key] = responseJSON;
                }.bind(this),
                onError: function(text, error){
                    alert(error + text);
                }
            });
            r.send();
        }
	},
	setLayoutStyle: function(node, classes, nodes){
		var styleNode = node || this.node;
		
		var elements = styleNode.getElements(".GOES");
		elements.each(function(item){
			var id = item.get("id");
			var styles = this.css[id];
			if (styles){
				item.setStyles(styles);
			}
			var idx = classes.indexOf(id);
			if (idx!=-1){
				this[nodes[idx]] = item;
			}
			item.removeProperty("id");
		}.bind(this));
	},
	setScrollBar: function(node, style, offset, callback){
		if (!style) style = "default";
		if (!offset){
			offset = {
				"V": {"x": 0, "y": 0},
				"H": {"x": 0, "y": 0}
			};
		};
		MWF.require("MWF.widget.ScrollBar", function(){
			new MWF.widget.ScrollBar(node, {
				"style": style,
				"offset": offset
			});
			if (callback) callback();
		});
		return false;
	}
});
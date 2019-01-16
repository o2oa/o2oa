o2.widget = o2.widget || {};
o2.widget.css = {};
o2.widget.Common = new Class({
	Implements: [Options, Events],
	options: {},
	initialize: function(options){
		this.setOptions(options);
	},
	_loadCss: function(reload){
        var key = encodeURIComponent(this.cssPath);
        if (!reload && o2.widget.css[key]){
            this.css = o2.widget.css[key];
        }else{
            this.cssPath = (this.cssPath.indexOf("?")!=-1) ? this.cssPath+"&v="+o2.version.v : this.cssPath+"?v="+o2.version.v;
            var r = new Request.JSON({
                url: this.cssPath,
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function(responseJSON, responseText){
                    this.css = responseJSON;
                    o2.widget.css[key] = responseJSON;
                }.bind(this),
                onError: function(text, error){
                    alert(error + text);
                }
            });
            // var r = new Request({
            //     url: this.cssPath,
            //     secure: false,
            //     async: false,
            //     method: "get",
            //     noCache: false,
            //     onSuccess: function(responseText, responseXML){
            //         var f = eval("(function(){return function(){\n return "+responseText+"\n}})();");
            //         this.css = f.apply(this);
            //         o2.widget.css[key] = this.css;
            //     }.bind(this),
            //     onError: function(text, error){
            //         alert(error + text);
            //     }
            // });
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
		o2.require("o2.widget.ScrollBar", function(){
			new o2.widget.ScrollBar(node, {
				"style": style,
				"offset": offset
			});
			if (callback) callback();
		});
		return false;
	}
});
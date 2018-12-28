o2.widget = o2.widget || {};
o2.widget.codemirror = {
	"codemirror_css": COMMON.contentPath+"/res/framework/codemirror/lib/codemirror.css",
	"codemirror": COMMON.contentPath+"/res/framework/codemirror/lib/codemirror.js",
	"mode": {
		"javascript": COMMON.contentPath+"/res/framework/codemirror/mode/javascript/javascript.js",
		"css": COMMON.contentPath+"/res/framework/codemirror/mode/css/css.js",
		"html": COMMON.contentPath+"/res/framework/codemirror/mode/htmlmixed/htmlmixed.js",
		"java": COMMON.contentPath+"/res/framework/codemirror/mode/java/java.js",
		"xml": COMMON.contentPath+"/res/framework/codemirror/mode/xml/xml.js",
		"php": COMMON.contentPath+"/res/framework/codemirror/mode/php/php.js"
	},
	"addon": {
		
		
	},
	"load": function(callback){
		var jsLoaded = false;
		var cssLoaded = false;
		COMMON.AjaxModule.load(this.codemirror, function(){
			jsLoaded = true;
			this.checkLoaded([jsLoaded, cssLoaded], callback);
		}.bind(this));
		COMMON.AjaxModule.loadCss(this.codemirror_css, function(){
			cssLoaded = true;
			this.checkLoaded([jsLoaded, cssLoaded], callback);
		}.bind(this));
	},
	loadJavascript: function(callback){
		this.loadMode("javascript", callback);
	},
	loadMode: function(name, callback){
		var url = this.mode[name];
		if (url){
			COMMON.AjaxModule.load(url, function(){
				if (callback) callback();
			});
		}
	},
	checkLoaded: function(check, callback){
		if (check.indexOf(false)==-1){
			if (callback) callback();
		}
	}
	
};





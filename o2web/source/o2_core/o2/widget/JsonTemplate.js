o2.widget = o2.widget || {};
o2.widget.JsonTemplate = new Class({
	Implements: [Options, Events],
	options: {},
	initialize: function(json, html, options){
		this.setOptions(options);
		
		this.json = json;
		this.html = html;
	},
	load: function(){
		if (this.fireEvent("queryLoad")){
			
			this.setContentText();
            this.setContentFun();
            //this.setContentBoolean();
            //this.setContentNumber();
			this.setContentEach();
			
			this.fireEvent("postLoad");
		}
		return this.html;
	},
    //setContentBoolean: function(){
    //    var regexp = /(boolean\{).+?\}/g;
    //    var r = this.html.match(regexp);
    //    if(r){
    //        if (r.length){
    //            for (var i=0; i<r.length; i++){
    //                var text = r[i].substr(0,r[i].lastIndexOf("}"));
    //                text = text.substr(text.indexOf("{")+1,text.length);
    //
    //                var value = this.getJsonContentBoolean(this.json ,text);
    //                this.html = this.html.replace(/(boolean\{).+\}/,value);
    //            }
    //        }
    //    }
    //},
    //setContentNumber: function(){
    //    var regexp = /(number\{).+?\}/g;
    //    var r = this.html.match(regexp);
    //    if(r){
    //        if (r.length){
    //            for (var i=0; i<r.length; i++){
    //                var text = r[i].substr(0,r[i].lastIndexOf("}"));
    //                text = text.substr(text.indexOf("{")+1,text.length);
    //
    //                var value = this.getJsonContentNumber(this.json ,text);
    //                this.html = this.html.replace(/(number\{).+\}/,value);
    //            }
    //        }
    //    }
    //},
	setContentText: function(){
		var regexp = /(text\{).+?\}/g;
		var r = this.html.match(regexp);
		if(r){
			if (r.length){
				for (var i=0; i<r.length; i++){
					var text = r[i].substr(0,r[i].lastIndexOf("}"));
					text = text.substr(text.indexOf("{")+1,text.length);
					
					var value = this.getJsonContent(this.json ,text);
					this.html = this.html.replace(/(text\{).+?\}/,value);
				}
			}
		}
	},
    setContentFun: function(){
        var regexp = /(fun\{).+?\}/g;
        var r = this.html.match(regexp);
        if(r){
            if (r.length){
                for (var i=0; i<r.length; i++){
                    var text = r[i].substr(0,r[i].lastIndexOf("}"));
                    text = text.substr(text.indexOf("{")+1,text.length);

                    var value = this.getJsonFunContent(this.json ,text);
                    this.html = this.html.replace(/(fun\{).+?\}/,value);
                }
            }
        }
    },
    //getJsonContentBoolean: function(json, text){
    //    debugger;
    //    o2.tempContent = json;
    //    var jsonStr = text.replace(/\$/g, "o2.tempContent");
    //
    //    o2.tempReturn = "";
    //    Browser.exec("o2.tempReturn ="+jsonStr);
    //    return (o2.tempReturn) ? true : false;
    //},
    //getJsonContentNumber: function(json, text){
    //    o2.tempContent = json;
    //    var jsonStr = text.replace(/\$/g, "o2.tempContent");
    //
    //    o2.tempReturn = "";
    //    Browser.exec("o2.tempReturn ="+jsonStr);
    //    return (o2.tempReturn).toInt();
    //},
	getJsonContent: function(json, text){
		//o2.tempContent = json;
		//var jsonStr = text.replace(/\$/g, "o2.tempContent");
		//o2.tempReturn = "";
		//Browser.exec("o2.tempReturn ="+jsonStr);
		//return o2.tempReturn || "";
        var $ = json;
        var f = eval("(x = function($){\n return "+text+";\n})");
        returnValue = f.apply(json, [$]);
        if (returnValue===undefined) returnValue="";
        returnValue = returnValue.toString();
        return returnValue || "";
	},
    getJsonFunContent: function(json, text){
        var $ = json;
        var f = eval("(x = function($){\n "+text+";\n})");
        returnValue = f.apply(json, [$]);
        return returnValue || "";
    },
	setContentEach: function(){
		var regexp = /(\{each\([\s\S]+\)\})[\s\S]+?(\{endEach\})/g;
		//var regexp = /(each\([\s\S]+\)\{)[\s\S]+\}/g;
		var r = this.html.match(regexp);
		if(r){
			if (r.length){
				for (var i=0; i<r.length; i++){
					var eachItemsStr = r[i].substr(0,r[i].indexOf(")"));
					eachItemsStr = eachItemsStr.substr(eachItemsStr.indexOf("(")+1,eachItemsStr.length);
					var pars = eachItemsStr.split(/,[\s]*/g);
					eachItemsPar = pars[0];
					eachItemsCount = pars[1].toInt();

					var eachItems = this.getJsonContent(this.json ,eachItemsPar);
					if (eachItems) if (eachItemsCount==0) eachItemsCount = eachItems.length;

					var eachContentStr = r[i].substr(0,r[i].lastIndexOf("{endEach}"));
					eachContentStr = eachContentStr.substr(eachContentStr.indexOf("}")+1,eachContentStr.length);
					
					var eachContent = [];
					if (eachItems){
						for (var n=0; n<Math.min(eachItems.length, eachItemsCount); n++){
							var item = eachItems[n];
							if (item){
								var tmpEachContentStr = eachContentStr;
								var textReg = /(eachText\{).+?\}/g;
								texts = tmpEachContentStr.match(textReg);
								if (texts){
									if (texts.length){
										for (var j=0; j<texts.length; j++){
											var text = texts[j].substr(0,texts[j].lastIndexOf("}"));
											text = text.substr(text.indexOf("{")+1,text.length);

											var value = this.getJsonContent(item ,text);
											tmpEachContentStr = tmpEachContentStr.replace(/(eachText\{).+?\}/,value);
										}
									}
								}
								eachContent.push(tmpEachContentStr);
							}
						}
					}
					
					this.html = this.html.replace(/(\{each\([\s\S]+\)\})[\s\S]+?(\{endEach\})/,eachContent.join(""));
				}
			}
		}
	}
});
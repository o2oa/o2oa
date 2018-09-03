MWF = window.MWF || {};
MWF.requiredModules = [];
MWF.defaultPath = "/res/mwf4/package";
MWF.language = "zh-cn";
var href = window.location.href;
var par = href.substr(href.lastIndexOf("?")+1, href.length);
if (par){
    var parList = par.split("&");
    parList.each(function(v){
		var kv = v.split("=");
		if (kv[0].toLowerCase()=="lg") MWF.language = kv[1] || "zh-cn";
	}.bind(this));
}
if (href.indexOf("language")!==-1) COMMON["debugger"] = true;

MWF.languagePackageStatus = "uninitialized";
MWF.splitStr = /(,\s*){1}|(;\s*){1}/g;
MWF.macro = {
	"temp": {}
};

MWF.require = function(module, callback, async, compression){
	//if module is loaded, do callback
	if (MWF.requiredModules.indexOf(module)!==-1){
		if (typeOf(callback).toLowerCase() === 'function'){
			callback();
		}else{
			MWF.runCallback(callback, "onSuccess");
		}
		return;
	}

	var levels = module.split(".");
	if (levels[levels.length-1]==="*"){
		levels[levels.length-1] = "package";
	}
	levels.shift();
	var jsPath = this.defaultPath;
    if (compression){
        jsPath += "/"+levels.join("/")+".min.js";
    }else{
        jsPath += "/"+levels.join("/")+".js";
    }
	
	var loadAsync = true;
	if (async===false){
		loadAsync = false;
	}

    if (!layout["debugger"]){
        jsPath = jsPath.replace(/\.js/, ".min.js");
    }
    jsPath = (jsPath.indexOf("?")!==-1) ? jsPath+"&v="+COMMON.version : jsPath+"?v="+COMMON.version;
	var r = new Request({
		url: jsPath,
		async: loadAsync,
		method: "get",
		onSuccess: function(){
			//var jsText = responseText;
			try{
				//Browser.exec(jsText);
				MWF.requiredModules.push(module);
			}catch (e){
				MWF.runCallback(callback, "onFailure", e);
				return;
			}
			if (typeOf(callback).toLowerCase() === 'function'){
				callback();
			}else{
				MWF.runCallback(callback, "onSuccess");
			}
			
		},
		onFailure: function(xhr){
			MWF.runCallback(callback, "onRequestFailure", xhr);
		}
	});
	r.send();
};
MWF.loadLP = function(name){
	var jsPath = this.defaultPath;
	jsPath = jsPath+"/lp/"+name+".js";
	var r = new Request({
		url: jsPath,
		async: false,
		method: "get",
		onSuccess: function(responseText){
			try{
				Browser.exec(responseText);
			}catch (e){}
		},
		onFailure: function(xhr){
			throw "MWF.loadLP Error: "+xhr.responseText;
		}
	});
	r.send();
};

MWF.runCallback = function(callback, name, par){
	if (typeOf(callback).toLowerCase()==='object'){
		if (callback[name]){
			callback[name].apply(callback, par);
		}
	}
};
MWF.getCenterPosition = function(el, width, height){
	var elPositon = $(el).getPosition();
	var elSize = $(el).getSize();
	var node = $("layout");
	var size = (node) ? $(node).getSize() : $(document.body).getSize();

	var top = (elPositon.y+elSize.y)/2 - (height/2);
	var left = (elPositon.x+elSize.x)/2-(width/2);
	if ((left+width)>size.x){
		left = size.x-width-10;
	}
	if ((top+height)>size.y){
		top = size.y-height-10;
	}

	return {"x": left, "y": top};
};
MWF.getMarkSize = function(node){
    var size;
    if (!node){
        size = $(document.body).getSize();
        var winSize = $(window).getSize();

        //var h = $(document.body).scrollHeight;
        //var w = $(document.body).scrollWidth;

//	var height = size.y+h+20;
//	var width = size.x+w;

        var height = size.y;
        var width = size.x;

        if (height<winSize.y) height = winSize.y;
        if (width<winSize.x) width = winSize.x;

        return {x: size.x, y: height};
    }else{
        size = $(node).getSize();
        return {x: size.x, y: size.y};
    }
};

MWF.json = function(jsonString, fun){
	var obj = JSON.decode(jsonString);
	var p = fun.split(".");
	var tmp = obj;
	p.each(function(item){
		if (item.indexOf("[")!==-1){
			var x = item.split("[");
			var i = parseInt(x[1].substr(0, x[1].indexOf("]")));
			tmp = tmp[x[0]][i];
		}else{
			tmp = tmp[item];
		}
	});
	return tmp;
};

MWF.getHTMLTemplate = function(url, callback, async){
	var loadAsync = true;
	if (async===false){
		loadAsync = false;
	}
	
	var res = new Request.HTML({
		url: url,
		async: loadAsync,
		method: "get",
		onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
			if (typeOf(callback).toLowerCase() === 'function'){
				callback(responseTree, responseElements, responseHTML, responseJavaScript);
			}else{
				MWF.runCallback(callback, "onSuccess", [responseTree, responseElements, responseHTML, responseJavaScript]);
			}
		}.bind(this),
		onFailure: function(xhr){
			MWF.runCallback(callback, "onRequestFailure", [xhr]);
		}
	});
	res.send();
};
MWF.getRequestText = function(url, callback, async){
	var loadAsync = true;
	if (async===false){
		loadAsync = false;
	}

    url = (url.indexOf("?")!==-1) ? url+"&v="+COMMON.version : url+"?v="+COMMON.version;
	var res = new Request({
		url: url,
		async: loadAsync,
		method: "get",
		onSuccess: function(responseText, responseXML){
			if (typeOf(callback).toLowerCase() === 'function'){
				callback(responseText, responseXML);
			}else{
				MWF.runCallback(callback, "onSuccess", [responseText, responseXML]);
			}
		}.bind(this),
		onFailure: function(xhr){
			MWF.runCallback(callback, "onRequestFailure", [xhr]);
		}
	});
	res.send();
};
MWF.getJSONP = function(url, callback, async, callbackKey){
	var loadAsync = true;
	if (async===false){
		loadAsync = false;
	}
	var callbackKeyWord = callbackKey || "callback";

    url = (url.indexOf("?")!==-1) ? url+"&v="+COMMON.version : url+"?v="+COMMON.version;
	var res = new Request.JSONP({
		url: url,
		secure: false,
		method: "get",
		noCache: true,
		async: loadAsync,
		callbackKey: callbackKeyWord,
		onSuccess: function(responseJSON, responseText){
			if (typeOf(callback).toLowerCase() === 'function'){
				callback(responseJSON, responseText);
			}else{
				MWF.runCallback(callback, "onSuccess", [responseJSON, responseText]);
			}
		}.bind(this),
		onFailure: function(xhr){
			MWF.runCallback(callback, "onRequestFailure", [xhr]);
		}.bind(this),
		onError: function(text, error){
			MWF.runCallback(callback, "onError", [text, error]);
		}.bind(this)
	});
	res.send();
};
MWF.getJSON = function(url, callback, async, withCredentials, nocache){
	var loadAsync = true;
	if (async===false){
		loadAsync = false;
	}
    var credentials = true;
    if (withCredentials===false){
        credentials = false;
    }

    var noJsonCache = false;
    if (nocache===true){
        noJsonCache = true;
    }

    url = (url.indexOf("?")!==-1) ? url+"&v="+COMMON.version : url+"?v="+COMMON.version;
	var res = new Request.JSON({
		url: url,
		secure: false,
		method: "get",
		noCache: noJsonCache,
		async: loadAsync,
		withCredentials: credentials,
		onSuccess: function(responseJSON, responseText){
			if (typeOf(callback).toLowerCase() === 'function'){
				callback(responseJSON, responseText);
			}else{
				MWF.runCallback(callback, "onSuccess", [responseJSON, responseText]);
			}
		}.bind(this),
		onFailure: function(xhr){
			MWF.runCallback(callback, "onRequestFailure", [xhr]);
		}.bind(this),
		onError: function(text, error){
			MWF.runCallback(callback, "onError", [text, error]);
		}.bind(this)
	});
//	res.setHeader("Cookie", "x_token=20150207151602%E5%BC%A0%E4%B8%89; path=/; domain=/");
	
	res.send();
};
MWF.restful = function(method, address, data, callback, async, withCredentials){
	var loadAsync = true;
	if (async===false){
		loadAsync = false;
	}
    var credentials = true;
    if (withCredentials===false){
        credentials = false;
    }

    address = (address.indexOf("?")!==-1) ? address+"&v="+COMMON.version : address+"?v="+COMMON.version;
	var res = new Request.JSON({
		url: address,
		secure: false,
		method: method,
		emulation: false,
		noCache: true,
		async: loadAsync,
		withCredentials: credentials,
		onSuccess: function(responseJSON, responseText){
			var xToken = this.getHeader("x-token");
			if (xToken){
				if (layout){
                    if (!layout.session) layout.session = {};
                    layout.session.token = xToken;
				}
			}
			if (typeOf(callback).toLowerCase() === 'function'){
				callback(responseJSON, responseText);
			}else{
				MWF.runCallback(callback, "onSuccess", [responseJSON, responseText]);
			}
		},
		onFailure: function(xhr){
			MWF.runCallback(callback, "onRequestFailure", [xhr]);
		}.bind(this),
		onError: function(text, error){
			MWF.runCallback(callback, "onError", [text, error]);
		}.bind(this)
	});
    if (layout["debugger"]){
        res.setHeader("x-debugger", "true");
	}
	res.setHeader("Content-Type", "application/json; charset=utf-8");
    if (layout) {
    	if (layout.session){
            if (layout.session.token) {
                res.setHeader("x-token", layout.session.token);
                res.setHeader("authorization", layout.session.token);
            }
		}
    }
	//Content-Type	application/x-www-form-urlencoded; charset=utf-8
	res.send(data);
    return res;
};
MWF.encodeJsonString = function(str){
	var tmp = [str];
	var dataStr = (JSON.encode(tmp));
	return dataStr.substr(2, dataStr.length-4);
};
MWF.decodeJsonString = function(str){
    var tmp = "[\""+str+"\"]";
    var dataObj = (JSON.decode(tmp));
    return dataObj[0];
};

MWF.getTextSize = function(text, styles){
	var tmpSpan = new Element("span", {
		"text": text,
		"styles": styles
	}).inject($(document.body));
	var size = tmpSpan.getSize();
	tmpSpan.destroy();
	return size;
};
MWF.getCenter = function(size, target, offset){
	debugger;
	if (!target) target = document.body;
	var targetSize = target.getSize();
	var targetPosition = target.getPosition(offset);
	var targetScroll = target.getScroll();

	var x = targetSize.x/2;
	var y = targetSize.y/2;

	x = x-(size.x/2);
	y = y-(size.y/2);
	x = x+targetPosition.x;
	y = y+targetPosition.y;
	x = x+targetScroll.x;
	y = y+targetScroll.y;
	
	return {"x": x, "y": y};
};
MWF.getEPointer = function(e){
    var x = 0;
    var y = 0;
    if (typeOf(e)=="element"){
        var position = e.getPosition(this.content);
        x = position.x;
        y = position.y;
    }else{
        if (Browser.name=="firefox"){
            x = parseFloat(e.event.clientX || e.event.x);
            y = parseFloat(e.event.clientY || e.event.y);
        }else{
            x = parseFloat(e.event.x);
            y = parseFloat(e.event.y);
        }

        if (e.target){
            var position = e.target.getPosition(this.content);
            x = position.x;
            y = position.y;
        }
        //    }
    }
    return {"x": x, "y": y};
};
MWF.getParent = function(node, tag){
	var pNode = node.parentElement;
	while(pNode && pNode.tagName.toString().toLowerCase() !== tag.toString().toLowerCase()){
		pNode = pNode.parentElement;
	}
	return pNode;
};
MWF.getOffset = function(evt){
    if (Browser.name==="firefox"){
        //var target = evt.target;
        //if (target.offsetLeft == undefined){
        //    target = target.parentNode;
        //}
        //var pageCoord = {x: 0, y: 0};
        //while (target){
        //    pageCoord.x += target.offsetLeft;
        //    pageCoord.y += target.offsetTop;
        //    target = target.offsetParent;
        //}
        //var eventCoord = {
        //    "x": window.pageXOffset + evt.clientX,
        //    "y": window.pageYOffset + evt.clientY
        //};

        return {
            "offsetX": evt.layerX,
            "offsetY": evt.layerY
        };
        //return offset;
    }else{
        return {
            "offsetX": evt.offsetX,
            "offsetY": evt.offsetY
        }
    }
};




String.implement({
	"getAllIndexOf": function(str){
		var idxs= [];
		var idx = this.indexOf(str);
		while (idx !== -1){
			idxs.push(idx);
			idx = this.indexOf(str, idx+1);
		}
		return idxs;
	}
});

//if (!Element.prototype.addEventListener){
//    Element.implement({
//        "addEventListener": function(name, fun){
//            this.addEvent(name, fun);
//        }
//    });
//}
Array.implement({
    "trim": function(){
        var arr = [];
        this.each(function(v){
            if (v) arr.push(v);
        });
        return v;
    },
	"isIntersect": function(arr){
        return this.some(function(item){ return (arr.indexOf(item)!==-1); })
	}
});
Element.implement({
    "appendHTML": function(html, where){
        if (this.insertAdjacentHTML){
            var whereText = "beforeEnd";
            if (where==="before") whereText = "beforeBegin";
            if (where==="after") whereText = "afterEnd";
            if (where==="bottom") whereText = "beforeEnd";
            if (where==="top") whereText = "afterBegin";
            this.insertAdjacentHTML(whereText, html);
        }else {
            if (where==="bottom") this.innerHTML = this.innerHTML+html;
            if (where==="top") this.innerHTML = html+this.innerHTML;
        }
    },
	"positionTo": function(x,y){
		var left = x.toFloat();
		var top = y.toFloat();
		var offsetNode = this.getOffsetParent();
		if (offsetNode){
			var offsetPosition = offsetNode.getPosition();
			left = left-offsetPosition.x;
			top = top-offsetPosition.y;
		}
		this.setStyles({"top": top, "left": left});
		return this;
	},
	"getBorder": function(){
		var positions = ["top", "left", "right", "bottom"];
		var styles = ["color", "style", "width"];
		
		var obj = {};
		positions.each(function (position){
			styles.each(function(style){
				var key = "border-"+position+"-"+style;
				obj[key] = this.getStyle(key);
			}.bind(this));
		}.bind(this));
		
		return obj;
	},
	"isOutside": function(e){
		var elementCoords = this.getCoordinates();
		var targetCoords  = this.getCoordinates();
		if(((e.page.x < elementCoords.left || e.page.x > (elementCoords.left + elementCoords.width)) ||
		    (e.page.y < elementCoords.top || e.page.y > (elementCoords.top + elementCoords.height))) &&
		   ((e.page.x < targetCoords.left || e.page.x > (targetCoords.left + targetCoords.width)) ||
		    (e.page.y < targetCoords.top || e.page.y > (targetCoords.top + targetCoords.height))) ) return true;
		
		return false;
	},
	"getAbsolutePosition":function(){
		var styleLeft = 0;
		var styleTop = 0;
		var node = this;

		styleLeft = node.offsetLeft;
		styleTop = node.offsetTop;
		
		node = node.parentElement;
		
		while (node && node.tagName.toString().toLowerCase()!=="body"){
			styleLeft += node.offsetLeft;
			styleTop += node.offsetTop;
			node = node.offsetParent;
		}
		return {x: styleLeft, y: styleTop};
	},
	"tweenScroll": function(to, time){
		if (!this.tweenScrollQueue){
			this.tweenScrollQueue = [];
		}
		if (this.tweenScrollQueue.length){
			this.tweenScrollQueue.push(to);
		}else{
			this.tweenScrollQueue.push(to);
			this.doTweenScrollQueue(time);
		}
	},
//	"doTweenScrollQueue1": function(time){
//		if (this.tweenScrollQueue.length){
//			var i = this.tweenScrollQueue.length;
//			var to = this.tweenScrollQueue[this.tweenScrollQueue.length-1];
//			
//			//var 
//			
//			var scroll = this.getScroll();
//			var dy = to - scroll.y;
//			var step = dy/time;
//			var count = 0;
//			var move = 0;
//			
//			var id = window.setInterval(function(){
//				move += scroll.y+step;
//				this.scrollTo(0, move);
//				step = step+1;
//				count++;
//			//	$("testArea").set("text", count);
//				if (move>=dy){
//					window.clearInterval(id);
//					for (var x=1; x<=i; x++) this.tweenScrollQueue.shift();
//					$("testArea").set("text", this.tweenScrollQueue.length);
//					if (this.tweenScrollQueue.length) this.doTweenScrollQueue(time);
//				}
//			}.bind(this), 1);
//		}
//	},
	"doTweenScrollQueue": function(time){
		if (this.tweenScrollQueue.length){
			var i = this.tweenScrollQueue.length;
			var to = this.tweenScrollQueue[this.tweenScrollQueue.length-1];
			
			//var 
			
			var scroll = this.getScroll();
			var dy = to - scroll.y;
			var step = dy/time;
			var count = 0;
			var move = 0;
			
			var id = window.setInterval(function(){

				this.scrollTo(0, scroll.y+count*step);
				count++;
			//	$("testArea").set("text", count);
				if (count>time){
					window.clearInterval(id);
					for (var x=1; x<=i; x++) this.tweenScrollQueue.shift();
					if (this.tweenScrollQueue.length) this.doTweenScrollQueue(time);
				}
			}.bind(this), 1);
		}
	},
	"isPointIn": function(px, py, offX, offY, el){
		if (!offX) offX = 0;
		if (!offY) offY = 0;
		var position = this.getPosition(el);
		var size = this.getSize();
		return (position.x-offX<=px && position.x+size.x+offX>=px && position.y-offY<=py && position.y+size.y+offY>=py);
	},
	"isInPointInRect": function(sx, sy, ex, ey){
		var position = this.getPosition();
		var size = this.getSize();
		
		var p1 = {"x": position.x, "y": position.y};
		var p2 = {"x": position.x+size.x, "y": position.y};
		var p3 = {"x": position.x+size.x, "y": position.y+size.y};
		var p4 = {"x": position.x, "y": position.y+size.y};
		
		var sp = {"x": Math.min(sx, ex), "y": Math.min(sy, ey)};
		var ep = {"x": Math.max(sx, ex), "y": Math.max(sy, ey)};
		
		if (p1.x>=sp.x && p1.y>=sp.y && p1.x<=ep.x && p1.y<=ep.y) return true;
		if (p2.x>=sp.x && p2.y>=sp.y && p2.x<=ep.x && p2.y<=ep.y) return true;
		if (p3.x>=sp.x && p3.y>=sp.y && p3.x<=ep.x && p3.y<=ep.y) return true;
		if (p4.x>=sp.x && p4.y>=sp.y && p4.x<=ep.x && p4.y<=ep.y) return true;
		if (p3.x>=sp.x && p3.y>=sp.y && p1.x<=sp.x && p1.y<=sp.y) return true;
		if (p3.x>=ep.x && p3.y>=ep.y && p1.x<=ep.x && p1.y<=ep.y) return true;
		if (p1.x<=sp.x && p2.x>=sp.x && p1.y>=sp.y && p4.y<=ep.y) return true;
		if (p1.y<=sp.y && p4.y>=sp.y && p1.x>=sp.x && p2.x<=ep.x) return true;
		

		return false;
	},
	
	"getUsefulSize": function(){
		var size = this.getSize();
		var borderLeft = this.getStyle("border-left").toInt();
		var borderBottom = this.getStyle("border-bottom").toInt();
		var borderTop = this.getStyle("border-top").toInt();
		var borderRight = this.getStyle("border-right").toInt();
		
		var paddingLeft = this.getStyle("padding-left").toInt();
		var paddingBottom = this.getStyle("padding-bottom").toInt();
		var paddingTop = this.getStyle("padding-top").toInt();
		var paddingRight = this.getStyle("padding-right").toInt();
		
		var x = size.x-paddingLeft-paddingRight;
		var y = size.y-paddingTop-paddingBottom;
		
		return {"x": x, "y": y};
	},
	"clearStyles": function(isChild){
		this.removeProperty("style");
		if (isChild){
			var subNode = this.getFirst();
			while (subNode){
				subNode.clearStyles(isChild);
				subNode = subNode.getNext();
			}
		}
	},
	"maskIf": function(styles, click){
		var style = {
			"background-color": "#666666",
			"opacity": 0.4,
			"z-index":100
		};
		if (styles){
            style = Object.merge(style, styles);
		}
        var position = this.getPosition(this.getOffsetParent());
        this.mask({
            "destroyOnHide": true,
            "style": style,
            "useIframeShim": true,
            "iframeShimOptions": {"browsers": true},
            "onShow": function(){
                this.shim.shim.setStyles({
                    "opacity": 0,
                    "top": ""+position.y+"px",
                    "left": ""+position.x+"px"
                });
            },
            "onClick": click
        });
	},
	scrollToNode: function(el, where){
		debugger;
		var scrollSize = this.getScrollSize();
		if (!scrollSize.y) return true;
        var wh = (where) ? where.toString().toLowerCase() : "bottom";
        var node = $(el);
        var size = el.getComputedSize();
        var p = el.getPosition(this);
		var thisSize = this.getComputedSize();
        var scroll = this.getScroll();
		if (wh==="top"){
            var n = (p.y-thisSize.computedTop);
            if (n<0) this.scrollTo(scroll.x, scroll.y+n);
            n = (size.totalHeight+p.y-thisSize.computedTop)-thisSize.height;
            if (n>0) this.scrollTo(scroll.x, scroll.y+n);

		}else{
            var n = (size.totalHeight+p.y-thisSize.computedTop)-thisSize.height;
            if (n>0) this.scrollTo(scroll.x, scroll.y+n);
            n = p.y-thisSize.computedTop;
            if (n<0) this.scrollTo(scroll.x, scroll.y+n);
		}

	}
});
Object.copy = function(from, to){
	Object.each(from, function(value, key){
		switch (typeOf(value)){
			case "object":
				Object.copy(value, to[key]);
				break;
			default: 
				to[key] = value;
		}
	});
};
JSON.format = function(json){
//		var jsonStrBegin = "{";
//		var jsonStr = "";
//		for (p in json){
//			jsonStr += this.parseJsonString(1, p, json[p]);
//		}
//		var jsonStrEnd = "}";
//		return jsonStrBegin+"\n"+jsonStr+"\n"+jsonStrEnd;
	var jsonStr = this.parseJsonString(0, "", json);
	return jsonStr.substring(0, jsonStr.length-2);
};
JSON.parseJsonString = function(level, p, v){
	var tab = "";
	for (var i=0; i<level; i++) tab+="\t";
	var title = p;
	if (title) title="\""+title+"\": ";
	
	var jsonStr = "";
	
	var nextLevel = level+1;
	switch (typeOf(v)){
		case "object":
			var jsonStrBegin = tab+title+"{";
			var jsonStrEnd = tab+"}";
			for (x in v){
				jsonStr += this.parseJsonString(nextLevel, x, v[x]);
			}
			jsonStr = jsonStrBegin+"\n"+jsonStr.substring(0, jsonStr.length-2)+"\n"+jsonStrEnd+",\n";
			break;
			
		case "array":
			var jsonStrBegin = tab+title+"[";
			var jsonStrEnd = tab+"]";
			
			v.each(function(item, idx){
				jsonStr += this.parseJsonString(nextLevel, "", item);
			}.bind(this));
			jsonStr = jsonStrBegin+"\n"+jsonStr.substring(0, jsonStr.length-2)+"\n"+jsonStrEnd+",\n";
			break;
			
		case "string":	
			jsonStr += tab+title+"\""+v+"\",\n";
			break;
		case "date":	
			jsonStr += tab+title+"\""+v+"\",\n";
			break;
		default: 
			jsonStr += tab+title+v+",\n";
	}
	return jsonStr;
};

Slick.definePseudo('src', function(value){
    return Element.get(this,"src").indexOf(value) !== -1;
});
Slick.definePseudo('srcarr', function(value){
	var vList = value.split(",");
	var src = Element.get(this,"src");
	var flag = false;
	for (var i=0; i<vList.length; i++){
		if (src.indexOf(vList[i])!==-1){
			flag = true;
			break;
		}
	}
    return flag;
});
Slick.definePseudo('ahref', function(value){
	var href = Element.get(this,"href");
	if (!href) href = "";
	href = href.toString().toLowerCase();
	return (href.indexOf(value)!==-1);
});

Slick.definePseudo('rowspanBefore', function(line){
	var tr = MWF.getParent(this, "tr");
	var rowspan = this.get("rowspan").toInt() || 1;
	var currentRowIndex = tr.rowIndex.toInt();
	
	return rowspan>1 && currentRowIndex<line.toInt() && currentRowIndex+rowspan-1>=line;
});
Slick.definePseudo('rowspan', function(){
	var rowspan = this.get("rowspan").toInt() || 1;
	return rowspan>1;
});

Slick.definePseudo('colspanBefore', function(col){
	var tr = MWF.getParent(this, "tr");
	var colspan = this.get("colspan").toInt() || 1;
	var currentColIndex = this.cellIndex.toInt();
	
	return colspan>1 && currentColIndex<col.toInt() && currentColIndex+colspan-1>=col.toInt();
});

Slick.definePseudo('colspan', function(){
	var colspan = this.get("colspan").toInt() || 1;
	return colspan>1;
});


MWF.common = MWF.common || {};

MWF.common.getResponseTextPost = function(path, body, contentType){
	var returnText = "";
	var options = {
		url: path,
		async: false,
		data: body,
		method: "post",
		onSuccess: function(esponseTree, responseElements, responseHTML, responseJavaScript){
			returnText = responseHTML;
		}
	};
	var r = new Request.HTML(options);
	r.send();
	return returnText;
};
MWF.common.getResponseText = function(path){
	var returnText = "";
	var options = {
		url: path,
		async: false,
		method: "get",
		onSuccess: function(esponseTree, responseElements, responseHTML, responseJavaScript){
			returnText = responseHTML;
		}
	};
	var r = new Request.HTML(options);
	r.send();
	return returnText;
};
MWF.common.toDate = function(str){
	var tmpArr = str.split(" ");
	if (!tmpArr[1]) tmpArr.push("0:0:0");
	var dateArr = tmpArr[0].split("-");
	var timeArr = tmpArr[1].split(":");
	return new Date(dateArr[0],parseInt(dateArr[1])-1,dateArr[2],timeArr[0],timeArr[1],timeArr[2]);
};

MWF.release = function(o){
    var type = typeOf(o);
    switch (type){
        case "object":
            for (var k in o){
                o[k] = null;
            }
            break;
        case "array":
            for (var i=0; i< o.length; i++){
                if (o[i]) o[i] = null;
            }
            break;
    }
};
// MWF.grayscale = function(src, width, height, callback){
//         var imgObj = new Image();
//         imgObj.addEventListener("loadeddata", function(){
//             try {
//                 var canvas = document.createElement('canvas');
//                 var ctx = canvas.getContext('2d');
//
//                 canvas.width = width || imgObj.width;
//                 canvas.height = height || imgObj.height;
//                 ctx.drawImage(imgObj, 0, 0);
//
//
//                 var imgPixels = ctx.getImageData(0, 0, canvas.width, canvas.height);
//                 for(var y = 0; y < imgPixels.height; y++){
//                     for(var x = 0; x < imgPixels.width; x++){
//                         var i = (y * 4) * imgPixels.width + x * 4;
//                         var avg = (imgPixels.data[i] + imgPixels.data[i + 1] + imgPixels.data[i + 2]) / 3;
//                         imgPixels.data[i] = avg;
//                         imgPixels.data[i + 1] = avg;
//                         imgPixels.data[i + 2] = avg;
//                     }
//                 }
//                 ctx.putImageData(imgPixels, 0, 0, 0, 0, imgPixels.width, imgPixels.height);
//                 var src = canvas.toDataURL();
//                 canvas.destroy();
//                 if (callback) callback({"status": "success", "src": src});
//             }catch(e){
//                 if (callback) callback({"status": "error", "src": src});
//             }
//         });
//         imgObj.src = src;
// };
MWF.grayscale = function(src, width, height, callback){
    try {
		var canvas = document.createElement('canvas');
		var ctx = canvas.getContext('2d');
        var imgObj = new Image();
        imgObj.src = src;
    	canvas.width = width || imgObj.width;
        canvas.height = height || imgObj.height;
        ctx.drawImage(imgObj, 0, 0);

        var imgPixels = ctx.getImageData(0, 0, canvas.width, canvas.height);
        for(var y = 0; y < imgPixels.height; y++){
            for(var x = 0; x < imgPixels.width; x++){
                var i = (y * 4) * imgPixels.width + x * 4;
                var avg = (imgPixels.data[i] + imgPixels.data[i + 1] + imgPixels.data[i + 2]) / 3;
                imgPixels.data[i] = avg;
                imgPixels.data[i + 1] = avg;
                imgPixels.data[i + 2] = avg;
            }
        }
        ctx.putImageData(imgPixels, 0, 0, 0, 0, imgPixels.width, imgPixels.height);
        var src1 = canvas.toDataURL();
        //var blob = canvas.toBlob();
        canvas.destroy();
        return {"status": "success", "src": src1};
    }catch(e){
        return {"status": "error", "src": src}
    }
};
MWF.eventPosition = function(e){
    var x = 0;
    var y = 0;
    if (Browser.name=="firefox"){
        x = parseFloat(e.event.clientX || e.event.x);
        y = parseFloat(e.event.clientY || e.event.y);
    }else{
        x = parseFloat(e.event.x);
        y = parseFloat(e.event.y);
    }
    return {"x": x, "y": y};
};


//MWF.mergeObject = function(obj1, obj2){
//    Object.each(obj2, function(v, k){
//        if (!obj1[k]){
//            switch (typeOf(v)){
//                case "array":
//                    obj1[k] = [];
//                    v.each(function(v1){
//                        switch (typeOf(v1)){
//
//                        }
//                    });
//                    break;
//                case "object":
//                    obj1[k] = {};
//                    MWF.mergeObject(obj1[k], v);
//                    break;
//                case "string": case "number": case "date": case "boolean": case "null":
//                obj1[k] = v;
//                break;
//            }
//        }
//    });
//};
//MWF.mergeArray = function(arr1, arr2){
//    arr2.each(function(v){
//        switch (typeOf(v)){
//            case "array":
//                obj1[k] = [];
//                break;
//            case "object":
//                obj1[k] = {};
//                MWF.mergeObject(obj1[k], v);
//                break;
//            case "string": case "number": case "date": case "boolean": case "null":
//            arr1.push(v);
//            break;
//        }
//    });
//};


//MWF.recycleCount = 0;
//MWF.recycle = function(o, deep){
//    if (!deep) deep = 0;
//    var type = typeOf(o);
//    if (deep>100) type = "ignore";
//    switch (type){
//        case "object":
//            MWF.defineProperties(o, {"iterated": {"value": true}});
//            for (var k in o){
//                var flag = k.substr(0,1);
//                if (o[k]){
//                    if (o.hasOwnProperty(k)){
//                        if (flag!="$" && flag!="_"){
//                            if (k!="css" && k!="style" && k.toLowerCase()!="lp" && k.toLowerCase()!="app"){
//                                if (o[k].type!="layout"){
//                                    if (!o[k].iterated){
//                                        MWF.recycle(o[k], ++deep);
//                                    }
//                                }
//                            }
//                            if (typeOf(o[k])!="function") o[k] = null;
//                        }
//                    }
//                }
//            }
//        case "array":
//            for (var i=0; i< o.length; i++){
//                if (o[i]) MWF.recycle(o[i], ++deep);
//            }
//        default:
//  //          MWF.recycleCount++;
//   //         o = null;
//    }
////    layout.desktop.topNode.set("text", MWF.recycleCount);
//};
MWF.defineProperties = Object.defineProperties || function (obj, properties) {
    function convertToDescriptor(desc) {
        function hasProperty(obj, prop) {
            return Object.prototype.hasOwnProperty.call(obj, prop);
        }

        function isCallable(v) {
            // NB: modify as necessary if other values than functions are callable.
            return typeof v === "function";
        }

        if (typeof desc !== "object" || desc === null)
            throw new TypeError("bad desc");

        var d = {};

        if (hasProperty(desc, "enumerable"))
            d.enumerable = !!desc.enumerable;
        if (hasProperty(desc, "configurable"))
            d.configurable = !!desc.configurable;
        if (hasProperty(desc, "value"))
            d.value = desc.value;
        if (hasProperty(desc, "writable"))
            d.writable = !!desc.writable;
        if (hasProperty(desc, "get")) {
            var g = desc.get;

            if (!isCallable(g) && typeof g !== "undefined")
                throw new TypeError("bad get");
            d.get = g;
        }
        if (hasProperty(desc, "set")) {
            var s = desc.set;
            if (!isCallable(s) && typeof s !== "undefined")
                throw new TypeError("bad set");
            d.set = s;
        }

        if (("get" in d || "set" in d) && ("value" in d || "writable" in d))
            throw new TypeError("identity-confused descriptor");

        return d;
    }

    if (typeof obj !== "object" || obj === null)
        throw new TypeError("bad obj");

    properties = Object(properties);

    var keys = Object.keys(properties);
    var descs = [];

    for (var i = 0; i < keys.length; i++)
        descs.push([keys[i], convertToDescriptor(properties[keys[i]])]);

    for (var i = 0; i < descs.length; i++){
    	if (Object.defineProperty && (Browser.name=="ie" && Browser.version!=8)){
            Object.defineProperty(obj, descs[i][0], descs[i][1]);
		}else{
            if (descs[i][1].value) obj[descs[i][0]] = descs[i][1].value;
            if (descs[i][1].get) obj["get"+descs[i][0].capitalize()] = descs[i][1].get;
            if (descs[i][1].set) obj["set"+descs[i][0].capitalize()] = descs[i][1].set;
		}
	}
    return obj;
};

MWF.defineProperty = (Object.defineProperty && (Browser.name=="ie" && Browser.version!=8)) ? Object.defineProperty : function (obj, k, properties) {
    function convertToDescriptor(desc) {
        function hasProperty(obj, prop) {
            return Object.prototype.hasOwnProperty.call(obj, prop);
        }

        function isCallable(v) {
            // NB: modify as necessary if other values than functions are callable.
            return typeof v === "function";
        }

        if (typeof desc !== "object" || desc === null)
            throw new TypeError("bad desc");

        var d = {};

        if (hasProperty(desc, "enumerable"))
            d.enumerable = !!desc.enumerable;
        if (hasProperty(desc, "configurable"))
            d.configurable = !!desc.configurable;
        if (hasProperty(desc, "value"))
            d.value = desc.value;
        if (hasProperty(desc, "writable"))
            d.writable = !!desc.writable;
        if (hasProperty(desc, "get")) {
            var g = desc.get;

            if (!isCallable(g) && typeof g !== "undefined")
                throw new TypeError("bad get");
            d.get = g;
        }
        if (hasProperty(desc, "set")) {
            var s = desc.set;
            if (!isCallable(s) && typeof s !== "undefined")
                throw new TypeError("bad set");
            d.set = s;
        }

        if (("get" in d || "set" in d) && ("value" in d || "writable" in d))
            throw new TypeError("identity-confused descriptor");

        return d;
    }

    if (typeof obj !== "object" || obj === null)
        throw new TypeError("bad obj");

    properties = Object(properties);

    var d = convertToDescriptor(properties);
    if (Object.defineProperty && (Browser.name=="ie" && Browser.version!=8)){
        Object.defineProperty(obj, key, d);
    }else{
        if (d.value) obj[key] = d.value;
        if (d.get) obj["get"+key.capitalize()] = d.get;
        if (d.set) obj["set"+key.capitalize()] = d.set;
    }
    return obj;
};
if (Browser.name=="ie" && Browser.version<9){
    Browser.ieuns = true;
}else if(Browser.name=="ie" && Browser.version<10){
    Browser.iecomp = true;
}
if (Browser.iecomp){
    COMMON.AjaxModule.load("ie_adapter", null, false);
    layout["debugger"] = true;
}

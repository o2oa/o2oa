o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.ScriptEditor = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default",
		"keySeparat": /[\.\,\;\s\{\}\(\)\n\r\t\:\/\*\"\'=\[\]\+\-\/]+/ig,
		"keyCodes": [220, 46, 13, 222, 219, 221, 57, 48, 191, 190, 186, 188, 32, 55, 16, 187, 189, 8, 9]
	},
	initialize: function(node, options){
		this.setOptions(options);
		
		this.node = $(node);
		
		this.path = o2.session.path+"/widget/$ScriptEditor/";
		this.cssPath = o2.session.path+"/widget/$ScriptEditor/"+this.options.style+"/css.wcss";
		this._loadCss();

		this.checkTextNodes = [];
	},
	load: function(content){
		if (this.fireEvent("queryLoad")){
		
			this.createContent(content);
			
			this.fireEvent("postLoad");
		}
	},
	
	createContent: function(content){
		this.editArea = new Element("iframe", {
			"styles": this.css.contentEditArea,
			"border": "0",
			"frameBorder": "0",
			"marginHeight": "0",
			"marginWidth": "0",
			"scrolling": "auto"
		}).inject(this.node);
		
		this.loadContent(content);
	},
	loadContent: function(content){
		this.editDocument = this.editArea.contentWindow.document;
		this.window = this.editArea.contentWindow;
		this.editDocument.write("<style type=\"text/css\">p {margin:0px;}</style><body style=\"overflow:visible; word-break:keep-all; word-wrap:normal; font-size:12px; font-family:Verdana, Geneva, sans-serif; line-height:20px;\">"+content+"</body>");
		this.editDocument.designMode = "On";
		
		this.setBodyEvent();
	},
	addElementEvent: function(el, event, fn){
		if (el.addEventListener){
			el.addEventListener(event, fn);
		}else{
			el.attachEvent("on"+event, fn);
		}
	},
	setBodyEvent: function(){
		this.editDocument.body.style.whiteSpace = "nowrap";
		
		this.addElementEvent(this.editDocument.body, "keydown", function(e){
			if (e.keyCode==9){
				
			}
		}.bind(this));
		
		this.addElementEvent(this.editDocument.body, "keyup", function(e){
			if (this.options.keyCodes.indexOf(e.keyCode)!=-1){

				window.setTimeout(function(){
					this.checkScriptFormat();
				}.bind(this), 10);
			}
		}.bind(this));
	},
	clearFontElements: function(){
		var fonts = this.editDocument.body.getElementsByTagName("font");
		var length = fonts.length;
		while (fonts.length){
			var font = fonts.item(0);
			if (font.previousSibling && font.previousSibling.nodeType==3){
				font.previousSibling.nodeValue = font.previousSibling.nodeValue+font.firstChild.nodeValue;
				
				var textNode = font.previousSibling;
				font.parentElement.removeChild(font);
				while (textNode.nextSibling && textNode.nextSibling.nodeType==3){
					textNode.nodeValue = textNode.nodeValue+textNode.nextSibling.nodeValue;
					textNode.nextSibling.parentNode.removeChild(textNode.nextSibling);
				}
			}else if (font.nextSibling && font.nextSibling.nodeType==3){
				font.nextSibling.nodeValue = font.firstChild.nodeValue+font.nextSibling.nodeValue;
				
				var textNode = font.nextSibling;
				font.parentElement.removeChild(font);
				while (textNode.previousSibling && textNode.previousSibling.nodeType==3){
					textNode.nodeValue = textNode.nodeValue+textNode.previousSibling.nodeValue;
					textNode.previousSibling.parentNode.removeChild(textNode.previousSibling);
				}
			}else{
				if (font.firstChild){
					font.parentNode.replaceChild(this.editDocument.createTextNode(font.firstChild.nodeValue), font);
				}else{
					font.parentNode.removeChild(font);
				}
			}
		}
	},
	getCheckScriptElements: function(node){
		if (node){
			if (node.nodeType==3){
				this.checkTextNodes.push(node);
			}else{
				if (node.nodeType==1 && node.tagName.toString().toLowerCase()=="font"){
				//	this.checkTextNodes.push(node);
				}else{
					var childNodes = node.childNodes;
					for (var i=0; i<childNodes.length; i++){
						this.getCheckScriptElements(childNodes[i]);
					}
				}
			}
		}
	},
	
	checkScriptFormat: function(direction){
	//	this.recordCurrentInsertPoint();
		
		this.clearFontElements();
		
		this.checkScriptFormatComment();
		this.checkScriptFormatString();
		this.checkScriptFormatNumber();
		this.checkScriptFormatKeyword();
	},
	
	recordCurrentInsertPoint: function(){

		this.currentInsertPointCount = 0;
		var selection = this.window.getSelection();
		var node = selection.focusNode;
		if (node.nodeType==3){
			
		}
	},
//	getCurrent
	
	
	
	checkScriptFormatNumber: function(){
		this.checkTextNodes = [];
		this.getCheckScriptElements(this.editDocument.body);

		this.checkTextNodes.each(function(node){
			this.checkScriptTextFormatNumber(node);
		}.bind(this));
	},
	checkScriptTextFormatNumber: function(scriptCheckNode){
		var color = this.css.number.color;
		//var reg = this.options.keySeparat;

		var currentRange = this.editDocument.createRange();
		
		var reg = /\b(-?\d+(\.\d+)?)/g;
		
		var matchNumber = scriptCheckNode.nodeValue.match(reg);
		if (matchNumber){
			var num = matchNumber[0];
			var index = scriptCheckNode.nodeValue.indexOf(num);
			var startIndex = index;
			var endIndex = startIndex+num.length;
			
			currentRange.setStart(scriptCheckNode, startIndex);
			currentRange.setEnd(scriptCheckNode, endIndex);
			
			var font = this.createFormatFont(currentRange, color, "number");
			
			var nextCheckNode = font.nextSibling;
			if (nextCheckNode && nextCheckNode.nodeType==3){
				this.checkScriptTextFormatNumber(nextCheckNode);
			}
		}
	},
	
	checkScriptFormatString: function(){
		this.checkTextNodes = [];
		this.getCheckScriptElements(this.editDocument.body);

		this.isScriptTextString = false;
		this.checkTextNodes.each(function(node){
			this.checkScriptTextFormatString(node);
		}.bind(this));
	},
	checkScriptTextFormatString: function(scriptCheckNode){
		var color = this.css.string.color;
		
		var currentRange = this.editDocument.createRange();
		
		var textValue = scriptCheckNode.nodeValue;
		var stringMark1Index = textValue.indexOf("\"");
		var stringMark2Index = textValue.indexOf("'");
		var startIndex = -1;
		var endIndex = -1;
		var markString = "\"";
		
		if (stringMark1Index!=-1 && stringMark2Index==-1){
			startIndex = stringMark1Index;
		}else if (stringMark1Index==-1 && stringMark2Index!=-1){
			startIndex = stringMark2Index;
			markString = "'";
		}else if (stringMark1Index!=-1 && stringMark2Index!=-1){
			if (stringMark1Index<stringMark2Index){
				startIndex = stringMark1Index;
			}else{
				startIndex = stringMark2Index;
				markString = "'";
			}
		}
		
		if (startIndex!=-1){
			var idx = textValue.indexOf("\"", startIndex+1);
			while (idx!=-1){
				var tmpValue = textValue.substr(idx-1, 1);
				if (tmpValue!="\\"){
					endIndex = idx+1;
					break;
				}
				var idx = textValue.indexOf("\"", idx+1);
			}
			if (endIndex!=-1){
				currentRange.setStart(scriptCheckNode, startIndex);
				currentRange.setEnd(scriptCheckNode, endIndex);
				
				var font = this.createFormatFont(currentRange, color, "string");
				
				var nextCheckNode = font.nextSibling;
				if (nextCheckNode && nextCheckNode.nodeType==3){
					this.checkScriptTextFormatString(nextCheckNode);
				}
			}
		}
	},
	
	checkScriptFormatKeyword: function(){
		this.checkTextNodes = [];
		this.getCheckScriptElements(this.editDocument.body);

		this.checkTextNodes.each(function(node){
			this.checkScriptTextFormatKeyword(node);
		}.bind(this)); 
	},
	
	checkScriptTextFormatKeyword: function(scriptCheckNode){
		var color = this.css.keywords.color;
		
		var currentRange = this.editDocument.createRange();
		
		var reg = /\b((function)|(if)|(else)|(case)|(switch)|(for)|(in)|(do)|(while)|(width)|(var)|(this)|(break)|(continue)|(return)|(true)|(false)|(throw)|(try)|(finally)|(catch)|(finally)|(debugger)|(new)|(delete))/ig;
		
		var matchNumber = scriptCheckNode.nodeValue.match(reg);
		if (matchNumber){
			var num = matchNumber[0];
			var index = scriptCheckNode.nodeValue.indexOf(num);
			var startIndex = index;
			var endIndex = startIndex+num.length;
			
			currentRange.setStart(scriptCheckNode, startIndex);
			currentRange.setEnd(scriptCheckNode, endIndex);
			
			var font = this.createFormatFont(currentRange, color, "keyword");
			
			var nextCheckNode = font.nextSibling;
			if (nextCheckNode && nextCheckNode.nodeType==3){
				this.checkScriptTextFormatKeyword(nextCheckNode);
			}
		}
	},
	
	checkScriptFormatComment: function(){
		this.checkTextNodes = [];
		this.getCheckScriptElements(this.editDocument.body);
		
		this.isScriptTextComment = false;
		this.checkTextNodes.each(function(node){
			this.checkScriptTextFormatComment(node);
		}.bind(this)); 
	},
	
	checkScriptTextFormatComment: function(scriptCheckNode){
		var color = this.css.comment.color;
		
		var currentRange = this.editDocument.createRange();
		if (!this.isScriptTextComment){
			var lineCommentIndex = scriptCheckNode.nodeValue.indexOf("//");
			var multiCommentIndex = scriptCheckNode.nodeValue.indexOf("/*");

			if (lineCommentIndex!=-1 && multiCommentIndex==-1){
				//发现单行注释//符号，没有发现/*符号
				currentRange.setStart(scriptCheckNode, lineCommentIndex);
				currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
				this.createFormatFont(currentRange, color, "comment");
				
			}else if (lineCommentIndex==-1 && multiCommentIndex!=-1){
				currentRange.setStart(scriptCheckNode, multiCommentIndex);
				currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
				this.isScriptTextComment = true;
				this.createFormatFont(currentRange, color, "comment");
				
			}else if (lineCommentIndex!=-1 && multiCommentIndex!=-1){
				if (lineCommentIndex<multiCommentIndex){
					currentRange.setStart(scriptCheckNode, lineCommentIndex);
					currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
					this.createFormatFont(currentRange, color, "comment");
				}else{
					currentRange.setStart(scriptCheckNode, multiCommentIndex);
					currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
					this.isScriptTextComment = true;
					this.createFormatFont(currentRange, color, "comment");
				}
			}
		}else{
			var multiCommentEndIndex = scriptCheckNode.nodeValue.indexOf("*/");
			if (multiCommentEndIndex!=-1){
				currentRange.setStart(scriptCheckNode, 0);
				currentRange.setEnd(scriptCheckNode, multiCommentEndIndex+2);
				this.isScriptTextComment = false;
				this.createFormatFont(currentRange, color, "comment");
			}else{
				currentRange.setStart(scriptCheckNode, 0);
				currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
				this.createFormatFont(currentRange, color, "comment");
			}
		}
	},
	
	createFormatFont: function(currentRange, color, type){
		var fontNode = this.editDocument.createElement("font");
		fontNode.appendChild(this.editDocument.createTextNode(currentRange.toString()));
		fontNode.MWFScriptType = type;
		fontNode.setAttribute("color", color);
		
		currentRange.deleteContents();
		currentRange.insertNode(fontNode);
		return fontNode;
	}
	
});

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
		var reg = this.options.keySeparat;

		var currentRange = this.editDocument.createRange();
		var tmpRange = this.editDocument.createRange();
		currentRange.setStart(scriptCheckNode, 0);
		currentRange.setEnd(scriptCheckNode, 1);
		
		var numberRanges = [];

		var length = scriptCheckNode.nodeValue.length;
		while (true){
			var text = currentRange.toString();
			
			if (!text.replace(reg, "")){
				currentRange.setStart(scriptCheckNode, currentRange.endOffset);
			}else{
				var flag = false;
				if (currentRange.endOffset+1>length){
					flag = true;
				}else{
					tmpRange.setStart(scriptCheckNode, currentRange.endOffset);
					tmpRange.setEnd(scriptCheckNode, currentRange.endOffset+1);
					flag = (!tmpRange.toString().replace(reg, ""));
				}
				
				if (flag){
				//	currentRange.setEnd(scriptCheckNode, currentRange.endOffset-1);
					
					if (Number.from(text)){
						numberRanges.push(currentRange.cloneRange());
					}

				//	currentRange.setEnd(scriptCheckNode, currentRange.endOffset+1);
					currentRange.setStart(scriptCheckNode, currentRange.endOffset);
				}
			}
			
			
			if (currentRange.endOffset+1>length){
				break;
			}else{
				currentRange.setEnd(scriptCheckNode, currentRange.endOffset+1);
			}
		}
		
		if (numberRanges.length){
			for (i=numberRanges.length-1; i>=0; i--){
				var range = numberRanges[i];
				this.createFormatFont(range, color, "number");
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
		
		if (scriptCheckNode.nodeValue.length){
			currentRange.setStart(scriptCheckNode, 0);
			currentRange.setEnd(scriptCheckNode, 1);
			
			var stringStartOffset = 0;
			var stringEndOffset = 0;
			
			var length = scriptCheckNode.nodeValue.length;
			
			var scriptTextStringMark = "\"";
			while (true){
				var text = currentRange.toString();
				if (!this.isScriptTextString){
					if (text=="\""){
						this.isScriptTextString = true;
						scriptTextStringMark = "\"";
						stringStartOffset = currentRange.startOffset;
					}
					if (text=="'"){
						this.isScriptTextString = true;
						scriptTextStringMark = "'";
						stringStartOffset = currentRange.startOffset;
					}
				}else{
					if (text==scriptTextStringMark){
						var tmpRange = this.editDocument.createRange();
						if (currentRange.startOffset>0){
							tmpRange.setEnd(scriptCheckNode, currentRange.startOffset);
							tmpRange.setStart(scriptCheckNode, currentRange.startOffset-1);
							if (tmpRange.toString()!="\\"){
								stringEndOffset = currentRange.endOffset;
							} 
						}else{
							stringEndOffset = currentRange.endOffset;
						}
						
						if (stringEndOffset!=0){
							tmpRange.setStart(scriptCheckNode, stringStartOffset);
							tmpRange.setEnd(scriptCheckNode, stringEndOffset);
							var font = this.createFormatFont(tmpRange, color, "string");
							this.isScriptTextString = false;
							
							scriptCheckNode = font.nextSibling;
							if (!scriptCheckNode || scriptCheckNode.nodeType!=3){
								break;
							}
							length = scriptCheckNode.nodeValue.length;
							
							currentRange.setStart(scriptCheckNode, 0);
							currentRange.setEnd(scriptCheckNode, 1);
						}
					}
				}

				if (currentRange.endOffset+1>length){
					break;
				}else{
					currentRange.setStart(scriptCheckNode, currentRange.startOffset+1);
					currentRange.setEnd(scriptCheckNode, currentRange.endOffset+1);
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
		var reds = this.css.keywords;
		var reg = this.options.keySeparat;
		
		var currentRange = this.editDocument.createRange();
		var tmpRange = this.editDocument.createRange();
		
		currentRange.setStart(scriptCheckNode, 0);
		currentRange.setEnd(scriptCheckNode, 1);
		
		var keyRanges = [];
		
		var idx=0;
		var length = scriptCheckNode.nodeValue.length;
		while (true){
			var text = currentRange.toString();
			
			if (!text.replace(reg, "")){
				currentRange.setStart(scriptCheckNode, currentRange.endOffset);
			}else{
				var flag = false;
				if (currentRange.endOffset+1>length){
					flag = true;
				}else{
					tmpRange.setStart(scriptCheckNode, currentRange.endOffset);
					tmpRange.setEnd(scriptCheckNode, currentRange.endOffset+1);
					flag = (!tmpRange.toString().replace(reg, ""));
				}
				
				if (flag){
				//	currentRange.setEnd(scriptCheckNode, currentRange.endOffset-1);
					
					if (reds.keys.indexOf(currentRange.toString().toLowerCase())!=-1){
						keyRanges.push(currentRange.cloneRange());
					}

				//	currentRange.setEnd(scriptCheckNode, currentRange.endOffset+1);
					currentRange.setStart(scriptCheckNode, currentRange.endOffset);
				}
			}
			
			
			if (currentRange.endOffset+1>length){
				break;
			}else{
				currentRange.setEnd(scriptCheckNode, currentRange.endOffset+1);
			}
		}
		
		if (keyRanges.length){
			for (i=keyRanges.length-1; i>=0; i--){
				var range = keyRanges[i];
				this.createFormatFont(range, reds.color, "keyword");
				
//				var fontNode = this.editDocument.createElement("font");
//				fontNode.appendChild(this.editDocument.createTextNode(range.toString()));
//				fontNode.MWFScriptType = "keyword";
//				fontNode.setAttribute("color", reds.color);
//				
//				range.deleteContents();
//				range.insertNode(fontNode);
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
			var multiCommentStartIndex = scriptCheckNode.nodeValue.indexOf("/*");

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
		
		
		

		if (!this.isScriptTextComment){
			if (scriptCheckNode.nodeValue.length>=2){
				currentRange.setStart(scriptCheckNode, 0);
				currentRange.setEnd(scriptCheckNode, 2);
				
				var length = scriptCheckNode.nodeValue.length;
				while (true){
					var text = currentRange.toString();
					if (text=="//"){
						currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
						this.createFormatFont(currentRange, color, "comment");
						break;
					}
					if (text=="/*"){
						currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
						this.createFormatFont(currentRange, color, "comment");
						this.isScriptTextComment = true;
						break;
					}
					if (currentRange.endOffset+1>length){
						break;
					}else{
						currentRange.setStart(scriptCheckNode, currentRange.startOffset+1);
						currentRange.setEnd(scriptCheckNode, currentRange.endOffset+1);
					}
				}
			}
		}else{
			currentRange.setStart(scriptCheckNode, 0);
			currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
			var lineText = currentRange.toString();
			if (lineText.indexOf("*/")==-1){
				currentRange.setStart(scriptCheckNode, 0);
				currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
				this.createFormatFont(currentRange, color, "comment");
			}else{
				if (scriptCheckNode.nodeValue.length>=2){
					currentRange.setStart(scriptCheckNode, 0);
					currentRange.setEnd(scriptCheckNode, 2);
					
					var length = scriptCheckNode.nodeValue.length;
					while (true){
						var text = currentRange.toString();
						if (text=="*/"){
							var offset = currentRange.startOffset;
							currentRange.setStart(scriptCheckNode, 0);
							var font = this.createFormatFont(currentRange, color, "comment");
							
							scriptCheckNode = font.nextSibling;
							if (!scriptCheckNode || scriptCheckNode.nodeType!=3){
								break;
							}
							length = scriptCheckNode.nodeValue.length;
							currentRange.setStart(scriptCheckNode, 0);
							currentRange.setEnd(scriptCheckNode, 2);
							
							//currentRange.setStart(scriptCheckNode, offset);
							this.isScriptTextComment = false;
						}
						
						if (text=="//"){
							currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
							this.createFormatFont(currentRange, color, "comment");
							break;
						}
						
						if (text=="/*"){
							currentRange.setEnd(scriptCheckNode, scriptCheckNode.nodeValue.length);
							this.createFormatFont(currentRange, color, "comment");
							this.isScriptTextComment = true;
							break;
						}
						if (currentRange.endOffset+1>length){
							break;
						}else{
							currentRange.setStart(scriptCheckNode, currentRange.startOffset+1);
							currentRange.setEnd(scriptCheckNode, currentRange.endOffset+1);
						}
					}
				}
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





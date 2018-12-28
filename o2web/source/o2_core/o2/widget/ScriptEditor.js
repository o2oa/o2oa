o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.ScriptEditor = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default",
		"keySeparat": /[\.\,\;\s\{\}\(\)\n\r\t\:\/\*\"\'=\[\]\+\-\/]+/ig,
		"keyCodes": [220, 46, 13, 222, 219, 221, 57, 48, 191, 190, 186, 188, 32, 55, 187, 189, 8, 9]
	},
	initialize: function(node, options){
		this.setOptions(options);
		
		this.node = $(node);
		
		this.path = o2.session.path+"/widget/$ScriptEditor/";
		this.cssPath = o2.session.path+"/widget/$ScriptEditor/"+this.options.style+"/css.wcss";
		this._loadCss();

		this.checkTextNodes = [];
		this.checkAll = false;
	},
	load: function(content, contentHTML){
		if (this.fireEvent("queryLoad")){
			this.createContent(content, contentHTML);		
			this.fireEvent("postLoad");
		}
	},
	
	toCode: function(){
		return this.editDocument.body.innerText;
	},
	toHTML: function(){
		return this.editDocument.body.innerHTML;
	},
	focus: function(){
		this.editDocument.body.focus();
	},
	createContent: function(content, contentHTML){
		this.editArea = new Element("iframe", {
			"styles": this.css.contentEditArea,
			"border": "0",
			"frameBorder": "0",
			"marginHeight": "0",
			"marginWidth": "0",
			"scrolling": "auto"
			
//			"events": {
//				"blur": function(){
//					this.fireEvent("change");
//				}.bind(this)
//			}
		}).inject(this.node);
		
		
		this.loadContent(content, contentHTML);
	},
	loadContent: function(content, contentHTML){
		this.editDocument = this.editArea.contentWindow.document;
		this.editWindow = this.editArea.contentWindow;
		if (contentHTML){
			this.editDocument.write("<style type=\"text/css\">p {margin:0px;}</style><body style=\"overflow:visible; word-break:keep-all; word-wrap:normal; font-size:12px; font-family:Verdana, Geneva, sans-serif; line-height:20px;\">"+contentHTML+"</body>");
		}else{
			this.editDocument.write("<style type=\"text/css\">p {margin:0px;}</style><body style=\"overflow:visible; word-break:keep-all; word-wrap:normal; font-size:12px; font-family:Verdana, Geneva, sans-serif; line-height:20px;\">"+content+"</body>");
		}
		
		this.editDocument.designMode = "On";
		
		this.clearFontElements(this.editDocument.body);
		
		this.checkScriptFormatComment(this.editDocument.body);
		this.checkScriptFormatString(this.editDocument.body);
		this.checkScriptFormatNumber(this.editDocument.body);
		this.checkScriptFormatKeyword(this.editDocument.body);
		
		this.setBodyEvent();
	},
	addElementEvent: function(el, event, fn){
		if (el.addEventListener){
			el.addEventListener(event, fn);
		}else{
			el.attachEvent("on"+event, function(){return fn();});
		}
	},
	setBodyEvent: function(){
		this.editDocument.body.style.whiteSpace = "nowrap";
		
		this.addElementEvent(this.editDocument.body, "blur", function(e){
			this.fireEvent("change");
		}.bind(this));
		
		this.addElementEvent(this.editDocument.body, "keydown", function(e){
			if (e.keyCode==9){
				var selection = this.editWindow.getSelection();
				var range = selection.getRangeAt(0);
				range.insertNode(this.editDocument.createTextNode("\x09"));
				
				try {e.preventDefault();}catch(e){};
				return false;
			}
		}.bind(this));
		
		this.addElementEvent(this.editDocument.body, "paste", function(e){
			this.checkAll = true;
		}.bind(this));
		
		this.addElementEvent(this.editDocument.body, "focus", function(e){
			if (this.checkAll){
				this.clearFontElements(this.editDocument.body);
				
				this.checkScriptFormatComment(this.editDocument.body);
				this.checkScriptFormatString(this.editDocument.body);
				this.checkScriptFormatNumber(this.editDocument.body);
				this.checkScriptFormatKeyword(this.editDocument.body);
				this.checkAll = false;
			}
		}.bind(this));
		this.addElementEvent(this.editDocument.body, "blur", function(e){
			if (this.checkAll){
				this.clearFontElements(this.editDocument.body);
				
				this.checkScriptFormatComment(this.editDocument.body);
				this.checkScriptFormatString(this.editDocument.body);
				this.checkScriptFormatNumber(this.editDocument.body);
				this.checkScriptFormatKeyword(this.editDocument.body);
				this.checkAll = false;
			}
		}.bind(this));
		
		this.addElementEvent(this.editDocument.body, "keyup", function(e){

			if (this.options.keyCodes.indexOf(e.keyCode)!=-1){
				this.checkScriptFormat(e.keyCode);
			}
		}.bind(this));
	},
	
	clearFontElements: function(node){
		var fonts = node.getElementsByTagName("font");
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
	checkScriptFormat: function(code){
		var selection = this.editWindow.getSelection();
		var textNode = selection.focusNode;
		
		if (code!=8 && code!=13 && code!=9){
			try {
				var offset = selection.focusOffset;
				var currentOffset = this.getCurrentOffset(textNode)+offset;
			}catch(e){};
		}

		var pNode = null;
		
		if (textNode.nodeType==3){
			pNode = textNode.parentNode;
			while(pNode && pNode.nodeType==1 && (pNode.tagName.toString().toLowerCase()!="p" && pNode.tagName.toString().toLowerCase()!="body")){
				pNode = pNode.parentElement;
			}
		}else{
			pNode = textNode.previousSibling;
			while(pNode && pNode.nodeType==1 && (pNode.tagName.toString().toLowerCase()!="p" && pNode.tagName.toString().toLowerCase()!="body")){
				pNode = pNode.parentElement;
			}
		}
		
		if (pNode){
			var cNode = (this.checkAll) ? this.editDocument.body : pNode;

			this.clearFontElements(cNode);
		//	this.checkTextNodes = [];
		//	this.getCheckScriptElements(pNode);
			
			this.checkScriptFormatComment(cNode);
			this.checkScriptFormatString(cNode);
			this.checkScriptFormatNumber(cNode);
			this.checkScriptFormatKeyword(cNode);
			this.checkAll = false;
			
			if (code!=8 && code!=13 && code!=9){
				try{
					var backOffsetObj = this.getBackOffset(currentOffset, pNode);
					selection.collapse(backOffsetObj.node, backOffsetObj.offset);
				}catch(e){};
			}
		}
	},
	getCurrentOffset: function(node){
		var offset = 0;
		var pnode = node.previousSibling;
		if (pnode){
			if (pnode.nodeType==3){
				offset = pnode.nodeValue.length;
			}else{
				offset = pnode.innerText.length;
			}
			offset += this.getCurrentOffset(pnode);
		}else{
			pnode = node.parentNode;
			if (pnode && pnode.nodeType==1 && (pnode.tagName.toString().toLowerCase()!="p" && pnode.tagName.toString().toLowerCase()!="body")){
				offset += this.getCurrentOffset(pnode);
			}
		}
		return offset;
	},
	getBackOffset: function(currentOffset, pNode){
		if (pNode.nodeType==3){
			var length = pNode.nodeValue.length;
			if (length>=currentOffset){
				return {"node": pNode, "offset": currentOffset};
			}else{
				currentOffset = currentOffset-length;
				tmpNode = pNode.nextSibling;
				while (!tmpNode){
					pNode = pNode.parentNode;
					tmpNode = pNode.nextSibling;
				}
				return this.getBackOffset(currentOffset, pNode.nextSibling);
			}
		}else{
			return this.getBackOffset(currentOffset, pNode.firstChild);
		}
	},
	
	checkScriptFormatNumber: function(pnode){
		this.checkTextNodes = [];
		this.getCheckScriptElements(pnode);

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
	
	checkScriptFormatString: function(pnode){
		this.checkTextNodes = [];
		this.getCheckScriptElements(pnode);

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
	
	checkScriptFormatKeyword: function(pnode){
		this.checkTextNodes = [];
		this.getCheckScriptElements(pnode);

		this.checkTextNodes.each(function(node){
			this.checkScriptTextFormatKeyword(node);
		}.bind(this)); 
	},
	
	checkScriptTextFormatKeyword: function(scriptCheckNode){
		var color = this.css.keywords.color;
		
		var currentRange = this.editDocument.createRange();
		
		var reg = /\b((function)|(if)|(else)|(case)|(switch)|(for)|(in)|(do)|(while)|(with)|(var)|(this)|(break)|(continue)|(return)|(true)|(false)|(throw)|(try)|(finally)|(catch)|(finally)|(debugger)|(new)|(delete))/ig;
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
	
	checkScriptFormatComment: function(pnode){
		this.checkTextNodes = [];
		this.getCheckScriptElements(pnode);
		
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
	},
	checkScriptFormat1: function(){
		var selection = this.editWindow.getSelection();
		var textNode = selection.focusNode;
		if (textNode.nodeType!=3){
			//不需要
			textNode = null;
		}else{
			var pNode = textNode.parentElement;
			if (pNode.tagName.toString().toLowerCase()=="font"){
				switch (pNode.MWFScriptType){
					case "comment_line": 
						
						
						
						//判断“//”符号，输入在//之前，插入点和输入内容移动到font之前， 并判断输入格式。
						//输入后无“//”， 删除整个font。并完整检查原font中的格式
						var text = pNode.firstChild.nodeValue;
						var idx = text.injdexOf("//");
						if (idx!=-1){
							var tmpText = text.substr(0, idx);
							if (tmpText){
								var prevNode = pNode.previousSibling;
								if (prevNode && prevNode.nodeType==3){
									prevNode.nodeValue = prevNode.nodeValue+tmpText; 
									textNode = prevNode;
								}else{
									pNode.parentElement.insertBefore(document.createTextNode(tmpText), pNode);
								}
								textNode.nodeValue = text.substr(idx, text.length);
							}
						}else{
							var prevNode = pNode.previousSibling;
							if (prevNode && prevNode.nodeType==3){
								prevNode.nodeValue = prevNode.nodeValue+text; 
							}else{
								pNode.parentElement.insertBefore(document.createTextNode(text), pNode);
							}
							pNode.parentElement.removeChild(pNode);
						}
						
					case "comment_multi":
						//先判断“/*”符号，输入在/*之前，插入点和输入内容移动到font之前， 并判断输入格式。
						//输入后无“/*”， 删除整个font，以及相关联的font。
						//输入后形成“*/”符号， 将font终点移到此处，并删除相关联的之后的font。
						//对删除font部分进行完整的格式检查。
						
					case "string": 
						//判断“"”或“'”符号，输入在 符号 之前，插入点和输入内容移动到font之前， 并判断输入格式。
						//判断“"”或“'”符号，输入在 符号 之后，插入点和输入内容移动到font之后， 并判断输入格式。
						//插入后无“"”或“'”符号，删除整个font， 对删除font部分进行完整的格式检查
						
						
					case "number": 
						//判断font中的内容是否还是数字，如果不是，删除font， 对删除font部分进行完整的格式检查
						
					case "keyword":
						//判断font中的内容是否还是关键字，如果不是，删除font， 对删除font部分进行完整的格式检查
						
				}
				
			}else{
				
				
			}
			
		}
	}
	
	
	
});





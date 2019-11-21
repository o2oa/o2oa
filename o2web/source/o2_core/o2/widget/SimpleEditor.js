/***************************
	HTML编辑器，使用前需要引入mootools js 文件和 o2 文件
	本文件可能用到的其他文件：
	../SimpleEditor/Actions 文件夹存放操作执行时需要的 js文件
	../SimpleEditor/<style> 存放对应样式的文件
	../SimpleEditor/<style>/editor.html 编辑器模板文件
	../SimpleEditor/<style>/css.wcss 编辑器样式文件
	../SimpleEditor/<style>/toolbarItems.json 操作对应的属性配置文件
	公用方法：
	var text = getContent()	获取编辑器的内容，返回字符串
	setContent(text)	传入字符串，设置编辑器的内容
	setDisable(boolean)	传入boolean使编辑器失效或有效
	setSubmitNodeDisable(boolean)	传入boolean使提交按钮失效或有效
	setCustomInfo(text)	传入文本，设置自定义区的内容
	setIframeHeight()	如果有iframe，同步编辑器和iframe的高度

	初始化样例如下
	参数说明： container 编辑器的容器，必要
				textarea 存储值的文本区，必要
				iframe	 如果编辑器包含在iframe中，那么传入iframe对象，否则传入null
	var editor; 
	function iniEditor(){
		var container = document.id( "container" );  
		var textarea = document.id( "demoTest" );		
		o2.require("o2.widget.SimpleEditor", function(){
			editor = new o2.widget.SimpleEditor({
				"style": "default",			//使用的样式文件夹
				"title": "",				//编辑器标题
				"hasTitleNode" : true,		//是否有标题区
				"editorDisabled": false,	//编辑区是否能进行编辑
				"hasToolbar" : true,		//是否生成操作条
				"toolbarDisable": false,	//操作条是否失效
				"hasSubmit" : true,			//是否形成提交按钮
				"submitDisable" : false,	//提交按钮是否失效
				"hasCustomArea" : true,		//是否有提示区
				"paragraphise": false,		//回车是否形成段落
				"minHeight": 200,			//最小高度
				"maxHeight": 717,			//编辑区的最大高度
				"overFlow" : "visible",		//可选项为 visible, auto 和 max ,visible 高度随内容变化， auto 内容高度超过minHeight时滚动条，max 内容高度超过maxHeight时滚动条(ie6 和 文档模式为杂项时，失效)
				"width": "95%",				//编辑器的宽度
				"action": "Image | Emotion",	//操作条上面有哪些操作，如果是all，表示使用 toolbarItems.json 里配置的所有操作，否则传入操作的 action ，用 空格隔开，比如 "Image | Emotion"
				"limit" : 0,				//输入长度限制，0表示无限制
				"onQueryLoad": function(){
					return true;
				},
				"onPostLoad": function(){
					editor.setCustomInfo("这是一个测试" );
				},
				"onSubmitForm": function(){
					editor.saveContent();
				}
			},container, textarea, iframe, null);
			editor.load();
		});
	}
	author cxy
***************************/
o2.widget.SimpleEditor = new Class({
	Extends: o2.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"title": "",
		"hasHeadNode" : true,
		"hasTitleNode" : true,
		"editorDisabled": false,
		"hasToolbar" : true,
		"toolbarDisable": false,
		"hasSubmit" : true,
		"submitDisable" : false,
		"hasCustomArea" : true,
		"paragraphise": false,
		"minHeight": 200,
		"maxHeight": 717,
		"limit" : 0,
		"overFlow" : "visible",		//可选项为 visible, auto 和 max ,visible 高度随内容变化， auto 内容高度超过minHeight时滚动条，max 内容高度超过maxHeight时滚动条(ie6 和 文档模式为杂项时，失效)
 		"width": "95%",
		"action": "all"	//操作条上面有哪些操作，如果是all，表示使用 toolbarItems.json 里配置的所有操作，否则传入操作的 action ，用 空格隔开，比如 "Image Emotion"
	},
	initialize: function( options, container, data, iframe, bindObject){
		
		this.protectRegex = /<(script|noscript|style)[\u0000-\uFFFF]*?<\/(script|noscript|style)>/g;

		this.setOptions(options);
		
		// action 具体的操作初始化在 EditorToolbarButton 类中执行
		
		this.bindObject = bindObject;

		this.protectedElements = [];

		this.path = o2.session.path+"/widget/$SimpleEditor/";
		this.cssPath = o2.session.path+"/widget/$SimpleEditor/"+this.options.style+"/css.wcss";
		this.editorPath = o2.session.path+"/widget/$SimpleEditor/"+this.options.style+"/editor.html";
		this.toolbarButtonPath = o2.session.path+"/widget/$SimpleEditor/"+this.options.style+"/toolbarItems.json";

		this._loadCss();

		this.iframe = iframe;
		this.container = document.id(container);
		this.data = data;
		//if(textarea){
		//	this.textarea = document.id(textarea);
		//}

	},
	load: function( data ){
		if( !this.container ) return;
		
		if (this.fireEvent("queryLoad",this)){
			if( this.iframe ){
				this.win = this.iframe.contentWindow;
				this.doc = (this.iframe.contentDocument) ? this.iframe.contentDocument : this.iframe.contentWindow.document ; //this.iframe.contentWindow.document; 
				// Mootoolize window, document and body
				Object.append(this.win, new Window);
				Object.append(this.doc, new Document);
				if (Browser.Element){
					var winElement = this.win.Element.prototype;
					for (var method in Element){ // methods from Element generics
						if (!method.test(/^[A-Z]|\$|prototype|mooEditable/)){
							winElement[method] = Element.prototype[method];
						}
					}
				} else {
					document.id(this.doc.body);
				}
			}else{
				this.win = window;
				this.doc = document;
			}
			
			if( data ){
				this.oldContent = data;
			}else{
				this.oldContent = this.data;
			}

			this.editorNode = this.createEditor();

			if( !this.editorNode ) return;
			if( this.options.hasTitleNode ) this.titleNode.set("text",this.options.title );
			this._setEditarea();
			if(this.options.hasToolbar)this._loadToolbar();
			if(this.options.hasSubmit)this.setSubmitNodeDisable( this.options.submitDisable );

			this.editorNode.inject( this.container );

			this.selection = new o2.widget.SimpleEditor.Selection(this.win,this.doc);

			this.setContent( this.oldContent );

			if( this.iframe )this.setIframeHeight();

		}
		
		this.fireEvent("postLoad",this);
	},
	setIframeHeight : function(){
		try{
			var iframeHeight = this.doc.body.scrollHeight;
			this.iframe.height = iframeHeight;
		}catch(e){
		}
	},
	createEditor: function(){
		var div = null;
		var request = new Request.HTML({
			url: this.editorPath,
			method: "GET",
			async: false,
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				div = responseTree[0];
			}
		});
		request.send();
		
		this.parentNode = div.getElement(".MWF_editor_parent");
		if( this.parentNode ){
			this.parentNode.set("styles", this.css.MWF_editor_parent);
			this.parentNode.setStyle("width", this.options.width );
		}

		this.headNode = div.getElement(".MWF_editor_head");
		if( this.options.hasHeadNode ){
			if( this.headNode )this.headNode.set("styles", this.css.MWF_editor_head);
		}else{
			this.headNode.dispose();
		}

		this.titleNode = div.getElement(".MWF_editor_title");
		if( this.titleNode ){
			if( this.options.hasTitleNode ){
				this.titleNode.set("styles", this.css.MWF_editor_title);
			}else{
				this.titleNode.dispose();
			}
		}else{
			this.options.hasTitleNode = false;
		}

		this.wrapperNode = div.getElement(".MWF_editor_wrapper");
		if( this.wrapperNode ) this.wrapperNode.set("styles", this.css.MWF_editor_wrapper);
		
		this.toolbarNode = div.getElement(".MWF_editor_toolbar");
		if( this.toolbarNode ){
			if( this.options.hasToolbar ){
				this.toolbarNode.set("styles", this.css.MWF_editor_toolbar);
			}else{
				this.toolbarNode.dispose();
			}
		}else{
			this.options.hasToolbar = false;
		}

		this.editareaWrapperNode = div.getElement(".MWF_editor_editarea_wrapper");
		if( this.editareaWrapperNode ) this.editareaWrapperNode.set("styles", this.css.MWF_editor_editarea_wrapper);

		this.editareaNode = div.getElement(".MWF_editor_editarea");
		if ( this.editareaNode ){
			this.editareaNode.set("styles", this.css.MWF_editor_editarea);
		}else{
			return null;
		}

		this.editbottomNode = div.getElement(".MWF_editor_bottom");
		if( this.editbottomNode ) this.editbottomNode.set('styles', this.css.MWF_editor_bottom);

		this.submitNode = div.getElement(".MWF_editor_submit");
		if( this.submitNode ){
			if( this.options.hasSubmit ){
				this.submitNode.set('styles', this.css.MWF_editor_submit);
			}else{
				this.submitNode.dispose();
			}
		}else{
			this.options.hasSubmit = false;
		}

		this.editCustomInfoNode = div.getElement(".MWF_editor_remindarea");
		if( this.editCustomInfoNode ){
			if( this.options.hasCustomArea ){
				this.editCustomInfoNode.set('styles', this.css.MWF_editor_remindarea);
				if( this.options.limit && this.options.limit!=0 ){
					this.editCustomInfoNode.set('text', "0/"+this.options.limit);
				}
			}else{
				this.editCustomInfoNode.dispose();
			}
		}else{
			this.options.hasCustomArea = false;
		}

		return div;
	},
	setCustomInfo : function( text ){
		if( this.options.hasCustomArea ) this.editCustomInfoNode.set("text",text );
	},
	_setEditarea : function(){
		var ie7s = (Browser.name == "ie" && !((typeof document.body.style.maxHeight != "undefined") && document.compatMode == "CSS1Compat"));
		if( ie7s ){
			/*
			this.editareaWrapperNode.set("styles", { 
					overflowY : 'auto',
					overflowX: 'hidden',
					height : (this.options.overFlow == "auto" ) ?  this.options.minHeight+2+'px' : 'auto'
				}
			)
			this.editareaNode.set("styles",{
					height : this.options.minHeight+'px'
				}
			)
			*/
			this.editareaNode.set("styles", { 
					overflowY : this.options.overFlow == 'max' ?  'visible' : this.options.overFlow,
					overflowX: 'hidden',
					height : this.options.minHeight+'px'
				}
			)
		}else{
			if (this.options.overFlow == "visible"){
				vMaxHeight = null;
				//vHeight = 'auto' ;
				vHeight = this.options.minHeight+'px';
			}else if (this.options.overFlow == "auto"){
				vMaxHeight = null;
				vHeight = this.options.minHeight+'px';
			}else if(this.options.overFlow == "max"){
				vMaxHeight = this.options.maxHeight+'px'
				vHeight = 'auto' ;
			}
			this.editareaNode.set("styles",{ 
				height : vHeight,
				minHeight : this.options.minHeight+'px' ,
				maxHeight : vMaxHeight ,
				overflowY : 'auto',
				//overflowY : 'visible',
				overflowX: 'hidden' }
			)
		}

		this.editareaNode.addEvents({
			mouseup: this._editorMouseUp.bind(this),
			mousedown: this._editorMouseDown.bind(this),
			mouseover: this._editorMouseOver.bind(this),
			mouseout: this._editorMouseOut.bind(this),
			mouseenter: this._editorMouseEnter.bind(this),
			mouseleave: this._editorMouseLeave.bind(this),
			contextmenu: this._editorContextMenu.bind(this),
			click: this._editorClick.bind(this),
			dblclick: this._editorDoubleClick.bind(this),
			keypress: this._editorKeyPress.bind(this),
			keyup: this._editorKeyUp.bind(this),
			keydown: this._editorKeyDown.bind(this),
			focus: this._editorFocus.bind(this),
			blur: this._editorBlur.bind(this),
			resize : this._editorResize.bind(this)
		});
		
		this.fireEvent("setEditarea",this);
	},
	setSubmitNodeDisable : function( submitDisable ){
		if( submitDisable ){
			if( this.css.MWF_editor_submit_disable ){
				this.submitNode.set("styles", this.css.MWF_editor_submit_disable );
				this.submitNode.removeEvents();
			}
		}else{
			this.submitNode.set('styles', this.css.MWF_editor_submit);
			if( this.css.MWF_editor_submit_hover ){
				this.submitNode.addEvents({
					mouseover: function(){
						this.submitNode.set("styles", this.css.MWF_editor_submit_hover )
					}.bind(this),
					mouseleave: function(){
						this.submitNode.set("styles", this.css.MWF_editor_submit )
					}.bind(this)
				});
			}
			
			this.submitNode.addEvent( "click", function(){
				this.fireEvent("submitForm",this);
			}.bind(this) )
		}
	},
	setDisable: function( disable ){
		this.options.editorDisabled = disable;
		if( this.options.hasToolbar ){
			this.toolbarDisable = disable;
			this.toolbar.setDisable(disable);
		}
		if( this.options.hasSubmit ){
			this.options.submitDisable = disable;
			this.setSubmitNodeDisable( disable );
		}
	},
	execute: function(command, param1, param2){
		if (this.busy) return;
		this.busy = true;
		this.doc.execCommand(command, param1, param2);
		this.saveContent();
		this.busy = false;
		return false;
	},
	
	setContent: function(content){
		var protect = this.protectedElements;
		content = content.replace(this.protectRegex, function(a){
			protect.push(a);
			return '<!-- editor:protect:' + (protect.length-1) + ' -->';
		});
		this.editareaNode.set('html', content);
		this.checkLimit("");
		return this;
	},
	getContent: function(){
		var protect = this.protectedElements;
		var html = this.editareaNode.get('html').replace(/<!-- editor:protect:([0-9]+) -->/g, function(a, b){
			return protect[b.toInt()];
		});
		// Remove useless BRs
		if (this.options.paragraphise) html = html.replace(/(h[1-6]|p|div|address|pre|li|ol|ul|blockquote|center|dl|dt|dd)><br ?\/?>/gi, '$1>');
		return html;
	},
	//saveContent: function(){
	//	if( this.textarea ){
	//		this.textarea.set('value', this.getContent());
	//	}
	//	return this;
	//},
	clear: function(){
		this.setContent("");
		if( this.options.hasCustomArea ){
			if( this.options.limit && this.options.limit!=0 ){
				this.editCustomInfoNode.set('text', "0/"+this.options.limit);
			}
		}
	},
	checkLimit : function(){
		if( this.options.limit == 0 )return;
		var html = this.editareaNode.get("html");

		this.contentLength = html.length;
		if( this.editCustomInfoNode ){
			if(  html == "<br>" ){
				this.editCustomInfoNode.set('text', "0/"+this.options.limit);
			}else{
				this.editCustomInfoNode.set('text', this.contentLength+"/"+this.options.limit);
			}
		}
		if( this.contentLength > this.options.limit ){
			if( !this.beyondLimit ){
				this.editareaNodeBGColor = this.editareaNode.getStyle("backgroundColor");
				this.editareaNode.setStyle( "backgroundColor" , "rgb(255, 216, 216)" );
				this.setSubmitNodeDisable( true );
				this.beyondLimit = true
			}
		}else{
			if( this.beyondLimit ){
				this.editareaNode.setStyle( "backgroundColor" , this.editareaNodeBGColor );
				this.setSubmitNodeDisable( false );
				this.beyondLimit = false
			}
		}
	},
	_loadToolbar: function(){
		this.toolbar =  new o2.widget.SimpleEditor.Toolbar( {
			"style" : this.options.style,
			"disable": this.options.toolbarDisable,
			"action": this.options.action,
			"onQueryToolbarLoad": function(){
				return true;
			},
			"onPostToolbarLoad": function(){
			}
		}, null, this.css, this.toolbarNode, this );
		this.toolbar.toolbarButtonPath = this.toolbarButtonPath;
		this.toolbar.load();
	},
	_editorResize: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}
		if( this.iframe && (this.options.overFlow == "visible" || this.options.overFlow == "max") ) this.setIframeHeight();
		
		this.fireEvent('_editorResize', [e, this]);
	},
	_editorFocus: function(e){
		this.oldContent = '';
		this.fireEvent('editorFocus', [e, this]);
	},
	
	_editorBlur: function(e){
		this.oldContent = this.getContent();

		this.checkLimit();

		this.fireEvent('editorBlur', [e, this]);
	},
	
	_editorMouseUp: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}
		
		//if (this.options.toolbar) this.checkStates();
		this.selectionRange = this.selection.getRange();
		
		this.fireEvent('editorMouseUp', [e, this]);
	},
	
	_editorMouseDown: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}
		
		this.fireEvent('editorMouseDown', [e, this]);
	},
	
	_editorMouseOver: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}
		
		this.fireEvent('editorMouseOver', [e, this]);
	},
	
	_editorMouseOut: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}
		
		this.fireEvent('editorMouseOut', [e, this]);
	},
	
	_editorMouseEnter: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}
		
		//if (this.oldContent && this.getContent() != this.oldContent){
		//	this.focus();
		//	this.fireEvent('editorPaste', [e, this]);
		//}
		
		this.fireEvent('editorMouseEnter', [e, this]);
	},
	
	_editorMouseLeave: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}
		
		this.fireEvent('editorMouseLeave', [e, this]);
	},
	
	_editorContextMenu: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}
		
		this.fireEvent('editorContextMenu', [e, this]);
	},
	
	_editorClick: function(e){
		// make images selectable and draggable in Safari
		if (Browser.name == "safari" || Browser.name == "chrome"){
			var el = e.target;
			if (Element.get(el, 'tag') == 'img'){
			
				// safari doesnt like dragging locally linked images
				//if (this.options.baseURL){
				//	if (el.getProperty('src').indexOf('http://') == -1){
				//		el.setProperty('src', this.options.baseURL + el.getProperty('src'));
				//	}
				//}
			
				this.selection.selectNode(el);
				//this.checkStates();
			}
		}
		
		this.selectionRange = this.selection.getRange();

		this.fireEvent('editorClick', [e, this]);
	},
	
	_editorDoubleClick: function(e){
		this.fireEvent('editorDoubleClick', [e, this]);
	},
	
	_editorKeyPress: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}

		//this.keyListener(e);
		
		this.fireEvent('editorKeyPress', [e, this]);
	},
	_editorKeyDown: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}
		if (e.key == 'enter'){
			if (this.options.paragraphise){
				if (e.shift && (Browser.name == "safari" || Browser.name == "chrome" || Browser.name == "firefox")){
					
					var s = this.selection;
					var r = s.getRange();
					
					// Insert BR element
					var br = this.doc.createElement('br');
					r.insertNode(br);
					
					// Place caret after BR
					r.setStartAfter(br);
					r.setEndAfter(br);
					s.setRange(r);
					
					// Could not place caret after BR then insert an nbsp entity and move the caret
					if (s.getSelection().focusNode == br.previousSibling){
						var nbsp = this.doc.createTextNode('\u00a0');
						var p = br.parentNode;
						var ns = br.nextSibling;
						(ns) ? p.insertBefore(nbsp, ns) : p.appendChild(nbsp);
						s.selectNode(nbsp);
						s.collapse(1);
					}
					
					// Scroll to new position, scrollIntoView can't be used due to bug: http://bugs.webkit.org/show_bug.cgi?id=16117
					this.win.scrollTo(0, Element.getOffsets(s.getRange().startContainer).y);
					
					e.preventDefault();
				} else if (Browser.name == "safari" || Browser.name == "chrome" || Browser.name == "firefox"){
					var node = this.selection.getNode();
					var isBlock = Element.getParents(node).include(node).some(function(el){
						return el.nodeName.test(blockEls);
					});
					if (!isBlock) this.execute('insertparagraph');
				}
			} else {
				if (Browser.name == "ie" ){
					//ie 会自动插入 p,改成插入换行
					var r = this.selection.getRange();
					var node = this.selection.getNode();
					if( r ){
						if( !node || node.get('tag') != 'li' ){
							this.selection.insertContent('<br>'); //r.text = "\r\n";
							r.select();
							this.selection.collapse(false);
						}
					}
					e.preventDefault();
				}
			}
		}
		
		this.fireEvent('editorKeyDown', [e, this]);
	},
	_editorKeyUp: function(e){
		if (this.options.editorDisabled){
			e.stopPropagation();
			return;
		}
		
		this.selectionRange = this.selection.getRange();

		if( this.iframe && (this.options.overFlow == "visible" || this.options.overFlow == "max") ) this.setIframeHeight();
		
		
		this.checkLimit();

		//var c = e.code;
		// 33-36 = pageup, pagedown, end, home; 45 = insert
		//if (this.options.toolbar && (/^enter|left|up|right|down|delete|backspace$/i.test(e.key) || (c >= 33 && c <= 36) || c == 45 || e.meta || e.control)){
		//	if (Browser.ie6){ // Delay for less cpu usage when you are typing
		//		clearTimeout(this.checkStatesDelay);
		//		this.checkStatesDelay = this.checkStates.delay(500, this);
		//	} else {
		//		this.checkStates();
		//	}
		//}
		
		this.fireEvent('editorKeyUp', [e, this]);
	}
});

o2.widget.SimpleEditor.Toolbar = new Class({
	Implements: [Options, Events],
	options: {
		"style" : "default",
		"disable": false,
		"action": "all"
	},
	initialize: function( options, container, css, toolbarNode, parentObj ){
		this.setOptions(options);
		this.container = container;
		this.css = css;
		this.editor = parentObj;
		if( toolbarNode ){
			this.node = toolbarNode;
			this.hasNode = true;
		}else{
			this.hasNode = false;
		}
		this.toolbarButtonPath = o2.session.path+"/widget/SimpleEditor/"+this.options.style+"/toolbarItems.json";
		this.actions = this.options.action.split(" ");

		this.items = [];
		this.children = [];
		this.childrenButton = [];
		this.childrenMenu = [];

	},
	load: function(){
		if( this.fireEvent( "queryToolbarLoad" ) ){
			if( !this.node ){
				this.node = new Element("div", {
					"styles": this.css.MWF_editor_toolbar,
					"class" : "MWF_editor_toolbar"
				});
			}
			var r = new Request.JSON({
				url: this.toolbarButtonPath,
				secure: false,
				async: false,
				method: "get",
				noCache: true,
				onSuccess: function(responseJSON, responseText){
					nodes = responseJSON.item;
					if (nodes) {
						if( this.actions.indexOf('all')!=-1 ){
							nodes.each(function(node, idx){
								switch (node.type){
								case "button" :
									this._loadToolBarButton(node);
									break;
								case "menu" :
									//this._loadToolBarMenu(); 
									break;
								case "separator" :
									this._loadToolBarSeparator();
									break;
								default :
									break;
								}
							}.bind(this))
						}else{
							var tmpNodes = {};
							nodes.each(function(node, idx){
								if( node.type=="button" || node.type=="menu" )tmpNodes[node.actionName]=node;
							})
							this.actions.each(function(action, idx){
								if( action == "|" ){
									this._loadToolBarSeparator();
								}else if( tmpNodes[action] ){
									node = tmpNodes[action];
									if( node.type=="button" ){
										this._loadToolBarButton(node);
									}else if(node.type=="menu"){
										//this._loadToolBarMenu(); 
									}
								}
							}.bind(this))
						}
					}
					if( !this.hasNode )this.node.inject(this.container);
					//if( this.options.disable) this.setDisable(this.options.disable);  
				}.bind(this),
				onError: function(text, error){
					alert(text);
				}
			});
			r.send();

			this.fireEvent( "postToolbarLoad" )
		}

	},
	_loadToolBarSeparator: function(){
		var node = new Element("label");
		node.set("styles", this.css.MWF_editor_toolbar_separator);
		node.inject(this.node);
	},
	_loadToolBarButton: function(node){
		if (node) {
			if( this.options.disable )node.disable = true;
			var btn =  new o2.widget.SimpleEditor.ToolbarButton( node, this.node, this.css,  this );
			btn.options.style = this.options.style;
			btn.load();
			this.fireEvent("buttonLoad", [btn]);
			if (btn.buttonID){
				this.items[btn.buttonID] = btn;
			}
			this.children.push(btn);
			this.childrenButton.push(btn);
		}
	},
	_loadToolBarMenu: function(nodes){
		var _self = this;
		if (nodes) {

		}
	},
	setDisable: function(flag){
		this.childrenButton.each(function(button, idx){
			button.setDisable( flag )
		}.bind(this));
	}
});

o2.widget.SimpleEditor.ToolbarButton = new Class({
	Implements: [Options, Events],
	options: {
		"style" : "default",
		"id" : "",
		"text": "",
		"title": "",
		"pic": "",
		"picDisable" : "",
		"actionName": "",
		"dialog" : "",
		"disable": false
	},
	initialize: function(options, toolbarNode, css, parentObj ){
		this.setOptions(options);
		this.toolbar = parentObj;
		if( this.toolbar.editor ) this.editor=this.toolbar.editor;
		this.toolbarNode = toolbarNode;
		this.css = css;		
		if(this.options.id!="")this.buttonID = this.options.id;
		this.modifiyStyle = false; //尚未开发
		this.isActive = false;
		this.dialogs = {};
	},
	load: function(){
		this.fireEvent("queryButtonLoad");

		this.node = new Element("span");
		this.node.set("styles", this.css.MWF_editor_toolbar_button_span);
		this.node.title = this.options.title;
		if (this.options.pic && this.options.pic!="") this.picNode = this._createImageNode( this.options.pic );
		if (this.options.text && this.options.text!="") this.textNode = this._createTextNode(this.options.text);

		if( this.options.disable) this.setDisable(this.options.disable); 
		this.node.inject(this.toolbarNode);

		var action = this.options.actionName;
		o2.require("o2.widget.$SimpleEditor.Actions."+action, function(){
			this.action = new o2.widget.SimpleEditor.Actions[action]({
				style: this.options.style
			},this.editor,this.toolbar,this);
			if (!this.action) return;
			if (this.action.options){
				//var key = act.options.shortcut;
				//if (key) this.keys[key] = action;
			}
			if (this.action.showDialog){
				//this.showDialog = act.showDialog;
			}
			if (this.action.events){
				Object.each(this.action.events, function(fn, event){
					this.addEvent(event, fn);
				}, this);
			}
		}.bind(this))
		
		this._addButtonEvent();
		this.fireEvent("postButtonLoad",this);
		
	},
	setDisable: function(flag){
		if (flag){
			this.node.set("styles", this.css.MWF_editor_toolbar_button_disable );
			if (this.picNode){
				this.picNode.set("styles", this.css.MWF_editor_toolbar_button_imagediv_disable);
				var img = this.picNode.getElement("img");
				img.set("src", this.options.picDisable );
			}
			if (this.textNode) this.textNode.set("styles", this.css.MWF_editor_toolbar_button_textdiv_disable);
			this.options.disable = true;
		}else{
			this.node.set("styles", this.css.MWF_editor_toolbar_button );
			if (this.picNode){
				this.picNode.set("styles", this.css.MWF_editor_toolbar_button_imagediv);
				var img = this.picNode.getElement("img");
				img.set("src", this.options.pic);
			}
			if (this.textNode) this.textNode.set("styles", this.css.MWF_editor_toolbar_button_textdiv);
			this.options.disable = false;
		}
	},
	_createImageNode: function(src){
		if (src){
			var div = new Element("span", {"styles": this.css.MWF_editor_toolbar_button_imagediv}).inject(this.node);
			var img = new Element("img", {
				"styles": this.css.MWF_editor_toolbar_button_image,
				"src": src
			}).inject(div);
			return div;
		}else{
			return null;
		}
	},
	_createTextNode: function(text){
		if (text){
			var div = new Element("span", {
				"styles": this.css.MWF_editor_toolbar_button_textdiv,
				"text": text
			}).inject(this.node);
			return div;
		}else{
			return null;
		}
	},

	_addButtonEvent: function(){
		this.node.addEvent("mouseover", this._buttonMouseOver.bind(this));
		this.node.addEvent("mouseout", this._buttonMouseOut.bind(this));
		this.node.addEvent("mousedown", this._buttonMouseDown.bind(this));
		this.node.addEvent("mouseup", this._buttonMouseUp.bind(this));
		this.node.addEvent("click", this._buttonClick.bind(this));
	},
	_buttonMouseOver: function(){
		if (this.modifiyStyle && !this.options.disable ) if (!this.options.disable){this.node.set("styles", this.css.MWF_editor_toolbar_button_over)};
	},
	_buttonMouseOut: function(){
		if (this.modifiyStyle && !this.options.disable ) if (!this.options.disable){this.node.set("styles", this.css.MWF_editor_toolbar_button_out)};
	},
	_buttonMouseDown: function(){
		if (this.modifiyStyle && !this.options.disable ) if (!this.options.disable){this.node.set("styles", this.css.MWF_editor_toolbar_button_down)};
	},
	_buttonMouseUp: function(){
		if (this.modifiyStyle && !this.options.disable ) if (!this.options.disable){this.node.set("styles", this.css.MWF_editor_toolbar_button_up)};
	},
	_buttonClick: function(){
		if (!this.options.disable){
			if (this.options.actionName && this.action && this.action.showDialog ){
				if( !this.isActive ){
					this.toolbar.childrenButton.each(function(button, idx){
						if( button.isActive && button.action && button.action.closeDialog ){
							button.action.closeDialog.call(button.action);
						}
					})
					this.action.showDialog.call(this.action);
					this.isActive = true;
				}
			}
		}
	}
});

o2.widget.SimpleEditor.Selection = new Class({

	initialize: function(win , doc){
		this.win = win;
		this.doc = doc;
	},

	getSelection: function(){
		//this.win.focus(); 
		return  ( this.doc.selection ) ? this.doc.selection : this.win.getSelection();
		//return (this.win.getSelection) ? this.win.getSelection() : this.win.document.selection;
	},

	getRange: function(){
		var s = this.getSelection();

		if (!s) return null;

		try {
			return s.rangeCount > 0 ? s.getRangeAt(0) : (s.createRange ? s.createRange() : null);
		} catch(e) {
			// IE bug when used in frameset
			return this.doc.body.createTextRange();
		}
	},

	setRange: function(range){
		if (range.select){
			Function.attempt(function(){
				range.select();
			});
		} else {
			var s = this.getSelection();
			if (s.addRange){
				s.removeAllRanges();
				s.addRange(range);
			}
		}
	},

	selectNode: function(node, collapse){
		var r = this.getRange();
		var s = this.getSelection();

		if (r.moveToElementText){
			Function.attempt(function(){
				r.moveToElementText(node);
				r.select();
			});
		} else if (s.addRange){
			collapse ? r.selectNodeContents(node) : r.selectNode(node);
			s.removeAllRanges();
			s.addRange(r);
		} else {
			s.setBaseAndExtent(node, 0, node, 1);
		}

		return node;
	},

	isCollapsed: function(){
		var r = this.getRange();
		if (r.item) return false;
		return r.boundingWidth == 0 || this.getSelection().isCollapsed;
	},

	collapse: function(toStart){
		var r = this.getRange();
		var s = this.getSelection();

		if (r.select){
			r.collapse(toStart);
			r.select();
		} else {
			toStart ? s.collapseToStart() : s.collapseToEnd();
		}
	},

	getContent: function(){
		var r = this.getRange();
		var body = new Element('body');

		if (this.isCollapsed()) return '';

		if (r.cloneContents){
			body.appendChild(r.cloneContents());
		} else if (r.item != undefined || r.htmlText != undefined){
			body.set('html', r.item ? r.item(0).outerHTML : r.htmlText);
		} else {
			body.set('html', r.toString());
		}

		var content = body.get('html');
		return content;
	},

	getText : function(){
		var r = this.getRange();
		var s = this.getSelection();
		return this.isCollapsed() ? '' : r.text || (s.toString ? s.toString() : '');
	},

	getNode: function(){
		var r = this.getRange();

		if ( Browser.name != "ie"  || Browser.version >= 9){
			var el = null;

			if (r){
				el = r.commonAncestorContainer;

				// Handle selection a image or other control like element such as anchors
				if (!r.collapsed)
					if (r.startContainer == r.endContainer)
						if (r.startOffset - r.endOffset < 2)
							if (r.startContainer.hasChildNodes())
								el = r.startContainer.childNodes[r.startOffset];

				while (typeOf(el) != 'element' && el) el = el.parentNode;
			}

			return document.id(el);
		}
		
		return document.id(r.item ? r.item(0) : r.parentElement());
	},

	insertContent: function(content){
		if (Browser.name == "ie"){
			var r = this.getRange();
			if (r.pasteHTML){
				r.pasteHTML(content);
				r.collapse(false);
				r.select();
			} else if (r.insertNode){
				r.deleteContents();
				if (r.createContextualFragment){
					 r.insertNode(r.createContextualFragment(content));
				} else {
					var doc = this.doc;
					var fragment = doc.createDocumentFragment();
					var temp = doc.createElement('div');
					fragment.appendChild(temp);
					temp.outerHTML = content;
					r.insertNode(fragment);
				}
				r.collapse(false);
			}
		} else {
			this.win.document.execCommand('insertHTML', false, content);
		}
	}

});

o2.widget.SimpleEditor.Dialog = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default",
		"fx": false	,	//动画效果
		"title": "dialog",
		"width": "300",
		"height": "150",
		"top": "0",
		"left": "0",
		"fromTop": "0",
		"fromLeft": "0",
		"mark": true,

		"html": "",
		"text": "",
		"url": "",
		"content": null,

		"isMax": false,
		"isClose": true,
		"isResize": true,
		"isMove": true,

		"buttons": null
	},
	initialize: function(container, options){
		this.setOptions(options);
		this.path = o2.session.path+"/widget/$SimpleEditor/"+this.options.style+"/Dialog/";
		this.cssPath = o2.session.path+"/widget/$SimpleEditor/"+this.options.style+"/Dialog/css.wcss";

		this._loadCss();

		if( container ){
			this.container = $(container)
		}else{
			this.container = $(document.body)
		}

		this.css.to.height = this.options.height;
		this.css.to.width = this.options.width;
		this.css.to.top = this.options.top;
		this.css.to.left = this.options.left;
		this.css.from.top = this.options.fromTop;
		this.css.from.left = this.options.fromLeft;

		this.contentUrl = o2.session.path+"/widget/$SimpleEditor/"+this.options.style+"/Dialog/dialog.html";

		var request = new Request.HTML({
			url: this.contentUrl,
			method: "GET",
			async: false,
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				this.node = responseTree[0];
				this.getDialogNode();
				this.fireEvent("postLoad", this);
			}.bind(this),
			onFailure: function(xhr){
				alert(xhr);
			}
		});
		request.send();
	},
	_loadCss: function(){
		var r = new Request.JSON({
			url: this.cssPath,
			secure: false,
			async: false,
			method: "get",
			noCache: true,
			onSuccess: function(responseJSON, responseText){
				this.css = responseJSON;
			}.bind(this),
			onError: function(text, error){
				alert(text);
			}
		});
		r.send();
	},
	reStyle: function(options){

		if (options) this.setOptions(options);
		this.css.to.height = this.options.height;
		this.css.to.width = this.options.width;
		this.css.to.top = this.options.top;
		this.css.to.left = this.options.left;
		this.css.to.top = this.options.top;
		this.css.from.top = this.options.fromTop;
		this.css.from.left = this.options.fromLeft;

		this.node.set("styles", this.css.from);
	},
	getDialogNode: function(){
		this.node.set("styles", this.css.from);

		this.title = this.node.getElement(".MWF_dialod_title");
		this.titleCenter = this.node.getElement(".MWF_dialod_title_center");
		this.titleText = this.node.getElement(".MWF_dialod_title_text");
		this.titleAction = this.node.getElement(".MWF_dialod_title_action");
		this.content = this.node.getElement(".MWF_dialod_content");
		this.bottom = this.node.getElement(".MWF_dialod_bottom");
		this.resizeNode = this.node.getElement(".MWF_dialod_bottom_resize");
		this.button = this.node.getElement(".MWF_dialod_button");

		if (this.title){
			this.title.addEvent("mousedown", function(){
				this.containerDrag = new Drag.Move(this.node);
			}.bind(this));
			this.title.addEvent("mouseup", function(){
				this.node.removeEvents("mousedown");
				this.title.addEvent("mousedown", function(){
					this.containerDrag = new Drag.Move(this.node);
				}.bind(this));
			}.bind(this));
		}

		if (this.titleText) this.getTitle();
		if (this.content) this.getContent();
		if (this.titleAction) this.getAction();
		if (this.resizeNode) this.setResizeNode();
		if (this.button) this.getButton();

		if (this.content) this.setContentSize();
		this.fireEvent("queryInjectNode");
		this.node.inject(this.container);
	},
	getButton: function(){
		for (i in this.options.buttons){
			var button = new Element("input", {
				"type": "button",
				"value": i,
				"styles": this.css.button,
				"events": {
					"click": this.options.buttons[i].bind(this)
				}
			}).inject(this.button)
		}
	},
	setContentSize: function(){
		var height = this.options.height;
		if (this.title){
			var h1 = this.title.getSize().y;
			height = height - h1;
		}
		if (this.bottom){
			var h2 = this.bottom.getSize().y;
			height = height - h2;
		}
		if (this.button){
			var h3 = this.button.getSize().y;
			height = height - h3;
		}
		this.content.setStyle("height", height);
	},
	getTitle: function(){
		this.titleText.set("text", this.options.title);
	},
	getContent: function(){
		if (this.options.content){
			this.options.content().inject(this.content);
		}else if (this.options.url){
			this.content.set("load", {"method": "get", "async": false});
			$(this.content).load(this.options.url);
			/*
			 var request = new Request.HTML({
			 url: this.options.url,
			 method: "GET",
			 onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
			 alert(responseHTML);
			 this.content.set("html", responseHTML);
			 }.bind(this),
			 onFailure: function(xhr){
			 alert("回退出现错误："+xhr.status+" "+xhr.statusText);
			 window.close();
			 }
			 });*/
		}else if (this.options.html){
			this.content.set("html", this.options.html);
		}else if (this.options.text){
			this.content.set("text", this.options.text);
		}
	},
	show: function(){
		if (this.options.mark) this._markShow();
		if (!this.morph && this.options.fx ){
			this.morph = new Fx.Morph(this.node, {duration: 200});
		}
		if (this.fireEvent("queryShow")){
			this.node.setStyle("display", "block");
			if( this.options.fx ){
				this.morph.start(this.css.to).chain(function(){
					this.fireEvent("postShow");
				}.bind(this));
			}else{
				this.node.setStyles(this.css.to);
				this.fireEvent("postShow");
			}
		}
	},
	hide: function() {
		if (!this.morph && this.options.fx ){
			this.morph = new Fx.Morph(this.node, {duration: 200});
		}
		if (this.fireEvent("queryHide")){
			if( this.options.fx ){
				this.morph.start(this.css.from).chain(function(){
					this._markHide();
					this.node.setStyle("display", "none");
					this.fireEvent("postHide");
				}.bind(this));
			}else{
				this._markHide();
				this.node.setStyle("display", "none");
				this.fireEvent("postHide");
			}
		}
	},
	close: function(){
		if (!this.morph && this.options.fx ){
			this.morph = new Fx.Morph(this.node, {duration: 200});
		}
		if (this.fireEvent("queryClose")){
			if( this.options.fx ){
				this.morph.start(this.css.from).chain(function(){
					this._markClose();
					this.node.destroy();
					this.node = null;
					this.fireEvent("postClose");
				}.bind(this));
			}else{
				this._markClose();
				this.node.destroy();
				this.fireEvent("postClose");
			}
		}
	},
	_markShow: function(){
		if (this.options.mark){
			if (!this.markNode){
				var size = o2.getMarkSize();
				this.markNode = new Element("div", {
					styles: this.css.mark
				}).inject($(document.body));
				this.markNode.set("styles", {
					"height": size.y,
					"width": size.x
				});
			}
			this.markNode.setStyle("display", "block");
		}
	},

	_markHide: function(){
		if (this.markNode){
			this.markNode.setStyle("display", "none");
		}
	},
	_markClose: function(){
		if (this.markNode){
			this.markNode.destroy();
		}
	}
});

o2.widget.SimpleEditor.Actions = o2.widget.SimpleEditor.Actions || {};
o2.widget.SimpleEditor.Actions.setting = o2.widget.SimpleEditor.Actions.setting || {};
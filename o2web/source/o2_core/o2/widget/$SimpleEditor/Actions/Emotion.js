o2.widget.SimpleEditor.Actions = o2.widget.SimpleEditor.Actions || {};
o2.widget.SimpleEditor.Actions.Emotion = new Class({
	Implements: [Options, Events],
	options: {
		style: "default",
		title: o2.LP.widget.SimpleEditor.insertEmotion
	},
	initialize: function(options, editor,toolbar,button,bindObj ){
		this.setOptions(options);
		this.editor = editor;
		this.toolbar = toolbar;
		this.button = button;
		this.bindObj = bindObj;
		this.settingPath = o2.session.path+"/widget/$SimpleEditor/"+this.options.style+"/ActionSetting.js";
		this._loadSetting();
	},
	_loadSetting : function(){
		var r = new Request({
			url: this.settingPath,
			async: false,
			method: "get",
			onSuccess: function(responseText, responseXML){
				this.setting = o2.widget.SimpleEditor.Actions.setting.emotion;
			}.bind(this),
			onFailure: function(xhr){
				alert(xhr.responseText);
			}
		});
		r.send();
	},
	command: function(e){
		this.fireEvent("queryCommand");

		var el = e.target;
		if (el.tagName.toLowerCase() != 'img') return;
		var src = $(el).get('src');
		this.closeDialog();
		var content = '<img class="MWF_editor_emotion" src="' + src + '" alt="">';
		if (this.editor.selectionRange){
			this.editor.selection.setRange(this.editor.selectionRange);
		}else{
			this.editor.editareaNode.focus();
		}
		this.editor.selection.insertContent(content);
		this.editor.selectionRange = this.editor.selection.getRange();
		this.fireEvent("postCommand");
	},
	closeDialog : function(){
		this.fireEvent("queryCloseDialog");
		this.dialog.close();
		this.dialog.node = null;
		if(this.button)this.button.isActive = false;
		if (this.editor.selectionRange)this.editor.selection.setRange(this.editor.selectionRange);
		if( this.closeFun ){
			document.body.removeEvent("mousedown", this.closeFun );
			this.closeFun = null;
		}
		this.fireEvent("postCloseDialog");
	},
	showDialog : function(){
		var self = this;
		var setting = this.setting;
		this.fireEvent("queryOpenDialog");



		var len = setting.imageName.length;
		var cols = setting.cols;
		var rows = setting.rows || Math.ceil( len / cols );
		var counter = 0;

		var html = "";
		html += "<center>";
		html += "  <table width=\"100%\">";
		for( var r=0 ;r < rows; r++ ){
			html += "    <tr>";
			for( var c=0; c < cols; c++ ){
				html += "   <td width=\"70\">";
				if( counter < len ){
					imgNames = setting.imageName[counter].split("|");
					title =  imgNames[1]  ?  "title='"+ imgNames[1] + "'" : "";
					html += "   <img style='cursor:pointer;border:0;padding:2px;' "+ title +" class='MWF_editor_emotion' src='"+ setting.imagesPath + imgNames[0] + setting.fileExt +"'>";
				}
				html += "  </td>";
				counter++;
			}
			html += "    </tr>";
		}
		html += "  </table>";
		html += "</center>";

		if( setting.dialog && setting.dialog.top ){
			var top = setting.dialog.top
			var container = $(document.body);
		}else if( self.editor.iframe ){
			var container = self.editor.iframe.getParent();
			var top = container.getCoordinates().top + ( self.button ? self.button.node.getCoordinates().bottom  : 130 ) ;
		}else{
			var top = self.button ? self.button.node.getCoordinates().bottom : 130 ;
			var container = self.editor.doc.body
		}

		if( setting.dialog && setting.dialog.left ){
			var left = setting.dialog.left
		}else if( self.editor.iframe ){
			var left = container.getCoordinates().left + ( self.button ? self.button.node.getCoordinates().left - 5 : 10 ) ;
		}else{
			var left = self.button ? self.button.node.getCoordinates().left : 10 ;
		}

		this.dialog = new o2.widget.SimpleEditor.Dialog( container, {
			"style": self.options.style,
			"title": self.options.title,
			"fx" : setting.dialog.fx || false,
			"html": html,
			"width": setting.dialog.width || cols*30,
			"height": setting.dialog.height || rows*30,
			"top": top ,
			"left": left,
			"fromTop": setting.dialog.fromTop || top,
			"fromLeft": setting.dialog.fromLeft || left,
			"isMax": setting.dialog.isMax || false,
			"isClose": setting.dialog.isClose || false,
			"isResize": setting.dialog.isResize || false,
			"isMove": setting.dialog.isMove || false,
			"mark": setting.dialog.mark || false,
			"onPostLoad": function(){
				//this.node.getElement(".MWF_dialod_close").addEvent("click", self.closeDialog.bind(self) );
				//this.node.getElements(".MWF_editor_emotion").addEvent("click", self.command.bind(self) );
			},
			"onQueryInjectNode": function(){
				//chrom  在iframe中，不能对已经存在的对象插入东东,所以不能在onPostLoad 做这件事
				this.node.addEvent("mousedown",function(e){e.stopPropagation()});
				this.node.getElement(".MWF_dialod_close") && this.node.getElement(".MWF_dialod_close").addEvent("click", self.closeDialog.bind(self) );
				this.node.getElements(".MWF_editor_emotion") && this.node.getElements(".MWF_editor_emotion").addEvent("click", self.command.bind(self) );
			},
			"onPostShow": function(){
				this.node.setStyle("height",(setting.dialog.height || rows*30)+30);
			}
		});
		this.dialog.show();
		this.closeFun = this.closeDialog.bind(this)
		document.body.addEvent("mousedown", this.closeFun );

		if(this.button)this.button.isActive = true;
		this.fireEvent("postOpenDialog");
	}
});
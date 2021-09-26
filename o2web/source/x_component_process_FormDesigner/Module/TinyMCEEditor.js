MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.TinyMCEEditor = MWF.FCTinyMCEEditor = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/TinyMCEEditor/TinyMCEEditor.html"
	},
	
	initialize: function(form, options){
		debugger;
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/TinyMCEEditor/";
		this.cssPath = "../x_component_process_FormDesigner/Module/TinyMCEEditor/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "tinymceeditor";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "tinymceeditor",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.addEvent("selectstart", function(e){
			e.preventDefault();
		});
//		this.loadCkeditor();
	},
	getDefaultConfig: function(){
		var config = {
			"branding": false,
			//skin:'oxide-dark',
			// language:'zh_CN',
			plugins: 'print preview searchreplace autolink directionality visualblocks visualchars fullscreen image link' +
				' media template code codesample table charmap hr pagebreak nonbreaking anchor insertdatetime' +
				' advlist lists wordcount imagetools textpattern help emoticons autosave' +
				' o2indent2em autoresize o2upimgs', //bdmap formatpainter
			toolbar: 'code undo redo | cut copy paste pastetext | forecolor backcolor bold italic underline strikethrough |'+ //restoredraft
				' alignleft aligncenter alignright alignjustify outdent indent o2indent2em lineheight | table image o2upimgs link |'+ //\\'+
    			' styleselect formatselect fontselect fontsizeselect | bullist numlist | blockquote subscript superscript removeformat |'+ // \\'+
				' media charmap emoticons anchor hr pagebreak insertdatetime print preview | fullscreen', //bdmap formatpainter
			height: 650, //编辑器高度
			min_height: 400,
			toolbar_mode: 'sliding',
			fontsize_formats: '12px 14px 16px 18px 24px 36px 48px 56px 72px',
			importcss_append: true,
			//自定义文件选择器的回调内容
			// file_picker_callback: function (callback, value, meta) {
			// 	if (meta.filetype === 'file') {
			// 		callback('https://www.baidu.com/img/bd_logo1.png', { text: 'My text' });
			// 	}
			// 	if (meta.filetype === 'image') {
			// 		callback('https://www.baidu.com/img/bd_logo1.png', { alt: 'My alt text' });
			// 	}
			// 	if (meta.filetype === 'media') {
			// 		callback('movie.mp4', { source2: 'alt.ogg', poster: 'https://www.baidu.com/img/bd_logo1.png' });
			// 	}
			// },
			toolbar_sticky: true,
			autosave_ask_before_unload: false,
		};
		if(o2.language === "zh-cn"){
			config.language = 'zh_CN';
			config.font_formats = '微软雅黑=Microsoft YaHei,Helvetica Neue,PingFang SC,sans-serif;' +
				'苹果苹方=PingFang SC,Microsoft YaHei,sans-serif;' +
				'宋体=simsun,serif;' +
				'仿宋体=FangSong,serif;' +
				'黑体=SimHei,sans-serif;' +
				'Arial=arial,helvetica,sans-serif;' +
				'Arial Black=arial black,avant garde;' +
				'Book Antiqua=book antiqua,palatino;';
		}
		return config;
	},
	_setEditStyle_custom: function(name){
		if (name=="editorProperties"){
			if (this.editor){
				Object.each(this.json.editorProperties, function(value, key){
					if (value=="true") this.json.editorProperties[key] = true;
					if (value=="false") this.json.editorProperties[key] = false;
				}.bind(this));
				this.distroyCkeditor();

                var config = Object.clone(this.json.editorProperties);
                if (this.json.config){
                    if (this.json.config.code){
                        var obj = MWF.Macro.exec(this.json.config.code, this);
                        Object.each(obj, function(v, k){
                            config[k] = v;
                        });
                    }
                }

				this.loadTinyMCEEditor(config);
			}
		}

        if (name=="templateCode"){
            if (this.editor) this.editor.setData(this.json.templateCode);
        }
	},
	

	_initModule: function(){
		this.node.empty();

        var config = Object.clone(this.json.editorProperties);
        if (this.json.config){
            if (this.json.config.code){
                var obj = MWF.Macro.exec(this.json.config.code, this);
                Object.each(obj, function(v, k){
                    config[k] = v;
                });
            }
        }

		this.loadTinyMCEEditor(config);
		this._setNodeProperty();
        if (!this.form.isSubform) this._createIconAction() ;
		this._setNodeEvent();
	},

	//ckeditor
	loadTinyMCEEditor: function(config){
		o2.load("../o2_lib/tinymce/tinymce_5.9.2/tinymce.min.js", function(){
			var editorConfig = Object.merge(this.getDefaultConfig(), config || {});

			var id = this.json.id + "_div";
			editorConfig.selector = '#'+id;
			var editorDiv = new Element("div", {"id": id}).inject(this.node);
			editorConfig.readonly = true;

			tinymce.init(editorConfig).then(function(v1, v2, v3){
				debugger;
			}.bind(this));

			// this.editor.on("dataReady", function(){
			// 	this.editor.setReadOnly(true);
			// }, this);
		}.bind(this));
	},
	destroy: function(){
		this.distroyCkeditor();
		this.form.moduleList.erase(this);
		this.form.moduleNodeList.erase(this.node);
		this.form.moduleElementNodeList.erase(this.node);

		if (this.form.scriptDesigner){
			this.form.scriptDesigner.removeModule(this.json);
		}

		if (this.property) this.property.destroy();
		this.node.destroy();
		this.actionArea.destroy();

		delete this.form.json.moduleList[this.json.id];
		this.json = null;
		delete this.json;

		this.treeNode.destroy();
		o2.release(this);
	},
	distroyCkeditor: function(){
		if (this.editor) this.editor.destroy();
		this.editor = null;
	}
});


//
// var useDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
//
// tinymce.init({
// 	selector: 'textarea#full-featured',
// 	plugins: 'print preview powerpaste casechange importcss tinydrive searchreplace autolink autosave save directionality advcode visualblocks visualchars fullscreen image link media mediaembed template codesample table charmap hr pagebreak nonbreaking anchor toc insertdatetime advlist lists checklist wordcount tinymcespellchecker a11ychecker imagetools textpattern noneditable help formatpainter permanentpen pageembed charmap tinycomments mentions quickbars linkchecker emoticons advtable export',
// 	tinydrive_token_provider: 'URL_TO_YOUR_TOKEN_PROVIDER',
// 	tinydrive_dropbox_app_key: 'YOUR_DROPBOX_APP_KEY',
// 	tinydrive_google_drive_key: 'YOUR_GOOGLE_DRIVE_KEY',
// 	tinydrive_google_drive_client_id: 'YOUR_GOOGLE_DRIVE_CLIENT_ID',
// 	mobile: {
// 		plugins: 'print preview powerpaste casechange importcss tinydrive searchreplace autolink autosave save directionality advcode visualblocks visualchars fullscreen image link media mediaembed template codesample table charmap hr pagebreak nonbreaking anchor toc insertdatetime advlist lists checklist wordcount tinymcespellchecker a11ychecker textpattern noneditable help formatpainter pageembed charmap mentions quickbars linkchecker emoticons advtable'
// 	},
// 	menu: {
// 		tc: {
// 			title: 'Comments',
// 			items: 'addcomment showcomments deleteallconversations'
// 		}
// 	},
// 	menubar: 'file edit view insert format tools table tc help',
// 	toolbar: 'undo redo | bold italic underline strikethrough | fontselect fontsizeselect formatselect | alignleft aligncenter alignright alignjustify | outdent indent |  numlist bullist checklist | forecolor backcolor casechange permanentpen formatpainter removeformat | pagebreak | charmap emoticons | fullscreen  preview save print | insertfile image media pageembed template link anchor codesample | a11ycheck ltr rtl | showcomments addcomment',
// 	autosave_ask_before_unload: true,
// 	autosave_interval: '30s',
// 	autosave_prefix: '{path}{query}-{id}-',
// 	autosave_restore_when_empty: false,
// 	autosave_retention: '2m',
// 	image_advtab: true,
// 	link_list: [
// 		{ title: 'My page 1', value: 'https://www.tiny.cloud' },
// 		{ title: 'My page 2', value: 'http://www.moxiecode.com' }
// 	],
// 	image_list: [
// 		{ title: 'My page 1', value: 'https://www.tiny.cloud' },
// 		{ title: 'My page 2', value: 'http://www.moxiecode.com' }
// 	],
// 	image_class_list: [
// 		{ title: 'None', value: '' },
// 		{ title: 'Some class', value: 'class-name' }
// 	],
// 	importcss_append: true,
// 	templates: [
// 		{ title: 'New Table', description: 'creates a new table', content: '<div class="mceTmpl"><table width="98%%"  border="0" cellspacing="0" cellpadding="0"><tr><th scope="col"> </th><th scope="col"> </th></tr><tr><td> </td><td> </td></tr></table></div>' },
// 		{ title: 'Starting my story', description: 'A cure for writers block', content: 'Once upon a time...' },
// 		{ title: 'New list with dates', description: 'New List with dates', content: '<div class="mceTmpl"><span class="cdate">cdate</span><br /><span class="mdate">mdate</span><h2>My List</h2><ul><li></li><li></li></ul></div>' }
// 	],
// 	template_cdate_format: '[Date Created (CDATE): %m/%d/%Y : %H:%M:%S]',
// 	template_mdate_format: '[Date Modified (MDATE): %m/%d/%Y : %H:%M:%S]',
// 	height: 600,
// 	image_caption: true,
// 	quickbars_selection_toolbar: 'bold italic | quicklink h2 h3 blockquote quickimage quicktable',
// 	noneditable_noneditable_class: 'mceNonEditable',
// 	toolbar_mode: 'sliding',
// 	spellchecker_ignore_list: ['Ephox', 'Moxiecode'],
// 	tinycomments_mode: 'embedded',
// 	content_style: '.mymention{ color: gray; }',
// 	contextmenu: 'link image imagetools table configurepermanentpen',
// 	a11y_advanced_options: true,
// 	skin: useDarkMode ? 'oxide-dark' : 'oxide',
// 	content_css: useDarkMode ? 'dark' : 'default',
// 	/*
//     The following settings require more configuration than shown here.
//     For information on configuring the mentions plugin, see:
//     https://www.tiny.cloud/docs/plugins/premium/mentions/.
//     */
// 	mentions_selector: '.mymention',
// 	mentions_fetch: mentions_fetch,
// 	mentions_menu_hover: mentions_menu_hover,
// 	mentions_menu_complete: mentions_menu_complete,
// 	mentions_select: mentions_select,
// 	mentions_item_type: 'profile'
// });




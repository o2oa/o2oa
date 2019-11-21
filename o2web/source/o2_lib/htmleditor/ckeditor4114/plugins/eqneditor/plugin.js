/* 
 Equation Editor Plugin for CKEditor v4
 Version 1.4

 This plugin allows equations to be created and edited from within CKEditor.
 For more information goto: http://www.codecogs.com/latex/integration/ckeditor_v4/install.php
 
 Copyright CodeCogs 2006-2013
 Written by Will Bateman.
*/
CKEDITOR.plugins.add( 'eqneditor', {
	availableLangs:{en:1},
	lang : "en",
	requires: [ 'dialog' ],
	icons: "eqneditor",
	
	init : function(editor)
	{		
	  var host='latex.codecogs.com';
	  var http=('https:' == document.location.protocol ? 'https://' : 'http://');
			
		// First make sure we have loaded the necessary scripts
  	CKEDITOR.scriptLoader.load( [
		http+host+'/js/eq_config.js', 
		http+host+'/js/eq_editor-lite-17.js',
		]);
		
		// Load Additional CSS 
		var fileref=document.createElement("link");
		fileref.setAttribute("rel", "stylesheet");
		fileref.setAttribute("type", "text/css");
		fileref.setAttribute("href", http+host+'/eqneditor/css/equation-embed.css');
		document.getElementsByTagName("head")[0].appendChild(fileref);
	
		var pluginCmd='eqneditorDialog';

		// Add the link and unlink buttons.
		editor.addCommand(pluginCmd, new CKEDITOR.dialogCommand(pluginCmd,
			{
				allowedContent: 'img[src,alt]',
				requiredContent: 'img[src,alt]'
			})
		);
		
		CKEDITOR.dialog.add(pluginCmd, this.path+"dialogs/eqneditor.js");
				
		editor.ui.addButton( 'EqnEditor', {
			label : editor.lang.eqneditor.toolbar,
			command : pluginCmd,
			icon: this.path + 'icons/eqneditor.png',
			toolbar: 'insert'
		});

		// add context-menu entry
		if (editor.contextMenu)
		{
			editor.addMenuGroup(editor.lang.eqneditor.menu);
			editor.addMenuItem( 'eqneditor', {
				label : editor.lang.eqneditor.edit,
				icon : this.path + 'icons/eqneditor.png',
				command : pluginCmd,
				group : editor.lang.eqneditor.menu
			});
			
			// if the selected item is image of class 'mathImg',
			// we shold be interested in it
			editor.contextMenu.addListener( function(element) {
				var res={};
				if (element.getAscendant('img', true))
				{
					var sName = element.getAttribute('src').match( /(gif|svg)\.latex\?(.*)/ );
					if(sName!=null)	
					{
						res['eqneditor'] = CKEDITOR.TRISTATE_OFF;
						return res;
					}
				}
			});
			
		}	
		
		editor.on( 'doubleclick', function(evt) 
		{
			var element = evt.data.element;
			if (element && element.is('img')) 
			{
				var sName = element.getAttribute('src').match( /(gif|svg)\.latex\?(.*)/ );
				if(sName!=null)
				{
					evt.data.dialog = pluginCmd;
					evt.cancelBubble = true; 
					evt.returnValue = false;
					evt.stop();	
				}
			}
		}, null, null, 1);
		
	}	
});

	


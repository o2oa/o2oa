CKEDITOR.plugins.add( 'textindent', {
    icons: 'textindent',
    availableLangs: {'pt-br':1, 'en':1},
    lang: 'pt-br, en',
	init: function( editor ) {

        var indentation = editor.config.indentation;
        var indentationKey = editor.config.indentationKey;

        if(typeof(indentation) == 'undefined')
            indentation = '50px';
        if(typeof(indentationKey) == 'undefined')
            indentationKey = 'tab';

        if(editor.ui.addButton){

            editor.ui.addButton( 'textindent', {
                label: editor.lang.textindent.labelName,
                command: 'ident-paragraph',
            });
        }

        if( indentationKey !== false){

            editor.on('key', function(ev) {
                if(ev.data.domEvent.$.key.toLowerCase() === indentationKey.toLowerCase().trim() || ev.data.keyCode === indentationKey){
                    editor.execCommand('ident-paragraph');
                    ev.cancel();  
                }
            });
        }
        
        editor.on( 'selectionChange', function()
            {
                var style_textindente = new CKEDITOR.style({
                        element: 'p',
                        styles: { 'text-indent': indentation },
                        overrides: [{
                            element: 'text-indent', attributes: { 'size': '0'}
                        }]
                    });

                if( style_textindente.checkActive(editor.elementPath(), editor) )
                   editor.getCommand('ident-paragraph').setState(CKEDITOR.TRISTATE_ON);
                else
                   editor.getCommand('ident-paragraph').setState(CKEDITOR.TRISTATE_OFF);
                
        });

        editor.addCommand("ident-paragraph", {
            allowedContent: 'p{text-indent}',
            requiredContent: 'p',
            exec: function(evt) {

                var range = editor.getSelection().getRanges()[0]; 

                var walker = new CKEDITOR.dom.walker( range ),
                node;

                var state = editor.getCommand('ident-paragraph').state;

                while ( ( node = walker.next() ) ) {
                    if ( node.type == CKEDITOR.NODE_ELEMENT ) {
                        if(node.getName() === "p"){
                                editor.fire('saveSnapshot');
                                if( state == CKEDITOR.TRISTATE_ON){
                                    node.removeStyle("text-indent");
                                    editor.getCommand('ident-paragraph').setState(CKEDITOR.TRISTATE_OFF);
                                }
                                else{
                                    node.setStyle( "text-indent", indentation );
                                    editor.getCommand('ident-paragraph').setState(CKEDITOR.TRISTATE_ON);
                                }
                        }
                    }
                }

                if(node === null){
                    
                    node = editor.getSelection().getStartElement().getAscendant('p', true);
    
                    editor.fire('saveSnapshot');

                    if( state == CKEDITOR.TRISTATE_ON){
                        node.removeStyle("text-indent");
                        editor.getCommand('ident-paragraph').setState(CKEDITOR.TRISTATE_OFF);
                    }
                    else{
                        node.setStyle( "text-indent", indentation );
                        editor.getCommand('ident-paragraph').setState(CKEDITOR.TRISTATE_ON);
                    }
                }
                

            }
        });
	}

});

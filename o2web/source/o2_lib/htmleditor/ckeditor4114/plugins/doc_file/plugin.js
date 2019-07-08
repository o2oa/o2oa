CKEDITOR.plugins.add( 'doc_file', {
    requires: 'widget',
    init: function( editor ) {
        editor.widgets.add( 'doc_file', {
            draggable: false,
            template:
                '<div class="o2_editorPlugin_doc_file"></div>',

            editables: {
                content: {
                    selector: '.o2_editorPlugin_doc_file',
                    allowedContent: true
                }
            },

            allowedContent:
                'div(o2_editorPlugin_doc_file)',

            requiredContent: 'div(o2_editorPlugin_doc_file);',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_file' );
            },

            edit: function(element){
            }
        });
    }
} );
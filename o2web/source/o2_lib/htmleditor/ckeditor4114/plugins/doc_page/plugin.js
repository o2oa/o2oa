CKEDITOR.plugins.add( 'doc_page', {
    requires: 'widget',
    init: function( editor ) {
        var pluginDirectory = this.path;
        editor.addContentsCss( pluginDirectory + 'contents.css' );
        editor.widgets.add( 'page', {

            button: 'page',
            draggable: false,

            template:
                '<div class="o2_editorPlugin_doc_Page"><div class="o2_editorPlugin_doc_Page_content"></div></div>',

            editables: {
                content: {
                    selector: '.o2_editorPlugin_doc_Page_content',
                    allowedContent: 'pre span div(o2_editorPlugin_redHeader)'
                }
            },

            allowedContent:
                'div(o2_editorPlugin_doc_Page); div(o2_editorPlugin_doc_Page_content)',

            requiredContent: 'div(o2_editorPlugin_doc_Page); div(o2_editorPlugin_doc_Page_content)',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_Page' );
            },

            edit: function(element){
            }
        });
    }
} );
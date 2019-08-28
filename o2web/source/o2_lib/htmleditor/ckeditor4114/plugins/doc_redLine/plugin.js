CKEDITOR.plugins.add( 'doc_redLine', {
    requires: 'widget',
    init: function( editor ) {
        editor.widgets.add( 'doc_redLine', {
            draggable: false,
            template:
                '<div class="o2_editorPlugin_doc_redline" ></div>',

            requiredContent: 'hr(o2_editorPlugin_doc_redline);',

            upcast: function( element ) {
                return element.name == 'hr' && element.hasClass( 'o2_editorPlugin_doc_redline' );
            },

            edit: function(element){
            }
        });
    }
});
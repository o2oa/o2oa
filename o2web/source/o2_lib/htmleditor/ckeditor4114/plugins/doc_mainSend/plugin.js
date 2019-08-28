CKEDITOR.plugins.add( 'doc_mainSend', {
    requires: 'widget',
    init: function( editor ) {
        editor.widgets.add( 'doc_mainSend', {
            draggable: false,
            template:
                '<div class="o2_editorPlugin_doc_mainSend">主送单位</div>',

            allowedContent:
                'div(o2_editorPlugin_doc_mainSend)',

            requiredContent: 'div(o2_editorPlugin_doc_mainSend);',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_mainSend' );
            },

            edit: function(element){
            }
        });
    }
} );
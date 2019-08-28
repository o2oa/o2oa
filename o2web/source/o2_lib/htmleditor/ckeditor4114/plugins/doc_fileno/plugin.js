CKEDITOR.plugins.add( 'doc_fileno', {
    requires: 'widget',
    init: function( editor ) {
        editor.widgets.add( 'doc_fileno', {
            draggable: false,
            template:
                '<div class="o2_editorPlugin_doc_fileno">浙移发〔2019〕20号</div>',

            // editables: {
            //     content: {
            //         selector: '.o2_editorPlugin_doc_copies; o2_editorPlugin_doc_secret; o2_editorPlugin_doc_priority',
            //         allowedContent: ''
            //     }
            // },

            allowedContent:
                'div(o2_editorPlugin_doc_fileno)',

            requiredContent: 'div(o2_editorPlugin_doc_fileno);',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_fileno' );
            },

            edit: function(element){
            }
        });
    }
});
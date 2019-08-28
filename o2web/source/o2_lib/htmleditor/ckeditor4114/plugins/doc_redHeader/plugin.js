CKEDITOR.plugins.add( 'doc_redHeader', {
    requires: 'widget',
    icons: 'ecnet',
    init: function( editor ) {
        // var pluginDirectory = this.path;
        // editor.addContentsCss( pluginDirectory + 'contents.css' );
        editor.widgets.add( 'doc_redHeader', {
            draggable: false,
            template:
                '<div class="o2_editorPlugin_redHeader"><div class="o2_editorPlugin_redHeader_content">文件红头</div></div>',

            editables: {
                content: {
                    selector: '.o2_editorPlugin_redHeader_content',
                    allowedContent: 'pre span'
                }
            },

            // allowedContent:
            //     'div(o2_editorPlugin_redHeader); div(o2_editorPlugin_redHeader_content)',

            requiredContent: 'div(o2_editorPlugin_redHeader); div(o2_editorPlugin_redHeader_content)',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'o2_editorPlugin_redHeader' );
            }
        });
    },
    onDestroy: function(e){
        e.cancel();
    },
    onfocus: function(){
    },
} );
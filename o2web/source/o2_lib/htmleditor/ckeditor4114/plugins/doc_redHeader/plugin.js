CKEDITOR.plugins.add( 'doc_redHeader', {
    requires: 'widget',
    icons: 'ecnet',
    init: function( editor ) {
        var pluginDirectory = this.path;
        editor.addContentsCss( pluginDirectory + 'contents.css' );
        editor.widgets.add( 'doc_redHeader', {

            button: 'Create a simple box',
            draggable: false,

            template:
                '<div class="o2_editorPlugin_redHeader"><div class="o2_editorPlugin_redHeader_content"><div class="o2_editorPlugin_redHeader_info">文件红头</div></div></div>',

            editables: {
                content: {
                    selector: '.o2_editorPlugin_redHeader_content',
                    allowedContent: 'pre span'
                }
            },

            allowedContent:
                'div(o2_editorPlugin_redHeader); div(o2_editorPlugin_redHeader_content); div(o2_editorPlugin_redHeader_info)',

            requiredContent: 'div(o2_editorPlugin_redHeader)',

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
    onLoad: function() {
        CKEDITOR.addCss(
            ".o2_editorPlugin_redHeader{\n" +
            "    overflow: hidden;\n" +
            "    text-align: center;\n" +
            "    margin-top: 3.5cm;\n" +
            "}\n" +
            ".o2_editorPlugin_redHeader_content{\n" +
            "    color: red;\n" +
            "    font-size: 1.8cm;\n" +
            "    font-family: \"宋体\", serif;\n" +
            "    line-height: normal;\n" +
            "    /*font-weight: bold;*/\n" +
            "    overflow: hidden;\n" +
            "}\n" +
            ".o2_editorPlugin_redHeader_info{\n" +
            "    color: #888888;\n" +
            "    font-weight: normal;\n" +
            "}"
        );
    }
} );
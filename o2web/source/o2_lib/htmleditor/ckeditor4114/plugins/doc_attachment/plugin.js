CKEDITOR.plugins.add( 'doc_attachment', {
    requires: 'widget',
    init: function( editor ) {
        editor.widgets.add( 'doc_attachment', {
            draggable: false,
            template:
                '<table class="o2_editorPlugin_doc_attachment" width="100%" cellpadding="0" cellspacing="0" border="0">' +
                '   <tr><td class="o2_editorPlugin_doc_attachment_title_td">' +
                '       <span>　　</span><span class="o2_editorPlugin_doc_attachment_title">附件：</span>' +
                '   </td><td class="o2_editorPlugin_doc_attachment_title_content_td">' +
                '       <span class="o2_editorPlugin_doc_attachment_content">附件名称</span>' +
                '   </td></tr>' +
                '</table>',

            requiredContent: 'div(o2_editorPlugin_doc_attachment);',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_attachment' );
            },

            edit: function(element){
            }
        });
    }
} );
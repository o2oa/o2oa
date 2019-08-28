CKEDITOR.plugins.add( 'doc_page', {
    requires: 'widget',
    init: function( editor ) {
        var pluginDirectory = this.path;
        editor.addContentsCss( pluginDirectory + 'contents.css' );
        editor.widgets.add( 'doc_page', {
            draggable: false,
            template:
                '<div class="o2_editorPlugin_doc_Page"><div class="o2_editorPlugin_doc_Page_content"></div></div>',

            editables: {
                content: {
                    selector: '.o2_editorPlugin_doc_file'
                    //allowedContent: true
                    // allowedContent: 'div(o2_editorPlugin_redHeader, o2_editorPlugin_redHeader_content,' +
                    //     'o2_editorPlugin_doc_copiesSecretPriority, o2_editorPlugin_doc_copies, o2_editorPlugin_doc_secret, ' +
                    //     'o2_editorPlugin_doc_priority,o2_editorPlugin_doc_fileno,o2_editorPlugin_doc_emptyLine,' +
                    //     'o2_editorPlugin_doc_subject,o2_editorPlugin_doc_mainSend);' +
                    //     'hr(o2_editorPlugin_doc_redline)[!color]'
                }
            },

            //allowedContent: 'div(o2_editorPlugin_doc_Page_content)',
                // 'div(o2_editorPlugin_redHeader, o2_editorPlugin_redHeader_content,' +
                // 'o2_editorPlugin_doc_copiesSecretPriority, o2_editorPlugin_doc_copies, o2_editorPlugin_doc_secret, o2_editorPlugin_doc_priority,' +
                // ',o2_editorPlugin_doc_subject,o2_editorPlugin_doc_mainSend,o2_editorPlugin_doc_file,o2_editorPlugin_doc_redline);' +
                // "table(o2_editorPlugin_doc_filenoup,o2_editorPlugin_doc_filenoup_signer_table)[width,cellpadding,cellspacing,border];" +
                // "tr td(o2_editorPlugin_doc_filenoup_td,o2_editorPlugin_doc_filenoup_td_signer,o2_editorPlugin_doc_filenoup_signer_td,o2_editorPlugin_doc_filenoup_signerContent_td);" +
                // 'span(o2_editorPlugin_doc_filenoup_fileno,o2_editorPlugin_doc_filenoup_signer,o2_editorPlugin_doc_filenoup_signerContent)',

            requiredContent: 'div(o2_editorPlugin_doc_Page); div(o2_editorPlugin_doc_Page_content)',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_Page' );
            },

            edit: function(element){
            }
        });
    }
} );

// CKEDITOR.plugins.add( 'doc_redHeader', {
//     requires: 'widget',
//     icons: 'ecnet',
//     init: function( editor ) {
//         editor.widgets.add( 'doc_redHeader', {
//             draggable: false,
//             template:
//                 '<div class="o2_editorPlugin_redHeader"><div class="o2_editorPlugin_redHeader_content">文件红头</div></div>',
//
//             editables: {
//                 content: {
//                     selector: '.o2_editorPlugin_redHeader_content',
//                     allowedContent: 'pre span'
//                 }
//             },
//             requiredContent: 'div(o2_editorPlugin_redHeader); div(o2_editorPlugin_redHeader_content)',
//             upcast: function( element ) {
//                 return element.name == 'div' && element.hasClass( 'o2_editorPlugin_redHeader' );
//             }
//         });
//     }
// });
//
// CKEDITOR.plugins.add( 'doc_copiesSecretPriority', {
//     requires: 'widget',
//     init: function( editor ) {
//         editor.widgets.add( 'doc_copiesSecretPriority', {
//             draggable: false,
//             template:
//                 '<div class="o2_editorPlugin_doc_copiesSecretPriority">' +
//                 '<div class="o2_editorPlugin_doc_copies">000001</div>' +
//                 '<div class="o2_editorPlugin_doc_secret">机密★1年</div>' +
//                 '<div class="o2_editorPlugin_doc_priority">特急</div>' +
//                 '</div>',
//
//             allowedContent:
//                 'div(o2_editorPlugin_doc_copiesSecretPriority, o2_editorPlugin_doc_copies, o2_editorPlugin_doc_secret, o2_editorPlugin_doc_priority)',
//
//             requiredContent: 'div(o2_editorPlugin_doc_copiesSecretPriority);',
//
//             upcast: function( element ) {
//                 return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_copiesSecretPriority' );
//             },
//
//             edit: function(element){
//             }
//         });
//     }
// });
//
// CKEDITOR.plugins.add( 'doc_fileno', {
//     requires: 'widget',
//     init: function( editor ) {
//         editor.widgets.add( 'doc_fileno', {
//             draggable: false,
//             template:
//                 '<div class="o2_editorPlugin_doc_fileno">浙移发〔2019〕20号</div>',
//             allowedContent: 'div(o2_editorPlugin_doc_fileno)',
//             requiredContent: 'div(o2_editorPlugin_doc_fileno);',
//             upcast: function( element ) {
//                 return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_fileno' );
//             },
//             edit: function(element){
//             }
//         });
//     }
// });
//
// CKEDITOR.plugins.add( 'doc_redLine', {
//     requires: 'widget',
//     init: function( editor ) {
//         editor.widgets.add( 'doc_redLine', {
//             draggable: false,
//             template:
//                 '<hr color="#ff0000" class="o2_editorPlugin_doc_redline" />',
//
//             requiredContent: 'hr(o2_editorPlugin_doc_redline);',
//
//             upcast: function( element ) {
//                 return element.name == 'hr' && element.hasClass( 'o2_editorPlugin_doc_redline' );
//             },
//
//             edit: function(element){
//             }
//         });
//     }
// });

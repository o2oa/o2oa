/**
 * @license Copyright (c) 2003-2021, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or https://ckeditor.com/legal/ckeditor-oss-license
 *但有html片段被黏贴到编辑器中，编辑器自动获取里面的图片链接上传到服务器
 */

'use strict';

( function() {
	var uniqueNameCounter = 0,
		// Black rectangle which is shown before the image is loaded.
		loadingImage = 'data:image/gif;base64,R0lGODlhDgAOAIAAAAAAAP///yH5BAAAAAAALAAAAAAOAA4AAAIMhI+py+0Po5y02qsKADs=';

	// Returns number as a string. If a number has 1 digit only it returns it prefixed with an extra 0.
	function padNumber( input ) {
		if ( input <= 9 ) {
			input = '0' + input;
		}

		return String( input );
	}

	// Returns a unique image file name.
	function getUniqueImageFileName( type ) {
		var date = new Date(),
			dateParts = [ date.getFullYear(), date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds() ];

		uniqueNameCounter += 1;

		return 'image-' + CKEDITOR.tools.array.map( dateParts, padNumber ).join( '' ) + '-' + uniqueNameCounter + '.' + type;
	}

	function isCurrentServerFile(url){
		if( url.indexOf("/x_file_assemble_control/") === -1 )return false;
		var fileHost = o2.Actions.getHost( "x_file_assemble_control" );
		var uri = new URI(url);
		var port = (uri.parsed.port && uri.parsed.port!=="80") ? (":"+uri.parsed.port) : "";
		var imgHost = uri.parsed.scheme + "://"+ uri.parsed.host + port;
		return fileHost === imgHost;
	}

	function getImageMaxWidth(editor){
		return editor.config.localImageMaxWidth || 2000;
	}

	function getFileName(url){
		var uri = new URI(url);
		var file = uri.parsed.file;
		if( !file ){
			return "untitle.jpg"
		}else if( file.indexOf(".") === -1 ){
			return file+".jpg"
		}else{
			return file;
		}
	}

	CKEDITOR.plugins.add( 'o2uploadremoteimage', {
		requires: 'uploadwidget',

		onLoad: function() {
			CKEDITOR.addCss(
				'.cke_upload_uploading img{' +
					'opacity: 0.3' +
				'}'
			);
		},

		isSupportedEnvironment: function() {
			return CKEDITOR.plugins.clipboard.isFileApiSupported;
		},

		init: function( editor ) {
			var _self = this;
			// Do not execute this paste listener if it will not be possible to upload file.
			if ( !this.isSupportedEnvironment() ) {
				return;
			}

			editor.filter.allow( 'img[alt,dir,id,lang,longdesc,!src,title,data-orgid,data-prv,data-id,data-width,data-height]{*}(*)' );
			editor.filter.allow( 'img[data-cke-img-unique-id]' );

			if( !editor.config.reference || !editor.config.referenceType )return;

			editor.on( 'paste', function( evt ) {
				debugger;

				if( evt.data.type !== "html" ){
					return;
				}

				if ( !evt.data.dataValue.match( /<img[\s\S]+src/i ) ) {
					return;
				}

				var data = evt.data,
					// Prevent XSS attacks.
					tempDoc = document.implementation.createHTMLDocument( '' ),
					temp = new CKEDITOR.dom.element( tempDoc.body ),
					imgs, img, i;

				// Without this isReadOnly will not works properly.
				temp.data( 'cke-editable', 1 );

				temp.appendHtml( data.dataValue );

				imgs = temp.find( 'img' );

				for ( i = 0; i < imgs.count(); i++ ) {
					img = imgs.getItem( i );

					// Assign src once, as it might be a big string, so there's no point in duplicating it all over the place.
					var imgSrc = img.getAttribute( 'src' ),
						// Image have to contain src=data:...
						isDataInSrc = imgSrc && imgSrc.substring( 0, 5 ) == 'data:',
						isRealObject = img.data( 'cke-realelement' ) === null,
						//inCurrentServer = isCurrentServerFile(imgSrc || "" ),
						isLocalFileSr = !isDataInSrc && imgSrc && imgSrc.substring( 0, 5 ) == 'file:';

					// We are not uploading images in non-editable blocs and fake objects (https://dev.ckeditor.com/ticket/13003).
					if ( imgSrc && !isDataInSrc && !isLocalFileSr && isRealObject && !img.data( 'cke-upload-id' ) && !img.isReadOnly( 1 ) ) {

						_self.upload(editor, img, imgSrc)

					}
				}

				data.dataValue = temp.getHtml();
			} );
		},
		upload: function (editor, img, imgSrc) {
			var imgFormat = imgSrc.match( /image\/([a-z]+?);/i ),
				imgFormat = ( imgFormat && imgFormat[ 1 ] ) || 'jpg',
				uniqueName = getUniqueImageFileName(imgFormat);

			img.setAttributes( {
				'data-cke-img-unique-id': uniqueName
			});

			var _upload = function ( width, height) {

				var fileName = getFileName(imgSrc);
				var	alt = img.getAttribute("alt");

				o2.Actions.load("x_file_assemble_control").FileAction.uploadWithUrl({
					referenceType: editor.config.referenceType,
					reference: editor.config.reference,
					scale: getImageMaxWidth(editor),
					fileName: fileName,
					fileUrl: imgSrc
				}, function (responseData) {
					var id = responseData.data ? responseData.data.id : responseData.id;
					var orgid = responseData.data ? responseData.data.origId : responseData.origId;
					var src = MWF.xDesktop.getImageSrc( id );

					// Width and height could be returned by server (https://dev.ckeditor.com/ticket/13519).
					//按最大宽度比率缩小
					var maxWidth = getImageMaxWidth(editor);
					if( maxWidth && maxWidth < width ){
						height = parseInt( height * (maxWidth / width) );
					}

					width = Math.min(width, maxWidth);

					var imgString = '<img src="' + src + '" ';
					imgString += 'data-id="' + id + '" ';
					imgString += 'data-orgid="' + orgid + '" ';
					imgString += 'data-height="' + height + 'px" ';
					imgString += 'data-width="' + width + 'px" ';
					imgString += 'style="max-width:100%; width:' + width + 'px" ';
					// imgString += 'onerror="MWF.xDesktop.setImageSrc()" ';
					imgString += 'alt="' + (alt || fileName) + '" ';
					imgString += 'data-prv="true" '; //editor.config.enablePreview ? 'data-prv="true" ' : 'data-prv="false" ';
					imgString += '">';

					var newImg = CKEDITOR.dom.element.createFromHtml( imgString );
					var oldImg = editor.editable().findOne( '[data-cke-img-unique-id="' + uniqueName + '"]');
					newImg.insertBefore( oldImg );
					oldImg.remove();

					editor.fire( 'change' );
				}, function () {
					return true;
				})
			};

			var width = img.getStyle("width");
			var $img = img.$;
			if( width && !isNaN(parseInt(width)) ){
				width = parseInt(width);
			}
			if( !width || isNaN(parseInt(width)) ){
				width = $img.naturalWidth;
			}

			var height = img.getStyle("height");
			if( height && !isNaN(parseInt(height)) ){
				height = parseInt(height);
			}
			if( !height || isNaN(parseInt(height)) ){
				height = $img.naturalHeight;
			}

			if( !width || !height ){
				new Element("img", {
					src: imgSrc,
					events: {
						"load": function () {
							if( this.naturalWidth && this.naturalHeight ){
								_upload(this.naturalWidth, this.naturalHeight);
							}
						}
					}
				})
			}else{
				_upload(width, height);
			}
		}
	} );

	/**
	 * The URL where images should be uploaded.
	 *
	 * @since 4.5.0
	 * @cfg {String} [imageUploadUrl='' (empty string = disabled)]
	 * @member CKEDITOR.config
	 */
} )();

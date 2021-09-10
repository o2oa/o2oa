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

	function isImageUploadUrl(uploadUrl){
		return uploadUrl.indexOf("/x_file_assemble_control/jaxrs/file/upload/with/url/referencetype/") > -1;
	}

	function getImageMaxWidth(editor){
		return editor.config.localImageMaxWidth || 2000;
	}

	CKEDITOR.plugins.add( 'o2remoteimage', {
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

		getImageUploadUrl: function( editor ){
			if (layout.config.app_protocol=="auto"){
				layout.config.app_protocol = window.location.protocol;
			}

			var addressObj = layout.serviceAddressList["x_file_assemble_control"];
			if (addressObj){
				var address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context;
			}else{
				var host = layout.config.center.host || window.location.hostname;
				var port = layout.config.center.port;
				var address = layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port)+"/x_program_center";
			}
			var url = "/jaxrs/file/upload/with/url/referencetype/"+editor.config.referenceType+"/reference/"+editor.config.reference+"/scale/"+ getImageMaxWidth(editor);
			return o2.filterUrl(address+url);
		},

		init: function( editor ) {
			// Do not execute this paste listener if it will not be possible to upload file.
			if ( !this.isSupportedEnvironment() ) {
				return;
			}

			editor.filter.allow( 'img[alt,dir,id,lang,longdesc,!src,title,onerror,data-orgid,data-prv,data-id,data-width,data-height]{*}(*)' );
			editor.filter.allow( 'img[data-cke-img-unique-id]' );

			if( !editor.config.reference || !editor.config.referenceType )return;

			var fileTools = CKEDITOR.fileTools,
				uploadUrl = this.getImageUploadUrl( editor, 'image' );

			if ( !uploadUrl ) {
				return;
			}


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
						inCurrentServer = isCurrentServerFile(imgSrc || "" ),
						isLocalFileSr = !isDataInSrc && imgSrc && imgSrc.substring( 0, 5 ) == 'file:';

					// We are not uploading images in non-editable blocs and fake objects (https://dev.ckeditor.com/ticket/13003).
					if ( imgSrc && !inCurrentServer && !isDataInSrc && !isLocalFileSr && isRealObject && !img.data( 'cke-upload-id' ) && !img.isReadOnly( 1 ) ) {

						var imgFormat = imgSrc.match( /image\/([a-z]+?);/i ),
						imgFormat = ( imgFormat && imgFormat[ 1 ] ) || 'jpg',
						uniqueName = getUniqueImageFileName(imgFormat);

						img.setAttributes( {
							'data-cke-img-unique-id': uniqueName
						});


						debugger;

						window.setTimeout(function(){
							var newImg = CKEDITOR.dom.element.createFromHtml( '<strong class="anyclass">My element</strong>' );
							var oldImg = editor.editable().findOne( '[data-cke-img-unique-id="' + this.uniqueName + '"]');
							newImg.insertBefore( oldImg );
						}.bind({uniqueName:uniqueName}), 100);



						continue;

						o2.Actions.load("x_file_assemble_control").uploadWithUrl(imgSrc, function (responseData) {
							var id = responseData.data ? responseData.data.id : responseData.id;
							var orgid = responseData.data ? responseData.data.origId : responseData.origId;
							var src = MWF.xDesktop.getImageSrc( id );

							// Width and height could be returned by server (https://dev.ckeditor.com/ticket/13519).
							var $img = this.img.$,
								width = $img.naturalWidth,
								height = $img.naturalHeight;

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
							imgString += 'onerror="MWF.xDesktop.setImageSrc()" ';
							if(upload.fileName){
								imgString += 'alt="' + upload.fileName + '" ';
							}
							imgString += editor.config.enablePreview ? 'data-prv="true" ' : 'data-prv="false" ';
							imgString += '">';

							// Set width and height to prevent blinking.
							// this.replaceWith( imgString );

							editor.fire( 'change' );
						}.bind({img: img}))
					}
				}

				data.dataValue = temp.getHtml();
			} );
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

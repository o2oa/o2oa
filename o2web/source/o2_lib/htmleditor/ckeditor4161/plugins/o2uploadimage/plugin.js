/**
 * @license Copyright (c) 2003-2021, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or https://ckeditor.com/legal/ckeditor-oss-license
 * 本插件允许从本地拖动/复制图片到编辑器中，编辑器自动上传到服务器
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

	function isImageUploadUrl(uploadUrl){
		return uploadUrl.indexOf("/x_file_assemble_control/jaxrs/file/upload/referencetype/") > -1;
	}

	function getImageMaxWidth(editor){
		return editor.config.localImageMaxWidth || 2000;
	}

	CKEDITOR.plugins.add( 'o2uploadimage', {
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
			var defaultPort = layout.config.app_protocol==='https' ? "443" : "80";
			if (addressObj){
				var appPort = addressObj.port || window.location.port;
				var address = layout.config.app_protocol+"//"+(addressObj.host || window.location.hostname)+((!appPort || appPort.toString()===defaultPort) ? "" : ":"+appPort)+addressObj.context;
			}else{
				var host = layout.desktop.centerServer.host || window.location.hostname;
				var port = layout.desktop.centerServer.port || window.location.port;
				var address = layout.config.app_protocol+"//"+host+((!port || port.toString()===defaultPort) ? "" : ":"+port)+"/x_file_assemble_control";
			}
			var url = "/jaxrs/file/upload/referencetype/"+editor.config.referenceType+"/reference/"+editor.config.reference+"/scale/"+ getImageMaxWidth(editor);
			return o2.filterUrl(address+url);
		},

		init: function( editor ) {
			// Do not execute this paste listener if it will not be possible to upload file.
			if ( !this.isSupportedEnvironment() ) {
				return;
			}

			editor.filter.allow( 'img[alt,dir,id,lang,longdesc,!src,title,data-orgid,data-prv,data-id,data-width,data-height]{*}(*)' );

			if( !editor.config.reference || !editor.config.referenceType )return;

			var fileTools = CKEDITOR.fileTools,
				uploadUrl = this.getImageUploadUrl( editor, 'image' );

			if ( !uploadUrl ) {
				return;
			}

			editor.on( 'fileUploadRequest', function( evt ) {
				debugger;
				var fileLoader = evt.data.fileLoader;

				//不是上传图片链接
				if( !isImageUploadUrl(fileLoader.uploadUrl) )return;

				fileLoader.xhr.open( 'PUT', fileLoader.uploadUrl, true );

				// Adding file to event's data by default - allows overwriting it by user's event listeners. (https://dev.ckeditor.com/ticket/13518)

				// if( (typeof fileLoader.file.name) === "string" ){ //is File
				// 	evt.data.requestData.file = fileLoader.file;
				// }else{  //is blob
				// 	evt.data.requestData.file = new window.File( [fileLoader.file], fileLoader.fileName, {
				// 		type: fileLoader.file.type
				// 	})
				// }
				// evt.data.requestData.file = fileLoader.file;
				// evt.data.requestData.name = fileLoader.fileName;

				evt.data.requestData.file = {
					file: fileLoader.file,
					name: fileLoader.fileName
				};
				evt.data.requestData.fileName = fileLoader.fileName;
				delete evt.data.requestData.upload;
			}, null, null, 10 );

			editor.on( 'fileUploadResponse', function( evt ) {
				debugger;
				var fileLoader = evt.data.fileLoader,
					xhr = fileLoader.xhr,
					data = evt.data;

				//不是上传图片链接
				if( !isImageUploadUrl(fileLoader.uploadUrl) )return;

				try {
					var responseJSON = JSON.parse( xhr.responseText );

					var success = function (responseJSON) {
						for ( var i in responseJSON ) {
							data[ i ] = responseJSON[ i ];
						}
					};

					var doError = function (xhr, text, error) {
						if (xhr.status!=0){
							var errorText = error;
							if (xhr){
								var json = JSON.decode(xhr.responseText);
								if (json){
									errorText = json.message.trim() || "request json error";
								}else{
									errorText = "request json error: "+xhr.responseText;
								}
							}
							errorText = errorText.replace(/\</g, "&lt;");
							errorText = errorText.replace(/\</g, "&gt;");
							data.message = errorText;
							MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
						}
					};

					if (responseJSON){
						switch(responseJSON.type) {
							case "warn":
								data.message = responseJSON.errorMessage.join("\n");
								MWF.xDesktop.notice("info", {x: "right", y:"top"}, data.message);
								success(responseJSON);
								evt.stop();
								break;
							case "error":
								doError(xhr, responseText, responseJSON.message);
								evt.cancel();
								break;
							default:
								success(responseJSON);
								evt.stop();
								break;
						}
					}else{
						doError(xhr, xhr.responseText, "");
						evt.cancel();
					}
				} catch ( err ) {
					// Response parsing error.
					data.message = fileLoader.lang.filetools.responseError;
					// CKEDITOR.warn( 'filetools-response-error', { responseText: xhr.responseText } );
					MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+xhr.responseText);

					evt.cancel();
				}
			}, null, null, 10 );

			// Handle images which are available in the dataTransfer.
			fileTools.addUploadWidget( editor, 'o2uploadimage', {
				supportedTypes: /image\/(jpeg|png|gif|bmp)/,

				skipNotifications :  true,

				uploadUrl: uploadUrl,

				fileToElement: function() {
					var img = new CKEDITOR.dom.element( 'img' );
					img.setAttribute( 'src', loadingImage );
					return img;
				},

				parts: {
					img: 'img'
				},

				onUploading: function( upload ) {
					// Show the image during the upload.
					this.parts.img.setAttribute( 'src', upload.data );
				},

				onUploaded: function( upload ) {

					debugger;
					var responseData = upload.responseData;
					var editor = upload.editor;

					var id = responseData.data ? responseData.data.id : responseData.id;
					var orgid = responseData.data ? responseData.data.origId : responseData.origId;
					var src = MWF.xDesktop.getImageSrc( id );

					// Width and height could be returned by server (https://dev.ckeditor.com/ticket/13519).
					var width = this.parts.img.getStyle("width");
					var $img = this.parts.img.$;
					if( width && !isNaN(parseInt(width)) ){
						width = parseInt(width);
					}
					if( !width || isNaN(parseInt(width)) ){
						width = upload.responseData.width || $img.naturalWidth;
					}

					var height = this.parts.img.getStyle("height");
					if( height && !isNaN(parseInt(height)) ){
						height = parseInt(height);
					}
					if( !height || isNaN(parseInt(height)) ){
						height = upload.responseData.height || $img.naturalHeight;
					}

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
					if(upload.fileName){
						imgString += 'alt="' + upload.fileName + '" ';
					}
					imgString += 'data-prv="true" '; //editor.config.enablePreview ? 'data-prv="true" ' : 'data-prv="false" ';
					imgString += '/>';

					// Set width and height to prevent blinking.
					this.replaceWith( imgString );

					editor.fire( 'change' );

					// this.replaceWith( '<img src="' + src + '" ' +
					// 	'width="' + width + '" ' +
					// 	'height="' + height + '">' );
				}
			} );

			// Handle images which are not available in the dataTransfer.
			// This means that we need to read them from the <img src="data:..."> elements.
			editor.on( 'paste', function( evt ) {
				// For performance reason do not parse data if it does not contain img tag and data attribute.
				debugger;
				//兼容几种模式: 1、File 2、Base64 3、图片地址指向本地，sr以 file:/// 开始，且带有File

				if( !evt.data.dataValue.match( /<img[\s\S]+data:/i ) && !evt.data.dataValue.match( /<img[\s\S]+file:\/\/\//i ) ){
					return;
				}

				// var localData = evt.data.dataTransfer._ ;
				// var flag = false;
				// if ( evt.data.dataValue.match( /<img[\s\S]+data:/i ) ) { //1、File 2、Base64
				// 	flag = true;
				// }else if( localData && localData.files && localData.files.length ){ //3、带有File
				// 	if( evt.data.dataValue.match( /<img[\s\S]+file:\/\/\//i ) ){ //有图片地址指向 file:///
				// 		flag = true;
				// 	}
				// }
				// if(!flag)return;

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
						isRealObject = img.data( 'cke-realelement' ) === null;

					// We are not uploading images in non-editable blocs and fake objects (https://dev.ckeditor.com/ticket/13003).
					if ( imgSrc && isRealObject && !img.data( 'cke-upload-id' ) && !img.isReadOnly( 1 ) ) {
						var file, imgFormat;
						if( imgSrc.substring( 0, 5 ) == 'data:' ){
							file = imgSrc;
							// Note that normally we'd extract this logic into a separate function, but we should not duplicate this string, as it might
							// be large.
							imgFormat = imgSrc.match( /image\/([a-z]+?);/i );
							imgFormat = ( imgFormat && imgFormat[ 1 ] ) || 'jpg';
						}else if( imgSrc.substring( 0, 5 ) == 'file:' ){
							var localData = evt.data.dataTransfer._ ;
							if( localData.files.length ){
								file = localData.files[0];
								imgFormat = (file.type || "").split("/");
								imgFormat = imgFormat.length > 1 ? imgFormat[imgFormat.length-1] : 'jpg';
							}else{
								img.remove();
							}
						}
						if(file){
							var loader = editor.uploadRepository.create( file, getUniqueImageFileName( imgFormat ) );
							loader.upload( uploadUrl );

							fileTools.markElement( img, 'o2uploadimage', loader.id );

							// fileTools.bindNotifications( editor, loader );
						}
					}
				}

				data.dataValue = temp.getHtml();
			} );

			editor.isO2uploadimageLoaded = true;
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

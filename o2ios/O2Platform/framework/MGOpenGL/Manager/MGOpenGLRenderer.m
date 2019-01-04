
/*
     File: RosyWriterOpenGLRenderer.m
 Abstract: The RosyWriter OpenGL effect renderer
  Version: 2.1
 Copyright (C) 2014 Apple Inc. All Rights Reserved.
 */

#import "MGOpenGLRenderer.h"
#import <OpenGLES/ES2/gl.h>
#import <OpenGLES/ES2/glext.h>
#import "GLESUtils.h"
#import "MGHeader.h"
#import "MGFaceModelArray.h"

@interface MGOpenGLRenderer ()
{
	EAGLContext *_oglContext;
	CVOpenGLESTextureCacheRef _textureCache;
	CVOpenGLESTextureCacheRef _renderTextureCache;
	CVPixelBufferPoolRef _bufferPool;
	CFDictionaryRef _bufferPoolAuxAttributes;
	CMFormatDescriptionRef _outputFormatDescription;
    
    GLuint _faceProgram;
    GLint _facePointSize;

    GLuint _face3DProgram;
    GLint _colorSelectorSlot;
    
    GLuint _videoProgram;
    GLint _frame;
    GLuint _offscreenBufferHandle;
    GLfloat _videoFrameW;
    GLfloat _videoFrameH;
}

@end

@implementation MGOpenGLRenderer

#pragma mark API

- (instancetype)init
{
    self = [super init];
    if ( self )
	{
		_oglContext = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES2];
		if ( ! _oglContext ) {
			NSLog( @"Problem with OpenGL context." );
			return nil;
		}
	}
	return self;
}

- (void)dealloc
{
	[self deleteBuffers];
	_oglContext = nil;
}

- (void)deleteBuffers
{
    EAGLContext *oldContext = [EAGLContext currentContext];
    if ( oldContext != _oglContext ) {
        if ( ! [EAGLContext setCurrentContext:_oglContext] ) {
            @throw [NSException exceptionWithName:NSInternalInconsistencyException reason:@"Problem with OpenGL context" userInfo:nil];
            return;
        }
    }
    if ( _offscreenBufferHandle ) {
        glDeleteFramebuffers( 1, &_offscreenBufferHandle );
        _offscreenBufferHandle = 0;
    }
    if ( _videoProgram ) {
        glDeleteProgram( _videoProgram );
        _videoProgram = 0;
    }
    if (_faceProgram) {
        glDeleteProgram(_faceProgram);
        _faceProgram = 0;
    }
    if (_face3DProgram) {
        glDeleteProgram(_face3DProgram);
        _face3DProgram = 0;
    }
    if ( _textureCache ) {
        CFRelease( _textureCache );
        _textureCache = 0;
    }
    if ( _renderTextureCache ) {
        CFRelease( _renderTextureCache );
        _renderTextureCache = 0;
    }
    if ( _bufferPool ) {
        CFRelease( _bufferPool );
        _bufferPool = NULL;
    }
    if ( _bufferPoolAuxAttributes ) {
        CFRelease( _bufferPoolAuxAttributes );
        _bufferPoolAuxAttributes = NULL;
    }
    if ( _outputFormatDescription ) {
        CFRelease( _outputFormatDescription );
        _outputFormatDescription = NULL;
    }
    if ( oldContext != _oglContext ) {
        [EAGLContext setCurrentContext:oldContext];
    }
}

#pragma mark RosyWriterRenderer

- (BOOL)operatesInPlace
{
	return NO;
}

- (FourCharCode)inputPixelFormat
{
	return kCVPixelFormatType_32BGRA;
}

- (void)prepareForInputWithFormatDescription:(CMFormatDescriptionRef)inputFormatDescription outputRetainedBufferCountHint:(size_t)outputRetainedBufferCountHint
{
	// The input and output dimensions are the same. This renderer doesn't do any scaling.
	CMVideoDimensions dimensions = CMVideoFormatDescriptionGetDimensions( inputFormatDescription );
	
	[self deleteBuffers];
	if ( ! [self initializeBuffersWithOutputDimensions:dimensions retainedBufferCountHint:outputRetainedBufferCountHint] ) {
		@throw [NSException exceptionWithName:NSInternalInconsistencyException reason:@"Problem preparing renderer." userInfo:nil];
	}
}

- (void)reset
{
	[self deleteBuffers];
}

//- (CVPixelBufferRef )copyRenderedPixelBuffer:(CMSampleBufferRef)sampleBufferRef faceModelArray:(MGFaceModelArray *)modelArray drawLandmark:(BOOL)drawLandmark
//{
//    CVPixelBufferRef pixelBuffer = CMSampleBufferGetImageBuffer(sampleBufferRef);
//
//    if ( _offscreenBufferHandle == 0 ) {
//        @throw [NSException exceptionWithName:NSInternalInconsistencyException reason:@"Unintialized buffer" userInfo:nil];
//        return nil;
//    }
//
//    if ( pixelBuffer == NULL ) {
//        @throw [NSException exceptionWithName:NSInvalidArgumentException reason:@"NULL pixel buffer" userInfo:nil];
//        return nil;
//    }
//
//    const CMVideoDimensions srcDimensions = { (int32_t)CVPixelBufferGetWidth(pixelBuffer), (int32_t)CVPixelBufferGetHeight(pixelBuffer) };
//    const CMVideoDimensions dstDimensions = CMVideoFormatDescriptionGetDimensions( _outputFormatDescription );
//
//    _videoFrameW = dstDimensions.width;
//    _videoFrameH = dstDimensions.height;
//
//    if ( _videoFrameW != _videoFrameW || _videoFrameH != _videoFrameH ) {
//        @throw [NSException exceptionWithName:NSInvalidArgumentException reason:@"Invalid pixel buffer dimensions" userInfo:nil];
//        return nil;
//    }
//
//    if ( CVPixelBufferGetPixelFormatType( pixelBuffer ) != kCVPixelFormatType_32BGRA ) {
//        @throw [NSException exceptionWithName:NSInvalidArgumentException reason:@"Invalid pixel buffer format" userInfo:nil];
//        return nil;
//    }
//
//    EAGLContext *oldContext = [EAGLContext currentContext];
//    if ( oldContext != _oglContext ) {
//        if ( ! [EAGLContext setCurrentContext:_oglContext] ) {
//            @throw [NSException exceptionWithName:NSInternalInconsistencyException reason:@"Problem with OpenGL context" userInfo:nil];
//            return nil;
//        }
//    }
//
//    CVReturn err = noErr;
//    CVOpenGLESTextureRef srcTexture = NULL, dstTexture = NULL;
//    CVPixelBufferRef dstPixelBuffer = NULL;
//
//    err = CVOpenGLESTextureCacheCreateTextureFromImage(kCFAllocatorDefault,
//                                                       _textureCache,
//                                                       pixelBuffer,
//                                                       NULL,
//                                                       GL_TEXTURE_2D,
//                                                       GL_RGBA,
//                                                       _videoFrameW, _videoFrameH,
//                                                       GL_BGRA,
//                                                       GL_UNSIGNED_BYTE,
//                                                       0,
//                                                       &srcTexture );
//    if ( ! srcTexture || err ) {
//        NSLog( @"Error at CVOpenGLESTextureCacheCreateTextureFromImage %d", err );
//        goto bail;
//    }
//
//    err = CVPixelBufferPoolCreatePixelBufferWithAuxAttributes( kCFAllocatorDefault, _bufferPool, _bufferPoolAuxAttributes, &dstPixelBuffer );
//    if ( err == kCVReturnWouldExceedAllocationThreshold ) {
//        // Flush the texture cache to potentially release the retained buffers and try again to create a pixel buffer
//        CVOpenGLESTextureCacheFlush( _renderTextureCache, 0 );
//        err = CVPixelBufferPoolCreatePixelBufferWithAuxAttributes( kCFAllocatorDefault, _bufferPool, _bufferPoolAuxAttributes, &dstPixelBuffer );
//    }
//    if ( err ) {
//        if ( err == kCVReturnWouldExceedAllocationThreshold ) {
//            NSLog( @"Pool is out of buffers, dropping frame" );
//        }
//        else {
//            NSLog( @"Error at CVPixelBufferPoolCreatePixelBuffer %d", err );
//        }
//        goto bail;
//    }
//
//    err = CVOpenGLESTextureCacheCreateTextureFromImage( kCFAllocatorDefault,
//                                                       _renderTextureCache,
//                                                       dstPixelBuffer,
//                                                       NULL,
//                                                       GL_TEXTURE_2D,
//                                                       GL_RGBA,
//                                                       _videoFrameW, _videoFrameH,
//                                                       GL_BGRA,
//                                                       GL_UNSIGNED_BYTE,
//                                                       0,
//                                                       &dstTexture );
//
//    if ( ! dstTexture || err ) {
//        NSLog( @"Error at CVOpenGLESTextureCacheCreateTextureFromImage %d", err );
//        goto bail;
//    }
//
//    glBindFramebuffer( GL_FRAMEBUFFER, _offscreenBufferHandle );
//    glViewport( 0, 0, srcDimensions.width, srcDimensions.height );
//    glUseProgram( _videoProgram );
//
//    // Set up our destination pixel buffer as the framebuffer's render target.
//    glActiveTexture( GL_TEXTURE0 );
//    glBindTexture( CVOpenGLESTextureGetTarget( dstTexture ), CVOpenGLESTextureGetName( dstTexture ) );
//    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
//    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
//    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
//    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );
//
//    glFramebufferTexture2D( GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, CVOpenGLESTextureGetTarget( dstTexture ), CVOpenGLESTextureGetName( dstTexture ), 0 );
//
//    // Render our source pixel buffer.
//    glActiveTexture( GL_TEXTURE1 );
//    glBindTexture( CVOpenGLESTextureGetTarget( srcTexture ), CVOpenGLESTextureGetName( srcTexture ) );
//    glUniform1i(_frame, 1 );
//
//    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//
//    glVertexAttribPointer(ATTRIB_VERTEX, 2, GL_FLOAT, 0, 0, squareVertices);
//    glEnableVertexAttribArray( ATTRIB_VERTEX );
//
//    glVertexAttribPointer(ATTRIB_TEXTUREPOSITON, 2, GL_FLOAT, 0, 0, textureVertices);
//    glEnableVertexAttribArray(ATTRIB_TEXTUREPOSITON );
//    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
//
//    glBindTexture( CVOpenGLESTextureGetTarget( srcTexture ), 0 );
//    glBindTexture( CVOpenGLESTextureGetTarget( dstTexture ), 0 );
//
//    if (modelArray && drawLandmark){
//        /* 绘制人脸关键点 */
//        glActiveTexture(GL_TEXTURE2);
//        glUseProgram(_faceProgram);
//
//        [self drawFaceWithRect:modelArray.detectRect];
//        for (int i =0; i < modelArray.count; i++) {
//            MGFaceInfo *model = [modelArray modelWithIndex:i];
//            [self drawFacePointer:model.points faceRect:model.rect];
//        }
//
//
//        if (self.show3DView == YES) {
//            if (modelArray.count >= 1) {
//                /* 绘制人脸 3D 图层 */
//                glActiveTexture(GL_TEXTURE3);
//                glUseProgram(_face3DProgram);
//
//                MGFaceInfo *firstInfo = [modelArray modelWithIndex:0];
//                [self drawTriConeX:-firstInfo.pitch Y:-firstInfo.yaw Z:-firstInfo.roll];
//            }
//        }
//    }
//
//    // Make sure that outstanding GL commands which render to the destination pixel buffer have been submitted.
//    // AVAssetWriter, AVSampleBufferDisplayLayer, and GL will block until the rendering is complete when sourcing from this pixel buffer.
//    glFlush();
//
//bail:
//    if ( oldContext != _oglContext ) {
//        [EAGLContext setCurrentContext:oldContext];
//    }
//    if ( srcTexture ) {
//        CFRelease( srcTexture );
//    }
//    if ( dstTexture ) {
//        CFRelease( dstTexture );
//    }
//
//    return dstPixelBuffer;
//}

- (CVPixelBufferRef )drawPixelBuffer:(CMSampleBufferRef)sampleBufferRef custumDrawing:(void (^)(void))draw
{
    CVPixelBufferRef pixelBuffer = CMSampleBufferGetImageBuffer(sampleBufferRef);
    
    if ( _offscreenBufferHandle == 0 ) {
        @throw [NSException exceptionWithName:NSInternalInconsistencyException reason:@"Unintialized buffer" userInfo:nil];
        return nil;
    }
    
    if ( pixelBuffer == NULL ) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException reason:@"NULL pixel buffer" userInfo:nil];
        return nil;
    }
    
    const CMVideoDimensions srcDimensions = { (int32_t)CVPixelBufferGetWidth(pixelBuffer), (int32_t)CVPixelBufferGetHeight(pixelBuffer) };
    const CMVideoDimensions dstDimensions = CMVideoFormatDescriptionGetDimensions( _outputFormatDescription );
    
    _videoFrameW = dstDimensions.width;
    _videoFrameH = dstDimensions.height;
    
    if ( _videoFrameW != _videoFrameW || _videoFrameH != _videoFrameH ) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException reason:@"Invalid pixel buffer dimensions" userInfo:nil];
        return nil;
    }
    
    if ( CVPixelBufferGetPixelFormatType( pixelBuffer ) != kCVPixelFormatType_32BGRA ) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException reason:@"Invalid pixel buffer format" userInfo:nil];
        return nil;
    }
    
    EAGLContext *oldContext = [EAGLContext currentContext];
    if ( oldContext != _oglContext ) {
        if ( ! [EAGLContext setCurrentContext:_oglContext] ) {
            @throw [NSException exceptionWithName:NSInternalInconsistencyException reason:@"Problem with OpenGL context" userInfo:nil];
            return nil;
        }
    }
    
    CVReturn err = noErr;
    CVOpenGLESTextureRef srcTexture = NULL, dstTexture = NULL;
    CVPixelBufferRef dstPixelBuffer = NULL;
    
    err = CVOpenGLESTextureCacheCreateTextureFromImage(kCFAllocatorDefault,
                                                       _textureCache,
                                                       pixelBuffer,
                                                       NULL,
                                                       GL_TEXTURE_2D,
                                                       GL_RGBA,
                                                       _videoFrameW, _videoFrameH,
                                                       GL_BGRA,
                                                       GL_UNSIGNED_BYTE,
                                                       0,
                                                       &srcTexture );
    if ( ! srcTexture || err ) {
        NSLog( @"Error at CVOpenGLESTextureCacheCreateTextureFromImage %d", err );
        goto bail;
    }
    
    err = CVPixelBufferPoolCreatePixelBufferWithAuxAttributes( kCFAllocatorDefault, _bufferPool, _bufferPoolAuxAttributes, &dstPixelBuffer );
    if ( err == kCVReturnWouldExceedAllocationThreshold ) {
        // Flush the texture cache to potentially release the retained buffers and try again to create a pixel buffer
        CVOpenGLESTextureCacheFlush( _renderTextureCache, 0 );
        err = CVPixelBufferPoolCreatePixelBufferWithAuxAttributes( kCFAllocatorDefault, _bufferPool, _bufferPoolAuxAttributes, &dstPixelBuffer );
    }
    if ( err ) {
        if ( err == kCVReturnWouldExceedAllocationThreshold ) {
            NSLog( @"Pool is out of buffers, dropping frame" );
        }
        else {
            NSLog( @"Error at CVPixelBufferPoolCreatePixelBuffer %d", err );
        }
        goto bail;
    }
    
    err = CVOpenGLESTextureCacheCreateTextureFromImage( kCFAllocatorDefault,
                                                       _renderTextureCache,
                                                       dstPixelBuffer,
                                                       NULL,
                                                       GL_TEXTURE_2D,
                                                       GL_RGBA,
                                                       _videoFrameW, _videoFrameH,
                                                       GL_BGRA,
                                                       GL_UNSIGNED_BYTE,
                                                       0,
                                                       &dstTexture );
    
    if ( ! dstTexture || err ) {
        NSLog( @"Error at CVOpenGLESTextureCacheCreateTextureFromImage %d", err );
        goto bail;
    }
    
    glBindFramebuffer( GL_FRAMEBUFFER, _offscreenBufferHandle );
    glViewport( 0, 0, srcDimensions.width, srcDimensions.height );
    glUseProgram( _videoProgram );
    
    // Set up our destination pixel buffer as the framebuffer's render target.
    glActiveTexture( GL_TEXTURE0 );
    glBindTexture( CVOpenGLESTextureGetTarget( dstTexture ), CVOpenGLESTextureGetName( dstTexture ) );
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );
    
    glFramebufferTexture2D( GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, CVOpenGLESTextureGetTarget( dstTexture ), CVOpenGLESTextureGetName( dstTexture ), 0 );
    
    // Render our source pixel buffer.
    glActiveTexture( GL_TEXTURE1 );
    glBindTexture( CVOpenGLESTextureGetTarget( srcTexture ), CVOpenGLESTextureGetName( srcTexture ) );
    glUniform1i(_frame, 1 );
    
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    
    glVertexAttribPointer(ATTRIB_VERTEX, 2, GL_FLOAT, 0, 0, squareVertices);
    glEnableVertexAttribArray( ATTRIB_VERTEX );
    
    glVertexAttribPointer(ATTRIB_TEXTUREPOSITON, 2, GL_FLOAT, 0, 0, textureVertices);
    glEnableVertexAttribArray(ATTRIB_TEXTUREPOSITON );
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    
    glBindTexture( CVOpenGLESTextureGetTarget( srcTexture ), 0 );
    glBindTexture( CVOpenGLESTextureGetTarget( dstTexture ), 0 );
    
    if (draw != nil) {
        draw();
    }
    
//    if (modelArray && drawLandmark){
//        /* 绘制人脸关键点 */
//        glActiveTexture(GL_TEXTURE2);
//        glUseProgram(_faceProgram);
//
//        [self drawFaceWithRect:modelArray.detectRect];
//        for (int i =0; i < modelArray.count; i++) {
//            MGFaceInfo *model = [modelArray modelWithIndex:i];
//            [self drawFacePointer:model.points faceRect:model.rect];
//        }
//
//
//        if (self.show3DView == YES) {
//            if (modelArray.count >= 1) {
//                /* 绘制人脸 3D 图层 */
//                glActiveTexture(GL_TEXTURE3);
//                glUseProgram(_face3DProgram);
//
//                MGFaceInfo *firstInfo = [modelArray modelWithIndex:0];
//                [self drawTriConeX:-firstInfo.pitch Y:-firstInfo.yaw Z:-firstInfo.roll];
//            }
//        }
//    }
    
    // Make sure that outstanding GL commands which render to the destination pixel buffer have been submitted.
    // AVAssetWriter, AVSampleBufferDisplayLayer, and GL will block until the rendering is complete when sourcing from this pixel buffer.
    glFlush();
    
bail:
    if ( oldContext != _oglContext ) {
        [EAGLContext setCurrentContext:oldContext];
    }
    if ( srcTexture ) {
        CFRelease( srcTexture );
    }
    if ( dstTexture ) {
        CFRelease( dstTexture );
    }
    
    return dstPixelBuffer;
}

- (void)drawFaceLandMark:(MGFaceModelArray *)faces {
    if (!faces || faces.count == 0) return;
    
    glActiveTexture(GL_TEXTURE2);
    glUseProgram(_faceProgram);
    
//    [self drawRect:faces.detectRect];
    for (int i =0; i < faces.count; i++) {
        MGFaceInfo *model = [faces modelWithIndex:i];
        [self drawFacePointer:model.points faceRect:model.rect];
    }
    
    
    if (self.show3DView == YES) {
        if (faces.count >= 1) {
            /* 绘制人脸 3D 图层 */
            glActiveTexture(GL_TEXTURE3);
            glUseProgram(_face3DProgram);
            
            MGFaceInfo *firstInfo = [faces modelWithIndex:0];
            [self drawTriConeX:-firstInfo.pitch Y:-firstInfo.yaw Z:-firstInfo.roll];
        }
    }
}

- (CMFormatDescriptionRef)outputFormatDescription
{
	return _outputFormatDescription;
}

- (void)setupFaceProgram
{
    NSString *vertexShaderPath = [[NSBundle mainBundle] pathForResource:@"FacePointSize"
                                                                 ofType:@"glsl"];
    NSString *fragmentShaderPath = [[NSBundle mainBundle] pathForResource:@"FacePointColor"
                                                                   ofType:@"glsl"];
    GLuint programHandle = [GLESUtils loadProgram:vertexShaderPath
                       withFragmentShaderFilepath:fragmentShaderPath];
    if (programHandle == 0) {
        NSLog(@" >> Error: Failed to setup face program.");
        return;
    }
    _faceProgram = programHandle;
    _colorSelectorSlot = glGetUniformLocation(_faceProgram, "sizeScale");
}

- (void)setupFace3Dprogram{
    NSString *vertexShaderPath = [[NSBundle mainBundle] pathForResource:@"Face3DVertex"
                                                                 ofType:@"glsl"];
    NSString *fragmentShaderPath = [[NSBundle mainBundle] pathForResource:@"Face3DFragment"
                                                                   ofType:@"glsl"];
    
    GLuint programHandle = [GLESUtils loadProgram:vertexShaderPath
                 withFragmentShaderFilepath:fragmentShaderPath];
    if (programHandle == 0) {
        NSLog(@" >> Error: Failed to setup 3D program.");
        return;
    }
    _face3DProgram = programHandle;
    _colorSelectorSlot = glGetUniformLocation(_face3DProgram, "color_selector");
}

- (void)setupVideoProgram{
    //Load vertex and fragment shaders
    GLint attribLocation[NUM_ATTRIBUTES] = {
        ATTRIB_VERTEX, ATTRIB_TEXTUREPOSITON,
    };
    GLchar *attribName[NUM_ATTRIBUTES] = {
        "position", "texturecoordinate",
    };
    
    const GLchar *vertSrc = [GLESUtils readFile:@"VideoVert.glsl"];
    const GLchar *fragSrc = [GLESUtils readFile:@"VideoFrag.glsl"];
    
    // shader program
    glueCreateProgram(vertSrc, fragSrc,
                      NUM_ATTRIBUTES, (const GLchar **)&attribName[0], attribLocation,
                      0, 0, 0,
                      &_videoProgram );
    
    if (_videoProgram == 0) {
        NSLog( @"Problem initializing the program." );
    }
}

#pragma mark Internal
- (BOOL)initializeBuffersWithOutputDimensions:(CMVideoDimensions)outputDimensions retainedBufferCountHint:(size_t)clientRetainedBufferCountHint
{
	BOOL success = YES;
	
	EAGLContext *oldContext = [EAGLContext currentContext];
	if ( oldContext != _oglContext ) {
		if ( ! [EAGLContext setCurrentContext:_oglContext] ) {
			@throw [NSException exceptionWithName:NSInternalInconsistencyException reason:@"Problem with OpenGL context" userInfo:nil];
			return NO;
		}
	}
	
	glDisable( GL_DEPTH_TEST );
    
    glGenFramebuffers( 1, &_offscreenBufferHandle );
    glBindFramebuffer( GL_FRAMEBUFFER, _offscreenBufferHandle );
	
    CVReturn err = CVOpenGLESTextureCacheCreate( kCFAllocatorDefault, NULL, _oglContext, NULL, &_textureCache );
    if ( err ) {
        NSLog( @"Error at CVOpenGLESTextureCacheCreate %d", err );
		success = NO;
		goto bail;
    }
	
	err = CVOpenGLESTextureCacheCreate( kCFAllocatorDefault, NULL, _oglContext, NULL, &_renderTextureCache );
    if ( err ) {
        NSLog( @"Error at CVOpenGLESTextureCacheCreate %d", err );
		success = NO;
		goto bail;
    }

    [self setupVideoProgram];
  	_frame = glueGetUniformLocation(_videoProgram, "videoframe");
	
    /*设置人脸标点图层Program*/
    [self setupFaceProgram];
    /* 设置3D图层 */
    [self setupFace3Dprogram];


	size_t maxRetainedBufferCount = clientRetainedBufferCountHint;
	_bufferPool = createPixelBufferPool(outputDimensions.width, outputDimensions.height, kCVPixelFormatType_32BGRA, (int32_t)maxRetainedBufferCount );
	if (! _bufferPool) {
		NSLog( @"Problem initializing a buffer pool." );
		success = NO;
		goto bail;
	}
	
	_bufferPoolAuxAttributes = createPixelBufferPoolAuxAttributes((int32_t)maxRetainedBufferCount);
	preallocatePixelBuffersInPool(_bufferPool, _bufferPoolAuxAttributes);
	
	CMFormatDescriptionRef outputFormatDescription = NULL;
	CVPixelBufferRef testPixelBuffer = NULL;
	CVPixelBufferPoolCreatePixelBufferWithAuxAttributes(kCFAllocatorDefault, _bufferPool, _bufferPoolAuxAttributes, &testPixelBuffer );
	if ( ! testPixelBuffer ) {
		NSLog( @"Problem creating a pixel buffer." );
		success = NO;
		goto bail;
	}
	CMVideoFormatDescriptionCreateForImageBuffer( kCFAllocatorDefault, testPixelBuffer, &outputFormatDescription );
	_outputFormatDescription = outputFormatDescription;
	CFRelease( testPixelBuffer );
	
bail:
	if ( ! success ) {
		[self deleteBuffers];
	}
	if ( oldContext != _oglContext ) {
		[EAGLContext setCurrentContext:oldContext];
	}
    return success;
}

static CVPixelBufferPoolRef createPixelBufferPool(int32_t width, int32_t height, FourCharCode pixelFormat, int32_t maxBufferCount )
{
	CVPixelBufferPoolRef outputPool = NULL;
	
	NSDictionary *sourcePixelBufferOptions = @{(id)kCVPixelBufferPixelFormatTypeKey : @(pixelFormat),
												(id)kCVPixelBufferWidthKey : @(width),
												(id)kCVPixelBufferHeightKey : @(height),
												(id)kCVPixelFormatOpenGLESCompatibility : @(YES),
												(id)kCVPixelBufferIOSurfacePropertiesKey : @{ /*empty dictionary*/ } };
	
	NSDictionary *pixelBufferPoolOptions = @{ (id)kCVPixelBufferPoolMinimumBufferCountKey : @(maxBufferCount) };
	
    CVPixelBufferPoolCreate(kCFAllocatorDefault, (__bridge CFDictionaryRef)pixelBufferPoolOptions, (__bridge CFDictionaryRef)sourcePixelBufferOptions, &outputPool );
    
	return outputPool;
}

static CFDictionaryRef createPixelBufferPoolAuxAttributes(int32_t maxBufferCount)
{
	// CVPixelBufferPoolCreatePixelBufferWithAuxAttributes() will return kCVReturnWouldExceedAllocationThreshold if we have already vended the max number of buffers
	return CFRetain((__bridge CFTypeRef)(@{(id)kCVPixelBufferPoolAllocationThresholdKey : @(maxBufferCount)}));
}

static void preallocatePixelBuffersInPool( CVPixelBufferPoolRef pool, CFDictionaryRef auxAttributes )
{
	// Preallocate buffers in the pool, since this is for real-time display/capture
	NSMutableArray *pixelBuffers = [[NSMutableArray alloc] init];
	while ( 1 )
	{
		CVPixelBufferRef pixelBuffer = NULL;
		OSStatus err = CVPixelBufferPoolCreatePixelBufferWithAuxAttributes( kCFAllocatorDefault, pool, auxAttributes, &pixelBuffer );
		
		if ( err == kCVReturnWouldExceedAllocationThreshold ) {
			break;
		}
		assert( err == noErr );
		
		[pixelBuffers addObject:(__bridge id)(pixelBuffer)];
		CFRelease( pixelBuffer );
	}
}


#pragma mark - 绘制矩形
- (void)drawRect:(CGRect )rect {
    if (CGRectIsNull(rect))  return;
    
    GLfloat lineWidth = _videoFrameH/480.0 * 3.0;
    glLineWidth(lineWidth);
    
    GLfloat top = (rect.origin.y - _videoFrameH/2) / (_videoFrameH/2);
    GLfloat left = (_videoFrameW/2 - rect.origin.x) / (_videoFrameW/2);
    GLfloat right = (_videoFrameW/2 - (rect.origin.x+rect.size.width)) / (_videoFrameW/2);
    GLfloat bottom = ((rect.origin.y + rect.size.height) - _videoFrameH/2) / (_videoFrameH/2);
    
    GLfloat tempFace[]= {
        right, top, 0.0f, // right  top
        left, top, 0.0f, // left  top
        left,  bottom, 0.0f, // left bottom
        right,  bottom, 0.0f, // right Bottom
    };
    GLubyte indices[] = {
        0, 1, 1, 2, 2, 3, 3, 0
    };
    
    glVertexAttribPointer( 0, 3, GL_FLOAT, GL_FALSE, 0, tempFace );
    glEnableVertexAttribArray(0 );
    glDrawElements(GL_LINES, sizeof(indices)/sizeof(GLubyte), GL_UNSIGNED_BYTE, indices);
}

#pragma mark - 绘制关键点
- (void)drawFaceWithRect:(CGRect)rect {
    if (CGRectIsNull(rect))  return;
    
    GLfloat lineWidth = _videoFrameH/480.0 * 3.0;
    glLineWidth(lineWidth);
    
    GLfloat top = [self changeToGLPointT:rect.origin.y];
    GLfloat left = [self changeToGLPointL:rect.origin.x];
    GLfloat right = [self changeToGLPointR:rect.size.width];
    GLfloat bottom = [self changeToGLPointB:rect.size.height];

    GLfloat tempFace[]= {
        bottom,left,0.0f,
        top, left, 0.0f,
        top, right, 0.0f,
        bottom,right,0.0f,
    };
    GLubyte indices[] = {
        0, 1, 1, 2, 2, 3, 3, 0
    };
    
    glVertexAttribPointer( 0, 3, GL_FLOAT, GL_FALSE, 0, tempFace );
    glEnableVertexAttribArray(0 );
    glDrawElements(GL_LINES, sizeof(indices)/sizeof(GLubyte), GL_UNSIGNED_BYTE, indices);
}

- (void)drawFacePointer:(NSArray *)pointArray faceRect:(CGRect)rect{
    
//    GLfloat lineWidth = _videoFrameH/480.0;
    const GLfloat lineWidth = rect.size.width/WIN_WIDTH * 1.5;

    glUniform1f(_facePointSize, lineWidth);
    
    const GLsizei pointCount = (GLsizei)pointArray.count;
    GLfloat tempPoint[pointCount * 3];
    GLubyte indices[pointCount];
    
    for (int i = 0; i < pointArray.count; i ++) {
        CGPoint pointer = [pointArray[i] CGPointValue];
        
        GLfloat top = [self changeToGLPointT:pointer.x];
        GLfloat left = [self changeToGLPointL:pointer.y];
        
        tempPoint[i*3+0]=top;
        tempPoint[i*3+1]=left;
        tempPoint[i*3+2]=0.0f;
        
        indices[i]=i;
    }
    
    glVertexAttribPointer( 0, 3, GL_FLOAT, GL_TRUE, 0, tempPoint );
    glEnableVertexAttribArray(GL_VERTEX_ATTRIB_ARRAY_POINTER);
    glDrawElements(GL_POINTS, (GLsizei)sizeof(indices)/sizeof(GLubyte), GL_UNSIGNED_BYTE, indices);
}

- (GLfloat)changeToGLPointT:(CGFloat)x{
    GLfloat tempX = (x - _videoFrameW/2) / (_videoFrameW/2);
    
    return tempX;
}
- (GLfloat)changeToGLPointL:(CGFloat)y{
    GLfloat tempY = (_videoFrameH/2 - (_videoFrameH - y)) / (_videoFrameH/2);
    return tempY;
}
- (GLfloat)changeToGLPointR:(CGFloat)y{
    GLfloat tempR = (_videoFrameH/2 - y) / (_videoFrameH/2);
    return tempR;
}
- (GLfloat)changeToGLPointB:(CGFloat)y{
    GLfloat tempB = (y - _videoFrameW/2) / (_videoFrameW/2);
    return tempB;
}


static void rotatePoint3f(float *points, int offset, float angle/*radis*/, int x_axis, int y_axis) {
    float x = points[offset + x_axis], y = points[offset + y_axis];
    float alpha_x = cosf(angle), alpha_y = sinf(angle);
    
    points[offset + x_axis] = x * alpha_x - y * alpha_y;
    points[offset + y_axis] = x * alpha_y + y * alpha_x;
}

- (void)drawTriConeX:(float)pitch Y:(float)yaw Z:(float)roll {
    
    GLfloat lineWidth = _videoFrameH/480.0 * 2.0;
    glLineWidth(lineWidth);
    
    GLfloat vertices[] = {
        0.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
        0.0f, 0.0f, -1.0f
    };
    int n = sizeof(vertices) / sizeof(GLfloat) / 3;
    
    float a = 0.2;
    GLfloat resize = _videoFrameW / _videoFrameH;
    for (int i = 0; i < n; ++i) {
        rotatePoint3f(vertices, i * 3, yaw, 2, 0);
        rotatePoint3f(vertices, i * 3, pitch, 2, 1);
        rotatePoint3f(vertices, i * 3, roll, 0, 1);
        
        vertices[i * 3 + 0] = vertices[i * 3 + 0] * a * 1 + 0.8f;
        vertices[i * 3 + 1] = vertices[i * 3 + 1] * a * resize + 0;
        vertices[i * 3 + 2] = vertices[i * 3 + 2] * a * 1 + 0;
    }
    
    GLubyte indices[] = {0, 1, 0, 2, 0, 3};
    
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_TRUE, 0, vertices );
    glEnableVertexAttribArray(GL_VERTEX_ATTRIB_ARRAY_POINTER);
    
    for (int i = 0; i < 3; ++i) {
        glUniform1f(_colorSelectorSlot, (float)(i + 1));
        glDrawElements(GL_LINES, 2, GL_UNSIGNED_BYTE, indices + i * 2);
    }
}

- (void)setUpOutSampleBuffer:(CGSize)outSize devicePosition:(AVCaptureDevicePosition)devicePosition{
    [EAGLContext setCurrentContext:_oglContext];
    
    CMVideoDimensions dimensions;
    dimensions.width = outSize.width;
    dimensions.height = outSize.height;
    
    [self deleteBuffers];
    if ( ! [self initializeBuffersWithOutputDimensions:dimensions retainedBufferCountHint:6] ) {
        @throw [NSException exceptionWithName:NSInternalInconsistencyException reason:@"Problem preparing renderer." userInfo:nil];
    }
}





@end

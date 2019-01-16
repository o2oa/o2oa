
 /*
     File: RosyWriterOpenGLRenderer.h
 Abstract: The RosyWriter OpenGL effect renderer
  Version: 2.1
 
 Disclaimer: IMPORTANT:  This Apple software is supplied to you by Apple
 Inc. ("Apple") in consideration of your agreement to the following
 terms, and your use, installation, modification or redistribution of
 this Apple software constitutes acceptance of these terms.  If you do
 not agree with these terms, please do not use, install, modify or
 redistribute this Apple software.
 
 In consideration of your agreement to abide by the following terms, and
 subject to these terms, Apple grants you a personal, non-exclusive
 license, under Apple's copyrights in this original Apple software (the
 "Apple Software"), to use, reproduce, modify and redistribute the Apple
 Software, with or without modifications, in source and/or binary forms;
 provided that if you redistribute the Apple Software in its entirety and
 without modifications, you must retain this notice and the following
 text and disclaimers in all such redistributions of the Apple Software.
 Neither the name, trademarks, service marks or logos of Apple Inc. may
 be used to endorse or promote products derived from the Apple Software
 without specific prior written permission from Apple.  Except as
 expressly stated in this notice, no other rights or licenses, express or
 implied, are granted by Apple herein, including but not limited to any
 patent rights that may be infringed by your derivative works or by other
 works in which the Apple Software may be incorporated.
 
 The Apple Software is provided by Apple on an "AS IS" basis.  APPLE
 MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 FOR A PARTICULAR PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND
 OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 
 IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 MODIFICATION AND/OR DISTRIBUTION OF THE APPLE SOFTWARE, HOWEVER CAUSED
 AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 
 Copyright (C) 2014 Apple Inc. All Rights Reserved.
 
 */

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>
#import "MGOpenGLConfig.h"

@class MGFaceModelArray;



@interface MGOpenGLRenderer : NSObject

/* Format/Processing Requirements */
@property(nonatomic, readonly) BOOL operatesInPlace; // When YES the input pixel buffer is written to by the renderer instead of writing the result to a new pixel buffer.
@property(nonatomic, readonly) FourCharCode inputPixelFormat; // One of 420f, 420v, or BGRA

/* Resource Lifecycle */
// Prepare and destroy expensive resources inside these callbacks.
// The outputRetainedBufferCountHint tells out of place renderers how many of their output buffers will be held onto by the downstream pipeline at one time.
// This can be used by the renderer to size and preallocate their pools.
- (void)prepareForInputWithFormatDescription:(CMFormatDescriptionRef)inputFormatDescription
               outputRetainedBufferCountHint:(size_t)outputRetainedBufferCountHint;

- (void)reset;

/* Rendering */
// Renderers which operate in place should return the input pixel buffer with a +1 retain count.
// Renderers which operate out of place should create a pixel buffer to return from a pool they own.
// When rendering to a pixel buffer with the GPU it is not necessary to block until rendering has completed before returning.
// It is sufficient to call glFlush() to ensure that the commands have been flushed to the GPU.
//- (CVPixelBufferRef )copyRenderedPixelBuffer:(CMSampleBufferRef)sampleBufferRef faceModelArray:(MGFaceModelArray *)modelArray drawLandmark:(BOOL)drawLandmark;

@property(nonatomic, assign) BOOL show3DView;

// This property must be implemented if operatesInPlace is NO and the output pixel buffers have a different format description than the input.
// If implemented a non-NULL value must be returned once the renderer has been prepared (can be NULL after being reset).
@property(nonatomic, readonly) CMFormatDescriptionRef __attribute__((NSObject)) outputFormatDescription;

- (void)setUpOutSampleBuffer:(CGSize)outSize devicePosition:(AVCaptureDevicePosition)devicePosition;


- (CVPixelBufferRef )drawPixelBuffer:(CMSampleBufferRef)sampleBufferRef custumDrawing:(void (^)(void))draw;

- (void)drawFaceLandMark:(MGFaceModelArray *)faces;

- (void)drawRect:(CGRect)rect;

- (void)drawFaceWithRect:(CGRect)rect;

@end

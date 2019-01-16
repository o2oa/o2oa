//
//  MGVideoManager.m
//  MGLivenessDetection
//
//  Created by 张英堂 on 16/3/31.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import "MGVideoManager.h"


//屏幕宽度 （区别于viewcontroller.view.fream）
#define MG_WIN_WIDTH  [UIScreen mainScreen].bounds.size.width
//屏幕高度 （区别于viewcontroller.view.fream）
#define MG_WIN_HEIGHT [UIScreen mainScreen].bounds.size.height

@interface MGVideoManager ()<AVCaptureVideoDataOutputSampleBufferDelegate, AVCaptureAudioDataOutputSampleBufferDelegate>
{
    AVCaptureConnection *_audioConnection;
    AVCaptureConnection *_videoConnection;
    NSDictionary *_audioCompressionSettings;
    AVCaptureDevice *_videoDevice;
    dispatch_queue_t _videoQueue;
}
@property(nonatomic, assign) CMFormatDescriptionRef outputAudioFormatDescription;
@property(nonatomic, assign) CMFormatDescriptionRef outputVideoFormatDescription;

@property (nonatomic, strong) AVCaptureVideoPreviewLayer *videoPreviewLayer;
@property(nonatomic, copy) NSString *sessionPreset;
@property(nonatomic, copy) NSString *tempVideoPath;


@property (nonatomic, assign) BOOL videoRecord;
@property (nonatomic, assign) BOOL videoSound;
@property (nonatomic, assign) BOOL startRecord;

@end

@implementation MGVideoManager

-(void)dealloc{
    _audioConnection = nil;
    _videoConnection = nil;
    self.videoDelegate = nil;
    self.sessionPreset = nil;
}

-(instancetype)initWithPreset:(NSString *)sessionPreset
               devicePosition:(AVCaptureDevicePosition)devicePosition
                  videoRecord:(BOOL)record
                   videoSound:(BOOL)sound{
    self = [super init];
    if (self) {
        self.sessionPreset = sessionPreset;
        _devicePosition = devicePosition;
        self.videoRecord = record;
        self.videoSound = sound;
        
        _startRecord = NO;
        _videoQueue = dispatch_queue_create("com.megvii.face.video", NULL);
    }
    return self;
}

+ (instancetype)videoPreset:(NSString *)sessionPreset
             devicePosition:(AVCaptureDevicePosition)devicePosition
                videoRecord:(BOOL)record
                 videoSound:(BOOL)sound{
    
    MGVideoManager *manager = [[MGVideoManager alloc] initWithPreset:sessionPreset
                                                      devicePosition:devicePosition
                                                         videoRecord:record
                                                          videoSound:sound];
    return manager;
}

#pragma mark - video 功能开关
- (void)stopRunning{
    if (self.videoSession) {
        [self.videoSession stopRunning];
    }
}

- (void)startRunning{
    [self initialSession];
    
    if (self.videoSession) {
        [self.videoSession startRunning];
    }
}
- (void)startRecording{
    [self startRunning];
    
    if (!self.videoRecord) {
        return;
    }
    _startRecord = YES;
}

- (NSString *)stopRceording{
    _startRecord = NO;
    
    
    NSString *tempString = @"no video!";
    return tempString;
}

#pragma mark - 初始化video配置
- (NSString *)sessionPreset{
    if (nil == _sessionPreset) {
        _sessionPreset = AVCaptureSessionPreset640x480;
    }
    return _sessionPreset;
}

-(AVCaptureVideoPreviewLayer *)videoPreviewLayer{
    if (nil == _videoPreviewLayer) {
        _videoPreviewLayer = [[AVCaptureVideoPreviewLayer alloc] initWithSession:self.videoSession];
        [_videoPreviewLayer setFrame:CGRectMake(0, 0, MG_WIN_WIDTH, MG_WIN_HEIGHT)];
        [_videoPreviewLayer setVideoGravity:AVLayerVideoGravityResizeAspectFill];
    }
    return _videoPreviewLayer;
}

-(AVCaptureVideoPreviewLayer *)videoPreview{
    return self.videoPreviewLayer;
}
-(BOOL)videoSound{
    if (_videoRecord && _videoSound) {
        return YES;
    }
    return NO;
}

- (CMFormatDescriptionRef)formatDescription{
    return self.outputVideoFormatDescription;
}

- (dispatch_queue_t)getVideoQueue{
    return _videoQueue;
}

//初始化相机
- (void) initialSession
{
    if (self.videoSession == nil) {
        
        /* session */
        _videoSession = [[AVCaptureSession alloc] init];
        
        /* 摄像头 */
        _videoDevice = [self cameraWithPosition:self.devicePosition];
        [self setMaxVideoFrame:60 videoDevice:_videoDevice];
        
        /* input */
        NSError *DeviceError;
        _videoInput = [[AVCaptureDeviceInput alloc] initWithDevice:_videoDevice error:&DeviceError];
        if (DeviceError) {
            [self videoError:DeviceError];
            return;
        }
        if ([self.videoSession canAddInput:self.videoInput]) {
            [self.videoSession addInput:self.videoInput];
        }
        
        /* output */
        AVCaptureVideoDataOutput *output = [[AVCaptureVideoDataOutput alloc] init];
        [output setSampleBufferDelegate:self queue:_videoQueue];
        output.videoSettings = @{(id)kCVPixelBufferPixelFormatTypeKey:@(kCVPixelFormatType_32BGRA)};
        output.alwaysDiscardsLateVideoFrames = NO;
        
        if ([self.videoSession canAddOutput:output]) {
            [self.videoSession addOutput:output];
        }
        
        /* sessionPreset */
        // 判断最佳分辨率
        if ([self.videoSession canSetSessionPreset:AVCaptureSessionPreset1920x1080]) {
            [self.videoSession setSessionPreset: AVCaptureSessionPreset1920x1080];
            NSLog(@"分辨率 1920*1080");
        }else if ([self.videoSession canSetSessionPreset:AVCaptureSessionPreset1280x720]) {
            [self.videoSession setSessionPreset: AVCaptureSessionPreset1280x720];
            NSLog(@"分辨率 1280*720");
        }else if ([self.videoSession canSetSessionPreset:AVCaptureSessionPresetiFrame960x540]) {
            [self.videoSession setSessionPreset: AVCaptureSessionPresetiFrame960x540];
            NSLog(@"分辨率 960*540");
        }else {
            [self.videoSession setSessionPreset: AVCaptureSessionPreset640x480];
            NSLog(@"分辨率 640*480");
        }
//        if ([self.videoSession canSetSessionPreset:self.sessionPreset]) {
//            [self.videoSession setSessionPreset: self.sessionPreset];
//        }else{
//            NSError *presetError = [NSError errorWithDomain:NSCocoaErrorDomain code:101 userInfo:@{@"sessionPreset":@"不支持的sessionPreset!"}];
//            [self videoError:presetError];
//            return;
//        }
        
        _videoConnection = [output connectionWithMediaType:AVMediaTypeVideo];
//        [_videoConnection setVideoOrientation:AVCaptureVideoOrientationPortrait];
        self.videoOrientation = _videoConnection.videoOrientation;
        
        /* 设置声音 */
        if (self.videoSound) {
            AVCaptureDevice *audioDevice = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeAudio];
            AVCaptureDeviceInput *audioIn = [[AVCaptureDeviceInput alloc] initWithDevice:audioDevice error:nil];
            if ( [self.videoSession canAddInput:audioIn] ) {
                [self.videoSession addInput:audioIn];
            }
            
            AVCaptureAudioDataOutput *audioOut = [[AVCaptureAudioDataOutput alloc] init];
            dispatch_queue_t audioCaptureQueue = dispatch_queue_create("com.megvii.audio", DISPATCH_QUEUE_SERIAL );
            [audioOut setSampleBufferDelegate:self queue:audioCaptureQueue];
            
            if ( [self.videoSession canAddOutput:audioOut] ) {
                [self.videoSession addOutput:audioOut];
            }
            _audioConnection = [audioOut connectionWithMediaType:AVMediaTypeAudio];
            output.alwaysDiscardsLateVideoFrames = YES;
            
            _audioCompressionSettings = [[audioOut recommendedAudioSettingsForAssetWriterWithOutputFileType:AVFileTypeQuickTimeMovie] copy];
        }
    }
}

- (void)initVideoRecord:(CMFormatDescriptionRef)formatDescription{
    
}

//前后摄像头
- (AVCaptureDevice *)cameraWithPosition:(AVCaptureDevicePosition) position {
    NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    for (AVCaptureDevice *device in devices) {
        if ([device position] == position) {
            return device;
        }
    }
    return nil;
}
//前后摄像头的切换
- (void)toggleCamera:(id)sender{
    NSUInteger cameraCount = [[AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo] count];
    if (cameraCount > 1) {
        NSError *error;
        AVCaptureDeviceInput *newVideoInput;
        AVCaptureDevicePosition position = [[_videoInput device] position];
        
        if (position == AVCaptureDevicePositionBack)
            newVideoInput = [[AVCaptureDeviceInput alloc] initWithDevice:[self cameraWithPosition:AVCaptureDevicePositionFront] error:&error];
        else if (position == AVCaptureDevicePositionFront)
            newVideoInput = [[AVCaptureDeviceInput alloc] initWithDevice:[self cameraWithPosition:AVCaptureDevicePositionBack] error:&error];
        else
            return;
        
        if (newVideoInput != nil) {
            [self.videoSession beginConfiguration];
            [self.videoSession removeInput:self.videoInput];
            if ([self.videoSession canAddInput:newVideoInput]) {
                [self.videoSession addInput:newVideoInput];
                _videoInput = newVideoInput;
            } else {
                [self.videoSession addInput:self.videoInput];
            }
            [self.videoSession commitConfiguration];
        } else if (error) {
            [self videoError:error];
        }
    }
}
// 设置 视频最大帧率
- (void)setMaxVideoFrame:(NSInteger)frame videoDevice:(AVCaptureDevice *)videoDevice{
    for(AVCaptureDeviceFormat *vFormat in [videoDevice formats])
    {
        CMFormatDescriptionRef description= vFormat.formatDescription;
        AVFrameRateRange *rateRange = (AVFrameRateRange*)[vFormat.videoSupportedFrameRateRanges objectAtIndex:0];
        float maxrate = rateRange.maxFrameRate;
        
        if(maxrate >= frame && CMFormatDescriptionGetMediaSubType(description)==kCVPixelFormatType_420YpCbCr8BiPlanarFullRange)
        {
            if (YES == [videoDevice lockForConfiguration:NULL])
            {
                videoDevice.activeFormat = vFormat;
                [videoDevice setActiveVideoMinFrameDuration:CMTimeMake(1,(int)(frame/3))];
                [videoDevice setActiveVideoMaxFrameDuration:CMTimeMake(1,(int)frame)];
                [videoDevice unlockForConfiguration];
            }
        }
    }
}

//录像功能
- (void)appendVideoBuffer:(CMSampleBufferRef)pixelBuffer
{
    
}

- (void)appendAudioBuffer:(CMSampleBufferRef)sampleBuffer{
    
}

- (CGAffineTransform)transformFromVideoBufferOrientationToOrientation:(AVCaptureVideoOrientation)orientation withAutoMirroring:(BOOL)mirror
{
    CGAffineTransform transform = CGAffineTransformIdentity;
    
    CGFloat orientationAngleOffset = [MGImage angleOffsetFromPortraitOrientationToOrientation:orientation];
    CGFloat videoOrientationAngleOffset = [MGImage angleOffsetFromPortraitOrientationToOrientation:self.videoOrientation];
    
    CGFloat angleOffset = orientationAngleOffset - videoOrientationAngleOffset;
    transform = CGAffineTransformMakeRotation(angleOffset);
    //    transform = CGAffineTransformRotate(transform, -M_PI);
    
    if ( _videoDevice.position == AVCaptureDevicePositionFront)
    {
        if (mirror) {
            transform = CGAffineTransformScale(transform, -1, 1);
        }else {
            transform = CGAffineTransformRotate(transform, M_PI );
        }
    }
    
    return transform;
}

#pragma mark - delegate
- (void)captureOutput:(AVCaptureOutput *)captureOutput
didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer
       fromConnection:(AVCaptureConnection *)connection
{
    @autoreleasepool {
        if (connection == _videoConnection)
        {
            CMFormatDescriptionRef formatDescription = CMSampleBufferGetFormatDescription(sampleBuffer);
            
            if (self.outputVideoFormatDescription == nil) {
                self.outputVideoFormatDescription = formatDescription;
            }
            if (self.videoDelegate) {
                [self.videoDelegate MGCaptureOutput:captureOutput didOutputSampleBuffer:sampleBuffer fromConnection:connection];
            }
            
            if (self.videoRecord && _startRecord) {
                [self appendVideoBuffer:sampleBuffer];
            }
        }else if (connection == _audioConnection){
            [self appendAudioBuffer:sampleBuffer];
        }
    }
}


#pragma mark - 视频流出错，抛出异常
- (void)videoError:(NSError *)error{
    if (self.videoDelegate && error) {
        [self.videoDelegate MGCaptureOutput:nil error:error];
    }
}

@end

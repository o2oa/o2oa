//
//  MGVideoManager.h
//  MGLivenessDetection
//
//  Created by 张英堂 on 16/3/31.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MGImage.h"
#import "MGBaseDefine.h"

@protocol MGVideoDelegate;

@interface MGVideoManager : NSObject


@property (nonatomic, assign) id<MGVideoDelegate> videoDelegate;

- (dispatch_queue_t)getVideoQueue;

/**
 *  初始化方法
 *
 *  @param sessionPreset 相机分辨率 如果设置为空，则默认为 AVCaptureSessionPreset640x480
 *  @param devicePosition 前置或者后置相机，则默认为 前置相机
 *  @param record 是否录像
 *  @param sound  是否录音，必须在录像模式下设置 yes 才有用
 *  @return 实例化对象
 */
+ (instancetype)videoPreset:(NSString *)sessionPreset
             devicePosition:(AVCaptureDevicePosition)devicePosition
                videoRecord:(BOOL)record
                 videoSound:(BOOL)sound;

@property (nonatomic, strong, readonly) AVCaptureSession *videoSession;
@property (nonatomic, strong, readonly) AVCaptureDeviceInput *videoInput;
@property (nonatomic, assign, readonly) AVCaptureDevicePosition devicePosition;


///**
// *  视频流的最大帧率
// */
//@property (nonatomic, assign) int maxFrame;


/**
 *  视频流的预览layer 默认全屏大小
 *  @return 实例化对象
 */
-(AVCaptureVideoPreviewLayer *)videoPreview;

/**
 *  视频流的方向
 */
@property(nonatomic, assign) AVCaptureVideoOrientation videoOrientation;

/**
 *  开启视频流
 */
- (void)startRunning;

/**
 *  关闭视频流
 */
- (void)stopRunning;

/**
 *  开始录像
 */
- (void)startRecording;

/**
 *  关闭录像
 *
 *  @return 录像存放地址
 */
- (NSString *)stopRceording;


- (CMFormatDescriptionRef)formatDescription;

/** only valid after startRunning has been called */
- (CGAffineTransform)transformFromVideoBufferOrientationToOrientation:(AVCaptureVideoOrientation)orientation
                                                    withAutoMirroring:(BOOL)mirroring;



@end


@protocol MGVideoDelegate <NSObject>

@required
- (void)MGCaptureOutput:(AVCaptureOutput *)captureOutput
  didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer
         fromConnection:(AVCaptureConnection *)connection;

- (void)MGCaptureOutput:(AVCaptureOutput *)captureOutput error:(NSError *)error;



@end


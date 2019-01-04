//
//  MGImage.m
//  MGBankCard
//
//  Created by 张英堂 on 15/12/11.
//  Copyright © 2015年 megvii. All rights reserved.
//

#import "MGImage.h"
#import <CoreGraphics/CoreGraphics.h>

@implementation MGImage

+ (UIImage*)fixOrientationFromSampleBuffer:(CMSampleBufferRef)sampleBuffer
                               orientation:(UIImageOrientation)orientation{
    UIImage *image2 = [self imageFromSampleBuffer:sampleBuffer orientation:orientation];
    return [self fixOrientationWithImage:image2];
}

+ (UIImage*)imageFromSampleBuffer:(CMSampleBufferRef)sampleBuffer
                      orientation:(UIImageOrientation) orientation{
    CVImageBufferRef imageBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
    CVPixelBufferLockBaseAddress(imageBuffer, 0);
    
    void *baseAddress = CVPixelBufferGetBaseAddress(imageBuffer);
    
    size_t bytesPerRow = CVPixelBufferGetBytesPerRow(imageBuffer);
    size_t width = CVPixelBufferGetWidth(imageBuffer);
    size_t height = CVPixelBufferGetHeight(imageBuffer);
    
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = CGBitmapContextCreate(baseAddress,
                                                 width,
                                                 height,
                                                 8,
                                                 bytesPerRow,
                                                 colorSpace,
                                                 kCGBitmapByteOrder32Little | kCGImageAlphaPremultipliedFirst);
    CGImageRef quartzImage = CGBitmapContextCreateImage(context);
    CVPixelBufferUnlockBaseAddress(imageBuffer,0);
    
    UIImage *image = [UIImage imageWithCGImage:quartzImage scale:1.0 orientation:orientation];
    
    CGImageRelease(quartzImage);
    CGContextRelease(context);
    CGColorSpaceRelease(colorSpace);
    
    return image;
}

+ (UIImage*)croppedImage:(UIImage *)image
                    rect:(CGRect)bounds{
    CGImageRef imageRef = CGImageCreateWithImageInRect([image CGImage], bounds);
    UIImage *croppedImage = [UIImage imageWithCGImage:imageRef];
    CGImageRelease(imageRef);
    return croppedImage;
}

UIImage* MGFixOrientationWithImageOrientation(UIImage *image, UIImageOrientation orientation){
    CGAffineTransform transform = CGAffineTransformIdentity;

    CGFloat height = 0, width = 0;
    
    switch (orientation) {
        case UIImageOrientationRight:
        {
            transform = CGAffineTransformRotate(transform, -M_PI_2);
            transform = CGAffineTransformTranslate(transform, -image.size.width, 0);

            width = image.size.height;
            height = image.size.width;
        }
            break;
        default:
        {
            width = image.size.width;
            height = image.size.height;
        }
            break;
    }
    
    CGContextRef ctx = CGBitmapContextCreate(NULL,
                                             width,
                                             height,
                                             CGImageGetBitsPerComponent(image.CGImage),
                                             0,
                                             CGImageGetColorSpace(image.CGImage),
                                             CGImageGetBitmapInfo(image.CGImage));
    CGContextConcatCTM(ctx, transform);
    
    switch (image.imageOrientation) {
        case UIImageOrientationRight:
        {
            CGContextDrawImage(ctx, CGRectMake(0,0,image.size.height,image.size.width), image.CGImage);
        }
            break;
        case UIImageOrientationRightMirrored:
        {
            CGContextDrawImage(ctx, CGRectMake(0,0,image.size.height,image.size.width), image.CGImage);
        }
            break;
        default:
        {
            CGContextDrawImage(ctx, CGRectMake(0,0,image.size.width,image.size.height), image.CGImage);
        }
            break;
    }
    
    CGImageRef cgimg = CGBitmapContextCreateImage(ctx);
    UIImage *resultImage = [UIImage imageWithCGImage:cgimg];
    CGContextRelease(ctx);
    CGImageRelease(cgimg);
    
    return resultImage;
}


+ (UIImage*)fixOrientationWithImage:(UIImage *)image{
    if (image.imageOrientation == UIImageOrientationUp) return image;
    
    CGAffineTransform transform = CGAffineTransformIdentity;
    
    switch (image.imageOrientation) {
        case UIImageOrientationDown:
        case UIImageOrientationDownMirrored:
            transform = CGAffineTransformTranslate(transform, image.size.width, image.size.height);
            transform = CGAffineTransformRotate(transform, M_PI);
            break;
            
        case UIImageOrientationLeft:
        case UIImageOrientationLeftMirrored:
            transform = CGAffineTransformTranslate(transform, image.size.width, 0);
            transform = CGAffineTransformRotate(transform, M_PI_2);
            break;
            
        case UIImageOrientationRight:
        case UIImageOrientationRightMirrored:
            transform = CGAffineTransformTranslate(transform, 0, image.size.height);
            transform = CGAffineTransformRotate(transform, -M_PI_2);
            break;
        case UIImageOrientationUp:
        case UIImageOrientationUpMirrored:
            break;
    }
    
    switch (image.imageOrientation) {
        case UIImageOrientationUpMirrored:
        case UIImageOrientationDownMirrored:
            transform = CGAffineTransformTranslate(transform, image.size.width, 0);
            transform = CGAffineTransformScale(transform, -1, 1);
            break;
            
        case UIImageOrientationLeftMirrored:
        case UIImageOrientationRightMirrored:
            transform = CGAffineTransformTranslate(transform, image.size.height, 0);
            transform = CGAffineTransformScale(transform, -1, 1);
            break;
        case UIImageOrientationUp:
        case UIImageOrientationDown:
        case UIImageOrientationLeft:
        case UIImageOrientationRight:
            break;
    }
    
    CGContextRef ctx = CGBitmapContextCreate(NULL,
                                             image.size.width,
                                             image.size.height,
                                             CGImageGetBitsPerComponent(image.CGImage),
                                             0,
                                             CGImageGetColorSpace(image.CGImage),
                                             CGImageGetBitmapInfo(image.CGImage));
    CGContextConcatCTM(ctx, transform);
    switch (image.imageOrientation) {
        case UIImageOrientationLeft:
        case UIImageOrientationLeftMirrored:
        case UIImageOrientationRight:
        case UIImageOrientationRightMirrored:
            CGContextDrawImage(ctx, CGRectMake(0,0,image.size.height,image.size.width), image.CGImage);
            break;
            
        default:
            CGContextDrawImage(ctx, CGRectMake(0,0,image.size.width,image.size.height), image.CGImage);
            break;
    }
    
    CGImageRef cgimg = CGBitmapContextCreateImage(ctx);
    UIImage *img = [UIImage imageWithCGImage:cgimg];
    CGContextRelease(ctx);
    CGImageRelease(cgimg);
    
    return img;
}

+ (CGFloat)angleOffsetFromPortraitOrientationToOrientation:(AVCaptureVideoOrientation)orientation
{
    CGFloat angle = 0.0;
    switch ( orientation )
    {
        case AVCaptureVideoOrientationPortrait:
            angle = 0.0;
            break;
        case AVCaptureVideoOrientationPortraitUpsideDown:
            angle = M_PI;
            break;
        case AVCaptureVideoOrientationLandscapeRight:
            angle = -M_PI_2;
            break;
        case AVCaptureVideoOrientationLandscapeLeft:
            angle = M_PI_2;
            break;
        default:
            break;
    }
    return angle;
}

@end

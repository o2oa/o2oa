//
//  QuickUIUtil.swift
//  CommonUtil
//
//  Created by lijunjie on 15/11/14.
//  Copyright © 2015年 lijunjie. All rights reserved.
//

import Foundation

public class QuickUIUtil {
    
    static let share = QuickUIUtil()
    
    private init () {}
    
    public func colorFromRed(red: CGFloat, green: CGFloat, blue: CGFloat) -> UIColor {
        return self.colorFromRed(red: red, green: green, blue: blue, alpha: 1.0)
    }
    
    public func colorFromRed(red: CGFloat, green: CGFloat, blue: CGFloat, alpha: CGFloat) -> UIColor {
        return UIColor(red: red/255.0, green: green/255.0, blue: blue/255.0, alpha: alpha)
    }
    
    public func colorFromHexString(hexString: String) -> UIColor {
        return self.colorFromHexString(hexString: hexString, alpha: 1.0)
    }
    
    public func colorFromHexString(hexString: String, alpha: CGFloat) -> UIColor {
        var hexNum: UInt32 = 0
        Scanner(string: hexString).scanHexInt32(&hexNum)
        return UIColor(red: ((CGFloat)((hexNum & 0xFF0000) >> 16))/255.0 , green: ((CGFloat)((hexNum & 0xFF00) >> 8))/255.0, blue: ((CGFloat)(hexNum & 0xFF))/255.0, alpha: alpha)
    }
    
//    
//    + (CGContextRef)getTextCTMContextRefFromView:(UIView *)aView
//    {
//    if (CUCFCheckObjectNull(aView)) {
//    return nil
//    }
//    
//    /* 获取图形上下文 */
//    CGContextRef context = UIGraphicsGetCurrentContext()
//    
//    /* 变换矩阵 */
//    CGContextTranslateCTM(context, 0, aView.bounds.size.height)
//    CGContextScaleCTM(context, 1, -1.0)
//    
//    /* 设置文字绘制矩阵 */
//    CGContextSetTextMatrix(context, CGAffineTransformIdentity)
//    
//    return context
//    }
//    
//    /* 根据颜色创建图片 */
//    + (UIImage *)imageForColor:(UIColor*)aColor withSize:(CGSize)aSize
//    {
//    if (CUCFCheckObjectNull(aColor)) {
//    return nil
//    }
//    
//    CGRect rect = CGRectMake(0, 0, aSize.width, aSize.height)
//    
//    UIGraphicsBeginImageContext(rect.size)
//    
//    CGContextRef context = UIGraphicsGetCurrentContext()
//    
//    CGContextSetFillColorWithColor(context, aColor.CGColor)
//    
//    CGContextFillRect(context, rect)
//    
//    UIImage *img = UIGraphicsGetImageFromCurrentImageContext()
//    
//    UIGraphicsEndImageContext()
//    
//    return img
//    }
//    
//    + (UIImage *)viewScreenShot:(UIView *)aView
//    {
//    return [CUCFQuickUIUitil layerScreenShot:aView.layer]
//    }
//    
//    + (UIImage *)layerScreenShot:(CALayer *)layer
//    {
//    if (CUCFCheckObjectNull(layer)) {
//    return nil
//    }
//    
//    UIGraphicsBeginImageContextWithOptions(layer.bounds.size, layer.opaque, 0.0f)
//    [layer renderInContext:UIGraphicsGetCurrentContext()]
//    UIImage* image = UIGraphicsGetImageFromCurrentImageContext()
//    UIGraphicsEndImageContext()
//    return image
//    }
//    
//    + (UIImage *)combineImage:(UIImage *)backgroundImage withMaskImage:(UIImage *)maskImage
//    {
//    if (CUCFCheckObjectNull(backgroundImage)) {
//    return nil
//    }
//    UIGraphicsBeginImageContext(backgroundImage.size)
//    
//    // 绘制改变大小的图片
//    CGRect newImageFrame = CGRectMake(0, 0,backgroundImage.size.width, backgroundImage.size.height)
//    
//    [backgroundImage drawInRect:newImageFrame]
//    
//    CGFloat originX = (newImageFrame.size.width - maskImage.size.width)/2
//    CGFloat originY = (newImageFrame.size.height - maskImage.size.height)/2
//    
//    CGRect maskImageFrame = CGRectMake(originX, originY, maskImage.size.width, maskImage.size.height)
//    
//    [maskImage drawInRect:maskImageFrame]
//    
//    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext()
//    
//    UIGraphicsEndImageContext()
//    
//    return scaledImage
//    
//    }
//    
//    + (UIImage *)fixOretationImage:(UIImage *)aImage
//    {
//    if (!aImage) {
//    return nil
//    }
//    
//    // 正确的方向
//    if (aImage.imageOrientation == UIImageOrientationUp){
//    
//    CGSize scaleSize = aImage.size
//    
//    UIGraphicsBeginImageContext(scaleSize)
//    
//    // 绘制改变大小的图片
//    [aImage drawInRect:CGRectMake(0, 0, scaleSize.width, scaleSize.height)]
//    
//    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext()
//    
//    UIGraphicsEndImageContext()
//    
//    return scaledImage
//    }
//    
//    // 错误的方向
//    CGAffineTransform transform = CGAffineTransformIdentity
//    
//    switch (aImage.imageOrientation) {
//    case UIImageOrientationDown:
//    case UIImageOrientationDownMirrored:
//    transform = CGAffineTransformTranslate(transform, aImage.size.width, aImage.size.height)
//    transform = CGAffineTransformRotate(transform, M_PI)
//    break
//    
//    case UIImageOrientationLeft:
//    case UIImageOrientationLeftMirrored:
//    transform = CGAffineTransformTranslate(transform, aImage.size.width, 0)
//    transform = CGAffineTransformRotate(transform, M_PI_2)
//    break
//    
//    case UIImageOrientationRight:
//    case UIImageOrientationRightMirrored:
//    transform = CGAffineTransformTranslate(transform, 0, aImage.size.height)
//    transform = CGAffineTransformRotate(transform, -M_PI_2)
//    break
//    case UIImageOrientationUp:
//    case UIImageOrientationUpMirrored:
//    break
//    }
//    
//    switch (aImage.imageOrientation) {
//    case UIImageOrientationUpMirrored:
//    case UIImageOrientationDownMirrored:
//    transform = CGAffineTransformTranslate(transform, aImage.size.width, 0)
//    transform = CGAffineTransformScale(transform, -1, 1)
//    break
//    
//    case UIImageOrientationLeftMirrored:
//    case UIImageOrientationRightMirrored:
//    transform = CGAffineTransformTranslate(transform, aImage.size.height, 0)
//    transform = CGAffineTransformScale(transform, -1, 1)
//    break
//    case UIImageOrientationUp:
//    case UIImageOrientationDown:
//    case UIImageOrientationLeft:
//    case UIImageOrientationRight:
//    break
//    }
//    
//    CGContextRef ctx = CGBitmapContextCreate(NULL, aImage.size.width, aImage.size.height,
//    CGImageGetBitsPerComponent(aImage.CGImage), 0,
//    CGImageGetColorSpace(aImage.CGImage),
//    CGImageGetBitmapInfo(aImage.CGImage))
//    CGContextConcatCTM(ctx, transform)
//    switch (aImage.imageOrientation) {
//    case UIImageOrientationLeft:
//    case UIImageOrientationLeftMirrored:
//    case UIImageOrientationRight:
//    case UIImageOrientationRightMirrored:
//    
//    CGContextDrawImage(ctx, CGRectMake(0,0,aImage.size.height,aImage.size.width), aImage.CGImage)
//    break
//    
//    default:
//    CGContextDrawImage(ctx, CGRectMake(0,0,aImage.size.width,aImage.size.height), aImage.CGImage)
//    break
//    }
//    
//    // 创建一张新图
//    CGImageRef cgimg = CGBitmapContextCreateImage(ctx)
//    UIImage *img = [UIImage imageWithCGImage:cgimg]
//    CGContextRelease(ctx)
//    CGImageRelease(cgimg)
//    
//    return img
//    }
//    
//    + (UIImage *)partImage:(UIImage *)aImage withRect:(CGRect)partRect
//    {
//    if (CUCFCheckObjectNull(aImage)) {
//    return nil
//    }
//    
//    CGImageRef imager = CGImageCreateWithImageInRect(aImage.CGImage,partRect)
//    
//    UIImage *partImage = [UIImage imageWithCGImage:imager]
//    
//    CGImageRelease(imager)
//    
//    return partImage
//    
//    }
//    
//    + (UIImage *)correctFullSolutionImageFromALAsset:(ALAsset *)asset withScaleSize:(CGFloat)scaleSize
//    {
//    if (CUCFCheckObjectNull(asset)) {
//    return nil
//    }
//    
//    ALAssetRepresentation *defaultReprensent = [asset defaultRepresentation]
//    
//    UIImage *fullSolutionImage = [UIImage imageWithCGImage:defaultReprensent.fullResolutionImage scale:1.0 orientation:(UIImageOrientation)[defaultReprensent orientation]]
//    
//    return [CUCFQuickUIUitil correctImageOrientation:fullSolutionImage withScaleSize:scaleSize]
//    }
//    
//    + (UIImage *)correctFullSolutionImageFromALAsset:(ALAsset *)asset
//    {
//    return [CUCFQuickUIUitil correctFullSolutionImageFromALAsset:asset withScaleSize:1.0]
//    }
//    
//    + (UIImage *)correctImageOrientation:(UIImage *)aImage
//    {
//    return [CUCFQuickUIUitil correctImageOrientation:aImage withScaleSize:1.0]
//    }
//    
//    + (UIImage *)correctImageOrientation:(UIImage *)aImage  withScaleSize:(CGFloat)scale
//    {
//    if (CUCFCheckObjectNull(aImage)) {
//    return nil
//    }
//    
//    // 正确的方向
//    if (aImage.imageOrientation == UIImageOrientationUp){
//    
//    CGSize scaleSize = CGSizeMake(aImage.size.width * scale, aImage.size.height * scale)
//    
//    UIGraphicsBeginImageContext(scaleSize)
//    
//    // 绘制改变大小的图片
//    [aImage drawInRect:CGRectMake(0, 0, scaleSize.width, scaleSize.height)]
//    
//    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext()
//    
//    UIGraphicsEndImageContext()
//    
//    return scaledImage
//    }
//    
//    // 错误的方向
//    CGAffineTransform transform = CGAffineTransformIdentity
//    
//    switch (aImage.imageOrientation) {
//    case UIImageOrientationDown:
//    case UIImageOrientationDownMirrored:
//    transform = CGAffineTransformTranslate(transform, aImage.size.width, aImage.size.height)
//    transform = CGAffineTransformRotate(transform, M_PI)
//    break
//    
//    case UIImageOrientationLeft:
//    case UIImageOrientationLeftMirrored:
//    transform = CGAffineTransformTranslate(transform, aImage.size.width, 0)
//    transform = CGAffineTransformRotate(transform, M_PI_2)
//    break
//    
//    case UIImageOrientationRight:
//    case UIImageOrientationRightMirrored:
//    transform = CGAffineTransformTranslate(transform, 0, aImage.size.height)
//    transform = CGAffineTransformRotate(transform, -M_PI_2)
//    break
//    case UIImageOrientationUp:
//    case UIImageOrientationUpMirrored:
//    break
//    }
//    
//    switch (aImage.imageOrientation) {
//    case UIImageOrientationUpMirrored:
//    case UIImageOrientationDownMirrored:
//    transform = CGAffineTransformTranslate(transform, aImage.size.width, 0)
//    transform = CGAffineTransformScale(transform, -1, 1)
//    break
//    
//    case UIImageOrientationLeftMirrored:
//    case UIImageOrientationRightMirrored:
//    transform = CGAffineTransformTranslate(transform, aImage.size.height, 0)
//    transform = CGAffineTransformScale(transform, -1, 1)
//    break
//    case UIImageOrientationUp:
//    case UIImageOrientationDown:
//    case UIImageOrientationLeft:
//    case UIImageOrientationRight:
//    break
//    }
//    
//    CGContextRef ctx = CGBitmapContextCreate(NULL, aImage.size.width, aImage.size.height,
//    CGImageGetBitsPerComponent(aImage.CGImage), 0,
//    CGImageGetColorSpace(aImage.CGImage),
//    CGImageGetBitmapInfo(aImage.CGImage))
//    CGContextConcatCTM(ctx, transform)
//    switch (aImage.imageOrientation) {
//    case UIImageOrientationLeft:
//    case UIImageOrientationLeftMirrored:
//    case UIImageOrientationRight:
//    case UIImageOrientationRightMirrored:
//    
//    CGContextDrawImage(ctx, CGRectMake(0,0,aImage.size.height,aImage.size.width), aImage.CGImage)
//    break
//    
//    default:
//    CGContextDrawImage(ctx, CGRectMake(0,0,aImage.size.width,aImage.size.height), aImage.CGImage)
//    break
//    }
//    
//    // 创建一张新图
//    CGImageRef cgimg = CGBitmapContextCreateImage(ctx)
//    UIImage *img = [UIImage imageWithCGImage:cgimg]
//    CGContextRelease(ctx)
//    CGImageRelease(cgimg)
//    
//    CGSize scaleSize = CGSizeMake(aImage.size.width * scale, aImage.size.height * scale)
//    
//    UIGraphicsBeginImageContext(scaleSize)
//    
//    // 绘制改变大小的图片
//    [img drawInRect:CGRectMake(0, 0, scaleSize.width, scaleSize.height)]
//    
//    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext()
//    
//    UIGraphicsEndImageContext()
//    
//    return scaledImage
//    
//    }
//    
//    + (UIImage *)roundImage:(UIImage *)aImage
//    {
//    if (CUCFCheckObjectNull(aImage)) {
//    return nil
//    }
//    
//    /* 取最小边，否则会出现椭圆 */
//    CGFloat itemWidth = MIN(aImage.size.width, aImage.size.height)
//    
//    //取中心
//    CGFloat originX = (aImage.size.width - itemWidth)/2
//    CGFloat originY = (aImage.size.height - itemWidth)/2
//    CGRect imageRect = (CGRect){originX,originY,itemWidth,itemWidth}
//    aImage = [CUCFQuickUIUitil partImage:aImage withRect:imageRect]
//    imageRect.origin = CGPointZero;//恢复
//    
//    UIGraphicsBeginImageContextWithOptions((CGSize){itemWidth,itemWidth}, 1.0, 0.0f)
//    
//    CGColorSpaceRef maskColorSpaceRef = CGColorSpaceCreateDeviceGray()
//    CGContextRef mainMaskContextRef = CGBitmapContextCreate(NULL,
//    imageRect.size.width,
//    imageRect.size.height,
//    8,
//    imageRect.size.width,
//    maskColorSpaceRef,
//    0)
//    CGContextFillRect(mainMaskContextRef, imageRect)
//    CGContextSetFillColorWithColor(mainMaskContextRef,[UIColor whiteColor].CGColor)
//    
//    // Create main mask shape
//    CGContextMoveToPoint(mainMaskContextRef, 0, 0)
//    CGContextAddEllipseInRect(mainMaskContextRef, imageRect)
//    CGContextFillPath(mainMaskContextRef)
//    
//    CGImageRef mainMaskImageRef = CGBitmapContextCreateImage(mainMaskContextRef)
//    CGContextRelease(mainMaskContextRef)
//    
//    CGContextRef contextRef = UIGraphicsGetCurrentContext()
//    CGContextSaveGState(contextRef)
//    
//    CGImageRef imageRef = CGImageCreateWithMask(aImage.CGImage, mainMaskImageRef)
//    
//    CGContextTranslateCTM(contextRef, 0, imageRect.size.height)
//    CGContextScaleCTM(contextRef, 1.0, -1.0)
//    
//    CGContextSaveGState(contextRef)
//    
//    
//    UIImage* image = [UIImage imageWithCGImage:imageRef]
//    
//    CGImageRelease(imageRef)
//    CGImageRelease(mainMaskImageRef)
//    
//    UIGraphicsEndImageContext()
//    
//    return image
//    }
//    
//    + (UIImage *)imageWithName:(NSString *)imageName
//    {
//    if (CUCFStringIsNull(imageName)) {
//    return nil
//    }
//    return [UIImage imageNamed:imageName]
//    }
//    
//    + (UIImage *)imageWithFilePath:(NSString *)filePath
//    {
//    if (CUCFStringIsNull(filePath)) {
//    return nil
//    }
//    return [UIImage imageWithContentsOfFile:filePath]
//    }
//    
//    + (UIImage *)imageUnArchievedFromFilePath:(NSString *)filePath
//    {
//    return CUCFUnArchieveObject(filePath)
//    }
//    
//    + (UIImage *)gradientLinearImageFromColor:(UIColor *)fromColor withToColor:(UIColor *)toColor withImageSize:(CGSize)size
//    {
//    return [CUCFQuickUIUitil gradientLinearImageFromColors:@[fromColor,toColor,fromColor] withImageSize:size]
//    }
//    
//    + (UIImage *)gradientLinearImageFromColors:(NSArray *)colors withImageSize:(CGSize)size
//    {
//    if (CUCFCheckObjectNull(colors)) {
//    return nil
//    }
//    
//    UIGraphicsBeginImageContext(size)
//    
//    CGContextRef context = UIGraphicsGetCurrentContext()
//    
//    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB()
//    
//    CGFloat _locations[] = {0.f,0.5f,1.f}
//    
//    NSMutableArray *_colors = [NSMutableArray array]
//    
//    id fromColor = nil
//    if (colors.count >= 1) {
//    fromColor = (__bridge id)[(UIColor *)[colors objectAtIndex:0] CGColor]
//    }
//    
//    id toColor = nil
//    if (colors.count >= 2) {
//    toColor = (__bridge id)[(UIColor *)[colors objectAtIndex:1] CGColor]
//    }
//    
//    id endColor = nil
//    if (colors.count == 3) {
//    endColor  = (__bridge id)[(UIColor *)[colors objectAtIndex:2] CGColor]
//    }
//    
//    if (fromColor) {
//    [_colors addObject:fromColor]
//    }
//    if (toColor) {
//    [_colors addObject:toColor]
//    }
//    if (endColor) {
//    [_colors addObject:endColor]
//    }
//    
//    CGGradientRef gradient =  CGGradientCreateWithColors(colorSpace, (__bridge CFArrayRef)_colors, _locations)
//    
//    CGPoint startPoint = CGPointMake(floorf(size.width), 0)
//    
//    CGPoint endPoint = CGPointMake(floorf(size.width), size.height)
//    
//    CGContextDrawLinearGradient(context, gradient, startPoint, endPoint, 0)
//    
//    CFRelease(gradient)
//    
//    CFRelease(colorSpace)
//    
//    UIImage *gradientImage = UIGraphicsGetImageFromCurrentImageContext()
//    
//    UIGraphicsEndImageContext()
//    
//    return gradientImage
//    
//    }
//    
//    + (UIImage *)gradientRadialImageFromColor:(UIColor *)fromColor withToColor:(UIColor *)toColor withImageSize:(CGSize)size
//    {
//    return [CUCFQuickUIUitil gradientRadialImageFromColors:@[fromColor,toColor] withImageSize:size]
//    }
//    
//    + (UIImage *)gradientRadialImageFromColors:(NSArray *)colors withImageSize:(CGSize)size
//    {
//    if (CUCFCheckObjectNull(colors)) {
//    return nil
//    }
//    
//    UIGraphicsBeginImageContext(size)
//    
//    CGContextRef context = UIGraphicsGetCurrentContext()
//    
//    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB()
//    
//    CGFloat _locations[] = {0.f,0.5,1.f}
//    
//    NSMutableArray *_colors = [NSMutableArray array]
//    
//    id fromColor = nil
//    if (colors.count >= 1) {
//    fromColor = (__bridge id)[(UIColor *)[colors objectAtIndex:0] CGColor]
//    }
//    
//    id toColor = nil
//    if (colors.count >= 2) {
//    toColor = (__bridge id)[(UIColor *)[colors objectAtIndex:1] CGColor]
//    }
//    
//    id endColor = nil
//    if (colors.count == 3) {
//    endColor  = (__bridge id)[(UIColor *)[colors objectAtIndex:2] CGColor]
//    }
//    
//    if (fromColor) {
//    [_colors addObject:fromColor]
//    }
//    if (toColor) {
//    [_colors addObject:toColor]
//    }
//    if (endColor) {
//    [_colors addObject:endColor]
//    }
//    
//    CGGradientRef gradient =  CGGradientCreateWithColors(colorSpace, (__bridge CFArrayRef)_colors, _locations)
//    
//    CGPoint startPoint = CGPointMake(floorf(size.width/2), size.height/2)
//    
//    CGFloat radius = MIN(size.width/2, size.height/2)
//    CGContextDrawRadialGradient(context, gradient, startPoint, 0, startPoint,radius, kCGGradientDrawsBeforeStartLocation|kCGGradientDrawsAfterEndLocation)
//    
//    CFRelease(gradient)
//    
//    CFRelease(colorSpace)
//    
//    UIImage *gradientImage = UIGraphicsGetImageFromCurrentImageContext()
//    
//    UIGraphicsEndImageContext()
//    
//    return gradientImage
//    }
//    
//    + (UIImage *)gridImageHorizonByLineGap:(CGFloat)lineGap withLineColor:(UIColor *)lineColor withImageSize:(CGSize)size
//    {
//    return [CUCFQuickUIUitil gridImageByHoriLineGap:lineGap withVerticalLineGap:0.f withLineColor:lineColor withImageSize:size]
//    }
//    
//    + (UIImage *)gridImageVerticalByLineGap:(CGFloat)lineGap withLineColor:(UIColor *)lineColor withImageSize:(CGSize)size
//    {
//    return [CUCFQuickUIUitil gridImageByHoriLineGap:0.f withVerticalLineGap:lineGap withLineColor:lineColor withImageSize:size]
//    }
//    
//    + (UIImage *)gridImageByHoriLineGap:(CGFloat)hLineGap withVerticalLineGap:(CGFloat)vLineGap withLineColor:(UIColor *)lineColor withImageSize:(CGSize)size
//    {
//    CGFloat width = size.width
//    CGFloat height = size.height
//    
//    UIGraphicsBeginImageContext(CGSizeMake(width, height))
//    CGContextRef context = UIGraphicsGetCurrentContext()
//    
//    CGContextSetStrokeColorWithColor(context, lineColor.CGColor)
//    
//    if (hLineGap > 0.f) {
//    
//    for (int y=0; y < height; y=y+hLineGap) {
//    
//    CGContextBeginPath(context)
//    
//    CGContextMoveToPoint(context, 0, y)
//    
//    CGContextAddLineToPoint(context, width, y)
//    
//    CGContextStrokePath(context)
//    }
//    }
//    
//    if (vLineGap > 0.f) {
//    
//    for (int x=0; x<width; x=x+vLineGap) {
//    
//    CGContextBeginPath(context)
//    
//    CGContextMoveToPoint(context, x, 0)
//    
//    CGContextAddLineToPoint(context, x, height)
//    
//    CGContextStrokePath(context)
//    }
//    }
//    
//    
//    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext()
//    UIGraphicsEndImageContext()
//    
//    return newImage
//    
//    }
//    
//    + (UIImage *)stretchImage:(UIImage *)originImage withTopOffset:(CGFloat)top withLeftOffset:(CGFloat)left
//    {
//    if (CUCFCheckObjectNull(originImage)) {
//    return nil
//    }
//    return [originImage stretchableImageWithLeftCapWidth:left topCapHeight:top]
//    }
//    
//    + (UIImage *)resizeImage:(UIImage *)originImage withEdgeTop:(CGFloat)top withEdgeBottom:(CGFloat)bottom withEdgeLeft:(CGFloat)left withEdgeRight:(CGFloat)right
//    {
//    if (CUCFCheckObjectNull(originImage)) {
//    return nil
//    }
//    return [originImage resizableImageWithCapInsets:UIEdgeInsetsMake(top, left, bottom, right) resizingMode:UIImageResizingModeStretch]
//    }
//    
//    + (void)animationDuration:(NSTimeInterval)duration action:(dispatch_block_t)block
//    {
//    [CUCFQuickUIUitil animationDelay:0 animationDuration:duration action:block]
//    }
//    
//    + (void)animationDelay:(NSTimeInterval )delaySecond animationDuration:(NSTimeInterval)duration action:(dispatch_block_t)block
//    {
//    if (CUCFCheckObjectNull(block)) {
//    return
//    }
//    
//    CUCFAsyncMainQueueDelay(delaySecond, ^{
//    
//    [UIView animateWithDuration:duration animations:block]
//    
//    })
//    }
//    
//    + (void)animationView:(UIView *)view withDuration:(NSTimeInterval)duration action:(dispatch_block_t)block withOptions:(UIViewAnimationOptions)options completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    [CUCFQuickUIUitil animationView:view delay:0 animationDuration:duration action:block withOptions:options completionBlock:completion]
//    }
//    
//    + (void)animationView:(UIView *)view delay:(NSTimeInterval)delaySecond animationDuration:(NSTimeInterval)duration action:(dispatch_block_t)block withOptions:(UIViewAnimationOptions)options completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    [UIView transitionWithView:view duration:duration options:options animations:block completion:completion]
//    }
//    
//    + (void)defaultHiddenShowView:(UIView *)view
//    {
//    [CUCFQuickUIUitil hiddenShowView:view withDuration:0.3]
//    }
//    
//    + (void)defaultShowHiddenView:(UIView *)view
//    {
//    [CUCFQuickUIUitil showHiddenView:view withDuration:0.3]
//    }
//    
//    + (void)defaultHiddenView:(UIView *)view
//    {
//    [CUCFQuickUIUitil hiddenView:view withDuration:0.3]
//    }
//    
//    + (void)defaultShowView:(UIView *)view
//    {
//    [CUCFQuickUIUitil showView:view withDuration:0.3]
//    }
//    
//    + (void)hiddenView:(UIView *)view withDuration:(NSTimeInterval)duration
//    {
//    [CUCFQuickUIUitil animationView:view fromAlpha:view.alpha toAlpha:0 withDuration:duration]
//    }
//    
//    + (void)showView:(UIView *)view withDuration:(NSTimeInterval)duration
//    {
//    [CUCFQuickUIUitil animationView:view fromAlpha:view.alpha toAlpha:1 withDuration:duration]
//    }
//    
//    + (void)showView:(UIView *)view finalAlpha:(CGFloat)alpha withDuration:(NSTimeInterval)duration
//    {
//    [CUCFQuickUIUitil animationView:view fromAlpha:view.alpha toAlpha:alpha withDuration:duration]
//    }
//    
//    + (void)hiddenShowView:(UIView *)view withDuration:(NSTimeInterval)duration
//    {
//    [CUCFQuickUIUitil animationView:view fromAlpha:0 toAlpha:1 withDuration:duration]
//    }
//    
//    + (void)showHiddenView:(UIView *)view withDuration:(NSTimeInterval)duration
//    {
//    [CUCFQuickUIUitil animationView:view fromAlpha:1 toAlpha:0 withDuration:duration]
//    }
//    
//    + (void)animationView:(UIView *)view fromAlpha:(CGFloat)fAlpha toAlpha:(CGFloat)tAlpha withDuration:(NSTimeInterval)duration
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    
//    view.alpha = fAlpha
//    
//    CUCFAnimationWithDuration(duration, ^{
//    
//    view.alpha = tAlpha
//    
//    })
//    }
//    
//    + (void)moveView:(UIView *)view newRect:(CGRect)rect withDuration:(NSTimeInterval)duration
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    
//    if (CGRectEqualToRect(view.frame, rect)) {
//    return
//    }
//    
//    CUCFAnimationWithDuration(duration, ^{
//    
//    view.frame = rect
//    
//    })
//    }
//    
//    + (void)moveViewX:(UIView *)view originXDetal:(CGFloat)moveX withDuration:(NSTimeInterval)duration
//    {
//    [CUCFQuickUIUitil moveView:view originXDetal:moveX originYDetal:0.f widthDetal:0.f heightDetla:0.f withDuration:duration]
//    }
//    
//    + (void)moveViewY:(UIView *)view originYDetal:(CGFloat)moveY withDuration:(NSTimeInterval)duration
//    {
//    [CUCFQuickUIUitil moveView:view originXDetal:0.f originYDetal:moveY widthDetal:0.f heightDetla:0.f withDuration:duration]
//    }
//    
//    + (void)moveViewWidth:(UIView *)view widthDetal:(CGFloat)moveWidth withDuration:(NSTimeInterval)duration
//    {
//    [CUCFQuickUIUitil moveView:view originXDetal:0.f originYDetal:0.f widthDetal:moveWidth heightDetla:0.f withDuration:duration]
//    }
//    
//    + (void)moveViewHeight:(UIView *)view heightDetal:(CGFloat)moveHeight withDuration:(NSTimeInterval)duration
//    {
//    [CUCFQuickUIUitil moveView:view originXDetal:0.f originYDetal:0.f widthDetal:0.f heightDetla:moveHeight withDuration:duration]
//    }
//    
//    + (void)moveViewToX:(UIView *)view toOriginX:(CGFloat)moveX withDuration:(NSTimeInterval)duration
//    {
//    CGFloat detal = moveX - view.frame.origin.x
//    [CUCFQuickUIUitil moveViewX:view originXDetal:detal withDuration:duration]
//    }
//    
//    + (void)moveViewToY:(UIView *)view toOriginY:(CGFloat)moveY withDuration:(NSTimeInterval)duration
//    {
//    CGFloat detal = moveY - view.frame.origin.y
//    [CUCFQuickUIUitil moveViewY:view originYDetal:detal withDuration:duration]
//    }
//    
//    + (void)moveViewToWidth:(UIView *)view toWidth:(CGFloat)moveWidth withDuration:(NSTimeInterval)duration
//    {
//    CGFloat detal = moveWidth - view.frame.size.width
//    [CUCFQuickUIUitil moveViewWidth:view widthDetal:detal withDuration:duration]
//    }
//    
//    + (void)moveViewToHeight:(UIView *)view toHeight:(CGFloat)moveHeight withDuration:(NSTimeInterval)duration
//    {
//    CGFloat detal = moveHeight - view.frame.size.height
//    [CUCFQuickUIUitil moveViewHeight:view heightDetal:detal withDuration:duration]
//    }
//    
//    + (void)moveViewCenter:(UIView *)view newCenter:(CGPoint)center withDuration:(NSTimeInterval)duration
//    {
//    CUCFAnimationWithDuration(duration, ^{
//    
//    view.center = center
//    
//    })
//    }
//    
//    + (void)moveViewSize:(UIView *)view newSize:(CGSize)size withDuration:(NSTimeInterval)duration
//    {
//    CGRect newFrame = view.frame
//    newFrame.size = size
//    CUCFAnimationMoveViewRect(view, newFrame, duration)
//    }
//    
//    + (void)moveView:(UIView *)view originXDetal:(CGFloat)moveX originYDetal:(CGFloat)moveY widthDetal:(CGFloat)moveWidth heightDetla:(CGFloat)moveHeight withDuration:(NSTimeInterval)duration
//    {
//    CGRect newFrame = view.frame
//    newFrame.origin.x = newFrame.origin.x + moveX
//    newFrame.origin.y = newFrame.origin.y + moveY
//    newFrame.size.width = newFrame.size.width + moveWidth
//    newFrame.size.height = newFrame.size.height + moveHeight
//    
//    [CUCFQuickUIUitil moveView:view newRect:newFrame withDuration:duration]
//    }
//    
//    + (void)flipViewFromLeft:(UIView *)view withDuration:(NSTimeInterval)duration action:(dispatch_block_t)block completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    [CUCFQuickUIUitil flipView:view fromLeft:YES fromRight:NO fromTop:NO fromBottom:NO action:block withDuration:duration completionBlock:completion]
//    }
//    
//    + (void)flipViewFromRight:(UIView *)view withDuration:(NSTimeInterval)duration action:(dispatch_block_t)block completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    [CUCFQuickUIUitil flipView:view fromLeft:NO fromRight:YES fromTop:NO fromBottom:NO action:block withDuration:duration completionBlock:completion]
//    }
//    
//    + (void)flipViewFromTop:(UIView *)view withDuration:(NSTimeInterval)duration action:(dispatch_block_t)block completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    [CUCFQuickUIUitil flipView:view fromLeft:NO fromRight:NO fromTop:YES fromBottom:NO action:block withDuration:duration completionBlock:completion]
//    }
//    
//    + (void)flipViewFromBottom:(UIView *)view withDuration:(NSTimeInterval)duration action:(dispatch_block_t)block completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    [CUCFQuickUIUitil flipView:view fromLeft:NO fromRight:NO fromTop:NO fromBottom:YES action:block withDuration:duration completionBlock:completion]
//    }
//    
//    + (void)flipView:(UIView *)view fromLeft:(BOOL)isLeft fromRight:(BOOL)isRight fromTop:(BOOL)isTop fromBottom:(BOOL)isBottom action:(dispatch_block_t)block withDuration:(NSTimeInterval)duration completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    if (isLeft) {
//    [CUCFQuickUIUitil animationView:view withDuration:duration action:block withOptions:UIViewAnimationOptionTransitionFlipFromLeft completionBlock:completion]
//    }
//    if (isRight) {
//    [CUCFQuickUIUitil animationView:view withDuration:duration action:block withOptions:UIViewAnimationOptionTransitionFlipFromRight completionBlock:completion]
//    }
//    if (isTop) {
//    [CUCFQuickUIUitil animationView:view withDuration:duration action:block withOptions:UIViewAnimationOptionTransitionFlipFromTop completionBlock:completion]
//    }
//    if (isBottom) {
//    [CUCFQuickUIUitil animationView:view withDuration:duration action:block withOptions:UIViewAnimationOptionTransitionFlipFromBottom completionBlock:completion]
//    }
//    }
//    
//    + (void)pageUpViewFromBottom:(UIView *)view withDuration:(NSTimeInterval)duration action:(dispatch_block_t)block completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    [CUCFQuickUIUitil pageUpView:view fromLeft:NO fromRight:NO fromTop:NO fromBottom:YES action:block withDuration:duration completionBlock:completion]
//    }
//    
//    + (void)pageUpView:(UIView *)view fromLeft:(BOOL)isLeft fromRight:(BOOL)isRight fromTop:(BOOL)isTop fromBottom:(BOOL)isBottom action:(dispatch_block_t)block withDuration:(NSTimeInterval)duration completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    if (isBottom) {
//    [CUCFQuickUIUitil animationView:view withDuration:duration action:block withOptions:UIViewAnimationOptionTransitionCurlUp|UIViewAnimationOptionCurveEaseInOut completionBlock:completion]
//    }
//    }
//    
//    + (void)pageDownViewFromTop:(UIView *)view withDuration:(NSTimeInterval)duration action:(dispatch_block_t)block completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    [CUCFQuickUIUitil pageDownView:view fromLeft:NO fromRight:NO fromTop:YES fromBottom:NO action:block withDuration:duration completionBlock:completion]
//    }
//    
//    + (void)pageDownView:(UIView *)view fromLeft:(BOOL)isLeft fromRight:(BOOL)isRight fromTop:(BOOL)isTop fromBottom:(BOOL)isBottom action:(dispatch_block_t)block withDuration:(NSTimeInterval)duration completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    if (isTop) {
//    [CUCFQuickUIUitil animationView:view withDuration:duration action:block withOptions:UIViewAnimationOptionTransitionCurlDown|UIViewAnimationOptionCurveEaseInOut completionBlock:completion]
//    }
//    }
//    
//    + (void)cubeView:(UIView *)view withDuration:(NSTimeInterval)duration action:(dispatch_block_t)block completionBlock:(CUCFQuickAnimationCompletionBlock)completion
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    [CUCFQuickUIUitil animationView:view withDuration:duration action:block withOptions:UIViewAnimationOptionTransitionCrossDissolve|UIViewAnimationOptionCurveEaseInOut completionBlock:completion]
//    }
//    
//    
//    + (void)rotationViewX:(UIView *)view withDegree:(CGFloat)degree withDuration:(NSTimeInterval)duration
//    {
//    NSString *path = @"transform.rotation.x"
//    [CUCFQuickUIUitil animationView:view withAnimationPath:path toValue:CUCFDegreeToRadiusValue(degree) withDuration:duration]
//    }
//    
//    + (void)rotationViewY:(UIView *)view withDegree:(CGFloat)degree withDuration:(NSTimeInterval)duration
//    {
//    NSString *path = @"transform.rotation.y"
//    [CUCFQuickUIUitil animationView:view withAnimationPath:path toValue:CUCFDegreeToRadiusValue(degree) withDuration:duration]
//    }
//    
//    + (void)rotationViewZ:(UIView *)view withDegree:(CGFloat)degree withDuration:(NSTimeInterval)duration
//    {
//    NSString *path = @"transform.rotation.z"
//    [CUCFQuickUIUitil animationView:view withAnimationPath:path toValue:CUCFDegreeToRadiusValue(degree) withDuration:duration]
//    }
//    
//    + (void)translationViewX:(UIView *)view withOriginX:(CGFloat)originX withDuration:(NSTimeInterval)duration
//    {
//    NSString *path = @"transform.translation.x"
//    [CUCFQuickUIUitil animationView:view withAnimationPath:path toValue:@(originX) withDuration:duration]
//    }
//    
//    + (void)translationViewY:(UIView *)view withOriginY:(CGFloat)originY withDuration:(NSTimeInterval)duration
//    {
//    NSString *path = @"transform.translation.y"
//    [CUCFQuickUIUitil animationView:view withAnimationPath:path toValue:@(originY) withDuration:duration]
//    }
//    
//    + (void)translationViewZ:(UIView *)view withOriginZ:(CGFloat)originZ withDuration:(NSTimeInterval)duration
//    {
//    NSString *path = @"transform.translation.z"
//    [CUCFQuickUIUitil animationView:view withAnimationPath:path toValue:@(originZ) withDuration:duration]
//    }
//    
//    + (void)scaleViewX:(UIView *)view withScaleSize:(CGFloat)size withDuration:(NSTimeInterval)duration
//    {
//    NSString *path = @"transform.scale.x"
//    [CUCFQuickUIUitil animationView:view withAnimationPath:path toValue:@(size) withDuration:duration]
//    }
//    
//    + (void)scaleViewY:(UIView *)view withScaleSize:(CGFloat)size withDuration:(NSTimeInterval)duration
//    {
//    NSString *path = @"transform.scale.y"
//    [CUCFQuickUIUitil animationView:view withAnimationPath:path toValue:@(size) withDuration:duration]
//    }
//    
//    + (void)scaleViewZ:(UIView *)view withScaleSize:(CGFloat)size withDuration:(NSTimeInterval)duration
//    {
//    NSString *path = @"transform.scale.z"
//    [CUCFQuickUIUitil animationView:view withAnimationPath:path toValue:@(size) withDuration:duration]
//    }
//    
//    + (void)animationView:(UIView *)view withAnimationPath:(NSString *)path toValue:(NSValue *)value withDuration:(NSTimeInterval)duration
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    CABasicAnimation* basicAnimation = [CABasicAnimation animationWithKeyPath:path]
//    basicAnimation.toValue = value
//    basicAnimation.duration = duration
//    basicAnimation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut]
//    [view.layer addAnimation:basicAnimation forKey:[NSString stringWithFormat:@"cucfAnimation_%@_%lu_%lu_%f",path,(unsigned long)[view hash],(unsigned long)[value hash],duration]]
//    }
//    
//    + (void)view3DRotateX:(UIView *)view withDegree:(CGFloat)degree
//    {
//    [CUCFQuickUIUitil view3DRotate:view x:1 y:0 z:0 withDegree:CUCFDegreeToRadius(degree)]
//    }
//    
//    + (void)view3DRotateY:(UIView *)view withDegree:(CGFloat)degree
//    {
//    [CUCFQuickUIUitil view3DRotate:view x:0 y:1 z:0 withDegree:CUCFDegreeToRadius(degree)]
//    }
//    
//    + (void)view3DRotateZ:(UIView *)view withDegree:(CGFloat)degree
//    {
//    [CUCFQuickUIUitil view3DRotate:view x:0 y:0 z:1 withDegree:CUCFDegreeToRadius(degree)]
//    }
//    
//    + (void)view3DRotate:(UIView *)view x:(CGFloat)xState y:(CGFloat)yState z:(CGFloat)zState withDegree:(CGFloat)degree
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    CUCFAnimationWithDuration(0.35, ^{
//    view.layer.transform = CATransform3DMakeRotation(degree, xState, yState, zState)
//    })
//    }
//    
//    + (void)view3DTranslateX:(UIView *)view withValue:(CGFloat)value
//    {
//    [CUCFQuickUIUitil view3DTranslate:view x:value y:0 z:0]
//    }
//    
//    + (void)view3DTranslateY:(UIView *)view withValue:(CGFloat)value
//    {
//    [CUCFQuickUIUitil view3DTranslate:view x:0 y:value z:0]
//    }
//    
//    + (void)view3DTranslateZ:(UIView *)view withValue:(CGFloat)value
//    {
//    [CUCFQuickUIUitil view3DTranslate:view x:0 y:0 z:value]
//    }
//    
//    + (void)view3DTranslate:(UIView *)view x:(CGFloat)xState y:(CGFloat)yState z:(CGFloat)zState
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    CUCFAnimationWithDuration(0.35, ^{
//    view.layer.transform = CATransform3DMakeTranslation(xState, yState, zState)
//    })
//    }
//    
//    + (void)view3DScaleX:(UIView *)view withValue:(CGFloat)value
//    {
//    [CUCFQuickUIUitil view3DScale:view x:value y:0 z:0]
//    }
//    
//    + (void)view3DScaleY:(UIView *)view withValue:(CGFloat)value
//    {
//    [CUCFQuickUIUitil view3DScale:view x:0 y:value z:0]
//    }
//    
//    + (void)view3DScaleZ:(UIView *)view withValue:(CGFloat)value
//    {
//    [CUCFQuickUIUitil view3DScale:view x:0 y:0 z:value]
//    }
//    
//    + (void)view3DScale:(UIView *)view x:(CGFloat)xState y:(CGFloat)yState z:(CGFloat)zState
//    {
//    if (CUCFCheckObjectNull(view)) {
//    return
//    }
//    CUCFAnimationWithDuration(0.35, ^{
//    view.layer.transform = CATransform3DMakeScale(xState, yState, zState)
//    })
//    }
//    
//    + (CUCFQuickUIUitil *)shareQuickUIUitil
//    {
//    static CUCFQuickUIUitil *_shareQuickUIInstance = nil
//    static dispatch_once_t onceToken
//    CUCFDispatchOnce(onceToken, ^{
//    
//    if (!_shareQuickUIInstance) {
//    _shareQuickUIInstance = [[self alloc]init]
//    }
//    })
//    return _shareQuickUIInstance
//    }
//    
//    - (void)addRepeatBlockInfo:(NSDictionary *)blockActionInfoDict
//    {
//    [self.repeatActionInfoDict setObject:blockActionInfoDict forKey:[blockActionInfoDict objectForKey:@"uniqueIdentifier"]]
//    }
//    
//    - (void)repeatAction:(NSTimer *)timer
//    {
//    if (self.repeatActionInfoDict.count == 0) {
//    [self.repeatTimer invalidate]
//    self.repeatTimer = nil
//    return
//    }
//    [self.repeatActionInfoDict enumerateKeysAndObjectsUsingBlock:^(NSString *blockIdentifier, NSDictionary *blockInfoDict, BOOL *stop) {
//    
//    dispatch_block_t action = [blockInfoDict objectForKey:@"action"]
//    
//    NSTimeInterval duration = [[blockInfoDict objectForKey:@"repeatDuration"]doubleValue]
//    
//    NSTimeInterval progress = [[blockInfoDict objectForKey:@"progress"]doubleValue]
//    
//    if (progress <= duration) {
//    
//    CUCFAsyncMainQueue(action)
//    
//    progress = progress + timer.timeInterval
//    
//    [blockInfoDict setValue:@(progress) forKey:@"progress"]
//    
//    [self.repeatActionInfoDict setObject:blockInfoDict forKey:blockIdentifier]
//    
//    }else{
//    
//    [self removeBlockInfo:blockIdentifier]
//    }
//    
//    }]
//    
//    }
//    
//    - (void)removeBlockInfo:(NSString *)blockIdentifier
//    {
//    if (CUCFStringIsNull(blockIdentifier)) {
//    return
//    }
//    if (self.repeatActionInfoDict.count == 0) {
//    return
//    }
//    __block NSString *findDestBlockIdentifier = nil
//    [self.repeatActionInfoDict enumerateKeysAndObjectsUsingBlock:^(NSString *blockIdentifier, NSDictionary *blockInfoDict, BOOL *stop) {
//    
//    NSString *blockInfoIdentifier = [blockInfoDict objectForKey:@"uniqueIdentifier"]
//    if ([blockInfoIdentifier isEqualToString:blockIdentifier]) {
//    findDestBlockIdentifier = blockInfoIdentifier
//    *stop = YES
//    }
//    }]
//    [self.repeatActionInfoDict removeObjectForKey:findDestBlockIdentifier]
//    }
//    
//    - (void)startRepeatAction
//    {
//    if (self.repeatActionInfoDict.count > 0) {
//    if (self.repeatTimer) {
//    [self.repeatTimer invalidate]
//    self.repeatTimer = nil
//    }
//    self.repeatTimer = [NSTimer scheduledTimerWithTimeInterval:0.3 target:self selector:@selector(repeatAction:) userInfo:nil repeats:YES]
//    }else{
//    if (self.repeatTimer) {
//    [self.repeatTimer invalidate]
//    self.repeatTimer = nil
//    }
//    }
//    }
//    
//    + (NSString *)repeatDoAction:(dispatch_block_t)block
//    {
//    return  [CUCFQuickUIUitil repeatDoAction:block withDelay:0.f]
//    }
//    
//    + (NSString *)repeatDoAction:(dispatch_block_t)block withDelay:(NSTimeInterval)delaySecond
//    {
//    return [CUCFQuickUIUitil repeatDoAction:block withDelay:delaySecond withRepeatDuration:0]
//    }
//    
//    + (NSString *)repeatDoAction:(dispatch_block_t)block withRepeatDuration:(NSTimeInterval)duration
//    {
//    return [CUCFQuickUIUitil repeatDoAction:block withDelay:0 withRepeatDuration:duration]
//    }
//    
//    + (NSString *)repeatDoAction:(dispatch_block_t)block withDelay:(NSTimeInterval)delaySecond withRepeatDuration:(NSTimeInterval)duration
//    {
//    if (CUCFCheckObjectNull(block)) {
//    return nil
//    }
//    
//    NSString *blockIdentifier = CUCFStringCurrentTimeStamp
//    
//    CUCFAsyncMainQueueDelay(delaySecond, ^{
//    
//    NSMutableDictionary *blockActionInfo = [NSMutableDictionary dictionary]
//    [blockActionInfo setObject:block forKey:@"action"]
//    [blockActionInfo setObject:blockIdentifier forKey:@"uniqueIdentifier"]
//    [blockActionInfo setObject:@(0) forKey:@"progress"]
//    [blockActionInfo setObject:@(duration) forKey:@"repeatDuration"]
//    
//    [[CUCFQuickUIUitil shareQuickUIUitil]addRepeatBlockInfo:blockActionInfo]
//    [[CUCFQuickUIUitil shareQuickUIUitil]startRepeatAction]
//    
//    })
//    
//    return blockIdentifier
//    }
//    
//    + (void)stopRepeatAction:(NSString *)blockIdentifier
//    {
//    if (CUCFStringIsNull(blockIdentifier)) {
//    return
//    }
//    [[CUCFQuickUIUitil shareQuickUIUitil]removeBlockInfo:blockIdentifier]
//    }
//    
//    + (void)animationViewXCycle:(UIView *)view withXMoveDetal:(CGFloat)moveX withDuration:(NSTimeInterval)duration
//    {
//    CUCFAnimationViewTranslationX(view, moveX, duration)
//    CUCFAnimationViewTranslationX(view, 0, duration)
//    CUCFAnimationViewTranslationX(view, -moveX, duration)
//    CUCFAnimationViewTranslationX(view, 0, duration)
//    }
//    
//    + (void)animationViewYCycle:(UIView *)view withYMoveDetal:(CGFloat)moveY withDuration:(NSTimeInterval)duration
//    {
//    CUCFAnimationViewTranslationY(view, moveY, duration)
//    CUCFAnimationViewTranslationY(view, 0, duration)
//    CUCFAnimationViewTranslationY(view, -moveY, duration)
//    CUCFAnimationViewTranslationY(view, 0, duration)
//    }
//    
//    + (void)animationViewZCycle:(UIView *)view withZMoveDetal:(CGFloat)moveZ withDuration:(NSTimeInterval)duration
//    {
//    CUCFAnimationViewTranslationZ(view, moveZ, duration)
//    CUCFAnimationViewTranslationZ(view, 0, duration)
//    CUCFAnimationViewTranslationZ(view, -moveZ, duration)
//    CUCFAnimationViewTranslationZ(view, 0, duration)
//    }
//    
//    + (void)animationViewRotateXCycle:(UIView *)view withXRotateDetal:(CGFloat)xDegree withDuration:(NSTimeInterval)duration
//    {
//    CUCFAnimationViewRotateX(view, xDegree, duration)
//    CUCFAnimationViewRotateX(view, 0, duration)
//    CUCFAnimationViewRotateX(view, -xDegree, duration)
//    CUCFAnimationViewRotateX(view, 0, duration)
//    }
//    
//    + (void)animationViewRotateYCycle:(UIView *)view withYRotateDetal:(CGFloat)yDegree withDuration:(NSTimeInterval)duration
//    {
//    CUCFAnimationViewRotateY(view, yDegree, duration)
//    CUCFAnimationViewRotateY(view, 0, duration)
//    CUCFAnimationViewRotateY(view, -yDegree, duration)
//    CUCFAnimationViewRotateY(view, 0, duration)
//    }
//    
//    + (void)animationViewRotateZCycle:(UIView *)view withZRotateDetal:(CGFloat)zDegree withDuration:(NSTimeInterval)duration
//    {
//    CUCFAnimationViewRotateZ(view, zDegree, duration)
//    CUCFAnimationViewRotateZ(view, 0, duration)
//    CUCFAnimationViewRotateZ(view, -zDegree, duration)
//    CUCFAnimationViewRotateZ(view, 0, duration)
//    }
//    
//    /* CAAnimation */
//    
//    + (void)animationLayer:(CALayer *)aLayer positionXWithFromValue:(NSValue *)fromValue withToValue:(NSValue *)toValue withRepeatCount:(NSInteger)repeatCount withDuration:(NSTimeInterval)duration
//    {
//    return [CUCFQuickUIUitil animationLayer:aLayer path:@"position.x" withFromValue:fromValue withToValue:toValue withRepeatCount:repeatCount withDuration:duration]
//    }
//    
//    + (void)animationLayer:(CALayer *)aLayer positionYWithFromValue:(NSValue *)fromValue withToValue:(NSValue *)toValue withRepeatCount:(NSInteger)repeatCount withDuration:(NSTimeInterval)duration
//    {
//    return [CUCFQuickUIUitil animationLayer:aLayer path:@"position.y" withFromValue:fromValue withToValue:toValue withRepeatCount:repeatCount withDuration:duration]
//    }
//    
//    + (void)animationLayer:(CALayer *)aLayer positionCenterWithFromValue:(NSValue *)fromValue withToValue:(NSValue *)toValue withRepeatCount:(NSInteger)repeatCount withDuration:(NSTimeInterval)duration
//    {
//    return [CUCFQuickUIUitil animationLayer:aLayer path:@"position" withFromValue:fromValue withToValue:toValue withRepeatCount:repeatCount withDuration:duration]
//    }
//    
//    + (void)animationLayer:(CALayer *)aLayer path:(NSString *)path withFromValue:(NSValue *)fromValue withToValue:(NSValue *)toValue withRepeatCount:(NSInteger)repeatCount withDuration:(NSTimeInterval)duration
//    {
//    if (CUCFCheckObjectNull(aLayer) || CUCFStringIsNull(path) || CUCFCheckKeyValueHasNull(fromValue, toValue)) {
//    return
//    }
//    
//    CABasicAnimation *basicAnimation = [CABasicAnimation animationWithKeyPath:path]
//    basicAnimation.fromValue = fromValue
//    basicAnimation.toValue = toValue
//    basicAnimation.duration = duration
//    basicAnimation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut]
//    basicAnimation.beginTime = 0
//    basicAnimation.autoreverses = YES
//    basicAnimation.repeatCount = repeatCount
//    
//    [aLayer addAnimation:basicAnimation forKey:[NSString stringWithFormat:@"cucf_animation_%@",path]]
//    }
//    
//    + (void)animationLayer:(CALayer *)aLayer positionXByValue:(NSValue *)value withRepeatCount:(NSInteger)repeatCount withDuration:(NSTimeInterval)duration
//    {
//    return [CUCFQuickUIUitil animationLayer:aLayer path:@"position.x" ByValue:value withRepeatCount:repeatCount withDuration:duration]
//    }
//    
//    + (void)animationLayer:(CALayer *)aLayer positionYByValue:(NSValue *)value  withRepeatCount:(NSInteger)repeatCount  withDuration:(NSTimeInterval)duration
//    {
//    return [CUCFQuickUIUitil animationLayer:aLayer path:@"position.y" ByValue:value withRepeatCount:repeatCount withDuration:duration]
//    }
//    
//    + (void)animationLayer:(CALayer *)aLayer positionCenterByValue:(NSValue *)value  withRepeatCount:(NSInteger)repeatCount  withDuration:(NSTimeInterval)duration
//    {
//    return [CUCFQuickUIUitil animationLayer:aLayer path:@"position" ByValue:value withRepeatCount:repeatCount withDuration:duration]
//    }
//    
//    + (void)animationLayer:(CALayer *)aLayer path:(NSString *)path ByValue:(NSValue *)value  withRepeatCount:(NSInteger)repeatCount  withDuration:(NSTimeInterval)duration
//    {
//    if (CUCFCheckObjectNull(aLayer) || CUCFStringIsNull(path) || CUCFCheckObjectNull(value)) {
//    return
//    }
//    
//    CABasicAnimation *basicAnimation = [CABasicAnimation animationWithKeyPath:path]
//    basicAnimation.byValue = value
//    basicAnimation.duration = duration
//    basicAnimation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut]
//    basicAnimation.beginTime = 0
//    basicAnimation.autoreverses = YES
//    basicAnimation.repeatCount = repeatCount
//    
//    [aLayer addAnimation:basicAnimation forKey:[NSString stringWithFormat:@"cucf_animation_by_%@",path]]
//    
//    }
//    
//    #pragma mark - 获取圆角图片
//    
//    // Returns true if the image has an alpha layer
//    + (BOOL)hasAlpha:(UIImage *)aImage {
//    CGImageAlphaInfo alpha = CGImageGetAlphaInfo(aImage.CGImage)
//    return (alpha == kCGImageAlphaFirst ||
//    alpha == kCGImageAlphaLast ||
//    alpha == kCGImageAlphaPremultipliedFirst ||
//    alpha == kCGImageAlphaPremultipliedLast)
//    }
//    
//    // Returns a copy of the given image, adding an alpha channel if it doesn't already have one
//    + (UIImage *)imageWithAlpha:(UIImage *)aImage {
//    if ([CUCFQuickUIUitil hasAlpha:aImage]) {
//    return aImage
//    }
//    
//    CGFloat scale = MAX(aImage.scale, 1.0f)
//    CGImageRef imageRef = aImage.CGImage
//    size_t width = CGImageGetWidth(imageRef)*scale
//    size_t height = CGImageGetHeight(imageRef)*scale
//    
//    // The bitsPerComponent and bitmapInfo values are hard-coded to prevent an "unsupported parameter combination" error
//    CGContextRef offscreenContext = CGBitmapContextCreate(NULL,
//    width,
//    height,
//    8,
//    0,
//    CGImageGetColorSpace(imageRef),
//    kCGBitmapByteOrderDefault | kCGImageAlphaPremultipliedFirst)
//    
//    // Draw the image into the context and retrieve the new image, which will now have an alpha layer
//    CGContextDrawImage(offscreenContext, CGRectMake(0, 0, width, height), imageRef)
//    CGImageRef imageRefWithAlpha = CGBitmapContextCreateImage(offscreenContext)
//    UIImage *imageWithAlpha = [UIImage imageWithCGImage:imageRefWithAlpha scale:aImage.scale orientation:UIImageOrientationUp]
//    
//    // Clean up
//    CGContextRelease(offscreenContext)
//    CGImageRelease(imageRefWithAlpha)
//    
//    return imageWithAlpha
//    }
//    
//    // Creates a copy of this image with rounded corners
//    // If borderSize is non-zero, a transparent border of the given size will also be added
//    // Original author: Björn Sållarp. Used with permission. See: http://blog.sallarp.com/iphone-uiimage-round-corners/
//    + (UIImage *)roundedCornerImage:(UIImage *)aImage cornerSize:(NSInteger)cornerSize borderSize:(NSInteger)borderSize {
//    // If the image does not have an alpha layer, add one
//    UIImage *image = [CUCFQuickUIUitil imageWithAlpha:aImage]
//    
//    CGFloat scale = MAX(aImage.scale,1.0f)
//    NSUInteger scaledBorderSize = borderSize * scale
//    
//    // Build a context that's the same dimensions as the new size
//    CGContextRef context = CGBitmapContextCreate(NULL,
//    image.size.width*scale,
//    image.size.height*scale,
//    CGImageGetBitsPerComponent(image.CGImage),
//    0,
//    CGImageGetColorSpace(image.CGImage),
//    CGImageGetBitmapInfo(image.CGImage))
//    
//    // Create a clipping path with rounded corners
//    
//    CGContextBeginPath(context)
//    [CUCFQuickUIUitil addRoundedRectToPath:CGRectMake(scaledBorderSize, scaledBorderSize, image.size.width*scale - borderSize * 2, image.size.height*scale - borderSize * 2)
//    context:context
//    ovalWidth:cornerSize*scale
//    ovalHeight:cornerSize*scale]
//    CGContextClosePath(context)
//    CGContextClip(context)
//    
//    // Draw the image to the context; the clipping path will make anything outside the rounded rect transparent
//    CGContextDrawImage(context, CGRectMake(0, 0, image.size.width*scale, image.size.height*scale), image.CGImage)
//    
//    // Create a CGImage from the context
//    CGImageRef clippedImage = CGBitmapContextCreateImage(context)
//    CGContextRelease(context)
//    
//    // Create a UIImage from the CGImage
//    UIImage *roundedImage = [UIImage imageWithCGImage:clippedImage scale:aImage.scale orientation:UIImageOrientationUp]
//    
//    CGImageRelease(clippedImage)
//    
//    return roundedImage
//    }
//    
//    #pragma mark -
//    #pragma mark Private helper methods
//    
//    // Adds a rectangular path to the given context and rounds its corners by the given extents
//    // Original author: Björn Sållarp. Used with permission. See: http://blog.sallarp.com/iphone-uiimage-round-corners/
//    + (void)addRoundedRectToPath:(CGRect)rect context:(CGContextRef)context ovalWidth:(CGFloat)ovalWidth ovalHeight:(CGFloat)ovalHeight {
//    if (ovalWidth == 0 || ovalHeight == 0) {
//    CGContextAddRect(context, rect)
//    return
//    }
//    CGContextSaveGState(context)
//    CGContextTranslateCTM(context, CGRectGetMinX(rect), CGRectGetMinY(rect))
//    CGContextScaleCTM(context, ovalWidth, ovalHeight)
//    CGFloat fw = CGRectGetWidth(rect) / ovalWidth
//    CGFloat fh = CGRectGetHeight(rect) / ovalHeight
//    CGContextMoveToPoint(context, fw, fh/2)
//    CGContextAddArcToPoint(context, fw, fh, fw/2, fh, 1)
//    CGContextAddArcToPoint(context, 0, fh, 0, fh/2, 1)
//    CGContextAddArcToPoint(context, 0, 0, fw/2, 0, 1)
//    CGContextAddArcToPoint(context, fw, 0, fw, fh/2, 1)
//    CGContextClosePath(context)
//    CGContextRestoreGState(context)
//    }
//    
//    + (UIImage *)createRoundCornerImage:(UIImage *)aImage withCornerSize:(NSInteger)cornerSize withBoardSize:(NSInteger)boardSize
//    {
//    if (!aImage) {
//    return nil
//    }
//    return [CUCFQuickUIUitil roundedCornerImage:aImage cornerSize:cornerSize borderSize:boardSize]
//    }
    
}

public let SharedQuickUIUtil: QuickUIUtil = QuickUIUtil.share

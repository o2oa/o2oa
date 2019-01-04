//
//  LXDScanCodeController.h
//  LXDScanQRCode
//
//  Created by 林欣达 on 15/10/14.
//  Copyright © 2015年 cnpayany. All rights reserved.
//

#import <UIKit/UIKit.h>

@class LXDScanCodeController;
@protocol LXDScanCodeControllerDelegate <NSObject>

- (void)scanCodeController: (LXDScanCodeController *)scanCodeController codeInfo: (NSString *)codeInfo;

@end

/*!
 *  @class
 *
 *  @abstract
 *  二维码/条形码扫描控制器
 */
@interface LXDScanCodeController : UIViewController

/*! 扫描回调代理人*/
@property (nonatomic, weak) id<LXDScanCodeControllerDelegate> scanDelegate;

/*! 扫描构造器*/
+ (instancetype)scanCodeController;

@end

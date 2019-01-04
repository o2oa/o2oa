//
//  MGLicenseHandle.m
//  MGSDKV2Test
//
//  Created by 张英堂 on 16/9/7.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import "MGFaceLicenseHandle.h"
#import "MGFacepp.h"
#import "MGNetAccount.h"


@implementation MGFaceLicenseHandle


+ (BOOL)getLicense{
    NSDate *sdkDate = [self getLicenseDate];
    return [self compareSDKDate:sdkDate];
}


+ (void)licenseForNetwokrFinish:(void(^)(bool License, NSDate *sdkDate))finish {
    

    
    // 检查 apk
    if ([MG_LICENSE_KEY isEqualToString:@""] || [MG_LICENSE_SECRET isEqualToString:@""]) {
        UIAlertController *controller = [UIAlertController alertControllerWithTitle:@"API Key 或 secret 不能为空"
                                                                            message:@"请到官网申请 ‘https://www.faceplusplus.com.cn’"
                                                                     preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction *action = [UIAlertAction actionWithTitle:@"好"
                                                         style:UIAlertActionStyleCancel
                                                       handler:nil];
        [controller addAction:action];
        UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
        UIViewController *currentVC = [MGFaceLicenseHandle getCurrentVCFrom:rootViewController];
        [currentVC presentViewController:controller animated:YES completion:nil];
        
        if (finish) {
            finish(NO, nil);
        }
        return;
    }
    
    NSDate *licenSDKDate = [self getLicenseDate];

    if ([self compareSDKDate:licenSDKDate] == NO) {
        if (finish) {
            finish(YES, [self getLicenseDate]);
        }
        return;
    }
    
    NSString *version = [MGFacepp getSDKVersion];
    NSString *uuid = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
    
    [MGLicenseManager getLicenseWithUUID:uuid
                                 version:version
                                  apiKey:MG_LICENSE_KEY
                               apiSecret:MG_LICENSE_SECRET
                             apiDuration:1
                               URLString:MGLicenseURL_CN
                                  finish:^(bool License, NSError *error) {
                                      if (error) {
                                          NSLog(@"Auth error = %@", error);
                                      }
                                      
                                      if (License) {
                                          NSDate  *nowSDKDate = [self getLicenseDate];
                                          
                                          if (finish) {
                                              finish(License, nowSDKDate);
                                          }
                                      }else{
                                          if (finish) {
                                              finish(License, licenSDKDate);
                                          }
                                      }
                                  }];

}

+ (NSDate *)getLicenseDate {
    NSString *modelPath = [[NSBundle mainBundle] pathForResource:KMGFACEMODELNAME ofType:@""];
    NSData *modelData = [NSData dataWithContentsOfFile:modelPath];
    MGAlgorithmInfo *sdkInfo = [MGFacepp getSDKAlgorithmInfoWithModel:modelData];
    if (sdkInfo.needNetLicense) {
        NSString *version = [MGFacepp getSDKVersion];
        NSDate *date = [MGLicenseManager getExpiretime:version];
        NSLog(@"过期时间 ： %@",date);
        return date;
    } else {
        NSLog(@"SDK 为非联网授权版");
        return sdkInfo.expireDate;
    }
}

+ (BOOL)compareSDKDate:(NSDate *)sdkDate{
    
    NSDate *nowDate = [NSDate date];
    double result = [sdkDate timeIntervalSinceDate:nowDate];

    
    if (result >= 1*1*60*60.0) {
        return NO;
    }
    return YES;
}

+ (BOOL)getNeedNetLicense{
    
    NSString *modelPath = [[NSBundle mainBundle] pathForResource:KMGFACEMODELNAME ofType:@""];
    NSData *modelData = [NSData dataWithContentsOfFile:modelPath];
    
    MGAlgorithmInfo *sdkInfo = [MGFacepp getSDKAlgorithmInfoWithModel:modelData];
    NSLog(@"\n************\nSDK 功能列表: %@\n是否需要联网授权: %d\n版本号:%@\n过期时间:%@ \n************", sdkInfo.SDKAbility, sdkInfo.needNetLicense, sdkInfo.version, sdkInfo.expireDate);
    
    return sdkInfo.needNetLicense;
}

+ (UIViewController *)getCurrentVCFrom:(UIViewController *)rootVC
{
    UIViewController *currentVC;
    
    if ([rootVC presentedViewController]) {
        // 视图是被presented出来的
        rootVC = [rootVC presentedViewController];
    }
    
    if ([rootVC isKindOfClass:[UITabBarController class]]) {
        // 根视图为UITabBarController
        currentVC = [self getCurrentVCFrom:[(UITabBarController *)rootVC selectedViewController]];
    } else if ([rootVC isKindOfClass:[UINavigationController class]]){
        // 根视图为UINavigationController
        currentVC = [self getCurrentVCFrom:[(UINavigationController *)rootVC visibleViewController]];
    } else {
        // 根视图为非导航类
        currentVC = rootVC;
    }
    
    return currentVC;
}


@end

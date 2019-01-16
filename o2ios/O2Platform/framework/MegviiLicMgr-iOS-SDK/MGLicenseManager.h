//
//  MGLicenseManager.h
//  MGBaseKit
//
//  Created by 张英堂 on 16/9/5.
//  Copyright © 2016年 megvii. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MGLicenseCommon.h"

@interface MGLicenseManager : NSObject


/**
 获取过期时间

 @param version SDK 版本号
 @return 过期日期
 */
+ (NSDate *)getExpiretime:(NSString *)version;



#pragma mark - 简单调用联网授权

/**
 联网获取授权信息
 
 @param UUID UUID
 @param version 通过SDK获取
 @param apiKey apiKey
 @param apiSecret apiSecret
 @param duration appKey有效期
 @param url
 @param complete 授权结束回调
 @return SessionTask
 */
+ (NSURLSessionTask *)getLicenseWithUUID:(NSString *)UUID
                                 version:(NSString *)version
                                  apiKey:(NSString *)apiKey
                               apiSecret:(NSString *)apiSecret
                             apiDuration:(NSInteger)duration
                               URLString:(NSString *)url
                                  finish:(void(^)(bool License, NSError *error))complete;



@end

//
//  SZKCleanCache.h
//  CleanCache
//
//  Created by sunzhaokai on 16/5/11.
//  Copyright © 2016年 孙赵凯. All rights reserved.
//

#import <Foundation/Foundation.h>


typedef void(^cleanCacheBlock)();


@interface SZKCleanCache : NSObject

/**
 *  清理缓存
 */
+(void)cleanCache:(cleanCacheBlock)block;

/**
 *  整个缓存目录的大小
 */
+(float)folderSizeAtPath;


@end

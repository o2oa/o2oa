//
//  DLCacheProtocol.h
//  DLSlideViewDemo
//
//  Created by Dongle Su on 15-2-13.
//  Copyright (c) 2015年 dongle. All rights reserved.
//

#ifndef DLSlideViewDemo_DLCacheProtocol_h
#define DLSlideViewDemo_DLCacheProtocol_h
#import <Foundation/Foundation.h>

@protocol DLCacheProtocol <NSObject>
- (void)setObject:(id)object forKey:(NSString *)key;
- (id)objectForKey:(NSString *)key;
@end

#endif

//
//  DLScrollTabbarView.h
//  DLSlideViewDemo
//
//  Created by Dongle Su on 15-2-12.
//  Copyright (c) 2015年 dongle. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DLSlideTabbarProtocol.h"

@interface DLScrollTabbarItem : NSObject
@property(nonatomic, strong) NSString *title;
@property(nonatomic, assign) CGFloat width;
+ (DLScrollTabbarItem *)itemWithTitle:(NSString *)title width:(CGFloat)width;
@end

@interface DLScrollTabbarView : UIView<DLSlideTabbarProtocol>
@property(nonatomic, strong) UIView *backgroundView;

// tabbar属性
@property (nonatomic, strong) UIColor *tabItemNormalColor UI_APPEARANCE_SELECTOR;
@property (nonatomic, strong) UIColor *tabItemSelectedColor UI_APPEARANCE_SELECTOR;
@property (nonatomic, assign) CGFloat tabItemNormalFontSize;
@property(nonatomic, strong) UIColor *trackColor UI_APPEARANCE_SELECTOR;
@property(nonatomic, strong) NSArray *tabbarItems;

// DLSlideTabbarProtocol
@property(nonatomic, assign) NSInteger selectedIndex;
@property(nonatomic, readonly) NSInteger tabbarCount;
@property(nonatomic, weak) id<DLSlideTabbarDelegate> delegate;
- (void)switchingFrom:(NSInteger)fromIndex to:(NSInteger)toIndex percent:(float)percent;

@end

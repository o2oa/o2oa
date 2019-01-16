//
//  DLCustomSlideView.h
//  DLSlideViewDemo
//
//  Created by Dongle Su on 15-2-12.
//  Copyright (c) 2015年 dongle. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "DLSlideTabbarProtocol.h"
#import "DLSlideView.h"
#import "DLTabedSlideView.h"
#import "DLCacheProtocol.h"


@class DLCustomSlideView;

@protocol DLCustomSlideViewDelegate <NSObject>
- (NSInteger)numberOfTabsInDLCustomSlideView:(DLCustomSlideView *)sender;
- (UIViewController *)DLCustomSlideView:(DLCustomSlideView *)sender controllerAt:(NSInteger)index;
@optional
- (void)DLCustomSlideView:(DLCustomSlideView *)sender didSelectedAt:(NSInteger)index;
@end



@interface DLCustomSlideView : UIView<DLSlideTabbarDelegate, DLSlideViewDelegate, DLSlideViewDataSource>
@property(nonatomic, weak) UIViewController *baseViewController;
@property(nonatomic, assign) NSInteger selectedIndex;

// tabbar
@property(nonatomic, strong) UIView<DLSlideTabbarProtocol> *tabbar;
@property(nonatomic, assign) float tabbarBottomSpacing;

// cache properties
@property(nonatomic, strong) id<DLCacheProtocol> cache;

// delegate
@property(nonatomic, weak)IBOutlet id<DLCustomSlideViewDelegate>delegate;

// init method. 初始分方法
- (void)setup;

@end

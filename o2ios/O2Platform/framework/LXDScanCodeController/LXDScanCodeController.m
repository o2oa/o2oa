//
//  LXDScanCodeController.m
//  LXDScanQRCode
//
//  Created by 林欣达 on 15/10/14.
//  Copyright © 2015年 cnpayany. All rights reserved.
//

#import "LXDScanCodeController.h"
#import "LXDScanView.h"

@interface LXDScanCodeController ()<LXDScanViewDelegate>

@property (nonatomic, strong) LXDScanView * scanView;

@end

@implementation LXDScanCodeController


#pragma mark - initial
+ (instancetype)scanCodeController
{
    return [[self alloc] init];
}

- (instancetype)init
{
    if (self = [super init]) {
        self.scanView = [LXDScanView scanViewShowInController: self];
    }
    return self;
}


#pragma mark - life
- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    [self.view addSubview: self.scanView];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear: animated];
    [self.scanView start];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear: animated];
    [self.scanView stop];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)dealloc
{
    [self.scanView stop];
}


#pragma mark - LXDScanCodeController
/**
 *  扫描成功时回调
 */
- (void)scanView:(LXDScanView *)scanView codeInfo:(NSString *)codeInfo
{
    if ([_scanDelegate respondsToSelector: @selector(scanCodeController:codeInfo:)]) {
        [_scanDelegate scanCodeController: self codeInfo: codeInfo];
        [self.navigationController popViewControllerAnimated: YES];
    } else {
        [[NSNotificationCenter defaultCenter] postNotificationName: LXDSuccessScanQRCodeNotification object: self userInfo: @{ LXDScanQRCodeMessageKey: codeInfo }];
    }
}


@end

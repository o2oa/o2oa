//
//  ZLImageScrollView.m
//  ZLImageScrollViewDemo
//
//  Created by Mr.LuDashi on 15/8/20.
//  Copyright (c) 2015年 zeluli. All rights reserved.
//

#import "ZLImageScrollView.h"
@interface ZLImageScrollView()<UIScrollViewDelegate>
@property (nonatomic, strong) UIScrollView *mainScrollView;

@property (nonatomic, strong) UIPageControl *mainPageControl;

@property (nonatomic, assign) CGFloat widthOfView;

@property (nonatomic, assign) CGFloat heightView;

@property (nonatomic, strong) NSArray *imagesNameArray;

@property (nonatomic, strong) NSMutableArray *imageViewsArray;

@property (nonatomic, assign) NSInteger currentPage;

@property (nonatomic, strong) NSTimer *timer;

@property (nonatomic, assign) UIViewContentMode imageViewcontentModel;

@property (nonatomic, strong) UIPageControl *imageViewPageControl;

@property (nonatomic, strong) TapImageViewButtonBlock block;

@property (nonatomic, assign) BOOL isRight;

@end

@implementation ZLImageScrollView

#pragma -- 遍历构造器
+ (instancetype) zlImageScrollViewWithFrame: (CGRect) frame
                                      WithImages: (NSArray *) images{
    ZLImageScrollView *instance = [[ZLImageScrollView alloc] initWithFrame:frame WithImages:images];
    return instance;
}


#pragma -- mark 遍历初始化方法
- (instancetype)initWithFrame: (CGRect)frame
                   WithImages: (NSArray *) images
{
    self = [super initWithFrame:frame];
    if (self) {
        //获取滚动视图的宽度
        _widthOfView = frame.size.width;
        
        //获取滚动视图的高度
        _heightView = frame.size.height;
        
        _scrollInterval = 3;
        
        _animationInterVale = 0.7;
        
        _isRight = YES;
        
        //当前显示页面
        _currentPage = 0;
        
        _imageViewcontentModel = UIViewContentModeScaleAspectFill;
        
        self.clipsToBounds = YES;
        
        _imagesNameArray = images;
        
        //初始化滚动视图
        [self initMainScrollView];
        
        //添加ImageView
        [self addImageviewsForMainScrollWithImageView];
        
        //添加timer
        [self addTimerLoop];
        
        [self addPageControl];
        
    }
    return self;
}


#pragma 添加PageControl
- (void) addPageControl{
    _imageViewPageControl = [[UIPageControl alloc] initWithFrame:CGRectMake(0, _heightView - 20, _widthOfView, 20)];
    
    _imageViewPageControl.numberOfPages = _imagesNameArray.count;
    
    _imageViewPageControl.currentPage = _currentPage - 1;
    
    _imageViewPageControl.currentPageIndicatorTintColor = [UIColor blackColor];
    
    _imageViewPageControl.pageIndicatorTintColor = [UIColor whiteColor];
    
    
    
    [self addSubview:_imageViewPageControl];
}



- (void) addTapEventForImageWithBlock: (TapImageViewButtonBlock) block{
    if (_block == nil) {
        if (block != nil) {
            _block = block;
            
            [self initImageViewButton];
            
        }
    }
}

#pragma -- mark 初始化按钮
- (void) initImageViewButton{
    
    for ( int i = 0; i < _imageViewsArray.count + 1; i ++) {
        
        CGRect currentFrame = CGRectMake(_widthOfView * i, 0, _widthOfView, _heightView);
        
        UIButton *tempButton = [[UIButton alloc] initWithFrame:currentFrame];
        [tempButton addTarget:self action:@selector(tapImageButton:) forControlEvents:UIControlEventTouchUpInside];
        if (i == 0) {
            tempButton.tag = _imageViewsArray.count;
        } else {
            tempButton.tag = i;
        }
        
        [_mainScrollView addSubview:tempButton];
    }
    
}


- (void) tapImageButton: (UIButton *) sender{
    if (_block) {
        _block(_currentPage + 1);
    }
}


#pragma -- mark 初始化ScrollView
- (void) initMainScrollView{
    
    _mainScrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, _widthOfView, _heightView)];
    
    _mainScrollView.contentSize = CGSizeMake(_widthOfView, _heightView);
    
    _mainScrollView.pagingEnabled = YES;
    
    _mainScrollView.showsHorizontalScrollIndicator = NO;
    
    _mainScrollView.showsVerticalScrollIndicator = NO;
    
    _mainScrollView.delegate = self;
    
    [self addSubview:_mainScrollView];
}

#pragma -- mark 给ScrollView添加ImageView 3个ImageView
-(void) addImageviewsForMainScrollWithImageView{
    //设置ContentSize
    _mainScrollView.contentSize = CGSizeMake(_widthOfView * 2, _heightView);
    
    _imageViewsArray = [[NSMutableArray alloc] initWithCapacity:2];
 
    
    for ( int i = 0; i < 2; i ++) {
        
        CGRect currentFrame = CGRectMake(_widthOfView * i, 0, _widthOfView, _heightView);
        
        UIImageView *tempImageView = [[UIImageView alloc] initWithFrame:currentFrame];
        
        tempImageView.contentMode = _imageViewcontentModel;
        
        tempImageView.clipsToBounds = YES;
        
        [_mainScrollView addSubview:tempImageView];
        
        [_imageViewsArray addObject:tempImageView];
    }
    
    UIImageView *tempImageView = _imageViewsArray[0];
    [tempImageView setImage:[UIImage imageNamed:_imagesNameArray[0]]];
    
}

- (void) addTimerLoop{
    
    self.translatesAutoresizingMaskIntoConstraints = NO;
    
    if (_timer == nil) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:_scrollInterval target:self selector:@selector(changeOffset) userInfo:nil repeats:YES];
    }
}

-(void) changeOffset{
    
    //获取ScrollView的offset.x
    
    _currentPage ++;
    
    //如果是最后一个图片，让其成为第一个
    if (_currentPage >= _imagesNameArray.count) {
        _currentPage = 0;
    }
    
    //将要显示的视图
    if(_currentPage < _imagesNameArray.count){
        UIImageView *tempImageView = _imageViewsArray[1] ;
        [tempImageView setImage:[UIImage imageNamed:_imagesNameArray[_currentPage]]];
    }
    
    
    
    [UIView animateWithDuration:_animationInterVale animations:^{
        
        if(_isRight){
            _mainScrollView.contentOffset = CGPointMake(_widthOfView, 0);
        } else {
            _mainScrollView.contentOffset = CGPointMake(-_widthOfView, 0);
        }
        
    } completion:^(BOOL finished) {
        //说明是用的第二个ImageView
        if (_currentPage < _imagesNameArray.count) {
            
            _mainScrollView.contentOffset = CGPointMake(0, 0);
            UIImageView *tempImageView = _imageViewsArray[0] ;
            [tempImageView setImage:[UIImage imageNamed:_imagesNameArray[_currentPage]]];
    
        }

       
        
    }];
    
    _imageViewPageControl.currentPage = _currentPage;
    
}


-(void) scrollViewDidScroll:(UIScrollView *)scrollView{
    CGFloat offsetx = scrollView.contentOffset.x - 0;
    if (offsetx > 3) {
        [self LoopRightWithBool:YES];
        return;
    }
    
    if (offsetx < -3) {
        [self LoopRightWithBool:NO];
        return;
    }
}

-(void) scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    _currentPage ++;
    
    //如果是最后一个图片，让其成为第一个
    if (_currentPage >= _imagesNameArray.count) {
        _currentPage = 0;
    }
    
    //将要显示的视图
    if(_currentPage < _imagesNameArray.count){
        UIImageView *tempImageView = _imageViewsArray[1] ;
        [tempImageView setImage:[UIImage imageNamed:_imagesNameArray[_currentPage]]];
    }
    

}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
        //说明是用的第二个ImageView
        if (_currentPage < _imagesNameArray.count) {
            
            _mainScrollView.contentOffset = CGPointMake(0, 0);
            UIImageView *tempImageView = _imageViewsArray[0] ;
            [tempImageView setImage:[UIImage imageNamed:_imagesNameArray[_currentPage]]];
            
        }
    
    
    _imageViewPageControl.currentPage = _currentPage;

    [self resumeTimer];
    
    
}

#pragma 暂停定时器
-(void)resumeTimer{
    
    if (![_timer isValid]) {
        return ;
    }
    
    [_timer setFireDate:[NSDate dateWithTimeIntervalSinceNow:_scrollInterval-_animationInterVale]];
    
}

#pragma 改变方向
- (void) LoopRightWithBool: (BOOL) isRight{
    _isRight =isRight;
    UIImageView *secondImageView = _imageViewsArray[1];
    
    if (isRight) {
        secondImageView.frame = CGRectMake(_widthOfView, 0, _widthOfView, _heightView);
    } else {
        secondImageView.frame = CGRectMake(-_widthOfView, 0, _widthOfView, _heightView);
    }
}



@end

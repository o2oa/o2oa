//
//  LDXScore.m
//  LDXScore
//
//  Created by Leen on 14-10-30.
//  Copyright (c) 2014年 Hillsun Cloud Commerce Technology Co. Ltd. All rights reserved.
//

#import "LDXScore.h"


@interface LDXScore()
{   
    CGFloat touchWidth;
    CGFloat starWidth;
    CGFloat toppading;
}

@end

@implementation LDXScore


- (void)awakeFromNib
{
    [self setup];
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        [self setup];
    }
    return self;
}

-(void)setup{
    if(_max_star<1)
        _max_star = 5;
    
    self.backgroundColor = [UIColor clearColor];
    
    
    if(_space<1){
        _space = 2.f;
    }
    
    UITapGestureRecognizer* singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleSingleTap:)];
    [self addGestureRecognizer:singleTap];
    __weak id delegateSelf = self;
    singleTap.delegate = delegateSelf;
    singleTap.cancelsTouchesInView = NO;
}


- (void)handleSingleTap:(id)sender
{
    if (_isSelect) {
        CGPoint pt = [sender locationInView:self];
        if (pt.x > _padding) {
            _show_star = (pt.x - _padding) / (starWidth + _space) + 1; //计算点击的百分比
            [self setNeedsDisplay];
        }
    }
}

-(void)layoutSubviews{
    
    starWidth = (self.frame.size.width - 20.f - (_max_star-1)*_space)/_max_star;
    starWidth = (starWidth > CGRectGetHeight(self.bounds))?CGRectGetHeight(self.bounds):starWidth;
    toppading = (self.bounds.size.height - starWidth)/2;
}

-(void)setIsSelect:(BOOL)isSelect{
    
    _isSelect = isSelect;
}

- (void)drawRect:(CGRect)rect {
    // Drawing code
    for (NSInteger i = 0; i<_max_star; i++) {
        if (i<_show_star) {
            [_highlightImg drawInRect:CGRectMake(_padding + (starWidth + _space)*i, toppading, starWidth, starWidth)];
        }
        else{
            [_normalImg drawInRect:CGRectMake(_padding + (starWidth + _space)*i, toppading, starWidth, starWidth)];
        }
    }
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event 
{
    if (_isSelect) {
        CGPoint pt = [[touches anyObject] locationInView:self];
        
        if (pt.x > _padding) {
            _show_star = (pt.x - _padding) / (starWidth + _space) + 1; //计算点击的百分比
            [self setNeedsDisplay];
        }
    }
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event 
{
    if (_isSelect) {
        CGPoint pt = [[touches anyObject] locationInView:self];
        
        if (pt.x > _padding) {
            _show_star = (pt.x - _padding) / (starWidth + _space) + 1; //计算点击的百分比
            [self setNeedsDisplay];
        }
    }
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event 
{

}


- (void)setShow_star:(NSInteger)show_star
{
    _show_star = show_star;
    [self setNeedsDisplay];
}

- (void)setMax_star:(NSInteger)max_star
{
    _max_star = max_star;
    starWidth = (self.frame.size.width - 20.f - (_max_star-1)*_space)/_max_star;
    [self setNeedsDisplay];
}

- (void)setSpace:(CGFloat)space
{
    _space = space;
    starWidth = (self.frame.size.width - 20.f - (_max_star-1)*_space)/_max_star;
    [self setNeedsDisplay];
}

- (void)setNormalImg:(UIImage *)normalImg
{
    _normalImg = normalImg;
    [self setNeedsDisplay];
}

- (void)setHighlightImg:(UIImage *)highlightImg
{
    _highlightImg = highlightImg;
    [self setNeedsDisplay];
}

@end

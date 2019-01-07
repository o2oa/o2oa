//
//  IGLDropDownMenu.m
//  IGLDropDownMenuDemo
//
//  Created by Galvin Li on 8/30/14.
//  Copyright (c) 2014 Galvin Li. All rights reserved.
//

#import "IGLDropDownMenu.h"

#ifdef NSFoundationVersionNumber_iOS_6_1
#define IOS7_OR_GREATER (floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_6_1)
#else
#define IOS7_OR_GREATER NO
#endif

@interface IGLDropDownMenu ()

@property (nonatomic, strong) IGLDropDownItem *menuButton;
@property (nonatomic, assign) CGFloat offsetX;
@property (nonatomic, assign) NSInteger selectedIndex;
@property (nonatomic, assign) CGRect oldFrame;
@property (nonatomic, assign) CGRect originalFrame;
@property (nonatomic, copy) void (^selectedItemChangeBlock)(NSInteger selectedIndex);

@end

@implementation IGLDropDownMenu

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
    }
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self commonInit];
    }
    return self;
}

- (void)commonInit
{
    self.originalFrame = self.frame;
    [self resetParams];
    self.menuButton = [[IGLDropDownItem alloc] init];
}

- (void)setFrame:(CGRect)frame
{
    super.frame = frame;
    self.originalFrame = frame;
}

- (void)setExpanding:(BOOL)expanding
{
    _expanding = expanding;
    
    [self updateView];
}

- (void)setDropDownItems:(NSArray *)dropDownItems
{
    _dropDownItems = [[NSArray alloc] initWithArray:dropDownItems copyItems:YES];
}

- (CGFloat)alphaOnFold
{
    if (_alphaOnFold != -1) {
        return _alphaOnFold;
    }
    if ([self isSlidingInType]) {
        return 0.0;
    }
    return 1.0;
}

- (void)selectItemAtIndex:(NSUInteger)index
{
    if (index < self.dropDownItems.count) {
        [self selectChangeToItem:self.dropDownItems[index]];
    }
}

- (void)resetParams
{
    super.frame = self.oldFrame;
    self.offsetX = 0;
    
    self.animationDuration = 0.3;
    self.animationOption = UIViewAnimationOptionCurveEaseOut | UIViewAnimationOptionBeginFromCurrentState;
    self.itemAnimationDelay = 0.0;
    self.direction = IGLDropDownMenuDirectionDown;
    self.rotate = IGLDropDownMenuRotateNone;
    self.type = IGLDropDownMenuTypeNormal;
    self.slidingInOffset = -1;
    self.gutterY = 0;
    self.alphaOnFold = -1;
    self.flipWhenToggleView = NO;
    _expanding = NO;
    self.useSpringAnimation = YES;
    
    self.selectedIndex = -1;
}

- (void)reloadView
{
    if (self.isExpanding) {
        super.frame = self.oldFrame;
    } else {
        self.oldFrame = self.frame;
    }
    self.itemSize = self.frame.size;
    // clear all subviews
    for (UIView *view in [self subviews]) {
        [view removeFromSuperview];
    }
    
    if (self.rotate == IGLDropDownMenuRotateLeft) {
        self.offsetX = self.dropDownItems.count * self.dropDownItems.count * self.itemSize.height / 28;
    }
    
    [super setFrame:CGRectMake(self.originalFrame.origin.x - self.offsetX, self.originalFrame.origin.y, self.frame.size.width + self.gutterY, self.frame.size.height)];
    
    self.menuButton.iconImage = self.menuIconImage;
    self.menuButton.text = self.menuText;
    self.menuButton.paddingLeft = self.paddingLeft;
    [self.menuButton setFrame:CGRectMake(self.offsetX + 0, 0, self.itemSize.width, self.itemSize.height)];
    switch (self.direction) {
        case IGLDropDownMenuDirectionDown:
            self.menuButton.layer.anchorPoint = CGPointMake(0.5, 0);
            break;
        case IGLDropDownMenuDirectionUp:
            self.menuButton.layer.anchorPoint = CGPointMake(0.5, 1);
            break;
        default:
            break;
    }
    [self.menuButton addTarget:self action:@selector(toggleView) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:self.menuButton];
    
    for (int i = (int)self.dropDownItems.count - 1; i >= 0; i--) {
        IGLDropDownItem *item = self.dropDownItems[i];
        item.index = i;
        item.paddingLeft = self.paddingLeft;
        [item addTarget:self action:@selector(itemClicked:) forControlEvents:UIControlEventTouchUpInside];
        [self setUpFoldItem:item];
        [self insertSubview:item belowSubview:self.menuButton];
    }
    
    [self updateSelfFrame];
    
}

- (BOOL)isSlidingInType
{
    switch (self.type) {
        case IGLDropDownMenuTypeNormal:
        case IGLDropDownMenuTypeStack:
            return NO;
        case IGLDropDownMenuTypeSlidingInBoth:
        case IGLDropDownMenuTypeSlidingInFromLeft:
        case IGLDropDownMenuTypeSlidingInFromRight:
            return YES;
        default:
            return NO;
    }
}

- (void)updateSelfFrame
{
    CGFloat buttonHeight = CGRectGetHeight(self.menuButton.frame);
    
    CGFloat x = self.frame.origin.x;
    CGFloat y = self.originalFrame.origin.y;
    CGFloat height = buttonHeight;
    CGFloat width = CGRectGetWidth(self.menuButton.frame);
    if (self.isExpanding) {
        for (IGLDropDownItem *item in self.dropDownItems) {
            if (item.alpha > 0) {
                width = MAX(width, CGRectGetMaxX(item.frame));
                switch (self.direction) {
                    case IGLDropDownMenuDirectionDown:
                        height = MAX(height, CGRectGetMaxY(item.frame));
                        break;
                    case IGLDropDownMenuDirectionUp:
                        height = MAX(height, -CGRectGetMinY(item.frame));
                        break;
                    default:
                        break;
                }
                
            }
        }
    }
    
    [self.menuButton setFrame:CGRectMake(self.offsetX + 0, 0, self.itemSize.width, self.itemSize.height)];

    if (self.direction == IGLDropDownMenuDirectionUp) {
        if (self.isExpanding) {
            height += buttonHeight;
        }
        y -= height - buttonHeight;
        [self.menuButton setFrame:CGRectMake(self.offsetX + 0, height - buttonHeight, self.itemSize.width, self.itemSize.height)];
        
        for (IGLDropDownItem *item in self.dropDownItems) {
            if (self.isExpanding) {
                item.center = CGPointMake(item.center.x, height - buttonHeight + item.center.y);
            } else {
                item.center = CGPointMake(item.center.x, item.center.y - self.frame.size.height + buttonHeight);
            }
        }
        
    }
    
    [super setFrame:CGRectMake(x, y, width, height)];
}

- (void)toggleView
{
    self.expanding = !self.isExpanding;
}

- (void)updateView
{
    if (self.shouldFlipWhenToggleView) {
        [self flipMainButton];
    }
    
    if (self.isExpanding) {
        [self expandView];
    } else {
        [self foldView];
    }
    
}

- (void)expandView
{
    // expand the view
    
    for (int i = (int)self.dropDownItems.count - 1; i >= 0; i--) {
        IGLDropDownItem *item = self.dropDownItems[i];
        CGFloat delay = 0;
        // item expand after the main button flip up
        if (self.shouldFlipWhenToggleView) {
            delay += 0.1;
        }
        if ([self isSlidingInType]) {
            // first item move first
            delay += self.itemAnimationDelay * i;
        } else {
            // last item move first
            delay += self.itemAnimationDelay * (self.dropDownItems.count - i - 1);
        }
        
        if (self.shouldUseSpringAnimation && IOS7_OR_GREATER) {
#if (defined(__IPHONE_OS_VERSION_MAX_ALLOWED) && __IPHONE_OS_VERSION_MAX_ALLOWED >= 70000)
            [UIView animateWithDuration:self.animationDuration * 2 delay:delay usingSpringWithDamping:0.5 initialSpringVelocity:2.0 options:self.animationOption animations:^{
                [self setUpExpandItem:item];
            } completion:^(BOOL finished) {
                if (i == 0) {
                    [self updateSelfFrame];
                }
            }];
#endif
        } else {
            [UIView animateWithDuration:self.animationDuration delay:delay options:self.animationOption animations:^{
                [self setUpExpandItem:item];
            } completion:^(BOOL finished) {
                if (i == 0) {
                    [self updateSelfFrame];
                }
            }];
        }
        
        
    }
    
}

- (void)foldView
{
    // fold the view
    
    for (int i = (int)self.dropDownItems.count - 1; i >= 0; i--) {
        IGLDropDownItem *item = self.dropDownItems[i];
        CGFloat delay = 0;
        // item fold after the main button flip up
        if (self.shouldFlipWhenToggleView) {
            delay += 0.1;
        }
        if ([self isSlidingInType]) {
            // last item move first
            delay += self.itemAnimationDelay * (self.dropDownItems.count - i - 1);
        } else {
            // first item move first
            delay += self.itemAnimationDelay * i;
        }
        
        if (self.shouldUseSpringAnimation && IOS7_OR_GREATER) {
#if (defined(__IPHONE_OS_VERSION_MAX_ALLOWED) && __IPHONE_OS_VERSION_MAX_ALLOWED >= 70000)
            [UIView animateWithDuration:self.animationDuration delay:delay usingSpringWithDamping:1.0 initialSpringVelocity:2.0 options:self.animationOption animations:^{
                [self setUpFoldItem:item];
            } completion:^(BOOL finished) {
                if (i == 0) {
                    [self updateSelfFrame];
                }
            }];
#endif
        } else {
            [UIView animateWithDuration:self.animationDuration delay:delay options:self.animationOption animations:^{
                [self setUpFoldItem:item];
            } completion:^(BOOL finished) {
                if (i == 0) {
                    [self updateSelfFrame];
                }
            }];
        }
        
    }
}

- (void)setUpExpandItem:(IGLDropDownItem*)item
{
    // set alpha for slidingIn
    item.alpha = 1.0;
    
    // set frame (MUST before rotation reset)
    [item setFrame:[self frameOnExpandForItemAtIndex:item.index]];
    
    // set rotate
    item.transform = [self transformOnExpandForItemAtIndex:item.index];
}

- (void)setUpFoldItem:(IGLDropDownItem*)item
{
    // reset rotate
    item.transform = CGAffineTransformMakeRotation(0);
    
    // set frame (MUST after rotation reset)
    [item setFrame:[self frameOnFoldForItemAtIndex:item.index]];
    
    // set alpha for slidingIn
    item.alpha = self.alphaOnFold;
}

- (void)flipMainButton
{
    CGFloat flipAxis = 1;
    if (self.direction == IGLDropDownMenuDirectionUp) {
        flipAxis = -1;
    }
    
    CABasicAnimation *topAnimation = [CABasicAnimation animationWithKeyPath:@"transform"];
    topAnimation.autoreverses = YES;
    topAnimation.duration = 0.2;
    topAnimation.toValue = [NSValue valueWithCATransform3D:CATransform3DPerspect(CATransform3DMakeRotation(M_PI_2/3*2, flipAxis, 0, 0), CGPointMake(0, 0), 400)];
    [self.menuButton.layer addAnimation:topAnimation forKey:nil];
}

- (CGRect)frameOnFoldForItemAtIndex:(NSInteger)index
{
    CGFloat x = self.offsetX;
    CGFloat y = 0;
    CGFloat width = self.itemSize.width;
    CGFloat height = self.itemSize.height;
    
    NSInteger count = index >= 2 ? 2 : index;
    CGFloat slidingInOffect = self.slidingInOffset != -1 ? self.slidingInOffset : self.itemSize.width / 3;
    
    switch (self.type) {
        case IGLDropDownMenuTypeNormal:
            // just take the default value
            break;
        case IGLDropDownMenuTypeStack:
            x += count * 2;
            y = (count + 1) * 3;
            width -= count * 4;
            break;
        case IGLDropDownMenuTypeSlidingInBoth:
            if (index % 2 != 0) {
                slidingInOffect = -slidingInOffect;
            }
            x = slidingInOffect;
            y = (index + 1) * (height + self.gutterY);
            break;
        case IGLDropDownMenuTypeSlidingInFromLeft:
            x = -slidingInOffect;
            y = (index + 1) * (height + self.gutterY);
            break;
        case IGLDropDownMenuTypeSlidingInFromRight:
            x = slidingInOffect;
            y = (index + 1) * (height + self.gutterY);
            break;
        default:
            break;
    }
    
    if (self.direction == IGLDropDownMenuDirectionUp) {
        if (self.isExpanding) {
            y = -y;
        } else {
            CGFloat buttonHeight = CGRectGetHeight(self.menuButton.frame);
            y = self.frame.size.height - buttonHeight - y;
            NSLog(@"bHeight: %f, height: %f", buttonHeight, self.frame.size.height);
        }
        
    }
    
    return CGRectMake(x, y, width, height);
}

- (CGRect)frameOnExpandForItemAtIndex:(NSInteger)index
{
    CGFloat x = 0;
    CGFloat y = (index + 1) * (self.itemSize.height + self.gutterY);
    CGFloat width = self.itemSize.width;
    CGFloat height = self.itemSize.height;
    
    switch (self.rotate) {
        case IGLDropDownMenuRotateNone:
            // just take the default value
            break;
        case IGLDropDownMenuRotateLeft:
            x = self.offsetX + -index * index * self.itemSize.height / 20.0;
            break;
        case IGLDropDownMenuRotateRight:
            x = index * index * self.itemSize.height / 20.0;
            break;
        case IGLDropDownMenuRotateRandom:
            x = floor([self random0to1] * 11 - 5);
            break;
        default:
            break;
    }
    
    if (self.direction == IGLDropDownMenuDirectionUp) {
        y = -y;
    }
    
    return CGRectMake(x, y, width, height);
}

- (CGAffineTransform)transformOnExpandForItemAtIndex:(NSInteger)index
{
    CGFloat angle = 0;
    switch (self.rotate) {
        case IGLDropDownMenuRotateNone:
            // just take the default value
            break;
        case IGLDropDownMenuRotateLeft:
            angle = 5.0 * index / 180.0 * M_PI;
            break;
        case IGLDropDownMenuRotateRight:
            angle = -5.0 * index / 180.0 * M_PI;
            break;
        case IGLDropDownMenuRotateRandom:
            angle = floor([self random0to1] * 11 - 5) / 180.0 * M_PI;
            break;
        default:
            break;
    }
    if (self.direction == IGLDropDownMenuDirectionUp) {
        angle = -angle;
    }
    return CGAffineTransformMakeRotation(angle);
}

- (void)addSelectedItemChangeBlock:(void (^)(NSInteger))block
{
    self.selectedItemChangeBlock = block;
}

- (void)selectChangeToItem:(IGLDropDownItem*)item
{
    self.menuButton.iconImage = item.iconImage;
    self.object = item.object;
    self.menuButton.text = item.text;
    self.expanding = NO;
    self.selectedIndex = item.index;
    if (self.selectedItemChangeBlock) {
        self.selectedItemChangeBlock(self.selectedIndex);
    }
    if ([self.delegate respondsToSelector:@selector(dropDownMenu:selectedItemAtIndex:)]) {
        [self.delegate dropDownMenu:self selectedItemAtIndex:self.selectedIndex];
    }
}

#pragma mark - button action

- (void)itemClicked:(IGLDropDownItem*)sender
{
    if (![sender.text isEqualToString:@"没有更多了"]) {
        if (self.isExpanding) {
            [self selectChangeToItem:sender];
        }
    }
    
}

#pragma mark -

- (float)random0to1
{
    return [self randomFloatBetween:0.0 andLargerFloat:1.0];
}

- (float)randomFloatBetween:(float)num1 andLargerFloat:(float)num2
{
    int startVal = num1*10000;
    int endVal = num2*10000;
    
    int randomValue = startVal +(arc4random()%(endVal - startVal));
    float a = randomValue;
    
    return(a /10000.0);
}

CATransform3D CATransform3DMakePerspective(CGPoint center, float disZ)
{
    CATransform3D transToCenter = CATransform3DMakeTranslation(-center.x, -center.y, 0);
    CATransform3D transBack = CATransform3DMakeTranslation(center.x, center.y, 0);
    CATransform3D scale = CATransform3DIdentity;
    scale.m34 = -1.0f/disZ;
    return CATransform3DConcat(CATransform3DConcat(transToCenter, scale), transBack);
}

CATransform3D CATransform3DPerspect(CATransform3D t, CGPoint center, float disZ)
{
    return CATransform3DConcat(t, CATransform3DMakePerspective(center, disZ));
}

@end

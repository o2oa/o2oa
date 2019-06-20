//
//  InputView.h
//  TableViewDemo
//
//  Created by BenGang on 14-7-21.
//  Copyright (c) 2014年 BenGang. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UIViewExt.h"
@class InputView;

@protocol InputViewDelegate <NSObject>

@optional
- (void)keyboardWillShow:(InputView *)inputView keyboardHeight:(CGFloat)keyboardHeight animationDuration:(NSTimeInterval)duration animationCurve:(UIViewAnimationCurve)animationCurve;

- (void)keyboardWillHide:(InputView *)inputView keyboardHeight:(CGFloat)keyboardHeight animationDuration:(NSTimeInterval)duration animationCurve:(UIViewAnimationCurve)animationCurve;

- (void)recordButtonDidClick:(UIButton *)button;

- (void)addButtonDidClick:(UIButton *)button;

- (void)publishButtonDidClick:(UIButton *)button;

- (void)textViewHeightDidChange:(CGFloat)height;

@end

@interface InputView : UIView <UITextViewDelegate>
@property (retain, nonatomic) IBOutlet UIImageView *images;

@property (weak, nonatomic) IBOutlet UIButton *recordButton;
@property (weak, nonatomic) IBOutlet UIButton *addButton;
@property (weak, nonatomic) IBOutlet UIButton *publishButton;
@property (weak, nonatomic) IBOutlet UITextView *inputTextView;
@property (weak, nonatomic) id<InputViewDelegate> delegate;


- (IBAction)recordButtonClick:(id)sender;
- (IBAction)addButtonClick:(id)sender;
- (IBAction)publishButtonClick:(id)sender;
- (void)resetInputView;

@end
/*
 - (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
 {
 [self.inputView.inputTextView resignFirstResponder];
 self.backView.hidden = YES;
 
 [UIView animateWithDuration:0.1 animations:^{
 self.inputView.bottom = self.view.height;
 }];
 }
 
 #pragma mark InputViewDelegate
 
 - (void)keyboardWillShow:(InputView *)inputView keyboardHeight:(CGFloat)keyboardHeight animationDuration:(NSTimeInterval)duration
 {
 self.backView.hidden = YES;
 self.keyboardHeight = keyboardHeight;
 [UIView animateWithDuration:duration animations:^{
 self.inputView.bottom = self.view.height - keyboardHeight;
 }];
 }
 
 - (void)keyboardWillHide:(InputView *)inputView keyboardHeight:(CGFloat)keyboardHeight animationDuration:(NSTimeInterval)duration
 {
 self.keyboardHeight = 0;
 self.backView.hidden = YES;
 [UIView animateWithDuration:duration animations:^{
 self.inputView.bottom = self.view.height;
 }];
 if ([self.inputView.inputTextView.text isEqualToString: @""]) {
 self.inputView.inputTextView.textColor = ColorWithRGB(70, 70, 70);
 self.currentSelectedReply.forUserId = [NSString stringWithFormat:@"%d",self.currentPost.userId];
 louzhu = YES;
 }
 }
*/
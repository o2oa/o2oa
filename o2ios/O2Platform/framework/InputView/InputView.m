//
//  InputView.m
//  TableViewDemo
//
//  Created by BenGang on 14-7-21.
//  Copyright (c) 2014年 BenGang. All rights reserved.
//

#import "InputView.h"
#define RGB(r, g, b) [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1]
#define ORINGIN_X(view) view.frame.origin.x
#define ORINGIN_Y(view) view.frame.origin.y
#define SCREEN_WIDTH    [[UIScreen mainScreen] bounds].size.width
#define VIEW_HEIGHT(view)  view.frame.size.height

@implementation InputView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    [_inputTextView setFont:[UIFont fontWithName:@"BauhausITC" size:17.0]];
    return self;
}

- (void)awakeFromNib
{
    self.inputTextView.layer.borderColor = [ RGB(200, 200, 200) CGColor];
    self.images.backgroundColor =  RGB(245, 245, 245);
    [_inputTextView setFrame:CGRectMake(ORINGIN_X(_inputTextView), ORINGIN_Y(_inputTextView), SCREEN_WIDTH-80, VIEW_HEIGHT(_inputTextView))];
  //  [self.inputTextView setRight:SCREEN_WIDTH-60];
//    [self.inputTextView setLeft:0];
//    [self.inputTextView setRight:(SCREEN_WIDTH - VIEW_WIDTH(_publishButton))];
    self.inputTextView.layer.borderWidth = 1.0;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShowNotification:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHideNotification:) name:UIKeyboardWillHideNotification object:nil];
}

- (void)keyboardWillShowNotification:(NSNotification *)notification
{
    /*
    NSDictionary *userInfo = [notification userInfo];
    CGRect keyboardFrame = [[userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    NSValue *animationDuration = [userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey];
    NSTimeInterval timeInterval = 0;
    [animationDuration getValue:&timeInterval];
    
    if ([self.delegate respondsToSelector:@selector(keyboardWillShow:keyboardHeight:animationDuration:)]) {
        [self.delegate keyboardWillShow:self keyboardHeight:keyboardFrame.size.height animationDuration:timeInterval];
    }
    */
    CGRect keyboardEndFrameWindow;
    [[notification.userInfo valueForKey:UIKeyboardFrameEndUserInfoKey] getValue: &keyboardEndFrameWindow];
    
    double keyboardTransitionDuration;
    [[notification.userInfo valueForKey:UIKeyboardAnimationDurationUserInfoKey] getValue:&keyboardTransitionDuration];
    
    UIViewAnimationCurve keyboardTransitionAnimationCurve;
    [[notification.userInfo valueForKey:UIKeyboardAnimationCurveUserInfoKey] getValue:&keyboardTransitionAnimationCurve];
    
    
  //  CGRect keyboardEndFrameView = [self convertRect:keyboardEndFrameWindow fromView:nil];
    // 参数 ：速度  高度，时间
    if ([self.delegate respondsToSelector:@selector(keyboardWillShow:keyboardHeight:animationDuration:animationCurve:)]) {
       
        [self.delegate keyboardWillShow:self keyboardHeight:keyboardEndFrameWindow.size.height animationDuration:keyboardTransitionDuration animationCurve:keyboardTransitionAnimationCurve];
    }
    
}

- (void)keyboardWillHideNotification:(NSNotification *)notification
{
    /*
    NSDictionary *userInfo = [notification userInfo];
    CGRect keyboardFrame = [[userInfo objectForKey:UIKeyboardFrameBeginUserInfoKey] CGRectValue];
    NSValue *animationDuration = [userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey];
    NSTimeInterval timeInterval = 0;
    [animationDuration getValue:&timeInterval];
    if ([self.delegate respondsToSelector:@selector(keyboardWillHide:keyboardHeight:animationDuration:)]) {
        [self.delegate keyboardWillHide:self keyboardHeight:keyboardFrame.size.height animationDuration:timeInterval];
    }
     */
    CGRect keyboardEndFrameWindow;
    [[notification.userInfo valueForKey:UIKeyboardFrameEndUserInfoKey] getValue: &keyboardEndFrameWindow];
    
    double keyboardTransitionDuration;// 获取键盘的速度
    [[notification.userInfo valueForKey:UIKeyboardAnimationDurationUserInfoKey] getValue:&keyboardTransitionDuration];
    
    UIViewAnimationCurve keyboardTransitionAnimationCurve;
    [[notification.userInfo valueForKey:UIKeyboardAnimationCurveUserInfoKey] getValue:&keyboardTransitionAnimationCurve];
    if ([self.delegate respondsToSelector:@selector(keyboardWillHide:keyboardHeight:animationDuration:animationCurve:)]) {
        
        [self.delegate keyboardWillHide:self keyboardHeight:keyboardEndFrameWindow.size.height animationDuration:keyboardTransitionDuration animationCurve:keyboardTransitionAnimationCurve];
    }

}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    NSMutableString *times = [[NSMutableString alloc]initWithFormat:@"%@", textView.text];
    //字符串查找,可以判断字符串中是否有
    if ([times hasPrefix:@"@"]) {
        textView.text = @"";
       // textView.textColor =  RGB(70, 70, 70);
    }
    return YES;
}

- (void)textViewDidChange:(UITextView *)textView
{
    //计算文本的高度
    CGSize constraintSize = CGSizeMake(textView.frame.size.width-16, 60);
    CGRect sizeFrame = CGRectZero;
    
    NSDictionary *attributes = @{NSFontAttributeName:textView.font};
    NSInteger options = NSStringDrawingUsesFontLeading | NSStringDrawingTruncatesLastVisibleLine | NSStringDrawingUsesLineFragmentOrigin;
    sizeFrame = [textView.text boundingRectWithSize:constraintSize options:options attributes:attributes context:NULL];
 
    sizeFrame.size.height += textView.font.lineHeight;
    textView.height = sizeFrame.size.height;
    //重新调整textView的高度
    if ([self.delegate respondsToSelector:@selector(textViewHeightDidChange:)]) {
        [self.delegate textViewHeightDidChange:textView.size.height];
    }
    
}

- (IBAction)recordButtonClick:(id)sender {
    if ([self.delegate respondsToSelector:@selector(recordButtonDidClick:)]) {
        [self.delegate recordButtonDidClick:sender];
    }
}

- (IBAction)addButtonClick:(id)sender {
    if ([self.delegate respondsToSelector:@selector(addButtonDidClick:)]) {
        [self.delegate addButtonDidClick:sender];
    }
}

- (IBAction)publishButtonClick:(id)sender {
    if ([self.delegate respondsToSelector:@selector(publishButtonDidClick:)]) {
        [self.delegate publishButtonDidClick:sender];
    }
}

- (void)resetInputView
{
    self.height = 44;
    self.inputTextView.height = 30;
    [self setNeedsLayout];
    
}
- (void)layoutSubviews
{
    [super layoutSubviews];
    self.addButton.top = self.height/2 - self.addButton.height/2;
    self.recordButton.top = self.height/2 - self.recordButton.height/2;
    self.publishButton.top = self.height/2 - self.publishButton.height/2;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end

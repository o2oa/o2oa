/*-
 * Copyright (c) 2011 Ryota Hayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR(S) ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $FreeBSD$
 */

#import <UIKit/UIKit.h>
#import "HRColorPickerMacros.h"

@class HRColorPickerView;

@protocol HRColorPickerViewControllerDelegate
- (void)setSelectedColor:(UIColor*)color tag:(int)tag;
@end

#define HRColorPickerDelegate HRColorPickerViewControllerDelegate
// Delegateの名前変えました。すみません。

typedef enum {
    HCPCSaveStyleSaveAlways,
    HCPCSaveStyleSaveAndCancel
} HCPCSaveStyle;

@interface HRColorPickerViewController : UIViewController {
    id<HRColorPickerViewControllerDelegate> __weak delegate;
    HRColorPickerView* colorPickerView;
    
    UIColor *_color;
    BOOL _fullColor;
    HCPCSaveStyle _saveStyle;
    
}

@property (nonatomic) int tag;

+ (HRColorPickerViewController *)colorPickerViewControllerWithColor:(UIColor *)color;
+ (HRColorPickerViewController *)cancelableColorPickerViewControllerWithColor:(UIColor *)color;
+ (HRColorPickerViewController *)fullColorPickerViewControllerWithColor:(UIColor *)color;
+ (HRColorPickerViewController *)cancelableFullColorPickerViewControllerWithColor:(UIColor *)color;

/** Initialize controller with selected color. 
 * @param defaultColor selected color
 * @param fullColor If YES, browseable full color. If NO color was limited.
 * @param saveStyle If it's HCPCSaveStyleSaveAlways, save color when self is closing. If it's HCPCSaveStyleSaveAndCancel, shows Cancel and Save button.
 */
- (id)initWithColor:(UIColor*)defaultColor fullColor:(BOOL)fullColor saveStyle:(HCPCSaveStyle)saveStyle;

/** @deprecated use -save: instead of this . */
- (void)saveColor:(id)sender;

- (void)save;
- (void)save:(id)sender;
- (void)cancel:(id)sender;


@property (weak) id<HRColorPickerViewControllerDelegate> delegate;


@end

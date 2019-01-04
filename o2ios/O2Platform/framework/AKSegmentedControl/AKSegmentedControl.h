//
// AKSegmentedControl.h
//
// Copyright (c) 2013 Ali Karagoz (http://alikaragoz.net)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

#import <UIKit/UIKit.h>

/** Behavior of when touching the each buttons. */
typedef NS_ENUM(NSUInteger, AKSegmentedControlMode) {
    /** Upon touching the buttons, they will remain selected. */
    AKSegmentedControlModeSticky,
    
    /** Button like mode. Upon touching the buttons, they will transition from selected to normal mode. */
    AKSegmentedControlModeButton,
    
    /** Multi selectionable. It is possible to select multiple buttons at once. */
    AKSegmentedControlModeMultipleSelectionable,
};

@interface AKSegmentedControl : UIControl

/** Array containing pointers to the `UIButton` of the segmented control. */
@property (nonatomic, strong, readwrite) NSArray *buttonsArray;

/** Image used to cover the background of the whole segmented control. */
@property (nonatomic, strong, readwrite) UIImage *backgroundImage;

/** Image used to represent the vertical separator between each button of the segmented control. */
@property (nonatomic, strong, readwrite) UIImage *separatorImage;

/** list of indexed currently selected. */
@property (nonatomic, strong, readwrite) NSIndexSet *selectedIndexes;

/** Insets of the whole segmented control view. */
@property (nonatomic, assign, readwrite) UIEdgeInsets contentEdgeInsets;

/** Button behavior used upon the user touch.  */
@property (nonatomic, assign, readwrite) AKSegmentedControlMode segmentedControlMode;


/**
 *  Manually selects an index of the segmented control.
 *
 *  @param index Index of the button you want to be selected.
 */
- (void)setSelectedIndex:(NSUInteger)index;

/**
 *  Manually sets the selected indexes when using the `AKSegmentedControlModeMultipleSelectionable` mode.
 *
 *  @param indexSet        Set of the indexes you want to be selected.
 *  @param expandSelection When set this keeps previously selected indexes.
 */
- (void)setSelectedIndexes:(NSIndexSet *)indexSet byExpandingSelection:(BOOL)expandSelection;

-(void)initButtonWithTitleandImage:(NSArray *)buttonTitleandImage;

@end

//
//  UIKit+Theme.swift
//  SwiftTheme
//
//  Created by Gesen on 16/1/22.
//  Copyright © 2016年 Gesen. All rights reserved.
//

import UIKit

@objc public extension UIView
{
    var theme_alpha: ThemeCGFloatPicker? {
        get { return getThemePicker(self, "setAlpha:") as? ThemeCGFloatPicker }
        set { setThemePicker(self, "setAlpha:", newValue) }
    }
    var theme_backgroundColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setBackgroundColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setBackgroundColor:", newValue) }
    }
    var theme_tintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setTintColor:", newValue) }
    }
}
@objc public extension UIApplication
{
    #if os(iOS)
    func theme_setStatusBarStyle(_ picker: ThemeStatusBarStylePicker, animated: Bool) {
        picker.animated = animated
        setThemePicker(self, "setStatusBarStyle:animated:", picker)
    }
    #endif
}
@objc public extension UIBarItem
{
    func theme_setTitleTextAttributes(_ picker: ThemeDictionaryPicker?, forState state: UIControl.State) {
        let statePicker = makeStatePicker(self, "setTitleTextAttributes:forState:", picker, state)
        setThemePicker(self, "setTitleTextAttributes:forState:", statePicker)
    }
}
@objc public extension UIBarButtonItem
{
    var theme_tintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setTintColor:", newValue) }
    }
}
@objc public extension UILabel
{
    var theme_font: ThemeFontPicker? {
        get { return getThemePicker(self, "setFont:") as? ThemeFontPicker }
        set { setThemePicker(self, "setFont:", newValue) }
    }
    var theme_textColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setTextColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setTextColor:", newValue) }
    }
    var theme_highlightedTextColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setHighlightedTextColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setHighlightedTextColor:", newValue) }
    }
    var theme_shadowColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setShadowColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setShadowColor:", newValue) }
    }
}
@objc public extension UINavigationBar
{
    #if os(iOS)
    var theme_barStyle: ThemeBarStylePicker? {
        get { return getThemePicker(self, "setBarStyle:") as? ThemeBarStylePicker }
        set { setThemePicker(self, "setBarStyle:", newValue) }
    }
    #endif
    var theme_barTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setBarTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setBarTintColor:", newValue) }
    }
    var theme_titleTextAttributes: ThemeDictionaryPicker? {
        get { return getThemePicker(self, "setTitleTextAttributes:") as? ThemeDictionaryPicker }
        set { setThemePicker(self, "setTitleTextAttributes:", newValue) }
    }
    var theme_largeTitleTextAttributes: ThemeDictionaryPicker? {
        get { return getThemePicker(self, "setLargeTitleTextAttributes:") as? ThemeDictionaryPicker }
        set { setThemePicker(self, "setLargeTitleTextAttributes:", newValue) }
    }
}
@objc public extension UITabBar
{
    #if os(iOS)
    var theme_barStyle: ThemeBarStylePicker? {
        get { return getThemePicker(self, "setBarStyle:") as? ThemeBarStylePicker }
        set { setThemePicker(self, "setBarStyle:", newValue) }
    }
    #endif
    var theme_barTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setBarTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setBarTintColor:", newValue) }
    }
}
@objc public extension UITableView
{
    var theme_separatorColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setSeparatorColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setSeparatorColor:", newValue) }
    }
    var theme_sectionIndexColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setSectionIndexColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setSectionIndexColor:", newValue) }
    }
    var theme_sectionIndexBackgroundColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setSectionIndexBackgroundColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setSectionIndexBackgroundColor:", newValue) }
    }
}
@objc public extension UITextField
{
    var theme_font: ThemeFontPicker? {
        get { return getThemePicker(self, "setFont:") as? ThemeFontPicker }
        set { setThemePicker(self, "setFont:", newValue) }
    }
    var theme_keyboardAppearance: ThemeKeyboardAppearancePicker? {
        get { return getThemePicker(self, "setKeyboardAppearance:") as? ThemeKeyboardAppearancePicker }
        set { setThemePicker(self, "setKeyboardAppearance:", newValue) }
    }
    var theme_textColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setTextColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setTextColor:", newValue) }
    }
    var theme_placeholderAttributes: ThemeDictionaryPicker? {
        get { return getThemePicker(self, "updatePlaceholderAttributes:") as? ThemeDictionaryPicker }
        set { setThemePicker(self, "updatePlaceholderAttributes:", newValue) }
    }
}
@objc public extension UITextView
{
    var theme_font: ThemeFontPicker? {
        get { return getThemePicker(self, "setFont:") as? ThemeFontPicker }
        set { setThemePicker(self, "setFont:", newValue) }
    }
    var theme_keyboardAppearance: ThemeKeyboardAppearancePicker? {
        get { return getThemePicker(self, "setKeyboardAppearance:") as? ThemeKeyboardAppearancePicker }
        set { setThemePicker(self, "setKeyboardAppearance:", newValue) }
    }
    var theme_textColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setTextColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setTextColor:", newValue) }
    }
}
@objc public extension UISearchBar
{
    #if os(iOS)
    var theme_barStyle: ThemeBarStylePicker? {
        get { return getThemePicker(self, "setBarStyle:") as? ThemeBarStylePicker }
        set { setThemePicker(self, "setBarStyle:", newValue) }
    }
    #endif
    var theme_keyboardAppearance: ThemeKeyboardAppearancePicker? {
        get { return getThemePicker(self, "setKeyboardAppearance:") as? ThemeKeyboardAppearancePicker }
        set { setThemePicker(self, "setKeyboardAppearance:", newValue) }
    }
    var theme_barTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setBarTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setBarTintColor:", newValue) }
    }
}
@objc public extension UIProgressView
{
    var theme_progressTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setProgressTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setProgressTintColor:", newValue) }
    }
    var theme_trackTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setTrackTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setTrackTintColor:", newValue) }
    }
}
@objc public extension UIPageControl
{
    var theme_pageIndicatorTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setPageIndicatorTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setPageIndicatorTintColor:", newValue) }
    }
    var theme_currentPageIndicatorTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setCurrentPageIndicatorTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setCurrentPageIndicatorTintColor:", newValue) }
    }
}
@objc public extension UIImageView
{
    var theme_image: ThemeImagePicker? {
        get { return getThemePicker(self, "setImage:") as? ThemeImagePicker }
        set { setThemePicker(self, "setImage:", newValue) }
    }
}
@objc public extension UIActivityIndicatorView
{
    var theme_activityIndicatorViewStyle: ThemeActivityIndicatorViewStylePicker? {
        get { return getThemePicker(self, "setActivityIndicatorViewStyle:") as? ThemeActivityIndicatorViewStylePicker }
        set { setThemePicker(self, "setActivityIndicatorViewStyle:", newValue) }
    }
}
@objc public extension UIScrollView
{
    var theme_indicatorStyle: ThemeScrollViewIndicatorStylePicker? {
        get { return getThemePicker(self, "setIndicatorStyle:") as? ThemeScrollViewIndicatorStylePicker }
        set { setThemePicker(self, "setIndicatorStyle:", newValue) }
    }
}
@objc public extension UIButton
{
    func theme_setImage(_ picker: ThemeImagePicker?, forState state: UIControl.State) {
        let statePicker = makeStatePicker(self, "setImage:forState:", picker, state)
        setThemePicker(self, "setImage:forState:", statePicker)
    }
    func theme_setBackgroundImage(_ picker: ThemeImagePicker?, forState state: UIControl.State) {
        let statePicker = makeStatePicker(self, "setBackgroundImage:forState:", picker, state)
        setThemePicker(self, "setBackgroundImage:forState:", statePicker)
    }
    func theme_setTitleColor(_ picker: ThemeColorPicker?, forState state: UIControl.State) {
        let statePicker = makeStatePicker(self, "setTitleColor:forState:", picker, state)
        setThemePicker(self, "setTitleColor:forState:", statePicker)
    }
}
@objc public extension CALayer
{
    var theme_backgroundColor: ThemeCGColorPicker? {
        get { return getThemePicker(self, "setBackgroundColor:") as? ThemeCGColorPicker}
        set { setThemePicker(self, "setBackgroundColor:", newValue) }
    }
    var theme_borderWidth: ThemeCGFloatPicker? {
        get { return getThemePicker(self, "setBorderWidth:") as? ThemeCGFloatPicker }
        set { setThemePicker(self, "setBorderWidth:", newValue) }
    }
    var theme_borderColor: ThemeCGColorPicker? {
        get { return getThemePicker(self, "setBorderColor:") as? ThemeCGColorPicker }
        set { setThemePicker(self, "setBorderColor:", newValue) }
    }
    var theme_shadowColor: ThemeCGColorPicker? {
        get { return getThemePicker(self, "setShadowColor:") as? ThemeCGColorPicker }
        set { setThemePicker(self, "setShadowColor:", newValue) }
    }
    var theme_strokeColor: ThemeCGColorPicker? {
        get { return getThemePicker(self, "setStrokeColor:") as? ThemeCGColorPicker }
        set { setThemePicker(self, "setStrokeColor:", newValue) }
    }
    var theme_fillColor: ThemeCGColorPicker?{
        get { return getThemePicker(self, "setFillColor:") as? ThemeCGColorPicker }
        set { setThemePicker(self, "setFillColor:", newValue) }
    }
}

#if os(iOS)
@objc public extension UIToolbar
{
    var theme_barStyle: ThemeBarStylePicker? {
        get { return getThemePicker(self, "setBarStyle:") as? ThemeBarStylePicker }
        set { setThemePicker(self, "setBarStyle:", newValue) }
    }
    var theme_barTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setBarTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setBarTintColor:", newValue) }
    }
}
@objc public extension UISwitch
{
    var theme_onTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setOnTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setOnTintColor:", newValue) }
    }
    var theme_thumbTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setThumbTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setThumbTintColor:", newValue) }
    }
}
@objc public extension UISlider
{
    var theme_thumbTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setThumbTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setThumbTintColor:", newValue) }
    }
    var theme_minimumTrackTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setMinimumTrackTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setMinimumTrackTintColor:", newValue) }
    }
    var theme_maximumTrackTintColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setMaximumTrackTintColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setMaximumTrackTintColor:", newValue) }
    }
}
@objc public extension UIPopoverPresentationController
{
    var theme_backgroundColor: ThemeColorPicker? {
        get { return getThemePicker(self, "setBackgroundColor:") as? ThemeColorPicker }
        set { setThemePicker(self, "setBackgroundColor:", newValue) }
    }
}
@objc public extension UIRefreshControl
{
    var theme_titleAttributes: ThemeDictionaryPicker? {
        get { return getThemePicker(self, "updateTitleAttributes:") as? ThemeDictionaryPicker }
        set { setThemePicker(self, "updateTitleAttributes:", newValue) }
    }
}
#endif

private func getThemePicker(
    _ object : NSObject,
    _ selector : String
) -> ThemePicker? {
    return object.themePickers[selector]
}

private func setThemePicker(
    _ object : NSObject,
    _ selector : String,
    _ picker : ThemePicker?
) {
    object.themePickers[selector] = picker
    object.performThemePicker(selector: selector, picker: picker)
}

private func makeStatePicker(
    _ object : NSObject,
    _ selector : String,
    _ picker : ThemePicker?,
    _ state : UIControl.State
) -> ThemePicker? {
    
    var picker = picker
    
    if let statePicker = object.themePickers[selector] as? ThemeStatePicker {
        picker = statePicker.setPicker(picker, forState: state)
    } else {
        picker = ThemeStatePicker(picker: picker, withState: state)
    }
    return picker
}

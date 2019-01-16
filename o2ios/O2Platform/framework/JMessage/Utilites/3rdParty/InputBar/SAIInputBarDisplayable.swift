//
//  SAIInputBarDisplayable.swift
//  SAIInputBar
//
//  Created by SAGESSE on 8/26/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit


///
/// 显示协议
///
/// NOTE: 目前不支持`UICollectionViewController`和`UITableViewController`
///
public protocol SAIInputBarDisplayable: NSObjectProtocol  {
    var scrollView: SAIInputBarScrollViewType { get }
}
public protocol SAIInputBarScrollViewType: NSObjectProtocol {
    
    var frame: CGRect { get }
    var bounds: CGRect { get }
    
    var contentSize: CGSize { set get }
    var contentOffset: CGPoint { set get }
    
    var contentInset: UIEdgeInsets { set get }
    var scrollIndicatorInsets: UIEdgeInsets { set get }
    
    func layoutSubviews()
}

extension UIScrollView: SAIInputBarScrollViewType {
}

// MARK:

internal extension SAIInputBarDisplayable {
    
    func ib_inputBar(_ inputBar: SAIInputBar, initWithFrame frame: CGRect) {
        
        UIView.performWithoutAnimation { 
            self.ib_extContentSize = CGSize(width: frame.width, height: frame.height)
            self.ib_extContentOffset = CGPoint.zero
        }
    }
    
    func ib_inputBar(_ inputBar: SAIInputBar, showWithFrame frame: CGRect) {
        guard let window = inputBar.window else {
            return
        }
        // If the previous scenario inputViewSet is Empty, forced to update
        if window.value(forKeyPath: "rootViewController.ib_oldInputViewSet.isEmpty") as? Bool ?? true {
            UIView.performWithoutAnimation {
                ib_inputBar(inputBar, didChangeFrame: frame)
            }
        } else {
            ib_inputBar(inputBar, didChangeFrame: frame)
        }
    }
    func ib_inputBar(_ inputBar: SAIInputBar, hideWithFrame frame: CGRect) {
        guard let window = inputBar.window else {
            return
        }
        // If the next scene inputViewSet is Empty, ignore the hidden events
        if window.value(forKeyPath: "rootViewController.ib_newInputViewSet.isEmpty") as? Bool ?? true {
            return
        }
        ib_inputBar(inputBar, didChangeFrame: frame)
    }
    
    func ib_inputBar(_ inputBar: SAIInputBar, didChangeOffset offset: CGPoint) {
       
        guard scrollView.contentOffset.y >= -scrollView.contentInset.top else {
            return
        }
            
        ib_extContentOffset = CGPoint(x: -offset.x, y: -offset.y)
    }
    func ib_inputBar(_ inputBar: SAIInputBar, didChangeFrame frame: CGRect) {
        
        let ty = (frame.height - self.ib_extContentSize.height) + (0 - self.ib_extContentOffset.y)
        guard ty != 0 else {
            return // no change
        }
        
        UIView.animate(withDuration: _SAInputDefaultAnimateDuration) { [scrollView] in
            UIView.setAnimationCurve(_SAInputDefaultAnimateCurve)
            
            let pt = scrollView.contentOffset
            let edg = scrollView.contentInset
            
            self.ib_extContentSize = CGSize(width: frame.width, height: frame.height)
            self.ib_extContentOffset = CGPoint.zero
            
            let ny = pt.y + ty
            let minY = -edg.top
            let maxY = scrollView.contentSize.height - scrollView.frame.height + (edg.bottom + ty)
            
            scrollView.contentOffset = CGPoint(x: pt.x, y: max(max(ny, maxY), minY))
            scrollView.layoutSubviews()
        }
    }
    
    var ib_extContentOffset: CGPoint {
        set { 
            let oldValue = ib_extContentOffset
            guard oldValue != newValue else {
                return
            }
        
            var edg1 = scrollView.contentInset 
            var edg2 = scrollView.scrollIndicatorInsets
            
            edg1.bottom += newValue.y - oldValue.y
            edg2.bottom += newValue.y - oldValue.y
            
            scrollView.contentInset = edg1
            scrollView.scrollIndicatorInsets = edg2
            
            return objc_setAssociatedObject(self, &_SAInputBarDisplayableExtContentOffset, NSValue(cgPoint: newValue), .OBJC_ASSOCIATION_RETAIN)
        }
        get {
            return (objc_getAssociatedObject(self, &_SAInputBarDisplayableExtContentOffset) as? NSValue)?.cgPointValue ?? CGPoint.zero 
        }
    }
    var ib_extContentSize: CGSize {
        set {
            let oldValue = ib_extContentSize
            guard oldValue != newValue else {
                return
            }
            
            var edg1 = scrollView.contentInset 
            var edg2 = scrollView.scrollIndicatorInsets
            
            edg1.bottom += newValue.height - oldValue.height
            edg2.bottom += newValue.height - oldValue.height
            
            scrollView.contentInset = edg1
            scrollView.scrollIndicatorInsets = edg2
            
            return objc_setAssociatedObject(self, &_SAInputBarDisplayableExtContentSize, NSValue(cgSize: newValue), .OBJC_ASSOCIATION_RETAIN)
        }
        get {
            return (objc_getAssociatedObject(self, &_SAInputBarDisplayableExtContentSize) as? NSValue)?.cgSizeValue ?? CGSize.zero
        }
    }
}

private extension UIViewController {
    
    /// 为了解决切换页面时的输入焦点问题
    
    @objc func ib_inputWindowController_setInputViewSet(_ arg1: AnyObject) {
        ib_newInputViewSet = arg1
        ib_inputWindowController_setInputViewSet(arg1)
        ib_oldInputViewSet = arg1
    }
    
    @objc var ib_oldInputViewSet: AnyObject? {
        set { return objc_setAssociatedObject(self, &_SAInputUIInputWindowControllerOldInputViewSet, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC) }
        get { return objc_getAssociatedObject(self, &_SAInputUIInputWindowControllerOldInputViewSet) as AnyObject? }
    }
    @objc var ib_newInputViewSet: AnyObject? {
        set { return objc_setAssociatedObject(self, &_SAInputUIInputWindowControllerNewInputViewSet, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC) }
        get { return objc_getAssociatedObject(self, &_SAInputUIInputWindowControllerNewInputViewSet) as AnyObject? }
    }
}

// MARK: method swizzling

internal func SAIInputBarDisplayableLoad() {
    _SAInputExchangeSelector(NSClassFromString("UIInputWindowController"), "setInputViewSet:", "ib_inputWindowController_setInputViewSet:")
}

private var _SAInputBarDisplayableExtContentSize = "_SAInputBarDisplayableExtContentSize"
private var _SAInputBarDisplayableExtContentOffset = "_SAInputBarDisplayableExtContentOffset"

private var _SAInputUIInputWindowControllerNewInputViewSet = "_SAInputUIInputWindowControllerNewInputViewSet"
private var _SAInputUIInputWindowControllerOldInputViewSet = "_SAInputUIInputWindowControllerOldInputViewSet"

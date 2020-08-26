//
//  O2CanCopyUILabel.swift
//  O2Platform
//
//  Created by FancyLou on 2020/8/26.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

// MARK: - 能够复制的UILabel
class O2CanCopyUILabel: UILabel {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setEvent()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        addLongPressGesture()
    }
    
    //必须实现的两个方法
    override var canBecomeFirstResponder: Bool  {
        return true
    }
    
    override func canPerformAction(_ action: Selector, withSender sender: Any?) -> Bool {
        if action == #selector(copyText) {
            return true
        }
        return false
    }
    
    private func setEvent() {
        addLongPressGesture()
        NotificationCenter.default.addObserver(forName: UIMenuController.willHideMenuNotification, object: nil, queue: nil) { (note) in
            self.backgroundColor = UIColor.white
        }
    }
    
    
    private func addLongPressGesture() {
        self.isUserInteractionEnabled = true
        let longTap = UILongPressGestureRecognizer.init(target: self, action: #selector(longTapRecognizer))
        // 长按手势最小触发时间
        longTap.minimumPressDuration = 1.0
        // 长按手势需要的同时敲击触碰数（手指数）
        longTap.numberOfTouchesRequired = 1
        // 长按有效移动范围（从点击开始，长按移动的允许范围 单位 px
//        longTap.allowableMovement = 15
        self.addGestureRecognizer(longTap)
    }
    
    private func setMenuItems(){
        self.becomeFirstResponder()
        // 如果 Menu 已经被创建那就不再重复创建 menu
        if (UIMenuController.shared.isMenuVisible){
            return
        }
        let item1 = UIMenuItem.init(title: "复制", action: #selector(copyText))
        // 单例的形式获取menu
        let menu = UIMenuController.shared
        // 设置箭头方向
        menu.arrowDirection = .default
        // 设置 Menu 所显示的 items
        menu.menuItems = [item1]
        // 设置添加上 menu 的目标控件的 rect 和目标控件
        menu.setTargetRect(self.frame, in: self.superview!)
        // 令 Menu 可见
        menu.setMenuVisible(true, animated: true)
    }

    
    @objc private func longTapRecognizer(recognizer: UIGestureRecognizer) {
        if recognizer.state == .ended{
            self.backgroundColor = UIColor.white
            return
        }else if recognizer.state == .began{
            self.backgroundColor = UIColor.lightGray
            setMenuItems()
        }
    }
    
    @objc private func copyText() {
        let pboard = UIPasteboard.general
        pboard.string = self.text
    }
    
}

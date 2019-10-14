//
//  O2WorkMoreActionSheet.swift
//  O2Platform
//
//  Created by FancyLou on 2019/5/22.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack


private let keyWindow = UIApplication.shared.keyWindow
class O2WorkMoreActionSheet: UIView, UIGestureRecognizerDelegate {
    
    typealias DidTapButton = (_ item: WorkNewActionItem) -> Void
    
    ////TODO 这些高度还没有适配全面屏。。。
    private let buttonHeight: CGFloat = 44.0
    private let toolbarHeight: CGFloat = 44.0
    private let buttonGap: CGFloat = 1.0
    private let bottomGap: CGFloat = 5.0
    private var toolBar: UIToolbar!
    private var didTap: DidTapButton?
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(moreControls: [WorkNewActionItem], didTapButton: @escaping DidTapButton) {
        super.init(frame: CGRect(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height))
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(close))
        tapGesture.delegate = self
        addGestureRecognizer(tapGesture)
        backgroundColor = .clear
        self.didTap = didTapButton
        //toolbar
        self.toolBar = UIToolbar(frame: CGRect(x: 0, y: self.frame.height - self.buttonHeight, width: self.frame.width, height: self.toolbarHeight))
        self.toolBar.items = self.toolBarItems(moreControls: moreControls)
        self.addSubview(self.toolBar)
       
        moreControls.forEachEnumerated { (index, item) in
            if index > 1 {
                let moreIndex = CGFloat.init(index - 2)
                let y = ( moreIndex * self.buttonHeight + moreIndex * self.buttonGap + self.buttonGap + self.toolbarHeight )
                DDLogDebug("button:\(y)")
                self.generateButton(y: y, item: item)
            }
        }
    }
    
    //打开
    open func show() {
        keyWindow?.addSubview(self)
        keyWindow?.bringSubviewToFront(self)
        self.backgroundColor =  UIColor.black.withAlphaComponent(0.4)
    }
    
    private func toolBarItems(moreControls: [WorkNewActionItem]) -> [UIBarButtonItem] {
        let firstItem = moreControls[0]
        let secondItem = moreControls[1]
        var items: [UIBarButtonItem] = []
        let spaceItem = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: self, action: nil)
        let firstButton = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
        firstButton.setTitle(firstItem.text, for: .normal)
        firstButton.setTitleColor(base_color, for: .normal)
        firstButton.addTapGesture { (tap) in
            self.clickButton(item: firstItem)
        }
        let firstButtonItem = UIBarButtonItem(customView: firstButton)
        items.append(spaceItem)
        items.append(firstButtonItem)
        items.append(spaceItem)
        let secondButton = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
        secondButton.setTitle(secondItem.text, for: .normal)
        secondButton.setTitleColor(base_color, for: .normal)
        secondButton.addTapGesture { (tap) in
            self.clickButton(item: secondItem)
        }
        let secondButtonItem = UIBarButtonItem(customView: secondButton)
        items.append(spaceItem)
        items.append(secondButtonItem)
        items.append(spaceItem)
        let moreButton = UIButton(frame: CGRect(x: 0, y: 0, width: 30, height: 30))
        moreButton.setImage(UIImage(named: "icon_more_s"), for: .normal)
        moreButton.addTapGesture { (tap) in
            self.close()
        }
        let moreButtonItem = UIBarButtonItem(customView: moreButton)
        items.append(moreButtonItem)
        return items
    }
    
    
    //生成按钮
    private func generateButton(y: CGFloat, item:WorkNewActionItem) {
        let button = UIButton(type: UIButton.ButtonType.system)
        let buttonY = self.frame.height - y - self.buttonHeight
        DDLogDebug("button y:\(buttonY)")
        button.frame = CGRect(x: CGFloat(0.0), y: buttonY, width: CGFloat(self.frame.width), height: CGFloat(self.buttonHeight))
        button.setTitle(item.text, for: .normal)
        button.setTitleColor(base_color, for: .normal)
        button.backgroundColor = UIColor.white
        button.addTapGesture { (tap) in
            DDLogDebug("点击了 \(item.text) , action: \(item.action) ， actionScript:\(item.actionScript)")
            self.clickButton(item: item)
        }
        self.addSubview(button)
    }
    
    private func clickButton(item: WorkNewActionItem) {
        self.didTap?(item)
        self.close()
    }
    
    //关闭
    @objc private func close() {
        self.backgroundColor = .clear
        self.removeFromSuperview()
    }
}

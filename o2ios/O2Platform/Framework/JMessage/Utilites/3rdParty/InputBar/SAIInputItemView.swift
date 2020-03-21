//
//  SAIInputItemView.swift
//  SAIInputBar
//
//  Created by SAGESSE on 8/3/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit

internal class SAIInputItemView: UICollectionViewCell {
    
    var item: SAIInputItem? {
        willSet {
            UIView.performWithoutAnimation {
                self._updateItem(newValue)
            }
        }
    }
    
    weak var delegate: SAIInputItemViewDelegate? {
        set { return _button.delegate = newValue }
        get { return _button.delegate }
    }
    
    func setSelected(_ selected: Bool, animated: Bool) {
        _button.setSelected(selected: selected, animated: animated)
    }
    
    func _init() {
        
        clipsToBounds = true
        backgroundColor = .clear
        //backgroundColor = UIColor.greenColor().colorWithAlphaComponent(0.2)
    }
    func _updateItem(_ newValue: SAIInputItem?) {
        
        guard let newValue = newValue else {
            // clear on nil
            _contentView?.removeFromSuperview()
            _contentView = nil
            return
        }
        
        if let customView = newValue.customView {
            // 需要显示自定义视图
            _contentView?.removeFromSuperview()
            _contentView = customView
        } else {
            // 显示普通按钮
            if _contentView !== _button {
                _contentView?.removeFromSuperview()
                _contentView = _button
            }
            // 更新按钮属性
            _button.barItem = newValue
        }
        // 更新视图
        if let view = _contentView, view.superview != contentView {
            view.frame = contentView.bounds
            view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
            contentView.addSubview(view)
        }
    }
    
    private var _contentView: UIView?
    private lazy var _button: SAIInputItemButton = {
        let view = SAIInputItemButton(type: .custom)
        //let view = SAIInputItemButton(type: .System)
        view.isMultipleTouchEnabled = false
        view.isExclusiveTouch = true
        return view
    }()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
}

internal class SAIInputItemButton: UIButton {
    
    override func beginTracking(_ touch: UITouch, with event: UIEvent?) -> Bool {
        guard let barItem = self.barItem else {
            return super.beginTracking(touch, with: event)
        }
        if delegate?.barItem(shouldHighlightFor: barItem) ?? true {
            allowsHighlight = true
            delegate?.barItem(didHighlightFor: barItem)
        } else {
            allowsHighlight = false
        }
        return super.beginTracking(touch, with: event)
    }
    
    var barItem: SAIInputItem? {
        willSet {
            guard barItem !== newValue else {
                return
            }
            UIView.performWithoutAnimation { 
                guard let item = newValue else {
                    return
                }
                self._updateBarItem(item)
            }
        }
    }
    weak var delegate: SAIInputItemViewDelegate?
    
    var allowsHighlight = true
    var _selected: Bool = false
    
    override var isHighlighted: Bool {
        set {
            guard allowsHighlight else {
                return
            }
            super.isHighlighted = newValue
            _setHighlighted(newValue, animated: true)
        }
        get { return super.isHighlighted }
    }
    override var state: UIControl.State {
        // 永远禁止系统的选中
        return super.state.subtracting(.selected)
    }
    
    func setSelected(selected: Bool, animated: Bool) {
        //logger.trace(selected)
        
        let op1: UIControl.State = [(selected ? .selected : .normal), .normal]
        let op2: UIControl.State = [(selected ? .selected : .normal), .highlighted]
        
        let n = barItem?.image(for: op1) ?? barItem?.image(for: .normal)
        let h = barItem?.image(for: op2)
        
        setImage(n, for: .normal)
        setImage(h, for: .highlighted)
        
        if animated {
            _addAnimation("selected")
        }
        
        _selected = selected
    }
    
    private func _init() {
        addTarget(self, action: #selector(_touchHandler), for: .touchUpInside)
    }
    
    private func _addAnimation(_ key: String) {
        let ani = CATransition()
        
        ani.duration = 0.35
        ani.fillMode = CAMediaTimingFillMode.backwards
        ani.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.linear)
        ani.type = CATransitionType.fade
        ani.subtype = CATransitionSubtype.fromTop
        
        layer.add(ani, forKey: key)
    }
    private func _setHighlighted(_ highlighted: Bool, animated: Bool) {
        //logger.trace(highlighted)
        // 检查高亮的时候有没有设置图片, 如果有关闭系统的变透明效果
        if barItem?.image(for: [(isSelected ? .selected : .normal), .highlighted]) != nil {
            imageView?.alpha = 1
        }
        if animated {
            _addAnimation("highlighted")
        }
    }
    
    func _updateBarItem(_ item: SAIInputItem) {
        
        let states: [UIControl.State] = [
            [.normal],
            [.highlighted],
            [.disabled],
            [.selected, .normal],
            [.selected, .highlighted],
            [.selected, .disabled]
        ]
        
        tag = item.tag
        isEnabled = item.enabled
        imageEdgeInsets = item.imageInsets
        
        tintColor = item.tintColor
        backgroundColor = item.backgroundColor
        
        if let font = item.font  {
            titleLabel?.font = font
        }
        
        states.forEach {
            setTitle(item.title(for: $0), for: $0)
            setTitleColor(item.titleColor(for: $0), for: $0)
            setTitleShadowColor(item.titleShadowColor(for: $0), for: $0)
            setAttributedTitle(item.attributedTitle(for: $0), for: $0)
            setImage(item.image(for: $0), for: $0)
            setBackgroundImage(item.backgroundImage(for: $0), for: $0)
        }
        
        setTitle(item.title, for: .normal)
        setImage(item.image, for: .normal)
    }

    
    @objc private func _touchHandler() {
        guard let barItem = barItem else {
            return
        }
        // delegate before the callback
        barItem.handler?(barItem)
        
        if !_selected {
            // select
            guard delegate?.barItem(shouldSelectFor: barItem) ?? true else {
                return
            }
            setSelected(selected: true, animated: true)
            delegate?.barItem(didSelectFor: barItem)
        } else {
            // Deselect
            guard delegate?.barItem(shouldDeselectFor: barItem) ?? true else {
                return
            }
            setSelected(selected: false, animated: true)
            delegate?.barItem(didDeselectFor: barItem)
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        _init()
    }
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        _init()
    }
}


internal protocol SAIInputItemViewDelegate: class {
    
    func barItem(shouldHighlightFor barItem: SAIInputItem) -> Bool
    func barItem(shouldDeselectFor barItem: SAIInputItem) -> Bool
    func barItem(shouldSelectFor barItem: SAIInputItem) -> Bool
    
    func barItem(didHighlightFor barItem: SAIInputItem)
    func barItem(didDeselectFor barItem: SAIInputItem)
    func barItem(didSelectFor barItem: SAIInputItem)
    
}

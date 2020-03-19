//
//  SAIInputItem.swift
//  SAIInputBar
//
//  Created by SAGESSE on 8/3/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit

public enum SAIInputItemPosition: Int {
    case top        = 0
    case left       = 1
    case right      = 3
    case bottom     = 4
    case center     = 2
}
public enum SAIInputItemAlignment: Int {
    //0xvvhh
    case top            = 0x0104 // Top + Center(H)
    case bottom         = 0x0204 // Bottom + Center(H)
    case left           = 0x0401 // Center(V) + Left
    case right          = 0x0402 // Center(V) + Right
    case topLeft        = 0x0101 // Top + Left
    case topRight       = 0x0102 // Top + Right
    case bottomLeft     = 0x0201 // Bottom + Left
    case bottomRight    = 0x0202 // Bottom + Right
    case center         = 0x0404 // Center(V) + Center(H)
    
    case automatic      = 0x0000
}

open class SAIInputItem: NSObject {
    
    // MARK: property
    
    open lazy var identifier: String = UUID().uuidString
    
    open var size: CGSize = CGSize.zero // default is CGSizeZero
    open var image: UIImage? // default is nil
    open var customView: UIView? // default is nil
    
    open var tag: Int = 0 // default is 0
    open var title: String? // default is nil
    open var enabled: Bool = true // default is YES
    
    open var font: UIFont? // default is nil
    open var backgroundColor: UIColor? // default is nil
    
    open var handler: ((SAIInputItem) -> Void)? // default is nil
    
    open var tintColor: UIColor?
    open var alignment: SAIInputItemAlignment = .automatic
    open var imageInsets: UIEdgeInsets = .zero // default is UIEdgeInsetsZero
    
    // MARK: setter
    
    open func setTitle(_ title: String?, for state: UIControl.State) {
        _titles[state.rawValue] = title
    }
    open func setTitleColor(_ color: UIColor?, for state: UIControl.State) {
        _titleColors[state.rawValue] = color
    }
    open func setTitleShadowColor(_ color: UIColor?, for state: UIControl.State) {
        _titleShadowColors[state.rawValue] = color
    }
    open func setAttributedTitle(_ title: NSAttributedString?, for state: UIControl.State) {
        _attributedTitles[state.rawValue] = title
    }
    open func setImage(_ image: UIImage?, for state: UIControl.State) {
        _images[state.rawValue] = image
    }
    open func setBackgroundImage(_ image: UIImage?, for state: UIControl.State) {
        _backgroundImages[state.rawValue] = image
    }
    
    // MARK: getter
    
    open func title(for state: UIControl.State) -> String? {
        return _titles[state.rawValue] ?? nil
    }
    open func titleColor(for state: UIControl.State) -> UIColor? {
        return _titleColors[state.rawValue] ?? nil
    }
    open func titleShadowColor(for state: UIControl.State) -> UIColor? {
        return _titleShadowColors[state.rawValue] ?? nil
    }
    open func attributedTitle(for state: UIControl.State) -> NSAttributedString? {
        return _attributedTitles[state.rawValue] ?? nil
    }
    open func image(for state: UIControl.State) -> UIImage? {
        return _images[state.rawValue] ?? nil
    }
    open func backgroundImage(for state: UIControl.State) -> UIImage? {
        return _backgroundImages[state.rawValue] ?? nil
    }
    
    
    open override var hash: Int {
        return identifier.hash
    }
//    open override var hashValue: Int {
//        return identifier.hashValue
//    }
    
    // MARK: ivar
    
    private var _titles: [UInt: String?] = [:]
    private var _titleColors: [UInt: UIColor?] = [:]
    private var _titleShadowColors: [UInt: UIColor?] = [:]
    private var _attributedTitles: [UInt: NSAttributedString?] = [:]
    
    private var _images: [UInt: UIImage?] = [:]
    private var _backgroundImages: [UInt: UIImage?] = [:]
    
    // MARK: create
    
    public override init() {
        super.init()
    }
    public convenience init(image: UIImage?, handler: ((SAIInputItem) -> Void)? = nil) {
        self.init()
        self.image = image
        self.handler = handler
    }
    public convenience init(title: String?, handler: ((SAIInputItem) -> Void)? = nil) {
        self.init()
        self.title = title
        self.handler = handler
    }
    
    public convenience init(customView: UIView) {
        self.init()
        self.customView = customView
    }
}


extension SAIInputItemPosition: CustomStringConvertible {
    public var description: String {
        switch self {
        case .top: return "Top(\(rawValue))"
        case .left: return "Left(\(rawValue))"
        case .right: return "Right(\(rawValue))"
        case .bottom: return "Bottom(\(rawValue))"
        case .center:  return "Center(\(rawValue))"
        }
    }
}

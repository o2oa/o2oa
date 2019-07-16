//
//  OOUITextField.swift
//  o2app
//
//  Created by 刘振兴 on 2017/9/6.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import SwiftValidator

protocol OOUITextFieldReturnNextDelegate {
    func next()
}

@IBDesignable
open class OOUITextField: UITextField {
    
    //左视图普通图片
    @IBInspectable open var leftImage:UIImage?
    //左视图高亮图片
    @IBInspectable open var leftLightImage:UIImage?
    //下划线普通颜色
    @IBInspectable open var lineColor:UIColor?
    //下划线高亮颜色
    @IBInspectable open var lineLightColor:UIColor?
    //下划线
    fileprivate var lineView:UIView!

    let validator = Validator()
    
    var rule:RegexRule? {
        didSet {
            validator.registerField(self, rules: [rule!])
        }
    }
    
    var returnNextDelegate: OOUITextFieldReturnNextDelegate?
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        commitInit()
       
    }
    
    open override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()

    }
    
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    open override func awakeFromNib() {
        commitInit()
    }
    
    fileprivate func commitInit() {
        self.textColor = UIColor.hexInt(0x666666)
        self.font = UIFont(name: "PingFangSC-Regular", size: 15)!
        
        self.attributedPlaceholder = NSMutableAttributedString(string: self.placeholder ?? "", attributes: [NSAttributedString.Key.foregroundColor:UIColor.hexInt(0x999999),NSAttributedString.Key.font:UIFont(name: "PingFangSC-Regular", size: 13)!])
        
        leftViewMode = .always
        self.delegate = self
        let iv = UIImageView(image: leftImage, highlightedImage: leftLightImage)
        iv.frame = CGRect(x: 0, y: 0, width: (leftImage?.size.width)! , height: (leftImage?.size.height)!)
        let ivContainer = UIView(frame: CGRect(x: 0, y: 0, width: iv.frame.width + 10, height: iv.frame.height))
        ivContainer.addSubview(iv)
        leftView = ivContainer

        lineView = UIView(frame:CGRect(x: 0, y: frame.height - 0.5, width: frame.width, height: 0.5))
        lineView.backgroundColor = lineColor
        addSubview(lineView)
    }
    
    open override func layoutSubviews() {
        super.layoutSubviews()
        lineView.frame = CGRect(x: 0, y: self.frame.height - 0.5, width: self.frame.width, height: 0.5)
    
    }
    
    open func themeUpdate(leftImage:UIImage?, leftLightImage:UIImage?, lineColor:UIColor?, lineLightColor:UIColor?) {
        self.leftImage = leftImage
        self.leftLightImage = leftLightImage
        self.lineColor = lineColor
        self.lineLightColor = lineLightColor
        let lv = self.leftView?.subviews.first as! UIImageView
        lv.image = leftImage
        lv.highlightedImage = leftLightImage
        lineView.backgroundColor = lv.isHighlighted ? lineLightColor : lineColor
    }
    
    
    
    fileprivate func changeShowStatus(_ status:Bool = false) -> Void {
        if status {
            let lv = self.leftView?.subviews.first as! UIImageView
            lv.isHighlighted = true
            lineView.backgroundColor = lineLightColor
        }else{
            let lv = self.leftView?.subviews.first as! UIImageView
            lv.isHighlighted = false
            lineView.backgroundColor = lineColor
        }
    }
    
}

extension OOUITextField:UITextFieldDelegate {
    
    public func textFieldDidBeginEditing(_ textField: UITextField) {
        changeShowStatus(true)
    }
    
    public func textFieldDidEndEditing(_ textField: UITextField) {
        changeShowStatus(false)
        validationRule(textField)
    }
    
    public func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        guard let _ = rule else {
            return true
        }
        validationRule(textField)
        if returnNextDelegate != nil {
            returnNextDelegate?.next()
        }
        return true
    }
    
    func validationRule(_ textField: UITextField) {
        validator.validateField(textField) { (error) in
            if error != nil  {
                
            }
        }
    }
}

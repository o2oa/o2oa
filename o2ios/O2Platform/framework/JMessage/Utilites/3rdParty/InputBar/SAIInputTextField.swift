//
//  SAIInputTextField.swift
//  SAIInputBar
//
//  Created by SAGESSE on 7/23/16.
//  Copyright © 2016-2017 SAGESSE. All rights reserved.
//

import UIKit

internal class SAIInputTextField: UITextView {
    
    override var contentSize: CGSize {
        didSet {
            item.contentSizeChanged()
        }
    }
    
    override var text: String! {
        set {
            super.text = newValue
            delegate?.textViewDidChange?(self)
        }
        get {
            return super.text
        }
    }
    override var attributedText: NSAttributedString! {
        set {
            super.attributedText = newValue
            delegate?.textViewDidChange?(self)
        }
        get {
            return super.attributedText
        }
    }
    
//    override func becomeFirstResponder() -> Bool {
//        super.becomeFirstResponder()
//        return self.becomeFirstResponder()
//    }
    
    
    func insertAttributedText(_ attributedText: NSAttributedString) {
        let currnetTextRange = selectedTextRange ?? UITextRange()
        let newTextLength = attributedText.length
        
        // read postion
        let location = offset(from: beginningOfDocument, to: currnetTextRange.start)
        let length = offset(from: currnetTextRange.start, to: currnetTextRange.end)
        let newRange = NSMakeRange(location, newTextLength)
        
        // update text
        let att = typingAttributes as! [NSAttributedString.Key:Any]
        textStorage.replaceCharacters(in: NSMakeRange(location, length), with: attributedText)
        textStorage.addAttributes(att, range: newRange)
        
        // update new text range
        let newPosition = position(from: beginningOfDocument, offset: location + newTextLength) ?? UITextPosition()
        selectedTextRange = textRange(from: newPosition, to: newPosition)
    }
    
    
    lazy var item: SAIInputTextFieldItem = SAIInputTextFieldItem(textView: self, backgroundView: self.backgroundView)
    lazy var backgroundView: UIImageView = UIImageView()
}


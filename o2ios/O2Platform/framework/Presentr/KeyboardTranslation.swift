//
//  KeyboardTranslation.swift
//  Presentr
//
//  Created by Aaron Satterfield on 7/15/16.
//  Copyright © 2016 danielozano. All rights reserved.
//

import Foundation
import UIKit

public enum KeyboardTranslationType {

    case none
    case moveUp
    case compress
    case stickToTop

    /**
     Calculates the correct frame for the keyboard translation type.

     - parameter keyboardFrame: The UIKeyboardFrameEndUserInfoKey CGRect Value of the Keyboard
     - parameter presentedFrame: The frame of the presented controller that may need to be translated.
     - returns: CGRect representing the new frame of the presented view.
     */
    public func getTranslationFrame(keyboardFrame: CGRect, presentedFrame: CGRect) -> CGRect {
        let keyboardTop = UIScreen.main.bounds.height - keyboardFrame.size.height
        let presentedViewBottom = presentedFrame.origin.y + presentedFrame.height + 20.0 // add a 20 pt buffer
        let offset = presentedViewBottom - keyboardTop
        switch self {
        case .moveUp:
            if offset > 0.0 {
                let frame = CGRect(x: presentedFrame.origin.x, y: presentedFrame.origin.y-offset, width: presentedFrame.size.width, height: presentedFrame.size.height)
                return frame
            }
            return presentedFrame
        case .compress:
            if offset > 0.0 {
                let y = max(presentedFrame.origin.y-offset, 20.0)
                let newHeight = y != 20.0 ? presentedFrame.size.height : keyboardTop - 40.0
                let frame = CGRect(x: presentedFrame.origin.x, y: y, width: presentedFrame.size.width, height: newHeight)
                return frame
            }
            return presentedFrame
        case .stickToTop:
            if offset > 0.0 {
                let y = max(presentedFrame.origin.y-offset, 20.0)
                let frame = CGRect(x: presentedFrame.origin.x, y: y, width: presentedFrame.size.width, height: presentedFrame.size.height)
                return frame
            }
            return presentedFrame
        case .none:
            return presentedFrame
        }
    }
}

// MARK: Notification + UIKeyboardInfo

extension Notification {

    /// Gets the optional CGRect value of the UIKeyboardFrameEndUserInfoKey from a UIKeyboard notification
    func keyboardEndFrame () -> CGRect? {
        return (self.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue
    }

    /// Gets the optional AnimationDuration value of the UIKeyboardAnimationDurationUserInfoKey from a UIKeyboard notification
    func keyboardAnimationDuration () -> Double? {
        return (self.userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber)?.doubleValue
    }
}

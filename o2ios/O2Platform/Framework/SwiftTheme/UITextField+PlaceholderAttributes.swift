//
//  UITextField+PlaceholderAttributes.swift
//  SwiftTheme
//
//  Created by kcramer on 3/6/18.
//  Copyright © 2018. All rights reserved.
//

import UIKit

extension UITextField {
    @objc func updatePlaceholderAttributes(_ newAttributes: [NSAttributedString.Key: Any]) {
        guard let placeholder = self.attributedPlaceholder else { return }
        let newString = NSAttributedString(attributedString: placeholder,
                                           merging: newAttributes)
        self.attributedPlaceholder = newString
    }
}

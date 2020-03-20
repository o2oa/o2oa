//
//  UIRefreshControl+TitleAttributes.swift
//  SwiftTheme
//
//  Created by kcramer on 4/1/18.
//  Copyright © 2018 Gesen. All rights reserved.
//

import Foundation

extension UIRefreshControl {
    @objc func updateTitleAttributes(_ newAttributes: [NSAttributedString.Key: Any]) {
        guard let title = self.attributedTitle else { return }
        let newString = NSAttributedString(attributedString: title,
                                           merging: newAttributes)
        self.attributedTitle = newString
    }
}

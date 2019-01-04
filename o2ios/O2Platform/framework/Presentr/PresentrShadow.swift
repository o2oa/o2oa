//
//  PresentrShadow.swift
//  Pods
//
//  Created by Daniel Lozano Valdés on 3/21/17.
//
//

import UIKit

/// Helper struct that represents the shadow properties
public struct PresentrShadow {

    public let shadowColor: UIColor?

    public let shadowOpacity: Float?

    public let shadowOffset: CGSize?

    public let shadowRadius: CGFloat?

    public init(shadowColor: UIColor?, shadowOpacity: Float?, shadowOffset: CGSize?, shadowRadius: CGFloat?) {
        self.shadowColor = shadowColor
        self.shadowOpacity = shadowOpacity
        self.shadowOffset = shadowOffset
        self.shadowRadius = shadowRadius
    }

}

//
//  CoverHorizontalAnimation.swift
//  Presentr
//
//  Created by Daniel Lozano on 5/15/16.
//  Copyright © 2016 danielozano. All rights reserved.
//

import UIKit

public class CoverHorizontalAnimation: PresentrAnimation {

    private var fromRight: Bool

    public init(fromRight: Bool = true) {
        self.fromRight = fromRight
    }

    override public func transform(containerFrame: CGRect, finalFrame: CGRect) -> CGRect {
        var initialFrame = finalFrame
        if fromRight {
            initialFrame.origin.x = containerFrame.size.width + initialFrame.size.width
        } else {
            initialFrame.origin.x = 0 - initialFrame.size.width
        }
        return initialFrame
    }

}

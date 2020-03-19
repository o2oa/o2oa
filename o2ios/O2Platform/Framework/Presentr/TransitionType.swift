//
//  TransitionType.swift
//  Presentr
//
//  Created by Daniel Lozano on 7/6/16.
//  Copyright © 2016 danielozano. All rights reserved.
//

import Foundation

/// Describes the transition animation for presenting the view controller.
///
/// - crossDissolve: Crossfade animation transition.
/// - coverVertical: Slides in vertically from bottom.
/// - coverVerticalFromTop: Slides in vertically from top.
/// - coverHorizontalFromRight: Slides in horizontally from right.
/// - coverHorizontalFromLeft: Slides in horizontally from left.
/// - custom: Custom transition animation provided by user.
public enum TransitionType {

    case crossDissolve
    case coverVertical
    case coverVerticalFromTop
    case coverHorizontalFromRight
    case coverHorizontalFromLeft
    case custom(PresentrAnimation)

    /// Associates a custom transition type to the class responsible for its animation.
    ///
    /// - Returns: PresentrAnimation subclass which conforms to 'UIViewControllerAnimatedTransitioning' to be used for the animation transition.
    func animation() -> PresentrAnimation {
        switch self {
        case .crossDissolve:
            return CrossDissolveAnimation()
        case .coverVertical:
            return CoverVerticalAnimation()
        case .coverVerticalFromTop:
            return CoverVerticalFromTopAnimation()
        case .coverHorizontalFromRight:
            return CoverHorizontalAnimation(fromRight: true)
        case .coverHorizontalFromLeft:
            return CoverHorizontalAnimation(fromRight: false)
        case .custom(let animation):
            return animation
        }
    }

}

//
//  PresentationType.swift
//  Presentr
//
//  Created by Daniel Lozano on 7/6/16.
//  Copyright © 2016 danielozano. All rights reserved.
//

import Foundation

/// Basic Presentr type. Its job is to describe the 'type' of presentation. The type describes the size and position of the presented view controller.
///
/// - alert: This is a small 270 x 180 alert which is the same size as the default iOS alert.
/// - popup: This is a average/default size 'popup' modal.
/// - topHalf: This takes up half of the screen, on the top side.
/// - bottomHalf: This takes up half of the screen, on the bottom side.
/// - fullScreen: This takes up the entire screen.
/// - dynamic: Uses autolayout to calculate width & height. Have to provide center position.
/// - custom: User provided custom width, height & center position.
public enum PresentationType {

    case alert
    case popup
    case topHalf
    case bottomHalf
    case fullScreen
    case dynamic(center: ModalCenterPosition)
    case custom(width: ModalSize, height: ModalSize, center: ModalCenterPosition)

    /// Describes the sizing for each Presentr type. It is meant to be non device/width specific, except for the .custom case.
    ///
    /// - Returns: A tuple containing two 'ModalSize' enums, describing its width and height.
    func size() -> (width: ModalSize, height: ModalSize)? {
        switch self {
        case .alert:
            return (.custom(size: 275), .custom(size: 282))
        case .popup:
            return (.default, .default)
        case .topHalf, .bottomHalf:
            return (.full, .half)
        case .fullScreen:
            return (.full, .full)
        case .custom(let width, let height, _):
            return (width, height)
        case .dynamic(_):
            return nil
        }
    }

    /// Describes the position for each Presentr type. It is meant to be non device/width specific, except for the .custom case.
    ///
    /// - Returns: Returns a 'ModalCenterPosition' enum describing the center point for the presented modal.
    func position() -> ModalCenterPosition {
        switch self {
        case .alert, .popup:
            return .center
        case .topHalf:
            return .topCenter
        case .bottomHalf:
            return .bottomCenter
        case .fullScreen:
            return .center
        case .custom(_, _, let center):
            return center
        case .dynamic(let center):
            return center
        }
    }

    /// Associates each Presentr type with a default transition type, in case one is not provided to the Presentr object.
    ///
    /// - Returns: Return a 'TransitionType' which describes a transition animation.
    func defaultTransitionType() -> TransitionType {
        switch self {
        case .topHalf:
            return .coverVerticalFromTop
        default:
            return .coverVertical
        }
    }

    /// Default round corners setting.
    var shouldRoundCorners: Bool {
        switch self {
        case .alert, .popup:
            return true
        default:
            return false
        }
    }

}

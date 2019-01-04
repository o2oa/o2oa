//
//  Presentr+Equatable.swift
//  Presentr
//
//  Created by Daniel Lozano on 7/6/16.
//  Copyright © 2016 danielozano. All rights reserved.
//

import Foundation

extension PresentationType: Equatable { }
public func == (lhs: PresentationType, rhs: PresentationType) -> Bool {
    switch (lhs, rhs) {
    case (let .custom(lhsWidth, lhsHeight, lhsCenter), let .custom(rhsWidth, rhsHeight, rhsCenter)):
        return lhsWidth == rhsWidth && lhsHeight == rhsHeight && lhsCenter == rhsCenter
    case (.alert, .alert):
        return true
    case (.popup, .popup):
        return true
    case (.topHalf, .topHalf):
        return true
    case (.bottomHalf, .bottomHalf):
        return true
    case (.dynamic, .dynamic):
        return true
    default:
        return false
    }
}

extension ModalSize: Equatable { }
public func == (lhs: ModalSize, rhs: ModalSize) -> Bool {
    switch (lhs, rhs) {
    case (let .custom(lhsSize), let .custom(rhsSize)):
        return lhsSize == rhsSize
    case (.default, .default):
        return true
    case (.half, .half):
        return true
    case (.full, .full):
        return true
    default:
        return false
    }
}

extension ModalCenterPosition: Equatable { }
public func == (lhs: ModalCenterPosition, rhs: ModalCenterPosition) -> Bool {
    switch (lhs, rhs) {
    case (let .custom(lhsCenterPoint), let .custom(rhsCenterPoint)):
        return lhsCenterPoint.x == rhsCenterPoint.x && lhsCenterPoint.y == rhsCenterPoint.y
    case (.center, .center):
        return true
    case (.topCenter, .topCenter):
        return true
    case (.bottomCenter, .bottomCenter):
        return true
    default:
        return false
    }
}

//
//  CellTouchImageView.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/19.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class CellTouchImageView: UIImageView {

    /**
     覆盖touch事件，不向父级传递
     
     - parameter touches:
     - parameter event:
     */
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
//        DDLogDebug(touches.debugDescription)
//        DDLogDebug(event.debugDescription)
    }

}

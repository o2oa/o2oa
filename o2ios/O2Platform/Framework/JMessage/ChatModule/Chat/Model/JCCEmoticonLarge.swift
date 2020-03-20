//
//  JCCEmoticonLarge.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

open class JCCEmoticonLarge: JCCEmoticon {
    
    open override func draw(in rect: CGRect, in ctx: CGContext) {
        guard let image = contents as? UIImage else {
            return
        }
        
        var nframe1 = rect.inset(by: UIEdgeInsets(top: 0, left: 0, bottom: 20, right: 0))
        var nframe2 = rect.inset(by: UIEdgeInsets(top: nframe1.height, left: 0, bottom: 0, right: 0))
        
        // 图标
        let scale = min(min(nframe1.width / image.size.width, nframe1.height / image.size.height), 1)
        let imageSize = CGSize(width: image.size.width * scale, height: image.size.height * scale)
        
        nframe1.origin.x = nframe1.minX + (nframe1.width - imageSize.width) / 2
        nframe1.origin.y = nframe1.minY + (nframe1.height - imageSize.height) / 2
        nframe1.size.width = imageSize.width
        nframe1.size.height = imageSize.height
        
        image.draw(in: nframe1)
        
        // 标题
        let cfg = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 12),
                   NSAttributedString.Key.foregroundColor: UIColor.gray]
        let name = title as NSString
        
        let titleSize = name.size(withAttributes: cfg)
        
        nframe2.origin.x = nframe2.minX + (nframe2.width - titleSize.width) / 2
        nframe2.origin.y = nframe2.minY + (nframe2.height - titleSize.height) / 2
        nframe2.size.width = titleSize.width
        nframe2.size.height = titleSize.height
        
        name.draw(in: nframe2, withAttributes: cfg)
    }
    
}

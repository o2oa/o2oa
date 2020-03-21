//
//  JCCEmotiocon.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

open class JCCEmoticon: JCEmoticon {

    public required init?(object: NSDictionary) {
        guard let id = object["id"] as? String, let title = object["title"] as? String, let type = object["type"] as? Int else {
            return nil
        }
        
        self.id = id
        self.title = title
        
        super.init()
        
        self.image = object["image"] as? String
        self.preview = object["preview"] as? String
        
        if type == 1 {
            self.contents = object["contents"]
        }
    }
    
    public static func emoticons(with objects: NSArray, at directory: String) -> [JCCEmoticon] {
        return objects.flatMap {
            guard let dic = $0 as? NSDictionary else {
                return nil
            }
            guard let e = self.init(object: dic) else {
                return nil
            }
            if let name = e.preview {
                e.contents = UIImage(contentsOfFile: "\(directory)/\(name)")
            }
            return e
        }
    }
    
    var id: String
    var title: String
    
    var image: String?
    var preview: String?
    
}

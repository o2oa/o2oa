//
//  OOContactSearchSectionHeaderView.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/28.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

enum OOContacSearchSectionHeaderType:Int{
    case unit = 0
    case person = 1
    case group = 2
    case all = 3
}

class OOContactSearchSectionHeaderView: UIView {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var sectionTitle: UILabel!
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    public func setHeaderType(_ headerType:OOContacSearchSectionHeaderType){
        let (iconName,title) = searchHeaderTypes[headerType]!
        self.iconImageView.image = UIImage(named:iconName)
        self.sectionTitle.text = title
    }
    
    private let searchHeaderTypes:[OOContacSearchSectionHeaderType:(String,String)] = [.unit:(O2ThemeManager.string(for: "Icon.icon_bumen")!,"组织"),.person:(O2ThemeManager.string(for: "Icon.icon_linkman")!,"联系人"),.group:(O2ThemeManager.string(for: "Icon.icon_group")!,"群组")]
    
}

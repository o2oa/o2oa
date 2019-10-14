//
//  OOContactGroupHeaderView.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/20.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

enum OOContactGroupHeaderType:Int{
    case department = 0
    case company = 1
    case group = 2
    case linkman = 3
}

class OOContactGroupHeaderView: UIView {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var groupTitleLabel: UILabel!
    
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    public func setHeaderType(_ headerType:OOContactGroupHeaderType){
        let (iconName,title) = headerTypes[headerType]!
        self.iconImageView.image = UIImage(named:iconName)
        self.groupTitleLabel.text = title
    }
    
    public func setSearchHeaderType(_ headerType:OOContactGroupHeaderType){
        let (iconName,title) = searchHeaderTypes[headerType]!
        self.iconImageView.image = UIImage(named:iconName)
        self.groupTitleLabel.text = title
    }
    
     private let searchHeaderTypes:[OOContactGroupHeaderType:(String,String)] = [.department:(O2ThemeManager.string(for: "Icon.icon_bumen")!,"部门"),.company:(O2ThemeManager.string(for: "Icon.icon_company")!,"公司"),.group:(O2ThemeManager.string(for: "Icon.icon_group")!,"群组"),.linkman:(O2ThemeManager.string(for: "Icon.icon_linkman")!,"常用联系人")]
    
    private let headerTypes:[OOContactGroupHeaderType:(String,String)] = [.department:(O2ThemeManager.string(for: "Icon.icon_bumen")!,"我的部门"),.company:(O2ThemeManager.string(for: "Icon.icon_company")!,"我的公司"),.group:(O2ThemeManager.string(for: "Icon.icon_group")!,"我的群组"),.linkman:(O2ThemeManager.string(for: "Icon.icon_linkman")!,"常用联系人")]

}

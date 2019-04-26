//
//  OOUISearchController.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/28.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit



class OOUISearchController: UISearchController,UISearchBarDelegate {
    
    override init(searchResultsController: UIViewController?) {
        super.init(searchResultsController: searchResultsController)
        self.dimsBackgroundDuringPresentation  = true
        //self.hidesNavigationBarDuringPresentation   = false
        let attributesKey = [NSAttributedString.Key.foregroundColor:UIColor(hex: "#999999"),NSAttributedString.Key.font:UIFont.init(name: "PingFangSC-Regular", size: 14)!]
        UIBarButtonItem.appearance(whenContainedInInstancesOf:[UISearchBar.self]).setTitleTextAttributes(attributesKey, for: .normal)
        UIBarButtonItem.appearance(whenContainedInInstancesOf: [UISearchBar.self]).title = "取消"
        let searchBar = self.searchBar
        searchBar.setBackgroundImage(UIImage(), for: .any, barMetrics: .default)
        //tintColor
        //searchBar.tintColor = UIColor(hex: "#999999")
        //searchBar.barTintColor = UIColor(hex : "#F5F5F5")
        //searchBar.setPositionAdjustment(UIOffsetMake(50, 0), for: .search)
        //searchBar.searchFieldBackgroundPositionAdjustment = UIOffsetMake(50, 0)
        //searchBar.searchTextPositionAdjustment = UIOffsetMake(50, 0)
        //searchBar.placeholder = "请输入组织、人员或群组名称或字母"
        
        let textField = searchBar.value(forKey: "_searchField") as! UITextField
        textField.attributedPlaceholder = NSMutableAttributedString(string: "请输入组织、人员或群组名称或字母", attributes: attributesKey)
        textField.textColor = UIColor(hex: "#FB4747")
        textField.font = UIFont(name: "PingFangSC-Regular", size: 14)

        //
        searchBar.setImage(#imageLiteral(resourceName: "icon_sousuo1"), for: .search, state: .normal)
        searchBar.delegate = self
        
    }
    
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    
}



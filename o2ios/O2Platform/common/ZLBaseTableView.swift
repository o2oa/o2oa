//
//  ZLBaseTableView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/20.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import EmptyDataSet_Swift

open class ZLBaseTableView: UITableView {
    
    var showTitle:Bool = true
    
    var showDesc:Bool = false
    
    public var emptyTitle:String = "" {
        didSet {
            showTitle = true
        }
    }
    
    public var emptyDesc:String = ""
    
    
    override init(frame: CGRect, style: UITableView.Style) {
        super.init(frame: frame, style: style)
        commonInit()
    }
    
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
    }
    
    private func commonInit() {
        self.emptyDataSetSource = self
        self.emptyDataSetDelegate = self
        self.tableFooterView = UIView()
    }
    
    
    
}

extension ZLBaseTableView:EmptyDataSetSource,EmptyDataSetDelegate{
    
    public func title(forEmptyDataSet scrollView: UIScrollView!) -> NSAttributedString! {
        
        let text  = emptyTitle
        let attributes = [NSAttributedString.Key.font:UIFont(name: "PingFangSC-Regular", size: 20.0)!,NSAttributedString.Key.foregroundColor:RGB(108, g: 108, b: 108)]
        return NSAttributedString(string: text, attributes: attributes)
    }
    
    public func description(forEmptyDataSet scrollView: UIScrollView!) -> NSAttributedString! {
        let text  = emptyDesc
        let attributes = [NSAttributedString.Key.font:UIFont(name: "PingFangSC-Light", size: 14.0)!,NSAttributedString.Key.foregroundColor:RGB(108, g: 108, b: 108)]
        return NSAttributedString(string: text, attributes: attributes)
    }
    
    public func image(forEmptyDataSet scrollView: UIScrollView!) -> UIImage! {
        return UIImage(named: "emptyStatusIcon")!
    }
    
    public func backgroundColor(forEmptyDataSet scrollView: UIScrollView!) -> UIColor! {
        return RGB(247, g: 247, b: 247)
    }
    
    public func emptyDataSetShouldDisplay(_ scrollView: UIScrollView!) -> Bool {
        return showTitle
    }

}

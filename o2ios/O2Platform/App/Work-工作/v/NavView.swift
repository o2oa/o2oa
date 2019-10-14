//
//  NavView1.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/3.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit


class MyView:UIView {
    
    var tableViews = Array<UITableView>()
    
    var myLabel:UILabel?
    
    var scanBtn:UIButton?
    
    var addBtn:UIButton?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        myLabel = UILabel(frame: .zero)
        myLabel?.text = "首页"
        myLabel?.textColor = UIColor.white
        myLabel?.center = CGPoint(x: width / 2 - 15, y: 25)
        addSubview(myLabel!)
        let y = safeAreaTopHeight / 2
        
        scanBtn = UIButton(type: .custom)
        scanBtn?.frame = CGRect(x: 20, y: y, width: 20, height: 20)
        scanBtn?.setBackgroundImage(UIImage(named: "menu_scan_qrcode"), for: .normal)
        addSubview(scanBtn!)
        
        addBtn = UIButton(type: .custom)
        addBtn?.frame = CGRect(x: self.width-45, y: y, width: 20, height: 20)
        addBtn?.setBackgroundImage(UIImage(named:"add"), for: .normal)
        addSubview(addBtn!)

    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func willMove(toSuperview newSuperview: UIView?) {
        super.willMove(toSuperview: newSuperview)
        for table in self.tableViews {
            table.addObserver(self, forKeyPath: "contentOffset", options: .new, context: nil)
        }
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if (!(keyPath == "contentOffset")) {
            super.observeValue(forKeyPath: keyPath, of: object, change: change, context: context)
            return;
        }
        
        let tableView = object as! UITableView
        let tableViewoffsetY = tableView.contentOffset.y
        
        self.backgroundColor = base_color.withAlphaComponent(min(1, tableViewoffsetY/136))
        if tableViewoffsetY < 125 {
            for table in self.tableViews {
                table.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 56, right: 0)
            }
            UIView.animate(withDuration: 0.25, animations: {
                self.myLabel?.isHidden = true
                self.addBtn?.alpha = 1 - (min(1, tableViewoffsetY/136))
                self.scanBtn?.alpha = 1 - (min(1, tableViewoffsetY/136))
                
            })
        }else{
            for table in self.tableViews {
                table.contentInset = UIEdgeInsets(top: 64, left: 0, bottom: 56, right: 0)
            }
            
            UIView.animate(withDuration: 0.25, animations: {
                self.myLabel?.isHidden = false
                self.scanBtn?.alpha = 1
                self.addBtn?.alpha = 1
            })
        }
        
    }
}


//public class NavView:UIView {
//    
//    var tableViews = Array<UITableView>()
//    
//    var titleLabel:UILabel?
//    
//    var scanBtn: UIButton?
//    
//    var addBtn:UIButton?
//    
//    public override init(frame: CGRect) {
//        super.init(frame: frame)
//        //commonInit()
//    }
//    
//    private func commonInit(){
////        do {
////            titleLabel = UILabel(frame: .zero)
////            titleLabel?.text = "首页"
////            titleLabel?.textColor = UIColor.white
////            titleLabel?.center = CGPoint(x: self.width / 2 - 15, y: 25)
////            titleLabel?.size = CGSize(width: 100, height: 30)
////            addSubview(titleLabel!)
////         }
////
////        do {
////            scanBtn = UIButton(type: UIButtonType.custom)
////            scanBtn?.frame = CGRect(x: 20, y: 30, width: 20, height: 20)
////            scanBtn?.setBackgroundImage(UIImage(named: "menu_scan_qrcode"), for: .normal)
////            addSubview(scanBtn!)
////        }
////
////        do {
////            addBtn = UIButton(type: UIButtonType.custom)
////            addBtn?.frame = CGRect(x: self.width-45, y: 30, w: 20, h: 20)
////            addBtn?.setBackgroundImage(UIImage(named:"add"), for: .normal)
////            addSubview(addBtn!)
////        }
//     }
//    
//    public required init(coder aDecoder: NSCoder) {
//        super.init(coder: aDecoder)!
//        //commonInit()
//    }
//    
//    override public func willMove(toSuperview newSuperview: UIView?) {
//        super.willMove(toSuperview: newSuperview)
//        for table in self.tableViews {
//            table.addObserver(self, forKeyPath: "contentOffset", options: .new, context: nil)
//        }
//    }
//    
//    override public func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
//        if (!(keyPath == "contentOffset")) {
//            super.observeValue(forKeyPath: keyPath, of: object, change: change, context: context)
//            return;
//        }
//        
//        let tableView = object as! UITableView
//        let tableViewoffsetY = tableView.contentOffset.y
//        
//        self.backgroundColor = base_color.withAlphaComponent(min(1, tableViewoffsetY/136))
//        if tableViewoffsetY < 125 {
//            for table in self.tableViews {
//                table.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 56, right: 0)
//            }
//            UIView.animate(withDuration: 0.25, animations: {
//                self.titleLabel?.isHidden = true
//                self.addBtn?.alpha = 1 - (min(1, tableViewoffsetY/136))
//                self.scanBtn?.alpha = 1 - (min(1, tableViewoffsetY/136))
//                
//            })
//        }else{
//            for table in self.tableViews {
//                table.contentInset = UIEdgeInsets(top: 64, left: 0, bottom: 56, right: 0)
//            }
//            
//            UIView.animate(withDuration: 0.25, animations: {
//                self.titleLabel?.isHidden = false
//                self.scanBtn?.alpha = 1
//                self.addBtn?.alpha = 1
//            })
//        }
//        
//    }
//    
//}

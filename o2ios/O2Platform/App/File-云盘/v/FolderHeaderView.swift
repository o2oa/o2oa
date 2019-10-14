//
//  FolderHeaderView.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/14.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

private  let buttonWidth = 60

private  let buttonHeight  = 30

private  let labelWidth = 15

private  let labelHeight = 30


protocol FolderHeaderViewDelegate {
    func headerClickSelected(currentFile f:OOFile,folderQueue fQueue:[OOFile])
}

class FolderHeaderView: UIView {
    
    var folderQueue:[OOFile]?
    
    var rootFile = OOFile()
    
    var delegate:FolderHeaderViewDelegate?
    
    
    override init(frame: CGRect) {
       super.init(frame: frame)
        //let button = createButton(file:rootFile,index:-1)
        let button = genernateButton(rootFile,-1)
        button.tag = 1001
        addSubview(button)
        let count = folderQueue?.count
        for i in 0 ..< (count ?? 0) {
            let f = self.folderQueue![i]
            let sp = genernateSymbol()
            addSubview(sp)
            let btn = genernateButton(f,i)
            btn.tag = 2000+i
            addSubview(btn)
        }
        
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        //根目录button
        var x=5,y=5
        super.layoutSubviews()
        for view in self.subviews {
            if view.isKind(of: UIButton.self){
                if view.tag == 1001 {
                    view.frame =  CGRect(x: CGFloat(x), y: CGFloat(y), width: CGFloat(buttonWidth), height: CGFloat(buttonHeight))
                }else{
                    x += labelWidth
                    view.frame =  CGRect(x: CGFloat(x), y: CGFloat(y), width: CGFloat(buttonWidth), height: CGFloat(buttonHeight))
                }
            }else if view.isKind(of: UILabel.self){
                x += buttonWidth
                view.frame = CGRect(x: CGFloat(x), y: CGFloat(y), width: CGFloat(labelWidth), height: CGFloat(labelHeight))
            }
        }
        
    }
    
    func genernateButton(_ file:OOFile,_ index:Int) -> UIButton {
        let button = UIButton(type: .custom)
        button.setTitle(file.name, for: .normal)
        button.setTitleColor(UIColor.gray, for: .normal)
        button.layer.borderWidth = 1
        button.layer.borderColor = base_color.cgColor
        button.layer.masksToBounds = true
        button.layer.cornerRadius = 12
        button.tag = index
        button.bounds = CGRect(x: 0, y: 0, width: CGFloat(buttonWidth), height: CGFloat(buttonHeight))
        button.titleLabel?.font = UIFont.systemFont(ofSize: 12)
        return button
    }
    
    func genernateSymbol() -> UILabel {
        let label = UILabel()
        label.text = "->"
        label.textColor = base_color
        label.font = UIFont.systemFont(ofSize: 12)
        label.bounds = CGRect(x: 0, y: 0, width: labelWidth, height: labelHeight)
        return label
    }
    
    
}

//
//  OOContactUnitHeader.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/21.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit


class OOContactUnitHeader: UIView {
    
    private let firstWords = "通讯录>"
    
    @IBOutlet weak var containerView: UIScrollView!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    //载入会执行
    override func awakeFromNib() {
        
    }
    
    
    
    func setNavBar(_ level:Int,_ levelName:String?){
        //增加第一个Label
        let textAttributes = [NSAttributedString.Key.foregroundColor:UIColor(hex:"#333333"),NSAttributedString.Key.font:UIFont(name:"PingFangSC-Regular",size:15)!]
        let firstSize = firstWords.getSize(with: 15)
        var oX = CGFloat(4.0)
        let oY = (containerView.bounds.height - firstSize.height) / 2
        let firstLabel = UILabel(frame: CGRect(x: CGFloat(oX), y: oY, width: firstSize.width, height: firstSize.height))
        firstLabel.textAlignment = .left
        firstLabel.attributedText = NSMutableAttributedString(string: firstWords, attributes: textAttributes)
        firstLabel.sizeToFit()
        oX += firstSize.width
        containerView.addSubview(firstLabel)
        
        guard let lName = levelName else {
            return
        }
        
        if level >= 1 {
            let theWords = lName.split(separator: "/")
            let lastWord = theWords.last
            theWords.forEach({ (word) in
                var title = ""
                if word == lastWord {
                    title = String(word)
                }else{
                    title = String(word)+">"
                }
                let wordSize = title.getSize(with: 15)
                let wordLabel = UILabel(frame: CGRect(x: oX, y: oY, width: wordSize.width, height: wordSize.height))
                wordLabel.textAlignment = .left
                wordLabel.attributedText = NSMutableAttributedString(string: title,attributes: textAttributes)
                wordLabel.sizeToFit()
                oX += wordSize.width
                containerView.addSubview(wordLabel)
                
            })
        }
        
        
    }
    
}

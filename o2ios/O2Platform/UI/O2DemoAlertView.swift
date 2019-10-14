//
//  O2DemoAlertView.swift
//  O2Platform
//
//  Created by FancyLou on 2019/1/22.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class O2DemoAlertView: UIView {
    
    let backFrame = CGRect.init(x: 0, y: 0, width: SCREEN_WIDTH, height: SCREEN_HEIGHT)
    let backColor = UIColor(r: 0, g: 0, b: 0, a: 0.6)
    var boardView = UIView()
    var closeBtn = UIButton.init(type: UIButton.ButtonType.custom)
    
    
    func initView() -> UIView {
        self.frame = backFrame
        self.isHidden = true
        self.backgroundColor = backColor
        return self
    }
    
    private func addBoardView() {
        // 公告内容放到屏幕外面
        let x = (SCREEN_WIDTH - 315 ) / 2
        self.boardView.frame = CGRect.init(x: x, y: -(SCREEN_HEIGHT), width: 315, height: 485)
        self.boardView.backgroundColor = UIColor.clear
        let boardBackImage = UIImageView.init(frame: CGRect.init(x: 0, y: 0, width: 315, height: 485))
        boardBackImage.layer.masksToBounds = true
        boardBackImage.contentMode = UIView.ContentMode.scaleAspectFill
        boardBackImage.image = UIImage(named: "pic_czsm")
        self.boardView.addSubview(boardBackImage)//添加公告
        let closeX = CGFloat(315 - 5 - 22)
        self.closeBtn.frame = CGRect.init(x: closeX, y: 5, width: 22, height: 22) // 关闭按钮在公告的右上角 right：5 top：5
        self.closeBtn.setImage(UIImage(named: "icon_off_white2"), for: UIControl.State.normal)
        self.closeBtn.addTarget(self, action: #selector(closeAlertView), for: UIControl.Event.touchUpInside)
        self.boardView.addSubview(self.closeBtn)
        self.closeBtn.isHidden = true //关闭按钮隐藏
    }
    
    func showFallDown() {
        UIApplication.shared.keyWindow?.addSubview(initView())
        self.isHidden = false //显示背景
        addBoardView() //添加公告
        self.addSubview(self.boardView)
        //执行动画 从上往下掉落 回弹一下
        let firstY = SCREEN_HEIGHT - 485
        let secondY = CGFloat(0.0)
        let lastY = (SCREEN_HEIGHT - 485) / 2
        UIView.animate(withDuration: 0.5, animations: {
            self.boardView.frame.origin.y = firstY
        }) { (_) in
            UIView.animate(withDuration: 0.2, animations: {
                self.boardView.frame.origin.y = secondY
            }, completion: { (_) in
                UIView.animate(withDuration: 0.2, animations: {
                    self.boardView.frame.origin.y = lastY
                }, completion: { (_) in
                    self.closeBtn.isHidden = false
                })
            })
        }
        
    }
    @objc func closeAlertView() {
        self.closeBtn.isHidden = true
        UIView.animate(withDuration: 0.5, animations: {
            self.boardView.frame.origin.y = -(SCREEN_HEIGHT)
        }) { (_) in
            self.removeFromSuperview()
        }
    }
}

//
//  OONoticationSettingController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/3/22.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import SnapKit

class OONoticationSettingController: UIViewController,UIScrollViewDelegate {
    
    
    @IBOutlet weak var containerView: UIScrollView!
    
    @IBOutlet weak var pageControl: UIPageControl!
    
    private var pageImageNames:[String] = ["s1","s2","s3"]
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "开启通知提示"
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "去设置", style: .plain, target: self, action:#selector(buttonClicked(_:)))
        initPage()
    }
    
    private func initPage(){
        //容器
        self.containerView.contentSize = imagesContentSize()
        self.containerView.isDirectionalLockEnabled = true
        self.containerView.delegate = self
        self.containerView.isPagingEnabled = true
        self.containerView.showsHorizontalScrollIndicator = false
        imageViews().forEach { (imageView) in
            self.containerView.addSubview(imageView)
        }
        self.pageControl.numberOfPages = pageImageNames.count
        self.pageControl.currentPage = 0
    }
    
    @objc private func buttonClicked(_ sender:Any?){
        let url = URL(string:UIApplication.openSettingsURLString)!
        UIApplication.shared.openURL(url)
        
    }
    
    private func imagesContentSize() -> CGSize {
        let sHeight = SCREEN_WIDTH
        let sWidth = SCREEN_WIDTH * CGFloat(pageImageNames.count)
        return CGSize(width: sWidth, height: sHeight)
    }
    
    // MARK:- 返回图片页视图列表
    private func imageViews() -> [UIView] {
        var views:[UIView] = []
        for index in 0..<pageImageNames.count {
            let imageView = UIImageView(image: UIImage(named: pageImageNames[index]))
            imageView.frame = CGRect(x: (SCREEN_WIDTH * CGFloat(index)), y: 0, width: SCREEN_WIDTH
                , height: SCREEN_HEIGHT)
            views.append(imageView)
//            if index == pageImageNames.count - 1 {
//                //使用UIView包含
//                let view = UIView()
//                view.frame = imageView.frame
//                imageView.frame = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: SCREEN_HEIGHT)
//                let btn = UIButton.btn(bgColor: .red, disabledColor: .red, title: "去设置", titleColor: .white)
//                btn.addTarget(self, action: #selector(buttonClicked(_:)), for: .touchUpInside)
//                view.addSubview(imageView)
//                view.insertSubview(btn, aboveSubview: imageView)
//                btn.snp.makeConstraints({ (make) in
//                    make.width.equalTo(150)
//                    make.height.equalTo(40)
//                    make.centerX.equalTo(view)
//                    make.bottom.equalTo(-40)
//                })
//                views.append(view)
//            }else{
//                views.append(imageView)
//            }
        }
        return views
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        //OOLog(scrollView.contentOffset)
        let currentPage  = scrollView.contentOffset.x / scrollView.frame.size.width
        pageControl.currentPage = Int(currentPage)
        if pageControl.currentPage + 1 == pageControl.numberOfPages {
            pageControl.isHidden = true
        }else{
            pageControl.isHidden = false
        }
    }
    
}

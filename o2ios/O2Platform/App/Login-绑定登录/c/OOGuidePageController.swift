//
//  OOGuidePageController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/5.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class OOGuidePageController: UIViewController,UIScrollViewDelegate {
    
    @IBOutlet weak var containerView: UIScrollView!
    
    @IBOutlet weak var pageControl: UIPageControl!
    
    @IBOutlet weak var startButton: OOBaseUIButton!
    

    private var pageImageNames:[String] = ["引导页-1","引导页-2","引导页-3","引导页-4","引导页-5"]
    
    override func awakeFromNib() {
        //设置button
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "引导页"
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
            UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.0, initialSpringVelocity: 0.0, options: .allowAnimatedContent, animations: {
                self.startButton.alpha = 1
            }) { (result) in
                
            }
        }else{
            pageControl.isHidden = false
            UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.0, initialSpringVelocity: 0.0, options: .allowAnimatedContent, animations: {
                self.startButton.alpha = 0
            }) { (result) in
                
            }
        }
    }
    
    @IBAction func startAppAction(_ sender: Any) {
        var login: LoginViewController?
        if self.presentingViewController is LoginViewController {
            DDLogDebug(" presenting is LoginViewController。")
            login = self.presentingViewController as? LoginViewController
        }
        self.dismiss(animated: true, completion: {
           DDLogDebug("关闭引导。。。。。。。。。。。。。。")
            if let lo = login {
                DDLogDebug(" 开始继续  LoginViewController。")
                lo.startFlowForPromise()
            }
        })
    }
    

}

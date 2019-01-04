//
//  SMobileChangeViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import ObjectMapper
import O2OA_Auth_SDK

class SMobileChangeViewController: UIViewController {

    @IBOutlet weak var modifyButton: UIButton!
    
    @IBOutlet weak var phoneNumberLabel: UILabel!
    
    override func awakeFromNib() {
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        let mobile = O2AuthSDK.shared.bindDevice()?.mobile
        
        self.phoneNumberLabel.text = mobile
        
        self.modifyButton.layer.borderWidth = 1.0
        self.modifyButton.layer.cornerRadius = 20
        self.modifyButton.layer.masksToBounds = true
        self.modifyButton.layer.borderColor = RGB(251, g: 71, b: 71).cgColor
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func modifyMobileAction(_ sender: UIButton) {
        
        let confirmController = UIAlertController(title: "", message: "", preferredStyle: .alert)
        //属性文本
        let str = "操作前需要验证你的登录密码"
        let titleAttribText = NSMutableAttributedString(string: str)
        titleAttribText.addAttribute(NSAttributedString.Key.font, value: UIFont(name: "PingFangSC-Thin", size: 13.0)!, range: NSMakeRange(0, str.length))

        confirmController.setValue(titleAttribText, forKey: "attributedMessage")
        confirmController.addTextField { (textField) in
            textField.attributedPlaceholder = NSAttributedString(string: "请输入登录密码", attributes:[NSAttributedString.Key.font:UIFont(name: "PingFangSC-Thin", size: 12.0)!])
        }
        let cancelAction = UIAlertAction(title: "取消", style: .cancel) { (action) in
            
        }
        cancelAction.setValue(RGB(51, g: 51, b: 51), forKey: "titleTextColor")
        let okAction = UIAlertAction(title: "确定", style: .destructive) { (action) in
            if  let inputText = confirmController.textFields?[0].text {
                //验证
                let account = O2AuthSDK.shared.myInfo()
                if inputText == account?.mobile {
                    //修改手机号码界面
                    self.performSegue(withIdentifier: "showModifyActionSegue", sender: nil)
                }else{
                    ProgressHUD.showError("密码错误")
                }
            }
            
        }
        confirmController.addAction(cancelAction)
        confirmController.addAction(okAction)
        self.present(confirmController, animated: true, completion: nil)
        
        
    }

   
}

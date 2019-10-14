//
//  SFeedbackViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/13.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

class SFeedbackViewController: UIViewController {
    
    @IBOutlet weak var inputText: UITextView!
    
    
    @IBOutlet weak var feedbackButton: UIButton!

    override func viewDidLoad() {
        super.viewDidLoad()
        feedbackButton.layer.cornerRadius = 8
        feedbackButton.layer.masksToBounds = true
        inputText.backgroundColor = UIColor.white
        inputText.alpha = 1.0
        self.inputText.addDoneButton()
        self.hideKeyboardWhenTappedAround()
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func feedbackButtonAction(_ sender: UIButton) {
        DDLogDebug("提交信息到后台服务器")
        self.showSuccess(title: "意见提交成功")
    }
    
 
    
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

//
//  OOCalendarEditRemarkViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/11.
//  Copyright © 2020 zoneland. All rights reserved.
//
import UIKit

protocol ContentEditBackDelegate {
    func backEditContent(contentHtml:String)
}
class OOCalendarEditRemarkViewController: ZSSRichTextEditor {
    var contentHTML:String?
       
    var backDelegate:ContentEditBackDelegate?
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "保存", style: .plain, target: self, action: #selector(tapSave))
        self.navigationItem.title = "编辑内容"
        
        if let content = contentHTML {
                   self.setHTML(content)
               }
        // Do any additional setup after loading the view.
    }
    

    @IBAction func save(_ sender: Any) {
        self.dismiss(animated: true) {
            ()-> Void in
            print("save.....")
             let dataDict = ["contentHtml" : self.getHTML()]
            NotificationCenter.default.post(name:Notification.Name(rawValue: "RegisterCompletionNotification"),object:nil,userInfo:dataDict)
        }
    }
    
    
    @IBAction func cancel(_ sender: Any) {
        self.dismiss(animated: true, completion: {print("cancel....")})
    }
    
    
    func tapSave(){
        print("dddd");
        
        
    }
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}

//
//  SCustomerServiceViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/13.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class SCustomerServiceViewController: UIViewController {
    
    @IBOutlet weak var sButton1: UIButton!
    
    @IBOutlet weak var sButton2: UIButton!

    @IBOutlet weak var sButton3: UIButton!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        sButton1.layer.cornerRadius = 8
        sButton1.layer.masksToBounds = true
        sButton1.addTarget(self, action: #selector(self.btnAction(sender:)), for: .touchUpInside)
        
        sButton2.layer.cornerRadius = 8
        sButton2.layer.masksToBounds = true
        sButton2.addTarget(self, action: #selector(self.btnAction(sender:)), for: .touchUpInside)
        
        sButton3.layer.cornerRadius = 8
        sButton3.layer.masksToBounds = true
        sButton3.addTarget(self, action: #selector(self.btnAction(sender:)), for: .touchUpInside)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func btnAction(sender:UIButton){
        let alertController = UIAlertController(title: "", message: "客服电话：0571-88480860", preferredStyle: .actionSheet)
        let telAction = UIAlertAction(title: "呼叫", style: .destructive) { (action) in
            
        }
        
        let cancelAction = UIAlertAction(title: "取消", style: .cancel) { (action) in
            
        }
        alertController.addAction(telAction)
        alertController.addAction(cancelAction)
        self.present(alertController, animated: true, completion: nil)
        
    }
    
}

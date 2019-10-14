//
//  SAboutViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/13.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class SAboutViewController: UIViewController {
    
    @IBOutlet weak var aboutLogo: UIImageView!
    
    @IBOutlet weak var verLabel: UILabel!
    
    @IBOutlet weak var aboutLabel: UILabel!
    
    @IBOutlet weak var aboutContentLabel: UILabel!
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.verLabel.text = "版本号:\(O2.appVersionAndBuild!)"
        
        aboutLogo.image = OOCustomImageManager.default.loadImage(.launch_logo)
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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

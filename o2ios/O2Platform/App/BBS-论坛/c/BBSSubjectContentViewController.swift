//
//  BBSSubjectContentViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

protocol SubjectContentEditBackDelegate {
    func backEditContent(contentHtml:String)
}

class BBSSubjectContentViewController: ZSSRichTextEditor {
    
    var myContentHTML:String?
    
    var backDelegate:SubjectContentEditBackDelegate?

    override func viewDidLoad() {
        super.viewDidLoad()
        if let content = myContentHTML {
            self.setHTML(content)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func closeEditWindow(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
    
    
    @IBAction func backEditContentSuper(_ sender: UIBarButtonItem) {
        
        if self.backDelegate != nil {
            self.backDelegate?.backEditContent(contentHtml: self.getHTML())
        }
        self.performSegue(withIdentifier: "backContentSegue", sender: nil)
        
        
    }
    
    
}

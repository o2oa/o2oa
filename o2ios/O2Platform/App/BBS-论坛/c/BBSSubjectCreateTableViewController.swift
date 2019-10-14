//
//  BBSSubjectCreateTableViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/11/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireObjectMapper
import ObjectMapper

import SwiftyJSON
import CocoaLumberjack

class BBSSubjectCreateTableViewController: UITableViewController {

    
    let subjectcategory = ["讨论","投票","新闻","悬赏","辩论","灌水","知识","动态"]
    
    var sectionData:BBSectionListData?
    
    @IBOutlet weak var sectionNameLabel: UILabel!
    
    @IBOutlet weak var subjectCategoryPickView: UIPickerView!

    @IBOutlet weak var subjectTextField: UITextField!
    
    @IBOutlet weak var descTextField: UITextField!
    
    @IBOutlet weak var contentContainerView: UIView!
    
    var myHtmlContent:String?
    
    var pushlishEntity  = PublishSubjectEntity()
    
    @IBOutlet weak var myWebView: UIWebView!
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
      
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
    }
    
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
    }
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.pushlishEntity.sectionId = self.sectionData?.id
        self.pushlishEntity.type = self.subjectcategory[0]
        self.sectionNameLabel.text = self.sectionData?.sectionName
        self.subjectCategoryPickView.dataSource  = self
        self.subjectCategoryPickView.delegate = self
        self.subjectTextField.delegate = self
        self.descTextField.delegate = self
        loadHtmlToWebView()
    }
    
    func loadHtmlToWebView(){
        self.myWebView.loadHTMLString(myHtmlContent == nil ? "" : myHtmlContent!, baseURL: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 2
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        switch section {
        case 0:
            return 4
        case 1:
            return 1
        default:
            return 0
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        switch indexPath.section {
        case 0:
            return 50.0
        case 1:
            return 280.0
        default:
            return 50.0
        }
    }
    
    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = UIView(frame: CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 40))
        if section ==  1 {
            let button = UIButton(type: .custom)
            let attributes = [NSAttributedString.Key.font:UIFont(name: "PingFangSC-Regular", size: 14.0)!,NSAttributedString.Key.foregroundColor:UIColor.white]
            let attrString = NSAttributedString(string: "点击编辑正文内容", attributes: attributes)
            button.setAttributedTitle(attrString, for: .normal)
            button.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
            button.frame = CGRect(x: 10, y: 5, width: 150, height: 30)
            button.addTarget(self, action: #selector(showEditControlAction(_:)), for: .touchUpInside)
            headerView.addSubview(button)
        }else{
            let label = UILabel(frame: CGRect(x: 10, y: 5, width: 150, height: 30))
            label.text = "发帖信息"
            label.font = UIFont(name: "PingFangSC-Regular", size: 14.0)!
            label.textColor = RGB(18, g: 18, b: 18)
            headerView.addSubview(label)
        }
        return headerView
    }
    
    
    @IBAction func publishSubjectAction(_ sender: UIBarButtonItem) {
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(BBSContext.bbsContextKey, query: BBSContext.itemCreateQuery, parameter: nil)
        Alamofire.request(url!, method: .post, parameters: pushlishEntity.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    DispatchQueue.main.async {
                        self.showSuccess(title: "发帖成功")
                        self.performSegue(withIdentifier:"backSectionListSegue", sender: nil)
                    }
                }else{
                    DDLogError(JSON(val).description)
                    DispatchQueue.main.async {
                        self.showError(title: "发帖失败")
                    }
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                DispatchQueue.main.async {
                    self.showError(title: "发帖失败")
                }
            }
        }
    }
    
    @IBAction func unBackEditContentAction(_ segue:UIStoryboardSegue){
        loadHtmlToWebView()
    }
    
    
    @IBAction func showEditControlAction(_ sender: UIButton) {
        self.performSegue(withIdentifier: "editSubjectContentSegue", sender: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "editSubjectContentSegue" {
            let navVC = segue.destination as! ZLNavigationController
            let destVC = navVC.topViewController as! BBSSubjectContentViewController
            destVC.backDelegate = self
            destVC.myContentHTML = self.myHtmlContent
        }
    }

   

}

extension BBSSubjectCreateTableViewController:UITextFieldDelegate{
    func textFieldDidEndEditing(_ textField: UITextField) {
        if textField.isEqual(subjectTextField){
            self.pushlishEntity.title = self.subjectTextField.text
        }else if  textField.isEqual(descTextField) {
            self.pushlishEntity.summary = descTextField.text
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField.isEqual(subjectTextField){
            descTextField.becomeFirstResponder()
        }else if  textField.isEqual(descTextField) {
            self.dismissKeyboard()
        }
        return true
    }
}

extension BBSSubjectCreateTableViewController:UIPickerViewDataSource,UIPickerViewDelegate{
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return self.subjectcategory.count
    }
    
    func pickerView(_ pickerView: UIPickerView, viewForRow row: Int, forComponent component: Int, reusing view: UIView?) -> UIView {
        let titleLabel = UILabel(frame: CGRect(x: 0, y: 0, width: 120, height: 30))
        let title = self.subjectcategory[row]
        titleLabel.text = title
        titleLabel.theme_textColor =  ThemeColorPicker(keyPath: "Base.base_color")
        titleLabel.textAlignment = .left
        titleLabel.font = UIFont(name: "PingFangSC-Regular", size: 14.0)!
        titleLabel.backgroundColor = UIColor.clear

        return titleLabel
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        let title = self.subjectcategory[row]
        DDLogDebug(title)
        self.pushlishEntity.type = title
    }
    
    func pickerView(_ pickerView: UIPickerView, widthForComponent component: Int) -> CGFloat {
        return 120.0
    }
}

extension BBSSubjectCreateTableViewController:SubjectContentEditBackDelegate{
    func backEditContent(contentHtml: String) {
        self.myHtmlContent = contentHtml
        self.pushlishEntity.content = contentHtml
        self.loadHtmlToWebView()
    }
}

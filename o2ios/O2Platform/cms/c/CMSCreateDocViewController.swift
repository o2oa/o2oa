//
//  CMSCreateDocViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/7/8.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import SwiftyJSON
import ObjectMapper
import Eureka
import CocoaLumberjack


class CMSCreateDocViewController: FormViewController {

    var category: CMSWrapOutCategoryList?
    
    var  identityList:[IdentityV2] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        TextRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        ActionSheetRow<String>.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        DateTimeRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_content_textFont
            cell.textLabel?.textColor  = setting_content_textColor
        }
        
        ButtonRow.defaultCellUpdate = {
            cell,row in
            cell.textLabel?.font = setting_item_textFont
            cell.textLabel?.theme_textColor = ThemeColorPicker(keyPath: "Base.base_color")
            
        }
        title = self.category?.categoryName
        loadDepartAndIdentity()
    }
    
    
    func loadDepartAndIdentity(){
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(PersonContext.personContextKey, query: PersonContext.personInfoQuery, parameter: nil)
        Alamofire.request(url!).responseJSON(completionHandler: { response in
            debugPrint(response.result)
            switch response.result {
            case .success(let val):
                let person = Mapper<PersonV2>().map(JSONString: JSON(val)["data"].description)!
                if let identities = person.woIdentityList {
                    self.identityList = identities
                }
                DispatchQueue.main.async {
                    self.showInputUI()
                }
            case .failure(let err):
                DDLogError(err.localizedDescription)
                DispatchQueue.main.async {
                    self.showError(title: "读取身份列表失败")
                }
            }
            
        })
        
    }
    
    func showInputUI(){
        form +++ Section("创建文档")
            <<< TextRow("title") {row in
                row.title = "文档标题"
                row.placeholder = "请输入文档标题"
                }.cellSetup({ (cell, row) in
                    //
                })
            
            <<< ActionSheetRow<IdentityV2>("selectedIdentity") {
                $0.title = "用户身份"
                $0.selectorTitle = "请选择身份"
                $0.options = self.identityList
                if(self.identityList.count > 0){
                    $0.value = self.identityList[0]
                }
                }.cellSetup({ (cell, row) in
                    //cell.height = 50
                })
            
            +++ Section()
            <<< ButtonRow("createButton") { (row:ButtonRow) in
                row.title = "创建"
                }.onCellSelection({ (cell, row) in
                    let titleRow:TextRow = self.form.rowBy(tag:"title")!
                    let identityRow:ActionSheetRow<IdentityV2> = self.form.rowBy(tag:"selectedIdentity")!
                    guard let title = titleRow.value else{
                        self.showError(title: "请输入标题")
                        return
                    }
                    guard let id = identityRow.value  else {
                        self.showError(title: "请选择身份")
                        return
                    }
                    self.createDocument(title, id.distinguishedName!)
                })
    }
    
    func createDocument(_ title: String, _ identity: String) {
        debugPrint("创建文档：\(title), \(identity)")
        var json:[String: Any] = [:]
        json["title"] = title
        json["appId"] = self.category?.appId
        json["categoryId"] = self.category?.id
        json["categoryAlias"] = self.category?.categoryAlias
        json["categoryName"] = self.category?.categoryName
        json["creatorIdentity"] = identity
        json["docStatus"] = "draft"
        json["isNewDocument"] = true
        let doc = CMSCategoryItemData(JSON: json)
        let url = AppDelegate.o2Collect.generateURLWithAppContextKey(CMSContext.cmsContextKey, query: CMSContext.cmsDocumentPost, parameter: nil)
        Alamofire.request(url!, method: .post, parameters: doc?.toJSON(), encoding: JSONEncoding.default, headers: nil).responseJSON { (response) in
            switch response.result {
            case .success(let val):
                let type = JSON(val)["type"]
                if type == "success" {
                    let docId = JSON(val)["data"]["id"].string!
                    DispatchQueue.main.async {
                        self.showSuccess(title: "创建文档成功")
                        self.performSegue(withIdentifier: "openDocument", sender: docId)
                    }
                }else{
                    DispatchQueue.main.async {
                        DDLogError(JSON(val).description)
                        self.showError(title: "创建文档失败")
                    }
                }
            case .failure(let err):
                DispatchQueue.main.async {
                    DDLogError(err.localizedDescription)
                    self.showError(title: "创建文档失败")
                }
            }
            
        }
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
   */
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "openDocument" {
            let destVC = segue.destination as! CMSItemDetailViewController
            destVC.documentId = sender as? String
            destVC.fromCreateDocVC = true
        }
    }
 

}

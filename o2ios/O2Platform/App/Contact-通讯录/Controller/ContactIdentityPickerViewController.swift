//
//  ContactIdentityPickerViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/8/12.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import Promises

class ContactIdentityPickerViewController: UITableViewController {

    // MARK: - 需要传入的参数
    var topUnitList: [String] = [] //顶级组织
    var dutyList: [String] = []  //职务列表 查询身份用的
    var backResultIsUser = false // 这个选择器是身份选择和用户选择共用的 这个参数表示返回的结果是用户还是身份
    
    // MARK: - 私有属性
    private var unitDataList:[OOUnitModel] = []
    private var identityDataList:[OOIdentityModel] = []
    private var breadcrumbList: [ContactBreadcrumbBean] = []
    private var unitParent: String = "-1"
    private var unitParentName: String = "通讯录"
    private let viewModel: ContactPickerViewModel = {
        return ContactPickerViewModel()
    }()
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.loadData()
    }
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 1
        } else if section == 1 {
            return self.unitDataList.count
        }
        return self.identityDataList.count
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 10
    }
    
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 3
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: "breadcrumbViewCell", for: indexPath) as! UnitBreadcrumbViewCell
            cell.refreshBreadcrumb(breadcrumbList: self.breadcrumbList)
            cell.delegate = self
            return cell
        }else if indexPath.section == 1{
            let cell = tableView.dequeueReusableCell(withIdentifier: "unitPickerViewCell", for: indexPath) as! UnitPickerTableViewCell
            let unit = self.unitDataList[indexPath.row]
            cell.loadUnitNotCheck(info: unit)
            cell.delegate = self
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "unitPickerViewCell", for: indexPath) as! UnitPickerTableViewCell
            let identity = self.identityDataList[indexPath.row]
            var isSelected:Bool
            if backResultIsUser {
                isSelected = self.isSelected(value: identity.person!)
            }else {
                isSelected = self.isSelected(value: identity.distinguishedName!)
            }
            cell.loadIdentity(identity: identity, checked: isSelected)
            return cell
        }
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 2 {
            let value = self.identityDataList[indexPath.row].distinguishedName!
            let person = self.identityDataList[indexPath.row].person!
            if backResultIsUser {
                let isSelected = self.isSelected(value: person)
                if isSelected {
                    self.removeSelected(value: person)
                }else {
                    self.addSelected(self.identityDataList[indexPath.row])
                }
            }else {
                let isSelected = self.isSelected(value: value)
                if isSelected {
                    self.removeSelected(value: value)
                }else {
                    self.addSelected(self.identityDataList[indexPath.row])
                }
            }
           
            self.tableView.reloadRows(at: [indexPath], with: .automatic)
        }
        self.tableView.deselectRow(at: indexPath, animated: false)
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    
    //MARK: - private method
    
    //获取组织数据 必须先操作面包屑导航数据
    private func loadData() {
        self.showLoading()
        viewModel.loadUnitList(parent: unitParent, topList: topUnitList)
            .then { (list) -> Promise<[OOIdentityModel]> in
                DDLogDebug("loadUnitList 结果： \(list.count)")
                self.unitDataList = list
                var bean = ContactBreadcrumbBean()
                bean.key =  self.unitParent
                bean.name = self.unitParentName
                bean.level = self.breadcrumbList.count
                self.breadcrumbList.append(bean)
                if self.unitParent != "-1" {
                    return self.viewModel.loadIdentityList(dutyList: self.dutyList, unit: self.unitParent)
                }else {
                    return Promise<[OOIdentityModel]> { fufill,reject in
                        fufill([])
                    }
                }
            }.then({ (result) in
                DDLogDebug("loadIdentityList 结果： \(result.count)")
                self.identityDataList = result
                self.tableView.reloadData()
                self.hideLoading()
            }).catch { (error) in
                DDLogError(error.localizedDescription)
                self.hideLoading()
        }
    }
    
    private func isSelected(value: String) -> Bool {
        if let vc = self.parent as? ContactPickerViewController {
            if backResultIsUser {
                return vc.isSelectedValue(type: .person, value: value)
            }else {
                return vc.isSelectedValue(type: .identity, value: value)
            }
        }
        return false
    }
    
    private func removeSelected(value: String) {
        if let vc = self.parent as? ContactPickerViewController {
            if backResultIsUser {
                vc.removeSelectedValue(type: .person, value: value)
            }else {
                vc.removeSelectedValue(type: .identity, value: value)
            }
            
        }
    }
    
    private func addSelected(_ identity: OOIdentityModel) {
        if let vc = self.parent as? ContactPickerViewController {
            if backResultIsUser {
                vc.addSelectedPerson(id: identity)
            }else {
                vc.addSelectedIdentity(id: identity)
            }
            
        }
    }

}



// MARK: - extension delegate

extension ContactIdentityPickerViewController : UnitPickerNextBtnDelegate {
    //进入下级组织
    func next(unitName: String?, unitDistinguishedName: String?) {
        DDLogDebug("name: \(String(describing: unitName)) dis:\(String(describing: unitDistinguishedName))")
        if unitName == nil || unitDistinguishedName == nil {
            DDLogError("参数为空。。。。。")
        }else {
            self.unitParentName = unitName!
            self.unitParent = unitDistinguishedName!
            self.loadData()
        }
    }
}

extension ContactIdentityPickerViewController: UnitPickerBreadcrumbClickDelegate {
    //点击面包屑导航上的组织按钮
    func breadcrumbTap(name: String, distinguished: String) {
        //清空后面的导航按钮
        for (index,unit) in self.breadcrumbList.enumerated() {
            if unit.key == distinguished {
                let n = self.breadcrumbList.count - index
                self.breadcrumbList.removeLast(n)
                break
            }
        }
        self.unitParentName = name
        self.unitParent = distinguished
        self.loadData()
    }
    
    
}

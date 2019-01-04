//
//  OOMeetingCreateViewController.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/26.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

private let headerIdentifier = "OOMeetingPersonSelectHeaderView"
private let footerIdentifier = "OOMeetingPersonFooterView"
private let personCellIdentifier = "OOMeetingPersonCell"
private let personActionCellIdentifier = "OOMeetingPersonActionCell"

class OOMeetingCreateViewController: UIViewController {
    
    @IBOutlet weak var ooFormView: OOMeetingCreateFormView!
    
    @IBOutlet weak var ooPersonCollectionView: UICollectionView!
    
    private lazy var  viewModel:OOMeetingCreateViewModel = {
       return OOMeetingCreateViewModel()
    }()
    
    @IBOutlet weak var topLayouConstraint: NSLayoutConstraint!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "申请会议"
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "创建", style: .plain, target: self, action: #selector(createMeetingAction(_:)))
        ooFormView.delegate = self
        ooPersonCollectionView.dataSource = self
        ooPersonCollectionView.delegate = self
        ooPersonCollectionView.register(UINib.init(nibName: "OOMeetingPersonCell", bundle: nil), forCellWithReuseIdentifier: personCellIdentifier)
         ooPersonCollectionView.register(UINib.init(nibName: "OMeetingPersonActionCell", bundle: nil), forCellWithReuseIdentifier: personActionCellIdentifier)
        ooPersonCollectionView.register(UINib.init(nibName: "OOMeetingPersonSelectHeaderView", bundle: nil), forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: headerIdentifier)
        ooPersonCollectionView.register(UINib.init(nibName: "OOMeetingPersonFooterView", bundle: nil), forSupplementaryViewOfKind: UICollectionView.elementKindSectionFooter, withReuseIdentifier: footerIdentifier)
//        topLayouConstraint.constant = 66
        ooFormView.ooFormsModels = viewModel.getFormModels()
    }
    
    @objc func createMeetingAction(_ sender:Any){
        let mForm = ooFormView.getFormDataFormBean()
        self.viewModel.selectedPersons.forEach { (p) in
            mForm.invitePersonList.append(p.id!)
        }
        let mBean = OOMeetingFormBean(meetingForm: mForm)
        if mBean.checkFormValues() {
            viewModel.createMeetingAction(mBean, completedBlock: { (resultMessage) in
                if let message = resultMessage {
                    //error fail
                    print(message)
                }else{
                    //successful
                    self.navigationController?.popViewController(animated: true)
                }
            })
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showPersonSelectedSegue" {
            let navVC = segue.destination as! ZLNavigationController
            let destVC = navVC.topViewController as! OOMeetingSelectedPersonController
            destVC.viewModel = self.viewModel
            destVC.delegate = self
            destVC.currentMode = 2
            destVC.title = "选择人员"
        }
    }
    
    
    
}
extension OOMeetingCreateViewController:UICollectionViewDataSource,UICollectionViewDelegate {
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return viewModel.collectionViewNumberOfSections()
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return viewModel.collectionViewNumberOfRowsInSection(section)
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        var cell:UICollectionViewCell?
        if let model = viewModel.collectionViewNodeForIndexPath(indexPath) {
            cell = collectionView.dequeueReusableCell(withReuseIdentifier: personCellIdentifier, for: indexPath)
            let uCell = cell as! (OOMeetingPersonCell & Configurable)
            uCell.viewModel = self.viewModel
            uCell.config(withItem: model)
        } else {
            cell = collectionView.dequeueReusableCell(withReuseIdentifier: personActionCellIdentifier, for: indexPath)
            let uCell = cell as! OOMeetingPersonActionCell
            uCell.delegate = self
        }
        return cell!
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        var reusableView:UICollectionReusableView = UICollectionReusableView(frame: .zero)
        if kind == UICollectionView.elementKindSectionHeader {
            reusableView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: headerIdentifier, for: indexPath)
            let headerView = reusableView as! OOMeetingPersonSelectHeaderView
            headerView.personCount = viewModel.collectionViewNumberOfRowsInSection(indexPath.section) - 1
        }else if kind == UICollectionView.elementKindSectionFooter {
            reusableView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: footerIdentifier, for: indexPath)
        }
        return reusableView
    }
    
}

extension OOMeetingCreateViewController:UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        return CGSize(width: kScreenW, height: 50)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForFooterInSection section: Int) -> CGSize {
        return CGSize(width: kScreenW, height: 30)
    }
}

extension OOMeetingCreateViewController:OOMeetingPersonActionCellDelegate{
    
    func addPersonActionClick(_ sender: UIButton) {
        //执行segue
        self.performSegue(withIdentifier: "showPersonSelectedSegue", sender: nil)
    }
}


// MARK:- Common Back Result
extension OOMeetingCreateViewController:OOCommonBackResultDelegate {
    func backResult(_ vcIdentifiter: String, _ result: Any?) {
        //返回的值
        
        if vcIdentifiter == "OOMeetingMeetingRoomManageController" {
            if let rooms = result as? [OOMeetingRoomInfo] {
                if !rooms.isEmpty {
                    self.ooFormView.setSelectedRoom(rooms.first!)
                }
            }
        }else if vcIdentifiter == "showPersonSelectedSegue" {
            if let persons = result as? [OOPersonModel] {
                if !persons.isEmpty{
                    self.viewModel.selectedPersons = persons
                    self.ooPersonCollectionView.reloadData()
                }
            }
        }
    }
}


// MARK:- OOMeetingCreateFormViewDelegate
extension OOMeetingCreateViewController:OOMeetingCreateFormViewDelegate{
    // MARK:- 人员选择
    func performPersonSelected() {
        
    }
    
    // MARK:- 会议室选择
    func performRoomSelected() {
        let destVC = self.storyboard?.instantiateViewController(withIdentifier: "OOMeetingMeetingRoomManageController") as! OOMeetingMeetingRoomManageController
        destVC.currentMode = 1 //单选
        destVC.delegate = self
        let navVC = ZLNavigationController(rootViewController: destVC)
        self.present(navVC, animated: true, completion: nil)
    }
}



//
//  JCGroupMembersViewController.swift
//  JChat
//
//  Created by deng on 2017/5/10.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage
import RxSwift
import RxCocoa

class JCGroupMembersViewController: UIViewController {
    
    var group: JMSGGroup!

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    fileprivate lazy var searchController: JCSearchController = JCSearchController(searchResultsController: nil)
    fileprivate lazy var searchView: UIView = UIView(frame: CGRect(x: 0, y: 0, width: self.view.width, height: 36))
    private var collectionView: UICollectionView!
    
    fileprivate var count = 0
    fileprivate var sectionCount = 0
    fileprivate lazy var users: [JMSGUser] = []
    
    fileprivate lazy var filteredUsersArray: [JMSGUser] = []
    
    let disposeBag = DisposeBag()
    
    private func _init() {
        self.title = "群成员"
        view.backgroundColor = .white
        definesPresentationContext = true
        
        users = group.memberArray()
        filteredUsersArray = users
        count = filteredUsersArray.count
        
        searchView.backgroundColor = UIColor(netHex: 0xe8edf3)
//        searchController.searchBar.delegate = self
        searchController.hidesNavigationBarDuringPresentation = false
        searchView.addSubview(searchController.searchBar)
        
        let flowLayout = UICollectionViewFlowLayout()
        flowLayout.scrollDirection = .vertical
        flowLayout.minimumInteritemSpacing = 0
        flowLayout.minimumLineSpacing = 0
        flowLayout.headerReferenceSize = CGSize(width: view.width, height: 36)
        collectionView = UICollectionView(frame: view.frame, collectionViewLayout: flowLayout)
        collectionView.bounces = true
        collectionView.register(UICollectionReusableView.self, forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: "kHeaderView")
        
        collectionView.backgroundColor = .white
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.register(JCGroupMemberCell.self, forCellWithReuseIdentifier: "JCGroupMemberCell")
        
        view.addSubview(collectionView)
        
        _ = searchController.searchBar.rx.text.orEmpty
            .throttle(0.5, scheduler: MainScheduler.instance).subscribe(onNext: { (text) in
                self.filter(text)
            })
            .disposed(by: disposeBag)
        _ = searchController.searchBar.rx.cancelButtonClicked.subscribe(onNext: { (_) in
            self.filter("")
        }).disposed(by: disposeBag)
        
    }
    
    fileprivate func filter(_ searchString: String) {
        if searchString.isEmpty || searchString == "" {
            filteredUsersArray = users
            collectionView.reloadData()
            return
        }
        
        filteredUsersArray = _JCFilterUsers(users: users, string: searchString)
        collectionView.reloadData()
    }

}

extension JCGroupMembersViewController: UICollectionViewDelegate, UICollectionViewDataSource {
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return filteredUsersArray.count
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        sizeForItemAtIndexPath indexPath: IndexPath) -> CGSize {
        return CGSize(width:Int(collectionView.frame.size.width / 5), height: 90)
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        return collectionView.dequeueReusableCell(withReuseIdentifier: "JCGroupMemberCell", for: indexPath)
    }
    
    func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
        guard let cell = cell as? JCGroupMemberCell else {
            return
        }
        cell.backgroundColor = .white
        cell.bindDate(user: filteredUsersArray[indexPath.row])
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        let header = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "kHeaderView", for: indexPath)
        if kind == UICollectionView.elementKindSectionHeader {
            header.backgroundColor = UIColor(netHex: 0xe8edf3)
            header.addSubview(searchView)
        }
        return header
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let user = filteredUsersArray[indexPath.row]
        let vc = JCUserInfoViewController()
        vc.user = user
        navigationController?.pushViewController(vc, animated: true)
        searchController.isActive = false
        filter("")
    }
}

//extension JCGroupMembersViewController: UISearchBarDelegate {
//    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
//        filter(searchText)
//    }
//    
//    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
//        filter("")
//    }
//}


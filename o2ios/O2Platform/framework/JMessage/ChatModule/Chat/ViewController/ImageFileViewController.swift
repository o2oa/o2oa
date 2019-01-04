//
//  ImageFileViewController.swift
//  JChat
//
//  Created by 邓永豪 on 2017/8/28.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

var messages: [JMessage] = []

class ImageFileViewController: UIViewController {
    
    var isEditModel: Bool {
        get {
            return isEdit
        } set {
            isEdit = newValue
            selectMessages = []
            collectionView.reloadData()
        }
    }

    var messages: [JMSGMessage] = []
    var selectMessages: [JMSGMessage] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }
    
    func reloadDate() {
        sortMessage()
        collectionView.reloadData()
    }
    
    fileprivate var isEdit = false
    private lazy var layout: UICollectionViewFlowLayout = {
        var layout = UICollectionViewFlowLayout()
//        layout.sectionInset = UIEdgeInsetsMake(5, 5, 5, 5);
        layout.minimumInteritemSpacing = 1
        layout.minimumLineSpacing = 1
        layout.itemSize = CGSize(width: (self.view.width - 4)  / 4, height: (self.view.width - 4) / 4)
        return layout
    }()
    private lazy var collectionView: UICollectionView = {
        var collectionView = UICollectionView(frame: .zero, collectionViewLayout: self.layout)
        collectionView.register(ImageFileCell.self, forCellWithReuseIdentifier:"ImageFileCell")
        collectionView.delegate = self;
        collectionView.dataSource = self;
        collectionView.backgroundColor = UIColor(netHex: 0xe8edf3)
        return collectionView
    }()
    let headerHeight = 30
    let headerIdentifier = "headView"
    
    fileprivate lazy var data: Dictionary<String, [JMSGMessage]> = Dictionary()
    fileprivate lazy var keys: [String] = []
    
    private func _init() {
        view.backgroundColor = UIColor(netHex: 0xe8edf3)
        
        view.addSubview(collectionView)
        collectionView.register(ImageFileHeader.classForCoder(), forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: headerIdentifier)
        
        view.addConstraint(_JCLayoutConstraintMake(collectionView, .left, .equal, view, .left))
        view.addConstraint(_JCLayoutConstraintMake(collectionView, .top, .equal, view, .top))
        view.addConstraint(_JCLayoutConstraintMake(collectionView, .right, .equal, view, .right))
        view.addConstraint(_JCLayoutConstraintMake(collectionView, .bottom, .equal, view, .bottom))
        
    }
    
    func sortMessage() {
        for message in messages {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM";
            let date = Date(timeIntervalSince1970: TimeInterval(message.timestamp.intValue / 1000))
            let key = formatter.string(from: date)
            var array = data[key]
            if array == nil {
                array = [message]
            } else {
                array?.append(message)
            }
            if !keys.contains(key) {
                keys.append(key)
            }
            
            data[key] = array
        }
        keys = keys.sorted(by: { (str1, str2) -> Bool in
            str1 > str2
        })
    }
}


extension ImageFileViewController: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        
        return keys.count
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return data[keys[section]]!.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ImageFileCell", for: indexPath) as! ImageFileCell
        let message = data[keys[indexPath.section]]![indexPath.row]
        cell.bindDate(message)
        cell.isEditMode = isEdit
        if !isEditModel {
            cell.isSelectImage = false
        }
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize{
        return CGSize(width: view.width, height: 26)
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView{
        var v = ImageFileHeader()
        if kind == UICollectionView.elementKindSectionHeader{
            v = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: headerIdentifier, for: indexPath) as! ImageFileHeader
            
            v.titleLabel?.text = keys[indexPath.section]
        }
        return v
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        guard let cell = collectionView.cellForItem(at: indexPath) as? ImageFileCell else {
            return
        }
        let message = data[keys[indexPath.section]]![indexPath.row]
        if cell.isEditMode {
            if cell.isSelectImage {
                selectMessages = selectMessages.filter({ (m) -> Bool in
                    message.msgId != m.msgId
                })
            } else {
                selectMessages.append(message)
            }
            cell.isSelectImage = !cell.isSelectImage
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "kDidSelectFileMessage"), object: nil)
        } else {
            if let image = cell.imageView.image {
                let browserImageVC = JCImageBrowserViewController()
                browserImageVC.imageArr = [image]
                browserImageVC.imgCurrentIndex = 0
                self.present(browserImageVC, animated: true) {}
            }
        }
    }
    
}

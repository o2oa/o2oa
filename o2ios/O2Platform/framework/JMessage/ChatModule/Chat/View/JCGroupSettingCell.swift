//
//  JCGroupSettingCell.swift
//  JChat
//
//  Created by deng on 2017/4/27.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

@objc public protocol JCGroupSettingCellDelegate: NSObjectProtocol {
    @objc optional func clickMoreButton(clickButton button: UIButton)
    @objc optional func clickAddCell(cell: JCGroupSettingCell)
    @objc optional func clickRemoveCell(cell: JCGroupSettingCell)
    @objc optional func didSelectCell(cell: JCGroupSettingCell, indexPath: IndexPath)
}

public class JCGroupSettingCell: UITableViewCell {
    
    weak var delegate: JCGroupSettingCellDelegate?
    
    var group: JMSGGroup!
    
    convenience init(style: UITableViewCell.CellStyle, reuseIdentifier: String?, group: JMSGGroup) {
        self.init(style: style, reuseIdentifier: reuseIdentifier)
        self.group = group
        _init()
    }
    
    override public func awakeFromNib() {
        super.awakeFromNib()
        _init()
    }

    private lazy var moreButton: UIButton = UIButton()
    fileprivate var count = 0
    fileprivate var sectionCount = 0
    fileprivate lazy var users: [JMSGUser] = []
    fileprivate var isMyGroup = false
    fileprivate var currentUserCount = 0

    private lazy var flowLayout: UICollectionViewFlowLayout = {
        let flowLayout = UICollectionViewFlowLayout()
        flowLayout.scrollDirection = .vertical
        flowLayout.minimumInteritemSpacing = 0
        flowLayout.minimumLineSpacing = 0
        return flowLayout
    }()
    private lazy var collectionView: UICollectionView = {
        let collectionView = UICollectionView(frame: CGRect.zero, collectionViewLayout: self.flowLayout)
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.register(JCGroupMemberCell.self, forCellWithReuseIdentifier: "JCGroupMemberCell")
        collectionView.isScrollEnabled = false
        collectionView.backgroundColor = UIColor.clear
        return collectionView
    }()
    
    func bindData(_ group: JMSGGroup) {
        self.group = group
        _getData()
        self.collectionView.reloadData()
    }
    
    private func _init() {
        
        _getData()

        addSubview(collectionView)

        let showMore = isMyGroup ? count > 13 : count > 14
        if showMore {
            moreButton.addTarget(self, action: #selector(_clickMore), for: .touchUpInside)
            moreButton.setTitleColor(UIColor(netHex: 0x999999), for: .normal)
            moreButton.titleLabel?.font = UIFont.systemFont(ofSize: 15)
            moreButton.setTitle("查看更多 >", for: .normal)
            self.addSubview(moreButton)
            
            addConstraint(_JCLayoutConstraintMake(moreButton, .centerX, .equal, self, .centerX))
            addConstraint(_JCLayoutConstraintMake(moreButton, .width, .equal, self, .width))
            addConstraint(_JCLayoutConstraintMake(moreButton, .height, .equal, nil, .notAnAttribute, 26))
            addConstraint(_JCLayoutConstraintMake(moreButton, .bottom, .equal, self, .bottom, -14))
        }
        
        addConstraint(_JCLayoutConstraintMake(collectionView, .left, .equal, self, .left, 15))
        addConstraint(_JCLayoutConstraintMake(collectionView, .right, .equal, self, .right, -15))
        addConstraint(_JCLayoutConstraintMake(collectionView, .top, .equal, self, .top))
        if isMyGroup {
            if count > 8 {
                addConstraint(_JCLayoutConstraintMake(collectionView, .height, .equal, nil, .notAnAttribute, 260))
            } else if count > 3 {
                addConstraint(_JCLayoutConstraintMake(collectionView, .height, .equal, nil, .notAnAttribute, 200))
            } else {
                addConstraint(_JCLayoutConstraintMake(collectionView, .height, .equal, nil, .notAnAttribute, 100))
            }
        } else {
            if count > 9 {
                addConstraint(_JCLayoutConstraintMake(collectionView, .height, .equal, nil, .notAnAttribute, 260))
            } else if count > 4 {
                addConstraint(_JCLayoutConstraintMake(collectionView, .height, .equal, nil, .notAnAttribute, 200))
            } else {
                addConstraint(_JCLayoutConstraintMake(collectionView, .height, .equal, nil, .notAnAttribute, 100))
            }
        }
    }
    
    @objc func _clickMore() {
        delegate?.clickMoreButton?(clickButton: moreButton)
    }
    
    fileprivate func _getData() {
        users = group.memberArray()
        
        currentUserCount = users.count
        
        let user = JMSGUser.myInfo()
//        && group.ownerAppKey == user.appKey!  这里group.ownerAppKey == "" 目测sdk bug
        if group.owner == user.username  {
            isMyGroup = true
        }
        
        count = users.count
        
        if isMyGroup {
            if count > 13 {
                currentUserCount = 13
            }
            if count > 8 {
                sectionCount = 3
            } else if count > 3 {
                sectionCount = 2
            } else {
                sectionCount = 1
            }
        } else {
            if count > 14 {
                currentUserCount = 14
            }
            if count > 9 {
                sectionCount = 3
            } else if count > 4 {
                sectionCount = 2
            } else {
                sectionCount = 1
            }
        }
        
    }

}

extension JCGroupSettingCell: UICollectionViewDelegate, UICollectionViewDataSource {
    public func numberOfSections(in collectionView: UICollectionView) -> Int {
        return sectionCount
    }
    
    public func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if isMyGroup {
            if section == 0 {
                return count >= 3 ? 5 : count + 2
            }
            if section == 1 {
                return  count >= 8 ? 5 : (count - 3)
            }
            return count >= 13 ? 5 : count - 8
        }
        if section == 0 {
            return count >= 4 ? 5 : count + 1
        }
        if section == 1 {
            return  count >= 9 ? 5 : (count - 4)
        }
        return count >= 14 ? 5 : count - 9
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        sizeForItemAtIndexPath indexPath: IndexPath) -> CGSize {
        return CGSize(width:Int(collectionView.frame.size.width / 5), height: Int(collectionView.frame.size.height / CGFloat(sectionCount)))
    }

    public func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        return collectionView.dequeueReusableCell(withReuseIdentifier: "JCGroupMemberCell", for: indexPath)
    }
    
    public func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
        guard let cell = cell as? JCGroupMemberCell else {
            return
        }
        let index = indexPath.section * 5 + indexPath.row
        if index == currentUserCount {
            cell.avator = UIImage.loadImage("com_icon_single_add")
            return
        }
        if index == currentUserCount + 1 {
            cell.avator = UIImage.loadImage("com_icon_remove")
            return
        }
        cell.bindDate(user: users[index])
    }
    
    public func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let index = indexPath.section * 5 + indexPath.row
        if index == currentUserCount {
            delegate?.clickAddCell?(cell: self)
            return
        }
        if index == currentUserCount + 1 {
            delegate?.clickRemoveCell?(cell: self)
            return
        }
        delegate?.didSelectCell?(cell: self, indexPath: indexPath)
    }
}

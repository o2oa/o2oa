//
//  NewMainCollectionView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/12.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import EmptyDataSet_Swift


protocol NewMainCollectionViewDelegate {
    func NewMainCollectionViewItemClickWithApp(_ app:O2App)
    func emptyTapClick()
}

class NewMainCollectionView: NSObject {
    
    
    var itemHeight:Double = 100.0
    
    var itemWidth:Double {
        return Double(SCREEN_WIDTH) / Double(apps.count)
    }
    
    //APP数据列表
    var apps:[O2App] = []
    
    var delegate:NewMainCollectionViewDelegate!
    
    override init() {
        super.init()
    }
}


extension NewMainCollectionView:UICollectionViewDataSource,UICollectionViewDelegateFlowLayout{
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return apps.count > 0 ? 1 : 0
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return apps.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "AppCollectionCell", for: indexPath) as! NewMainAppCollectionViewCell
        cell.app = apps[indexPath.row]
        return cell
    }
    
    //FlowLayout
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: itemWidth, height: itemHeight)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return UIEdgeInsets(top: 1, left: 1, bottom: 1, right: 1)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        return CGSize(width: 0,height: 0)
    }
    
    func collectionView(_ collectionView:UICollectionView,layout collectionViewLayout:UICollectionViewLayout,referenceSizeForFooterInSection section: Int) -> CGSize {
        return CGSize(width: 0,height: 0)
    }
    
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    
    //点击了其中一个
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        //代理有设置执行代理
        if let d = delegate {
            d.NewMainCollectionViewItemClickWithApp(apps[indexPath.row])
        }
    }

}

extension NewMainCollectionView:EmptyDataSetSource,EmptyDataSetDelegate {
    
    func title(forEmptyDataSet scrollView: UIScrollView) -> NSAttributedString? {
        let attributes = [NSAttributedString.Key.font:UIFont(name: "PingFangSC-Regular", size: 16)!,NSAttributedString.Key.foregroundColor: UIColor(hex: "333333")]
        return NSAttributedString(string: "没有配置首页应用，请进入应用管理界面配置", attributes: attributes)
    }
    
    func buttonTitle(forEmptyDataSet scrollView: UIScrollView, for state: UIControl.State) -> NSAttributedString? {
        let attributes = [
            NSAttributedString.Key.font: UIFont(name: "PingFangSC-Regular", size: 15)!,
            NSAttributedString.Key.foregroundColor: UIColor.white,
            NSAttributedString.Key.backgroundColor: O2ThemeManager.color(for: "Base.base_color")!
        ]
        return NSAttributedString(string: "点击进入", attributes: attributes)
    }

    func emptyDataSet(_ scrollView: UIScrollView, didTapButton button: UIButton) {
        O2Logger.debug("emptyDataSet didTap Button")
        if let d = delegate {
            d.emptyTapClick()
        }
    }
}


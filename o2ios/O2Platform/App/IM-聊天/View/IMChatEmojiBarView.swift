//
//  IMChatEmojiBarView.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/11.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack

protocol IMChatEmojiBarClickDelegate {
    func clickEmoji(emoji: String)
}

class IMChatEmojiBarView: UIView {
    
    @IBOutlet weak var collectionView: UICollectionView!
    
    private let emojiList: [String] = {
        var list: [String] = []
        for i in 1...87 {
            if i < 10 {
                list.append("[0\(i)]")
            }else {
                list.append("[\(i)]")
            }
        }
        return list
    }()
    
    var delegate: IMChatEmojiBarClickDelegate? = nil
    
    override func awakeFromNib() {
        collectionView.register(UINib(nibName: "IMChatEmojiItemCell", bundle: nil), forCellWithReuseIdentifier: "IMChatEmojiItemCell")
        collectionView.delegate = self
        collectionView.dataSource = self
        DDLogDebug("list size \(emojiList.count)")
        
    }
}

extension IMChatEmojiBarView: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return emojiList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width:SCREEN_WIDTH / 10, height: 42)
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        if let cell =  collectionView.dequeueReusableCell(withReuseIdentifier: "IMChatEmojiItemCell", for: indexPath) as? IMChatEmojiItemCell {
            cell.setEmoji(emoji: self.emojiList[indexPath.row])
            return cell
        }
        return UICollectionViewCell()
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if delegate != nil {
            delegate?.clickEmoji(emoji: self.emojiList[indexPath.row])
        }
        collectionView.deselectItem(at: indexPath, animated: false)
    }
    
}

//
//  IMChatEmojiItemCell.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/11.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit

class IMChatEmojiItemCell: UICollectionViewCell {
    @IBOutlet weak var emojiImage: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    func setEmoji(emoji: String) {
        let bundle = Bundle().o2EmojiBundle(anyClass: IMChatEmojiItemCell.self)
        let path = o2ImEmojiPath(emojiBody: emoji)
        self.emojiImage.image = UIImage(named: path, in: bundle, compatibleWith: nil)
    }

}

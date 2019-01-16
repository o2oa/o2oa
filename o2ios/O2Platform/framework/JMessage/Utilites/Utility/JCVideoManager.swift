//
//  JCVideoManager.swift
//  JChat
//
//  Created by deng on 2017/4/26.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import AVFoundation
import AVKit

class JCVideoManager {
    
    static func playVideo(data: Data, _ fileType: String = "MOV", currentViewController: UIViewController) {
        let  playVC = AVPlayerViewController()
        
        let filePath = "\(NSHomeDirectory())/Documents/abcd." + fileType
        
        if JCFileManager.saveFileToLocal(data: data, savaPath: filePath) {
            let url = URL(fileURLWithPath: filePath)
            let player = AVPlayer(url: url)
            playVC.player = player
            currentViewController.present(playVC, animated: true, completion: nil)
        }
    }
    
    static func playVideo(path: String, currentViewController: UIViewController) {
        let  playVC = AVPlayerViewController()
        let url = URL(fileURLWithPath: path)
        let player = AVPlayer(url: url)
        playVC.player = player
        currentViewController.present(playVC, animated: true, completion: nil)
    }
    
    static func getFristImage(data: Data) -> UIImage? {
        let filePath = "\(NSHomeDirectory())/Documents/getImage.MOV"
        if !JCFileManager.saveFileToLocal(data: data, savaPath: filePath) {
            return nil
        }
        let videoURL = URL(fileURLWithPath: filePath)
        let avAsset = AVAsset(url: videoURL)
        let generator = AVAssetImageGenerator(asset: avAsset)
        generator.appliesPreferredTrackTransform = true
        let time = CMTimeMakeWithSeconds(0.0,preferredTimescale: 600)
        var actualTime = CMTimeMake(value: 0,timescale: 0)
        do {
            let imageRef = try generator.copyCGImage(at: time, actualTime: &actualTime)
            let frameImg = UIImage(cgImage: imageRef)
            return frameImg
        } catch {
            return UIImage.createImage(color: .gray, size: CGSize(width: 160, height: 120))
        }
    }

}

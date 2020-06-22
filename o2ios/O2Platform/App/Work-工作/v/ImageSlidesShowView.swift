//
//  ImageSlidesShowView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/12.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import ImageSlideshow
import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import ObjectMapper
import CocoaLumberjack

protocol ImageSlidesShowViewDelegate {
    func ImageSlidesShowClick(taskImageshowEntity: TaskImageshowEntity)
}

class ImageSlidesShowView: UIView {

    private lazy var imageSlideshow: ImageSlideshow = ImageSlideshow()

    var delegate: ImageSlidesShowViewDelegate?

    var imageshowEntitys: [TaskImageshowEntity] = [] {

        didSet {
            self.imageURLS.removeAll(keepingCapacity: true)
            imageshowEntitys.forEach { (taskImageshowEntity) in
                let imageURL = AppDelegate.o2Collect.generateURLWithAppContextKey(FileContext.fileContextKey, query: FileContext.fileDownloadNoStreamIdQuery, parameter: ["##id##": taskImageshowEntity.picId as AnyObject])
                DDLogDebug("hot image url : \(String(describing: imageURL))")
                let afurl = O2AlamofireSource(urlString: imageURL!)
                self.imageURLS.append(afurl!)
            }
            imageSlideshow.setImageInputs(imageURLS)

        }
    }

    fileprivate var imageURLS: [O2AlamofireSource] = []

    override init(frame: CGRect) {
        super.init(frame: frame)
        self.imageSlideshow.frame = CGRect(x: 0, y: 0, width: frame.width, height: frame.height)
        self.imageSlideshow.backgroundColor = UIColor.white
        self.imageSlideshow.slideshowInterval = 6.0
        self.imageSlideshow.preload = .all
        self.imageSlideshow.pageControlPosition = PageControlPosition.insideScrollView
        self.imageSlideshow.pageControl.currentPageIndicatorTintColor = navbar_barTint_color
        self.imageSlideshow.pageControl.pageIndicatorTintColor = UIColor.lightGray
        self.imageSlideshow.contentScaleMode = UIView.ContentMode.scaleToFill
        self.imageSlideshow.addTapGesture(target: self, action: #selector(imageSlideshowClick(sender:)))
        self.addSubview(self.imageSlideshow)

    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }


    @objc public func imageSlideshowClick(sender: ImageSlideshow?) {
        //print(self.imageSlideshow.currentPage)
        if delegate != nil {
            let entity = self.imageshowEntitys[self.imageSlideshow.currentPage]
            self.delegate?.ImageSlidesShowClick(taskImageshowEntity: entity)
        }

    }


}

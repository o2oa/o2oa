//
//  ZoomImageView.swift
//  O2Platform
//
//  Created by FancyLou on 2019/11/7.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit


class ZoomImageView: UIScrollView {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.setupView()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.setupView()
    }
    
    var _imageView: UIImageView?
    
    func setupView()  {
        self.delegate = self
        self.showsHorizontalScrollIndicator = false
        self.showsVerticalScrollIndicator = false
        //设置最大放大倍数
        self.minimumZoomScale = 1.0;
        self.maximumZoomScale = 2.0;
        //粘贴一张图片
        self._imageView = UIImageView(frame: CGRect(x: 0.0, y: 0.0, width: self.frame.size.width, height: self.frame.size.height))
        self._imageView!.center = CGPoint(x: self.frame.size.width/2, y: self.frame.size.height/2);
        self._imageView!.contentMode = .scaleAspectFit;
        //添加双击事件
        let doubleTapGesture = UITapGestureRecognizer(target: self, action: #selector(handleDoubleTap))
        doubleTapGesture.numberOfTapsRequired = 2
        self._imageView!.addGestureRecognizer(doubleTapGesture)
        self._imageView!.isUserInteractionEnabled = true
        self.addSubview(self._imageView!)
    }
    
    @objc private func handleDoubleTap(gesture: UIGestureRecognizer) {
        var zoomScale = self.zoomScale
        if zoomScale == 1.0 {
            zoomScale = 2.0
        }else {
            zoomScale = 1.0
        }
        let zoomRect = self.zoomRectForScale(scale: zoomScale, center: gesture.location(in: gesture.view))
        self.zoom(to: zoomRect, animated: true)
    }
    
    private func zoomRectForScale(scale: CGFloat, center: CGPoint) -> CGRect{
        let width = self.frame.size.width  / scale
        let height = self.frame.size.height / scale
        return CGRect(x: center.x - (width  / 2.0), y: center.y - (height / 2.0), width: width, height: height)
    }
    
}

extension ZoomImageView: UIScrollViewDelegate {
    
}

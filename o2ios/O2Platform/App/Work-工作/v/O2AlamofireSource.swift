//
//  O2Alam.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/12/16.
//  Copyright © 2016年 zoneland. All rights reserved.
//
import ImageSlideshow
import Alamofire
import AlamofireImage
import CocoaLumberjack

public class O2AlamofireSource: NSObject, InputSource {
    var url: URL
    
    public init(url: URL) {
        self.url = url
        super.init()
    }
    
//    var imageHeight:CGFloat {
//        get {
//            var tmp = CGFloat(142.0)
//            let deviceName = UIDevice.deviceModelReadable()
//            switch deviceName {
//            case "iPhone 5","iPhone 5C","iPhone 5S","iPhone SE":
//                tmp = CGFloat(142.0)
//            case "iPhone 6","iPhone 6S","iPhone 7":
//                tmp = CGFloat(172.0)
//            case "iPhone 6 Plus","iPhone 6S Plus","iPhone 7 Plus":
//                tmp = CGFloat(202.0)
//            default:
//                tmp = CGFloat(142.0)
//            }
//            return tmp
//        }
//    }
    
    public init?(urlString: String) {
        if let validUrl = URL(string: urlString) {
            self.url = validUrl
            super.init()
        } else {
            return nil
        }
    }
    
    public func load(to imageView: UIImageView, with callback: @escaping (_ image: UIImage?) -> Void){
//        let frame = imageView.bounds
//        if  frame.width <= 0 || frame.height <= 0 {
//            let height = SCREEN_WIDTH / 2
//            imageView.bounds = CGRect(x: 0, y: 0, w: SCREEN_WIDTH, h: height)
//        }
//        imageView.hnk_setImageFromURL(self.url, placeholder: UIImage(named: "pic_lunbo_1"), format: nil, failure: { (error) in
//            DDLogError("下载图片异常\(String(describing: error))")
//            callback(nil)
//        }) { (newImage) in
//            imageView.image = newImage
//            callback(newImage)
//        }
        
        let config = ImageDownloader.defaultURLSessionConfiguration()
        let imageDownloader = ImageDownloader(configuration: config,
                                              downloadPrioritization: .fifo,
                                              maximumActiveDownloads: 10,
                                              imageCache: AutoPurgingImageCache())
        //ImageFilter
        let height = SCREEN_WIDTH / 2
        let imageFilter = ScaledToSizeFilter(size: CGSize(width: SCREEN_WIDTH, height: height))
        //placeholderImage

        imageView.af_imageDownloader = imageDownloader
        //        let tempURL = URL(string: "http://d.ifengimg.com/mw978_mh598/p0.ifengimg.com/cmpp/2018/03/21/06/530de4b1-3127-4a9b-b764-efa3444d04e0_size275_w1024_h768.jpg")!
        var urlRequest = URLRequest(url: self.url)
        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
        imageView.af_setImage(withURLRequest: urlRequest, placeholderImage: UIImage(named: "pic_lunbo_1"), filter: imageFilter, progress: nil, progressQueue: DispatchQueue.main, imageTransition: .crossDissolve(0.1), runImageTransitionIfCached: false) { (response) in
            if let value = response.result.value {
                let newImage = value
                // let newImage = UIImage.scaleTo(image: value, w: SCREEN_WIDTH , h: self.imageHeight)
                imageView.image = newImage
                callback(newImage)
            }
        }
    }
    
//    public func load(to imageView: UIImageView, with callback: @escaping (UIImage) -> ()) {
//
//        let config = ImageDownloader.defaultURLSessionConfiguration()
//        let imageDownloader = ImageDownloader(configuration: config,
//                                              downloadPrioritization: .fifo,
//                                              maximumActiveDownloads: 10,
//                                              imageCache: AutoPurgingImageCache())
//        //ImageFilter
//        let imageFilter = ScaledToSizeFilter(size: CGSize(width:SCREEN_WIDTH,height:self.imageHeight))
//        //placeholderImage
//
//        imageView.af_imageDownloader = imageDownloader
////        let tempURL = URL(string: "http://d.ifengimg.com/mw978_mh598/p0.ifengimg.com/cmpp/2018/03/21/06/530de4b1-3127-4a9b-b764-efa3444d04e0_size275_w1024_h768.jpg")!
//        var urlRequest = URLRequest(url: self.url)
//        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
//        imageView.af_setImage(withURLRequest: urlRequest, placeholderImage: UIImage(named: "pic_lunbo_1"), filter: imageFilter, progress: nil, progressQueue: DispatchQueue.main, imageTransition: .crossDissolve(0.1), runImageTransitionIfCached: false) { (response) in
//            if let value = response.result.value {
//                let newImage = value
//                // let newImage = UIImage.scaleTo(image: value, w: SCREEN_WIDTH , h: self.imageHeight)
//                imageView.image = newImage
//                callback(newImage)
//            }
//        }
//    }
    
}

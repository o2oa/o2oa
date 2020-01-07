//
//  O2SlideImageAFSource.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/29.
//  Copyright © 2019 zoneland. All rights reserved.
//

import ImageSlideshow
import Alamofire
import AlamofireImage
import CocoaLumberjack

public class O2SlideImageAFSource: NSObject, InputSource {
    var url: URL
    
    public init(url: URL) {
        self.url = url
        super.init()
    }
    
    public init?(urlString: String) {
        if let validUrl = URL(string: urlString) {
            self.url = validUrl
            super.init()
        } else {
            return nil
        }
    }
    
    public func load(to imageView: UIImageView, with callback: @escaping (_ image: UIImage?) -> Void){
        
        let config = ImageDownloader.defaultURLSessionConfiguration()
        let imageDownloader = ImageDownloader(configuration: config,
                                              downloadPrioritization: .fifo,
                                              maximumActiveDownloads: 10,
                                              imageCache: AutoPurgingImageCache())
        //ImageFilter
        let imageFilter = AspectScaledToFillSizeFilter(size: CGSize(width: SCREEN_WIDTH, height: SCREEN_HEIGHT))
        //placeholderImage
        
        imageView.af_imageDownloader = imageDownloader
        var urlRequest = URLRequest(url: self.url)
        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
        imageView.af_setImage(withURLRequest: urlRequest, placeholderImage: UIImage(named: "file_unknown_icon"), filter: imageFilter, progress: nil, progressQueue: DispatchQueue.main, imageTransition: .crossDissolve(0.1), runImageTransitionIfCached: false) { (response) in
            print(response)
            if let value = response.result.value {
                let newImage = value
                imageView.image = newImage
                callback(newImage)
            }
        }
    }
    
    
}


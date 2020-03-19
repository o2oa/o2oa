//
//  JCDocumentViewController.swift
//  JChat
//
//  Created by deng on 2017/7/24.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import WebKit

class JCDocumentViewController: UIViewController, CustomNavigation {
    
    var filePath: String!
    var fileType: String!
    var fileData: Data!

    override func viewDidLoad() {
        super.viewDidLoad()
        _init()
    }

    fileprivate lazy var webView: UIWebView = {
        var webView = UIWebView(frame: .zero)
        webView.delegate = self
        webView.backgroundColor = .white
        webView.scrollView.isDirectionalLockEnabled = true
        webView.scrollView.showsHorizontalScrollIndicator = false
        return webView
    }()
    private var fileUrl: URL?
    private lazy var documentInteractionController = UIDocumentInteractionController()
    fileprivate lazy var leftButton = UIButton(frame: CGRect(x: 0, y: 0, width: 60, height: 65 / 3))
    
    private func _init() {
        view.backgroundColor = .white
        automaticallyAdjustsScrollViewInsets = false
        view.addSubview(webView)
        
        _setupNavigation()
        
        view.addConstraint(_JCLayoutConstraintMake(webView, .left, .equal, view, .left))
        view.addConstraint(_JCLayoutConstraintMake(webView, .right, .equal, view, .right))
        view.addConstraint(_JCLayoutConstraintMake(webView, .top, .equal, view, .top, 64))
        view.addConstraint(_JCLayoutConstraintMake(webView, .bottom, .equal, view, .bottom))
        
        let encodeWord = filePath.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)
        let url = URL(fileURLWithPath: encodeWord!)
        let fileName = url.lastPathComponent
        
        let path = "\(NSHomeDirectory())/tmp/" + fileName + "." + fileType
        if JCFileManager.saveFileToLocal(data: fileData, savaPath: path) {
            fileUrl = URL(fileURLWithPath: path)
            do {
                let string = try String(contentsOf: fileUrl!, encoding: .utf8)
                webView.loadHTMLString(string, baseURL: nil)
            } catch {
                let request = URLRequest(url: fileUrl!)
                webView.loadRequest(request)
            }
        }
    }
    
    private func _setupNavigation() {
        let navButton = UIButton(frame: CGRect(x: 0, y: 0, width: 18, height: 18))
        navButton.setImage(UIImage.loadImage("com_icon_file_more"), for: .normal)
        navButton.addTarget(self, action: #selector(_openFile), for: .touchUpInside)
        let item1 = UIBarButtonItem(customView: navButton)
        navigationItem.rightBarButtonItems =  [item1]
        
        customLeftBarButton(delegate: self)
    }

    func _openFile() {
        guard let url = fileUrl else {
            return
        }
        documentInteractionController.url = url
        documentInteractionController.delegate = self
        documentInteractionController.presentOptionsMenu(from: .zero, in: self.view, animated: true)
    }

}

extension JCDocumentViewController: UIDocumentInteractionControllerDelegate {
    func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        return self
    }
    func documentInteractionControllerViewForPreview(_ controller: UIDocumentInteractionController) -> UIView? {
        return self.view
    }
    
    func documentInteractionControllerRectForPreview(_ controller: UIDocumentInteractionController) -> CGRect {
        return self.view.frame
    }
}

extension JCDocumentViewController: UIWebViewDelegate {
    func webViewDidFinishLoad(_ webView: UIWebView) {
        print("webViewDidFinishLoad")
        
    }
    func webView(_ webView: UIWebView, didFailLoadWithError error: Error) {
        print(error.localizedDescription)
    }
}

extension JCDocumentViewController: UIGestureRecognizerDelegate {
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        return true
    }
}

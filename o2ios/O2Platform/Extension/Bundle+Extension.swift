//
//  Bundle+Extension.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/23.
//  Copyright © 2019 zoneland. All rights reserved.
//

import Foundation


/**
 *  当调用onLanguage后替换掉mainBundle为当前语言的bundle
 */
//private  let _bundle:UnsafePointer<Void> =  unsafeBitCast(0,to: UnsafePointer<Void>.self)

class BundleEx: Bundle {

    override func localizedString(forKey key: String, value: String?, table tableName: String?) -> String {
        if let bundle = languageBundle() {
            return bundle.localizedString(forKey: key, value: value, table: tableName)
        } else {
            return super.localizedString(forKey: key, value: value, table: tableName)
        }
    }


}

extension Bundle {
//    private struct Static {
//        static let onceToken: Static = { Static() }()
//    }
//
    func onLanguage() {
        //替换NSBundle.mainBundle()为自定义的BundleEx
        DispatchQueue.once(token: "language") {
            object_setClass(Bundle.main, BundleEx.self)
        }
    }

    //当前语言的bundle
    func languageBundle() -> Bundle? {
        return Languager.standardLanguager().currentLanguageBundle
    }

    //o2 表情包读取
    func o2EmojiBundle(anyClass: AnyClass) -> Bundle {
        var bundle: Bundle = Bundle.main
        if let resource = Bundle(for: anyClass.self).path(forResource: "O2Emoji", ofType: "bundle") {
            bundle = Bundle(path: resource) ?? Bundle.main
        }
        return bundle
    }
}


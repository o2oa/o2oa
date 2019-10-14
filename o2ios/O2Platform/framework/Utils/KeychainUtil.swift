//
//  KeychanUtil.swift
//  CommonUtil
//
//  Created by lijunjie on 15/11/12.
//  Copyright © 2015年 lijunjie. All rights reserved.
//

import Foundation

public enum CommonUtilError: Error {
    case KeychainGetError
    case KeychainSaveError
    case KeychainDeleteError
    case KeychainUpdateError
}

public class KeychainUtil {
    
    static let share = KeychainUtil()
    
    private init () {}
    
    public func getBaseKeychainQueryWithAccount(account: String, service: String, accessGroup: String) -> [NSString : AnyObject] {
        var res = [NSString : AnyObject]()
        res[kSecClass] = kSecClassGenericPassword
        res[kSecAttrAccount] = account as AnyObject?
        res[kSecAttrService] = service as AnyObject?
        
        if !accessGroup.isEmpty {
            #if TARGET_IPHONE_SIMULATOR
                /*
                如果在模拟器上运行，则忽略accessGroup
                模拟器运行app没有签名, 所以这里没有accessGroup
                在模拟器上运行时，所有的应用程序可以看到所有钥匙串项目
                如果SecItem包含accessGroup,当SecItemAdd and SecItemUpdate时，将返回-25243 (errSecNoAccessForItem)
                */
            #else
                res[kSecAttrAccessGroup] = accessGroup as AnyObject?
            #endif
        }
        return res
    }
    
    public func getDataWithAccount(account: String, service: String, accessGroup: String) -> NSData? {
        var res: [NSString : AnyObject] = self.getBaseKeychainQueryWithAccount(account: account, service: service, accessGroup: accessGroup)
        res[kSecMatchCaseInsensitive] = kCFBooleanTrue
        res[kSecMatchLimit] = kSecMatchLimitOne
        res[kSecReturnData] = kCFBooleanTrue
        var queryErr: OSStatus = noErr
        var udidValue: NSData?
        var inTypeRef : AnyObject?
        
        queryErr = SecItemCopyMatching(res as CFDictionary, &inTypeRef)
        udidValue = inTypeRef as? NSData
        if (queryErr != errSecSuccess) {
            return nil
        }
        return udidValue
    }
    
    public func saveData(data: NSData, account: String, service:String, accessGroup: String) throws {
        var query : [NSString : AnyObject] = self.getBaseKeychainQueryWithAccount(account: account, service: service, accessGroup: accessGroup)
        query[kSecAttrLabel] = "" as AnyObject?
        query[kSecValueData] = data
        var writeErr: OSStatus = noErr
        writeErr = SecItemAdd(query as CFDictionary, nil)
        if writeErr != errSecSuccess {
            throw CommonUtilError.KeychainSaveError
        }
    }
    
    public func deleteDataWithAccount(account: String, service: String, accessGroup: String) throws {
        let dictForDelete: [NSString : AnyObject] = self.getBaseKeychainQueryWithAccount(account: account, service: service, accessGroup: accessGroup)
        var deleteErr: OSStatus = noErr
        
        deleteErr = SecItemDelete(dictForDelete as CFDictionary)
        
        if(deleteErr != errSecSuccess){
            throw CommonUtilError.KeychainDeleteError
        }
    }
    
    public func updateData(data: NSData, account:String, service:String, accessGroup:String) throws {
        var dictForQuery: [NSString : AnyObject] = self.getBaseKeychainQueryWithAccount(account: account, service: service, accessGroup: accessGroup)
        dictForQuery[kSecMatchCaseInsensitive] = kCFBooleanTrue
        dictForQuery[kSecMatchLimit] = kSecMatchLimitOne
        dictForQuery[kSecReturnData] = kCFBooleanTrue
        dictForQuery[kSecReturnAttributes] = kCFBooleanTrue
        var queryResultRef: AnyObject?
        SecItemCopyMatching(dictForQuery as CFDictionary, &queryResultRef)
        if queryResultRef != nil {
            var dictForUpdate: [NSString : AnyObject] = self.getBaseKeychainQueryWithAccount(account: account, service: service, accessGroup: accessGroup)
            dictForUpdate[kSecValueData] = data
            var updateErr: OSStatus = noErr
            updateErr = SecItemUpdate(dictForQuery as CFDictionary, dictForUpdate as CFDictionary)
            if (updateErr != errSecSuccess) {
                print("Update KeyChain Item Error!!! Error Code:%ld", updateErr)
                throw CommonUtilError.KeychainUpdateError
            }
        }
    }
}

public let SharedKeychanUtil: KeychainUtil = KeychainUtil.share

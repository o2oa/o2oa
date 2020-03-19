//
//  BaseCoreData.swift
//  CommonUtil
//
//  Created by lijunjie on 15/12/7.
//  Copyright © 2015年 lijunjie. All rights reserved.
//

import Foundation
import CoreData

public class BaseCoreData {
    
    private var PSC: NSPersistentStoreCoordinator?
    private var persistentStoreCoordinator: NSPersistentStoreCoordinator? {
        get {
            if managedObjectModel == nil {
                return nil
            }
            
            if PSC != nil {
                return PSC
            }
            
            let databasePath = self.getPath()
            let storeURL = NSURL.fileURL(withPath: databasePath)
            
            PSC = NSPersistentStoreCoordinator.init(managedObjectModel:managedObjectModel!)
            
            do {
                try persistentStore = PSC?.addPersistentStore(ofType: storeType, configurationName: nil, at: storeURL, options: nil)
            } catch {
                print("Unresolved error")
                abort()
            }
            
            if persistentStore == nil {
                print("Unresolved error")
                abort()
            }
            
            return PSC
        }
        set {
            self.persistentStoreCoordinator = newValue
        }
    }
    
    private var MOM: NSManagedObjectModel?
    private var managedObjectModel: NSManagedObjectModel? {
        get {
            if modelFileName.isEmpty {
                return nil
            }
            
            if MOM != nil {
                return MOM
            }
            
            let rag = modelFileName.range(of:".momd")
            var copymodFileName = modelFileName
            
            if  let r =  rag {
                copymodFileName = modelFileName.substring(to:r.upperBound)
            }
            let modelURL = Bundle.main.url(forResource: copymodFileName, withExtension: "momd")
            MOM = NSManagedObjectModel(contentsOf: modelURL!)
            return MOM
        }
        set {
            MOM = newValue
        }
    }
    public var managedObjectContext: NSManagedObjectContext?
    private var persistentStore: NSPersistentStore?
    private var modelFileName: String = ""
    private var savePath: String = ""
    private var saveName: String = ""
    private var storeType: String = ""
    
    public init(modelFileName: String, savePath: String, saveName: String, storeType: String) {
        self.modelFileName = modelFileName
        self.saveName = saveName
        self.savePath = savePath
        self.storeType = storeType
        self.initManagedObjectContext()
    }
    
    public func cleanUp() {
        persistentStoreCoordinator = nil
        managedObjectModel = nil
        managedObjectContext = nil
        persistentStore = nil
    }
    
    public func initManagedObjectContext() {
        if managedObjectModel == nil {
            return
        }
        
        if persistentStoreCoordinator == nil {
            return
        }
        
        if managedObjectContext == nil {
            managedObjectContext = NSManagedObjectContext.init(concurrencyType: .privateQueueConcurrencyType)
            managedObjectContext?.persistentStoreCoordinator = persistentStoreCoordinator
            managedObjectContext?.mergePolicy = NSMergeByPropertyStoreTrumpMergePolicy
        }
    }
    
    private func getPath() -> String {
        let fileMgr: FileManager = FileManager.default
        
        if !fileMgr.fileExists(atPath: savePath) {
            do {
                try fileMgr.createDirectory(atPath: savePath, withIntermediateDirectories: true, attributes: nil)
            } catch {
                print("创建文件失败！")
            }
        }
        return (savePath as NSString).appendingPathComponent(saveName)
    }
    
    public func performBlock(block: @escaping () -> Void) {
        let moc = managedObjectContext
        moc?.perform(block)
    }
    
    public func performBlock(block: @escaping (_ moc: NSManagedObjectContext) -> Void, complete: @escaping () -> Void) {
        let moc = managedObjectContext
        moc?.perform({ () -> Void in
            block(moc!)
            DispatchQueue.main.async(execute: complete)
        })
    }
    
    public func safelySaveContextMOC() {
        self.managedObjectContext?.performAndWait({ () -> Void in
            self.saveContextMOC()
        })
    }
    
    public func unsafelySaveContextMOC() {
        self.managedObjectContext?.perform({ () -> Void in
            self.saveContextMOC()
        })
    }
    
    private func saveContextMOC() {
        self.saveContext(savedMoc: self.managedObjectContext!)
    }
    
    private func saveContext(savedMoc:NSManagedObjectContext) -> Bool {
        var contextToSave:NSManagedObjectContext? = savedMoc
        while (contextToSave != nil) {
            var success = false
            do {
                let s: NSSet = (contextToSave?.insertedObjects)! as NSSet
                try contextToSave?.obtainPermanentIDs(for: s.allObjects as! [NSManagedObject])
            } catch {
                print("保存失败！！！")
                return false
            }
            if contextToSave?.hasChanges == true {
                do {
                    try contextToSave?.save()
                    success = true
                } catch {
                    print("Saving of managed object context failed")
                    success = false
                }
            } else {
                success = true
            }
            if success == false {
                return false
            }
            if contextToSave!.parent == nil && contextToSave!.persistentStoreCoordinator == nil {
                print("Reached the end of the chain of nested managed object contexts without encountering a persistent store coordinator. Objects are not fully persisted.")
                return false
            }
            contextToSave = contextToSave?.parent
        }
        return true
    }
}

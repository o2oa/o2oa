//
//  Stack.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/14.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
class Stack<T> {
    
    fileprivate var top: Int
    fileprivate var items: [T]
    var size:Int
    
    init() {
        top = -1
        items = [T]()
        size = 100
    }
    
    init(size:Int) {
        top = -1
        items = [T]()
        self.size = size
    }
    
    func push(_ item: T) -> Bool {
        if !isFull() {
            items.append(item)
            top+=1
            return true
        }
        print("Stack is full! Could not pushed.")
        return false
    }
    
    func pop() -> T? {
        if !isEmpty() {
            top-=1
            return items.removeLast()
        }
        
        print("Stack is empty! Could not popped.")
        return nil
    }
    
    func peek() -> T? {
        if !isEmpty() {
            return items.last
        }
        return nil
    }
    
    func isEmpty() -> Bool {
        return top == -1
    }
    
    func isFull() -> Bool {
        return top == (size - 1)
    }
    
    func count() -> Int {
        return (top + 1)
    }
    
    func printStack() {
//        for var i = items.count-1; i>=0; i-=1 {
//            print("|  \(items[i])  |")
//        }
//        print(" ------ ")
//        print("\n\n")
    }
}

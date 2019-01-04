//
//  OOTableViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/8.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit


public protocol Configurable {
    func config(withItem item: Any?)
}

open class OOTableViewController<T,Cell:UITableViewCell>:UITableViewController where Cell:Configurable {
    
    private let cellIdentifier = String(describing: Cell.self)
    
    var data = [T]() {
        didSet {
            tableView.reloadData()
            if tableView.numberOfRows(inSection: 0) > 0 {
                tableView.scrollToRow(at: IndexPath.init(row: 0, section: 0) , at: .top, animated: true)
            }
        }
    }
    
    init() {
        super.init(nibName:nil,bundle:nil)
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    open override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        navigationController?.navigationBar.isTranslucent = false
    }
    
    override open func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(Cell.self, forCellReuseIdentifier: cellIdentifier)
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 60
    }
    
    override open func tableView(_ tableView:UITableView,numberOfRowsInSection section:Int) -> Int {
        return data.count
    }
    
    override open func tableView(_ tableView:UITableView,cellForRowAt indexPath:IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! Cell
        cell.config(withItem: data[indexPath.row])
        return cell
    }
    
}

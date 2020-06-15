//
//  O2WebsocketManager.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/1.
//  Copyright © 2020 zoneland. All rights reserved.
//

import Foundation
import Starscream
import CocoaLumberjack


class O2WebsocketManager {

    static let instance: O2WebsocketManager = {
        return O2WebsocketManager()
    }()

    private init() { }

    private var socket: WebSocket?


    // wsUrl: ws://xxx.o2oa.net:20020/x_message_assemble_communicate/ws/collaboration?x-token=xxxxxxx
    //开启连接
    func startConnect(wsUrl: String, delegate: WebSocketDelegate) {
        let request = URLRequest(url: URL(string: wsUrl)!)
        socket = WebSocket(request: request)
        socket?.delegate = delegate
        socket?.connect()
    }

    //发送消息
    func send(msg: String) {
        guard let s = socket else {
            DDLogError("socket 为空 还未启动 无法发送消息")
            return
        }
        s.write(string: msg)
    }

    //关闭连接
    func closeConnect() {
        guard let s = socket else {
            DDLogError("socket 为空 无需关闭")
            return
        }
        s.disconnect(closeCode: CloseCode.goingAway.rawValue)
        socket = nil
    }

}




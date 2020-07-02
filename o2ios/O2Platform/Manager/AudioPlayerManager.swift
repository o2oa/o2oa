//
//  AudioPlayerManager.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/17.
//  Copyright © 2020 zoneland. All rights reserved.
//

import AVFoundation

protocol AudioPlayerManagerDelegate {
    func didAudioPlayerBeginPlay(_ AudioPlayer: AVAudioPlayer)
    func didAudioPlayerStopPlay(_ AudioPlayer: AVAudioPlayer)
    func didAudioPlayerPausePlay(_ AudioPlayer: AVAudioPlayer)
}

class AudioPlayerManager: NSObject {
    static let  shared: AudioPlayerManager = {
        return AudioPlayerManager()
    }()
    
    private override init() {super.init()}
    
    var delegate: AudioPlayerManagerDelegate?
    var player: AVAudioPlayer!
    
    
    
    func managerAudioWithData(_ data:Data, toplay:Bool) {
        if toplay {
            playAudioWithData(data)
        } else {
            pausePlayingAudio()
        }
    }
    
    func playAudioWithData(_ voiceData:Data) {
        do {
            try AVAudioSession.sharedInstance().setCategory(.playAndRecord, mode: .default, options: .defaultToSpeaker)
        } catch let error as NSError {
            print("set category fail \(error)")
        }
        
        if player != nil {
            player.stop()
            player = nil
        }
        
        do {
            let pl: AVAudioPlayer = try AVAudioPlayer(data: voiceData)
            pl.delegate = self
            pl.play()
            player = pl
        } catch let error as NSError {
            print("alloc AVAudioPlayer with voice data fail with error \(error)")
        }
        
        UIDevice.current.isProximityMonitoringEnabled = true
    }
    
    func pausePlayingAudio() {
        player?.pause()
    }
    
    func stopAudio() {
        if player != nil && player.isPlaying {
            player.stop()
        }
        UIDevice.current.isProximityMonitoringEnabled = false
        delegate?.didAudioPlayerStopPlay(player)
    }
}

extension AudioPlayerManager: AVAudioPlayerDelegate {
    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        stopAudio()
    }
}

//
//  ConvertMp3.m
//  SwiftRecorder
//
//  Created by iOS on 2018/9/25.
//  Copyright © 2018 AidaHe. All rights reserved.
//

#import "ConvertMp3.h"
#import "lame.h"

#define KFILESIZE (1 * 1024 * 1024)

@implementation ConvertMp3

- (void) audioPCMtoMP3:(NSString *)audioFileSavePath mp3File:(NSString *)mp3FilePath{
    NSLog(@"转码开始---");
    CFAbsoluteTime startTime =CFAbsoluteTimeGetCurrent();

    @try {
        int read, write;
        
        FILE *pcm = fopen([audioFileSavePath cStringUsingEncoding:1], "rb");  //source 被转换的音频文件位置
        fseek(pcm, 4*1024, SEEK_CUR);                                   //skip file header
        FILE *mp3 = fopen([mp3FilePath cStringUsingEncoding:1], "wb");  //output 输出生成的Mp3文件位置
        
        const int PCM_SIZE = 8192;
        const int MP3_SIZE = 8192;
        short int pcm_buffer[PCM_SIZE*2];
        unsigned char mp3_buffer[MP3_SIZE];
        
        lame_t lame = lame_init();
        //设置声道1单声道，2双声道
        lame_set_num_channels(lame,1);
        lame_set_in_samplerate(lame, 8000.0);
        lame_set_VBR(lame, vbr_off);
        lame_init_params(lame);
        
        do {
            read = (int)fread(pcm_buffer, 2*sizeof(short int), PCM_SIZE, pcm);
            
            if (read == 0)
                write = lame_encode_flush(lame, mp3_buffer, MP3_SIZE);
            else
                write = lame_encode_buffer_interleaved(lame,pcm_buffer, read, mp3_buffer, MP3_SIZE);
            
            fwrite(mp3_buffer, write, 1, mp3);
        } while (read != 0);
        
        lame_close(lame);
        fclose(mp3);
        fclose(pcm);
    }
    @catch (NSException *exception) {
        NSLog(@"%@",[exception description]);
    }
    NSLog(@"转码结束----");
    CFAbsoluteTime linkTime = (CFAbsoluteTimeGetCurrent() - startTime);
    
    NSLog(@"Linked in %f ms", linkTime *1000.0);

    
}


@end

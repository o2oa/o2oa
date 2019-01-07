package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by FancyLou on 2015/11/20.
 */
public class TCPClient {
    /** 信道选择器 */
    private Selector mSelector;

    /** 服务器通信的信道 */
    private SocketChannel mChannel;

    /** 远端服务器ip地址 */
    private String mRemoteIp;

    /** 远端服务器端口 */
    private int mPort;

    /** 是否加载过的标识 */
    private boolean mIsInit = false;

    /** 单键实例 */
    private static TCPClient gTcp;

    private TCPClientEventListener mEventListener;

    /** 默认链接超时时间 */
    public static final int TIME_OUT = 10000;

    /** 读取buff的大小 */
    public static final int READ_BUFF_SIZE = 1024;

    /** 消息流的格式 */
    public static final String BUFF_FORMAT = "utf-8";

    public static synchronized TCPClient instance() {
        if ( gTcp == null ) {
            gTcp = new TCPClient();
        }
        return gTcp;
    }

    private TCPClient() {

    }

    /**
     * 链接远端地址
     * @param remoteIp
     * @param port
     * @param tcel
     * @return
     */
    public void connect( String remoteIp, int port, TCPClientEventListener tcel ) {
        mRemoteIp = remoteIp;
        mPort = port;
        mEventListener = tcel;
        connect();
    }

    /**
     * 链接远端地址
     * @param remoteIp
     * @param port
     * @return
     */
    public void connect( String remoteIp, int port ) {
        connect(remoteIp,port,null);
    }

    private void connect() {
        //需要在子线程下进行链接
        MyConnectRunnable connect = new MyConnectRunnable();
        new Thread(connect).start();
    }

    /**
     * 发送字符
     * @param msg
     * @return
     */
    public boolean sendMsg(String msg) {
        boolean bRes = false;
        try {
            bRes = sendMsg(msg.getBytes(BUFF_FORMAT));
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return bRes;
    }

    /**
     * 发送数据,此函数需要在独立的子线程中完成,可以考虑做一个发送队列
     * 自己开一个子线程对该队列进行处理,就好像connect一样
     * @param bt
     * @return
     */
    public boolean sendMsg( byte[] bt ) {
        boolean bRes = false;
        if ( !mIsInit ) {
            return bRes;
        }
        try {
            ByteBuffer buf = ByteBuffer.wrap(bt);
            int nCount = mChannel.write(buf);
            if ( nCount > 0 ) {
                bRes = true;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return bRes;
    }

    public Selector getSelector() {
        return mSelector;
    }

    /**
     * 是否链接着
     * @return
     */
    public boolean isConnect() {
        if ( !mIsInit ) {
            return false;
        }
        return mChannel.isConnected();
    }

    /**
     * 关闭链接
     */
    public void close() {
        mIsInit = false;
        mRemoteIp = null;
        mPort = 0;
        try {
            if ( mSelector != null ) {
                mSelector.close();
            }
            if ( mChannel != null ) {
                mChannel.close();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 重连
     * @return
     */
    public void reConnect() {
        close();
        connect();
    }

    /**
     * 发送一个测试数据到服务器,检测服务器是否关闭
     * @return
     */
    public boolean canConnectServer() {
        boolean bRes = false;
        if ( !isConnect() ) {
            return bRes;
        }
        try {
            mChannel.socket().sendUrgentData(0xff);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return bRes;
    }

    /**
     * 每次读完数据后,需要重新注册selector读取数据
     * @return
     */
    private synchronized boolean repareRead() {
        boolean bRes = false;
        try {
            //打开并注册选择器到信道
            mSelector = Selector.open();
            if ( mSelector != null ) {
                mChannel.register(mSelector, SelectionKey.OP_READ);
                bRes = true;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return bRes;
    }

    public void revMsg() {
        if ( mSelector == null ) {
            return;
        }
        boolean bres = true;
        while ( mIsInit ) {
            if ( !isConnect() ) {
                bres = false;
            }
            if ( !bres ) {
                try {
                    Thread.sleep(100);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }

                continue;
            }

            try {
                //有数据就一直接收
                while (mIsInit && mSelector.select() > 0) {
                    for ( SelectionKey sk : mSelector.selectedKeys() ) {
                        //如果有可读数据
                        if ( sk.isReadable() ) {
                            //使用NIO读取channel中的数据
                            SocketChannel sc = (SocketChannel)sk.channel();
                            //读取缓存
                            ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFF_SIZE);
                            //实际的读取流
                            ByteArrayOutputStream read = new ByteArrayOutputStream();
                            int nRead = 0;
                            int nLen = 0;
                            //单个读取流
                            byte[] bytes;
                            //读完为止
                            while ( (nRead = sc.read(readBuffer) ) > 0 ) {
                                //整理
                                readBuffer.flip();
                                bytes = new byte[nRead];
                                nLen += nRead;
                                //将读取的数据拷贝到字节流中
                                readBuffer.get(bytes);
                                //将字节流添加到实际读取流中
                                read.write(bytes);
                                /////////////////////////////////////
                                //@ 需要增加一个解析器,对数据流进行解析

                                /////////////////////////////////////

                                readBuffer.clear();
                            }
                            if ( nLen > 0 ) {
                                if ( mEventListener != null ) {
                                    mEventListener.recvMsg(read);
                                } else {
                                    String info = new String(read.toString(BUFF_FORMAT));
                                    System.out.println("rev:"+info);
                                }
                            }

                            //为下一次读取做准备
                            sk.interestOps(SelectionKey.OP_READ);
                        }

                        //删除此SelectionKey
                        mSelector.selectedKeys().remove(sk);
                    }
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

    }


    public interface TCPClientEventListener {
        /**
         * 多线程下接收到数据
         * @param read
         * @return
         */
        void recvMsg(ByteArrayOutputStream read);
    }

    /**
     * 链接线程
     * @author HeZhongqiu
     *
     */
    private class MyConnectRunnable implements Runnable {

        @Override
        public void run() {
            try {
                //打开监听信道,并设置为非阻塞模式
                SocketAddress ad = new InetSocketAddress(mRemoteIp, mPort);
                mChannel = SocketChannel.open( ad );
                if ( mChannel != null ) {
                    mChannel.socket().setTcpNoDelay(false);
                    mChannel.socket().setKeepAlive(true);

                    //设置超时时间
                    mChannel.socket().setSoTimeout(TIME_OUT);
                    mChannel.configureBlocking(false);

                    mIsInit = repareRead();

                    //创建读线程
                    RevMsgRunnable rev = new RevMsgRunnable();
                    new Thread(rev).start();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            } finally {
                if ( !mIsInit ) {
                    close();
                }
            }
        }
    }

    private class RevMsgRunnable implements Runnable {

        @Override
        public void run() {
            revMsg();
        }

    }
}

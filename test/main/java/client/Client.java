package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.CharsetUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 穆书伟
 * @description Simple client for module test
 * @date 2017/6/13 下午16:05:22
 */
public class Client {
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8090"));
    public static final int clientNum = Integer.parseInt(System.getProperty("size", "1"));
    public static final int frequency = 100;  //ms
    private static ChannelFuture channelFuture = null;
    public static void main(String[] args) throws Exception{
        beginPressTest();
    }

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void beginPressTest() throws InterruptedException{
        EventLoopGroup group = new NioEventLoopGroup();
          Bootstrap b = new Bootstrap();
          b.group(group)
                  .channel(NioSocketChannel.class)
                  .option(ChannelOption.TCP_NODELAY,true)
                  .handler(new ChannelInitializer<SocketChannel>() {
                      @Override
                      protected void initChannel(SocketChannel socketChannel) throws Exception {
                          ChannelPipeline ch=socketChannel.pipeline();                          //
                          ch.addLast(new ClientHandler());  
                      }
                  });
          // Start the client

          for (int i=0;i<clientNum;i++){
              startConnection(b,i);

          }


    }
//    public static void send(Message message) {
//		channelFuture.channel().writeAndFlush(message);
//	}

    public static void startConnection(Bootstrap b, final int index){

    	channelFuture=b.connect(HOST,PORT)
                .addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if(channelFuture.isSuccess()){
                            //init registry
                            logger.info("Client[{}] connected Gate Success...",index);
                            //channelFuture.channel().writeAndFlush(new Message("Hi"));
                            channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", //2
                                    CharsetUtil.UTF_8));

                        }else {
                            logger.info("Client[{}] connected Gate Failed",index);
                        }
                    }
                });
    	
    }
}

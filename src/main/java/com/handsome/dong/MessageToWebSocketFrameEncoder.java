package com.handsome.dong;

import com.handsome.dong.entity.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

public class MessageToWebSocketFrameEncoder extends MessageToMessageEncoder<Message.ResponseMsg> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message.ResponseMsg responseMsg, List<Object> list) throws Exception {
        byte[] bytes = responseMsg.toByteArray();
        ByteBuf byteBuf = Unpooled.buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        list.add(new BinaryWebSocketFrame(byteBuf));
    }
}

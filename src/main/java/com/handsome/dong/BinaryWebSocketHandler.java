package com.handsome.dong;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class BinaryWebSocketHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        ByteBuf content = msg.content();
        content.markReaderIndex();
        int flag = content.readInt();
        content.resetReaderIndex();

        ByteBuf byteBuf = Unpooled.directBuffer(msg.content().capacity());
        byteBuf.writeBytes(content);

        String id = ctx.channel().id().asLongText();
        String user = TextWebSocketHandler.idUserMap.get(id);
        byteBuf.writeBytes(("\\\\\\///" + user).getBytes(Charset.forName("utf-8")));

        TextWebSocketHandler.channelGroup.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }
}

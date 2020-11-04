package com.handsome.dong;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

public class TextWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static Set<String> onlineUsers = new HashSet<>();
    public static Map<String, String> idUserMap = new HashMap<>();
    public static Map<String, Channel> userChannelMap = new HashMap<>();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
//        String message = msg.text();
//        String id = ctx.channel().id().asLongText();
//        if (message.startsWith("YlcK3yD86ll1TjvzQymOAUHy")) {
//            login(message, ctx.channel());
//            return;
//        }
//
//        String user = idUserMap.get(id);
//        if (user == null) {
//            ctx.channel().writeAndFlush("{\"user\": \"system\", \"msg\": \"你已被挤下线或未登录\"}");
//            return;
//        }
//
//        if (channelGroup.contains(ctx.channel())) {
//            channelGroup.writeAndFlush(new TextWebSocketFrame(
//                    String.format("{\"user\": \"%s\",\"msg\":\"%s\"}", idUserMap.get(id), message)));
//        }
//
//        System.out.println(idUserMap.get(id) + "说:" + message);
    }
}

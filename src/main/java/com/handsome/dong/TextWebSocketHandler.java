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

    private void login(String token, Channel channel) {
        String user = "";
        if ("YlcK3yD86ll1TjvzQymOAUHyheynJeBf".equals(token)) {
            user = "东哥";
        }
        if ("YlcK3yD86ll1TjvzQymOAUHyjeynJeBf".equals(token)) {
            user = "菊长";
        }
        if ("YlcK3yD86ll1TjvzQymOAUHyqeynJeBf".equals(token)) {
            user = "强哥";
        }
        if ("YlcK3yD86ll1TjvzQymOAUHyseynJeBf".equals(token)) {
            user = "骚鸭子";
        }
        if ("YlcK3yD86ll1TjvzQymOAUHysyynJeBf".equals(token)) {
            user = "神秘人";
        }
        if ("".equals(user)) {
            channel.writeAndFlush(new TextWebSocketFrame("{\"user\": \"system\", \"msg\": \"系统找不到你这个的信息……滚！\"}"));
            return;
        }

        if (!onlineUsers.contains(user)) {
            onlineUsers.add(user);
        } else {
            Channel originalChannel = userChannelMap.get(user);
            originalChannel.writeAndFlush(new TextWebSocketFrame("{\"user\": \"system\", \"msg\": \"你已被挤下线或未登录\"}"));
            channelGroup.remove(originalChannel);
            originalChannel.close();
        }
        channelGroup.writeAndFlush(
                new TextWebSocketFrame(String.format("{\"user\": \"system\", \"msg\": \"%s来了\"}", user)));
        idUserMap.put(channel.id().asLongText(), user);
        userChannelMap.put(user, channel);
        channelGroup.add(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String message = msg.text();
        String id = ctx.channel().id().asLongText();
        if (message.startsWith("YlcK3yD86ll1TjvzQymOAUHy")) {
            login(message, ctx.channel());
            return;
        }

        String user = idUserMap.get(id);
        if (user == null) {
            ctx.channel().writeAndFlush("{\"user\": \"system\", \"msg\": \"你已被挤下线或未登录\"}");
            return;
        }

        if (channelGroup.contains(ctx.channel())) {
            channelGroup.writeAndFlush(new TextWebSocketFrame(
                    String.format("{\"user\": \"%s\",\"msg\":\"%s\"}", idUserMap.get(id), message)));
        }

        System.out.println(idUserMap.get(id) + "说:" + message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            System.out.println("握手成功");
            // WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete =
            //         (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            // String requestUri = handshakeComplete.requestUri();
            // String subproTocol = handshakeComplete.selectedSubprotocol();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String id = ctx.channel().id().asLongText();
        String user = "";
        if (idUserMap.containsKey(id)) {
            user = idUserMap.get(id);
            idUserMap.remove(id);
        }

        if (onlineUsers.contains(user)) {
            channelGroup.writeAndFlush(
                    new TextWebSocketFrame(String.format("{\"user\":\"system\",\"msg\":\"%s退出了\"}", user)));
            onlineUsers.remove(user);
        }
    }
}

package com.handsome.dong;

import com.handsome.dong.entity.Message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MyHandler extends SimpleChannelInboundHandler<Message.RequestMsg> {
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static Set<String> onlineUsers = new HashSet<>();
    public static Map<String, String> idUserMap = new HashMap<>();
    public static Map<String, Channel> userChannelMap = new HashMap<>();

    private void login(String token, Channel channel) {
        //type 0:系统消息 1:文本消息 2.图片消息
        Message.ResponseMsg.Builder builder = Message.ResponseMsg.newBuilder();
        builder.setType(0);
        String user = "";
        //这里识别用户名  token生成很简单  HandsomeDong+用户名，再用base64编码
        byte[] asBytes = Base64.getDecoder().decode(token);
        String userTmp = new String(asBytes, StandardCharsets.UTF_8);
        System.out.println(userTmp);
        if (userTmp.startsWith("HandsomeDong")) {
            user = userTmp.substring(userTmp.indexOf('g') + 1);
        } else {
            builder.setTextData("系统找不到你这个的信息……滚！");
            channel.writeAndFlush(builder.build());
            return;
        }

        if (!onlineUsers.contains(user)) {
            onlineUsers.add(user);
        } else {
            Channel originalChannel = userChannelMap.get(user);
            builder.setTextData("你已被挤下线或未登录");
            originalChannel.writeAndFlush(builder.build());
            channelGroup.remove(originalChannel);
            originalChannel.close();
        }
        builder.setTextData(user + "来了");
        channelGroup.writeAndFlush(builder.build());
        idUserMap.put(channel.id().asLongText(), user);
        userChannelMap.put(user, channel);
        channelGroup.add(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.RequestMsg requestMsg) {
        String id = ctx.channel().id().asLongText();
        String user = idUserMap.get(id);
        // type 0:登录 1:文本消息 2:图片消息
        if (requestMsg.getType() == 0) {
            login(requestMsg.getToken(), ctx.channel());
        } else {
            Message.ResponseMsg.Builder builder = Message.ResponseMsg.newBuilder();
            if (user == null) {
                builder.setType(0);
                builder.setTextData("你已被挤下线或未登录");
                ctx.channel().writeAndFlush(builder.build());
            } else {
                builder.setType(requestMsg.getType());
                builder.setUserName(idUserMap.get(id));
                if (requestMsg.getType() == 1) {
                    builder.setTextData(requestMsg.getTextData());
                } else {
                    builder.setPicData(requestMsg.getPicData());
                }
                channelGroup.writeAndFlush(builder.build());
                System.out.println(idUserMap.get(id) + "说:" + requestMsg.getTextData());
            }
        }
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
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
            Message.ResponseMsg.Builder builder = Message.ResponseMsg.newBuilder();
            builder.setType(0);
            builder.setTextData(user + "走了");
            channelGroup.writeAndFlush(builder.build());
            onlineUsers.remove(user);
        }
    }
}

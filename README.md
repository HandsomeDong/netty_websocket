# netty_websocket
一个基于netty开发的websocket服务器demo，用Spring整合
# 运行
>1. git clone下来后用IDEA打开，用maven下载好依赖包后，直接启动NettyApplication即可启动Spring和Netty服务器。
>2. 打开 front/html 里的html并在get参数里输入token的参数即可进入聊天室，token可以随便生成，用HandsomeDong+用户名然后Base64编码即可。可以在MyHandler中看到识别的逻辑。如：test.html?token=SGFuZHNvbWVEb25n5Lic5ZOl，这个token base64解码后就是“HandsomeDong东哥”，用户名就是“东哥”

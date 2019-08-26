# JDND
Public repository for the Java Developer Nanodegree program.

#### Websocket Chatroom 总结([参考此处](https://www.jianshu.com/p/55cfc9fcb69e))

- Websocket特点

> 1. 基于tcp协议
> 2. 浏览器与服务器只需一次握手即可建立长连接
> 3. 连接建立以后，通过send()方法服务器发送数据，通过onmessage事件接收数据

- 客户端的实现

> 1. 创建WebSocket对象
> 2. 实现以下4个回调函数
>
> | 事件类型 | 回调函数  |            描述            |
> | :------: | :-------: | :------------------------: |
> |   open   |  onopen   |       打开连接时触发       |
> | message  | onmessage | 客户端接收服务端数据时触发 |
> |  error   |  onerror  |       通信异常时触发       |
> |  close   |  onclose  |       连接关闭时触发       |
>
> ```javascript
> function getWebSocket() {
>         /**
>          * WebSocket client PS：URL shows WebSocket protocal, port number, and then end point.
>          */
>         var webSocket = new WebSocket(/*[[${webSocketUrl}]]*/ 'ws://localhost:8080/chat');
>         /**
>          * websocket open connection.
>          */
>         webSocket.onopen = function (event) {
>             console.log('WebSocket open connection');
>         };
> 
>         /**
>          * Server send 1) broadcast message, 2) online users.
>          */
>         webSocket.onmessage = function (event) {
>             console.log('WebSocket Receives：%c' + event.data, 'color:green');
>             //Receive Message from Server
>             var message = JSON.parse(event.data) || {};
>             var $messageContainer = $('.message-container');
>             if (message.type === 'CHAT') {
>                 $messageContainer.append(
>                     '<div class="mdui-card" style="margin: 10px 0;">' +
>                     '<div class="mdui-card-primary">' +
>                     '<div class="mdui-card-content message-content">' + message.userName + "：" + message.content + '</div>' +
>                     '</div></div>');
>             }
>             $('.chat-num').text(message.onlineUsers);
>             var $cards = $messageContainer.children('.mdui-card:visible').toArray();
>             if ($cards.length > 5) {
>                 $cards.forEach(function (item, index) {
>                     index < $cards.length - 5 && $(item).slideUp('fast');
>                 });
>             }
>         };
> 
>         /**
>          * Close connection
>          */
>         webSocket.onclose = function (event) {
>             console.log('WebSocket close connection.');
>         };
> 
>         /**
>          * Exception
>          */
>         webSocket.onerror = function (event) {
>             console.log('WebSocket exception.');
>         };
>         return webSocket;
>     }
> ```
>
> 3. 实现发送消息函数sendMsgToServer()
>
> ```javascript
> function sendMsgToServer() {
>         var $message = $('#msg');
>         if ($message.val()) {
>           	// 从前端获取id为username和msg的标签的值，转换为bean，再包装成json转换为string，通过send函数传给服务器。
>             webSocket.send(JSON.stringify({userName: $('#username').text(), content: $message.val()}));
>             $message.val(null); // 清空message的值以便接收下一次的输入
>         }
>     }
> ```

- 实现服务端

  - Web服务端自动注册

  ```java
  // WebSockectConfig.java
  @Configuration
  public class WebSocketConfig {
  
      @Bean
      public ServerEndpointExporter serverEndpointExporter() {
          return new ServerEndpointExporter();
      }
  }
  ```

  - 创建WebSocket服务端

  ```java
  // WebSockectChatServer.java
  @Component
  @ServerEndpoint("/chat")
  public class WebSocketChatServer {
  
      /**
       * All chat sessions.
       */
      private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();
  
      private static void sendMessageToAll(String msg) {
          //TODO: add send message method.
          for (Session session : onlineSessions.values()) {
              try {
                  session.getBasicRemote().sendText(msg);
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
  
      /**
       * Open connection, 1) add session, 2) add user.
       */
      @OnOpen
      public void onOpen(Session session) {
          //TODO: add on open connection.
          onlineSessions.put(session.getId(), session);
          sendMessageToAll(Message.serializer(Message.ENTER, "", "", onlineSessions.size()));
      }
  
      /**
       * Send message, 1) get username and session, 2) send message to all.
       */
      @OnMessage
      public void onMessage(Session session, String jsonStr) {
          //TODO: add send message.
          Message message = JSON.parseObject(jsonStr, Message.class);
          sendMessageToAll(Message.serializer(Message.CHAT, message.getUserName(), message.getContent(), onlineSessions.size()));
      }
  
      /**
       * Close connection, 1) remove session, 2) update user.
       */
      @OnClose
      public void onClose(Session session) {
          //TODO: add close connection.
          onlineSessions.remove(session.getId());
          sendMessageToAll(Message.serializer(Message.LEAVE, "", "", onlineSessions.size()));
      }
  
      /**
       * Print exception.
       */
      @OnError
      public void onError(Session session, Throwable error) {
          error.printStackTrace();
      }
  
  }
  ```

  


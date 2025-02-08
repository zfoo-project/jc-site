package fun.jiucai.home.chatai.xunfei;

import com.zfoo.event.manager.EventBus;
import com.zfoo.net.session.Session;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.RandomUtils;
import fun.jiucai.common.protocol.chatgpt.ChatgptMessage;
import fun.jiucai.home.chatai.xunfei.client.XunFeiChatRequest;
import fun.jiucai.home.chatai.xunfei.client.XunFeiChatResponse;
import fun.jiucai.home.chatai.xunfei.event.XunFeiMessageEvent;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

@Deprecated
public class XunfeiWebsocketListener extends WebSocketListener {

    public static String hostUrl = "https://spark-api.xf-yun.com/v1.1/chat";
    public String APPID;//从开放平台控制台中获取
    public String APIKEY;//从开放平台控制台中获取
    public String APISecret;//从开放平台控制台中获取
    public List<ChatgptMessage> messages;//可以修改question 内容，来向模型提问

    public Session session;
    public long requestId;
    public long requestSid;

    public XunfeiWebsocketListener() {
    }

    public XunfeiWebsocketListener(Session session, long requestId, long requestSid, String APPID, String APIKEY, String APISecret, List<ChatgptMessage> messages) {
        this.session = session;
        this.requestId = requestId;
        this.requestSid = requestSid;
        this.APPID = APPID;
        this.APIKEY = APIKEY;
        this.APISecret = APISecret;
        this.messages = messages;
    }

    public void start() throws Exception {
        //构建鉴权httpurl
        String authUrl = getAuthorizationUrl(hostUrl, APIKEY, APISecret);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        String url = authUrl.replace("https://", "wss://").replace("http://", "ws://");
        Request request = new Request.Builder().url(url).build();
        WebSocket webSocket = okHttpClient.newWebSocket(request, this);
    }


    //鉴权url
    public static String getAuthorizationUrl(String hostUrl, String apikey, String apisecret) throws Exception {
        //获取host
        URL url = new URL(hostUrl);
        //获取鉴权时间 date
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        System.out.println("format:\n" + format);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        //获取signature_origin字段
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").
                append("date: ").append(date).append("\n").
                append("GET ").append(url.getPath()).append(" HTTP/1.1");
        System.out.println("signature_origin:\n" + builder);
        //获得signatue
        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec sp = new SecretKeySpec(apisecret.getBytes(charset), "hmacsha256");
        mac.init(sp);
        byte[] basebefore = mac.doFinal(builder.toString().getBytes(charset));
        String signature = Base64.getEncoder().encodeToString(basebefore);
        //获得 authorization_origin
        String authorization_origin = String.format("api_key=\"%s\",algorithm=\"%s\",headers=\"%s\",signature=\"%s\"", apikey, "hmac-sha256", "host date request-line", signature);
        //获得authorization
        String authorization = Base64.getEncoder().encodeToString(authorization_origin.getBytes(charset));
        //获取httpurl
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().//
                addQueryParameter("authorization", authorization).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        return httpUrl.toString();
    }

    //重写onopen
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        send(webSocket);
    }

    public void send(WebSocket webSocket) {
        new Thread(() -> {
            var header = new XunFeiChatRequest.Header();
            var chat = new XunFeiChatRequest.Chat();
            var parameter = new XunFeiChatRequest.Parameter();
            var payload = new XunFeiChatRequest.Payload();
            var message = new XunFeiChatRequest.Message();
            var chatMessages = new ArrayList<XunFeiChatRequest.Text>();

            //填充header
            header.setApp_id(APPID);
            header.setUid(RandomUtils.randomString(10));
            //填充parameter
            chat.setDomain("general");
            chat.setTemperature(0.5);
            chat.setMax_tokens(2000);
            parameter.setChat(chat);

            for (var chatgptMessage : messages) {
                chatMessages.add(new XunFeiChatRequest.Text(chatgptMessage.getRole(), chatgptMessage.getContent()));
            }

            //填充payload
            payload.setMessage(message);
            message.setText(chatMessages);

            var frame = new XunFeiChatRequest(header, parameter, payload);
            System.out.println(JsonUtils.object2String(frame));

            webSocket.send(JsonUtils.object2String(frame));
        }
        ).start();
    }
    //重写onmessage

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        System.out.println("text:\n" + text);
        try {
            XunFeiChatResponse responseData = JsonUtils.string2Object(text, XunFeiChatResponse.class);
            EventBus.post(new XunFeiMessageEvent(session, responseData, requestId, requestSid));
//        System.out.println("code:\n" + responseData.getHeader().get("code"));
            if (0 == responseData.getHeader().getCode()) {
                System.out.println("###########");
            } else {
                System.out.println("返回结果错误：\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //重写onFailure

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        System.out.println(response);
    }


}
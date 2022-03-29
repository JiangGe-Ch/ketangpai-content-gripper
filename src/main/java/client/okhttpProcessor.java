package client;

import okhttp3.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class okhttpProcessor {

    Logger log=Logger.getLogger(okhttpProcessor.class);

    public void postRequest(){
        OkHttpClient client=new OkHttpClient();
    }

    public void getRequest(){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url("https://www.baidu.com")
                .build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
               log.info("Request测试失败...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                log.info("Request测试成功...");
                log.info("code["+response.code()+"]...");
                log.info("response body["+response.body().string()+"]...");
            }
        });
    }
}

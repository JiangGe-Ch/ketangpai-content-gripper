package client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class KeTangPaiClient {
    private static final Logger log=Logger.getLogger(KeTangPaiClient.class);

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client=new OkHttpClient();

    public int start() throws IOException {
        log.info("start...");
        String token=login();
        if(token==null) {
            log.info("登录失败，未获取到token...");
            return -1;
        }
        log.info("登录成功，获取到token["+token+"]...");
        JSONArray courseListJson=getCourseList(token);
        if(courseListJson==null){
            log.info("获取课程列表失败...");
            return -2;
        }
        log.info("获取到课程列表json["+courseListJson+"]...");
        JSONArray contentListJson=getCourseContent(courseListJson, token);
        if(contentListJson.size()==0){
            log.info("获取课程内容失败...");
            return -3;
        }
        log.info("content["+contentListJson.toJSONString()+"]...");
        printContentList(contentListJson);


        return 0;
    }

    /**
     * 登录方法
     * @return  token
     * @throws IOException
     */
    private String login() throws IOException {
        log.info("login...");
//        String bodyStr="{\"email\":\"133***1837\",\"password\":\"*********\",\"remember\":\"0\",\"code\":\"\",\"mobile\":\"\",\"type\":\"login\",\"reqtimestamp\":1648456768097}";
        Scanner sc=new Scanner(System.in);
        System.out.println("请输入账号...");
        String email=sc.nextLine();
        System.out.println("请输入密码...");
        String password=sc.next();
        JSONObject reqbody=JSONObject.parseObject("{\"remember\":\"0\",\"code\":\"\",\"mobile\":\"\",\"type\":\"login\"}");
        reqbody.put("email", email);
        reqbody.put("password", password);
        reqbody.put("reqtimestamp", System.currentTimeMillis());
        RequestBody body=RequestBody.create(JSON, reqbody.toJSONString());
        Request request=new Request.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36")
                .url("https://openapiv5.ketangpai.com//UserApi/login")
                .post(body)
                .build();
        Response response=client.newCall(request).execute();
        JSONObject json=JSONObject.parseObject(response.body().string());
        if(json.get("message").equals("访问成功")){
            return json.getJSONObject("data").get("token").toString();
        }else {
            return null;
        }
    }


    /**
     * 获取课程列表（以2021-2022学年第二学期为例，修改请求体可相应获取各学期数据）
     * @param token
     * @return  课程列表jsonarray
     * @throws IOException
     */
    private JSONArray getCourseList(String token) throws IOException {
        log.info("getCourseList...");
//        String bodyStr="{\"isstudy\":\"1\",\"search\":\"\",\"semester\":\"2021-2022\",\"term\":\"2\",\"reqtimestamp\":1648457586350}";
        JSONObject reqjson=JSONObject.parseObject("{\"isstudy\":\"1\",\"search\":\"\",\"semester\":\"2021-2022\",\"term\":\"2\",\"reqtimestamp\":1648457586350}");
        reqjson.put("reqtimestamp", System.currentTimeMillis());
        RequestBody body=RequestBody.create(JSON, reqjson.toJSONString());
        Request request=new Request.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36")
                .addHeader("token", token)
                .url("https://openapiv5.ketangpai.com//CourseApi/semesterCourseList")
                .post(body)
                .build();
        Response response=client.newCall(request).execute();
        JSONObject json=JSONObject.parseObject(response.body().string());
        if(json.get("message").equals("访问成功")){
            return json.getJSONArray("data");
        }else{
            return null;
        }
    }

    /**
     * 获取课程内容（作业）
     * @param courseListJson    课程列表的jsonarray
     * @param token     token 从登录方法获取
     * @return  课程内容（作业）的jsonarray
     * @throws IOException
     */
    private JSONArray getCourseContent(JSONArray courseListJson, String token) throws IOException {
        log.info("getCourseContent...");
//        String bodyStr="{\"contenttype\":4,\"dirid\":0,\"lessonlink\":[],\"sort\":[],\"page\":1,\"limit\":50,\"desc\":3,\"courserole\":0,\"vtr_type\":\"\",\"reqtimestamp\":1648459039651}";
        JSONArray contenJsonArray=new JSONArray();
        for(int i=0; i<courseListJson.size(); i++){
            JSONObject reqBodyJson=JSONObject.parseObject("{\"contenttype\":4,\"dirid\":0,\"lessonlink\":[],\"sort\":[],\"page\":1,\"limit\":50,\"desc\":3,\"courserole\":0,\"vtr_type\":\"\"}");
            reqBodyJson.put("courseid", courseListJson.getJSONObject(i).getString("id"));
            reqBodyJson.put("reqtimestamp", System.currentTimeMillis());
            log.info(reqBodyJson.toJSONString());
            RequestBody body=RequestBody.create(JSON, reqBodyJson.toJSONString());
            Request request=new Request.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36")
                    .addHeader("token", token)
                    .url("https://openapiv5.ketangpai.com//FutureV2/CourseMeans/getCourseContent")
                    .post(body)
                    .build();
            Response response=client.newCall(request).execute();
            JSONObject json=JSONObject.parseObject(response.body().string());
            if(json.getString("message").equals("访问成功")){
                log.info("courseid ["+courseListJson.getJSONObject(i).getString("id")+"] 访问成功...");
                JSONArray contentArray=json.getJSONObject("data").getJSONArray("list");
                for(int j=0;j<contentArray.size();j++){
                    contenJsonArray.add(contentArray.getJSONObject(j));
                }
//                contenJsonArray.add(json.getJSONObject("data"));
            }else{
                log.info("错误，courseid ["+courseListJson.getJSONObject(i).getString("id")+"] 访问失败...");
            }
        }
        log.info(contenJsonArray.size()+"项课程内容访问成功...");
        return contenJsonArray;
    }

    /**
     * 将获取到的ContentList通过结束时间排序
     * @param contentList 待排序json数组
     * @param dec true 降序，fals 升序
     */
    private void sortContentListByEndTime(JSONArray contentList, boolean dec){
        if(dec){
            for(int j=0;j<contentList.size();j++){
                for(int i=0; i<contentList.size()-1;i++){
                    if(contentList.getJSONObject(i).getLong("endtime")<contentList.getJSONObject(i+1).getLong("endtime")){
                        JSONObject tmp=contentList.getJSONObject(i);
                        contentList.set(i, contentList.getJSONObject(i+1));
                        contentList.set(i+1, tmp);
                    }
                }
            }
        }else{
            for(int j=0;j<contentList.size();j++){
                for(int i=0; i<contentList.size()-1;i++){
                    if(contentList.getJSONObject(i).getLong("endtime")>contentList.getJSONObject(i+1).getLong("endtime")){
                        JSONObject tmp=contentList.getJSONObject(i);
                        contentList.set(i, contentList.getJSONObject(i+1));
                        contentList.set(i+1, tmp);
                    }
                }
            }
        }
    }

    /**
     * 示例输出方法
     * @param contentListJson
     */
    private void printContentList(JSONArray contentListJson){
        sortContentListByEndTime(contentListJson, true);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(int i=0; i<contentListJson.size(); i++){
            long lt=Long.parseLong(contentListJson.getJSONObject(i).getString("endtime"));
            log.info("endtimestamp["+lt+"]...");
            Date endtime=new Date(lt*1000);
            String mstatusStr;
            if(contentListJson.getJSONObject(i).getInteger("mstatus")==1){
                mstatusStr="是";
            }else{
                mstatusStr="否";
            }
            System.out.println("[作业名称："+contentListJson.getJSONObject(i).getString("title")+
                    "] [截止时间："+sdf.format(endtime)+
                    "] [已提交："+mstatusStr+"]");
        }
    }
}

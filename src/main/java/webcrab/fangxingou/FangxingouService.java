package webcrab.fangxingou;

import cn.edu.hfut.dmic.webcollector.util.MD5Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import webcrab.fangxingou.module.ProductDetailQueryParam;
import webcrab.fangxingou.module.ProductListQueryParam;
import webcrab.util.JsonUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 放心购服务
 */
public class FangxingouService {
    private final Retrofit retrofit;
    private final FangxingouApi api;
    private final OkHttpClient okHttpClient;

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String APP_KEY = FangxingouConstants.APP_KEY;
    private final String APP_SECRET = FangxingouConstants.APP_SECRET;
    private final String API_VERSION = FangxingouConstants.API_VERSION;
    private final String API_BASE_URL = FangxingouConstants.OPEN_API_BASE_URL;

    public FangxingouService() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();

        retrofit = new Retrofit.Builder() //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create(gson)) //设置网络请求的Url地址
                .baseUrl(API_BASE_URL).build(); // 创建网络请求接口的实例

        api = retrofit.create(FangxingouApi.class);

        okHttpClient = new OkHttpClient();
    }

    /**
     * 获取签名
     *
     * @return
     */
    private static String sign(String appKey, String appSecret, String method,
                               String paramJson, String timestamp, String apiVersion) {
//        a. 将param_json中参数按照key大小排序，组成json
//        例如: param_json={"product_id":"123123123","code":"C1"} 需要调整为 param_json={"code":"HHK","product_id":"123123123"}
//

//        b. 所有请求参数按照字母先后顺序排列
//        例如：将param_json,method,app_key,timestamp,v 排序为 app_key,method,param_json,timestamp,v
//
//        c. 把所有参数名和参数值进行拼装
//                app_keyxxxmethodxxxparam_jsonxxxtimestampxxxvxxx
//
        String info = "app_key" + appKey
                + "method" + method
                + "param_json" + paramJson
                + "timestamp" + timestamp
                + "v" + apiVersion;

//        d. 把appSecret分别拼接在c步得到的字符串的两端
//        例如：appSecret+XXXX+appSecret
//
        String secretInfo = appSecret + info + appSecret;

        System.out.println(secretInfo);
//        e. 使用MD5进行加密得到sign, 传入url参数中
//
        String md5 = "";
        try {
            md5 = MD5Utils.md5(secretInfo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

//        f. 原始参数内容中含有含有 + 号, 需要在正式请求时确保被替换成%2b, 否则被无法正常识别
//

//        g. 所有参数类型均为 string, param_json中的所有参数也都需为 string
        return md5;
    }

    /**
     * 签名
     *
     * @param method
     * @param paramJson
     * @param timestamp
     * @return
     */
    private String sign(String method,
                        String paramJson, String timestamp) {
        return sign(APP_KEY, APP_SECRET, method, paramJson, timestamp, API_VERSION);
    }

    public <T> Response callRemoteMethod(String methodUrl, T paramObject) {

        String method = methodUrl.replace("/", ".");
        Date now = new Date();
        String timestamp = dateFormat.format(now);
        String paramJson = JsonUtils.toJson(paramObject);
        String sign = sign(method, paramJson, timestamp);

        String urlParams = "app_key=" + APP_KEY
                + "&" + "method=" + method
                + "&" + "param_json=" + paramJson
                + "&" + "timestamp=" + timestamp
                + "&" + "v=" + API_VERSION
                + "&" + "sign=" + sign;

        String uri = API_BASE_URL + "/" + methodUrl + "?" + urlParams;
        System.out.println(uri);
        final Request request = new Request.Builder().url(uri)
                .get().build();

        try {
            Response resp = okHttpClient.newCall(request).execute();
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        Call call = okHttpClient.newCall(request);
//
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//                try {
//                    System.out.println(response.body().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    public void test() {
        String method = null;
        Response resp = null;

        method = FangxingouApi.PRODUCT_LIST;

        ProductListQueryParam param = new ProductListQueryParam();
        param.setPage("0");
        param.setSize("10");
        param.setStatus("0");

        resp = callRemoteMethod(method, param);
        try {
            System.out.println(resp.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //-----------------------------------
        method = FangxingouApi.PRODUCT_DETAIL;

        ProductDetailQueryParam param2 = new ProductDetailQueryParam();
        param2.setProduct_id("3300321774121703800");
        resp = callRemoteMethod(method, param2);
        try {
            System.out.println(resp.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void runTest() {
        FangxingouService service = new FangxingouService();
        service.test();
    }
}

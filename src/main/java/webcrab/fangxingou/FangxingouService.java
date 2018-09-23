package webcrab.fangxingou;

import cn.edu.hfut.dmic.webcollector.util.MD5Utils;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import webcrab.datatype.TreeNode;
import webcrab.fangxingou.module.Category;
import webcrab.fangxingou.module.Product;
import webcrab.fangxingou.module.po.*;
import webcrab.util.JsonUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 放心购服务
 */
public class FangxingouService {
    private static Logger logger = LoggerFactory.getLogger(FangxingouService.class);

    protected static FangxingouService instance;
    static {
        instance = new FangxingouService();
    }

    private final Retrofit retrofit;
    private final FangxingouApi api;
    private final OkHttpClient okHttpClient;

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String APP_KEY = FangxingouAppConstant.APP_KEY;
    private final String APP_SECRET = FangxingouAppConstant.APP_SECRET;
    private final String API_VERSION = FangxingouAppConstant.API_VERSION;
    private final String API_BASE_URL = FangxingouAppConstant.OPEN_API_BASE_URL;

    public static FangxingouService getInstance() {
        return instance;
    }

    protected FangxingouService() {
        Gson gson = JsonUtils.createGson();

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

        //System.out.println(secretInfo);
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

    /**
     * 调用远程方法
     *
     * @param methodUrl
     * @param paramObject
     * @param <T>         传入的参数实体类型，会自动转换
     * @return
     */
    public <T> Response callRemoteMethod(String methodUrl, T paramObject) {

        String method = methodUrl.replace("/", ".");
        Date now = new Date();
        String timestamp = dateFormat.format(now);
        String paramJson = JsonUtils.toJson(paramObject);
        //原始参数内容中含有含有 + 号, 需要在正式请求时确保被替换成%2b, 否则被无法正常识别
        paramJson = paramJson.replace("+", "%2b");
        String sign = sign(method, paramJson, timestamp);

        String urlParams = "app_key=" + APP_KEY
                + "&" + "method=" + method
                + "&" + "param_json=" + paramJson
                + "&" + "timestamp=" + timestamp
                + "&" + "v=" + API_VERSION
                + "&" + "sign=" + sign;

        String uri = API_BASE_URL + "/" + methodUrl + "?" + urlParams;
        logger.info(uri);
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

    public <T,R> R callRemoteMethod(String methodUrl, T paramObject, Type resultType) {
        Response resp = callRemoteMethod(methodUrl,paramObject);
        if (resp == null) {
            return null;
        }
        try {
            String rstJson = resp.body().string();
            return JsonUtils.fromJson(rstJson, resultType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 查规格详情
     */
    public SpecAddResult specAdd(String specs, String name) {
        String method = FangxingouApi.SPEC_ADD;
        SpecAddParam param = new SpecAddParam();
        param.setSpecs(specs);
        param.setName(name);
        SpecAddResult resp = callRemoteMethod(method, param, SpecAddResult.class);
        return resp;
    }

    /**
     * 查规格详情
     */
    public SpecDetailResult specDetail(String specId) {
        String method = FangxingouApi.SPEC_SPECDETAIL;
        SpecDetailQueryParam param = new SpecDetailQueryParam();
        param.setId(specId);
        SpecDetailResult resp = callRemoteMethod(method, param, SpecDetailResult.class);
        return resp;
    }
    /**
     * 查规格列表
     */
    public SpecListResult specList() {
        String method = FangxingouApi.SPEC_LIST;
        Object param3 = new Object();
        SpecListResult resp = callRemoteMethod(method, param3, SpecListResult.class);
        return resp;
    }

    /**
     * 查询商品分类
     *
     * @param cid 商品父分类id,根据父id可以获取子分类，一级分类cid=0,必填。
     */
    public void productCategory(String cid) {
        String method = FangxingouApi.PRODUCT_GET_GOODS_CATEGORY;
        ProductCategoryQueryParam param = new ProductCategoryQueryParam();
        param.setCid(cid);

        Response resp = callRemoteMethod(method, param);
        try {
            String s = resp.body().string();
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void buildCategoryTree(TreeNode<Category> node, Category category) {
        node.setValue(category);

        String method = FangxingouApi.PRODUCT_GET_GOODS_CATEGORY;
        ProductCategoryQueryParam param = new ProductCategoryQueryParam();
        param.setCid(String.valueOf(category.getId()));
        CategoryResult subCategories = callRemoteMethod(method, param, new ParameterizedTypeReference<CategoryResult>() {
        }.getType());

        List<TreeNode<Category>> children = node.getChildren();
        for (Category subCategory: subCategories.getData()) {
            TreeNode<Category> subNode = new TreeNode<>();
            children.add(subNode);
            buildCategoryTree(subNode, subCategory);
        }
    }

    /**
     * 获取产品类型树
     */
    public void productCategoryTree() {
        Category rootCategory = new Category();
        rootCategory.setId(0L);
        rootCategory.setName("rootCategory");

        TreeNode<Category> categoryTree = new TreeNode<>();
        buildCategoryTree(categoryTree, rootCategory);

        TreeNode.printTree(categoryTree, 0);
    }

    /**
     * 查产品列表
     * @param page 第几页
     * @param size 每页的个数
     * @param status 0-上架  1-下架
     */
    public void productList(String page, String size, String status) {
        String method = FangxingouApi.PRODUCT_LIST;
        ProductListQueryParam param = new ProductListQueryParam();
        param.setPage(page);
        param.setSize(size);
        param.setStatus(status);

        Response resp = callRemoteMethod(method, param);
        try {
            System.out.println(resp.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加商品
     *
     * @param param 参数
     */
    public ProductAddResult productAdd(Product param) {
        String method = FangxingouApi.PRODUCT_ADD;
        ProductAddResult rst = callRemoteMethod(method, param, ProductAddResult.class);
        logger.info(JsonUtils.toJson(rst));
        return rst;
    }

    /**
     * 查产品详情
     * @param productId
     */
    public void productDetail(String productId) {
        String method = FangxingouApi.PRODUCT_DETAIL;

        ProductDetailQueryParam param2 = new ProductDetailQueryParam();
        param2.setProductId(productId);
        Response resp = callRemoteMethod(method, param2);
        try {
            System.out.println(resp.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查产品详情
     *
     * @param outProductId 商品外部编号
     */
    public boolean isProductExist(String outProductId) {
        String method = FangxingouApi.PRODUCT_DETAIL;

        ProductDetailQueryParam param2 = new ProductDetailQueryParam();
        param2.setOutProductId(outProductId);
        Response resp = callRemoteMethod(method, param2);
        try {
            String res = resp.body().string();
            if (res.contains("record not found")) {
                return false;
            } else if (res.contains(outProductId)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void test() {
        //productList("0", "10", "0");

        //-----------------------------------
        productDetail("3300321774121703800");
        //productDetail("330032177412170380");

        //-----------------------------------
        specList();

        specDetail("3067592");

        //specDetail("3070833");
    }


    public static void runTest() {
        FangxingouService service = new FangxingouService();
        service.test();
    }
}

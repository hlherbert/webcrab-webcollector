package webcrab.fangxingou;

import cn.edu.hfut.dmic.webcollector.util.MD5Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import webcrab.conf.SellerProperties;
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
import java.util.*;

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
    private final SellerProperties sellerProperties = SellerProperties.getInstance();
    private final String APP_KEY = sellerProperties.getAppkey();
    private final String APP_SECRET = sellerProperties.getAppsecret();
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
     * paramJson里面的数据需要按照key排序
     *
     * @param paramJson 参数json，未排序
     * @return paramJson里字段按照key排序
     */
    private String sortJsonParam(String paramJson) {
        Map<String, Object> paramMap = JsonUtils.fromJson(paramJson, new TypeToken<Map<String, String>>() {
        }.getType());
        Set<String> paramKeys = paramMap.keySet();
        List<String> keyList = new ArrayList<>();
        keyList.addAll(paramKeys);
        Collections.sort(keyList);
        Map<String, Object> paramMapOrdered = new LinkedHashMap<>();
        for (String key : keyList) {
            paramMapOrdered.put(key, paramMap.get(key));
        }
        paramJson = JsonUtils.toJson(paramMapOrdered);
        return paramJson;
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

        //　paramJson里面的数据需要按照key排序
        paramJson = sortJsonParam(paramJson);

        String sign = sign(method, paramJson, timestamp);

        //原始参数内容中含有含有 + 号, 需要在正式请求时确保被替换成%2b, 否则被无法正常识别. 先签名再转换
        paramJson = paramJson.replace("+", "%2B");

        String urlParams = "app_key=" + APP_KEY
                + "&" + "method=" + method
                + "&" + "param_json=" + paramJson
                + "&" + "timestamp=" + timestamp
                + "&" + "v=" + API_VERSION
                + "&" + "sign=" + sign;

        String uri = API_BASE_URL + "/" + methodUrl + "?" + urlParams;
        logger.info(uri);

//        // TODO: test
//        HttpUrl httpUrl = HttpUrl.parse(uri);
//        String httpUrlQuery = httpUrl.encodedQuery();
//        logger.info(httpUrlQuery);

        final Request request = new Request.Builder().url(uri)
                .get().build();

        try {
            Response resp = okHttpClient.newCall(request).execute();
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        SpecDetailParam param = new SpecDetailParam();
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
        ProductCategoryParam param = new ProductCategoryParam();
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
        ProductCategoryParam param = new ProductCategoryParam();
        param.setCid(String.valueOf(category.getId()));
        CategoryResult subCategories = callRemoteMethod(method, param, CategoryResult.class);

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
        ProductListParam param = new ProductListParam();
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
     * @param outProductId 外部ID，例如淘宝ID
     */
    public void productDetail(String outProductId) {
        String method = FangxingouApi.PRODUCT_DETAIL;

        ProductDetailParam param2 = new ProductDetailParam();
        param2.setOutProductId(outProductId);
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

        ProductDetailParam param2 = new ProductDetailParam();
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

    /**
     * sku列表
     */
    public void skuList(String outProductId) {
        String method = FangxingouApi.SKU_LIST;

        SkuListParam param = new SkuListParam();
        param.setOutProductId(outProductId);
        Response resp = callRemoteMethod(method, param);
        try {
            System.out.println(resp.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

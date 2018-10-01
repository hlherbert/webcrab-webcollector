# webcrab-webcollector

# openapi
http://openapidoc.jinritemai.com

# 服务器返回错误码列表
字段名 	释义
1 	"请登录后再操作",
2 	"无权限",
3 	"缺少参数",
4 	"参数错误",
5 	"参数不合法",
6 	"业务参数json解析失败, 所有参数需为string类型",
7 	"服务器错误",
8 	"服务繁忙",
9 	"访问太频繁",
10 	"需要用 POST 请求",
11 	"签名校验失败",
12 	"版本太旧，请升级",
301 	"不是授权用户",
302 	"没有user_id",

# 商品审核状态列表

字段名 	释义
1 	商品未提交
2 	商品待审核
3 	商品审核通过
4 	商品审核未通过
5 	商品下线

# URL中特殊符号，需要替换，否则sign失败
    1、+ URL 中+号表示空格 %2B
    2、空格 URL中的空格可以用+号或者编码 %20
    3、 / 分隔目录和子目录 %2F
    4、 ? 分隔实际的 URL 和参数 %3F
    5、 % 指定特殊字符 %25
    6、# 表示书签 %23
    7、 & URL 中指定的参数间的分隔符 %26
    8、 = URL 中指定参数的值 %3D


fangxingou-service@bytedance.com
http://openapi.jinritemai.com/product/add?app_key=3300835641834754583&method=product.add&param_json={"cos_ratio":"0","description":"https://img.alicdn.com/imgextra/i4/T2s4moXH8XXXXXXXXX-350475995.png?p\u003dmodify_tools_52278_start_top_1|https://img.alicdn.com/imgextra/i4/T2s4moXH8XXXXXXXXX-350475995.png?p\u003dmodify_tools_52013_start_top_1|https://img.alicdn.com/imgextra/i4/3368305815/TB2am3HFNWYBuNjy1zkXXXGGpXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2pAxsoFooBKNjSZPhXXc2CXXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i1/3368305815/TB2DE0_oJcnBKNjSZR0XXcFqFXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2VbUmoQ7mBKNjSZFyXXbydFXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i1/3368305815/TB2gTL6oHZnBKNjSZFKXXcGOVXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i4/3368305815/TB2kzcCoTCWBKNjSZFtXXaC3FXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2kXwloTqWBKNjSZFxXXcpLpXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i4/T2s4moXH8XXXXXXXXX-350475995.png?p\u003dmodify_tools_52278_end_top_1|https://img.alicdn.com/imgextra/i2/3368305815/TB2caQyGGSWBuNjSsrbXXa0mVXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i3/3368305815/TB2kHKtHhGYBuNjy0FnXXX5lpXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i1/3368305815/TB2RD83G7yWBuNjy0FpXXassXXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2Ss7OGFmWBuNjSspdXXbugXXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i4/3368305815/TB2ARr0G21TBuNjy0FjXXajyXXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2mcfxdib.BuNjt_jDXXbOzpXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2nlD0G21TBuNjy0FjXXajyXXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i1/3368305815/TB2swXcyIuYBuNkSmRyXXcA3pXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2oPbCG7SWBuNjSszdXXbeSpXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2mo4CG3mTBuNjy1XbXXaMrVXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i4/3368305815/TB28jV2G1OSBuNjy0FdXXbDnVXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i1/3368305815/TB294WKr5MnBKNjSZFoXXbOSFXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i3/3368305815/TB2hCjDrVooBKNjSZFPXXXa2XXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i3/3368305815/TB207XsG7KWBuNjy1zjXXcOypXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB26qo3yviSBuNkSnhJXXbDcpXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i4/3368305815/TB2sekAyyCYBuNkSnaVXXcMsVXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i4/3368305815/TB2PNT4h4tnkeRjSZSgXXXAuXXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2npmhGY5YBuNjSspoXXbeNFXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i1/3368305815/TB2kRbJrY3nBKNjSZFMXXaUSFXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i3/3368305815/TB2f5n4h4tnkeRjSZSgXXXAuXXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i3/3368305815/TB2RDI5GNSYBuNjSsphXXbGvVXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i4/3368305815/TB2jKKEG4SYBuNjSspjXXX73VXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i4/3368305815/TB2T1yVGY1YBuNjSszeXXablFXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i1/3368305815/TB2GXH0r9cqBKNjSZFgXXX_kXXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i3/3368305815/TB2IuOEG4SYBuNjSspjXXX73VXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i1/3368305815/TB256PSr5MnBKNjSZFCXXX0KFXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i1/3368305815/TB2QtEmrGQoBKNjSZJnXXaw9VXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i4/3368305815/TB2B1GVGY1YBuNjSszeXXablFXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i4/3368305815/TB2MdHWXPfguuRjSszcXXbb7FXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i3/3368305815/TB2Q0LWXPfguuRjSszcXXbb7FXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2vvrSr_mWBKNjSZFBXXXxUFXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2Q9dqyOOYBuNjSsD4XXbSkFXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB20USrG41YBuNjy1zcXXbNcXXa_!!3368305815.jpg","discount_price":"19800","extra":"AutoUploadbyHL","first_cid":"1307","market_price":"66600","mobile":"861234567","name":"她叙2018春秋新款中跟尖头女鞋百搭真皮温柔高跟浅口粗跟单鞋女潮","out_product_id":"574378898908","pay_type":"1","pic":"https://img.alicdn.com/imgextra/i3/3368305815/TB23Sd3G7yWBuNjy0FpXXassXXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i3/3368305815/TB2hvWAGYSYBuNjSspiXXXNzpXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2JyXsG7KWBuNjy1zjXXcOypXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i2/3368305815/TB2EPY0G21TBuNjy0FjXXajyXXa_!!3368305815.jpg|https://img.alicdn.com/imgextra/i1/3368305815/TB2dqh5G29TBuNjy0FcXXbeiFXa_!!3368305815.jpg","product_format":"图案|纯色^鞋垫材质|羊皮^品牌|她叙^货号|T18D037^内里材质|头层猪皮^适用场景|日常^鞋头款式|尖头^跟底款式|粗跟^后跟高|高跟(5-8cm)^尺码|34 35 36 37 38 39^开口深度|浅口^鞋底材质|TPR(牛筋）^上市年份季节|2018年秋季^适用对象|青年（18-40周岁）^颜色分类|灰色 黑色 杏色^帮面材质|牛反绒（磨砂皮）^流行元素|浅口 粗跟^风格|简约^款式|单鞋^闭合方式|套脚^鞋制作工艺|胶粘鞋","recommend_remark":"这个商品很好啊","second_cid":"1408","spec_id":"3337416","spec_pic":"27768834|https://gd2.alicdn.com/imgextra/i4/3368305815/TB2NDM2yviSBuNkSnhJXXbDcpXa_!!3368305815.jpg^27768835|https://gd1.alicdn.com/imgextra/i3/3368305815/TB2D7N7GVuWBuNjSszbXXcS7FXa_!!3368305815.jpg^27768836|https://gd1.alicdn.com/imgextra/i3/3368305815/TB2KO4yG1GSBuNjSspbXXciipXa_!!3368305815.jpg","third_cid":"2653"}&timestamp=2018-09-25 10:33:52&v=1&sign=95b8b25cd548b1e4669005583c2518a1
{"err_no":11,"message":"认证失败, sign校验失败"}


# 注意事项
卖家填写seller.properties
品牌请填写自己认证的品牌，并在后面加括号：例如   brand=宿巢()
推荐语要么不填，要么填写8-50个字

# 上传前检查项
商品标题不超过30个中文，60个西文
必须有详情图片和轮播图片

# TODOLIST
OK: 解决主规格图片为空问题
OK: 实现随机取商家推荐语
TODO: 商品重量KG，平台API不提供上传的能力，0.01-9999.99
TODO: 商品分类获取，并对应到平台里分类

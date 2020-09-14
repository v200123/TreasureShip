package com.jzz.treasureship.model.api

import com.google.gson.JsonObject
import com.jzz.treasureship.model.bean.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.*

interface JzzApiService {

    /**
     * 城市地址：获取全部地点
     */
    @POST("/api/v1/city/getPlaces")
    suspend fun getCityPlaces(@Body body: RequestBody): JzzResponse<CityPlaces>

    /**
     * 城市地址：获取全部地点,重写了传入的参数
     */
    @POST("/api/v1/city/getPlaces")
    suspend fun getCityPlaces02(@Body body: BaseRequestBody): JzzResponse<CityPlaces>


    /**
     * 登录相关：绑定手机
     */
    @POST("/api/v1/common/bindMobile")
    suspend fun bindMobile(@Body body: RequestBody): JzzResponse<User>

    /**
     * 登录相关：绑定微信
     */
    @POST("/api/v1/common/bindWeChat")
    suspend fun bindWeChat(@Body body: RequestBody): JzzResponse<User>

    /**
     * 用户信息：邀请明细
     */
    @POST("/api/v1/user/invitationList")
    suspend fun getInvitedList(@Body body: RequestBody): JzzResponse<InvitedList>

    /**
     * 用户信息：修改个人信息
     */
    @POST("/api/v1/user/modifiedPersonalInfo")
    suspend fun modifinedPersonalInfo(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 用户信息：上传个人资质认证
     */
    @POST("/api/v1/user/savePersonalQualification")
    suspend fun savePersonalQualification(@Body body: RequestBody): JzzResponse<ArrayList<Qualification>>

    /**
     * 用户信息：排行榜
     */
    @POST("/api/v1/user/rank")
    suspend fun rank(@Body body: RequestBody): JzzResponse<Rank>

    /**
     * 登录相关：获取用户信息
     */
    @POST("/api/v1/common/getUserInfo")
    suspend fun getUserInfo(@Body body: RequestBody): JzzResponse<User>

    /**
     * 登录相关：用户登录
     */
    @POST("/api/v1/common/login")
    suspend fun login(@Body body: RequestBody)
            : JzzResponse<User>

    /**
     * 登录相关：登录相关：用户登录(短信验证码)
     */
    @POST("/api/v1/common/loginByCode")
    suspend fun loginByCode(@Body body: RequestBody): JzzResponse<User>

    /**
     * 登录：微信授权登录
     */
    @POST("/api/v1/common/wxLogin")
    suspend fun wxLogin(@Body body: RequestBody): JzzResponse<User>

    /**
     * 登录相关：没有登录
     */
    @POST("/api/v1/common/needLogin")
    suspend fun needLogin(): JzzResponse<User>

    /**
     * 登录相关：用户注册（短信验证码）
     */
    @POST("/api/v1/common/registerByCode")
    suspend fun registerByCode(@Body body: RequestBody): JzzResponse<User>

    /**
     * 短信验证码：发送验证码
     */
    @POST("/api/v1/smsCode/sendCode")
    suspend fun sendSmsCode(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 视频商品：多条件分页获取
     */
    @POST("/api/v1/video/getPageList")
    suspend fun getPageList(@Body body: RequestBody): JzzResponse<VideoPageList>

    /**
     * 用户收藏分类：用户添加收藏
     */
    @POST("/api/v1/userCollect/addCollect")
    suspend fun addCollect(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 用户收藏分类：用户添加收藏分类
     */
    @POST("/api/v1/userCollect/addCollectCategory")
    suspend fun addCollectCategory(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 用户收藏分类：用户移动收藏
     */
    @POST("/api/v1/userCollect/moveCollect")
    suspend fun moveCollect(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 用户收藏分类：用户取消收藏
     */
    @POST("/api/v1/userCollect/cancelCollect")
    suspend fun cancelCollect(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 用户收藏分类：用户删除收藏分类
     */
    @POST("/api/v1/userCollect/deleteCollectCategory")
    suspend fun deleteCollectCategory(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 用户收藏分类：获取用户收藏分类列表
     */
    @POST("/api/v1/userCollect/getCollectCategory")
    suspend fun getCollectCategory(@Body body: RequestBody): JzzResponse<ArrayList<CollectCategory>>

    /**
     * 用户收藏分类：获取收藏列表
     */
    @POST("/api/v1/userCollect/getCollectVideoList")
    suspend fun getCollectVideoList(@Body body: RequestBody): JzzResponse<VideoPageList>

    /**
     * 视频评论：home_comments、评论回复
     */
    @POST("/api/v1/comment/addComment")
    suspend fun addComment(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 视频评论：评论点赞
     */
    @POST("/api/v1/comment/addPraise")
    suspend fun addPraise(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 视频评论：多条件分页获取
     */
    @POST("/api/v1/comment/getCommentPageList")
    suspend fun getCommentPageList(@Body body: RequestBody): JzzResponse<CommentPageList>

    /**
     * 搜索相关接口：搜索获取品牌列表
     */
    @POST("/api/v1/search/getBrandList")
    suspend fun getBrandList(@Body body: RequestBody): JzzResponse<ArrayList<Brand>>

    /**
     * 搜索相关接口：搜索获取病症列表
     */
    @POST("/api/v1/search/getIllnessList")
    suspend fun getIllnessList(@Body body: RequestBody): JzzResponse<ArrayList<Illness>>

    /**
     * 搜索相关接口：搜索获取热搜列表
     */
    @POST("/api/v1/search/getHotList")
    suspend fun getHotSearchList(@Body body: RequestBody): JzzResponse<ArrayList<HotSearch>>

    /**
     * 搜索相关接口：搜索条件获取视频列表
     */
    @POST("/api/v1/search/getSearchPageList")
    suspend fun getSearchPageList(@Body body: RequestBody): JzzResponse<VideoPageList>

    /**
     * 首页tab：获取首页tab
     */
    @POST("/api/v1/video/getVideoCategory")
    suspend fun getHomeTabs(@Body body: RequestBody): JzzResponse<HomeTabBean>

    /**
     * 商品信息：获取商品详情
     */
    @POST("/api/v1/goods/getGoodsDetail")
    suspend fun getGoodsDetail(@Body body: RequestBody): JzzResponse<GoodsDetail>

    /**
     * shopcar_no_goods：添加到购物车
     */
    @POST("/api/v1/cart/addCart")
    suspend fun addCart(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * shopcar_no_goods：购物车结算
     */
    @POST("/api/v1/cart/cartSettlement")
    suspend fun cartSettlement(@Body body: RequestBody): JzzResponse<ShopcarBuyBean>

    /**
     * shopcar_no_goods：获取购物车商品数量
     */
    @POST("/api/v1/cart/getCount")
    suspend fun getCount(@Body body: RequestBody): JzzResponse<Int>

    /**
     * shopcar_no_goods：直接购
     */
    @POST("/api/v1/cart/getDirectBuy")
    suspend fun getDirectBuy(@Body body: RequestBody): JzzResponse<ShopcarBuyBean>

    /**
     * shopcar_no_goods：获取购物车商品列表
     */
    @POST("/api/v1/cart/getCartList")
    suspend fun getCartList(@Body body: RequestBody): JzzResponse<CartList>

    /**
     * shopcar_no_goods：获取购物车商品列表
     */
    @POST("/api/v1/cart/deleteCart")
    suspend fun deleteCart(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 订单信息：订单支付（微信支付）
     */
    @POST("/api/v1/order/payByOrder")
    suspend fun payByOrder(@Body body: RequestBody): JzzResponse<OrdersGoPayBean>

    /**
     * 订单信息：查询订单状态
     */
    @POST("/api/v1/order/queryOrderStatus")
    suspend fun queryOrderStatus(@Body body: RequestBody): JzzResponse<Data>

    /**
     * 订单信息：确认收货
     */
    @POST("/api/v1/order/sureReceived")
    suspend fun sureReceived(@Body body: RequestBody): JzzResponse<Order>

    /**
     * 订单信息：申请退款
     */
    @POST("/api/v1/order/orderRefund")
    suspend fun refundQuery(@Body body: RequestBody): JzzResponse<Refund>

    /**
     * 订单信息：添加医嘱
     */
    @POST("/api/v1/order/addDoctorAdvice")
    suspend fun addDoctorAdvice(@Body body: RequestBody): JzzResponse<AddAdviceResBean>

    /**
     * 订单信息：创建支付订单
     */
    @POST("/api/v1/order/createOrder")
    suspend fun createOrder(@Body body: RequestBody): JzzResponse<Order>

    /**
     * 订单信息：获取订单列表
     */
    @POST("/api/v1/order/getOrderList")
    suspend fun getOrderList(@Body body: RequestBody): JzzResponse<Orders>

    /**
     * 收货地址管理：添加收货地址
     */
    @POST("/api/v1/receiveAddress/add")
    suspend fun addPayAddress(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 收货地址管理：删除收货地址
     */
    @POST("/api/v1/receiveAddress/delete")
    suspend fun delPayAddress(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 收货地址管理：分页获取收货地址
     */
    @POST("/api/v1/receiveAddress/getPageList")
    suspend fun getAddressPageList(@Body body: RequestBody): JzzResponse<AddressPageList>

    /**
     * 收货地址管理：获取默认购物地址
     */
    @POST("/api/v1/receiveAddress/getPayAddress")
    suspend fun getPayAddress(@Body body: RequestBody): JzzResponse<Address>

    /**
     * 收货地址管理：修改收货地址
     */
    @POST("/api/v1/receiveAddress/update")
    suspend fun updatePayAddress(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 收货地址管理：设置为默认收货地址
     */
    @POST("/api/v1/receiveAddress/updateIsDefault")
    suspend fun updateDefalutPayAddress(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 通讯录：获取通讯录列表
     */
    @POST("/api/v1/member/list")
    suspend fun getMemberList(@Body body: RequestBody): JzzResponse<Contacter>

    /**
     * 通讯录：获取产品列表
     */
    @POST("/api/v1/member/goodsList")
    suspend fun getGoodsList(@Body body: RequestBody): JzzResponse<ContacterGoods>

    /**
     * 通讯录：设置提醒
     */
    @POST("/api/v1/member/setNotice")
    suspend fun setNotice(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 意见反馈：意见反馈
     */
    @POST("/api/v1/suggestion/suggestion")
    suspend fun sendSuggestion(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 订单：获取订单物流信息
     */
    @POST("/api/v1/order/getOrderExpress")
    suspend fun getOrderExpress(@Body body: RequestBody): JzzResponse<OrderExpress>

    /**
     * 我的钱包：余额信息
     */
    @POST("/api/v1/accountRecords/balance")
    suspend fun getBalance(@Body body: RequestBody): JzzResponse<WalletBalance>

    /**
     * 我的钱包：列表
     */
    @POST("/api/v1/accountRecords/list")
    suspend fun getBalanceList(@Body body: RequestBody): JzzResponse<WalletList>

    /**
     * icon_withdraw_comfirm：申请提现接口
     */
    @POST("/api/withdraw/apply")
    suspend fun askWithdraw(@Body body: RequestBody): JzzResponse<Unit>

    /**
     * 文件上传：文件上传
     */
    @Multipart
    @POST("/api/v1/common/uploadImage")
    suspend fun uploadFile(@Part part: MultipartBody.Part): JzzResponse<UploadImgBean>

    /**
     * APP：App检测更新
     */
    @POST("/api/v1/version/get")
    suspend fun checkUpdate(@Body body: RequestBody): JzzResponse<UpdateAppBean>

    /**
     * 文档内容：获取文档内容
     */
    @POST("/api/v1/doc/getDocContent")
    suspend fun getDocContent(@Body body: RequestBody): JzzResponse<Agreement>

    /**
     * 文档内容：获取合同内容
     */
    @POST("/api/v1/doc/getDocContract")
    suspend fun getDocContract(@Body body: RequestBody): JzzResponse<Agreement>

    /**
     * 获取未答题目
     */
    @POST("/api/v1/questionnaire/getQuestionnaire")
    suspend fun getQuestionnaire(@Body body: RequestBody): JzzResponse<QuestionnaireResponseVo>

    /**
     * 提交问卷
     */
    @POST("/api/v1/questionnaire/submitQuestionnaire")
    suspend fun submitQuestionnaire(@Body body: RequestBody): JzzResponse<Reward.RedEnvelopeRecordVo>

    /**
     * 领取红包
     */
    @POST("/api/v1/redEnvelope/receiveRedEnvelope")
    suspend fun receiveRedEnvelope(@Body body: RequestBody): JzzResponse<Reward>

    /**
     * 获取活动图片
     */
    @POST("/api/v1/ad/getAdList")
    suspend fun getAdList(@Body body: RequestBody): JzzResponse<Ad>


    /**
     * 获取身份信息，该接口用于用户认证的身份选择
     */
    @POST("/api/v1/userOccupation/getUserOccupation")
    suspend fun getAuthType(@Body body: BaseRequestBody):JzzResponse<UserAuthTypeBean>

    /**
     *
     */
    @POST("/api/v1/userOccupation/getUserOccupationCredentials")
    suspend fun getOccupationType(@Body body: BaseRequestBody):JzzResponse<OccupationBean>
       @POST("/api/v1/hospitalDepartment/getHospitalDepartment")
    suspend fun getHospitalType(@Body body: BaseRequestBody):JzzResponse<DepartmentBean>

    @POST("/api/v1/user/savePersonalQualification")
    suspend fun uploadInformation(@Body body: BaseRequestBody):JzzResponse<Qualification>


}

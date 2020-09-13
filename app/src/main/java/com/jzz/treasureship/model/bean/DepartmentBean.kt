package com.jzz.treasureship.model.bean

import com.google.gson.annotations.SerializedName


/**
 *@date: 2020/9/13
 *@describe: 获取用户认证的科室实体类
 *@Auth: 29579
 *
 * 需要的请求体为：｛
 * {
"body": {
"name": "string"
},
"header": {
"os": "ios",
"pageNum": 1,
"pageSize": 20
}
}
｝
 *
 **/
class DepartmentBean(
    @SerializedName("list")
    var mList: List<DepartmentType> = listOf()
) {

    class DepartmentType(
        @SerializedName("createTime")
        var mCreateTime: String = "", // string
        @SerializedName("id")
        var mId: Int = 0, // 0
        @SerializedName("isEnable")
        var mIsEnable: Int = 0, // 0
        @SerializedName("name")
        var mName: String = "", // string
        @SerializedName("remark")
        var mRemark: String = "", // string
        @SerializedName("sort")
        var mSort: Int = 0 // 0
    )
}

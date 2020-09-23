package com.jzz.treasureship.model.repository

import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.AddressPageList
import com.jzz.treasureship.model.bean.CityPlace
import com.jzz.treasureship.model.bean.CityPlaces
import com.jzz.treasureship.model.bean.JzzResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class AddressRepository : BaseRepository() {
    //获取默认收货地址
    suspend fun getPlaces(): Result<JzzResponse<CityPlaces>> {
        return safeApiCall(
            call = { requestCityPlaces() },
            errorMessage = "获取全部地点请求失败!"
        )
    }

    //请求获取默认收货地址
    private suspend fun requestCityPlaces(): Result<JzzResponse<CityPlaces>> {
        val root = JSONObject()
        val body = JSONObject()
        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getCityPlaces(requestBody)

        return executeResponse(response)
    }

    //获取默认收货地址
    suspend fun getAddressPageList(pageNum: Int): Result<JzzResponse<AddressPageList>> {
        return safeApiCall(
            call = { requestAddressPageList(pageNum) },
            errorMessage = "分页获取收货地址请求失败!"
        )
    }

    //请求获取默认收货地址
    private suspend fun requestAddressPageList(pageNum: Int): Result<JzzResponse<AddressPageList>> {
        val root = JSONObject()
        val header = JSONObject()

        header.put("os", "android")
        header.put("pageNum", pageNum)
        header.put("pageSize", 20)

        val body = JSONObject()

        root.put("body", body)
        root.put("header", header)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getAddressPageList(requestBody)

        return executeResponse(response)
    }

    //添加地址
    suspend fun addPayPlace(
        address: String,
        consignee: String,
        mobile: String,
        topCityPlace: CityPlace,
        midCityPlace: CityPlace,
        lastCityPlace: CityPlace,
        isDefault: Int,
        phone: String = ""
    ): Result<JzzResponse<Unit>> {
        return safeApiCall(
            call = {
                requestAddPayPlace(
                    address,
                    consignee,
                    mobile,
                    topCityPlace,
                    midCityPlace,
                    lastCityPlace,
                    isDefault,
                    phone
                )
            },
            errorMessage = "新增收货地址失败!"
        )
    }

    private suspend fun requestAddPayPlace(
        address: String,
        consignee: String,
        mobile: String,
        topCityPlace: CityPlace,
        midCityPlace: CityPlace,
        lastCityPlace: CityPlace,
        isDefault: Int,
        phone: String = ""
    ): Result<JzzResponse<Unit>> {
        val root = JSONObject()

        val body = JSONObject()
        //详细地址
        body.put("address", address)
        body.put("city", midCityPlace.id)
        body.put("cityName", midCityPlace.areaName)
        body.put("consignee", consignee)
        body.put("district", lastCityPlace.id)
        body.put("districtName", lastCityPlace.areaName)
        body.put("isDefault", isDefault)
        body.put("mobile", mobile)
        body.put("phone", phone)
        body.put("province", topCityPlace.id)
        body.put("provinceName", topCityPlace.areaName)


        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.addPayAddress(requestBody)

        return executeResponse(response)
    }

    //修改地址
//    suspend fun updatePayPlace(
//        id: Int,
//        address: String,
//        consignee: String,
//        mobile: String,
//        topCityPlace: CityPlace,
//        midCityPlace: CityPlace,
//        lastCityPlace: CityPlace,
//        isDefault: Int,
//        phone: String = ""
//    ): Result<JzzResponse<Unit>> {
//        return safeApiCall(
//            call = {
//                requestUpdatePayPlace(
//                    id,
//                    address,
//                    consignee,
//                    mobile,
//                    topCityPlace,
//                    midCityPlace,
//                    lastCityPlace,
//                    isDefault,
//                    phone
//                )
//            },
//            errorMessage = "新增收货地址失败!"
//        )
//    }

//    private suspend fun  requestUpdatePayPlace(
//        id: Int,
//        address: String,
//        consignee: String,
//        mobile: String,
//        topCityPlace: CityPlace,
//        midCityPlace: CityPlace,
//        lastCityPlace: CityPlace,
//        isDefault: Int,
//        phone: String = ""
//    ): Result<JzzResponse<Unit>> {
//        val root = JSONObject()
//
//        val body = JSONObject()
//        //详细地址
//        body.put("id", id)
//        body.put("address", address)
//        body.put("city", midCityPlace.id)
//        body.put("cityName", midCityPlace.areaName)
//        body.put("consignee", consignee)
//        body.put("district", lastCityPlace.id)
//        body.put("districtName", lastCityPlace.areaName)
//        body.put("isDefault", isDefault)
//        body.put("mobile", mobile)
//        body.put("phone", phone)
//        body.put("province", topCityPlace.id)
//        body.put("provinceName", topCityPlace.areaName)
//
//
//        root.put("body", body)
//
//        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
//        val response = JzzRetrofitClient.service.updatePayAddress(requestBody)
//
//        return executeResponse(response)
//    }


    //添加地址
    suspend fun delAddress(addressId: Int): Result<JzzResponse<Unit>> {
        return safeApiCall(
            call = {
                requestDelAddress(addressId)
            },
            errorMessage = "删除收货地址失败!"
        )
    }

    private suspend fun requestDelAddress(addressId: Int): Result<JzzResponse<Unit>> {
        val root = JSONObject()

        val body = JSONObject()

        body.put("id", addressId)

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.delPayAddress(requestBody)

        return executeResponse(response)
    }

    //修改默认地址
    suspend fun setAddressDefault(addressId: Int): Result<JzzResponse<Unit>> {
        return safeApiCall(
            call = {
                requestSetDefaultAddress(addressId)
            },
            errorMessage = "设置默认地址失败!"
        )
    }

    private suspend fun requestSetDefaultAddress(addressId: Int): Result<JzzResponse<Unit>> {
        val root = JSONObject()

        val body = JSONObject()

        body.put("id", addressId)

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.updateDefalutPayAddress(requestBody)

        return executeResponse(response)
    }
}
package com.jzz.treasureship.model.api

import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.jzz.treasureship.utils.PreferenceUtils
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import okio.IOException
import org.json.JSONObject
import java.io.*


class CommonInterceptor : Interceptor {

    @Synchronized
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = rebuildRequest(chain.request())

        return chain.proceed(request)
    }

    @Throws(IOException::class)
    private fun rebuildRequest(request: Request): Request {

        return when (request.method) {
            "POST" -> {
                rebuildPostRequest(request)
            }
            "GET" -> {
                rebuildGetRequest(request)
            }
            else -> {
                request
            }
        }
    }

    /**
     * 对post请求添加统一参数
     */
    private fun rebuildPostRequest(request: Request): Request {
        val signParams: MutableMap<String, String?> = HashMap() // 假设你的项目需要对参数进行签名
        val originalRequestBody: RequestBody = request.body!!
        var newRequestBody: RequestBody

        // 传统表单
        if (originalRequestBody is FormBody) {
            val builder = FormBody.Builder()
            val requestBody = request.body as FormBody
            val fieldSize = requestBody.size
            for (i in 0 until fieldSize) {
                builder.add(requestBody.name(i), requestBody.value(i))
                signParams[requestBody.name(i)] = requestBody.value(i)
            }
            if (commonParams != null && commonParams!!.size > 0) {
                signParams.putAll(commonParams!!)
                for (paramKey in commonParams!!.keys) {
                    builder.add(paramKey, commonParams!![paramKey]!!)
                }
            }
            // ToDo 此处可对参数做签名处理 signParams
            /**
             * String sign = SignUtil.sign(signParams);
             * builder.add("sign", sign);
             */
            newRequestBody = builder.build()
        }
        // 文件
        else if (originalRequestBody is MultipartBody) {
            val requestBody = request.body as MultipartBody
            val multipartBodybuilder = MultipartBody.Builder()
            for (i in 0 until requestBody.size) {
                val part = requestBody.part(i)
                multipartBodybuilder.addPart(part)
                /*
                 上传文件时，请求方法接收的参数类型为RequestBody或MultipartBody.Part参见ApiService文件中uploadFile方法
                 RequestBody作为普通参数载体，封装了普通参数的value; MultipartBody.Part即可作为普通参数载体也可作为文件参数载体
                 当RequestBody作为参数传入时，框架内部仍然会做相关处理，进一步封装成MultipartBody.Part，因此在拦截器内部，
                 拦截的参数都是MultipartBody.Part类型
                 */
/*
                 1.若MultipartBody.Part作为文件参数载体传入，则构造MultipartBody.Part实例时，
                 需使用MultipartBody.Part.createFormData(String name, @Nullable String filename, RequestBody body)方法，
                 其中name参数可作为key使用(因为你可能一次上传多个文件，服务端可以此作为区分)且不能为null，
                 body参数封装了包括MimeType在内的文件信息，其实例创建方法为RequestBody.create(final @Nullable MediaType contentType, final File file)
                 MediaType获取方式如下：
                 String fileType = FileUtil.getMimeType(file.getAbsolutePath());
                 MediaType mediaType = MediaType.parse(fileType);

                 2.若MultipartBody.Part作为普通参数载体，建议使用MultipartBody.Part.createFormData(String name, String value)方法创建Part实例
                   name可作为key使用，name不能为null,通过这种方式创建的实例，其RequestBody属性的MediaType为null；当然也可以使用其他方法创建
                 */
/*
                  提取非文件参数时,以RequestBody的MediaType为判断依据.
                  此处提取方式简单暴力。默认part实例的RequestBody成员变量的MediaType为null时，part为非文件参数
                  前提是:
                  a.构造RequestBody实例参数时，将MediaType设置为null
                  b.构造MultipartBody.Part实例参数时,推荐使用MultipartBody.Part.createFormData(String name, String value)方法，或使用以下方法
                    b1.MultipartBody.Part.create(RequestBody body)
                    b2.MultipartBody.Part.create(@Nullable Headers headers, RequestBody body)
                    若使用方法b1或b2，则要求

                  备注：
                  您也可根据需求修改RequestBody的MediaType，但尽量保持外部传入参数的MediaType与拦截器内部添加参数的MediaType一致，方便统一处理
                 */
                val mediaType: MediaType? = part.body.contentType()
                if (mediaType == null) {
                    var normalParamKey: String
                    var normalParamValue: String
                    try {
                        normalParamValue = getParamContent(requestBody.part(i).body)
                        val headers: Headers? = part.headers
                        if (!TextUtils.isEmpty(normalParamValue) && headers != null) {
                            for (name in headers.names()) {
                                val headerContent: String = headers.get(name)!!
                                if (!TextUtils.isEmpty(headerContent)) {
                                    val normalParamKeyContainer =
                                        headerContent.split("name=\"").toTypedArray()
                                    if (normalParamKeyContainer.size == 2) {
                                        normalParamKey =
                                            normalParamKeyContainer[1].split("\"").toTypedArray()[0]
                                        signParams[normalParamKey] = normalParamValue
                                        break
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            if (commonParams != null && commonParams!!.isNotEmpty()) {
                signParams.putAll(commonParams!!)
                for (paramKey in commonParams!!.keys) { // 两种方式添加公共参数
// method 1
                    multipartBodybuilder.addFormDataPart(
                        paramKey,
                        commonParams!![paramKey]!!
                    )
                    // method 2
//                    MultipartBody.Part part = MultipartBody.Part.createFormData(paramKey, commonParams.get(paramKey));
//                    multipartBodybuilder.addPart(part);
                }
            }
            // ToDo 此处可对参数做签名处理 signParams
            /**
             * String sign = SignUtil.sign(signParams);
             * multipartBodybuilder.addFormDataPart("sign", sign);
             */
            newRequestBody = multipartBodybuilder.build()
        } else {
            try {
                val jsonObject: JSONObject = if (originalRequestBody.contentLength() == 0L) {
                    JSONObject()
                } else {
                    JSONObject(getParamContent(originalRequestBody))
                }
                if (commonParams != null && commonParams!!.isNotEmpty()) {
                    for (commonParamKey in commonParams!!.keys) {
                        jsonObject.put(commonParamKey, commonParams!![commonParamKey])
                    }
                }
                if (!jsonObject.has("header")) {
                    val header = JSONObject()
                    header.put("os", "android")
                    header.put("pageNum", 1)
                    header.put("pageSize", 20)
                    jsonObject.put("header", header)
                }
                // ToDo 此处可对参数做签名处理
                /**
                 * String sign = SignUtil.sign(signParams);
                 * jsonObject.put("sign", sign);
                 */
                newRequestBody = jsonObject.toString().toRequestBody(originalRequestBody.contentType())
            } catch (e: Exception) {
                newRequestBody = originalRequestBody
                e.printStackTrace()
            }
        }
        val access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
        return if (access.isBlank() or ("null" == access) or request.url.toString().contains("/api/v1/smsCode/sendCode")) {
            request.newBuilder()
                .addHeader("Accept", "*/*")
                .method(request.method, newRequestBody)
                .build()
        } else {
            request.newBuilder()
                .addHeader("Accept", "*/*")
                .addHeader("accessToken", access)
                .method(request.method, newRequestBody)
                .build()
        }
    }

    /**
     * 获取常规post请求参数
     */
    @Throws(IOException::class)
    private fun getParamContent(body: RequestBody?): String {
        val buffer = Buffer()
        body!!.writeTo(buffer)
        return buffer.readUtf8()
    }

    /**
     * 对get请求做统一参数处理
     */
    private fun rebuildGetRequest(request: Request): Request {
        if (commonParams == null || commonParams!!.isEmpty()) {
            return request
        }
        val url: String = request.url.toString()
        val separatorIndex = url.lastIndexOf("?")
        val sb = StringBuilder(url)
        if (separatorIndex == -1) {
            sb.append("?")
        }
        for (commonParamKey in commonParams!!.keys) {
            sb.append("&").append(commonParamKey).append("=")
                .append(commonParams!![commonParamKey])
        }
        val requestBuilder: Request.Builder = request.newBuilder()
        return requestBuilder.url(sb.toString()).build()
    }

    companion object {
        private var commonParams: MutableMap<String, String?>? = null
        @Synchronized
        fun setCommonParam(commonParams: Map<String, String?>?) {
            if (commonParams != null) {
                if (Companion.commonParams != null) {
                    Companion.commonParams!!.clear()
                } else {
                    Companion.commonParams = HashMap()
                }
                for (paramKey in commonParams.keys) {
                    Companion.commonParams!![paramKey] = commonParams[paramKey]
                }
            }
        }

        @Synchronized
        fun updateOrInsertCommonParam(paramKey: String, paramValue: String) {
            if (commonParams == null) {
                commonParams = HashMap()
            }
            commonParams!![paramKey] = paramValue
        }

        @Throws(IOException::class)
        fun toByteArray(body: RequestBody): ByteArray {
            val buffer = Buffer()
            body.writeTo(buffer)
            val inputStream: InputStream = buffer.inputStream()
            val output = ByteArrayOutputStream()
            val bufferWrite = ByteArray(4096)
            var n: Int
            while (-1 != inputStream.read(bufferWrite).also { n = it }) {
                output.write(bufferWrite, 0, n)
            }
            return output.toByteArray()
        }
    }
}

package com.example.rocketcat.ui.fragment.response


import com.example.common.data.network.FlowOfResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {


    /**
     * 获取首页文章数据
     */
    @GET("article/list/{page}/json")
    fun getArticleList(@Path("page") pageNo: Int): FlowOfResponse<ArticleData>

}
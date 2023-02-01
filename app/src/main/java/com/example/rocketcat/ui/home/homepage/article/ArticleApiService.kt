package com.example.rocketcat.ui.home.homepage.article


import com.example.common.data.network.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ArticleApiService {


    /**
     * 获取首页文章数据
     */
    @GET("article/list/{page}/json")
    suspend fun getArticleList(@Path("page") pageNo: Int): Response<ArticleData>

//    @GET("article/list/{page}/json")
//    fun getArticleList(@Path("page") pageNo: Int): FlowOfResponse<ArticleData>

}
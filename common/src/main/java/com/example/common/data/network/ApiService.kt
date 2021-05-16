package com.example.common.data.network

import com.example.common.data.network.response.ApiPagerResponse
import com.example.common.data.network.response.ApiResponse
import com.example.common.data.network.response.ArticleResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    companion object {
        const val SERVER_URL = "https://wanandroid.com/"
    }


    /**
     * 获取首页文章数据
     */
    @GET("article/list/{page}/json")
    suspend fun getArticleList(@Path("page") pageNo: Int): ApiResponse<ApiPagerResponse<List<ArticleResponse>>>

}
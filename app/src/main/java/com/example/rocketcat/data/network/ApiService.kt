package com.example.rocketcat.data.network

import com.example.rocketcat.data.network.response.ApiPagerResponse
import com.example.rocketcat.data.network.response.ApiResponse
import com.example.rocketcat.data.network.response.ArticleResponse
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
    suspend fun getAritrilList(@Path("page") pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>>

}
package com.example.common.data.network

import com.example.common.data.network.response.ApiPagerResponse
import com.example.common.data.network.response.ApiResponse
import com.example.common.data.network.response.ArticleResponse

object RequestManager {


    /**
     * 获取首页文章数据
     */
    suspend fun getHomeData(pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<ArticleResponse>>> =
        NetworkApi.service.getAritrilList(pageNo)


}
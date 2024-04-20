package com.example.common.dsl.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.common.dsl.IListItemUnique
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


data class PagingResponse(val dataList: List<IListItemUnique>, val hasMore: Boolean)

fun pagingDataOf(
    pageSize: Int,
    prefetchDistance: Int = pageSize,
    block: suspend (from: Int) -> PagingResponse
) = Pager(
    PagingConfig(
        pageSize = pageSize,
        prefetchDistance = prefetchDistance,
        enablePlaceholders = false
    )
) {
    pagingSourceOf(block)
}.flow

fun pagingSourceOf(block: suspend (from: Int) -> PagingResponse) = object : PagingSource<Int, IListItemUnique>() {
    override fun getRefreshKey(state: PagingState<Int, IListItemUnique>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IListItemUnique> {
        val from = params.key ?: 0
        return try {
            val (list, hasMore) = withContext(Dispatchers.IO) { block.invoke(from) }
            LoadResult.Page(data = list, prevKey = null, nextKey = if (hasMore) from + list.size else null)
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

}
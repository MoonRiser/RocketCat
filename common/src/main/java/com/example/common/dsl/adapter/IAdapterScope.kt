package com.example.common.dsl.adapter

import com.example.common.dsl.IListItem

interface IAdapterScope {

    fun currentList(): List<IListItem>

    val configMap: ConfigMap

}

interface ListAdapterScope : IAdapterScope
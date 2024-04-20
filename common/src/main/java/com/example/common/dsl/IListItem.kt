package com.example.common.dsl

import kotlin.reflect.KClass

interface IListItem {

    fun qualifiedType(): Int = qualifiedTypeOf(this::class)

    companion object {
        inline fun <reified T> qualifiedTypeOf(): Int {
            return qualifiedTypeOf(T::class)
        }

        fun qualifiedTypeOf(clazz: KClass<*>): Int {
            return clazz.hashCode()
        }

    }
}

interface IListItemUnique : IListItem {
    val uniqueId: String
}

abstract class DataItem : IListItem

abstract class DataItemUnique(uniqueId: Any) : IListItemUnique {
    override val uniqueId: String = uniqueId.toString()
}

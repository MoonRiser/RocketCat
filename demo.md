![xx](https://github.com/MoonRiser/RocketCat/blob/15220ea6da365719bbf64f7accc3d8d34b7b3d40/9acf4576-6940-4951-803f-c8aee6e2ef3f.gif)
```
// in fragment
private val articleAdapter = pagingAdapterOf { // 创建 adapter 的dsl
    withViewHolder<ArticleUIData, ItemRvArticleBinding>(){  
        doOnItemClick{ toast("just click $adapterPostion ") }
        itemBinding.btn.setOnLongClickListener{ toast("long click $data") }
        doOnBind{ /* 对应 onBindViewHolder */  }
    }
    withViewHolder<StickyUIData, ItemRvStickyBinding>(isSticky = true) // 吸顶
}

binding.recyclerView.apply {
    adapter = articleAdapter.withRefreshHeaderAndLoadStateFooter() // 加上刷新的header 和 加载更多的footer
    itemAnimator = null
    layoutManager = StickyHeaderLinearLayoutManager()
}

lifecycleScope.launch {
      viewModel.articleFlow.collectLatest {
          articleAdapter.submitData(it)
       }
 }


// in ViewModel
val articleFlow = pagingDataOf(pagesize = 20){ from ->
    val rsp = repository.requestApi(start=from) // 拉取每页数据 
    val dataList = rsp.list +  StickyUIData() // 每页后面跟随一个吸顶的 item
    PagingResponse(dataList,rsp.hasMore)
}.cachedIn(viewModelScope)


```



![yy](https://github.com/MoonRiser/RocketCat/blob/15220ea6da365719bbf64f7accc3d8d34b7b3d40/output.png)

```
data class Config( val dividend :List<NextDividend>, val price:InputRange) // 配置

val configFlow = MutableStateFlow(Config.Default) // 配置 flow

val filterPage = filterPageOf(configFlow){ // 筛选页面 dsl 构建器
    choice(Config::dividend,){  // 选项
        onSelect{
            toast("just click $it")
            notifyChange(KEY_PRICE) // 通知Price 模块更新
         }
    }
    inputBox(Config::price,key=KEY_PRICE ){ // 输入框
        visible{ snapshot.dividend.any{ it  == ONE_WEEK } } // 只有在‘下次派息‘ 选中 ‘一周内‘ 时才展示
    }
}

binding.btn.setOnClickListener{
    filterPage.start() // 启动筛选页面
}

lifecycleScope.launch{
    configFlow.collect{  //  收集配置 flow
        viewModel.request(it)
    }
}
```

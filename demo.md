![xx](https://github.com/MoonRiser/RocketCat/blob/15220ea6da365719bbf64f7accc3d8d34b7b3d40/9acf4576-6940-4951-803f-c8aee6e2ef3f.gif)
```
private val articleAdapter = pagingAdapterOf { // 创建 adapter 的dsl
    withViewHolder<ArticleBean, ItemRvArticleBinding>(){
        doOnItemClick{ toast("just click $adapterPostion ") }
    }
    withViewHolder<AdBean, ItemRvAdBinding>()
    withViewHolder<StickyBean, ItemRvStickyBinding>(isSticky = true)
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

```



![yy](https://github.com/MoonRiser/RocketCat/blob/15220ea6da365719bbf64f7accc3d8d34b7b3d40/output.png)

```
data class Config( val aa:List<AA>, val price:InputRange) // 配置

val configFlow = MutableStateFlow(Config.Default) // 配置 flow

val filterPage = filterPageOf(configFlow){ // 筛选页面 dsl 构建器
    choice(Config::aa){  // 选项
        onSelect{ toast("just click : $it") }
    }
    inputBox(Config::price) // 输入框
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

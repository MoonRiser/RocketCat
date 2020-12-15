# android 四级联动地址选择器
之前遇到一个需求，需要一个四级联动的地址选择器，Github上找了许久，要么是年代过于久远不维护，要么是只有三级联动没有满意的，遂自己动手实现，于是有了本项目

-------
## 技术
本人喜欢新东西，故本项目都是采用的较新的框架和技术，对于安卓5.0之前的兼容性未经过测试。
* 项目源码是kotlin编写
* 数据库是Jetpack的Room
* ViewPager2

## 数据源
国内的地址数据来源于Github上开源项目[Administrative-divisions-of-China](https://github.com/modood/Administrative-divisions-of-China#administrative-divisions-of-china)，该项目所爬数据的最终来源是国家统计局等政府权威部门，目前最新的数据是2019年（2020年2月发布）
## 预览
![QQ20201107-185553-HD](https://raw.githubusercontent.com/MoonRiser/images/master/20201107190129.jpg)


## 使用
Gradle依赖 （中间的版本号代表数据更新的年份），Jcenter仓库托管

`implementation  'com.xres.selecor:address-selector:0.2019.14'`

### 主要api
* AddressSelector 已实现的带对话框样式，可以直接使用
    
```
    AddressSelector(context) 
      show()；
      dismiss()；
      setOnSelectCompletedListener((addressSelector, province, city, area, street) ->{
        addressSelector.dismiss;
        city.name;//名称
        city.code;//对应的行政编码
    } )
```
* AddressSelectorView 不包含对话框，可以自行定制

```
    setOnSelectCompletedListener()
```

## 最后
有什么bug问题可以提issue，我会抽空解决

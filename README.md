#RecyclerViewHeader

## 概述
RecyclerViewHeader是一个很方便的为RecyclerView添加HeaderView的开源库。

目前Github上start数最高的两个类似项目分别为[RecyclerViewHeader](https://github.com/blipinsk/RecyclerViewHeader)和[HeaderRecyclerView](https://github.com/Karumi/HeaderRecyclerView)

* RecyclerViewHeader

	RecyclerViewHeader大致是通过监听滑动距离的原理来实现的(没详细看)。此库存在一些issues，我遇到就是当headerView复杂的时候滑动效果不好，headerView与recyclerView中间会留有空白，特别时fling的时候，详细请看[issues](https://github.com/blipinsk/RecyclerViewHeader/issues)
	
* HeaderRecyclerView

	HeaderRecyclerView是通过viewtype的方式实现的，这种实现方式的问题是HeaderView是能被回收掉的，而且调用notifyDatasetChanged时headerView也会被刷新。
	

由于上面两个库并不能满足我的需求，所以我就自己写了一个库来实现这个功能。目前此库支持可以支持位RecyclerView添加HeaderView的需求，但是现在目前还存在一些问题没有解决

* fling的时候HeaderView与RecyclerView中间还是有一些分割感，不过可以设置背景色与HeaderView和RecyclerView的背景色相同来规避这个问题。

* 当RecyclerView的item不足以填充填充完RecyclerView时，此时还是能滑动HeaderView。

目前所支持的一些特性

* 可以响应HeaderView的左右滑动事件

* 类似于ScrollView的滑动效果


## 使用

使用是非常的简单

		private void initRecycler() {
        	mRecycler = (RecyclerView) findViewById(recycler);
        	mRecycler.setHasFixedSize(false);
        	mRecycler.setLayoutManager(new LinearLayoutManager(this)）);
        	mRecycler.setAdapter(new MyAdapter());

        	RecyclerViewHeader header = RecyclerViewHeader.fromXml(this, 	R.layout.recycler_view_header) ;
        	header.attachTo(mRecycler);

    }
    
看到没有只需要两行代码，至于headerView中的元素直接findViewById()就好。
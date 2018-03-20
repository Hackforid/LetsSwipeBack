# LetsSwipeBack

Android滑动返回lib

支持滑动时将Activity置为透明，无需修改主题。滑动结束后将Activity恢复为非透明，防止上一个Activity未stop，产生重复绘制影响性能。



## Usage

```Java
// init
	SwipeManager.inst().registerApplication(application)
   
// in activity
    class SampleActivity : Activity {
        override fun onPostCreate(savedInstanceState: Bundle?) {
        	super.onPostCreate(savedInstanceState)
        	SwipeManager.inst().createPage(this)
    	}
    }

// other 
    SwipeManager.inst().setSwipeEnable(Activity activity, boolean enable)
    SwipeManager.inst().setEnableResetTranslucent(Activity activity, boolean enable)


```


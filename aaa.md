``` 
@Override public boolean dispatchTouchEvent(MotionEvent ev) { 
   findViewById(R.id.container).dispatchTouchEvent(ev); 
   return false; 
} 
``` 



在上述代码中，看似事件分发分两步： <br>
1.将事件分发给container ViewGroup  <br>
2.表明Activity不处理事件 
### Activity不处理事件  <br>
追踪Activity dispatchTouchEvent调用，可以看到，其来源于DecorView的dispatchTouchEvent，代码如下所示：
``` 
@Override public boolean dispatchTouchEvent(MotionEvent ev) { 
final Window.Callback cb = mWindow.getCallback(); 
   return cb != null && 
         !mWindow.isDestroyed() && 
         mFeatureId < 0 ? cb.dispatchTouchEvent(ev) : super.dispatchTouchEvent(ev);
 } 
 ``` 
[PS:这里的cb就是Activity] 可以看出：  <br>
* 如果返回false，事件将会在DecorView的父布局处理，接着向其他子节点分发； 
* 如果返回true，则证明Activity消耗事件，事件也不会继续向下传递 <br>综上所述：无论这里Activity dispatchTouchEvent返回true还是false，事件均会终止，不会在想ContentView部分传递，进而这里不会触发onClick事件。 

### Container dispatchTouchEvent container对应的控件未自定义，古调用其dispatchTouchEvent，会调用其父类ViewGroup的dispatchTouchEvent方法，ViewGroup dispatchTouchEvent一共分为三大步骤：
1. 判断是否拦截事件，处理DISALLOW_INTERCEPT_TOUCH_EVENT flag和onInterceptTouchEvent 
2. ACTION_DOWN事件时构建mFirstTouchTarget链表，该链表是由消耗事件的子View以及递归得到的子View的父布局构成，其中消耗事件的子View判定，是通过将屏幕坐标点转化成View坐标系后，判断该点有没有落在View内来得到结果的 
3. 在经过2后，存在两种情况： 
* mFirstTouchTarget不为空：则说明找到了消耗事件的子View，后面来的move up事件均向该子View分发，此时遍历mFirstTouchTarget链表即可 
* mFirstTouchtarget为空：说明没有子View消耗事件，那么就会将当前ViewGroup当作View来处理，事件分发至View的onTouchEvent方法中 清楚了以上dispatchTouchEvent，乍一看，这里应该可以触发onClick事件，因为我们每一步都符合要求，其实不然，我们进一步回溯mFirstTouchTarget的形成过程可以发现，一个View或者ViewGroup能进入mFirstTouchTarget肯定会造成一个唯一后果---------其父布局必然在mFirstTouchTarget链表中，假设能够正常响应onClick事件，那也就意味着button控件在链表中，进而根container ViewGroup，乃至再往上的ContentFramelayout(android.R.id.content)，DecorView也在链表中，但实际情况是我们根本没有执行到DecorView的superDispatchTouchEvent方法，显然不可能构建mFirstTouchTarget链表，进而事件会被container调用view的onTouchEvent直接处理掉。 前面已经介绍了按mFirstTouchTarget链表进行推理，那么问题究竟发生在那一步呢？ 没错，发生在第二步，在将屏幕坐标点转化成View坐标点后，由于缺乏关联数据，判断出来点击事件没有落在container的任何子View上，导致container调用父类的onTouchEvent消耗事件。
package touch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhiping on 2022/11/10.
 */
public class ViewGroup extends View {

    // 第一个处理点击事件的view
    // 1. 非多点触控：mFirstTouchTarget链表退化成单个TouchTarget对象。
    // 2. 多点触控，目标相同：同样为单个TouchTarget对象，只是pointerIdBits保存了多个pointerId信息。
    // 3. 多点触控，目标不同：mFirstTouchTarget成为链表。
    private TouchTarget mFirstTouchTarget;
    // 子view列表
    private List<View> childList = new ArrayList<>();
    // 子view数组，数组的遍历比较快，所以需要把list转为数组
    private View[] mChildren = new View[0];

    private String name;

    public ViewGroup(int left, int top, int right, int bottom) {
        super(left, top, right, bottom);
    }

    public void addView(View view) {
        if (view == null) {
            return;
        }
        childList.add(view);
        mChildren = (View[]) childList.toArray(new View[childList.size()]);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * ViewGroup的事件分发入口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 子view是否处理
        boolean handled = false;
        // 判断是否拦截
        boolean intercepted = onInterceptTouchEvent();
        // 获取事件类型
        int actionMasked = event.getActionMasked();
        // 第一个事件是down事件，目的是找到处理事件的控件
        if (actionMasked == MotionEvent.ACTION_DOWN && !intercepted) {
            final View[] children = mChildren;
            // 倒叙遍历children，原因是后续添加的view会在顶层，所以先遍历后续添加的view
            for (int i = children.length - 1; i >= 0; i--) {
                View child = mChildren[i];
                // 判断是不是这个view来捕获down事件
                if (!child.isContainer(event.getX(), event.getY())) {
                    continue;
                }

                // 找到能够处理这个down事件的子类，分发给它
                if (dispatchTransformedTouchEvent(event, child)) {
                    addTouchTarget(child);
                    handled = true;
                    // 事件已经被消费，跳出循环
                    break;
                }
            }
        }
        // 子view不需要事件，自己处理
        if (mFirstTouchTarget == null) {
            handled = dispatchTransformedTouchEvent(event, null);
        } else {
            TouchTarget touchTarget = mFirstTouchTarget;
            System.out.println("------move------");
            // move 事件
            while (touchTarget != null) {
                TouchTarget next = touchTarget.next;
                if (dispatchTransformedTouchEvent(event, touchTarget.child)) {
                    handled = true;
                }
                touchTarget = next;
            }
        }
        return handled;
    }

    private TouchTarget addTouchTarget(View child) {
        final TouchTarget touchTarget = TouchTarget.obtain(child);
        touchTarget.next = mFirstTouchTarget;
        mFirstTouchTarget = touchTarget;
        return touchTarget;
    }

    /**
     * 分发处理子控件
     */
    private boolean dispatchTransformedTouchEvent(MotionEvent event, View child) {
        // 子类是否消费
        boolean handled;
        if (child != null) {
            handled = child.dispatchTouchEvent(event);
        } else {
            // 自己处理
            handled = super.dispatchTouchEvent(event);
        }
        return handled;
    }

    /**
     * 当前view是否拦截事件，子类可以实现
     */
    public boolean onInterceptTouchEvent() {
        return false;
    }

    @Override
    public String toString() {
        return "ViewGroup{" +
                "name='" + name + '\'' +
                '}';
    }

    private static final class TouchTarget {

        public View child; // 当前的view

        public TouchTarget next;

        private static TouchTarget sRecycleBin;

        private static int sRecycleCount;

        private static final Object sRecyclerLock = new Object[0];

        public static TouchTarget obtain(View view) {
            TouchTarget touchTarget;
            synchronized (sRecyclerLock) {
                if (sRecycleBin == null) {
                    touchTarget = new TouchTarget();
                } else {
                    touchTarget = sRecycleBin;
                }
                sRecycleBin = touchTarget.next;
                sRecycleCount--;
                touchTarget.next = null;
            }
            touchTarget.child = view;
            return touchTarget;
        }

        public void recycle() {
            if (child == null) {
                throw new IllegalStateException("view已经被回收了");
            }
            synchronized (sRecyclerLock) {
                if (sRecycleCount < 32) {
                    next = sRecycleBin;
                    sRecycleBin = this;
                    sRecycleCount += 1;
                }
            }
        }
    }
}

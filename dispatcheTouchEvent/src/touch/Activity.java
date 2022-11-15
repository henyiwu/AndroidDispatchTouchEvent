package touch;

/**
 * Created by wangzhiping on 2022/11/10.
 */
public class Activity {

    public static void main(String[] args) {
        MotionEvent event = new MotionEvent(100, 100);
        event.setActionMasked(MotionEvent.ACTION_DOWN);
        dispatchTouchEvent(event);

        event = new MotionEvent(101, 101);
        event.setActionMasked(MotionEvent.ACTION_MOVE);
        dispatchTouchEvent(event);

        event = new MotionEvent(102, 102);
        event.setActionMasked(MotionEvent.ACTION_MOVE);
        dispatchTouchEvent(event);
    }

    public static boolean dispatchTouchEvent(MotionEvent event) {
        System.out.println("----dispatchTouchEvent----");
        ViewGroup viewGroup = new ViewGroup(0, 0, 1000, 1920) {
            @Override
            public boolean onInterceptTouchEvent() {
                return false;
            }
        };
        viewGroup.setName("顶级容器");

        View child = new View(0, 200, 0, 200);
        child.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                System.out.println("view : onTouch");
                return true;
            }
        });
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("view : onClick");
            }
        });
        viewGroup.addView(child);

        viewGroup.dispatchTouchEvent(event);

        System.out.println("-----结束-----");

        return true;
    }
}

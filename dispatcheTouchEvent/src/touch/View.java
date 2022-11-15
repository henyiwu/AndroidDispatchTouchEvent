package touch;

/**
 * Created by wangzhiping on 2022/11/10.
 */
public class View {

    private OnTouchListener onTouchListener;
    private OnClickListener onClickListener;

    private int left;
    private int right;
    private int top;
    private int bottom;

    public View() {

    }

    public View(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    /**
     * 是否在点击范围内
     */
    public boolean isContainer(int x, int y) {
        return x > left && x < right && y > top && y < bottom;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = false;
        if (onTouchListener != null && onTouchListener.onTouch(this, event)) {
            result = true;
        }
        if (!result && onTouchEvent(event)) {
            return true;
        }
        return result;
    }

    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("-----onTouchEvent-----");
        if (onClickListener != null) {
            onClickListener.onClick(this);
            return true;
        }
        return false;
    }
}

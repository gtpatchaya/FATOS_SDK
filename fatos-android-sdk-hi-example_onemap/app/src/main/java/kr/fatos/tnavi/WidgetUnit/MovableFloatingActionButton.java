package kr.fatos.tnavi.WidgetUnit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import kr.fatos.tnavi.TNaviMainActivity;

public class MovableFloatingActionButton extends FloatingActionButton implements View.OnTouchListener {
    private final static float CLICK_DRAG_TOLERANCE = 10;

    private float downRawX, downRawY;
    private float dX, dY;

    public MovableFloatingActionButton(Context context) {
        super(context);
        init();
    }

    public MovableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovableFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float[] fXY = ((TNaviMainActivity)getContext()).LoadFloatingButtonXY();

        if(fXY != null)
        {
            this.setX(fXY[0]);
            this.setY(fXY[1]);
        }

        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        if (action == MotionEvent.ACTION_DOWN)
        {
            downRawX = motionEvent.getRawX();
            downRawY = motionEvent.getRawY();
            dX = view.getX() - downRawX;
            dY = view.getY() - downRawY;

            return true;

        }
        else if (action == MotionEvent.ACTION_MOVE)
        {
            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight();

            View viewParent = (View)view.getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            float newX = motionEvent.getRawX() + dX;
            newX = Math.max(0, newX);
            newX = Math.min(parentWidth - viewWidth, newX);

            float newY = motionEvent.getRawY() + dY;
            newY = Math.max(0, newY);
            newY = Math.min(parentHeight - viewHeight, newY);

            view.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start();

            return true;

        }
        else if (action == MotionEvent.ACTION_UP)
        {
            float upRawX = motionEvent.getRawX();
            float upRawY = motionEvent.getRawY();

            float upDX = upRawX - downRawX;
            float upDY = upRawY - downRawY;

            ((TNaviMainActivity)getContext()).SaveFloatingButtonXY(this.getTranslationX(), this.getTranslationY());

            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE)
            {
                return performClick();
            }
            else
            {
                return true;
            }
        }
        else
        {
            return super.onTouchEvent(motionEvent);
        }
    }
}
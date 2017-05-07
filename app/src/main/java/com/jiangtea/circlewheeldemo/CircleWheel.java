package com.jiangtea.circlewheeldemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

public class CircleWheel extends RelativeLayout {

    private GestureDetector mDetector;//手势识别器
    private ValueAnimator mVa;//惯性动画

    public CircleWheel(Context context) {
        super(context);
        init();
    }

    public CircleWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleWheel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //创建手势识别器
        mDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {

            //当手指按下的时候调用
            @Override
            public boolean onDown(MotionEvent e) {
                //如果正在执行fling则立即停止
                if (mVa != null && mVa.isRunning()) {
                    mVa.cancel();
                }
                return false;
            }

            //当手指按下挤压的短暂时间就调用这个方法
            @Override
            public void onShowPress(MotionEvent e) {

            }

            //手指单击的时候调用该方法
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                int index = getClickItem(e);
                if (index > -1) {
                    View childAt = CircleWheel.this.getChildAt(index);
                    childAt.performClick();//直接运行对应view的点击事件
                }
                return false;
            }

            //当手指滑动的时候调用
            /**
             *
             * @param e1 手指按下的时间
             * @param e2  是最后一次的滑动事件
             * @param distanceX = 上一次的移动事件 - 最后一次的移动事件的x轴的间距
             * @param distanceY = 上一次的移动事件 - 最后一次的移动事件的y轴的间距
             * @return
             */
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float
                    distanceY) {
                double degreeEnd = getDegree(e2.getY(), e2.getX());//最后一次滑动事件的角度
                double degreeStart = getDegree(e2.getY() + distanceY, e2.getX() + distanceX);//最后一次的上一次移动事件的角度
                double diffdegree = degreeEnd - degreeStart;
                setDegree(degree+diffdegree);
                return false;
            }

            //长按的时候调用
            @Override
            public void onLongPress(MotionEvent e) {

            }

            /**
             *
             * @param e1 手指按下的时间
             * @param e2  是最后一次的滑动事件
             * @param velocityX 当手指离开屏幕，x轴的移动速度，像素/秒
             * @param velocityY 当手指离开屏幕，y轴的移动速度，像素/秒
             * @return
             */
            //手指滑动后一个惯性的状态时调用
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //先获取最后一个移动事件的角度
                double degreestart = getDegree(e2.getY(), e2.getX());//最后一次滑动事件的角度
                //计算出1ms之后的角度
                double degreeend = getDegree(e2.getY() + velocityY / 1000, e2.getX() +
                        velocityX / 1000);

                double degreeAfter1ms = degreeend - degreestart;//1ms之后的角度变化
                double degreeAfter1s = degreeAfter1ms*1000;//1s之后的角速度
                stattAnimation(degreeAfter1s);
                return false;
            }
        });
    }


    //返回点击到子控件的角标，没有点击则返回-1
    private int getClickItem(MotionEvent e) {
        for (int i = 0; i < this.getChildCount(); i++) {
            View childAt = this.getChildAt(i);
            if (e.getX() > childAt.getLeft() && e.getX() < childAt.getRight() && e.getY() >
                    childAt.getTop() && e.getY() < childAt.getBottom()) {
                return  i;
            }
        }
        return  -1;
    }

    //惯性动画
    private void stattAnimation(double degreeAfter1s) {
        long duration = (long) Math.abs(degreeAfter1s * 1000);
        if (duration > 500) {
            duration = 500;
        }
        double diffDegree = degreeAfter1s*duration/1000;//fling时间内的角度变化
        mVa = ValueAnimator.ofFloat((float) degree, (float)( degree+diffDegree));
        mVa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                System.out.println((Float) (animation.getAnimatedValue()));
                setDegree((Float) (animation.getAnimatedValue()));//在fling的时候将对应的角度传递给控件，让它重新排版
            }
        });
        mVa.setDuration(duration);
        mVa.setInterpolator(new DecelerateInterpolator(2));
        mVa.start();
    }

    private double getDegree(float y, float x) {
        return Math.atan2(y - center.y, x - center.x);
    }

    private PointF center = new PointF();//圆心
    float radius ;//半径
    double cellDegree;//用来记录子控件之间的夹角
    double degree;//定义一个角度的变化；默认为0,记录当前的控件的角度
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //用来计算圆心与 半径
        compareValue();
        //排版每个子控件
        for (int i = 0; i < this.getChildCount(); i++) {
            View childView = this.getChildAt(i);
            childView.layout(
                    (int)(center.x+Math.sin(i*cellDegree+degree)*radius - childView.getWidth()/2),
                    (int)( center.y-Math.cos(i*cellDegree+degree)*radius - childView.getHeight()/2),
                    (int)( center.x+Math.sin(i*cellDegree+degree)*radius + childView.getWidth()/2),
                    (int)( center.y-Math.cos(i*cellDegree+degree)*radius + childView.getHeight()/2));
        }
    }

    //计算圆心与半径
    private void compareValue() {
        center.x = getWidth() / 2;
        center.y = getHeight() / 2;
        int maxWidth = 0;//最大宽度
        int maxHeight = 0;//最大高度
        for (int i = 0; i < this.getChildCount(); i++) {
            View childAt = this.getChildAt(i);
            if (maxWidth < childAt.getWidth()) {
                maxWidth = childAt.getWidth();
            }

            if (maxHeight < childAt.getHeight()) {
                maxHeight = childAt.getHeight();
            }
        }

        float r1 = center.x - maxWidth / 2;
        float r2 = center.y - maxHeight / 2;
        radius = Math.min(r1, r2);//计算出了圆的半径
        //计算子控件之间的夹角,pi=180度
        cellDegree = Math.PI * 2 / this.getChildCount();
    }

    //当角度变化时，让界面进行重新排版
    public void setDegree(double degree) {
        this.degree = degree;
        requestLayout();//强制layout方法被调用
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);//将触摸事件交由手势识别器来进行操作
        return true;
    }

    //事件拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}

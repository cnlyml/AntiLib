package dog.abcd.lib.views;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * <b>流布局控件</b><br>
 * 流布局自定义控件
 *
 * @author ramon<br>
 *         <b> create at </b>2016年2月15日 下午12:02:16
 */

public class AstiFlowLayout extends ViewGroup {

    // 存储所有子View
    private List<List<View>> mAllChildViews = new ArrayList<List<View>>();
    // 每一行的高度
    private List<Integer> mLineHeight = new ArrayList<Integer>();

    /**
     * 构造方法
     *
     * @param context
     */
    public AstiFlowLayout(Context context) {
        this( context, null );
    }

    public AstiFlowLayout(Context context, AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public AstiFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super( context, attrs, defStyle );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        // 父控件传进来的宽度和高度以及对应的测量模式
        int sizeWidth = MeasureSpec.getSize( widthMeasureSpec );
        int modeWidth = MeasureSpec.getMode( widthMeasureSpec );
        int sizeHeight = MeasureSpec.getSize( heightMeasureSpec );
        int modeHeight = MeasureSpec.getMode( heightMeasureSpec );

        // 如果当前ViewGroup的宽高为wrap_content的情况
        int width = 0;// 自己测量的 宽度
        int height = 0;// 自己测量的高度
        // 记录每一行的宽度和高度
        int lineWidth = 0;
        int lineHeight = 0;

        // 获取子view的个数
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt( i );
            // 测量子View的宽和高
            measureChild( child, widthMeasureSpec, heightMeasureSpec );
            // 子View占据的宽度
            int childWidth = child.getMeasuredWidth();
            // 子View占据的高度
            int childHeight = child.getMeasuredHeight();
            // 换行时候
            if (lineWidth + childWidth > sizeWidth) {
                // 对比得到最大的宽度
                width = Math.max( width, lineWidth );
                // 重置lineWidth
                lineWidth = childWidth;
                // 记录行高
                height += lineHeight;
                lineHeight = childHeight;
            } else {// 不换行情况
                // 叠加行宽
                lineWidth += childWidth;
                // 得到最大行高
                lineHeight = Math.max( lineHeight, childHeight );
            }
            // 处理最后一个子View的情况
            if (i == childCount - 1) {
                width = Math.max( width, lineWidth );
                height += lineHeight;
            }
        }
        // wrap_content
        setMeasuredDimension( modeWidth == MeasureSpec.EXACTLY ? sizeWidth
                : width, modeHeight == MeasureSpec.EXACTLY ? sizeHeight
                : height );
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        mAllChildViews.clear();
        mLineHeight.clear();
        // 获取当前ViewGroup的宽度
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;
        // 记录当前行的view
        List<View> lineViews = new ArrayList<View>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt( i );
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 如果需要换行
            if (childWidth + lineWidth > width) {
                // 记录LineHeight
                mLineHeight.add( lineHeight );
                // 记录当前行的Views
                mAllChildViews.add( lineViews );
                // 重置行的宽高
                lineWidth = 0;
                lineHeight = childHeight;
                // 重置view的集合
                lineViews = new ArrayList<View>();
            }
            lineWidth += childWidth;
            lineHeight = Math.max( lineHeight, childHeight );
            lineViews.add( child );
        }
        // 处理最后一行
        mLineHeight.add( lineHeight );
        mAllChildViews.add( lineViews );

        // 设置子View的位置
        int left = 0;
        int top = 0;
        // 获取行数
        int lineCount = mAllChildViews.size();
        for (int i = 0; i < lineCount; i++) {
            // 当前行的views和高度
            lineViews = mAllChildViews.get( i );
            lineHeight = mLineHeight.get( i );
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get( j );
                // 判断是否显示
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                int cLeft = left;
                int cTop = top;
                int cRight = cLeft + child.getMeasuredWidth();
                int cBottom = cTop + child.getMeasuredHeight();
                // 进行子View进行布局
                child.layout( cLeft, cTop, cRight, cBottom );
                left += child.getMeasuredWidth();
            }
            left = 0;
            top += lineHeight;
        }

    }

}

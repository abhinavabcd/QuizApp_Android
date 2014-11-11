package com.amcolabs.quizapp.widgets;
 
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;
import android.content.Context;
 
public class ExpandableHeightListView extends ListView
{
 
boolean expanded = false;
 
public ExpandableHeightListView(Context context)
    {
        super(context);
    }
 
    public ExpandableHeightListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
 
    public ExpandableHeightListView(Context context, AttributeSet attrs,
            int defStyle)
    {
        super(context, attrs, defStyle);
    }
 
    public boolean isExpanded()
    {
        return expanded;
    }
 
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // HACK! TAKE THAT ANDROID!
        if (isExpanded())
        {
            // Calculate entire height by providing a very large height hint.
            // But do not use the highest 2 bits of this integer; those are
            // reserved for the MeasureSpec mode.
            int expandSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
 
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }
        else
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
 
    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }
}
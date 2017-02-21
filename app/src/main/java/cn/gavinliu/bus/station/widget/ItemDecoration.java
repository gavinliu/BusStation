package cn.gavinliu.bus.station.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.gavinliu.bus.station.R;


/**
 * Created by gavin on 2017/2/20.
 */

public class ItemDecoration extends RecyclerView.ItemDecoration {

    private int mDividerSize;
    private Drawable mDividerDrawable;

    public ItemDecoration(Context context) {
        mDividerSize = context.getResources().getDimensionPixelOffset(R.dimen.divider_size);
        mDividerDrawable = new ColorDrawable(context.getResources().getColor(R.color.divider));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(outRect.left, outRect.top, outRect.right, outRect.bottom + mDividerSize);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mDividerDrawable == null) {
            super.onDrawOver(c, parent, state);
            return;
        }

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerSize;
            mDividerDrawable.setBounds(left, top, right, bottom);
            mDividerDrawable.draw(c);
        }
    }

}
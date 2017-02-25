package cn.gavinliu.bus.station.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import cn.gavinliu.bus.station.R;

/**
 * Created by gavin on 2017/2/25.
 */

public class AlertLayout extends FrameLayout {

    private ItemListener mItemListener;

    public AlertLayout(Context context) {
        super(context);
        init();
    }

    public AlertLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlertLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemListener != null) mItemListener.onCloseClick();
            }
        });
    }


    public void setItemListener(ItemListener itemListener) {
        mItemListener = itemListener;
    }

    public interface ItemListener {
        void onCloseClick();
    }
}

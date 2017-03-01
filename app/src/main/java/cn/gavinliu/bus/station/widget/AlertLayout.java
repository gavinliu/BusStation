package cn.gavinliu.bus.station.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.gavinliu.bus.station.R;

/**
 * Created by gavin on 2017/2/25.
 */

public class AlertLayout extends FrameLayout {

    private ItemListener mItemListener;

    private TextView mTitle;

    private TextView mContent;

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

        mTitle = (TextView) findViewById(R.id.title);
        mContent = (TextView) findViewById(R.id.content);
    }

    public void setTitle(String title) {
        if (mTitle != null) mTitle.setText(title);
    }

    public void setContent(String content) {
        if (mContent != null) mContent.setText(content);
    }

    public void setItemListener(ItemListener itemListener) {
        mItemListener = itemListener;
    }

    public interface ItemListener {
        void onCloseClick();
    }
}

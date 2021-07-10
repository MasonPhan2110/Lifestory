package com.minh.app.Adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class test extends LinearLayout {
    private Adapter adapter;
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            reloadChildView();
        }
    };

    private void reloadChildView() {
        removeAllViews();
        if(adapter == null) return;
        int count = adapter.getCount();
        for(int position =0;position<count;position++){
            View v = adapter.getView(position,null,this);
            if(v!=null) addView(v);
        }
        requestLayout();
    }
    public void setAdapter(Adapter adapter){
        if(this.adapter == adapter) return;
        this.adapter = adapter;
        if(adapter!=null) adapter.registerDataSetObserver(dataSetObserver);
        reloadChildView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (adapter != null) adapter.unregisterDataSetObserver(dataSetObserver);
    }

    public test(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public test(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public test(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public test(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}

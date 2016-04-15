package com.beevle.renxing.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beevle.renxing.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.io.Serializable;
import java.util.List;

public class ProductListAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<Products> list;
    // 点击索引：点击列表项；点击按钮；点击名字。
    public final static int CLICK_INDEX_ITEM = 0;
    public final static int CLICK_INDEX_PAY = 1;
    public final static int CLICK_INDEX_NAME = 2;
    // 记录Activity中接受消息的Handler
    private Handler mHandle;
    // 关键字
    public final static String BUNDLE_KEY_LIDATA = "lidata";

    public ProductListAdapter(Context mContext, List<Products> mlist, Handler handler) {
        this.mContext = mContext;
        this.list = mlist;
        this.mHandle = handler;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        v.findViewById(R.id.delete).setOnClickListener(
                new OnItemChildClickListener(CLICK_INDEX_PAY, position));
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        Products m = list.get(position);
        TextView t = (TextView) convertView.findViewById(R.id.protitle);
        t.setText(m.getTitle());
        TextView d = (TextView) convertView.findViewById(R.id.days);
        d.setText("有效期：" + m.getDays());
        TextView p = (TextView) convertView.findViewById(R.id.price);
        p.setText("原价：¥" + m.getOldprice() + " 现价：¥" + m.getCurprice());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ProList implements Serializable {
        public int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public List<Products> getData() {
            return data;
        }

        public void setData(List<Products> data) {
            this.data = data;
        }

        private int errcode;
        private String errmsg;
        private List<Products> data;
    }

    public class Products implements Serializable {
        private int pid;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public float getOldprice() {
            return oldprice;
        }

        public void setOldprice(float oldprice) {
            this.oldprice = oldprice;
        }

        public float getCurprice() {
            return curprice;
        }

        public void setCurprice(float curprice) {
            this.curprice = curprice;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        private String title;
        private float oldprice;
        private float curprice;
        private int days;
    }

    private class OnItemChildClickListener implements View.OnClickListener {
        // 点击类型索引，对应前面的CLICK_INDEX_xxx
        private int clickIndex;
        // 点击列表位置
        private int position;

        public OnItemChildClickListener(int clickIndex, int position) {
            this.clickIndex = clickIndex;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            // 创建Message并填充数据，通过mHandle联系Activity接收处理
            Message msg = new Message();
            msg.what = clickIndex;
            msg.arg1 = position;
            Bundle b = new Bundle();
            b.putSerializable(BUNDLE_KEY_LIDATA, list.get(position));
            msg.setData(b);
            mHandle.sendMessage(msg);
        }
    }
}

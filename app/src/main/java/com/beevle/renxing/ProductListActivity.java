package com.beevle.renxing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.beevle.renxing.adapter.ProductListAdapter;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.google.gson.Gson;
import com.tencent.mm.sdk.modelpay.PayReq;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import network.scau.com.waitingview.WaitingView;

public class ProductListActivity extends BaseActivity {

    private ListView mListView;
    private ProductListAdapter mAdapter;
    private Context mContext = this;
    private List<ProductListAdapter.Products> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        mListView = (ListView) findViewById(R.id.listview);
        /**
         * The following comment is the sample usage of ArraySwipeAdapter.
         */
//        String[] adapterData = new String[]{"Activity", "Service", "Content Provider", "Intent", "BroadcastReceiver", "ADT", "Sqlite3", "HttpClient",
//                "DDMS", "Android Studio", "Fragment", "Loader", "Activity", "Service", "Content Provider", "Intent",
//                "BroadcastReceiver", "ADT", "Sqlite3", "HttpClient", "Activity", "Service", "Content Provider", "Intent",
//                "BroadcastReceiver", "ADT", "Sqlite3", "HttpClient"};
//        mListView.setAdapter(new ArraySwipeAdapterSample<String>(this, R.layout.listview_item, R.id.position, adapterData));

        mAdapter = new ProductListAdapter(this, list, mEventHandle);
        mListView.setAdapter(mAdapter);
        mAdapter.setMode(Attributes.Mode.Single);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout) (mListView.getChildAt(position - mListView.getFirstVisiblePosition()))).open(true);
            }
        });
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("ListView", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("ListView", "onItemSelected:" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("ListView", "onNothingSelected:");
            }
        });

        UpdateData();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        WaitingView.showWaitingView(this,mListView);
    }
    private void UpdateData() {
        RequestParams requestParams = new RequestParams("http://renxingapi2.wo-ish.com/product/list");
        requestParams.addBodyParameter("access_token", "FksNb+qpV0BDaAilZeCaV5p3");

        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                //Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
                LogUtil.i(result);
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", result);
                msg.setData(data);
                msg.what = 1;
                handler.sendMessage(msg);
            }
        });
    }

    private Handler mEventHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ProductListAdapter.Products data = (ProductListAdapter.Products)
                    msg.getData().getSerializable(ProductListAdapter.BUNDLE_KEY_LIDATA);
            switch (msg.what) {
                case ProductListAdapter.CLICK_INDEX_PAY:
                    //WaitingView.showWaitingView(x.app(), mListView);
                    //onItemClicked(msg.arg1, data);
                    LogUtil.i(String.valueOf(data.getPid()));
                    Intent intent = new Intent(getApplicationContext(), PayActivity.class);
                    intent.putExtra(PayActivity.EXTRA_PARAM_ORDER, data.getPid());
                    startActivity(intent);
                    //finish();
                    break;
                case ProductListAdapter.CLICK_INDEX_ITEM:
                    //onCountryClicked(data);
                    break;
                case ProductListAdapter.CLICK_INDEX_NAME:
                    //onNameClicked(data);
                    break;
            }
        }

    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (msg.what) {
                case 1:
                    try {
                        if (WaitingView.isExist()) {
                            WaitingView.close();
                        }
                        Gson g = new Gson();
                        ProductListAdapter.ProList plist = g.fromJson(data.getString("value"),
                                ProductListAdapter.ProList.class);
                        if (plist.getErrcode() == 0) {
                            list = plist.getData();
                            mAdapter = new ProductListAdapter(x.app(), list, mEventHandle);
                            mListView.setAdapter(mAdapter);
                        } else {
                            Toast.makeText(x.app(), "暂无数据", Toast.LENGTH_SHORT);
                        }
                    } catch (Exception ex) {
                        LogUtil.e(ex.getMessage());
                    }
                    break;
            }
        }
    };
}

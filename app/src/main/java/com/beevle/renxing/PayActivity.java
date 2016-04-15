package com.beevle.renxing;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import network.scau.com.waitingview.WaitingView;

@ContentView(value = R.layout.pay)
public class PayActivity extends BaseActivity {
    private IWXAPI api;
    @ViewInject(value = R.id.appay_btn)
    private Button appayBtn;
    @ViewInject(value = R.id.check_pay_btn)
    private Button checkappBtn;
    public final static String EXTRA_PARAM_ORDER = "OrderId";
    private String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        pid = String.valueOf(getIntent().getIntExtra(EXTRA_PARAM_ORDER, 1));
        LogUtil.d(String.valueOf(pid));
        appayBtn.performClick();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        WaitingView.showWaitingView(x.app(), appayBtn);
    }

    @Event(value = R.id.appay_btn, type = View.OnClickListener.class)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.appay_btn:
//                if(!WaitingView.isExist()) {
//                    WaitingView.showWaitingView(x.app(), v);
//                }
                //Toast.makeText(x.app(), "等待支付...", Toast.LENGTH_SHORT).show();
                new Thread(networkTask).start();
                break;
            case R.id.check_pay_btn:
                if (CheckWxPay()) {
                    Toast.makeText(x.app(), "微信支付可用", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(x.app(), "微信支付不可用", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void GetOrderId() {
        RequestParams requestParams = new RequestParams("http://renxingapi2.wo-ish.com/order/add");
        requestParams.addBodyParameter("access_token", "FksNb+qpV0BDaAilZeCaV5p3");
        requestParams.addBodyParameter("pid", pid);
        requestParams.addBodyParameter("num", "1");
        x.http().post(
                requestParams,
                new Callback.CommonCallback<String>() {
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

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(Callback.CancelledException cex) {
                        Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (msg.what) {
                case 1:
                    try {
                        JSONObject jsonObject = new JSONObject(data.getString("value"));
                        if (null != jsonObject && jsonObject.getInt("errcode") == 0) {
                            String orderid = jsonObject.getString("data");
                            RequestParams requestParams = new RequestParams("http://renxingapi2.wo-ish.com/order/payparam");
                            requestParams.addBodyParameter("access_token", "FksNb+qpV0BDaAilZeCaV5p3");
                            requestParams.addBodyParameter("orderid", orderid);
                            requestParams.addBodyParameter("payment", "2");
                            x.http().post(
                                    requestParams,
                                    new org.xutils.common.Callback.CommonCallback<String>() {
                                        @Override
                                        public void onSuccess(String result) {
                                            //Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
                                            LogUtil.i(result);
                                            Message msg = new Message();
                                            Bundle data = new Bundle();
                                            data.putString("value", result);
                                            msg.setData(data);
                                            msg.what = 2;
                                            handler.sendMessage(msg);
                                        }

                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {
                                            Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onCancelled(org.xutils.common.Callback.CancelledException cex) {
                                            Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onFinished() {

                                        }
                                    });
                        } else {
                            Toast.makeText(x.app(), "未获得数据", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        LogUtil.e(ex.getMessage());
                    }
                    break;
                case 2:
                    try {
                        if (WaitingView.isExist()) {
                            WaitingView.close();
                        }
                        JSONObject json = new JSONObject(data.getString("value"));
                        if (null != json && json.getInt("errcode") == 0) {
                            JSONObject d = (JSONObject) json.get("data");
                            PayReq req = new PayReq();
                            req.appId = d.getString("appid");
                            req.partnerId = d.getString("partnerid");
                            req.prepayId = d.getString("prepayid");
                            req.nonceStr = d.getString("noncestr");
                            req.timeStamp = d.getString("timestamp");
                            req.packageValue = d.getString("package");
                            req.sign = d.getString("sign");
                            //req.extData			= "app data"; // optional

                            if (CheckWxPay() && api.registerApp(Constants.APP_ID)) {
                                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                api.sendReq(req);
                                Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PayActivity.this, "微信支付不可用", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(x.app(), "未获取到数据", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        LogUtil.e(ex.getMessage());
                    }
                    break;
            }
            // TODO
            // UI界面的更新等相关操作
        }
    };
    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // 在这里进行 http request.网络请求相关操作
            GetOrderId();
        }
    };

    private boolean CheckWxPay() {
//        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
//        api.isWXAppSupportAPI();
        return api.isWXAppInstalled() && api.isWXAppSupportAPI();
    }
}

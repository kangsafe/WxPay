package com.beevle.renxing.wxapi;

import com.beevle.renxing.Constants;
import com.beevle.renxing.BaseActivity;
import com.beevle.renxing.R;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;

@ContentView(value = R.layout.pay_result)
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtil.d("onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_tip);
            switch (resp.errCode) {
                case 0:
                    builder.setMessage(getString(R.string.pay_result_callback_msg, "支付成功"));
                    break;
                case -1:
                    builder.setMessage(getString(R.string.pay_result_callback_msg, "可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等"));
                    break;
                case -2:
                    builder.setMessage(getString(R.string.pay_result_callback_msg, "无需处理。发生场景：用户不支付了，点击取消，返回APP"));
                    break;
            }

            builder.show();
        }
    }
}
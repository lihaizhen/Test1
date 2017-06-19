package com.htkg.policedirect.android.activity.imperson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.htkg.policedirect.android.R;
import com.htkg.policedirect.android.activity.BaseActivity;
import com.htkg.policedirect.android.common.Constants;
import com.htkg.policedirect.android.myadapter.InterimControlCallBackScanAdapter;
import com.htkg.policedirect.android.mybean.InterimControlFeedbackDetail;
import com.htkg.policedirect.android.mybean.SpDataDictionary;
import com.htkg.policedirect.android.mybean.ZdryControlFeedbackerEntity;
import com.htkg.policedirect.android.myutils.SharedUtils;
import com.htkg.policedirect.android.utils.ToastUtils;
import com.htkg.policedirect.android.utils.photo.UniversalImageLoadTool;
import com.lz.lzpolicetraffic.imp.RequestDataInterface;
import com.lz.lzpolicetraffic.webservice.WebParmars;
import com.lz.lzpolicetraffic.webservice.WebserviceAsyncTack;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
//临控反馈详情页面
public class InterimControlInfoCallbackDetail extends BaseActivity implements
        RequestDataInterface {

    private ImageView head_title_back;
    private ImageView pictureHead;
    private TextView head_title;
    private TextView name;
    private TextView sex;
    private TextView peopletype;
    private TextView controlRequest;
    private Button nextBtn;

    private ListScrollView scrollView;
    private ListView listView;
    private InterimControlFeedbackDetail mInterimDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_interimcontrolcallbackdetail);

        initView();
        initData();
    }

    private void initView() {// 初始化视图
        // TODO Auto-generated method stub
        // 为activity标题赋值
        head_title = (TextView) findViewById(R.id.head_title);
        head_title.setText("反馈详情");

        // 返回键
        head_title_back = (ImageView) findViewById(R.id.head_title_back);
        pictureHead = (ImageView) findViewById(R.id.pictureHead);
        head_title_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        scrollView = (ListScrollView) findViewById(R.id.listScrollView);
        listView = (ListView) findViewById(R.id.listView);
        name = (TextView) findViewById(R.id.name);
        sex = (TextView) findViewById(R.id.sex);
        peopletype = (TextView) findViewById(R.id.peopletype);
        controlRequest = (TextView) findViewById(R.id.controlRequest);
        controlRequest.setMovementMethod(new ScrollingMovementMethod());
        nextBtn = (Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mInterimDetail != null) {
                    Intent i = new Intent(
                            InterimControlInfoCallbackDetail.this,
                            InsterimCallbackActivity.class);
                    i.putExtra("name", mInterimDetail.getUsername());
                    startActivity(i);
                } else {
                    ToastUtils.show(InterimControlInfoCallbackDetail.this,
                            "未获取到人员信息，无法进行反馈");
                }
            }
        });
        scrollView.setListView(listView);
        String string = "ListItem";
    }

    private void initData() {
        executeInterimDetailRequest(
                InterimControlCallbackActivity.currentControllApplyId,
                InterimControlCallbackActivity.currentNotifyId);
    }

    private void executeInterimDetailRequest(String id, String notifyId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("notifyId", notifyId);
        WebserviceAsyncTack mAsyncTask = new WebserviceAsyncTack(this,
                Constants.ACTION_INTERIM_CONTROL_FEEDBACK_DETAIL, params);
        mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void updateSurface() {
        UniversalImageLoadTool.disPlay(WebParmars.URL_DOWNLOAD_FILE
                + mInterimDetail.getFrontPhotoId(), new ImageViewAware(
                pictureHead), R.drawable.ic_launcher);
        name.setText(mInterimDetail.getUsername());
        
        
       
    	/** 性别 */
		if (mInterimDetail.getSex() != null) {
			if (mInterimDetail.getSex().equals("0")) {
				sex.setText("男");
			} else {
				sex.setText("女");
			}
		}
        
        
        
        String peopleTypestr = new SpDataDictionary(SharedUtils.getInstance()
				.getStringSp(InterimControlInfoCallbackDetail.this, SharedUtils.PERS_TYPE, ""))
				.getValueByKey(mInterimDetail.getPersonType() == null ? ""
						: mInterimDetail.getPersonType());
        peopletype.setText(peopleTypestr);
        controlRequest.setText(mInterimDetail.getControllReqirement().trim());

        List<ZdryControlFeedbackerEntity> feedback = mInterimDetail
                .getFeedback();
        if (feedback != null) {
            InterimControlCallBackScanAdapter adapter = new InterimControlCallBackScanAdapter(
                    this, feedback); // android.R.layout.simple_list_item_1
            listView.setAdapter(adapter);
        }
    }

    // 重新绘制高度的方法，无用
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        listView.setLayoutParams(params);
    }

    @Override
    public void onRequestStart(int action) {
        // TODO Auto-generated method stub

    }

    @Override
    public String onRequestReturn(int action, String result) {
        Map resultMap = JSON.parseObject(result, Map.class);
        if (resultMap != null) {
            String code = resultMap.get("code").toString();
            if ("200".equals(code)) {
                Map infoMap = JSON.parseObject(
                        resultMap.get("data").toString(), Map.class);
                String baseInfo = infoMap.get("baseinfo").toString();
                if (baseInfo != null) {
                    mInterimDetail = JSON.parseObject(baseInfo,
                            InterimControlFeedbackDetail.class);
                    mInterimDetail.setFeedback(JSON.parseArray(
                            infoMap.get("feedback").toString(),
                            ZdryControlFeedbackerEntity.class));
                    return null;
                }
            } else if ("204".equals(code)) {
                return "未获取到相关数据";
            }
        }
 
        return "获取数据失败，请重试";
    }

    @Override
    public void onRequestFinish(int action, String errMessage) {
        if (errMessage != null) {
            ToastUtils.show(this, errMessage);
        } else {
            updateSurface();
        }
    }

}

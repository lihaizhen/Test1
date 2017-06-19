package com.htkg.policedirect.android.activity.imperson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.htkg.policedirect.android.R;
import com.htkg.policedirect.android.activity.BaseActivity;
import com.htkg.policedirect.android.activity.instruct.InstructListActivity;
import com.htkg.policedirect.android.activity.instruct.entity.InstructFeedbackInfo;
import com.htkg.policedirect.android.activity.peoplecheck.PeoplePhotoGridViewAcitvity;
import com.htkg.policedirect.android.common.AppSettings;
import com.htkg.policedirect.android.common.Constants;
import com.htkg.policedirect.android.mybean.ZdryControllTmpApplyEntity;
import com.htkg.policedirect.android.myutils.MyDialogutil;
import com.htkg.policedirect.android.myutils.UploadUtil;
import com.htkg.policedirect.android.utils.ActivityAni;
import com.htkg.policedirect.android.utils.ToastUtils;
import com.htkg.policedirect.android.utils.Utils;
import com.lz.lzpolicetraffic.imp.RequestDataInterface;
import com.lz.lzpolicetraffic.webservice.WebParmars;
import com.lz.lzpolicetraffic.webservice.WebserviceAsyncTack;

/**
 * 临控反馈页面 上传提交页面
 * 
 * @author lihaizhen
 * 
 */
public class InsterimCallbackActivity extends BaseActivity implements
		OnClickListener, RequestDataInterface {

	private RelativeLayout personNameRl;
	private TextView personNameTv,clue;
	private TextView actiontrack,idnumusestatus,others;
	private TextView feedbackActivityTv;
	private EditText feedbackDetailEt;
	private Button feedbackCommitBtn;
	private ImageView feedbackImageIv;

	private Dialog mLoadingDialog;
	private ZdryControllTmpApplyEntity zdryControllTmpApplyEntity;
	private int mChoseActivityType = -1;
	private String mActivityType;
	private String mFeedbackDetail;
	private String mChoseImagePath;
	private String mPersonName;

	private final String[] ACTIVITY_TYPE = new String[] { "长期在家", "长期不在家",
			"在外经商", "无固定住所，行踪不定" };
	public static final String KEY_FEEDBACK_SUCCESS = "feedback_success";
	private static final String SDCARD_ROOT_PATH = AppSettings.getDataHome();// 路径
	private static final String SAVE_PATH_IN_SDCARD = "/Evidence.data/";
	private static final String SAVE_PATH = SDCARD_ROOT_PATH
			+ SAVE_PATH_IN_SDCARD;
	public static final int RESULT_CODE_FEEDBACK = 1;
	public static final int REQUEST_CODE_CHOOSE_IMAGE = 1;

	private Intent intent;
	private PopupWindow popupwindow;
	private LinearLayout popviewLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_insterim_callback);

		intent = this.getIntent();
		zdryControllTmpApplyEntity = (ZdryControllTmpApplyEntity) intent
				.getSerializableExtra("ZdryControllTmpApplyEntity");

		 
		initView();
		initData();
	}
	   private void popup(String message) {
	        new AlertDialog.Builder(this)
	                .setTitle("提示")
	                .setMessage(message)
	                .setPositiveButton("确定", null)
	                .show();
	    }
	protected void initView() {
		
		// 返回键
		ImageView p_title_back = (ImageView) findViewById(R.id.p_title_back);
		p_title_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		popviewLayout=(LinearLayout) findViewById(R.id.popviewLayout);
		popviewLayout.setVisibility(8);
		
		actiontrack= (TextView) findViewById(R.id.actiontrack);
		idnumusestatus= (TextView) findViewById(R.id.idnumusestatus);
		others= (TextView) findViewById(R.id.others);
		
		actiontrack.setText(zdryControllTmpApplyEntity.getActiontrack());
		idnumusestatus.setText(zdryControllTmpApplyEntity.getIdnumusestatus());
		others.setText(zdryControllTmpApplyEntity.getOthers());
		
		actiontrack.setOnClickListener(this);
		idnumusestatus.setOnClickListener(this);
		others.setOnClickListener(this);
		
		
		
		mPersonName = getIntent().getStringExtra("name");
		personNameRl = (RelativeLayout) findViewById(R.id.personNameRl);
		personNameTv = (TextView) findViewById(R.id.personNameTv);
		feedbackActivityTv = (TextView) findViewById(R.id.feedbackActivityTv);
		feedbackDetailEt = (EditText) findViewById(R.id.feedbackDetailEt);
		feedbackCommitBtn = (Button) findViewById(R.id.feedbackCommitBtn);
		feedbackImageIv = (ImageView) findViewById(R.id.feedbackImageIv);
		clue= (TextView) findViewById(R.id.clue);
		clue.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//弹出菜单
				/*if (popupwindow != null&&popupwindow.isShowing()) {
					popupwindow.dismiss();
					return;
				} else {
					initmPopupWindowView();
					popupwindow.showAsDropDown(v, 0, 5);
				}*/
				if (popviewLayout.isShown()) {
					popviewLayout.setVisibility(8);
				} else {
					popviewLayout.setVisibility(0);
				}
				
				
				
				
				
			}
		});
		TextView p_title = (TextView) findViewById(R.id.p_title);
		p_title.setText("反馈信息");

		personNameRl.setOnClickListener(this);
		feedbackActivityTv.setOnClickListener(this);

		feedbackCommitBtn.setOnClickListener(this);
		feedbackImageIv.setOnClickListener(this);
		mLoadingDialog = MyDialogutil.getLoadingDialog(this, "正在上传，请稍候", null);
		personNameTv.setText(mPersonName);
	}

	protected void initData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personNameRl: // 重点人
			intent = new Intent(InsterimCallbackActivity.this,
					ControledInfoDetail.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("ZdryControllTmpApplyEntity",
					zdryControllTmpApplyEntity);
			intent.putExtras(bundle);

			startActivity(intent);
			break;
		case R.id.feedbackActivityTv: // 选择人员活动情况
			displayChooseDialog(feedbackActivityTv, ACTIVITY_TYPE);
			break;

		case R.id.feedbackCommitBtn: // 提交指令反馈数据
			if (validateInputContent()) {
				uploadImage();
			}
			break;
		case R.id.feedbackImageIv:
			chooseImage();
			break;
		case R.id.actiontrack:
			popup(zdryControllTmpApplyEntity.getActiontrack());
			break;
			
		case R.id.idnumusestatus:
			popup(zdryControllTmpApplyEntity.getIdnumusestatus());
			break;
			
			
		case R.id.others:
			popup(zdryControllTmpApplyEntity.getOthers());
			break;
		 
			
		default:
			break;
		}
	}
	
	

	public void initmPopupWindowView() {

	 
		View customView = getLayoutInflater().inflate(R.layout.popview_item,
				null, false);
	 
		popupwindow = new PopupWindow(customView,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT,true);
		 
		popupwindow.setAnimationStyle(R.style.AnimationFade);
	 
		popupwindow.showAtLocation(customView, Gravity.CLIP_HORIZONTAL, 0, -300);
		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupwindow != null && popupwindow.isShowing()) {
				
					popupwindow.dismiss();
					popupwindow = null;
				}

				return false;
			}
		});

		 
		/*Button btton2 = (Button) customView.findViewById(R.id.button2);
		Button btton3 = (Button) customView.findViewById(R.id.button3);
		Button btton4 = (Button) customView.findViewById(R.id.button4);
		TextView text=(TextView) findViewById(R.id.text);
		btton2.setOnClickListener(this);
		btton3.setOnClickListener(this);
		btton4.setOnClickListener(this);

		btton2.setText("这是第一个butt111155551111111111111111111111");*/
		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_CHOOSE_IMAGE:
				feedbackImageIv.setImageBitmap(Utils
						.getBitmapFromFile(mChoseImagePath));

				break;

			default:
				break;
			}

		}

	}

	private boolean validateInputContent() {
		mActivityType = feedbackActivityTv.getText().toString().trim();
		mFeedbackDetail = feedbackDetailEt.getText().toString().trim();

		if (TextUtils.isEmpty(mActivityType)) {
			ToastUtils.show(this, "请选择活动详情");
			return false;
		}
		if (TextUtils.isEmpty(mFeedbackDetail)) {
			ToastUtils.show(this, "请输入反馈内容");
			return false;
		}
		if (mChoseImagePath == null) {
			ToastUtils.show(InsterimCallbackActivity.this, "请先拍摄照片");
			return false;
		}
		return true;
	}

	private void displayChooseDialog(final TextView view,
			final String[] printArray) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, printArray);
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择活动详情")
				.setAdapter(adapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mChoseActivityType = which;
						view.setText(printArray[which]);
						dialog.dismiss();
					}
				}).create();
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	private void chooseImage() {
		SDCardChecked();
		getCurrentSavePath();
		Intent intent = new Intent();
		intent.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		File saveFile = new File(mChoseImagePath);
		if (saveFile.exists() == false) {
			File vDirPath = saveFile.getParentFile();
			vDirPath.mkdirs();
		}
		Uri uri = Uri.fromFile(saveFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		ActivityAni.startActivityForResult(this, intent,
				REQUEST_CODE_CHOOSE_IMAGE);
		PeoplePhotoGridViewAcitvity.isPass = true;
	}

	/**
	 * 返回当前可用的图片文件路径
	 * 
	 * @return
	 */
	private String getCurrentSavePath() {
		mChoseImagePath = SAVE_PATH + System.currentTimeMillis()
				+ Constants.SUFFIX_PHOTO;
		return mChoseImagePath;
	}

	private void uploadImage() {
		mLoadingDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(mChoseImagePath);
				final String uploadedImageId = UploadUtil.getInstance().upFile(
						file, WebParmars.URL_UPLOAD_FILE, file.getName());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (!TextUtils.isEmpty(uploadedImageId)) {
							executeUploadFeedbackRequest(
									uploadedImageId,
									InterimControlCallbackActivity.currentNotifyId);
						} else {
							mLoadingDialog.dismiss();
							ToastUtils.show(InsterimCallbackActivity.this,
									"上传图片失败，请重试");
						}
					}
				});
			}
		}).start();

	}

	private void executeUploadFeedbackRequest(String imageId, String notifyId) {
		InstructFeedbackInfo info = new InstructFeedbackInfo();
		info.setNotificationId(notifyId);
		info.setActivityInfo(ACTIVITY_TYPE[mChoseActivityType]);
		info.setFeedbackUserId(AppSettings.USERID);
		info.setControllApplyId(InterimControlCallbackActivity.currentControllApplyId);
		info.setFeedbackUserName(AppSettings.USERNAME);
		info.setFeedbackContent(mFeedbackDetail);
		info.setFeedbackImageFir(imageId);
		Map<String, Object> params = new HashMap<>();
		params.put("jsonStr", JSON.toJSONString(info));
		WebserviceAsyncTack asyncTask = new WebserviceAsyncTack(this,
				Constants.ACTION_INTERIM_CONTROL_FEEDBACK_COMMIT, params);
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void SDCardChecked() {
		/* 增加SD卡检查 */
		if (!Utils.isHasSdcard()) {
			Utils.ShowWarnigDialog(this,
					getResources().getString(R.string.dialog_no_sd));
			return;
		}
	}

	@Override
	public void onRequestStart(int action) {

	}

	@Override
	public String onRequestReturn(int action, String result) {
		Map resultMap = JSON.parseObject(result, Map.class);
		if (resultMap != null) {
			String code = resultMap.get("code").toString();
			if (code.equals("200")) {
				return null;
			}
		}
		return "上传反馈内容失败，请重试";
	}

	@Override
	public void onRequestFinish(int action, String errMessage) {
		mLoadingDialog.dismiss();
		if (errMessage == null) {
			ToastUtils.show(this, "已成功上传反馈信息");
			WebParmars.isSuccessResult=true;

			finish();
		} else {
			ToastUtils.show(this, errMessage);
		}
		mLoadingDialog.dismiss();
	}
}

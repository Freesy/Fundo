package com.szkct.weloopbtsmartdevice.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

@SuppressLint("InflateParams")
public class SendMessageDialog extends Dialog {

	private EditText message_et;
	private Button sendMessageButton;
	private TextView tv_num,titleView;
	private final int num = 140;
	private Context mcontext;
	private SendMessageListener sendMessageListener;
	private String btntext,edtexthint;

	public SendMessageDialog(Context context,
			SendMessageListener sendMessageListener,String btntext,String edtexthint) {
		super(context, R.style.AddFriendDialog);
		this.mcontext = context;
		this.sendMessageListener = sendMessageListener;
		this.btntext = btntext;
		this.edtexthint = edtexthint;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewGroup.LayoutParams params = SendMessageDialog.measureDialog(
				getWindow(), 0.5, 0.6);
		View view = getLayoutInflater().inflate(R.layout.send_message_dialog,
				null);
		setContentView(view, params);
		initView();
	}

	private void initView() {
		message_et = (EditText) findViewById(R.id.message_info);
		titleView = (TextView) findViewById(R.id.search_friend_text);
		titleView.setText(btntext);
		message_et.setHint(edtexthint);
		sendMessageButton = (Button) findViewById(R.id.sendmessageButton);
		sendMessageButton.setText(btntext);
		tv_num = (TextView) findViewById(R.id.message_tv_num);

		message_et.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = num - s.length();
				tv_num.setText("" + number);
				selectionStart = message_et.getSelectionStart();
				selectionEnd = message_et.getSelectionEnd();
				// System.out.println("start="+selectionStart+",end="+selectionEnd);
				if (temp.length() > num) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					message_et.setText(s);
					message_et.setSelection(tempSelection);// 设置光标在最后
				}
			}
		});

		sendMessageButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = message_et.getText().toString();

				sendMessageListener.sendMessage(message);
			}
		});

	}

	// 发送消息接口
	public interface SendMessageListener {
		public void sendMessage(String message);
	}

	/**
	 * 显示对话框
	 */
	public void show() {
		super.show();
	}

	/**
	 * 销毁对话框
	 * 
	 * @return
	 */
	public void dismiss() {
		BTNotificationApplication.requestQueue.cancelAll(mcontext);
		super.dismiss();

	}

	@Override
	public void cancel() {
		super.cancel();

	}

	public static LayoutParams measureDialog(Window window, double baseRate,
			double hwRate) {
		return measureDialog(window.getWindowManager().getDefaultDisplay(),
				baseRate, hwRate);
	}

	@SuppressWarnings("deprecation")
	public static LayoutParams measureDialog(Display display, double baseRate,
			double hwRate) {
		return measureDialog(display.getWidth(), display.getHeight(), baseRate,
				hwRate);
	}

	public static LayoutParams measureDialog(int maxWidth, int maxHeight,
			double baseRate, double hwRate) {
		int width = -1, height = -1;
		if (maxWidth > maxHeight) {
			width = (int) (maxWidth * baseRate);
		} else {
			double rate = baseRate * maxHeight / maxWidth;
			rate = rate > 0.95 ? 0.95 : rate;
			width = (int) (maxWidth * rate);
		}
		height = (int) (width * hwRate);
		LayoutParams params = new LayoutParams(width, height);
		return params;
	}

}

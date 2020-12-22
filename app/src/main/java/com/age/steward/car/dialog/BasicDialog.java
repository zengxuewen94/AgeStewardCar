package com.age.steward.car.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.age.steward.car.R;


/**
 * @Description: 对话框基类
 */
public class BasicDialog extends Dialog {
	private Context context;

	private LinearLayout contentLayout;
	private Button submitButton;
	private Button cancelButton;
	private TextView titleText;

	public BasicDialog(Context context, int theme) {
		super(context, theme);
		initView(context);
		initEvents();
	}

	public BasicDialog(Context context) {
		this(context, R.style.dialog);
		this.context = context;
	}

	private void initView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.public_dialog_picker, null);
		contentLayout = view.findViewById(R.id.public_dialog_content);
		titleText = view.findViewById(R.id.public_dialog_title);
		submitButton = view.findViewById(R.id.public_dialog_submit);
		cancelButton = view.findViewById(R.id.public_dialog_cancel);
		setContentView(view);
		setCanceledOnTouchOutside(false);
	}

	private void initEvents() {
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	protected void setOnSubmitClickListener(View.OnClickListener l) {
		this.submitButton.setOnClickListener(l);
	}

	protected BasicDialog setTilteText(String text) {
		titleText.setText(text);
		return this;
	}

	protected BasicDialog setTilteText(int text) {
		titleText.setText(context.getResources().getString(text));
		return this;
	}

	protected void setContentLayout(View view) {
		contentLayout.addView(view);
	}
}

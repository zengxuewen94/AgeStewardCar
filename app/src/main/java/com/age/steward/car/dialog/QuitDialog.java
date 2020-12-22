package com.age.steward.car.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class QuitDialog extends BasicDialog {
	private TextView contentText;
	private String title,content;

	public QuitDialog(Context context, String title, String content) {
		super(context);
		this.content=content;
		this.title=title;
		initLogoutView(context);
	}

	private void initLogoutView(Context context) {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		contentText = new TextView(context);
		contentText.setPadding(0, 30, 0, 30);
		contentText.setGravity(Gravity.CENTER);
		contentText.setLayoutParams(lp);
		contentText.setText(content);
		setTilteText(title);
		setContentLayout(contentText);
	}

	public void setOnSubmitClickListener(View.OnClickListener l) {
		dismiss();
		super.setOnSubmitClickListener(l);
	}

	public void setContent(String content) {
		if (content != null) {
			contentText.setText(content);
		}
	}
}

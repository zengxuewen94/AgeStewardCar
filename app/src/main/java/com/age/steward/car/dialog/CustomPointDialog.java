package com.age.steward.car.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.age.steward.car.R;

import com.age.steward.car.utils.StringUtils;


/**
 * 自定义提示弹出提示Dialog
 */
public class CustomPointDialog extends Dialog {

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvCancel;
    private TextView tvConfirm;

    public CustomPointDialog(@NonNull Context mContext) {
        super(mContext, R.style.dialog);
        initView();
        windowAnim();
    }

    private void initView(){
        setContentView(R.layout.custom_point_dialog);
        setCanceledOnTouchOutside(false);
        tvTitle=findViewById(R.id.txt_custompoint_dialog_title);
        tvContent=findViewById(R.id.txt_custompoint_dialog_contnt);
        tvCancel=findViewById(R.id.txt_custompoint_dialog_cancel);
        tvConfirm=findViewById(R.id.txt_custompoint_dialog_confirm);

    }

    /**
     * @description 标题内容设置
     */
    public CustomPointDialog setTitleText(String content) {
        tvTitle.setText(content);
        tvTitle.setVisibility(StringUtils.isNull(content)? View.GONE: View.VISIBLE);
        return this;
    }


    /**
     * 标题颜色设置
     * @param colorId
     * @return
     */
    public CustomPointDialog setTitleTextColor(int colorId) {
        if(tvTitle!=null)
            tvTitle.setTextColor(colorId);
        return this;
    }

    /**
     * 文本内容设置
     * @param content
     * @return
     */
    public CustomPointDialog setContentText(String content) {
        if (tvContent!=null)
            tvContent.setText(content);
        return this;
    }

    /**
     * 文本颜色设置
     * @param colorId
     * @return
     */
    public CustomPointDialog setContentTextColor(int colorId) {
        if(tvContent!=null)
            tvContent.setTextColor(colorId);
        return this;
    }



    /**
     * 多个按钮显示设置
     * @return
     */
    public CustomPointDialog setMultipleButton() {
        tvCancel.setVisibility(View.VISIBLE);
        tvConfirm.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * 单个按钮显示设置
     * @param cancel
     * @return
     */
    public CustomPointDialog setSingleCancelOrConfirm(boolean cancel) {
        if (cancel) {
            tvCancel.setVisibility(View.VISIBLE);
            tvConfirm.setVisibility(View.GONE);
        } else {
            tvCancel.setVisibility(View.GONE);
            tvConfirm.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * 确定按钮颜色设置
     * @param colorId
     * @return
     */
    public CustomPointDialog setConfirmButtonTextColor(int colorId) {
        if (tvConfirm != null) {
            tvConfirm.setTextColor(colorId);
        }
        return this;
    }


    /**
     * 取消按钮颜色设置
     * @param colorId
     * @return
     */
    public CustomPointDialog setCancelButtonTextColor(int colorId) {
        if (tvCancel != null) {
            tvCancel.setTextColor(colorId);
        }

        return this;
    }

    /**
     * 按钮文本设置
     * @param leftText
     * @param rightText
     * @return
     */
    public CustomPointDialog setButtonsText(String leftText, String rightText) {
        if (tvCancel != null) {
            tvCancel.setText(leftText);
        }
        if (tvConfirm != null) {
            tvConfirm.setText(rightText);
        }
        return this;
    }

    public CustomPointDialog setButtonsClickListener(final OnButtonsClickListener onButtonsClickListener){
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CustomPointDialog.this.isShowing()) {
                    CustomPointDialog.this.dismiss();
                }

            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonsClickListener.onConfirmClick(v);
                if (CustomPointDialog.this.isShowing()) {
                    CustomPointDialog.this.dismiss();
                }
            }
        });
        return this;
    }
    /**
     * 单个确认按钮点击事件
     * @param
     * @return
     */
    public void setSingleButtonClickListener(final OnSingleButtonClickListener singleButtonClickListener) {
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleButtonClickListener.onConfirmClick(v);
                if (CustomPointDialog.this.isShowing()) {
                    CustomPointDialog.this.dismiss();
                }
            }
        });
    }

    private OnButtonsClickListener buttonsClickListener;
    /**
     * @description 提示弹窗按钮点击监听
     */
    public interface OnButtonsClickListener {

        /**
         * @description 取消按钮点击回调
         */
        void onCancelClick(View v);

        /**
         * @description 确认按钮点击回调
         */
        void onConfirmClick(View v);
    }

    /**
     *
     */
    private OnSingleButtonClickListener singleButtonClickListener;
    public interface OnSingleButtonClickListener {

        /**
         * @description 单按钮确定点击回调
         */
        void onConfirmClick(View v);
    }


    //设置窗口显示
    public void windowAnim() {
        Window window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.dialog); //设置窗口弹出动画
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.dismiss();
    }
}

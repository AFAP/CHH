package com.afap.discuz.chh.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressUtils {
	private static ProgressDialog proDialog;

	public static void show(int resId, Context context, Boolean cancelable) {
		String msg = context.getResources().getString(resId);
		show(msg, context, cancelable);
	}

	public static ProgressDialog getDialog() {
		return proDialog;
	}

	public static void show(String msg, Context context, Boolean cancelable) {
		if (proDialog != null && proDialog.isShowing()) {
			proDialog.dismiss();
		}
		proDialog = new ProgressDialog(context);
		proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		proDialog.setMessage(msg);
		proDialog.setIndeterminate(false);// 设置进度条是否为不明确
		proDialog.setCancelable(cancelable);// 设置进度条是否可以按退回键取消
		proDialog.show();
	}

	// public static void showProgressDialogHorizontal(ProgressDialog
	// proDialog,String msg, Boolean cancelable,int value,int max) {
	// ProgressDialogUtils.proDialog = proDialog;
	// proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	// proDialog.setMessage(msg);
	// proDialog.setIndeterminate(false);// 设置进度条是否为不明确
	// proDialog.setCancelable(cancelable);// 设置进度条是否可以按退回键取消
	// proDialog.setProgress(value);
	// proDialog.setMax(max);
	// proDialog.show();
	// }

	public static void dismiss() {
		if (proDialog != null) {
			proDialog.dismiss();
		}
	}
}
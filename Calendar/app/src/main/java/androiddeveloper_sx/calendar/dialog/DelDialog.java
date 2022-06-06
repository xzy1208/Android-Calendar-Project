package androiddeveloper_sx.calendar.dialog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import edu.zjut.androiddeveloper_sx.calendar.R;

/**
 * Created by ChunY on 2022/5/28.
 */

public class DelDialog {
    //点击确认按钮回调接口
    public interface OnConfirmListener {
        public void onConfirmClick();
    }

    public static void show(Activity activity, String content,
                            final OnConfirmListener confirmListener) {
        // 加载布局文件
        View view = View.inflate(activity, R.layout.delete_dialog, null);
        TextView text = (TextView) view.findViewById(R.id.del_dialog_text);
        TextView confirm = (TextView) view.findViewById(R.id.del_dialog_confirm);
        TextView cancel = (TextView) view.findViewById(R.id.del_dialog_cancel);

        if (!isNullOrEmpty(content)) {
            text.setText(content);
        }

        // 创建Dialog
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setCancelable(false);// 设置点击dialog以外区域不取消Dialog
        dialog.show();
        dialog.setContentView(view);
        dialog.getWindow().setLayout(getWidth(activity) / 5 * 4,
                ActionBar.LayoutParams.WRAP_CONTENT);//设置弹出框宽度为屏幕宽度的三分之二

        // 确定
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                confirmListener.onConfirmClick();
            }
        });

        // 取消
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static boolean isNullOrEmpty(String string) {
        boolean flag = false;
        if (null == string || string.trim().length() == 0) {
            flag = true;
        }
        return flag;
    }

    public static int getWidth(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        return width;
    }
}

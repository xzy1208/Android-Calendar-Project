package edu.zjut.androiddeveloper_sx.calendar.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class NumberPickerDialog extends AlertDialog implements android.content.DialogInterface.OnClickListener {
 
    private OnNumberSelectedListener mListener;
    private NumberPicker mNumberPicker;
 
    public NumberPickerDialog(final Context context, OnNumberSelectedListener mListener, int currentNumber) {
        super(context);
        this.mListener = mListener;
        setTitle("重复时间");
        setButton(BUTTON_NEGATIVE, "取消", this);
        setButton(BUTTON_POSITIVE, "确认", this);
 
        mNumberPicker = new NumberPicker(context);
        mNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(99);
        mNumberPicker.setValue(currentNumber);

        LinearLayout layout = new LinearLayout(context);
        layout.setGravity(Gravity.CENTER);
        layout.addView(mNumberPicker, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setView(layout);
        setCanceledOnTouchOutside(true);
    }
 
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mListener != null) {
                    mNumberPicker.clearFocus();
                    mListener.onNumberSelected(mNumberPicker, mNumberPicker.getValue());
                }
                break;
        }
    }

    public interface OnNumberSelectedListener {
        void onNumberSelected(NumberPicker view, int number);
    }
 
}
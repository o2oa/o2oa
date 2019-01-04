package jiguang.chat.utils.timechoose;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jiguang.chat.utils.citychoose.view.adapter.AbstractWheelTextAdapter;

/**
 * Created by ${chenyn} on 2017/2/23.
 */

public class NumericWheelAdapter extends AbstractWheelTextAdapter {

    /** The default min value */
    public static final int DEFAULT_MAX_VALUE = 9;

    /** The default max value */
    private static final int DEFAULT_MIN_VALUE = 0;

    // Values
    private int minValue;
    private int maxValue;

    // format
    private String format;

    private String label;

    private int multiple;

    /**
     * Constructor
     * @param context the current context
     */
    public NumericWheelAdapter(Context context) {
        this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue) {
        this(context, minValue, maxValue, null);
    }

    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format the format string
     */
    public NumericWheelAdapter(Context context, int minValue, int maxValue, String format) {
        super(context);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
    }

    public NumericWheelAdapter(Context context, int minValue, int maxValue, String format,int multiple) {
        super(context);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
        this.multiple = multiple;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {

            int value = 0;
            if (multiple != 0){
                value = minValue + index * multiple;
            }else{
                value = minValue + index;
            }
//            int value = minValue + index;
            return format != null ? String.format(format, value) : Integer.toString(value);
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        if (index >= 0 && index < getItemsCount()) {
            if (convertView == null) {
                convertView = getView(itemResourceId, parent);
            }
            TextView textView = getTextView(convertView, itemTextResourceId);
            if (textView != null) {
                CharSequence text = getItemText(index);
                if (text == null) {
                    text = "";
                }
                textView.setText(text+label);
                textView.setPadding(0,3,0,3);
                if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
                    configureTextView(textView);
                }
            }
            return convertView;
        }
        return null;
    }

    public void setLabel(String label) {
        this.label=label;
    }
}

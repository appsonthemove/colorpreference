package com.kizitonwose.colorpreferencecompat;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;
import com.kizitonwose.colorpreference.ColorUtils;
import com.kizitonwose.colorpreference.PreviewSize;

/**
 * Created by Kizito Nwose on 9/26/2016.
 */
public class ColorPreferenceCompat extends Preference implements ColorDialog.OnColorSelectedListener {
    private int[] colorChoices = {};
    private String[] colorChoiceNames = {};
    private String colorNotSetString = "No colour set";
    private int value = 0;
    private int itemLayoutId = R.layout.pref_color_layout;
    private int itemLayoutLargeId = R.layout.pref_color_layout_large;
    private int numColumns = 5;
    private ColorShape colorShape = ColorShape.CIRCLE;
    private boolean showDialog = true;

    public ColorPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs, defStyleAttr);
    }

    public ColorPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
    }

    public ColorPreferenceCompat(Context context) {
        super(context);
        initAttrs(null, 0);
    }

    public void setColorChoices(int[] colorChoices) {
        this.colorChoices = colorChoices;
    }

    public void setColorChoiceNames(String[] colorChoiceNames) {
        this.colorChoiceNames = colorChoiceNames;
    }

    public void setColorNotSetString(String value) {
        this.colorNotSetString = value;
    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.ColorPreferenceCompat, defStyle, defStyle);

        PreviewSize previewSize;
        try {
            numColumns = a.getInteger(R.styleable.ColorPreferenceCompat_numColumns, numColumns);
            colorShape = ColorShape.getShape(a.getInteger(R.styleable.ColorPreferenceCompat_colorShape, 1));
            previewSize = PreviewSize.getSize(a.getInteger(R.styleable.ColorPreferenceCompat_viewSize, 1));
            showDialog = a.getBoolean(R.styleable.ColorPreferenceCompat_showDialog, true);
            int choicesResId = a.getResourceId(R.styleable.ColorPreferenceCompat_colorChoices,
                    R.array.default_color_choice_values);
            colorChoices = ColorUtils.extractColorArray(choicesResId, getContext());

        } finally {
            a.recycle();
        }
        setWidgetLayoutResource(previewSize == PreviewSize.NORMAL ? itemLayoutId : itemLayoutLargeId);

    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ImageView colorView = (ImageView) holder.findViewById(R.id.color_view);
        if (colorView != null) {
            ColorUtils.setColorViewValue(colorView, value, false, colorShape);
            colorView.setContentDescription(colorNotSetString);
            if (colorChoiceNames != null) {
                for (int index = 0; index < colorChoices.length; index++) {
                    if (colorChoices[index] == value) {
                        if (index < colorChoiceNames.length) {
                            colorView.setContentDescription(colorChoiceNames[index]);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void setValue(int value, boolean persist) {
        if (callChangeListener(value)) {
            this.value = value;
            if (persist) {
                persistInt(value);
            }
            notifyChanged();
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (showDialog) {
            ColorUtils.showDialog(getContext(), this, getFragmentTag(),
                    numColumns, colorShape, colorChoices, colorChoiceNames, getValue(), colorNotSetString);
        }
    }

    @Override
    public void onAttached() {
        super.onAttached();
        //helps during activity re-creation
        if (showDialog) {
            ColorUtils.attach(getContext(), this, getFragmentTag());
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue, true);
    }

    public String getFragmentTag() {
        return "color_" + getKey();
    }

    public int getValue() {
        return value;
    }

    @Override
    public void onColorSelected(int newColor, String tag) {
        setValue(newColor, true);
    }
}

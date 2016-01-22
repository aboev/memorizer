package memorizer.freecoders.com.flashcards.classes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import memorizer.freecoders.com.flashcards.R;
import memorizer.freecoders.com.flashcards.common.Multicards;
import memorizer.freecoders.com.flashcards.json.TagDescriptor;

/**
 * Created by alex-mac on 16.01.16.
 */
public class TagView extends FrameLayout {

    private TextView textViewTagName;
    private String strTagID = "";
    private int color;
    public Boolean boolSelected = false;
    private Drawable background;
    private View view;

    public TagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TagView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        view = inflate(getContext(), R.layout.tag_view, null);
        textViewTagName = (TextView) view.findViewById(R.id.textViewTagName);
        background = textViewTagName.getBackground();
        addView(view);
    }

    public String getName() {
        return textViewTagName.getText().toString();
    }

    public String getTagID() {
        return this.strTagID;
    }

    public void setTag (TagDescriptor tag) {
        textViewTagName.setText(tag.getName());
        this.strTagID = tag.tag_id;
        setColor();
        invalidate();
    }

    public Boolean onSelect () {
        if (this.boolSelected)
            this.boolSelected = false;
        else
            this.boolSelected = true;
        setColor();
        return  boolSelected;
    }

    private void setColor () {
        if (boolSelected) {
            int colorSelected =
                    ContextCompat.getColor(Multicards.getMainActivity(), R.color.colorSelected);
            if (background instanceof ShapeDrawable) {
                ShapeDrawable shapeDrawable = (ShapeDrawable)background;
                shapeDrawable.getPaint().setColor(colorSelected);
            } else if (background instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable)background;
                gradientDrawable.setColor(colorSelected);
            }
        } else {
            int colorDeselected =
                    ContextCompat.getColor(Multicards.getMainActivity(), R.color.colorDeselected);
            if (background instanceof ShapeDrawable) {
                ShapeDrawable shapeDrawable = (ShapeDrawable)background;
                shapeDrawable.getPaint().setColor(colorDeselected);
            } else if (background instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable)background;
                gradientDrawable.setColor(colorDeselected);
            }
        }
    }
}
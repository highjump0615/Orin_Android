package br.com.dina.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.dina.ui.R;

public class UIButtonWithSwitch extends LinearLayout {

    private LayoutInflater mInflater;
    private LinearLayout mButtonContainer;
    private ClickListener mClickListener;
    private CharSequence mTitle;
    private CharSequence mSubtitle;
    private int mImage;
    private Button mBtnSwitch;
    private boolean mToggleState;

    public UIButtonWithSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setClickable(true);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mButtonContainer = (LinearLayout) mInflater.inflate(R.layout.list_item_switch, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UIButton, 0, 0);
        mTitle = a.getString(R.styleable.UIButton_uititle);
        mSubtitle = a.getString(R.styleable.UIButton_uisubtitle);
        mImage = a.getResourceId(R.styleable.UIButton_uiimage, -1);
        mToggleState = a.getBoolean(R.styleable.UIButton_uistate, true);

        if (mTitle != null) {
            ((TextView) mButtonContainer.findViewById(R.id.title)).setText(mTitle.toString());
        } else {
            ((TextView) mButtonContainer.findViewById(R.id.title)).setText("subtitle");
        }

        if (mSubtitle != null) {
            ((TextView) mButtonContainer.findViewById(R.id.subtitle)).setText(mSubtitle.toString());
        } else {
            ((TextView) mButtonContainer.findViewById(R.id.subtitle)).setVisibility(View.GONE);
        }

        if (mImage > -1) {
            ((ImageView) mButtonContainer.findViewById(R.id.image)).setImageResource(mImage);
        }

        mBtnSwitch = (Button) mButtonContainer.findViewById(R.id.btn_switch);
        mBtnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null)
                    mClickListener.onClick(UIButtonWithSwitch.this);

                setToggleState(!mToggleState);
            }
        });

        addView(mButtonContainer, params);
    }

    public void setToggleState(boolean state) {
        mToggleState = state;
        mBtnSwitch.setBackgroundResource(mToggleState ? R.drawable.tgbtn_bg_on : R.drawable.tgbtn_bg_off);
    }

    public interface ClickListener {
        void onClick(View view);
    }

    /**
     * @param listener
     */
    public void addClickListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    /**
     *
     */
    public void removeClickListener() {
        this.mClickListener = null;
    }

}

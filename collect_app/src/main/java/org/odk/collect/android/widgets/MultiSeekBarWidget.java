package org.odk.collect.android.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectMultiData;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.form.api.FormEntryPrompt;
import org.javarosa.xpath.expr.XPathFuncExpr;
import org.odk.collect.android.R;
import org.odk.collect.android.external.ExternalDataUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom (multi) seekbar widget.
 * <p/>
 * Created by sidneyfeygin on 7/29/15.
 */
public class MultiSeekBarWidget extends QuestionWidget {

    List<SelectChoice> mItems;
    List<SeekBar> mSeekBars;
    Map<Integer,Boolean> mIsSeekbarTouched;


    // Mapping of seekBarIds to the current values of the seek bar
    Map<Integer, Integer> mCurrValues;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("unchecked")
    public MultiSeekBarWidget(Context context, FormEntryPrompt prompt) {
        super(context, prompt);
        mPrompt = prompt;
        mSeekBars = new ArrayList<>();
        mCurrValues = new HashMap<>();
        mIsSeekbarTouched = new HashMap<>();
        // SurveyCTO-added support for dynamic select content (from .csv files)
        XPathFuncExpr xPathFuncExpr = ExternalDataUtil.getSearchXPathExpression(prompt.getAppearanceHint());
        if (xPathFuncExpr != null) {
            mItems = ExternalDataUtil.populateExternalChoices(prompt, xPathFuncExpr);
        } else {
            mItems = prompt.getSelectChoices();
        }

        setOrientation(LinearLayout.VERTICAL);

        List<Selection> ve = new ArrayList<>();
        if (prompt.getAnswerValue() != null) {
            ve = (List<Selection>) prompt.getAnswerValue().getValue();
        }

        if (mItems != null) {
            for (int i = 0; i < mItems.size(); i++) {
                SeekBar sB = new SeekBar(getContext());
                sB.setTag(i);
                sB.setId(QuestionWidget.newUniqueId());
                sB.setFocusable(!prompt.isReadOnly());
                sB.setEnabled(!prompt.isReadOnly());

                final SelectChoice selectChoice = mItems.get(i);

                final String value = selectChoice.getValue();
                final String[] split = value.split(",");

                final String min = split[0];
                final String max = split[1];
                final String labelRight;
                final String labelLeft;
                if (split.length > 2) {
                    labelLeft = split[2];
                    labelRight = split[3];
                } else {
                    labelLeft = min;
                    labelRight = max;
                }
                final Integer minVal = Integer.valueOf(min);
                final Integer maxVal = Integer.valueOf(max);

                final String labelInnerText = selectChoice.getLabelInnerText();

                sB.setMax((maxVal - minVal));
                initSeekBar(sB);
                // default
                mIsSeekbarTouched.put(i,false);

                if (ve != null && ve.size() > 0) {
                    if (ve.size() - i > 0) {
                        if(ve.get(i).xmlValue.equals("-1")){
                            initSeekBar(sB);
                        }else {
                            sB.setThumb(getContext().getResources().getDrawable(R.drawable.custom_blue_btn));
                            sB.setProgress(Integer.parseInt(ve.get(i).getValue()));
                            mIsSeekbarTouched.put(i, true);
                        }
                    }
                    else{
                        mIsSeekbarTouched.put(i,false);
                        initSeekBar(sB);
                    }
                }


                addView(createSBLabel(i, labelInnerText));

                mSeekBars.add(sB);

                sB.setMinimumHeight(50);

                sB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekBar.setThumb(getContext().getResources().getDrawable(R.drawable.custom_blue_btn));
                        seekBar.setProgressDrawable(getContext().getResources().getDrawable(R.drawable.transparent_progress));
                        seekBar.getX();
                        if (progress == 0 || progress == seekBar.getMax()) {
                            initSeekBar(seekBar);
                        } else if (progress >= 0 && progress < seekBar.getMax()) {
                            seekBar.getThumb().setVisible(true, false);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mCurrValues.put(seekBar.getId(), seekBar.getProgress());
                    }
                });

                addView(sB);

                addView(createSBRangeText(i, labelLeft, labelRight));

                ImageView divider = new ImageView(getContext());
                divider.setBackgroundResource(android.R.drawable.divider_horizontal_bright);
                if (i != mItems.size() - 1) {

                    divider.setPadding(0, 0, 0, 8);
                    addView(divider);
                }
            }
        }

    }

    public void initSeekBar(SeekBar seekBar) {
        seekBar.setThumb(getContext().getResources().getDrawable(R.drawable.transparent_thumb_drawable));
        seekBar.setProgressDrawable(getContext().getResources().getDrawable(R.drawable.transparent_progress));
    }


    /**
     * Creates two new text views associated with the ranges (min and max) of a SeekBar
     *
     * @param i   index used to identify SeekBar
     * @param min minimum range of the SeekBar
     * @param max maximum range of the SeekBar
     */
    public LinearLayout createSBRangeText(int i, String min, String max) {

        LinearLayout container = new LinearLayout(getContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.setWeightSum(1.0f);

        TextView minTv = makeSBTextView(i, min);
        TextView maxTv = makeSBTextView(i, max);

        //split weight equally between TextViews
        final ViewGroup.LayoutParams rangeParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.5f);

        minTv.setGravity(Gravity.LEFT);
        maxTv.setGravity(Gravity.RIGHT);

        container.addView(minTv, rangeParams);
        container.addView(maxTv, rangeParams);

        container.setPadding(24, 24, 24, 16);

        return container;
    }


    public TextView createSBLabel(int i, String labelInnerText) {
        TextView label = makeSBTextView(i, labelInnerText);
        label.setText(Html.fromHtml(labelInnerText));
        label.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        label.setPadding(0, 24, 0, 24);

        return label;
    }


    @Override
    public IAnswerData getAnswer() {
        List<Selection> vc = new ArrayList<>();
        for (int i = 0; i < mSeekBars.size(); i++) {
            SeekBar sB = mSeekBars.get(i);
            final int progress = sB.getProgress();
            if(progress>0) {
                final Selection selection = new Selection(progress);
                selection.attachChoice(mItems.get(i));
                selection.xmlValue = String.valueOf(progress);
                vc.add(i,selection);
            }else{
                if(!mIsSeekbarTouched.get(i)){
                    final Selection selection = new Selection("-1");
                    selection.attachChoice(mItems.get(i));
                    selection.xmlValue = String.valueOf("-1");
                    vc.add(i,selection);
                    initSeekBar(sB);
                }
            }
        }
        if (vc.size() == 0)
            return null;
        else
            return new SelectMultiData(vc);
    }

    @Override
    public void clearAnswer() {
        for (int i = 0; i < mSeekBars.size(); i++) {
            final SeekBar sB = mSeekBars.get(i);
            if (sB.getProgress() > 0) {
                sB.setProgress(-1);
                initSeekBar(sB);
            }
        }
    }

    @Override
    public void setFocus(Context context) {
        // Hide the soft keyboard if it's showing.
        InputMethodManager inputManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        super.cancelLongPress();
        for (SeekBar sB : mSeekBars) {
            sB.cancelLongPress();
        }
    }

    @Override
    public boolean suppressFlingGesture(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return (Math.abs(velocityX) < 700);
    }

    /**
     * Utility to make TextViews associated with each seekbar
     *
     * @param i    the seekbar associated with this TextView
     * @param text the text to put in the TextView
     * @return the TextView
     */
    private TextView makeSBTextView(int i, String text) {
        TextView label = new TextView(getContext());
        label.setText(text);
        label.setId(i);
        return label;
    }

}

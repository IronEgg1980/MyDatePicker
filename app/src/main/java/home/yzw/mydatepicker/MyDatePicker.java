package home.yzw.mydatepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.Nullable;

public class MyDatePicker extends LinearLayout {

    public interface DatePickerClickListener{
        void onClick(int year,int month,int dayOfMonth);
    }

    public void setClickListener(DatePickerClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private String TAG = "殷宗旺";

    private DatePickerClickListener clickListener;

    private boolean isMultiSelect = false;

    private int dateTextNormalColor,dateTextSelectedColor,dateNormalBG,dateSelectedBG;
    private Drawable selectedBGDrawable;
    private Calendar mCalendar;
    private SimpleDateFormat dateFormat,dayOfMonthFormat;
    private long today,selectedDay,monthDay;
    private int[] ids;
    private int currentId;
    private TextView preMonthView, titleView,nextMonthView;
    private LinearLayout dateViewGroup;

    public MyDatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.MyDatePicker);
        dateTextNormalColor = typedArray.getColor(R.styleable.MyDatePicker_dateTextNormalColor, Color.BLACK);
        dateTextSelectedColor = typedArray.getColor(R.styleable.MyDatePicker_dateTextSelectedColor,Color.WHITE);
        dateNormalBG = typedArray.getColor(R.styleable.MyDatePicker_dateNormalBG,Color.TRANSPARENT);
        dateSelectedBG = typedArray.getColor(R.styleable.MyDatePicker_dateSelectedBG,Color.parseColor("#0e7772"));
        selectedBGDrawable = typedArray.getDrawable(R.styleable.MyDatePicker_dateSelectedDrawable);
        typedArray.recycle();
        initial();
        initialViews();
        setDate(today);
    }

    public void setDate(long timeInMillis){
        long[] values =new long[42];
        mCalendar.setTimeInMillis(timeInMillis);
        mCalendar.set(Calendar.DAY_OF_MONTH,15);
        monthDay = mCalendar.getTimeInMillis();

        mCalendar.set(Calendar.DAY_OF_MONTH,1);
        int week = mCalendar.get(Calendar.DAY_OF_WEEK);
        int diff = week> 1 && week< 4? (-5-week) % 7 - 7:  (-5-week) % 7;
        mCalendar.add(Calendar.DAY_OF_MONTH,diff);

        for(int i  = 0;i<42;i++){
            if(i > 0)
                mCalendar.add(Calendar.DAY_OF_MONTH,1);
            values[i] = mCalendar.getTimeInMillis();
        }

        for(int i = 0;i<42;i++){
            long time = values[i];
            String dateString = dayOfMonthFormat.format(time);
            TextView textView = findViewById(ids[i]);
            if(textView!=null){
                textView.setTextColor(dateTextNormalColor);
                textView.setBackgroundColor(dateNormalBG);
                textView.setText(dateString);
                textView.setTag(time);
                if (compareDay(time, selectedDay) == 0) {
                    textView.setTextColor(dateTextSelectedColor);
                    if(selectedBGDrawable != null){
                        textView.setBackground(selectedBGDrawable);
                    }else {
                        textView.setBackgroundColor(dateSelectedBG);
                    }
                    currentId = textView.getId();
                } else if(compareMonth(time,monthDay) != 0) {
                    textView.setTextColor(Color.GRAY);
                }
            }
        }
        mCalendar.setTimeInMillis(monthDay);
        titleView.setText(dateFormat.format(monthDay));
    }

    private void initial(){
        mCalendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy年M月", Locale.CHINA);
        dayOfMonthFormat = new SimpleDateFormat("d", Locale.CHINA);
        today = mCalendar.getTimeInMillis();
        selectedDay = today;
        mCalendar.set(Calendar.DAY_OF_MONTH,15);
        monthDay = mCalendar.getTimeInMillis();
        currentId = -1;
        ids = new int[42];
        for(int i = 0;i<42;i++){
            ids[i] = generateViewId();
        }
    }

    private void initialViews(){
        setOrientation(VERTICAL);
        removeAllViews();
        addTitleViews();
        addWeekTitleViews();
        addDateViews();
    }

    private void addTitleViews(){
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(HORIZONTAL);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8,0,8,0);

        preMonthView = new TextView(getContext());
        preMonthView.setGravity(Gravity.CENTER);
        preMonthView.setPadding(8,8,8,8);
        preMonthView.setTextSize(25);
        preMonthView.setText("<");
        preMonthView.setTextColor(dateTextNormalColor);
        preMonthView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                preMonthClick();
            }
        });

        titleView = new TextView(getContext());
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(8,8,8,8);
        titleView.setTextSize(25);
        titleView.setText("");
        titleView.setTextColor(dateTextNormalColor);
        titleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonthClick();
            }
        });

        nextMonthView = new TextView(getContext());
        nextMonthView.setGravity(Gravity.CENTER);
        nextMonthView.setPadding(8,8,8,8);
        nextMonthView.setTextSize(25);
        nextMonthView.setText(">");
        nextMonthView.setTextColor(dateTextNormalColor);
        nextMonthView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthClick();
            }
        });

        LayoutParams childLP = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        linearLayout.addView(preMonthView,childLP);
        linearLayout.addView(titleView,new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,2));
        linearLayout.addView(nextMonthView,childLP);

        addView(linearLayout,lp);
    }

    private void addWeekTitleViews(){
        LinearLayout linearLayout = new LinearLayout(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8,0,8,0);
        String[] weeks = {"一","二","三","四","五","六","日"};
        LayoutParams childLP = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        for(int i = 0 ;i<7;i++){
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            textView.setText(weeks[i]);
            if(i == 5)
                textView.setTextColor(Color.GREEN);
            if(i == 6)
                textView.setTextColor(Color.RED);
            linearLayout.addView(textView,childLP);
        }
        addView(linearLayout,lp);
    }

    private void addDateViews(){
        dateViewGroup = new LinearLayout(getContext());
        dateViewGroup.setOrientation(VERTICAL);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(8,0,8,0);
        LayoutParams _lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1);
        LayoutParams childLP = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1);
        LayoutParams child_child_LP = new LayoutParams(120, 120);
        child_child_LP.gravity = Gravity.CENTER;
        int index = 0;
        for(int i = 0;i<6;i++){
            LinearLayout linearLayout =  new LinearLayout(getContext());
            linearLayout.setOrientation(HORIZONTAL);
            for(int j = 0;j<7;j++){
                LinearLayout ll = new LinearLayout(getContext());
                final TextView textView = new TextView(getContext());
                textView.setPadding(10,10,10,10);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(20);
                textView.setId(ids[index++]);
                textView.setTextColor(dateTextNormalColor);
                textView.setBackgroundColor(dateNormalBG);
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dateViewClick(textView);
                    }
                });
                ll.addView(textView,child_child_LP);
                linearLayout.addView(ll,childLP);
            }
            dateViewGroup.addView(linearLayout,_lp);
        }
        addView(dateViewGroup,lp);
    }

    private void dateViewClick(TextView textView){
        long value = (long) textView.getTag();
        selectedDay = value;
        textView.setTextColor(dateTextSelectedColor);
        if(selectedBGDrawable != null){
            textView.setBackground(selectedBGDrawable);
        }else {
            textView.setBackgroundColor(dateSelectedBG);
        }

        mCalendar.setTimeInMillis(value);
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int diff = compareMonth(value,monthDay);
        if(diff == 0) {
            if (currentId != -1) {
                TextView oldTV = findViewById(currentId);
                if (oldTV != null) {
                    oldTV.setTextColor(dateTextNormalColor);
                    oldTV.setBackgroundColor(dateNormalBG);
                }
                currentId = textView.getId();
            }
        }else if(diff > 0){
            nextMonthClick();
        }else {
            preMonthClick();
        }

        if(clickListener != null){
            clickListener.onClick(year,month,day);
        }
        mCalendar.setTimeInMillis(monthDay);
    }

    private void preMonthClick(){
        mCalendar.add(Calendar.MONTH,-1);
        mCalendar.set(Calendar.DAY_OF_MONTH,15);
        monthDay = mCalendar.getTimeInMillis();
        setDate(monthDay);
    }

    private void currentMonthClick(){
        mCalendar.setTimeInMillis(today);
        mCalendar.set(Calendar.DAY_OF_MONTH,15);
        monthDay = mCalendar.getTimeInMillis();
        setDate(monthDay);
    }

    private void nextMonthClick(){
        mCalendar.add(Calendar.MONTH,1);
        mCalendar.set(Calendar.DAY_OF_MONTH,15);
        monthDay = mCalendar.getTimeInMillis();
        setDate(monthDay);
    }

    private int compareMonth(long d1,long d2){
        mCalendar.setTimeInMillis(d1);
        int y1 = mCalendar.get(Calendar.YEAR);
        int m1 = mCalendar.get(Calendar.MONTH);
        mCalendar.setTimeInMillis(d2);
        int y2 = mCalendar.get(Calendar.YEAR);
        int m2 = mCalendar.get(Calendar.MONTH);
        mCalendar.setTimeInMillis(monthDay);
        if(y1 == y2){
            return Integer.compare(m1,m2);
        }
        return Integer.compare(y1,y2);
    }

    private int compareDay(long d1,long d2){
        int i = compareMonth(d1,d2);
        if(i == 0){
            mCalendar.setTimeInMillis(d1);
            int day1 = mCalendar.get(Calendar.DAY_OF_MONTH);
            mCalendar.setTimeInMillis(d2);
            int day2 = mCalendar.get(Calendar.DAY_OF_MONTH);
            mCalendar.setTimeInMillis(monthDay);
            return Integer.compare(day1,day2);
        }
        return i;
    }
}

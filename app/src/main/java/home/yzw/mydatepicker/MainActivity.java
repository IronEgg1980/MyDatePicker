package home.yzw.mydatepicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private SimpleDateFormat format;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        format =new SimpleDateFormat("yyyy年M月d日 EEEE",Locale.CHINA);
        textView = findViewById(R.id.textview);
        final MyDatePicker datePicker = findViewById(R.id.datepicker);
        datePicker.isMultiSelect = true;
        datePicker.setClickListener(new MyDatePicker.DatePickerClickListener() {
            @Override
            public void onClick(int year, int month, int dayOfMonth) {
                Calendar calendar = new GregorianCalendar(Locale.CHINA);
                calendar.set(year,month,dayOfMonth);
                textView.setText(format.format(calendar.getTime()));
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long[] times = datePicker.getSelectDateRange();
                String s = format.format(times[0]) +" | "+format.format(times[1]);
                textView.setText(s);
            }
        });
    }
}

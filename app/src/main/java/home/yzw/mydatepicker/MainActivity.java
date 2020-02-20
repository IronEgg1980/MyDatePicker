package home.yzw.mydatepicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);
        MyDatePicker datePicker = findViewById(R.id.datepicker);
        datePicker.setClickListener(new MyDatePicker.DatePickerClickListener() {
            @Override
            public void onClick(int year, int month, int dayOfMonth) {
                Calendar calendar = new GregorianCalendar(Locale.CHINA);
                calendar.set(year,month,dayOfMonth);
                textView.setText(new SimpleDateFormat("yyyy年M月d日 EEEE",Locale.CHINA).format(calendar.getTime()));
            }
        });
    }
}

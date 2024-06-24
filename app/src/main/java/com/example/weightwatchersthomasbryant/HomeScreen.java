package com.example.weightwatchersthomasbryant;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Date;

public class HomeScreen extends AppCompatActivity {
    User user;
    DatabaseHelper database = new DatabaseHelper(HomeScreen.this);
    ImageButton imgBtn_Settings;
    TextView textViewGoalWeightEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        imgBtn_Settings = findViewById(R.id.imageButtonProfile);
        Intent i = getIntent();
        //int userID, String email, String password, double currGoalWeight, double currWeight
        int userID = i.getIntExtra("userID", 0); // if there's a default value we have a problem.
        String email = i.getStringExtra("userEmail");
        String password = i.getStringExtra("userPassword");
        double currGoalWeight = i.getDoubleExtra("userCurrGoalWeight", 0.0);
        double currWeight = i.getDoubleExtra("userCurrWeight", 0.0);
        textViewGoalWeightEntry = findViewById(R.id.textViewGoalWeightEntry);
        user = new User(userID, email, password, currGoalWeight, currWeight);


        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> goalSeries = new LineGraphSeries<>();
        Cursor cs = database.returnWeightList(user);
        if(cs.moveToFirst())
        {
            do
            {
                double DB_CURR_WEIGHT = cs.getDouble(2);
                double DB_GOAL_WEIGHT = cs.getDouble(3);
                String DB_DATE = cs.getString(4);
                //Log.d("HomeScreen", DB_DATE);
                int year, month, day;
                year = Integer.parseInt(DB_DATE.substring(0, 4)); //DATE FORMAT: 1999-12-12 // 0123 4 56 7 89 10 length somehow 3?
                month = Integer.parseInt(DB_DATE.substring(5, 7));
                day = Integer.parseInt(DB_DATE.substring(8, 10));
                Date date = new Date(year, month, day); //could store as such.. but bah humbug.
                series.appendData(new DataPoint(date, DB_CURR_WEIGHT), true, 365);
                goalSeries.appendData(new DataPoint(date, DB_GOAL_WEIGHT), true, 365);
                String text = DB_GOAL_WEIGHT + " lbs"; //set repeatedly until most recent (latest) entry.
                textViewGoalWeightEntry.setText(text);
            } while (cs.moveToNext());
            cs.close();

            //graph config
            graph.addSeries(series);
            graph.addSeries(goalSeries);
            graph.setTitle("Weight Graph");
            graph.setTitleColor(Color.parseColor("#2196F3"));
            graph.setTitleTextSize(60);
            graph.setHorizontalScrollBarEnabled(true);
            graph.setVerticalScrollBarEnabled(true);

            //curr weight series config
            series.setColor(Color.parseColor("#2196F3"));
            series.setTitle("Current Weight");
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(10);
            series.setThickness(8);

            //goal weight series config
            goalSeries.setColor(Color.GRAY);
            goalSeries.setDrawDataPoints(false);
            goalSeries.setThickness(8);

            //grid config
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle("Date (UTC)"); //This is a great plugin... Unfortunately, there isn't a way to properly display date here without hacking it together. Really bummed about that.
            gridLabel.setHorizontalAxisTitleTextSize(50);
            gridLabel.setHorizontalAxisTitleColor(Color.parseColor("#2196F3"));
            gridLabel.setHorizontalLabelsAngle(100);

            gridLabel.setVerticalAxisTitle("Weight (lbs)");
            gridLabel.setVerticalAxisTitleTextSize(50);
            gridLabel.setVerticalAxisTitleColor(Color.parseColor("#2196F3"));

        }
        else
        {
            Toast.makeText(HomeScreen.this, "ERORR: Failed to acquire user weight data for graph.", Toast.LENGTH_LONG).show();
        }

        imgBtn_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, Settings.class);
                intent.putExtra("userID", user.getUserID());
                intent.putExtra("userEmail", user.getEmail());
                intent.putExtra("userPassword", user.getPassword());
                intent.putExtra("userCurrGoalWeight", user.getCurrGoalWeight());
                intent.putExtra("userCurrWeight", user.getCurrWeight());
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
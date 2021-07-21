package com.example.oop_project_2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TodoListMain2 extends AppCompatActivity {


    DBHelper mydb;
    LinearLayout empty;
    NestedScrollView scrollView;
    LinearLayout todayContainer, tomorrowContainer, otherContainer;
    NoScrollListView taskListToday, taskListTomorrow, taskListUpcoming;
    ArrayList<HashMap<String, String>> todayList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tomorrowList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> upcomingList = new ArrayList<HashMap<String, String>>();
    DelayedTask moDelayedTask = null;// new DelayedTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_todo_list_main2);

        mydb = new DBHelper(this);
        empty = findViewById(R.id.empty);
        scrollView = findViewById(R.id.scrollView);
        todayContainer = findViewById(R.id.todayContainer);
        tomorrowContainer = findViewById(R.id.tomorrowContainer);
        otherContainer = findViewById(R.id.otherContainer);
        taskListToday = findViewById(R.id.taskListToday);
        taskListTomorrow = findViewById(R.id.taskListTomorrow);
        taskListUpcoming = findViewById(R.id.taskListUpcoming);
        try {
//            moDelayedTask = new DelayedTask(getApplicationContext());
//            Date odate = new Date();
//            Calendar calobj = Calendar.getInstance();
//            Date oDateNow= calobj.getTime();
//            Log.d("Task", "Task Started");
//            // to test add five seconds or 5000 msec
//            odate.setTime(oDateNow.getTime() +5000);
//            moDelayedTask.SetEmailForTask("tanmaychowhan@gmail.com","Test1","This is a send1");
//            moDelayedTask.AddTask(odate,2);
//            Date odate1 = new Date();
//            odate1.setTime(oDateNow.getTime() +10000);
//            moDelayedTask.SetEmailForTask("tanmaychowhan@gmail.com","Test2","This is a send2");
//
//            moDelayedTask.AddTask(odate1,3);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void openAddModifyTask(View view) {
        startActivity(new Intent(this, AddModifyTask.class));
    }


    @Override
    public void onResume() {
        super.onResume();
        populateData();
    }


    public void populateData() {
        mydb = new DBHelper(this);

        runOnUiThread(new Runnable() {
            public void run() {
                fetchDataFromDB();
            }
        });
    }


    public void fetchDataFromDB() {
        todayList.clear();
        tomorrowList.clear();
        upcomingList.clear();

        Cursor today = mydb.getTodayTask();
        Cursor tomorrow = mydb.getTomorrowTask();
        Cursor upcoming = mydb.getUpcomingTask();

        loadDataList(today, todayList);
        loadDataList(tomorrow, tomorrowList);
        loadDataList(upcoming, upcomingList);


        if (todayList.isEmpty() && tomorrowList.isEmpty() && upcomingList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            if (todayList.isEmpty()) {
                todayContainer.setVisibility(View.GONE);
            } else {
                todayContainer.setVisibility(View.VISIBLE);
                loadListView(taskListToday, todayList);
            }

            if (tomorrowList.isEmpty()) {
                tomorrowContainer.setVisibility(View.GONE);
            } else {
                tomorrowContainer.setVisibility(View.VISIBLE);
                loadListView(taskListTomorrow, tomorrowList);
            }

            if (upcomingList.isEmpty()) {
                otherContainer.setVisibility(View.GONE);
            } else {
                otherContainer.setVisibility(View.VISIBLE);
                loadListView(taskListUpcoming, upcomingList);
            }
        }
    }


    public void loadDataList(Cursor cursor, ArrayList<HashMap<String, String>> dataList) {
        if (cursor != null) {

            cursor.moveToFirst();
            String finalDate;
            while (cursor.isAfterLast() == false) {

                HashMap<String, String> mapToday = new HashMap<String, String>();
                mapToday.put("id", cursor.getString(0).toString());
                mapToday.put("task", cursor.getString(1).toString());
                mapToday.put("date", cursor.getString(2).toString());
                mapToday.put("status", cursor.getString(3).toString());
                dataList.add(mapToday);
                Log.e("date",cursor.getString(2).toString());
                String sDate1=cursor.getString(2).toString();
                try {
                    String[] a1=sDate1.split(" ");
                    String[] b1=a1[1].split(":");
                    int hour=Integer.parseInt(b1[0])-1;
                    Log.e("b1", String.valueOf(hour));
                    //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Log.e("after",a1[0]+" "+hour+":"+b1[1]);
                    finalDate=a1[0]+" "+hour+":"+b1[1];
                    moDelayedTask = new DelayedTask(getApplicationContext());

                    Date odate1 = new Date();
                    Calendar calobj = Calendar.getInstance();
                    Date oDateNow= calobj.getTime();
                    odate1.setTime(oDateNow.getTime());
                    odate1.setHours(hour);

                    odate1.setMinutes(Integer.valueOf(b1[1]));
                    Log.e("lmao", finalDate);

                    moDelayedTask.SetEmailForTask("tanmaychowhan@gmail.com","Reminder: " + cursor.getString(1).toString(),"A reminder for your scheduled task");
                    moDelayedTask.AddTask(odate1,2);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                cursor.moveToNext();
            }
        }
    }

    public void loadListView(NoScrollListView listView, final ArrayList<HashMap<String, String>> dataList) {
        ListTaskAdapter adapter = new ListTaskAdapter(this, dataList, mydb);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getApplicationContext(), AddModifyTask.class);
                i.putExtra("isModify", true);
                i.putExtra("id", dataList.get(+position).get("id"));
                try {
                   // moDelayedTask = new DelayedTask(getApplicationContext());
                   // Date odate = new Date();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                startActivity(i);
            }
        });
    }
}

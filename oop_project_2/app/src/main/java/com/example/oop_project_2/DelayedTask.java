package com.example.oop_project_2;
import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

class DelayedTask {
    Timer timer;
    boolean mbForLongTimeCameraHeatCheck = false;
    RemindTask mRemindTask = null;
    Context moContext =null;

    String mszEmailID ="tanmaychowhan@gmail.com";
    String mszSubject = "Test";
    String mszMessage = "My message";

    public DelayedTask(Context oContext) throws Exception {
        try {
            moContext = oContext;
            timer = new Timer();
          //  Log.d("Task", "New Task " + String.valueOf(seconds));
           // mRemindTask = new RemindTask();
            //timer.schedule(mRemindTask, seconds * 1000);
            //timer.schedule(mRemindTask, oDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw (ex);

        }
    }
    public void StopTimer()
    {
        timer.cancel();
    }

    void SetEmailForTask(String szEmailID ,
                         String szSubject,
                         String szMessage)
    {
        mszEmailID = szEmailID;
        mszSubject = szSubject;
        mszMessage = szMessage;


    }


   public void  AddTask(Date odate, int iId)
    {
        RemindTask oRemindTask = new RemindTask();
        oRemindTask.iUniqueID =iId;
        oRemindTask.ocontext =moContext;
        oRemindTask.szMessage = mszEmailID;
        oRemindTask.szSubject = mszSubject;
        oRemindTask.szMessage =mszMessage;
        timer.schedule(oRemindTask,odate);
    }

    public void ResetTimer(int seconds) throws Exception {
        try {
            mRemindTask.cancel();
            mRemindTask = new RemindTask();
           // timer.schedule(mRemindTask, seconds * 1000);
            //Log.d("Task", "Task Canceled and rescheduled");
        } catch (Exception ex) {
            ex.printStackTrace();

            throw (ex);

        }
    }

    class RemindTask extends TimerTask {
        int iUniqueID =0;
        Context ocontext = null;
        com.example.oop_project_2.JavaMailAPI javaMailAPI =null;
        String szEmailID ="tanmaychowhan@gmail.com";
        String szSubject = "Test";
        String szMessage = "My message";

        public void run() {
            try {
                Log.d("Task", "Task Completed for " + iUniqueID);

                // Send Email'
                javaMailAPI = new com.example.oop_project_2.JavaMailAPI(ocontext, szEmailID, szSubject, szMessage);
                javaMailAPI.execute();
                // Send Sms

                //timer.cancel(); //Terminate the timer thread

            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
    }


}

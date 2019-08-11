package com.videostatusiranna.utilities;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.videostatusiranna.R;
import com.videostatusiranna.activity.DetailActivity;
import com.videostatusiranna.activity.HomeActivity;



public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private String title;
    private String message;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "MyOrderPage Body: " + remoteMessage.getNotification().getBody());
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            String msg = remoteMessage.getData().toString().replace("{message=","");
            msg = msg.substring(0,msg.length()-1);
            Log.e(TAG, "push json: " +msg);


            try {
                JSONObject json = new JSONObject(msg);
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }



    private void handleDataMessage(JSONObject json) {



        try {
            json = new JSONObject(json.toString());
            String message = json.optString("message");
            title = json.optString("title");

            JSONObject jsonObject = json.optJSONObject("video_details");
            if(jsonObject!=null) {

                String id = jsonObject.optString("id");
                String video_category = jsonObject.optString("video_category");
                String video_subcategory = jsonObject.optString("video_subcategory");
                String cat_id = jsonObject.optString("cat_id");
                String subcat_id = jsonObject.optString("subcat_id");
                String video_title = jsonObject.optString("video_title");
                String video = jsonObject.optString("video");
                String image = jsonObject.optString("image");
                String view = jsonObject.optString("view");
                String download = jsonObject.optString("download");


                Intent resultIntent = new Intent(getApplicationContext(), DetailActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("videoTitle",video_title );
                resultIntent.putExtra("videoId", video);
                resultIntent.putExtra("videoCategory",video_category );
                resultIntent.putExtra("videoSubCategory",video_subcategory);
                resultIntent.putExtra("videoSubCategoryId",subcat_id);
                resultIntent.putExtra("videoCategoryId",cat_id);
                resultIntent.putExtra("videoDownload",download );
                resultIntent.putExtra("videoImage",image);
                resultIntent.putExtra("videoView",view );
                resultIntent.putExtra("id", id);
          //      resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(DetailActivity.class);
                stackBuilder.addNextIntent(resultIntent);


              //  PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                resultIntent.putExtra("message", message);
                showNotificationWithImage(getApplicationContext(), title, message,image,pendingIntent);
            }
            else {

                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
                showNotificationMessage(title, message,pendingIntent);
            }



        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void showNotificationWithImage(Context applicationContext, String title, String message, String image, PendingIntent resultIntent) {

        Bitmap bitmap = getBitmapFromURL(image);

        Notification notif = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            notif = new Notification.Builder(applicationContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_play)
                    .setColor(Color.BLACK)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                    .setContentIntent(resultIntent)
                    .setAutoCancel(true)
                    .setSubText(message)
              //      .setContentText(message)
                    .build();
        }
        else {
            notif = new Notification.Builder(applicationContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_play)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                    .setContentIntent(resultIntent)
                    .setAutoCancel(true)
                    .setSubText(message)
                    //      .setContentText(message)
                    .build();

        }
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notif);

    }

    private void showNotificationMessage(String title, String message, PendingIntent resultIntent) {
        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_play);
        mBuilder.setContentTitle(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(Color.BLACK);
        }
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        //    mBuilder.setContentText(message);
       mBuilder.setStyle(new Notification.BigTextStyle().bigText(message));
       mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultIntent);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);

// notificationId is a unique int for each notification that you must define
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(getString(R.string.link)+"images/thumbnail/" + strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}

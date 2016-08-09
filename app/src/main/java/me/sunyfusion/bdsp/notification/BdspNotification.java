package me.sunyfusion.bdsp.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import me.sunyfusion.bdsp.R;

public class BdspNotification {

    public static Notification notify(final Context context,
                              final String exampleString, final int number) {
        final Resources res = context.getResources();

        final Bitmap picture = BitmapFactory.decodeResource(res, R.mipmap.logo);


        final String ticker = exampleString;
        final String title = res.getString(
                R.string.bdsp_notification_title_template, exampleString);
        final String text = res.getString(
                R.string.bdsp_notification_placeholder_text_template, exampleString);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(title)
                .setContentText(text)

                // All fields below this line are optional.

                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(picture)
                .setTicker(ticker)

                .setNumber(number)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                .setOngoing(true);
        return builder.build();
    }
}

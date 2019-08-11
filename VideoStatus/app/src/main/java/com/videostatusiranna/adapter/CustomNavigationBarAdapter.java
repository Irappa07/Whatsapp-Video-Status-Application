package com.videostatusiranna.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.app.AlertDialog;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.videostatusiranna.activity.CategoriesActivity;
import com.videostatusiranna.activity.DownloadsActivity;
import com.videostatusiranna.activity.HomeActivity;
import com.videostatusiranna.activity.PopularActivity;
import com.videostatusiranna.activity.SettingActivity;
import com.videostatusiranna.R;

import static com.videostatusiranna.activity.HomeActivity.prefName;


public class CustomNavigationBarAdapter extends BaseAdapter {
    private final String[] navItems;
    private final LayoutInflater layoutInflater;
    private final Context mContext;
    private final DrawerLayout drawerLayout;


    public CustomNavigationBarAdapter(Context context, DrawerLayout drawerLayout) {
        navItems = new String[]{context.getString(R.string.nav_home), context.getString(R.string.nav_popular), context.getString(R.string.nav_categories), context.getString(R.string.nav_downloaded), context.getString(R.string.nav_setting), context.getString(R.string.nav_rate), context.getString(R.string.nav_share), context.getString(R.string.nav_request)};
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        // mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().remove("selectedItemNav").apply();
        this.drawerLayout = drawerLayout;
    }


    @Override
    public int getCount() {
        return navItems.length;
    }

    @Override
    public Object getItem(int position) {
        return navItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View currentView = convertView;

        if (currentView == null) {
            currentView = layoutInflater.inflate(R.layout.cell_nav_drawer, parent, false);
        }

        TextView navItem = currentView.findViewById(R.id.navItem);
        final ImageView navItemImage = currentView.findViewById(R.id.iv_nav_background);
        navItem.setText(navItems[position]);
        navItem.setTypeface(HomeActivity.tf_main_medium);

        Log.e(mContext.getClass().getName(), "Position" + position + "SharedPrefPosition" + mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE).getInt("selectedItemNav", -2));
        if (position == mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE).getInt("selectedItemNav", -2))
            navItemImage.setImageResource(R.drawable.btn_selectsidemenu);
        else navItemImage.setImageResource(android.R.color.transparent);


        currentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                SharedPreferences.Editor editor = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();

                switch (position) {

                    case 0:
                        intent = new Intent(mContext, HomeActivity.class);
                        saveClickEvent(editor, position);
                        break;

                    case 1:
                        intent = new Intent(mContext, PopularActivity.class);
                        saveClickEvent(editor, position);
                        break;
                    case 2:
                        intent = new Intent(mContext, CategoriesActivity.class);
                        saveClickEvent(editor, position);
                        break;
                    case 3:
                        intent = new Intent(mContext, DownloadsActivity.class);
                        saveClickEvent(editor, position);
                        break;
                    case 4:
                        intent = new Intent(mContext, SettingActivity.class);
                        saveClickEvent(editor, position);
                        break;
                    case 5:
                        rateUs();
                        saveClickEvent(editor, position);
                        break;
                    case 6:
                        shareApp();
                        saveClickEvent(editor, position);
                        break;
                    case 7:
                        requestVideo();
                        saveClickEvent(editor, position);
                        break;


                }
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                    if (drawerLayout.isDrawerOpen(Gravity.START))
                        drawerLayout.closeDrawer(Gravity.START);
                }
            }
        });


        return currentView;
    }

    private void requestVideo() {
        String recipient = mContext.getString(R.string.email);
        String subject = mContext.getString(R.string.txt_request_video);
        @SuppressWarnings("unused")
        String body = "";
        String[] recipients = {recipient};
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, recipients);
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        try {
            email.setPackage("com.google.android.gm");
            if (isAvailable(mContext, email))
                mContext.startActivity(email);

            else {
                email.setPackage("com.android.email");
                if (isAvailable(mContext, email))
                    mContext.startActivity(email);
            }
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(mContext, R.string.txt_no_email_client, Toast.LENGTH_LONG).show();

        }
    }

    private void shareApp() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.playStore_address) + mContext.getPackageName());
        mContext.startActivity(Intent.createChooser(share, "Share link!"));

    }

    private void rateUs() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(R.string.request_rateus);

        //   alert.setIcon(R.drawable.book);
        alert.setMessage(mContext.getString(R.string.txt_rate_part1) + mContext.getString(R.string.app_name) + mContext.getString(R.string.txt_rate_part2));
        alert.setPositiveButton(R.string.txt_rate_it, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String url = mContext.getString(R.string.playStore_address) + mContext.getPackageName();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mContext.startActivity(i);
            }
        });
        alert.setNegativeButton(R.string.txt_not_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();

    }

    private void saveClickEvent(SharedPreferences.Editor editor, int position) {
        editor.putInt("selectedItemNav", position);
        editor.apply();
    }

    private static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}

package com.example.iris.login1;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by shivenmian on 13/03/18.
 */

public class LoginTimeComparator implements Comparator<UserInfo>{

    public static final String TAG = "LoginComparator";
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);


    @Override
    public int compare(UserInfo lhs, UserInfo rhs) {
        try {
            return dateFormat.parse(lhs.getLastLoginTime()).compareTo(dateFormat.parse(rhs.getLastLoginTime()));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return 0;
        }
    }
}

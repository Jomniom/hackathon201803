package com.superaligator.konferencja.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.superaligator.konferencja.BuildConfig;
import com.superaligator.konferencja.Config;
import com.superaligator.konferencja.dbmodels.Event;
import com.superaligator.konferencja.dbmodels.ChatQuestion;
import com.superaligator.konferencja.dbmodels.Quiz;
import com.superaligator.konferencja.dbmodels.QuizQuestion;
import com.superaligator.konferencja.dbmodels.QuizQuestionAnswer;
import com.superaligator.konferencja.models.Form;
import com.superaligator.konferencja.models.FormAnswer;
import com.superaligator.konferencja.models.FormQuestion;

import java.io.File;

public class UserManager {
    private static final String PREFS_USER_TOKEN = "dfgsdfgsd";
    private Context ctx;
    private final String PREFS_API_KEY = "prefs_api_key";
    private final String PREFS_EMAIL = "prefs_email";
    private final String PREFS_USER_ID = "prefs_user_id";
    private String email = null;
    private String apiKey = null;
    private String userId = null;
    private String id_token = null;
    private static UserManager instance = null;

    public UserManager(Context ctx) {
        this.ctx = ctx.getApplicationContext();
        readSharedPrefs();
    }

    public static UserManager init(Context ctx) {
        if (instance == null) {
            instance = new UserManager(ctx);
        }
        return instance;
    }

    public static UserManager getInstance() {
        return instance;
    }

    private void readSharedPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        email = prefs.getString(PREFS_EMAIL, null);
        // apiKey = prefs.getString(PREFS_API_KEY, null);
        userId = prefs.getString(PREFS_USER_ID, null);
        id_token = prefs.getString(PREFS_USER_TOKEN, "braak");
    }

    public boolean userLoggedIn(String apiKey, String email, String userId, String id_token) {
       /* if (apiKey.length() == 0 || email.length() == 0 || userId.length() == 0) {
            return false;
        }*/
//       if (id_token == null || id_token.length() == 0) {
//           return false;
//       }
        // UserManager.this.apiKey = apiKey;
        UserManager.this.email = email;
        UserManager.this.userId = userId;
        UserManager.this.id_token = id_token;
        SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        // sp.putString(PREFS_API_KEY, apiKey);
        sp.putString(PREFS_EMAIL, email);
        sp.putString(PREFS_USER_ID, userId);
        sp.putString(PREFS_USER_TOKEN, id_token);
        sp.apply();
        switchDatabase(userId);
        return true;
    }

    private void switchDatabase(String userId) {
        if (isDbInitialize)
            ActiveAndroid.dispose();

        String dbName = userId + ".db";
        if (BuildConfig.DEBUG) {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            dbName = path.getPath() + "/" + dbName;
        }

        Configuration dbConfiguration = new Configuration.Builder(ctx)
                .setModelClasses(Event.class, ChatQuestion.class, Form.class, FormQuestion.class, FormAnswer.class,
                        Quiz.class, QuizQuestion.class, QuizQuestionAnswer.class)//manifest
                .setDatabaseVersion(Config.BD_VERSION)
                .setDatabaseName(dbName).create();
        ActiveAndroid.initialize(dbConfiguration);
        isDbInitialize = true;

    }

    private boolean isDbInitialize = false;

    public void initDatabase() {
        if (!isLoggedIn()) {
            return;
        }
        switchDatabase(getUserId());
    }

    public boolean isLoggedIn() {
        return (email != null && email.length() > 0 && userId != null && userId.length() > 0);
    }

    public boolean logout() {
        SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        sp.remove(PREFS_API_KEY);
        sp.remove(PREFS_EMAIL);
        sp.remove(PREFS_USER_ID);
        sp.remove(PREFS_USER_TOKEN);

        if (sp.commit()) {
            //apiKey = null;
            email = null;
            userId = null;
            //sendBroadcastLogout();
            return true;
        }
        return false;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getId_token() {
        if (id_token == null)
            return "nie ma?";
        return id_token;
    }
}

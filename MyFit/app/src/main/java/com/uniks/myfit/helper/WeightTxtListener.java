package com.uniks.myfit.helper;

import android.text.Editable;
import android.text.TextWatcher;

import com.uniks.myfit.database.AppDatabase;
import com.uniks.myfit.model.User;

/**
 * helper class to save user weight, when user changes value in UI
 */
public class WeightTxtListener implements TextWatcher {

    private AppDatabase db;
    private User user;

    private CharSequence changedTxt = "";

    public WeightTxtListener(AppDatabase db, User user) {
        this.db = db;
        this.user = user;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        changedTxt = s;
    }

    @Override
    public void afterTextChanged(Editable s) {

        // user is done change the weight -> save the new weight
        if (changedTxt.length() != 0) {
            user.setWeight(Integer.parseInt(changedTxt.toString()));
            db.userDao().updateUser(user);
        }
    }
}

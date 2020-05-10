package com.uniks.myfit.helper;

import android.content.Intent;
import android.view.View;

import com.uniks.myfit.DetailActivity;
import com.uniks.myfit.MainActivity;
import com.uniks.myfit.controller.CardsRecyclerViewAdapter;

/**
 * helper class to open detailView from click on exercise card.
 */
public class DoneExerciseCardClickListener implements CardsRecyclerViewAdapter.MyClickListener {

    private MainActivity mainActivity;

    public DoneExerciseCardClickListener(MainActivity mainActivity) {

        this.mainActivity = mainActivity;
    }

    @Override
    public void onItemClick(int position, View v) {
        // change to DetailActivity and also send index of Exercise

        Intent showDetails = new Intent(v.getContext(), DetailActivity.class);
        showDetails.putExtra("POSITION", position);
        mainActivity.startActivity(showDetails);

    }
}

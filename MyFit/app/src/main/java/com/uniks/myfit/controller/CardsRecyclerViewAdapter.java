package com.uniks.myfit.controller;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.uniks.myfit.MainActivity;
import com.uniks.myfit.R;
import com.uniks.myfit.model.SportExercise;
import com.uniks.myfit.helper.DeleteButtonHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * The Adapter to connect the exercise data to the CardView
 */
public class CardsRecyclerViewAdapter extends RecyclerView.Adapter<CardsRecyclerViewAdapter.DataObjectHolder> {

    private ArrayList<SportExercise> sportExercises;
    private static MyClickListener myClickListener;
    private MainActivity mainActivity;

    public CardsRecyclerViewAdapter(ArrayList<SportExercise> sportExercises, MainActivity mainActivity) {
        this.sportExercises = sportExercises;
        this.mainActivity = mainActivity;
    }

    public void setSportExercises(ArrayList<SportExercise> sportExercises) {
        this.sportExercises = sportExercises;
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView exerciseIcon;
        TextView date;
        TextView time;
        TextView duration;
        ImageButton deleteButton;

        public DataObjectHolder(View itemView) {
            super(itemView);

            exerciseIcon = itemView.findViewById(R.id.fragment_exercise_icon);
            date = itemView.findViewById(R.id.sport_exercise_fragment_date);
            time = itemView.findViewById(R.id.sport_exercise_fragment_time);
            duration = itemView.findViewById(R.id.sport_exercise_fragment_duration);
            deleteButton = itemView.findViewById(R.id.delete_button);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_sport_exercise, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        // set icon depending on exercise mode
        switch (sportExercises.get(position).getMode()) {
            case 0:
                holder.exerciseIcon.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.ic_run_black, null));
                break;
            case 1:
                holder.exerciseIcon.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.ic_bike_black, null));
                break;
            case 2:
                holder.exerciseIcon.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.ic_pushups_black, null));
                break;
            case 3:
                holder.exerciseIcon.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.ic_situps_black, null));
                break;
        }

        // set date, time and duration in the Card
        holder.date.setText(getDateString(sportExercises.get(position).getDate()));
        holder.time.setText(getTimeString(sportExercises.get(position).getDate()));
        holder.duration.setText(String.valueOf(sportExercises.get(position).getTripTime()));

        holder.deleteButton.setOnClickListener(new DeleteButtonHelper(mainActivity, sportExercises.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return sportExercises.size();
    }

    /**
     * the click listener for the card
     */
    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    /**
     * formats the given date to dd.MM.yyyy
     *
     * @param date the date to format
     * @return the formatted date
     */
    private String getDateString(Date date) {

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

        return df.format(date);

    }

    /**
     * formats the given date to time format HH:mm
     *
     * @param date the date to format
     * @return the formatted time
     */
    private String getTimeString(Date date) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.GERMANY);

        return df.format(date);

    }
}

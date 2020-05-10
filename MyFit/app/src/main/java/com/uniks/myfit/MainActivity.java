package com.uniks.myfit;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.uniks.myfit.controller.CardsRecyclerViewAdapter;
import com.uniks.myfit.database.AppDatabase;
import com.uniks.myfit.model.SportExercise;
import com.uniks.myfit.model.User;
import com.uniks.myfit.helper.DoneExerciseCardClickListener;
import com.uniks.myfit.helper.StartButtonHelper;
import com.uniks.myfit.helper.WeightTxtListener;

import java.util.ArrayList;
import java.util.List;

/**
 * the main activity of this app.
 */
public class MainActivity extends AppCompatActivity {
    static final String DATABASE_NAME = "myFitDB";

    public AppDatabase db;
    public User user;

    public CardsRecyclerViewAdapter cardsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // model
        // setup the database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();

        // if there is no user, create one
        List<User> users = db.userDao().getAll();

        if (users == null || users.isEmpty()) {
            // empty table so create an dummy user
            User newUser = new User();
            newUser.setWeight(65);
            db.userDao().insert(newUser);
            users = db.userDao().getAll();
        }
        user = users.get(0); // for this small project there is only one user

        // view
        setContentView(R.layout.activity_main);

        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText weightTxt = findViewById(R.id.input_weight);
        weightTxt.setText(String.valueOf(user.getWeight()), TextView.BufferType.EDITABLE);
        weightTxt.addTextChangedListener(new WeightTxtListener(db, user));

        // set listener to each button, to start tracking according to mode
        FloatingActionButton startRunning = findViewById(R.id.add_exercise_running);
        setStartListener(0, startRunning);

        FloatingActionButton startCycling = findViewById(R.id.add_exercise_cycling);
        setStartListener(1, startCycling);

        FloatingActionButton startPushups = findViewById(R.id.add_exercise_pushups);
        setStartListener(2, startPushups);

        FloatingActionButton startSitups = findViewById(R.id.add_exercise_situps);
        setStartListener(3, startSitups);

        // set cards of completed tours
        RecyclerView cardRecyclerView = findViewById(R.id.cards_recycler_view);
        cardRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager cardsLayoutManager = new LinearLayoutManager(this);
        cardRecyclerView.setLayoutManager(cardsLayoutManager);
        cardsAdapter = new CardsRecyclerViewAdapter(getDataSet(), this);
        cardRecyclerView.setAdapter(cardsAdapter);

    }

    /**
     * gets all exercises for user from db
     *
     * @return all exercises for current user
     */
    public ArrayList<SportExercise> getDataSet() {

        List<SportExercise> allExercises = db.sportExerciseDao().getAllFromUser(user.getUid());

        return new ArrayList<>(allExercises);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // reload to show done exercises
        cardsAdapter.setSportExercises(getDataSet());
        cardsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cardsAdapter.setOnItemClickListener(new DoneExerciseCardClickListener(this));
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private void setStartListener(int modeCode, FloatingActionButton startButton) {
        startButton.setOnClickListener(new StartButtonHelper(modeCode, this));
    }

}

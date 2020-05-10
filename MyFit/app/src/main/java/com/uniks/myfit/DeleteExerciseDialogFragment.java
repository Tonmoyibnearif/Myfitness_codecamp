package com.uniks.myfit;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.uniks.myfit.model.SportExercise;

/**
 * the dialog that asks user confirmation to delete exercise entry.
 */
public class DeleteExerciseDialogFragment extends DialogFragment {

    private long exerciseId;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        super.onCreateDialog(savedInstanceState);
        this.exerciseId = getArguments().getLong("index");

        builder.setMessage(R.string.dialog_delete)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ok, delete entry

                        MainActivity mainActivity = (MainActivity) getActivity();

                        SportExercise exercise = mainActivity.db.sportExerciseDao().getExerciseById(exerciseId).get(0);

                        mainActivity.db.sportExerciseDao().deleteExercise(exercise); //exercise.getId()

                        // refresh mainActivity
                        mainActivity.cardsAdapter.setSportExercises(mainActivity.getDataSet());
                        mainActivity.cardsAdapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton(R.string.keep, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // NO! don't do it!

                        dialog.cancel();
                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }


}

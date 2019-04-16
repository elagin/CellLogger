package ru.crew4dev.celllogger.gui.dialogs;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import ru.crew4dev.celllogger.gui.modeles.interfaces.Delete;

public class DeleteConfirm {

    private static AlertDialog dialog;

    public static void showConfirm(final Context context, Delete holder, long sessionId, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("Да",
                (dialog, id) -> {
                    holder.positive(sessionId);
                });
        builder.setNegativeButton("Нет",
                (dialog, id) -> {
                    holder.negative();
                });
        try {
            dialog = builder.show();
        } catch (Exception e) {
            //Log.d(TAG, "Exception", e);
        }
    }
}

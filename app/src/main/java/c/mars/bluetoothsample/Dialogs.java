package c.mars.bluetoothsample;

import android.app.AlertDialog;
import android.content.Context;

import javax.inject.Inject;

/**
 * Created by Constantine Mars on 3/10/15.
 */
public class Dialogs {
    private Context c;

    @Inject
    public Dialogs(Context c) {
        this.c = c;
    }

    public void showError(String m) {
        new AlertDialog.Builder(c)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Alert")
                .setMessage(m)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }
}

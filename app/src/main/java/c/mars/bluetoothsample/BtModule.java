package c.mars.bluetoothsample;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Constantine Mars on 3/10/15.
 */
@Module(injects = MainActivity.class, library = true)
public class BtModule {
    private Activity a;
    private Display display;

    public BtModule(Activity a, Display display) {
        this.a = a;
        this.display = display;
    }

    @Provides
    BtConnector provideBtConnector(Dialogs d) {
        return new BtConnector(a, d, display);
    }

    @Provides
    Dialogs provideDialogs() {
        return new Dialogs(a);
    }
}

package c.mars.bluetoothsample;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import a.mars.bluetoothsample.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dagger.ObjectGraph;
import timber.log.Timber;


public class MainActivity extends ActionBarActivity {

    @Inject
    BtConnector bt;

    @InjectView(R.id.log)
    TextView log;

    @InjectView(R.id.discover)
    Button discover;

    private ObjectGraph og;
    private Display display = new Display() {
        @Override
        public void show(String m) {
            log.setText(m);
        }

        @Override
        public void add(String m) {
            log.append(m);
        }
    };

    @OnClick(R.id.discover)
    void discover() {
        if (!bt.isDiscoveryActive()) {
            bt.discover();
            discover.setText("cancel discovery");
        } else {
            bt.cancelDiscovery();
            discover.setText("discover");
        }
    }

    @OnClick(R.id.paired)
    void paired() {
        bt.paired();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        Timber.plant(new Timber.DebugTree());


        og = ObjectGraph.create(new BtModule(this, display));
        og.inject(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        bt.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}

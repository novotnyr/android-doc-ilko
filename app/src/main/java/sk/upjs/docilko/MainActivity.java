package sk.upjs.docilko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements OnCityClickListener {

    public static final String NO_NAME = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isSinglePanelMode()) {
            MasterFragment masterFragment = new MasterFragment();
            masterFragment.setOnCityClickListener(this);

            getFragmentManager()
                    .beginTransaction()
                        .replace(R.id.activity_main, masterFragment)
                    .commit();

        } else {
            MasterFragment masterFragment = (MasterFragment) getFragmentManager().findFragmentById(R.id.masterFragment);
            masterFragment.setOnCityClickListener(this);
        }
    }

    private boolean isSinglePanelMode() {
        return findViewById(R.id.activity_main) != null;
    }

    @Override
    public void onCityClicked(String city) {
        if (isSinglePanelMode()) {
            DetailFragment detailFragment = DetailFragment.newDetailFragment(getTemperatureFromCity(city));
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_main, detailFragment)
                    .addToBackStack(NO_NAME)
                    .commit();

        } else {
            DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.detailFragment);
            detailFragment.setTemperature(getTemperatureFromCity(city));
        }
    }

    private int getTemperatureFromCity(String city) {
        return -city.hashCode();
    }
}

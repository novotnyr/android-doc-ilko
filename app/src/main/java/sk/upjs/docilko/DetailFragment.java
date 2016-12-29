package sk.upjs.docilko;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailFragment extends Fragment {

    public static final String TEMPERATURE_BUNDLE_KEY = "temperature";

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle arguments = getArguments();
        if(arguments != null) {
            int temperature = arguments.getInt(TEMPERATURE_BUNDLE_KEY);
            setTemperature(temperature);
        }
    }

    public void setTemperature(int temperature) {
        TextView textView = (TextView) getView().findViewById(R.id.temperatureTextView);
        textView.setText(Integer.toString(temperature));
    }

    public static DetailFragment newDetailFragment(int temperature) {
        DetailFragment fragment = new DetailFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(TEMPERATURE_BUNDLE_KEY, temperature);

        fragment.setArguments(arguments);

        return fragment;
    }

}

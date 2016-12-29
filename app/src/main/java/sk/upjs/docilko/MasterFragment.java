package sk.upjs.docilko;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class MasterFragment extends Fragment {

    private OnCityClickListener onCityClickListener;

    public MasterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_master, container, false);
        ListView cityListView = (ListView) view.findViewById(R.id.cityListView);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onCityListViewItemClickListener(adapterView, view, i, l);
            }
        });

        return view;
    }

    private void onCityListViewItemClickListener(AdapterView<?> adapterView, View view, int i, long l) {
        String city = (String) adapterView.getAdapter().getItem(i);
        onCityClickListener.onCityClicked(city);
    }

    public void setOnCityClickListener(OnCityClickListener onCityClickListener) {
        this.onCityClickListener = onCityClickListener;
    }
}

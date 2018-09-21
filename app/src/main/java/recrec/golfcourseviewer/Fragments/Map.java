package recrec.golfcourseviewer.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import recrec.golfcourseviewer.Entity.CourseViewModel;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.Response.Hole;


public class Map extends Fragment {

    private FloatingActionButton fab;
    private TextView distanceToHole;
    CourseViewModel courseViewModel;
    private TextView weather;



    public Map() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        final CourseViewModel vm = ViewModelProviders.of(getActivity())
                .get(CourseViewModel.class);
        courseViewModel = ViewModelProviders.of(getActivity())
                .get(CourseViewModel.class);
        fab = view.findViewById(R.id.next_fab);
        distanceToHole = view.findViewById(R.id.txt_distance);

        courseViewModel.distanceToHole.observe(getActivity(), new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double aDouble) {
                distanceToHole.setText(String.format(Locale.UK,"%3.2fm",
                        aDouble));
            }
        });
        weather = view.findViewById(R.id.txt_weather);
        courseViewModel.weatherData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(!weather.getText().equals(s)){
                    weather.setText(s);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curId = vm.holeID.getValue();
                List<Hole> availableList = vm.holes.getValue();
                int index = 0;
                if(curId != null && availableList != null){
                    for(int i = 0; i < availableList.size(); i++){
                        if(curId.equals(availableList.get(i).getHoleId())){
                            index = i +1;
                            break;
                        }
                    }
                    if(index == availableList.size()){
                        index = 0;
                    }
                    curId = availableList.get(index).getHoleId();
                    vm.holeID.setValue(curId);
                }
            }
        });

        vm.holeID.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                fab.setVisibility(View.VISIBLE);
                distanceToHole.setVisibility(View.VISIBLE);

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fab.setVisibility(View.INVISIBLE);
        distanceToHole.setVisibility(View.INVISIBLE);
    }

}

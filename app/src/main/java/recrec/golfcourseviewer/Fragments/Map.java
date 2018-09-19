package recrec.golfcourseviewer.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import recrec.golfcourseviewer.Entity.CourseViewModel;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.Response.Hole;


public class Map extends Fragment {

    private FloatingActionButton fab;


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
        final CourseViewModel vm = ViewModelProviders.of(getActivity()).get(CourseViewModel.class);
        fab = (FloatingActionButton) view.findViewById(R.id.next_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curId = vm.holeID.getValue();
                List<Hole> availableList = vm.holes.getValue();
                int index = 0;
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
        });

        vm.holeID.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                fab.setVisibility(View.VISIBLE);
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
    }

}

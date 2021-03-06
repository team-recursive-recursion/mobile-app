/*
 * Filename: HolesListFragment.java
 * Author: Team Recursive Recursion
 * Class: HolesListFragment
 *       Displays a list of al the holes from the current course.
 * */
package recrec.golfcourseviewer.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import recrec.golfcourseviewer.Adapter.MyHolesListRecyclerViewAdapter;
import recrec.golfcourseviewer.Entity.CourseViewModel;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.ApiClientRF;
import recrec.golfcourseviewer.Requests.Response.Zone;
import recrec.golfcourseviewer.Requests.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HolesListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    public HolesListFragment() {
    }


    @SuppressWarnings("unused")
    public static HolesListFragment newInstance(int columnCount) {
        HolesListFragment fragment = new HolesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    MyHolesListRecyclerViewAdapter adapter;
    CourseViewModel courseViewModel;
    List<Zone> holesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_holeslist_list,
                container, false);
        courseViewModel = ViewModelProviders.of(getActivity())
                .get(CourseViewModel.class);
        subscribe();

        ApiClientRF clientRF = ServiceGenerator.getService();
        Call<Zone> call = clientRF.getZones(courseViewModel
                .courseID.getValue());

        call.enqueue(new Callback<Zone>() {
            @Override
            public void onResponse(Call<Zone> call, Response<Zone> response) {
                if(response.body() != null){
                    courseViewModel.holes.setValue(response.body()
                            .getInnerZones());
                    courseViewModel.courseZone.setValue(response.body());
                    Log.d("Hole", response.body().getZoneName());
                }
            }

            @Override
            public void onFailure(Call<Zone> call, Throwable t) {
                Log.d("Hole", "Failed: " + t.getMessage());
            }
        });

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context,
                        mColumnCount));
            }
            adapter = new MyHolesListRecyclerViewAdapter(holesList,
                    courseViewModel, getActivity().getApplicationContext());
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

/*----------------------------------------------------------------------------
subscribe() : void
    Adds values to the adapter and notifies the change so that the
    recycler view can be updated.
-----------------------------------------------------------------------------*/
    private void subscribe(){
        courseViewModel.holes.observe(this, new Observer<List<Zone>>() {
            @Override
            public void onChanged(@Nullable List<Zone> holes) {
                adapter.setmValues(holes);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

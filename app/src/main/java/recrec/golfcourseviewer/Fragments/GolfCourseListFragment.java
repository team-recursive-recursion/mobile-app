/*
 * Filename: GolfCourseListFragment.java
 * Author: Team Recursive Recursion
 * Class: GolfCourseListFragment
 *       Displays a list of al the golf courses.
 * */
package recrec.golfcourseviewer.Fragments;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import recrec.golfcourseviewer.Activity.MainActivity;
import recrec.golfcourseviewer.Adapter.MyGolfCourseListRecyclerViewAdapter;
import recrec.golfcourseviewer.Entity.CourseViewModel;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.ApiClientRF;
import recrec.golfcourseviewer.Requests.Response.Zone;
import recrec.golfcourseviewer.Requests.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GolfCourseListFragment extends Fragment
        implements SearchView.OnQueryTextListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public GolfCourseListFragment() {
    }

    @SuppressWarnings("unused")
    public static GolfCourseListFragment newInstance(int columnCount) {
        GolfCourseListFragment fragment = new GolfCourseListFragment();
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

    MyGolfCourseListRecyclerViewAdapter adapter;
    CourseViewModel courseViewModel;
    ArrayList<Zone> zoneModels = new ArrayList<>();
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_golfcourselist_list,
                container, false);
        courseViewModel = ViewModelProviders.of(getActivity())
                .get(CourseViewModel.class);
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);
        mFusedLocationClient = LocationServices
                .getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
        }
        final double[] latLon = {0, 0};

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(),
                        new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latLon[0] = location.getLatitude();
                            latLon[1] = location.getLongitude();
                        }
                    }
                });

        ApiClientRF client = ServiceGenerator.getService();
        Call<List<Zone>> call = client.getZones();

        call.enqueue(new Callback<List<Zone>>() {
            @Override
            public void onResponse(Call<List<Zone>> call,
                                   Response<List<Zone>> response) {
                if (response.body()!= null && !response.body().isEmpty()) {
                    Log.d("CoursesCall", response.body()
                            .get(0).getZoneName());
                    courseViewModel.courses.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Zone>> call, Throwable t) {
                Log.d("CoursesCall", "Call to getCourse failed: "
                        + t.getMessage());
            }
        });

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(
                    new GridLayoutManager(context, mColumnCount));
        }
        adapter = new MyGolfCourseListRecyclerViewAdapter(zoneModels,
                courseViewModel);
        recyclerView.setAdapter(adapter);

        subscribeAdapter();

        return view;
    }
/*----------------------------------------------------------------------------
    subscribeAdapter() : void
        Adds values to the adapter and notifies the change so that the
        recycler view can be updated.
-----------------------------------------------------------------------------*/
    private void subscribeAdapter() {
        courseViewModel.courses.observe(this,
                new Observer<List<Zone>>() {
            @Override
            public void onChanged(@Nullable List<Zone> courses) {
                Log.d("Hey", courses.get(0).getZoneName());
                if (adapter != null) {
                    adapter.mValues = courses;
                    adapter.mFilter = courses;
                    adapter.notifyDataSetChanged();
                }
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

    @Override
    public boolean onQueryTextSubmit(String query) {
       // this.adapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        this.adapter.getFilter().filter(newText);
        return false;
    }
}

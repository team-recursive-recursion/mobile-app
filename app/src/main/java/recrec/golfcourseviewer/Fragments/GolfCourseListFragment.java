/*
 * Filename: GolfCourseListFragment.java
 * Author: Team Recursive Recursion
 * Class: GolfCourseListFragment
 *       Displays a list of al the golf courses.
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import recrec.golfcourseviewer.Activity.MainActivity;
import recrec.golfcourseviewer.Adapter.MyGolfCourseListRecyclerViewAdapter;
import recrec.golfcourseviewer.Entity.GolfCourseListViewModel;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.db.AppDatabase;
import recrec.golfcourseviewer.db.Models.GolfCourseModel;

public class GolfCourseListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    public MainActivity mainActivity;

    ArrayList<GolfCourseModel> courseModels;
    AppDatabase db;
    public GolfCourseListFragment() {
        db = AppDatabase.getInMemoryDatabase(getActivity());

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
    GolfCourseListViewModel courseViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_golfcourselist_list, container, false);
        courseViewModel = ViewModelProviders.of(this).get(GolfCourseListViewModel.class);

        courseModels = new ArrayList<>();
        adapter = new MyGolfCourseListRecyclerViewAdapter(courseModels, mainActivity);
        subscribeAdapter();
        // Set the adapter
        if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
                recyclerView.setAdapter(adapter);
            }
        return view;
    }

    private void subscribeAdapter(){
        courseViewModel.courses.observe(this, new Observer<List<GolfCourseModel>>() {
            @Override
            public void onChanged(@Nullable List<GolfCourseModel> golfCourseModels) {
//                courseModels = new ArrayList<>();
                adapter.mValues = golfCourseModels;
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

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
import recrec.golfcourseviewer.Requests.Response.Hole;
import recrec.golfcourseviewer.Requests.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the
 * interface.
 */
public class HolesListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HolesListFragment() {
    }

    // TODO: Customize parameter initialization
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
    List<Hole> holesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_holeslist_list, container, false);
        courseViewModel = ViewModelProviders.of(getActivity()).get(CourseViewModel.class);
        subscribe();

        ApiClientRF clientRF = ServiceGenerator.getService();
        Call<List<Hole>> call = clientRF.getHolesByCourseId(courseViewModel.courseID.getValue());

        call.enqueue(new Callback<List<Hole>>() {
            @Override
            public void onResponse(Call<List<Hole>> call, Response<List<Hole>> response) {
                courseViewModel.holes.setValue(response.body());
                Log.d("Hole", response.body().get(0).getName());
            }

            @Override
            public void onFailure(Call<List<Hole>> call, Throwable t) {
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
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyHolesListRecyclerViewAdapter(holesList,courseViewModel);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    private void subscribe(){
        courseViewModel.holes.observe(this, new Observer<List<Hole>>() {
            @Override
            public void onChanged(@Nullable List<Hole> holes) {
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

package recrec.golfcourseviewer.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import recrec.golfcourseviewer.Entity.CourseViewModel;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.Response.Hole;
import recrec.golfcourseviewer.Requests.Response.Zone;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyHolesListRecyclerViewAdapter extends RecyclerView.Adapter<MyHolesListRecyclerViewAdapter.ViewHolder> {
    private List<Zone> mValues;
    private CourseViewModel courseViewModel;
    private Context fragmentActivity;

    public MyHolesListRecyclerViewAdapter(List<Zone> items, CourseViewModel
            vm, Context ma) {
        mValues = items;
        courseViewModel = vm;
        fragmentActivity = ma;
    }
    public void setmValues(List<Zone> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_holeslist, parent, false);
        return new ViewHolder(view, courseViewModel);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getZoneName());
        holder.mInfoView.setText(fragmentActivity.getString(R.string.par)
                + mValues.get(position).getInfo());

    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private int counter = 0;
    public class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        final TextView mIdView;
        private final TextView mContentView;
        final TextView mInfoView;
        Zone mItem;

        public ViewHolder(View view, final CourseViewModel vm) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mInfoView = (TextView) view.findViewById(R.id.hole_info);
            mIdView.setText(String.valueOf(++counter));

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vm.holeID.setValue(mItem.getZoneID());
                    Log.d("Hole id: ",mItem.getZoneID());
                }
            });
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

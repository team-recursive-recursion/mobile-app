/*
 * Filename: MyGolfCourseListRecyclerViewAdapter.java
 * Author: Team Recursive Recursion
 * Class: MyGolfCourseListRecyclerViewAdapter
 *       This class extends RecyclerView.Adapter. It is used to keep the list
 *       of courses up to date.
 * */
package recrec.golfcourseviewer.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import recrec.golfcourseviewer.Entity.CourseViewModel;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.Response.Zone;

import java.util.ArrayList;
import java.util.List;

public class MyGolfCourseListRecyclerViewAdapter extends RecyclerView.Adapter<MyGolfCourseListRecyclerViewAdapter.ViewHolder> {

    public List<Zone> mValues;
    public List<Zone> mFilter;

    private CourseViewModel courseVM;

    public MyGolfCourseListRecyclerViewAdapter(List<Zone> items, CourseViewModel courseListViewModel) {
        courseVM = courseListViewModel;
        mValues = items;
        mFilter = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_golfcourselist, parent, false);
        return new ViewHolder(view, courseVM);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mInfo.setText(mValues.get(position).getInfo());
        holder.mItem = mValues.get(position).getZoneName();
        holder.mCourseNameView.setText(mValues.get(position).getZoneName());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCourseNameView;
        public final TextView mInfo;
        public String mItem;

        public ViewHolder(View view, final CourseViewModel c) {
            super(view);
            mView = view;
            mCourseNameView = (TextView) view.findViewById(R.id.course_name);
            mInfo = (TextView) view.findViewById(R.id.course_info);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String courseID = mValues.get(getAdapterPosition()).getZoneID();
                    c.courseID.setValue(courseID);
                    Log.d("Course clicked Id: ", courseID);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCourseNameView.getText() + "'";
        }

    }


    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mValues = (List<Zone>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Zone> filter = new ArrayList<>();
                if (constraint.length() == 0 && filter.size() == 0) {
                    filter.addAll(mFilter);
                }else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mFilter.size(); i++) {
                        String dataNames = mFilter.get(i).getZoneName();
                        if (dataNames.toLowerCase().contains(constraint.toString())) {
                            filter.add(mFilter.get(i));
                        }
                    }
                }

                results.count = filter.size();
                results.values = filter;
                Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;
    }
}

/*
 * Filename: MyGolfCourseListRecyclerViewAdapter.java
 * Author: Team Recursive Recursion
 * Class: MyGolfCourseListRecyclerViewAdapter
 *       This class extends RecyclerView.Adapter. It is used to keep the list
 *       of courses up to date.
 * */
package recrec.golfcourseviewer.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import recrec.golfcourseviewer.Activity.MainActivity;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.db.Models.GolfCourseModel;

import java.util.List;
public class MyGolfCourseListRecyclerViewAdapter extends RecyclerView.Adapter<MyGolfCourseListRecyclerViewAdapter.ViewHolder> {

    public List<GolfCourseModel> mValues;
    private MainActivity mainActivity;

    public MyGolfCourseListRecyclerViewAdapter(List<GolfCourseModel> items, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_golfcourselist, parent, false);
        return new ViewHolder(view, mainActivity);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position).CourseName;
        holder.mContentView.setText(mValues.get(position).CourseName);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public String mItem;

        public ViewHolder(View view, final MainActivity mainActivity) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.course_name);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String courseID = mValues.get(getAdapterPosition()).CourseId;
                    mainActivity.getPolygonsFromAdapter(courseID, mainActivity);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

    }
}

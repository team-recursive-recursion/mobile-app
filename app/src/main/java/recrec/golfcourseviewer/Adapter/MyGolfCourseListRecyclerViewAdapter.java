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
import android.widget.TextView;

import recrec.golfcourseviewer.Entity.CourseViewModel;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.Response.Course;

import java.util.List;
public class MyGolfCourseListRecyclerViewAdapter extends RecyclerView.Adapter<MyGolfCourseListRecyclerViewAdapter.ViewHolder> {

    public List<Course> mValues;
    private CourseViewModel courseVM;

    public MyGolfCourseListRecyclerViewAdapter(List<Course> items, CourseViewModel courseListViewModel) {
        courseVM = courseListViewModel;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_golfcourselist, parent, false);
        return new ViewHolder(view, courseVM);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position).getCourseName();
        holder.mContentView.setText(mValues.get(position).getCourseName());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public String mItem;

        public ViewHolder(View view, final CourseViewModel c) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.course_name);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String courseID = mValues.get(getAdapterPosition()).getCourseId();
                    c.courseID.setValue(courseID);
                    Log.d("Course clicked Id: ", courseID);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

    }
}

package recrec.golfcourseviewer.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import recrec.golfcourseviewer.Entity.CourseViewModel;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.Response.Holes;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyHolesListRecyclerViewAdapter extends RecyclerView.Adapter<MyHolesListRecyclerViewAdapter.ViewHolder> {
    private List<Holes> mValues;
    private CourseViewModel courseViewModel;

    public MyHolesListRecyclerViewAdapter(List<Holes> items, CourseViewModel vm) {
        mValues = items;
        courseViewModel = vm;
    }
    public void setmValues(List<Holes> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_holeslist, parent, false);
        return new ViewHolder(view, courseViewModel);
    }

    private int counter = 0;
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(++counter));
        holder.mContentView.setText(mValues.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Holes mItem;

        public ViewHolder(View view, final CourseViewModel vm) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);

            mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vm.holeID.setValue(mItem.getHoleId());
                    Log.d("Hole id: ",mItem.getHoleId());
                }
            });
        }



        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

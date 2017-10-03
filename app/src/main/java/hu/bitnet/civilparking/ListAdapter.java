package hu.bitnet.civilparking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import hu.bitnet.civilparking.Objects.ParkingListObject;

/**
 * Created by Attila on 2017.08.17..
 */

public class ListAdapter extends ArrayAdapter<ParkingListObject> {

    List<ParkingListObject> parking_places;
    Context context;
    private LayoutInflater mInflater;

    // Constructors
    public ListAdapter(Context context, List<ParkingListObject> objects) {
        super(context, 0, objects);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        parking_places = objects;
    }

    @Override
    public ParkingListObject getItem(int position) {
        return parking_places.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.layout_row_view, parent, false);
            vh = ViewHolder.create((RelativeLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        ParkingListObject item = getItem(position);

        vh.textViewName.setText(item.getName());
        vh.textViewEmail.setText(item.getDescription());

        return vh.rootView;
    }

    private static class ViewHolder {
        public final RelativeLayout rootView;
        public final TextView textViewName;
        public final TextView textViewEmail;

        private ViewHolder(RelativeLayout rootView, TextView textViewName, TextView textViewEmail) {
            this.rootView = rootView;
            this.textViewName = textViewName;
            this.textViewEmail = textViewEmail;
        }

        public static ViewHolder create(RelativeLayout rootView) {
            TextView textViewName = (TextView) rootView.findViewById(R.id.textViewName);
            TextView textViewEmail = (TextView) rootView.findViewById(R.id.textViewEmail);
            return new ViewHolder(rootView, textViewName, textViewEmail);
        }
    }
}

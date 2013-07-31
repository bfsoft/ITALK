package staff.iprofe.test;

import java.util.ArrayList;
import java.util.List;

import staff.iprofe.italk.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TestBaseAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<String> mData;
	
	public TestBaseAdapter(Context context) {  
        inflater = LayoutInflater.from(context);  
        mData = getData();
    }  
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.d("getCount", "getCount() is invoked!");
		return (null == mData)?0:(mData.size());
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Log.d("getItem", "getItem() is invoked!");
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Log.d("getItemId", "getItemId() is invoked!");  
        Log.d("getItemId", "position = " + position); 
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return null;
		Log.d("getView", "getView() is invoked!"); 
		
		ViewHolder vholder = null;
		if (null == convertView) {
			
			vholder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item1, null);
			vholder.title = (TextView) convertView.findViewById(R.id.text1);
			convertView.setTag(vholder);
		} else 
			vholder = (ViewHolder) convertView.getTag();
		
		
		vholder.title.setText(mData.get(position));
		return convertView;
	}
	
	private List<String> getData(){		
		List<String> data = new ArrayList<String>();
		data.add("data 1");
		data.add("data 2");
		data.add("data 3");
		data.add("data 4");
		
		return data;
	}

}

final class ViewHolder {
	public TextView title;
}

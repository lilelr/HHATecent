package hha.medicinecold;

import hha.robot.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MedicineColdActivity extends ListActivity{

	private List<Map<String, Object>> mData;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Intent fromcoldModeIntent = getIntent();
//		String cold_symtom=fromcoldModeIntent.getStringExtra("cold_symtom");
//		String[] cold_sysmtoms = cold_symtom.split("_");
//		JSONObject jsonObjectRequest=new JSONObject();
//		try {
//			jsonObjectRequest.put("symton", "cold");
//			JSONObject symtonItems =new JSONObject();
//			symtonItems.put("cold_nose", cold_sysmtoms[0]);
//			symtonItems.put("cold_fever", cold_sysmtoms[1]);
//			symtonItems.put("cold_snot", cold_sysmtoms[2]);
//			symtonItems.put("cold_cough", cold_sysmtoms[3]);
//			jsonObjectRequest.put("subsymtons", symtonItems);
//			getJSONVolley(jsonObjectRequest);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		mData = getData();
		MyAdapter adapter = new MyAdapter(this);
		setListAdapter(adapter);
	}
	
	
	// 获取json字符串
		public void getJSONVolley(JSONObject jsonRequest) {
			RequestQueue requestQueue = Volley.newRequestQueue(this);
			String JSONDateUrl = "http://www.wwtliu.com/jsondata.html";
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
					Request.Method.GET, JSONDateUrl, jsonRequest,
					new Response.Listener<JSONObject>() {
						public void onResponse(JSONObject response) {
							System.out.println("response=" + response);
						}
					}, new Response.ErrorListener() {
						public void onErrorResponse(
								com.android.volley.VolleyError arg0) {
							System.out.println("对不起，有问题");
						}
					});
			requestQueue.add(jsonObjectRequest);
		}
		
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		InputStream inputStream;
		try {
			inputStream=this.getAssets().open("static_medicine.txt");
			String json=readTextFile(inputStream);
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				map = new HashMap<String, Object>();
				map.put("name", array.getJSONObject(i).getString("desc"));
				map.put("imgurl", array.getJSONObject(i).getString("imgurl"));
				map.put("priceurl",array.getJSONObject(i).getString("priceurl"));
				map.put("pop", array.getJSONObject(i).getString("pop"));
				list.add(map);
			}
			return list;	
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		return null;	
	}
	

	
	public final class ViewHolder{
		public ImageView medicine_img;
		public TextView medicine_name;
		public ImageView medicine_price;
		public TextView medicine_popularity;
	}	
	

	
	public class MyAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		
		public MyAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {
				
				holder=new ViewHolder();  
				
				convertView = mInflater.inflate(R.layout.medicine_cold, null);
				holder.medicine_img = (ImageView)convertView.findViewById(R.id.cold_img);
				holder.medicine_name = (TextView)convertView.findViewById(R.id.cold_name);
				holder.medicine_price = (ImageView)convertView.findViewById(R.id.cold_price);
				holder.medicine_popularity=(TextView) convertView.findViewById(R.id.cold_pop);
				convertView.setTag(holder);
				
			}else {
				
				holder = (ViewHolder)convertView.getTag();
			}
			

			holder.medicine_img.setImageBitmap(getHome((String)mData.get(position).get("imgurl")));
			holder.medicine_name.setText((String)mData.get(position).get("name"));

			holder.medicine_popularity.setText("人气:"+(String) mData.get(position).get("pop"));
			holder.medicine_price.setImageBitmap(getHome((String)mData.get(position).get("priceurl")));

			return convertView;
		}
		
	}
	
////工具类
	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	public String readTextFile(InputStream inputStream) {
		String readedStr = "";
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String tmp;
			while ((tmp = br.readLine()) != null) {
				readedStr += tmp;
			}
			br.close();
			inputStream.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return readedStr;
	}

	/**
	 * 根据图片名称获取主页图片
	 */
	public Bitmap getHome(String photo){		
	
		InputStream is=null;
		
        try {
        	is=getAssets().open("staticmedicine/"+photo);
            Bitmap bitmap = BitmapFactory.decodeStream(is);     
            is.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return null;

	}
	
}

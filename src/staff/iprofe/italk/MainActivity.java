package staff.iprofe.italk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import staff.iprofe.tools.AsyncImageLoader;
import staff.iprofe.tools.CallbackImpl;
import staff.iprofe.tools.Common;
import staff.iprofe.tools.DBCP;

@SuppressLint("CutPasteId")
public class MainActivity extends Activity {
	
	private static final String NAME = "name", NUMBER = "number", SORT_KEY = "sort_key";
	
	private BaseAdapter adapter;
	private ListView personList;
	private AsyncQueryHandler asyncQuery; 
	
	private String useNetWork;
	
	private AsyncImageLoader loader = new AsyncImageLoader();
	
	public MyHandler myHandler;
	
	private ViewStub stub;
	
	public Boolean initInfo = true;
	public Boolean refresh = false;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    personList = (ListView) findViewById(R.id.listItem);
	    asyncQuery = new MyAsyncQueryHandler(getContentResolver());
	}
	
	@Override  
    protected void onResume() {  
        super.onResume();  
        Log.e("Resume", "start");
        
        if (null == useNetWork) 
            useNetWork = Common.checkNetworkInfo(getApplicationContext());
        
        if ("".equals(useNetWork)) 
        	Common.showMsg(this, "网络连接不可用,请稍后重试");
          
        String[] projection = { "data", "uid", "sort_key" };
        asyncQuery.startQuery(0, null, DBCP.CONTENT_URI_FRIEND, projection, null, null, "sort_key COLLATE LOCALIZED asc");
    } 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override  
	protected void onDestroy() {  
	    super.onDestroy();   
	}
	
	//查询联系人
    private class MyAsyncQueryHandler extends AsyncQueryHandler {  
  
        public MyAsyncQueryHandler(ContentResolver cr) {  
            super(cr);  
        }  
        
        public void startQuery()
        {
        	
        }
  
        @Override  
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {  
        	Log.i("DB", "COUNT " + cursor.getCount());
        	
        	if (cursor != null && cursor.getCount() > 0) {  
        		List<ContentValues> list = new ArrayList<ContentValues>();  
                cursor.moveToFirst();  
                
                for (int i = 0; i < cursor.getCount(); i++) {
                	
                    ContentValues cv = new ContentValues();  
                    cursor.moveToPosition(i);  
                    String data = cursor.getString(0);  
                    String uid = cursor.getString(1);  
                    String sortKey = cursor.getString(2);
                    
                    cv.put("UID", uid);  
                    cv.put("DATA", data);  
                    cv.put(SORT_KEY, sortKey);
                      
                    list.add(cv);  
                }  
                
                Log.i("Adapter", "refresh " + refresh + " adapter " + ((null == adapter)?0:1));
                if (refresh && null != adapter) {
                	Log.i("Adapter", "refresh");
                    ((ListAdapter) adapter).refresh(list);
                } else if (list.size() > 0) {  
                    setAdapter(list);  
                }  
                
                Timer mTimer = new Timer(true);
                TimerTask  mTimerTask = new TimerTask() {
                	@Override
                    public void run() {
                        if (initInfo && ("WIFI".equals(useNetWork) || "3G".equals(useNetWork))) {
                        	Log.i("FriendList", "update");
                        	Looper.prepare();
                        	myHandler = new MyHandler();
                    		
                    		MyThread m = new MyThread();
                            new Thread(m).start();
                        }
                    }
                };
                
                mTimer.schedule(mTimerTask, 3000);
                
            } else if (!"".equals(useNetWork)) {
            	initInfo = false;
            	
            	stub = (ViewStub) findViewById(R.id.viewstub_load);  
                stub.inflate();  
                
            	GifView gf1 = (GifView) findViewById(R.id.loading); 
            	// 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示  
            	gf1.setGifImageType(GifImageType.WAIT_FINISH);  
            	// 设置显示的大小，拉伸或者压缩  
            	gf1.setShowDimension(65, 65); 
            	// 设置Gif图片源  
            	gf1.setGifImage(R.drawable.loading);
            	
            	myHandler = new MyHandler();
        		
        		MyThread m = new MyThread();
                new Thread(m).start();
            }  
            
        } 
    }
    
    private void setAdapter(List<ContentValues> list) {
    	Log.i("Adapter", "show");
    	if (null != stub) {
    		stub.setVisibility(View.GONE);
    		stub = null;
    	}
    		
    	adapter = new ListAdapter(this, list);
        personList.setAdapter(adapter); 
        
        personList.setOnItemClickListener(new OnItemClickListener() {  

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				 TextView number = (TextView)arg1.findViewById(R.id.number);  
				 TextView realname = (TextView)arg1.findViewById(R.id.name);
				 TextView atname = (TextView)arg1.findViewById(R.id.atname);
				 
	             new AlertDialog.Builder(MainActivity.this).setTitle("列表框").
	             setItems(new String[] { "realname: " + realname.getText(), "atname: " + atname.getText(), "number: " + number.getText() }, null).
	             setNegativeButton("确定", null).show();
			}  
        });
    }
    
    private class ListAdapter extends BaseAdapter {
    	 private LayoutInflater inflater;  
         private List<ContentValues> list;
    	
    	public ListAdapter(Context context, List<ContentValues> list) {
    		this.inflater = LayoutInflater.from(context);
    		this.list = list;
    	}
    	
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void refresh(List<ContentValues> list) {  
	        this.list = list;  
	        notifyDataSetChanged();  
	    } 
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {  
                convertView = inflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();  
                holder.alpha = (TextView) convertView.findViewById(R.id.alpha);  
                holder.name = (TextView) convertView.findViewById(R.id.name);  
                holder.number = (TextView) convertView.findViewById(R.id.number); 
                holder.img = (ImageView) convertView.findViewById(R.id.image_view);
                holder.atname = (TextView) convertView.findViewById(R.id.atname);
                convertView.setTag(holder);  
            } else {  
                holder = (ViewHolder) convertView.getTag();  
            }  
			
            ContentValues cv = list.get(position);  
        	
            try {
            	
            	JSONObject data = new JSONObject(cv.getAsString("DATA"));
            	
            	holder.name.setText(data.getString("realname"));
            	holder.number.setText(data.getString("mphone"));
            	holder.atname.setText(data.getString("atname"));
            	String imgUrl = data.getString("avatar").trim();
            	if (!"".equals(imgUrl)) 
            		imgUrl = Common.host + imgUrl;
            	
            	loadImage(imgUrl, holder.img);
            	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            Log.d("Cursor", "sort_key is " + list.get(position).getAsString(SORT_KEY));
            
            String currentStr = getAlpha(list.get(position).getAsString(SORT_KEY));
            String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).getAsString(SORT_KEY)) : " ";
            if (!previewStr.equals(currentStr)) {  
                holder.alpha.setVisibility(View.VISIBLE);
                holder.alpha.setText(currentStr);
            } else {  
                holder.alpha.setVisibility(View.GONE);
            }    
            
            holder.img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {	
					Drawable img = ((ImageView) v).getDrawable();
					ImageView imgv = new ImageView(MainActivity.this);
					imgv.setImageDrawable(img);
					imgv.setMinimumHeight(300);
					imgv.setMinimumWidth(300);
					new AlertDialog.Builder(MainActivity.this).setTitle("图片框").setView(imgv).setPositiveButton("确定", null).show();
				}
			});
            
            holder.number.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i("mphone", "tel " + ((TextView) v).getText());		
					Intent intent=new Intent();
					intent.setAction(Intent.ACTION_CALL);   
					intent.setData(Uri.parse("tel:"+((TextView) v).getText()));
					startActivity(intent);
				}
			});

            return convertView;  
            
		}
		
		private class ViewHolder {
			TextView alpha;  
            TextView name;  
            TextView number;
            TextView atname;
            ImageView img;
		}
    	
    }
    
    class MyHandler extends Handler {
    	
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
        	Log.v("url response", "hello");
        	super.handleMessage(msg);
        	
            // 此处可以更新UI
            Bundle b = msg.getData();
            String color = b.getString("token");
            Log.e("handler", "color " + color);
        }
    }
    
    class MyThread implements Runnable {
    	
        public void run() {
            Log.d("thread.......", "mThread........");
            
            JSONObject jsonObject = startUrlCheck();
            
            if (null == jsonObject) 
            	return;
            
            Iterator iter = jsonObject.keys();
            while (iter.hasNext()) {
            	String id = (String) iter.next();
            	
            	JSONObject nameList = null;
            	try {
					nameList = jsonObject.getJSONObject(id);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	
            	try {
            		ContentValues initialValues = new ContentValues();
            		initialValues.put("data", nameList.get("data").toString());
            		initialValues.put("uid", Integer.parseInt(id));
            		initialValues.put("sort_key", nameList.getString("sk"));
            		asyncQuery.startInsert(0, null, ContentUris.withAppendedId(DBCP.CONTENT_URI_FRIEND, Integer.parseInt(id)), initialValues);
            	} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					//db.endTransaction();
				}
            	
            }
            
            if (initInfo) {
               refresh = true;
               initInfo = false;
            }
            
            String[] projection = { "data", "uid", "sort_key" };
            asyncQuery.startQuery(0, null, DBCP.CONTENT_URI_FRIEND, projection, null, null, "sort_key COLLATE LOCALIZED asc");
              
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
			b.putString("token", "ok");
            msg.setData(b);
    		
            MainActivity.this.myHandler.sendMessage(msg); // 向Handler发送消息,更新UI

        }
    }

    
    private JSONObject startUrlCheck()
    {
    	    
         HttpClient client = new DefaultHttpClient();
         StringBuilder builder = new StringBuilder();

         String url = Common.friendDataSource();
         HttpGet myget = new HttpGet(url);
         try {
        	 HttpResponse response = client.execute(myget);
             BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    
             for (String s = reader.readLine(); s != null; s = reader.readLine()) {
            	 builder.append(s);
             }
                    
             return new JSONObject(builder.toString()); 
          } catch (Exception e) {
             Log.v("url response", "false");
             e.printStackTrace();
          } 
         
         return null;
    }
    
    //url：下载图片的URL  
    //id：ImageView控件的ID  
    @SuppressWarnings("deprecation")
	private void loadImage(final String url, final ImageView imageView) {  
    	
    	Log.e("IMG", "URL " + url);
    	
        // 如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行   
        CallbackImpl callbackImpl = new CallbackImpl(imageView, getApplicationContext());  
        Drawable cacheImage = loader.loadDrawable(url, callbackImpl, useNetWork);  
        if (cacheImage != null) { 
            imageView.setImageDrawable(cacheImage);  
        }  
    } 
    
    //获得汉语拼音首字母
    @SuppressLint("DefaultLocale")
	private String getAlpha(String str) {  
        if (str == null) {  
            return "#";  
        }  
  
        if (str.trim().length() == 0) {  
            return "#";  
        }  
  
        char c = str.trim().substring(0, 1).charAt(0);  
        // 正则表达式，判断首字母是否是英文字母  
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");  
        if (pattern.matcher(c + "").matches()) {  
            return (c + "").toUpperCase();  
        } else {  
            return "#";  
        }  
    }  
    
}

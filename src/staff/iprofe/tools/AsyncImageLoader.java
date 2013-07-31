package staff.iprofe.tools;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//该类的主要作用是实现图片的异步加载  
@SuppressLint("HandlerLeak")
public class AsyncImageLoader {  
    // 图片缓存对象  
    // 键是图片的URL，值是一个SoftReference对象，该对象指向一个Drawable对象  
    private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();  

    // 实现图片的异步加载  
	public Drawable loadDrawable(final String imageUrl, final ImageCallback callback, final String useNetWork) {  
		
		if ("".equals(imageUrl)) 
			return callback.defaultRes();
		
	    // 查询缓存，查看当前需要下载的图片是否已经存在于缓存当中  
	    if (imageCache.containsKey(imageUrl)) {  
	        SoftReference<Drawable> softReference = imageCache.get(imageUrl);  
	        if (softReference.get() != null) {  
	        	Log.e("SR", "from softReference");
	            return softReference.get();  
	        }  
	    }  
	    
	    String bitName = queryFileName(imageUrl);
	    if (callback.BitmapfileExists(bitName)) {
	    	
	    	Bitmap output = callback.toRoundCorner(callback.resizeBitmap(callback.getFileInfo(bitName),65, 65), 8);
	    	Drawable obj = new BitmapDrawable(output);
	    	// 然后把图片放入缓存当中  
            imageCache.put(imageUrl, new SoftReference<Drawable>(obj)); 
            Log.e("SR", "from file");
            return obj;
	    }
	    
	    if ("".equals(useNetWork)) 
	    	return callback.defaultRes();
	
	    final Handler handler = new Handler() {  
	        @SuppressWarnings("deprecation")
			@Override  
	        public void handleMessage(Message msg) {  
	            callback.imageLoaded((Drawable) msg.obj);  
	        }  
	    };  
	    
	    // 新开辟一个线程，该线程用于进行图片的下载  
	    new Thread() {  
	        public void run() {  
	        	
	        	String bitName = queryFileName(imageUrl);
	            //Drawable drawable = callback.zoomDrawable(loadImageFromUrl(imageUrl), 65, 65); 
	        	Drawable drawable = loadImageFromUrl(imageUrl);
		        if (null != drawable) {
		            Bitmap output = callback.toRoundCorner(callback.resizeBitmap(callback.drawableToBitmap((Drawable) drawable),65, 65), 8);
		            
		            if (!callback.BitmapfileExists(bitName)) {
			            Log.e("Append File", " is " + bitName);
			            callback.saveMyBitmap(bitName, output);
		            }
		            
		            drawable = new BitmapDrawable(output);
		            // 然后把图片放入缓存当中  
		            imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));  
		        } else {
		        	drawable = callback.defaultRes();
		        }
	            Message message = handler.obtainMessage(0, drawable);  
	            
	            handler.sendMessage(message);  
	        };  
	    }.start();  
	    return null;  
    }  
	
	protected String queryFileName(String imageUrl) {
		int fs = imageUrl.lastIndexOf('/')+1;
    	int fe = imageUrl.lastIndexOf('.');
    	return imageUrl.substring(fs, fe);
	}

    // 该方法用于根据图片的URL，从网络上下载图片  
    protected Drawable loadImageFromUrl(String imageUrl) {  
    	
        try {  
            // 根据图片的URL，下载图片，并生成一个Drawable对象  
            return Drawable.createFromStream(new URL(imageUrl).openStream(), "src");  

        } catch (Exception e) {  
        	return null;
        }  
    }  

    // 回调接口  
    public interface ImageCallback {  
        public void imageLoaded(Drawable imageDrawable);  
        public void saveMyBitmap(String bitName, Bitmap mBitmap);
        public Boolean delBitmapFile(String bitName);
        public Bitmap getFileInfo(String bitName);
        public Boolean BitmapfileExists(String bitName);
        
        public Drawable defaultRes();
        public Bitmap drawableToBitmap(Drawable drawable);
        public Bitmap toRoundCorner(Bitmap bitmap, int pixels);
        public Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight);
        public Drawable zoomDrawable(Drawable drawable, int w, int h);
    }  
}  
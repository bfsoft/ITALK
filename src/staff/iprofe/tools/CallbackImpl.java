package staff.iprofe.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import staff.iprofe.italk.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

public class CallbackImpl implements AsyncImageLoader.ImageCallback{  
    private ImageView imageView ;  
    
    private Context context;
      
    public CallbackImpl(ImageView imageView, Context context) {  
        super();  
        this.imageView = imageView;  
        this.context = context;
    }  
  
    @Override  
    public void imageLoaded(Drawable imageDrawable) {  
        imageView.setImageDrawable(imageDrawable);  
    }
  
    public Bitmap toRoundCorner(Bitmap bitmap, int pixels) {  
        
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
  
        final int color = 0xff424242;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
        final float roundPx = pixels;  
  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  
  
        return output;  
    }
    
    public Bitmap drawableToBitmap(Drawable drawable) {  
        // ȡ drawable �ĳ���  
        int w = drawable.getIntrinsicWidth();  
        int h = drawable.getIntrinsicHeight();  
  
        // ȡ drawable ����ɫ��ʽ  
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                : Bitmap.Config.RGB_565;  
        // ������Ӧ bitmap  
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);  
        // ������Ӧ bitmap �Ļ���  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, w, h);  
        // �� drawable ���ݻ���������  
        drawable.draw(canvas);  
        return bitmap;  
    } 
    
    public Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {  
        final int reflectionGap = 4;  
        int w = bitmap.getWidth();  
        int h = bitmap.getHeight();  
      
        Matrix matrix = new Matrix();  
        matrix.preScale(1, -1);  
      
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,  
                h / 2, matrix, false);  
      
        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),  
                Config.ARGB_8888);  
      
        Canvas canvas = new Canvas(bitmapWithReflection);  
        canvas.drawBitmap(bitmap, 0, 0, null);  
        Paint deafalutPaint = new Paint();  
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);  
      
        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);  
      
        Paint paint = new Paint();  
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,  
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,  
                0x00ffffff, TileMode.CLAMP);  
        paint.setShader(shader);  
        // Set the Transfer mode to be porter duff and destination in  
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));  
        // Draw a rectangle using the paint with our linear gradient  
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()  
                + reflectionGap, paint);  
      
        return bitmapWithReflection;  
    } 
    
    public Drawable defaultRes(){
    	return context.getResources().getDrawable(R.drawable.default_useravatar);
    }
    
    public Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {

        int originWidth  = bitmap.getWidth();
        int originHeight = bitmap.getHeight();

        //no need to resize
        if (originWidth < maxWidth && originHeight < maxHeight) {
            return bitmap;
        }

        int width  = originWidth;
        int height = originHeight;

        //��ͼƬ����, �򱣳ֳ��������ͼƬ
        if (originWidth > maxWidth) {
            width = maxWidth;

            double i = originWidth * 1.0 / maxWidth;
            height = (int) Math.floor(originHeight / i);

            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        }

        // ��ͼƬ����, ����϶˽�ȡ
        if (height > maxHeight) {
            height = maxHeight;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        }

        Log.i("IMG", width + " width");
        Log.i("IMG", height + " height");

        return bitmap;
    }
    
    public Drawable zoomDrawable(Drawable drawable, int w, int h) {  
        int width = drawable.getIntrinsicWidth();  
        int height = drawable.getIntrinsicHeight();  
        // drawableת����bitmap  
        Bitmap oldbmp = drawableToBitmap(drawable);  
        // ��������ͼƬ�õ�Matrix����  
        Matrix matrix = new Matrix();  
        // �������ű���  
        float sx = ((float) w / width);  
        float sy = ((float) h / height);  
        // �������ű���  
        matrix.postScale(sx, sy);  
        // �����µ�bitmap���������Ƕ�ԭbitmap�����ź��ͼ  
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,  matrix, true);  
        return new BitmapDrawable(newbmp);  
    } 
    
    public Boolean delBitmapFile(String bitName) {
    	
    	return context.deleteFile(bitName);
    }
    
    public Bitmap getFileInfo(String bitName) {
    	
    	FileInputStream fin = null;  
        try {  
        	fin = context.openFileInput(bitName);  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        }
        
        return BitmapFactory.decodeStream(fin);
    }
    
    public Boolean BitmapfileExists(String bitName) {
    	String[] fileList = context.fileList();
    	int ind = Arrays.binarySearch(fileList, bitName);
    	
    	if (0 > ind) 
    		return false;
    	
    	return true;
    }
    
    public void saveMyBitmap(String bitName, Bitmap mBitmap) {  

        FileOutputStream fOut = null;  
        try {  
        	fOut = context.openFileOutput(bitName, 0);  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        }  
        
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);  
        
        try {  
        	fOut.flush();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        
        try {  
        	fOut.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
}
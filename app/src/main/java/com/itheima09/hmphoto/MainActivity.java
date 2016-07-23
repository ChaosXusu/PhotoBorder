package com.itheima09.hmphoto;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private ImageView iv;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv);
        int img = R.mipmap.img;
        bitmap = loadOriginalBitmap(getResources(), img);
        iv.setImageBitmap(bitmap);
    }

    public void onClick(View v) {
        //加载左上角
        Bitmap leftTopCorner = BitmapFactory.decodeResource(getResources(), R.mipmap.photo_frame_corner);
        //获取角的宽高
        int cornerWidth = leftTopCorner.getWidth();
        int cornerHeight = leftTopCorner.getHeight();
        //获取右上角
        Matrix m = new Matrix();
        float sx = -1;//负号实现图形的x反向
        float sy = 1;
        m.setScale(sx, sy);
        Bitmap rightTopCorner = Bitmap.createBitmap(leftTopCorner, 0, 0, cornerWidth, cornerHeight, m, true);
        //获取左下角
        sx = 1;
        sy = -1;
        m.setScale(sx, sy);
        Bitmap leftBottomCorner = Bitmap.createBitmap(leftTopCorner, 0, 0, cornerWidth, cornerHeight, m, true);

        //获取右下角
        sx = -1;
        sy = -1;
        m.setScale(sx, sy);
        Bitmap rightBottomCorner = Bitmap.createBitmap(leftTopCorner, 0, 0, cornerWidth, cornerHeight, m, true);

        //获取中间的连接部分
        int horClipWidth = 10;
        int verClipWidth = 10;
        //获取上边的水平方向的连接部分
        int x = cornerWidth - horClipWidth;
        int y = 0;
        int width = horClipWidth;
        int height = cornerHeight;
        Bitmap horTopBorder = Bitmap.createBitmap(leftTopCorner, x, y, width, height);
        //获取左边竖直方向上的连接部分
        x = 0;
        y = cornerHeight - verClipWidth;
        width = cornerWidth;
        height = verClipWidth;
        Bitmap verLeftBorder = Bitmap.createBitmap(leftTopCorner, x, y, width, height);
        //获取右边竖直方向上的连接部分
        sx = -1;
        sy = 1;
        m.setScale(sx, sy);
        Bitmap verRightBorder = Bitmap.createBitmap(verLeftBorder, 0, 0, verLeftBorder.getWidth(), verLeftBorder.getHeight(), m, true);
        //获取下边水平方向上的连接部分
        sx = 1;
        sy = -1;
        m.setScale(sx, sy);
        Bitmap horBottomBorder = Bitmap.createBitmap(horTopBorder, 0, 0, horTopBorder.getWidth(), horTopBorder.getHeight(), m, true);

        //绘制相框
        int finalWidth = 0;
        int finalHeight = 0;
        //获取原图片的宽高
        int originalBitmapWidth = bitmap.getWidth();
        int originalBitmapHeight = bitmap.getHeight();

        int padding = 10;
        int pWidth = originalBitmapWidth + padding * 2;
        int pHeight = originalBitmapHeight + padding * 2;
        Bitmap photoFrameBitmap = Bitmap.createBitmap(pWidth, pHeight, Bitmap.Config.ARGB_8888);
        //获取相框的画布
        Canvas canvas = new Canvas(photoFrameBitmap);
        //画左上角
        Paint paint = new Paint();
        int left = 0;
        int top = 0;
        canvas.drawBitmap(leftTopCorner, left, top, paint);
        left += cornerWidth;
        //画上边的连接部分
        while (left + horClipWidth < pWidth - cornerWidth) {
            canvas.drawBitmap(horTopBorder, left, top, paint);
            left += horClipWidth;
        }
        //画右上角
        canvas.drawBitmap(rightTopCorner, left, top, paint);
        top += cornerHeight;
        finalWidth = left + cornerWidth;
        //画右边竖直方向上的连接部分
        while (top + verClipWidth < pHeight - cornerHeight) {
            canvas.drawBitmap(verRightBorder, left, top, paint);
            top += verClipWidth;
        }
        //画左边竖直方向上的连接部分
        left = 0;
        top = cornerHeight;
        while (top + verClipWidth < pHeight - cornerHeight) {
            canvas.drawBitmap(verLeftBorder, left, top, paint);
            top += verClipWidth;
        }
        //画左下角
        canvas.drawBitmap(leftBottomCorner, left, top, paint);
        left += cornerWidth;
        finalHeight = top + cornerHeight;
        //画下边水平方向的连接部分 //4
        while (left + horClipWidth < pWidth - cornerWidth) {
            canvas.drawBitmap(horBottomBorder, left, top, paint);
            left += horClipWidth;
        }
        //画右下角
        canvas.drawBitmap(rightBottomCorner, left, top, paint);
        //修正一下相框
        Bitmap finalPhotoFrame = Bitmap.createBitmap(photoFrameBitmap, 0, 0, finalWidth, finalHeight);

        //相框放到原图片上面
        Bitmap finalBitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(finalBitmap);
        //画原图片
        left = (finalWidth - originalBitmapWidth) / 2;
        top = (finalHeight - originalBitmapHeight) / 2;
        canvas.drawBitmap(bitmap, left, top, paint);
        //画相框
        canvas.drawBitmap(finalPhotoFrame, 0, 0, paint);
        iv.setImageBitmap(finalBitmap);
    }

    //加载原图片
    public Bitmap loadOriginalBitmap(Resources res, int id) {
        //加载策略
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;//只加载边界信息，不真正加载到内存
        BitmapFactory.decodeResource(res, id, opts);
        //获取图片的原始宽高
        int outWidth = opts.outWidth;//700
        int outHeight = opts.outHeight;
        //480*800作为边界
        if (outWidth > 480 && outHeight > 800) {
            int w_ratio = outWidth / 480;
            int h_ratio = outHeight / 800;
            opts.inSampleSize = Math.max(w_ratio, h_ratio);
        }
        //加载图片
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(res, id, opts);
        return bitmap;
    }
}

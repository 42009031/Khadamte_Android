package com.khdamte.bitcode.khdamte_app.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

public class CircleTransform
  implements Transformation
{
  public String key()
  {
    return "circle";
  }
  
  public Bitmap transform(Bitmap paramBitmap)
  {
    int i = Math.min(paramBitmap.getWidth(), paramBitmap.getHeight());
    Bitmap localBitmap1 = Bitmap.createBitmap(paramBitmap, (paramBitmap.getWidth() - i) / 2, (paramBitmap.getHeight() - i) / 2, i, i);
    if (localBitmap1 != paramBitmap) {
      paramBitmap.recycle();
    }
    Bitmap localBitmap2 = Bitmap.createBitmap(i, i, paramBitmap.getConfig());
    Canvas localCanvas = new Canvas(localBitmap2);
    Paint localPaint = new Paint();
    localPaint.setShader(new BitmapShader(localBitmap1, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
    localPaint.setAntiAlias(true);
    float f = i / 2.0F;
    localCanvas.drawCircle(f, f, f, localPaint);
    localBitmap1.recycle();
    return localBitmap2;
  }
}



/* Location:           E:\workspace\4 apktool\dex2jar-0.0.9.15\dex2jar-0.0.9.15\classes_dex2jar.jar

 * Qualified Name:     tech.dawoud.com.mtse_controller_app.common.CircleTransform

 * JD-Core Version:    0.7.0.1

 */
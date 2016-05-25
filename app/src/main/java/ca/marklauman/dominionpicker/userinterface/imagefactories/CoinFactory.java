package ca.marklauman.dominionpicker.userinterface.imagefactories;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.ImageSpan;

import ca.marklauman.dominionpicker.R;

/** Factory which provides {@link CoinDrawable}s to views that need them.
 *  @author Mark Lauman */
public class CoinFactory extends ImageFactory {

    private static final ImageLibrary lib = new ImageLibrary();

    /** Available image sizes. */
    private final int[] img_size;
    /** Background color for the coin */
    private final int coinBack;
    /** Edge color for the coin */
    private final int coinEdge;
    /** Current ImageSpan id value (increments as they are retrieved) */
    private int spanId = 0;

    /** Create a CoinFactory from the given resources. */
    public CoinFactory(Context context){
        Resources res = context.getResources();
        img_size = new int[]{res.getDimensionPixelSize(R.dimen.drawable_size_small),
                             res.getDimensionPixelSize(R.dimen.drawable_size_med),
                             res.getDimensionPixelSize(R.dimen.drawable_size_large)};
        coinBack = ContextCompat.getColor(context, R.color.coin_back);
        coinEdge = ContextCompat.getColor(context, R.color.drawable_edge);
    }

    @Override
    public Drawable getDrawable(CharSequence value, int size) {
        Drawable res = lib.getDrawable(value, size);
        if(res == null) res = makeDrawable(value, size);
        return res;
    }

    @Override
    public ImageSpan getSpan(CharSequence value, int size) {
        ImageSpan res = lib.getSpan(value, size, spanId);
        if(res == null) {
            makeDrawable(value, size);
            res = lib.getSpan(value, size, spanId);
        }
        spanId++;
        return res;
    }

    @Override
    public void newSpannableView() {
        spanId = 0;
    }

    /** Get a CoinDrawable with the provided value.
     *  @param val The value to display on the coin. */
    private CoinDrawable makeDrawable(CharSequence val, int size) {
        CoinDrawable res = new CoinDrawable(""+val);
        res.setBounds(0, 0, img_size[size], img_size[size]);
        lib.setDrawable(val, size, res);
        return res;
    }


    public class CoinDrawable extends Drawable {
        /** Value to display in this coin */
        private final String val;
        /** Paint object used by this drawable */
        private final Paint paint;
        /** Rectangle used to determine the text bounds */
        private final Rect textBounds;

        CoinDrawable(String value) {
            paint = new Paint();
            paint.setAntiAlias(true);
            val = value;
            textBounds = new Rect();
        }

        @Override
        public void draw(Canvas canvas) {
            // Provided width and height
            float height = getBounds().height();
            float width = getBounds().width();

            // X and Y in the center of the canvas
            float x = width/2f;
            float y = height/2f;

            // The size is the lesser of width and height
            float size = (width < height) ? width : height;
            size -= 2; // for anti-aliasing on the borders

            // Draw the coin
            float coinSize = size/2f;
            paint.setColor(coinEdge);
            canvas.drawCircle(x, y, coinSize, paint);
            paint.setColor(coinBack);
            canvas.drawCircle(x, y, 0.9f*coinSize, paint);

            // Draw the text
            float fontSize = (val.length() < 2) ? 0.7f*size
                                                : 0.6f*size;
            paint.setTextSize(fontSize);
            paint.setColor(Color.BLACK);
            paint.getTextBounds(val, 0, val.length(), textBounds);
            canvas.drawText(val, x-textBounds.exactCenterX(), y-textBounds.exactCenterY(), paint);
        }


        @Override
        public int getIntrinsicHeight() {
            return img_size[SIZE_MED];
        }


        @Override
        public int getIntrinsicWidth() {
            return img_size[SIZE_MED];
        }


        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
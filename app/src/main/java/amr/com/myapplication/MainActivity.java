package amr.com.myapplication;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Rect imageBounds;
    float heightRatio, widthRatio;
    ImageView ivTest;
    int actionBarHeight;
    float density;
    int[] imagePositions;
    TextView tvResult;
    float intrinsicHeight;
    int statusBarHeight;

    /**
     * Returns the bitmap position inside an imageView.
     *
     * @param imageView source ImageView
     * @return 0: left, 1: top, 2: width, 3: height
     */
    public static int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] ret = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH) / 2;
        int left = (int) (imgViewW - actW) / 2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivTest = (ImageView) findViewById(R.id.test);
        tvResult = (TextView) findViewById(R.id.result);

//do whatever magic to get your touch point
//MotionEvent event;
        ivTest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (heightRatio == 0) {
                    poo();
                }
                //get the distance from the left and top of the image bounds
                float scaledImageOffsetX = event.getX() - imageBounds.left;
                float scaledImageOffsetY = event.getY() - imageBounds.top - ivTest.getPaddingTop();

                //scale these distances according to the ratio of your scaling
                //For example, if the original image is 1.5x the size of the scaled
                //image, and your offset is (10, 20), your original image offset
                //values should be (15, 30).
                float originalImageOffsetX = scaledImageOffsetX * widthRatio;
                float yPosition = imagePositions[1] / density;
                float barY = actionBarHeight;
                float statusY = statusBarHeight;
                float originalImageOffsetY = scaledImageOffsetY * heightRatio - intrinsicHeight;
                Log.d("Original", "X: " + originalImageOffsetX + " Y: " + originalImageOffsetY);
                Log.d("barY", "barY: " + barY);
                Log.d("yPosition", "yPosition: " + yPosition);
                Log.d("statusY", "statusY: " + statusY);
                tvResult.setText("Original X:" + originalImageOffsetX+"Original Y:" + originalImageOffsetY);

                if (originalImageOffsetY > 260 && originalImageOffsetY < 310) {
                    if (originalImageOffsetX > 170 && originalImageOffsetX < 255) {
                        Toast.makeText(MainActivity.this, "Fish", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                if (originalImageOffsetY > 0) {
                    if (originalImageOffsetY < 65) {
                        Toast.makeText(MainActivity.this, "Red", Toast.LENGTH_SHORT).show();
                    } else if (originalImageOffsetY < 140) {
                        Toast.makeText(MainActivity.this, "Green", Toast.LENGTH_SHORT).show();
                    } else if (originalImageOffsetY < 240) {
                        Toast.makeText(MainActivity.this, "Blue", Toast.LENGTH_SHORT).show();
                    } else if (originalImageOffsetY < 330) {
                        Toast.makeText(MainActivity.this, "Orange", Toast.LENGTH_SHORT).show();
                    } else if (originalImageOffsetY < 435) {
                        Toast.makeText(MainActivity.this, "Brown", Toast.LENGTH_SHORT).show();
                    }
                }
//                if (originalImageOffsetX > 800 && originalImageOffsetY > 350 && originalImageOffsetY < 370) {
//                    if (originalImageOffsetX < 875) {
//                        Toast.makeText(MainActivity.this, "Community", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(MainActivity.this, "Wiki", Toast.LENGTH_SHORT).show();
//                    }
//                }
                return false;
            }
        });

    }
    float intrinsicWidth;
    private void poo() {
        Drawable drawable = ivTest.getDrawable();
        imageBounds = drawable.getBounds();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        density = dm.density;
        //original height and width of the bitmap
        intrinsicHeight = drawable.getIntrinsicHeight() / density;
         intrinsicWidth = drawable.getIntrinsicWidth() / density;

        imagePositions = getBitmapPositionInsideImageView(ivTest);

//height and width of the visible (scaled) image
        int yPositionTop = imagePositions[1];
//        int yPositionBottom= imagePositions[4];

        int scaledHeight = imagePositions[3];
        int scaledWidth = imagePositions[2];

//Find the ratio of the original image to the scaled image
//Should normally be equal unless a disproportionate scaling
//(e.g. fitXY) is used.
        heightRatio = intrinsicHeight / scaledHeight;
        widthRatio = intrinsicWidth / scaledWidth;

        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

//        Rect rectangle = new Rect();
//        Window window = getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
//         statusBarHeight = rectangle.top;
//        int contentViewTop =
//                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
//        int titleBarHeight= contentViewTop - statusBarHeight;
//
//        Log.i("*** Elenasys :: ", "StatusBar Height= " + statusBarHeight + " , TitleBar Height = " + titleBarHeight);
        statusBarHeight = getStatusBarHeight();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}

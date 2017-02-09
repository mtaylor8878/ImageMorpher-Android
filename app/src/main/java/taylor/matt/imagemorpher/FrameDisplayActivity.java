package taylor.matt.imagemorpher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * Activity to display completed frames once morphing process is complete
 *
 * @author Matthew Taylor
 */
public class FrameDisplayActivity extends AppCompatActivity {
    public static final int MAX_ANIMATION_SPEED = 10;

    private Bitmap[]        bmp;
    private int             currIndex;
    private ImageView       imageView;
    private int             direction;
    private NumberPicker    np;
    private boolean         playing;
    private Toast           frameNumber;
    private Handler         timerHandler;


    /**
     * Initialize Activity variables on creation
     *
     * @param savedInstanceState Default value
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_display);

        // Get file paths to images from intent data
        String[] filePaths;
        Intent intent = getIntent();
        filePaths = intent.getStringArrayExtra(MainActivity.FILE_PATHS);
        bmp = new Bitmap[filePaths.length];
        for(int i = 0; i < filePaths.length; i++)
            bmp[i] = BitmapFactory.decodeFile(filePaths[i]);

        np = (NumberPicker) findViewById(R.id.number_picker_speed);
        np.setMaxValue(MAX_ANIMATION_SPEED);
        np.setMinValue(1);
        imageView = (ImageView) findViewById(R.id.image_view_display);
        imageView.setImageBitmap(bmp[0]);
        currIndex = 0;
        direction = 1;
        playing = false;
        timerHandler = new Handler();

        // Click listener for the Previous Frame button
        ImageButton b = (ImageButton) findViewById(R.id.button_prev);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevImage();
                frameNumber.setText("Frame " + (currIndex + 1) + "/" + bmp.length);
                frameNumber.show();
            }
        });

        // Click listener for the Next Frame button
        b = (ImageButton) findViewById(R.id.button_next);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextImage();
                frameNumber.setText("Frame " + (currIndex + 1) + "/" + bmp.length);
                frameNumber.show();
            }
        });

        // Click listener for the Play Animation button
        b = (ImageButton) findViewById(R.id.button_play);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton b = (ImageButton) v;
                if(!playing) {
                    playing = true;
                    b.setImageResource(R.drawable.stop_icon);
                    imageView.setImageBitmap(bmp[0]);
                    timerHandler.postDelayed(playAnimation, 0);
                } else {
                    playing = false;
                    b.setImageResource(R.drawable.play_icon);
                    timerHandler.removeCallbacks(playAnimation);
                }
            }
        });

        // Initialize frame number toast
        frameNumber = Toast.makeText(getApplicationContext(),
                "", Toast.LENGTH_SHORT);
    }

    /**
     * Sets displayed image to the previous frame.
     */
    private void prevImage() {
        if(--currIndex == -1)
            currIndex = bmp.length - 1;
        imageView.setImageBitmap(bmp[currIndex]);
    }

    /**
     * Advances displayed image to the next frame.
     */
    private void nextImage() {
        if(++currIndex == bmp.length)
            currIndex = 0;
        imageView.setImageBitmap(bmp[currIndex]);
    }

    /**
     * Runnable to call to play the animation
     */
    Runnable playAnimation = new Runnable() {
        public void run () {
            int speed = ((MAX_ANIMATION_SPEED + 1) - np.getValue()) * 100;
            if(direction > 0)
                nextImage();
            else
                prevImage();
            if(currIndex == 0 || currIndex == bmp.length - 1) {
                timerHandler.postDelayed(this, speed * 3);
                direction *= -1;
            } else
                timerHandler.postDelayed(this, speed);
        }
    };


}

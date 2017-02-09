package taylor.matt.imagemorpher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Matt on 2017-01-17.
 */

public class DrawableImageView extends ImageView {
    private Paint pLine, pCircle;
    private ArrayList<Line> cLines;
    private Bitmap original;
    private int selectedLine;
    private boolean showLines;
    private int lastLine;
    public int leftBound, topBound, rightBound, bottomBound;

    public DrawableImageView(Context context) {
        super(context);
        init();
    }

    public DrawableImageView(Context context, AttributeSet as) {
        super(context, as);
        init();
    }

    public DrawableImageView(Context context, AttributeSet as, int defStyle) {
        super(context, as, defStyle);
        init();
    }

    public void setSelectedLine(int i){
        selectedLine = i;
        this.invalidate();
    }

    private void init() {
        pLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        pLine.setColor(Color.GREEN);
        pLine.setStyle(Paint.Style.STROKE);
        pLine.setStrokeWidth(3);
        pCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        pCircle.setColor(Color.RED);
        cLines = new ArrayList<>();
        lastLine = -1;
        selectedLine = -1;
        showLines = true;
    }

    public void resetView() {
        original = null;
        init();
        this.setImageResource(0);
        this.setBackgroundResource(R.drawable.image_view_bg);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(showLines) {
            for (int i = 0; i < cLines.size(); i++) {
                Line l = cLines.get(i);
                if (i == selectedLine)
                    pLine.setColor(Color.CYAN);
                canvas.drawLine(l.getStart().x, l.getStart().y, l.getEnd().x, l.getEnd().y, pLine);
                if (i == selectedLine)
                    pLine.setColor(Color.GREEN);
                for (int j = 0; j < 2; j++)
                    canvas.drawCircle(l.getPoint(j).x, l.getPoint(j).y, 10, pCircle);
            }
        }
    }

    public void setImageBitmap(Bitmap bmp) {
        int oHeight = this.getHeight();
        int oWidth = this.getWidth();
        int inHeight = bmp.getHeight();
        int inWidth = bmp.getWidth();
        double cRatio = (double)oWidth / oHeight;
        double iRatio = (double)inWidth / (double)inHeight;
        int newHeight, newWidth;
        if(iRatio > cRatio) {
            newWidth = oWidth;
            newHeight = (int)(oWidth / iRatio);
        } else {
            newWidth = (int)(oHeight * iRatio);
            newHeight = oHeight;
        }
        original = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, true);
        leftBound = (oWidth - newWidth) / 2;
        rightBound = oWidth - leftBound;
        topBound = (oHeight - newHeight) / 2;
        bottomBound = oHeight - topBound;
        super.setImageBitmap(bmp);
    }

    public Bitmap getBitmap() {
        return original;
    }

    public void showLines() {
        showLines = true;
        this.invalidate();
    }

    public void hideLines() {
        showLines = false;
        this.invalidate();
    }

    public void toggleLines() {
        showLines = !showLines;
        this.invalidate();
    }

    public void addLine(Point s, Point e) {
        cLines.add(new Line(s, e));
        ++lastLine;
    }

    public void addLine(Line l) {
        cLines.add(l);
        ++lastLine;
    }

    public void deleteLine(int index) {
        if(index < cLines.size() && index >= 0) {
            cLines.remove(index);
            lastLine--;
            invalidate();
        }
    }

    public void deleteSelected() {
        if(selectedLine != -1){
            cLines.remove(selectedLine);
            lastLine--;
            selectedLine = -1;
            invalidate();
        }
    }

    public Line[] getLineArray() {
        return cLines.toArray(new Line[cLines.size()]);
    }

    public boolean inBounds(Point p) {
        return (p.x >= leftBound && p.x < rightBound && p.y >= topBound && p.x < bottomBound);
    }

    public Line getLine(int index){
        return cLines.get(index);
    }

    public int getLastLine(){
        return lastLine;
    }

    public Pair<Integer, Integer> getClosestPointIndex(int x, int y) {
        int closestD = distance(x, y, cLines.get(0).getStart().x, cLines.get(0).getStart().y);
        Pair<Integer, Integer> closest = new Pair<>(0, 0);
        for(int i = 0; i < cLines.size(); i++){
            for(int j = 0; j < 2; j++) {
                int thisD = distance(x, y, cLines.get(i).getPoint(j).x, cLines.get(i).getPoint(j).y);
                if(thisD < closestD) {
                    closest = new Pair<>(i, j);
                    closestD = thisD;
                }
            }
        }
        return closest;
    }

    private int distance(int x, int y, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2));
    }
}
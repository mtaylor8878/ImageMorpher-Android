package taylor.matt.imagemorpher;

import android.graphics.Point;

/**
 * Container to store data on a Line.
 *
 * @author Matt
 * Created by Matt on 2017-01-17.
 */

public class Line {
    private Point[] points;
    public static int START_POINT = 0;
    public static int END_POINT = 1;

    public Line(Point s, Point e){
        points = new Point[2];
        points[START_POINT] = s;
        points[END_POINT] = e;
    }

    public Line(Line c) {
        points = new Point[2];
        points[START_POINT] = new Point(c.getStart());
        points[END_POINT] = new Point(c.getEnd());
    }

    public void setStart(Point s) {
        points[START_POINT] = s;
    }

    public void setEnd(Point e) {
        points[END_POINT] = e;
    }

    public void setPoint(int i, Point p) {
        points[i] = p;
    }

    public Point getStart() {
        return points[START_POINT];
    }

    public Point getEnd() {
        return points[END_POINT];
    }

    public Point getPoint(int i) {
        return points[i];
    }
}

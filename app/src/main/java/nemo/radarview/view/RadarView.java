package nemo.radarview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.math.BigDecimal;
import java.util.ArrayList;

import nemo.radarview.util.DisplayUtil;
import wh.sesamecreditview.R;

/**
 * Description: 雷达图
 *
 * @author Li Jianying
 * @version 2.0
 * @since 2016-8-30
 */
public class RadarView extends View {

    //默认值
    private float DEFAULT_LABEL_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
    private float DEFAULT_LABEL_DRAWABLE_PADDING = 10f;

    //默认颜色
    private int DEFAULT_LINE_COLOR = Color.parseColor("#dbdbdb");
    private int DEFAULT_OVERLAY_COLOR=Color.parseColor("#7bbff8");
    private  int DEFAULT_RADAR_BG_COLOR=Color.parseColor("#2195f4");
    private int DEFAULT_CENTER_TEXT_COLOR=Color.parseColor("#f7fafa");

    //中心点坐标点
    private Point mCenterPoint = new Point();

    //多边形半径
    private int mRadius;

    //画笔
    private Paint mDefaultPaint = new Paint();
    private Paint mLineDefaultPaint = new Paint();
    private Paint mOverlayPaint = new Paint();
    private Paint mCenterTextPaint = new Paint();
    private Paint mBitmapPaint = new Paint();
    private Paint mLabelPaint = new Paint();

    //文字区域
    private Rect mTextRect = new Rect();

    //图片与雷达图间距离
    private int mPicAndViewSpacing;

    private ArrayList<Region> mPicAreas = new ArrayList<Region>();
    private ArrayList<Bitmap> mPicBitmap;

    private int[] mPicResIds = new int[]{R.mipmap.ic_account, R.mipmap.ic_phone, R.mipmap.ic_safe_center,
            R.mipmap.ic_weather, R.mipmap.ic_pig_box};
    private String[] mLabelsStr = new String[]{"诈骗电话", "手机优化", "流量异常", "上网安全", "垃圾短信"};

    //各模块值
    private float[] scoreValues = new float[]{0, 0, 0, 0, 0};

    //模块最大值
    private int maxValue = 0;

    //雷达图最大值
    private float max = 100f;

    private OnItemClickListener onItemClickListener;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        loadPicBitmap();
        mPicAndViewSpacing = DisplayUtil.dip2px(context, 40);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {

        //雷达图背景色
        mDefaultPaint.setDither(true);
        mDefaultPaint.setAntiAlias(true);
        mDefaultPaint.setColor(DEFAULT_RADAR_BG_COLOR);
        mDefaultPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //线
        mLineDefaultPaint.setDither(true);
        mLineDefaultPaint.setAntiAlias(true);
        mLineDefaultPaint.setStrokeWidth(1f);
        mLineDefaultPaint.setColor(DEFAULT_LINE_COLOR);
        mLineDefaultPaint.setStyle(Paint.Style.STROKE);

        //值区域
        mOverlayPaint.setDither(true);
        mOverlayPaint.setAntiAlias(true);
        mOverlayPaint.setStrokeWidth(3f);
        mOverlayPaint.setColor(DEFAULT_OVERLAY_COLOR);
        mOverlayPaint.setAlpha(50);
        mOverlayPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //中心文本
        mCenterTextPaint.setDither(true);
        mCenterTextPaint.setAntiAlias(true);
        mCenterTextPaint.setColor(DEFAULT_CENTER_TEXT_COLOR);
        mCenterTextPaint.setFakeBoldText(true);
        mCenterTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));

        //图片
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setStyle(Paint.Style.FILL);

        //标签文本
        mLabelPaint.setTextSize(DEFAULT_LABEL_SIZE);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setDither(true);
        mLabelPaint.setColor(DEFAULT_CENTER_TEXT_COLOR);
        mLabelPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 加载图片
     */
    private void loadPicBitmap() {
        mPicBitmap = new ArrayList<>();
        for (int i = 0; i < mPicResIds.length; i++) {
            mPicBitmap.add(BitmapFactory.decodeResource(getResources(), mPicResIds[i]));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mPicAreas.size() == 0) {
                    return true;
                }
                for (int i = 0; i < mPicAreas.size(); i++) {
                    Region mPicArea = mPicAreas.get(i);
                    if (mPicArea.contains((int) event.getX(), (int) event.getY())) {
                        if (onItemClickListener != null) {
                            switch (i) {
                                case 0:
                                    onItemClickListener.onFraudPhoneClick(scoreValues[i]*max);
                                    break;
                                case 1:
                                    onItemClickListener.onMobileOptimizeClick(scoreValues[i]*max);
                                    break;
                                case 2:
                                    onItemClickListener.onTrafficClick(scoreValues[i]*max);
                                    break;
                                case 3:
                                    onItemClickListener.onNetSafeClick(scoreValues[i]*max);
                                    break;
                                case 4:
                                    onItemClickListener.onSpamMessageClick(scoreValues[i]*max);
                                    break;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
        super.onSizeChanged(width, height, oldwidth, oldheight);

        Point mPoint = getPICMaxHeightAndWidth();

        int mViewWidth = width - (mPoint == null ? 0 : 2 * mPoint.x) - 2 * mPicAndViewSpacing;
        int mViewHeight = height - (mPoint == null ? 0 : 2 * mPoint.y) - 2 * mPicAndViewSpacing;

        mCenterPoint.set(width / 2, height / 2);
        mRadius = mViewWidth > mViewHeight ? mViewHeight / 2 : mViewWidth / 2;
    }

    private Point getPICMaxHeightAndWidth() {
        if (mPicBitmap == null || mPicBitmap.size() == 0) {
            return null;
        }
        Point point = new Point();
        int maxHeight = 0, maxWidth = 0;
        for (int i = 0; i < mPicBitmap.size(); i++) {
            maxHeight = Math.max(maxHeight, mPicBitmap.get(i).getHeight());
            maxWidth = Math.max(maxWidth, mPicBitmap.get(i).getWidth());
        }
        //添加文字大小和文字间距
        maxHeight += (DEFAULT_LABEL_SIZE + DEFAULT_LABEL_DRAWABLE_PADDING);
        point.set(maxWidth, maxHeight);
        return point;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制图片
        int mPicValue = mRadius + mPicAndViewSpacing;
        ArrayList<PointF> mPICDefaultPointF = getPoints(mCenterPoint, mPicValue, mPicValue, mPicValue, mPicValue, mPicValue);
        drawBitmap(canvas, mPICDefaultPointF);

        //绘制默认灰色背景
        ArrayList<PointF> mDefaultPointF = getPoints(mCenterPoint, mRadius, mRadius, mRadius, mRadius, mRadius);
        ArrayList<Path> mDefaultPath = getPaths(mCenterPoint, mDefaultPointF);
        drawView(canvas, mDefaultPath, mDefaultPaint);

        //为灰色背景添加明显线条,以便区分块儿
        ArrayList<PointF> mLineDefaultPointF = getPoints(mCenterPoint, mRadius, mRadius, mRadius, mRadius, mRadius);
        ArrayList<Path> mLineDefaultPath = getPaths(mCenterPoint, mLineDefaultPointF);
        drawView(canvas, mLineDefaultPath, mLineDefaultPaint);

        //绘制显示色块
        ArrayList<PointF> mOverLayPointF = getPoints(mCenterPoint, scoreValues[0]*mRadius, scoreValues[1]*mRadius, scoreValues[2]*mRadius, scoreValues[3]*mRadius, scoreValues[4]*mRadius);
        ArrayList<Path> mOverLayPath = getPaths(mCenterPoint, mOverLayPointF);
        drawView(canvas, mOverLayPath, mOverlayPaint);
    }

    /**
     * 获取各个角上不同半径点集合
     *
     * @param center      中心点
     * @param angleValue1 角一值
     * @param angleValue2 角二值
     * @param angleValue3 角三值
     * @param angleValue4 角四值
     * @param angleValue5 角五值
     */
    private ArrayList<PointF> getPoints(Point center, double angleValue1, double angleValue2, double angleValue3, double angleValue4, double angleValue5) {
        ArrayList<PointF> points = new ArrayList<PointF>();
        points.add(new PointF(center.x, toFloat(center.y - angleValue1)));
        points.add(new PointF(toFloat(center.x + Math.sin(Math.toRadians(72D)) * angleValue2), toFloat(center.y - Math.cos(Math.toRadians(72d)) * angleValue2)));
        points.add(new PointF(toFloat(center.x + Math.cos(Math.toRadians(54D)) * angleValue3), toFloat(center.y + Math.sin(Math.toRadians(54d)) * angleValue3)));
        points.add(new PointF(toFloat(center.x - Math.cos(Math.toRadians(54D)) * angleValue4), toFloat(center.y + Math.sin(Math.toRadians(54d)) * angleValue4)));
        points.add(new PointF(toFloat(center.x - Math.sin(Math.toRadians(72D)) * angleValue5), toFloat(center.y - Math.cos(Math.toRadians(72d)) * angleValue5)));
        return points;
    }

    /**
     * 获取雷达图路径
     *
     * @param center
     * @param points
     * @return
     */
    private ArrayList<Path> getPaths(Point center, ArrayList<PointF> points) {
        if (points == null || points.size() == 0) {
            return null;
        }
        ArrayList<Path> paths = new ArrayList<Path>();
        for (int i = 0; i < points.size(); i++) {
            Path path = new Path();
            path.reset();
            path.moveTo(points.get(i).x, points.get(i).y);
            path.lineTo(center.x, center.y);
            path.lineTo(points.get(i == points.size() - 1 ? 0 : i + 1).x, points.get(i == points.size() - 1 ? 0 : i + 1).y);
            path.close();
            paths.add(path);
        }
        return paths;
    }

    /**
     * 绘制图片
     *
     * @param canvas
     * @param mPICDefaultPointF
     */
    private void drawBitmap(Canvas canvas, ArrayList<PointF> mPICDefaultPointF) {
        if (mPicBitmap == null || mPicBitmap.size() == 0) {
            return;
        }
        if (mPICDefaultPointF == null || mPICDefaultPointF.size() == 0) {
            return;
        }
        mPicAreas.clear();
        for (int i = 0; i < mPicBitmap.size(); i++) {
            PointF point = mPICDefaultPointF.get(i);
            Bitmap bitmap = mPicBitmap.get(i);
            Region area = new Region();
            area.set((int) (point.x - bitmap.getWidth() / 2), (int) (point.y - bitmap.getHeight() / 2), (int) (point.x + bitmap.getWidth() / 2), (int) (point.y + bitmap.getHeight() / 2));
            mPicAreas.add(area);
            canvas.drawBitmap(bitmap, point.x - bitmap.getWidth() / 2, point.y - bitmap.getHeight() / 2 - DEFAULT_LABEL_DRAWABLE_PADDING / 2 - DEFAULT_LABEL_SIZE / 2, mBitmapPaint);

            //绘制图片下的文本
            mLabelPaint.getTextBounds(mLabelsStr[i], 0, mLabelsStr[i].length(), mTextRect);
            canvas.drawText(mLabelsStr[i], point.x, point.y + (bitmap.getHeight() + DEFAULT_LABEL_SIZE) / 2, mLabelPaint);

        }

    }

    private void drawView(Canvas mCanvas, ArrayList<Path> paths, Paint mPaint) {
        if (paths == null || paths.size() == 0) {
            return;
        }
        for (int i = 0; i < paths.size(); i++) {
            mCanvas.drawPath(paths.get(i), mPaint);
        }

        mCenterTextPaint.getTextBounds(String.valueOf(maxValue), 0, String.valueOf(maxValue).length(), mTextRect);
        mCanvas.drawText(String.valueOf(maxValue), mCenterPoint.x - (mTextRect.width() / 2), mCenterPoint.y + (mTextRect.height() / 2), mCenterTextPaint);
    }


    public float toFloat(double d) {
        BigDecimal bigDecimal = new BigDecimal(d);
        return bigDecimal.floatValue();
    }

    /**
     * 分数值
     *
     * @param scoreValues
     */
    public void setData(float[] scoreValues) {
        if (scoreValues == null)
            return;
        this.scoreValues = scoreValues;
        float tempt = scoreValues[0];
        for (int i = 0; i < scoreValues.length; i++) {
            if (tempt < scoreValues[i]) {
                tempt = scoreValues[i];
            }
            scoreValues[i] /= max;
        }

        this.maxValue = (int) tempt;

        invalidateView();
    }

    /**
     * 设置雷达图最值
     *
     * @param max
     */
    public void setMax(int max) {
        this.max = max;
        for (int i = 0; i < scoreValues.length; i++) {
            scoreValues[i] /= max;
        }

        invalidateView();
    }

    /**
     * 刷新视图
     */
    public void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    /**
     * 模块点击回调
     */
    public interface OnItemClickListener {
        void onFraudPhoneClick(float value);

        void onMobileOptimizeClick(float value);

        void onTrafficClick(float value);

        void onNetSafeClick(float value);

        void onSpamMessageClick(float value);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

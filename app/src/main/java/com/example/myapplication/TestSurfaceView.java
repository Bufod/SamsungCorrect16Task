package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private DrawThread drawThread;

    public class DrawThread extends Thread {

        private SurfaceHolder surfaceHolder;

        private volatile boolean running = true;
        private Paint backgroundPaint = new Paint();
        private int towardPointX;
        private int towardPointY;
        int radius = 0;
        boolean touch = false;
        {
            backgroundPaint.setColor(Color.BLUE);
        }

        public DrawThread(Context context, SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void requestStop() {
            running = false;
        }

        public void setTowardPoint(int x, int y) {
            towardPointX = x;
            towardPointY = y;
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    try {
                        canvas.drawRect(0, 0,
                                canvas.getWidth(), canvas.getHeight(), backgroundPaint);

                        if (touch) {
                            Paint paint = new Paint();
                            paint.setColor(Color.YELLOW);
                            canvas.drawCircle(towardPointX, towardPointY, radius, paint);
                            radius += 5;
                        }

                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public TestSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getContext(),getHolder());
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.requestStop();
        boolean retry = true;
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        drawThread.setTowardPoint((int)event.getX(),
                (int)event.getY());
        drawThread.touch = true;
        drawThread.radius = 0;
        return super.onTouchEvent(event);
    }
}
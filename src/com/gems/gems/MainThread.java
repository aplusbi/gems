package com.gems.gems;

import android.view.SurfaceHolder;
import android.util.Log;
import android.graphics.Canvas;

public class MainThread extends Thread {
    private boolean running;
    private SurfaceHolder m_holder;
    private MainGameView m_view;

    private static final String TAG = MainThread.class.getSimpleName();

    public MainThread(SurfaceHolder holder, MainGameView view) {
        super();
        this.m_holder = holder;
        this.m_view = view;
    }

    public void setRunning(boolean r) {
        this.running = r;
    }

    @Override public void run() {
        Canvas canvas;
        long tickCount = 0L;
        Log.d(TAG, "Starting thread loop");
        while(running) {
            canvas = null;
            try {
                canvas = this.m_holder.lockCanvas();
                synchronized(this.m_holder) {
                    this.m_view.onDraw(canvas);
                }
            }
            finally {
                if(canvas != null) {
                    this.m_holder.unlockCanvasAndPost(canvas);
                }
            }
            ++tickCount;
        }
        Log.d(TAG, "Game loop executed " + tickCount + " loops");
    }
}

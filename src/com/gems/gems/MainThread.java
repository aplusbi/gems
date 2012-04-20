package com.gems.gems;

import android.view.SurfaceHolder;
import android.graphics.Canvas;

public class MainThread extends Thread {
    private boolean running;
    private SurfaceHolder m_holder;
    private MainGameView m_view;

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
        long time = System.currentTimeMillis();
        long lastTime = time;
        while(running) {
            canvas = null;
            try {
                canvas = this.m_holder.lockCanvas();
                synchronized(this.m_holder) {
                    
                    this.m_view.update(time - lastTime);
                    this.m_view.onDraw(canvas);
                    lastTime = time;
                    time = System.currentTimeMillis();
                }
            }
            finally {
                if(canvas != null) {
                    this.m_holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}

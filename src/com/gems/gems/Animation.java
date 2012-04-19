package com.gems.gems;

import android.graphics.Canvas;

public interface Animation {
    public void update(long time);
    public void draw(Canvas canvas);
    public boolean isDone();
}

package com.gems.gems;

import android.util.Log;
import android.graphics.Canvas;
import android.graphics.Bitmap;

public class Sprite {
    private Bitmap m_bitmap;
    private int m_x, m_y;
    private boolean m_touched;
    private static final String TAG = Sprite.class.getSimpleName();

    public Sprite(Bitmap bm, int x, int y)
    {
        m_bitmap = bm;
        m_x = x;
        m_y = y;
        m_touched = false;
    }

    public void setBitmap(Bitmap bm)
    {
        m_bitmap = bm;
    }

    public void setX(int x)
    {
        m_x = x;
    }

    public void setY(int y)
    {
        m_y = y;
    }

    public int getX()
    {
        return m_x;
    }

    public int getY()
    {
        return m_y;
    }

    public void setTouched(boolean t)
    {
        m_touched = t;
    }

    public boolean isTouched()
    {
        return m_touched;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(m_bitmap, m_x - (m_bitmap.getWidth()/2), m_y - (m_bitmap.getHeight()/2), null);
    }

    public void handleActionDown(int x, int y)
    {
        if(x > m_x - (m_bitmap.getWidth()/2) && x < m_x + (m_bitmap.getWidth()/2))
        {
            if(y > m_y - (m_bitmap.getHeight()/2) && y < m_y + (m_bitmap.getHeight()/2))
            {
                setTouched(true);
                Log.d(TAG, "Touched");
            }
            else
            {
                setTouched(false);
            }
        }
        else
        {
            setTouched(false);
        }
    }
}

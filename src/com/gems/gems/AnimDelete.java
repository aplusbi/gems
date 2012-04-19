package com.gems.gems;

import android.graphics.Canvas;
import android.graphics.Bitmap;

public class AnimDelete implements Animation {
    private boolean m_isRunning;
    private long m_time;
    private long m_elapsed;
    private int m_x;
    private int m_y;
    private int m_color;
    private int m_twidth;
    private int m_theight;
    private Bitmap m_bitmap;
    private int[][] m_board;
    private final int m_rate = 50;

    public AnimDelete(long time, Bitmap bm, int x, int y, int c, int[][] board)
    {
        m_isRunning = true;
        m_time = time;
        m_elapsed = 0;
        m_x = x;
        m_y = y;
        m_color = c;
        m_bitmap = bm;
        m_twidth = m_bitmap.getWidth();
        m_theight = m_bitmap.getHeight();
        m_board = board;
    }

	@Override
	public void update(long time) {
        m_elapsed += time;
        if(m_isRunning && m_elapsed > m_time)
        {
            m_isRunning = false;
            m_board[m_x][m_y] &= ~m_color;
        }
	}

	@Override
	public void draw(Canvas canvas) {
        // blink on and off
        if(m_isRunning && (m_elapsed / m_rate) % 2 == 0)
            canvas.drawBitmap(m_bitmap, m_x*m_twidth, m_y*m_theight, null);
	}
	@Override
	public boolean isDone() {
		return m_elapsed > m_time;
	}

}

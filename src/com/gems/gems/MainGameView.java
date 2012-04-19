package com.gems.gems;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.Random;
import java.util.ArrayList;

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback
{
    private MainThread thread;
    private static final String TAG = MainGameView.class.getSimpleName();
    private int[][] m_board;
    private final int m_width = 10;
    private final int m_height = 10;

    private Bitmap[] m_tiles;
    private Bitmap m_delete;
    private int m_twidth;
    private int m_theight;

    private ArrayList<Animation> m_anims;

    private final int RED = 1;
    private final int GREEN = 2;
    private final int BLUE = 4;
    private final int YELLOW = RED | GREEN;
    private final int PURPLE = RED | BLUE;
    private final int CYAN = GREEN | BLUE;
    private final int WHITE = RED | GREEN | BLUE;

    private final int DELETE = 8;
    private final int SHIFT = 16;
    private final int COLOR_MASK = ~(DELETE | SHIFT);

    private Random r;

	public MainGameView(Context context)
    {
		super(context);

        m_board = new int[m_width][m_height];
        r = new Random();
        for(int y=0; y<m_height; ++y)
        {
            for(int x=0; x<m_width; ++x)
            {
                m_board[x][y] = getRandom();
            }
        }


        loadTiles();

        m_anims = new ArrayList<Animation>();

        getHolder().addCallback(this);
        setFocusable(true);

        thread = new MainThread(getHolder(), this);
	}
    
    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override public void surfaceCreated(SurfaceHolder holder)
    {
        thread.setRunning(true);
        thread.start();
    }
    
    @Override public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        while(retry)
        {
            try
            {
                thread.join();
                retry = false;
            }
            catch(InterruptedException e)
            {
            }
        }
    }
    
    @Override public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
            Log.d(TAG, "Action move");
        }
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //if(event.getY() > getHeight() - 50) {
                //thread.setRunning(false);
            //}
            Log.d(TAG, "DOWN " + event.getX() + ", " + event.getY());
            int x = (int)event.getX() / m_twidth;
            int y = (int)event.getY() / m_theight;

            if(x < m_width && y < m_height)
            {
                removeTiles(x, y);
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
        }
    	return super.onTouchEvent(event);
    }

    void update(long time)
    {
        for(int i = 0; i < m_anims.size(); ++i)
        {
            if(m_anims.get(i).isDone())
                m_anims.remove(i);
            else
                m_anims.get(i).update(time);
        }

        scanForEmpties();
    }

    @Override protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.BLACK);
        for(int y = 0; y < m_height; ++y)
        {
            for(int x = 0; x < m_width; ++x)
            {
                canvas.drawBitmap(m_tiles[m_board[x][y] & COLOR_MASK], x*m_twidth, y*m_theight, null);
            }
        }

        // draw animations
        for(int i = 0; i < m_anims.size(); ++i)
        {
            m_anims.get(i).draw(canvas);
        }
    }

    void loadTiles()
    {
        m_tiles = new Bitmap[WHITE+1];
        m_tiles[0] = BitmapFactory.decodeResource(getResources(), R.drawable.transparent);
        m_tiles[RED] = BitmapFactory.decodeResource(getResources(), R.drawable.red);
        m_tiles[GREEN] = BitmapFactory.decodeResource(getResources(), R.drawable.green);
        m_tiles[BLUE] = BitmapFactory.decodeResource(getResources(), R.drawable.blue);
        m_tiles[YELLOW] = BitmapFactory.decodeResource(getResources(), R.drawable.yellow);
        m_tiles[PURPLE] = BitmapFactory.decodeResource(getResources(), R.drawable.purple);
        m_tiles[CYAN] = BitmapFactory.decodeResource(getResources(), R.drawable.cyan);
        m_tiles[WHITE] = BitmapFactory.decodeResource(getResources(), R.drawable.white);

        m_delete = BitmapFactory.decodeResource(getResources(), R.drawable.delete);

        m_twidth = m_tiles[0].getWidth();
        m_theight = m_tiles[0].getHeight();
    }

    int getRandom()
    {
        return 1 + r.nextInt(WHITE);
    }

    void shiftDown(int x, int y)
    {
        for(int i = y; i > 0; --i)
        {
            m_board[x][i] = m_board[x][i-1];
        }
        m_board[x][0] = getRandom();
    }

    boolean checkTile(int x, int y)
    {
        int color = m_board[x][y];
        int count = 0;
        if(color == RED || color == BLUE || color == GREEN)
        {
            if(x > 0 && (m_board[x-1][y] & color) != 0) ++count;
            if(x < (m_width - 1) && (m_board[x+1][y] & color) != 0) ++count;
            if(y > 0 && (m_board[x][y-1] & color) != 0) ++count;
            if(y < (m_height - 1) && (m_board[x][y+1] & color) != 0) ++count;
        }
        return count >= 2;
    }

    void removeTiles(int x, int y)
    {
        if(checkTile(x, y))
        {
            int color = m_board[x][y];
            removeColor(color, x, y);
        }
    }

    void removeColor(int color, int x, int y)
    {
        if((m_board[x][y] & DELETE) == 0 && (m_board[x][y] & color) != 0)
        {
            //m_board[x][y] &= ~color;
            m_board[x][y] |= DELETE;
            m_anims.add(new AnimDelete(500, m_delete, x, y, color | DELETE, m_board));

            if(x > 0) removeColor(color, x-1, y);
            if(x < m_width - 1) removeColor(color, x+1, y);
            if(y > 0) removeColor(color, x, y-1);
            if(y < m_height - 1) removeColor(color, x, y+1);
        }
    }

    void scanForEmpties()
    {
        for(int y = 0; y < m_height; ++y)
        {
            for(int x = 0; x < m_width; ++x)
            {
                if((m_board[x][y]) == 0)
                    shiftDown(x, y);
            }
        }
    }
}

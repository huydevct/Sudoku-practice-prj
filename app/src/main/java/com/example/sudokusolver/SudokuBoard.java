package com.example.sudokusolver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SudokuBoard extends View {
    private final int cellHighLightColor;
    private final int cellFillColor;
    private final int boardColor;

    private final int letterColor;
    private final int letterColorSolve;

    private final Paint boardColorPaint = new Paint();
    private final Paint cellFillColorPaint = new Paint();
    private final Paint cellHighLightColorPaint = new Paint();
    private final Paint letterPaint = new Paint();
    private final Rect letterPaintBounds = new Rect();
    private int celSize;

    private Solver solver = new Solver();


    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,
                0, 0);

        try {
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor, 0);
            cellFillColor = a.getInteger(R.styleable.SudokuBoard_cellFillColor, 0);
            cellHighLightColor = a.getInteger(R.styleable.SudokuBoard_cellHighLightColor, 0);
            letterColor = a.getInteger(R.styleable.SudokuBoard_letterColor, 0);
            letterColorSolve = a.getInteger(R.styleable.SudokuBoard_letterColorSolve, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);

        int dimension = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        celSize = dimension / 9;

        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(16);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);

        cellFillColorPaint.setStyle(Paint.Style.FILL);
        cellFillColorPaint.setAntiAlias(true);
        cellFillColorPaint.setColor(cellFillColor);

        cellHighLightColorPaint.setStyle(Paint.Style.FILL);
        cellHighLightColorPaint.setAntiAlias(true);
        cellHighLightColorPaint.setColor(cellHighLightColor);

        letterPaint.setStyle(Paint.Style.FILL);
        letterPaint.setAntiAlias(true);
        letterPaint.setColor(letterColor);

        colorCell(canvas, solver.getSelected_row(), solver.getSelected_column());
        canvas.drawRect(0, 0, getWidth(), getHeight(), boardColorPaint);
        drawBoard(canvas);
        drawNumber(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        boolean isValid;

        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN){
            solver.setSelected_row((int) Math.ceil(y/celSize));
            solver.setSelected_column((int) Math.ceil(x/celSize));
            isValid = true;
        }else{
            isValid = false;
        }

        return isValid;

    }

    private void drawNumber(Canvas canvas){
        letterPaint.setTextSize(celSize);


        for (int r=0; r<9; r++){
            for (int c=0; c<9; c++){
                if(solver.getBoard()[r][c] != 0){
                    String text = Integer.toString(solver.getBoard()[r][c]);
                    float width, heght;

                    letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
                    width = letterPaint.measureText(text);
                    heght = letterPaintBounds.height();

                    canvas.drawText(text,
                            (c*celSize) + ((celSize - width)/2),
                            (r*celSize+celSize)-((celSize - heght)/2),
                            letterPaint);
                }
            }
        }

        letterPaint.setColor(letterColorSolve);

        for (ArrayList<Object> letter : solver.getEmptyBoxIndex()){
            int r = (int) letter.get(0);
            int c = (int) letter.get(1);

            String text = Integer.toString(solver.getBoard()[r][c]);
            float width, heght;

            letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
            width = letterPaint.measureText(text);
            heght = letterPaintBounds.height();

            canvas.drawText(text,
                    (c*celSize) + ((celSize - width)/2),
                    (r*celSize+celSize)-((celSize - heght)/2),
                    letterPaint);
        }
    }

    private void colorCell(Canvas canvas,int r, int c){
        if(solver.getSelected_column() != -1 && solver.getSelected_row() != -1){
            canvas.drawRect((c-1)*celSize, 0, c*celSize, celSize*9, cellHighLightColorPaint);
            canvas.drawRect(0, (r-1)*celSize, 9*celSize, celSize*r, cellHighLightColorPaint);
            canvas.drawRect((c-1)*celSize, (r-1)*celSize, c*celSize, celSize*r, cellHighLightColorPaint);
        }

        invalidate();
    }

    private void drawThickLine() {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(10);
        boardColorPaint.setColor(boardColor);
    }

    private void drawThinLine() {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(4);
        boardColorPaint.setColor(boardColor);
    }

    private void drawBoard(Canvas canvas) {
        for (int c = 0; c < 10; c++) {
            if (c % 3 == 0) {
                drawThickLine();
            } else {
                drawThinLine();
            }

            canvas.drawLine(celSize * c,
                    0,
                    celSize * c,
                    getWidth(),
                    boardColorPaint);
        }

        for (int r = 0; r < 10; r++) {
            if (r % 3 == 0) {
                drawThickLine();
            } else {
                drawThinLine();
            }
            canvas.drawLine(0,
                    celSize*r,
                    getWidth(),
                    celSize*r,
                    boardColorPaint);
        }
    }

    public Solver getSolver(){
        return this.solver;
    }
}

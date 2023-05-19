package com.example.breakingblock

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.view.View

class MainView(context: Context) : View(context) {
    var m_ViewWidth: Int = 0   //화면의 넓이
    var m_ViewHeight: Int = 0  //화면의 높이

    lateinit var m_Img_btnLeft: Bitmap
    lateinit var m_Img_btnRight: Bitmap
    lateinit var m_Img_Paddle: Bitmap

    var m_BtnLeft_X : Int = 0           //왼쪽 버튼 X좌표
    var m_BtnLeft_Y : Int = 0           //왼쪽 버튼 Y좌표
    var m_BtnRight_X : Int = 0          //오른쪽 버튼 X좌표
    var m_BtnRight_Y : Int = 0         //오른쪽 버튼 Y좌표
    var m_Paddle_X : Int = 0          //패들 X좌표
    var m_Paddle_Y : Int = 0          //패들 Y좌표

    var m_Btn_W :Int = 0             //버튼 넓이
    var m_Btn_H :Int = 0            //버튼 높이
    var m_Paddle_W : Int = 0        // 패들 넓이
    var m_Paddle_H : Int = 0        // 패들 높이


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        m_ViewHeight = h;
        m_ViewWidth =w
        func_Setting()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.YELLOW)  // 배경 색 노랑으로 설정 (추후 변경 예정)
        canvas.drawBitmap(m_Img_Paddle,m_Paddle_X.toFloat(),m_Paddle_Y.toFloat(),null)
        canvas.drawBitmap(m_Img_btnLeft,m_BtnLeft_X.toFloat(),m_BtnLeft_Y.toFloat(),null)
        canvas.drawBitmap(m_Img_btnRight,m_BtnRight_X.toFloat(),m_BtnRight_Y.toFloat(),null)
    }

    fun func_Setting() {
        // 버튼 초기화
        m_Img_btnLeft = BitmapFactory.decodeResource(resources, R.drawable.btn_block_left)
        m_Img_btnRight = BitmapFactory.decodeResource(resources, R.drawable.btn_block_right)
        m_Btn_W = m_ViewWidth / 8 // 버튼 크기 설정 (정사각형)
        m_Btn_H = m_ViewWidth / 8 // 버튼 크기 설정 (정사각형)
        m_BtnLeft_X = 0
        m_BtnLeft_Y = m_ViewHeight - m_Btn_H
        m_BtnRight_X = m_ViewWidth - m_Btn_W
        m_BtnRight_Y = m_ViewHeight - m_Btn_H
        m_Img_btnLeft = Bitmap.createScaledBitmap(m_Img_btnLeft, m_Btn_W, m_Btn_H, false)
        m_Img_btnRight = Bitmap.createScaledBitmap(m_Img_btnRight, m_Btn_W, m_Btn_H, false)

        // 패들 초기화
        m_Img_Paddle = BitmapFactory.decodeResource(resources, R.drawable.block_paddle)
        m_Paddle_W = m_ViewWidth / 5
        m_Paddle_H = m_Paddle_W / 4
        m_Paddle_X = m_ViewWidth / 2 - m_Paddle_W / 2
        m_Paddle_Y = m_BtnLeft_Y - m_Paddle_H - m_Paddle_H / 2
        m_Img_Paddle = Bitmap.createScaledBitmap(m_Img_Paddle, m_Paddle_W, m_Paddle_H, false)
    }

}
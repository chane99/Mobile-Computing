package com.example.breakingblock

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
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

    var mRectBtnLeft: Rect = Rect() //버튼 터치 영역
    var mRectBtnRight: Rect = Rect() //버튼 터치 영역

    var m_Img_Ball: Bitmap? = null // 공
    var m_Ball_X : Int = 0 // 공의 X좌표
    var m_Ball_Y : Int = 0 //  공의 Y좌표
    var m_Ball_D : Int = 0 // 공의 지름
    var m_Ball_R : Int = 0 // 공의 반지름
    var m_Ball_Speed : Int = 0 // 공의 속도
    var m_Ball_SpeedX : Int = 0 // 공의 X방향 속도
    var m_Ball_SpeedY : Int = 0 // 공의 Y방향 속도

    var m_IsPlay : Boolean = false // 게임상태

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        m_ViewHeight = h
        m_ViewWidth =w
        func_Setting()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.YELLOW)  // 배경 색 노랑으로 설정 (추후 변경 예정)
        func_BallMove()

        m_Img_Ball?.let { canvas.drawBitmap(it,m_Ball_X.toFloat(),m_Ball_Y.toFloat(),null) }
        canvas.drawBitmap(m_Img_Paddle,m_Paddle_X.toFloat(),m_Paddle_Y.toFloat(),null)
        canvas.drawBitmap(m_Img_btnLeft,m_BtnLeft_X.toFloat(),m_BtnLeft_Y.toFloat(),null)
        canvas.drawBitmap(m_Img_btnRight,m_BtnRight_X.toFloat(),m_BtnRight_Y.toFloat(),null)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val w_X: Int = event?.x?.toInt() ?: 0
        val w_Y: Int = event?.y?.toInt() ?: 0
        val wKeyAction: Int = event?.action ?: 0

        when (wKeyAction) {
            MotionEvent.ACTION_DOWN -> {
                if(m_IsPlay) {
                    if(mRectBtnLeft.contains(w_X,w_Y)){ //왼쪽 버튼을 눌렀을때
                        m_IsTouch = true
                        m_Handler_btnLeft(0)
                    }else if(mRectBtnRight.contains(w_X,w_Y)){      //오른쪽 버튼을 눌렀을때
                        m_IsTouch = true
                        m_Handler_btnRight(0)
                    }
                }
                else {
                    if(mRectBtnLeft.contains(w_X,w_Y)){ //왼쪽 버튼을 눌렀을때
                        m_IsPlay = true
                        m_Ball_SpeedX = -m_Ball_Speed
                        m_Ball_SpeedY = -m_Ball_Speed
                    }else if(mRectBtnRight.contains(w_X,w_Y)){      //오른쪽 버튼을 눌렀을때
                        m_IsPlay = true
                        m_Ball_SpeedX = m_Ball_Speed
                        m_Ball_SpeedY = -m_Ball_Speed
                    }
                }
            }
            MotionEvent.ACTION_UP ->{
                m_IsTouch = false
            }
        }
        return true
    }

    private fun func_Setting() {
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
        mRectBtnLeft = Rect(m_BtnLeft_X, m_BtnLeft_Y, m_BtnLeft_X + m_Btn_W, m_BtnLeft_Y + m_Btn_H)
        mRectBtnRight = Rect(m_BtnRight_X, m_BtnRight_Y, m_BtnRight_X + m_Btn_W, m_BtnRight_Y + m_Btn_H)

        // 패들 초기화
        m_Img_Paddle = BitmapFactory.decodeResource(resources, R.drawable.block_paddle)
        m_Paddle_W = m_ViewWidth / 5
        m_Paddle_H = m_Paddle_W / 4
        m_Paddle_X = m_ViewWidth / 2 - m_Paddle_W / 2
        m_Paddle_Y = m_BtnLeft_Y - m_Paddle_H - m_Paddle_H / 2
        m_Img_Paddle = Bitmap.createScaledBitmap(m_Img_Paddle, m_Paddle_W, m_Paddle_H, false)

        // 공 초기화
        val tempBitmap = BitmapFactory.decodeResource(resources, R.drawable.block_ball)
        m_Ball_D = m_Paddle_H
        m_Ball_R = m_Ball_D / 2
        m_Ball_X = m_ViewWidth / 2 - m_Ball_R
        m_Ball_Y = m_Paddle_Y - m_Ball_D
        m_Img_Ball = Bitmap.createScaledBitmap(tempBitmap, m_Ball_D, m_Ball_D, false)

        m_Ball_Speed = m_Ball_R
        m_Ball_SpeedX = 0
        m_Ball_SpeedY = 0

        m_Handler_ViewReload(0)
    }

    //공 리셋 처리
    private fun func_Reset(){
        m_IsPlay = false
        m_IsTouch = false

        m_Ball_SpeedX = 0
        m_Ball_SpeedY = 0

        m_Paddle_X = m_ViewWidth / 2 - m_Paddle_W / 2
        m_Paddle_Y = m_BtnLeft_Y - m_Paddle_H - m_Paddle_H / 2

        m_Ball_X = m_ViewWidth / 2 - m_Ball_R
        m_Ball_Y = m_Paddle_Y - m_Ball_D
    }

    //공의 움직임 처리
    private fun func_BallMove(){
        if(m_IsPlay){
            m_Ball_X += m_Ball_SpeedX
            m_Ball_Y += m_Ball_SpeedY

            if(m_Ball_X <= 0 || m_Ball_X >= m_ViewWidth - m_Ball_D) {
                m_Ball_SpeedX *= -1
            }
            if(m_Ball_Y <= 0) {
                m_Ball_SpeedY *= -1
            }
            if(m_Ball_Y >= m_ViewHeight) {
                func_Reset()
            }
        }
    }

    var m_IsEnd: Boolean = true  //메모리 누수 방지를 위한 핸들러
    private fun m_Handler_ViewReload(p_DelayTime: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            invalidate()
            if(m_IsEnd)m_Handler_ViewReload(30)
        }, p_DelayTime)
    }

    var m_IsTouch : Boolean = false
    private fun m_Handler_btnLeft(p_DelayTime: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            m_Paddle_X = m_Paddle_X - m_Paddle_W/20     //Paddle X의 값이 계속 변하므로 수정이 필요
            if (m_Paddle_X < 0) {
                m_Paddle_X = 0
                m_IsTouch = false
            }
            if(m_IsTouch)m_Handler_btnLeft(30)
        }, p_DelayTime)
    }

    private fun m_Handler_btnRight(p_DelayTime: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            m_Paddle_X = m_Paddle_X + m_Paddle_W/20     //Paddle X의 값이 계속 변하므로 수정이 필요
            if (m_Paddle_X >= m_ViewWidth - m_Paddle_W) {
                m_Paddle_X = m_ViewWidth - m_Paddle_W
                m_IsTouch = false
            }
            if(m_IsTouch)m_Handler_btnRight(30)
        }, p_DelayTime)
    }
}
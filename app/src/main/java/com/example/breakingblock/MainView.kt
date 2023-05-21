package com.example.breakingblock

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View

class MainView(context: Context) : View(context) {
    var viewWidth: Int = 0   //화면의 넓이
    var viewHeight: Int = 0  //화면의 높이

    lateinit var imgBtnLeft: Bitmap
    lateinit var imgBtnRight: Bitmap
    lateinit var imgPaddle: Bitmap

    var btnLeftX: Int = 0           //왼쪽 버튼 X좌표
    var btnLeftY: Int = 0           //왼쪽 버튼 Y좌표
    var btnRightX: Int = 0          //오른쪽 버튼 X좌표
    var btnRightY: Int = 0         //오른쪽 버튼 Y좌표
    var paddleX: Int = 0          //패들 X좌표
    var paddleY: Int = 0          //패들 Y좌표

    var btnWidth: Int = 0             //버튼 넓이
    var btnHeight: Int = 0            //버튼 높이
    var paddleWidth: Int = 0        // 패들 넓이
    var paddleHeight: Int = 0        // 패들 높이

    var mRectBtnLeft: Rect = Rect() //버튼 터치 영역
    var mRectBtnRight: Rect = Rect() //버튼 터치 영역

    var imgBall: Bitmap? = null // 공
    var ballX: Int = 0 // 공의 X좌표
    var ballY: Int = 0 //  공의 Y좌표
    var ballDiameter: Int = 0 // 공의 지름
    var ballRadius: Int = 0 // 공의 반지름
    var ballSpeed: Int = 0 // 공의 속도
    var ballSpeedX: Int = 0 // 공의 X방향 속도
    var ballSpeedY: Int = 0 // 공의 Y방향 속도

    lateinit var m_Img_Block: Bitmap // 블럭 이미지
    val m_Arr_BlockList: ArrayList<Block> = ArrayList<Block>()

    var isPlay: Boolean = false // 게임상태

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewHeight = h
        viewWidth = w
        func_Setting()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.YELLOW)  // 배경 색 노랑으로 설정 (추후 변경 예정)
        func_BallMove()
        func_PaddleCheck()
        func_BlockCheck()

        imgBall?.let { canvas.drawBitmap(it, ballX.toFloat(), ballY.toFloat(), null) }
        canvas.drawBitmap(imgPaddle, paddleX.toFloat(), paddleY.toFloat(), null)
        canvas.drawBitmap(imgBtnLeft, btnLeftX.toFloat(), btnLeftY.toFloat(), null)
        canvas.drawBitmap(imgBtnRight, btnRightX.toFloat(), btnRightY.toFloat(), null)
        // 블럭
        for (w_Block in m_Arr_BlockList) {
            canvas.drawBitmap(m_Img_Block, w_Block.Block_X.toFloat(), w_Block.Block_Y.toFloat(), null)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX: Int = event?.x?.toInt() ?: 0  // 터치한 곳의 X좌표
        val touchY: Int = event?.y?.toInt() ?: 0  // 터치한 곳의 Y좌표
        val wKeyAction: Int = event?.action ?: 0

        when (wKeyAction) {
            MotionEvent.ACTION_DOWN -> {
                if (isPlay) {
                    if (mRectBtnLeft.contains(touchX, touchY)) { //왼쪽 버튼을 눌렀을때
                        isTouch = true
                        handlerBtnLeft(0)
                    } else if (mRectBtnRight.contains(touchX, touchY)) {      //오른쪽 버튼을 눌렀을때
                        isTouch = true
                        handlerBtnRight(0)
                    }
                } else {
                    if (mRectBtnLeft.contains(touchX, touchY)) { //왼쪽 버튼을 눌렀을때
                        isPlay = true
                        ballSpeedX = -ballSpeed
                        ballSpeedY = -ballSpeed
                    } else if (mRectBtnRight.contains(touchX, touchY)) {      //오른쪽 버튼을 눌렀을때
                        isPlay = true
                        ballSpeedX = ballSpeed
                        ballSpeedY = -ballSpeed
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                isTouch = false
            }
        }
        return true
    }

    private fun func_Setting() {
        // 버튼 초기화
        imgBtnLeft = BitmapFactory.decodeResource(resources, R.drawable.btn_block_left)
        imgBtnRight = BitmapFactory.decodeResource(resources, R.drawable.btn_block_right)
        btnWidth = viewWidth / 8 // 버튼 크기 설정 (정사각형)
        btnHeight = viewWidth / 8 // 버튼 크기 설정 (정사각형)
        btnLeftX = 0
        btnLeftY = viewHeight - btnHeight
        btnRightX = viewWidth - btnWidth
        btnRightY = viewHeight - btnHeight
        imgBtnLeft = Bitmap.createScaledBitmap(imgBtnLeft, btnWidth, btnHeight, false)
        imgBtnRight = Bitmap.createScaledBitmap(imgBtnRight, btnWidth, btnHeight, false)
        mRectBtnLeft = Rect(btnLeftX, btnLeftY, btnLeftX + btnWidth, btnLeftY + btnHeight)
        mRectBtnRight = Rect(btnRightX, btnRightY, btnRightX + btnWidth, btnRightY + btnHeight)

        // 패들 초기화
        imgPaddle = BitmapFactory.decodeResource(resources, R.drawable.block_paddle)
        paddleWidth = viewWidth / 5
        paddleHeight = paddleWidth / 4
        paddleX = viewWidth / 2 - paddleWidth / 2
        paddleY = btnLeftY - paddleHeight - paddleHeight / 2
        imgPaddle = Bitmap.createScaledBitmap(imgPaddle, paddleWidth, paddleHeight, false)

        // 공 초기화
        val tempBitmap = BitmapFactory.decodeResource(resources, R.drawable.block_ball)
        ballDiameter = paddleHeight
        ballRadius = ballDiameter / 2
        ballX = viewWidth / 2 - ballRadius
        ballY = paddleY - ballDiameter
        imgBall = Bitmap.createScaledBitmap(tempBitmap, ballDiameter, ballDiameter, false)

        ballSpeed = ballRadius
        ballSpeedX = 0
        ballSpeedY = 0

        // 블럭 생성
        func_MakeBlock()

        handlerViewReload(0)
    }

    //공 리셋 처리
    private fun func_Reset() {
        isPlay = false
        isTouch = false

        ballSpeedX = 0
        ballSpeedY = 0

        paddleX = viewWidth / 2 - paddleWidth / 2
        paddleY = btnLeftY - paddleHeight - paddleHeight / 2

        ballX = viewWidth / 2 - ballRadius
        ballY = paddleY - ballDiameter
    }

    //블럭 만들기
    private fun func_MakeBlock() {
        val w_Block_W = viewWidth / 7
        val w_Block_H = w_Block_W / 3

        m_Img_Block = BitmapFactory.decodeResource(resources, R.drawable.block_block01)
        m_Img_Block = Bitmap.createScaledBitmap(m_Img_Block, w_Block_W, w_Block_H, false)
        m_Arr_BlockList.clear()
        for (i in 0 until 3) {
            val w_Block_Y = w_Block_H * 2 + w_Block_H * i
            for (j in 0 until 7) {
                val w_Block_X = w_Block_W * j
                val w_Block = Block(w_Block_W, w_Block_H, w_Block_X, w_Block_Y)
                m_Arr_BlockList.add(w_Block)
            }
        }
    }

    //공의 움직임 처리
    private fun func_BallMove() {
        if (isPlay) {
            ballX += ballSpeedX
            ballY += ballSpeedY

            if (ballX <= 0 || ballX >= viewWidth - ballDiameter) {
                ballSpeedX *= -1
            }
            if (ballY <= 0) {
                ballSpeedY *= -1
            }
            if (ballY >= viewHeight) {
                func_Reset()
            }
        }
    }

    //패들 충돌 확인
    private fun func_PaddleCheck() {
        if (isPlay) {
            if (paddleX - ballRadius <= ballX && ballX <= paddleX + paddleWidth - ballRadius
                && paddleY - ballDiameter <= ballY && ballY <= paddleY - ballRadius
            ) {
                ballSpeedY *= -1
                if (paddleY - ballDiameter <= ballY) ballY =
                    paddleY - ballDiameter - (ballY - (paddleY - ballDiameter))
            } else if (paddleY - ballRadius <= ballY && ballY <= paddleY + ballRadius
                && paddleX - ballDiameter <= ballX && ballX <= paddleX + paddleWidth
            ) {
                ballSpeedX *= -1
                ballX += ballSpeedX
            }
        }

    }

    // 블럭 충돌 확인
    private fun func_BlockCheck() {
        for (w_Block in m_Arr_BlockList) {
            val w_BlockCheck = w_Block.IsClash(ballX, ballY, ballDiameter, ballRadius)
            when (w_BlockCheck) {
                0 -> continue
                1, 2 -> ballSpeedX *= -1
                3, 4 -> ballSpeedY *= -1
            }
            m_Arr_BlockList.remove(w_Block)
            break
        }
    }


    var isEnd: Boolean = true  //메모리 누수 방지를 위한 핸들러
    private fun handlerViewReload(delayTime: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            invalidate()
            if (isEnd) handlerViewReload(30)
        }, delayTime)
    }

    var isTouch: Boolean = false
    private fun handlerBtnLeft(delayTime: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            paddleX = paddleX - paddleWidth / 20
            if (paddleX < 0) {
                paddleX = 0
                isTouch = false
            }
            if (isTouch) handlerBtnLeft(30)
        }, delayTime)
    }

    private fun handlerBtnRight(delayTime: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            paddleX = paddleX + paddleWidth / 20
            if (paddleX >= viewWidth - paddleWidth) {
                paddleX = viewWidth - paddleWidth
                isTouch = false
            }
            if (isTouch) handlerBtnRight(30)
        }, delayTime)
    }
}

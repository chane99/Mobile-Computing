package com.example.breakingblock

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View

class GameView(context: Context) : View(context) {
    var viewWidth: Int = 0
    var viewHeight: Int = 0

    lateinit var imgPaddle: Bitmap

    var paddleX: Int = 0
    var paddleY: Int = 0

    var paddleWidth: Int = 0
    var paddleHeight: Int = 0

    var imgBall: Bitmap? = null
    var ballX: Float = 0F
    var ballY: Float = 0F
    var ballDiameter: Int = 0
    var ballRadius: Int = 0
    var ballSpeed: Float = 0F
    var ballSpeedX: Float = 0F
    var ballSpeedY: Float = 0F

    lateinit var m_Img_Block1: Bitmap
    lateinit var m_Img_Block2: Bitmap
    lateinit var m_Img_Block3: Bitmap
    val m_Arr_BlockList: ArrayList<Block> = ArrayList<Block>()

    var isPlay: Boolean = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewHeight = h
        viewWidth = w
        func_Setting()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        func_BallMove()
        func_PaddleCheck()
        func_BlockCheck()

        imgBall?.let { canvas.drawBitmap(it, ballX.toFloat(), ballY.toFloat(), null) }
        canvas.drawBitmap(imgPaddle, paddleX.toFloat(), paddleY.toFloat(), null)

        for (w_Block in m_Arr_BlockList) {
            val blockImg = when (w_Block.collisionCount) {
                3 -> m_Img_Block3
                2 -> m_Img_Block2
                else -> m_Img_Block1
            }
            canvas.drawBitmap(
                blockImg,
                w_Block.Block_X.toFloat(),
                w_Block.Block_Y.toFloat(),
                null
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX: Int = event?.x?.toInt() ?: 0
        val wKeyAction: Int = event?.action ?: 0

        when (wKeyAction) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (!isPlay) {
                    isPlay = true
                    ballSpeedX = if (touchX < viewWidth / 2) -ballSpeed else ballSpeed
                    ballSpeedY = -ballSpeed
                }
                paddleX = touchX - paddleWidth / 2
                if (paddleX < 0) {
                    paddleX = 0
                } else if (paddleX > viewWidth - paddleWidth) {
                    paddleX = viewWidth - paddleWidth
                }
            }
        }
        return true
    }

    private fun func_Setting() {
        imgPaddle = BitmapFactory.decodeResource(resources, R.drawable.block_paddle)
        paddleWidth = viewWidth / 5
        paddleHeight = paddleWidth / 4
        paddleX = viewWidth / 2 - paddleWidth / 2
        paddleY = viewHeight - paddleHeight * 2
        imgPaddle = Bitmap.createScaledBitmap(imgPaddle, paddleWidth, paddleHeight, false)

        val tempBitmap = BitmapFactory.decodeResource(resources, R.drawable.block_ball)
        ballDiameter = paddleHeight
        ballRadius = ballDiameter / 2
        ballX = (viewWidth / 2 - ballRadius).toFloat()
        ballY = (paddleY - ballDiameter).toFloat()
        imgBall = Bitmap.createScaledBitmap(tempBitmap, ballDiameter, ballDiameter, false)

        ballSpeed = ballRadius.toFloat()
        ballSpeedX = 0F
        ballSpeedY = 0F

        func_MakeBlock()

        handlerViewReload(0)
    }

    private fun func_Reset() {
        isPlay = false

        ballSpeedX = 0F
        ballSpeedY = 0F

        paddleX = viewWidth / 2 - paddleWidth / 2
        paddleY = viewHeight - paddleHeight * 2

        ballX = (viewWidth / 2 - ballRadius).toFloat()
        ballY = (paddleY - ballDiameter).toFloat()
    }

    // 벽돌 생성 - 빨간색: 3번 충돌 후 깨짐, 파란색: 2번 충돌 후 깨짐, 노란색: 1번 충돌 후 깨짐
    // 빨간색 -> 파란색 -> 노란색 순으로 색깔 변화
    private fun func_MakeBlock() {
        val w_Block_W = viewWidth / 7
        val w_Block_H = w_Block_W / 3

        m_Img_Block1 = BitmapFactory.decodeResource(resources, R.drawable.block_block01)
        m_Img_Block2 = BitmapFactory.decodeResource(resources, R.drawable.block_block02)
        m_Img_Block3 = BitmapFactory.decodeResource(resources, R.drawable.block_block03)

        m_Img_Block1 = Bitmap.createScaledBitmap(m_Img_Block1, w_Block_W, w_Block_H, false)
        m_Img_Block2 = Bitmap.createScaledBitmap(m_Img_Block2, w_Block_W, w_Block_H, false)
        m_Img_Block3 = Bitmap.createScaledBitmap(m_Img_Block3, w_Block_W, w_Block_H, false)

        m_Arr_BlockList.clear()
        for (i in 0 until 3) {
            val w_Block_Y = w_Block_H * 2 + w_Block_H * i
            for (j in 0 until 7) {
                val w_Block_X = w_Block_W * j
                val w_Block: Block
                if (i == 0) {
                    w_Block = Block(w_Block_W, w_Block_H, w_Block_X, w_Block_Y, m_Img_Block3, 3)
                } else if (i == 1) {
                    w_Block = Block(w_Block_W, w_Block_H, w_Block_X, w_Block_Y, m_Img_Block2, 2)
                } else {
                    w_Block = Block(w_Block_W, w_Block_H, w_Block_X, w_Block_Y, m_Img_Block1, 1)
                }
                m_Arr_BlockList.add(w_Block)
            }
        }
    }


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
            // 패들 중앙의 X좌표
            val paddleCenterX = paddleX + paddleWidth / 2

            // 패들 길이 9등분한 길이
            val intervalX = paddleWidth / 9

            // 공과 패들 충돌 시
            if (paddleX - ballRadius <= ballX && ballX <= paddleX + paddleWidth + ballRadius
                && paddleY - ballDiameter <= ballY && ballY <= paddleY - ballRadius
            ) {
                // 공의 Y축 속도를 뒤집음
                ballSpeedY = -ballSpeed
                ballY = (paddleY - ballDiameter).toFloat()

                if (ballX <= paddleX + intervalX) {
                    ballSpeedX = -ballSpeed
                }
                else if (ballX <= paddleX + intervalX * 2) {
                    ballSpeedX = -ballSpeed * 3 / 4
                }
                else if (ballX <= paddleX + intervalX * 3) {
                    ballSpeedX = -ballSpeed / 2
                    ballSpeedY = (ballSpeedY * 1.1).toFloat()
                }
                else if (ballX <= paddleX + intervalX * 4) {
                    ballSpeedX = -ballSpeed / 3
                    ballSpeedY = (ballSpeedY * 1.3).toFloat()
                }
                else if (ballX <= paddleX + intervalX * 5) {
                    ballSpeedX = 0F
                    ballSpeedY = (ballSpeedY * 1.5).toFloat()
                }
                else if (ballX <= paddleX + intervalX * 6) {
                    ballSpeedX = ballSpeed / 3
                    ballSpeedY = (ballSpeedY * 1.3).toFloat()
                }
                else if (ballX <= paddleX + intervalX * 7) {
                    ballSpeedX = ballSpeed / 2
                    ballSpeedY = (ballSpeedY * 1.1).toFloat()
                }
                else if (ballX <= paddleX + intervalX * 8) {
                    ballSpeedX = ballSpeed * 3 / 4
                }
                else {
                    ballSpeedX = ballSpeed
                }
            }

            // 아래쪽으로 공이 벗어난 경우
            else if (paddleY - ballRadius <= ballY && ballY <= paddleY + ballRadius
                && paddleX - ballDiameter <= ballX && ballX <= paddleX + paddleWidth + ballDiameter
            ) {
                // 공의 Y축 속도를 뒤집음
                ballSpeedY *= -1
                ballY += ballSpeedY
            }
        }
    }

    // 블럭 충돌 확인
    private fun func_BlockCheck() {
        val iterator = m_Arr_BlockList.iterator()
        while (iterator.hasNext()) {
            val w_Block = iterator.next()
            val w_BlockCheck = w_Block.IsClash(ballX, ballY, ballDiameter, ballRadius)
            when (w_BlockCheck) {
                0 -> continue
                1, 2 -> {
                    ballSpeedX *= -1
                    w_Block.collisionCount--
                }
                3, 4 -> {
                    ballSpeedY *= -1
                    w_Block.collisionCount--
                }
            }
            if (w_Block.collisionCount <= 0) {
                iterator.remove()
            }
            break
        }
    }


    var isEnd: Boolean = true  //메모리 누수 방지를 위한 핸들러
    private fun handlerViewReload(delayTime: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            invalidate()
            if (isEnd) handlerViewReload(0)
        }, delayTime)
    }
}

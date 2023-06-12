package com.example.breakingblock

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min


class GameView(context: Context) : View(context) {
    var ballSpeed: Float = 0F
    var ballSpeedX: Float = 0F
    var ballSpeedY: Float = 0F
    var savedBallSpeedX: Float = 0F
    var savedBallSpeedY: Float = 0F
    var lives: Int = 3
    var viewWidth: Int = 0
    var viewHeight: Int = 0
    var itemActive = false  // 아이템이 활성화 상태인지 여부
    val itemRadius = 30    // 아이템의 반지름
    val pop = MediaPlayer.create(context, R.raw.pop)
    lateinit var imgPaddle: Bitmap
    lateinit var pauseBtn: Bitmap

    var paddleX: Int = 0
    var paddleY: Int = 0

    var paddleWidth: Int = 0
    var paddleHeight: Int = 0
    var originalPaddleWidth: Int = 0
    var isExpanded: Boolean = false
    var startTime: Long = 0
    val expandDuration: Long = 5 * 1000 // 5 seconds in milliseconds

    var imgBall: Bitmap? = null
    var ballX: Float = 0F
    var ballY: Float = 0F
    var ballDiameter: Int = 0
    var ballRadius: Int = 0

    var pauseBtnWidth : Int = 0
    var pauseBtnHeight: Int = 0
    var pauseBtnX : Int = 0
    var pauseBtnY : Int = 0
    private var pauseBtnPressed = false


    lateinit var m_Img_Block1: Bitmap
    lateinit var m_Img_Block2: Bitmap
    lateinit var m_Img_Block3: Bitmap
    val m_Arr_BlockList: ArrayList<Block> = ArrayList<Block>()

    var isPlay: Boolean = false

    var score: Int = 0 // 점수 초기화
    var heartX :Float =0F // 목숨아이템의 X 좌표
    var heartY :Float =0F // 목숨아이템의 Y 좌표
    var longX :Float =0F // 롱아이템의 X 좌표
    var longY :Float =0F // 롱아이템의 Y 좌표
    var longWidth: Int = 0
    var longHeight: Int = 0
    var heartWidth: Int = 0
    var heartHeight: Int = 0
    var longActive = false // 롱아이템 활성화 여부
    lateinit var longItem: Bitmap
    lateinit var heartItem: Bitmap
    lateinit var superItem:Bitmap
    var superActive=false
    var superX:Float=0F
    var superY:Float=0F
    var superWidth:Int=0
    var superHeight:Int=0




    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewHeight = h
        viewWidth = w
        func_Setting()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.WHITE)
        func_BlockCheck()
        func_BallMove()
        func_PaddleCheck()


        imgBall?.let { canvas.drawBitmap(it, ballX, ballY, null) }
        canvas.drawBitmap(imgPaddle, paddleX.toFloat(), paddleY.toFloat(), null)
        canvas.drawBitmap(pauseBtn,pauseBtnX.toFloat(),pauseBtnY.toFloat(),null)

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
         val paint = Paint()
        paint.setColor(Color.RED)

        //item 생성
        if (itemActive) {
            canvas.drawBitmap(heartItem, heartX, heartY,null)
        }

        // 롱아이템 그리기
        if (longActive && longItem != null) {
            canvas?.drawBitmap(longItem!!, longX, longY, null)
        }
        
         if(superActive && superItem !=null){
            canvas?.drawBitmap(superItem!!,superX,superY,null)
        }

        // 초기화
        val heartDrawable = resources.getDrawable(R.drawable.life, null)
        val heartWidth = 80
        val heartHeight = 80

        val verticalGap = 15

        for (i in 0 until lives) {
            val xPosition = i * (heartWidth + 10) + 10 * (i + 1)
            val yPosition = verticalGap + heartHeight

            heartDrawable.setBounds(xPosition, yPosition - heartHeight, xPosition + heartWidth, yPosition)
            heartDrawable.draw(canvas)
        }


        // 점수 표시
        val scoreText = "점수: $score"
        val scorePaint = Paint()
        scorePaint.color = Color.BLACK
        scorePaint.textSize = 70f
        val textWidth = scorePaint.measureText(scoreText)
        val x = (width - textWidth) / 2
        val y = 70f
        canvas.drawText(scoreText, x, y, scorePaint)


    }
    //일시정지시 화면을 보여주는 메소드
    fun showCustomDialog() {
        val dialogFragment = PauseDialogFragment()
        dialogFragment.show((context as AppCompatActivity).supportFragmentManager, "PauseDialog")
        dialogFragment.setOnContinueClickListener {
            isPlay = true
            ballSpeedX = savedBallSpeedX
            ballSpeedY = savedBallSpeedY
            pauseBtnPressed = false
        }
        dialogFragment.setOnExitClickListener {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
        dialogFragment.setOnRetryClickListener {
            func_Setting()
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX: Int = event?.x?.toInt() ?: 0
        val touchY: Int = event?.y?.toInt() ?: 0
        val wKeyAction: Int = event?.action ?: 0

        if (lives <= 0) {
            if (wKeyAction == MotionEvent.ACTION_DOWN) {
                lives = 3 // Reset lives
                func_Setting() // Reset everything
            }
            return true
        }

        when (wKeyAction) {
            MotionEvent.ACTION_DOWN -> {
                // pause 버튼의 영역이 눌렸는지 확인
                if (touchX >= pauseBtnX && touchX <= pauseBtnX + pauseBtnWidth &&
                    touchY >= pauseBtnY && touchY <= pauseBtnY + pauseBtnHeight) {
                    savedBallSpeedY = ballSpeedY
                    savedBallSpeedX = ballSpeedX
                    pauseBtnPressed = true
                    isPlay = false
                    showCustomDialog()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if(pauseBtnPressed){
                    return true
                }
                if (!isPlay) {
                    paddleX = touchX - paddleWidth / 2
                    if (paddleX < 0) {
                        paddleX = 0
                    } else if (paddleX > viewWidth - paddleWidth) {
                        paddleX = viewWidth - paddleWidth
                    }
                    ballX = (paddleX + paddleWidth / 2 - ballRadius).toFloat()
                } else {
                    paddleX = touchX - paddleWidth / 2
                    if (paddleX < 0) {
                        paddleX = 0
                    } else if (paddleX > viewWidth - paddleWidth) {
                        paddleX = viewWidth - paddleWidth
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if(pauseBtnPressed){
                    return true
                }
                if (!isPlay && !(touchX >= pauseBtnX && touchX <= pauseBtnX + pauseBtnWidth &&
                            touchY >= pauseBtnY && touchY <= pauseBtnY + pauseBtnHeight)) {
                    isPlay = true
                    ballSpeedX = if (touchX < viewWidth / 2) -ballSpeed else ballSpeed
                    ballSpeedY = -ballSpeed
                }
            }
        }
        return true
    }







    private fun func_Setting() {
        imgPaddle = BitmapFactory.decodeResource(resources, R.drawable.block_paddle)
        pauseBtn = BitmapFactory.decodeResource(resources, R.drawable.pause_btn)
        originalPaddleWidth = viewWidth / 5
        paddleWidth = originalPaddleWidth
        paddleHeight = paddleWidth / 4
        paddleX = viewWidth / 2 - paddleWidth / 2
        paddleY = viewHeight - paddleHeight * 2
        pauseBtnWidth = (viewWidth / 10)
        pauseBtnHeight= pauseBtnWidth
        pauseBtnX = viewWidth - pauseBtnWidth
        pauseBtnY = 0
        imgPaddle = Bitmap.createScaledBitmap(imgPaddle, paddleWidth, paddleHeight, false)
        pauseBtn = Bitmap.createScaledBitmap(pauseBtn, pauseBtnWidth, pauseBtnHeight, false)
        lives = 3
        score = 0
        pauseBtnPressed = false
        heartItem = BitmapFactory.decodeResource(resources, R.drawable.life)
        heartWidth = viewWidth / 14
        heartHeight = viewWidth / 14
        heartItem = Bitmap.createScaledBitmap(heartItem, heartWidth, heartHeight, false)

        longItem = BitmapFactory.decodeResource(resources, R.drawable.long_item)
        longWidth = viewWidth / 7
        longHeight = viewWidth / 7
        longItem = Bitmap.createScaledBitmap(longItem, longWidth, longHeight, false)
        isExpanded = false
        startTime = 0 // startTime 초기화
        superItem=BitmapFactory.decodeResource(resources, R.drawable.skeleton)
        superWidth=viewWidth/10
        superHeight=viewWidth/10
        superItem=Bitmap.createScaledBitmap(superItem, superWidth, superHeight, false)

        val tempBitmap = BitmapFactory.decodeResource(resources, R.drawable.block_ball)
        ballDiameter = viewWidth / 21  // 블럭의 높이와 동일
        ballRadius = ballDiameter / 2
        ballX = (paddleX + paddleWidth / 2 - ballRadius).toFloat()
        ballY = (paddleY - ballDiameter).toFloat()
        imgBall = Bitmap.createScaledBitmap(tempBitmap, ballDiameter, ballDiameter, false)

        ballSpeed = ballRadius.toFloat()
        ballSpeedX = 0F
        ballSpeedY = 0F

        func_MakeBlock()

        handlerViewReload(0)
    }

    private fun func_Reset() {
        lives -= 1
        if (lives <= 0) {
            isPlay = false
            val sound = MediaPlayer.create(context, R.raw.fail)
            sound.setVolume(1.0F, 1.0F)
            sound.start()
            sound.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }
        showFinishview()

        } else {
            isPlay = false

            ballSpeedX = 0F
            ballSpeedY = 0F

            paddleX = viewWidth / 2 - paddleWidth / 2
            paddleY = viewHeight - paddleHeight * 2

            ballX = (viewWidth / 2 - ballRadius).toFloat()
            ballY = (paddleY - ballDiameter).toFloat()

            paddleWidth = originalPaddleWidth
            imgPaddle = Bitmap.createScaledBitmap(imgPaddle, paddleWidth, paddleHeight, false)
            isExpanded = false
            startTime = 0 // startTime 초기화
        }

    }


    // 벽돌 생성 - 빨간색: 3번 충돌 후 깨짐, 파란색: 2번 충돌 후 깨짐, 노란색: 1번 충돌 후 깨짐
    // 빨간색 -> 파란색 -> 노란색 순으로 색깔 변화
    private fun func_MakeBlock() {
        val blockRowCount = 7       // 행의 개수
        val blockColumnCount = 7    // 열의 개수
        val w_Block_W = viewWidth / 7
        val w_Block_H = w_Block_W / 3

        m_Img_Block1 = BitmapFactory.decodeResource(resources, R.drawable.block_block01)
        m_Img_Block2 = BitmapFactory.decodeResource(resources, R.drawable.block_block02)
        m_Img_Block3 = BitmapFactory.decodeResource(resources, R.drawable.block_block03)

        m_Img_Block1 = Bitmap.createScaledBitmap(m_Img_Block1, w_Block_W, w_Block_H, false)
        m_Img_Block2 = Bitmap.createScaledBitmap(m_Img_Block2, w_Block_W, w_Block_H, false)
        m_Img_Block3 = Bitmap.createScaledBitmap(m_Img_Block3, w_Block_W, w_Block_H, false)

        m_Arr_BlockList.clear()
        for (row in 0 until blockRowCount) {
            val w_Block_Y = w_Block_H * row + 120   // Y좌표에 120을 더해 블록을 아래로 이동

            val blockColor = when (row) {
                0 -> m_Img_Block3    // 빨간색 블록
                1, 2 -> m_Img_Block2 // 파란색 블록
                else -> m_Img_Block1  // 노란색 블록
            }
            for (column in 0 until blockColumnCount) {
                val w_Block_X = w_Block_W * column
                val collisionCount = when (row) {
                    0 -> 3    // 빨간색 블록
                    1, 2 -> 2 // 파란색 블록
                    else -> 1  // 노란색 블록
                }
                val w_Block = Block(w_Block_W, w_Block_H, w_Block_X, w_Block_Y, blockColor, collisionCount)
                m_Arr_BlockList.add(w_Block)
            }

        }

        val blueBlocks = m_Arr_BlockList.filter { it.collisionCount == 2 && it.img == m_Img_Block2 }
            .shuffled()
            .take(7)
        for (block2 in blueBlocks) {
            block2.img = m_Img_Block3
            block2.collisionCount = 3
        }

        val yellowBlocks = m_Arr_BlockList.filter { it.collisionCount == 1 && it.img == m_Img_Block1 }
            .shuffled()
            .take(7)

        for (block in yellowBlocks) {
            block.img = m_Img_Block2
            block.collisionCount = 2
        }

        val redBlocks = m_Arr_BlockList.filter { it.collisionCount == 3 && it.img == m_Img_Block3 }
            .shuffled()
            .take(3)
        for (block in redBlocks) {
            block.img = m_Img_Block1
            block.collisionCount = 1
        }
    }

    // 추가되는 블럭 생성 로직
    private fun addAdditionalBlocks() {
        val blockRowCount = 8       // 증가된 행의 개수
        val blockColumnCount = 7    // 열의 개수

        val w_Block_W = viewWidth / blockColumnCount
        val w_Block_H = w_Block_W / 3

        // 기존 블록들을 아래로 이동시키기
        for (block in m_Arr_BlockList) {
            block.Block_Y = block.Block_Y + w_Block_H
            if (block.Block_Y >= 2.5*paddleY) {
                showFinishview()

            }
        }

        // 추가된 행의 블록 생성
        val additionalBlocks = mutableListOf<Block>()
        val random = Random()

        for (column in 0 until blockColumnCount) {
            val w_Block_X = w_Block_W * column
            val w_Block_Y = 120   // Y좌표에 120을 더해 블록을 아래로 이동

            val blockColor = when (random.nextInt(3)) {
                0 -> m_Img_Block3    // 빨간색 블록
                1 -> m_Img_Block2    // 파란색 블록
                else -> m_Img_Block1  // 노란색 블록
            }

            val collisionCount = when (blockColor) {
                m_Img_Block3 -> 3    // 빨간색 블록
                m_Img_Block2 -> 2    // 파란색 블록
                else -> 1            // 노란색 블록
            }

            val w_Block = Block(w_Block_W, w_Block_H, w_Block_X, w_Block_Y, blockColor, collisionCount)
            additionalBlocks.add(w_Block)
        }

        m_Arr_BlockList.addAll(additionalBlocks)
        invalidate()
    }



    private fun func_BallMove() {
        if (isPlay) {
            ballX += ballSpeedX
            ballY += ballSpeedY

            if (ballX <= 0) {
                ballSpeedX *= -1
                ballX = 0F
            } else if (ballX >= viewWidth - ballDiameter) {
                ballSpeedX *= -1
                ballX = (viewWidth - ballDiameter).toFloat()
            }

            if (ballY <= 0) {
                ballSpeedY *= -1
                ballY = 0F
            } else if (ballY >= viewHeight) {
                func_Reset()
                val sound = MediaPlayer.create(context, R.raw.falling)
                sound.setVolume(1.0F, 1.0F)
                sound.start()
            }
        }
    }




    //패들 충돌 확인
    private fun func_PaddleCheck() {
        if (isPlay) {

            // 패들 길이 9등분한 길이
            val intervalX = paddleWidth / 10

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
                    ballSpeedX = -ballSpeed / 4
                    ballSpeedY = (ballSpeedY * 1.5).toFloat()
                }
                else if (ballX <= paddleX + intervalX * 6) {
                    ballSpeedX = ballSpeed / 4
                    ballSpeedY = (ballSpeedY * 1.3).toFloat()
                }
                else if (ballX <= paddleX + intervalX * 7) {
                    ballSpeedX = ballSpeed / 3
                    ballSpeedY = (ballSpeedY * 1.1).toFloat()
                }
                else if (ballX <= paddleX + intervalX * 8) {
                    ballSpeedX = ballSpeed /2
                }
                else if (ballX <= paddleX + intervalX * 9) {
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
        var collisionOccurred = false
        val blocksToRemove = ArrayList<Block>()

        for (w_Block in m_Arr_BlockList) {
            val w_BlockCheck = w_Block.IsClash(ballX, ballY, ballRadius)
            when (w_BlockCheck) {
                0 -> continue
                1, 2 -> {
                    pop.start()
                    ballSpeedX *= -1
                    w_Block.collisionCount--
                    collisionOccurred = true

                }
                3, 4 -> {
                    pop.start()
                    ballSpeedY *= -1
                    w_Block.collisionCount--
                    collisionOccurred = true
                }
            }
            if (w_Block.collisionCount <= 0) {
                blocksToRemove.add(w_Block)
                if (!itemActive && Math.random() < 0.1) {
                    // 확률을 조정하여 아이템이 생성되는 빈도를 조절할 수 있습니다.
                    // 여기서는 10%의 확률로 아이템이 생성되도록 설정하였습니다.
                    itemActive = true
                    heartX = w_Block.Block_X.toFloat() + (w_Block.Block_W / 2)
                    heartY = w_Block.Block_Y.toFloat() + (w_Block.Block_H / 2)
                }
                if (!longActive && Math.random() < 0.1) {
                    // 확률을 조정하여 롱아이템이 생성되는 빈도를 조절할 수 있습니다.
                    // 여기서는 10%의 확률로 아이템이 생성되도록 설정하였습니다.
                    longActive = true
                    longX= w_Block.Block_X.toFloat() + (w_Block.Block_W / 2)
                    longY = w_Block.Block_Y.toFloat() + (w_Block.Block_H / 2)
                }
                 if (!superActive && Math.random() < 0.1) {
                     // 확률을 조정하여 슈퍼아이템이 생성되는 빈도를 조절할 수 있습니다.
                     // 여기서는 10%의 확률로 아이템이 생성되도록 설정하였습니다.
                    superActive = true
                    superX = w_Block.Block_X.toFloat() + (w_Block.Block_W / 2)
                    superY = w_Block.Block_Y.toFloat() + (w_Block.Block_H / 2)
                }
                score += 1 // 블럭이 사라질 때마다 점수 1점 추가
            }
        }

        m_Arr_BlockList.removeAll(blocksToRemove)

        // 블럭 개수 35개보다 줄어들면 블럭 한 줄(7개) 추가
        if (m_Arr_BlockList.size < 35) {
            addAdditionalBlocks()

        }

        if (collisionOccurred) {
            invalidate()
        }



        if (itemActive) {
            heartY += 10
            // 아이템이 화면 아래로 벗어나면 비활성화
            if (heartY > viewHeight) {
                itemActive = false
            } else {
                // 아이템과 패들의 충돌 체크
                if (heartY + itemRadius >= paddleY &&
                    heartX + itemRadius >= paddleX &&
                    heartX - itemRadius <= paddleX + paddleWidth

                ) {
                    val sound = MediaPlayer.create(context, R.raw.item)
                    sound.setVolume(1.0F, 1.0F)
                    sound.start()
                    // 아이템이 패들과 충돌하면 목숨 회복
                    if (lives < 3) {
                        lives++
                    }
                    itemActive = false  // 아이템 비활성화
                }
            }
        }

        if (longActive) {
            longY += 10
            // 롱아이템이 화면 아래로 벗어나면 비활성화
            if (longY > viewHeight) {
                longActive = false
            } else {
                // 롱아이템과 패들의 충돌 체크
                if (longY + longHeight >= paddleY &&
                    longY <= paddleY + paddleHeight &&
                    longX + longWidth >= paddleX &&
                    longX <= paddleX + paddleWidth
                ) {
                    val sound = MediaPlayer.create(context, R.raw.item)
                    sound.setVolume(1.0F, 1.0F)
                    sound.start()
                    // 아이템이 패들과 충돌하면 패들 길이 증가
                    paddleWidth = viewWidth / 3
                    imgPaddle = Bitmap.createScaledBitmap(imgPaddle, paddleWidth, paddleHeight, false)
                    startTime = System.currentTimeMillis()
                    isExpanded = true
                    longActive = false  // 아이템 비활성화
                }
            }

        }
        if(superActive){
            superY+=10
            if(superY>viewHeight){
                superActive=false
            }else{
                if(superY+longHeight>=paddleY &&
                        superY <=paddleY+paddleHeight&&
                        superX + superWidth>=paddleX&&
                        superX <=paddleX+paddleWidth){
                    val sound = MediaPlayer.create(context, R.raw.boom1)
                    sound.setVolume(1.0F, 1.0F)
                    sound.start()


                    if(lives<=3){
                        lives--
                        if(lives==0){
                            func_Reset()
                        }
                    }

                    superActive = false  // 아이템 비활성화


                }
            }
        }
        // 패들의 상태를 업데이트
        if (isExpanded && System.currentTimeMillis() - startTime >= expandDuration) {
            isExpanded = false
        }
        if (!isExpanded) {
            paddleWidth = originalPaddleWidth
            imgPaddle = Bitmap.createScaledBitmap(imgPaddle, paddleWidth, paddleHeight, false)
            startTime = 0 // startTime 초기화
        }
    }


    var isEnd: Boolean = true  //메모리 누수 방지를 위한 핸들러
    private fun handlerViewReload(delayTime: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            invalidate()
            if (isEnd) handlerViewReload(0)
        }, delayTime)
    }

    private fun showFinishview(){
        val dialogFragment = FinishDialogFragment(score)
        dialogFragment.show((context as AppCompatActivity).supportFragmentManager, "FinishDialog")

        dialogFragment.setOnExitClickListener {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
        dialogFragment.setOnRetryClickListener {
            func_Setting()
        }
    }


}

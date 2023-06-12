package com.example.breakingblock

import android.graphics.Bitmap
import android.media.MediaPlayer

class Block(
    var Block_W: Int,
    var Block_H: Int,
    var Block_X: Int,
    var Block_Y: Int,
    var img: Bitmap? = null,
    var collisionCount: Int
) {
    // 생성자
    init {
        this.Block_W = Block_W
        this.Block_H = Block_H
        this.Block_X = Block_X
        this.Block_Y = Block_Y
        this.img = img

    }

    // 블럭에 충돌 확인(0: 충돌하지 않음, 1: 왼쪽, 2: 오른쪽, 3: 위, 4: 아래)
    fun IsClash(ballX: Float, ballY: Float, ballRadius: Int): Int {
        val ballLeft = ballX - ballRadius
        val ballTop = ballY - ballRadius
        val ballRight = ballX + ballRadius
        val ballBottom = ballY + ballRadius

        val blockLeft = Block_X.toFloat()
        val blockRight = Block_X + Block_W
        val blockTop = Block_Y.toFloat()
        val blockBottom = Block_Y + Block_H

        if (ballRight >= blockLeft && ballLeft <= blockRight && ballBottom >= blockTop && ballTop <= blockBottom) {
            val isHorizontalCollision = (ballY >= blockTop) && (ballY <= blockBottom)
            val isVerticalCollision = (ballX >= blockLeft) && (ballX <= blockRight)

            // 충돌이 발생한 경우, 공의 위치를 벽돌 바깥쪽으로 이동시킵니다.
            if (isHorizontalCollision) {
                if (ballX < blockLeft) {
                    return 1
                } else {
                    return 2
                }
            } else if (isVerticalCollision) {
                if (ballY < blockTop) {
                    return 3
                } else {
                    return 4
                }
            }
        }
        return 0
    }




}
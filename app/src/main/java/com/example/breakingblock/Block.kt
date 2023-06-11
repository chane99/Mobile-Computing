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
    fun IsClash(Ball_X: Float, Ball_Y: Float, Ball_D: Int, Ball_R: Int): Int {
        // 블럭 좌측 충돌 확인

        if (Block_X - Ball_D <= Ball_X && Ball_X <= Block_X - Ball_R
            && Block_Y - Ball_R <= Ball_Y && Ball_Y <= Block_Y + Block_H - Ball_R
        ) {
            return 1
        }

        // 블럭 우측 충돌 확인
        if (Block_X + Block_W >= Ball_X && Ball_X >= Block_X + Block_W - Ball_R
            && Block_Y - Ball_R <= Ball_Y && Ball_Y <= Block_Y + Block_H - Ball_R
        ) {
            return 2
        }

        // 블럭 상측 충돌 확인
        if (Block_Y - Ball_D <= Ball_Y && Ball_Y <= Block_Y - Ball_R
            && Block_X - Ball_R <= Ball_X && Ball_X <= Block_X + Block_W - Ball_R
        ) {
            return 3
        }

        // 블럭 하측 충돌 확인
        if (Block_Y + Block_H >= Ball_Y && Ball_Y >= Block_Y + Block_H - Ball_R
            && Block_X - Ball_R <= Ball_X && Ball_X <= Block_X + Block_W - Ball_R
        ) {
            return 4
        }

        return 0
    }
}
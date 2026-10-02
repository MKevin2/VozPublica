package com.example.appeleitoral

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class GraficoPizza @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var valores =
        emptyList<Int>()


    fun atualizarDados(
        novosValores: List<Int>
    ) {

        valores = novosValores

        invalidate()
    }


    fun obterCores(): List<Int> {

        return listOf(
            0xFF2196F3.toInt(),
            0xFFF44336.toInt(),
            0xFFFFC107.toInt(),
            0xFF16A765.toInt(),
            0xFF9C27B0.toInt(),
            0xFF795548.toInt(),
            0xFF607D8B.toInt()
        )
    }


    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        if (valores.isEmpty()) {
            return
        }


        val total =
            valores.sum()


        if (total == 0) {
            return
        }


        val tamanho =
            minOf(
                width,
                height
            )


        val margem = 10f


        val rect =
            RectF(
                (width - tamanho) / 2f + margem,
                (height - tamanho) / 2f + margem,
                (width + tamanho) / 2f - margem,
                (height + tamanho) / 2f - margem
            )


        val cores =
            obterCores()


        var anguloInicial =
            -90f


        for (i in valores.indices) {

            val porcentagem =
                valores[i].toFloat() /
                        total


            val angulo =
                porcentagem * 360f


            paint.color =
                cores[i % cores.size]


            paint.style =
                Paint.Style.FILL


            canvas.drawArc(
                rect,
                anguloInicial,
                angulo,
                true,
                paint
            )


            anguloInicial +=
                angulo
        }
    }
}
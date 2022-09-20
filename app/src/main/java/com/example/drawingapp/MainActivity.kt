package com.example.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var ibBrushSize: ImageButton? = null
    private var mIbCurrentColor: ImageButton? = null
    private var ibColorPastel: ImageButton? = null
    private var ibColorYellow: ImageButton? = null
    private var ibColorRed: ImageButton? = null
    private var ibColorBlack: ImageButton? = null
    private var ibColorGreen: ImageButton? = null
    private var ibColorBlue: ImageButton? = null
    private var ibColorPurple: ImageButton? = null
    private var ibColorTeal: ImageButton? = null
    private var ibColorWhite: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setOnClick()
    }

    private fun initializeViews() {
        val llPaintColors: LinearLayout = findViewById(R.id.ll_activity_main_paint_colors)
        mIbCurrentColor = llPaintColors[2] as ImageButton
        mIbCurrentColor!!.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.palette_selected
            )
        )
        drawingView = findViewById(R.id.dv_activity_main_canvas)
        drawingView?.setSizeForBrush(10f)

        ibBrushSize = findViewById(R.id.ib_activity_main_brush_size)
        ibBrushSize?.setOnClickListener {
            showBrushSizeChooserDialog()
        }
        ibColorPastel = findViewById(R.id.ib_activity_main_color_pastel)
        ibColorYellow = findViewById(R.id.ib_activity_main_color_yellow)
        ibColorRed = findViewById(R.id.ib_activity_main_color_red)
        ibColorBlack = findViewById(R.id.ib_activity_main_color_black)
        ibColorGreen = findViewById(R.id.ib_activity_main_color_green)
        ibColorBlue = findViewById(R.id.ib_activity_main_color_blue)
        ibColorPurple = findViewById(R.id.ib_activity_main_color_purple)
        ibColorTeal = findViewById(R.id.ib_activity_main_color_teal)
        ibColorWhite = findViewById(R.id.ib_activity_main_color_white)
    }

    private fun setOnClick() {
        ibColorPastel?.setOnClickListener {
            paintClicked(ibColorPastel!!)
        }
        ibColorYellow?.setOnClickListener {
            paintClicked(ibColorYellow!!)
        }
        ibColorRed?.setOnClickListener {
            paintClicked(ibColorRed!!)
        }
        ibColorBlack?.setOnClickListener {
            paintClicked(ibColorBlack!!)
        }
        ibColorGreen?.setOnClickListener {
            paintClicked(ibColorGreen!!)
        }
        ibColorBlue?.setOnClickListener {
            paintClicked(ibColorBlue!!)
        }
        ibColorPurple?.setOnClickListener {
            paintClicked(ibColorPurple!!)
        }
        ibColorTeal?.setOnClickListener {
            paintClicked(ibColorTeal!!)
        }
        ibColorWhite?.setOnClickListener {
            paintClicked(ibColorWhite!!)
        }
    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size: ")
        val smallBtn: ImageButton = brushDialog.findViewById(R.id.ib_dialog_brush_small)
        smallBtn.setOnClickListener {
            drawingView?.setSizeForBrush(10f)
            brushDialog.dismiss()
        }
        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ib_dialog_brush_medium)
        mediumBtn.setOnClickListener {
            drawingView?.setSizeForBrush(20f)
            brushDialog.dismiss()
        }
        val largeButton: ImageButton = brushDialog.findViewById(R.id.ib_dialog_brush_large)
        largeButton.setOnClickListener {
            drawingView?.setSizeForBrush(30f)
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

    private fun paintClicked(imageButton: ImageButton) {
        if (imageButton != mIbCurrentColor) {
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.palette_selected
                )
            )
            mIbCurrentColor!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.palette_normal
                )
            )

            mIbCurrentColor = imageButton
        }
    }
}
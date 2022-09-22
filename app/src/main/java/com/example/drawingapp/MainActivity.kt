package com.example.drawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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
    private var ibGallery: ImageButton? = null
    private var ibUndo: ImageButton? = null
    private var ibSave: ImageButton? = null
    private var customProgressDialog: Dialog? = null

    val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imgBackground: ImageView = findViewById(R.id.iv_activity_main_background)
                imgBackground.setImageURI(result.data?.data)
            }
        }

    val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted) {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(intent)
                } else {
                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

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
        ibGallery = findViewById(R.id.ib_activity_main_pick_image)
        ibUndo = findViewById(R.id.ib_activity_main_undo)
        ibSave = findViewById(R.id.ib_activity_main_save)
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
        ibGallery?.setOnClickListener {
            requestStoragePermission()
        }
        ibUndo?.setOnClickListener {
            drawingView?.undo()
        }
        ibSave?.setOnClickListener {
            if (isReadStorageAllowed()) {
                showProgressDialog()
                lifecycleScope.launch {
                    val flContainer: FrameLayout = findViewById(R.id.fl_activity_main_container)
                    saveBitmapFile(getBitmapFromView(flContainer))
                }
            }

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

    private fun showRationaleDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun isReadStorageAllowed(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showRationaleDialog(
                "Drawing App",
                "Drawing App needs to access your external storage"
            )

        } else {
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val resultBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        val background = view.background
        if (background != null) {
            background.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return resultBitmap
    }

    private suspend fun saveBitmapFile(bitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (bitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                    val file = File(
                        externalCacheDir?.absoluteFile.toString()
                                + File.separator
                                + "DrawingApp_"
                                + System.currentTimeMillis() / 1000
                                + ".png"
                    )

                    val fileOutput = FileOutputStream(file)
                    fileOutput.write(bytes.toByteArray())
                    fileOutput.close()

                    result = file.absolutePath

                    runOnUiThread {
                        dismissProgressDialog()
                        if (result.isNotEmpty()) {
                            Snackbar.make(
                                findViewById(R.id.ib_activity_main_save),
                                "File saved successfully : $result",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else {
                            Snackbar.make(
                                findViewById(R.id.ib_activity_main_save),
                                "Something went wrong while saving the file.",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    private fun showProgressDialog() {
        customProgressDialog = Dialog(this)
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
        customProgressDialog?.show()
    }

    private fun dismissProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }
}
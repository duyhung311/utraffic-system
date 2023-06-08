package com.hcmut.admin.utraffictest.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.appcompat.widget.AppCompatTextView
import com.hcmut.admin.utraffictest.R
import java.lang.ref.WeakReference

class MessageGiftDialog(
        context: Context,
        private var description: String? = ""

) : View.OnClickListener {


    private var tvDescription: AppCompatTextView? = null
    private var tvOk: AppCompatTextView? = null

    private var dialog: Dialog? = null
    private val weakReference: WeakReference<Context> = WeakReference(context)
    private var clickOk: () -> Unit = {}

    private var okLabel: String? = null

    fun setClickOk(clickOk: () -> Unit): MessageGiftDialog {
        this.clickOk = clickOk
        return this
    }




    fun setButtonOkText(s: String?): MessageGiftDialog {
        this.okLabel = if (s == null || s == "") "Ok" else s
        return this
    }

    fun setDescription(s: String?): MessageGiftDialog {
        this.description = if (s == null || s == "") "Something went wrong." else s
        return this
    }

    fun setTouch(isTouch: Boolean): MessageGiftDialog {
        dialog?.setCancelable(isTouch)
        return this
    }




    fun show() {
        dialog = Dialog(weakReference.get() ?: return)
        val view = LayoutInflater.from(weakReference.get())
                .inflate(R.layout.layout_gift_inform, null)

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(view)

        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_cardview)


        tvDescription = view.findViewById(R.id.giftInform)
        tvOk = view.findViewById(R.id.tvOk)


        setView()
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.show()
    }

    private fun setView() {
        tvDescription?.text = description

        tvOk?.text = okLabel ?: "Ok"
        tvOk?.setOnClickListener(this)
    }


    private fun dismiss() {
        if (dialog != null && dialog!!.isShowing)
            dialog!!.dismiss()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvOk -> {
                dismiss()
                clickOk()
            }

        }
    }
}
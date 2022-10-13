package com.hcmut.admin.utrafficsystem.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.Group
import com.hcmut.admin.utrafficsystem.R
import java.lang.ref.WeakReference

class MessageDialog(
        context: Context,
        private var title: String? = "",
        private var description: String? = "",
        private val hasChoose: Boolean
) : View.OnClickListener {

    private var tvTitle: TextView? = null
    private var tvDescription: AppCompatTextView? = null
    private var tvYes: AppCompatTextView? = null
    private var tvNo: AppCompatTextView? = null
    private var tvOk: AppCompatTextView? = null
    private var group: Group? = null

    private var dialog: Dialog? = null
    private val weakReference: WeakReference<Context> = WeakReference(context)
    private var clickYes: () -> Unit = {}
    private var clickNo: () -> Unit = {}
    private var clickOk: () -> Unit = {}

    private var yesLabel: String? = null
    private var noLabel: String? = null
    private var okLabel: String? = null
    fun setClickYes(clickYes: () -> Unit): MessageDialog {
        this.clickYes = clickYes
        return this
    }

    fun setClickNo(clickNo: () -> Unit): MessageDialog {
        this.clickNo = clickNo
        return this
    }

    fun setClickOk(clickOk: () -> Unit): MessageDialog {
        this.clickOk = clickOk
        return this
    }

    fun setTitle(s: String?): MessageDialog {
        this.title = if (s == null || s == "") "Error" else s
        return this
    }

    fun setButtonYesText(s: String?): MessageDialog {
        this.yesLabel = if (s == null || s == "") "Yes" else s
        return this
    }

    fun setButtonNoText(s: String?): MessageDialog {
        this.noLabel = if (s == null || s == "") "No" else s
        return this
    }

    fun setButtonOkText(s: String?): MessageDialog {
        this.okLabel = if (s == null || s == "") "Ok" else s
        return this
    }

    fun setDescription(s: String?): MessageDialog {
        this.description = if (s == null || s == "") "Something went wrong." else s
        return this
    }

    fun setTouch(isTouch: Boolean): MessageDialog {
        dialog?.setCancelable(isTouch)
        return this
    }

    fun setColorTitle(color: Int): MessageDialog {
        tvTitle?.setTextColor(weakReference.get()?.resources?.getColor(color, null) ?: return this)
        return this
    }

    fun show() {
        dialog = Dialog(weakReference.get() ?: return)
        val view = LayoutInflater.from(weakReference.get())
                .inflate(R.layout.layout_message, null)

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(view)

        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_cardview)

        tvTitle = view.findViewById(R.id.tvTitle)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvYes = view.findViewById(R.id.tvYes)
        tvNo = view.findViewById(R.id.tvNo)
        tvOk = view.findViewById(R.id.tvOk)
        group = view.findViewById(R.id.group)

        setView()
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.show()
    }

    private fun setView() {
        tvTitle?.text = title
        tvDescription?.text = description
        tvYes?.text = yesLabel ?: "Yes"
        tvNo?.text = noLabel ?: "No"
        tvOk?.text = okLabel ?: "Ok"
        setHasChoose(hasChoose)
        tvYes?.setOnClickListener(this)
        tvNo?.setOnClickListener(this)
        tvOk?.setOnClickListener(this)
    }

    private fun setHasChoose(hasChoose: Boolean) {
        if (hasChoose) {
            tvOk?.visibility = View.GONE
        } else {
            group?.visibility = View.GONE
        }
    }

    private fun dismiss() {
        if (dialog != null && dialog!!.isShowing)
            dialog!!.dismiss()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvYes -> {
                dismiss()
                clickYes()
            }
            R.id.tvOk -> {
                dismiss()
                clickOk()
            }
            R.id.tvNo -> {
                dismiss()
                clickNo()
            }

        }
    }
}
package com.akeshishi.readmoretext

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat

const val DEFAULT_MAX_LINES = 3
const val INVALID_END_INDEX = -1
const val ELLIPSIZE = "... "

class ReadMoreText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppCompatTextView(context, attrs) {

    private lateinit var originalText: CharSequence
    private var bufferType: BufferType? = null
    private var hasReadMore = true
    private var readMoreText = context.getString(R.string.read_more)
    private var readLessText = context.getString(R.string.read_less)
    private lateinit var expandingText: CharSequence
    private lateinit var collapsingText: CharSequence
    private lateinit var viewMoreSpan: ReadMoreClickableSpan
    private var readMoreTextColor = 0
    private var lineEndIndex = 0
    private var maxLinesCount = 0

    init {
        setUpAttributes(context, attrs)
    }

    override fun setText(text: CharSequence, type: BufferType?) {
        super.setText(text, type)
        originalText = text
        bufferType = type
        setText()
    }

    private fun setText() {
        super.setText(displayableText, bufferType)
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
    }

    private val displayableText: CharSequence?
        get() = setupReadMore(originalText)

    private fun setUpAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ReadMoreText)
        maxLinesCount = typedArray.getInt(R.styleable.ReadMoreText_readMoreMaxLines, DEFAULT_MAX_LINES)

        expandingText = typedArray.getString(R.styleable.ReadMoreText_expandingText) ?: readMoreText
        collapsingText = typedArray.getString(R.styleable.ReadMoreText_collapsingText) ?: readLessText

        readMoreTextColor = typedArray.getColor(
            R.styleable.ReadMoreText_readMoreTextColor,
            ContextCompat.getColor(context, R.color.accent)
        )
        typedArray.recycle()
        viewMoreSpan = ReadMoreClickableSpan()
        onGlobalLayoutLineEndIndex()
        setText()
    }

    private fun getLineEndIndex() {
        try {
            lineEndIndex = when (maxLinesCount) {
                0 -> layout.getLineEnd(0)
                in 1..lineCount -> layout.getLineEnd(maxLinesCount - 1)
                else -> INVALID_END_INDEX
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupReadMore(text: CharSequence?): CharSequence? {
        if (text != null && lineEndIndex > 0) {
            if (hasReadMore) {
                if (layout.lineCount > maxLinesCount) {
                    return expandText()
                }
            } else {
                return collapseText()
            }
        }
        return text
    }

    private fun expandText(): CharSequence {
        val trimEndIndex = lineEndIndex - (ELLIPSIZE.length + expandingText.length + 1)

        val collapsedText = SpannableStringBuilder(originalText, 0, trimEndIndex)
            .append(ELLIPSIZE)
            .append(expandingText)

        return addClickableSpan(collapsedText, expandingText)
    }

    private fun collapseText(): CharSequence {
        val expandedText =
            SpannableStringBuilder(originalText, 0, originalText.length).append(collapsingText)

        return addClickableSpan(expandedText, collapsingText)
    }

    private fun addClickableSpan(spannable: SpannableStringBuilder, trimText: CharSequence): CharSequence {
        spannable.setSpan(
            viewMoreSpan,
            spannable.length - trimText.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    inner class ReadMoreClickableSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            hasReadMore = !hasReadMore
            setText()
        }

        override fun updateDrawState(textPaint: TextPaint) {
            textPaint.color = readMoreTextColor
        }
    }

    private fun onGlobalLayoutLineEndIndex() {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                getLineEndIndex()
                setText()
            }
        })
    }
}
package com.lc.mybaselibrary

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

/**
 *@date: 2020/9/16
 *@describe:
 *@Auth: 29579
 **/
class ShapeButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatButton(context, attrs, defStyleAttr) {
    var shapeBuilder: ShapeBuilder? = null
    var attributeSetData: AttributeSetData = AttributeSetData()

    init {
        attributeSetData = AttributeSetHelper().loadFromAttributeSet(context, attrs)
        shapeBuilder = ShapeBuilder()
        shapeBuilder?.init(this, attributeSetData)
    }
}
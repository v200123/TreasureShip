package com.jzz.treasureship.utils

import java.math.BigDecimal
import java.text.DecimalFormat


/**
 * 金额工具类,主要是金额的格式化,金额的加、减
 * @author Tiny
 */
object MoneyUtil {
    var fnum = DecimalFormat("##0.00000000000000000000")
    /**
     * 格式化金额
     * @param value
     * @return
     */
    fun formatMoney(value: String?): String {
        var value = value
        if (value == null || value === "") {
            value = "0.00"
        }
        return fnum.format(BigDecimal(value).stripTrailingZeros().toPlainString())
    }

    /**
     * 金额相加
     * @param valueStr 基础值
     * @param minusValueStr 被加数
     * @return
     */
    fun moneyAdd(valueStr: String?, addStr: String?): String {
        val value = BigDecimal(valueStr)
        val augend = BigDecimal(addStr)
        return fnum.format(value.add(augend))
    }

    /**
     * 金额相加
     * @param valueStr 基础值
     * @param minusValueStr 被加数
     * @return
     */
    fun moneyAdd(value: BigDecimal, augend: BigDecimal?): BigDecimal {
        return value.add(augend?.stripTrailingZeros())
    }

    /**
     * 金额相减
     * @param valueStr 基础值
     * @param minusValueStr 减数
     * @return
     */
    fun moneySub(valueStr: String?, minusStr: String?): String {
        val value = BigDecimal(valueStr).stripTrailingZeros()
        val subtrahend = BigDecimal(minusStr).stripTrailingZeros()
        return fnum.format(value.subtract(subtrahend?.stripTrailingZeros()))
    }

    /**
     * 金额相减
     * @param value 基础值
     * @param subtrahend 减数
     * @return
     */
    fun moneySub(value: BigDecimal, subtrahend: BigDecimal?): BigDecimal {
        return value.subtract(subtrahend?.stripTrailingZeros())
    }

    /**
     * 金额相乘
     * @param valueStr 基础值
     * @param minusValueStr 被乘数
     * @return
     */
    fun moneyMul(valueStr: String?, mulStr: String?): String {
        val value = BigDecimal(valueStr).stripTrailingZeros()
        val mulValue = BigDecimal(mulStr).stripTrailingZeros()
        return fnum.format(value.multiply(mulValue))
    }

    /**
     * 金额相乘
     * @param value 基础值
     * @param mulValue 被乘数
     * @return
     */
    fun moneyMul(value: BigDecimal, mulValue: BigDecimal?): BigDecimal {
        return value.multiply(mulValue?.stripTrailingZeros())
    }

    /**
     * 金额相除 <br></br>
     * 精确小位小数
     * @param valueStr 基础值
     * @param minusValueStr 被乘数
     * @return
     */
    fun moneydiv(valueStr: String?, divideStr: String?): String {
        val value = BigDecimal(valueStr)
        val divideValue = BigDecimal(divideStr)
        return fnum.format(value.divide(divideValue, 2, BigDecimal.ROUND_HALF_UP))
    }

    /**
     * 金额相除 <br></br>
     * 精确小位小数
     * @param value 基础值
     * @param divideValue 被乘数
     * @return
     */
    fun moneydiv(value: BigDecimal, divideValue: BigDecimal?): BigDecimal {
        return value.divide(divideValue, 2, BigDecimal.ROUND_HALF_UP)
    }

    /**
     * 值比较大小
     * <br></br>如果valueStr大于等于compValueStr,则返回true,否则返回false
     * true 代表可用余额不足
     * @param valueStr (需要消费金额)
     * @param compValueStr (可使用金额)
     * @return
     */
    fun moneyComp(valueStr: String?, compValueStr: String?): Boolean {
        val value = BigDecimal(valueStr)
        val compValue = BigDecimal(compValueStr)
        //0:等于    >0:大于    <0:小于
        val result = value.compareTo(compValue)
        return result >= 0
    }

    /**
     * 值比较大小
     * <br></br>如果valueStr大于等于compValueStr,则返回true,否则返回false
     * true 代表可用余额不足
     * @param valueStr (需要消费金额)
     * @param compValueStr (可使用金额)
     * @return
     */
    fun moneyComp(
        valueStr: BigDecimal,
        compValueStr: BigDecimal?
    ): Boolean { //0:等于    >0:大于    <0:小于
        val result = valueStr.compareTo(compValueStr)
        return result >= 0
    }

    /**
     * 金额乘以，省去小数点
     * @param valueStr 基础值
     * @return
     */
    fun moneyMulOfNotPoint(valueStr: String, divideStr: String?): String {
        var valueStr = valueStr
        val value = BigDecimal(valueStr).stripTrailingZeros()
        val mulValue = BigDecimal(divideStr).stripTrailingZeros()
        valueStr = fnum.format(value.multiply(mulValue))
        return fnum.format(value.multiply(mulValue)).substring(0, valueStr.length - 3)
    }

    /**
     * 给金额加逗号切割
     * @param str
     * @return
     */
    fun addComma(str: String): String? {
        var str = str
        return try {
            var banNum = ""
            if (str.contains(".")) {
                val arr = str.split("\\.").toTypedArray()
                if (arr.size == 2) {
                    str = arr[0]
                    banNum = "." + arr[1]
                }
            }
            // 将传进数字反转
            val reverseStr = StringBuilder(str).reverse().toString()
            var strTemp = ""
            for (i in 0 until reverseStr.length) {
                if (i * 3 + 3 > reverseStr.length) {
                    strTemp += reverseStr.substring(i * 3, reverseStr.length)
                    break
                }
                strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ","
            }
            // 将[789,456,] 中最后一个[,]去除
            if (strTemp.endsWith(",")) {
                strTemp = strTemp.substring(0, strTemp.length - 1)
            }
            // 将数字重新反转
            var resultStr = StringBuilder(strTemp).reverse().toString()
            resultStr += banNum
            resultStr
        } catch (e: Exception) {
            str
        }
    }

    /**
     * @Title: clearNoUseZeroForBigDecimal
     * @Description: 去掉BigDecimal尾部多余的0，通过stripTrailingZeros().toPlainString()实现
     * @param num
     * @return BigDecimal
     */
    fun clearNoUseZeroForBigDecimal(num: BigDecimal): BigDecimal? {
        var returnNum: BigDecimal? = null
        val numStr = num.stripTrailingZeros().toPlainString()
        returnNum = if (numStr.indexOf(".") == -1) { // 如果num 不含有小数点,使用stripTrailingZeros()处理时,变成了科学计数法
            BigDecimal(numStr)
        } else {
            if (num.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal.ZERO
            } else {
                num.stripTrailingZeros()
            }
        }
        return returnNum
    }
}

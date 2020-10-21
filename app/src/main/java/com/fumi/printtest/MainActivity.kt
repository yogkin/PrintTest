package com.fumi.printtest

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.printer.sdk.Barcode
import com.printer.sdk.PrinterConstants
import com.printer.sdk.PrinterInstance

class MainActivity : AppCompatActivity() {

    private val line80 = "::::::::::::::::::::::::::::::::::::::::::::::::\n"

    //height 0-7
    private val fontSizeBig2 = 2
    private val fontSizeBig = 1
    private val fontSizeNormal = 0

    val printData by lazy { Gson().fromJson(cmdStr, PrintData::class.java) }
    val printer = initBlePrinter("DC:0D:30:8A:BD:ED")

    fun printLine() {
        printer?.setFont(0, fontSizeNormal, fontSizeNormal, 0, 0)
        printer?.printText("$line80")
    }

    fun printEmptyLine() = printer?.printText("\n")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val printer = initBlePrinter("DC:0D:30:8A:BD:ED")
        printer?.openConnection()
        findViewById<View>(R.id.btn_click).setOnClickListener {
            printer?.apply {

                //名称
                setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
                setFont(0, fontSizeBig, fontSizeBig, 1, 0)
                printText("${printData.list.consignee}\n")
                printEmptyLine()

                //收货地址
                setFont(0, fontSizeBig, fontSizeBig, 0, 0)
                printText("收货地址：${printData.list.address}\n")
                printEmptyLine()

                //条形码
                setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
                printBarCode(Barcode(PrinterConstants.BarcodeType.CODE128,0,0,0,printData.list.wave_picking_no))
                printEmptyLine()
                printText("${printData.list.wave_picking_no}\n")



                //快递公司
                setFont(0, fontSizeNormal, fontSizeNormal, 1, 0)
                setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
                printText("快递公司：${printData.list.shipping_company}\n")

                //商品列表信息
                setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
                printData.list.goods_info.forEachIndexed { index, item ->
                    printEmptyLine()
                    //good_no
                    setFont(0, fontSizeNormal, fontSizeNormal, 1, 0)
                    printText("${item.goods_no}                              ${item.goods_location}\n")
                    printEmptyLine()
                    printText("(${item.no}) ${item.sku.joinToString("")}\n")
                    setFont(0, fontSizeNormal, fontSizeNormal, 1, 0)
                    printText("${item.goods_name}\n")
                    printEmptyLine()
                    printLine()
                }

                //合计
                setFont(0, fontSizeBig2, fontSizeBig2, 0, 0)
                setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER)
                printText("共${printData.list.spu_num}款，${printData.list.sku_num}件\n")
                setFont(0, fontSizeNormal, fontSizeNormal, 0, 0)
                printLine()

                //订单号列表
                setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT)
                setFont(0, fontSizeNormal, fontSizeNormal, 1, 0)
                printData.list.order_info.forEach {
                    printEmptyLine()
                    printText("(${it.order_no})订单号：${it.order_sn}\n")
                }

                printEmptyLine()
                setFont(0, fontSizeNormal, fontSizeNormal, 0, 0)
                printLine()
                //走纸
                repeat(5) { printText("\n") }
                cutPaper(0,0)
            }

        }
    }

    fun initBlePrinter(macAddress: String): PrinterInstance? {
        if (macAddress.isEmpty()) {
            Toast.makeText(this, "打印地址不能为空", Toast.LENGTH_SHORT).show()
        }
        val defaultAdapter = BluetoothAdapter.getDefaultAdapter()
        val device = defaultAdapter.getRemoteDevice(macAddress)
        return PrinterInstance.getPrinterInstance(device, null)

    }
}
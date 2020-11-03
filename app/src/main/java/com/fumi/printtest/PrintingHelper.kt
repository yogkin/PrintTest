package com.fumi.printtest

import com.printer.sdk.LabelPrint
import com.printer.sdk.PrinterConstants
import com.printer.sdk.PrinterInstance
import com.printer.sdk.PrinterConstants.Command
import com.printer.sdk.PrinterConstants.TwoBarCodeType.QR
import com.printer.sdk.utils.XLog


class PrintingHelper {
    companion object {

        private const val line80 = "------------------------------------------------\n"
        private const val line110 = "--------------------------------------------------------------------\n"

        private const val sizeUnit = 24

        @JvmStatic
        fun print(
            printer: PrinterInstance?,
            printings: List<PrintingModel>,
            printTimes: Int,
            needSetWidth: Boolean
        ) {
            val pageModel = "width_110"
            val mPageWidth = if (needSetWidth) if (pageModel == "width_80") 576 else 816 else 816
            printer?.apply {
                paperWidth = mPageWidth
                for (i in 1..printTimes) {
                    printings.forEach {
                        when {
                            it.type == 1 -> {
                                val align = when (it.align) {
                                    "center" -> Command.ALIGN_CENTER
                                    "right" -> Command.ALIGN_RIGHT
                                    else -> Command.ALIGN_LEFT
                                }
                                val width = when (it.fontSize) {
                                    1, 2 -> 0
                                    3, 4 -> 1
                                    else -> 0
                                }
                                val height = when (it.fontSize) {
                                    1, 3 -> 0
                                    2, 4 -> 1
                                    else -> 0
                                }
                                setFont(0, width, height, it.bold, 0)
                                setPrinter(Command.ALIGN, align)
                                printText("${it.content}\n")
                            }
                            it.type == 2 -> {
                                setFont(0, 0, 0, 0, 0)
                                if (mPageWidth == 576) {
                                    printText(line80)
                                } else if (mPageWidth == 816) {
                                    printText(line110)
                                }
                            }
                            it.type == 3 && it.bitmap != null -> {
                                printColorImg2Gray(it.bitmap, PrinterConstants.PAlign.NONE, 0, false)
                                Thread.sleep(200)
                                setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)
                            }
                            it.type == 4 -> {

                                val areaHeight = 200

                                setFont(0, 0, 0, 0, 0)
                                setPrinter(Command.ALIGN, Command.ALIGN_LEFT)
                                pageSetup(PrinterConstants.LablePaperType.Size_100mm, mPageWidth, areaHeight)

                                this.paperWidth = mPageWidth
                                this.printText(LabelPrint.label_set_page(mPageWidth, areaHeight, 0))

                                val codeSize = 168
                                val centerX = mPageWidth / 2 - codeSize / 2
                                val ver = 5
                                val lel = 1

                                LabelPrint.label_put_drawBarcodeTSPL(centerX, 0, QR,50,50,0,0,0,it.codes[0].codeContent)
                                drawQrCode(this, centerX, 0, it.codes[0].codeContent, PrinterConstants.PRotate.Rotate_0, ver, lel)
                                when (it.codes.size) {
                                    1 -> {
                                        drawQrCode(this, centerX, 0, it.codes[0].codeContent, PrinterConstants.PRotate.Rotate_0, ver, lel)
                                        drawText(centerX, codeSize, centerX + codeSize, areaHeight, PrinterConstants.PAlign.CENTER, PrinterConstants.PAlign.CENTER,
                                                it.codes[0].codeName, PrinterConstants.LableFontSize.Size_24, 0, 0, 0, 0, PrinterConstants.PRotate.Rotate_0)
                                    }
                                    2 -> {
                                        drawQrCode(this, centerX - codeSize, 0, it.codes[0].codeContent, PrinterConstants.PRotate.Rotate_0, ver, lel)
                                        drawQrCode(this, centerX + codeSize, 0, it.codes[1].codeContent, PrinterConstants.PRotate.Rotate_0, ver, lel)
                                        drawText(centerX - codeSize, codeSize, centerX, areaHeight, PrinterConstants.PAlign.CENTER, PrinterConstants.PAlign.CENTER,
                                                it.codes[0].codeName, PrinterConstants.LableFontSize.Size_24, 0, 0, 0, 0, PrinterConstants.PRotate.Rotate_0)

                                        drawText(centerX + codeSize, codeSize, centerX + codeSize * 2, areaHeight, PrinterConstants.PAlign.CENTER, PrinterConstants.PAlign.CENTER,
                                                it.codes[1].codeName, PrinterConstants.LableFontSize.Size_24, 0, 0, 0, 0, PrinterConstants.PRotate.Rotate_0)
                                    }
                                    else -> {
                                        drawQrCode(this, centerX - codeSize - 30, 0, it.codes[0].codeContent, PrinterConstants.PRotate.Rotate_0, ver, lel)
                                        drawQrCode(this, centerX, 0, it.codes[1].codeContent, PrinterConstants.PRotate.Rotate_0, ver, lel)
                                        drawQrCode(this, centerX + codeSize + 30, 0, it.codes[2].codeContent, PrinterConstants.PRotate.Rotate_0, ver, lel)
                                        drawText(centerX - codeSize - 30, codeSize, centerX - 30, areaHeight, PrinterConstants.PAlign.CENTER, PrinterConstants.PAlign.CENTER,
                                                it.codes[0].codeName, PrinterConstants.LableFontSize.Size_24, 0, 0, 0, 0, PrinterConstants.PRotate.Rotate_0)

                                        drawText(centerX, codeSize, centerX + codeSize, areaHeight, PrinterConstants.PAlign.CENTER, PrinterConstants.PAlign.CENTER,
                                                it.codes[1].codeName, PrinterConstants.LableFontSize.Size_24, 0, 0, 0, 0, PrinterConstants.PRotate.Rotate_0)

                                        drawText(centerX + codeSize + 30, codeSize, centerX + codeSize * 2 + 30, areaHeight, PrinterConstants.PAlign.CENTER, PrinterConstants.PAlign.CENTER,
                                                it.codes[2].codeName, PrinterConstants.LableFontSize.Size_24, 0, 0, 0, 0, PrinterConstants.PRotate.Rotate_0)
                                    }
                                }
                                print(PrinterConstants.PRotate.Rotate_0, 0)
                                setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1)
                            }

                            //左边大字，右边小字
                            it.type == 5 -> {
                                val splitTime = it.contents[1].split("\r\n")
                                val cmdStr = "! 0 200 200 85 1\r\n" +
                                        "SETMAG 2 2\r\n" +
                                        "SETBOLD 0\r\n" +
                                        "TEXT 8 0 10 10 ${it.contents[0]}\r\n" +
                                        "SETMAG 1 1\r\n" +
                                        "SETBOLD 0\r\n" +
                                        "TEXT 8 0 550 15 ${splitTime[0]}\r\n" +
                                        "TEXT 8 0 550 47 ${splitTime[1]}\r\n" +
                                        "PRINT\r\n"
                                sendStrToPrinterTSPL(cmdStr)
                            }

                            //左边二维码，右边文字
                            it.type == 6 -> {
                                val cmdStr = "! 0 200 200 210 1\r\n" +
                                        "B QR 10 10 M 2 U 4\r\n" +
                                        "M4A,${it.codes[0].codeContent}\r\n" +
                                        "ENDQR\r\n" +
                                        "SETMAG 3 3\r\n" +
                                        "SETBOLD 0\r\n" +
                                        "T 8 0 280 75 ${it.codes[0].codeName}\r\n" +
                                        "PRINT\r\n"

                                sendStrToPrinterTSPL(cmdStr)
                            }

                            //左边文字，右边二维码
                            it.type == 7 -> {
                                val textCmdSb = StringBuffer()
                                val splitTime = it.codes[0].codeName.split("\n")
                                splitTime.forEachIndexed { index, s ->
                                    val y = 30 + (i.times(32) + 5)
                                    textCmdSb.append("TEXT 8 0 0 $y $s\r\n")
                                }
                                val cmdStr = "! 0 200 200 200 1\r\n" +
                                        "B QR 680 10 M 2 U 3\r\n" +
                                        "M4A,${it.codes[0].codeContent}\r\n" +
                                        "ENDQR\r\n" +
                                        "$textCmdSb" +
                                        "PRINT\r\n"
                                sendStrToPrinterTSPL(cmdStr)
                            }


                        }
                    }
                    if (mPageWidth == 576) {
                        cutPaper(66, 50)
                    }
                    cutPaper(66, 50)
                }
            }
        }

        private fun drawQrCode(p: PrinterInstance, start_x: Int, start_y: Int, text: String, rotate: PrinterConstants.PRotate, ver: Int, lel: Int) {
            var level = "M"
            if (lel == 0) {
                level = "L"
            } else if (lel == 1) {
                level = "M"
            } else if (lel == 2) {
                level = "Q"
            } else if (lel == 3) {
                level = "H"
            }

            var rot = "B"
            if (rotate != PrinterConstants.PRotate.Rotate_0) {
                rot = "VB"
            }

            val cmd = rot + " QR " + start_x + " " + start_y + " M 2 U " + ver + "\r\n" + level + "4A," + text + "\r\n" + "ENDQR" + "\r\n"
            p.printText(cmd)
            XLog.i("PrinterInstance", cmd)
        }

    }
}
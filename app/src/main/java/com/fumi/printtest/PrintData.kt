package com.fumi.printtest

import android.graphics.Bitmap
import com.google.gson.Gson


val cmdStr =
    "{\"list\":{\"1162900001\":true,\"spu_num\":1,\"sku_num\":\"30\",\"wave_picking_id\":\"3521249\",\"wave_picking_no\":\"B12010152836234\",\"consignee\":\"钱货\",\"address\":\"海南省保亭黎族苗族自治县保城镇1\",\"shipping_company\":\"顺丰\",\"order_info\":[{\"order_no\":1,\"order_sn\":\"12037115352326\",\"custom_note\":\"\",\"admin_note\":\"\",\"work_id\":\"\",\"problem_name\":\"\",\"problem_desc\":\"\"}],\"goods_info\":[{\"no\":1,\"goods_no\":\"21875707\",\"goods_name\":\"斜跨包  退换货欠货商品1#  345\",\"goods_location\":\"--\",\"sku\":[\"1162900001/灰色/S/10\"],\"area\":null,\"shelf\":null,\"floor\":null,\"position\":null},{\"no\":1,\"goods_no\":\"21875707\",\"goods_name\":\"斜跨包  退换货欠货商品1#  345\",\"goods_location\":\"E65-4-2\",\"sku\":[\"1162900001/灰色/S/20\"],\"area\":\"E\",\"shelf\":\"65\",\"floor\":\"4\",\"position\":\"2\"}]},\"print_time\":\"2020-10-20 18:03:36\"}"

data class PrintData(
    val list: PrintList = PrintList(),
    val print_time: String = "" // 2020-10-20 18:03:36
)

data class PrintList(
    val address: String = "", // 海南省保亭黎族苗族自治县保城镇1
    val consignee: String = "", // 钱货
    val goods_info: List<GoodsInfo> = listOf(),
    val order_info: List<OrderInfo> = listOf(),
    val shipping_company: String = "", // 顺丰
    val sku_num: String = "", // 30
    val spu_num: Int = 0, // 1
    val wave_picking_id: String = "", // 3521249
    val wave_picking_no: String = "" // B12010152836234
)

data class GoodsInfo(
    val area: Any = Any(), // null
    val floor: Any = Any(), // null
    val goods_location: String = "", // --
    val goods_name: String = "", // 斜跨包  退换货欠货商品1#  345
    val goods_no: String = "", // 21875707
    val no: Int = 0, // 1
    val position: Any = Any(), // null
    val shelf: Any = Any(), // null
    val sku: List<String> = listOf()
)

data class OrderInfo(
    val admin_note: String = "",
    val custom_note: String = "",
    val order_no: Int = 0, // 1
    val order_sn: String = "", // 12037115352326
    val problem_desc: String = "",
    val problem_name: String = "",
    val work_id: String = ""
)

class PrintingModel(
    val type: Int,
    val content: String,
    val fontSize: Int,
    val align: String,
    val bold: Int,
    val codes: List<PrintCodeModel>,
    val contents: List<String>

) {
    var bitmap: Bitmap? = null
}

class PrintCodeModel(
    val codeContent: String,
    val codeName: String
)

class PrintListModel(
    val list: List<PrintingModel>
)
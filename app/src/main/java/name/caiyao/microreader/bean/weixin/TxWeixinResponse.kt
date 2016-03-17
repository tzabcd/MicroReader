package name.caiyao.microreader.bean.weixin

import java.util.*

/**
 * Created by 蔡小木 on 2016/3/4 0004.
 */
data class TxWeixinResponse(
    var code: Int = 0,
    var msg: String = "",
    var newslist: ArrayList<WeixinNews>? = null
)

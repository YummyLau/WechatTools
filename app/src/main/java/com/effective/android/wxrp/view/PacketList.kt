package com.effective.android.wxrp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.effective.android.wxrp.R
import com.effective.android.wxrp.data.db.PacketRecord
import com.effective.android.wxrp.utils.ToolUtil


class PacketList : RecyclerView {

    private var adapter: PacketAdapter? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context)
    }

    private fun initView(context: Context) {
        adapter = PacketAdapter(context)
        this.setAdapter(adapter)
        this.layoutManager = LinearLayoutManager(context)
    }

    fun setPackets(packetList: List<PacketRecord>?) {
        adapter!!.setPackets(packetList)
    }

    fun addPacket(packet: PacketRecord?) {
        adapter!!.addPacket(packet)
    }
}

class PacketAdapter : RecyclerView.Adapter<PacketHolder> {

    private var context: Context? = null
    private val packetList: ArrayList<PacketRecord> = ArrayList<PacketRecord>()

    constructor(context: Context) : super() {
        this.context = context
    }

    fun setPackets(packetList: List<PacketRecord>?) {
        if (packetList == null || packetList.isEmpty()) {
            return
        }
        this.packetList.clear()
        this.packetList.addAll(packetList)
        notifyDataSetChanged()
    }

    fun addPacket(packet: PacketRecord?) {
        if (packet == null) {
            return
        }
        this.packetList.add(packet)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PacketHolder {
        return PacketHolder(LayoutInflater.from(context).inflate(R.layout.item_packet_holder, p0, false))
    }

    override fun getItemCount(): Int {
        return packetList.size
    }

    override fun onBindViewHolder(p0: PacketHolder, p1: Int) {
        p0.bindData(packetList[p1])
    }
}

class PacketHolder : RecyclerView.ViewHolder {

    private var tip: TextView? = null

    constructor(itemView: View) : super(itemView) {
        tip = itemView.findViewById(R.id.tip)
    }

    fun bindData(packetRecord: PacketRecord) {
        val time = ToolUtil.getTimeShowString(packetRecord.time)
        val userName = packetRecord.postUser
        val num = packetRecord.num.toString() + "¥"
        tip!!.setText("$time 抢了$userName $num")
    }
}
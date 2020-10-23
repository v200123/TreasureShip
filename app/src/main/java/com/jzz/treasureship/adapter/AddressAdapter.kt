package com.jzz.treasureship.adapter

import android.widget.CheckBox
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.Address

import com.lc.liuchanglib.adapterUtils.setChecked

class AddressAdapter(layoutResId: Int = R.layout.item_address) :
    BaseBindAdapter<Address>(layoutResId) {

    init {
        addChildClickViewIds(
            R.id.tv_addr_edit,
            R.id.tv_addr_delete,
            R.id.layout_item,
            R.id.cb_setDefault
        )
    }

    override fun convert(holder: BaseViewHolder, item: Address) {
        super.convert(holder, item)
        holder.setText(R.id.tv_name, item.consignee)
        holder.setText(R.id.tv_phone, item.mobile)

        val address = "${item.provinceName} ${item.cityName} ${item.districtName} ${item.address}"
        holder.setText(R.id.tv_address, address)
        holder.getView<CheckBox>(R.id.cb_setDefault).isEnabled = item.isDefault != 1
        holder.setChecked(R.id.cb_setDefault, item.isDefault == 1)
    }
}
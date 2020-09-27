package com.jzz.treasureship.adapter

import android.widget.CheckBox
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.Address

class AddressAdapter(layoutResId: Int = R.layout.item_address) :
    BaseBindAdapter<Address>(layoutResId, BR.address) {

    override fun convert(helper: BindViewHolder, item: Address) {
        super.convert(helper, item)
        helper.setText(R.id.tv_name, item.consignee)
        helper.setText(R.id.tv_phone, item.mobile)

        val address = "${item.provinceName} ${item.cityName} ${item.districtName} ${item.address}"
        helper.setText(R.id.tv_address, address)
        helper.getView<CheckBox>(R.id.cb_setDefault).isEnabled = item.isDefault == 1
        helper.setChecked(R.id.cb_setDefault, item.isDefault == 1)

        helper.addOnClickListener(R.id.tv_addr_edit)
        helper.addOnClickListener(R.id.tv_addr_delete)
        helper.addOnClickListener(R.id.layout_item)
        helper.addOnClickListener(R.id.cb_setDefault)
    }
}
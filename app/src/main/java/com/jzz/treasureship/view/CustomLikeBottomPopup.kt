package com.jzz.treasureship.view

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.CollectAdapter
import com.jzz.treasureship.model.bean.CollectCategory
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.dialog_home_like.view.*
import kotlin.math.roundToInt

//收藏
class CustomLikeBottomPopup(
    context: Context,
    var videoId: Int,
    var type:Int,//0收藏，1移动视频
    list: ArrayList<CollectCategory>?,
    viewModel: HomeViewModel
) :
    BottomPopupView(context) {

    private val mList: ArrayList<CollectCategory>? = list
    private val mAdapter by lazy { CollectAdapter() }
    private val viewModel: HomeViewModel = viewModel


    override fun getImplLayoutId() = R.layout.dialog_home_like



    override fun initPopupContent() {
        super.initPopupContent()

        initRecycleView()
    }

    private fun initRecycleView() {
        likeCategoryRecycle.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            adapter = mAdapter
        }

        mAdapter.run {
            this.removeAllFooterView()
            val addCategoryFooter: View = View.inflate(context, R.layout.dialog_collect_footer, null)
            addCategoryFooter.setOnClickListener {
                this@CustomLikeBottomPopup.dismiss()
                XPopup.Builder(context).asCustom(CustomInputDialog(context, viewModel)).show()
            }

            val dismissFooter: View = View.inflate(context, R.layout.dialog_dissmiss_footer, null)
            dismissFooter.setOnClickListener {
                var clickedCollect by PreferenceUtils(PreferenceUtils.CLICKED_COLLECT_ID, -1)
                clickedCollect = -1
                this@CustomLikeBottomPopup.dismiss()
            }

            this.addFooterView(addCategoryFooter)
            this.addFooterView(dismissFooter)
            setOnItemChildClickListener{ adapter, view, position ->
                when (view.id) {
                    R.id.layout_collects_item -> {
                        if (type != 1){
                            viewModel.addCollect(mAdapter.data[position].id, "", videoId)
                        }
                    }
                }
                var clickedCollect by PreferenceUtils(PreferenceUtils.CLICKED_COLLECT_ID, -1)
                clickedCollect = mAdapter.data[position].id

                dismiss()
            }

        }
//        likeCategoryRecycle.run {
//            layoutManager = LinearLayoutManager(context).also {
//                it.orientation = LinearLayoutManager.VERTICAL
//            }
//            adapter = mAdapter
//        }
        mAdapter.setList(mList)
        mAdapter.notifyDataSetChanged()
    }



    override fun getMaxHeight(): Int {
        return (XPopupUtils.getWindowHeight(context) * .75f).roundToInt()
    }


}

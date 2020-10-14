package com.jzz.treasureship.ui.questions

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.AnswersAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.AnswerItem
import com.jzz.treasureship.model.bean.Questionnaire
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.ui.wallet.WalletFragment
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CheckRewardDialog
import com.jzz.treasureship.view.NoticeGetRewardDialog
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_answer_questions.*
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.getViewModel


class QuestionsFragment : BaseVMFragment<HomeViewModel>() {

    private val mAnswersAdapter by lazy { AnswersAdapter() }
    private var questionnaire: Questionnaire? = null
    private var ansArray: ArrayList<String> = ArrayList()
    val go2Wallet by PreferenceUtils(PreferenceUtils.goto_wallet, false)

    companion object {
        fun newInstance(questions: Questionnaire): QuestionsFragment {
            val f = QuestionsFragment()
            val bundle = Bundle()
            bundle.putParcelable("questions", questions)
            f.arguments = bundle
            return f
        }
    }


    override fun getLayoutResId() = R.layout.fragment_answer_questions

    override fun initVM(): HomeViewModel = getViewModel()

    override fun initView() {
        //activity!!.nav_view.visibility = View.GONE
        tv_back.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }
        arguments?.let {
            questionnaire = it.getParcelable("questions")
        }
        questionnaire?.apply {
            Glide.with(this@QuestionsFragment).load(this.iconPath).into(iv_shopIcon)
            tv_title.text = this.title
            tv_subTitle.text = this.subtitle

            when (this.type) {
                1 -> {
                    //单选
                    tv_question.text = "（单选）${this.question}"
                }
                2 -> {
                    //多选
                    tv_question.text = "（多选）${this.question}"
                }
            }


            initRecycleView()
        }


    }

    private fun initRecycleView() {
        mAnswersAdapter.apply {

            val content = JSONObject(questionnaire?.content)
//            )
            val questionItems = JSONArray(content.get("items").toString())

            val list = ArrayList<AnswerItem>(questionItems.length())

            for (i in 0 until questionItems.length()) {
                val questionItem: AnswerItem =
                    AnswerItem(questionItems.get(i).toString(), false)
                list.add(questionItem)
            }

            setList(list)
            notifyDataSetChanged()
           setOnItemChildClickListener() { adapter, view, position ->
                when (view.id) {
                    R.id.layout_ansItem -> {
                        val json = JSONObject(mAnswersAdapter.getItem(position)!!.item)
                        val ans = json.get("item").toString()
                        when (questionnaire!!.type) {
                            1 -> {
                                if (mAnswersAdapter.getItem(position)!!.isClicked) {
                                    mAnswersAdapter.getItem(position)!!.isClicked = false
                                    view.background = null
                                    ansArray.remove(ans)
                                } else {
                                    mAnswersAdapter.getItem(position)!!.isClicked = true
                                    view.background = context!!.resources.getDrawable(R.color.color_answer_selected)
                                }

                                for (item in mAnswersAdapter.data) {
                                    if (mAnswersAdapter.getItem(position)!!.item != item.item) {
                                        mAnswersAdapter.getItem(position)!!.isClicked = false
                                        notifyDataSetChanged()
                                    }
                                }
                                ansArray.clear()
                                ansArray.add(ans)
                            }
                            2 -> {
                                if (mAnswersAdapter.getItem(position)!!.isClicked) {
                                    mAnswersAdapter.getItem(position)!!.isClicked = false
                                    view.background = null
                                    ansArray.remove(ans)
                                } else {
                                    mAnswersAdapter.getItem(position)!!.isClicked = true
                                    view.background = context!!.resources.getDrawable(R.color.greenYellow)
                                    ansArray.add(ans)
                                }
                            }
                        }
                    }
                }
            }
        }

        rcv_answers.apply {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            adapter = mAnswersAdapter
        }
    }

    override fun initData() {
        layout_clickSubmit.setOnClickListener {
            if (ansArray.isNotEmpty()) {
                Log.d("answers", ansArray.toString())
                mViewModel.submitQuestionnaire(ansArray.toString(), questionnaire!!.id!!)
            } else {
                ToastUtils.showShort("您还未选择答案！")
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    override fun startObserve() {
        mViewModel.apply {
            redEnvState.observe(this@QuestionsFragment, Observer {

                it.showSuccess?.let {
                    XPopup.Builder(context)
                        .asCustom(NoticeGetRewardDialog(context!!, mViewModel)).show()
                }

                it.showError?.let { err ->
                    Log.e("redEnvState", err)
                    ToastUtils.showShort(err)
                    activity!!.supportFragmentManager.popBackStack()
                }
            })

            rewardState.observe(this@QuestionsFragment, Observer {

                it.showSuccess?.let {
                    XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {
                        override fun onDismiss(popupView: BasePopupView) {
                            super.onDismiss(popupView)
                            if (go2Wallet) {
                                activity!!.supportFragmentManager.beginTransaction()
                                    .addToBackStack(WalletFragment.javaClass.name)
                                    .hide(this@QuestionsFragment)//隐藏当前Fragment
                                    .add(
                                        R.id.frame_content,
                                        WalletFragment.newInstance(),
                                        WalletFragment.javaClass.name
                                    )
                                    .commit()
                            } else {
                                activity!!.supportFragmentManager.popBackStack()
                            }
                        }
                    }
                    )
                        .asCustom(it.redEnvelopeRecord?.amount?.let { it1 -> CheckRewardDialog(context!!, it1) }).show()
                }

                it.showError?.let { err ->
                    Log.e("rewardState", err)
                    ToastUtils.showShort(err)
                    activity!!.supportFragmentManager.popBackStack()
                }
            })
        }
    }

    override fun initListener() {

    }


}

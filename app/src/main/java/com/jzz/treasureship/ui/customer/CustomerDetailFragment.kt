package com.jzz.treasureship.ui.customer


class CustomerDetailFragment
//    : BaseVMFragment<OrdersViewModel>()
//    private var mMemberInfo: DataX? = null
//
//    companion object {
//        fun newInstance(contacter: DataX): CustomerDetailFragment {
//            val f = CustomerDetailFragment()
//            val bundle = Bundle()
//            bundle.putParcelable("memberInfo", contacter)
//            f.arguments = bundle
//            return f
//        }
//    }
//
//    private val mAdapter by lazy { CustomerDetailsAdapter() }
//
//    override fun getLayoutResId() = R.layout.layout_customer_detail
//
//    override fun initVM(): OrdersViewModel = getViewModel()
//
//    override fun initView() {
//        //activity!!.nav_view.visibility = View.GONE
//        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))
//        tv_title.text = "客户详情"
//
//        rlback.setOnClickListener {
//            activity!!.supportFragmentManager.popBackStack()
//        }
//
//        arguments?.let {
//            mMemberInfo = it.getParcelable("memberInfo")
//        }
//
//        mMemberInfo?.let {
//            Glide.with(context!!).load(it.avatar).apply(RequestOptions.bitmapTransform(CircleCrop()))
//                .into(iv_customer_head)
//            tv_customer_name.text = it.nikeName
//
//            when (it.sex) {
//                0 -> {
//                    Glide.with(context!!).load(context!!.resources.getDrawable(R.drawable.icon_male))
//                        .into(iv_sex)
//                    tv_sex.text = "男"
//                }
//                1 -> {
//                    Glide.with(context!!).load(context!!.resources.getDrawable(R.drawable.icon_female))
//                        .into(iv_sex)
//                    tv_sex.text = "女"
//                }
//                else -> {
//                    Glide.with(context!!).load(context!!.resources.getDrawable(R.drawable.icon_male))
//                        .into(iv_sex)
//                    tv_sex.text = "其它"
//                }
//            }
//
//
//        }
//
//        rcv_orders.run {
//            layoutManager = LinearLayoutManager(context).also {
//                it.orientation = LinearLayoutManager.VERTICAL
//            }
//
//            mAdapter.run {
//                setOnItemChildClickListener() { adapter, view, position ->
//                    when (view.id) {
//                        R.id.tv_checkDoctorAdvice -> {
//                            XPopup.Builder(view.context)
//                                .asCustom(CustomCheckDoctorAdvicePopup(view.context, mAdapter.getItem(position)!!.doctorAdvice))
//                                .show()
//                        }
//                        R.id.tv_check_commission -> {
//                            XPopup.Builder(view.context)
//                                .asCustom(CustomCheckCommissionPopup(view.context, mAdapter.getItem(position)!!))
//                                .show()
//                        }
//                    }
//                }
//            }
//
//            adapter = mAdapter
//        }
//
//        iv_remind.setOnClickListener {
//            XPopup.Builder(it.context).setPopupCallback(object : SimpleCallback() {
//                override fun onDismiss(popupView: BasePopupView) {
//                    super.onDismiss(popupView)
//                    val noticeTime by PreferenceUtils(PreferenceUtils.NOTICE_TIME, "")
//
//                    var noticeType by PreferenceUtils(PreferenceUtils.NOTICE_TYPE, -1)
//
//
//                    if (!noticeTime.isBlank()) {
//                        mViewModel.setNotice(mMemberInfo!!.id!!, noticeType, noticeTime)
//                    }
//
//                    noticeType = 1
//                }
//
//
//            }).asCustom(CustomSetNoticePopup(it.context)).show()
//        }
//    }
//
//
//    override fun initData() {
//        arguments?.let {
//            mViewModel.getOrderList(mMemberInfo!!.id, null, 8, 0)
//        }
//    }
//
//    override fun startObserve() {
//        mViewModel.apply {
//
//            noticeState.observe(this@CustomerDetailFragment, Observer {
//                it.showError?.let { message ->
//                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
//                }
//
//                it.needLogin?.let { needLogin ->
//                    if (needLogin) {
//                        ToastUtils.showShort("未登录，请登录后再操作！")
//                        startActivity(Intent(this@CustomerDetailFragment.context, LoginActivity::class.java))
//                    }
//                }
//
//                it.showSuccess.let {
//                    var noticeType by PreferenceUtils(PreferenceUtils.NOTICE_TYPE, -1)
//                    if (noticeType == 1) {
//                        ToastUtils.showShort("设置提醒成功！")
//                        noticeType = -1
//                    }
//
//                }
//            })
//        }
//    }
//
//    override fun initListener() {
//    }

//}
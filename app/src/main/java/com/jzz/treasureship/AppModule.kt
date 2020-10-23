package com.jzz.treasureship

import com.jzz.treasureship.constants.Constants
import com.jzz.treasureship.model.api.JzzApiService
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.repository.*
import com.jzz.treasureship.ui.address.AddressViewModel
import com.jzz.treasureship.ui.addressbook.AddressbookViewModel
import com.jzz.treasureship.ui.auth.viewmodel.UserViewModel
import com.jzz.treasureship.ui.goods.GoodsDetailViewModel
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.ui.invite.InviteViewModel
import com.jzz.treasureship.ui.login.LoginViewModel
import com.jzz.treasureship.ui.msg.MsgViewModel
import com.jzz.treasureship.ui.orders.OrdersViewModel
import com.jzz.treasureship.ui.paypal.PaypalViewModel
import com.jzz.treasureship.ui.ranking.RankingViewModel
import com.jzz.treasureship.ui.search.SearchViewModel
import com.jzz.treasureship.ui.shopcar.ShopCarViewModel
import com.jzz.treasureship.ui.trace.TraceViewModel
import com.jzz.treasureship.ui.wallet.WalletViewModel
import com.jzz.treasureship.ui.withdraw.WithdrawViewModel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { GoodsDetailViewModel(get(), get()) }
    viewModel { ShopCarViewModel(get(), get()) }
    viewModel { PaypalViewModel(get(), get()) }
    viewModel { AddressViewModel(get(), get()) }
    viewModel { OrdersViewModel(get(), get()) }
    viewModel { AddressbookViewModel(get(), get()) }
    viewModel { TraceViewModel(get(), get()) }
    viewModel { WalletViewModel(get(), get()) }
    viewModel { WithdrawViewModel(get(), get()) }
    viewModel { InviteViewModel(get(), get()) }
    viewModel { RankingViewModel(get(), get()) }
    viewModel { UserViewModel(get(), get()) }
    viewModel { MsgViewModel(get(), get()) }
}

val repositoryModule = module {
    single { JzzRetrofitClient.getService(JzzApiService::class.java, Constants.BASE_URL) }
    single { CoroutinesDispatcherProvider() }
    single { LoginRepository(get()) }
    single { HomeRepository() }
    single { HomeSearchRepository() }
    single { GoodsRepository() }
    single { GoodsCartRepository() }
    single { PaypalRepository() }
    single { AddressRepository() }
    single { OrdersRepository() }
    single { AddressBookRepository() }
    single { ExpressRepository() }
    single { WalletRespository() }
    single { WithdrawRepository() }
    single { InviteRespository() }
    single { RankingRepository() }
    single { UserRepository() }
    single { MsgRepository() }

}

val appModule = listOf(viewModelModule, repositoryModule)

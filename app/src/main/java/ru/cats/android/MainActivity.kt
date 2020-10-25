package ru.cats.android

import android.os.Bundle
import android.widget.Toast
import io.reactivex.disposables.Disposable
import moxy.MvpAppCompatActivity
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.cats.android.di.DI
import ru.cats.android.model.system.message.SystemMessageNotifier
import ru.cats.android.model.system.message.SystemMessageType
import ru.cats.android.ui.global.BaseFragment
import ru.cats.android.ui.global.MessageDialogFragment
import ru.rosnet.charging.presentation.app.AppPresenter
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import toothpick.ktp.KTP
import javax.inject.Inject

class MainActivity : MvpAppCompatActivity(), MvpView {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var systemMessageNotifier: SystemMessageNotifier

    private var notifierDisposable: Disposable? = null

    @InjectPresenter
    lateinit var presenter: AppPresenter

    @ProvidePresenter
    fun providePresenter(): AppPresenter = KTP
            .openScope(DI.APP_SCOPE)
            .getInstance(AppPresenter::class.java)

    private val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment

    private val navigator: Navigator =
            object : SupportAppNavigator(this, supportFragmentManager, R.id.container) {
                override fun setupFragmentTransaction(
                        command: Command,
                        currentFragment: androidx.fragment.app.Fragment?,
                        nextFragment: androidx.fragment.app.Fragment?,
                        fragmentTransaction: androidx.fragment.app.FragmentTransaction
                ) {
                    //fix incorrect order lifecycle callback of MainFragment
                    fragmentTransaction.setReorderingAllowed(true)
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        KTP.openScope(DI.APP_SCOPE).inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_container)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        subscribeOnSystemMessages()
        navigatorHolder.setNavigator(navigator)
    }


    override fun onPause() {
        navigatorHolder.removeNavigator()
        unsubscribeOnSystemMessages()
        super.onPause()
    }


    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: super.onBackPressed()
    }

    private fun showAlertMessage(message: String, title: String? = null) {
        if (title == null) {
            MessageDialogFragment.create(
                    message = message
            ).show(supportFragmentManager, null)
        } else {
            MessageDialogFragment.create(
                    message = message,
                    title = title
            ).show(supportFragmentManager, null)
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun subscribeOnSystemMessages() {
        notifierDisposable = systemMessageNotifier.notifier
                .subscribe { msg ->
                    when (msg.type) {
                        SystemMessageType.ALERT -> showAlertMessage(msg.text, msg.title)
                        SystemMessageType.TOAST -> showToastMessage(msg.text)
                    }
                }
    }

    private fun unsubscribeOnSystemMessages() {
        notifierDisposable?.dispose()
    }

}
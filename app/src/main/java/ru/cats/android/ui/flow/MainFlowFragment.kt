package ru.cats.android.ui.flow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_main_flow.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.cats.android.R
import ru.cats.android.Screens
import ru.cats.android.di.module.FlowNavigationModule
import ru.cats.android.model.system.events.EventNotifier
import ru.cats.android.model.system.events.EventType
import ru.cats.android.model.system.flow.FlowRouter
import ru.cats.android.presentation.main.MainFlowPresenter
import ru.cats.android.presentation.main.MainFlowView
import ru.cats.android.ui.cats.CatsFlowFragment
import ru.cats.android.ui.global.BaseFragment
import ru.cats.android.ui.global.FlowFragment
import ru.cats.android.ui.vote.FavoritesFlowFragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Back
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Replace
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class MainFlowFragment : BaseFragment(), MainFlowView {

    private val catsFlowTabFragment: CatsFlowFragment by lazy {
        childFragmentManager.findFragmentByTag(Screens.CATS_FLOW) as? CatsFlowFragment
                ?: CatsFlowFragment.newInstance()
    }

    private val favoritesFlowTabFragment: FavoritesFlowFragment by lazy {
        childFragmentManager.findFragmentByTag(Screens.FAVORITES_FLOW) as? FavoritesFlowFragment
                ?: FavoritesFlowFragment.newInstance()
    }

    //will point to the fragment that is currently visible
    private var currentTab: String = Screens.CATS_FLOW
    val currentTabKey = "CURRENT_TAB"

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var flowRouter: FlowRouter

    var exitPressed = false

    private var menuStateDisposable: Disposable? = null

    @Inject
    lateinit var eventNotifier: EventNotifier

    private var eventDisposable: Disposable? = null

    override val layoutRes = R.layout.fragment_main_flow

    private val currentFragment
        get() = childFragmentManager.findFragmentById(R.id.mainContainer) as? BaseFragment


    @InjectPresenter
    lateinit var presenter: MainFlowPresenter

    @ProvidePresenter
    fun providePresenter(): MainFlowPresenter {
        return scope.getInstance(MainFlowPresenter::class.java)
    }

    override fun installModules(scope: Scope) {
        scope.installModules(
                FlowNavigationModule(scope.getInstance(Router::class.java))
        )
    }

    private fun subscribeOnEvents() {
        eventDisposable = eventNotifier.notifier
                .subscribe { msg ->
                    when (msg.type) {
                        EventType.SHOW_VOTE -> {
                            bottomNavigationView.selectedItemId = R.id.navigation_favorites
                        }
                        else -> {
                        }
                    }
                }
    }

    private fun unsubscribeOnSystemMessages() {
        eventDisposable?.dispose()
    }

    private val navigator: Navigator by lazy {
        object : SupportAppNavigator(this.activity!!, childFragmentManager, R.id.mainContainer) {

            override fun applyCommands(commands: Array<out Command>) {
                //super.applyCommands(commands)
                for (command in commands) {
                    applyCommand(command)
                }
            }

            override fun applyCommand(command: Command) {
                when (command) {
                    is Back -> router.exit()
                    is Replace -> {
                        when (command.screen.screenKey) {
                            Screens.CATS_FLOW -> changeTab(
                                    catsFlowTabFragment,
                                    Screens.CATS_FLOW
                            )
                            Screens.FAVORITES_FLOW -> changeTab(favoritesFlowTabFragment, Screens.FAVORITES_FLOW)
                        }
                    }
                    else -> super.applyCommand(command)
                }
            }

            override fun setupFragmentTransaction(
                    command: Command,
                    currentFragment: Fragment?,
                    nextFragment: Fragment?,
                    fragmentTransaction: FragmentTransaction
            ) {
                // Fix incorrect order lifecycle callback of MainFragment
                fragmentTransaction.setReorderingAllowed(true)
            }

            //we could also use attach() and detach() instead of show() and hide().
            private fun changeTab(targetFragment: FlowFragment, key: String) {
                with(childFragmentManager.beginTransaction()) {
                    childFragmentManager.fragments.filter { it != targetFragment }.forEach {
                        hide(it)
                        it.userVisibleHint =
                                false //since hide doesnt trigger onPause, we use this instead to let the fragment know it is not visible
                    }
                    targetFragment.let {
                        currentTab = key
                        if (it.isAdded) {
                            show(it)
                        } else add(R.id.mainContainer, it, key)
                        it.userVisibleHint = true
                    }
                    commit()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toothpick.inject(this, scope)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_cats -> {
                    flowRouter.replaceScreen(Screens.CatsFlow)
                }
                R.id.navigation_favorites -> {
                    flowRouter.replaceScreen(Screens.FavoritesFlow)
                }
            }
            true
        }

        if (savedInstanceState != null) {
            bottomNavigationView.selectedItemId = R.id.navigation_cats
        } else {
            savedInstanceState?.getString(currentTabKey)?.let { currentTab = it }
            when (currentTab) {
                Screens.CATS_FLOW -> {
                    bottomNavigationView.selectedItemId = R.id.navigation_cats
                }
                Screens.FAVORITES_FLOW -> {
                    bottomNavigationView.selectedItemId = R.id.navigation_favorites
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(currentTabKey, currentTab)
    }


    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
        subscribeOnEvents()
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        menuStateDisposable?.dispose()
        unsubscribeOnSystemMessages()
        super.onPause()
    }


    override fun onBackPressed() {
        val activeFlow = childFragmentManager.findFragmentByTag(currentTab) as? FlowFragment
        activeFlow?.onBackPressed() ?: router.exit()
    }

}
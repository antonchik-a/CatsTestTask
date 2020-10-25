package ru.cats.android.ui.global

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import moxy.MvpAppCompatFragment
import ru.cats.android.R
import ru.cats.android.di.DI
import ru.cats.android.extension.objectScopeName
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import toothpick.ktp.KTP


private const val PROGRESS_TAG = "bf_progress"
private const val STATE_SCOPE_NAME = "state_scope_name"


abstract class BaseFragment : MvpAppCompatFragment() {

    internal val ARG_BUNDLE = "arguments"

    abstract val layoutRes: Int

    private var instanceStateSaved: Boolean = false

    private val viewHandler = Handler()

    protected open val parentScopeName: String by lazy {
        (parentFragment as? BaseFragment)?.fragmentScopeName
                ?: DI.APP_SCOPE
    }

    private lateinit var fragmentScopeName: String
    protected lateinit var scope: Scope
        private set

    protected open fun installModules(scope: Scope) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        fragmentScopeName = savedInstanceState?.getString(STATE_SCOPE_NAME) ?: objectScopeName()

        if (Toothpick.isScopeOpen(fragmentScopeName)) {
            Timber.d("Get exist UI scope: $fragmentScopeName")
            scope = Toothpick.openScope(fragmentScopeName)
        } else {
            Timber.d("Init new UI scope: $fragmentScopeName")
            scope = Toothpick.openScopes(parentScopeName, fragmentScopeName)
            installModules(scope)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) =
            inflater.inflate(layoutRes, container, false)

    override fun onResume() {
        super.onResume()
        instanceStateSaved = false
    }

    // Fix for async views (like swipeToRefresh and RecyclerView)
    // If synchronously call actions on swipeToRefresh in sequence show and hide then swipeToRefresh will not hidden
    protected fun postViewAction(action: () -> Unit) {
        viewHandler.post(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewHandler.removeCallbacksAndMessages(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        instanceStateSaved = true
        outState.putString(STATE_SCOPE_NAME, fragmentScopeName)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (needCloseScope()) {
            // Destroy this fragment with scope
            Timber.d("Destroy UI scope: $fragmentScopeName")
            KTP.closeScope(scope.name)
        }
    }

    // This is android, baby!
    private fun isRealRemoving(): Boolean =
            (isRemoving && !instanceStateSaved) || // Because isRemoving == true for fragment in backstack on screen rotation
                    ((parentFragment as? BaseFragment)?.isRealRemoving() ?: false)

    // It will be valid only for 'onDestroy()' method
    private fun needCloseScope(): Boolean =
            when {
                activity?.isChangingConfigurations == true -> false
                activity?.isFinishing == true -> true
                else -> isRealRemoving()
            }


    protected fun showErrorMessage(message: String) {

        var adb = androidx.appcompat.app.AlertDialog.Builder(context!!)
        adb.setTitle(R.string.error_dialog_title)
        adb.setMessage(message)
        adb.setPositiveButton(R.string.ok, DialogInterface.OnClickListener({ dialog, which ->
            dialog.dismiss()
        }))
        adb.show()
    }

    protected fun showSuccessMessage(message: String) {
        var adb = androidx.appcompat.app.AlertDialog.Builder(context!!)
        adb.setTitle(R.string.dialog_title)
        adb.setMessage(message)
        adb.setPositiveButton(R.string.ok, DialogInterface.OnClickListener({ dialog, which ->
            dialog.dismiss()
        }))
        adb.show()
    }

    protected fun showDialog(title: String, message: String) {
        var adb = android.app.AlertDialog.Builder(context)
        adb.setTitle(title)
        adb.setMessage(message)
        adb.setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        adb.show()
    }

    open fun onBackPressed() {}


    fun hideKeyboard(view: View?) {
        if (view != null) {
            val imm = activity?.getSystemService(
                    Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

}


@file:Suppress("UNCHECKED_CAST")

package com.c22_ce02.awmonitorapp.utils

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * ini digunakan untuk delegate viewBinding di activity.
 * penggunaan : private val binding by viewBinding<NamaActivityBinding>()
 */
inline fun <reified T : ViewBinding> viewBinding() =
    AppCompatActivityViewBindingDelegate(T::class.java)

/**
 * ini digunakan untuk delegate viewBinding di fragment.
 * penggunaan : private val binding by viewBinding<NamaFragmentBinding>()
 */
inline fun <reified T : ViewBinding> Fragment.viewBinding() =
    FragmentViewBindingDelegate(T::class.java, this)

class AppCompatActivityViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>
) :
    ReadOnlyProperty<AppCompatActivity, T> {

    private var binding: T? = null

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        binding?.let { return it }

        val inflateMethod = bindingClass.getMethod("inflate", LayoutInflater::class.java)
        val invokeLayout = inflateMethod.invoke(null, thisRef.layoutInflater) as T

        thisRef.setContentView(invokeLayout.root)
        return invokeLayout.also { this.binding = it }
    }
}

class FragmentViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>,
    private val fragment: Fragment
) : ReadOnlyProperty<Fragment, T> {

    private var binding: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        binding?.let { return it }

        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            binding = null
                        }
                    })
                }
            }
        })

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("${thisRef::class.java.simpleName} : Cannot access view bindings. View lifecycle is ${lifecycle.currentState}!")
        }

        val bindMethod = bindingClass.getMethod("bind", View::class.java)
        val invoke = bindMethod.invoke(null, thisRef.requireView()) as T

        return invoke.also { this.binding = it }
    }
}
package com.core.presentation.navigation3.model.entry

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.core.presentation.navigation3.manager.INavigationGraphManager
import com.core.presentation.navigation3.model.destination.INavigationDestination
import dagger.Lazy
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

abstract class INavigationEntry<out T : INavigationDestination, out VM: ViewModel> {

    abstract val navigator: Lazy<INavigationGraphManager>

    val destination: KClass<out T> by lazy {
        extractTypeArgument(index = 0)
    }

    val viewModel: KClass<out VM> by lazy {
        extractTypeArgument(index = 1)
    }

    @Composable
    abstract fun Render(
        params: @UnsafeVariance T,
        viewModel: @UnsafeVariance VM
    )

    @Suppress("UNCHECKED_CAST")
    private fun <R : Any> extractTypeArgument(index: Int): KClass<R> {
        var current: Class<*>? = this::class.java

        while (current != null && current != Any::class.java) {
            val genericSuper = current.genericSuperclass
            if (genericSuper is ParameterizedType && genericSuper.rawType == INavigationEntry::class.java) {
                return (genericSuper.actualTypeArguments[index] as Class<R>).kotlin
            }

            current = current.superclass
        }


        throw NoSuchElementException("Generic type not found")
    }
}
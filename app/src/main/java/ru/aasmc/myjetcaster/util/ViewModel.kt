package ru.aasmc.myjetcaster.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Returns a [ViewModelProvider.Factory] which will return the result of [create] whet it's
 * [ViewModelProvider.Factory.create] function is called.
 *
 * If the created ViewModel doesn't match the requested class, an [IllegalArgumentException]
 * is thrown.
 */
fun <VM : ViewModel> viewModelProviderFactoryOf(
    create: () -> VM
): ViewModelProvider.Factory = SimpleFactory(create)

private class SimpleFactory<VM : ViewModel>(
    private val create: () -> VM
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val vm = create()
        if (modelClass.isInstance(vm)) {
            @Suppress("UNCHECKED_CAST")
            return vm as T
        }
        throw IllegalArgumentException("Can't create ViewModel for class: $modelClass")
    }
}
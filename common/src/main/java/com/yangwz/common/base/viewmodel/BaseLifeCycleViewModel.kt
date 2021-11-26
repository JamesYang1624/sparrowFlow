package com.yangwz.common.base.viewmodel

import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import com.yangwz.common.base.ViewEvent
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject


/**
 * @author : JamesYang
 * @date : 2021/11/26 15:19
 * @GitHub : https://github.com/JamesYang1624
 */
open class BaseLifeCycleViewModel :ViewModel(), LifecycleScopeProvider<ViewEvent>{
    private val lifecycleEvents = BehaviorSubject.createDefault(ViewEvent.CREATED)
    companion object {
        var CORRESPONDING_EVENTS: CorrespondingEventsFunction<ViewEvent> = CorrespondingEventsFunction { event ->
            when (event) {
                ViewEvent.CREATED -> ViewEvent.DESTROY
                else -> throw LifecycleEndedException(
                    "Cannot bind to ViewModel lifecycle after onCleared.")
            }
        }
    }
    fun <T> auto(provider: ScopeProvider): AutoDisposeConverter<T> {
        return AutoDispose.autoDisposable(provider)
    }
    override fun lifecycle(): Observable<ViewEvent> {
        return lifecycleEvents.hide()
    }

    override fun correspondingEvents(): CorrespondingEventsFunction<ViewEvent> {
        return CORRESPONDING_EVENTS
    }

    override fun peekLifecycle(): ViewEvent? {
        return lifecycleEvents.value as ViewEvent
    }
    /**
     * Emit the [ViewModelEvent.CLEARED] event to
     * dispose off any subscriptions in the ViewModel.
     */
    override fun onCleared() {
        lifecycleEvents.onNext(ViewEvent.DESTROY)
        super.onCleared()
    }

}
package dev.drewhamilton.rxpreferences.example

import dagger.BindsInstance
import dagger.Component
import dev.drewhamilton.rxpreferences.example.edit.EditingFragment
import dev.drewhamilton.rxpreferences.example.observe.ObservationFragment

@Component(modules = [
    ApplicationModule::class,
    PersistenceModule::class
])
interface ApplicationComponent {

    fun inject(editingFragment: EditingFragment)

    fun inject(observationFragment: ObservationFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: ExampleApplication): ApplicationComponent
    }

    companion object {
        fun create(application: ExampleApplication) = DaggerApplicationComponent.factory().create(application)
    }
}

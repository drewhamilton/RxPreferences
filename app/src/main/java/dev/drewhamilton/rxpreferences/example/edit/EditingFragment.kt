package dev.drewhamilton.rxpreferences.example.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import dev.drewhamilton.rxpreferences.example.ExampleApplication
import dev.drewhamilton.rxpreferences.example.R
import dev.drewhamilton.rxpreferences.example.base.ui.GenericViewModelFactory
import dev.drewhamilton.rxpreferences.example.base.ui.RxFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.edit.integerValue
import kotlinx.android.synthetic.main.edit.putButton
import kotlinx.android.synthetic.main.edit.removeButton
import kotlinx.android.synthetic.main.edit.stringValue
import javax.inject.Inject

class EditingFragment : RxFragment() {

    @Suppress("ProtectedInFinal")
    @Inject protected lateinit var viewModelFactory: GenericViewModelFactory<EditingViewModel>

    private lateinit var editingViewModel: EditingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        ExampleApplication.applicationComponent.inject(this)
        editingViewModel = ViewModelProviders.of(this, viewModelFactory).get()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.edit, container)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        integerValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                putButton.isEnabled = s?.isNotEmpty() ?: false
            }
        })

        editingViewModel.getExampleStringOnce()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { string -> stringValue.setText(string) }
            .trackUntilDestroyView()
        editingViewModel.getExampleIntegerOnce()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { integer -> integerValue.setText(integer.toString()) }
            .trackUntilDestroyView()

        putButton.setOnClickListener {
            editingViewModel.setExampleValues(stringValue.text.toString(), integerValue.text.toString().toInt())
                .subscribe()
                .trackUntilDestroyView()
        }
        removeButton.setOnClickListener {
            editingViewModel.removeExampleValues()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    stringValue.text = null
                    integerValue.text = null
                }
                .trackUntilDestroyView()
        }
    }
}

package drewhamilton.rxpreferences.example.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import drewhamilton.rxpreferences.example.R
import drewhamilton.rxpreferences.example.base.ui.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.edit.*

class EditingFragment : RxFragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.edit, container)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val editingViewModel = ViewModelProviders.of(this).get<EditingViewModel>()

    integerValue.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

      override fun afterTextChanged(s: Editable?) {
        putButton.isEnabled = s?.isNotEmpty() ?: false
      }
    })

    editingViewModel.getExampleString()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { it -> stringValue.setText(it) }
        .trackUntilDestroyView()
    editingViewModel.getExampleInteger()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { it -> integerValue.setText(it.toString()) }
        .trackUntilDestroyView()

    putButton.setOnClickListener {
      editingViewModel.setExampleValues(stringValue.text.toString(), integerValue.text.toString().toInt())
          .subscribe()
          .trackUntilDestroyView()
    }
    removeButton.setOnClickListener {
      editingViewModel.removeExampleValues()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { integerValue.text = null }
          .trackUntilDestroyView()
    }
  }
}

package drewhamilton.rxpreferences.example.edit

import android.os.Bundle
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

    editingViewModel.getExampleString()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { it -> string.setText(it) }
        .trackUntilDestroyView()
    editingViewModel.getExampleInteger()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { it -> integer.setText(it.toString()) }
        .trackUntilDestroyView()

    putString.setOnClickListener {
      editingViewModel.setExampleString(string.text.toString())
          .subscribe()
          .trackUntilDestroyView()
    }
    removeString.setOnClickListener {
      editingViewModel.removeExampleString()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { string.text = null }
          .trackUntilDestroyView()
    }

    putInteger.setOnClickListener {
      editingViewModel.setExampleInteger(integer.text.toString().toInt())
          .subscribe()
          .trackUntilDestroyView()
    }
    removeInteger.setOnClickListener {
      editingViewModel.removeExampleInteger()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe { integer.text = null }
          .trackUntilDestroyView()
    }
  }
}

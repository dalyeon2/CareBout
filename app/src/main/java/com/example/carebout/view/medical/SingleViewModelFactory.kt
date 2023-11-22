import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.carebout.view.medical.MedicalViewModel

class SingleViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    companion object {
        private var instance: SingleViewModelFactory? = null
        fun getInstance() = instance ?: synchronized(SingleViewModelFactory::class.java) {
            instance ?: SingleViewModelFactory().also { instance = it }
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MedicalViewModel::class.java) -> MedicalViewModel.getInstance()
                else -> throw IllegalArgumentException("Unknown viewModel class $modelClass")
            }
        } as T
}
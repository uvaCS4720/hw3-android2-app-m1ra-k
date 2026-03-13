import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class BasketballScoresViewModel(private val repository: BasketballRepository) : ViewModel() {

    private val _games = MutableStateFlow<List<GameEntity>>(emptyList())
    val games: StateFlow<List<GameEntity>> = _games.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _isWomen = MutableStateFlow(false)
    val isWomen: StateFlow<Boolean> = _isWomen.asStateFlow()

    init {
        loadData()
    }

    fun toggleGender() {
        _isWomen.value = !_isWomen.value
        loadData()
    }

    fun updateDate(newDate: LocalDate) {
        _selectedDate.value = newDate
        loadData()
    }

    fun refresh() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val date = _selectedDate.value
            val gender = if (_isWomen.value) "women" else "men"

            val dateString = date.toString()

            _games.value = repository.getGamesFromDb(dateString, gender)

            _isLoading.value = true
            try {
                repository.fetchAndSaveScores(gender, date)

                _games.value = repository.getGamesFromDb(dateString, gender)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
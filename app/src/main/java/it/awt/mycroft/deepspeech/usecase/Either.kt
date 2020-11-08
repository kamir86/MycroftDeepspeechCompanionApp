package it.awt.mycroft.deepspeech.usecase

sealed class Either<out E, out S> {
    data class Error<out E>(val error: E) : Either<E, Nothing>()
    data class Success<out S>(val data: S) : Either<Nothing, S>()

    val isSuccess get() = this is Success<S>
    val isError get() = this is Error<E>

    fun <E> error(error: E) = Error(error)
    fun <S> success(data: S) = Success(data)

    fun consume(onError: (E) -> Any, onSuccess: (S) -> Any): Any =
            when (this) {
                is Error -> onError(error)
                is Success -> onSuccess(data)
            }
}
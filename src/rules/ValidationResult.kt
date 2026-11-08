package rules

sealed class ValidationResult {
    data object Valid : ValidationResult()

    data class Invalid(val reason: String) : ValidationResult()

    val isValid: Boolean
        get() = this is Valid
}

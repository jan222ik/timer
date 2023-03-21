class Retry {
    private var instance: Instance = Instance(-1) { true }

    internal data class Instance(
        val remRepeats: Int,
        val block: () -> Boolean
    )

    fun times(count: Int): Retry {
        instance = instance.copy(remRepeats = count)
        return this
    }

    private fun run(instance: Instance): Boolean {
        if (instance.remRepeats < 0) return false
        val success = instance.block.invoke()
        return if (success) {
            true
        } else {
            run(instance.copy(remRepeats = instance.remRepeats.dec()))
        }
    }

    fun test(block: () -> Boolean, onSuccess: () -> Unit) {
        if (run(instance.copy(block = block))) {
            onSuccess.invoke()
        }
    }
}

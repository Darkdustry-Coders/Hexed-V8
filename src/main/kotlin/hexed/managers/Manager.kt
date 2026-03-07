package hexed.managers

// Manager lifecycle methods are called every round
interface Manager {
    fun init()
    fun reset()
}
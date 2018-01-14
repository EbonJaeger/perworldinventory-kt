package me.ebonjaeger.perworldinventory

class UpdateTimeoutsTask(private val plugin: PerWorldInventory) : Runnable
{

    override fun run()
    {
        if (plugin.timeouts.isEmpty())
        {
            return
        }

        val iter = plugin.timeouts.entries.iterator()
        while (iter.hasNext())
        {
            val e = iter.next()
            val value = e.value - 1
            if (value > 0)
            {
                e.setValue(value)
            } else
            {
                iter.remove()
            }
        }
    }
}

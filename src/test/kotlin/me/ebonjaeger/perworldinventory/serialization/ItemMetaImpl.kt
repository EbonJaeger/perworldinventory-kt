package me.ebonjaeger.perworldinventory.serialization

import com.google.common.collect.Multimap
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.tags.CustomItemTagContainer
import org.bukkit.persistence.PersistentDataContainer

/**
 * Implementation of [ItemMeta] for usage in tests.
 *
 * As a stand-in for state, a [map][providedMap] is maintained, which should be preserved during
 * serialization and deserialization.
 */
class ItemMetaTestImpl : ItemMeta, Damageable {

    val providedMap: MutableMap<String, Any>

    private var version = 0

    /**
     * Default constructor.
     */
    constructor() {
        this.providedMap = mutableMapOf()
    }

    /**
     * Deserialization constructor, as used in
     * [org.bukkit.configuration.serialization.ConfigurationSerialization.getConstructor].
     */
    constructor(map: MutableMap<String, Any>) {
        this.providedMap = map
    }

    override fun serialize(): MutableMap<String, Any> =
            HashMap(this.providedMap)

    override fun clone(): ItemMetaTestImpl {
        val mapClone = HashMap(providedMap)
        return ItemMetaTestImpl(mapClone)
    }

    override fun setDisplayName(name: String?) {
        throw NotImplementedError("not implemented")
    }

    override fun getLore(): MutableList<String> {
        throw NotImplementedError("not implemented")
    }

    override fun setLore(lore: MutableList<String>?) {
        throw NotImplementedError("not implemented")
    }

    override fun hasEnchants(): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun setLocalizedName(name: String?) {
        throw NotImplementedError("not implemented")
    }

    override fun hasLore(): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun addItemFlags(vararg itemFlags: ItemFlag?) {
        throw NotImplementedError("not implemented")
    }

    override fun hasDisplayName(): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun getItemFlags(): MutableSet<ItemFlag> {
        throw NotImplementedError("not implemented")
    }

    override fun setUnbreakable(unbreakable: Boolean) {
        throw NotImplementedError("not implemented")
    }

    override fun spigot(): ItemMeta.Spigot {
        throw NotImplementedError("not implemented")
    }

    override fun getDisplayName(): String {
        throw NotImplementedError("not implemented")
    }

    override fun getEnchants(): MutableMap<Enchantment, Int> {
        throw NotImplementedError("not implemented")
    }

    override fun getLocalizedName(): String {
        throw NotImplementedError("not implemented")
    }

    override fun isUnbreakable(): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun removeItemFlags(vararg itemFlags: ItemFlag?) {
        throw NotImplementedError("not implemented")
    }

    override fun hasLocalizedName(): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun getDamage(): Int
    {
        return 0
    }

    override fun hasDamage(): Boolean
    {
        throw NotImplementedError("not implemented")
    }

    override fun setDamage(damage: Int)
    {
        throw NotImplementedError("not implemented")
    }

    override fun addEnchant(ench: Enchantment, level: Int, ignoreLevelRestriction: Boolean): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun hasConflictingEnchant(ench: Enchantment): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun getCustomModelData(): Int {
        throw NotImplementedError("not implemented")
    }

    override fun setAttributeModifiers(attributeModifiers: Multimap<Attribute, AttributeModifier>?) {
        throw NotImplementedError("not implemented")
    }

    override fun removeAttributeModifier(attribute: Attribute): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun removeAttributeModifier(slot: EquipmentSlot): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun removeAttributeModifier(attribute: Attribute, modifier: AttributeModifier): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun hasCustomModelData(): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun addAttributeModifier(attribute: Attribute, modifier: AttributeModifier): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun getEnchantLevel(ench: Enchantment): Int {
        throw NotImplementedError("not implemented")
    }

    override fun setVersion(version: Int) {
        this.version = version
    }

    override fun hasEnchant(ench: Enchantment): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun setCustomModelData(data: Int?) {
        throw NotImplementedError("not implemented")
    }

    override fun hasAttributeModifiers(): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun getCustomTagContainer(): CustomItemTagContainer {
        throw NotImplementedError("not implemented")
    }

    override fun hasItemFlag(flag: ItemFlag): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun removeEnchant(ench: Enchantment): Boolean {
        throw NotImplementedError("not implemented")
    }

    override fun getAttributeModifiers(): Multimap<Attribute, AttributeModifier>? {
        throw NotImplementedError("not implemented")
    }

    override fun getAttributeModifiers(slot: EquipmentSlot): Multimap<Attribute, AttributeModifier> {
        throw NotImplementedError("not implemented")
    }

    override fun getAttributeModifiers(attribute: Attribute): MutableCollection<AttributeModifier> {
        throw NotImplementedError("not implemented")
    }

    override fun getPersistentDataContainer(): PersistentDataContainer {
        throw NotImplementedError("not implemented")
    }
}

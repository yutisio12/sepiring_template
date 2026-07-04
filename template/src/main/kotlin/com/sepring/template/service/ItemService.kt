package com.sepring.template.service

import com.sepring.template.model.Item
import com.sepring.template.repository.ItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class ItemService(private val itemRepository: ItemRepository) {

    fun findAll(): List<Item> = itemRepository.findAll()

    fun findById(id: Long): Item = itemRepository.findById(id)
        .orElseThrow { NoSuchElementException("Item not found: $id") }

    fun create(name: String, description: String?): Item {
        val now = Instant.now()
        val item = Item(name = name, description = description).apply {
            createdAt = now
            updatedAt = now
        }
        return itemRepository.save(item)
    }

    fun update(id: Long, name: String, description: String?): Item {
        val item = findById(id)
        item.name = name
        item.description = description
        return itemRepository.save(item)
    }

    fun delete(id: Long) {
        if (!itemRepository.existsById(id)) {
            throw NoSuchElementException("Item not found: $id")
        }
        itemRepository.deleteById(id)
    }
}

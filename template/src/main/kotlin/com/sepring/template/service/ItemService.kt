package com.sepring.template.service

import com.sepring.template.dto.PageResponse
import com.sepring.template.model.Item
import com.sepring.template.repository.ItemRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class ItemService(private val itemRepository: ItemRepository) {

    fun findAll(page: Int = 0, size: Int = 20, sort: String? = null): PageResponse<Item> {
        val sortObj = if (sort != null) {
            val parts = sort.split(",")
            if (parts.size == 2 && parts[1].equals("desc", ignoreCase = true))
                Sort.by(parts[0]).descending()
            else
                Sort.by(parts[0]).ascending()
        } else {
            Sort.by("id").ascending()
        }
        val pageable = PageRequest.of(page, size, sortObj)
        val pageResult = itemRepository.findAll(pageable)
        return PageResponse(
            content = pageResult.content,
            page = pageResult.number,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
            first = pageResult.isFirst,
            last = pageResult.isLast
        )
    }

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

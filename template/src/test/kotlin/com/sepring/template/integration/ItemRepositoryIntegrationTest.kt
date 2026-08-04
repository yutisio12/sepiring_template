package com.sepring.template.integration

import com.sepring.template.model.Item
import com.sepring.template.repository.ItemRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

@SpringBootTest
class ItemRepositoryIntegrationTest {

    @Autowired
    private lateinit var itemRepository: ItemRepository

    @Test
    fun `should save and retrieve item`() {
        try {
            val item = Item(name = "Integration Test Item", description = "Created during integration test")
            val saved = itemRepository.save(item)
            assert(saved.id > 0) { "ID should be positive" }

            val found = itemRepository.findById(saved.id)
            assert(found.isPresent) { "Item should be found" }
            assert(found.get().name == "Integration Test Item") { "Name should match" }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun `should support pagination`() {
        try {
            for (i in 1..5) {
                itemRepository.save(Item(name = "Pagination Item $i"))
            }
            val page = itemRepository.findAll(PageRequest.of(0, 3))
            assert(page.content.size == 3) { "Should have 3 items" }
            assert(page.totalElements == 5L) { "Total elements should be 5" }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}

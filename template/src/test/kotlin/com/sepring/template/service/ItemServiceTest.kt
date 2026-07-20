package com.sepring.template.service

import com.sepring.template.model.Item
import com.sepring.template.repository.ItemRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional

class ItemServiceTest {

    private lateinit var itemRepository: ItemRepository
    private lateinit var itemService: ItemService

    @BeforeEach
    fun setup() {
        itemRepository = mock(ItemRepository::class.java)
        itemService = ItemService(itemRepository)
    }

    @Test
    fun `findAll should return paginated items`() {
        val items = listOf(Item(1, "Item 1", "Desc 1"))
        `when`(itemRepository.findAll(any<Pageable>())).thenReturn(PageImpl(items))

        val result = itemService.findAll(0, 10, null)
        assertThat(result.content).hasSize(1)
        assertThat(result.page).isZero()
        assertThat(result.totalElements).isEqualTo(1)
    }

    @Test
    fun `findById should return item when exists`() {
        val item = Item(1, "Test Item", "Test Desc")
        `when`(itemRepository.findById(1L)).thenReturn(Optional.of(item))

        val result = itemService.findById(1)
        assertThat(result.name).isEqualTo("Test Item")
    }

    @Test
    fun `findById should throw when not found`() {
        `when`(itemRepository.findById(anyLong())).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { itemService.findById(99) }
    }

    @Test
    fun `create should save and return item`() {
        val item = Item(1, "New Item", "New Desc")
        `when`(itemRepository.save(any<Item>())).thenReturn(item)

        val result = itemService.create("New Item", "New Desc")
        assertThat(result.name).isEqualTo("New Item")
        verify(itemRepository).save(any<Item>())
    }

    @Test
    fun `delete should remove item when exists`() {
        `when`(itemRepository.existsById(1L)).thenReturn(true)

        itemService.delete(1)
        verify(itemRepository).deleteById(1L)
    }

    @Test
    fun `delete should throw when not found`() {
        `when`(itemRepository.existsById(anyLong())).thenReturn(false)

        assertThrows<NoSuchElementException> { itemService.delete(99) }
    }
}

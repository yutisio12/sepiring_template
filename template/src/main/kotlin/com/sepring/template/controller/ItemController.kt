package com.sepring.template.controller

import com.sepring.template.dto.PageResponse
import com.sepring.template.dto.PaginationRequest
import com.sepring.template.model.Item
import com.sepring.template.service.ItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

data class ItemRequest(
    @field:NotBlank @field:Size(max = 255)
    val name: String,
    @field:Size(max = 2000)
    val description: String?
)

data class ItemResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: String?,
    val updatedAt: String?
)

private fun Item.toResponse() = ItemResponse(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt?.toString(),
    updatedAt = updatedAt?.toString()
)

@RestController
@RequestMapping("/api/v1/items")
@Tag(name = "Items", description = "CRUD template — requires Bearer JWT")
@SecurityRequirement(name = "bearer-jwt")
class ItemController(private val itemService: ItemService) {

    @GetMapping
    @Operation(summary = "List items with pagination")
    fun findAll(request: PaginationRequest): PageResponse<ItemResponse> {
        val page = itemService.findAll(request.page, request.size, request.sort)
        return PageResponse(
            content = page.content.map { it.toResponse() },
            page = page.page,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            first = page.first,
            last = page.last
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID")
    fun findById(@PathVariable id: Long): ItemResponse =
        itemService.findById(id).toResponse()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new item")
    fun create(@Valid @RequestBody request: ItemRequest): ItemResponse =
        itemService.create(request.name, request.description).toResponse()

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing item")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: ItemRequest): ItemResponse =
        itemService.update(id, request.name, request.description).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an item")
    fun delete(@PathVariable id: Long) = itemService.delete(id)
}

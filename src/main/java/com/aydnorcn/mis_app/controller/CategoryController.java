package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.category.CreateCategoryRequest;
import com.aydnorcn.mis_app.entity.Category;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Retrieve category by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Category retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @Operation(
            summary = "Retrieve categories by pagination"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))),
            }
    )
    @Parameters({
            @Parameter(name = "page-no", in = ParameterIn.QUERY, description = "Page number", schema = @Schema(type = "integer")),
            @Parameter(name = "page-size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer")),
    })
    @GetMapping
    public ResponseEntity<PageResponseDto<Category>> getCategories(@RequestParam(name = "page-no", required = false, defaultValue = "0") int pageNo,
                                                                   @RequestParam(name = "page-size", required = false, defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(categoryService.getCategories(pageNo, pageSize));
    }

    @Operation(
            summary = "Create a new category"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Category created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If given name is not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict | If given name is already exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Category> createCategory(@Validated @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @Operation(
            summary = "Update category by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Category updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If given name is not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict | If given name is already exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Category> updateCategory(@PathVariable String categoryId, @Validated @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }

    @Operation(
            summary = "Delete category by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
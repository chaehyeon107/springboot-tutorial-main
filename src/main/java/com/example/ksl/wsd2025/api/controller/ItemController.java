package com.example.ksl.wsd2025.api.controller;

import com.example.ksl.wsd2025.api.dto.ItemDto;
import com.example.ksl.wsd2025.api.request.ItemCreateRequest;
import com.example.ksl.wsd2025.api.request.ItemUpdateRequest;
import com.example.ksl.wsd2025.api.response.ItemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    // 메모리용 임시 DB (예제용)
    private final Map<Long, ItemDto> store = new HashMap<>();
    private long sequence = 1L;


    // 1) GET: 전체 조회
    @GetMapping
    public ResponseEntity<?> getItems(@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "false") boolean serviceDown) {
        try {

            // 503 Service Unavailable
            if (serviceDown) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ItemResponse.error(Map.of("message", "서비스를 일시적으로 사용할 수 없습니다.")));
            }

            // 400 Bad Request
            if (limit <= 0) {
                return ResponseEntity.badRequest().body(ItemResponse.error(Map.of("message", "limit은 1 이상이어야 합니다.")));
            }

            // 422 Unprocessable Entity
            if (limit > 100) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ItemResponse.error(Map.of("message", "limit은 최대 100까지 가능합니다.")));
            }

            List<ItemDto> allItems = new ArrayList<>(store.values());

            List<ItemDto> result = allItems.stream().limit(limit).toList();

            // 206 Partial Content
            if (allItems.size() > limit) {
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(ItemResponse.success(result));
            }

            // 200 OK
            return ResponseEntity.ok(ItemResponse.success(result));

        } catch (Exception e) {

            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ItemResponse.error(Map.of("message", "서버 내부 오류가 발생했습니다.")));
        }
    }


    // 2) GET: 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean cached, @RequestParam(defaultValue = "false") boolean serviceDown) {
        try {

            // 503 Service Unavailable
            if (serviceDown) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ItemResponse.error(Map.of("message", "서비스를 일시적으로 사용할 수 없습니다.")));
            }

            // 400 Bad Request
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(ItemResponse.error(Map.of("message", "id는 1 이상이어야 합니다.")));
            }

            ItemDto item = store.get(id);

            // 404 Not Found
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ItemResponse.error(Map.of("message", "해당 아이템을 찾을 수 없습니다.")));
            }

            // 203 Non-Authoritative Information
            if (cached) {
                return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(ItemResponse.success(item));
            }

            // 200 OK
            return ResponseEntity.ok(ItemResponse.success(item));

        } catch (Exception e) {

            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ItemResponse.error(Map.of("message", "서버 내부 오류가 발생했습니다.")));
        }
    }


    // 1) POST: 아이템 생성
    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemCreateRequest request, @RequestParam(defaultValue = "false") boolean validateOnly, @RequestParam(defaultValue = "false") boolean serviceDown) {
        try {

            // 503 Service Unavailable
            if (serviceDown) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ItemResponse.error(Map.of("message", "서비스를 일시적으로 사용할 수 없습니다.")));
            }

            // 400 Bad Request
            if (request.getName() == null || request.getName().trim().isEmpty() || request.getPrice() == null || request.getPrice() < 0 || request.getItemRarity() == null || request.getStock_quantity() == null || request.getStock_quantity() < 0) {

                return ResponseEntity.badRequest().body(ItemResponse.error(Map.of("message", "입력값이 올바르지 않습니다.")));
            }

            // 409 Conflict
            boolean duplicate = store.values().stream().anyMatch(item -> item.getName().equalsIgnoreCase(request.getName()));

            if (duplicate) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ItemResponse.error(Map.of("message", "동일한 이름의 아이템이 이미 존재합니다.")));
            }

            // 200 OK
            if (validateOnly) {
                return ResponseEntity.ok(ItemResponse.success(Map.of("message", "아이템 입력값 검증에 성공했습니다.")));
            }

            ItemDto item = new ItemDto();

            item.setId(sequence++);
            item.setName(request.getName());
            item.setPrice(request.getPrice());
            item.setDescription(request.getDescription());
            item.setItemRarity(request.getItemRarity());
            item.setStock_quantity(request.getStock_quantity());

            store.put(item.getId(), item);

            // 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.success(item));

        } catch (Exception e) {

            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ItemResponse.error(Map.of("message", "서버 내부 오류가 발생했습니다.")));
        }
    }


    // 2) POST: 헤더 포함 요청
    @PostMapping("/with-header")
    public ResponseEntity<?> createItemWithHeader(@RequestBody ItemCreateRequest request, @RequestHeader(value = "X-USER-ID", required = false) String userId, @RequestHeader(value = "Authorization", required = false) String authorization, @RequestParam(defaultValue = "false") boolean validateOnly, @RequestParam(defaultValue = "false") boolean serviceDown) {
        try {

            // 503 Service Unavailable
            if (serviceDown) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ItemResponse.error(Map.of("message", "서비스를 일시적으로 사용할 수 없습니다.")));
            }

            // 400 Bad Request
            if (request.getName() == null || request.getName().trim().isEmpty() || request.getPrice() == null) {

                return ResponseEntity.badRequest().body(ItemResponse.error(Map.of("message", "아이템 이름과 가격은 필수입니다.")));
            }

            // 401 Unauthorized
            if (authorization == null || authorization.trim().isEmpty()) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ItemResponse.error(Map.of("message", "Authorization 헤더가 필요합니다.")));
            }

            // 200 OK
            if (validateOnly) {
                return ResponseEntity.ok(ItemResponse.success(Map.of("message", "요청 Header와 Body 검증에 성공했습니다.", "userId", userId == null ? "" : userId)));
            }

            ItemDto item = new ItemDto();

            item.setId(sequence++);
            item.setName(request.getName());
            item.setPrice(request.getPrice());
            item.setDescription(request.getDescription());
            item.setItemRarity(request.getItemRarity());
            item.setStock_quantity(request.getStock_quantity());

            store.put(item.getId(), item);

            Map<String, Object> result = new HashMap<>();

            result.put("item", item);
            result.put("userId", userId);
            result.put("authorization", authorization);

            // 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.success(result));

        } catch (Exception e) {

            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ItemResponse.error(Map.of("message", "서버 내부 오류가 발생했습니다.")));
        }
    }


    // 1) PUT: 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody ItemUpdateRequest request, @RequestParam(defaultValue = "false") boolean async, @RequestParam(defaultValue = "false") boolean serviceDown) {
        try {

            // 503 Service Unavailable
            if (serviceDown) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ItemResponse.error(Map.of("message", "서비스를 일시적으로 사용할 수 없습니다.")));
            }

            // 400 Bad Request
            if (id == null || id <= 0 || (request.getPrice() != null && request.getPrice() < 0) || (request.getStock_quantity() != null && request.getStock_quantity() < 0)) {

                return ResponseEntity.badRequest().body(ItemResponse.error(Map.of("message", "수정 요청값이 올바르지 않습니다.")));
            }

            ItemDto item = store.get(id);

            // 404 Not Found
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ItemResponse.error(Map.of("message", "해당 아이템을 찾을 수 없습니다.")));
            }

            // 202 Accepted
            if (async) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(ItemResponse.success(Map.of("message", "아이템 수정 요청이 접수되었습니다.", "id", id)));
            }

            if (request.getName() != null) {
                item.setName(request.getName());
            }

            if (request.getPrice() != null) {
                item.setPrice(request.getPrice());
            }

            if (request.getDescription() != null) {
                item.setDescription(request.getDescription());
            }

            if (request.getItemRarity() != null) {
                item.setItemRarity(request.getItemRarity());
            }

            if (request.getStock_quantity() != null) {
                item.setStock_quantity(request.getStock_quantity());
            }

            // 200 OK
            return ResponseEntity.ok(ItemResponse.success(item));

        } catch (Exception e) {

            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ItemResponse.error(Map.of("message", "서버 내부 오류가 발생했습니다.")));
        }
    }


    // 2) PUT: 재고 수량 변경
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestParam(required = false) Integer quantity, @RequestParam(defaultValue = "false") boolean async, @RequestParam(defaultValue = "false") boolean serviceDown) {
        try {

            // 503 Service Unavailable
            if (serviceDown) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ItemResponse.error(Map.of("message", "서비스를 일시적으로 사용할 수 없습니다.")));
            }

            // 400 Bad Request
            if (id == null || id <= 0 || quantity == null || quantity < 0) {
                return ResponseEntity.badRequest().body(ItemResponse.error(Map.of("message", "id와 재고 수량을 확인해주세요.")));
            }

            ItemDto item = store.get(id);

            // 404 Not Found
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ItemResponse.error(Map.of("message", "해당 아이템을 찾을 수 없습니다.")));
            }

            // 202 Accepted
            if (async) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(ItemResponse.success(Map.of("message", "재고 변경 요청이 접수되었습니다.", "id", id, "quantity", quantity)));
            }

            item.setStock_quantity(quantity);

            // 200 OK
            return ResponseEntity.ok(ItemResponse.success(item));

        } catch (Exception e) {

            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ItemResponse.error(Map.of("message", "서버 내부 오류가 발생했습니다.")));
        }
    }


    // 1) DELETE: 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean async, @RequestParam(defaultValue = "false") boolean serviceDown) {
        try {

            // 503 Service Unavailable
            if (serviceDown) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ItemResponse.error(Map.of("message", "서비스를 일시적으로 사용할 수 없습니다.")));
            }

            // 400 Bad Request
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(ItemResponse.error(Map.of("message", "id는 1 이상이어야 합니다.")));
            }

            ItemDto item = store.get(id);

            // 404 Not Found
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ItemResponse.error(Map.of("message", "해당 아이템을 찾을 수 없습니다.")));
            }

            // 202 Accepted
            if (async) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(ItemResponse.success(Map.of("message", "아이템 삭제 요청이 접수되었습니다.", "id", id)));
            }

            store.remove(id);

            // 200 OK
            return ResponseEntity.ok(ItemResponse.success(Map.of("message", "아이템이 삭제되었습니다.", "item", item)));

        } catch (Exception e) {

            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ItemResponse.error(Map.of("message", "서버 내부 오류가 발생했습니다.")));
        }
    }


    // 2) DELETE: 품절 상품 일괄 삭제
    @DeleteMapping("/out-of-stock")
    public ResponseEntity<?> deleteOutOfStockItems(@RequestParam(defaultValue = "false") boolean confirm, @RequestHeader(value = "X-ADMIN", required = false) String admin, @RequestParam(defaultValue = "false") boolean async, @RequestParam(defaultValue = "false") boolean serviceDown) {
        try {

            // 503 Service Unavailable
            if (serviceDown) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ItemResponse.error(Map.of("message", "서비스를 일시적으로 사용할 수 없습니다.")));
            }

            // 400 Bad Request
            if (!confirm) {
                return ResponseEntity.badRequest().body(ItemResponse.error(Map.of("message", "confirm=true가 필요합니다.")));
            }

            // 403 Forbidden
            if (admin == null || !admin.equalsIgnoreCase("true")) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ItemResponse.error(Map.of("message", "관리자 권한이 필요합니다.")));
            }

            // 202 Accepted
            if (async) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(ItemResponse.success(Map.of("message", "품절 상품 삭제 요청이 접수되었습니다.")));
            }

            int beforeSize = store.size();

            store.entrySet().removeIf(entry -> entry.getValue().getStock_quantity() != null && entry.getValue().getStock_quantity() == 0);

            int deletedCount = beforeSize - store.size();

            // 200 OK
            return ResponseEntity.ok(ItemResponse.success(Map.of("message", "품절 상품 삭제가 완료되었습니다.", "deletedCount", deletedCount)));

        } catch (Exception e) {

            // 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ItemResponse.error(Map.of("message", "서버 내부 오류가 발생했습니다.")));
        }
    }
}
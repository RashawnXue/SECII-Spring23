package org.fffd.l23o6.controller;

import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.pojo.vo.route.AddRouteRequest;
import org.fffd.l23o6.pojo.vo.route.RouteVO;
import org.fffd.l23o6.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    /**
     * 添加路线
     * @param request 添加路线request
     * @return success响应
     */
    @PostMapping("admin/route")
    public CommonResponse<?> addRoute(@Valid @RequestBody AddRouteRequest request) {
        routeService.addRoute(request.getName(), request.getStationIds());
        return CommonResponse.success();
    }

    /**
     * 获取路线列表
     * @return 路线列表
     */
    @GetMapping("admin/route")
    public CommonResponse<List<RouteVO>> getRoutes() {
        return CommonResponse.success(routeService.listRoutes());
    }

    /**
     * 获取对应路线
     * @param routeId 路线的id
     * @return 对应的RouteVO
     */
    @GetMapping("admin/route/{routeId}")
    public CommonResponse<RouteVO> getRoute(@PathVariable("routeId") Long routeId) {
        return CommonResponse.success(routeService.getRoute(routeId));
    }

    /**
     * 修改Route
     * @param routeId routeId
     * @param request 修改Route信息的request
     * @return success响应
     */
    @PutMapping("admin/route/{routeId}")
    public CommonResponse<?> editRoute(@PathVariable("routeId") Long routeId, @Valid @RequestBody AddRouteRequest request) {
        routeService.editRoute(routeId, request.getName(), request.getStationIds());
        return CommonResponse.success();
    }

    /**
     * 删除route
     * @param routeId routeId
     * @return success响应
     */
    @DeleteMapping("admin/route/{routeId}")
    public CommonResponse<?> deleteRoute(@PathVariable("routeId") Long routeId){
        routeService.deleteRoute(routeId);
        return CommonResponse.success();
    }
}
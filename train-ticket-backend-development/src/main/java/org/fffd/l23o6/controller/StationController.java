package org.fffd.l23o6.controller;

import java.util.List;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.fffd.l23o6.pojo.vo.station.AddStationRequest;
import org.fffd.l23o6.pojo.vo.station.StationVO;
import org.fffd.l23o6.service.StationService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    /**
     * 获取station列表
     * @return station列表
     */
    @GetMapping("station")
    public CommonResponse<List<StationVO>> listStations() {
        return CommonResponse.success(stationService.listStations());
    }

    /**
     * 根据stationId获取对应的station
     * @param stationId stationId
     * @return 对应的StationVO
     */
    @GetMapping("station/{stationId}")
    public CommonResponse<StationVO> getStation(@PathVariable Long stationId) {
        return CommonResponse.success(stationService.getStation(stationId));
    }

    /**
     * 添加station
     * @param request 添加station的request
     * @return success响应
     */
    @PostMapping("admin/station")
    public CommonResponse<?> addStation(@Valid @RequestBody AddStationRequest request) {
        stationService.addStation(request.getName());
        return CommonResponse.success();
    }

    /**
     * 修改station
     * @param stationId 想要修改的station的id
     * @param request 修改station信息的request
     * @return  success响应
     */
    @PutMapping("admin/station/{stationId}")
    public CommonResponse<?> editStation(@PathVariable("stationId") Long stationId, @Valid @RequestBody AddStationRequest request) {
        stationService.editStation(stationId, request.getName());
        return CommonResponse.success();
    }

    /**
     * 删除station
     * @param stationId 想要删除的station的id
     * @return success响应
     */
    @DeleteMapping("admin/station/{stationId}")
    public CommonResponse<?> deleteStation(@PathVariable("stationId") Long stationId){
        stationService.deleteStation(stationId);
        return CommonResponse.success();
    }
}

package sast.evento.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Location;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.EventModelMapper;
import sast.evento.mapper.LocationMapper;
import sast.evento.model.EventModel;
import sast.evento.service.EventService;
import sast.evento.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/8 13:44
 */
@Service
public class EventServiceImpl implements EventService {
    @Resource
    private EventModelMapper eventModelMapper;
    @Resource
    private LocationMapper locationMapper;

    @Resource
    private TimeUtil timeUtil;

    // 获取活动详情
    @Override
    public EventModel getEvent(Integer eventId) {
        if (eventId == null || eventId <= 0) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        EventModel eventModel = eventModelMapper.getById(eventId);
        if (eventModel == null) {
            return null;
        }

        String locationIdStr = eventModel.getLocation();
        if (locationIdStr != null && !"".equals(locationIdStr)) {
            Integer locationIdInt = Integer.valueOf(locationIdStr);
            String locationName = locationMapper.getLocationName(locationIdInt);
            eventModel.setLocation(locationName);
        }
        return eventModel;
    }

    // 查看用户历史活动列表（参加过已结束）
    @Override
    public List<EventModel> getHistory(Integer userId) {
        if (userId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        return eventModelMapper.getHistory(userId);
    }

    // 查看所有正在进行的活动列表
    @Override
    public List<EventModel> getConducting() {
        return eventModelMapper.getConducting();
    }

    // 查看最新活动列表（按开始时间正序排列未开始的活动）
    @Override
    public List<EventModel> getNewest() {
        return eventModelMapper.getNewest();
    }

    // 获取活动列表(分页）
    @Override
    public List<EventModel> getEvents(Integer page, Integer size) {
        Integer index = (page - 1) * size;
        return eventModelMapper.getEvents(index, size);
    }

    @Override
    public List<EventModel> postForEvents(List<Integer> typeId, List<Integer> departmentId, String time) {
        if (typeId.isEmpty()) {
            if (departmentId.isEmpty()) {
                if (time.isEmpty()) {
                    return getEvents(1, 10);
                }
                List<Date> date = timeUtil.getDateOfMonday(time);
                return eventModelMapper.getEventByTime(date.get(0), date.get(1));
            }
            if (time.isEmpty()) {
                return eventModelMapper.getEventByDepartmentId(departmentId);
            }
            List<Date> date = timeUtil.getDateOfMonday(time);
            return eventModelMapper.getEventByDepartmentIdAndTime(departmentId, date.get(0), date.get(1));
        }
        if (departmentId.isEmpty()) {
            if (time.isEmpty()) {
                return eventModelMapper.getEventByTypeId(typeId);
            }
            List<Date> date = timeUtil.getDateOfMonday(time);
            return eventModelMapper.getEventByTypeIdAndTime(typeId, date.get(0), date.get(1));
        }
        if (time.isEmpty()) {
            return eventModelMapper.getEventByTypeIdAndDepartmentId(typeId, departmentId);
        }
        List<Date> date = timeUtil.getDateOfMonday(time);
        return eventModelMapper.postForEventsByAll(typeId, departmentId, date.get(0), date.get(1));
    }

    /**
     * @param events 需要转换的地址
     * @return List<EventModel>
     * @author Aiden
     * 将活动中的location转化为要求的格式
     */
    @Override
    public List<EventModel> exchangeLocationOfEvents(List<EventModel> events) {
        // 获取所有location
        List<Location> locations = locationMapper.selectList(null);
        // 获取event中的location并转化成符合要求的结果
        Integer locationId;
        // 用于判断每个连接的字符串的后面是否需要空格，最详细的那一栏后面不需要
        Boolean isNeedSpace = false;
        StringBuilder fullAddress = new StringBuilder();
        List<EventModel> resultEvents = new ArrayList<>();
        for (EventModel event : events) {
            locationId = Integer.valueOf(event.getLocation());
            while (locationId > 0) {
                fullAddress.insert(0, locations.get(locationId - 1).getLocationName() + " ");
                if (isNeedSpace.equals(false)) {
                    fullAddress.deleteCharAt(fullAddress.length() - 1);
                    isNeedSpace = true;
                }
                locationId = locations.get(locationId - 1).getParentId();
            }
            event.setLocation(fullAddress.toString());
            resultEvents.add(event);
            fullAddress = new StringBuilder();
            isNeedSpace = false;
        }
        return resultEvents;
    }
}
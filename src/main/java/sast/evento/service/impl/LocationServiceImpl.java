package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Location;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.LocationMapper;
import sast.evento.model.treeDataNodeDTO.AntDesignTreeDataNode;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.service.LocationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    @Resource
    private LocationMapper locationMapper;
    @Override
    public Boolean addLocation(Location location) {
        if (location == null || location.getLocationName() == null || location.getParentId() == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        return locationMapper.insert(location) > 0;
    }

    @Override
    public Boolean deleteLocation(Integer id) {
        return locationMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean updateLocation(Location location) {
        if (location == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", location.getId());
        return locationMapper.update(location, updateWrapper) > 0;
    }

    @Override
    public List<TreeDataNode> getLocations() {
        List<Location> locations = locationMapper.selectList(null);
        // TODO 与 PermissionServiceImpl 中 toTreeData 的方法类似，可以考虑抽象复用
        HashMap<Integer, List<TreeDataNode>> nodeMap = locations.stream()
                .collect(HashMap::new, (map, location) -> {
                    if (!map.containsKey(location.getParentId())) {
                        map.put(location.getParentId(), new ArrayList<>());
                    } else {
                        map.get(location.getParentId()).add(new AntDesignTreeDataNode(location.getLocationName(), location.getLocationName(), location.getId().toString()));
                    }
                }, HashMap::putAll);
        return nodeMap.keySet().stream()
                .collect(
                        ArrayList::new,
                        (treeDataNodes, s) -> {
                            String locationName = locations.get(s).getLocationName();
                            TreeDataNode node = new AntDesignTreeDataNode(locationName, locationName, s.toString());
                            node.addChildren(nodeMap.get(s));
                            treeDataNodes.add(node);
                        },
                        List::addAll);
    }
}

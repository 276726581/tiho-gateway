package com.tiho.http.gateway.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RouteConfigMapper {

    @Insert("insert into route_config (config) values (#{config})")
    void saveRouteConfig(@Param("config") String config);

    @Select("select config from route_config order by id")
    List<String> getRouteConfigList();
}

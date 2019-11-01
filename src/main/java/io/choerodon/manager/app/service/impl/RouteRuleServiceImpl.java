package io.choerodon.manager.app.service.impl;import com.github.pagehelper.PageInfo;import io.choerodon.core.exception.CommonException;import io.choerodon.manager.api.dto.RouteRuleDTO;import io.choerodon.manager.api.dto.RouteRuleVO;import io.choerodon.manager.app.service.RouteRuleService;import io.choerodon.manager.infra.feign.IamClient;import org.springframework.data.domain.Pageable;import org.springframework.http.ResponseEntity;import org.springframework.stereotype.Service;import java.util.ArrayList;/** * RouteRuleServiceImpl * * @author pengyuhua * @date 2019/10/25 */@Servicepublic class RouteRuleServiceImpl implements RouteRuleService {    public static final String IAM_CLIENT_FEIGN_EXECUTE_EXCEPTION = "error.base.service.feign.execute.fail";    private IamClient iamClient;    public RouteRuleServiceImpl(IamClient iamClient) {        this.iamClient = iamClient;    }    @Override    public PageInfo<RouteRuleVO> listRouteRules(Pageable pageable, String code) {        PageInfo<RouteRuleVO> pageInfo = new PageInfo<>();        ResponseEntity<PageInfo<RouteRuleVO>> pageInfoResponseEntity;        // 查询路由信息        try {            pageInfoResponseEntity = iamClient.listRouteRules(pageable, code);        } catch (Exception e) {            throw new CommonException(IAM_CLIENT_FEIGN_EXECUTE_EXCEPTION, e);        }        if (pageInfoResponseEntity == null) {            pageInfo.setPageSize(pageable.getPageSize());            pageInfo.setPageNum(pageable.getPageNumber());            pageInfo.setList(new ArrayList<>());            return pageInfo;        }        // 查询每个路由下的主机信息        PageInfo<RouteRuleVO> routeRuleVOPageInfo = pageInfoResponseEntity.getBody();        routeRuleVOPageInfo.getList().forEach(v -> {            // todo 查询主机信息        });        return routeRuleVOPageInfo;    }    @Override    public RouteRuleVO queryRouteRuleDetailById(Long id) {        // 删除base 路由用户关联信息        ResponseEntity<RouteRuleVO> routeRuleVOResponseEntity;        try {            routeRuleVOResponseEntity = iamClient.queryRouteRuleDetailById(id);        } catch (Exception e) {            throw new CommonException(IAM_CLIENT_FEIGN_EXECUTE_EXCEPTION, e);        }        if (routeRuleVOResponseEntity == null) {            throw new CommonException("error.route.rule.query.fail");        }        // todo 删除 路由主机关联信息        return routeRuleVOResponseEntity.getBody();    }    @Override    public RouteRuleVO insertRouteRule(RouteRuleVO routeRuleVO) {        // 更新路由及关联用户信息        ResponseEntity<RouteRuleVO> routeRuleVOResponseEntity;        try {            routeRuleVOResponseEntity = iamClient.insertRouteRule(routeRuleVO);        } catch (Exception e) {            throw new CommonException(IAM_CLIENT_FEIGN_EXECUTE_EXCEPTION, e);        }        if (routeRuleVOResponseEntity == null) {            throw new CommonException("error.route.rule.insert");        }        // todo 更新路由配置主机信息        return routeRuleVOResponseEntity.getBody();    }    @Override    public Boolean deleteRouteRuleById(Long id) {        // 删除路由及关联用户信息        ResponseEntity<Boolean> booleanResponseEntity;        try {            booleanResponseEntity = iamClient.deleteRouteRuleById(id);        } catch (Exception e) {            throw new CommonException(IAM_CLIENT_FEIGN_EXECUTE_EXCEPTION, e);        }        if (booleanResponseEntity == null) {            throw new CommonException("error.route.rule.delete");        }        // todo 删除路由配置主机信息        return booleanResponseEntity.getBody();    }    @Override    public RouteRuleVO updateRouteRule(RouteRuleVO routeRuleVO, Long objectVersionNumber) {        // 更新路由及关联用户信息        ResponseEntity<RouteRuleVO> routeRuleVOResponseEntity;        try {            routeRuleVOResponseEntity = iamClient.updateRouteRule(routeRuleVO, objectVersionNumber);        } catch (Exception e) {            throw new CommonException(IAM_CLIENT_FEIGN_EXECUTE_EXCEPTION, e);        }        if (routeRuleVOResponseEntity == null) {            throw new CommonException("error.route.rule.update");        }        // todo 更新配置主机信息        return routeRuleVOResponseEntity.getBody();    }    @Override    public Boolean checkCode(RouteRuleVO routeRuleVO) {        ResponseEntity<Boolean> booleanResponseEntity;            try {                booleanResponseEntity = iamClient.checkCode(new RouteRuleDTO().setCode(routeRuleVO.getCode()));            } catch (Exception e) {                throw new CommonException(IAM_CLIENT_FEIGN_EXECUTE_EXCEPTION, e);            }            if (booleanResponseEntity == null) {                throw new CommonException("error.route.rule.code.check.fail");            }        return booleanResponseEntity.getBody();    }}
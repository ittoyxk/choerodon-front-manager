package io.choerodon.manager.api.eventhandler;

import io.choerodon.eureka.event.AbstractEurekaEventObserver;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.manager.domain.service.IActuatorRefreshService;
import io.choerodon.manager.domain.service.IDocumentService;
import io.choerodon.manager.domain.service.IRouteService;
import io.choerodon.manager.domain.service.ISwaggerRefreshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EurekaEventObserver extends AbstractEurekaEventObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaEventObserver.class);

    private IDocumentService iDocumentService;

    private ISwaggerRefreshService swaggerRefreshService;

    private IRouteService iRouteService;

    private IActuatorRefreshService actuatorRefreshService;

    public EurekaEventObserver(IDocumentService iDocumentService,
                               ISwaggerRefreshService swaggerRefreshService,
                               IRouteService iRouteService, IActuatorRefreshService actuatorRefreshService) {
        this.iDocumentService = iDocumentService;
        this.swaggerRefreshService = swaggerRefreshService;
        this.iRouteService = iRouteService;
        this.actuatorRefreshService = actuatorRefreshService;
    }

    @Override
    public void receiveUpEvent(EurekaEventPayload payload) {
        try {
            String json = iDocumentService.fetchSwaggerJsonByIp(payload);
            if (StringUtils.isEmpty(json)) {
                throw new RemoteAccessException("fetch swagger json data is empty, " + payload);
            }
            swaggerRefreshService.updateOrInsertSwagger(payload, json);
            iRouteService.autoRefreshRoute(json);
        } catch (Exception e) {
            LOGGER.warn("process swagger data exception skip: {}", payload, e);
        }
        try {
            String actuatorJson = iDocumentService.fetchActuatorJson(payload);
            if (StringUtils.isEmpty(actuatorJson)) {
                throw new RemoteAccessException("fetch actuator json data is empty, " + payload);
            }
            if(actuatorRefreshService.updateOrInsertActuator(payload.getAppName(), payload.getVersion(), actuatorJson)){
                LOGGER.info("actuator data saga apply success: {}", payload.getId());
            } else {
                LOGGER.info("actuator data not change skip: {}", payload.getId());
            }
        } catch (Exception e) {
            LOGGER.warn("process actuator data exception skip: {}", payload, e);
        }

        try {
            String metadataJson = iDocumentService.fetchMetadataJson(payload);
            if (StringUtils.isEmpty(metadataJson)) {
                LOGGER.info("fetch metadata json data is empty skip: {}", payload.getId());
            } else {
                actuatorRefreshService.sendMetadataEvent(metadataJson, payload.getAppName());
            }
        } catch (Exception e) {
            LOGGER.warn("process metadata data exception skip: {}", payload, e);
        }
    }

    @Override
    public void receiveDownEvent(EurekaEventPayload payload) {
        // do nothing
    }
}

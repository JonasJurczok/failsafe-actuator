package org.zalando.failsafeactuator.endpoint;

import net.jodah.failsafe.CircuitBreaker;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.zalando.failsafeactuator.endpoint.domain.CircuitBreakerState;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link AbstractEndpoint} for Failsafe purposes.
 *
 * <p>It will return all names of registered {@link CircuitBreaker}'s and their state as JSON.
 *
 * @author mpickhan on 29.06.16.
 */
@ConfigurationProperties(prefix = "endpoints.failsafe")
public class FailsafeEndpoint extends AbstractEndpoint<Map<String, CircuitBreakerState>> {

  private static final String ENDPOINT_ID = "failsafe";
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  public FailsafeEndpoint(final CircuitBreakerRegistry circuitBreakerRegistry) {
    super(ENDPOINT_ID);
    this.circuitBreakerRegistry = circuitBreakerRegistry;
  }

  @Override
  public Map<String, CircuitBreakerState> invoke() {
    final Map<String, CircuitBreaker> breakerMap = circuitBreakerRegistry.getConcurrentBreakerMap();
    final Map<String, CircuitBreakerState> breakerStates = new HashMap<>();

    final List<String> breakersToRemove = new ArrayList<>();
    for (final String identifier : breakerMap.keySet()) {
      final CircuitBreaker breaker = breakerMap.get(identifier);
      if (breaker == null) {
        //Memorize unreferenced breakers which need to be removed later on
        breakersToRemove.add(identifier);
      } else {
        final CircuitBreakerState state =
            new CircuitBreakerState(identifier, breaker.getState());
        breakerStates.put(identifier, state);
      }
    }
    removeUnreferencedBreakers(breakersToRemove);
    return breakerStates;
  }

  private void removeUnreferencedBreakers(final List<String> breakersToRemove) {
    for (final String identifier : breakersToRemove) {
      circuitBreakerRegistry.getConcurrentBreakerMap().remove(identifier);
    }
  }
}

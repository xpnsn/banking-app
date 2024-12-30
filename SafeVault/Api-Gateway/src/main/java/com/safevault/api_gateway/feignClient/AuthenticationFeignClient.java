package com.safevault.api_gateway.feignClient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "SECURITY")
public interface AuthenticationFeignClient {


}

/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package smoketest.jersey;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Smoke tests for Jersey configured as a Servlet.
 *
 * @author Andy Wilkinson
 * @author Kristoffer Larsen Hopland
 */
class JerseyServletApplicationTests extends AbstractJerseyApplicationTests {

	@Autowired
	private RestClient.Builder restClientBuilder;

	@Test
	void starterRetainsJackson2ForHttpClients() {
		assertThat(ClassUtils.isPresent("tools.jackson.databind.json.JsonMapper", null)).isFalse();
		assertThat(ClassUtils.isPresent("org.glassfish.jersey.jackson3.JacksonFeature", null)).isFalse();
		MockRestServiceServer server = MockRestServiceServer.bindTo(this.restClientBuilder).build();
		server.expect(requestTo("/message"))
			.andExpect(content().json("{\"FirstName\":\"Jersey\"}"))
			.andRespond(withSuccess());
		this.restClientBuilder.build().post().uri("/message").body(new Message("Jersey")).retrieve().toBodilessEntity();
		server.verify();
	}

	@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
	record Message(String firstName) {
	}

}
